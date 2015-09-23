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
import android.os.Bundle;
import android.widget.AdapterView;


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
  public    static final String  TAG                                          = "AKiteFragment";

  private   static final String  BUNDLE_KEY_MANAGED_ADAPTOR_VIEW_POSITION     = "managedAdaptorViewPosition";
  protected static final String  BUNDLE_KEY_ASSETS_AND_QUANTITY_LIST          = "assetAndQuantityList";
  protected static final String  BUNDLE_KEY_PRODUCT                           = "product";


  public  static final long    MAX_ACCEPTED_PRODUCT_AGE_MILLIS          = 1000 * 60 * 60;  // 1 hour



  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected AKiteActivity   mKiteActivity;

  private   AdapterView<?>  mManagedAdaptorView;
  private   int             mManagedAdaptorViewPosition;


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
   * Called to save the fragment's state.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    // If we are managing an adaptor view - save its state in the bundle
    if ( mManagedAdaptorView != null )
      {
      outState.putInt( BUNDLE_KEY_MANAGED_ADAPTOR_VIEW_POSITION, mManagedAdaptorView.getFirstVisiblePosition() );
      }
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


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

