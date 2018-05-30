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
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.app.ARetainedDialogFragment;
import ly.kite.app.RetainedFragmentHelper;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment is a retained fragment that loads the
 * catalogue.
 *
 *****************************************************/
public class CatalogueLoaderFragment extends ARetainedDialogFragment implements ICatalogueConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "CatalogueLoaderFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ProgressDialog   mProgressDialog;

  private CatalogueLoader  mCatalogueLoader;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Attaches the fragment to the activity, and then
   * submits the order.
   *
   *****************************************************/
  static private CatalogueLoaderFragment start( Activity activity, CatalogueLoaderFragment catalogueLoaderFragment, String... filterProductIds )
    {
    catalogueLoaderFragment.addTo( activity, TAG );

    catalogueLoaderFragment.loadCatalogue( activity, filterProductIds );

    return ( catalogueLoaderFragment );
    }


  /*****************************************************
   *
   * Attaches this fragment to the activity, and then
   * submits the order.
   *
   *****************************************************/
  static private CatalogueLoaderFragment start( Activity activity, String... filterProductIds )
    {
    CatalogueLoaderFragment catalogueLoaderFragment = new CatalogueLoaderFragment();

    return ( start( activity, catalogueLoaderFragment, filterProductIds ) );
    }


  /*****************************************************
   *
   * Attaches this fragment to the activity, and then
   * submits the order.
   *
   *****************************************************/
  static private <F extends Fragment & ICatalogueConsumer> CatalogueLoaderFragment start( F catalogueConsumerFragment, String... filterProductIds )
    {
    CatalogueLoaderFragment catalogueLoaderFragment = new CatalogueLoaderFragment();

    catalogueLoaderFragment.setTargetFragment( catalogueConsumerFragment, 0 );

    return ( start( catalogueConsumerFragment.getActivity(), catalogueLoaderFragment, filterProductIds ) );
    }


  /*****************************************************
   *
   * Tries to find this fragment, and returns it.
   *
   *****************************************************/
  static public CatalogueLoaderFragment find( Activity activity )
    {
    return ( (CatalogueLoaderFragment)find( activity, TAG, CatalogueLoaderFragment.class ) );
    }


  /*****************************************************
   *
   * Returns this fragment, if it is already attached to
   * the activity. Otherwise will create and display a
   * new fragment.
   *
   *****************************************************/
  static public CatalogueLoaderFragment findOrStart( Activity activity, String... filterProductIds )
    {
    CatalogueLoaderFragment fragment = find( activity );

    if ( fragment != null ) return ( fragment );

    return ( start( activity, filterProductIds ) );
    }


  /*****************************************************
   *
   * Returns this fragment, if it is already attached to
   * the activity. Otherwise will create and display a
   * new fragment.
   *
   *****************************************************/
  static public <F extends Fragment & ICatalogueConsumer> CatalogueLoaderFragment findOrStart( F catalogueConsumerFragment, String... filterProductIds )
    {
    CatalogueLoaderFragment foundFragment = find( catalogueConsumerFragment.getActivity() );

    if ( foundFragment != null ) return ( foundFragment );

    return ( start( catalogueConsumerFragment, filterProductIds ) );
    }


  ////////// Constructor(s) //////////

  public CatalogueLoaderFragment()
    {
    super( ICatalogueConsumer.class );
    }


  ////////// DialogFragment Method(s) //////////

  /*****************************************************
   *
   * Called to create a dialog.
   *
   *****************************************************/
  @Override
  public AlertDialog onCreateDialog( Bundle savedInstanceState )
    {
    // If there isn't already a progress dialog - create one now

    if ( mProgressDialog == null )
      {
      mProgressDialog = new ProgressDialog( getActivity() );

      mProgressDialog.setIndeterminate( true );
      mProgressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
      mProgressDialog.setProgressPercentFormat( null );
      mProgressDialog.setProgressNumberFormat( null );   // Don't display the "N/100" text
      mProgressDialog.setTitle( R.string.kitesdk_Loading_catalogue);
      }

    setCancelable( true );

    return ( mProgressDialog );
    }


  /*****************************************************
   *
   * Called when the dialog is cancelled.
   *
   *****************************************************/
  @Override
  public void onCancel( DialogInterface dialogInterface )
    {
    onCatalogueCancelled();
    }


  ////////// CatalogueLoader.ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the catalogue is loaded successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueSuccess( final Catalogue catalogue )
    {
    setStateNotifier( new RetainedFragmentHelper.AStateNotifier()
      {
      @Override
      public void notify( Object catalogueConsumerObject )
        {
        if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( TAG, "notify( catalogueConsumerObject = " + catalogueConsumerObject + " ) - success" );

        ( (ICatalogueConsumer)catalogueConsumerObject ).onCatalogueSuccess( catalogue );

        remove();
        }
      } );
    }


  /*****************************************************
   *
   * Called when the catalogue load is cancelled.
   *
   *****************************************************/
  @Override
  public void onCatalogueCancelled()
    {
    setStateNotifier( new RetainedFragmentHelper.AStateNotifier()
      {
      @Override
      public void notify( Object catalogueConsumerObject )
        {
        if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( TAG, "notify( catalogueConsumerObject = " + catalogueConsumerObject + " ) - cancelled" );

        ( (ICatalogueConsumer)catalogueConsumerObject ).onCatalogueCancelled();

        cancel();
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
    setStateNotifier( new RetainedFragmentHelper.AStateNotifier()
      {
      @Override
      public void notify( Object catalogueConsumerObject )
        {
        if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( TAG, "notify( catalogueConsumerObject = " + catalogueConsumerObject + " ) - success" );

        ( (ICatalogueConsumer)catalogueConsumerObject ).onCatalogueError( exception );

        remove();
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
  public void cancel()
    {
    if ( mCatalogueLoader != null ) mCatalogueLoader.cancelRequests();

    remove();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

