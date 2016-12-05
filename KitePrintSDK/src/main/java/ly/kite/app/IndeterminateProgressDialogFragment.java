/*****************************************************
 *
 * IndeterminateProgressDialogFragment.java
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

package ly.kite.app;


///// Import(s) /////

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import ly.kite.R;
import ly.kite.widget.AEditTextEnforcer;
import ly.kite.widget.CVVEditTextEnforcer;
import ly.kite.widget.CardNumberEditTextEnforcer;
import ly.kite.widget.MonthEditTextEnforcer;
import ly.kite.widget.YearEditTextEnforcer;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment displays a dialog containing a progress
 * spinner.
 *
 *****************************************************/
public class IndeterminateProgressDialogFragment extends DialogFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public  final String  TAG                = "IndeterminateProgres...";

  static private final String  BUNDLE_KEY_MESSAGE = "message";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String           mMessage;
  private ICancelListener  mCancelListener;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates an instance of this fragment, with a message
   * passed in the arguments.
   *
   *****************************************************/
  static public IndeterminateProgressDialogFragment newInstance( String message )
    {
    IndeterminateProgressDialogFragment fragment = new IndeterminateProgressDialogFragment();

    Bundle arguments = new Bundle();

    arguments.putString( BUNDLE_KEY_MESSAGE, message );

    fragment.setArguments( arguments );

    return ( fragment );
    }


  /*****************************************************
   *
   * Creates an instance of this fragment, with a message
   * passed in the arguments.
   *
   *****************************************************/
  static public IndeterminateProgressDialogFragment newInstance( Context context, int messageResourceId )
    {
    return ( newInstance( context.getString( messageResourceId ) ) );
    }


  /*****************************************************
   *
   * Creates an instance of this fragment, with a message
   * passed in the arguments.
   *
   *****************************************************/
  static public IndeterminateProgressDialogFragment newInstance( Fragment fragment, int messageResourceId )
    {
    return ( newInstance( fragment.getString( messageResourceId ) ) );
    }


  ////////// Constructor(s) //////////


  ////////// DialogFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState)
    {
    super.onCreate( savedInstanceState );

    setStyle( STYLE_NO_TITLE, 0 );


    // Get the message

    Bundle arguments = getArguments();

    if ( arguments != null )
      {
      mMessage = arguments.getString( BUNDLE_KEY_MESSAGE );
      }
    }


  /*****************************************************
   *
   * Creates and returns the view for this fragment.
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.dialog_indeterminate_progress, container, false );

    TextView messageTextView = (TextView)view.findViewById( R.id.message_text_view );


    // Set the message
    if ( messageTextView != null ) messageTextView.setText( mMessage );


    return ( view );
    }


  /*****************************************************
   *
   * Called if the dialog is cancelled.
   *
   *****************************************************/
  @Override
  public void onCancel( DialogInterface dialogInterface )
    {
    if ( mCancelListener != null ) mCancelListener.onDialogCancelled( this );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Displays this progress dialog.
   *
   *****************************************************/
  public void show( Activity activity, ICancelListener cancelListener )
    {
    if ( cancelListener != null )
      {
      mCancelListener = cancelListener;

      setCancelable( true );
      }
    else
      {
      setCancelable( false );
      }

    show( activity.getFragmentManager(), TAG );
    }


  /*****************************************************
   *
   * Displays this progress dialog.
   *
   *****************************************************/
  public void show( Activity activity )
    {
    show( activity, null );
    }


  /*****************************************************
   *
   * Displays this progress dialog.
   *
   *****************************************************/
  public void show( Fragment fragment, ICancelListener cancelListener )
    {
    Activity activity = fragment.getActivity();

    if ( activity != null )
      {
      show( activity, cancelListener );
      }
    else
      {
      Log.e( TAG, "Unable to show dialog - no activity found" );
      }
    }


  /*****************************************************
   *
   * Displays this progress dialog.
   *
   *****************************************************/
  public void show( Fragment fragment )
    {
    show( fragment, null );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A cancel listener.
   *
   *****************************************************/
  public interface ICancelListener
    {
    public void onDialogCancelled( IndeterminateProgressDialogFragment dialogFragment );
    }

  }

