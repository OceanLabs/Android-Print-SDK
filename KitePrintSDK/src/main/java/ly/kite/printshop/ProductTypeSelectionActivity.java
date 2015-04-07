package ly.kite.printshop;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ly.kite.R;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.Asset;
import ly.kite.print.PrintOrder;
import ly.kite.print.Template;

/**
 * Created by kostas on 2/19/15.
 */
public class ProductTypeSelectionActivity extends Activity {

    private static final int REQUEST_CODE_CHECKOUT = 2;

    private ProductTypeAdapter productTypeAdapter;
    private String templateClass;
    private ArrayList<Asset> assets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        templateClass = (String) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_TEMPLATE_CLASS);
        assets = (ArrayList<Asset>) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_ASSETS);

        setContentView(R.layout.activity_product_type_selection);

        productTypeAdapter = new ProductTypeAdapter();
        productTypeAdapter.setTemplates(filterTemplates(Template.getTemplates()));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(productTypeAdapter, assets))
                    .commit();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public List<Template> filterTemplates(List<Template> templates){
        ArrayList<Template> filteredTemplates = new ArrayList<Template>();
        for (int i = 0; i < templates.size(); i++){
            Template t = templates.get(i);
            if (t.getCoverPhotoURL() == null || t.getTemplateUI() == Template.TemplateUI.NA || t.getCoverPhotoURL() == null || t.getCoverPhotoURL().isEmpty() || t.getCoverPhotoURL().equalsIgnoreCase("null")){
                continue;
            }

            if (t.getTemplateClass() != null && t.getTemplateClass().equals(this.templateClass)){
                filteredTemplates.add(t);
            }

        }
        return filteredTemplates;
    }

    public static class PlaceholderFragment extends Fragment{
        private final ProductTypeAdapter adapter;
        private ArrayList<Asset> assets;

        public PlaceholderFragment(ProductTypeAdapter adapter, ArrayList<Asset> assets){
            this.adapter = adapter;
            this.assets = assets;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_product_type_selection, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ListView productHomeList = (ListView) view.findViewById(R.id.list_view_product_type_selection);
            productHomeList.setAdapter(adapter);


            productHomeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long position) {
                    Intent intent = new Intent(getActivity(), ProductOverviewActivity.class);
                    intent.putExtra(CheckoutActivity.EXTRA_PRINT_TEMPLATE, (Parcelable) adapter.getItem(i));
                    intent.putExtra(CheckoutActivity.EXTRA_PRINT_ASSETS, (Serializable) assets);
                    startActivityForResult(intent, REQUEST_CODE_CHECKOUT);
                }
            });

        }
    }

    private static class ProductTypeAdapter extends BaseAdapter {

        private List<Template> templates;

        public void setTemplates(List<Template> templates){
            this.templates = templates;
            notifyDataSetInvalidated();
        }

        @Override
        public int getCount(){
            return templates.size();
        }

        @Override
        public View getView(int position, final View convertView, final ViewGroup viewGroup) {
            View v = convertView;
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.product_home_list_item, null);
            }

            Template template = templates.get(position);

            ImageView imageView = ((ImageView) v.findViewById(R.id.productCoverImageView));
            Picasso.with(viewGroup.getContext()).load(template.getCoverPhotoURL()).into(imageView);

            TextView textView = ((TextView) v.findViewById(R.id.productNameLabel));
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(template.getLabelColor());
            textView.setText(template.getTemplateType());

            return v;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i){
            return templates.get(i);

        }
    }

}
