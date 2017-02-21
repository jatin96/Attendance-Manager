package com.narula.jatin.attendancemanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    SeekBar seekBar;
    com.zcw.togglebutton.ToggleButton toggleBtn;
    TextView textView;
    Button setTime,setTime1;
    private PendingIntent pendingIntent;
    Intent alarmIntent;
    AlarmManager alarmManager;
    int PERCENT = 75;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
         sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
       editor = sp.edit();
        seekBar= (SeekBar) findViewById(R.id.slider);
        PERCENT = sp.getInt("percent_key",75);
        seekBar.setProgress(PERCENT);
        textView= (TextView) findViewById(R.id.textView);
        textView.setText(Integer.toString(PERCENT));
        setTime= (Button) findViewById(R.id.reminder_time);
        setTime1= (Button) findViewById(R.id.notification_time);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {

            public void onStopTrackingTouch(SeekBar bar)
            {
                PERCENT = bar.getProgress();// the value of the seekBar progress
                editor.putInt("percent_key",PERCENT);
                editor.commit();
            }

            public void onStartTrackingTouch(SeekBar bar)
            {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean)
            {
                textView.setText("" + paramInt + "%"); // here in textView the percent will be shown
            }
        });

        toggleBtn= (com.zcw.togglebutton.ToggleButton) findViewById(R.id.reminder_toggle);
        toggleBtn.setOnToggleChanged(new com.zcw.togglebutton.ToggleButton.OnToggleChanged(){
            @Override
            public void onToggle(boolean on) {
                if(on)
                {
                    Toast.makeText(getBaseContext(),"enabled",Toast.LENGTH_SHORT).show();
                    editor.putString("reminder_key","ON");
                    editor.commit();
                    setTime.setEnabled(true);
                }
                else
                {
                    editor.putString("reminder_key","OFF");
                    editor.commit();
                    setTime.setEnabled(false);
                }
            }
        });

        toggleBtn= (com.zcw.togglebutton.ToggleButton) findViewById(R.id.notification_toggle);
        toggleBtn.setOnToggleChanged(new com.zcw.togglebutton.ToggleButton.OnToggleChanged(){
            @Override
            public void onToggle(boolean on) {
                if(on)
                {
                    Toast.makeText(getBaseContext(),"enabled",Toast.LENGTH_SHORT).show();
                    editor.putString("notification_key","ON");
                    editor.commit();
                    setTime1.setEnabled(true);
                }
                else
                {
                    editor.putString("notification_key","OFF");
                    editor.commit();
                    setTime1.setEnabled(false);
                }
            }
        });
        //-------------------------------------------------
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLangDialog1("reminder_key");
            }
        });
        //---------------------------------------------------restoring values
        if(sp.getString("reminder_key","OFF").equals("OFF"))
            setTime.setEnabled(false);
        else
            setTime.setEnabled(true);
        //---------------------------------------------------
//-------------------------------------------------------------
        setTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLangDialog1("notification_key");
            }
        });
        if(sp.getString("notification_key","OFF").equals("OFF"))
            setTime1.setEnabled(false);
        else
            setTime1.setEnabled(true);
//-------------------------------------------------------------

    }
    public void showChangeLangDialog1(final String str) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog2, null);
        dialogBuilder.setView(dialogView);

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        dialogBuilder.setTitle("Select Time");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                saveTime(str,hour,min);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    void saveTime(String key,int hr,int min)
    {
        editor.putInt(key+"_hr",hr);
        editor.commit();
        editor.putInt(key+"_min",min);
        editor.commit();
        if(key.equals("reminder_key"))
            setNotification(hr,min);
        if(key.equals("notification_key"))
            setNotification2(hr,min);
    }
    void setNotification(int hr,int min)
    {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmIntent = new Intent(getBaseContext(), AlarmReceiver1.class); // AlarmReceiver1 = broadcast receiver

            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
            alarmManager.cancel(pendingIntent);

            Calendar alarmStartTime = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            alarmStartTime.set(Calendar.HOUR_OF_DAY, hr);
            alarmStartTime.set(Calendar.MINUTE, min);
            alarmStartTime.set(Calendar.SECOND, 0);
            if (now.after(alarmStartTime)) {
                Log.d("Hey","Added a day");
                alarmStartTime.add(Calendar.DATE, 1);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            Toast.makeText(getBaseContext(),"Reminder is set for - "+hr+":"+min,Toast.LENGTH_SHORT).show();
        }
    void setNotification2(int hr,int min)
    {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(getBaseContext(), AlarmReceiver2.class); // AlarmReceiver1 = broadcast receiver

        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
        alarmManager.cancel(pendingIntent);

        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, hr);
        alarmStartTime.set(Calendar.MINUTE, min);
        alarmStartTime.set(Calendar.SECOND, 0);
        if (now.after(alarmStartTime)) {
            Log.d("Hey","Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getBaseContext(),"Reminder is set for - "+hr+":"+min,Toast.LENGTH_SHORT).show();
    }
    }
