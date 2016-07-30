package blurtic.haru;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import blurtic.haru.APISet.TestActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btn_testActivity_change;

    void OnInitSet()
    {
        btn_testActivity_change = (Button)findViewById(R.id.btn_changeTestAct);
        btn_testActivity_change.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId())
        {
            case R.id.btn_changeTestAct:
                intent = new Intent(this, TestActivity.class);
                startActivity(intent);
                break;

        }
    }

    //TODO : 시발
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnInitSet();
    }
}//160617
