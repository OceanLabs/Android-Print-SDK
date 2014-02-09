package co.oceanlabs.pssdk;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import co.oceanlabs.pssdk.address.Address;
import co.oceanlabs.pssdk.address.AddressSearchRequest;
import co.oceanlabs.pssdk.address.AddressSearchRequestListener;
import co.oceanlabs.pssdk.address.Country;
import co.oceanlabs.pssdk.asset.Asset;
import co.oceanlabs.pssdk.asset.AssetGetBytesLengthListener;
import co.oceanlabs.pssdk.asset.AssetGetBytesListener;
import co.oceanlabs.pssdk.asset.AssetUploadRequest;
import co.oceanlabs.pssdk.asset.AssetUploadRequestListener;

public class MainActivity extends Activity {

    private static final int SELECT_PICTURE = 1;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PSPrintSDK.initialize("7c575489215d24bfb3d3df3b6df053eac83542da");

        AddressSearchRequest req = new AddressSearchRequest();
        req.search("6 Leda Cottages", Country.getInstance("GBR"), new AddressSearchRequestListener() {
            @Override
            public void onMultipleChoices(List<Address> options) {
                for (Address addr : options) {
                    System.out.println("option: " + addr.toString());
                }
            }

            @Override
            public void onUniqueAddress(Address address) {
                    System.out.println("unique: " + address.toString());
            }

            @Override
            public void onError(Exception error) {
                System.out.println("address search error: " + error.toString());
            }
        });

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        imageView = (ImageView) findViewById(R.id.image_view);

    }

    public void onCropButtonClicked(View view) {
        Log.i("pssdk2", "Crop button clicked");
    }

    public void onGalleryButtonClicked(View view) {
        Log.i("pssdk2", "Gallery button clicked");
        // in onCreate or any event where your want the user to
        // select a file
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(selectedImageUri);

                imageView.setImageURI(selectedImageUri);

                Asset uriAsset = new Asset(selectedImageUri);
                Asset pathAsset = new Asset(selectedImagePath);
                Asset resourceAsset = new Asset(R.drawable.c1);

                ArrayList<Asset> assets = new ArrayList<Asset>();
                assets.add(uriAsset);
                assets.add(resourceAsset);
                assets.add(pathAsset);

                AssetUploadRequest uploadRequest = new AssetUploadRequest();
                uploadRequest.uploadAssets(assets, this, new AssetUploadRequestListener() {
                    @Override
                    public void onProgress(AssetUploadRequest req, int totalAssetsUploaded, int totalAssetsToUpload, long bytesWritten, long totalAssetBytesWritten, long totalAssetBytesExpectedToWrite) {
                        Log.i("pssdk2", String.format("Upload progress for asset %d/%d %d%% complete", totalAssetsUploaded, totalAssetsToUpload, (100 * totalAssetBytesWritten) / totalAssetBytesExpectedToWrite));
                    }

                    @Override
                    public void onUploadComplete(AssetUploadRequest req, List<Asset> assets) {
                        Log.i("pssdk2", "Successfully uploaded " + assets.size() + " assets");
                        for (Asset a : assets) {
                            Log.i("pssdk2", "Asset " + a.getId() + " with url: " + a.getPreviewURL());
                        }
                    }

                    @Override
                    public void onError(AssetUploadRequest req, Exception error) {
                        Log.i("pssdk2", "Failed to upload asset with error: " + error.toString());
                    }
                });
            }
        }
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
