package blurtic.haru;

import android.util.Log;

import org.json.JSONArray;

/**
 * Created by DCB on 2016-10-18.
 */
public class LikeJsonParser {//1xxx=영화, 2xxx=음악, 3xxx=?
    public static JSONArray toJson(){
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<MovieDatabase.items.length; i++){
            if(MovieDatabase.items[i].favorite) {
                jsonArray.put(1000 + i);
            }
        }
        for(int i=0; i<MusicDatabase.items.length; i++){
            if(MusicDatabase.items[i].favorite)
                jsonArray.put(2000+i);
        }
        for(int i=0; i<FoodDatabase.items.length; i++){
            if(FoodDatabase.items[i].favorite)
                jsonArray.put(3000+i);
        }
        return jsonArray;
    }
    public static void fromJson(JSONArray jsonArray){
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                int index = jsonArray.getInt(i);
                if(index >= 1000 && index < 2000){
                    MovieDatabase.items[index-1000].favorite=true;
                }
                else if(index >= 2000 && index < 3000){
                    MusicDatabase.items[index-2000].favorite=true;
                }
                else if(index >= 3000 && index < 4000){
                    FoodDatabase.items[index-3000].favorite=true;
                }
            }
        }
        catch(Exception e){
            Log.e("test",e.getMessage());
        }
    }

    public static void fromJson(String jsonString){
        try {
            fromJson(new JSONArray(jsonString));
        }
        catch(Exception e){

        }
    }
}
