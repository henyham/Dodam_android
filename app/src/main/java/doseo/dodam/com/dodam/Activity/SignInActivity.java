package doseo.dodam.com.dodam.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import doseo.dodam.com.dodam.Dialog.CustomDialog;
import doseo.dodam.com.dodam.R;


/**
 * Created by Administrator on 2018-02-12.
 */

public class SignInActivity extends AppCompatActivity{

    private ImageButton kakaoLoginBtn, facebookLoginBtn;
    private CallbackManager callbackManager;
    private static String jsonString;

    //커스텀 다이얼로그
    private CustomDialog mCustomDialog;

    //인터넷 연결 확인 변수
    public static final String WIFE_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";

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
            facebookLoginBtn = (ImageButton) findViewById(R.id.facebook_login_btn);
            kakaoLoginBtn = (ImageButton)findViewById(R.id.kakao_login_btn);
            Login();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //서버와 연결
    private final MyHandler mHandler = new SignInActivity.MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<SignInActivity> weakReference;

        public MyHandler(SignInActivity signInActivity) {
            weakReference = new WeakReference<SignInActivity>(signInActivity);
        }

        @Override
        public void handleMessage(Message msg) {

            SignInActivity signInactivity = weakReference.get();

            if (signInactivity != null) {
                switch (msg.what) {

                    case 101:

                        jsonString = (String)msg.obj;
                        //jsonString에 결과값 있음
                        break;
                }
            }
        }
    }


    private void Login() {
        Log.d("TAG","Login() start");
        facebookLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //인터넷 연결 확인
                if(getWhatKindOfNetwork(getApplicationContext()) == NONE_STATE){
                    //커스텀 다이얼로그 띄워주기
                    mCustomDialog = new CustomDialog(SignInActivity.this, "인터넷 연결을 해주세요.", singleListener);
                    mCustomDialog.show();

                    //확인 버튼 누른후 SignInActivity Reload
                    //Intent intent = new Intent(SignInActivity.this, SignInActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //startActivity(intent);
                }
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
                                                    //Log.i("TAG", "AccessToken: " + loginResult.getAccessToken().getToken());
                                                    MainActivity.currentUser.setUserId(user.getString("id"));
                                                    MainActivity.currentUser.setUserName(user.getString("name"));
                                                    setResult(RESULT_OK);

                                                    if (isInitial() == 0) {
                                                        //회원가입
                                                        //postUser();
                                                    } else if (isInitial() == 1) {

                                                    }
                                                    else {
                                                        Log.d("Error Tag", "ERROR from Server: isInitial returned -1 ");
                                                    }

                                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();


                                            /*//프로필 사진 저장하기
                                            Profile profile = Profile.getCurrentProfile();
                                            MainActivity.currentUser.setUserProfile("https://graph.facebook.com/" + MainActivity.currentUser.getUserId() + "/picture?type=large");*/
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
            //Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
        }
    };

    private int isInitial(){
        String userId = AccessToken.getCurrentAccessToken().getUserId();
        Log.d("UserId : ",userId);
        int existenceFlag= -1;
        getUserInfo(userId);
        //JSONObject stringToJson = new JSONObject(jsonString);

        try{
            //if(jsonObject.getString("userStatus") == "newUser") existenceFlag = 0;
            //else if(jsonObject.getString("userStatus") == "joinedUser") existenceFlag = 1;
         //   else    Log.d("ERR existenceFlag = ",String.valueOf(existenceFlag));
        }
        catch(Exception e){
            String result = e.toString();
            Log.d("LOG: ",result);
        }

        if(existenceFlag == 0) return 0; //회원 정보가 DB에 존재하지 않을 경우 회원 가입
        else if(existenceFlag == 1) return 1; //존재할 경우 로그인
        return -1;  //error
    }

    public int getUserInfo(final String userId) {

        Thread thread = new Thread(new Runnable() {

            public void run() {

                String result;
                String REQUEST_URL = "http://13.125.145.191:8000/users/exist?userId=" + userId;

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
                    Log.d("Tag", result);


                } catch (Exception e) {
                    result = e.toString();
                    Log.d("LOG: ",result);
                }

                Message message = mHandler.obtainMessage(101, result);
                mHandler.sendMessage(message);
            }

        });
        thread.start();

        return -1;
    }



}
