package edu.umbc.ebiquity.mithril.ui.fragments.prefsactivityfragments;

import android.app.Activity;
import android.app.AppOpsManager;
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

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.ebiquity.mithril.ui.activities.CoreActivity;
import edu.umbc.ebiquity.mithril.util.services.FetchAddressIntentService;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeekPreference;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.TimePreference;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.AddressKeyMissingError;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */
public class PrefsFragment extends PreferenceFragment {
//        implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        ResultCallback<Status> {

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME = 1;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK = 2;

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

//        appOps();
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

    private void appOps() {
//        PackageInfo mPackageInfo = null;
//        String mPackageName = null;
//        boolean newState = false;
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        mAppOps.setPrivacyGuardSettingForPackage(app.uid, app.packageName, app.privacyGuardEnabled);
        mAppOps.setMode(AppOpsManager.OP_WRITE_SETTINGS,
                mPackageInfo.applicationInfo.uid, mPackageName, newState
                        ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_ERRORED);
       /*
        mCurSysAppOpMode = mAppOps.checkOp(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg);
        mCurToastAppOpMode = mAppOps.checkOp(AppOpsManager.OP_TOAST_WINDOW, uid, pkg);
        mAppOps.setMode(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg, AppOpsManager.MODE_IGNORED);
        mAppOps.setMode(AppOpsManager.OP_TOAST_WINDOW, uid, pkg, AppOpsManager.MODE_IGNORED);
        mAppOps.setMode(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg, mCurSysAppOpMode);
        mAppOps.setMode(AppOpsManager.OP_TOAST_WINDOW, uid, pkg, mCurToastAppOpMode);
        final int switchOp = AppOpsManager.opToSwitch(firstOp.getOp());
        int mode = mAppOps.checkOp(switchOp, entry.getPackageOps().getUid(), entry.getPackageOps().getPackageName());
        mAppOps.setMode(switchOp, entry.getPackageOps().getUid(), entry.getPackageOps().getPackageName(), positionToMode(position));
        sw.setChecked(mAppOps.checkOp(switchOp, entry.getPackageOps()
                .getUid(), entry.getPackageOps().getPackageName()) == AppOpsManager.MODE_ALLOWED);
        sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mAppOps.setMode(switchOp, entry.getPackageOps()
                                .getUid(), entry.getPackageOps()
                                .getPackageName(),
                        isChecked ? AppOpsManager.MODE_ALLOWED
                                : AppOpsManager.MODE_IGNORED);
            }
        });
        List<AppOpsManager.PackageOps> pkgs;
        if (packageName != null) {
            pkgs = mAppOps.getOpsForPackage(uid, packageName, tpl.ops);
        } else {
            pkgs = mAppOps.getPackagesForOps(tpl.ops);
        }
         */
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
//                Log.d(MithrilApplication.getDebugTag(), getString(R.string.address_found) + ":" + mAddressOutput);
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