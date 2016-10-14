package blurtic.haru;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class PlanActivity extends AppCompatActivity {

    private ArrayList<PlanItem> planItemList=null;
    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;
    private ImageView writeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        writeButton = (ImageView) findViewById(R.id.btn_planWrite);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_plan, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(PlanActivity.this);
        builder.setView(dialogView);
        builder.setTitle("일정 작성");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edit_name = (EditText) dialogView.findViewById(R.id.edit_name);
                        EditText edit_hour = (EditText) dialogView.findViewById(R.id.edit_hour);
                        EditText edit_minute = (EditText) dialogView.findViewById(R.id.edit_minute);
                        planItemList.add(new PlanItem(Integer.parseInt(edit_hour.getText().toString()),
                                Integer.parseInt(edit_minute.getText().toString()),
                                edit_name.getText().toString()));
                        Collections.sort(planItemList);
                        mListView.deferNotifyDataSetChanged();
                        savePlanToFile();
                        edit_name.setText("");
                        edit_hour.setText("");
                        edit_minute.setText("");
                    }
                }
        );
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        loadPlanFromFile();
        if(planItemList==null)planItemList = new ArrayList<PlanItem>();

        mListView = (SwipeMenuListView) findViewById(R.id.planListView);

        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(70));
                // set item title
                openItem.setTitle("수정");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(70));
                // set a icon
                //deleteItem.setIcon(R.drawable.ic_setting);
                deleteItem.setTitle("삭제");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //Toast.makeText(PlanActivity.this, ""+position+index, Toast.LENGTH_SHORT).show();
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        planItemList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        savePlanToFile();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition( R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
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
            holder.checkImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(PlanActivity.this, ""+position+"check", Toast.LENGTH_SHORT).show();
                    ViewHolder holderCopy=holder;
                    PlanItem item= planItemList.get(position);

                    ColorDrawable viewColor = (ColorDrawable) holder.background.getBackground();
                    int originalColor = viewColor.getColor();

                    ValueAnimator colorAnim = ObjectAnimator.ofInt(holder.background, "backgroundColor", Color.WHITE, originalColor);
                    colorAnim.setDuration(300);
                    colorAnim.setEvaluator(new ArgbEvaluator());
                    colorAnim.start();

                    if(item.getCompleteHour()==-1){
                        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        int minute = Calendar.getInstance().get(Calendar.MINUTE);
                        item.planComplete(hour, minute);
                    }
                    else{
                        item.planCompleteCancel();
                    }
                    mAdapter.notifyDataSetChanged();
                    savePlanToFile();
                }
            });
            holder.infoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(PlanActivity.this, ""+position+"check", Toast.LENGTH_SHORT).show();
                }
            });
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

    private void savePlanToFile(){
        String filename="p"+stringifyDate();
        if(planItemList.size()!=0) {
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(PlanListJsonParser.toJson(planItemList).toString().getBytes());
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
    private void loadPlanFromFile(){
        String filename="p"+stringifyDate();
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

    public String stringifyDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(c.getTime());
    }

    public String make2Digit(int num){
        if(num<10)return "0"+num;
        else return ""+num;
    }
}


