package com.example.hp.homework;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Sergii Varenyk on 26.07.15.
 */
public class CriminalIntentJSONSerializer {

    private Context mContext;
    private String mFilename;
    private static final String TAG="SD";
    private static final String DIR_SD="home/hp/Загрузки";
    private static final String NAMEFILE_SD = "fnsd.txt";

    public CriminalIntentJSONSerializer(Context c,String f){
        mContext = c;
        mFilename = f;
    }


    public ArrayList<Crime> loadCrimes() throws IOException,JSONException {
        ArrayList<Crime> crimes = new ArrayList<>();
        BufferedReader reader = null;
        try{
            //open & read file in StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line=reader.readLine())!=null){
                jsonString.append(line);
            }
            //analiz JSON with JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //build Crime objects' array from JSONObject datas
            for(int i=0;i<array.length();i++){
                crimes.add(new Crime(array.getJSONObject(i)));
            }

        }catch (FileNotFoundException e){
                //if start from 0
        }finally {
                if(reader!=null){
                    reader.close();
                }
        }
        return crimes;
    }


    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException,IOException{
        //build array in JSON
        JSONArray array = new JSONArray();
        for (Crime c:crimes)
            array.put(c.toJSON());
        //write file on disk
        Writer writer=null;
        try{
            OutputStream out = mContext.openFileOutput(mFilename,Context.MODE_PRIVATE);
            writer=new OutputStreamWriter(out);
            writer.write(array.toString());
            Log.d("Internal dir", " " + mContext.getFilesDir());
        }finally {
            if(writer!=null)
                writer.close();
        }
    }

/*
????? with external storage

    public void writeFileSD(ArrayList<Crime> crimes) throws JSONException,IOException{

        JSONArray array = new JSONArray();
        for (Crime c:crimes)
            array.put(c.toJSON());

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(TAG,"SD not avaliable "+Environment.getExternalStorageState());
            return;
        }
        //get sdCardPath
        //File sdPath = Environment.getExternalStorageDirectory();
        //make folder
        File sdPath = new File(DIR_SD);
        Log.d("SDPath","SD Path "+sdPath.getAbsolutePath());
        //sdPath.mkdirs();
        //make file
        File sdFile = new File(sdPath,NAMEFILE_SD);

        Writer writer=null;
        try{
            //FileOutputStream out = mContext.openFileOutput(NAMEFILE_SD,Context.MODE_PRIVATE);
            FileOutputStream out = new FileOutputStream(sdFile);
            writer=new OutputStreamWriter(out);
            writer.write(array.toString());
        }catch (IOException e){
            Log.e(TAG,"Error SD write");
        }finally {
            if(writer != null)
                writer.close();
        }
    }
    */
}
