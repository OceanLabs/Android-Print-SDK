/*****************************************************
 *
 * ARetainedDialogFragment.java
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

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import ly.kite.KiteSDK;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the parent class for retained dialog fragments.
 * It provides helper methods for attaching to activities,
 * and providing callbacks.
 *
 *****************************************************/
abstract public class ARetainedDialogFragment extends DialogFragment
  {
  ////////// Static Constant(s) //////////

  static private final String LOG_TAG = "ARetainedDialogFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected RetainedFragmentHelper  mRetainedFragmentHelper;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Tries to find this fragment, and returns it.
   *
   *****************************************************/
  static protected Fragment find( Activity activity, String tag, Class<? extends ARetainedDialogFragment> fragmentClass )
    {
    if ( activity == null )
      {
      Log.e( LOG_TAG, "Null activity supplied" );

      return ( null );
      }


    FragmentManager fragmentManager = activity.getFragmentManager();

    if ( fragmentManager != null )
      {
      Fragment foundFragment = fragmentManager.findFragmentByTag( tag );

      if ( foundFragment != null )
        {
        Class<?> foundFragmentClass = foundFragment.getClass();

        if ( foundFragmentClass.equals( fragmentClass ) )
          {
          return ( foundFragment );
          }
        }
      }

    return ( null );
    }


  ////////// Constructor(s) //////////

  public ARetainedDialogFragment( Class<?> callbackClass )
    {
    mRetainedFragmentHelper = new RetainedFragmentHelper( this, callbackClass );
    }


  ////////// DialogFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    mRetainedFragmentHelper.onCreate( savedInstanceState );
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

    mRetainedFragmentHelper.onAttach( activity );
    }


  /*****************************************************
   *
   * Called when a target fragment is set.
   *
   *****************************************************/
  @Override
  public void setTargetFragment( Fragment fragment, int requestCode )
    {
    super.setTargetFragment( fragment, requestCode );

    mRetainedFragmentHelper.onSetTargetFragment( fragment, requestCode );
    }


  /*****************************************************
   *
   * Called when the view is destroyed.
   *
   *****************************************************/
  @Override
  public void onDestroyView()
    {
    // Work-around for dialog fragment not being retained
    // following orientation change.
    if ( getDialog() != null && getRetainInstance() )
      {
      getDialog().setDismissMessage( null );
      }

    super.onDestroyView();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds this fragment to the activity.
   *
   *****************************************************/
  public void addTo( Activity activity, String tag )
    {
    mRetainedFragmentHelper.addTo( activity, tag );
    }


  /*****************************************************
   *
   * Sets the current state notifier. The notifier may
   * get called twice, if there is both an attached activity
   * and a target fragment of the correct callback type.
   *
   *****************************************************/
  protected void setStateNotifier( RetainedFragmentHelper.AStateNotifier stateNotifier )
    {
    mRetainedFragmentHelper.setState( stateNotifier );
    }


  /*****************************************************
   *
   * Removes this fragment from the activity.
   *
   *****************************************************/
  public void remove()
    {
    Activity activity = getActivity();

    if ( activity != null ) mRetainedFragmentHelper.removeFrom( activity );

    super.setTargetFragment( null, 0 );
    }


  ////////// Inner Class(es) //////////

  }

