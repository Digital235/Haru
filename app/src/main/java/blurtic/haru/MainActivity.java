package blurtic.haru;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.androidconnect.listview.horizontal.adapter.HorizontalListView;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import blurtic.haru.APISet.Weather.Class.WeatherData;
import blurtic.haru.APISet.Weather.Class.WeatherToTime;
import blurtic.haru.APISet.Weather.Manager.URLConnectionManager;
import blurtic.haru.APISet.Weather.Message.HandlerMessage;

public class MainActivity extends AppCompatActivity {
    int currentWeather=0;
    float currentTemp=0;
    ArrayList<TimeWeatherItem> timeWeatherItemList = new ArrayList<TimeWeatherItem>();
    ImageView todayWeatherIcon;
    TextView todayTemp;
    TextView todaySummery;
    TextView todayDate;
    TextView todayDetail;
    FrameLayout recommendMovie;
    FrameLayout recommendMusic;
    FrameLayout recommendFood;
    HorizontalListView timeWeatherListView;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    URLConnectionManager mManager;
    ArrayList<WeatherToTime> mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(blurtic.haru.R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        ImageButton bt_Detail = (ImageButton)findViewById(R.id.imgbtn_detail);
        todayWeatherIcon = (ImageView)findViewById(R.id.img_todayweather);
        todayTemp = (TextView)findViewById(R.id.txt_currentTemp);
        todaySummery = (TextView)findViewById(R.id.txt_todaysummery);
        todayDate = (TextView)findViewById(R.id.txt_date);
        todayDetail = (TextView)findViewById(R.id.txt_todayweather_detail);
        final LinearLayout layout_TodayDetail = (LinearLayout)findViewById(R.id.layout_today_detail);
        timeWeatherListView = (HorizontalListView) findViewById(R.id.listview_timeweather);
        recommendMovie = (FrameLayout)findViewById(R.id.recommend_movie);
        recommendMusic = (FrameLayout)findViewById(R.id.recommend_music);
        recommendFood = (FrameLayout)findViewById(R.id.recommend_food);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item2);
//        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item3);

        mManager = new URLConnectionManager(this.getApplicationContext(),"",this);
        mManager.run();

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "menu1", Toast.LENGTH_SHORT).show();
                materialDesignFAM.close(false);

                Intent intent=new Intent(MainActivity.this,PlanActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "menu2", Toast.LENGTH_SHORT).show();
                materialDesignFAM.close(false);

                Intent intent=new Intent(MainActivity.this,DiaryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
//        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "menu3", Toast.LENGTH_SHORT).show();
//                materialDesignFAM.close(false);
//            }
//        });

        View.OnClickListener onDetailClicked = new View.OnClickListener() {
            public void onClick(View v) {
                ImageButton bt = (ImageButton) v;
                if(layout_TodayDetail.getVisibility()==View.VISIBLE)//hide weather detail
                {
                    bt.setImageResource(android.R.drawable.arrow_down_float);
                    collapse(layout_TodayDetail);
                }
                else
                {
                    bt.setImageResource(android.R.drawable.arrow_up_float);// show weather detail
                    expand(layout_TodayDetail);
                }
            }
        };

        recommendMovie.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", Color.WHITE, Color.parseColor("#c8000000"));
                colorAnim.setDuration(300);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.start();

                //Toast.makeText(MainActivity.this, "movie", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,RecommendMovieActivity.class);
                intent.putExtra("weather", currentWeather);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });

        recommendMusic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", Color.WHITE, Color.parseColor("#c8000000"));
                colorAnim.setDuration(300);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.start();

                // Toast.makeText(MainActivity.this, "music", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,RecommendMusicActivity.class);
                intent.putExtra("weather", currentWeather);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });

        recommendFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", Color.WHITE, Color.parseColor("#c8000000"));
                colorAnim.setDuration(300);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.start();

                // Toast.makeText(MainActivity.this, "music", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,RecommendFoodActivity.class);
                intent.putExtra("weather", currentWeather);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });

        bt_Detail.setOnClickListener(onDetailClicked);
        layout_TodayDetail.setVisibility(View.GONE);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy년 M월 d일");
        todayDate.setText(df.format(c.getTime()));

        loadLikes();
    }

    void loadLikes(){
        String filename="l"+stringifyDate();
        try {
            File file = getFileStreamPath(filename);
            if(file == null || !file.exists()) {
                return;
            }
            Log.d("test","fileexist" );
            FileInputStream fis = openFileInput(filename);
            byte[] data = new byte[fis.available()];
            fis.read(data);

           LikeJsonParser.fromJson(new String(data));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String stringifyDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(c.getTime());
    }

    void setWeatherImage(ImageView image, int weather){
        if(weather==0)image.setImageResource(R.mipmap.icon_sun);
        else if(weather==1)image.setImageResource(R.mipmap.icon_cloud);
        else if(weather==2)image.setImageResource(R.mipmap.icon_rain);
        else if(weather==3)image.setImageResource(R.mipmap.icon_thunder);
        else if(weather==4)image.setImageResource(R.mipmap.icon_gurum);
        else if(weather==5)image.setImageResource(R.mipmap.icon_snow);
    }

    void setTodaySummery(int min, int max){
        todaySummery.setText("최저기온 : "+min+"℃\n최대기온 : "+max+"℃");
    }

    class TimeWeatherItem{
        int date;
        int time;
        int weather;
        float temp;

//        TimeWeatherItem(int t, int w, int t2){
//            time=t;
//            weather=w;
//            temp=(float)t2;
//        }
//        TimeWeatherItem(int t, int w, float t2){
//            time=t;
//            weather=w;
//            temp=t2;
//        }
        TimeWeatherItem(int d, int t, int w, float t2){
            date=d;
            time=t;
            weather=w;
            temp=t2;
        }
    }

    private class  HAdapter extends BaseAdapter {

        public HAdapter(){
            super();
        }

        public int getCount() {
            return timeWeatherItemList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
            TextView title = (TextView) retval.findViewById(R.id.txt_timeweather_date);
            TextView temp = (TextView) retval.findViewById(R.id.txt_timeweather_temp);
            ImageView image = (ImageView) retval.findViewById(R.id.img_timeweather_img);
            temp.setText(""+timeWeatherItemList.get(position).temp+"℃");
            setWeatherImage(image, timeWeatherItemList.get(position).weather);
            title.setText(""+timeWeatherItemList.get(position).date+"일 "+timeWeatherItemList.get(position).time+"시");

            return retval;
        }

    };
    ////////////////////////////////////////////////////weather list

    public static void expand(final View v) {
        //v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                v.getLayoutParams().height = interpolatedTime == 1
//                        ? LinearLayout.LayoutParams.WRAP_CONTENT
//                        : (int)(targetHeight * interpolatedTime);
                v.getLayoutParams().height = interpolatedTime == 1 ? targetHeight : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HandlerMessage.THREAD_HANDLER_MIDDLELAND_SUCCESS_INFO:
                    WeatherData mTotal = (WeatherData)msg.obj;
                    mTime = mTotal.mTimeWeather; // 이놈 쓰면댐
//                    String data = "";
//                    for(int i = 0; i < mTotal.mDayWeather.size(); i++)
//                    {
//                        data = data +  mTotal.mDayWeather.get(i).day + "/"
//                                + mTotal.mDayWeather.get(i).time + "/"
//                                + mTotal.mDayWeather.get(i).weather + "/"
//                                + mTotal.mDayWeather.get(i).maxTemp + "/"
//                                + mTotal.mDayWeather.get(i).minTemp + "\r\n";
//                    }
                   // tv_totalRead.setText(data);
                    if(mTime.size()>0) {
                        int tempMax=-9999;
                        int tempMin=9999;
                        int skyCode=Integer.parseInt(mTime.get(0).categoryValue[3]);
                        int ptyCode=Integer.parseInt(mTime.get(0).categoryValue[1]);
                        String sky=null;
                        String pty=null;
                        if(skyCode==1)sky="맑음";
                        else if(skyCode==2)sky="구름조금";
                        else if(skyCode==3)sky="구름많음";
                        else if(skyCode==4)sky="흐림";

                        if(ptyCode==0)pty="없음";
                        if(ptyCode==1)pty="비";
                        if(ptyCode==2)pty="진눈개비";
                        if(ptyCode==3)pty="눈";
                        todayDetail.setText(
                                "하늘상태 : " + sky + "\n" +
                                "강수형태 : " + pty + "\n" +
                                "강수확률 : " + mTime.get(0).categoryValue[0] + "%\n\n" +
                                "풍속(동서) : " + mTime.get(0).categoryValue[4] + "m/s\n" +
                                "풍속(남북) : " + mTime.get(0).categoryValue[5] + "m/s\n" +
                                "풍향 : " + mTime.get(0).categoryValue[6] + "\n\n" +
                                "습도 : " + mTime.get(0).categoryValue[2] + "%"
                        );
                        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                        if(ptyCode==2 || ptyCode==3)currentWeather = 5;
                        else if(ptyCode == 1)currentWeather=2;
                        else if(skyCode == 1)currentWeather = 0;
                        else if(skyCode == 2 || skyCode == 3)currentWeather = 4;
                        else if(skyCode == 4)currentWeather = 1;

                        currentTemp = Float.parseFloat(mTime.get(0).categoryValue[8]);
                        for (int i = 1; i < timeWeatherItemList.size(); i++) {
                            if (timeWeatherItemList.get(i).time > currentHour) {
                                currentWeather = timeWeatherItemList.get(i - 1).weather;
                                currentTemp = timeWeatherItemList.get(i - 1).temp;
                                break;
                            }
                        }
                        setWeatherImage(todayWeatherIcon, currentWeather);
                        todayTemp.setText(Float.toString(currentTemp) + "℃");

                        for(int i=0; i<mTime.size(); i++){
                            WeatherToTime data = mTime.get(i);
                            int time=Integer.parseInt(data.time.substring(0, 2));

                            int skyCodeTime=Integer.parseInt(data.categoryValue[3]);
                            int ptyCodeTime=Integer.parseInt(data.categoryValue[1]);

                            int weather=0;
                            if(ptyCodeTime==2 || ptyCodeTime==3)weather = 5;
                            else if(ptyCodeTime == 1)weather=2;
                            else if(skyCodeTime == 1)weather = 0;
                            else if(skyCodeTime == 2 || skyCodeTime == 3)weather = 4;
                            else if(skyCodeTime == 4)weather = 1;

                            float temp=0;
                            temp = Float.parseFloat(data.categoryValue[8]);
                            if(i<8) {
                                if ((int) temp < tempMin) tempMin = (int) temp;
                                if ((int) temp > tempMax) tempMax = (int) temp;
                            }

                            int date=0;
                            date=Integer.parseInt(data.date.substring(6));

                            timeWeatherItemList.add(new TimeWeatherItem(date, time, weather, temp));
                        }
                        timeWeatherListView.setAdapter(new HAdapter());

                        setTodaySummery(tempMin, tempMax);
                    }
            }
        }
    };
}
