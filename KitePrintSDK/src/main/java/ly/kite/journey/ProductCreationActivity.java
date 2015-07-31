/*****************************************************
 *
 * ProductCreationActivity.java
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

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.checkout.CheckoutActivity;
import ly.kite.print.Asset;
import ly.kite.print.PrintJob;
import ly.kite.print.PrintOrder;
import ly.kite.print.Product;
import ly.kite.print.ProductGroup;
import ly.kite.product.UserJourneyCoordinator;

/*****************************************************
 *
 * This activity coordinates the various fragments involved
 * in selecting and creating a product.
 *
 *****************************************************/
public class ProductCreationActivity extends AKiteActivity implements FragmentManager.OnBackStackChangedListener,
                                                                       ChooseProductGroupFragment.ICallback,
                                                                       ChooseProductFragment.ICallback,
                                                                       ProductOverviewFragment.ICallback,
                                                                       PhoneCaseFragment.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                         = "ProductSelectionAct.";  // Can't be more than 23 characters ... who knew?!

  private static final String  INTENT_EXTRA_NAME_ASSET_LIST    = KiteSDK.INTENT_PREFIX + ".AssetList";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Asset>            mAssetArrayList;

  private FragmentManager             mFragmentManager;

  private ChooseProductGroupFragment  mProductGroupFragment;
  private ChooseProductFragment       mProductFragment;
  private ProductOverviewFragment     mProductOverviewFragment;

  private AJourneyFragment            mCurrentFragment;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  public static void start( Context context, ArrayList<Asset> assetArrayList )
    {
    Intent intent = new Intent( context, ProductCreationActivity.class );

    intent.putParcelableArrayListExtra( INTENT_EXTRA_NAME_ASSET_LIST, assetArrayList );

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


    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
      {
      getWindow().setStatusBarColor( getResources().getColor( R.color.translucent_status_bar ) );
      }



    // Get the assets

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


    // Set up the screen content
    setContentView( R.layout.screen_product_creation );


    // Listen for changes to the fragment back stack

    mFragmentManager = getFragmentManager();

    mFragmentManager.addOnBackStackChangedListener( this );


    if ( savedInstanceState != null )
      {
      // If we are being re-started - get the current fragment again.

      determineCurrentFragment();
      }
    else
      {
      // Start the first fragment

      addFragment( mProductGroupFragment = ChooseProductGroupFragment.newInstance(), ChooseProductGroupFragment.TAG );
      }
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    // See what menu item was selected

    int itemId = item.getItemId();

    if ( itemId == android.R.id.home )
      {
      ///// Home /////

      // We intercept the home button and do the same as if the
      // back key had been pressed. We don't allow fragments to
      // intercept this one.

      super.onBackPressed();

      return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called when the back key is pressed. Some fragments
   * intercept the back key and do something internally.
   *
   *****************************************************/
  @Override
  public void onBackPressed()
    {
    if ( mCurrentFragment != null && mCurrentFragment.onBackPressIntercepted() )
      {
      return;
      }

    super.onBackPressed();
    }


  /*****************************************************
   *
   * Called when an activity result is received.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    super.onActivityResult( requestCode, resultCode, data );

    // If we successfully completed check-out then we want to exit so the user goes back to
    // the original app.
    if ( requestCode == ACTIVITY_REQUEST_CODE_CHECKOUT && resultCode == RESULT_OK )
      {
      finish();
      }
    }


  ////////// FragmentManager.OnBackStackChangedListener Method(s) //////////

  /*****************************************************
   *
   * Listens for changes to the back stack, so we can exit
   * the activity when there are no more fragments on it.
   *
   *****************************************************/
  @Override
  public void onBackStackChanged()
    {
    int entryCount = mFragmentManager.getBackStackEntryCount();

    if ( entryCount < 1 )
      {
      finish();
      }


    determineCurrentFragment();
    }


  ////////// ChooseProductGroupFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a product group is chosen.
   *
   *****************************************************/
  @Override
  public void pgOnProductGroupChosen( ProductGroup productGroup )
    {
    // Display the product selection fragment

    mProductFragment = ChooseProductFragment.newInstance( productGroup );

    addFragment( mProductFragment, ChooseProductFragment.TAG );
    }


  ////////// ChooseProductFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when a product group is chosen.
   *
   *****************************************************/
  @Override
  public void pOnProductChosen( Product product )
    {
    // Display the product overview fragment

    mProductOverviewFragment = ProductOverviewFragment.newInstance( product );

    addFragment( mProductOverviewFragment, ProductOverviewFragment.TAG );
    }


  ////////// ProductOverviewFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the user wishes to create a product.
   *
   *****************************************************/
  @Override
  public void poOnCreateProduct( Product product )
    {
    AJourneyFragment fragment = UserJourneyCoordinator.getInstance().getFragment( this, mAssetArrayList, product );

    addFragment( fragment, "Custom" );
    }


  ////////// PhoneCaseFragment.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when the product has been created.
   *
   *****************************************************/
  @Override
  public void pcOnCreated( Product product, Asset imageAsset )
    {
    // Create the print order

    PrintOrder printOrder = new PrintOrder();

    printOrder.addPrintJob( PrintJob.createPrintJob( product, imageAsset ) );


    // Start the check out activity
    CheckoutActivity.start( this, printOrder, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Displays a fragment.
   *
   *****************************************************/
  private void addFragment( AJourneyFragment fragment, String tag )
    {
    mFragmentManager
      .beginTransaction()
            .replace( R.id.fragment_container, fragment, tag )
            .addToBackStack( tag )  // Use the tag as the name so we can find it later
      .commit();
    }


  /*****************************************************
   *
   * Works out what the current fragment is.
   *
   *****************************************************/
  private void determineCurrentFragment( int entryCount )
    {
    try
      {
      FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt( entryCount - 1 );

      mCurrentFragment = (AJourneyFragment)mFragmentManager.findFragmentByTag( entry.getName() );
      }
    catch ( Exception e )
      {
      Log.e( LOG_TAG, "Could not get current fragment", e );

      mCurrentFragment = null;
      }

    //Log.d( LOG_TAG, "Current fragment = " + mCurrentFragment );
    }


  /*****************************************************
   *
   * Works out what the current fragment is.
   *
   *****************************************************/
  private void determineCurrentFragment()
    {
    determineCurrentFragment( mFragmentManager.getBackStackEntryCount() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

