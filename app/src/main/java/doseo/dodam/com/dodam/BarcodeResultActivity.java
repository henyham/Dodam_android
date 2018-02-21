package doseo.dodam.com.dodam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 조현정 on 2018-02-12.
 */

public class BarcodeResultActivity extends AppCompatActivity {

    int isbn_type;
    String isbn_str;
    private TextView tv1, tv2,textviewJSONText;
    private String REQUEST_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_result);

        Intent intent = getIntent();

        isbn_type = intent.getExtras().getInt("isbn_type");
        isbn_str = intent.getStringExtra("isbn_str");
        REQUEST_URL = "https://dapi.kakao.com//v2/search/book?target=isbn&query="+isbn_str;

        getSearchResult();
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        textviewJSONText = (TextView)findViewById(R.id.tvJSONText);

        tv1.setText("isbn_str : " + isbn_str);
        tv2.setText("isbn_num : " + String.valueOf(isbn_type));
    }

    //서버 연결시 필요한 핸들러 선언
    private final BarcodeResultActivity.BarcodeResultHandler mHandler = new BarcodeResultActivity.BarcodeResultHandler(this);

    //서버 연결시 필요한 핸들러 클래스 생성
    private static class BarcodeResultHandler extends Handler {
        private final WeakReference<BarcodeResultActivity> weakReference;

        public BarcodeResultHandler(BarcodeResultActivity barcodeResultactivity) {
            weakReference = new WeakReference<BarcodeResultActivity>(barcodeResultactivity);
        }

        @Override
        public void handleMessage(Message msg) {

            BarcodeResultActivity barcodeResultActivity = weakReference.get();

            if (barcodeResultActivity != null) {
                switch (msg.what) {

                    case 101:

                        String jsonString = (String)msg.obj;
                        barcodeResultActivity.textviewJSONText.setText(jsonString);
                        break;
                }
            }
        }
    }

    public void getSearchResult() {

        Thread thread = new Thread(new Runnable() {

            public void run() {

                String result;

                try {
                    Log.d("LOG: ", "getJSON_RUN started");
                    URL url = new URL(REQUEST_URL);
                    Log.d("LOG: ", REQUEST_URL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    //httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestProperty("Authorization","KakaoAK " + getResources().getString(R.string.kakao_app_rest_key));
                    httpURLConnection.connect();


                    int responseStatusCode = httpURLConnection.getResponseCode();

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        Log.d("LOG: ", "responseStatusCode OK");
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        Log.d("LOG: ", "responseStatusCode NON OK");
                        Log.d("LOG: ", String.valueOf(responseStatusCode));
                        inputStream = httpURLConnection.getErrorStream();

                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line;


                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    httpURLConnection.disconnect();

                    result = sb.toString().trim();


                } catch (Exception e) {
                    result = e.toString();
                    Log.d("LOG: ", result);
                }

                Message message = mHandler.obtainMessage(101, result);
                mHandler.sendMessage(message);
            }

        });
        thread.start();
    }

}