/*****************************************************
 *
 * AKiteActivity.java
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.product.ProductLoader;

/*****************************************************
 *
 * This abstract class is the base class for activities
 * in the Kite SDK. It provides some common functionality.
 *
 *****************************************************/
public abstract class AKiteActivity extends Activity implements FragmentManager.OnBackStackChangedListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private   static final String  LOG_TAG                                     = "AKiteActivity";

  public    static final String  INTENT_EXTRA_NAME_ASSETS_AND_QUANTITY__LIST = KiteSDK.INTENT_PREFIX + ".assetsAndQuantityList";

  public    static final int     DONT_DISPLAY_BUTTON                         = 0;

  public    static final String  IMAGE_CLASS_STRING_PRODUCT_ITEM             = "product_item";

  protected static final int     ACTIVITY_REQUEST_CODE_CHECKOUT              = 1;
  protected static final int     ACTIVITY_REQUEST_CODE_CREATE                = 2;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private   boolean           mActivityIsVisible;

  private   Dialog            mDialog;

  private   FragmentManager   mFragmentManager;

  protected AKiteFragment     mCurrentFragment;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


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


    // TODO: Fix this dirty hack
    ProductLoader.getInstance( this );


    // Listen for changes to the fragment back stack

    mFragmentManager = getFragmentManager();

    mFragmentManager.addOnBackStackChangedListener( this );


    // If we are being re-started - get the current fragment again.

    if ( savedInstanceState != null )
      {
      determineCurrentFragment();
      }
    }


  /*****************************************************
   *
   * Sets the content view.
   *
   *****************************************************/
  @Override
  public void setContentView( int layoutResourceId )
    {
    super.setContentView( layoutResourceId );


    Resources resources = getResources();


    int titleViewId = resources.getIdentifier( "action_bar_title", "id", "android" );

    if ( titleViewId != 0 )
      {
      View titleView = findViewById( titleViewId );

      if ( titleView != null )
        {
        ViewGroup.LayoutParams titleLayoutParams = titleView.getLayoutParams();

        if ( titleLayoutParams instanceof ViewGroup.MarginLayoutParams )
          {
          ViewGroup.MarginLayoutParams titleMarginLayoutParams = (ViewGroup.MarginLayoutParams)titleLayoutParams;

          titleMarginLayoutParams.leftMargin = (int)resources.getDimension( R.dimen.action_bar_title_text_left_spacing );

          titleView.setLayoutParams( titleMarginLayoutParams );
          }
        }
      }

    }


  /*****************************************************
   *
   * Called when the activity becomes visible.
   *
   *****************************************************/
  @Override
  public void onStart()
    {
    super.onStart();

    mActivityIsVisible = true;
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
   * Called when the activity is no longer visible.
   *
   *****************************************************/
  @Override
  public void onStop()
    {
    mActivityIsVisible = false;

    super.onStop();
    }


  /*****************************************************
   *
   * Called when the activity is destroyed.
   *
   *****************************************************/
  @Override
  public void onDestroy()
    {
    mActivityIsVisible = false;

    ensureDialogGone();

    super.onDestroy();
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

    // If we successfully completed check-out then return the result back to any
    // calling activity, and exit so the user goes back to the original app.
    if ( requestCode == ACTIVITY_REQUEST_CODE_CHECKOUT && resultCode == RESULT_OK )
      {
      setResult( RESULT_OK );

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



  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns true if the activity is visible, false otherwise.
   *
   *****************************************************/
  public boolean isVisible()
    {
    return ( mActivityIsVisible );
    }


  /*****************************************************
   *
   * Displays a modal dialog.
   *
   *****************************************************/
  protected void displayModalDialog(
          int      titleTextResourceId,
          String   messageText,
          int      positiveTextResourceId,
          Runnable positiveRunnable,
          int      negativeTextResourceId,
          Runnable negativeRunnable )
    {
    // Don't do anything if the activity is no longer visible
    if ( ! mActivityIsVisible ) return;

    ensureDialogGone();

    DialogCallbackHandler callbackHandler = new DialogCallbackHandler( positiveRunnable, negativeRunnable );

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this )
            .setTitle( titleTextResourceId )
            .setMessage( messageText )
            .setCancelable( true )
            .setOnCancelListener( callbackHandler );

    if ( positiveTextResourceId != 0 ) alertDialogBuilder.setPositiveButton( positiveTextResourceId, callbackHandler );
    if ( negativeTextResourceId != 0 ) alertDialogBuilder.setNegativeButton( negativeTextResourceId, callbackHandler );

    mDialog = alertDialogBuilder.create();

    mDialog.show();
    }


  /*****************************************************
   *
   * Displays a modal dialog.
   *
   *****************************************************/
  public void displayModalDialog(
          int      titleTextResourceId,
          int      messageTextResourceId,
          int      positiveTextResourceId,
          Runnable positiveRunnable,
          int      negativeTextResourceId,
          Runnable negativeRunnable )
    {
    displayModalDialog( titleTextResourceId, getString( messageTextResourceId ), positiveTextResourceId, positiveRunnable, negativeTextResourceId, negativeRunnable );
    }


  /*****************************************************
   *
   * Ensures any dialog is gone.
   *
   *****************************************************/
  private void ensureDialogGone()
    {
    if ( mDialog != null )
      {
      mDialog.dismiss();

      mDialog = null;
      }
    }


  /*****************************************************
   *
   * Displays a fragment.
   *
   *****************************************************/
  protected void addFragment( AKiteFragment fragment, String tag )
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

      mCurrentFragment = (AKiteFragment)mFragmentManager.findFragmentByTag( entry.getName() );

      mCurrentFragment.onTop();
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
   * A runnable that exits the current activity.
   *
   *****************************************************/
  public class FinishRunnable implements Runnable
    {
    public FinishRunnable()
      {
      }

    @Override
    public void run()
      {
      finish();
      }
    }


  /*****************************************************
   *
   * Receives dialog callbacks and processes them
   * accordingly.
   *
   *****************************************************/
  private class DialogCallbackHandler implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener
    {
    private Runnable  mPositiveRunnable;
    private Runnable  mNegativeRunnable;


    DialogCallbackHandler( Runnable okRunnable, Runnable cancelRunnable )
      {
      mPositiveRunnable = okRunnable;
      mNegativeRunnable = cancelRunnable;
      }


    @Override
    public void onClick( DialogInterface dialog, int which )
      {
      switch ( which )
        {
        case DialogInterface.BUTTON_POSITIVE:

          if ( mPositiveRunnable != null )
            {
            mPositiveRunnable.run();
            }

          break;

        case DialogInterface.BUTTON_NEGATIVE:

          performNegativeAction();

          break;

        case DialogInterface.BUTTON_NEUTRAL:

          break;
        }

      ensureDialogGone();
      }


    @Override
    public void onCancel( DialogInterface dialog )
      {
      // Perform the same action as the negative button
      performNegativeAction();

      ensureDialogGone();
      }


    private void performNegativeAction()
      {
      if ( mNegativeRunnable != null )
        {
        mNegativeRunnable.run();
        }
      }
    }


  }

