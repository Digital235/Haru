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

import blurtic.haru.APISet.Weather.Class.CurrentTimeInfo;
import blurtic.haru.APISet.Weather.Class.LocationInput;
import blurtic.haru.APISet.Weather.Class.MiddleInfo;
import blurtic.haru.APISet.Weather.Class.SpaceTimeCategory;
import blurtic.haru.APISet.Weather.Class.WeatherData;
import blurtic.haru.APISet.Weather.Class.WeatherToDay;
import blurtic.haru.APISet.Weather.Class.WeatherToTime;
import blurtic.haru.APISet.Weather.Message.HandlerMessage;
import blurtic.haru.MainActivity;

/**
 * Created by Kim W on 2016-06-13.
 */

// WindMake(String rawData)
// LightMake
// typeMake
// SkyMake
// 중기 예보를 먼저 받고, 그다음 단기 예보를 받아야함

    /*
        16-07-30
           현재 날씨 데이터 중 Location에 대한 정보는 생략
           고정 Location으로 중기 날씨, 중기 온도 받는 부분 구현
           (현재 기준 3일 후 ~10일 후 일자별 날씨 데이터 받음)
           ex) 0101 -> 0104 날씨 출력

           시간별 날씨를 통해 3일 빈 날씨를 출력해야함
           현재 위치 정보는 고정으로 쓰며,
           날씨별 데이터 구성을 하고 있으며 Return값으로 mTotalWeather로 받을 수 있음
           Main에서 Handler를 통해서 받을 수 있습니다.

     */
public class URLConnectionManager extends Thread {


    final static int YEAR = 0;
    final static int MONTH = 1;
    final static int DAY = 2;
    final static int HOUR = 3;
    final static int MIN = 4;
    final static String TAG = "URLConnectionManager";
    MainActivity mHandler;
    String inputLocation;
    WeatherData mTotalWeather;
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
    String spaceData;

    /*
       MiddleBaseTime
       날씨 검색이 되는 시간 매핑
     */
    String MiddleBaseTime(String hour) {

        if (Integer.valueOf(hour) > 18) return "1800";
        if (Integer.valueOf(hour) > 6) return "0600";

        return "0000";
    }

    String SpaceBaseTime(String hour) {
        CurrentTimeInfo mapping = new CurrentTimeInfo();
        String base_time = mapping.DateTimeMapping(hour);
        if (base_time == "") return "";

        return base_time;
    }

    String StringToCategory(String category) {


        return "";
    }

    public String makeSpaceDataURL(String inputLocation) {
//        LocationInput mLocation = new LocationInput(mContext);
//        String regId = mLocation.makeRegId(inputLocation);
//        int val =  Integer.valueOf(mCurrentTimeAndDay.get(DAY)) - 1;
//        String currentDay = mCurrentTimeAndDay.get(YEAR) + mCurrentTimeAndDay.get(MONTH) + val;
//        String time = SpaceBaseTime(mCurrentTimeAndDay.get(HOUR));
//        String nx = mLocation.ChangeLonToX();
//        String ny = mLocation.ChangeLatToY();


        Calendar calendar = Calendar.getInstance();


        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        if (hour < 2) {
            hour = 20;
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        else if (hour < 5) {
            hour = 23;
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        else if (hour < 8)hour = 2;
        else if (hour < 11)hour = 5;
        else if (hour < 14)hour = 8;
        else if (hour < 17)hour = 11;
        else if (hour < 20)hour = 14;
        else if (hour < 23)hour = 17;

        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        String currentDay = dateFormat.format(today);
        String baseTime;
        if (hour < 10) baseTime = "0" + hour + "00";
        else baseTime = "" + hour + "00";


        String resultUrl = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?ServiceKey" +
                "=msBaq3etrDaXttTCXKAC9yeCoS%2Fn3%2BAARQ3J1727dBNDcGwzhs0Twu%2BDn1PDeKCu8iZtKJ9Mib9w3bXqXSvK2Q%3D%3D&base_date=" + currentDay + "&base_time=" + baseTime + "&nx=55&ny=127&numOfRows=300";
        return resultUrl;
    }


    public URLConnectionManager(Context mContext, String inputLocation, MainActivity mData) {
        this.mContext = mContext;
        mCurrentTimeAndDay = MakeCurrentDayAndTime();
        this.inputLocation = inputLocation;
        this.mHandler = mData;
    }

    /*
        makeMiddleLandURL
        중기 예보 URL Make

    */
    public String makeMiddleLandURL(String inputLocation) {
        LocationInput mLocation = new LocationInput(mContext);

        String regId = mLocation.makeRegId(inputLocation);
        String currentDay = mCurrentTimeAndDay.get(YEAR) + mCurrentTimeAndDay.get(MONTH) + mCurrentTimeAndDay.get(DAY);
        String time = MiddleBaseTime(mCurrentTimeAndDay.get(HOUR));

        String resultUrl = "http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather?ServiceKey=" + st_key +
                "&regId=" + regId + "&tmFc=" + currentDay + time + "&numOfRows=1&pageNo=1";
        return resultUrl;
    }


    /*
        makeMiddleTempURL
        중기 온도 URL Make
     */

    public String makeMiddleTempURL(String inputLocation) {
        String regId = "11H10701";
        String currentDay = mCurrentTimeAndDay.get(YEAR) + mCurrentTimeAndDay.get(MONTH) + mCurrentTimeAndDay.get(DAY);
        String time = MiddleBaseTime(mCurrentTimeAndDay.get(HOUR));

        String resultUrl = "http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleTemperature?ServiceKey=" + st_key +
                "&regId=" + regId + "&tmFc=" + currentDay + time + "&pageNo=1&numOfRows=1";


        return resultUrl;
    }

    /*
        MakeCurrentDayAndTime
        현재 시간 측정
        토크나이저로 . 제거 년도(0), 월(1), 일(2), 시간(3), 분(4)
   */
    public ArrayList<String> MakeCurrentDayAndTime() {
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
//SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String time = currentTime.format(new Date(System.currentTimeMillis()));

        StringTokenizer mToken = new StringTokenizer(time, ".");
        ArrayList<String> mTemp = new ArrayList<>();
        while (mToken.hasMoreTokens()) {
            mTemp.add(mToken.nextToken());
        }


        Log.i(TAG, "time print: " + time);
        for (int i = 0; i < mTemp.size(); i++)
            Log.i(TAG, "time print: " + mTemp.get(i));

        return mTemp;
    }

    public String DateAdd(int afterDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, afterDay);

        // 특정 형태의 날짜로 값을 뽑기
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String strDate = df.format(cal.getTime());
        Log.i(TAG, "add Date: " + strDate);
        return strDate;
    }

    /*
        RequestURLTodayWeather()


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
    */
    /*
        RequestURL_MiddleInfoWeather()
        중기 온도

    */
    ContentValues RequestURL_MiddleInfoWeather(String urlData, String initial_TagName) {
        int idx = 0;
        int tagIndex[] = {10, 3, 4, 5, 6, 7, 8, 9};

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
                    if (parser.getName().contains(initial_TagName)) {
                        String name = parser.getName();
                        String value = parser.nextText();
                        mContent.put(name, value);
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mContent;
    }


    /*
        TimeData 만듬
        BaseTime 따로 저장. // 카테고리에 따라 fcstValue의 값을 토대로 측정
        - baseDate
        - basetime -> 현재 시간 보다 작으면 저장 X
        - category -> POP / PTY / REH /
        - fcstDate ->
        - fcstTime
        - fcstValue
        x
        y
     */
    ArrayList<SpaceTimeCategory> RequestURL_CurrentTimeInfo(String urlData) {

        CurrentTimeInfo mInfo = new CurrentTimeInfo();
        ArrayList<String> mTemp = new ArrayList<>();
        ArrayList<SpaceTimeCategory> mSpaceTime = new ArrayList<>();
        ContentValues mContent = new ContentValues();
        //BaseTime -> CurrentTimeInfo 참고
        String category[] = {"category", "fcstDate", "fcstTime", "fcstValue"};
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
                Log.i(TAG, "parser : getName() = " + parser.getName());
                if (parserEvent == XmlPullParser.START_TAG && parser.getName().equals("item")) {
                    //시작태그의 이름을 알아냄

                    SpaceTimeCategory mVal = new SpaceTimeCategory();
                    for (; ; ) {

                        for (int i = 0; i < category.length; i++) {
                            String parser_test = parser.getName();
                            if (parser_test == null) {
                                i = 0;
                                parserEvent = parser.next();
                                parser_test = parser.getName();
                            }
                            if (parser_test.equals(category[i])) { // Parser 네임
                                String name = parser.getName();
                                String value = parser.nextText();
                                mVal.InsideValue(name, value);
                                tagName = parser.getName();
                                mTemp.add(name);
                                mTemp.add(value);
                                mTemp.add("/");
                                // parserEvent = parser.next();

                            }
                        }

                        parserEvent = parser.next();
                        if (parser.getName() == null) continue;

                        if (parser.getName().equals("item") && parserEvent == XmlPullParser.END_TAG)
                            break;
                    }
                    mSpaceTime.add(mVal);
                }
                parserEvent = parser.next();

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mSpaceTime;
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

    int isAmorPm(String data) {
        if (data.contains("Am"))
            return MiddleInfo.AM;
        else if (data.contains("Pm"))
            return MiddleInfo.PM;
        else
            return MiddleInfo.NOTHING;


    }

    /*

        fcstDate를 통해서, +0 , +1 , +2 , +3 을 통해 값을 찾아내야할듯
        // category 통해서 fcstValue값을 배정
        //시간 / 온도만 뽑아내면됨
     */

    public ArrayList<WeatherToTime> ContentToTimeArrayList(ArrayList<SpaceTimeCategory> mTime) {
        ArrayList<WeatherToTime> time_data = new ArrayList<>();

//        String year = mCurrentTimeAndDay.get(YEAR);
//        String month = mCurrentTimeAndDay.get(MONTH);
//        String day = mCurrentTimeAndDay.get(DAY);
//        int integer_day = Integer.valueOf(day);
//
//        String cur_date = mCurrentTimeAndDay.get(YEAR) + mCurrentTimeAndDay.get(MONTH) +
//                (mCurrentTimeAndDay.get(DAY));
//        String st_date1 = year+month+day; // + 0 일
//        String st_date2 = year+month+(integer_day+1);
//        String st_date3 = year+month+(integer_day+2);
//        String st_date4 = year+month+(integer_day+3);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date afterDay1 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date afterDay2 = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date afterDay3 = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        String todayAsString = dateFormat.format(today);
        String afterDay1AsString = dateFormat.format(afterDay1);
        String afterDay2AsString = dateFormat.format(afterDay2);
        String afterDay3AsString = dateFormat.format(afterDay3);

        //String test_data = "20161014";
        for (int i = 0; i < mTime.size(); i++) {
            //내부적 루프 돌아서 다넣는 식,

            WeatherToTime mTemp = new WeatherToTime(mTime.get(i).fcstDate, mTime.get(i).fcstTime, "", "");
            if (todayAsString.equals(mTime.get(i).fcstDate))
            //if(test_data.equals(mTime.get(i).fcstDate))
            {
                for (int j = i; j < mTime.size(); i++, j++) {
                    if (mTemp.getTime().equals(mTime.get(j).fcstTime)) {
                        mTemp.HashValue(mTime.get(j).category, mTime.get(j).fcstValue);
                    }
                    if (mTemp.isFullAllData()) break;
                }
                mTemp.ChangeMapping();
                time_data.add(mTemp);
            } else if (afterDay1AsString.equals(mTime.get(i).fcstDate)) {
                for (int j = i; j < mTime.size(); i++, j++) {
                    if (mTemp.getTime().equals(mTime.get(j).fcstTime)) {
                        mTemp.HashValue(mTime.get(j).category, mTime.get(j).fcstValue);
                    }
                    if (mTemp.isFullAllData()) break;
                }
                mTemp.ChangeMapping();
                time_data.add(mTemp);


            } else if (afterDay2AsString.equals(mTime.get(i).fcstDate)) {
                for (int j = i; j < mTime.size(); i++, j++) {
                    if (mTemp.getTime().equals(mTime.get(j).fcstTime)) {
                        mTemp.HashValue(mTime.get(j).category, mTime.get(j).fcstValue);
                    }
                    if (mTemp.isFullAllData()) break;
                }
                mTemp.ChangeMapping();
                time_data.add(mTemp);
            } else if (afterDay3AsString.equals(mTime.get(i).fcstDate)) {
                for (int j = i; j < mTime.size(); i++, j++) {
                    if (mTemp.getTime().equals(mTime.get(j).fcstTime)) {
                        mTemp.HashValue(mTime.get(j).category, mTime.get(j).fcstValue);
                    }
                    if (mTemp.isFullAllData()) break;
                }
                mTemp.ChangeMapping();
                time_data.add(mTemp);
            }


        }


        //한 날씨에 대한 정보가 아닌,
        return time_data;
    }

    public void ContentToMiddleArrayList(ContentValues mWeather, ContentValues mTemp) {
        String keyName = "ta";
        String maxString = "Max";
        String minString = "Min";
        for (int i = 0, j = 0; i < MiddleInfo.checkWeatherData.length; i++) {
            String weatherInfo = "";
            String time = "";
            String max = "";
            String min = "";

            weatherInfo = String.valueOf(mWeather.get(MiddleInfo.checkWeatherData[i]));
            int checkTime = isAmorPm(MiddleInfo.checkWeatherData[i]);
            switch (checkTime) {
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
            min = String.valueOf(mTemp.get(keyName + minString + MiddleInfo.AfterDay(MiddleInfo.checkWeatherData[i])));

            Log.i(TAG, "time : " + time);
            mDayWeather.add(new WeatherToDay(time,
                    DateAdd(MiddleInfo.AfterDay(MiddleInfo.checkWeatherData[i])),
                    weatherInfo, max, min));
        }


    }
    // MakeMiddleLandURL -> String url
    // MakeMiddleTempURL -> String url

    //
    //
    //
   /*
      중기 데이터, 쪼개기 완료, 날짜와 이전 데이터 저장이 필요한데 이전은 일단 제외
      단기 데이터 시간에 따른 데이터 받기 필요

    */
    @Override
    public void run() {
        super.run();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
         /*
            중기 데이터 변환 완료
         */
        mDayWeather = new ArrayList<>();
        middleWeather = makeMiddleLandURL(inputLocation);
        middleTemparature = makeMiddleTempURL(inputLocation);
        ContentValues mVal = RequestURL_MiddleInfoWeather(middleWeather, "wf");
        ContentValues mVal2 = RequestURL_MiddleInfoWeather(middleTemparature, "ta");

        /*
            단기 데이터 변환

         */

        mTimeWeather = new ArrayList<>();
        spaceData = makeSpaceDataURL(inputLocation);
        ArrayList<SpaceTimeCategory> mTime = RequestURL_CurrentTimeInfo(spaceData);
        mTimeWeather = ContentToTimeArrayList(mTime);

        /*
            Total Data에 단기, 중기 데이터 넣음

         */
        ContentToMiddleArrayList(mVal, mVal2);
        mTotalWeather = new WeatherData(mDayWeather, mTimeWeather);
        //this.mHandler = mHandler;
//        Message msg =l


//        for(int i = 1; ;i++)
//            if(RequestURLMiddle(i) == 0)  break;
//Message.obtain() , Handler.obtainMessage
        Message msg = Message.obtain();
        msg.what = HandlerMessage.THREAD_HANDLER_MIDDLELAND_SUCCESS_INFO;
        msg.obj = mTotalWeather;
        this.mHandler.handler.sendMessage(msg);
        //Thread 작업 종료, UI 작업을 위해 MainHandler에 Message보냄    }
    }


}