package edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.util.receivers.AddressResultReceiver;
import edu.umbc.cs.ebiquity.mithril.util.services.FetchAddressIntentService;
import edu.umbc.cs.ebiquity.mithril.util.services.GeofenceTransitionsIntentService;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.context.DayOfWeekPreference;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.context.TimePreference;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.errorsnexceptions.GeofenceErrorMessages;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Prajit Kumar Das on 6/11/2016.
 */

public class PrefsFragment extends PreferenceFragment implements
        GoogleApiClient.ConnectionCallbacks,
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
    /**
     * Represents a selected geographical location.
     */
    private Location mSelectedLocation;
    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;
    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences sharedPrefs;

    private SwitchPreference mSwitchPrefEnableLocationEnabled;
    private EditTextPreference mEditTextPrefHomeLocation;
    private EditTextPreference mEditTextPrefWorkLocation;

    private SwitchPreference mSwitchPrefEnableTemporalEnabled;

    private DayOfWeekPreference mDayOfWeekPrefWorkDays;
    private TimePreference mTimePrefWorkHoursStart;
    private TimePreference mTimePrefWorkHoursEnd;

    private DayOfWeekPreference mDayOfWeekPrefDNDDays;
    private TimePreference mTimePrefDNDHoursStart;
    private TimePreference mTimePrefDNDHoursEnd;

    private SwitchPreference mSwitchPrefEnablePresenceInfoEnabled;
    private EditTextPreference mEditTextPrefPresenceInfoSupervisor;
    private EditTextPreference mEditTextPrefPresenceInfoColleague;

    private Context context;

    // Buttons for kicking off the process of adding or removing geofences.
//    private Button mAddGeofencesButton;
//    private Button mRemoveGeofencesButton;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
            Date date = new Date(selectedYear, selectedMonth, selectedDay - 1);
            String dayOfWeek = simpledateformat.format(date);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        mResultReceiver = new AddressResultReceiver(new Handler(), context);
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;

//        appOps();
        initViews();
        initData();
        setOnPreferenceClickListeners();
        setOnPreferenceChangeListeners();
    }

    private void initData() {
        sharedPrefs = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), MODE_PRIVATE);

        mSwitchPrefEnableLocationEnabled.setChecked(sharedPrefs.getBoolean(MithrilApplication.getPrefLocationContextEnableKey(), false));
        mEditTextPrefHomeLocation.setSummary(sharedPrefs.getString(MithrilApplication.getPrefHomeLocationKey(), getResources().getString(R.string.pref_home_location_summary)));
        mEditTextPrefWorkLocation.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkLocationKey(), getResources().getString(R.string.pref_work_location_summary)));

        mSwitchPrefEnableTemporalEnabled.setChecked(sharedPrefs.getBoolean(MithrilApplication.getPrefTemporalContextEnableKey(), false));

        mDayOfWeekPrefWorkDays.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkDaysKey(), getResources().getString(R.string.pref_work_days)));
        mTimePrefWorkHoursStart.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkHoursStartKey(), getResources().getString(R.string.pref_work_hours_start)));
        mTimePrefWorkHoursEnd.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkHoursEndKey(), getResources().getString(R.string.pref_work_hours_end)));

        mDayOfWeekPrefDNDDays.setSummary(sharedPrefs.getString(MithrilApplication.getPrefDndDaysKey(), getResources().getString(R.string.pref_DND_days)));
        mTimePrefDNDHoursStart.setSummary(sharedPrefs.getString(MithrilApplication.getPrefDndHoursStartKey(), getResources().getString(R.string.pref_DND_hours_start)));
        mTimePrefDNDHoursEnd.setSummary(sharedPrefs.getString(MithrilApplication.getPrefDndHoursEndKey(), getResources().getString(R.string.pref_DND_hours_end)));

        mSwitchPrefEnablePresenceInfoEnabled.setChecked(sharedPrefs.getBoolean(MithrilApplication.getPrefPresenceInfoContextEnableKey(), false));
        mEditTextPrefPresenceInfoSupervisor.setSummary(sharedPrefs.getString(MithrilApplication.getPrefPresenceInfoSupervisorKey(), getResources().getString(R.string.pref_presence_info_supervisor_summary)));
        mEditTextPrefPresenceInfoColleague.setSummary(sharedPrefs.getString(MithrilApplication.getPrefPresenceInfoColleagueKey(), getResources().getString(R.string.pref_presence_info_colleague_summary)));
    }

    private void appOps() {
//        PackageInfo mPackageInfo = null;
//        String mPackageName = null;
//        boolean newState = false;
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
/*
        mAppOps.setMode(AppOpsManager.OP_WRITE_SETTINGS,
                mPackageInfo.applicationInfo.uid, mPackageName, newState
                        ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_ERRORED);
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

    private void setupGeoFences() {
        // Get the UI widgets.
//        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
//        mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        sharedPrefs = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = sharedPrefs.getBoolean(MithrilApplication.GEOFENCES_ADDED_KEY, false);
        setButtonsEnabledState();

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
    }

    private void initViews() {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        mSwitchPrefEnableLocationEnabled = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefLocationContextEnableKey());
        mEditTextPrefHomeLocation = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefHomeLocationKey());
        mEditTextPrefWorkLocation = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkLocationKey());

        mSwitchPrefEnableTemporalEnabled = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefTemporalContextEnableKey());

        mDayOfWeekPrefWorkDays = (DayOfWeekPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkDaysKey());
        mTimePrefWorkHoursStart = (TimePreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkHoursStartKey());
        mTimePrefWorkHoursEnd = (TimePreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkHoursEndKey());

        mDayOfWeekPrefDNDDays = (DayOfWeekPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefDndDaysKey());
        mTimePrefDNDHoursStart = (TimePreference) getPreferenceManager().findPreference(MithrilApplication.getPrefDndHoursStartKey());
        mTimePrefDNDHoursEnd = (TimePreference) getPreferenceManager().findPreference(MithrilApplication.getPrefDndHoursEndKey());

        mSwitchPrefEnablePresenceInfoEnabled = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoContextEnableKey());
        mEditTextPrefPresenceInfoSupervisor = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoSupervisorKey());
        mEditTextPrefPresenceInfoColleague = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoColleagueKey());

        setEnabledEditTexts();
    }

    private void setEnabledEditTexts() {
        mEditTextPrefHomeLocation.setEnabled(mSwitchPrefEnableLocationEnabled.isChecked());
        mEditTextPrefWorkLocation.setEnabled(mSwitchPrefEnableLocationEnabled.isChecked());

        mDayOfWeekPrefWorkDays.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
        mTimePrefWorkHoursStart.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
        mTimePrefWorkHoursEnd.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());

        mDayOfWeekPrefDNDDays.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
        mTimePrefDNDHoursStart.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
        mTimePrefDNDHoursEnd.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());

        mEditTextPrefPresenceInfoColleague.setEnabled(mSwitchPrefEnablePresenceInfoEnabled.isChecked());
        mEditTextPrefPresenceInfoSupervisor.setEnabled(mSwitchPrefEnablePresenceInfoEnabled.isChecked());
    }

    private void setOnPreferenceClickListeners() {
        mEditTextPrefHomeLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                return false;
            }
        });

        mEditTextPrefWorkLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                return false;
            }
        });
    }

    private void setOnPreferenceChangeListeners() {
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        mSwitchPrefEnableLocationEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnableLocationEnabled.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefLocationContextEnableKey(), mSwitchPrefEnableLocationEnabled.isChecked());
                editor.commit();

                mEditTextPrefHomeLocation.setEnabled(mSwitchPrefEnableLocationEnabled.isChecked());
                mEditTextPrefWorkLocation.setEnabled(mSwitchPrefEnableLocationEnabled.isChecked());
                return true;
                /**
                 * When the user enables the location settings, we set up the geo fences but we have to be careful about how we set this up.
                 * We have to be careful because there's a limit on how many geofences we can create per device.
                 * So, we will set this up only when the user sets up some locations
                 */
//                setupGeoFences();
//                return false;
            }
        });

        mEditTextPrefHomeLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for searching home location and storing it for now we don't do anything and have hardcoded the options
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefHomeLocationKey(), changedValue);
                editor.commit();

                return true;
            }
        });

        mEditTextPrefWorkLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkLocationKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mSwitchPrefEnableTemporalEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnableTemporalEnabled.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefTemporalContextEnableKey(), mSwitchPrefEnableTemporalEnabled.isChecked());
                editor.commit();

                mDayOfWeekPrefWorkDays.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
                mTimePrefWorkHoursStart.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
                mTimePrefWorkHoursEnd.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());

                mDayOfWeekPrefDNDDays.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
                mTimePrefDNDHoursStart.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
                mTimePrefDNDHoursEnd.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());

                return true;
            }
        });

        mDayOfWeekPrefWorkDays.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkDaysKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mTimePrefWorkHoursStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkHoursStartKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mTimePrefWorkHoursEnd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkHoursEndKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mDayOfWeekPrefDNDDays.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefDndDaysKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mTimePrefDNDHoursStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefDndHoursStartKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mTimePrefDNDHoursEnd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefDndHoursEndKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mSwitchPrefEnablePresenceInfoEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnablePresenceInfoEnabled.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefPresenceInfoContextEnableKey(), mSwitchPrefEnablePresenceInfoEnabled.isChecked());
                editor.commit();

                mEditTextPrefPresenceInfoColleague.setEnabled(mSwitchPrefEnablePresenceInfoEnabled.isChecked());
                mEditTextPrefPresenceInfoSupervisor.setEnabled(mSwitchPrefEnablePresenceInfoEnabled.isChecked());
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
                editor.commit();

                return false;
            }
        });

        mEditTextPrefPresenceInfoColleague.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefPresenceInfoColleagueKey(), changedValue);
                editor.commit();

                return false;
            }
        });
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
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME: {
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                    mSelectedLocation = new Location(LocationManager.GPS_PROVIDER);
                    mSelectedLocation.setLatitude(place.getLatLng().latitude);
                    mSelectedLocation.setLongitude(place.getLatLng().longitude);

                    startSearchAddressIntentService(MithrilApplication.getPrefHomeLocationKey());

                    Log.i(MithrilApplication.getDebugTag(), "Place: " + place.getAddress());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                    // TODO: Handle the error.
                    Log.i(MithrilApplication.getDebugTag(), status.getStatusMessage());

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
            case PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK: {
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                    mSelectedLocation = new Location(LocationManager.GPS_PROVIDER);
                    mSelectedLocation.setLatitude(place.getLatLng().latitude);
                    mSelectedLocation.setLongitude(place.getLatLng().longitude);

                    startSearchAddressIntentService(MithrilApplication.getPrefWorkLocationKey());

                    Log.i(MithrilApplication.getDebugTag(), "Place: " + place.getAddress());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                    // TODO: Handle the error.
                    Log.i(MithrilApplication.getDebugTag(), status.getStatusMessage());

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startSearchAddressIntentService(String key) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(context, FetchAddressIntentService.class);

        intent.putExtra(MithrilApplication.ADDRESS_REQUESTED_EXTRA, mAddressRequested);
        intent.putExtra(MithrilApplication.ADDRESS_KEY, key);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(MithrilApplication.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(MithrilApplication.LOCATION_DATA_EXTRA, mSelectedLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        context.startService(intent);
    }

    /**************************************START OF GEOFENCE CODE*********************************************************/
    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mGoogleApiClient.disconnect();
//    }

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
    public void addGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(context, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
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
    public void removeGeofencesButtonHandler(View view) {
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
            setButtonsEnabledState();

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
        Intent intent = new Intent(getActivity(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : MithrilApplication.BALTIMORE_COUNTY_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
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
        }
    }

    /**
     * Ensures that only one button is enabled at any time. The Add Geofences button is enabled
     * if the user hasn't yet added geofences. The Remove Geofences button is enabled if the
     * user has added geofences.
     */
    private void setButtonsEnabledState() {
        if (mGeofencesAdded) {
//            mAddGeofencesButton.setEnabled(false);
//            mRemoveGeofencesButton.setEnabled(true);
        } else {
//            mAddGeofencesButton.setEnabled(true);
//            mRemoveGeofencesButton.setEnabled(false);
        }
    }
    /**************************************END OF GEOFENCE CODE*********************************************************/
}