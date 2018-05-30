/*****************************************************
 *
 * LogOutDialogFragment.java
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

package ly.kite.journey;


///// Import(s) /////

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment displays a dialog that shows the length
 * of time before the customer session is ended.
 *
 *****************************************************/
public class LogOutDialogFragment extends DialogFragment implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "LogOutDialogFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private TextView  mTimeRemainingTextView;
  private Button    mCancelButton;
  private Button    mLogOutButton;

  private long      mTimeRemainingMillis;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


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


    setCancelable( false );
    }


  /*****************************************************
   *
   * Returns the view.
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup parent, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.dialog_inactivity, parent, false );

    mTimeRemainingTextView = (TextView)view.findViewById( R.id.time_remaining_text_view );
    mCancelButton          = (Button)view.findViewById( R.id.cancel_button );
    mLogOutButton          = (Button)view.findViewById( R.id.log_out_button );

    if ( mTimeRemainingMillis > 0 )
      {
      displayTimeRemaining();
      }

    mCancelButton.setOnClickListener( this );
    mLogOutButton.setOnClickListener( this );

    return ( view );
    }


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a view is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    Activity activity = getActivity();

    if ( activity instanceof ICallback )
      {
      ICallback callback = (ICallback)activity;

      if ( view == mCancelButton )
        {
        ///// Cancel /////

        callback.onCancelLogOut();
        }
      else if ( view == mLogOutButton )
        {
        ///// Log out /////

        callback.onLogOut();
        }
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the time remaining.
   *
   *****************************************************/
  public void setTimeRemaining( long timeRemainingMillis )
    {
    mTimeRemainingMillis = timeRemainingMillis;

    displayTimeRemaining();
    }


  /*****************************************************
   *
   * Displays the time remaining.
   *
   *****************************************************/
  private void displayTimeRemaining()
    {
    Activity activity = getActivity();

    if ( activity != null )
      {
      mTimeRemainingTextView.setText( activity.getString( R.string.kitesdk_time_remaining_format_string, mTimeRemainingMillis / 1000 ) );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback.
   *
   *****************************************************/
  public interface ICallback
    {
    public void onCancelLogOut();
    public void onLogOut();
    }

  }

