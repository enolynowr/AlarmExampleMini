package com.example.alarmexample70821;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent intent2 = new Intent();
		intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		intent2.setAction(Intent.ACTION_CALL);
    	Uri uri = Uri.parse("tel:" + UserData.u_phone_number);
    	Log.e("전화번호============", UserData.u_phone_number);
    	intent2.setData(uri);
    	
    	context.startActivity(intent2);
    	    	 
    	// 알람매니저 종료 함수.(혹시 몰라서 넣었습니다. 메모링 남아있을까봐...)
    	MainActivity.mManager.cancel(MainActivity.pi);
	}
}
