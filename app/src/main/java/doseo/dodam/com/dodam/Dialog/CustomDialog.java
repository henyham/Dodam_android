package doseo.dodam.com.dodam.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import doseo.dodam.com.dodam.R;

/**
 * Created by Administrator on 2018-02-21.
 */

public class CustomDialog extends Dialog {
    private TextView mTitleView;
    private Button mSingleButton;
    private String mTitle;


    private View.OnClickListener mSingleClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        //커스텀 다이얼로그 layout 적용
        setContentView(R.layout.activity_custom_dialog);

        mTitleView = (TextView) findViewById(R.id.txt_title);
        mSingleButton = (Button) findViewById(R.id.btn_single);

        // 제목을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);

        // 클릭 이벤트 셋팅
        mSingleButton.setOnClickListener(mSingleClickListener);

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CustomDialog(Context context, String title,
                        View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mSingleClickListener = singleListener;
    }

}
