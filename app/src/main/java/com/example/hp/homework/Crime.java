package com.example.hp.homework;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Sergii Varenyk on 10.07.15.
 */
public class Crime {

    private static final String JSON_ID="id";
    private static final String JSON_TITLE="title";
    private static final String JSON_SOLVED="solved";
    private static final String JSON_DATE="date";
    private static final String JSON_PHOTO="photo";
    private static final String JSON_SUSPECT_NAME="suspectName";
    private static final String JSON_SUSPECT_PHONE="suspectPhone";

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto;
    private String mSuspectName;
    private String mSuspectPhone;
    //private SimpleDateFormat dateFormatCrime = new SimpleDateFormat("EEEE, MMM dd, yyyy");

    public Crime(){
        //generate unic identifier
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject jsonObject) throws JSONException{
        mId=UUID.fromString(jsonObject.getString(JSON_ID));
        mTitle=jsonObject.getString(JSON_TITLE);
        mSolved=jsonObject.getBoolean(JSON_SOLVED);
        mDate=new Date(jsonObject.getLong(JSON_DATE));
        if (jsonObject.has(JSON_PHOTO)){
            mPhoto=new Photo(jsonObject.getJSONObject(JSON_PHOTO));
        }
        if (jsonObject.has(JSON_SUSPECT_NAME)){
            mSuspectName=jsonObject.getString(JSON_SUSPECT_NAME);
        }
        if (jsonObject.has(JSON_SUSPECT_PHONE)){
            mSuspectPhone=jsonObject.getString(JSON_SUSPECT_PHONE);
        }
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put(JSON_ID,mId.toString());
        json.put(JSON_TITLE,mTitle);
        json.put(JSON_SOLVED,mSolved);
        json.put(JSON_DATE,mDate.getTime());
        if (mPhoto!=null){
            json.put(JSON_PHOTO,mPhoto.toJSON());
        }
        json.put(JSON_SUSPECT_NAME,mSuspectName);
        json.put(JSON_SUSPECT_PHONE,mSuspectPhone);
        return json;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Photo getPhoto(){
        return mPhoto;
    }

    public void setPhoto(Photo p){
        mPhoto=p;
    }

    public String getSuspectName(){
        return mSuspectName;
    }

    public void setSuspectName(String suspectName){
        mSuspectName=suspectName;
    }

    public String getSuspectPhone(){
        return mSuspectPhone;
    }

    public void setSuspectPhone(String suspectPhone){
        mSuspectPhone=suspectPhone;
    }

    @Override
    public String toString(){
        return mTitle;
    }

}
