package com.ambinusian.adab.mainactivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.ambinusian.adab.allclasses.AllClassesFragment;
import com.ambinusian.adab.calendar.CalendarFragment;
import com.ambinusian.adab.forum.ForumFragment;
import com.ambinusian.adab.help.HelpFragment;
import com.ambinusian.adab.R;
import com.ambinusian.adab.setting.SettingFragment;
import com.ambinusian.adab.topics.TopicsFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> listSemester;
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    Spinner SpinnerlistSemester;
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.tool_bar);
        mDrawerLayout = findViewById(R.id.mDrawerLayout);

        mNavigationView = findViewById(R.id.nv_adab);
        listSemester = new ArrayList<>();

        //set up spinner
        SpinnerlistSemester = mNavigationView.getHeaderView(0).findViewById(R.id.spinner_list_semesters);

        listSemester.add("2018 Semester 1");
        listSemester.add("2018 Semester 2");

        SpinnerlistSemester.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,listSemester));

        //icon menu clicked
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //set first fragment launched
        getSupportFragmentManager().beginTransaction().replace(R.id.adab_fragment,new AllClassesFragment()).commit();

        //navigation item clicked
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.menu_allClasses){
                    getSupportFragmentManager().beginTransaction().replace(R.id.adab_fragment,new AllClassesFragment()).commit();
                }
                else if(id == R.id.menu_topics){
                    getSupportFragmentManager().beginTransaction().replace(R.id.adab_fragment,new TopicsFragment()).commit();
                }
                else if(id == R.id.menu_calendar){
                    getSupportFragmentManager().beginTransaction().replace(R.id.adab_fragment,new CalendarFragment()).commit();
                }
                else if(id == R.id.menu_forum){
                    getSupportFragmentManager().beginTransaction().replace(R.id.adab_fragment,new ForumFragment()).commit();
                }
                else if(id == R.id.menu_help){
                    getSupportFragmentManager().beginTransaction().replace(R.id.adab_fragment,new HelpFragment()).commit();
                }
                else if(id == R.id.menu_setting){
                    getSupportFragmentManager().beginTransaction().replace(R.id.adab_fragment,new SettingFragment()).commit();
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

}
