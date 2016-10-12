package blurtic.haru;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DCB on 2016-10-11.
 */
public class PlanListJsonParser {
    static public JSONArray toJson(ArrayList<PlanItem> planList){
        JSONArray jsonArray = new JSONArray();

        for(int i=0; i<planList.size(); i++){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("hour", planList.get(i).getHour());
                jsonObject.put("minute", planList.get(i).getMinute());
                jsonObject.put("completeHour", planList.get(i).getCompleteHour());
                jsonObject.put("completeMinute", planList.get(i).getCompleteMinute());
                jsonObject.put("name", planList.get(i).getName());
                jsonArray.put(jsonObject);
            }
            catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return jsonArray;
    }
    static public ArrayList<PlanItem> fromJson(JSONArray jsonArray){
        ArrayList<PlanItem> planList = new ArrayList<PlanItem>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int hour=obj.getInt("hour");
                int minute=obj.getInt("minute");
                int completeHour=obj.getInt("completeHour");
                int completeMinute=obj.getInt("completeMinute");
                String name=obj.getString("name");
                planList.add(new PlanItem(hour,minute,completeHour,completeMinute,name));
            }
        }
        catch(JSONException e){

        }
        return planList;
    }
    static public ArrayList<PlanItem> fromJson(String jsonString){
        ArrayList<PlanItem> planList=null;
        try {
            planList = fromJson(new JSONArray(jsonString));
        }
        catch(JSONException e){

        }
        return planList;
    }
}
