package edu.umbc.ebiquity.mithril.util.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;

/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {
    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String addressKey;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link android.os.ResultReceiver} in * CoreActivity to process content
     * sent from this service.
     * <p>
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        addressKey = intent.getStringExtra(MithrilAC.getAddressKey());
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(MithrilAC.getAppReceiver());
        // Check if receiver was properly registered.
        if (mReceiver == null) {
//            Log.wtf(MithrilAC.getDebugTag(), "No receiver received. There is nowhere to send the results.");
            return;
        }

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(MithrilAC.getLocationDataExtra());
        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided);
//            Log.wtf(MithrilAC.getDebugTag(), errorMessage);
            deliverResultToReceiver(MithrilAC.FAILURE_RESULT, errorMessage);
            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
//            Log.e(MithrilAC.getDebugTag(), errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
//            Log.e(MithrilAC.getDebugTag(), errorMessage + ". " +
//                    "Latitude = " + location.getLatitude() +
//                    ", Longitude = " +
//                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
//                Log.e(MithrilAC.getDebugTag(), errorMessage);
            }
            deliverResultToReceiver(MithrilAC.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            Gson gson = new Gson();
            String jsonAddress = gson.toJson(address);
//            List<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
//            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                addressFragments.add(address.getAddressLine(i));
//            }
//            Log.i(MithrilAC.getDebugTag(), getString(R.string.address_found) + " : " + address.getPostalCode());
//            Log.i(MithrilAC.getDebugTag(), getString(R.string.address_found) + " : " + jsonAddress);
//            deliverResultToReceiver(MithrilAC.SUCCESS_RESULT,
//                    TextUtils.join(System.getProperty("line.separator"),
//                            addressFragments));
            deliverResultToReceiver(MithrilAC.SUCCESS_RESULT,
                    jsonAddress);
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(MithrilAC.getResultDataKey(), message);
        bundle.putString(MithrilAC.getAddressKey(), addressKey);

        mReceiver.send(resultCode, bundle);
    }
}