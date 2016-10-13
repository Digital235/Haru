package blurtic.haru;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {

    private TextView tvDate;
    private ImageView prevMonthBtn;
    private ImageView nextMonthBtn;
    private GridAdapter gridAdapter;
    private ArrayList<String> dayList;
    private GridView gridView;

    private Calendar mCal;
    private Calendar todayCal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        tvDate = (TextView)findViewById(R.id.tv_date);
        gridView = (GridView)findViewById(R.id.gridview);
        prevMonthBtn = (ImageView)findViewById(R.id.btn_prevMonth);
        nextMonthBtn = (ImageView)findViewById(R.id.btn_nextMonth);
        todayCal = Calendar.getInstance();

        prevMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mCal.get(Calendar.YEAR);
                int month = mCal.get(Calendar.MONTH)-1;
                mCal.set(year, month, 1);
                makeDayList(new Date(mCal.getTimeInMillis()));
                gridAdapter.notifyDataSetChanged();
                gridAdapter.setYearMonth(year,month+1);
            }
        });

        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mCal.get(Calendar.YEAR);
                int month = mCal.get(Calendar.MONTH)+1;
                mCal.set(year, month, 1);
                makeDayList(new Date(mCal.getTimeInMillis()));
                gridAdapter.notifyDataSetChanged();
                gridAdapter.setYearMonth(year,month+1);
            }
        });

        dayList = new ArrayList<String>();

        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        makeDayList(date);
        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder holder=(ViewHolder)view.getTag();
                String sDay=holder.calendarDayView.getText().toString();
                if(TextUtils.isDigitsOnly(sDay) && !sDay.isEmpty()) {
                    if(sDay.length()==1)sDay="0"+sDay;
                    Toast.makeText(DiaryActivity.this, gridAdapter.getYearMonth()+sDay, Toast.LENGTH_SHORT).show();
                }
                else{

                }
            }
        });
    }

    /**
     * 해당 월에 표시할 일 수 구함
     *
     * @param month
     */
    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);

        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
    }

    private void calculateFirstDay(Date date){
        SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }
    }
    private void makeDayList(Date date){
        SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        tvDate.setText(curYearFormat.format(date) + "년 " + curMonthFormat.format(date) + "월");
        //gridview 요일 표시
        dayList.clear();
        dayList.add("일");
        dayList.add("월");
        dayList.add("화");
        dayList.add("수");
        dayList.add("목");
        dayList.add("금");
        dayList.add("토");

        if(mCal==null)mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        calculateFirstDay(date);
        //day리스트 추가
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);
    }

    /**
     * 그리드뷰 어댑터
     *
     */
    private class GridAdapter extends BaseAdapter {

        private final List<String> list;
        private final LayoutInflater inflater;
        int year;
        int month;
        /**
         * 생성자
         *
         * @param context
         * @param list
         */
        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            long now = System.currentTimeMillis();
            final Date date = new Date(now);

            SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
            SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
            SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

            year=Integer.parseInt(curYearFormat.format(date));
            month=Integer.parseInt(curMonthFormat.format(date));
        }

        public void setYearMonth(int y, int m){
            year=y;
            month=m;
        }

        public String getYearMonth(){
            String sMonth="";
            if(month<10)sMonth="0"+month;
            else sMonth=""+month;
            return ""+year+sMonth;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendar_item, parent, false);
                holder = new ViewHolder();

                holder.dayBackground = (LinearLayout)convertView.findViewById(R.id.layout_dayBackground);
                holder.calendarDayView = (TextView)convertView.findViewById(R.id.txt_calendarDay);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.calendarDayView.setText("" + getItem(position));

            //해당 날짜 텍스트 컬러,배경 변경
            holder.dayBackground.setBackgroundColor(Color.parseColor("#00000000"));
            if(mCal==null)mCal = Calendar.getInstance();
            //오늘 day 가져옴
            int yearOfToday = todayCal.get(Calendar.YEAR);
            int monthOfToday = todayCal.get(Calendar.MONTH)+1;
            int dayOfToday = todayCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(dayOfToday);
            if (yearOfToday==gridAdapter.year && monthOfToday==gridAdapter.month &&sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                holder.dayBackground.setBackgroundColor(Color.parseColor("#8CFFB700"));
            }
            return convertView;
        }
    }

    class ViewHolder {
        LinearLayout dayBackground;
        TextView calendarDayView;
    }
}