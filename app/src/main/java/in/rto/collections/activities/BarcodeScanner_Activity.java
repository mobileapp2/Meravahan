package in.rto.collections.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;

import in.rto.collections.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.ContentValues.TAG;

public class BarcodeScanner_Activity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    private boolean mFlash;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        ViewGroup contentFrame = findViewById(R.id.content_frame);

        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        setUpToolBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.setAspectTolerance(0.2f);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.d(TAG, rawResult.getText());
        Log.d(TAG, rawResult.getBarcodeFormat().toString());

        Intent intent = getIntent();
        intent.putExtra("key", rawResult.toString());
        setResult(RESULT_OK, intent);
        finish();


    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Barcode/QR Code Scanner");

        toolbar.setNavigationIcon(R.drawable.icon_backarrow_16p);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_barcode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_flash) {
            toggleFlash();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void toggleFlash() {
        mFlash = !mFlash;
        if (!mFlash) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_flash));
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icon_noflash));
        }

        mScannerView.setFlash(mFlash);
    }
}
