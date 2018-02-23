package doseo.dodam.com.dodam.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import doseo.dodam.com.dodam.Connection.GetHttpURLConnection;
import doseo.dodam.com.dodam.R;

/**
 * Created by 조현정 on 2018-02-12.
 */

public class BarcodeResultActivity extends AppCompatActivity {

    //isbn info 변수
    int isbn_type;
    String isbn_str;

    //test용 textView 변수
    private TextView tv1, tv2,textviewJSONText;

    //요청 url 변수
    private String REQUEST_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_result);

        Intent intent = getIntent();

        isbn_type = intent.getExtras().getInt("isbn_type");
        isbn_str = intent.getStringExtra("isbn_str");

        //위젯 참조
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        textviewJSONText = (TextView)findViewById(R.id.tvJSONText);

        //URL 설정
        REQUEST_URL = "https://dapi.kakao.com//v2/search/book?target=isbn&query="+isbn_str;

        //AsyncTask를 통해 HttpURLConnection 수행
        SearchBook searchBook = new SearchBook(REQUEST_URL,"Authorization", "KakaoAK " + getResources().getString(R.string.kakao_app_rest_key),null);
        searchBook.execute();

        //test용
        tv1.setText("isbn_str : " + isbn_str);
        tv2.setText("isbn_numa : " + String.valueOf(isbn_type));
    }

    public class SearchBook extends AsyncTask<Void, Void, JSONObject> {
        private String url;
        private ContentValues values;
        private String header_key;
        private String header_value;

        public SearchBook(String url, String header_key, String header_value, ContentValues values){
            this.url = url;
            this.values = values;
            this.header_key = header_key;
            this.header_value = header_value;
        }

        @Override
        protected JSONObject doInBackground(Void ... params){
            JSONObject jsonObject = null;   //요청 결과를 json 객체로 저장할 변수
            String result;  //요청 결과를 string 형태로 저장할 변수

            GetHttpURLConnection getHttpURLConnection = new GetHttpURLConnection();
            result = getHttpURLConnection.request(url,header_key, header_value,values);
            Log.d("result",result);
            try{
                jsonObject = new JSONObject(result);
            }catch(JSONException e){
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject j){
            super.onPostExecute(j);

            //doInBackground로부터 리턴된 값이 onPostExecute의 매개변수
            //json에서 getString, getInt 등으로 필요한 정보 빼낸다.
            textviewJSONText.setText(j.toString());
        }
    }

}