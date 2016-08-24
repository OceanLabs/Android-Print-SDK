/*****************************************************
 *
 * AGCMListenerService.java
 *
 *
 * Copyright (c) 2015 Kite Tech Ltd. https://www.kite.ly
 *
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

package ly.kite.gcm;


///// Import(s) /////

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ly.kite.KiteSDK;


///// Class Declaration /////

/*****************************************************
 *
 * This service receives GCM messages.
 *
 *****************************************************/
abstract public class AGCMListenerService extends GcmListenerService
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                          = "AGCMListenerService";

  static private final String  PARAMETER_NAME_LAST_MESSAGE      = "last_message";
  static private final String  PARAMETER_NAME_LAST_MESSAGE_DATE = "last_message_date";

  static private final String  DEFAULT_MESSAGE                  = "";
  static private final String  DEFAULT_MESSAGE_DATE             = "00000000";
  static private final String  DATE_FORMAT                      = "yyyyMMdd";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a notification to launch an activity.
   *
   *****************************************************/
  static protected void showNotification( Context context, String from, String message, Class<? extends Activity> activityClass, int requestCode, int iconResourceId, int titleResourceId, int notificationId )
    {
    // We want to go into the home activity when the notification is clicked

    Intent intent = new Intent( context, activityClass );

    PendingIntent pendingIntent = PendingIntent.getActivity( context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT );


    Notification.Builder notificationBuilder = new Notification.Builder( context );

    notificationBuilder
            .setSmallIcon( iconResourceId )
            .setContentTitle( context.getString( titleResourceId ) )
            .setContentText( message )
            .setContentIntent( pendingIntent )
            .setAutoCancel( true );


    NotificationManager notificationManager = (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );

    notificationManager.notify( notificationId, notificationBuilder.getNotification() );
    }


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when a GCM message is received.
   *
   *****************************************************/
  public void onMessageReceived( Context context, String from, Bundle data, int preferenceKeyResourceId )
    {
    String message = data.getString( "message" );

    Log.i( LOG_TAG, "From    : " + from );
    Log.i( LOG_TAG, "Message : " + message );


    // We don't do anything with topics at the moment, so every message is assumed
    // to be a normal downstream message.


    // Check if the user is OK to receive notification messages

    if ( preferenceKeyResourceId != 0 )
      {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

      if ( ! sharedPreferences.getBoolean( context.getString( preferenceKeyResourceId ), true ) )
        {
        Log.d( LOG_TAG, "User has disabled push messages - message discarded" );

        return;
        }
      }


    // Check that the message is new, i.e. not a duplicate. We do this by checking for the same
    // message within a certain time frame.

    // Get last message and received date

    KiteSDK kiteSDK = KiteSDK.getInstance( context );

    String lastMessage           = kiteSDK.getStringAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_LAST_MESSAGE,      DEFAULT_MESSAGE );
    String lastMessageDateString = kiteSDK.getStringAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_LAST_MESSAGE_DATE, DEFAULT_MESSAGE_DATE );


    // Get the current date
    Date             currentDate       = new Date();
    SimpleDateFormat dateFormat        = new SimpleDateFormat( DATE_FORMAT );
    String           messageDateString = dateFormat.format( currentDate );


    // Save the new message date
    kiteSDK.setAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_LAST_MESSAGE_DATE, messageDateString );


    // If the new message is identical to the last message, we only accept it if it was
    // received before today.

    if ( message != null && message.equals( lastMessage ) )
      {
      if ( lastMessageDateString.compareTo( messageDateString ) == 0 ) return;
      }
    else
      {
      // The new message is different to the last message, so save it
      kiteSDK.setAppParameter( KiteSDK.Scope.CUSTOMER_SESSION, PARAMETER_NAME_LAST_MESSAGE, message );
      }

    onNewMessage( context, from, message );
    }


  /*****************************************************
   *
   * Called when a new GCM message is received.
   *
   *****************************************************/
  abstract protected void onNewMessage( Context context, String from, String message );


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

