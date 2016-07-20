package com.example.laituan.simplenetworkanalyzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Lai Tuan on 7/20/2016.
 */
public class ResultActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ResultActivity";
    private String mLog, mLogType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        mLog = intent.getStringExtra(MainActivity.EXTRA_LOG);
        switch (intent.getIntExtra(MainActivity.EXTRA_LOGTYPE, -1)) {
            case MainActivity.ACTIVENETWORK_LOGTYPE:
                mLogType = "Active Network";
                break;
            case MainActivity.TESTREQUEST_LOGTYPE:
                mLogType = "Test Request";
                break;
            default:
                mLogType = "Unknown";
                break;
        }

        ((TextView) findViewById(R.id.logTextView)).setText(mLog);
        ((TextView) findViewById(R.id.logTextView)).setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.saveLogBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveLogBtn:
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("message/rfc822");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "laituan.test@gmail.com" });
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "[LOG - " + mLogType + "]");
                sendIntent.putExtra(Intent.EXTRA_TEXT, mLog);
                startActivity(sendIntent);
                break;
            default:
                break;
        }
    }
}
