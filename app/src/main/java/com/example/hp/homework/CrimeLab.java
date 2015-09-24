package com.example.hp.homework;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Sergii Varenyk on 11.07.15.
 */
//singleton
public class CrimeLab {
    private static final String TAG="CrimeLab";
    private static final String FILENAME="crimes.json";
    private CriminalIntentJSONSerializer mSerializer;

    private ArrayList<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private CrimeLab(Context appContext){
        mAppContext=appContext;
        mSerializer=new CriminalIntentJSONSerializer(mAppContext,FILENAME);
        try{
            mCrimes=mSerializer.loadCrimes();
        }catch (Exception e){
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG,"Error loading crimes: ",e);
        }
    }

    public static CrimeLab get(Context c){
        if(sCrimeLab==null){
            sCrimeLab=new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c){
        mCrimes.add(c);
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for(Crime c:mCrimes){
            if(c.getId().equals(id)){
                return c;
            }
        }
        return null;
    }

    public boolean saveCrimes(){
        try{
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG,"item saved to file");
            return true;
        }catch (Exception e){
            Log.e(TAG, "Error item saving");
            return false;
        }
    }

    public void deleteCrime(Crime crime){
        mCrimes.remove(crime);
    }


    public boolean saveCrimesToSD(){
        /*
        try{
            mSerializer.writeFileSD(mCrimes);
            Log.d(TAG,"item saved to file on SD");
            return true;
        }catch (Exception e){
            Log.e(TAG, "Error item saving to SD");
            return false;
        }*/
        //temporary
        return true;
    }

}
