package co.oceanlabs.sample;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import ly.kite.print.print.Asset;
import ly.kite.print.print.KitePrintSDK;
import ly.kite.print.print.PrintJob;
import ly.kite.print.print.PrintOrder;
import ly.kite.print.address.Address;
import ly.kite.print.checkout.CheckoutActivity;

public class MainActivity extends Activity {

    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CODE_CHECKOUT = 2;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KitePrintSDK.initialize("ba171b0d91b1418fbd04f7b12af1e37e42d2cb1e", KitePrintSDK.Environment.TEST);
        imageView = (ImageView) findViewById(R.id.image_view);
    }

    public void onGalleryButtonClicked(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onCheckoutButtonClicked(View view) {
        ArrayList<Asset> assets = new ArrayList<Asset>();
        assets.add(new Asset(R.drawable.instagram1));

        try {
            URL remoteImageURL = new URL("http://psps.s3.amazonaws.com/sdk_static/4.jpg");
            assets.add(new Asset(remoteImageURL));
        } catch (Exception ex) {}

        PrintOrder printOrder = new PrintOrder();
        printOrder.addPrintJob(PrintJob.createMagnetsPrintJob(assets));

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
        } else {
//            if (resultCode == RESULT_OK) {
//                if (requestCode == SELECT_PICTURE) {
//                    Uri selectedImageUri = data.getData();
//                    imageView.setImageURI(selectedImageUri);
//
//                }
//            }
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
