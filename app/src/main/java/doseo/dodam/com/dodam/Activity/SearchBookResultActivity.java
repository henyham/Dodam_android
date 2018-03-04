package doseo.dodam.com.dodam.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

    private Button bookSearchBtn;
    private List<String> resultList;
    private ListView resultListView;
    private EditText bookSearchBar;
    private ArrayList<String> arrayList;
    private SearchBookResultAdapter adapter;

    //요청 url 변수
    private String REQUEST_URL;

    private Boolean lastitemVisibleFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("progressTag", "creating SerachBookResultActivity");
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_search_book_result);

        jsonData = null;
        pageNum = 1;
        searchStr = null;

        //이전 액티비티에서 넘어온 데이터를 받음
        Intent intentData = getIntent();

        //데이터 저장
        if(intentData.getExtras() != null) {
            jsonData = intentData.getExtras().getString("resultObject");    // JSONArrary(String 형) 값 content에 저장
            pageNum = intentData.getExtras().getInt("pageNum");             // 전 액티비티에서 검색했을 때 사용한 page 값
            searchStr = intentData.getExtras().getString("searchStr");      // 전 액티비티에서 검색한 책의 제목
        }
        Log.d("progressTag","intent data: " + jsonData);

        //setting button, edit text, listview
        bookSearchBtn = findViewById(R.id.book_search_btn);
        bookSearchBar = findViewById(R.id.book_search_bar);
        resultListView = findViewById(R.id.result_list_view);

        //리스트 생성
        resultList = new ArrayList<String>();

        //검색에 사용할 데이터를 미리 저장
        if(jsonData != null){
            try {
                JSONObject obj = new JSONObject(jsonData);
                Log.d("progressTag", "before settinfList: " + obj.getJSONArray("documents").toString());
                //검색 결과 리스트 나열
                settingList(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //리스트의 모든 데이터를 arraylist에 복사(result list의 복사본 생성)
        arrayList = new ArrayList<String>();
        arrayList.addAll(resultList);

        //리스트뷰에 연동될 어댑터 생성
        adapter = new SearchBookResultAdapter(resultList, this);
        //리스트뷰에 어댑터를 연결
        resultListView.setAdapter(adapter);

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

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });


    }

    public void clickSearchBookByWord(View view) throws UnsupportedEncodingException {
        String text = bookSearchBar.getText().toString();
        Log.d("progressTag", "string from EditBox: " + text);

        //url parameter 인코딩(utf-8)
        text = URLEncoder.encode(text, "utf-8");

        //URL 설정(제목으로 검색)
        REQUEST_URL = "https://dapi.kakao.com/v2/search/book?target=title&size=20&query=" + text;
        pageNum = 1;
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
                    Log.d("progressTag", "result JSON Object: "+j.toString());
                    Log.d("progressTag", "result JSON Object length: " + j.length());
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
            } else {
                sendData = j.toString();
            }
            Intent intent = new Intent(SearchBookResultActivity.this, SearchBookResultActivity.class);

            intent.putExtra("resultObject", sendData);
            intent.putExtra("pageNum", pageNum);
            if(searchStr != null){
                intent.putExtra("searchStr", searchStr);
            } else {
                intent.putExtra("searchStr", bookSearchBar.getText().toString());
            }

            Log.d("progressTag", "***in Post Execute: resultObj = " + sendData);
            Log.d("progressTag", "***in Post Execute: pageNum = " + pageNum);
            Log.d("progressTag", "***in Post Execute: searchStr = " + searchStr);

            startActivity(intent);
            finish();

        }

    }

    //검색에 사용될 데이터를 리스트에 추가한다다
    private void settingList(JSONObject jsonObject) throws JSONException {
        //Log.d("progressTag", "in settingList");
        //Log.d("progressTag", "from settingList" + jsonObject.getJSONArray("documents").getJSONObject(0).getString("title"));

        JSONArray returnResult = jsonObject.getJSONArray("documents");
        Log.d("progressTag", " result JSONArrary: " + returnResult.toString());
        Log.d("progressTag", String.valueOf(returnResult.length()));

            for(int i=0;i<returnResult.length();i++){
                Log.d("titleListTag", String.valueOf(i));
                Log.d("titleListTag", returnResult.getJSONObject(i).toString());
                //returnResult.getJSONObject(i);
                Log.d("titleListTag",  returnResult.getJSONObject(i).getString("title"));
                resultList.add(returnResult.getJSONObject(i).getString("title").toString());
        }
    }

    /*
    private Runnable updateUI = new Runnable() {
        public void run() {
            SearchBookResultActivity.this.adapter.notifyDataSetChanged();
        }
    };
    */













    //검색을 수행하는 메소드
    /*
    public void search(String charText){

        //문자 입력시마다 리스트르르 지우고 새로 뿌려준다
        resultList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            resultList.addAll(arrayList);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arrayList.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arrayList.get(i).toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    resultList.add(arrayList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }
    */


}
