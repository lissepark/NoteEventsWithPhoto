package com.example.hp.homework;

        import android.annotation.TargetApi;
        import android.app.Activity;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.content.res.Configuration;
        import android.database.Cursor;
        import android.graphics.drawable.BitmapDrawable;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.provider.ContactsContract;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.NavUtils;
        import android.support.v7.app.ActionBarActivity;
        import android.text.format.DateFormat;
        import android.view.ActionMode;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.ContextMenu;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ImageView;

        import java.io.File;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.UUID;
        import javax.security.auth.callback.Callback;

/**
 * Created by Sergii Varenyk on 10.07.15.
 */
public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "com.example.hp.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE =-1;
    private static final int REQUEST_PHOTO=1;
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_CONTACT=2;
    private static final int REQUEST_PHONE_NUMBER=3;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageView mImagePhotoPrev;
    private Button reportButton;
    private Button mSuspectButton;
    private Button mToCallSuspectButton;
    private Callback mCallback;

    private Button mDeleteCrimeContextButton;
    private ActionMode.Callback mActionModeCallback;
    private ImageButton mImageButton;
    private SimpleDateFormat dateFormatCrime = new SimpleDateFormat("EEEE, MMM dd, yyyy");

    public interface Callback {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallback = (Callback) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        showPhoto();
    }

    public void updateDate(){
        mDateButton.setText(dateFormatCrime.format(mCrime.getDate()).toString());
    }


    @TargetApi(11)
    @Override
    public View onCreateView(final LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime,parent,false);

        /**1). inflate(R.layout.fragment_crime,parent,false); Парсит xml и преобразует в объект класса View.
         * 2). Когда мы в Activity указываем setContentView(int id) - мы передаем ссылку на xml.
         * Внутри Activity находится экземпляр класса Window, он парсит наш xml и получает объект
         * класса View. Дальше при вызове в Activity findViewById(int id) мы обращаемся к экземпляру
         * класса Window, в свою очередь он делегирует (передает вызов) экземпляру класса View.
         * А класс View содержит метод findViewById(int id), и уже он возвращает искомый объект
         * В обоих случаях механизм один и тот же, только LayoutInflater обычно используется для парсинга
         * xml не являющихся layout Activity. Например при отрисовке Fragments (данный случай) и
         * CustomView или при компоновке Activity из Java.
         */

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
         //   getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        //}

        mTitleField=(EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            //слушатель; анонимный класс, реакция виджета на ввод пользователя
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
                mCallback.onCrimeUpdated(mCrime);
            }
            @Override
            public void afterTextChanged(Editable c) {
            }
        });

        //DateFormat formats = DateFormat.getDateInstance();
        mDateButton=(Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                mCallback.onCrimeUpdated(mCrime);
            }
        });

        mImageButton=(ImageButton)v.findViewById(R.id.crime_imageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        PackageManager pm = getActivity().getPackageManager();
        if(!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)&&!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            mImageButton.setEnabled(false);
        }

        mDeleteCrimeContextButton=(Button)v.findViewById(R.id.delete_context);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(mDeleteCrimeContextButton);
        }else{

            mActionModeCallback = new ActionMode.Callback(){

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    MenuInflater inflater1 = actionMode.getMenuInflater();
                    inflater1.inflate(R.menu.crime_list_item_context,menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_item_delete_crime:
                            CrimeLab.get(getActivity()).deleteCrime(mCrime);
                            Intent i = new Intent(getActivity(),CrimeListActivity.class);
                            startActivityForResult(i, 0);
                            return true;
                        default:
                            return false;
                    }

                }
                @Override
                public void onDestroyActionMode(ActionMode actionMode) {

                }
            };

        }

        mImagePhotoPrev = (ImageView)v.findViewById(R.id.crime_imageView);
        mImagePhotoPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p==null){return;}
                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm,DIALOG_IMAGE);
                //int config = getResources().getConfiguration().orientation;
                //value 2 for landscape, value 1 for portrait
            }
        });


        mDeleteCrimeContextButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getActivity().startActionMode(mActionModeCallback);
                v.setSelected(true);
                return true;
            }

        });

        reportButton=(Button)v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i=Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton=(Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspectName()!=null){
            mSuspectButton.setText(mCrime.getSuspectName());
        }

        mToCallSuspectButton=(Button)v.findViewById(R.id.crime_callButton);
        mToCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCrime.getSuspectPhone()!=null){
                    Uri number = Uri.parse(mCrime.getSuspectPhone());
                    Log.i("phone number ",""+number);
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    //i.setType("date");
                    //i.setData(number);
                    i=Intent.createChooser(i,getString(R.string.send_report));
                    startActivity(i);
                }
            }
        });

        return v;
    }

    private void showPhoto(){
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p!=null){
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b=PictureUtils.getScaleDrawable(getActivity(),path);
        }
        mImagePhotoPrev.setImageDrawable(b);
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.crime_list_item_context,menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_item_delete_crime:
                String path = getActivity().getFileStreamPath(mCrime.getPhoto().getFilename()).getAbsolutePath();
                File oldFile = new File(path);
                oldFile.delete();
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                Intent i = new Intent(getActivity(),CrimeListActivity.class);
                startActivityForResult(i,0);
                return true;
        }
        return super.onContextItemSelected(item);
    }
*/
    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString=getString(R.string.crime_report_solved);
        }else{
            solvedString=getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE,MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspectName();
        if (suspect==null){
            suspect=getString(R.string.crime_report_no_suspect);
        }else{
            suspect=getString(R.string.crime_report_suspect,suspect);
        }
        String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID,crimeId);
        //присоединение пакета аргументов до добавления фрагмента в активность
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode!= Activity.RESULT_OK){return;}
        if(requestCode == REQUEST_DATE){
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mCallback.onCrimeUpdated(mCrime);
            updateDate();
        }else if (requestCode==REQUEST_PHOTO){
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename!=null){
                Photo p = new Photo(filename);
/*
                //delete old img
                if (mCrime.getPhoto()!=null){
                    Photo oldPhoto = mCrime.getPhoto();
                    String path = getActivity().getFileStreamPath(oldPhoto.getFilename()).getAbsolutePath();
                    File oldFile = new File(path);
                    Log.i("old file: ",""+oldFile+" was detected");
                    oldFile.delete();
                    if (oldFile!=null) {
                        Log.i("old file: ", "" + oldFile +": "+ oldFile.getName()+" was not deleted");
                    }else{
                        Log.i("old file: ", "" + oldFile + " was deleted");
                    }
                }
*/
                mCrime.setPhoto(p);
                mCallback.onCrimeUpdated(mCrime);
                showPhoto();
            }
        }else if (requestCode==REQUEST_CONTACT){
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME,ContactsContract.Contacts._ID,};
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            if (c.getCount()==0){
                c.close();
                return;
            }
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspectName(suspect);
            mSuspectButton.setText(suspect);

            String[] phone = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            c = getActivity().getContentResolver().query(contactUri,phone,null,null,null);
            if (c.getCount()==0){
                c.close();
                return;
            }
            c.moveToFirst();
            int numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = c.getString(numberIndex);
            mCrime.setSuspectPhone(number);
            mCallback.onCrimeUpdated(mCrime);
            c.close();
        }
        /*
        else if (requestCode==REQUEST_PHONE_NUMBER){
            Uri phoneUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor c = getActivity().getContentResolver().query(phoneUri,queryFields,null,null,null);
            if (c.getCount()==0){
                c.close();
                return;
            }
            c.moveToFirst();
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                Intent i = new Intent(getActivity(),CrimeListActivity.class);
                startActivityForResult(i,0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
        CrimeLab.get(getActivity()).saveCrimesToSD();
    }

    @Override
    public void onStop(){
        super.onStop();
        PictureUtils.cleanImageView(mImagePhotoPrev);
    }

}
