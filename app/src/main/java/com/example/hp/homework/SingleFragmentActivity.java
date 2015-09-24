package com.example.hp.homework;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Sergii Varenyk on 11.07.15.
 */
public abstract class SingleFragmentActivity extends ActionBarActivity{

    protected abstract Fragment createFragment();

    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        //создадим объект FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if(fragment == null){
            fragment = createFragment();
            //создадим и закрепим транзакцию фрагмента
            fm.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
            //R.id.fragmentContainer - идентификатор контейнерного представления, выполняет 2 ф-ии:
            //1. сообщает FragmentManager где в представлении активности должно находиться
            //представление фрагмента,
            //2. обеспечивает однозначную идентификацию фрагмента в списке FragmentManager
        }
    }

}