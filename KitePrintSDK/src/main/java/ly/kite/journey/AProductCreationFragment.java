/*****************************************************
 *
 * AProductCreationFragment.java
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

package ly.kite.journey;


///// Import(s) /////


///// Class Declaration /////

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import ly.kite.R;
import ly.kite.product.Product;

/*****************************************************
 *
 * This is the abstract super-class of product creation
 * fragments. It provides some common features.
 *
 *****************************************************/
abstract public class AProductCreationFragment extends AKiteFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "AProductCreationFrag.";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected ArrayList<AssetsAndQuantity>  mAssetsAndQuantityArrayList;
  protected Product                       mProduct;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AKiteFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // We want to check if we are being re-created first, because the assets may have changed
    // since we were first launched - in which case the asset lists from the saved instance state
    // will be different from those that were provided in the argument bundle.

    if ( savedInstanceState != null )
      {
      mAssetsAndQuantityArrayList = savedInstanceState.getParcelableArrayList( BUNDLE_KEY_ASSETS_AND_QUANTITY_LIST );
      }


    // Get the assets and product

    Bundle arguments = getArguments();

    if ( arguments == null )
      {
      Log.e( LOG_TAG, "No arguments found" );

      return;
      }


    // Only get the original asset list from the intent if we couldn't find any saved lists

    if ( mAssetsAndQuantityArrayList == null )
      {
      mAssetsAndQuantityArrayList = arguments.getParcelableArrayList( BUNDLE_KEY_ASSETS_AND_QUANTITY_LIST );
      }


    // Get the product
    mProduct = arguments.getParcelable( BUNDLE_KEY_PRODUCT );
    }


  /*****************************************************
   *
   * Saves the current state to the supplied bundle.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    outState.putParcelableArrayList( BUNDLE_KEY_ASSETS_AND_QUANTITY_LIST, mAssetsAndQuantityArrayList );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Checks that the asset lists are OK.
   *
   *****************************************************/
  protected boolean assetListValid()
    {
    if ( mAssetsAndQuantityArrayList == null )
      {
      Log.e( TAG, "Invalid asset list" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_no_asset_list,
              R.string.alert_dialog_message_no_asset_list,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable() );

      return ( false );
      }


    return ( true );
    }


  /*****************************************************
   *
   * Checks that the product is OK.
   *
   *****************************************************/
  protected boolean productIsValid()
    {
    if ( mProduct == null )
      {
      Log.e( TAG, "No product found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_product_not_found,
              R.string.alert_dialog_message_product_not_found,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return ( false );
      }


    return ( true );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

