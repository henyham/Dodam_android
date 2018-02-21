package doseo.dodam.com.dodam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;


import java.lang.ref.WeakReference;
import java.util.Arrays;


/**
 * Created by Administrator on 2018-02-12.
 */

public class SignInActivity extends AppCompatActivity{

    private Button customLoginButton;
    private CallbackManager callbackManager;
    private int existUserCheck = 0; //0 회원가입 ,1 로그인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)

        if(AccessToken.getCurrentAccessToken() == null) {

            setContentView(R.layout.activity_sign_in);
            callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자
            customLoginButton = (Button) findViewById(R.id.loginBtn);
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

                        String jsonString = (String)msg.obj;
                        //jsonString에 결과값 있음
                        break;
                }
            }
        }
    }

    private void Login() {
        Log.d("TAG","Login start");
        customLoginButton.setText("로구인");
        customLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LoginManager - 요청된 읽기 또는 게시 권한으로 로그인 절차를 시작합니다.
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
                                            try{
                                                Log.i("TAG", "user: " + user.toString());
                                                //Log.i("TAG", "AccessToken: " + loginResult.getAccessToken().getToken());
                                                setResult(RESULT_OK);

                                                if(true/*isInital() == 0*/){
                                                    //회원가입
                                                    //postUser();
                                                }
                                                else if(true/*isInital() == 1*/){
                                                    //로그인 -> mainActivity로 넘어간다.
                                                }
                                                //MainActivity.currentUser.setUserId(user.getString("id"));
                                                //MainActivity.currentUser.setUserName(user.getString("name"));

                                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();


                                            /*//프로필 사진 저장하기
                                            Profile profile = Profile.getCurrentProfile();
                                            MainActivity.currentUser.setUserProfile("https://graph.facebook.com/" + MainActivity.currentUser.getUserId() + "/picture?type=large");*/
                                            }
                                            catch(Exception e){
                                                String result = e.toString();
                                                Log.d("LOG: ",result);

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
        });
    }

}
