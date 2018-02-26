package doseo.dodam.com.dodam.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import doseo.dodam.com.dodam.Connection.GetHttpURLConnection;
import doseo.dodam.com.dodam.Connection.PostHttpURLConnection;
import doseo.dodam.com.dodam.Dialog.CustomDialog;
import doseo.dodam.com.dodam.R;


/**
 * Created by Administrator on 2018-02-12.
 */

public class SignInActivity extends AppCompatActivity{

    private ImageButton kakaoLoginBtn, facebookLoginBtn;
    private CallbackManager callbackManager;

    //커스텀 다이얼로그
    private CustomDialog mCustomDialog;

    //인터넷 연결 확인 변수
    public static final String WIFE_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";

    //요청 url 변수
    private String REQUEST_URL;

    public static String getWhatKindOfNetwork(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //getActiveNetworkInfo() -> 인터넷이 연결돼있는 경우 인터넷 환경에 대한 여러가지 정보를 담은 객체를 리턴
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFE_STATE;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE;
            }
        }
        return NONE_STATE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화

        //현재 로그아웃 상태이면
        if(AccessToken.getCurrentAccessToken() == null) {

            setContentView(R.layout.activity_sign_in);
            callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자
            facebookLoginBtn = findViewById(R.id.facebook_login_btn);
            kakaoLoginBtn = findViewById(R.id.kakao_login_btn);
            Login();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void Login() {
        Log.d("TAG","Login() start");
        facebookLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //인터넷 연결 되지 않았을 때
                if(getWhatKindOfNetwork(getApplicationContext()) == NONE_STATE){
                    mCustomDialog = new CustomDialog(SignInActivity.this, "인터넷 연결을 해주세요.", singleListener);
                    mCustomDialog.show();
                }
                //인터넷 연결되어 있을때
                else {
                    //LoginManager - 요청된 읽기 또는 게시 권한으로 로그인 절차를 시작
                    LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this,
                            Arrays.asList("public_profile"));
                    LoginManager.getInstance().registerCallback(callbackManager,
                            new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    Log.e("onSuccess", "onSuccess");
                                    GraphRequest request;
                                    request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                                        @Override
                                        public void onCompleted(JSONObject user, GraphResponse response) {
                                            if (response.getError() != null) {

                                            } else {
                                                try {
                                                    Log.i("TAG", "user: " + user.toString());
                                                    MainActivity.currentUser.setUserId(user.getString("id"));
                                                    MainActivity.currentUser.setUserName(user.getString("name"));
                                                    MainActivity.currentUser.setUserProfile("https://graph.facebook.com/" + user.getString("id")+ "/picture?type=large");
                                                    setResult(RESULT_OK);

                                                    //페이스북에서 userId, userName 받아와서 안드로이드의 User객체에 저장 후 isInitial 호출
                                                    isInitial();

                                                } catch (Exception e) {
                                                    String result = e.toString();
                                                    Log.d("LOG: ", result);

                                                }
                                            }
                                        }
                                    });
                                    Bundle parameters = new Bundle();
                                    parameters.putString("fields", "id,name,email,gender,birthday");
                                    request.setParameters(parameters);
                                    request.executeAsync();
                                }
                                @Override
                                public void onCancel() {
                                    Log.e("onCancel", "onCancel");
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    Log.e("onError", "onError " + exception.getLocalizedMessage());
                                }
                            });
                }
            }
        });
    }

    private View.OnClickListener singleListener = new View.OnClickListener() {
        public void onClick(View v) {
            mCustomDialog.dismiss();
        }
    };

    private void isInitial(){
        Log.d("TAG","isInitial() start");

        String userId = MainActivity.currentUser.getUserId();

        //URL 설정
        REQUEST_URL = "http://13.125.145.191:8000/users/exist?userId=" + userId;

        //AsyncTask를 통해 HttpURLConnection 수행
        /**
         * GetUserInfo 클래스 내에서
         * 초기화 -> doInBackground() -> onPostExecute() 순으로 실행
         * getUserInfo.execute()에서
         * 기존 회원일 경우 goMain() 실행
         * 회원가입 해야하는 경우 postUserInfo() 실행.
         */
        GetUserInfo getUserInfo = new GetUserInfo(REQUEST_URL,null,null,null);
        getUserInfo.execute();
    }

    private void goSignIn(){
        //URL설정
        REQUEST_URL = "http://13.125.145.191:8000/users/sign_in";

        //전달할 파라미터들. body에 저장된다.
        ContentValues cvalues = new ContentValues();
        cvalues.put("userName", MainActivity.currentUser.getUserName());
        cvalues.put("userId",MainActivity.currentUser.getUserId());

        //AsyncTask를 통해 HttpURLConnection 수행
        PostUserInfo postUserInfo = new PostUserInfo(REQUEST_URL,cvalues);
        postUserInfo.execute();
    }

    public class GetUserInfo extends AsyncTask<Void, Void, JSONObject> {
        private String url;
        private ContentValues values;
        private String header_key;
        private String header_value;

        public GetUserInfo(String url, String header_key, String header_value, ContentValues values){
            this.url = url;
            this.header_key = header_key;
            this.header_value = header_value;
            this.values = values;
        }

        @Override
        protected JSONObject doInBackground(Void ... params){
            JSONObject jsonObject = null;   //요청 결과를 json 객체로 저장할 변수
            String result;  //요청 결과를 string 형태로 저장할 변수

            GetHttpURLConnection getHttpURLConnection = new GetHttpURLConnection();
            result = getHttpURLConnection.request(url,header_key, header_value,values);
            Log.d("request result : ",result);
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
            try{
                Log.d("여기는",j.getString("userState"));
                if(j.getString("userState").equals("joinedUser"))       goMain();
                else if(j.getString("userState").equals("newUser"))     goSignIn();
                else {
                    Log.d("ERROR userState : ",j.getString("userState"));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public class PostUserInfo extends AsyncTask<Void, Void, JSONObject> {
        private String url;
        private ContentValues values;

        public PostUserInfo(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected JSONObject doInBackground(Void ... params){
            JSONObject jsonObject = null;   //요청 결과를 json 객체로 저장할 변수
            String result;  //요청 결과를 string 형태로 저장할 변수

            PostHttpURLConnection postHttpURLConnection = new PostHttpURLConnection();
            result = postHttpURLConnection.request(url,values);
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
            goMain();
        }
    }

    public void goMain(){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
