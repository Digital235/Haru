package blurtic.haru;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecommendMusicActivity extends AppCompatActivity {
    ViewGroup layout;
    ImageView weatherIcon;
    TextView weatherMessage;

    ArrayList<MusicItem> musicArray;
    int currentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_music);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        weatherIcon = (ImageView)findViewById(R.id.img_musicweather);
        weatherMessage = (TextView)findViewById(R.id.txt_musicweather);
        layout = (ViewGroup)findViewById(R.id.root_layout);

        musicArray = new ArrayList<MusicItem>();

        Intent intent = getIntent();
        currentWeather = intent.getExtras().getInt("weather");
        setWeather(currentWeather);

        MusicItem.globalNumber=0;
        MovieItem.globalNumber=0;
        for(int i=0; i<10; i++){
            if(MovieDatabase.items[i]!=null) {
                musicArray.add(MusicDatabase.items[i]);
            }
        }

        for(int i=0; i<musicArray.size(); i++){
            addRow(musicArray.get(i));
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition( R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    void addRow(MusicItem mov){
        View item = LayoutInflater.from(this).inflate(R.layout.recommend_listitem, null);
        TextView titleView = (TextView) item.findViewById(R.id.txt_recommendinfo2);
        TextView genreView = (TextView) item.findViewById(R.id.txt_recommendinfo1);
        titleView.setText(mov.genre);
        genreView.setText(mov.title);
        final String url=mov.url;
        final int movNumber=mov.number;

        final LinearLayout info = (LinearLayout) item.findViewById(R.id.layout_recommendinfo);
        info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ColorDrawable viewColor = (ColorDrawable) info.getBackground();
                int colorId = viewColor.getColor();

                ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", Color.WHITE, colorId);
                colorAnim.setDuration(300);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.start();

                AlertDialog.Builder ab = new AlertDialog.Builder(RecommendMusicActivity.this);
                final WebView webView = new WebView(RecommendMusicActivity.this);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // TODO Auto-generated method stub
                        view.loadUrl(url);
                        return true;
                    }
                });
                webView.loadUrl(url);
                ab.setView(webView);
                ab.setPositiveButton("확인", null);
                ab.show();
            }
        });
        final ImageView likeButton = (ImageView)item.findViewById(R.id.bt_like);
        likeButton.setOnClickListener(new View.OnClickListener() {
            ImageView button = likeButton;
            int number = movNumber;

            public void onClick(View v) {
                ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", Color.WHITE, Color.parseColor("#00000000"));
                colorAnim.setDuration(300);
                colorAnim.setEvaluator(new ArgbEvaluator());
                colorAnim.start();

                if (musicArray.get(number).favorite) {
                    musicArray.get(number).favorite = false;
                    button.setImageResource(R.drawable.ic_heart1);
                    Toast.makeText(RecommendMusicActivity.this, "해당 음악에 '좋아요'를 해제하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    musicArray.get(number).favorite = true;
                    button.setImageResource(R.drawable.ic_heart2);
                    Toast.makeText(RecommendMusicActivity.this, "해당 음악에 '좋아요'를 설정하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        layout.addView(item);

        View horlzontalLine = LayoutInflater.from(this).inflate(R.layout.horizontal_line, null);
        layout.addView(horlzontalLine);
    }

    void setWeather(int weather){
        if(weather==0){
            weatherIcon.setImageResource(R.mipmap.icon_sun);
            weatherMessage.setText("오늘의 날씨는 '맑음'입니다");
        }
        else if(weather==1){
            weatherIcon.setImageResource(R.mipmap.icon_cloud);
            weatherMessage.setText("오늘의 날씨는 '흐림'입니다");
        }
        else if(weather==2){
            weatherIcon.setImageResource(R.mipmap.icon_rain);
            weatherMessage.setText("오늘의 날씨는 '비'입니다");
        }
        else if(weather==3){
            weatherIcon.setImageResource(R.mipmap.icon_thunder);
            weatherMessage.setText("오늘의 날씨는 '천둥'입니다");
        }
    }
}

class MusicItem{
    static int globalNumber=0;
    int number;
    String genre;
    String title;
    String url;
    boolean favorite;

    MusicItem(String g, String t, String u){
        number=globalNumber;
        genre=g;
        title=t;
        url=u;
        favorite=false;
        globalNumber++;
    }
}

class MusicDatabase{
    public static MusicItem[] items = new MusicItem[10];
    static{
        items[0]=new MusicItem("볼빨간사춘기","우주를 줄게","http://www.melon.com/album/detail.htm?albumId=2707131");
        items[1]=new MusicItem("박효신","숨","http://www.melon.com/album/detail.htm?albumId=10001952");
        items[2]=new MusicItem("임창정","내가 저지른 사랑","http://www.melon.com/album/detail.htm?albumId=2708957");
        items[3]=new MusicItem("한동근", "이 소설의 끝을 다시 써보려 해", "http://www.melon.com/album/detail.htm?albumId=2284064");
        items[4]=new MusicItem("Red Velvet (레드벨벳)", "러시안 룰렛 (Russian Roulette)", "http://www.melon.com/album/detail.htm?albumId=2709585");
        items[5]=new MusicItem("한동근", "그대라는 사치", "http://www.melon.com/album/detail.htm?albumId=2706226");
        items[6]=new MusicItem("유재석, EXO", "Dancing King", "http://www.melon.com/album/detail.htm?albumId=2711209");
        items[7]=new MusicItem("어반자카파", "목요일 밤 (Feat. 빈지노)", "http://www.melon.com/album/detail.htm?albumId=2705941");
        items[8]=new MusicItem("헤이즈 (Heize)", "돌아오지마 (Feat. 용준형 Of 비스트)", "http://www.melon.com/album/detail.htm?albumId=2679308");
        items[9]=new MusicItem("BLACKPINK", "휘파람", "http://www.melon.com/album/detail.htm?albumId=2703168");
    }
}
