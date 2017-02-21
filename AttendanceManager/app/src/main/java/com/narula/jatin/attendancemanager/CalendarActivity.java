package com.narula.jatin.attendancemanager;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends FragmentActivity {

    CaldroidFragment caldroidFragment;
    final String[] items = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    SQLiteDatabase mydatabase;
    Cursor resultSet,resultSet2,resultSet3;
    Spinner spinner;
    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayAdapter adapter;
    ListView listView;
    String spinner_subject;
    View listv1,viewc;
    Date datec;
    List<Date> listdates=new ArrayList<Date>();
    String clickedDay;

    //~~~~~~TAGS~~~~~~~~~~~~~~~
    final int PRESENT=1;
    final int ABSENT=-1;
    final int OFF=0;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    com.github.clans.fab.FloatingActionButton help_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        CreateCalendar();
        spinner = (Spinner)findViewById(R.id.spinnerSub);
        addItemsToSpinner();

        adapter = new ArrayAdapter<String>(this, R.layout.listview, arrayList);
        listView = (ListView) findViewById(R.id.calendarList);
        listView.setAdapter(adapter);


        mydatabase = openOrCreateDatabase("SubjectsDB",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " +/*FOREIGN KEY (P_Id) REFERENCES Persons(P_Id)*/
                "attendance(id INTEGER PRIMARY KEY AUTOINCREMENT,subid int,date varchar,status int,FOREIGN KEY (subid) REFERENCES subject(id));");
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        help_btn= (FloatingActionButton) findViewById(R.id.help);
        help_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="Use this calendar to mark your attendance. \n\n" +
                        "1) Use the drop down list to change the subject. \n\n" +
                        "2) Click on any date to see the classes for that day.\n\n" +
                        "3) Click on any event to mark your attendance.\n\n" +
                        "Note : \n\n" +
                        "GREEN : all classes attended.\n" +
                        "YELLOW : all classes were off.\n" +
                        "ORANGE : some classes attended.\n" +
                        "RED : all classes missed.\n";
                AlertDialog.Builder dialog = new AlertDialog.Builder(CalendarActivity.this);
                dialog.setTitle("How to use?");
                dialog.setMessage(msg);
                dialog.create().show();
            }
        });



        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                spinner_subject = spinner.getSelectedItem().toString();
                arrayList.clear();
                adapter.clear();
                ColorMonth();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                arrayList.clear();
                adapter.clear();
                final CaldroidListener listener = new CaldroidListener() {

                    @Override
                    public void onSelectDate(final Date date, final View view) {
                        viewc=view;
                        datec = date;
                        //Toast.makeText(getBaseContext(),"first click",Toast.LENGTH_SHORT).show();
                        arrayList.clear();
                        adapter.clear();
                        // Toast.makeText(getBaseContext(),"JAydeep saur",Toast.LENGTH_LONG).show();
                        clickedDay = (String) android.text.format.DateFormat.format("EEEE", date);
                        mydatabase = openOrCreateDatabase("SubjectsDB", MODE_PRIVATE, null);
                        try {
                            // Toast.makeText(getBaseContext(),"dfasdf",Toast.LENGTH_SHORT).show();
                            resultSet = mydatabase.rawQuery("SELECT * FROM subject WHERE subjects='" + spinner_subject +
                                    "' AND days='" + clickedDay + "';", null);
                        } catch (SQLiteException e) {
                            Toast.makeText(getBaseContext(), "Sorry", Toast.LENGTH_LONG).show();
                        }
                        // Toast.makeText(getApplicationContext(),currentDate,
                        //          Toast.LENGTH_SHORT).show();
                        int pos = 0;
                        if (resultSet.moveToFirst()) {
                            int idx=0;
                            do {
                                String str = resultSet.getString(3) + " - " + resultSet.getString(4);
                                // int idx=resultSet.getInt(0);

                                arrayList.add(str);
                                //-------------------------------------
                                listv1 = getViewByPosition(idx, listView);
                                listv1.setBackgroundResource(R.color.caldroid_transparent);
                                listv1.refreshDrawableState();
                                idx++;
                                //-------------------------------------
                                // Toast.makeText(getBaseContext(),arrayList.get(0),Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                                int pid = resultSet.getInt(0);

                               // Toast.makeText(getBaseContext(), "pid = "+pid, Toast.LENGTH_SHORT).show();

                                final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
                                final String currentdate = formatter.format(date);
                                try {
                                    resultSet2 = mydatabase.rawQuery("SELECT * FROM attendance where subid=" + pid +
                                            " AND date='" + currentdate + "';", null);
                                } catch (SQLiteException e) {
                                    Toast.makeText(getBaseContext(), "unsucessful on update", Toast.LENGTH_SHORT).show();
                                }
                                listv1 = getViewByPosition(pos, listView);
                                listv1.setBackgroundResource(R.color.caldroid_transparent);
                                listv1.refreshDrawableState();
                                pos++;




                                if (resultSet2.moveToFirst()) {
                                    //Toast.makeText(getBaseContext(),"hello bye",Toast.LENGTH_SHORT).show();

                                    int status = resultSet2.getInt(3);
                                    //Toast.makeText(getBaseContext(),"hello bye->"+Integer.toString(status),Toast.LENGTH_SHORT).show();
                                    resultSet2.moveToNext();
                                    if (status == PRESENT) {
                                       // Toast.makeText(getBaseContext(), Integer.toString(status), Toast.LENGTH_SHORT).show();
                                        listv1.setBackgroundResource(R.color.present);
                                        listv1.refreshDrawableState();
                                    } else if (status == ABSENT) {
                                       // Toast.makeText(getBaseContext(), Integer.toString(status), Toast.LENGTH_SHORT).show();
                                        listv1.setBackgroundResource(R.color.absent);
                                        listv1.refreshDrawableState();
                                    } else if (status == OFF) {
                                        listv1.setBackgroundResource(R.color.off);
                                        listv1.refreshDrawableState();
                                    }
                                    listv1.refreshDrawableState();
                                  //  Toast.makeText(getBaseContext(),"clled",Toast.LENGTH_LONG).show();

                                }


                            } while (resultSet.moveToNext());
                            //Toast.makeText(getBaseContext(), "no:" + Integer.toString(pos), Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getBaseContext(),"There are no classes.",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChangeMonth(int month, int year) {
                        String text = "month: " + month + " year: " + year;
                        // Toast.makeText(getApplicationContext(), text,
                        //       Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongClickDate(Date date, View view) {
                        //showMenu(date, view);
                    }

                };


                // Setup Caldroid
                caldroidFragment.setCaldroidListener(listener);

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View listv, int position, long id) {
                String listtime = (String) parent.getItemAtPosition(position);
                showMenu(listv,listtime);
            }
        });
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ColorMonth();
    }
    public View getViewByPosition(int pos, ListView listView) {
        try {
            final int firstListItemPosition = listView
                    .getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition
                    + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                //This may occure using Android Monkey, else will work otherwise
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    int getIndex(String clickDay) {
        if (clickDay.equals("Monday"))
            return 0;
        else if (clickDay.equals("Tuesday"))
            return 1;
        else if (clickDay.equals("Wednesday"))
            return 2;
        else if (clickDay.equals("Thursday"))
            return 3;
        else if (clickDay.equals("Friday"))
            return 4;
        else if (clickDay.equals("Saturday"))
            return 5;
        else
            return 6;
    }

    private void showMenu(final View listv,final String listtime)
    {
        final String startlisttime=listtime.substring(0,listtime.indexOf('-')-1);
        final String endlisttime=listtime.substring(listtime.indexOf('-')+2);
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        final String currentdate=formatter.format(datec);
       // Toast.makeText(CalendarActivity.this,clickedDay, Toast.LENGTH_SHORT).show();

       // Toast.makeText(getBaseContext(),startlisttime,Toast.LENGTH_SHORT).show();
       // Toast.makeText(getBaseContext(),endlisttime,Toast.LENGTH_SHORT).show();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_color);
        builder.setItems(R.array.menu_color, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {



                try
                {
                    resultSet = mydatabase.rawQuery("SELECT * FROM subject WHERE subjects='"
                            + spinner_subject + "' AND start='" + startlisttime + "' AND end='"
                            + endlisttime + "' AND days='"+clickedDay+"';", null);
                }
                catch(SQLiteException e)
                {
                    Toast.makeText(getBaseContext(),"unsuccessful",Toast.LENGTH_SHORT).show();
                }


               // Toast.makeText(getBaseContext(), Integer.toString(id), Toast.LENGTH_SHORT).show();

                resultSet.moveToFirst();
                int pid=resultSet.getInt(0);
                if (which == 0)
                {
                    try {
                        if(resultSet.moveToFirst())
                        mydatabase.execSQL("DELETE FROM attendance WHERE subid="+pid +" AND date='"+currentdate+"';");
                        mydatabase.execSQL("INSERT INTO attendance(subid,date,status) VALUES("+pid + ",'" + currentdate
                                + "'," + PRESENT + ");");
                    }catch(SQLiteException e)
                    {
                        Toast.makeText(getBaseContext(),"unsuccessful new table",Toast.LENGTH_SHORT).show();
                    }
                    listv.setBackgroundResource(R.color.present);
                    listv1.refreshDrawableState();
                    caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.rgb(76, 140, 80)), datec);
                }
                    //Toast.makeText(getBaseContext(),"JAydeep saur",Toast.LENGTH_LONG).show();
                else if (which == 1)
                {
                    mydatabase.execSQL("DELETE FROM attendance WHERE subid="+pid +" AND date='"+currentdate+"';");
                    mydatabase.execSQL("INSERT INTO attendance(subid,date,status) VALUES("+pid + ",'" + currentdate
                            + "'," + ABSENT + ");");

                    listv.setBackgroundResource(R.color.absent);
                    listv1.refreshDrawableState();
                    caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.rgb(229, 57, 53)), datec);
                }
                else if (which == 2)
                {
                    mydatabase.execSQL("DELETE FROM attendance WHERE subid="+pid +" AND date='"+currentdate+"';");
                    mydatabase.execSQL("INSERT INTO attendance(subid,date,status) VALUES("+ pid + ",'" + currentdate
                            + "'," + OFF + ");");

                    listv.setBackgroundResource(R.color.off);
                    listv1.refreshDrawableState();
                    caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.rgb(255,215,0)), datec);
                }
                caldroidFragment.refreshView();
            }
        });
        builder.show();
    }

    public void addItemsToSpinner() {
        mydatabase = openOrCreateDatabase("SubjectsDB", MODE_PRIVATE, null);
        resultSet = mydatabase.rawQuery("SELECT DISTINCT subjects FROM subject", null);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        if (resultSet.moveToFirst()) {
            do {
                String str = resultSet.getString(0);
                spinnerAdapter.add(str);
                spinnerAdapter.notifyDataSetChanged();
            } while (resultSet.moveToNext());
        }
    }
    void ColorMonth()
    {
        if(listdates!=null)
        caldroidFragment.clearBackgroundDrawableForDates(listdates);
        if(listdates!=null)
        listdates.clear();
        mydatabase = openOrCreateDatabase("SubjectsDB", MODE_PRIVATE, null);
        //mydatabase = openOrCreateDatabase("AttendanceDB", MODE_PRIVATE, null);
        resultSet = mydatabase.rawQuery("SELECT * FROM subject WHERE subjects='"+spinner_subject+"';", null);
        if(resultSet.moveToFirst())
        {
            do{
                int pid=resultSet.getInt(0);
                resultSet2=mydatabase.rawQuery("SELECT * FROM attendance WHERE subid="+pid+";",null);
                if(resultSet2.moveToFirst())
                {
                    do{
                        String date=resultSet2.getString(2);
                        resultSet3=mydatabase.rawQuery("SELECT * FROM attendance WHERE date='"
                                +date+"' AND subid IN (SELECT id FROM subject WHERE subjects='"+spinner_subject+"');",null);
                        int cntp=0,cnta=0;
                        if(resultSet3.moveToFirst())
                        {
                            do{
                                int status=resultSet3.getInt(3);
                                if(status == PRESENT)
                                    cntp++;
                                else if(status == ABSENT)
                                    cnta++;
                            }while(resultSet3.moveToNext());
                            //----------------------------------------
                            DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                            Date dateobj=null;
                            try {
                                dateobj = df.parse(date);
                                String newDateString = df.format(dateobj);
                                System.out.println(newDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            listdates.add(dateobj);
                            //---------------------------------------
                           // Toast.makeText(CalendarActivity.this, "asdf"+cnta+" "+cntp, Toast.LENGTH_SHORT).show();
                            if(cnta == 0 && cntp==0) //all are off
                                caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.rgb(255,215,0)), dateobj);
                            else if(cnta==0)//all are present
                                caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.rgb(76, 140, 80)), dateobj);
                            else if(cntp == 0)// all are absent
                                caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.rgb(229, 57, 53)), dateobj);
                            else // mixed bag
                                caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.rgb(251, 140, 0)), dateobj);

                        }


                    }while(resultSet2.moveToNext());
                }
            }while(resultSet.moveToNext());
        }
        caldroidFragment.refreshView();

    }
    void CreateCalendar()
    {
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        caldroidFragment.setMaxDate(Calendar.getInstance().getTime());
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();
    }
}