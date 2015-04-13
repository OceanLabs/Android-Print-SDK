package ly.kite.printshop;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.edmodo.cropper.CropImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ly.kite.R;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.Asset;
import ly.kite.print.AssetGetBytesListener;
import ly.kite.print.PrintOrder;
import ly.kite.print.Template;

/**
 * Created by kostas on 3/30/15.
 */
public class SingleImageProductReviewActivity extends Activity{
    private Template template;
    private ArrayList<Asset> assets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        template = (Template) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_TEMPLATE);
        assets = (ArrayList<Asset>) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_ASSETS);

        setContentView(R.layout.activity_single_image_product_review);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(template, assets))
                    .commit();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class PlaceholderFragment extends Fragment {
        Template template;
        private ArrayList<Asset> assets;

        public PlaceholderFragment(Template template, ArrayList<Asset> assets){
            this.template = template;
            this.assets = assets;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_single_image_product_review, container, false);

            final CropImageView cropImageView = (CropImageView) rootView.findViewById(R.id.CropImageView);
            Asset asset = assets.get(0);
            asset.getBytes(rootView.getContext(), new AssetGetBytesListener() {
                @Override
                public void onBytes(Asset asset, byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    cropImageView.setImageBitmap(image);

                    if (template.getSizePx().x != 0){
                        Log.v("", "Size:" + (int) template.getSizePx().x + ", " + (int) template.getSizePx().y);
                        cropImageView.setAspectRatio(1, 1);
                    }
                    else {
                        Log.v("", "Size:" + (int) template.getSizeCm().x + ", " + (int) template.getSizeCm().y);
                        cropImageView.setAspectRatio((int) template.getSizeCm().x, (int) template.getSizeCm().y);
                    }

                    cropImageView.setFixedAspectRatio(true);

                    cropImageView.setGuidelines(0);
                }

                @Override
                public void onError(Asset asset, Exception ex) {

                }
            });

            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {


        }
    }
}
