package blurtic.haru;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DCB on 2016-10-14.
 */
public class DiaryItem {
    private String content="";

    DiaryItem(){}
    DiaryItem(String c){
        content=c;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String c) {
        content = c;
    }

    static public JSONObject toJson(DiaryItem item) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", item.getContent());
            return jsonObject;
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
    }

    static public DiaryItem fromJson(JSONObject jsonObject){
        try {
            String content = jsonObject.getString("content");
            return new DiaryItem(content);
        }
        catch(JSONException e){
            return null;
        }
    }

    static public DiaryItem fromJson(String jsonString){
        try {
            DiaryItem diaryItem = fromJson(new JSONObject(jsonString));
            return diaryItem;
        }
        catch(JSONException e){
            return null;
        }
    }
}
