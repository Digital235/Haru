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
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.androidconnect.listview.horizontal.adapter.HorizontalListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import blurtic.haru.APISet.Weather.Class.WeatherData;
import blurtic.haru.APISet.Weather.Class.WeatherToTime;
import blurtic.haru.APISet.Weather.Manager.URLConnectionManager;
import blurtic.haru.APISet.Weather.Message.HandlerMessage;

public class MainActivity extends AppCompatActivity {
    int currentWeather;
    int currentTemp;
    ArrayList<TimeWeatherItem> timeWeatherItemList = new ArrayList<TimeWeatherItem>();
    ImageView todayWeatherIcon;
    TextView todayTemp;
    TextView todaySummery;
    TextView todayDate;
    TextView todayDetail;
    FrameLayout recommendMovie;
    FrameLayout recommendMusic;

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
        HorizontalListView timeWeatherListView = (HorizontalListView) findViewById(R.id.listview_timeweather);
        recommendMovie = (FrameLayout)findViewById(R.id.recommend_movie);
        recommendMusic = (FrameLayout)findViewById(R.id.recommend_music);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item3);

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
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "menu3", Toast.LENGTH_SHORT).show();
                materialDesignFAM.close(false);
            }
        });

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

        bt_Detail.setOnClickListener(onDetailClicked);
        layout_TodayDetail.setVisibility(View.GONE);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy년 M월 d일");
        todayDate.setText(df.format(c.getTime()));

        setTodaySummery(14, 24);



        timeWeatherItemList.add(new TimeWeatherItem(3, 1, 14));
        timeWeatherItemList.add(new TimeWeatherItem(6, 1, 14));
        timeWeatherItemList.add(new TimeWeatherItem(9, 1, 16));
        timeWeatherItemList.add(new TimeWeatherItem(12, 1, 21));
        timeWeatherItemList.add(new TimeWeatherItem(15, 1, 24));
        timeWeatherItemList.add(new TimeWeatherItem(18, 0, 20));
        timeWeatherItemList.add(new TimeWeatherItem(21, 0, 18));
        timeWeatherItemList.add(new TimeWeatherItem(24, 0, 15));
        todayDetail.setText("강수확률 : 20%\n\n풍향 : 남동\n풍속 : 2 (m/s)\n\n" + "습도 : 61%\n\n식중독지수 : 29 (관심) \n자외선지수 : 3 (보통)");

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        currentWeather=timeWeatherItemList.get(timeWeatherItemList.size()-1).weather;
        currentTemp=timeWeatherItemList.get(timeWeatherItemList.size()-1).temp;
        for(int i=1; i<timeWeatherItemList.size(); i++){
            if(timeWeatherItemList.get(i).time>currentHour) {
                currentWeather = timeWeatherItemList.get(i - 1).weather;
                currentWeather = timeWeatherItemList.get(i - 1).temp;
                break;
            }
        }
        setWeatherImage(todayWeatherIcon, currentWeather);
        todayTemp.setText(Integer.toString(currentTemp)+"℃");

        timeWeatherListView.setAdapter(new HAdapter());
    }

    void setWeatherImage(ImageView image, int weather){
        if(weather==0)image.setImageResource(R.mipmap.icon_sun);
        else if(weather==1)image.setImageResource(R.mipmap.icon_cloud);
        else if(weather==2)image.setImageResource(R.mipmap.icon_rain);
        else if(weather==3)image.setImageResource(R.mipmap.icon_thunder);
    }

    void setTodaySummery(int min, int max){
        todaySummery.setText("최저기온 : "+min+"℃\n최대기온 : "+max+"℃");
    }

    class TimeWeatherItem{
        int time;
        int weather;
        int temp;

        TimeWeatherItem(int t, int w, int t2){
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
            title.setText(""+timeWeatherItemList.get(position).time+"시");

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
            }
        }
    };
}
