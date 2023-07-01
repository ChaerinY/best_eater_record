package com.example.mobileassign_mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.radiobutton.MaterialRadioButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/* 상세보기 혹은 수정하기 화면으로 사용 */

public class DiaryDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView mTvDate;   //날짜 설정 텍스트
    private EditText mEtTitle, mEtContent, mEtLocation;  //글 제목, 내용, 위치
    private RadioGroup mRgRating, mRgCategory;
    private int mSelectedCategory = -1;
    private int mSelectedRating = -1; // 선택된 평점 값 (1~5까지)

    private String mBeforeDate = "";  //intent로 받아낸 게시글
    private String mDetailMode = "";  //인텐트로 받아낸 게시글 모드
    private String mSelectedUserDate = ""; //선택된 날짜 값

    private DatabaseHelper mDatabaseHelper; //database 유틸 객체


    // 이미지 불러오기를 위한 함수
    static final int REQUEST_CODE = 1;
    ImageView iv_food;        //음식 사진
    Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE) {
            uri = data.getData();
            setImage(uri);
        }
    }

    private void setImage(Uri uri) {
        try{
            InputStream in = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            iv_food.setImageBitmap(bitmap);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    // 액티비티가 실행될때
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 데이터 베이스 객체 생성
        mDatabaseHelper = new DatabaseHelper(this);

        mTvDate = findViewById(R.id.tv_date);
        mEtTitle = findViewById(R.id.et_title);
        mEtContent = findViewById(R.id.et_content);
        mRgRating = findViewById(R.id.rg_rating);

        mEtLocation = findViewById(R.id.et_location);
        mRgCategory = findViewById(R.id.rg_category);


        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView iv_check = findViewById(R.id.iv_check);

        Button btn_imgopen = findViewById(R.id.btn_openimg);
        iv_food = findViewById(R.id.iv_foodpic);
        btn_imgopen.setOnClickListener(this);

        ImageView iv_edit = findViewById(R.id.iv_edit);
        iv_edit.setOnClickListener(this);
        ImageView iv_del = findViewById(R.id.iv_trash);
        iv_del.setOnClickListener(this);

        iv_back.setOnClickListener(this);      //onClick에다가 붙여라
        iv_check.setOnClickListener(this);
        mTvDate.setOnClickListener(this);

        // 기본으로 설정된 날짜의 값을 지정 (현재 시간 기준으로 셋팅)
        mSelectedUserDate = new SimpleDateFormat("yyyy-MM-dd (E)", Locale.KOREAN).format(new Date());
        mTvDate.setText(mSelectedUserDate);

        //이전 액티비티로 부터 값을 전달 받기

        Intent intent = getIntent();
        if(intent.getExtras() != null){
            //데이터들이 있다면

            String BeforeDate = intent.getStringExtra("beforeDate");
            DiaryModel diaryModel = mDatabaseHelper.getDiaryByDate(BeforeDate);

            //DiaryModel diaryModel = (DiaryModel) intent.getSerializableExtra("diaryModel");
            mDetailMode = intent.getStringExtra("mode");
            mBeforeDate = diaryModel.getWriteDate();      //게시글 database update 쿼리문 처리를 위해서 여기서 받아둠.

            mEtTitle.setText(diaryModel.getTitle());
            mEtContent.setText(diaryModel.getContent());
            int rating = diaryModel.getRating();
            ((MaterialRadioButton) mRgRating.getChildAt(rating)).setChecked(true);
            mSelectedUserDate = diaryModel.getUserDate();
            mTvDate.setText(diaryModel.getUserDate());

            int category = diaryModel.getCategory();
            ((MaterialRadioButton) mRgCategory.getChildAt(category)).setChecked(true);
            mEtLocation.setText(diaryModel.getLocation());
            iv_food.setImageBitmap(diaryModel.getFoodImage());

            if (mDetailMode.equals("modify")){
                //수정모드
                TextView tv_newtext_title = findViewById(R.id.tv_newtext_title);
                tv_newtext_title.setText("수정하기");

            }
            else if (mDetailMode.equals("detail")){
                //상세모드
                TextView tv_newtext_title = findViewById(R.id.tv_newtext_title);
                tv_newtext_title.setText("상세보기");

                //읽기전용으로 표시햐
                iv_edit.setVisibility(View.VISIBLE);
                iv_del.setVisibility(View.VISIBLE);
                mEtTitle.setEnabled(false);
                mEtContent.setEnabled(false);
                mEtLocation.setEnabled(false);
                mTvDate.setEnabled(false);
                mEtTitle.setEnabled(false);
                for (int i = 0 ; i<mRgRating.getChildCount(); i++){
                    mRgRating.getChildAt(i).setEnabled(false);
                }
                for (int i = 0 ; i<mRgCategory.getChildCount(); i++){
                    mRgCategory.getChildAt(i).setClickable(false);
                }
                iv_check.setVisibility(View.GONE);
                btn_imgopen.setVisibility(View.INVISIBLE);
            }


        }



    }

    @Override
    public void onClick(View v) {
        // setOnClickListener가 붙어있는 뷰들은 클릭이 발생하면 모두 이 곳을 지나치게 됨
        switch (v.getId()){
            case R.id.iv_back:
                //뒤로가기
                finish();     //현재 액티비티 종료
                break;

            case R.id.iv_check:
                //w작성 완료 버튼

                mSelectedRating = mRgRating.indexOfChild(findViewById(mRgRating.getCheckedRadioButtonId()));  //라디오 그룹에서 현재 체크된 값 추출
                mSelectedCategory = mRgCategory.indexOfChild(findViewById(mRgCategory.getCheckedRadioButtonId()));

                //입력 필드가 비어있는지 체크
                if (mEtTitle.getText().length()==0 || mEtContent.getText().length()==0|| mEtLocation.getText().length()==0){
                    //에러
                    Toast.makeText(this, "입력되지 않은 필드가 존재합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //평점선택이 되어있는지 체크
                if (mSelectedRating == -1){
                    Toast.makeText(this, "평점을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSelectedCategory == -1){
                    Toast.makeText(this, "분류를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //데이터 저장
                String location = mEtLocation.getText().toString();
                Bitmap food_img = ((BitmapDrawable)iv_food.getDrawable()).getBitmap();


                String title = mEtTitle.getText().toString();       //제목 입력값
                String content = mEtContent.getText().toString();          //내용 입력값
                String userdate = mSelectedUserDate;  //사용자가 선택한 일시
                if (userdate.equals("")){
                    userdate = mTvDate.getText().toString();
                }
                String writedate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREAN).format(new Date());  //나중에 데이터베이스에 저장하기위해 시점 정확히

                // 액티비티의 현재 모드에 따라 데이터베이스에 저장
                if (mDetailMode.equals("modify")){
                    //수정모드냐
                    mDatabaseHelper.setUpdateDiaryList(title, content, mSelectedRating, userdate, writedate, mBeforeDate, location, mSelectedCategory, food_img);
                    Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //작성모드냐
                    mDatabaseHelper.setInsertDiaryList(title, content, mSelectedRating, userdate, writedate, location, mSelectedCategory, food_img);
                    Toast.makeText(this, "작성되었습니다.", Toast.LENGTH_SHORT).show();
                }

                finish(); //현재 액티비티 종료

                break;

            case R.id.tv_date:
                //날짜 설정 텍스트

                // 달력을 띄워서 사용자에게 일시를 입력 받는다

                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //달력에 선택된 날짜를 가지고와서 다시 캘린더 함수에 넣은 뒤 사용자가 선택한 요일을 알아낸다
                        Calendar innerCal = Calendar.getInstance();
                        innerCal.set(Calendar.YEAR, year);
                        innerCal.set(Calendar.MONTH, month);
                        innerCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        mSelectedUserDate = new SimpleDateFormat("yyyy-MM-dd (E)", Locale.KOREAN).format(innerCal.getTime());
                        mTvDate.setText(mSelectedUserDate);     //텍스트뷰에 날짜 셋팅
                        mTvDate.setTextColor(Color.BLACK);
                    }
                }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));

                dialog.show();    //다이얼로그 팝업 활성화

                break;

            case R.id.btn_openimg:        //이미지 불러오기 버튼
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
                break;

            case R.id.iv_edit:  //다이어리 수정버튼
                Intent diaryDetailIntent = new Intent(this, DiaryDetailActivity.class);
                diaryDetailIntent.putExtra("beforeDate", mBeforeDate);      // 현재 키 값을 이용해서
                diaryDetailIntent.putExtra("mode", "modify");      // 수정모드로 액티비티열고
                this.startActivity(diaryDetailIntent);
                finish();    //현재액티비티는 파괴
                break;

            case R.id.iv_trash:   //다이어리 삭제버튼
                mDatabaseHelper.setDeleteDiaryList(mBeforeDate);
                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;


        }
    }
}