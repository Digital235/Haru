package blurtic.haru.APISet.Weather.Class;

/**
 * Created by Kim W on 2016-06-13.
 */
public class WeatherToTime {
    public String date = "";
    public String type = "";
    public String rain6_Percent = "";
    public String rain1_Percent = "";
    public String rain_Weight = "";

    public String snow6_Percent = "";
    public String lightPercent ="";
    public  String wind_Direction = "";
    public String temp3_Current = "";
    public String temp_Min = "";
    public String temp_Max = "";

    public String humi = "";
    public String skyType = "";
    String categoryValue[] = {"POP","PTY","R06","REH","SO6","SKY","T3H","TMN","TMX","UUU","VVV","WAV","VEC","WSD"};

    //각 코드에 매핑
    // 한글로 변환은 currentTimeInfo에 기술
    public void HashValue(String category,String val)
    {
        switch(category)
        {
            case "POP":

                break;
            case "PTY":

                break;
            case "R06":

                break;
            case "REH":

                break;
            case "SO6":

                break;
            case "SKY":

                break;
            case "TMN":

                break;
            case "TMX":

                break;
            case "UUU":

                break;
            case "VVV":

                break;
            case "WAV":

                break;
            case "VEC":

                break;
            case "WSD":

                break;
        }


    }





    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRain6_Percent(String rain6_Percent) {
        this.rain6_Percent = rain6_Percent;
    }

    public void setRain1_Percent(String rain1_Percent) {
        this.rain1_Percent = rain1_Percent;
    }

    public void setRain_Weight(String rain_Weight) {
        this.rain_Weight = rain_Weight;
    }

    public void setSnow6_Percent(String snow6_Percent) {
        this.snow6_Percent = snow6_Percent;
    }

    public void setLightPercent(String lightPercent) {
        this.lightPercent = lightPercent;
    }

    public void setWind_Direction(String wind_Direction) {
        this.wind_Direction = wind_Direction;
    }

    public void setTemp3_Current(String temp3_Current) {
        this.temp3_Current = temp3_Current;
    }

    public void setTemp_Min(String temp_Min) {
        this.temp_Min = temp_Min;
    }

    public void setTemp_Max(String temp_Max) {
        this.temp_Max = temp_Max;
    }

    public void setHumi(String humi) {
        this.humi = humi;
    }

    public void setSkyType(String skyType) {
        this.skyType = skyType;
    }
}
