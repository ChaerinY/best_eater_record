package com.example.mobileassign_mydiary;

// 다이어리 리스트 아이템을 구성하는 모델

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class DiaryModel implements Serializable {      //직렬화. 다이어리모델 클래스를 인텐트로 다음액티비티로 넘길수있게해줌
    int id;            //게시물 고유 ID 값
    String title;      //게시물 제목
    String content;     //게시물 내용
    int rating;         //평점 (1~5점)
    String userDate;    //사용자가 지정한 날짜
    String writeDate;    //게시글 작성한 날짜

    String location;  //위치
    int category;  //종류 {밥집, 술집, 카페, 집밥, 기타}
    Bitmap foodImage;

    //getter & setter


    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public Bitmap getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(Bitmap foodImage) {
        this.foodImage = foodImage;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }
}
