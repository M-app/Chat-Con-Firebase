package com.upvhas.app.chaty.salas;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.upvhas.app.chaty.R;

public class SalasActivity extends AppCompatActivity {

    private String[] mTitulos;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salas);

        mTitulos = new String[]{"Opción 1", "Opción 2", "Opción 3"};
        drawerLayout = (DrawerLayout) findViewById(R.id.salas_drawer);
        drawerListView = (ListView) findViewById(R.id.salas_drawer_Listview);

        drawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item,R.id.item_drawer_title,mTitulos));

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_salas_container,new SalasFragment())
                    .commit();
        }
    } // --> end onCreate


}
