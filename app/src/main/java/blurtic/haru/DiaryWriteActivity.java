package blurtic.haru;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class DiaryWriteActivity extends AppCompatActivity {
    private ArrayList<PlanItem> planItemList=null;
    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;
    private TextView okButton;
    private String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
        Intent intent = getIntent();
        dateString = intent.getExtras().getString("date");

        loadPlanFromFile(dateString);

        if(planItemList!=null) {
            mListView = (SwipeMenuListView) findViewById(R.id.planListView);
            okButton = (TextView)findViewById(R.id.bt_ok);

            mAdapter = new AppAdapter();
            mListView.setAdapter(mAdapter);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    class AppAdapter extends BaseSwipListAdapter {

        @Override
        public int getCount() {
            return planItemList.size();
        }

        @Override
        public PlanItem getItem(int position) {
            return planItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.planlist_item, null);
                new ViewHolder(convertView);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            PlanItem item = getItem(position);

            holder.nameView.setText(item.getName());
            if(item.getCompleteHour()==-1)
            {
                holder.checkImage.setImageResource(R.drawable.ic_check1);
                holder.timeView.setText(make2Digit(item.getHour()) + ":" + make2Digit(item.getMinute()));
            }
            else
            {
                holder.checkImage.setImageResource(R.drawable.ic_check2);
                holder.timeView.setText(make2Digit(item.getHour()) + ":" + make2Digit(item.getMinute()) + " / "
                        + make2Digit(item.getCompleteHour()) + ":" + make2Digit(item.getCompleteMinute()));
            }
            return convertView;
        }

        class ViewHolder {
            ImageView checkImage;
            TextView timeView;
            TextView nameView;
            LinearLayout infoView;
            LinearLayout background;

            public ViewHolder(View view) {
                checkImage = (ImageView) view.findViewById(R.id.img_check);
                timeView = (TextView) view.findViewById(R.id.txt_time);
                nameView = (TextView) view.findViewById(R.id.txt_name);
                infoView = (LinearLayout) view.findViewById(R.id.layout_planInfo);
                background = (LinearLayout) view.findViewById(R.id.layout_background);
                view.setTag(this);
            }
        }
    }

    private void loadPlanFromFile(String dateSting){
        String filename="p"+dateSting;
        try {
            File file = getFileStreamPath(filename);
            if(file == null || !file.exists()) {
                return;
            }
            FileInputStream fis = openFileInput(filename);
            byte[] data = new byte[fis.available()];
            fis.read(data);

            planItemList = PlanListJsonParser.fromJson(new String(data));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String make2Digit(int num){
        if(num<10)return "0"+num;
        else return ""+num;
    }
}
