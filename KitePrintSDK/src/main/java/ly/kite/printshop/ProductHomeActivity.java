package ly.kite.printshop;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ly.kite.R;
import ly.kite.print.Template;

/**
 * Created by kostas on 2/19/15.
 */
public class ProductHomeActivity extends Activity {

    private ProductHomeAdapter productHomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_home);

        productHomeAdapter = new ProductHomeAdapter();
        productHomeAdapter.setTemplates(Template.getTemplates());

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(productHomeAdapter))
                    .commit();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class PlaceholderFragment extends Fragment{
        private final ProductHomeAdapter adapter;

        public PlaceholderFragment(ProductHomeAdapter adapter){
            this.adapter = adapter;
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

//            productHomeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long position) {
//                    final Address address = (Address) adapter.getItem((int) position);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle(address.toString())
//                            .setItems(new String[] {"Edit Address", "Delete Address"}, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    if (i == 0) {
//                                        Intent intent = new Intent(getActivity(), AddressEditActivity.class);
//                                        intent.putExtra(AddressEditActivity.EXTRA_ADDRESS, (Parcelable) address);
//                                        startActivityForResult(intent, REQUEST_CODE_ADD_ADDRESS);
//                                    } else if (i == 1) {
//                                        address.deleteFromAddressBook(getActivity());
//                                        adapter.setAddresses(Address.getAddressBook(getActivity()));
//                                    }
//                                }
//                            });
//                    builder.create().show();
//                    return true;
//                }
//            });

//            productHomeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long position) {
//                    Address addr = (Address) adapter.getItem((int) position);
//                    Intent data = new Intent();
//                    data.putExtra(EXTRA_ADDRESS, (Parcelable) addr);
//                    getActivity().setResult(Activity.RESULT_OK, data);
//                    getActivity().finish();
//
//                }
//            });

//            TextView empty = (TextView) view.findViewById(R.id.empty);
//            productHomeList.setEmptyView(empty);
        }
    }

    private static class ProductHomeAdapter extends BaseAdapter {

        private List<Template> templates;

        public void setTemplates(List<Template> templates){
            this.templates = filterTemplates(templates);
            notifyDataSetInvalidated();
        }

        public List<Template> filterTemplates(List<Template> templates){
            ArrayList<Template> templateArrayList = new ArrayList<Template>(templates);
            boolean haveAtLeastOnePoster = false;
            boolean haveAtLeastOneFrame = false;
            for (int i = 0; i < templates.size(); i++){
                Template t = templates.get(i);
                if (t.getCoverPhotoURL() == null || t.getTemplateClass() == Template.TemplateClass.NA){
                    templateArrayList.remove(t);
                }

                if (t.getTemplateClass() == Template.TemplateClass.Frame){
                    if (haveAtLeastOneFrame){
                        templateArrayList.remove(t);
                    }
                    else{
                        haveAtLeastOneFrame = true;
                    }
                }

                if (t.getTemplateClass() == Template.TemplateClass.Poster){
                    if (haveAtLeastOnePoster){
                        templateArrayList.remove(t);
                    }
                    else{
                        haveAtLeastOnePoster = true;
                    }
                }

            }
            return templateArrayList;
        }

        @Override
        public int getCount(){
            return templates.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
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
            textView.setText(template.getName());

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
