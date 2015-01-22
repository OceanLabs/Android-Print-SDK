package co.oceanlabs.sample;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.oceanlabs.sample.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;
import ly.kite.print.Asset;
import ly.kite.print.KitePrintSDK;
import ly.kite.print.PrintJob;
import ly.kite.print.PrintOrder;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.ProductType;

public class MainActivity extends Activity {

    private static final String API_KEY_TEST = "ba171b0d91b1418fbd04f7b12af1e37e42d2cb1e";
    private static final String API_KEY_LIVE = "7c575489215d24bfb3d3df3b6df053eac83542da";

    private static final int REQUEST_CODE_SELECT_PICTURE = 1;
    private static final int REQUEST_CODE_CHECKOUT = 2;

    private Switch environmentSwitch;
    private Spinner productSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        environmentSwitch = (Switch) findViewById(R.id.environment);
        productSpinner = (Spinner) findViewById(R.id.spinner_product);

        ArrayAdapter adapter = new ArrayAdapter<ProductType>(this, android.R.layout.simple_list_item_1, ProductType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(adapter);
        productSpinner.setSelection(Arrays.asList(ProductType.values()).indexOf(ProductType.MAGNETS));
    }

    public void onGalleryButtonClicked(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_SELECT_PICTURE);
    }

    public void onCheckoutButtonClicked(View view) {
        ArrayList<Asset> assets = new ArrayList<Asset>();
        assets.add(new Asset(R.drawable.instagram1));

        try {
            assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/1.jpg")));
            assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/2.jpg")));
            assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/3.jpg")));
            assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/4.jpg")));
        } catch (Exception ex) {}

        checkoutWithAssets(assets);
    }

    private void checkoutWithAssets(List<Asset> assets) {
        String apiKey = API_KEY_TEST;
        KitePrintSDK.Environment env = KitePrintSDK.Environment.TEST;
        if (environmentSwitch.isChecked()) {
            apiKey = API_KEY_LIVE;
            env = KitePrintSDK.Environment.LIVE;
        }

        KitePrintSDK.initialize(apiKey, env, getApplicationContext());

        ProductType productType = (ProductType) productSpinner.getSelectedItem();
        PrintOrder printOrder = new PrintOrder();
        printOrder.addPrintJob(PrintJob.createPrintJob(assets, productType));

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder);
        startActivityForResult(intent, REQUEST_CODE_CHECKOUT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHECKOUT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "User successfully checked out!", Toast.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "User cancelled checkout :(", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                ArrayList<Asset> assets = new ArrayList<Asset>();
                assets.add(new Asset(selectedImageUri));
                checkoutWithAssets(assets);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

}
