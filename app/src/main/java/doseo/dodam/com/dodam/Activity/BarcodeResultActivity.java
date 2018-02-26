package doseo.dodam.com.dodam.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import doseo.dodam.com.dodam.Connection.GetHttpURLConnection;
import doseo.dodam.com.dodam.Object.Book;
import doseo.dodam.com.dodam.R;

/**
 * Created by 조현정 on 2018-02-12.
 */

public class BarcodeResultActivity extends AppCompatActivity {

    //isbn info 변수
    int isbn_type;
    String isbn_str;

    //검색 결과 책 저장할 객체
    private Book resultBook = new Book();
    Bitmap bitmap;
    //위젯 참조 변수
    private ImageView bookCover;
    private TextView bookTitle, bookWriter, bookDetail;
    private Button addBookBtn;

    //test용 textView 변수
    //private TextView tv1, tv2,textviewJSONText;

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
        //tv1 = (TextView) findViewById(R.id.tv1);
        //tv2 = (TextView) findViewById(R.id.tv2);
        //textviewJSONText = (TextView)findViewById(R.id.tvJSONText);
        bookCover = findViewById(R.id.book_cover);
        bookTitle= findViewById(R.id.book_title);
        bookWriter = findViewById(R.id.book_writer);
        bookDetail = findViewById(R.id.book_detail);
        addBookBtn = findViewById(R.id.add_book_btn);

        //URL 설정
        REQUEST_URL = "https://dapi.kakao.com//v2/search/book?target=isbn&query="+isbn_str;

        resultBook.setIsbn(isbn_str);
        //AsyncTask를 통해 HttpURLConnection 수행
        SearchBook searchBook = new SearchBook(REQUEST_URL,"Authorization", "KakaoAK " + getResources().getString(R.string.kakao_app_rest_key),null);
        searchBook.execute();

        //썸네일 이미지
        Thread mThread = new Thread(){
            @Override
            public void run(){
                try{
                    URL url = new URL(resultBook.getBook_cover());

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                }catch(MalformedURLException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();;
                }
            }
        };
        mThread.start();

        try{
            mThread.join();

            bookCover.setImageBitmap(bitmap);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
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

            try{
                Log.d("LOGLOG",j.getJSONArray("documents").getJSONObject(0).getString("category"));
                resultBook.setBookAttribute(j);

                bookTitle.setText(resultBook.getTitle());
                int idx = 0;
                String authors = null;
                while(resultBook.getBook_authors().size()<idx){
                    authors += resultBook.getBook_authors().get(idx);
                    idx++;
                }
                bookWriter.setText(authors);
                bookDetail.setText(resultBook.getPub_date() + " - " + resultBook.getPublisher() + " - " + resultBook.getCategory_name());

                REQUEST_URL = resultBook.getBook_cover();

                //썸네일 이미지
                Thread mThread = new Thread(){
                    @Override
                    public void run(){
                        try{
                            URL url = new URL(REQUEST_URL);

                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.setDoInput(true);
                            conn.connect();

                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);

                        }catch(MalformedURLException e){
                            e.printStackTrace();
                        }catch(IOException e){
                            e.printStackTrace();;
                        }
                    }
                };
                mThread.start();

                try{
                    mThread.join();

                    bookCover.setImageBitmap(bitmap);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}