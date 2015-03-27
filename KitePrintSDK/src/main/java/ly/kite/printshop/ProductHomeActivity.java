package ly.kite.printshop;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ly.kite.R;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.PrintOrder;
import ly.kite.print.Template;

/**
 * Created by kostas on 2/19/15.
 */
public class ProductHomeActivity extends Activity {

    private static final int REQUEST_CODE_CHECKOUT = 2;

    private ProductHomeAdapter productHomeAdapter;
    private PrintOrder printOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        printOrder = (PrintOrder) getIntent().getSerializableExtra(CheckoutActivity.EXTRA_PRINT_ORDER);

        setContentView(R.layout.activity_product_home);

        productHomeAdapter = new ProductHomeAdapter();
        productHomeAdapter.setTemplates(Template.getTemplates());

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(productHomeAdapter, printOrder))
                    .commit();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class PlaceholderFragment extends Fragment{
        private final ProductHomeAdapter adapter;
        private PrintOrder printOrder;

        public PlaceholderFragment(ProductHomeAdapter adapter, PrintOrder printOrder){
            this.adapter = adapter;
            this.printOrder = printOrder;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_product_home, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ListView productHomeList = (ListView) view.findViewById(R.id.list_view_product_home);
            productHomeList.setAdapter(adapter);


            productHomeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long position) {
                    Intent intent = new Intent(getActivity(), ProductOverviewActivity.class);
                    intent.putExtra(CheckoutActivity.EXTRA_PRINT_TEMPLATE, (Parcelable) adapter.getItem(i));
                    startActivityForResult(intent, REQUEST_CODE_CHECKOUT);
                }
            });

        }
    }

    private static class ProductHomeAdapter extends BaseAdapter {

        private LinkedHashMap<String, List<Template>> templatesPerClass;

        public void setTemplates(List<Template> templates){
            this.templatesPerClass = filterTemplates(templates);
            notifyDataSetInvalidated();
        }

        public LinkedHashMap<String, List<Template>> filterTemplates(List<Template> templates){
            LinkedHashMap<String, List<Template>> templatesPerClass = new LinkedHashMap<String, List<Template>>();
            for (int i = 0; i < templates.size(); i++){
                Template t = templates.get(i);
                if (t.getCoverPhotoURL() == null || t.getTemplateUI() == Template.TemplateUI.NA){
                    continue;
                }

                if (!templatesPerClass.keySet().contains(t.getTemplateClass())){
                    templatesPerClass.put(t.getTemplateClass(), new ArrayList<Template>());
                    templatesPerClass.get(t.getTemplateClass()).add(t);
                }
                else{
                    templatesPerClass.get(t.getTemplateClass()).add(t);
                }

            }
            return templatesPerClass;
        }

        @Override
        public int getCount(){
            return templatesPerClass.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.product_home_list_item, null);
            }

            Template template = templatesPerClass.get(templatesPerClass.keySet().toArray()[position]).get(0);

            ImageView imageView = ((ImageView) v.findViewById(R.id.productCoverImageView));
            if (template.getClassPhotoURL() != null && !template.getClassPhotoURL().isEmpty() ) {
                Picasso.with(viewGroup.getContext()).load(template.getClassPhotoURL()).into(imageView);
            }
            else{
                Picasso.with(viewGroup.getContext()).load(template.getCoverPhotoURL()).into(imageView);
            }

            TextView textView = ((TextView) v.findViewById(R.id.productNameLabel));
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(template.getLabelColor());
            textView.setText(template.getTemplateClass());

            return v;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i){
            return templatesPerClass.get(i);

        }
    }

}
