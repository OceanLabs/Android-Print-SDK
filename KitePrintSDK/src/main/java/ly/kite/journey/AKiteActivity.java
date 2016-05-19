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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.CatalogueLoader;
import ly.kite.image.ImageAgent;
import ly.kite.journey.creation.imagesource.ImageSourceFragment;
import ly.kite.util.StringUtils;


///// Class Declaration /////

/*****************************************************
 *
 * This abstract class is the base class for activities
 * in the Kite SDK. It provides some common functionality.
 *
 *****************************************************/
public abstract class AKiteActivity extends Activity implements FragmentManager.OnBackStackChangedListener,
                                                                View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                                      = "AKiteActivity";

  static public  final String INTENT_EXTRA_NAME_IMAGE_SPEC_LIST             = KiteSDK.INTENT_PREFIX + ".imageSpecList";

  static public  final int     NO_BUTTON                                    = 0;

  static public  final int     ACTIVITY_REQUEST_CODE_ADD_TO_BASKET          = 10;
  static public  final int     ACTIVITY_REQUEST_CODE_GO_TO_BASKET           = 11;
  static public  final int     ACTIVITY_REQUEST_CODE_EDIT_BASKET_ITEM       = 12;
  static public  final int     ACTIVITY_REQUEST_CODE_CHECKOUT               = 20;
  static public  final int     ACTIVITY_REQUEST_CODE_CREATE                 = 30;
  static public  final int     ACTIVITY_REQUEST_CODE_GET_ADDRESS            = 40;
  static public  final int     ACTIVITY_REQUEST_CODE_GET_CONTACT_DETAILS    = 45;
  static public  final int     ACTIVITY_REQUEST_CODE_SELECT_DEVICE_IMAGE    = 50;
  static public  final int     ACTIVITY_REQUEST_CODE_SELECT_INSTAGRAM_IMAGE = 60;
  static public  final int     ACTIVITY_REQUEST_CODE_EDIT_IMAGE             = 70;

  static public  final int     ACTIVITY_RESULT_CODE_CONTINUE_SHOPPING       = 15;
  static public  final int     ACTIVITY_RESULT_CODE_CHECKED_OUT             = 25;
  static public  final int     ACTIVITY_RESULT_CODE_END_CUSTOMER_SESSION    = 35;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private   boolean           mActivityIsVisible;
  private   boolean           mCanAddFragment;

  private   AKiteFragment     mPendingFragment;
  private   String            mPendingFragmentTag;

  private   Dialog            mDialog;

  protected FragmentManager   mFragmentManager;

  protected AKiteFragment     mTopFragment;

  private   ImageView         mEndCustomerSessionImageView;
  private   Button            mCTABarLeftButton;
  private   Button            mCTABarRightButton;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns the string contents of an edit text. If the edit
   * text is empty (null or blank string) - returns null.
   *
   *****************************************************/
  static protected String getPopulatedStringOrNull( EditText editText )
    {
    if ( editText == null ) return ( null );

    Editable editable = editText.getText();

    if ( editable == null ) return ( null );

    String text = editable.toString();

    if ( text == null || text.trim().equals( "" ) ) return ( null );

    return ( text );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // TODO: Fix this dirty hack
    CatalogueLoader.getInstance( this );


    // Listen for changes to the fragment back stack

    mFragmentManager = getFragmentManager();

    mFragmentManager.addOnBackStackChangedListener( this );
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


    // Get references to any call-to-action bar buttons
    mCTABarLeftButton  = (Button)findViewById( R.id.cta_bar_left_button );
    mCTABarRightButton = (Button)findViewById( R.id.cta_bar_right_button );

    // Automatically add listeners
    if ( mCTABarLeftButton  != null ) mCTABarLeftButton.setOnClickListener( this );
    if ( mCTABarRightButton != null ) mCTABarRightButton.setOnClickListener( this );
    }


  /*****************************************************
   *
   * Called after the activity has been created.
   *
   *****************************************************/
  @Override
  protected void onPostCreate( Bundle savedInstanceState )
    {
    super.onPostCreate( savedInstanceState );

    // If we are being re-created - work out the top fragment.
    if ( savedInstanceState != null )
      {
      determineTopFragment();
      }
    }


  /*****************************************************
   *
   * Called when the activity becomes visible.
   *
   *****************************************************/
  @Override
  protected void onStart()
    {
    super.onStart();

    mActivityIsVisible = true;
    }


  /*****************************************************
   *
   * Called after the activity gains focus, and guaranteed
   * to be after the state is restored.
   *
   *****************************************************/
  @Override
  protected void onPostResume()
    {
    super.onPostResume();

    mCanAddFragment = true;


    // If we are waiting to add a fragment - do so now

    if ( mPendingFragment != null )
      {
      addFragment( mPendingFragment, mPendingFragmentTag );

      mPendingFragment    = null;
      mPendingFragmentTag = null;
      }
    }


  /*****************************************************
   *
   * Called to prepare the options menu.
   *
   *****************************************************/
  @Override
  public boolean onPrepareOptionsMenu( Menu menu )
    {
    boolean displayMenu = super.onPrepareOptionsMenu( menu );



    // If we have been supplied an end customer session icon - inflate the menu
    // and set the icon.

    String endCustomerSessionIconURL = KiteSDK.getInstance( this ).getEndCustomerSessionIconURL();

    if ( StringUtils.isNeitherNullNorBlank( endCustomerSessionIconURL ) )
      {
      // Only add the end customer session menu item if it has not already been added

      if ( menu.findItem( R.id.end_customer_session_item ) == null )
        {
        MenuInflater menuInflator = getMenuInflater();

        menuInflator.inflate( R.menu.end_customer_session, menu );

        MenuItem   endCustomerSessionMenuItem;
        View       actionView;

        if ( ( endCustomerSessionMenuItem   = menu.findItem( R.id.end_customer_session_item )                            ) != null &&
             ( actionView                   = endCustomerSessionMenuItem.getActionView()                                 ) != null &&
             ( mEndCustomerSessionImageView = (ImageView)actionView.findViewById( R.id.end_customer_session_image_view ) ) != null )
          {
          try
            {
            ImageAgent
                    .with( this )
                    .load( new URL( endCustomerSessionIconURL ), KiteSDK.IMAGE_CATEGORY_APP )
                    .reduceColourSpace()
                    .into( mEndCustomerSessionImageView );

            displayMenu = true;

            mEndCustomerSessionImageView.setOnClickListener( this );
            }
          catch ( MalformedURLException mue )
            {
            Log.e( LOG_TAG, "Unable to set end customer session icon", mue );
            }
          }
        }
      }


    return ( displayMenu );
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
    // If there is a top fragment and it consumes the back press, don't
    // do anything more.
    if ( mTopFragment != null && mTopFragment.onBackPressIntercepted() )
      {
      return;
      }


    // If this is the last fragment - call the hook
    if ( mFragmentManager.getBackStackEntryCount() <= 1 )
      {
      onBackFromLastFragment();

      return;
      }


    super.onBackPressed();
    }


  /*****************************************************
   *
   * Called when there is just one fragment left.
   *
   *****************************************************/
  protected void onBackFromLastFragment()
    {
    super.onBackPressed();
    }


  /*****************************************************
   *
   * Called to save the instance state.
   *
   *****************************************************/
  @Override
  protected void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    mCanAddFragment = false;
    }


  /*****************************************************
   *
   * Called when the activity no longer has focus.
   *
   *****************************************************/
  @Override
  protected void onPause()
    {
    super.onPause();

    mCanAddFragment = false;
    }


  /*****************************************************
   *
   * Called when the activity is no longer visible.
   *
   *****************************************************/
  @Override
  protected void onStop()
    {
    mCanAddFragment    = false;
    mActivityIsVisible = false;

    super.onStop();
    }


  /*****************************************************
   *
   * Called when the activity is destroyed.
   *
   *****************************************************/
  @Override
  protected void onDestroy()
    {
    mCanAddFragment    = false;
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
    // If we successfully completed check-out then return the result back to any
    // calling activity, and exit so the user goes back to the original app.

    if ( resultCode == ACTIVITY_RESULT_CODE_CHECKED_OUT ||
         resultCode == ACTIVITY_RESULT_CODE_END_CUSTOMER_SESSION )
      {
      setResult( resultCode );

      finish();

      return;
      }


    super.onActivityResult( requestCode, resultCode, data );
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
      onNoMoreFragments();
      }


    determineTopFragment();
    }


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Listens for clicks on a view.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mEndCustomerSessionImageView )
      {
      ///// End customer session /////

      showEndCustomerSessionDialog();

      return;
      }

    if ( view == mCTABarLeftButton )
      {
      ///// CTA bar left button /////

      onLeftButtonClicked();

      return;
      }

    if ( view == mCTABarRightButton )
      {
      ///// CTA bar right button /////

      onRightButtonClicked();

      return;
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Hides the on-screen keyboard.
   *
   *****************************************************/
  protected void hideKeyboard()
    {
    //getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN );

    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService( Activity.INPUT_METHOD_SERVICE );

    // Find the currently focused view, so we can grab the correct window token from it.
    View view = getCurrentFocus();

    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if ( view == null )
      {
      view = new View( this );
      }

    inputMethodManager.hideSoftInputFromWindow( view.getWindowToken(), 0 );
    }


  /*****************************************************
   *
   * Hides the on-screen keyboard but delayed.
   *
   *****************************************************/
  @SuppressWarnings( "NewAPI" )
  protected void hideKeyboardDelayed()
    {
    new Handler().post( new HideKeyboardRunnable() );
    }


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
   * Called when there are no more fragments on the back
   * stack.
   *
   *****************************************************/
  protected void onNoMoreFragments()
    {
    finish();
    }


  /*****************************************************
   *
   * Displays a modal dialog.
   *
   *****************************************************/
  public void displayModalDialog(
          String   titleText,
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
            .setTitle( titleText )
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
   * Displays a modal dialog.
   *
   *****************************************************/
  protected void displayModalDialog(
          int      titleTextResource,
          String   messageText,
          int      positiveTextResourceId,
          Runnable positiveRunnable,
          int      negativeTextResourceId,
          Runnable negativeRunnable )
    {
    displayModalDialog( getString( titleTextResource ),  messageText, positiveTextResourceId, positiveRunnable, negativeTextResourceId, negativeRunnable );
    }


  /*****************************************************
   *
   * Displays an error dialog.
   *
   *****************************************************/
  protected void showErrorDialog( String message )
    {
    displayModalDialog
            (
            R.string.alert_dialog_title_oops,
            message,
            R.string.OK,
            null,
            NO_BUTTON,
            null
            );
    }


  /*****************************************************
   *
   * Displays an error dialog.
   *
   *****************************************************/
  protected void showErrorDialog( int messageResourceId )
    {
    showErrorDialog( getString( messageResourceId ) );
    }


  /*****************************************************
   *
   * Displays an end session confirmation dialog.
   *
   *****************************************************/
  protected void showEndCustomerSessionDialog()
    {
    displayModalDialog
            (
            R.string.alert_dialog_title_end_customer_session,
            R.string.alert_dialog_message_end_customer_session,
            R.string.End_Session,
            new EndCustomerSessionRunnable(),
            R.string.Cancel,
            null
            );
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
   * Displays a fragment and adds it to the back stack.
   *
   *****************************************************/
  protected void addFragment( AKiteFragment fragment, String tag )
    {
    // If the instance state has been saved, then we don't want to commit any
    // fragment transaction - otherwise we will get an exception on some platforms.

    // However, we need to remember what was requested so that after any state
    // has been restored, we can then add the fragment.

    if ( mCanAddFragment )
      {
      mFragmentManager
        .beginTransaction()
              .replace( R.id.fragment_container, fragment, tag )
              .addToBackStack( tag )  // Use the tag as the name so we can find it later
        .commit();
      }
    else
      {
      mPendingFragment    = fragment;
      mPendingFragmentTag = tag;
      }

    }


  /*****************************************************
   *
   * Displays a fragment by replacing the current one.
   *
   *****************************************************/
  protected void replaceFragment( AKiteFragment fragment, String tag )
    {
    mFragmentManager
      .beginTransaction()
        .replace( R.id.fragment_container, fragment, tag )
      .commit();
    }


  /*****************************************************
   *
   * Works out what the current fragment is.
   *
   *****************************************************/
  private void determineTopFragment( int entryCount )
    {
    AKiteFragment lastTopFragment = mTopFragment;


    try
      {
      FragmentManager.BackStackEntry entry;


      // See if there is a top fragment

      int entryIndex = entryCount - 1;

      if ( entryCount > 0 && ( entry = mFragmentManager.getBackStackEntryAt( entryIndex ) ) != null )
        {
        mTopFragment = (AKiteFragment)mFragmentManager.findFragmentByTag( entry.getName() );

        if ( mTopFragment != null ) onNotifyTop( mTopFragment );
        }
      else
        {
        mTopFragment = null;
        }


      // Notify all other fragments that they are not top-most

      for ( entryIndex --; entryIndex >= 0; entryIndex -- )
        {
        entry = mFragmentManager.getBackStackEntryAt( entryIndex );

        if ( entry != null )
          {
          AKiteFragment fragment = (AKiteFragment)mFragmentManager.findFragmentByTag( entry.getName() );

          if ( fragment != null )
            {
            onNotifyNotTop( fragment );
            }
          }
        }

      }
    catch ( Exception e )
      {
      Log.e( LOG_TAG, "Could not determine current fragment", e );

      mTopFragment = null;
      }

    //Log.d( LOG_TAG, "Current fragment = " + mTopFragment );
    }


  /*****************************************************
   *
   * Works out what the current fragment is.
   *
   *****************************************************/
  private void determineTopFragment()
    {
    determineTopFragment( mFragmentManager.getBackStackEntryCount() );
    }


  /*****************************************************
   *
   * Called with the current top-most fragment.
   *
   * Will never be called with a null fragment.
   *
   *****************************************************/
  protected void onNotifyTop( AKiteFragment topFragment )
    {
    topFragment.onTop();
    }


  /*****************************************************
   *
   * Called when fragments are no longer top-most.
   *
   * Will never be called with a null fragment.
   *
   *****************************************************/
  protected void onNotifyNotTop( AKiteFragment fragment )
    {
    fragment.onNotTop();
    }


  /*****************************************************
   *
   * Removes a fragment.
   *
   *****************************************************/
  protected void popFragment()
    {
    mFragmentManager.popBackStack();
    }


  /*****************************************************
   *
   * Removes a fragment without triggering the back stack
   * listener.
   *
   *****************************************************/
  protected void popFragmentSecretly()
    {
    mFragmentManager.removeOnBackStackChangedListener( this );

    mFragmentManager.popBackStackImmediate( ImageSourceFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE );

    // Restore the back stack listener
    mFragmentManager.addOnBackStackChangedListener( this );
    }


  /*****************************************************
   *
   * Returns the left button.
   *
   *****************************************************/
  protected Button getLeftButton()
    {
    return ( mCTABarLeftButton );
    }


  /*****************************************************
   *
   * Returns the right button.
   *
   *****************************************************/
  protected Button getRightButton()
    {
    return ( mCTABarRightButton );
    }


  /*****************************************************
   *
   * Sets the text of a button.
   *
   *****************************************************/
  private void setButtonText( Button button, int textResourceId )
    {
    if ( button != null ) button.setText( textResourceId );
    }


  /*****************************************************
   *
   * Sets the text of any left button.
   *
   *****************************************************/
  protected void setLeftButtonText( int textResourceId )
    {
    setButtonText( getLeftButton(), textResourceId );
    }


  /*****************************************************
   *
   * Sets the text of any right button.
   *
   *****************************************************/
  protected void setRightButtonText( int textResourceId )
    {
    setButtonText( getRightButton(), textResourceId );
    }


  /*****************************************************
   *
   * Sets the colour of any left button.
   *
   *****************************************************/
  private void setButtonColourRes( Button button, int colourResourceId )
    {
    if ( button != null )
      {
      Resources resources = getResources();

      button.setTextColor( resources.getColor( colourResourceId ) );
      }
    }


  /*****************************************************
   *
   * Sets the colour of any left button.
   *
   *****************************************************/
  protected void setLeftButtonColourRes( int colourResourceId )
    {
    setButtonColourRes( getLeftButton(), colourResourceId );
    }


  /*****************************************************
   *
   * Sets the colour of any right button.
   *
   *****************************************************/
  protected void setRightButtonColourRes( int colourResourceId )
    {
    setButtonColourRes( getRightButton(), colourResourceId );
    }


  /*****************************************************
   *
   * Sets the enabled state of a button.
   *
   *****************************************************/
  private void setButtonEnabled( Button button, boolean enabled )
    {
    if ( button != null ) button.setEnabled( enabled );
    }


  /*****************************************************
   *
   * Sets the enabled state of any left button.
   *
   *****************************************************/
  protected void setLeftButtonEnabled( boolean enabled )
    {
    setButtonEnabled( getLeftButton(), enabled );
    }


  /*****************************************************
   *
   * Sets the enabled state of any right button.
   *
   *****************************************************/
  protected void setRightButtonEnabled( boolean enabled )
    {
    setButtonEnabled( getRightButton(), enabled );
    }


  /*****************************************************
   *
   * Sets the visible state of a button.
   *
   *****************************************************/
  private void setButtonVisible( Button button, boolean visible )
    {
    if ( button != null ) button.setVisibility( visible ? View.VISIBLE : View.INVISIBLE );
    }


  /*****************************************************
   *
   * Sets the visible state of any left button.
   *
   *****************************************************/
  protected void setLeftButtonVisible( boolean visible )
    {
    setButtonVisible( getLeftButton(), visible );
    }


  /*****************************************************
   *
   * Sets the visible state of any right button.
   *
   *****************************************************/
  protected void setRightButtonVisible( boolean visible )
    {
    setButtonVisible( getRightButton(), visible );
    }


  /*****************************************************
   *
   * Called when the left CTA button is clicked.
   *
   *****************************************************/
  protected void onLeftButtonClicked()
    {
    }


  /*****************************************************
   *
   * Called when the right CTA button is clicked.
   *
   *****************************************************/
  protected void onRightButtonClicked()
    {
    }


  /*****************************************************
   *
   * Creates and returns a parameter transferer.
   *
   *****************************************************/
  protected ParameterTransferer using( JSONObject jsonObject, KiteSDK.Scope scope )
    {
    return ( new ParameterTransferer( jsonObject, scope ) );
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
   * A runnable that ends the customer session.
   *
   *****************************************************/
  public class EndCustomerSessionRunnable implements Runnable
    {
    public EndCustomerSessionRunnable()
      {
      }

    @Override
    public void run()
      {
      KiteSDK.getInstance( AKiteActivity.this ).endCustomerSession();

      setResult( ACTIVITY_RESULT_CODE_END_CUSTOMER_SESSION );

      finish();
      }
    }


  /*****************************************************
   *
   * A runnable that hides the on-screen keyboard.
   *
   *****************************************************/
  public class HideKeyboardRunnable implements Runnable
    {
    @Override
    public void run()
      {
      hideKeyboard();
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


  /*****************************************************
   *
   * A parameter transferer.
   *
   *****************************************************/
  protected class ParameterTransferer
    {
    private JSONObject     mSourceJSONObject;
    private KiteSDK.Scope  mTargetScope;

    private KiteSDK        mKiteSDK;

    ParameterTransferer( JSONObject sourceJSONObject, KiteSDK.Scope targetScope )
      {
      mSourceJSONObject = sourceJSONObject;
      mTargetScope      = targetScope;

      mKiteSDK = KiteSDK.getInstance( AKiteActivity.this );
      }


    /*****************************************************
     *
     * Retrieves a string parameter from a JSON object, and
     * saves it as a parameter.
     *
     *****************************************************/
    public ParameterTransferer transferString( String sourceParameterName, String targetParameterName )
      {
      String stringValue = mSourceJSONObject.optString( sourceParameterName );

      if ( stringValue == null )
        {
        Log.e( LOG_TAG, "Unable to find parameter " + sourceParameterName + " in " + mSourceJSONObject.toString() );

        // TODO: Decide what we want to do here

        return ( this );
        }

      mKiteSDK.setAppParameter( mTargetScope, targetParameterName, stringValue );

      return ( this );
      }


    /*****************************************************
     *
     * Retrieves a string parameter from a JSON object, and
     * saves it as a parameter.
     *
     *****************************************************/
    public ParameterTransferer transferString( String parameterName )
      {
      return ( transferString( parameterName, parameterName ) );
      }

    }


  }

