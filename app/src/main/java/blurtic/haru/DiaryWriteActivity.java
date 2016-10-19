package blurtic.haru;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DiaryWriteActivity extends AppCompatActivity {
    private ArrayList<PlanItem> planItemList=null;
    private AppAdapter mAdapter;
    private LikeAdapter mLikeAdapter;
    private SwipeMenuListView mPlanListView;
    private SwipeMenuListView mLikeListView;
    private TextView okButton;
    private TextView todayDate;
    private String dateString;
    private EditText diaryContent;
    private JSONArray likeJson=null;

    private DiaryItem diaryItem=null;

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
        okButton = (TextView)findViewById(R.id.bt_ok);
        todayDate = (TextView)findViewById(R.id.txt_diaryWriteDate);
        diaryContent = (EditText)findViewById(R.id.diaryContents);

        todayDate.setText(dateString.substring(0,4)+"년 " + dateString.substring(4,6)+ "월 " + dateString.substring(6,8)+"일");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaryItem.setContent(diaryContent.getText().toString());
                saveDiaryToFile();
                finish();
            }
        });

        loadDiaryFromFile(dateString);
        loadPlanFromFile(dateString);
        loadLikes(dateString);

        if(diaryItem!=null){
            diaryContent.setText(diaryItem.getContent());
        }
        else{
            diaryItem=new DiaryItem();
        }
        if(planItemList!=null) {
            mPlanListView = (SwipeMenuListView) findViewById(R.id.planListView);
            mAdapter = new AppAdapter();
            mPlanListView.setAdapter(mAdapter);
        }

        if(likeJson!=null) {
            mLikeListView = (SwipeMenuListView) findViewById(R.id.likeListView);
            mLikeAdapter = new LikeAdapter();
            mLikeListView.setAdapter(mLikeAdapter);
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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

    class LikeAdapter extends BaseSwipListAdapter {
        @Override
        public int getCount() {
            return likeJson.length();
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
            //PlanItem item = getItem(position);
            int index=0;
            try {
                index = likeJson.getInt(position);
            }
            catch(Exception e)
            {}

            if(index>=1000 && index <2000){
                holder.nameView.setText(MovieDatabase.items[index-1000].title);
                holder.checkImage.setImageResource(R.drawable.movie);
                holder.timeView.setText("영화");
            }
            else if(index>=2000 && index <3000){
                holder.nameView.setText(MusicDatabase.items[index-2000].title);
                holder.checkImage.setImageResource(R.drawable.cd_music);
                holder.timeView.setText("음악");
            }
            else if(index >=3000 && index<4000){
                holder.nameView.setText(FoodDatabase.items[index-3000].title);
                holder.checkImage.setImageResource(R.drawable.food);
                holder.timeView.setText("먹거리");
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

    public void loadDiaryFromFile(String dateSting){
        String filename="d"+dateSting;
        try {
            File file = getFileStreamPath(filename);
            if(file == null || !file.exists()) {
                return;
            }
            FileInputStream fis = openFileInput(filename);
            byte[] data = new byte[fis.available()];
            fis.read(data);

            diaryItem = DiaryItem.fromJson(new String(data));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadLikes(String dateSting){
        String filename="l"+dateSting;
        try {
            File file = getFileStreamPath(filename);
            if(file == null || !file.exists()) {
                return;
            }
            Log.d("test", "fileexist");
            FileInputStream fis = openFileInput(filename);
            byte[] data = new byte[fis.available()];
            fis.read(data);

            likeJson = new JSONArray(new String(data));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDiaryToFile(){
        String filename="d"+dateString;
        if(!diaryItem.getContent().isEmpty()) {
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(DiaryItem.toJson(diaryItem).toString().getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            File dir = getFilesDir();
            File file = new File(dir, filename);
            file.delete();
        }
    }

    public String make2Digit(int num){
        if(num<10)return "0"+num;
        else return ""+num;
    }
}
