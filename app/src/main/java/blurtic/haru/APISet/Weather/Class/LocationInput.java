package blurtic.haru.APISet.Weather.Class;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Kim W on 2016-06-15.
 */
public class LocationInput {
    Context mContext;
    double lat;
    double lon;
    String address;
    static final String TAG = "LocationInput";
    public LocationInput(Context mContext) {
        this.mContext = mContext;
    }

    public String makeRegId(String inputLocation)
    {
        String location = chnageLocationToMiddleLandRegId(inputLocation);
        MiddleInfo mInfo = new MiddleInfo();
        String result = "";
        for(int i = 0; i < mInfo.mMiddleList.size(); i++)
        {
            result = mInfo.mMiddleList.get(i).CheckRegion(location);
            if(!result.equals("")) return result;

        }

        Log.i(TAG,"MakeRegId Failed");
        return "";
    }


    String chnageLocationToMiddleLandRegId(String location) // 도.시,군 단위 입력
    {
        Geocoder mGeocoder = new Geocoder(mContext);
        List<Address> mListAddress;
        Address mAddress;
        String result = "";
        try{
            mListAddress = mGeocoder.getFromLocationName(location, 5);
            if(mListAddress.size() > 0)
            {
                mAddress = mListAddress.get(0); // 0 번째 주소값,
                lat = mAddress.getLatitude();
                lon = mAddress.getLongitude();
                address = mAddress.getAddressLine(0);


                result = mAddress.getAddressLine(0);
                StringTokenizer mToken = new StringTokenizer(result, " ");
                mToken.nextToken(); // 대한민국 제외, 서울특별시,
                result = mToken.nextToken();
            }else
                Toast.makeText(mContext, "위치 검색 실패", Toast.LENGTH_SHORT).show();
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    // 기존 위,경도 -> 기상청 위,경도
    public String ChangeLonToX()
    {
        double re = 6371.00877; // 지도반경
        double grid = 5.0;
        double setLon = 126.0;
        double setLat = 38.0;

        return "test";
    }


    public String ChangeLatToY()
    {

        return "test";
    }
}
