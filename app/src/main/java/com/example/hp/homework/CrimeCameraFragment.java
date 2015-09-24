package com.example.hp.homework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sergii Varenyk on 30.07.15.
 */
@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mJpegCallback= new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String filename = UUID.randomUUID().toString()+".jpg";
            FileOutputStream os = null;
            boolean success = true;
            try{
                os=getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            }catch (Exception e){
                Log.e(TAG,"Error writing img to file"+filename,e);
                success = false;
            }finally{
                try{
                    if (os!=null) os.close();
                }catch (Exception e) {
                    Log.e(TAG,"Error closing file"+filename,e);
                    success = false;
                }
            }
            if (success) {
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME,filename);
                getActivity().setResult(Activity.RESULT_OK,i);
            }else{
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };
    public static final String EXTRA_PHOTO_FILENAME = "com.example.hp.homework.photo_filename";

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime_camera,parent,false);
        Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera!=null){
                    mCamera.takePicture(mShutterCallback,null,mJpegCallback);
                }
            }
        });
        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//устаревший код

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if (mCamera!=null){
                        mCamera.setPreviewDisplay(holder);
                    }
                }
                catch (IOException exception){
                    Log.e(TAG,"Error camera preview",exception);
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera==null) return;

                /*
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(0,info);
                WindowManager windowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
                int rotation = windowManager.getDefaultDisplay().getRotation();
                Log.i("rotation",""+rotation);
                Log.i("orientation",""+info.orientation);//always 90
                int degrees=0;
                switch(rotation){
                    case Surface.ROTATION_0:degrees=0;break;//val=0
                    case Surface.ROTATION_90:degrees=90;break;//val=1
                    case Surface.ROTATION_180:degrees=180;break;//val=2
                    case Surface.ROTATION_270:degrees=270;break;//val=3
                }
                int result;

                if (info.facing==Camera.CameraInfo.CAMERA_FACING_BACK){
                    result=(info.orientation-degrees+360)%360;
                    Log.i("result",""+result);
                    result=(360+result)%360;
                }else{
                    result=(info.orientation+degrees)%360;
                }

                Camera.Parameters parameters = mCamera.getParameters();
                int rotate = (degrees+270)%360;
                Log.i("rotate",""+rotate);
                parameters.setRotation(rotate);
                //mCamera.setParameters(parameters);
                */

                Camera.Parameters parameters = mCamera.getParameters();
                Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(),width,height);
                parameters.setPreviewSize(s.width,s.height);
                s=getBestSupportedSize(parameters.getSupportedPictureSizes(),width,height);
                parameters.setPictureSize(s.width,s.height);
                mCamera.setParameters(parameters);
                try{
                    mCamera.startPreview();
                }catch (Exception e){
                    Log.e(TAG,"Couldn't start preview",e);
                    mCamera.release();
                    mCamera=null;
                }
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera!=null){
                    mCamera.stopPreview();
                }
            }
        });

        mProgressContainer=v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        mCamera = Camera.open(0);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
        }
    }

    private Size getBestSupportedSize(List<Size> sizes,int width,int height){
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width*bestSize.height;
        for(Size s:sizes){
            int area = s.width*s.height;
            if(area>largestArea){
                bestSize=s;
                largestArea=area;
            }
        }
        return bestSize;
    }

}
