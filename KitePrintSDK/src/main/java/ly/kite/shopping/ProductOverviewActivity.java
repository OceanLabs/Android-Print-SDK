/*****************************************************
 *
 * ProductOverviewActivity.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.shopping;


///// Import(s) /////


///// Class Declaration /////

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.address.Country;
import ly.kite.print.Asset;
import ly.kite.print.Product;
import ly.kite.print.ProductManager;
import ly.kite.print.SingleUnitSize;
import ly.kite.print.UnitOfLength;

/*****************************************************
 *
 * This activity displays a product overview.
 *
 *****************************************************/
public class ProductOverviewActivity extends AKiteActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                           = "ProductOverviewActivity";

  public  static final String      INTENT_EXTRA_NAME_ASSET_LIST      = KiteSDK.INTENT_PREFIX + ".AssetList";
  public  static final String      INTENT_EXTRA_NAME_PRODUCT_ID      = KiteSDK.INTENT_PREFIX + ".ProductId";

  private static final BigDecimal  BIG_DECIMAL_ZERO                  = BigDecimal.valueOf( 0 );


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Asset>         mAssetArrayList;
  private Product                  mProduct;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static void start( Context context, ArrayList<Asset> assetArrayList, Product product )
    {
    Intent intent = new Intent( context, ProductOverviewActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );
    intent.putExtra( INTENT_EXTRA_NAME_PRODUCT_ID, product.getId() );

    context.startActivity( intent );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Get the assets and product

    Intent intent = getIntent();

    if ( intent == null )
      {
      Log.e( LOG_TAG, "No intent found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_intent,
              R.string.alert_dialog_message_no_intent,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      finish();

      return;
      }

    if ( ( mAssetArrayList = intent.getParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST ) ) == null || mAssetArrayList.size() < 1 )
      {
      Log.e( LOG_TAG, "No asset list found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_asset_list,
              R.string.alert_dialog_message_no_asset_list,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      finish();

      return;
      }


    String productId = intent.getStringExtra( INTENT_EXTRA_NAME_PRODUCT_ID );

    if ( productId == null )
      {
      Log.e( LOG_TAG, "No product id found" );

      displayModalDialog(
              R.string.alert_dialog_title_no_product_id,
              R.string.alert_dialog_message_no_product_id,
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      finish();

      return;
      }

    Product mProduct = ProductManager.getInstance().getProductById( productId );

    if ( mProduct == null )
      {
      Log.e( LOG_TAG, "No product found for id " + productId );

      displayModalDialog(
              R.string.alert_dialog_title_product_not_found,
              getString( R.string.alert_dialog_message_no_product_for_id, productId ),
              DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              new FinishRunnable()
      );

      finish();

      return;
      }


    // Set up the screen

    setTitle( mProduct.getName() );

    setContentView( R.layout.activity_product_overview );

    TextView priceTextView        = (TextView)findViewById( R.id.price_text_view );
    TextView quantitySizeTextView = (TextView)findViewById( R.id.quantity_size_text_view );
    TextView shippingTextView     = (TextView)findViewById( R.id.shipping_text_view );


    // Price

    SingleCurrencyCost cost = mProduct.getCostWithFallback( Country.getInstance( Locale.getDefault() ).getCurrencyCode() );

    if ( cost != null ) priceTextView.setText( cost.getFormattedAmount() );


    // Quantity / size

    int quantityPerSheet = mProduct.getQuantityPerSheet();

    if ( quantityPerSheet > 1 )
      {
      StringBuilder quantitySizeStringBuilder = new StringBuilder( getString( R.string.product_quantity_format_string, quantityPerSheet ) );


      // Get the size

      SingleUnitSize size = mProduct.getSizeWithFallback( UnitOfLength.INCHES );

      if ( size != null )
        {
        quantitySizeStringBuilder
                .append( "\n" )
                .append( String.format( getString( R.string.product_size_format_string ), size.getWidth(), size.getHeight(), size.getUnit().plural( this ).toUpperCase() ) );
        }


      quantitySizeTextView.setText( quantitySizeStringBuilder.toString() );
      }
    else
      {
      quantitySizeTextView.setVisibility( View.GONE );
      }


    // Shipping (postage)

    Locale locale = Locale.getDefault();

    MultipleCurrencyCost shippingCost = mProduct.getShippingCostTo( locale.getISO3Country() );

    if ( shippingCost != null )
      {
      // Get the cost in the default currency for the locale, and format the amount.

      NumberFormat numberFormatter = NumberFormat.getCurrencyInstance( locale );

      cost = shippingCost.get( numberFormatter.getCurrency().getCurrencyCode() );

      if ( cost != null )
        {
        if ( cost.getAmount().compareTo( BIG_DECIMAL_ZERO ) != 0 )
          {
          shippingTextView.setText( getString( R.string.product_shipping_format_string, numberFormatter.format( cost.getAmount().doubleValue() ) ) );
          }
        else
          {
          // Free shipping
          shippingTextView.setText( R.string.product_free_shipping );
          }
        }
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

