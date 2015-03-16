package ly.kite.printshop;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import ly.kite.R;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.PrintOrder;
import ly.kite.print.Template;

/**
 * Created by kostas on 3/16/15.
 */
public class ProductOverviewActivity extends Activity {
    private Template template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        template = (Template) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_TEMPLATE);

        setContentView(R.layout.activity_product_overview);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.overview_container, new PlaceholderFragment(template, new PagesAdapter(template)))
                    .commit();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private Template template;
        private PagesAdapter adapter;

        public PlaceholderFragment(Template template, PagesAdapter adapter){
            this.adapter = adapter;
            this.template = template;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_product_overview, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ViewPager pageContent = (ViewPager) view.findViewById(R.id.pager);
            pageContent.setAdapter(adapter);
        }
    }

    private static class PagesAdapter extends PagerAdapter {
        private Template template;

        public PagesAdapter (Template template){
            this.template = template;
        }

        @Override
        public boolean isViewFromObject (View view, Object object){
            return false;
        }

        @Override
        public int getCount(){
            return template.getProductPhotographyURLs().size();
        }

        @Override
        public Object instantiateItem (ViewGroup container, int position){
            LayoutInflater li = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View contentPage = li.inflate(R.layout.product_overview_content_item, null);
            container.addView(contentPage);

            ImageView imageView = ((ImageView) contentPage.findViewById(R.id.overview_imageView));
            Picasso.with(container.getContext()).load(template.getProductPhotographyURLs().get(position)).into(imageView);

            return contentPage;
        }
    }
}
