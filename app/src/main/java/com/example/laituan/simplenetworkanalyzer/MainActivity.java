package com.example.laituan.simplenetworkanalyzer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public final static String EXTRA_LOG = "com.example.laituan.simplenetworkanalyzer.LOG";
    public final static String EXTRA_LOGTYPE = "com.example.laituan.simplenetworkanalyzer.LOGTYPE";
    public final static int ACTIVENETWORK_LOGTYPE = 0;
    public final static int TESTREQUEST_LOGTYPE = 1;
    public final static String google204URL = "http://www.google.com/gen_204";
    public final static String kenh14URL = "http://kenh14.vn/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.checkNetworkBtn).setOnClickListener(this);
        findViewById(R.id.generate204RequestBtn).setOnClickListener(this);
        findViewById(R.id.generateKenh14RequestBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkNetworkBtn:
                NetworkInfo networkInfo = getActiveNetworkInfo();
                String log = generateLog(networkInfo);
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(EXTRA_LOG, log);
                intent.putExtra(EXTRA_LOGTYPE, ACTIVENETWORK_LOGTYPE);
                startActivity(intent);
                break;
            case R.id.generate204RequestBtn:
                findViewById(R.id.generate204RequestBtn).setEnabled(false);
                findViewById(R.id.generateKenh14RequestBtn).setEnabled(false);
                findViewById(R.id.myProgressBar).setVisibility(View.VISIBLE);
                new ExecuteGetRequestTask().execute(google204URL);
                break;
            case R.id.generateKenh14RequestBtn:
                findViewById(R.id.generate204RequestBtn).setEnabled(false);
                findViewById(R.id.generateKenh14RequestBtn).setEnabled(false);
                findViewById(R.id.myProgressBar).setVisibility(View.VISIBLE);
                new ExecuteGetRequestTask().execute(kenh14URL);
                break;
            default:
                break;
        }
    }

    private String generateLog(NetworkInfo networkInfo) {
        String log = networkInfo == null ? "no active network" : networkInfo.toString();
        return log;
    }

    private NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo;
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class ExecuteGetRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            InputStream is = null;
            int len = 1500;
            String rsString = "GET " + urls[0] + "\n";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000 /* milliseconds */);
                conn.setConnectTimeout(20000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setInstanceFollowRedirects(false);
                // Starts the query
                conn.connect();
                rsString = rsString + "Response Code: " + Integer.toString(conn.getResponseCode()) + "\n";
                Map<String, List<String>> map = conn.getHeaderFields();
                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                    rsString  = rsString + entry.getKey() + ": " + entry.getValue() + "\n";
                }
                is = conn.getInputStream();
                String contentAsString = readIt(is, len);
                rsString = rsString + "\nBody content:\n";
                rsString = rsString + contentAsString;
            }
            catch (Exception e) {
                rsString = rsString + "There is an exception:\n" + e.toString();
            }
            finally {
                return rsString;
            }
        }
        @Override
        protected void onPostExecute(String log) {
            findViewById(R.id.generate204RequestBtn).setEnabled(true);
            findViewById(R.id.generateKenh14RequestBtn).setEnabled(true);
            findViewById(R.id.myProgressBar).setVisibility(View.INVISIBLE);
            Intent intent = new Intent(getApplication(), ResultActivity.class);
            intent.putExtra(EXTRA_LOG, log);
            intent.putExtra(EXTRA_LOGTYPE, TESTREQUEST_LOGTYPE);
            startActivity(intent);
        }
    }
}
