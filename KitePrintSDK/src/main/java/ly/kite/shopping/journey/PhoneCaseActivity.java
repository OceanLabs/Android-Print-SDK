/*****************************************************
 *
 * PhoneCaseActivity.java
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

package ly.kite.shopping.journey;


///// Import(s) /////

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.net.URL;
import java.util.ArrayList;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.print.Asset;
import ly.kite.print.Bleed;
import ly.kite.print.Product;
import ly.kite.print.ProductManager;
import ly.kite.shopping.AKiteActivity;
import ly.kite.util.ImageManager;
import ly.kite.widget.MaskedRemoteImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to create a phone
 * case design using an image.
 *
 *****************************************************/
public class PhoneCaseActivity extends AKiteActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                      = "PhoneCaseActivity";

  public  static final String      INTENT_EXTRA_NAME_ASSET_LIST = KiteSDK.INTENT_PREFIX + ".AssetList";
  public  static final String      INTENT_EXTRA_NAME_PRODUCT_ID = KiteSDK.INTENT_PREFIX + ".ProductId";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Asset>       mAssetArrayList;
  private Product                mProduct;

  private MaskedRemoteImageView  mMaskedRemoteImageView;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  public static void start( Context context, ArrayList<Asset> assetArrayList, Product product )
    {
    Intent intent = new Intent( context, PhoneCaseActivity.class );

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

      return;
      }


    // Set up the screen

    setContentView( R.layout.screen_phone_case );

    mMaskedRemoteImageView = (MaskedRemoteImageView)findViewById( R.id.masked_remote_image_view );


    // Request the image and mask

    ImageManager imageManager = ImageManager.getInstance( this );
    Handler      handler      = new Handler();

    Asset        asset        = mAssetArrayList.get( 0 );
    URL          maskURL      = mProduct.getMaskURL();
    Bleed        maskBleed    = mProduct.getMaskBleed();

    mMaskedRemoteImageView.setImageKey( asset );
    mMaskedRemoteImageView.setMaskDetails( maskURL, maskBleed );

    imageManager.getImage( IMAGE_CLASS_STRING_PRODUCT_ITEM, asset, handler, mMaskedRemoteImageView );
    imageManager.getRemoteImage( IMAGE_CLASS_STRING_PRODUCT_ITEM, maskURL, handler, mMaskedRemoteImageView );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the Next button is clicked.
   *
   *****************************************************/
  public void onNextClicked( View view )
    {
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

