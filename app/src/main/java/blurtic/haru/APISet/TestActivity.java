package blurtic.haru.APISet;

/**
 * Created by Kim W on 2016-07-24.
 */

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import blurtic.haru.R;
import blurtic.haru.APISet.Weather.Manager.URLConnectionManager;
import blurtic.haru.APISet.Weather.Message.HandlerMessage;

public class TestActivity extends AppCompatActivity {
    Button btn_totalRead;
    Button btn_timeRead;

    TextView tv_totalRead;
    TextView tv_timeRead;
    TestActivity mMain;
    ProgressDialog dialog;
    public void OnInit() {
        btn_totalRead = (Button)findViewById(R.id.btn_totalRead);
        btn_timeRead = (Button)findViewById(R.id.btn_timeRead);
        tv_totalRead = (TextView)findViewById(R.id.btn_timeRead);
        tv_timeRead = (TextView)findViewById(R.id.btn_timeRead);
        mMain = this;

    }
    //
//    dialog = ProgressDialog.show(this, "",
//            "Loading", true);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        OnInit();
        URLConnectionManager mManage = new URLConnectionManager(this.getApplicationContext(),"구미시");
        mManage.run();
    }
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HandlerMessage.THREAD_HANDLER_MIDDLELAND_SUCCESS_INFO:
//                    ArrayList<ContentValues> mTrainTicekt = (ArrayList<ContentValues>)msg.obj;
//                    ContentValueToArrayList(mTrainTicekt);
//                    ArrayList<String> result_Text = makeResult();
//                    if(result_Text == null) {
//                        Toast.makeText(mContext, "역 운행정보가 없습니다", Toast.LENGTH_SHORT);
//                        break;
//                    }
//                    String tv_SetResult = "";
//                    for(int i = 0; i < result_Text.size(); i++)
//                    {
//                        String temp = result_Text.get(i);
//                        tv_SetResult += temp;
//                    }
//                    tv_result.setText(tv_SetResult);
//
//                    dialog.dismiss();
//                    break;
//                default:
//                    break;
            }
        }
    };
}
