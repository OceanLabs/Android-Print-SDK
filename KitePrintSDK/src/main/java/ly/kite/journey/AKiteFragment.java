/*****************************************************
 *
 * AKiteFragment.java
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
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.app.IndeterminateProgressDialogFragment;
import ly.kite.catalogue.Catalogue;
import ly.kite.ordering.OrderingDataAgent;
import ly.kite.journey.basket.BasketActivity;
import ly.kite.widget.LabelledImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This is the parent class of Kite SDK fragments.
 *
 *****************************************************/
abstract public class AKiteFragment extends Fragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private   final String  LOG_TAG                                      = "AKiteFragment";

  static private   final String  BUNDLE_KEY_MANAGED_ADAPTOR_VIEW_POSITION     = "managedAdaptorViewPosition";
  static protected final String  BUNDLE_KEY_PRODUCT                           = "product";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected AKiteActivity                        mKiteActivity;

  private   IndeterminateProgressDialogFragment  mProgressDialogFragment;

  private   MenuItem                             mBasketMenuItem;

  private   AdapterView<?>                       mManagedAdaptorView;
  private   int                                  mManagedAdaptorViewPosition;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Fragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // See if we have saved any managed adaptor view position

    if ( savedInstanceState != null )
      {
      mManagedAdaptorViewPosition = savedInstanceState.getInt( BUNDLE_KEY_MANAGED_ADAPTOR_VIEW_POSITION );
      }
    }


  /*****************************************************
   *
   * Called when the fragment is attached to an activity.
   *
   *****************************************************/
  @Override
  public void onAttach( Activity activity )
    {
    super.onAttach( activity );

    mKiteActivity = (AKiteActivity)activity;
    }


  /*****************************************************
   *
   * Called when the fragment is visible and actively running.
   *
   *****************************************************/
  @Override
  public void onResume()
    {
    super.onResume();

    setUpBasketActionIcon();
    }


  /*****************************************************
   *
   * Called when an action bar item is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem menuItem )
    {
    int itemId = menuItem.getItemId();

    if ( itemId == R.id.basket_menu_item )
      {
      BasketActivity.startForResult( mKiteActivity, AKiteActivity.ACTIVITY_REQUEST_CODE_GO_TO_BASKET );

      return ( true );
      }

    return ( super.onOptionsItemSelected( menuItem ) );
    }


  /*****************************************************
   *
   * Called to save the fragment's state.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    if ( KiteSDK.DEBUG_SAVE_INSTANCE_STATE ) Log.d( LOG_TAG, "--> onSaveInstanceState( outState = " + outState + " )" );

    // If we are managing an adaptor view - save its state in the bundle
    if ( mManagedAdaptorView != null )
      {
      outState.putInt( BUNDLE_KEY_MANAGED_ADAPTOR_VIEW_POSITION, mManagedAdaptorView.getFirstVisiblePosition() );
      }

    if ( KiteSDK.DEBUG_SAVE_INSTANCE_STATE ) Log.d( LOG_TAG, "<-- onSaveInstanceState( outState = " + outState + " )" );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets an adaptor view who's position we want to maintain
   * when changing orientation, or when leaving / coming
   * back to this screen.
   *
   *****************************************************/
  protected void setManagedAdaptorView( AdapterView adaptorView )
    {
    mManagedAdaptorView = adaptorView;
    }


  /*****************************************************
   *
   * Called when the back key is pressed. The fragment
   * can either intercept it, or ignore it - in which case
   * the default behaviour is performed.
   *
   *****************************************************/
  public boolean onBackPressIntercepted()
    {
    return ( false );
    }


  /*****************************************************
   *
   * Saves an adapter view position.
   *
   *****************************************************/
  protected void onSaveManagedAdaptorViewPosition( int position )
    {
    mManagedAdaptorViewPosition = position;
    }


  /*****************************************************
   *
   * Saves an adapter view position.
   *
   *****************************************************/
  protected void onSaveManagedAdaptorViewPosition()
    {
    if ( mManagedAdaptorView != null )
      {
      onSaveManagedAdaptorViewPosition( mManagedAdaptorView.getFirstVisiblePosition() );
      }
    }


  /*****************************************************
   *
   * Called when the adaptor is set up.
   *
   *****************************************************/
  protected void onRestoreManagedAdaptorViewPosition()
    {
    if ( mManagedAdaptorView != null )
      {
      if ( mManagedAdaptorViewPosition >= 0 && mManagedAdaptorViewPosition < mManagedAdaptorView.getCount() )
        {
        mManagedAdaptorView.setSelection( mManagedAdaptorViewPosition );
        }
      }
    }


  /*****************************************************
   *
   * Called when the fragment is top-most. Can be used as
   * an equivalent to onResume, to set the title etc.
   *
   *****************************************************/
  public void onTop()
    {
    setUpBasketActionIcon();
    }


  /*****************************************************
   *
   * Called when the fragment is not top-most. Can be
   * used to free resources.
   *
   *****************************************************/
  public void onNotTop()
    {
    }


  /*****************************************************
   *
   * Called to prepare the menu.
   *
   *****************************************************/
  @Override
  public void onPrepareOptionsMenu( Menu menu )
    {
    mBasketMenuItem = menu.findItem( R.id.basket_menu_item );

    setUpBasketActionIcon();
    }


  /*****************************************************
   *
   * Sets up the action bar basket icon.
   *
   *****************************************************/
  protected void setUpBasketActionIcon()
    {
    if ( mBasketMenuItem != null )
      {
      mBasketMenuItem.setIcon( getBasketActionIcon() );
      }
    }


  /*****************************************************
   *
   * Returns a basket action icon.
   *
   *****************************************************/
  protected Drawable getBasketActionIcon()
    {
    // Get the number of items in the basket

    int itemCount = OrderingDataAgent.getInstance( getActivity() ).getItemCount();


    Resources resources = getResources();

    Bitmap basketBitmap = BitmapFactory.decodeResource( resources, ( itemCount > 0 ? R.drawable.ic_basket_items : R.drawable.ic_basket_empty ) );

    if ( basketBitmap == null ) return ( null );

    int width  = basketBitmap.getWidth();
    int height = basketBitmap.getHeight();

    Bitmap targetBitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );

    Canvas targetCanvas = new Canvas( targetBitmap );

    Rect rect = new Rect( 0, 0, width, height );

    // Draw the base bitmap
    targetCanvas.drawBitmap( basketBitmap, rect, rect, null );


    if ( itemCount > 0 )
      {
      // Draw the quantity text

      float quantityTextSize = resources.getDimension( R.dimen.basket_action_icon_quantity_text_size );

      Paint paint = new Paint();
      paint.setTextSize( quantityTextSize );
      paint.setColor( resources.getColor( R.color.basket_action_icon_quantity_text ) );
      paint.setTextAlign( Paint.Align.CENTER );


      TypedValue insetValue = new TypedValue();

      resources.getValue( R.dimen.basket_action_icon_quantity_text_horizontal_inset, insetValue, true );
      float horizontalInsetProportion = insetValue.getFloat();

      resources.getValue( R.dimen.basket_action_icon_quantity_text_vertical_inset, insetValue, true );
      float verticalInsetProportion = insetValue.getFloat();

      targetCanvas.drawText( String.valueOf( itemCount ), width - ( horizontalInsetProportion * width ), verticalInsetProportion * height, paint );
      }


    return ( new BitmapDrawable( resources, targetBitmap ) );
    }


  /*****************************************************
   *
   * Sets a theme colour.
   *
   *****************************************************/
  protected void setThemeColour( int themeColour, LabelledImageView labelledImageView )
    {
    if ( themeColour != Catalogue.NO_COLOUR && labelledImageView != null )
      {
      labelledImageView.setForcedLabelColour( themeColour );
      }
    }


  /*****************************************************
   *
   * Sets a theme colour.
   *
   *****************************************************/
  protected void setThemeColour( int themeColour, View view )
    {
    if ( themeColour != Catalogue.NO_COLOUR && view != null )
      {
      view.setBackgroundColor( themeColour );
      }
    }


  /*****************************************************
   *
   * Sets a theme colour.
   *
   *****************************************************/
  protected void setThemeColour( int themeColour, View contentView, int viewId )
    {
    if ( contentView != null )
      {
      View view = contentView.findViewById( viewId );

      setThemeColour( themeColour, view );
      }
    }


  /*****************************************************
   *
   * Ensures that a progress dialog is showing.
   *
   *****************************************************/
  protected void displayProgressDialog( int messageResourceId )
    {
    displayProgressDialog( messageResourceId, null );
    }


  /*****************************************************
   *
   * Ensures that a progress dialog is showing.
   *
   *****************************************************/
  protected void displayProgressDialog( int messageResourceId, IndeterminateProgressDialogFragment.ICancelListener cancelListener )
    {
    if ( mProgressDialogFragment == null )
      {
      mProgressDialogFragment = IndeterminateProgressDialogFragment.newInstance( this, R.string.kitesdk_Loading_catalogue);

      mProgressDialogFragment.show( this, cancelListener );
      }
    }


  /*****************************************************
   *
   * Ensures that any display progress dialog is hidden.
   *
   *****************************************************/
  protected void hideProgressDialog()
    {
    if ( mProgressDialogFragment != null )
      {
      mProgressDialogFragment.dismissAllowingStateLoss();

      mProgressDialogFragment = null;
      }
    }


  /*****************************************************
   *
   * Removes this fragment.
   *
   *****************************************************/
  protected void finish()
    {
    if ( mKiteActivity != null ) mKiteActivity.onBackPressed();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

