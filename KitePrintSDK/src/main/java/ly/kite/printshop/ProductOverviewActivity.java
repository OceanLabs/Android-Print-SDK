package ly.kite.printshop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import ly.kite.R;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.Asset;
import ly.kite.print.Template;

public class ProductOverviewActivity extends FragmentActivity {
    private Template template;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<Asset> assets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_overview);

        template = (Template) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_TEMPLATE);
        assets = (ArrayList<Asset>) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_ASSETS);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ProductOverviewAdapter(getSupportFragmentManager(), template, assets);
        mPager.setAdapter(mPagerAdapter);

        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        int width = p.x / 3;

        TextView shippingLabel = (TextView) findViewById(R.id.shippingLabel);
        shippingLabel.setText("FREE POSTAGE");
        shippingLabel.getLayoutParams().width = width;

        TextView priceLabel = (TextView) findViewById(R.id.priceLabel);
        priceLabel.setText(template.getCost("GBP").toString()); //TODO
        priceLabel.getLayoutParams().width = width;

        TextView dimensionsLabel = (TextView) findViewById(R.id.dimensionsLabel);
        dimensionsLabel.getLayoutParams().width = width;
        dimensionsLabel.setText("" + template.getSizeCm().x + " x " + template.getSizeCm().y);

        WhiteSquare whiteSquare = (WhiteSquare) findViewById(R.id.whiteSquare);
        whiteSquare.getLayoutParams().width = width;

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setTitle(template.getName());
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ProductOverviewAdapter extends FragmentStatePagerAdapter {
        private Template template;
        private ArrayList<Asset> assets;

        public ProductOverviewAdapter(FragmentManager fm, Template template, ArrayList<Asset> assets) {
            this(fm);
            this.template = template;
            this.assets = assets;
        }

        public ProductOverviewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment(template, position, assets);
        }

        @Override
        public int getCount() {
            return template.getProductPhotographyURLs().size();
        }
    }

    public static class ScreenSlidePageFragment extends Fragment {
        private Template template;
        private ArrayList<Asset> assets;
        private int position;

        public ScreenSlidePageFragment (Template template, int position, ArrayList<Asset> assets){
            this.template = template;
            this.position = position;
            this.assets = assets;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_product_overview, container, false);

            final ViewGroup vg = container;
            ImageView imageView = ((ImageView) rootView.findViewById(R.id.overview_imageView));
            Picasso.with(container.getContext()).load(template.getProductPhotographyURLs().get(position)).into(imageView);

            final Activity activity = getActivity();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, PhotoEditActivity.class);
                    intent.putExtra(CheckoutActivity.EXTRA_PRINT_TEMPLATE, (Parcelable)template);
                    intent.putExtra(CheckoutActivity.EXTRA_PRINT_ASSETS, assets);
                    intent.putExtra("photo", (Serializable)assets.get(0));
                    startActivityForResult(intent, 0);
                }
            });

            return rootView;
        }
    }
}