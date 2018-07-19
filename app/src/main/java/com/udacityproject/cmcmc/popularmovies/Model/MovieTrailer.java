package com.udacityproject.cmcmc.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieTrailer implements Parcelable{
    private String id;
    private String iso_639_1;
    private String iso_3166_1;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    public MovieTrailer(Parcel in){
        String[] strData = new String[7];
        in.readStringArray(strData);
        this.id = strData[0];
        this.iso_639_1 = strData[1];
        this.iso_3166_1 = strData[2];
        this.key = strData[3];
        this.name = strData[4];
        this.site = strData[5];
        this.type = strData[6];
        this.size = in.readInt();
    }
    public MovieTrailer(String id, String iso_639_1, String iso_3166_1, String key, String name, String site, String type, int size) {
        this.id = id;
        this.iso_639_1 = iso_639_1;
        this.iso_3166_1 = iso_3166_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
        this.size = size;
    }
    @Override
    public int describeContents(){
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeStringArray(new String[]{
                this.id,
                this.iso_639_1,
                this.iso_3166_1,
                this.key,
                this.name,
                this.site,
                this.type
        });
        dest.writeInt(this.size);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public MovieTrailer createFromParcel(Parcel in){
            return new MovieTrailer(in);
        }
        public MovieTrailer[] newArray(int size){
            return new MovieTrailer[size];
        }
    };
    public MovieTrailer(String id){
        this.id = id;
    }
    public MovieTrailer(String id, String key, String name, String site, int size, String type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }
    public MovieTrailer(JSONObject jsonTrailerInfo){
        if(jsonTrailerInfo != null){
            try{
                this.id = jsonTrailerInfo.getString("id");
                this.iso_639_1 = jsonTrailerInfo.getString("iso_639_1");
                this.iso_3166_1 = jsonTrailerInfo.getString("iso_3166_1");
                this.key = jsonTrailerInfo.getString("key");
                this.name = jsonTrailerInfo.getString("name");
                this.site = jsonTrailerInfo.getString("site");
                this.size = jsonTrailerInfo.getInt("size");
                this.type = jsonTrailerInfo.getString("type");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public String toString(){
        return this.site + " trailer: " + this.name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public String getIso_3166_1() {
        return iso_3166_1;
    }

    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
