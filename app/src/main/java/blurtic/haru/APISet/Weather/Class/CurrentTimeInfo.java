package blurtic.haru.APISet.Weather.Class;

/**
 * Created by Kim W on 2016-06-13.
 */
public class CurrentTimeInfo {
    String for_token[] = {
            "baseDate",
            "baseTime",
            "category",
            "fcstDate",
            "fcstTime",
            "fcstValue",
            "nx",
            "ny"
    };


    String station_Time[] = {
            "00:00","01:00","02:00","03:00","04:00","05:00",
            "06:00","07:00","08:00","09:00","10:00",
            "11:00","12:00","13:00","15:00","16:00",
            "17:00","18:00","19:00","20:00","21:00",
            "22:00","23:00"};
    //http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib?ServiceKey=
    // msBaq3etrDaXttTCXKAC9yeCoS%2Fn3%2BAARQ3J1727dBNDcGwzhs0Twu%2BDn1PDeKCu8iZtKJ9Mib9w3bXqXSvK2Q%3D%3D&base_date=20160613&base_time=0800&nx=55&ny=127&numOfRows=300
    // 해당 지역의 정확한 날씨 정보,
    // 초 단기실황

    String apiSupportTime[] = {
            "00:30","01:30","02:30","03:30","04:30","05:30",
            "06:30","07:30","08:30","09:30","10:30",
            "11:30","12:30","13:30","15:30","16:30",
            "17:30","18:30","19:30","20:30","21:30",
            "22:30","23:30"};
    //API를 부여 받을 수 있는 시간 getForecastTimeData
    //초 단기 예보조회 + 3시간 조회가능
    //http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastTimeData?ServiceKey=
    // msBaq3etrDaXttTCXKAC9yeCoS%2Fn3%2BAARQ3J1727dBNDcGwzhs0Twu%2BDn1PDeKCu8iZtKJ9Mib9w3bXqXSvK2Q%3D%3D&base_date=20160613&base_time=1730&nx=55&ny=127&numOfRows=100

    // getForecastSpaceDataRequest 를 위한 base Tiem
    String base_Time[] = {"0200","0500","0800","1100","1400",
    "1700","2000","2300"};
    //API값이 초기화 되는 시간
    //http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?ServiceKey
    // =msBaq3etrDaXttTCXKAC9yeCoS%2Fn3%2BAARQ3J1727dBNDcGwzhs0Twu%2BDn1PDeKCu8iZtKJ9Mib9w3bXqXSvK2Q%3D%3D&base_date=20160613&base_time=1700&nx=55&ny=127&numOfRows=300
    // 3시간 주기, 날씨 체크 baseTime은 초기화가 되는 시간, , 00시 부터 발표난 시간까지의 정보

    /*
        DateTimeMapping()
        동네예보 조회를 위해 BaseTime 매핑
      */

    public String DateTimeMapping(String hour)
    {
        int it_hour = Integer.valueOf(hour);
        for(int i = base_Time.length ; i >= 0; i--)
        {
            int data = Integer.valueOf(base_Time[i]);
            if(it_hour > data) return String.valueOf(data);

        }
        return "";
    }

    String SkyMake(String rawData){
        String sky = "SKY";
        String skyCodeString[] = {"맑음","구름조금","구름많음","흐림"};
        int skyCode[] = {1,2,3,4};

        return "";
    }

    String typeMake(String rawData)
    {
        String pty = "PTY";
        String ptyCodeString[] = {"없음","비","진눈개비","눈"};
        int ptyCode[] = {0,1,2,3};

        return "";
    }

    String RainWeightMake(String rawData)
    {
        String rain6 = "R06"; // 6시간 단위 강수량
        String rain1 = "RN1"; // 1시간 단위 강수량
        String snow06 = "S06"; //눈
        String rainWeightCodeString[] = {"0","1","5","10","20","40","70","100"};
        int rainWeightCode[] = {0,1,5,10,20,40,70,100};
        String snowWeightCodeString[] = {"0","1","5","10","20","100"};
        int snowWeightCode[] = {0,1,5,10,20,100};


        return "";
    }

    String LightMake(String rawData)
    {
        int lightCode[] = {0,1,2,3}; // 초 단기예보만 가능
        String lighCodeString[] = {"확률없음","낮음","보통","높음"};

        return "";
    }

    String WindMake(String rawData)
    {
        String wind[] = {"UUU","VVV","WSD"}; //uuu + 동, - 서
        String windResult[] ={"동","서","북","남"};
        //vvv + 북, - 남 //VEC 풍향
        //WSD 풍속

        return "";
    }


    String TempMake(String rawData)
    {
        String temparature[] = {"T3H","TMN","TMX"};
        String tempResult[] = {"3시간 기온", "일 최저기온", "일 최고기온"};


        // 3시간 온도, 최고온도 최저온도

        return "";
    }

    String HumidityMake(String rawData)
    {
        String humi = "REH"; // 습도 확률
        String humiResult = "습도";


        return "";
    }


    String RainPercentMake(String rawData)
    {
        String rainPercent = "POP"; // 강수 확률
        String rainResult = "강수확률";
        return "";
    }


    String LocationXYReturn(double lat,double lon)
    {
        return "";
    }

}
