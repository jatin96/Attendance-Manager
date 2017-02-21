package com.narula.jatin.attendancemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import az.plainpie.PieView;

public class StatsActivity extends AppCompatActivity {
    SQLiteDatabase mydatabase,mydatabase2;
    Cursor resultSet,resultSet2;
    String subjectName;
    int present,absent,off,total;
    double percent;
    PieView pieView;
    TextView t;
    double CONST = 0.75;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Intent i = getIntent();
        subjectName = i.getExtras().getString("subjectName");
       // Toast.makeText(getBaseContext(),subjectName,Toast.LENGTH_SHORT).show();
        SettingsActivity sa=new SettingsActivity();
        //-----------------------------------------
        CONST=getPercent()*0.01;
        //Toast.makeText(getBaseContext(),"cal:"+CONST,Toast.LENGTH_SHORT).show();
        //-----------------------------------------
        calculatePercantage();
    }
    void calculatePercantage()
    {
        present=0;absent=0;off=0;
        mydatabase = openOrCreateDatabase("SubjectsDB",MODE_PRIVATE,null);
        //mydatabase2 = openOrCreateDatabase("AttendanceDB",MODE_PRIVATE,null);
        resultSet = mydatabase.rawQuery("SELECT * FROM subject WHERE subjects='"+subjectName+"';",null);
        if(resultSet.moveToFirst())
        {
            do {
                int pid=resultSet.getInt(0);
                resultSet2 = mydatabase.rawQuery("SELECT * FROM attendance WHERE subid="+pid+";",null);
                if(resultSet2.moveToFirst())
                {
                    do{
                        int status=resultSet2.getInt(3);
                        if(status==1)
                            present++;
                        else if(status==0)
                            off++;
                        else
                            absent++;
                    }while(resultSet2.moveToNext());
                }
            }while(resultSet.moveToNext());
            total=present+absent;
            percent=(present*100.0)/total;
            percent=Math.round(percent*100.0)/100.0;//rounding to two decimal places
            pieView = (PieView) findViewById(R.id.pieView);
            pieView.setInnerTextVisibility(View.VISIBLE);
            pieView.setInnerText(Double.toString(percent));
            if(percent != 0.0)
            pieView.setmPercentage((float)percent);
            else
            {
                pieView.setmPercentage(0.00001f);
            }
            pieView.setPercentageTextSize(50);
            if(percent>=90.0)
                pieView.setPercentageBackgroundColor(getResources().getColor(R.color.barcolor1));
            else if(percent>=80.0)
                pieView.setPercentageBackgroundColor(getResources().getColor(R.color.barcolor2));
            else if(percent>=(getPercent()-5))
                pieView.setPercentageBackgroundColor(getResources().getColor(R.color.barcolor3));
            else
                pieView.setPercentageBackgroundColor(getResources().getColor(R.color.barcolor4));


            //------------------------------------------------------
            t=(TextView)findViewById(R.id.subName);
            t.setText(subjectName);

            t=(TextView)findViewById(R.id.totalClasses);
            t.setText("Total Classes : "+(total+off));

            t=(TextView)findViewById(R.id.attended);
            t.setText("Total Classes attended : "+present);

            t=(TextView)findViewById(R.id.absent);
            t.setText("Total Classes absent : " + absent);

            t=(TextView)findViewById(R.id.cancelled);
            t.setText("Total Classes cancelled : " + off);

            t=(TextView)findViewById(R.id.predicted);
            int x =(int) Math.floor((present -CONST*total)/CONST);
            int y =(int) Math.ceil((CONST*total - present)/(1-CONST));
            if(x==0)
                t.setText("You cannot miss the next class.");
            else if(y==0)
                t.setText("You are right on the margin!");
            else if(percent > getPercent())
            t.setText("You can miss the next " + x + " classes !");
            else
            t.setText("You have to attend the next " + y + " classes !");
        }
    }
    int getPercent()
    {
        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        int PERCENT = sp.getInt("percent_key",75);
        return PERCENT;
    }
}
