package co.oceanlabs.sample;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import co.oceanlabs.pssdk.Asset;
import co.oceanlabs.pssdk.PSPrintSDK;
import co.oceanlabs.pssdk.PrintJob;
import co.oceanlabs.pssdk.PrintOrder;
import co.oceanlabs.pssdk.PrintOrderSubmissionListener;
import co.oceanlabs.pssdk.payment.PayPalCard;
import co.oceanlabs.pssdk.payment.PayPalCardChargeListener;
import co.oceanlabs.pssdk.payment.PayPalCardVaultStorageListener;
import co.oceanlabs.sample.R;
import co.oceanlabs.pssdk.address.Address;
import co.oceanlabs.pssdk.checkout.CheckoutActivity;

public class MainActivity extends Activity {

    private static final int SELECT_PICTURE = 1;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PSPrintSDK.initialize("ba171b0d91b1418fbd04f7b12af1e37e42d2cb1e");
        imageView = (ImageView) findViewById(R.id.image_view);

        if (PayPalCard.getLastUsedCard(this) != null) {
            PayPalCard.getLastUsedCard(this).chargeCard(PayPalCard.Environment.SANDBOX, new BigDecimal("9.99"), PayPalCard.Currency.GBP, "description", new PayPalCardChargeListener() {

                @Override
                public void onChargeSuccess(PayPalCard card, String proofOfPayment) {
                    Log.i("pssdk2", "Successfully charged card with proof: " + proofOfPayment);
                }

                @Override
                public void onError(PayPalCard card, Exception ex) {
                    Log.i("pssdk2", "Failed to charge card with error: " + ex.toString());
                }
            });
        } else {
            final PayPalCard card = new PayPalCard(PayPalCard.CardType.VISA, "4012888888881881", 12, 15, "123");
            card.storeCard(PayPalCard.Environment.SANDBOX, new PayPalCardVaultStorageListener() {
                @Override
                public void onStoreSuccess(PayPalCard card) {
                    Log.i("pssdk2", "Successfully stored PayPal");
                    card.saveAsLastUsedCard(MainActivity.this);
                }

                @Override
                public void onError(PayPalCard card, Exception ex) {
                    Log.i("pssdk2", "Failed to store PayPal card: " + ex.toString());
                }
            });
        }
    }

    public void onGalleryButtonClicked(View view) {
        // in onCreate or any event where your want the user to
        // select a file
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onCheckoutButtonClicked(View view) {
        ArrayList<Asset> assets = new ArrayList<Asset>();
        assets.add(new Asset(R.drawable.instagram1));
        assets.add(new Asset(R.drawable.instagram2));
        assets.add(new Asset(R.drawable.instagram3));
        assets.add(new Asset(R.drawable.instagram4));

        try {
            URL remoteImageURL = new URL("http://www.catster.com/files/original.jpg");
            assets.add(new Asset(remoteImageURL));
        } catch (Exception ex) {}

        PrintOrder printOrder = new PrintOrder();
        printOrder.addPrintJob(PrintJob.createMagnetsPrintJob(assets));
        printOrder.setShippingAddress(Address.getPSTeamAddress());

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder);
        intent.putExtra(CheckoutActivity.EXTRA_PRINT_ENVIRONMENT, CheckoutActivity.ENVIRONMENT_STAGING);
        intent.putExtra(CheckoutActivity.EXTRA_PRINT_API_KEY, "ba171b0d91b1418fbd04f7b12af1e37e42d2cb1e");
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                imageView.setImageURI(selectedImageUri);
                try {
                    createAndSubmitPrintJob(selectedImageUri);
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex); // will never happen ;)
                }
            }
        }
    }

    public void createAndSubmitPrintJob(Uri imageUri) throws MalformedURLException {
        URL remoteImageURL = new URL("http://www.catster.com/files/original.jpg");
        ArrayList<Asset> assets = new ArrayList<Asset>();
        assets.add(new Asset(imageUri));               // 1. Example Asset from Android Uri
        assets.add(new Asset(getPath(imageUri)));      // 2. Example Asset from file path
        assets.add(new Asset(R.drawable.instagram1));  // 3. Example Asset from Android resource id
        assets.add(new Asset(remoteImageURL));         // 4. Example Asset from remote URL

        PrintOrder printOrder = new PrintOrder();
        printOrder.addPrintJob(PrintJob.createMagnetsPrintJob(assets));

        /*
         * XXX: You won't ever need to do the following, it's taking care by the checkout activities but it's just here for completeness
         */
        printOrder.setProofOfPayment("PAY-fake-proof");
        printOrder.setShippingAddress(Address.getPSTeamAddress());

        Log.i("psprintstudio", "presave");
        printOrder.saveToHistory(this);
        Log.i("psprintstudio", "postsave");

        List<PrintOrder> orders = PrintOrder.getPrintOrderHistory(this);
        Log.i("psprintstudio", orders.size() + "orders in history");
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
