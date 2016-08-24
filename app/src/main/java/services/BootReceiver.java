package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import common.App;
import common.AppModel;
import common.HomeCtrl;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (App.IsTesting)
			Toast.makeText(context, "BootReceiver", Toast.LENGTH_LONG).show();

		new AppModel(context);

		HomeCtrl.SetAlarm(context);
	}
}
