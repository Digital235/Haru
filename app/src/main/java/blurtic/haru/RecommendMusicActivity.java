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
                            Log.v("test", bo.toString());
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

    void initialize(){
        musicArray = new ArrayList<MusicItem>();

        Intent intent = getIntent();
        currentWeather = intent.getExtras().getInt("weather");
        setWeather(currentWeather);

        MusicItem.globalNumber = 0;
        musicArray.clear();

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
            if (MusicDatabase.items[shuffleList.get(i)] != null) {
                MusicDatabase.items[shuffleList.get(i)].count();
                musicArray.add(MusicDatabase.items[shuffleList.get(i)]);
            }
        }

        for (int i = 0; i < 10; i++) {
            addRow(musicArray.get(i));
        }
    }

    @Override
    public void finish(){
        saveLikes();
        super.finish();
        overridePendingTransition( R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    void addRow(MusicItem mov){
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
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);
                ab.setView(webView);
                ab.setPositiveButton("확인", null);
                ab.show();
            }
        });
        final ImageView likeButton = (ImageView)item.findViewById(R.id.bt_like);
        if (musicArray.get(movNumber).favorite) {
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
        else if(weather==4) {
            weatherIcon.setImageResource(R.mipmap.icon_gurum);
            weatherMessage.setText("오늘의 날씨는 '구름'입니다");
        }
        else if(weather==5) {
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



class MusicItem{
    static int globalNumber=0;
    int number;
    String genre;
    String title;
    String url;
    boolean favorite;

    MusicItem(String g, String t, String u){
        genre=g;
        title=t;
        url=u;
        favorite=false;
    }

    void count(){
        number=globalNumber;
        globalNumber++;
    }
}



class MusicDatabase{
    public static MusicItem[] items = new MusicItem[39];
    static{
        items[0]=new MusicItem("볼빨간사춘기","우주를 줄게","http://www.melon.com/album/detail.htm?albumId=2707131");
        items[1]=new MusicItem("박효신","숨","http://www.melon.com/album/detail.htm?albumId=10001952");
        items[2]=new MusicItem("임창정","내가 저지른 사랑","http://www.melon.com/album/detail.htm?albumId=2708957");
        items[3]=new MusicItem("박경 (블락비)","자격지심 (Feat. 은하 Of 여자친구)","http://www.melon.com/album/detail.htm?albumId=2685311");
        items[4]=new MusicItem("Red Velvet (레드벨벳)", "러시안 룰렛 (Russian Roulette)", "http://www.melon.com/album/detail.htm?albumId=2709585");
        items[5]=new MusicItem("한동근", "그대라는 사치", "http://www.melon.com/album/detail.htm?albumId=2706226");
        items[6]=new MusicItem("유재석, EXO", "Dancing King", "http://www.melon.com/album/detail.htm?albumId=2711209");
        items[7]=new MusicItem("TWICE (트와이스)","CHEER UP","http://www.melon.com/album/detail.htm?albumId=2681333");
        items[8]=new MusicItem("헤이즈 (Heize)", "돌아오지마 (Feat. 용준형 Of 비스트)", "http://www.melon.com/album/detail.htm?albumId=2679308");
        items[9]=new MusicItem("BLACKPINK", "휘파람", "http://www.melon.com/album/detail.htm?albumId=2703168");
        items[10]=new MusicItem("인피니트","태풍 (The Eye)","http://www.melon.com/album/detail.htm?albumId=2711212");
        items[11]=new MusicItem("엠씨더맥스","그대 그대 그대","http://www.melon.com/album/detail.htm?albumId=10004085");
        items[12]=new MusicItem("여자친구","너 그리고 나 (NAVILLERA)","http://www.melon.com/album/detail.htm?albumId=2696751");
        //
        items[13]=new MusicItem("한동근", "이 소설의 끝을 다시 써보려 해", "http://www.melon.com/album/detail.htm?albumId=2284064");
        items[14]=new MusicItem("어반자카파", "목요일 밤 (Feat. 빈지노)", "http://www.melon.com/album/detail.htm?albumId=2705941");
        items[15]=new MusicItem("혁오 (hyukoh)","와리가리","http://www.melon.com/album/detail.htm?albumId=2320721");
        items[16]=new MusicItem("자우림","스물다섯, 스물하나","http://www.melon.com/album/detail.htm?albumId=2210557");
        items[17]=new MusicItem("MC그리","이불 밖은 위험해","http://www.melon.com/album/detail.htm?albumId=10004202");
        items[18]=new MusicItem("김범수","사랑해요","http://www.melon.com/album/detail.htm?albumId=2702198");
        items[19]=new MusicItem("10cm","길어야 5분","http://www.melon.com/album/detail.htm?albumId=10004832");
        items[20]=new MusicItem("정준영","내가 너에게 가든 네가 나에게 오든","http://www.melon.com/album/detail.htm?albumId=2700037");
        items[21]=new MusicItem("브리즈","뭐라할까","http://www.melon.com/album/detail.htm?albumId=873053");
        items[22]=new MusicItem("웅산","아픔아","http://www.melon.com/album/detail.htm?albumId=10002979");
        items[23]=new MusicItem("온유 (ONEW), 이진아","밤과 별의 노래 (Starry Night)","http://www.melon.com/album/detail.htm?albumId=2704058");
        items[24]=new MusicItem("존박","네 생각","http://www.melon.com/album/detail.htm?albumId=2697716");
        items[25]=new MusicItem("BewhY (비와이)","Forever (Prod. By GRAY)","http://www.melon.com/album/detail.htm?albumId=2695189");
        //
        items[26]=new MusicItem("Crush","어떻게 지내","http://www.melon.com/album/detail.htm?albumId=10006417");
        items[27]=new MusicItem("방탄소년단","Stigma","http://www.melon.com/album/detail.htm?albumId=10004707");
        items[28]=new MusicItem("에일리","Home (Feat. 윤미래)","http://www.melon.com/album/detail.htm?albumId=10003256");
        items[29]=new MusicItem("DEAN","D (half moon) (Feat. 개코)","http://www.melon.com/album/detail.htm?albumId=2674623");
        items[30]=new MusicItem("씨잼 (C Jamm), BewhY (비와이)","puzzle","http://www.melon.com/album/detail.htm?albumId=2703714");
        items[31]=new MusicItem("BLACKPINK","붐바야","http://www.melon.com/album/detail.htm?albumId=2703168");
        items[32]=new MusicItem("케이윌","녹는다","http://www.melon.com/album/detail.htm?albumId=2711241");
        items[33]=new MusicItem("원더걸스","Why So Lonely","http://www.melon.com/album/detail.htm?albumId=2695552");
        items[34]=new MusicItem("방탄소년단","불타오르네 (FIRE)","http://www.melon.com/album/detail.htm?albumId=2680573");
        items[35]=new MusicItem("다비치","받는 사랑이 주는 사랑에게","http://www.melon.com/album/detail.htm?albumId=10005930");
        items[36]=new MusicItem("정준일","안아줘","http://www.melon.com/album/detail.htm?albumId=2038488");
        items[37]=new MusicItem("신용재 (포맨)","빌려줄게","http://www.melon.com/album/detail.htm?albumId=10005885");
        items[38]=new MusicItem("국카스텐","Pulse","http://www.melon.com/album/detail.htm?albumId=2690346");
    }
}
