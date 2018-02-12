package doseo.dodam.com.dodam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by 조현정 on 2018-02-12.
 */

public class BacordResultActivity extends AppCompatActivity {


    private TextView tv1, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacord_result);

        Intent intent = getIntent();

        int isbn_type = intent.getExtras().getInt("isbn_type");
        String isbn_str = intent.getStringExtra("isbn_str");

        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);

        tv1.setText("isbn_str : " +isbn_str);
        tv2.setText("isbn_num : "  + String.valueOf(isbn_type));
    }
}