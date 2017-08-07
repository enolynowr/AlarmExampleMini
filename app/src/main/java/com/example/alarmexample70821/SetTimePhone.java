package com.example.alarmexample70821;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetTimePhone extends Activity {

	Button btnSetSave;
	String user_input_time;
	TextView tv_input_parent_phonenumber;	
	TextView tv_input_time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_time_phone);
		
		btnSetSave = (Button) findViewById(R.id.set_save);//db에 설정을 저장하는 버튼
		btnSetSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String result = UserData.u_name + UserData.u_phone_number
						+ UserData.u_set_time;
				Log.e("더한 값은================", result);
				Log.e("더한 값은================", UserData.u_name);
				Log.e("더한 값은================", UserData.u_phone_number);
				Log.e("더한 값은================", UserData.u_set_time);

				try {
					writeDB(UserData.u_set_time, UserData.u_phone_number,
							UserData.u_name);

					Intent moveMainAct = new Intent(SetTimePhone.this, MainActivity.class);
					startActivity(moveMainAct);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

//입력받은 시간을 불러와 저장하는 텍스트 뷰========================================================================================			
		tv_input_time = (TextView)findViewById(R.id.time_info);//시간입력출력 텍스트뷰
		tv_input_time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uInputTime();
			}
		});
	
//입력받은 전화번호부를 불러와 저장하는 텍스트 뷰===================================================================================		
		tv_input_parent_phonenumber = (TextView)findViewById(R.id.call_info);//전화번호부입력출력 텍스트뷰	
		tv_input_parent_phonenumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent contact_picker = new Intent(Intent.ACTION_PICK);
				contact_picker.setType(ContactsContract.Contacts.CONTENT_TYPE);
				startActivityForResult(contact_picker, 2);

			}
		});
	}

	
//전화번호부를 불러오는 메소드=============================================================================================	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 2) {
				Log.e("IntentTester", data.getData().toString());

				Uri dataUri = data.getData();
				Cursor cursor = managedQuery(dataUri, null, null, null, null);

				while (cursor.moveToNext()) {
					int getcolumnId = cursor
							.getColumnIndex(ContactsContract.Contacts._ID);

					String id = cursor.getString(getcolumnId);
					String people_Name = cursor
							.getString(cursor
									.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

					String hasPhoneNumber = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

					if (hasPhoneNumber.equalsIgnoreCase("1")) {
						hasPhoneNumber = "true";
					} else {
						hasPhoneNumber = "false";
					}

					String people_Number = null;
					if (Boolean.parseBoolean(hasPhoneNumber)) {
						Cursor phones = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + id, null, null);

						while (phones.moveToNext()) {
							people_Number = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						}

						phones.close(); // End
					}

					Log.e("test", "name: " + people_Name);
					Log.e("test", "number: " + people_Number);

					TextView callInfo = (TextView) findViewById(R.id.call_info);

					callInfo.setText("번호 :" + people_Number + "\n" + "이름 :"
							+ people_Name);

					// 전화번호부에서 불러온 이름 전화 번호를 분사
					UserData.u_name = people_Name;
					UserData.u_phone_number = people_Number;

					String u_phone_info = "이름 : " + people_Name + "\n"
							+ "전화번호 : " + people_Number;

					MainActivity.main_tv_setted_phone.setText(u_phone_info);

				}
			}

			super.onActivityResult(requestCode, resultCode, data);
		}
	}

// 시간을 입력 받는 메소드==================================================================================================
	public void uInputTime() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("緊急電話　時間設定");
		alert.setMessage("時間を入力してください");

		final EditText name = new EditText(this);
		name.setInputType(InputType.TYPE_CLASS_NUMBER);
		name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
		alert.setView(name);

		alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				user_input_time = name.getText().toString();
				Log.e("유저이름==========", user_input_time);

				TextView callInfo = (TextView) findViewById(R.id.time_info);

				callInfo.setText("설정 시간 : " + user_input_time + " 시간");

				// 다이얼로그에서 입력받은 설정 시간 값을 텍스트뷰에 분사
				UserData.u_set_time = user_input_time;
				MainActivity.main_tv_settedTime
						.setText(user_input_time + " 시간");

			}
		});

		alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		alert.show();
	}

	/**
	 * DB에 쓰기
	 * 
	 * @param  , pw, name 등등 회원정보를 저장
	 * @return none
	 */
	private void writeDB(String settime, String setphone, String name)
			throws Exception {

		// 컨텐츠 프로바이더가 제공하는 데이터베이스를 나타내는 URI （1）
		Uri uri = Uri.parse("content://com.client.logininfo.user2/");

		// 컨텐츠 프로바이더가 제공하는 데이터베이스로 접근 （2）
		ContentValues cv = new ContentValues();
		cv.put("inx", "0");
		cv.put("settime", settime);
		cv.put("setphone", setphone);
		cv.put("name", name);
		this.getContentResolver().update(uri, cv, null, null);
	}

	/**
	 * DB로부터 읽기
	 * 
	 * @param col
	 * @return col 정보
	 */
	private String readDB(int col) throws Exception {
		// 컨텐츠 프로바이더가 제공하는 데이터베이스가 나타내는 URI
		Uri uri = Uri.parse("content://com.client.logininfo.user2/");
		// Uri uri = Uri.parse("http://localhost/join.do?cmd=insert");

		// 컨텐츠 프로바이더가 제공하는 데이터베이스로 접근, index 0에 저장된 회원정보 확인
		Cursor c = this.getContentResolver().query(uri,
				new String[] { "inx", "settime", "setphone" }, "inx='0'", null,
				null);
		if (c.getCount() == 0)
			throw new Exception();
		c.moveToFirst();

		Log.e("RegisterForm @readDB", "회원정보 리스트 ====================");
		return c.getString(col);
	}

}
