package com.example.mobileassign_mydiary;

// 데이터베이스 관리 클래스

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


//SQLite : 안드로이드에서 지원하는 앱 내부 데이터베이스 시스템
//내장db이므로 앱을 삭제하면 데이터 날아감
public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;
    private static int DB_VERSION = 1;
    private static String DB_NAME = "MyFoodDiary.db";

    //생성자
    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //데이터베이스 생성
        //sql이란 데이터베이스에 접근하기위해서 명령을 내리는 쿼리문.
        db.execSQL("CREATE TABLE IF NOT EXISTS DiaryInfo " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, " +
                "content TEXT NOT NULL, rating INTEGER NOT NULL, userDate TEXT NOT NULL, writeDate TEXT NOT NULL, " +
                "location TEXT NOT NULL, category INTEGER NOT NULL, img BLOB NOT NULL)");
        //테이블을 생성

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


    public DiaryModel getDiaryByDate(String _beforeDate){
        DiaryModel diary = new DiaryModel();
        SQLiteDatabase db = getReadableDatabase();  //read 데이터베이스
        Cursor cursor = db.rawQuery("SELECT * FROM DiaryInfo WHERE writeDate = '"+ _beforeDate +"'", null);     //작성일자에 대해 내림차순으로 정렬
        if (cursor.getCount() != 0){
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
                String userDate = cursor.getString(cursor.getColumnIndexOrThrow("userDate"));
                String writeDate = cursor.getString(cursor.getColumnIndexOrThrow("writeDate"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                int category = cursor.getInt(cursor.getColumnIndexOrThrow("category"));

                byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow("img"));
                Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);


                // create data class

                diary.setId(id);
                diary.setTitle(title);
                diary.setRating(rating);
                diary.setContent(content);
                diary.setUserDate(userDate);
                diary.setWriteDate(writeDate);
                diary.setLocation(location);
                diary.setCategory(category);
                diary.setFoodImage(img);

            }
        }
        cursor.close();

        return diary;
    }


    /**
     * 다이어리 작성 데이터를 db에 저장
     **/
    public void setInsertDiaryList(String _title, String _content, int _rating, String _userDate, String _writeDate, String _location, int _category, Bitmap _img){
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("INSERT INTO DiaryInfo (title, content, rating, userDate, writeDate, location, category) VALUES ('"+ _title +"','"+ _content +"','"+ _rating +"','"+ _userDate +"','"+ _writeDate +"','"+ _location +"','"+ _category +"')");

        //이미지 저장
        Drawable _drawable = new BitmapDrawable(_img);

        byte[] foodImg = getByteArrayFromDrawable(_drawable);
        SQLiteStatement p = db.compileStatement("INSERT INTO DiaryInfo(title, content, rating, userDate, writeDate, location, category, img) VALUES ('"+ _title +"','"+ _content +"','"+ _rating +"','"+ _userDate +"','"+ _writeDate +"','"+ _location +"','"+ _category +"',?)");
        p.bindBlob(1, foodImg);
        p.execute();
    }

    //다이어리 작성 데이터를 조회하여 가지고 온다.
    public ArrayList<DiaryModel> getDiaryListFromDB(){
        ArrayList<DiaryModel> lstDiary = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();  //read 데이터베이스
        Cursor cursor = db.rawQuery("SELECT * FROM DiaryInfo ORDER BY userDate DESC", null);     //작성일자에 대해 내림차순으로 정렬
        if (cursor.getCount() != 0){
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
                String userDate = cursor.getString(cursor.getColumnIndexOrThrow("userDate"));
                String writeDate = cursor.getString(cursor.getColumnIndexOrThrow("writeDate"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                int category = cursor.getInt(cursor.getColumnIndexOrThrow("category"));

                byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow("img"));
                Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);


                // create data class

                DiaryModel diaryModel = new DiaryModel();
                diaryModel.setId(id);
                diaryModel.setTitle(title);
                diaryModel.setRating(rating);
                diaryModel.setContent(content);
                diaryModel.setUserDate(userDate);
                diaryModel.setWriteDate(writeDate);
                diaryModel.setLocation(location);
                diaryModel.setCategory(category);
                diaryModel.setFoodImage(img);

                lstDiary.add(diaryModel);
            }
        }
        cursor.close();

        return lstDiary;
    }


    //기존 작성 데이터를 수정한다. _beforeDate는 수정을 위한 키값
    public void setUpdateDiaryList(String _title, String _content, int _rating, String _userDate, String _writeDate, String _beforeDate, String _location, int _category, Bitmap _foodImage){
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("UPDATE DiaryInfo SET title='"+ _title +"', content='"+ _content +"', rating='"+ _rating +"', userDate='"+ _userDate +"', writeDate='"+ _writeDate +"', location ='"+ _location +"', category='"+ _category +"' WHERE writeDate = '"+ _beforeDate +"'");

        //이미지 저장
        Drawable _drawable = new BitmapDrawable(_foodImage);
        byte[] foodImg = getByteArrayFromDrawable(_drawable);
        SQLiteStatement p = db.compileStatement("UPDATE DiaryInfo SET title='"+ _title +"', content='"+ _content +"', rating='"+ _rating +"', userDate='"+ _userDate +"', writeDate='"+ _writeDate +"', location ='"+ _location +"', category='"+ _category +"', img=? WHERE writeDate = '"+ _beforeDate +"'");
        p.bindBlob(1, foodImg);
        p.execute();

    }

    //기존 작성 데이터를 삭제한다.
    public void setDeleteDiaryList(String _writeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM DiaryInfo WHERE writeDate = '"+ _writeDate +"'");
    }

    // 이미지를 db에 저장하기위해 비트맵에서 byte[]로 변경
    public byte[] getByteArrayFromDrawable(Drawable d) {
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        return data;
    }


    //넘겨받은 키워드가 들어간 데이터들만 가지고온다
    public ArrayList<DiaryModel> getDiarySearchListFromDB(String keyword){
        ArrayList<DiaryModel> lstDiary = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();  //read 데이터베이스
        Cursor cursor = db.rawQuery("SELECT * FROM DiaryInfo WHERE title LIKE '%"+ keyword +"%'OR content LIKE '%"+ keyword +"%' OR location LIKE '%"+ keyword +"%' ORDER BY userDate DESC", null);     //작성일자에 대해 내림차순으로 정렬
        if (cursor.getCount() != 0){
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));
                String userDate = cursor.getString(cursor.getColumnIndexOrThrow("userDate"));
                String writeDate = cursor.getString(cursor.getColumnIndexOrThrow("writeDate"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                int category = cursor.getInt(cursor.getColumnIndexOrThrow("category"));

                byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow("img"));
                Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);


                // create data class

                DiaryModel diaryModel = new DiaryModel();
                diaryModel.setId(id);
                diaryModel.setTitle(title);
                diaryModel.setRating(rating);
                diaryModel.setContent(content);
                diaryModel.setUserDate(userDate);
                diaryModel.setWriteDate(writeDate);
                diaryModel.setLocation(location);
                diaryModel.setCategory(category);
                diaryModel.setFoodImage(img);

                lstDiary.add(diaryModel);
            }
        }
        cursor.close();

        return lstDiary;
    }

}
