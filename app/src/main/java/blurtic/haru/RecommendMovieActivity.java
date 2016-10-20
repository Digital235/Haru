package blurtic.haru;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

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

        weatherIcon = (ImageView) findViewById(R.id.img_movieweather);
        weatherMessage = (TextView) findViewById(R.id.txt_movieweather);
        layout = (ViewGroup) findViewById(R.id.root_layout);

        weatherMessage.setText("데이터를 받아오는 중입니다.");

        new Thread() {
            @Override
            public void run() {
                String path ="http://asdfgh25.dothome.co.kr/haruconnect.txt";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");
                    c.connect();
                    InputStream in = c.getInputStream();
                    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    in.read(buffer); // Read from Buffer.
                    bo.write(buffer); // Write Into Buffer.

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TextView text = (TextView) findViewById(R.id.TextView1);
                            //text.setText(bo.toString());
                            Log.v("test",bo.toString());
                            if(bo.toString().startsWith("ok")){
                                initialize();
                            }
                            else{
                                weatherMessage.setText("서버 접속에 실패했습니다.");
                            }
                            try {
                                bo.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.v("error", e.getMessage());
                    weatherMessage.setText("서버 접속에 실패했습니다.");
                }
//               catch (ProtocolException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }.start();

        //initialize();

    }

    public void initialize(){
        movieArray = new ArrayList<MovieItem>();

        Intent intent = getIntent();
        currentWeather = intent.getExtras().getInt("weather");
        setWeather(currentWeather);

        MovieItem.globalNumber = 0;
        movieArray.clear();

        ArrayList<Integer> shuffleList= new ArrayList<Integer>();

        if(currentWeather==1 || currentWeather == 4){//흐림 13~25
            for(int i=13; i<26; i++){
                shuffleList.add(i);
            }
        }
        else if(currentWeather==2 || currentWeather == 3){//비 26~38
            for(int i=26; i<39; i++){
                shuffleList.add(i);
            }
        }
        else{//맑음, 기타 0~12
            for(int i=0; i<13; i++){
                shuffleList.add(i);
            }
        }

        Collections.shuffle(shuffleList);

        for (int i = 0; i < 10; i++) {
            if (MovieDatabase.items[shuffleList.get(i)] != null) {
                MovieDatabase.items[shuffleList.get(i)].count();
                movieArray.add(MovieDatabase.items[shuffleList.get(i)]);
            }
        }

        for (int i = 0; i < 10; i++) {
            addRow(movieArray.get(i));
        }
    }


    @Override
    public void finish() {
        saveLikes();
        super.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    void addRow(MovieItem mov) {
        View item = LayoutInflater.from(this).inflate(R.layout.recommend_listitem, null);
        TextView titleView = (TextView) item.findViewById(R.id.txt_recommendinfo2);
        TextView genreView = (TextView) item.findViewById(R.id.txt_recommendinfo1);
        titleView.setText(mov.title);
        genreView.setText(mov.genre);
        final String url = mov.url;
        final int movNumber = mov.number;

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
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);
                ab.setView(webView);
                ab.setPositiveButton("확인", null);
                ab.show();
            }
        });
        final ImageView likeButton = (ImageView) item.findViewById(R.id.bt_like);
        if (movieArray.get(movNumber).favorite) {
            likeButton.setImageResource(R.drawable.ic_heart2);
        } else {
            likeButton.setImageResource(R.drawable.ic_heart1);
        }
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

    void setWeather(int weather) {
        if (weather == 0) {
            weatherIcon.setImageResource(R.mipmap.icon_sun);
            weatherMessage.setText("오늘의 날씨는 '맑음'입니다");
        } else if (weather == 1) {
            weatherIcon.setImageResource(R.mipmap.icon_cloud);
            weatherMessage.setText("오늘의 날씨는 '흐림'입니다");
        } else if (weather == 2) {
            weatherIcon.setImageResource(R.mipmap.icon_rain);
            weatherMessage.setText("오늘의 날씨는 '비'입니다");
        } else if (weather == 3) {
            weatherIcon.setImageResource(R.mipmap.icon_thunder);
            weatherMessage.setText("오늘의 날씨는 '천둥'입니다");
        } else if (weather == 4) {
            weatherIcon.setImageResource(R.mipmap.icon_gurum);
            weatherMessage.setText("오늘의 날씨는 '구름'입니다");
        } else if (weather == 5) {
            weatherIcon.setImageResource(R.mipmap.icon_snow);
            weatherMessage.setText("오늘의 날씨는 '눈'입니다");
        }
    }

    void saveLikes() {
        String filename = "l" + stringifyDate();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(LikeJsonParser.toJson().toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String stringifyDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(c.getTime());
    }
}

class MovieItem {
    static int globalNumber = 0;
    int number;
    String genre;
    String title;
    String url;
    boolean favorite;

    MovieItem(String g, String t, String u) {
        genre = g;
        title = t;
        url = u;
        favorite = false;
    }
    void count(){
        number=globalNumber;
        globalNumber++;
    }
}

class MovieDatabase {
    public static MovieItem[] items = new MovieItem[39];

    static {
        items[0] = new MovieItem("애니메이션, 모험, 가족","드림 쏭","http://movie.naver.com/movie/bi/mi/basic.nhn?code=133447");
        items[1] = new MovieItem("판타지, 미스터리", "미스 페레그린과 이상한 아이들의 집", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=129383");
        items[2] = new MovieItem("미스터리, 스릴러","인페르노","http://movie.naver.com/movie/bi/mi/basic.nhn?code=129461");
        items[3] = new MovieItem("액션, 스릴러","어카운턴트","http://movie.naver.com/movie/bi/mi/basic.nhn?code=134859");
        items[4] = new MovieItem("코미디","럭키","http://movie.naver.com/movie/bi/mi/basic.nhn?code=140695");
        items[5] = new MovieItem("멜로/로맨스, 드라마","노트북","http://movie.naver.com/movie/bi/mi/basic.nhn?code=38899");
        items[6] = new MovieItem("다큐멘터리","자백","http://movie.naver.com/movie/bi/mi/basic.nhn?code=146534");
        items[7] = new MovieItem("드라마","걷기왕","http://movie.naver.com/movie/bi/mi/basic.nhn?code=145804");
        items[8] = new MovieItem("드라마","비틀스: 에잇 데이즈 어 위크 - 투어링 이어즈","http://movie.naver.com/movie/bi/mi/basic.nhn?code=152266");
        items[9] = new MovieItem("액션","바스티유 데이","http://movie.naver.com/movie/bi/mi/basic.nhn?code=137921");
        items[10] = new MovieItem("코미디, 가족, 판타지","미스터 캣","http://movie.naver.com/movie/bi/mi/basic.nhn?code=137972");
        items[11] = new MovieItem("모험, 판타지", "피터와 드래곤", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=134847");
        items[12] = new MovieItem("코미디, 멜로", "브리짓 존스의 베이비", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=143456");
        //
        items[13] = new MovieItem("드라마", "설리: 허드슨강의 기적", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=143495");
        items[14] = new MovieItem("액션", "밀정", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=137952");
        items[15] = new MovieItem("드라마", "그물", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=149174");
        items[16] = new MovieItem("드라마", "죽여주는 여자", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=146508");
        items[17] = new MovieItem("애니메이션, 모험", "바다 탐험대 옥토넛 시즌4: 늪지탐험선K", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=149505");
        items[18] = new MovieItem("애니메이션, 멜로/로맨스","예전부터 계속 좋아했어. ~고백실행위원회~","http://movie.naver.com/movie/bi/mi/basic.nhn?code=150338");
        items[19] = new MovieItem("드라마","다가오는 것들","http://movie.naver.com/movie/bi/mi/basic.nhn?code=147945");
        items[20] = new MovieItem("드라마, 애니메이션, 판타지","킹 오브 프리즘","http://movie.naver.com/movie/bi/mi/basic.nhn?code=152160");
        items[21] = new MovieItem("코미디, 드라마, 멜로/로맨스","카페 소사이어티","http://movie.naver.com/movie/bi/mi/basic.nhn?code=136870");
        items[22] = new MovieItem("드라마, 멜로/로맨스","립반윙클의 신부","http://movie.naver.com/movie/bi/mi/basic.nhn?code=150551");
        items[23] = new MovieItem("코미디, 멜로/로맨스, 판타지","미드나잇 인 파리","http://movie.naver.com/movie/bi/mi/basic.nhn?code=74610");
        items[24] = new MovieItem("공포, 스릴러", "맨 인 더 다크", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=144944");
        items[25] = new MovieItem("드라마","비바","http://movie.naver.com/movie/bi/mi/basic.nhn?code=144290");
        //
        items[26] = new MovieItem("드라마, 판타지","우주의 크리스마스", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=142641");
        items[27] = new MovieItem("드라마","설리: 허드슨강의 기적","http://movie.naver.com/movie/bi/mi/basic.nhn?code=143495");
        items[28] = new MovieItem("드라마","그물","http://movie.naver.com/movie/bi/mi/basic.nhn?code=149174");
        items[29] = new MovieItem("범죄, 스릴러, 미스터리","유주얼 서스펙트","http://movie.naver.com/movie/bi/mi/basic.nhn?code=17150");
        items[30] = new MovieItem("다큐멘터리, 드라마","물숨","http://movie.naver.com/movie/bi/mi/basic.nhn?code=137841");
        items[31] = new MovieItem("다큐멘터리"," 다음 침공은 어디?","http://movie.naver.com/movie/bi/mi/basic.nhn?code=142241");
        items[32] = new MovieItem("드라마","태풍이 지나가고","http://movie.naver.com/movie/bi/mi/basic.nhn?code=146548");
        items[33] = new MovieItem("액션, 스릴러","드라이브","http://movie.naver.com/movie/bi/mi/basic.nhn?code=80466");
        items[34] = new MovieItem("다큐멘터리","연인과 독재자","http://movie.naver.com/movie/bi/mi/basic.nhn?code=146193");
        items[35] = new MovieItem("액션, 모험, 드라마, 공포, 스릴러","디시에르토","http://movie.naver.com/movie/bi/mi/basic.nhn?code=120581");
        items[36] = new MovieItem("SF, 스릴러, 액션","칠드런 오브 맨","http://movie.naver.com/movie/bi/mi/basic.nhn?code=63113");
        items[37] = new MovieItem("범죄, 액션", "아수라", "http://movie.naver.com/movie/bi/mi/basic.nhn?code=44913");
        items[38] = new MovieItem("드라마","우리들","http://movie.naver.com/movie/bi/mi/basic.nhn?code=146504");
    }
}

