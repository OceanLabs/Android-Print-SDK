/*****************************************************
 *
 * AHomeActivity.java
 *
 *
 * Copyright (c) 2015 Kite Tech Ltd. https://www.kite.ly
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

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ListView;

import ly.kite.R;
import ly.kite.journey.selection.ChooseProductGroupFragment;
import ly.kite.journey.selection.ProductSelectionActivity;
import ly.kite.catalogue.AssetHelper;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the activity for thw Kite SDK sample app.
 * It demonstrates how to create some image assets for
 * personalisation, and then how to start the SDK product
 * selection / shopping journey.
 *
 *****************************************************/
abstract public class AHomeActivity extends ProductSelectionActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String LOG_TAG                      = "AHomeActivity";

  private static final int    SIMULATED_TOUCH_X            = 1;
  private static final int    SIMULATED_TOUCH_Y            = 1;
  private static final float  NORMAL_PRESSURE              = 1.0f;
  private static final float  SIMULATED_TOUCH_SIZE         = 0.1f;
  private static final int    NO_META_STATE_FLAGS          = 0;
  private static final float  SIMULATED_X_PRECISION        = 0.1f;
  private static final float  SIMULATED_Y_PRECISION        = 0.1f;
  private static final int    DEVICE_ID                    = 1;

  private static final long   DOWN_EVENT_DELAY_MILLIS      = 1000L;
  private static final long   UP_EVENT_DELAY_MILLIS        = 1000L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected DrawerLayout           mDrawerLayout;
  protected ListView               mNavigationDrawerListView;

  private   boolean                mShowDrawerOnResume;

  private   ActionBarDrawerToggle  mDrawerToggle;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// ProductSelectionActivity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Set up the navigation drawer

    mDrawerLayout             = (DrawerLayout)findViewById( R.id.drawer_layout );
    mNavigationDrawerListView = (ListView)findViewById( R.id.navigation_drawer_list_view );

    mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.drawer_open, R.string.drawer_closed );
    mDrawerLayout.setDrawerListener( mDrawerToggle );


    // On devices running older versions of Android, there is no menu icon, so the user may not
    // know there's a menu. We want to let them know by showing a hint of the menu.

    if ( savedInstanceState == null && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 )
      {
      mShowDrawerOnResume = true;
      }
    }


  /*****************************************************
   *
   * Called when the activity becomes visible.
   *
   *****************************************************/
  @Override
  protected void onResume()
    {
    super.onResume();


    // We display a hint of the menu by simulating a pointer down touch event, and then releasing
    // it after a short time.

    if ( mShowDrawerOnResume )
      {
      mShowDrawerOnResume = false;

      final Handler handler = new Handler();

      handler.postDelayed( new Runnable()
      {
      public void run()
        {
        final long uptimeMillis = SystemClock.uptimeMillis();

        mDrawerLayout.onTouchEvent(
                MotionEvent.obtain(
                        uptimeMillis,
                        uptimeMillis,
                        MotionEvent.ACTION_DOWN,
                        SIMULATED_TOUCH_X,
                        SIMULATED_TOUCH_Y,
                        NORMAL_PRESSURE,
                        SIMULATED_TOUCH_SIZE,
                        NO_META_STATE_FLAGS,
                        SIMULATED_X_PRECISION,
                        SIMULATED_Y_PRECISION,
                        DEVICE_ID,
                        MotionEvent.EDGE_LEFT ) );


        handler.postDelayed( new Runnable()
        {
        public void run()
          {
          mDrawerLayout.onTouchEvent(
                  MotionEvent.obtain(
                          uptimeMillis + UP_EVENT_DELAY_MILLIS,
                          uptimeMillis + UP_EVENT_DELAY_MILLIS,
                          MotionEvent.ACTION_UP,
                          SIMULATED_TOUCH_X,
                          SIMULATED_TOUCH_Y,
                          NORMAL_PRESSURE,
                          SIMULATED_TOUCH_SIZE,
                          NO_META_STATE_FLAGS,
                          SIMULATED_X_PRECISION,
                          SIMULATED_Y_PRECISION,
                          DEVICE_ID,
                          MotionEvent.EDGE_LEFT ) );
          }
        }, UP_EVENT_DELAY_MILLIS );

        }
      }, DOWN_EVENT_DELAY_MILLIS );
      }

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

    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
    }


  /*****************************************************
   *
   * Called after the configuration changes.
   *
   *****************************************************/
  @Override
  public void onConfigurationChanged( Configuration newConfig )
    {
    super.onConfigurationChanged( newConfig );

    mDrawerToggle.onConfigurationChanged( newConfig );
    }


  /*****************************************************
   *
   * Called when an action bar item is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    // Pass the event to ActionBarDrawerToggle, if it returns
    // true, then it has handled the app icon touch event.

    if ( mDrawerToggle.onOptionsItemSelected( item ) )
      {
      return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called with the current top-most fragment.
   *
   *****************************************************/
  protected void onNotifyTop( AKiteFragment topFragment )
    {
    ActionBar actionBar = getActionBar();


    // Determine which fragment is top-most

    String tag = topFragment.getTag();

    if ( tag != null && tag.equals( ChooseProductGroupFragment.TAG ) )
      {
      ///// Home page /////

      // We only enable the menu on the home page
      mDrawerToggle.setDrawerIndicatorEnabled( true );
      mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED );

      // We display the logo on the home page
      actionBar.setDisplayShowTitleEnabled( false );
      actionBar.setDisplayShowCustomEnabled( true );
      }
    else
      {
      mDrawerToggle.setDrawerIndicatorEnabled( false );
      mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED );

      // On other pages we show a title
      actionBar.setDisplayShowTitleEnabled( true );
      actionBar.setDisplayShowCustomEnabled( false );
      }


    super.onNotifyTop( topFragment );
    }


  /*****************************************************
   *
   * Called when an activity result is received.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    // For the Sticky 9 app, we don't want to exit the activity once check-out is
    // complete - we want to go back to the landing page - the choose product group
    // fragment.

    if ( requestCode == ACTIVITY_REQUEST_CODE_CHECKOUT && resultCode == RESULT_OK )
      {
      mFragmentManager.popBackStackImmediate( ChooseProductGroupFragment.TAG, 0 );

      // If we have successfully completed a check-out, then also clear out any cached
      // assets. (This won't clear any product images).
      AssetHelper.clearCachedImages( this );
      }
    else
      {
      super.onActivityResult( requestCode, resultCode, data );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////

  }
