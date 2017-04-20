package edu.umbc.ebiquity.mithril.ui.fragments.prefsactivityfragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.ebiquity.mithril.ui.activities.CoreActivity;
import edu.umbc.ebiquity.mithril.util.services.FetchAddressIntentService;
import edu.umbc.ebiquity.mithril.util.services.GeofenceTransitionsIntentService;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeekPreference;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.TimePreference;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.AddressKeyMissingError;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.GeofenceErrorMessages;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */
public class PrefsFragment extends PreferenceFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME = 1;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK = 2;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    private boolean mAddressRequested;
    private AddressResultReceiver mResultReceiver;
    private SemanticLocation homeSemanticLocation;
    private SemanticLocation workSemanticLocation;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private SwitchPreference mSwitchPrefAllDone;
    private SwitchPreference mSwitchPrefEnableLocation;
    private Preference mPrefHomeLocation;
    private Preference mPrefWorkLocation;
    private SwitchPreference mSwitchPrefEnableTemporal;
    private DayOfWeekPreference mDayOfWeekPrefWorkDays;
    private TimePreference mTimePrefWorkHoursStart;
    private TimePreference mTimePrefWorkHoursEnd;
    private DayOfWeekPreference mDayOfWeekPrefDNDDays;
    private TimePreference mTimePrefDNDHoursStart;
    private TimePreference mTimePrefDNDHoursEnd;
    private SwitchPreference mSwitchPrefEnablePresenceInfo;
    private EditTextPreference mEditTextPrefPresenceInfoSupervisor;
    private EditTextPreference mEditTextPrefPresenceInfoColleague;
    private Context context;
    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(MithrilApplication.getDebugTag(), res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        sharedPrefs = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), MODE_PRIVATE);
        editor = sharedPrefs.edit();

        initViews();
        initData();
        setOnPreferenceClickListeners();
        setOnPreferenceChangeListeners();
    }

    private void initViews() {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        mSwitchPrefAllDone = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefAllDoneKey());

        mSwitchPrefEnableLocation = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefLocationContextEnableKey());
        mPrefHomeLocation = getPreferenceManager().findPreference(MithrilApplication.getPrefHomeLocationKey());
        mPrefWorkLocation = getPreferenceManager().findPreference(MithrilApplication.getPrefWorkLocationKey());

        mSwitchPrefEnableTemporal = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefTemporalContextEnableKey());

        mDayOfWeekPrefWorkDays = (DayOfWeekPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkDaysKey());
        mTimePrefWorkHoursStart = (TimePreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkHoursStartKey());
        mTimePrefWorkHoursEnd = (TimePreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkHoursEndKey());

        mDayOfWeekPrefDNDDays = (DayOfWeekPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefDndDaysKey());
        mTimePrefDNDHoursStart = (TimePreference) getPreferenceManager().findPreference(MithrilApplication.getPrefDndHoursStartKey());
        mTimePrefDNDHoursEnd = (TimePreference) getPreferenceManager().findPreference(MithrilApplication.getPrefDndHoursEndKey());

        mSwitchPrefEnablePresenceInfo = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoContextEnableKey());
        mEditTextPrefPresenceInfoSupervisor = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoSupervisorKey());
        mEditTextPrefPresenceInfoColleague = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoColleagueKey());

        /********************************************* Geofence related stuff **************************************************/
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = sharedPrefs.getBoolean(MithrilApplication.GEOFENCES_ADDED_KEY, false);

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        setEnabledEditTexts();
    }

    private void initData() {
        mResultReceiver = new AddressResultReceiver(new Handler(), context);
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;

        boolean isChecked = sharedPrefs.getBoolean(MithrilApplication.getPrefAllDoneKey(), false);
        mSwitchPrefAllDone.setChecked(isChecked);
        editor.putBoolean(MithrilApplication.getPrefAllDoneKey(), isChecked);

        isChecked = sharedPrefs.getBoolean(MithrilApplication.getPrefLocationContextEnableKey(), false);
        mSwitchPrefEnableLocation.setChecked(isChecked);
        editor.putBoolean(MithrilApplication.getPrefLocationContextEnableKey(), isChecked);

        isChecked = sharedPrefs.getBoolean(MithrilApplication.getPrefTemporalContextEnableKey(), false);
        mSwitchPrefEnableTemporal.setChecked(isChecked);
        editor.putBoolean(MithrilApplication.getPrefTemporalContextEnableKey(), isChecked);

        isChecked = sharedPrefs.getBoolean(MithrilApplication.getPrefPresenceInfoContextEnableKey(), false);
        mSwitchPrefEnablePresenceInfo.setChecked(isChecked);
        editor.putBoolean(MithrilApplication.getPrefPresenceInfoContextEnableKey(), isChecked);

        /********************************************** Location instances *************************************************/

        mPrefHomeLocation.setSummary(sharedPrefs.getString(MithrilApplication.getPrefHomeLocationKey(), getResources().getString(R.string.pref_home_location_summary)));
        mPrefWorkLocation.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkLocationKey(), getResources().getString(R.string.pref_work_location_summary)));

        /********************************************** Temporal instances *************************************************/

        String stringValue = sharedPrefs.getString(MithrilApplication.getPrefWorkDaysKey(), getResources().getString(R.string.pref_work_days));
        mDayOfWeekPrefWorkDays.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkDaysKey(), getResources().getString(R.string.pref_work_days)));
        editor.putString(MithrilApplication.getPrefWorkDaysKey(), stringValue);

        stringValue = sharedPrefs.getString(MithrilApplication.getPrefWorkHoursStartKey(), getResources().getString(R.string.pref_work_hours_start));
        mTimePrefWorkHoursStart.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkHoursStartKey(), getResources().getString(R.string.pref_work_hours_start)));
        editor.putString(MithrilApplication.getPrefWorkHoursStartKey(), stringValue);

        stringValue = sharedPrefs.getString(MithrilApplication.getPrefWorkHoursEndKey(), getResources().getString(R.string.pref_work_hours_end));
        mTimePrefWorkHoursEnd.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkHoursEndKey(), getResources().getString(R.string.pref_work_hours_end)));
        editor.putString(MithrilApplication.getPrefWorkHoursEndKey(), stringValue);

        stringValue = sharedPrefs.getString(MithrilApplication.getPrefDndDaysKey(), getResources().getString(R.string.pref_DND_days));
        mDayOfWeekPrefDNDDays.setSummary(sharedPrefs.getString(MithrilApplication.getPrefDndDaysKey(), getResources().getString(R.string.pref_DND_days)));
        editor.putString(MithrilApplication.getPrefDndDaysKey(), stringValue);

        stringValue = sharedPrefs.getString(MithrilApplication.getPrefDndHoursStartKey(), getResources().getString(R.string.pref_DND_hours_start));
        mTimePrefDNDHoursStart.setSummary(sharedPrefs.getString(MithrilApplication.getPrefDndHoursStartKey(), getResources().getString(R.string.pref_DND_hours_start)));
        editor.putString(MithrilApplication.getPrefDndHoursStartKey(), stringValue);

        stringValue = sharedPrefs.getString(MithrilApplication.getPrefDndHoursEndKey(), getResources().getString(R.string.pref_DND_hours_end));
        mTimePrefDNDHoursEnd.setSummary(sharedPrefs.getString(MithrilApplication.getPrefDndHoursEndKey(), getResources().getString(R.string.pref_DND_hours_end)));
        editor.putString(MithrilApplication.getPrefDndHoursEndKey(), stringValue);

        /********************************************** Presence info instances *************************************************/

        stringValue = sharedPrefs.getString(MithrilApplication.getPrefPresenceInfoSupervisorKey(), getResources().getString(R.string.pref_presence_info_supervisor_summary));
        mEditTextPrefPresenceInfoSupervisor.setText(sharedPrefs.getString(MithrilApplication.getPrefPresenceInfoSupervisorKey(), getResources().getString(R.string.pref_presence_info_supervisor_summary)));
        editor.putString(MithrilApplication.getPrefPresenceInfoSupervisorKey(), stringValue);

        stringValue = sharedPrefs.getString(MithrilApplication.getPrefPresenceInfoColleagueKey(), getResources().getString(R.string.pref_presence_info_colleague_summary));
        mEditTextPrefPresenceInfoColleague.setText(sharedPrefs.getString(MithrilApplication.getPrefPresenceInfoColleagueKey(), getResources().getString(R.string.pref_presence_info_colleague_summary)));
        editor.putString(MithrilApplication.getPrefPresenceInfoColleagueKey(), stringValue);

        editor.apply();
    }

    private void setEnabledEditTexts() {
        mPrefHomeLocation.setEnabled(mSwitchPrefEnableLocation.isChecked());
        mPrefWorkLocation.setEnabled(mSwitchPrefEnableLocation.isChecked());

        mDayOfWeekPrefWorkDays.setEnabled(mSwitchPrefEnableTemporal.isChecked());
        mTimePrefWorkHoursStart.setEnabled(mSwitchPrefEnableTemporal.isChecked());
        mTimePrefWorkHoursEnd.setEnabled(mSwitchPrefEnableTemporal.isChecked());

        mDayOfWeekPrefDNDDays.setEnabled(mSwitchPrefEnableTemporal.isChecked());
        mTimePrefDNDHoursStart.setEnabled(mSwitchPrefEnableTemporal.isChecked());
        mTimePrefDNDHoursEnd.setEnabled(mSwitchPrefEnableTemporal.isChecked());

        mEditTextPrefPresenceInfoColleague.setEnabled(mSwitchPrefEnablePresenceInfo.isChecked());
        mEditTextPrefPresenceInfoSupervisor.setEnabled(mSwitchPrefEnablePresenceInfo.isChecked());
    }

    private void setOnPreferenceClickListeners() {
        mPrefHomeLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openAutocompleteActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME);
                return true;
            }
        });

        mPrefWorkLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openAutocompleteActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK);
                return true;
            }
        });
    }

    private void openAutocompleteActivity(int requestCode) {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(MithrilApplication.getDebugTag(), message);
            PermissionHelper.toast(getActivity(), message, Toast.LENGTH_SHORT);
        }
    }

    /**********************************************************Geofence code********************************************************/

    /**
     * A location was selected by the user for their home/work address
     * EDUCATION MOMENT (from http://android-er.blogspot.com/2013/02/convert-between-latlng-and-location.html):
     * //Convert LatLng to Location
     * Location location = new Location("Test");
     * location.setLatitude(point.latitude);
     * location.setLongitude(point.longitude);
     * location.setTime(new Date().getTime()); //Set time as current Date
     * info.setText(location.toString());
     * <p>
     * //Convert Location to LatLng
     * LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
     * Called after the autocomplete activity has finished to return its result.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                mPrefHomeLocation.setSummary(place.getAddress());
                editor.putString(MithrilApplication.getPrefHomeLocationKey(), place.getAddress().toString());
                editor.apply();

                Location location = new Location("placesAPI");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                homeSemanticLocation = new SemanticLocation(MithrilApplication.getPrefHomeLocationKey(), location);

                // We have a geofences to put around the home location now!
                populateGeofenceList(MithrilApplication.getPrefHomeLocationKey(), location.getLatitude(), location.getLongitude());

                /**
                 * We know the location has changed, let's check the address
                 */
                mAddressRequested = true;
                startSearchAddressIntentService(location, MithrilApplication.getPrefHomeLocationKey());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(MithrilApplication.getDebugTag(), "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                mPrefWorkLocation.setSummary(place.getAddress());
                editor.putString(MithrilApplication.getPrefWorkLocationKey(), place.getAddress().toString());
                editor.apply();

                Location location = new Location("placesAPI");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                workSemanticLocation = new SemanticLocation(MithrilApplication.getPrefWorkLocationKey(), location);

                // We have a geofences to put around the work location now!
                populateGeofenceList(MithrilApplication.getPrefWorkLocationKey(), location.getLatitude(), location.getLongitude());

                /**
                 * We know the location has changed, let's check the address
                 */
                mAddressRequested = true;
                startSearchAddressIntentService(location, MithrilApplication.getPrefWorkLocationKey());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(MithrilApplication.getDebugTag(), "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startSearchAddressIntentService(Location location, String key) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(context, FetchAddressIntentService.class);

        intent.putExtra(MithrilApplication.ADDRESS_REQUESTED_EXTRA, mAddressRequested);
        intent.putExtra(MithrilApplication.ADDRESS_KEY, key);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(MithrilApplication.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(MithrilApplication.LOCATION_DATA_EXTRA, location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        context.startService(intent);
    }

    private void setOnPreferenceChangeListeners() {
        mSwitchPrefAllDone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefAllDone.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefAllDoneKey(), mSwitchPrefAllDone.isChecked());
                editor.apply();

                addGeofences();

                if (mSwitchPrefAllDone.isChecked())
                    context.startActivity(new Intent(context, CoreActivity.class));

                return true;
            }
        });

        mSwitchPrefEnableLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnableLocation.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefLocationContextEnableKey(), mSwitchPrefEnableLocation.isChecked());
                editor.apply();

                mPrefHomeLocation.setEnabled(mSwitchPrefEnableLocation.isChecked());
                mPrefWorkLocation.setEnabled(mSwitchPrefEnableLocation.isChecked());

                return true;
            }
        });

        mPrefHomeLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for searching home location and storing it for now we don't do anything and have hardcoded the options
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefHomeLocationKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mPrefWorkLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkLocationKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mSwitchPrefEnableTemporal.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnableTemporal.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefTemporalContextEnableKey(), mSwitchPrefEnableTemporal.isChecked());
                editor.apply();

                mDayOfWeekPrefWorkDays.setEnabled(mSwitchPrefEnableTemporal.isChecked());
                mTimePrefWorkHoursStart.setEnabled(mSwitchPrefEnableTemporal.isChecked());
                mTimePrefWorkHoursEnd.setEnabled(mSwitchPrefEnableTemporal.isChecked());

                mDayOfWeekPrefDNDDays.setEnabled(mSwitchPrefEnableTemporal.isChecked());
                mTimePrefDNDHoursStart.setEnabled(mSwitchPrefEnableTemporal.isChecked());
                mTimePrefDNDHoursEnd.setEnabled(mSwitchPrefEnableTemporal.isChecked());

                return true;
            }
        });

        mDayOfWeekPrefWorkDays.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);
                Log.d(MithrilApplication.getDebugTag() + " persisting? ", changedValue);

                editor.putString(MithrilApplication.getPrefWorkDaysKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mTimePrefWorkHoursStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkHoursStartKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mTimePrefWorkHoursEnd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkHoursEndKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mDayOfWeekPrefDNDDays.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefDndDaysKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mTimePrefDNDHoursStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefDndHoursStartKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mTimePrefDNDHoursEnd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefDndHoursEndKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mSwitchPrefEnablePresenceInfo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnablePresenceInfo.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefPresenceInfoContextEnableKey(), mSwitchPrefEnablePresenceInfo.isChecked());
                editor.apply();

                mEditTextPrefPresenceInfoColleague.setEnabled(mSwitchPrefEnablePresenceInfo.isChecked());
                mEditTextPrefPresenceInfoSupervisor.setEnabled(mSwitchPrefEnablePresenceInfo.isChecked());
                return true;
            }
        });

        mEditTextPrefPresenceInfoSupervisor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefPresenceInfoSupervisorKey(), changedValue);
                editor.apply();

                return true;
            }
        });

        mEditTextPrefPresenceInfoColleague.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefPresenceInfoColleagueKey(), changedValue);
                editor.apply();

                return true;
            }
        });
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(MithrilApplication.getDebugTag(), "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(MithrilApplication.getDebugTag(), "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(MithrilApplication.getDebugTag(), "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(context, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(context, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(MithrilApplication.getDebugTag(), "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(MithrilApplication.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
//            setButtonsEnabledState();

            Toast.makeText(
                    context,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(context,
                    status.getStatusCode());
            Log.e(MithrilApplication.getDebugTag(), errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList(String semanticIdentifier, double latitude, double longitude) {
//        for (Map.Entry<String, LatLng> entry : MithrilApplication.BALTIMORE_COUNTY_LANDMARKS.entrySet()) {

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(semanticIdentifier)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        latitude,
                        longitude,
                        MithrilApplication.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(MithrilApplication.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());
//        }
    }

    class AddressResultReceiver extends ResultReceiver {
        private Context context;
        private SharedPreferences sharedPref;
        /**
         * The formatted location address.
         */
        private Address mAddressOutput;
        /**
         * Tracks whether the user has requested an address. Becomes true when the user requests an
         * address and false when the address (or an error message) is delivered.
         * The user requests an address by pressing the Fetch Address button. This may happen
         * before GoogleApiClient connects. This activity uses this boolean to keep track of the
         * user's intent. If the value is true, the activity tries to fetch the address as soon as
         * GoogleApiClient connects.
         */
        private boolean mAddressRequested;

        public AddressResultReceiver(Handler handler, Context aContext) {
            super(handler);

            context = aContext;
            // Set defaults, then update using values stored in the Bundle.
            mAddressRequested = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                mAddressOutput = new Address(context.getResources().getConfiguration().getLocales().get(0));
            else
                mAddressOutput = new Address(context.getResources().getConfiguration().locale);
            sharedPref = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressRequested = resultData.getBoolean(MithrilApplication.ADDRESS_REQUESTED_EXTRA, false);
            String key = resultData.getString(MithrilApplication.ADDRESS_KEY, null);
            if (key.equals(null))
                throw new AddressKeyMissingError();
            else
                storeAddressInDB(key, resultCode, resultData);
        }

        protected void storeAddressInDB(String key, int resultCode, Bundle resultData) {
            // Display the address string
            // or an error message sent from the intent service.
            Gson gson = new Gson();
            String json = resultData.getString(MithrilApplication.RESULT_DATA_KEY, "");
            try {
                mAddressOutput = gson.fromJson(json, Address.class);
            } catch (JsonSyntaxException e) {
                Log.d(MithrilApplication.getDebugTag(), e.getMessage());
            }

            Log.d(MithrilApplication.getDebugTag(), "Prefs address " + resultData.getString(MithrilApplication.ADDRESS_KEY) + mAddressRequested + key + json);
            // Show a toast message if an address was found.
            if (resultCode == MithrilApplication.SUCCESS_RESULT) {
//                Log.d(MithrilApplication.getDebugMithrilApplication.getDebugTag()(), getString(R.string.address_found) + ":" + mAddressOutput);
//                PermissionHelper.toast(context, getString(R.string.address_found) + ":" + mAddressOutput);
                storeInSharedPreferences(resultData.getString(
                        key),
                        mAddressOutput);
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }

        /**
         * From http://stackoverflow.com/a/18463758/1816861
         *
         * @param key
         * @param address To Retreive
         *                Gson gson = new Gson();
         *                String json = mPrefs.getString("MyObject", "");
         *                MyObject obj = gson.fromJson(json, MyObject.class);
         */
        public void storeInSharedPreferences(String key, Address address) {
            SharedPreferences.Editor editor = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
            Gson gson = new Gson();
            String json = gson.toJson(address);
            editor.putString(key, json);
            editor.apply();
        }
    }
}