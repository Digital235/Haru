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
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
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

public class RecommendFoodActivity extends AppCompatActivity {
    ViewGroup layout;
    ImageView weatherIcon;
    TextView weatherMessage;

    ArrayList<FoodItem> foodArray;
    int currentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_food);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        weatherIcon = (ImageView)findViewById(R.id.img_foodweather);
        weatherMessage = (TextView)findViewById(R.id.txt_foodweather);
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
        foodArray = new ArrayList<FoodItem>();

        Intent intent = getIntent();
        currentWeather = intent.getExtras().getInt("weather");
        setWeather(currentWeather);

        FoodItem.globalNumber = 0;
        foodArray.clear();

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
            if (FoodDatabase.items[shuffleList.get(i)] != null) {
                FoodDatabase.items[shuffleList.get(i)].count();
                foodArray.add(FoodDatabase.items[shuffleList.get(i)]);
            }
        }

        for (int i = 0; i < 10; i++) {
            addRow(foodArray.get(i));
        }
    }

    @Override
    public void finish(){
        saveLikes();
        super.finish();
        overridePendingTransition( R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    void addRow(FoodItem mov){
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

                AlertDialog.Builder ab = new AlertDialog.Builder(RecommendFoodActivity.this);
                final WebView webView = new WebView(RecommendFoodActivity.this);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // TODO Auto-generated method stub
                        view.loadUrl(url);
                        return true;
                    }
                });

                webView.setWebChromeClient(new WebChromeClient() {
                    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                        callback.invoke(origin, true, false);
                    }
                });
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setGeolocationEnabled(true);
                webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath() );
                webView.loadUrl(url);
                ab.setView(webView);
                ab.setPositiveButton("확인", null);
                ab.show();
            }
        });
        final ImageView likeButton = (ImageView)item.findViewById(R.id.bt_like);
        if (foodArray.get(movNumber).favorite) {
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

                if (foodArray.get(number).favorite) {
                    foodArray.get(number).favorite = false;
                    button.setImageResource(R.drawable.ic_heart1);
                    Toast.makeText(RecommendFoodActivity.this, "해당 음악에 '좋아요'를 해제하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    foodArray.get(number).favorite = true;
                    button.setImageResource(R.drawable.ic_heart2);
                    Toast.makeText(RecommendFoodActivity.this, "해당 음악에 '좋아요'를 설정하였습니다.", Toast.LENGTH_SHORT).show();
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



class FoodItem{
    static int globalNumber=0;
    int number;
    String genre;
    String title;
    String url;
    boolean favorite;

    FoodItem(String g, String t, String u){
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



class FoodDatabase{
    public static FoodItem[] items = new FoodItem[39];
    static{
        items[0]=new FoodItem("면/한식","냉면","http://map.daum.net/?from=total&nil_suggest=btn&q=냉면&tab=place");
        items[1]=new FoodItem("덮밥/한식","제육덮밥","http://map.daum.net/?from=total&nil_suggest=btn&q=제육덮밥&tab=place");
        items[2]=new FoodItem("고기/중식","탕수육","http://map.daum.net/?from=total&nil_suggest=btn&q=탕수육&tab=place");
        items[3]=new FoodItem("쌀/한식","비빔밥","http://map.daum.net/?from=total&nil_suggest=btn&q=비빔밥&tab=place");
        items[4]=new FoodItem("고기/한식","삼계탕","http://map.daum.net/?from=total&nil_suggest=btn&q=삼계탕&tab=place");
        items[5]=new FoodItem("면/베트남","쌀국수","http://map.daum.net/?from=total&nil_suggest=btn&q=쌀국수&tab=place");
        items[6]=new FoodItem("덮밥/인도", "커리","http://map.daum.net/?from=total&nil_suggest=btn&q=커리&tab=place");
        items[7]=new FoodItem("고기/한식", "갈비찜","http://map.daum.net/?from=total&nil_suggest=btn&q=갈비찜&tab=place");
        items[8]=new FoodItem("고기/한식", "돈까스","http://map.daum.net/?from=total&nil_suggest=btn&q=돈까스&tab=place");
        items[9]=new FoodItem("면/한식", "쫄면", "http://map.daum.net/?from=total&nil_suggest=btn&q=쫄면&tab=place");
        items[10]=new FoodItem("쌀/한식", "곤드레밥", "http://map.daum.net/?from=total&nil_suggest=btn&q=곤드레밥&tab=place");
        items[11]=new FoodItem("쌀/한식", "떡볶이", "http://map.daum.net/?from=total&nil_suggest=btn&q=떡볶이&tab=place");
        items[12]=new FoodItem("쌀/한식", "김밥", "http://map.daum.net/?from=total&nil_suggest=btn&q=김밥&tab=place");
        //
        items[13]=new FoodItem("면/중식","짬뽕","http://map.daum.net/?from=total&nil_suggest=btn&q=짬뽕&tab=place");
        items[14]=new FoodItem("인스턴트/양식", "햄버거","http://map.daum.net/?from=total&nil_suggest=btn&q=햄버거&tab=place");
        items[15]=new FoodItem("쌀/일식", "스시","http://map.daum.net/?from=total&nil_suggest=btn&q=스시&tab=place");
        items[16]=new FoodItem("해물/한식", "낙지볶음","http://map.daum.net/?from=total&nil_suggest=btn&q=낙지&tab=place");
        items[17]=new FoodItem("해물/한식", "아귀찜","http://map.daum.net/?from=total&nil_suggest=btn&q=아귀찜&tab=place");
        items[18]=new FoodItem("고기/한식", "삼겹살","http://map.daum.net/?from=total&nil_suggest=btn&q=삼겹살&tab=place");
        items[19]=new FoodItem("중식", "만두", "http://map.daum.net/?from=total&nil_suggest=btn&q=만두&tab=place");
        items[20]=new FoodItem("고기/중식", "양꼬치", "http://map.daum.net/?from=total&nil_suggest=btn&q=양꼬치&tab=place");
        items[21]=new FoodItem("고기/한식", "순대", "http://map.daum.net/?from=total&nil_suggest=btn&q=순대&tab=place");
        items[22]=new FoodItem("고기/한식", "수육", "http://map.daum.net/?from=total&nil_suggest=btn&q=수육&tab=place");
        items[23]=new FoodItem("쌀/한식", "김치볶음밥", "http://map.daum.net/?from=total&nil_suggest=btn&q=김치볶음밥&tab=place");
        items[24]=new FoodItem("탕/한식","육개장","http://map.daum.net/?from=total&nil_suggest=btn&q=육개장&tab=place");
        items[25]=new FoodItem("찌개/한식", "부대찌개", "http://map.daum.net/?from=total&nil_suggest=btn&q=부대찌개&tab=place");
        //
        items[26]=new FoodItem("전/한식", "파전","http://map.daum.net/?from=total&nil_suggest=btn&q=파전&tab=place");
        items[27]=new FoodItem("면/일식", "커리","http://map.daum.net/?from=total&nil_suggest=btn&q=라멘&tab=place");
        items[28]=new FoodItem("덮밥/인도", "커리","http://map.daum.net/?from=total&nil_suggest=btn&q=커리&tab=place");
        items[29]=new FoodItem("면/양식", "스파게티","http://map.daum.net/?from=total&nil_suggest=btn&q=스파게티&tab=place");
        items[30]=new FoodItem("고기/한식", "양념치킨","http://map.daum.net/?from=total&nil_suggest=btn&q=양념치킨&tab=place");
        items[31]=new FoodItem("해물/한식", "해물찜","http://map.daum.net/?from=total&nil_suggest=btn&q=해물찜&tab=place");
        items[32]=new FoodItem("면/한식", "칼국수","http://map.daum.net/?from=total&nil_suggest=btn&q=칼국수&tab=place");
        items[33]=new FoodItem("인스턴트/양식", "피자","http://map.daum.net/?from=total&nil_suggest=btn&q=피자&tab=place");
        items[34]=new FoodItem("면/일식","우동","http://map.daum.net/?from=total&nil_suggest=btn&q=우동&tab=place");
        items[35]=new FoodItem("면/중식","자장면","http://map.daum.net/?from=total&nil_suggest=btn&q=자장면&tab=place");
        items[36]=new FoodItem("해물/한식", "매운탕","http://map.daum.net/?from=total&nil_suggest=btn&q=매운탕&tab=place");
        items[37]=new FoodItem("해물/한식","회","http://map.daum.net/?from=total&nil_suggest=btn&q=회&tab=place");
        items[38]=new FoodItem("찌개/한식","김치찌개","http://map.daum.net/?from=total&nil_suggest=btn&q=김치찌개&tab=place");
    }
}
