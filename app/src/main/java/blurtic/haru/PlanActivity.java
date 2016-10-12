package blurtic.haru;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class PlanActivity extends AppCompatActivity {

    private ArrayList<PlanItem> planItemList;
    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        planItemList = new ArrayList<PlanItem>();
        planItemList.add(new PlanItem(10, 0, "test1"));
        planItemList.add(new PlanItem(11, 0, "test2"));
        planItemList.add(new PlanItem(13,0,"test3"));
        planItemList.add(new PlanItem(14,0,"test4"));
        planItemList.add(new PlanItem(15,0,"test5"));
        planItemList.add(new PlanItem(16,0,"test6"));

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
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
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
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_setting);
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
                Toast.makeText(PlanActivity.this, ""+position+index, Toast.LENGTH_SHORT).show();
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        planItemList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.planlist_item, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            PlanItem item = getItem(position);
            //holder.checkImage.setImageDrawable(item.loadIcon(getPackageManager()));
            holder.timeView.setText(""+item.getHour()+":"+item.getMinute());
            holder.nameView.setText(item.getName());
            holder.checkImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PlanActivity.this, "check", Toast.LENGTH_SHORT).show();
                }
            });
            holder.infoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PlanActivity.this, "info", Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView checkImage;
            TextView timeView;
            TextView nameView;
            LinearLayout infoView;

            public ViewHolder(View view) {
                checkImage = (ImageView) view.findViewById(R.id.img_check);
                timeView = (TextView) view.findViewById(R.id.txt_time);
                nameView = (TextView) view.findViewById(R.id.txt_name);
                infoView = (LinearLayout) view.findViewById(R.id.layout_planInfo);
                view.setTag(this);
            }
        }
    }
}


