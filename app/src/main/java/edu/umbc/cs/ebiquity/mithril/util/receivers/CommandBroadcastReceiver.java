package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CommandBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub to be removed after WiSec 2015 is over
	}

	/** For WiSec 2015 we removed the notion of receving a braodcast and are manually generating the data
	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Broadcast received with "+intent.getStringExtra("uri")+Integer.toString(intent.getIntExtra("ruleId", -1)), Toast.LENGTH_LONG).show();
		Violation tempViolation = new Violation(-1,intent.getStringExtra("uri"),1,intent.getIntExtra("ruleId", -1));
		ViolationWarningActivity.getViolationDBHelper().addViolation(ViolationWarningActivity.getViolationDB(), tempViolation);
	}
	*/
}