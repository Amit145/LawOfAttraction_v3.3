package com.apps.amit.lawofattraction;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.apps.amit.lawofattraction.helper.InputFilterMinMax;
import com.apps.amit.lawofattraction.helper.LocaleHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.ButterKnife;

import static java.lang.Integer.parseInt;

public class SetTimeActivity extends AppCompatActivity {

    Button b1;
    EditText e1;
    TextView seconds;
    TextView title;
    TextView subTitle;
    RadioButton timerON ;
    RadioButton timerOFF;
    String timerMode="ON";
    static final String TIMER_ENABLE = "timerEnable";
    static final String USER_LANGUAGE = "UserLang";
    static final String LANGUAGE = "language";
    int e;
    private AdView adView;

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();

        e1=null;
        b1=null;
        this.finish();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        b1=null;
        e1=null;
        this.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        b1 =  findViewById(R.id.button);                    //set time button
        e1 =  findViewById(R.id.editText);
        timerOFF =  findViewById(R.id.radioOFF);
        timerON =  findViewById(R.id.radioON);
        seconds =  findViewById(R.id.textView9);          //seconds

        title =  findViewById(R.id.textView8);      //set time title
        subTitle =  findViewById(R.id.textView91);     // set time sub title

        e1.setFilters(new InputFilter[] { new InputFilterMinMax("1","900")});

        SharedPreferences sp1 = getSharedPreferences(TIMER_ENABLE, Exercise1Activity.MODE_PRIVATE);
        String timervalue = sp1.getString(TIMER_ENABLE,"ON");

        ButterKnife.bind(this);

        //get user selected language from shared preferences
        SharedPreferences pref = getSharedPreferences(USER_LANGUAGE,MODE_PRIVATE);

        //Store selected language in a Variable called value
        final String value1 = pref.getString(LANGUAGE,"en");
        updateViews(value1);

        Context context = LocaleHelper.setLocale(getApplicationContext(), value1);
        final Resources resources = context.getResources();

        if(timervalue.contains("ON")) {

            timerON.setChecked(true);
            timerOFF.setChecked(false);
            e1.setVisibility(View.VISIBLE);
            b1.setVisibility(View.VISIBLE);
            seconds.setText(resources.getString(R.string.seconds_text));
        }
        else {

            timerON.setChecked(false);
            timerOFF.setChecked(true);
            e1.setVisibility(View.INVISIBLE);
            b1.setVisibility(View.INVISIBLE);
            seconds.setVisibility(View.VISIBLE);
            seconds.setText(resources.getString(R.string.activityTimer_text3));
        }


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    e  = parseInt(e1.getText().toString());
                }
                catch(NumberFormatException exception) {

                    Toast.makeText(getApplicationContext(),resources.getString(R.string.timer_text),Toast.LENGTH_LONG).show();
                }

                SharedPreferences sp = getSharedPreferences("your_prefs", SetTimeActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("your_int_key", e);
                editor.apply();

                SharedPreferences sp1 = getSharedPreferences(TIMER_ENABLE, Exercise1Activity.MODE_PRIVATE);
                String timervalue = sp1.getString(TIMER_ENABLE,"ON");

                if(timervalue.contains("ON")) {

                    if (e > 0 && timervalue.contains("ON")) {

                        Toast.makeText(getApplicationContext(), resources.getString(R.string.timer_set)+" " + e + " "+resources.getString(R.string.seconds_text), Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), resources.getString(R.string.timer_enable), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void timerON(View view) {

        ButterKnife.bind(this);

        //get user selected language from shared preferences
        SharedPreferences pref = getSharedPreferences( USER_LANGUAGE,MODE_PRIVATE);

        //Store selected language in a Variable called value
        final String value1 = pref.getString(LANGUAGE,"en");

        Context context = LocaleHelper.setLocale(getApplicationContext(), value1);
        final Resources resources = context.getResources();

        timerMode="ON";
        timerON.setChecked(true);
        timerOFF.setChecked(false);
        e1.setVisibility(View.VISIBLE);
        b1.setVisibility(View.VISIBLE);
        seconds.setText(resources.getString(R.string.seconds_text));
        SharedPreferences timerEnable = getSharedPreferences(TIMER_ENABLE, SetTimeActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = timerEnable.edit();
        editor.putString(TIMER_ENABLE, timerMode);
        editor.apply();

        Toast.makeText(getApplicationContext(), resources.getString(R.string.timer_on)  , Toast.LENGTH_LONG).show();
    }

    public void timerOFF(View view) {

        ButterKnife.bind(this);

        //get user selected language from shared preferences
        SharedPreferences pref = getSharedPreferences(USER_LANGUAGE,MODE_PRIVATE);

        //Store selected language in a Variable called value
        final String value1 = pref.getString(LANGUAGE,"en");

        Context context = LocaleHelper.setLocale(getApplicationContext(), value1);
        final Resources resources = context.getResources();

        timerMode="OFF";
        timerON.setChecked(false);
        timerOFF.setChecked(true);
        e1.setVisibility(View.INVISIBLE);
        b1.setVisibility(View.INVISIBLE);
        seconds.setVisibility(View.VISIBLE);
        seconds.setText(resources.getString(R.string.activityTimer_text3));
        SharedPreferences timerEnable = getSharedPreferences(TIMER_ENABLE, SetTimeActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = timerEnable.edit();
        editor.putString(TIMER_ENABLE, timerMode);
        editor.apply();

        Toast.makeText(getApplicationContext(), resources.getString(R.string.timer_off) , Toast.LENGTH_LONG).show();
    }

    private void updateViews(String languageCode) {

        Context context = LocaleHelper.setLocale(this, languageCode);
        Resources resources = context.getResources();

        //StoryActivity
        title.setText(resources.getString(R.string.activityTimer_text1));
        subTitle.setText(resources.getString(R.string.activityTimer_text2));
        timerOFF.setText(resources.getString(R.string.off_text));
        timerON.setText(resources.getString(R.string.on_text));
        seconds.setText(resources.getString(R.string.seconds_text));
        b1.setText(resources.getString(R.string.activityTimer_text1));
    }
}
