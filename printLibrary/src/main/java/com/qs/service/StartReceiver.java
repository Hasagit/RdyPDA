package com.qs.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

			Intent newIntent = new Intent(context, ScanService.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(newIntent);

			Toast.makeText(context, "¿ª»úÆô¶¯", Toast.LENGTH_SHORT).show();

		}

	}

}
