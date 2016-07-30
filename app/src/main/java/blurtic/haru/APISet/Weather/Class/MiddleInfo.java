package blurtic.haru.APISet.Weather.Class;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Kim W on 2016-06-13.
 */
public class MiddleInfo {

    ArrayList<MiddleTemp> mTempList;
    ArrayList<MiddleLand> mMiddleList;

    public static final int AM = 1;
    public static final int PM = 2;
    public static final int NOTHING = 3;

    public MiddleInfo() {
        mMiddleList = new ArrayList<>();
        mTempList = new ArrayList<>();
        InitialMiddleLandRegId();
    }

    public static final String checkWeatherData[] = {
            "wf3Am","wf3Pm","wf4Am",
            "wf4Pm","wf5Am","wf5Pm",
            "wf6Am","wf6Pm","wf7Am",
            "wf7Pm","wf8","wf9",
            "wf10"}; // 12 ( 0 ~ 12) 13
    public static final String checkTempMaxData[] = {
           "taMax3","taMax4","taMax5",
            "taMax6","taMax7","taMax8",
            "taMax9", "taMax10"
    };
    public static final String checkTempMinData[] = {
            "taMin3","taMin4","taMin5",
            "taMin6","taMin7","taMin8",
            "taMin9","taMin10"
    };

    String baseTimeList[] = {"06:00","18:00"};
    // 이 시간 기준으로 데이터가 업데이트 됨
    static final String TAG = "MiddleInfo";

    public static int AfterDay(String data)
    {
        String mData = "";
        mData = data.replaceAll("[^0-9]", "");

        Log.i(TAG, TAG + " : " + mData);
        return Integer.valueOf(mData);
    }
   // http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather?ServiceKey=
   // &regId=11B00000&tmFc=201606140600&numOfRows=1&pageNo=1

    public void InitialMiddleTempRegId()
    {
//        11B10101	서울	11G00401	서귀포
//        11B20201	인천	11F20501	광주
//        11B20601	수원	21F20801	목포
//        11B20305	파주	11F20401	여수
//        11D10301	춘천	11F10201	전주
//        11D10401	원주	21F10501	군산
//        11D20501	강릉	11H20201	부산
//        11C20401	대전	11H20101	울산
//        11C20101	서산	11H20301	창원
//        11C20404	세종	11H10701	대구
//        11C10301	청주	11H10501	안동
//        11G00201	제주	11H10201	포항

        ArrayList<String> seoul = new ArrayList<>();
        ArrayList<String> daegue = new ArrayList<>();
        ArrayList<String> daejeon = new ArrayList<>();
        ArrayList<String> busan = new ArrayList<>();
        ArrayList<String> gwangju = new ArrayList<>();
        ArrayList<String> chooncheon = new ArrayList<>();
        ArrayList<String> jeju = new ArrayList<>();

    }
    public void InitialMiddleLandRegId()
    {
        ArrayList<String> soodoGuan = new ArrayList<>();
        ArrayList<String> gangwonEast = new ArrayList<>();
        ArrayList<String> gangwonWest = new ArrayList<>();
        ArrayList<String> chungNam = new ArrayList<>();
        ArrayList<String> chungBook = new ArrayList<>();
        ArrayList<String> gwangJooAround = new ArrayList<>();
        ArrayList<String> jeonraBookDo = new ArrayList<>();
        ArrayList<String> daegueAround = new ArrayList<>();
        ArrayList<String> busanAround = new ArrayList<>();
        ArrayList<String> jeju = new ArrayList<>();

        soodoGuan.add("서울특별시"); soodoGuan.add("인천광역시"); soodoGuan.add("경기도");


        chungNam.add("대전광역시"); chungNam.add("세종특별자치시"); chungNam.add("충청남도");
        chungBook.add("충청북도");
        gwangJooAround.add("광주광역시"); gwangJooAround.add("전라남도");
        jeonraBookDo.add("전라북도");
        daegueAround.add("대구광역시"); daegueAround.add("경상북도");
        busanAround.add("부산광역시"); busanAround.add("울산광역시"); busanAround.add("경상남도");
        jeju.add("제주특별자치도");
        mMiddleList.add(new MiddleLand("11B00000",soodoGuan));
        //mMiddleList.add(new MiddleLand("11D10000",soodoGuan));
        //mMiddleList.add(new MiddleLand("11D20000",soodoGuan));
        mMiddleList.add(new MiddleLand("11C20000",chungNam));
        mMiddleList.add(new MiddleLand("11C10000",chungBook));
        mMiddleList.add(new MiddleLand("11F20000",gwangJooAround));
        mMiddleList.add(new MiddleLand("11F10000",jeonraBookDo));
        mMiddleList.add(new MiddleLand("11H10000",daegueAround));
        mMiddleList.add(new MiddleLand("11H20000",busanAround));
        mMiddleList.add(new MiddleLand("11G0000",jeju));
        //mTempList.add(new MiddleTemp());

//        11B00000	서울, 인천,경기도 // 인천광역시 서울특별시 경기도
//        11D10000	강원도영서 //
//        11D20000	강원도영동
//        11C20000	대전, 세종, 충청남도 // 대전광역시, 세종특별자치시 , 충청남도
//        11C10000	충청북도
//        11F20000	광주, 전라남도 // 광주광역시, 전라남도
//        11F10000	전라북도
//        11H10000	대구, 경상북도 // 대구광역시
//        11H20000	부산, 울산, 경상남도 // 울산광역시 부산광역시 경상남도
//        11G0000	제주도 제주특별자치도


    }

    class MiddleTemp{
        String code;
        ArrayList<String> name;

        public MiddleTemp(String code, ArrayList<String> name) {
            this.code = code;
            this.name = name;
        }
        public String CheckRegion(String region)
        {
            if(name.size() == 0) return "";

            for(int i = 0 ; i < name.size(); i++)
                if(name.get(i).equals(region)) return this.code;

            return "";
        }
    }

    class MiddleLand{
        String code;
        ArrayList<String> name;

        public MiddleLand(String code,ArrayList<String> name) {
            this.code = code;
            this.name = name;
        }

        public String CheckRegion(String region)
        {
            if(name.size() == 0) return "";

            for(int i = 0 ; i < name.size(); i++)
                if(name.get(i).equals(region)) return this.code;

            return "";
        }
    }


}
