package doseo.dodam.com.dodam;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by 조현정 on 2018-02-21.
 */

public class HttpConnHandlerManager {

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

                        Object jsonObject = msg.obj;
                        break;
                }
            }
        }
    }

    public Object resultObject(Object object){
        return object;
    }
}
