package ly.kite.journey.shipping;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ly.kite.R;
import ly.kite.address.Country;
import ly.kite.catalogue.MultipleCurrencyAmounts;
import ly.kite.catalogue.Product;
import ly.kite.journey.basket.BasketActivity;

import static ly.kite.R.drawable.check_on;


/**
 * Created by andrei on 03/07/2017.
 */

public class ShippingMethod extends BasketActivity{

    private ListView  mListView;
    private ShippingAdapter mShippingAdapter;
    private List<ArrayList> mItemList;
    private ArrayList<Integer> mPositionIndex = new ArrayList<Integer>();
    ArrayList<ShippingMethodItem> list = new ArrayList<>();
    //TODO declare all variables here

    @Override
    public void onCreate( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.shipping_method);


        String[] productName;
        String[] productType;
        String[] product;
        String[] quantity;
        JSONObject shippingInfo = new JSONObject();


        int noOfItems=0;
        String local = Locale.getDefault().toString();
        Locale loc = Locale.getDefault();
        Country country = Country.getInstance(loc);
        Country shippingCountry;
        String jsonCountryCode;
        JSONArray shippingCosts = new JSONArray();
        JSONObject reggionMapping;


        int k=0;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                    productName = productType = product = quantity = null;
            } else {
                shippingCountry= Country.getInstance(extras.getString("shippingCountry"));
                noOfItems = extras.getInt("noOfItems");

                for(int i= 0 ; i < noOfItems;i++)
                {

                    try {
                        shippingInfo = new JSONObject(getIntent().getStringExtra("shippingMethods"+i));
                        reggionMapping = new JSONObject(getIntent().getStringExtra("regionMapping"+i));
                        jsonCountryCode = reggionMapping.getString(shippingCountry.iso3Code());
                        shippingCosts = shippingInfo.getJSONObject(jsonCountryCode).getJSONArray("shipping_classes");
                        for(int j=0;j<=shippingCosts.length();j++) {
                            k++;
                            mPositionIndex.add(i);
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    list.add(new ShippingMethodItem(extras.getString("productName"+i),extras.getInt("orderQuantity"+i),i,
                            shippingCosts, shippingCountry.iso3CurrencyCode()));
                }
            }
        } else {//Todo complete code below
            noOfItems = (Integer) savedInstanceState.getSerializable("noOfItems");
            shippingCountry= Country.getInstance((String) savedInstanceState.getSerializable(("shippingCountry")));
            for(int i= 0 ; i < noOfItems;i++)
            {
                try {
                    shippingInfo = new JSONObject( (String) savedInstanceState.getSerializable("shippingMethods"+i));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                list.add(new ShippingMethodItem((String) savedInstanceState.getSerializable("productName"+i),
                        (int) savedInstanceState.getInt("orderQuantity"+i) ,i,
                        shippingCosts, shippingCountry.iso3CurrencyCode()));
            }
        }

        mListView  = (ListView)findViewById(R.id.shipping_method_listview);

        mShippingAdapter = new ShippingAdapter(this, list);

        mListView.setAdapter(mShippingAdapter);




        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),""+position,Toast.LENGTH_SHORT);
                Log.wtf("clicked",""+position);


            }
        }); */




    }

    @Override
    public void onSaveInstanceState(Bundle outState) {//don't forget to change in onCreate to outState
    }

    public class ShippingMethodItem {

        private String itemName;
        private int itemQuantity;
        private int noOfOptions;
        private JSONArray shipCosts;
        private int currentShippingMethos;
        private int displayIndex;
        private String  currencyCode;
        private boolean showTitle = true;
        private int shippingOptionIndex = 0;
        private boolean takenIntoAccount;


        public ShippingMethodItem(String name, int quantity,int elementIndex,JSONArray shippingCosts,String currencyCode) {
            this.itemName = name;
            this.itemQuantity = quantity;
            this.shipCosts = shippingCosts;
            this.currencyCode = currencyCode;

            this.noOfOptions = shippingCosts.length();
            this.currentShippingMethos = noOfOptions;
            this.displayIndex = noOfOptions+1;
            this.takenIntoAccount = false;

            //todo DON'T FORGET TO PASS THE json WITH THE COUNTRY ALLREADY SELECTED
            //todo here get the totalShipping methods info
        }

        public String getItemName() {
            return this.itemName;
        }

        public int getItemQuantity() {
            return itemQuantity;
        }

        public JSONArray getShipCosts() { return this.shipCosts; }

        public int getNoOfOptions() { return  this.noOfOptions; }

        public int getShippingOptionIndex() { return  this.shippingOptionIndex; }

        public void setShippingOptionIndex(int newIndex) { shippingOptionIndex = newIndex; }

        public boolean getTakenIntoAccount() { return takenIntoAccount; }

        public boolean setTakenIntoAccount() { return false;}


        public int getCurrentShippingMethos(){
            currentShippingMethos = this.currentShippingMethos-1;
            int result = this.noOfOptions-this.currentShippingMethos-1;
            if(result==noOfOptions-1)
                currentShippingMethos = noOfOptions;
            return  result;
        }

        public boolean getShowTitle() { return this.showTitle; }


        public String getCurrencyCode(){ return currencyCode; }

    }

    public static class ShippingAdapter extends BaseAdapter {
        public double totalshippingPrice;
        private MultipleCurrencyAmounts mShipCost;
        public int place = 0;
        private Context context;
        private ArrayList<ShippingMethodItem> items;
        private int totalShippingOptions =2;
        public String totalShippingCost;


        public ShippingAdapter(Context context, ArrayList<ShippingMethodItem> items) {
            this.context = context;
            this.items = items;
        }

        public String getTotalShippingCost(){return totalShippingCost;}

        @Override
        public int getCount() {
            int count=0;
            //int ceapa = items.size()*(currentItem.getNoOfOptions()+1); //returns total item in the list
            for(int i=0;i<items.size();i++)
            {
                count+=items.get(i).getNoOfOptions()+1;//+1 for including the title

            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            //ShippingMethodItem currentItem = (ShippingMethodItem) getItem(0);//todo it crashes , why?
            //int ceapa = position/(totalShippingOptions+1);
            //Log.wtf("index",""+ceapa);
            //return items.get(ceapa); //returns the item at the specified position
            //int q =positionIndex[position];


            for (int i = 0; i < items.size(); ++i) {
                ShippingMethodItem item = items.get(i);
                if (position < item.getNoOfOptions() + 1) {
                    return item;
                } else {
                    position -= item.getNoOfOptions() + 1;
                }
            }

            throw new IllegalStateException("Will never get here");
            //return items.get(mPositionIndex.get(position));

            /*
            int i=0;
            while(items.get(i).getDisplayIndex() == 0) {
                i++;
                if (i>=items.size())
                    return items.get(1);

            }
                return items.get(i);
                */

        }

        @Override
        public long getItemId(int position) {
            return position;
            //return 0L;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            /*ShippingMethodItem currentItem = (ShippingMethodItem) getItem(position);

            if(currentItem.getShowTitle()) {
                return 0;
            }
            else
                return 1;
                */
            //int q = mPositionIndex.get(position);
            for (int i = 0; i < items.size(); ++i) {
                ShippingMethodItem item = items.get(i);
                if (position == 0) {
                    return 0;
                } else if (position < item.getNoOfOptions() + 1) {
                    return 1;
                } else {
                    position -= item.getNoOfOptions() + 1;
                }
            }
            throw new IllegalStateException("Will never get here");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ShippingMethodItem currentItem = (ShippingMethodItem) getItem(position);
            int viewType = this.getItemViewType(position);
            LayoutInflater vi;

            if(viewType == 0)
            {
                vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewHolder1 title;
                //if(convertView == null)
                //{

                    convertView = vi.inflate(R.layout.list_item_shipping_method_title, null);
                    title = new ViewHolder1(convertView);

                //}
                //else {
               //     title = (ViewHolder1) convertView.getTag();
               // }




                title.itemName.setText(currentItem.getItemQuantity()+" x "+currentItem.getItemName());




                //convertView.setTag(title);
                return convertView;


            }
            else if(viewType == 1){
                vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final ViewHolder2 info;

               // if (convertView == null) {

                    convertView = vi.inflate(R.layout.list_item_shipping_methods, null);
                    info = new ViewHolder2(convertView);




                //} else {
                  //  info = (ViewHolder2) convertView.getTag();
                //}


                try {
                    final int shippingMethodIndex = currentItem.getCurrentShippingMethos();
                    JSONObject temp = currentItem.getShipCosts().getJSONObject(shippingMethodIndex);
                    info.shippingClassName.setText(temp.getString("class_name"));
                    info.shippingTime.setText(temp.getString("min_delivery_time")+" - "+temp.getString("max_delivery_time")+" days");
                    JSONArray costArray = temp.getJSONArray("costs");
                    String itemCost ="err";

                    if(currentItem.getShippingOptionIndex() == shippingMethodIndex)
                        info.optionSelected.setVisibility(View.VISIBLE);
                    else
                        info.optionSelected.setVisibility(View.INVISIBLE);


                    mShipCost = new MultipleCurrencyAmounts(costArray);
                    final String ceapa = mShipCost.getDisplayAmountWithFallbackMultipliedBy("USD",currentItem.getItemQuantity(),Locale.getDefault());

                    if(!currentItem.getTakenIntoAccount())
                        totalShippingCost = ceapa;




                    /*for(int i=0;i<costArray.length();i++)
                    {
                        JSONObject temp2 = costArray.getJSONObject(i);
                        String jsonCurrencyCode = temp2.getString("currency");
                        if(jsonCurrencyCode.equals(currentItem.getCurrencyCode()))
                            itemCost = temp2.getString("amount");
                    }*/

                    info.shippingPrice.setText(ceapa);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            info.optionSelected.setVisibility(View.VISIBLE);
                            currentItem.setShippingOptionIndex(shippingMethodIndex);
                            totalShippingCost = ceapa;
                            notifyDataSetChanged();
                        }
                    });


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                //convertView.setTag(info);
                return convertView;
            }
            else return null;

        }
        //ViewHolder inner class
       private class ViewHolder1 {
            TextView itemName;

            public ViewHolder1(View view) {
                itemName = (TextView) view.findViewById(R.id.nameText0);
            }
        }

        private class ViewHolder2 {
            TextView shippingClassName;
            TextView shippingTime;
            TextView shippingPrice;
            ImageView optionSelected;

            public ViewHolder2(View view) {
                 shippingClassName = (TextView) view.findViewById(R.id.shipping_method_class);
                 shippingTime = (TextView) view.findViewById(R.id.shipping_method_time);
                 shippingPrice = (TextView) view.findViewById(R.id.shipping_method_price);
                 optionSelected = (ImageView) view.findViewById(R.id.shipping_method_selected);
                 optionSelected.setImageResource(R.drawable.check_on);
            }
        }

    }

}
