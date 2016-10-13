package blurtic.haru;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    int currentWeather;
    ArrayList<TimeWeatherItem> timeWeatherItemList = new ArrayList<TimeWeatherItem>();
    ImageView todayWeatherIcon;
    TextView todaySummery;
    TextView todayDate;
    FrameLayout recommendMovie;
    FrameLayout recommendMusic;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;


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
        todaySummery = (TextView)findViewById(R.id.txt_todaysummery);
        todayDate = (TextView)findViewById(R.id.txt_date);
        final LinearLayout layout_TodayDetail = (LinearLayout)findViewById(R.id.layout_today_detail);
        HorizontalListView timeWeatherListView = (HorizontalListView) findViewById(R.id.listview_timeweather);
        recommendMovie = (FrameLayout)findViewById(R.id.recommend_movie);
        recommendMusic = (FrameLayout)findViewById(R.id.recommend_music);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item3);

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

        setTodaySummery(10,20);

        currentWeather=0;
        setWeatherImage(todayWeatherIcon,currentWeather);

        timeWeatherItemList.add(new TimeWeatherItem(0, 1, 2));
        timeWeatherItemList.add(new TimeWeatherItem(3, 2, 3));
        timeWeatherItemList.add(new TimeWeatherItem(6, 3, 4));

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
    ////////////////////////////////////////////////////weather list
//    private static String[] dataObjects = new String[]{
//            "시간별 날씨",
//            "시간별 날씨2",
//            "시간별 날씨3",
//            "시간별 날씨4",
//            "시간별 날씨5",
//            "시간별 날씨6",
//            "시간별 날씨7",
//            "시간별 날씨8",
//            "시간별 날씨9",
//            "시간별 날씨10"};

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
            temp.setText(""+timeWeatherItemList.get(position).time+"℃");
            setWeatherImage(image, timeWeatherItemList.get(position).weather);
            title.setText(""+timeWeatherItemList.get(position).temp+"시");

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
}
