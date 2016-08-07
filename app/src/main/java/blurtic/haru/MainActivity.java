package blurtic.haru;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.androidconnect.listview.horizontal.adapter.HorizontalListView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(blurtic.haru.R.layout.activity_main);

        ImageButton bt_Detail = (ImageButton)findViewById(R.id.imgbtn_detail);
        final LinearLayout layout_TodayDetail = (LinearLayout)findViewById(R.id.layout_today_detail);
        HorizontalListView timeWeatherListView = (HorizontalListView) findViewById(R.id.listview_timeweather);

        timeWeatherListView.setAdapter(new HAdapter());

        View.OnClickListener onDetailClicked = new View.OnClickListener() {
            public void onClick(View v) {
                ImageButton bt = (ImageButton) v;
                if(layout_TodayDetail.getVisibility()==View.VISIBLE)
                {
                    bt.setImageResource(android.R.drawable.arrow_down_float);
                    layout_TodayDetail.setVisibility(View.GONE);
                }
                else
                {
                    bt.setImageResource(android.R.drawable.arrow_up_float);
                    layout_TodayDetail.setVisibility(View.VISIBLE);
                }
            }
        };
        bt_Detail.setOnClickListener(onDetailClicked);

        layout_TodayDetail.setVisibility(View.GONE);
    }

    private static String[] dataObjects = new String[]{
            "시간별 날씨",
            "시간별 날씨2",
            "시간별 날씨3",
            "시간별 날씨4",
            "시간별 날씨5",
            "시간별 날씨6",
            "시간별 날씨7",
            "시간별 날씨8",
            "시간별 날씨9",
            "시간별 날씨10"};

    private class  HAdapter extends BaseAdapter {

        public HAdapter(){
            super();
        }

        public int getCount() {
            return dataObjects.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
            TextView title = (TextView) retval.findViewById(R.id.txt_timeweather_date);
            TextView temp = (TextView) retval.findViewById(R.id.txt_timeweather_temp);
            ImageView image = (ImageView) retval.findViewById(R.id.img_timeweather_img);
            temp.setText(dataObjects[position]);
            title.setText(dataObjects[position]);

            return retval;
        }

    };
}//160617
