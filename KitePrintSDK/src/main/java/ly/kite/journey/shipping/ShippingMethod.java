package ly.kite.journey.shipping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import ly.kite.R;
import ly.kite.address.Country;
import ly.kite.catalogue.MultipleCurrencyAmounts;
import ly.kite.catalogue.Product;
import ly.kite.journey.basket.BasketActivity;


/**
 * Created by andrei on 03/07/2017.
 */

public class ShippingMethod extends BasketActivity {

  private ListView mListView;
  private ShippingAdapter mShippingAdapter;
  ArrayList<ShippingMethodItem> mList = new ArrayList<>();
  private ArrayList<Integer> mShippingClasses = new ArrayList<Integer>();
  private ArrayList<Integer> mShippingClassIndex = new ArrayList<Integer>();

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.screen_shipping_method);

    Product product;
    JSONObject shippingInfo = new JSONObject();


    int noOfItems = 0;
    int selectedShippingClass = 123;// value for N/A
    Country shippingCountry;
    String jsonCountryCode;
    JSONArray shippingCosts = new JSONArray();
    JSONObject reggionMapping;

    Bundle extras = getIntent().getExtras();
    if (extras == null) {
      product = null;
    } else {
      shippingCountry = Country.getInstance(extras.getString("shippingCountry"));
      noOfItems = extras.getInt("noOfItems"); // get number of items from basket

      int k = 0; // current index number for mShippingClassIndex
      for (int i = 0; i < noOfItems; i++) {
        product = (Product) extras.get("product" + i);

        try {

          shippingInfo = new JSONObject(product.getShippingMethods());
          reggionMapping = new JSONObject(product.getRegionMapping());
          jsonCountryCode = reggionMapping.getString(shippingCountry.iso3Code());
          shippingCosts = shippingInfo.getJSONObject(jsonCountryCode).getJSONArray("shipping_classes");
          selectedShippingClass = extras.getInt("selectedShippingClass" + i);

          if (selectedShippingClass == 123) { //if shipping class N/A value
            selectedShippingClass = shippingCosts.getJSONObject(0).getInt("id");//get the first information from shipping json object
          }

          for (int j = -1; j < shippingCosts.length(); j++) {
            k++;
            mShippingClassIndex.add(j);
          }

          JSONObject temp = shippingCosts.getJSONObject(0);//select default shipping method (first/only method present) for each object
          mShippingClasses.add(i,temp.getInt("id"));//set our default shippingClasses methods

        } catch (JSONException e) {
          e.printStackTrace();
        }
        mList.add(new ShippingMethodItem(product, extras.getInt("orderQuantity" + i), i, shippingCosts, shippingCountry.iso3CurrencyCode(), selectedShippingClass));
      }
    }

    mListView = (ListView) findViewById(R.id.shipping_method_listview);

    mShippingAdapter = new ShippingAdapter(this, mList);

    mShippingAdapter.setShippingClasses(mShippingClasses);//get current shipping classes in case basket items have been removed

    mListView.setAdapter(mShippingAdapter);
  }

  @Override
  public void onBackPressed() {
    //when either back button is pressed , finish current activity , send selected shipping classes back to basket and open it
    finish();
    mShippingClasses = ShippingAdapter.getShippingClasses();

    Intent intent = new Intent(this, BasketActivity.class);
    intent.putExtra("shippingClass", mShippingClasses);

    startActivity(intent);
  }

  public class ShippingMethodItem {

    private int itemQuantity;
    private int noOfOptions;//number of shipping classes for each element
    private JSONArray shippingCosts;
    private String currencyCode;
    private int elementIndex;
    Product product;
    private int mSelectedShippingClass;


    public ShippingMethodItem(Product product, int quantity, int elementIndex, JSONArray shippingCosts, String currencyCode, int selectedShippingClass) {
      this.product = product;
      this.itemQuantity = quantity;
      this.shippingCosts = shippingCosts;
      this.currencyCode = currencyCode;

      this.noOfOptions = shippingCosts.length();
      this.elementIndex = elementIndex;
      this.mSelectedShippingClass = selectedShippingClass;


    }

    public int getElementIndex() {
      return this.elementIndex;
    }

    public Product getProduct() {
      return this.product;
    }

    public int getItemQuantity() {
      return itemQuantity;
    }

    public JSONArray getShippingCosts() {
      return this.shippingCosts;
    }

    public int getNoOfOptions() {
      return this.noOfOptions;
    }

    public int getSelectedShippingClass() {
      return this.mSelectedShippingClass;
    }

    public void setSelectedShippingClass(int shippingClass) {
      this.mSelectedShippingClass = shippingClass;
    }

    public int getCurrentShippingMethods(int position) {
      return mShippingClassIndex.get(position);
    }

    public String getCurrencyCode() {
      return currencyCode;
    }
  }


  public static class ShippingAdapter extends BaseAdapter {
    private static ArrayList<Integer> mShippingClasses = new ArrayList<Integer>();
    private MultipleCurrencyAmounts mShipCost;
    private Context context;
    private ArrayList<ShippingMethodItem> items;


    public ShippingAdapter(Context context, ArrayList<ShippingMethodItem> items) {
      this.context = context;
      this.items = items;
    }

    @Override
    public int getCount() {
      int count = 0;

      for (int i = 0; i < items.size(); i++) {
        count += items.get(i).getNoOfOptions() + 1;//+1 for including the title

      }
      return count;
    }

    @Override
    public Object getItem(int position) {

      for (int i = 0; i < items.size(); ++i) {
        ShippingMethodItem item = items.get(i);
        if (position < item.getNoOfOptions() + 1) {
          return item;
        } else {
          position -= item.getNoOfOptions() + 1;
        }
      }

      throw new IllegalStateException("Will never get here");

    }

    @Override
    public long getItemId(int position) {
      return 0L;
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }

    @Override
    public int getItemViewType(int position) {
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

    public static ArrayList<Integer> getShippingClasses() {
      return mShippingClasses;
    }

    public void setShippingClasses(ArrayList<Integer> shippingClasses) { mShippingClasses = shippingClasses; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      final ShippingMethodItem currentItem = (ShippingMethodItem) getItem(position);
      int viewType = this.getItemViewType(position);
      LayoutInflater vi;

      //if title element
      if (viewType == 0) {
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder1 title;
        if (convertView == null) {
          convertView = vi.inflate(R.layout.list_item_shipping_method_title, null);
          title = new ViewHolder1(convertView);
        } else
          title = (ViewHolder1) convertView.getTag();

        title.itemName.setText(currentItem.getItemQuantity() + " x " + currentItem.getProduct().getDisplayLabel() + " (" + currentItem.getProduct().getCategory() + ")");

        convertView.setTag(title);
        return convertView;

        //if shipping method element
      } else if (viewType == 1) {
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder2 info;

        if (convertView == null) {
          convertView = vi.inflate(R.layout.list_item_shipping_methods, null);
          info = new ViewHolder2(convertView);
        } else
          info = (ViewHolder2) convertView.getTag();

        try {
          //get the information from the json object storred in shippingCost string
          JSONObject temp = currentItem.getShippingCosts().getJSONObject(currentItem.getCurrentShippingMethods(position));

          final int id = temp.getInt("id");
          int index = currentItem.getElementIndex();

          info.shippingClassName.setText(temp.getString("mobile_shipping_name"));
          info.shippingTime.setText(temp.getString("min_delivery_time") + " - " + temp.getString("max_delivery_time") + " days");
          JSONArray costArray = temp.getJSONArray("costs");

          //if current item is the default/selected shipping class
          if (id == currentItem.getSelectedShippingClass()) {
            //place icon to show that it is selected
            info.optionSelected.setVisibility(View.VISIBLE);

            //place the shipping class information into the dedicated list
            mShippingClasses.set(index, id);

          } else
            info.optionSelected.setVisibility(View.INVISIBLE);


          mShipCost = new MultipleCurrencyAmounts(costArray);
          final String shippingCost = mShipCost.getDisplayAmountWithFallbackMultipliedBy(Country.getInstance().iso3CurrencyCode(), currentItem.getItemQuantity(), Locale.getDefault());

          info.shippingPrice.setText(shippingCost);
          convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //when a shipping class is selected it becomes the default one for that item , and is storred in the dedicated list
              info.optionSelected.setVisibility(View.VISIBLE);
              currentItem.setSelectedShippingClass(id);
              notifyDataSetChanged();
              mShippingClasses.set(currentItem.getElementIndex(), id);
            }
          });


        } catch (JSONException e) {
          e.printStackTrace();
        }

        convertView.setTag(info);
        return convertView;
      } else return null;

    }

    //ViewHolder1 inner class
    private class ViewHolder1 {
      TextView itemName;

      public ViewHolder1(View view) {
        itemName = (TextView) view.findViewById(R.id.nameText0);
      }
    }

    //ViewHolder2 inner class
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
        optionSelected.setImageResource(R.drawable.tick);
      }
    }

  }

}
