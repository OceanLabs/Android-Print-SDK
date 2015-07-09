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

package ly.kite.shopping;


///// Import(s) /////


///// Class Declaration /////

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.print.ProductCache;

/*****************************************************
 *
 * This abstract class is the base class for activities
 * in the Kite SDK. It provides some common functionality.
 *
 *****************************************************/
public abstract class AKiteActivity extends Activity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private   static final String  LOG_TAG                         = "AKiteActivity";

  protected static final int     DONT_DISPLAY_BUTTON             = 0;

  protected static final String  IMAGE_CLASS_STRING_PRODUCT_ITEM = "product_item";

  protected static final int     ACTIVITY_REQUEST_CODE_CHECKOUT = 1;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Dialog  mDialog;


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
    ProductCache.getInstance( this );
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


    // If we can find a home as up icon and/or a title - set the spacing manually

    Resources resources = getResources();

//    int homeAsUpViewId = resources.getIdentifier( "up", "id", "android" );
//
//    if ( homeAsUpViewId != 0 )
//      {
//      View homeAsUpView = findViewById( homeAsUpViewId );
//
//      if ( homeAsUpView != null )
//        {
//        ViewGroup.LayoutParams homeAsUpLayoutParams = homeAsUpView.getLayoutParams();
//
//        if ( homeAsUpLayoutParams instanceof ViewGroup.MarginLayoutParams )
//          {
//          ViewGroup.MarginLayoutParams homeAsUpMarginLayoutParams = (ViewGroup.MarginLayoutParams)homeAsUpLayoutParams;
//
//          homeAsUpMarginLayoutParams.leftMargin = (int)resources.getDimension( R.dimen.action_bar_home_as_up_icon_left_spacing );
//
//          homeAsUpView.setLayoutParams( homeAsUpMarginLayoutParams );
//          }
//        }
//      }


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
   * Called when the home action is clicked.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    switch ( item.getItemId() )
      {
      case android.R.id.home:
        finish();
        return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called when the activity is destroyed.
   *
   *****************************************************/
  @Override
  public void onDestroy()
    {
    super.onDestroy();

    ensureDialogGone();
    }


  ////////// Method(s) //////////

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
  protected void displayModalDialog(
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



  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A runnable that exits the current activity.
   *
   *****************************************************/
  protected class FinishRunnable implements Runnable
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

