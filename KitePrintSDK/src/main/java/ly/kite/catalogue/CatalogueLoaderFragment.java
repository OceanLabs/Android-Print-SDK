/*****************************************************
 *
 * CatalogueLoaderFragment.java
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

package ly.kite.catalogue;


///// Import(s) /////

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.app.ARetainedFragment;
import ly.kite.app.RetainedFragmentHelper;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.selection.ChooseProductFragment;
import ly.kite.journey.selection.ChooseProductGroupFragment;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment is a retained fragment that loads the
 * catalogue.
 *
 *****************************************************/
public class CatalogueLoaderFragment extends ARetainedFragment<ICatalogueConsumer> implements ICatalogueConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "CatalogueLoaderFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private CatalogueLoader  mCatalogueLoader;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Attaches this fragment to the activity, and then
   * submits the order.
   *
   *****************************************************/
  static public CatalogueLoaderFragment start( Activity activity, String[] filterProductIds )
    {
    CatalogueLoaderFragment catalogueLoaderFragment = new CatalogueLoaderFragment();

    catalogueLoaderFragment.addTo( activity, TAG );

    catalogueLoaderFragment.loadCatalogue( activity, filterProductIds );

    return ( catalogueLoaderFragment );
    }


  /*****************************************************
   *
   * Tries to find this fragment, and returns it.
   *
   *****************************************************/
  static public CatalogueLoaderFragment findFragment( Activity activity )
    {
    return ( (CatalogueLoaderFragment)findFragment( activity, TAG, CatalogueLoaderFragment.class ) );
    }


  ////////// Constructor(s) //////////


  ////////// ARetainedFragment Method(s) //////////


  ////////// CatalogueLoader.ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the catalogue is loaded successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueSuccess( final Catalogue catalogue )
    {
    setState( mRetainedFragmentHelper.new AStateNotifier()
      {
      @Override
      public void notify( ICatalogueConsumer catalogueConsumer )
        {
        if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( TAG, "notify( catalogueConsumer = " + catalogueConsumer + " ) - success" );

        catalogueConsumer.onCatalogueSuccess( catalogue );
        }
      } );
    }


  /*****************************************************
   *
   * Called when the catalogue load fails.
   *
   *****************************************************/
  @Override
  public void onCatalogueError( final Exception exception )
    {
    setState( mRetainedFragmentHelper.new AStateNotifier()
      {
      @Override
      public void notify( ICatalogueConsumer catalogueConsumer )
        {
        if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( TAG, "notify( catalogueConsumer = " + catalogueConsumer + " ) - error" );

        catalogueConsumer.onCatalogueError( exception );
        }
      } );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Requests the catalogue load.
   *
   *****************************************************/
  private void loadCatalogue( Context context, String[] filterProductIds )
    {
    if ( mCatalogueLoader == null )
      {
      mCatalogueLoader = KiteSDK.getInstance( context ).getCatalogueLoader();
      }

    mCatalogueLoader.requestCatalogue( KiteSDK.MAX_ACCEPTED_PRODUCT_AGE_MILLIS, filterProductIds, this );
    }


  /*****************************************************
   *
   * Cancels any running requests.
   *
   *****************************************************/
  public void cancelRequests()
    {
    if ( mCatalogueLoader != null ) mCatalogueLoader.cancelRequests();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

