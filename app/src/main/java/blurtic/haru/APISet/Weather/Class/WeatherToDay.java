package blurtic.haru.APISet.Weather.Class;

/**
 * Created by Kim W on 2016-06-13.
 */
public class WeatherToDay {
    String time; // am , pm 구분 , NULL 도 잇음
    String day;
    String weather;
    String maxTemp;
    String minTemp;

    public WeatherToDay(String time, String day, String weather, String maxTemp, String minTemp) {
        this.time = time;
        this.day = day;
        this.weather = weather;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }
}