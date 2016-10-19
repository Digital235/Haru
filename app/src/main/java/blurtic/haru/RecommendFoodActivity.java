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

        weatherIcon = (ImageView) findViewById(R.id.img_foodweather);
        weatherMessage = (TextView) findViewById(R.id.txt_foodweather);
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
        foodArray = new ArrayList<FoodItem>();

        Intent intent = getIntent();
        currentWeather = intent.getExtras().getInt("weather");
        setWeather(currentWeather);

        FoodItem.globalNumber = 0;
        for (int i = 0; i < 10; i++) {
            if (FoodDatabase.items[i] != null) {
                foodArray.add(FoodDatabase.items[i]);
            }
        }

        for (int i = 0; i < foodArray.size(); i++) {
            addRow(foodArray.get(i));
        }
    }


    @Override
    public void finish() {
        saveLikes();
        super.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    void addRow(FoodItem mov) {
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
                webView.loadUrl(url);
                ab.setView(webView);
                ab.setPositiveButton("확인", null);
                ab.show();
            }
        });
        final ImageView likeButton = (ImageView) item.findViewById(R.id.bt_like);
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
                    Toast.makeText(RecommendFoodActivity.this, "해당 영화에 '좋아요'를 해제하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    foodArray.get(number).favorite = true;
                    button.setImageResource(R.drawable.ic_heart2);
                    Toast.makeText(RecommendFoodActivity.this, "해당 영화에 '좋아요'를 설정하였습니다.", Toast.LENGTH_SHORT).show();
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

class FoodItem {
    static int globalNumber = 0;
    int number;
    String genre;
    String title;
    String url;
    boolean favorite;

    FoodItem(String g, String t, String u) {
        number = globalNumber;
        genre = g;
        title = t;
        url = u;
        favorite = false;
        globalNumber++;
    }
}

class FoodDatabase {
    public static FoodItem[] items = new FoodItem[10];

    static {
        items[0] = new FoodItem("판타지, 미스터리", "미스 페레그린과 이상한 아이들의 집", "https://m.map.naver.com/search2/search.nhn?query=%EB%B3%BC%EB%A7%81%EC%9E%A5&siteSort=1&sm=clk");
        items[1] = new FoodItem("공포, 스릴러", "맨 인 더 다크", "http://food.naver.com/food/bi/mi/basic.nhn?code=144944");
        items[2] = new FoodItem("범죄, 액션", "아수라", "http://food.naver.com/food/bi/mi/basic.nhn?code=44913");
        items[3] = new FoodItem("코미디, 멜로", "브리짓 존스의 베이비", "http://food.naver.com/food/bi/mi/basic.nhn?code=143456");
        items[4] = new FoodItem("드라마", "설리: 허드슨강의 기적", "http://food.naver.com/food/bi/mi/basic.nhn?code=143495");
        items[5] = new FoodItem("액션", "밀정", "http://food.naver.com/food/bi/mi/basic.nhn?code=137952");
        items[6] = new FoodItem("드라마", "그물", "http://food.naver.com/food/bi/mi/basic.nhn?code=149174");
        items[7] = new FoodItem("모험, 판타지", "피터와 드래곤", "http://food.naver.com/food/bi/mi/basic.nhn?code=134847");
        items[8] = new FoodItem("드라마", "죽여주는 여자", "http://food.naver.com/food/bi/mi/basic.nhn?code=146508");
        items[9] = new FoodItem("애니메이션, 모험", "바다 탐험대 옥토넛 시즌4: 늪지탐험선K", "http://food.naver.com/food/bi/mi/basic.nhn?code=149505");
    }
}

