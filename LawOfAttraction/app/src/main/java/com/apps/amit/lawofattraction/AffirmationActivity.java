package com.apps.amit.lawofattraction;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.amit.lawofattraction.notifications.AffirmationNotificationReceiver;
import com.apps.amit.lawofattraction.service.FirebaseTokenGenerator;
import com.apps.amit.lawofattraction.sqlitedatabase.ActivityTrackerDatabaseHandler;
import com.apps.amit.lawofattraction.utils.ManifestationTrackerUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AffirmationActivity extends AppCompatActivity {

    TextView count;
    TextView affirmationTitle;
    TextView affirmationSubTitle;
    AffirmationHome affirmations ;
    Button button;
    boolean flag = false;
    LinearLayout mainlayout;
    int counter = 0 ;
    String s = null;
    String subTitle = null;
    ActivityTrackerDatabaseHandler db;
    Calendar cal;
    String token = "";
    public static final String DAY_COUNTER = "counter";
    int displayCount = 0;
    private InterstitialAd interstitialAd;
    ConnectivityManager connMngr;
    String userInfo = "http://www.innovativelabs.xyz/insert_affirmUser.php";
    NetworkInfo netInfo;
    RequestQueue requestQueue;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    public static final String TAG = AffirmationActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affirmationactivity);

        mainlayout = findViewById(R.id.mainlayout);

        Glide.with(this).load(R.drawable.starshd).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mainlayout.setBackground(resource);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

                /*
                Not required
                 */
            }
        });

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE,1);

        affirmations = new AffirmationHome();

        Intent result = getIntent();

        if(result.getExtras()!=null) {
            counter = result.getExtras().getInt(DAY_COUNTER);
        }

        count = findViewById(R.id.affirmationCount);
        affirmationTitle = findViewById(R.id.affirmationTitle);
        affirmationSubTitle = findViewById(R.id.affirmationSubtitle);
        button = findViewById(R.id.affirmationDoneButton);

        if(counter<affirmations.getList().size()) {
            s = affirmations.getList().get(counter);
        } else {
            counter = 0 ;
            s = affirmations.getList().get(counter);
        }

        displayCount = counter + 1;
        count.setText(getString(R.string.hashTag)+displayCount);
        affirmationTitle.setText(s);

        affirmationSubTitle.setText(getString(R.string.myAffirmSubtitle));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(buttonClick);
                Intent intent = new Intent(getApplicationContext(), AffirmationHome.class);
                intent.putExtra(DAY_COUNTER,counter++);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy",
                        Locale.getDefault());
                String date = df.format(c);

                db = new ActivityTrackerDatabaseHandler(getApplicationContext());
                db.addContact(new ManifestationTrackerUtils(date, s));

                SharedPreferences sp = getSharedPreferences("Affirmation_Counter", AffirmationActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(DAY_COUNTER,counter);
                editor.putString("Time",String.valueOf(cal.getTime()));
                editor.apply();

                connMngr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connMngr!=null && connMngr.getActiveNetworkInfo() != null){

                    netInfo = connMngr.getActiveNetworkInfo();
                }

                if((netInfo!= null && netInfo.isConnected()) && counter!=affirmations.getList().size()){

                        FirebaseTokenGenerator firebaseTokenGenerator = new FirebaseTokenGenerator();
                        token = firebaseTokenGenerator.getFirebaseToken();

                        SharedPreferences sp1 = getSharedPreferences("timerEnable", MyStoryActivity.MODE_PRIVATE);

                        String name = sp1.getString("userName","");

                        requestQueue = Volley.newRequestQueue(getApplicationContext());

                        sendDataToServer(String.valueOf(displayCount),name,s,date,token);
                }


                if(counter!=affirmations.getList().size()){
                    callNotification(cal.getTimeInMillis());
                    displayInterstitial();
                }
            }
        });

        // Create the InterstitialAd and set the adUnitId.
        interstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        interstitialAd.setAdUnitId(getString(R.string.TestInterstitialAdsBannerGoogle));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "Interstitial ad Loaded!");
                flag=Boolean.TRUE;
            }
        });


    }

    private void sendDataToServer(final String day, final String name, final String affirmation, final String date, final String token) {

            String url = userInfo;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                           }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.e("Error.Response", ""+error.getMessage());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<>();

                    params.put("date", date);
                    params.put("day", day);
                    params.put("name", name);
                    params.put("affirmation", affirmation);
                    params.put("token", token);

                    return params;
                }
            };

            requestQueue.add(postRequest);


    }

    private void callNotification(long timeInMillis) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AffirmationNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        if(alarmManager!=null) {
            alarmManager.setRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitialAd == null || !interstitialAd.isLoaded()) {
            return;
        }

        if (Boolean.TRUE.equals(flag) && interstitialAd.isLoaded()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    interstitialAd.show();
                }
            }, 1000);
        }
    }
}
