package com.example.mobileassign_mydiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.CursorWindow;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRVDiary;          //리스트 뷰
    DiaryListAdaptor mAdaptor;        //리사이클러 뷰와 연동할 어댑터
    ArrayList<DiaryModel> mListDiary;    //리스트에 표현할 다이어리 데이터들(배열)
    DatabaseHelper mDatabaseHelper;          //데이터베이스 헬퍼 클래스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //row too big to fit into cursorwindow requiredpos=0, totalrows=1; 에러 해결위해 추가
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDatabaseHelper = new DatabaseHelper(this);

        mListDiary = new ArrayList<>();
        mRVDiary = findViewById(R.id.rv_diary);

        mAdaptor = new DiaryListAdaptor();   //리사이클러 뷰 어댑터 인스턴스 생성

        mRVDiary.setAdapter(mAdaptor);


        // 액티비티(화면)이 실행될 때 가장 먼저 호출되는 곳
        FloatingActionButton floatingActionButton = findViewById(R.id.btn_create);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 작성하기 버튼을 누를 때 호출되는 곳
                Intent intent = new Intent(MainActivity.this, DiaryDetailActivity.class);
                startActivity(intent);
            }
        });

        EditText etSearch = findViewById(R.id.et_search);    //검색란
        ImageView imSearchBtn = findViewById(R.id.iv_serach_btn);    //검색버튼
        imSearchBtn.setOnClickListener(new View.OnClickListener() {    //검색버튼을 눌렀을때
            @Override
            public void onClick(View v) {
                String keyword = etSearch.getText().toString();
                setLoadSearchList(keyword);
            }
        });



    }

    // 액티비티가 재개될 때(처음 시작될때 포함)
    @Override
    protected void onResume() {
        super.onResume();
        //get load list
        EditText etSearch = findViewById(R.id.et_search);
        String keyword = etSearch.getText().toString();

        if (keyword.isEmpty())        //검색란에 키워드가 없다면
            setLoadRecentList();          //전체 다 불러옴
        else                           //키워드가 있으면 키워드를 포함한 다이어리들만 불러옴
            setLoadSearchList(keyword);

    }

    private void setLoadRecentList(){
        // 최근 데이터 베이스 정보를 가지고와서 리사이클러 뷰에 갱신

        // 이전에 배열리스트에  저장된 데이터가 있으면 비워버림
        if (!mListDiary.isEmpty()){
            mListDiary.clear();
        }

        mListDiary = mDatabaseHelper.getDiaryListFromDB();   //데이터베이스로부터 db를 확인하여 가지고옴
        mAdaptor.setListInit(mListDiary);

    }

    // 키워드를 검색하여 키워드를 포함한 다이어리들만 리사이클러 뷰에 갱신
    private void setLoadSearchList(String keyword){
        if (!mListDiary.isEmpty()){
            mListDiary.clear();
        }

        mListDiary = mDatabaseHelper.getDiarySearchListFromDB(keyword);   //데이터베이스로부터 db를 확인하여 가지고옴
        mAdaptor.setListInit(mListDiary);
    }
}