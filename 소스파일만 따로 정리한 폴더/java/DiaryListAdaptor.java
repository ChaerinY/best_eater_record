package com.example.mobileassign_mydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiaryListAdaptor extends RecyclerView.Adapter<DiaryListAdaptor.ViewHolder> {

    ArrayList<DiaryModel> mListDiary;    //데이터들을 들고있는 자료형
    Context mContext;
    DatabaseHelper mDatabaseHelper;       //데이터베이스 헬퍼 클래스

    @NonNull
    @Override
    public DiaryListAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아이템 뷰가 최초로 생성이 될때 호출되는 곳
        mContext = parent.getContext();
        mDatabaseHelper = new DatabaseHelper(mContext);
        View holder = LayoutInflater.from(mContext).inflate(R.layout.list_item_diary, parent,false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryListAdaptor.ViewHolder holder, int position) {
        // 생성된 아이템 뷰가 실제 연동이 되어지는 곳

        // 평점의 경우의 수 작성
        int rating = mListDiary.get(position).getRating();

        switch (rating){
            case 0:
                holder.iv_rating.setImageResource(R.drawable.img_rating1);
                break;
            case 1:
                holder.iv_rating.setImageResource(R.drawable.img_rating2);
                break;
            case 2:
                holder.iv_rating.setImageResource(R.drawable.img_rating3);
                break;
            case 3:
                holder.iv_rating.setImageResource(R.drawable.img_rating4);
                break;
            case 4:
                holder.iv_rating.setImageResource(R.drawable.img_rating5);
                break;
        }

        // 분류 경우의수
        int category = mListDiary.get(position).getCategory();

        switch (category){
            case 0:
                holder.tv_category.setText("#밥집");
                break;
            case 1:
                holder.tv_category.setText("#술집");
                break;
            case 2:
                holder.tv_category.setText("#카페");
                break;
            case 3:
                holder.tv_category.setText("#집밥");
                break;
            case 4:
                holder.tv_category.setText("#기타");
                break;
        }

        String title = mListDiary.get(position).getTitle();
        String userDate = mListDiary.get(position).getUserDate();

        String location = mListDiary.get(position).getLocation();
        String content = mListDiary.get(position).getContent();
        Bitmap img = mListDiary.get(position).getFoodImage();

        holder.tv_title.setText(title);
        holder.tv_user_date.setText(userDate);
        holder.tv_content.setText(content);
        holder.tv_location.setText("#"+location);
        holder.iv_food_img.setImageBitmap(img);

    }

    @Override
    public int getItemCount() {
        // 아이템 뷰의 총 개수를 관리하는 곳
        return mListDiary.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_rating, iv_food_img;
        TextView tv_title, tv_user_date, tv_content, tv_category, tv_location;

        // 하나의 아이템 뷰
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_rating = itemView.findViewById(R.id.iv_rating);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_user_date = itemView.findViewById(R.id.tv_user_date);

            tv_content = itemView.findViewById(R.id.tv_content);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_location = itemView.findViewById(R.id.tv_location);

            iv_food_img = itemView.findViewById(R.id.iv_pic);

            // 일반 클릭 (상세보기)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //현재 클릭이 된 위치 (0부터 시작)
                    int currentPosition = getAdapterPosition();
                    // 현재 클릭된 리스트 아이템 정보를 가지는 변수
                    DiaryModel diaryModel = mListDiary.get(currentPosition);

                    //화면 이동 및 다이어리 데이터를 다음 액티비티로 전달
                    Intent diaryDetailIntent = new Intent(mContext, DiaryDetailActivity.class);
                    diaryDetailIntent.putExtra("beforeDate", diaryModel.getWriteDate());      // 다이어리 데이터 넘기기
                    /*
                    * diaryDetailIntent.putExtra("Diary", diaryModel); 로 했다가
                    * parcelable encountered ioexception writing serializable object 오류랑
                    * java.lang.runtimeexception: could not copy bitmap to parcel blob. 발생!!
                    *
                    * 다이어리 모델 클래스에 bitmap이 포함되어있는데 비트맵은 serializable 하지 않을뿐만 아니라
                    * 인텐트로는 000kb단위의 작은 크기의 이미지밖에 못넘김.
                    * 따라서 키값이 되는 값만 넘겨주고 키값을 이용해 db에서 가져오게한다.
                    *  DatabaseHelper.java에서
                    *  public DiaryModel getDiaryByDate(String _beforeDate) 작성 후
                    *  다이어리 값을 받을 액티비티에서 위 함수 사용.
                    *
                    * */
                    diaryDetailIntent.putExtra("mode", "detail");      // 상세보기 모드로 설정
                    //Toast.makeText(mContext,"확인용",Toast.LENGTH_SHORT).show();
                    mContext.startActivity(diaryDetailIntent);


                }
            });

            // 선택지 옵션 팝업 (수정, 삭제)
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //현재 클릭이 된 위치 (0부터 시작)
                    int currentPosition = getAdapterPosition();
                    DiaryModel diaryModel = mListDiary.get(currentPosition);

                    //버튼 선택지 배열
                    String[] strChoiceArray = { "수정", "삭제"};

                    //팝업 표시
                    new AlertDialog.Builder(mContext).setTitle("원하시는 동작을 선택하세요").setItems(strChoiceArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which==0){
                                // 수정하기 버튼 눌렀을때
                                Intent diaryDetailIntent = new Intent(mContext, DiaryDetailActivity.class);
                                diaryDetailIntent.putExtra("beforeDate", diaryModel.getWriteDate());      // 다이어리 데이터 넘기기
                                diaryDetailIntent.putExtra("mode", "modify");      // 상세보기 모드로 설정
                                mContext.startActivity(diaryDetailIntent);

                            }
                            else {
                                // 삭제하기 버튼 눌렀을때
                                // 데이터베이스에서 삭제
                                mDatabaseHelper.setDeleteDiaryList(diaryModel.getWriteDate());

                                //ui에서 삭제
                                mListDiary.remove(currentPosition);     //배열에서 삭제
                                notifyItemRemoved(currentPosition);                    //리스트뷰에서 제거
                            }
                        }
                    }).show();

                    return false;
                }
            });

        }
    }

    public void setListInit(ArrayList<DiaryModel> lstDiary){

        //데이터 리스트 update
        mListDiary = lstDiary;
        notifyDataSetChanged();  // 리스트뷰 새로고침

    }
}
