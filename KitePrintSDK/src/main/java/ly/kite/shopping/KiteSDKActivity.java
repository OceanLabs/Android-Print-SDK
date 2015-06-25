/*****************************************************
 *
 * KiteSDKActivity.java
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
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import ly.kite.R;

/*****************************************************
 *
 * This class is the base class for activities in the
 * Kite SDK. It provides some common functionality.
 *
 *****************************************************/
public abstract class KiteSDKActivity extends Activity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "KiteSDKActivity";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

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


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Displays a model dialog.
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
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this )
            .setTitle( titleTextResourceId )
            .setMessage( messageTextResourceId )
            .setCancelable( true );

    DialogCallbackHandler callbackHandler = new DialogCallbackHandler( positiveRunnable, negativeRunnable );

    if ( positiveTextResourceId != 0 ) alertDialogBuilder.setPositiveButton( positiveTextResourceId, callbackHandler );
    if ( negativeTextResourceId != 0 ) alertDialogBuilder.setNegativeButton( negativeTextResourceId, callbackHandler );

    alertDialogBuilder.show();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A runnable that exits the current activity.
   *
   *****************************************************/
  protected class FinishRunnable implements Runnable
    {
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
  private class DialogCallbackHandler implements DialogInterface.OnClickListener
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

          if ( mNegativeRunnable != null )
            {
            mNegativeRunnable.run();
            }

          break;

        case DialogInterface.BUTTON_NEUTRAL:

          break;
        }
      }
    }


  }

