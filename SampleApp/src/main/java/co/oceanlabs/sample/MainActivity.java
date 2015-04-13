package co.oceanlabs.sample;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.oceanlabs.sample.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import ly.kite.address.Address;
import ly.kite.print.Asset;
import ly.kite.print.KitePrintSDK;
import ly.kite.print.PrintJob;
import ly.kite.print.PrintOrder;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.ProductType;
import ly.kite.print.Template;
import ly.kite.printshop.ProductHomeActivity;

public class MainActivity extends Activity {

    /**********************************************************************
     * Insert your Kite API keys here. These are found under your profile
     * by logging in to the developer portal at https://www.kite.ly
     **********************************************************************/
    private static final String API_KEY_TEST = "a45bf7f39523d31aa1ca4ecf64d422b4d810d9c4";
    private static final String API_KEY_LIVE = "REPLACE_ME";

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
        Toast.makeText(this, "Not supported yet", Toast.LENGTH_SHORT).show();
//        ArrayList<Asset> assets = new ArrayList<Asset>();
////        assets.add(new Asset(R.drawable.instagram1));
//
//        try {
//            assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/1.jpg")));
//            assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/2.jpg")));
//            assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/3.jpg")));
//            assets.add(new Asset(new URL("http://psps.s3.amazonaws.com/sdk_static/4.jpg")));
//
//        } catch (Exception ex) {}
//
//        checkoutWithAssets(assets);
    }

    private void checkoutWithAssets(List<Asset> assets) {
        String apiKey = API_KEY_TEST;
        KitePrintSDK.Environment env = KitePrintSDK.Environment.TEST;
        if (environmentSwitch.isChecked()) {
            apiKey = API_KEY_LIVE;
            env = KitePrintSDK.Environment.LIVE;
        }

        if (apiKey.equals("REPLACE_ME")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set API Keys");
            builder.setMessage("Please set your Kite API keys at the top of the SampleApp's MainActivity.java. You can find these by logging into https://www.kite.ly.");
            builder.setPositiveButton("OK", null);
            builder.show();
            return;
        }

        KitePrintSDK.initialize(apiKey, env, getApplicationContext());

        Intent intent = new Intent(this, ProductHomeActivity.class);
        intent.putExtra(CheckoutActivity.EXTRA_PRINT_ASSETS, (Serializable) assets);
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

}
