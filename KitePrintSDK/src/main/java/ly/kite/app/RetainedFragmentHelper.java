/*****************************************************
 *
 * RetainedFragmentHelper.java
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

package ly.kite.app;


///// Import(s) /////


///// Class Declaration /////

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import ly.kite.KiteSDK;

/*****************************************************
 *
 * This class provides helper methods for retained fragments,
 * both standard and dialog.
 *
 *****************************************************/
public class RetainedFragmentHelper<C>
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "RetainedFragmentHelper";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Fragment        mFragment;

  private AStateNotifier  mStateNotifier;



  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  RetainedFragmentHelper( Fragment fragment )
    {
    mFragment = fragment;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  public void onCreate( Bundle savedInstanceState )
    {
    // Make sure we are retained even if the activity is destroyed, e.g. during
    // orientation changes.
    mFragment.setRetainInstance( true );
    }


  /*****************************************************
   *
   * Adds the fragment to the activity.
   *
   *****************************************************/
  public void addTo( Activity activity, String tag )
    {
    FragmentManager fragmentManager = activity.getFragmentManager();

    if ( fragmentManager != null )
      {
      fragmentManager
        .beginTransaction()
          .add( mFragment, tag )
        .commit();
      }
    }


  /*****************************************************
   *
   * Called when the fragment is attached to an activity.
   *
   *****************************************************/
  public void onAttach( Activity activity )
    {
    if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( LOG_TAG, "onAttach( activity = " + activity + " )" );

    checkNotifyState();
    }


  /*****************************************************
   *
   * Called when a target fragment is set.
   *
   *****************************************************/
  public void setTargetFragment( Fragment fragment, int requestCode )
    {
    if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( LOG_TAG, "setTargetFragment( fragment = " + fragment + ", requestCode = " + requestCode + " )" );

    checkNotifyState();
    }


  /*****************************************************
   *
   * Checks for any previous update, and re-runs it.
   *
   *****************************************************/
  private void checkNotifyState()
    {
    if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( LOG_TAG, "checkNotifyState() mStateNotifier = " + mStateNotifier );

    if ( mStateNotifier != null )
      {
      // If we are attached to an activity that is the correct callback type -
      // notify it of the current state.

      C activityCallback = getActivityCallback();

      if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( LOG_TAG, "  activityCallback = " + activityCallback );

      if ( activityCallback != null ) mStateNotifier.notify( activityCallback );


      // If we have a target fragment that is the correct callback type -
      // notify it of the current state.

      C fragmentCallback = getFragmentCallback();

      if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( LOG_TAG, "  fragmentCallback = " + fragmentCallback );

      if ( fragmentCallback != null ) mStateNotifier.notify( fragmentCallback );
      }


    // We don't clear the state; the callback will get re-notified as many times as
    // we are re-attached.
    }


  /*****************************************************
   *
   * Returns any activity cast to the callback type.
   *
   *****************************************************/
  private C getActivityCallback()
    {
    Activity activity;

    if ( ( activity = mFragment.getActivity() ) != null )
      {
      try
        {
        return ( (C)activity );
        }
      catch ( ClassCastException cce )
        {
        // Fall through
        }
      }

    return ( null );
    }


  /*****************************************************
   *
   * Returns any target fragment cast to the callback type
   *
   *****************************************************/
  private C getFragmentCallback()
    {
    Fragment fragment;

    if ( ( fragment = mFragment.getTargetFragment() ) != null )
      {
      try
        {
        return ( (C)fragment );
        }
      catch ( ClassCastException cce )
        {
        // Fall through
        }
      }

    return ( null );
    }


  /*****************************************************
   *
   * Sets the current state notifier. The notifier may
   * get called twice, if there is both an attached activity
   * and a target fragment of the correct callback type.
   *
   *****************************************************/
  protected void setState( AStateNotifier stateNotifier )
    {
    if ( KiteSDK.DEBUG_RETAINED_FRAGMENT ) Log.d( LOG_TAG, "setState( stateNotifier = " + stateNotifier + " )" );

    mStateNotifier = stateNotifier;

    checkNotifyState();
    }


  /*****************************************************
   *
   * Removes the fragment from the activity.
   *
   *****************************************************/
  public void removeFrom( Activity activity )
    {
    FragmentManager fragmentManager = activity.getFragmentManager();

    if ( fragmentManager != null )
      {
      fragmentManager
        .beginTransaction()
          .remove( mFragment )
        .commit();
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An interface for state notification.
   *
   *****************************************************/
  abstract public class AStateNotifier
    {
    abstract public void notify( C callback );
    }

  }

