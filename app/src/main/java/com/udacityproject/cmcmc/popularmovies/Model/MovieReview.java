package com.udacityproject.cmcmc.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieReview implements Parcelable{
    private String id;
    private String author;
    private String content;
    private String url;

    MovieReview(Parcel in){
        this.id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(author);
        out.writeString(content);
        out.writeString(url);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>(){
        public MovieReview createFromParcel(Parcel in){
            return new MovieReview(in);
        }
        public MovieReview[] newArray(int size){
            return new MovieReview[size];
        }
    };
    //end parcelable stuff

    public MovieReview(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }
    public MovieReview(JSONObject jsonReviewInfo){
        if(jsonReviewInfo != null){
            try{
                this.id = jsonReviewInfo.getString("id");
                this.author = jsonReviewInfo.getString("author");
                this.content = jsonReviewInfo.getString("content");
                this.url = jsonReviewInfo.getString("url");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public String toString(){
        return this.getAuthor() + " says, \"" + this.getContent() + "\"";
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
