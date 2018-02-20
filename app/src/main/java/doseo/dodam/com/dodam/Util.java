package doseo.dodam.com.dodam;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.SharedPreferences.*;

/**
 * Created by 조현정 on 2018-02-20.
 */

public class Util {
    private String mFacebookAccessToken;

    // app 쉐어드 프레퍼런스에 값 저장
    private void setAppPreferences(Activity context, String key, String value)
    {
        SharedPreferences pref = context.getSharedPreferences("FacebookCon", 0);
        Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);

        prefEditor.commit();
    }

    // app 쉐어드 프레퍼런스에서 값을 읽어옴
    private String getAppPreferences(Activity context, String key)
    {
        String returnValue = null;

        SharedPreferences pref = null;
        pref = context.getSharedPreferences("FacebookCon", 0);

        returnValue = pref.getString(key, "");

        return returnValue;
    }
}
