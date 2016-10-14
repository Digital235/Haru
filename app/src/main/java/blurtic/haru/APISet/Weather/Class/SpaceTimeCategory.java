package blurtic.haru.APISet.Weather.Class;

/**
 * Created by Kim W on 2016-08-06.
 */
public class SpaceTimeCategory {
    public String key_category;
    public String key_fcstDate;
    public String key_fcstTime;
    public String key_fcstValue;

    public String category;
    public String fcstDate;
    public String fcstTime;
    public String fcstValue;
    public String nx;
    public String ny;

    public SpaceTimeCategory() {
        this.category = "";
        this.fcstDate = "";
        this.fcstTime = "";
        this.fcstValue = "";
        this.nx = nx;
        this.ny = ny;
    }


    // 카테고리 text와 value 섞음
    public void InsideValue(String name,String val)
    {
        switch(name)
        {
            case "category":
                this.category = val;
                break;
            case "fcstDate":
                this.fcstDate = val;
                break;
            case "fcstTime":
                this.fcstTime = val;
                break;
            case "fcstValue":
                this.fcstValue = val;
                break;

        }


    }

    boolean IsValueFull()
    {
        if(this.category.equals("") || this.fcstDate.equals("")|| this.fcstTime.equals("")|| this.fcstValue.equals(""))
            return false;

        return true;
    }


}
