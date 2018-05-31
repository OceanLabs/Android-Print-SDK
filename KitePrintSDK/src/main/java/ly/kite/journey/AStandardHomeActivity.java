/*****************************************************
 *
 * AStandardHomeActivity.java
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
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import ly.kite.journey.selection.ChooseProductGroupFragment;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the home activity for apps that use a
 * standard menu indicator.
 *
 *****************************************************/
abstract public class AStandardHomeActivity extends AHomeActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String LOG_TAG             = "AStandardHomeActivity";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ActionBarDrawerToggle  mDrawerToggle;


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


    // Set up the drawer toggle
    mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.kitesdk_drawer_open, R.string.kitesdk_drawer_closed);
    mDrawerLayout.setDrawerListener( mDrawerToggle );


    // Set up the action bar

    ActionBar actionBar = getActionBar();

    if ( actionBar != null )
      {
      actionBar.setDisplayHomeAsUpEnabled( true );
      actionBar.setHomeButtonEnabled( true );
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
    // true, then it has handled the app icon touch event
    if ( mDrawerToggle.onOptionsItemSelected( item ) )
      {
      return ( true );
      }

    return super.onOptionsItemSelected( item );
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

      if ( mShowMenuOnce )
        {
        mShowMenuOnce = false;


        // Open and close the menu

        mHandler = new Handler();

        mHandler.postDelayed( new OpenMenuRunnable(), OPEN_MENU_DELAY_MILLIS );
        }

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
   * Called when the back key is pressed.
   *
   *****************************************************/
  @Override
  public void onBackPressed()
    {
    // If the drawer is open - close it
    if ( mDrawerLayout.isDrawerOpen( Gravity.LEFT ) )
      {
      mDrawerLayout.closeDrawer( Gravity.LEFT );

      return;
      }

    super.onBackPressed();
    }


  ////////// Method(s) //////////


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Runnable to open the menu.
   *
   *****************************************************/
  private class OpenMenuRunnable implements Runnable, DrawerLayout.DrawerListener
    {
    public void run()
      {
      // Override the drawer listener so we know when it has fully opened.
      mDrawerLayout.setDrawerListener( this );

      mDrawerLayout.openDrawer( Gravity.LEFT );
      }

    @Override
    public void onDrawerSlide( View drawerView, float slideOffset )
      {
      // Ignore
      }

    @Override
    public void onDrawerOpened( View drawerView )
      {
      // We no longer need to listen for drawer events
      mDrawerLayout.setDrawerListener( null );

      mHandler.postDelayed( new CloseMenuRunnable(), CLOSE_MENU_DELAY_MILLIS );
      }

    @Override
    public void onDrawerClosed( View drawerView )
      {
      // Ignore
      }

    @Override
    public void onDrawerStateChanged( int newState )
      {
      // Ignore
      }
    }


  /*****************************************************
   *
   * Runnable to close the menu.
   *
   *****************************************************/
  private class CloseMenuRunnable implements Runnable
    {
    public void run()
      {
      mDrawerLayout.closeDrawers();
      }
    }


  }
