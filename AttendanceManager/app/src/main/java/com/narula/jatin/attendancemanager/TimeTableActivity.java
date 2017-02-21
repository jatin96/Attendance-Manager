package com.narula.jatin.attendancemanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TimeTableActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    com.cuboid.cuboidcirclebutton.CuboidButton btn,btn0,btn1,btn2,btn3,btn4,btn5,btn6;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter adapter;
    ImageButton help_table;
    SQLiteDatabase mydatabase;
    Cursor resultSet;
    final String msg="Know your classes for a particular day by clicking on the appropriate button.\n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        //------------------------------------------------------
            MyRecyclerViewAdapter obj=new MyRecyclerViewAdapter(null);
            obj.setLayout(0);
        //------------------------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        help_table= (ImageButton) findViewById(R.id.help_table);
        help_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TimeTableActivity.this);
                dialog.setTitle("How to use?");
                dialog.setMessage(msg);
                dialog.create().show();

            }
        });
        ButtonBar();
        //--------------------------------

        //--------------------------------
    }
    void ButtonBar()
    {
        btn=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Mon);
        decolorAll();
        btn.setBackgroundColor(Color.rgb(156, 39, 176));
        CreateList("Monday");

        btn0=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Mon);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decolorAll();
                btn0.setBackgroundColor(Color.rgb(156, 39, 176));
                CreateList("Monday");
            }
        });

        btn1=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Tue);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decolorAll();
                btn1.setBackgroundColor(Color.rgb(156, 39, 176));
                CreateList("Tuesday");
            }
        });

        btn2=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Wed);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decolorAll();
                btn2.setBackgroundColor(Color.rgb(156, 39, 176));
                CreateList("Wednesday");
            }
        });

        btn3=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Thu);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decolorAll();
                btn3.setBackgroundColor(Color.rgb(156, 39, 176));
                CreateList("Thursday");
            }
        });

        btn4=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Fri);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decolorAll();
                btn4.setBackgroundColor(Color.rgb(156, 39, 176));
                CreateList("Friday");
            }
        });

        btn5=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Sat);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decolorAll();
                btn5.setBackgroundColor(Color.rgb(156, 39, 176));
                CreateList("Saturday");
            }
        });

        btn6=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Sun);
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decolorAll();
                btn6.setBackgroundColor(Color.rgb(156, 39, 176));
                CreateList("Sunday");
            }
        });

    }
    void decolorAll()
    {
        com.cuboid.cuboidcirclebutton.CuboidButton btn1;
        btn1=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Mon);
        btn1.setBackgroundColor(Color.TRANSPARENT);
        btn1=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Tue);
        btn1.setBackgroundColor(Color.TRANSPARENT);
        btn1=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Wed);
        btn1.setBackgroundColor(Color.TRANSPARENT);
        btn1=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Thu);
        btn1.setBackgroundColor(Color.TRANSPARENT);
        btn1=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Fri);
        btn1.setBackgroundColor(Color.TRANSPARENT);
        btn1=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Sat);
        btn1.setBackgroundColor(Color.TRANSPARENT);
        btn1=(com.cuboid.cuboidcirclebutton.CuboidButton)findViewById(R.id.Sun);
        btn1.setBackgroundColor(Color.TRANSPARENT);
    }
    void CreateList(String day)
    {
        if(mAdapter!=null)
            mAdapter=null;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        ArrayList results = new ArrayList<DataObject>();
        mydatabase = openOrCreateDatabase("SubjectsDB",MODE_PRIVATE,null);
        resultSet = mydatabase.rawQuery("SELECT * FROM subject WHERE days='"+day+"';",null);
        int index=0;
        if(resultSet.moveToFirst())
        {
            do {
                String str=resultSet.getString(3)+" - "+resultSet.getString(4);
                DataObject obj = new DataObject(resultSet.getString(1), str);
                results.add(index, obj);
                index++;
            }while(resultSet.moveToNext());
        }

        mAdapter = new MyRecyclerViewAdapter(results);
        mRecyclerView.setAdapter(mAdapter);
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(final int position, View v) {
               /* Log.i(LOG_TAG, " Clicked on Item " + position);
                final String text = ((TextView)v.findViewById(R.id.textView)).getText().toString();
                Intent i = new Intent(getBaseContext(),StatsActivity.class);
                i.putExtra("subjectName",text);
                startActivity(i);*/
                AlertDialog.Builder dialog = new AlertDialog.Builder(TimeTableActivity.this);
                dialog.setTitle("Options");
                final String sub = ((TextView)v.findViewById(R.id.textView)).getText().toString();
                final String time = ((TextView)v.findViewById(R.id.textView2)).getText().toString();
                final String starttime = time.substring(0,time.indexOf('-')).trim();
                final String endtime = time.substring(time.indexOf('-')+2).trim();
                String[] items = {"Delete"};
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      /*  if (which == 0)        //Edit
                        {
                            Toast.makeText(getBaseContext(),"called",Toast.LENGTH_SHORT).show();
                            //showCustomDialog(sub,position);
                        }*/

                        if (which == 0)      //Delete
                        {
                            SQLiteDatabase mydatabase = openOrCreateDatabase("SubjectsDB", MODE_PRIVATE, null);
                            mydatabase.execSQL("DELETE FROM subject WHERE subjects='"+sub+"' AND start='"+starttime+"' AND end='"+endtime+"';");
                            //Toast.makeText(getBaseContext(), sub + " Successfully Deleted ", Toast.LENGTH_SHORT).show();
                            ((MyRecyclerViewAdapter) mAdapter).deleteItem(position);
                        }
                    }
                });
                dialog.create().show();
            }
        });
    }
    public void showCustomDialog(final String old1,final int pos)
    {
        final Dialog dialog2 = new Dialog(TimeTableActivity.this);
        dialog2.setContentView(R.layout.custom_dialog1);
        dialog2.setTitle("Enter Subject name:");

        Button btn = (Button) dialog2.findViewById(R.id.btn);
        // if button is clicked, close the custom dialog
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) dialog2.findViewById(R.id.editText);
                String changedName = editText.getText().toString();
                if(changedName.equals(null))
                {
                    Toast.makeText(getBaseContext(), "Enter a valid name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveData(old1,changedName);

                    ((MyRecyclerViewAdapter) mAdapter).deleteItem(pos);
                    DataObject obj=new DataObject(changedName,"");
                    ((MyRecyclerViewAdapter) mAdapter).addItem(obj,pos);
                }
                dialog2.dismiss();
            }
        });
        dialog2.show();
    }
    public void saveData(String old1,String changedName)
    {
        //SQL database
        SQLiteDatabase mydatabase = openOrCreateDatabase("SubjectsDB",MODE_PRIVATE,null);
        String query="";
        String oldname = old1;

        query = "UPDATE subject SET subjects='" + changedName + "' WHERE subjects='" + oldname + "';" ;

        //     Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
        //   Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
        mydatabase.execSQL(query);
        Toast.makeText(getBaseContext(), "Subjects Successfully Updated", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}