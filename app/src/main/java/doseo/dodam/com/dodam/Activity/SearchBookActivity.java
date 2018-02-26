package doseo.dodam.com.dodam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import doseo.dodam.com.dodam.R;

/**
 * Created by 조현정 on 2018-02-27.
 */

public class SearchBookActivity extends AppCompatActivity {

    private Button barcodeBtn, wordBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_search_book);

        //위젯 참조
        barcodeBtn = findViewById(R.id.barcode_btn);
        wordBtn = findViewById(R.id.word_btn);

    }

    public void scanBarcode(View view) {

        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setOrientationLocked(false);

        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //data = new Intent(MainActivity.this, BarcodeResultActivity.class);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), BarcodeResultActivity.class);
                String tmp_isbn = result.getContents();
                intent.putExtra("isbn_str", tmp_isbn);
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
