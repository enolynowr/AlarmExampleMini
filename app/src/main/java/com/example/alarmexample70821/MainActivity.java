package com.example.alarmexample70821;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;

public class MainActivity extends Activity {

	public static AlarmManager mManager;// 알람 매니저 클래스 선언
	public static long uset_time;// 사용지 입력 시간
	public static Intent intent; // 인텐트 선언
	public static PendingIntent pi; // 펜딩인텐트 선언
	public static TextView main_tv_settedTime;//db에서 불러온 시간 저장 메인화면
	public static TextView main_tv_setted_phone;//db에서 불러온 전화번호 저장 메인화면

	String serviceData;
	Button btnStartService;// 서비스 시작 버튼
	Button btnStopService;// 서비스 정지 버튼
	Button btnSetAd; // 설정변경으로 넘어가는 버튼
	Intent intentMyService;
	BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

// db에서 불러온 값 저장=========================================================================================================================================
		main_tv_settedTime = (TextView) findViewById(R.id.setted_time);
		main_tv_setted_phone = (TextView) findViewById(R.id.setted_phone);

		try {
			main_tv_settedTime.setText(readDB(1) + " 시간");

			String phone_name = "";
			phone_name = "전화번호 : " + readDB(2) + "\n" + "이  름 : " + readDB(3);
			main_tv_setted_phone.setText(phone_name);

		} catch (Exception e1) {

			e1.printStackTrace();
		}

// 설정변경으로 넘어가는 버튼 그리고 인텐트 생산=========================================================================================================================
		btnSetAd = (Button) findViewById(R.id.set_service);
		btnSetAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent it = new Intent(MainActivity.this, SetTimePhone.class);
				startActivity(it);

			}
		});

// 서비스 시작하는 버튼 메서드===================================================================================================================================
		btnStartService = (Button) findViewById(R.id.start_service);
		btnStartService.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {

					try {

						IntentFilter mainFilter = new IntentFilter(
								"com.androday.test.step");

						registerReceiver(receiver, mainFilter);

						startService(intentMyService);

						setAlarm();

						Toast.makeText(getApplicationContext(), "맘보기 시작", Toast.LENGTH_SHORT)
								.show();
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(getApplicationContext(),
								"설정을 변경해 주세요" + e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}

			});
		
//서비스를 정지하는 버튼 메소드===================================================================================================================================
		btnStopService = (Button) findViewById(R.id.stop_service);
		btnStopService.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {

					unregisterReceiver(receiver);

					stopService(intentMyService);

					// receiver = null;
					// intentMyService = null;

					resetAlarm(); // 알람매니저 캔슬
					values.Step = 0; // value값 0으로

					Toast.makeText(getApplicationContext(), "맘보기 스탑", Toast.LENGTH_SHORT)
							.show();

				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(getApplicationContext(),
							"설정을 변경 해 주세요" + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		

		intentMyService = new Intent(this, MyServiceIntent.class);
		receiver = new MyMainLocalRecever();
	}

// 알람의 설정 메소드=========================================================================================================================================
	public void setAlarm() {

		GregorianCalendar mCalendar = new GregorianCalendar();

		long current_time = mCalendar.getTimeInMillis();
		Log.d("current_time???? ", " " + current_time);

		int input_utime = Integer.parseInt(UserData.u_set_time);

		uset_time = current_time + (input_utime * 10000);
		Log.e("원하는 시간은 ", "" + uset_time);
		// 인텐트 변수에 인텐트 생성(삽입)
		intent = new Intent(getApplicationContext(), AlarmReceiver.class);
		// 펜딩인텐트 변수에 펜딩인텐트 생성(삽입)
		pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		// 알람매니저 클래스에 알람매니저 생성
		mManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// 알람매니저 셋팅 및 펜딩인텐트(pi) 넣어줌
		mManager.setRepeating(AlarmManager.RTC_WAKEUP, uset_time, 0, pi);

		Log.e("알람의 설정        ", mCalendar.getTime().toString());
		Toast.makeText(MainActivity.this, "알람이 set되었습니다.", Toast.LENGTH_SHORT)
				.show();
	}

// 알람의 해제 메소드===================================================================================================================
	public void resetAlarm() {
		mManager.cancel(pi); // 알람매니저 서비스 해제
		Toast.makeText(MainActivity.this, "알람이 cancel되었습니다.",
				Toast.LENGTH_SHORT).show();
	}

// 앱 종료시 알람매니저 해제==============================================================================================================
	@Override
	public void onDestroy() { // 앱이 종료되면, (액티비티가 종료되면),
		super.onDestroy();

		// mManager.cancel(pi); // 알람매니저 해제
		// unregisterReceiver(receiver);
		// stopService(intentMyService); // 맘보기서비스 해제

	}

// 맘보기가 리시버를 해서 알람매니저 시작과 재설정을 실행===============================================================================
	class MyMainLocalRecever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			serviceData = intent.getStringExtra("김현종");

			int aaa = Integer.parseInt(serviceData);

			if (aaa == 10) {

				resetAlarm();
				values.Step = 0;
				MyServiceIntent.count = 0;

				setAlarm();
			}

		}

	}

// 데이터베이스에서 데이터 읽어 오기=======================================================================================================
	public String readDB(int col) throws Exception {
		// 컨텐츠 프로바이더가 제공하는 데이터베이스가 나타내는 URI
		Uri uri = Uri.parse("content://com.client.logininfo.user2/");
		// Uri uri = Uri.parse("http://localhost/join.do?cmd=insert");

		// 컨텐츠 프로바이더가 제공하는 데이터베이스로 접근, index 0에 저장된 회원정보 확인
		Cursor c = this.getContentResolver().query(uri,
				new String[] { "inx", "settime", "setphone", "name" },
				"inx='0'", null, null);
		if (c.getCount() == 0) {
			Log.e("안녕하세요==========", "" + c.getCount());
			throw new Exception();
		}
		c.moveToFirst();

		Log.e("RegisterForm @readDB", "회원정보 리스트 ========================"
				+ c.getString(col));
		return c.getString(col);
	}

}
