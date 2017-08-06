/**
 * Copyright (c) 2015 (author or corporation).
 * All right reserved.
 *
 * This software is the confidential and proprietary information of (author or corporation).
 * You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with (author or corporation).
 *
 */

package com.example.alarmexample70821;

import android.content.ContentProvider;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**  
 * #name : User.java
 * #description : DB를 제공하는 컨텐츠 프로바이더를 이용한 회원정보 DB 및 테이블 생성, 데이터 관리
 * 
 * #revision 
 * #
 * #    Date          Author                  Description
 * # ---------       ---------     -------------------------------
 * # 2015.08.12   		ih      		 Initial Creation
 * 
 * @author ih
 * @since 2015. 08.01
 * @version 1.0
 * 
 */
public class MyContentProvider extends ContentProvider {
	private final static String DB_NAME ="userinfo.db";//DB명
	private final static String DB_TABLE="userinfo"; //테이블명
	private final static int DB_VERSION=1; //버전
	
	private SQLiteDatabase db;	// 데이터베이스
	
	/**
	 * 컨텐츠 프로바이더의 초기화
	 */	
	@Override
	public boolean onCreate() {
		//데이터베이스의 생성 （3）
		DBHelper dbHelper=new DBHelper(getContext());
		db=dbHelper.getWritableDatabase();
		return (db!=null);
	}
	
	/**
	 * 데이터베이스의 select 쿼리 명령
	 * @param uri 컨텐츠 프로바이더 URI
	 * @param columns 획득하고자 하는 데이터 컬럼
	 * @param selection 조건절
	 * @param selectionArgs 조건겂
	 * @param sortOrder 정렬순서
	 */	
	//데이터베이스의 쿼리 명령 （4）
	@Override
		public Cursor query(Uri uri,String[] columns,String selection,
		String[] selectionArgs,String sortOrder) {
		return db.query(DB_TABLE,columns,selection,
			selectionArgs,null,null,null);
	}
	
	/**
	 * 데이터베이스의 update 쿼리 명령
	 * @param uri 컨텐츠 프로바이더 URI
	 * @param values 갱신하고자 하는 데이터 컬럼
	 * @param selection 조건절
	 * @param selectionArgs 조건겂
	 */	
	@Override
	public int update(Uri uri,ContentValues values,
		String selection,String[] selectionArgs) {
		int num=db.update(DB_TABLE,values,null,null);
		if (num==0) db.insert(DB_TABLE,"",values);
		return 1;
	}
		
	
	/**
	 * 데이터베이스 헬퍼 정의
	 */	
	private static class DBHelper extends SQLiteOpenHelper {
		//생성자
		public DBHelper(Context context) {
			super(context,DB_NAME,null,DB_VERSION);
		}
			
		/**
		 * 데이터베이스의 생성
		 */	
		@Override
			public void onCreate(SQLiteDatabase db) {
			db.execSQL(
				"create table if not exists "+
				DB_TABLE+"(inx text primary key, settime text, setphone text, name text)");
		}
		
		/**
		 * 데이터베이스의 업그레이드
		 */			
		@Override
			public void onUpgrade(SQLiteDatabase db,
			int oldVersion,int newVersion) {
			//db.execSQL("drop table if exists "+DB_TABLE);
			// onCreate(db);
		}
	}


	/**
	 * 레코드 삭제
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int count = db.delete(DB_TABLE, selection, selectionArgs); 
	  
		return 0;
	}

	/**
	 * 레코드 타입 획득
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 레코드 추가
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}


}