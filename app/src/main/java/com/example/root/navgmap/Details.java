package com.example.root.navgmap;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Details extends AppCompatActivity {
    double distance;
    String duration;
    double minute;
    String end;
    TextView textView;
    TextView cngtextview;
    TextView ubertextview;
    TextView pathaotextview;
    Double cngtotal;
    Double pathaototal;
    Double ubertotal;
    Button b1, b2;
    Double watingtime;
    Double watingcost;
    String time;
    Calendar calander;
    SimpleDateFormat simpleDateFormat;
    double Starttime;
    double totaltime;
    Chronometer simpleChronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        distance = Double.parseDouble(getIntent().getStringExtra("distancetotal"));
        duration = getIntent().getStringExtra("durationtotal");
        minute = Double.parseDouble(getIntent().getStringExtra("minutes"));
        end = getIntent().getStringExtra("endlocation");
        simpleChronometer =  findViewById(R.id.stopwatch);


        textView =  findViewById(R.id.text);
        cngtextview =  findViewById(R.id.text1);
        pathaotextview =  findViewById(R.id.text2);
        ubertextview =  findViewById(R.id.text3);

        b1 =  findViewById(R.id.start);
        b2 =  findViewById(R.id.end);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watingtime = 0.00;
                Starttime = timepicker();
                simpleChronometer.setBase(SystemClock.elapsedRealtime());
                simpleChronometer.start();


            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double endtime = timepicker();
                totaltime = endtime - Starttime;

                if (minute < totaltime) {
                    watingtime = (totaltime - minute);
                    watingcost = (watingtime * 2);
                    Double uberwatingcost = (watingtime * 3);
                    cngtotal += (watingtime * 2);
                    pathaototal += (watingtime * 2);
                    ubertotal += (watingtime * 3);
                    cngtextview.setText(" For CNG \n " + "Wating time: " + watingtime + " minutes" +
                            "\n" + "Wating charge: " + watingcost + " TK" +
                            "\n" + " Total cost: " + cngtotal + " TK");
                    pathaotextview.setText("For Pathao \n " + "Wating time: " + watingtime + " minutes" +
                            "\n" + "Wating charge: " + watingcost + " Tk" +
                            "\n" + " Total cost: " + pathaototal + " TK");

                    ubertextview.setText("For UBER \n " + "Wating time: " + watingtime + " minutes" +
                            "\n" + "Wating charge: " + uberwatingcost + " Tk" +
                            "\n" + " Total cost: " + ubertotal + " TK");
                }

                simpleChronometer.stop();

            }
        });


        textView.setText("CurrentLocation  To " + end + "\n" + "The distance is: "
                + distance + " Km " + "\n" +
                "The duration is: " + duration);


        if (distance > 2.00) {
            cngtotal = distance * 12;


        } else {
            cngtotal = 40.00;

        }


        Double litersOfPetrol1 = cngtotal;
        DecimalFormat df1 = new DecimalFormat("0.00");
        df1.setMaximumFractionDigits(2);
        cngtotal = Double.valueOf(df1.format(litersOfPetrol1));


        cngtextview.setText("For CNG \n " + "Wating charge: " + 0 +
                " TK" + "\n" + " Total cost: " + cngtotal + " TK");

        Double pathao = distance * 12;

        if (pathao <= 25) {
            pathaototal = 25.00;


        } else {
            pathaototal = pathao;

        }


        Double litersOfPetrol2 = pathaototal;
        DecimalFormat df2 = new DecimalFormat("0.00");
        df2.setMaximumFractionDigits(2);
        pathaototal = Double.valueOf(df2.format(litersOfPetrol2));

        pathaotextview.setText("For Pathao \n " + "Wating charge: " + 0 +
                " TK" + "\n" + " Total cost: " + pathaototal + " TK");


        Double uber = distance * 21;

        if (uber <= 50) {
            ubertotal = 50.00;


        } else {
            ubertotal = uber;

        }


        Double litersOfPetrol3 = ubertotal;
        DecimalFormat df3 = new DecimalFormat("0.00");
        df3.setMaximumFractionDigits(2);
        ubertotal = Double.valueOf(df3.format(litersOfPetrol3));

        ubertextview.setText("For UBER \n " + "Wating charge: " + 0 +
                " TK" + "\n" + " Total cost: " + ubertotal + " TK");


    }

    public int timepicker() {

        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");

        time = simpleDateFormat.format(calander.getTime());
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentminutes = rightNow.get(Calendar.MINUTE);
        int totalminute = hour * 60 + currentminutes;
        String result = "Current Time = " + time;
        Toast.makeText(Details.this, result, Toast.LENGTH_LONG).show();

        return totalminute;


    }
}