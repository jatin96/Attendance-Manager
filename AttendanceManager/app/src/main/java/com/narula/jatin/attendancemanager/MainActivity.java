package com.narula.jatin.attendancemanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;


import com.majeur.cling.Cling;
import com.majeur.cling.ClingManager;
import com.majeur.cling.ViewTarget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public TextView date_time;
    public ImageButton calendar;
    private ImageButton subjects;
    private ImageButton timeTable;
    private ImageButton predictor;
    private TextView banner;
    PendingIntent pIntent;
    SQLiteDatabase mydatabase, mydatabase2;
    private PendingIntent pendingIntent;
    Cursor resultSet;
    AlarmManager alarmManager;
    public static final String MyPREFERENCES = "MyPrefs";
    Intent alarmIntent;
    //----------Show Case View
   // private SharedPreferences mSharedPreferences;
    private ClingManager mClingManager;
    String START_TUTORIAL_KEY="showcase";
    String SHOW_CASE="showCase";
    //--------------------------
    private com.github.clans.fab.FloatingActionButton menu_item1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------------------------------------------------
        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String enabled = sp.getString("reminder_key", "OFF");
        int hr = sp.getInt("reminder_key_hr", 19);
        int min = sp.getInt("reminder_key_min", 0);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        createDB();
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        date_time = (TextView) findViewById(R.id.date_time);
        date_time.setText(formattedDate);

        DashBoard();
        Banner();
        SetAvatar();


        menu_item1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.menu_item);
        menu_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), InputActivity.class);
                startActivity(i);

            }
        });
    }

    void SetAvatar() {

    }

    void createDB() {
        mydatabase = openOrCreateDatabase("SubjectsDB", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                "subject(id INTEGER PRIMARY KEY AUTOINCREMENT,subjects varchar,days varchar,start varchar,end varchar);");

       // mydatabase2 = openOrCreateDatabase("AttendanceDB", MODE_PRIVATE, null);
        //mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
             //   "attendance(id INTEGER PRIMARY KEY AUTOINCREMENT,subid int,date varchar,status int);");

    }

    void Banner() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String day = sdf.format(d);
        mydatabase = openOrCreateDatabase("SubjectsDB", MODE_PRIVATE, null);
        resultSet = mydatabase.rawQuery("SELECT * FROM subject WHERE days='" + day + "';", null);
        String displayString = "";
        if (resultSet.moveToFirst()) {
            do {
                displayString += resultSet.getString(1) + ", ";
            } while (resultSet.moveToNext());
        }
        if (!displayString.equals("")) {
            displayString = "New Notification! Today is " + day + " . Classes for Today : " + displayString;
            displayString = displayString.substring(0, displayString.length() - 2) + ".";
            banner = (TextView) findViewById(R.id.notify);
            banner.setSelected(true);//doubt1
            banner.setText(displayString);
        }
    }

    void DashBoard() {
        calendar = (ImageButton) findViewById(R.id.calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CalendarActivity.class);
                startActivity(i);
            }
        });

        subjects = (ImageButton) findViewById(R.id.subjects);
        subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), SubjectsActivity.class);
                startActivity(i);
            }
        });

        timeTable = (ImageButton) findViewById(R.id.timeTable);
        timeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), TimeTableActivity.class);
                startActivity(i);
            }
        });

        predictor = (ImageButton) findViewById(R.id.predictor);
        predictor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), PredictorActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            StartTutorial();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_share) {
            Intent shareIntent =
                    new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Insert Subject Here");//doubt2
            String shareMessage = "Hey! Please download Attendance Assist from PlayStore.";
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    shareMessage);
            startActivity(Intent.createChooser(shareIntent,
                    "Insert share chooser title here"));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

       // }

    }
    void StartTutorial()
    {
        mClingManager = new ClingManager(this);

        // When no Target is set, Target.NONE is used
        mClingManager.addCling(new Cling.Builder(this)
                .setTitle("Welcome to Attendance Assist!")
                .setContent("Manage your attendance on the go.")
                .build());

        mClingManager.addCling(new Cling.Builder(this)
                .setTitle("Calendar")
                .setContent("Mark your attendance on a particular date.")
                .setMessageBackground(getResources().getColor(R.color.calendar_color))
                .setTarget(new ViewTarget(this, R.id.calendar))
                .build());

        mClingManager.addCling(new Cling.Builder(this)
                .setTitle("Subjects")
                .setContent("Know your statistics for a particular subject.")
                .setMessageBackground(getResources().getColor(R.color.subjects_color))
                .setTarget(new ViewTarget(this, R.id.subjects))
                .build());

        mClingManager.addCling(new Cling.Builder(this)
                .setTitle("Time Table")
                .setContent("View your classes for a week.")
                .setMessageBackground(getResources().getColor(R.color.timetable_color))
                .setTarget(new ViewTarget(this, R.id.timeTable))
                .build());
        mClingManager.addCling(new Cling.Builder(this)
                .setTitle("Predictor")
                .setContent("Predict your attendance percentage.")
                .setMessageBackground(getResources().getColor(R.color.predictor_color))
                .setTarget(new ViewTarget(this, R.id.predictor))
                .build());
        mClingManager.addCling(new Cling.Builder(this)
                .setTitle("Add Events")
                .setContent("Set your subject entries here.")
                .setMessageBackground(getResources().getColor(R.color.add_button_color))
                .setTarget(new ViewTarget(this, R.id.menu))
                .build());


        mClingManager.setCallbacks(new ClingManager.Callbacks() {
            @Override
            public boolean onClingClick(int position) {

                return false;
            }

            @Override
            public void onClingShow(int position) {
                //Toast.makeText(MainActivity.this, "Cling #" + position + " is shown", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClingHide(int position) {
               // Toast.makeText(MainActivity.this, "Cling #" + position + " is hidden", Toast.LENGTH_SHORT).show();

                // Last Cling has been shown, tutorial is ended.
            }
        });

        mClingManager.start();
    }
}
