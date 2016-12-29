package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.errors.AddressKeyMissingError;

/**
 * Created by Prajit on 12/26/2016.
 */

public class AddressResultReceiver extends ResultReceiver {
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
        Log.e(MithrilApplication.getDebugTag(), "Key error: " + resultData.getString(MithrilApplication.ADDRESS_KEY));
        if (resultData.getString(MithrilApplication.ADDRESS_KEY, null) == null)
            throw new AddressKeyMissingError();
        // Display the address string
        // or an error message sent from the intent service.
        Gson gson = new Gson();
        String json = resultData.getString(MithrilApplication.RESULT_DATA_KEY);
        mAddressOutput = gson.fromJson(json, Address.class);
//            displayAddressOutput();

        // Show a toast message if an address was found.
        if (resultCode == MithrilApplication.SUCCESS_RESULT) {
//                Log.d(MithrilApplication.getDebugTag(), getString(R.string.address_found) + ":" + mAddressOutput);
//                Toast.makeText(context, getString(R.string.address_found) + ":" + mAddressOutput, Toast.LENGTH_LONG).show();
            storeInSharedPreferences(resultData.getString(
                    MithrilApplication.ADDRESS_KEY),
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
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(address);
        editor.putString(key, json);
        editor.commit();
    }
}