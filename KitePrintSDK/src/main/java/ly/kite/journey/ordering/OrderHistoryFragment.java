/*****************************************************
 *
 * OrderHistoryFragment.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.journey.ordering;


///// Import(s) /////

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ly.kite.R;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.CatalogueLoaderFragment;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.checkout.OrderReceiptActivity;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.AKiteFragment;
import ly.kite.ordering.Order;
import ly.kite.ordering.OrderHistoryItem;
import ly.kite.ordering.OrderingDataAgent;
import ly.kite.pricing.OrderPricing;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment shows the order history screen.
 *
 *****************************************************/
public class OrderHistoryFragment extends AKiteFragment implements AdapterView.OnItemClickListener,
                                                                   ICatalogueConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "OrderHistoryFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Catalogue                            mCatalogue;

  private ListView                             mListView;

  private List<OrderHistoryItem>               mOrderHistoryItemList;
  private OrderHistoryAdaptor                  mOrderHistoryAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void start( Context context )
    {
    Intent intent = new Intent( context, OrderHistoryFragment.class );

    context.startActivity( intent );
    }


  ////////// Constructor(s) //////////


  ////////// AKiteFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is resumed.
   *
   *****************************************************/
  @Override
  public void onResume()
    {
    super.onResume();


    // Request the catalogue. We do this when the activity resumes, because
    // we may have looked at a failed order which gets re-tried and then
    // succeeds. If this happens, we need to refresh our order history list.
    requestCatalogue();
    }


  /*****************************************************
   *
   * Called to create the view.
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_order_history, container, false );

    mListView = (ListView)view.findViewById( R.id.list_view );


    // Listen for clicks
    mListView.setOnItemClickListener( this );


    checkDisplayOrders();


    return ( view );
    }


  ////////// ICatalogueConsumer Method(s) //////////

  @Override
  public void onCatalogueSuccess( Catalogue catalogue )
    {
    mCatalogue = catalogue;

    checkDisplayOrders();
    }


  @Override
  public void onCatalogueCancelled()
    {
    }


  @Override
  public void onCatalogueError( Exception exception )
    {
    // Display an error dialog
    ( (AKiteActivity)getActivity() ).displayModalDialog
            (
                    R.string.kitesdk_alert_dialog_title_error_retrieving_products,
                    R.string.kitesdk_alert_dialog_message_error_retrieving_products,
                    R.string.kitesdk_Retry,
                    new RequestCatalogueRunnable(),
                    R.string.kitesdk_Cancel,
                    new FinishRunnable()
            );
    }


  ////////// AdapterView.OnItemClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when an order is clicked.
   *
   *****************************************************/
  @Override
  public void onItemClick( AdapterView<?> parent, View view, int position, long id )
    {
    // Get the order history item
    OrderHistoryItem orderHistoryItem = mOrderHistoryItemList.get( position );


    try
      {
      // Create an order using all the information from the order history item

      String userDataJSON = orderHistoryItem.getUserDataJSON();

      Order order = new Order(
              getActivity(),
              orderHistoryItem.getBasket(),
              orderHistoryItem.getShippingAddress(),
              orderHistoryItem.getNotificationEmail(),
              orderHistoryItem.getNotificationPhone(),
              ( userDataJSON != null ? new JSONObject( userDataJSON ) : null ),
              orderHistoryItem.getAdditionalParametersMap(),
              orderHistoryItem.getPromoCode(),
              new OrderPricing( orderHistoryItem.getPricingJSON() ),
              orderHistoryItem.getProofOfPayment(),
              orderHistoryItem.getReceipt() );

      // Start the receipt screen with the order
      OrderReceiptActivity.start( getActivity(), orderHistoryItem.getOrderId(), order, true );
      }
    catch ( JSONException je )
      {
      Log.e( TAG, "Unable to recreate pricing JSON", je );
      }

    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Requests the catalogue.
   *
   *****************************************************/
  void requestCatalogue()
    {
    CatalogueLoaderFragment.findOrStart( this );
    }


  /*****************************************************
   *
   * Checks whether we have all the information we need
   * to display orders.
   *
   *****************************************************/
  private void checkDisplayOrders()
    {
    if ( mCatalogue == null || mListView == null ) return;


    // Set up the order history list

    mOrderHistoryItemList = OrderingDataAgent.getInstance( getActivity() ).getOrderHistoryList( mCatalogue );

    mOrderHistoryAdaptor = new OrderHistoryAdaptor();

    mListView.setAdapter( mOrderHistoryAdaptor );
    }


  /*****************************************************
   *
   * Called to finish this activity.
   *
   *****************************************************/
  void onLoadCancelled()
    {
    Activity activity = getActivity();

    if ( activity != null && activity instanceof ICancelListener )
      {
      ( (ICancelListener)activity ).onLoadCancelled();
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A listener for cancel events.
   *
   *****************************************************/
  public interface ICancelListener
    {
    public void onLoadCancelled();
    }


  /*****************************************************
   *
   * The order history adaptor.
   *
   *****************************************************/
  private class OrderHistoryAdaptor extends BaseAdapter
    {
    @Override
    public int getCount()
      {
      return ( mOrderHistoryItemList.size() );
      }


    @Override
    public Object getItem( int position )
      {
      return ( mOrderHistoryItemList.get( position ) );
      }


    @Override
    public long getItemId( int position )
      {
      return ( 0 );
      }


    @Override
    public View getView( int position, View convertView, ViewGroup parent )
      {
      Object      tag;
      View        view;
      ViewHolder  viewHolder;

      if ( convertView != null &&
           ( tag = convertView.getTag() ) != null &&
           tag instanceof ViewHolder )
        {
        view       = convertView;
        viewHolder = (ViewHolder)tag;
        }
      else
        {
        view       = LayoutInflater.from( getActivity() ).inflate( R.layout.list_item_order_history, parent, false );
        viewHolder = new ViewHolder( view );

        view.setTag( viewHolder );
        }


      OrderHistoryItem orderHistoryItem = (OrderHistoryItem)getItem( position );

      viewHolder.bind( orderHistoryItem );

      return ( view );
      }


    private class ViewHolder
      {
      View      view;
      TextView  dateTextView;
      TextView  descriptionTextView;

      ViewHolder( View view )
        {
        this.view = view;
        this.dateTextView = (TextView)view.findViewById( R.id.date_text_view );
        this.descriptionTextView = (TextView)view.findViewById( R.id.description_text_view );
        }

      void bind( OrderHistoryItem orderHistoryItem )
        {
        this.dateTextView.setText( orderHistoryItem.getDateString() );
        this.descriptionTextView.setText( orderHistoryItem.getDescription() );
        }
      }
    }


  /*****************************************************
   *
   * A request catalogue runnable.
   *
   *****************************************************/
  private class RequestCatalogueRunnable implements Runnable
    {
    @Override
    public void run()
      {
      requestCatalogue();
      }
    }


  /*****************************************************
   *
   * A finish runnable.
   *
   *****************************************************/
  private class FinishRunnable implements Runnable
    {
    @Override
    public void run()
      {
      onLoadCancelled();
      }
    }

  }

