package blurtic.haru.APISet.Weather.Class;

/**
 * Created by Kim W on 2016-06-13.
 */
public class WeatherToTime {
    String date = "";
    String type = "";
    String rain6_Percent = "";
    String rain1_Percent = "";
    String rain_Weight = "";

    String snow6_Percent = "";
    String lightPercent ="";
    String wind_Direction = "";
    String temp3_Current = "";
    String temp_Min = "";
    String temp_Max = "";

    String humi = "";
    String skyType = "";

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
