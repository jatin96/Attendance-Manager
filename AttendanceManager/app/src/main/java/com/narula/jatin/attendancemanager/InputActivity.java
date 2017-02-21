package com.narula.jatin.attendancemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class InputActivity extends AppCompatActivity {

    private Button subButton;
    private Button dayButton;
    private Button startButton;
    private Button endButton;
    private Button OKButton;
    private AlertDialog.Builder alert;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter adapter;
    private Calendar calendar;
    private String format = "";
    private String time;
    SQLiteDatabase mydatabase;
    Cursor resultSet;
    String subject_input;
    String day_input;
    String start_input;
    String end_input;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        createDB();
        Input();
    }
    void createDB()
    {
        mydatabase = openOrCreateDatabase("SubjectsDB",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                "subject(id INTEGER PRIMARY KEY AUTOINCREMENT,subjects varchar,days varchar,start varchar,end varchar);");


    }

    void Input()
    {
        subButton = (Button) findViewById(R.id.subButton);
        alert = new AlertDialog.Builder(this);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showChangeLangDialog();

            }
        });
        dayButton = (Button) findViewById(R.id.dayButton);
        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(InputActivity.this);
                dialog.setTitle("Days");
                final String[] items = {"Monday", "Tuesday" , "Wednesday","Thursday","Friday","Saturday","Sunday"};
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        day_input = items[which];
                    }
                });
                dialog.create().show();
            }
        });
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChangeLangDialog1();

            }
        });

        endButton = (Button) findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChangeLangDialog2();

            }
        });

        OKButton  =(Button )findViewById(R.id.OKButton);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subject_input==null || day_input==null || start_input==null || end_input==null)
                {
                    Toast.makeText(getBaseContext(),"Something wrong",Toast.LENGTH_LONG).show();
                }
                else if(subject_input.equals(""))
                {
                    Toast.makeText(getBaseContext(),"Subject empty",Toast.LENGTH_LONG).show();
                }
                else
                {
                    int flag=0;
                    try{
                        mydatabase.execSQL("INSERT INTO subject(subjects,days,start,end) VALUES('"
                                +subject_input+"','"+day_input+"','"+start_input+"','"
                                +end_input+"');");
                        //CalendarActivity cal = new CalendarActivity();
                        //cal.addItemsToSpinner(subject_input);
                    }
                    catch (SQLException e) {
                       flag=1;
                    }
                    if(flag == 1)
                        Toast.makeText(getBaseContext(),"Unsuccessful",Toast.LENGTH_LONG).show();
                    else
                    {
                        Toast.makeText(getBaseContext(),"Successful",Toast.LENGTH_LONG).show();
                        Intent i=new Intent(getBaseContext(),MainActivity.class);
                        startActivity(i);
                    }
                }
            }
        });
    }
    public void showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        time = hour +":"+min+":"+format;
    }


    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Add Subject");
        dialogBuilder.setMessage("Enter subject name :");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                subject_input = edt.getText().toString();
               // Toast.makeText(getBaseContext(),"Done!",Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    public void showChangeLangDialog1() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog2, null);
        dialogBuilder.setView(dialogView);

       final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
        calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        showTime(hour,min);
        dialogBuilder.setTitle("Select Time");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                showTime(hour, min);
                start_input=time;
                Toast.makeText(getBaseContext(),time,Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showChangeLangDialog2() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog2, null);
        dialogBuilder.setView(dialogView);

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
        calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        showTime(hour,min);
        dialogBuilder.setTitle("Select Time");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                showTime(hour, min);
                end_input=time;
                Toast.makeText(getBaseContext(),time,Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


}