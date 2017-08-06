package blurtic.haru.APISet.Weather.Class;

import java.util.ArrayList;

/**
 * Created by Kim W on 2016-06-13.
 */
public class WeatherData {
    public ArrayList<WeatherToDay> mDayWeather;
    public ArrayList<WeatherToTime> mTimeWeather;

    public WeatherData(ArrayList<WeatherToDay> mDayWeather, ArrayList<WeatherToTime> mTimeWeather) {
        this.mDayWeather = mDayWeather;
        this.mTimeWeather = mTimeWeather;
    }



}
