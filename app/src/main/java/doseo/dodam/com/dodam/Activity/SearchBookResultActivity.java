package doseo.dodam.com.dodam.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import doseo.dodam.com.dodam.Adapter.SearchBookResultAdapter;
import doseo.dodam.com.dodam.Connection.GetHttpURLConnection;
import doseo.dodam.com.dodam.R;

/**
 * Created by Administrator on 2018-02-27.
 */

public class SearchBookResultActivity extends AppCompatActivity {

    //bok search result data from previous intent
    private String jsonData;
    private int pageNum;
    private String searchStr;

    private RelativeLayout rl;
    private Button bookSearchBtn;
    private List<String> resultList;
    private ListView resultListView;
    private EditText bookSearchBar;
    private ArrayList<String> arrayList;
    private SearchBookResultAdapter adapter;
    private InputMethodManager imm;

    //요청 url 변수
    private String REQUEST_URL;

    private Boolean lastitemVisibleFlag = false;
    private Boolean resetListItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("progressTag", "creating SerachBookResultActivity");
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_search_book_result);

        jsonData = null;
        pageNum = 1;
        searchStr = null;

        //relative layer, setting button, edit text, listview
        rl = findViewById(R.id.book_search_layer);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        bookSearchBtn = findViewById(R.id.book_search_btn);
        bookSearchBar = (EditText) findViewById(R.id.book_search_bar);
        resultListView = findViewById(R.id.result_list_view);


        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d("progressTag", "in onClick" + bookSearchBar.getWindowToken());
                imm.hideSoftInputFromWindow(bookSearchBar.getWindowToken(), 0);
            }
        });


        //리스트 생성
        resultList = new ArrayList<String>();

        //리스트의 모든 데이터를 arraylist에 복사(result list의 복사본 생성)
        arrayList = new ArrayList<String>();
        arrayList.addAll(resultList);

        //리스트뷰에 연동될 어댑터 생성
        adapter = new SearchBookResultAdapter(resultList, this);
        //리스트뷰에 어댑터를 연결
        resultListView.setAdapter(adapter);


        //무한 스크롤
        resultListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
                    // 데이터 로드
                    String text = searchStr;
                    //url parameter 인코딩(utf-8)
                    try {
                        text = URLEncoder.encode(text, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //URL 설정(제목으로 검색)
                    pageNum++;
                    REQUEST_URL = "https://dapi.kakao.com/v2/search/book?target=title&size=20&page=" + pageNum + "&query=" + text;

                    //Log.d("progressTag", "string of REQUEST_URL: " + REQUEST_URL);

                    SearchBookByWord searchBookByWord = new SearchBookByWord(REQUEST_URL,"Authorization", "KakaoAK " + getResources().getString(R.string.kakao_app_rest_key),null);
                    searchBookByWord.execute();

                }
            }

            //스크롤의 끝에 도달했는지 판별
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });


    }

    /*
    public void layerOnClick(View v){
        Log.d("progressTag", "asjhglerlgjl");
        Log.d("progressTag", "keyboard function: "+ bookSearchBar.getWindowToken());
        imm.hideSoftInputFromWindow(bookSearchBar.getWindowToken(), 0);
    }
    */

    public void clickSearchBookByWord(View view) throws UnsupportedEncodingException {
        String text = bookSearchBar.getText().toString();
        Log.d("progressTag", "string from EditBox: " + text);

        //url parameter 인코딩(utf-8)
        text = URLEncoder.encode(text, "utf-8");

        //URL 설정(제목으로 검색)
        REQUEST_URL = "https://dapi.kakao.com/v2/search/book?target=title&size=20&query=" + text;
        pageNum = 1;
        searchStr = bookSearchBar.getText().toString();
        resetListItem = true;
        //Log.d("progressTag", "string of REQUEST_URL: " + REQUEST_URL);

        SearchBookByWord searchBookByWord = new SearchBookByWord(REQUEST_URL,"Authorization", "KakaoAK " + getResources().getString(R.string.kakao_app_rest_key),null);
        searchBookByWord.execute();
    }

    public class SearchBookByWord extends AsyncTask<Void, Void, JSONObject>{
        private String url;
        private ContentValues values;
        private String header_key;
        private String header_value;

        public SearchBookByWord(String url, String header_key, String header_value, ContentValues values){
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
            //Log.d("result",result);
            try{
                Log.d("progressTag", "in doInBackground putting resultString to json object");

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

            JSONObject tmpJson;
            String sendData ="";

            if(jsonData != null){
                try {
                    tmpJson = new JSONObject(jsonData);

                    for(int i=0; i<j.getJSONArray("documents").length();i++){
                        try {
                            tmpJson.getJSONArray("documents").put(j.getJSONArray("documents").getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    sendData = tmpJson.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                sendData = j.toString();
            }

            jsonData = sendData;

            try {
                settingList(j);
                resetListItem = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    //검색에 사용될 데이터를 리스트에 추가한다다
    private void settingList(JSONObject jsonObject) throws JSONException {
        if(resetListItem == true){
            resultList.clear();
        }
        JSONArray returnResult = jsonObject.getJSONArray("documents");

            for(int i=0;i<returnResult.length();i++){
                resultList.add(returnResult.getJSONObject(i).getString("title").toString());
        }

        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }


}
