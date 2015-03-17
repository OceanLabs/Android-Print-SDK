package ly.kite.printshop;

import android.graphics.Point;
import android.os.Bundle;
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

import ly.kite.R;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.Template;

public class ProductOverviewActivity extends FragmentActivity {
    private Template template;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_overview);

        template = (Template) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_TEMPLATE);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ProductOverviewAdapter(getSupportFragmentManager(), template);
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

        public ProductOverviewAdapter(FragmentManager fm, Template template) {
            this(fm);
            this.template = template;
        }

        public ProductOverviewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment(template, position);
        }

        @Override
        public int getCount() {
            return template.getProductPhotographyURLs().size();
        }
    }

    public static class ScreenSlidePageFragment extends Fragment {
        private Template template;
        int position;

        public ScreenSlidePageFragment (Template template, int position){
            this.template = template;
            this.position = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_product_overview, container, false);

            ImageView imageView = ((ImageView) rootView.findViewById(R.id.overview_imageView));
            Picasso.with(container.getContext()).load(template.getProductPhotographyURLs().get(position)).into(imageView);

            return rootView;
        }
    }
}