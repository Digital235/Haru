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

public class RecommendMovieActivity extends AppCompatActivity {
    ViewGroup layout;
    ImageView weatherIcon;
    TextView weatherMessage;

    ArrayList<MovieItem> movieArray;
    int currentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand_movie);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        weatherIcon = (ImageView)findViewById(R.id.img_movieweather);
        weatherMessage = (TextView)findViewById(R.id.txt_movieweather);
        layout = (ViewGroup)findViewById(R.id.root_layout);

        movieArray = new ArrayList<MovieItem>();

        Intent intent = getIntent();
        currentWeather = intent.getExtras().getInt("weather");
        setWeather(currentWeather);

        MovieItem.globalNumber=0;
        for(int i=0; i<MovieDatabase.items.length; i++){
            if(MovieDatabase.items[i]!=null) {
                movieArray.add(MovieDatabase.items[i]);
            }
        }

        for(int i=0; i<movieArray.size(); i++){
            addRow(movieArray.get(i));
        }
    }

    void addRow(MovieItem mov){
        View item = LayoutInflater.from(this).inflate(R.layout.recommend_listitem, null);
        TextView titleView = (TextView) item.findViewById(R.id.txt_recommendinfo2);
        TextView genreView = (TextView) item.findViewById(R.id.txt_recommendinfo1);
        titleView.setText(mov.title);
        genreView.setText(mov.genre);
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

                AlertDialog.Builder ab = new AlertDialog.Builder(RecommendMovieActivity.this);
                final WebView webView = new WebView(RecommendMovieActivity.this);
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

                if (movieArray.get(number).favorite) {
                    movieArray.get(number).favorite = false;
                    button.setImageResource(R.drawable.ic_heart1);
                    Toast.makeText(RecommendMovieActivity.this, "해당 영화에 '좋아요'를 해제하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    movieArray.get(number).favorite = true;
                    button.setImageResource(R.drawable.ic_heart2);
                    Toast.makeText(RecommendMovieActivity.this, "해당 영화에 '좋아요'를 설정하였습니다.", Toast.LENGTH_SHORT).show();
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

class MovieItem{
    static int globalNumber=0;
    int number;
    String genre;
    String title;
    String url;
    boolean favorite;

    MovieItem(String g, String t, String u){
        number=globalNumber;
        genre=g;
        title=t;
        url=u;
        favorite=false;
        globalNumber++;
    }
}

class MovieDatabase{
    public static MovieItem[] items = new MovieItem[10];
    static{
        items[0]=new MovieItem("판타지, 미스터리","미스 페레그린과 이상한 아이들의 집","http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=129383");
        items[1]=new MovieItem("공포, 스릴러","맨 인 더 다크","http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=144944");
        items[2]=new MovieItem("범죄, 액션","아수라","http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=44913");
        items[3]=new MovieItem("코미디, 멜로", "브리짓 존스의 베이비", "http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=143456");
        items[4]=new MovieItem("드라마", "설리: 허드슨강의 기적", "http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=143495");
        items[5]=new MovieItem("액션", "밀정", "http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=137952");
        items[6]=new MovieItem("드라마", "그물", "http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=149174");
        items[7]=new MovieItem("모험, 판타지", "피터와 드래곤", "http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=134847");
        items[8]=new MovieItem("드라마", "죽여주는 여자", "http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=146508");
        items[9]=new MovieItem("애니메이션, 모험", "바다 탐험대 옥토넛 시즌4: 늪지탐험선K", "http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=149505");
    }
}
