package blurtic.haru.APISet.Weather.Manager;

import android.content.ContentValues;
import android.content.Context;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import blurtic.haru.APISet.Weather.Class.LocationInput;
import blurtic.haru.APISet.Weather.Class.MiddleInfo;
import blurtic.haru.APISet.Weather.Class.WeatherToDay;
import blurtic.haru.APISet.Weather.Class.WeatherToTime;
import blurtic.haru.APISet.TestActivity;
import blurtic.haru.APISet.Weather.Message.HandlerMessage;

/**
 * Created by Kim W on 2016-06-13.
 */


// 중기 예보를 먼저 받고, 그다음 단기 예보를 받아야함
public class URLConnectionManager extends Thread{



    //좌표 -> 지역으로 바꾸기 시@발..
    final static String TAG = "URLConnectionManager";
    TestActivity mHandler;
    String inputLocation;
    ArrayList<WeatherToDay> mDayWeather;
    ArrayList<WeatherToTime> mTimeWeather;
    ArrayList<String> mCurrentTimeAndDay;
    Context mContext;
    String lat;
    String lon;
    String st_key = "msBaq3etrDaXttTCXKAC9yeCoS%2Fn3%2BAARQ3J1727dBNDcGwzhs0Twu%2BDn1PDeKCu8iZtKJ9Mib9w3bXqXSvK2Q%3D%3D";
    String middleWeather;
    //00시에 어떤 값을 토대로 측정해야할까

//    String middleTemparature = "http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature?ServiceKey="+st_key +
//            "&regId=11D20501&tmFc=201606150600&pageNo=1&numOfRows=1";
    String middleTemparature;
    String MiddleBaseTime(String hour)
    {
        if(Integer.valueOf(hour) > 18) return "1800";
        if(Integer.valueOf(hour) > 6) return "0600";

        return "0000";
    }

    public URLConnectionManager(Context mContext, String inputLocation) {
        this.mContext = mContext;
        mCurrentTimeAndDay = MakeCurrentDayAndTime();
        this.inputLocation = inputLocation;
    }

    public String makeMiddleLandURL(String inputLocation)
    {
        LocationInput mLocation = new LocationInput(mContext);

        String regId = mLocation.makeRegId(inputLocation);
        String currentDay = mCurrentTimeAndDay.get(0) + mCurrentTimeAndDay.get(1) + mCurrentTimeAndDay.get(2);
        String time = MiddleBaseTime(mCurrentTimeAndDay.get(4));

        String resultUrl = "http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather?ServiceKey="+st_key+
                "&regId=" + regId + "&tmFc="+currentDay + time + "&numOfRows=1&pageNo=1";
        return resultUrl;
    }

    public String makeMiddleTempURL(String inputLocation)
    {
        String regId = "11H10701";
        String currentDay = mCurrentTimeAndDay.get(0) + mCurrentTimeAndDay.get(1) + mCurrentTimeAndDay.get(2);
        String time = MiddleBaseTime(mCurrentTimeAndDay.get(4));

        String resultUrl = "http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature?ServiceKey="+st_key +
            "&regId="+regId + "&tmFc="+currentDay+time+"&pageNo=1&numOfRows=1";


        return resultUrl;
    }


    public ArrayList<String> MakeCurrentDayAndTime()
    {
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
//SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String time = currentTime.format(new Date(System.currentTimeMillis()));

        StringTokenizer mToken = new StringTokenizer(time,".");
        ArrayList<String> mTemp = new ArrayList<>();
        while(mToken.hasMoreTokens())
        {
            mTemp.add(mToken.nextToken());
        }


        Log.i(TAG, "time print: " + time);
        for(int i = 0; i < mTemp.size(); i++)
            Log.i(TAG, "time print: " + mTemp.get(i));

        return mTemp;
    }

    public String DateAdd(int afterDay)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, afterDay);

        // 특정 형태의 날짜로 값을 뽑기
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String strDate = df.format(cal.getTime());
        Log.i(TAG,"add Date: " + strDate);
        return strDate;
    }

    int RequestURLTodayWeather(int pageNo)
    {
        int idx = 0;
        //
        try {

            URL url = new URL("");

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // 위에서 생성된 URL을 통하여 서버에 요청하면 결과가 XML Resource로 전달됨

            XmlPullParser parser = factory.newPullParser();
            // XML Resource를 파싱할 parser를 factory로 생성

            parser.setInput(url.openStream(), null);
            // 파서를 통하여 각 요소들의 이벤트성 처리를 반복수행

            int parserEvent = parser.getEventType();

            String initiail_TagName = "wf";
            String tagName = "";
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                // XML문이 끝날 때 까지 정보를 읽는다
                if (parserEvent == XmlPullParser.START_TAG) {
//                    TrainInfo train = new TrainInfo();
                    //시작태그의 이름을 알아냄
                    ContentValues mContent = new ContentValues();
                    if(parser.getName().contains(initiail_TagName)) {
                        for (; ; ) { // 하나의 item이 끝날 때, 들어감.
                            tagName = parser.getName();


                            parserEvent = parser.next();
                        }
                    }
                }
                parserEvent = parser.next();

            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return idx;
    }

   ContentValues RequestURL_MiddleInfoWeather(String urlData,String initial_TagName)
    {
        int idx = 0;
        int tagIndex[] = {10,3,4,5,6,7,8,9};

        ArrayList<String> mTemp = new ArrayList<>();

        ContentValues mContent = new ContentValues();
        //발표시각 06:00 , 18:00 둘중 하나
        try {

            URL url = new URL(urlData);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // 위에서 생성된 URL을 통하여 서버에 요청하면 결과가 XML Resource로 전달됨

            XmlPullParser parser = factory.newPullParser();
            // XML Resource를 파싱할 parser를 factory로 생성

            parser.setInput(url.openStream(), null);
            // 파서를 통하여 각 요소들의 이벤트성 처리를 반복수행

            int parserEvent = parser.getEventType();

            String tagName = "";
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                // XML문이 끝날 때 까지 정보를 읽는다
                if (parserEvent == XmlPullParser.START_TAG) {
                    //시작태그의 이름을 알아냄
                    if(parser.getName().contains(initial_TagName)) {
                        String name = parser.getName();
                        String value = parser.nextText();
                        mContent.put(name,value);
                        tagName = parser.getName();
                        mTemp.add(name);
                        mTemp.add(value);
                        mTemp.add("/");
                       // parserEvent = parser.next();

                        //for (int i = 0; i < tagIndex.length; i++) { // 하나의 item이 끝날 때, 들어감.

                        //}
                    }
                }
                parserEvent = parser.next();

            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return mContent;
    }
//    ContentValues RequestURL_MiddleInfoTemparature(URL url)
//    {
//        int idx = 0;
//        int tagIndex[] = {10,3,4,5,6,7,8,9};
//        ArrayList<String> mTemp = new ArrayList<>();
//        ContentValues mContent = new ContentValues();
//        //발표시각 06:00 , 18:00 둘중 하나
//        try {
//            //http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature?ServiceKey=TEST_SERVICEKEY&regId=11D20501&tmFc=201404080600&pageNo=1&numOfRows=1
//             url = new URL("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature?ServiceKey="+st_key +
//                    "&regId=11D20501&tmFc=201606150600&pageNo=1&numOfRows=1");
//
//            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//            // 위에서 생성된 URL을 통하여 서버에 요청하면 결과가 XML Resource로 전달됨
//
//            XmlPullParser parser = factory.newPullParser();
//            // XML Resource를 파싱할 parser를 factory로 생성
//
//            parser.setInput(url.openStream(), null);
//            // 파서를 통하여 각 요소들의 이벤트성 처리를 반복수행
//
//            int parserEvent = parser.getEventType();
//
//            String initial_TagName = "wf";
//            String tagName = "";
//            while (parserEvent != XmlPullParser.END_DOCUMENT) {
//                // XML문이 끝날 때 까지 정보를 읽는다
//                if (parserEvent == XmlPullParser.START_TAG) {
////                    TrainInfo train = new TrainInfo();
//                    //시작태그의 이름을 알아냄
//                    if(parser.getName().contains(initial_TagName)) {
//                        String name = parser.getName();
//                        String value = parser.nextText();
//                        mContent.put(name,value);
//
//                        tagName = parser.getName();
//                        mTemp.add(name);
//                        mTemp.add(value);
//                        mTemp.add("/");
//                        //parserEvent = parser.next();
//
//                        //for (int i = 0; i < tagIndex.length; i++) { // 하나의 item이 끝날 때, 들어감.
//
//                        //}
//                    }
//                }
//                parserEvent = parser.next();
//
//            }
//        }catch(MalformedURLException e){
//            e.printStackTrace();
//        }catch(XmlPullParserException e){
//            e.printStackTrace();
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//        return mContent;
//    }

    int isAmorPm(String data)
    {
        if(data.contains("am"))
            return MiddleInfo.AM;
        else if(data.contains("pm"))
            return MiddleInfo.PM;
        else
            return MiddleInfo.NOTHING;


    }

    public void ContentToMiddleArrayList(ContentValues mWeather, ContentValues mTemp)
    {
        String keyName = "ta";
        String maxString = "Max";
        String minString = "Min";
        for(int i = 0; i < MiddleInfo.checkWeatherData.length; i++)
        {
            String weatherInfo = ""; String time= ""; String max = ""; String min = "";
            weatherInfo = String.valueOf(mWeather.get(MiddleInfo.checkWeatherData[i]));
            int checkTime = isAmorPm(MiddleInfo.checkWeatherData[i]);
            switch(checkTime)
            {
                case MiddleInfo.AM:
                    time = "Am";
                    break;
                case MiddleInfo.PM:
                    time = "Pm";
                    break;
                default:
                    break;
            }


            max = String.valueOf(mTemp.get(keyName + maxString + MiddleInfo.AfterDay(MiddleInfo.checkWeatherData[i])));
            min = String.valueOf(mTemp.get(keyName + minString +  MiddleInfo.AfterDay(MiddleInfo.checkWeatherData[i])));
            mDayWeather.add(new WeatherToDay(time,
                    DateAdd(MiddleInfo.AfterDay(MiddleInfo.checkWeatherData[i])),
                    weatherInfo,max,min));
        }



    }

    @Override
    public void run() {
        super.run( );
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mDayWeather = new ArrayList<>();
        middleWeather = makeMiddleLandURL(inputLocation);
        middleTemparature = makeMiddleTempURL(inputLocation);
        ContentValues mVal=  RequestURL_MiddleInfoWeather(middleWeather,"wf");
        ContentValues mVal2 = RequestURL_MiddleInfoWeather(middleTemparature, "ta");


        ContentToMiddleArrayList(mVal,mVal2);
        this.mHandler = mHandler;
//        Message msg =


//        for(int i = 1; ;i++)
//            if(RequestURLMiddle(i) == 0)  break;
//Message.obtain() , Handler.obtainMessage
        Message msg= Message.obtain();
        msg.what = HandlerMessage.THREAD_HANDLER_MIDDLELAND_SUCCESS_INFO;
        //msg.obj = mResult;
        this.mHandler.handler.sendMessage(msg);
        //Thread 작업 종료, UI 작업을 위해 MainHandler에 Message보냄    }
    }



}
