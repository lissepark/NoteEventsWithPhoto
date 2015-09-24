package com.example.hp.homework;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Sergii Varenyk on 01.08.15.
 */
public class ImageFragment extends DialogFragment {
    public static final String EXTRA_IMAGE_PATH = "com.example.hp.homework.image_path";
    private ImageView mImageView;

    public static ImageFragment newInstance(String imagePath){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH,imagePath);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaleDrawable(getActivity(), path);
        mImageView.setImageDrawable(image);
        //registerForContextMenu(mImageView);
        return mImageView;
    }

/*
    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                Log.i("context ","working");

            case R.id.menu_item_delete_crime_cancel:
                Log.i("context ","working");

        }
        return true;
    }
/*
    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                Log.i("context ","working");
                String path = getActivity().getFileStreamPath(mCrime.getPhoto().getFilename()).getAbsolutePath();
                File oldFile = new File(path);
                oldFile.delete();
                Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                startActivityForResult(i, 0);
            case R.id.menu_item_delete_crime_cancel:
                Log.i("context ","working");
                return true;
        }
        return super.onContextItemSelected(item);
    }
*/
        @Override
        public void onDestroyView () {
            super.onDestroyView();
            PictureUtils.cleanImageView(mImageView);
        }
}
