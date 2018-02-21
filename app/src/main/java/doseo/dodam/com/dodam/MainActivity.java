package doseo.dodam.com.dodam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import doseo.dodam.com.dodam.Object.User;


public class MainActivity extends AppCompatActivity {

    private String tmp_isbn;
    private TextView textviewJSONText;
    private ImageView imgView;
    private String SEARCH_URL = "http://13.125.145.191:8000/test";
    private String REQUEST_URL = SEARCH_URL;
    private Bitmap bitmap;
    final static User currentUser = new User();
    private Button logoutBtn, buttonRequestJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        buttonRequestJSON = (Button)findViewById(R.id.button_main_requestjson);
        logoutBtn = (Button)findViewById(R.id.logout_btn);
        imgView = (ImageView)findViewById(R.id.user_profile_pic);
        textviewJSONText = (TextView)findViewById(R.id.textview_main_jsontext);

        checkLogin();

        textviewJSONText.setMovementMethod(new ScrollingMovementMethod());

        //프로필 사진
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is =conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try{
            thread.join();
            imgView.setImageBitmap(bitmap);
        }catch(Exception e){
            e.printStackTrace();
        }
*/
        /*button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });*/
        buttonRequestJSON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("LOG: ","CLICKED BUTTON");
                getJSON();
            }
        });

    }

    //서버 연결 코드
    private final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity mainactivity) {
            weakReference = new WeakReference<MainActivity>(mainactivity);
        }

        @Override
        public void handleMessage(Message msg) {

            MainActivity mainactivity = weakReference.get();

            if (mainactivity != null) {
                switch (msg.what) {

                    case 101:

                        String jsonString = (String)msg.obj;
                        mainactivity.textviewJSONText.setText(jsonString);
                        break;
                }
            }
        }
    }




    public void  getJSON() {

        Thread thread = new Thread(new Runnable() {

            public void run() {

                String result;

                try {
                    Log.d("LOG: ","getJSON_RUN started");
                    URL url = new URL(REQUEST_URL);
                    Log.d("LOG: ",REQUEST_URL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    //httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.connect();


                    int responseStatusCode = httpURLConnection.getResponseCode();

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        Log.d("LOG: ","responseStatusCode OK");
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        Log.d("LOG: ","responseStatusCode NON OK");
                        Log.d("LOG: ",String.valueOf(responseStatusCode));
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
                    Log.d("LOG: ",result);
                }


                Message message = mHandler.obtainMessage(101, result);
                mHandler.sendMessage(message);
            }

        });
        thread.start();
    }

    public void scanBarcode(View view) {


        IntentIntegrator integrator = new IntentIntegrator(this);

        //new IntentIntegrator(this).initiateScan();

        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setOrientationLocked(false);

        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data = new Intent(MainActivity.this, BacordResultActivity.class);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), BacordResultActivity.class);
                tmp_isbn = result.getContents();
                if(tmp_isbn.length() == 13){
                    intent.putExtra("isbn_type",13);
                    Log.d("Scannded isbn13 : " , tmp_isbn);
                }
                else {
                    intent.putExtra("isbn_type",10);
                }
                intent.putExtra("isbn_str", tmp_isbn);
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkLogin() {
        Log.d("TAG","checkLogin start");
        if (AccessToken.getCurrentAccessToken() != null) {
            //로그인 되어있는 상태
            Log.d("TAG","로그인 되어있음");
            Log.d("accessToken",AccessToken.getCurrentAccessToken().getToken());
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logOut();
                    checkLogin();
                }
            });
        } else {
            //로그아웃 되어있는 상태
            Intent i = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(i);
            finish();
        }
    }
}
