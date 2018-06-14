/*****************************************************
 *
 * AImageSource.java
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

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;

import ly.kite.R;
import ly.kite.util.Asset;


///// Class Declaration /////

/*****************************************************
 *
 * An image source.
 *
 *****************************************************/
abstract public class AImageSource
  {
  ///// Static Constant(s) /////

  static private final String  LOG_TAG          = "AImageSource";

  static public  final int     UNLIMITED_IMAGES = 0;
  static public  final int     SINGLE_IMAGE     = 1;


  ///// Member Variable(s) /////

  private int  mHorizontalBackgroundColourResourceId;
  private int  mVerticalBackgroundColourResourceId;

  private int  mHorizontalLayoutIconResourceId;
  private int  mVerticalLayoutIconResourceId;

  private int  mLabelResourceId;

  private int  mMenuItemId;
  private int  mMenuItemTitleResourceId;

  private int  mActivityRequestCode;


  ///// Static Method(s) /////


  ///// Constructor(s) /////

  protected AImageSource( int horizontalBackgroundColourResourceId,
                          int verticalBackgroundColourResourceId,
                          int horizontalLayoutIconResourceId,
                          int verticalLayoutIconResourceId,
                          int labelResourceId,
                          int menuItemId,
                          int menuItemTitleResourceId )
    {
    mHorizontalBackgroundColourResourceId = horizontalBackgroundColourResourceId;
    mVerticalBackgroundColourResourceId   = verticalBackgroundColourResourceId;
    mHorizontalLayoutIconResourceId       = horizontalLayoutIconResourceId;
    mVerticalLayoutIconResourceId         = verticalLayoutIconResourceId;
    mLabelResourceId                      = labelResourceId;
    mMenuItemId                           = menuItemId;
    mMenuItemTitleResourceId              = menuItemTitleResourceId;
    }


  protected AImageSource( int backgroundColourResourceId,
                          int iconResourceId,
                          int labelResourceId,
                          int menuItemId,
                          int menuItemTitleResourceId )
    {
    this( backgroundColourResourceId,
          backgroundColourResourceId,
          iconResourceId,
          iconResourceId,
          labelResourceId,
          menuItemId,
          menuItemTitleResourceId );
    }


  /*****************************************************
   *
   * Returns the resource id of the background colour that
   * represents this image source.
   *
   *****************************************************/
  int getBackgroundColourResourceId( LayoutType layoutType )
    {
    switch ( layoutType )
      {
      case HORIZONTAL:

        return ( mHorizontalBackgroundColourResourceId );

      case VERTICAL:

        return ( mVerticalBackgroundColourResourceId );
      }

    return ( 0 );
    }


  /*****************************************************
   *
   * Returns the resource id of the icon that represents
   * this image source.
   *
   *****************************************************/
  int getIconResourceId( LayoutType layoutType )
    {
    switch ( layoutType )
      {
      case HORIZONTAL:

        return ( mHorizontalLayoutIconResourceId );

      case VERTICAL:

        return ( mVerticalLayoutIconResourceId );
      }

    return ( 0 );
    }


  /*****************************************************
   *
   * Returns the resource id of the label string that
   * represents this image source.
   *
   *****************************************************/
  int getLabelResourceId()
    {
    return ( mLabelResourceId );
    }


  /*****************************************************
   *
   * Returns the id of the menu item for this image source.
   *
   *****************************************************/
  public int getMenuItemId()
    {
    return ( mMenuItemId );
    }


  /*****************************************************
   *
   * Adds this image source as a menu item. The order is
   * the same as the request code.
   *
   *****************************************************/
  public void addAsMenuItem( Menu menu )
    {
    menu.add( 0, mMenuItemId, mActivityRequestCode, mMenuItemTitleResourceId );
    }


  /*****************************************************
   *
   * Sets the activity request code. This should not be used
   * by the app; the the Kite SDK assigns request codes to the
   * image sources automatically.
   *
   *****************************************************/
  public void setActivityRequestCode( int requestCode )
    {
    mActivityRequestCode = requestCode;
    }


  /*****************************************************
   *
   * Returns the activity request code.
   *
   *****************************************************/
  public int getActivityRequestCode()
    {
    return ( mActivityRequestCode );
    }


  /*****************************************************
   *
   * Returns true if this image source is available.
   *
   *****************************************************/
  abstract public boolean isAvailable( Context context );


  /*****************************************************
   *
   * Returns the layout resource id to be used to display
   * this image source for the supplied layout type.
   *
   *****************************************************/
  public int getLayoutResource( LayoutType layoutType )
    {
    switch ( layoutType )
      {
      case HORIZONTAL:

        return ( R.layout.grid_item_image_source_horizontal );

      case VERTICAL:

        return ( R.layout.grid_item_image_source_vertical );
      }

    return ( 0 );
    }


  /*****************************************************
   *
   * Called when this image source is clicked.
   *
   *****************************************************/
  abstract public void onPick( Fragment fragment, int addedAssetCount, boolean supportsMultiplePacks, int packSize, int maxImageCount );


  /*****************************************************
   *
   * Called when this image source is clicked.
   *
   *****************************************************/
  public void onPick( Fragment fragment, boolean supportsMultiplePacks, int packSize, int maxImageCount )
    {
    onPick( fragment, UNLIMITED_IMAGES, supportsMultiplePacks, packSize, maxImageCount);
    }


  /*****************************************************
   *
   * Called when this image source is clicked.
   *
   *****************************************************/
  public void onPick( Fragment fragment, boolean selectSingleImage, boolean supportsMultiplePacks, int packSize, int maxImageCount )
    {
    onPick( fragment, ( selectSingleImage ? 1 : UNLIMITED_IMAGES ), supportsMultiplePacks, packSize, maxImageCount );
    }


  /*****************************************************
   *
   * Calls the runnable once the supplied permissions
   * have been granted.
   *
   *****************************************************/
  protected void requestPermissions( String[] permissions, AStartPickerRunnable runnable )
    {
    Activity activity = runnable.getActivity();

    if ( activity instanceof AKiteActivity )
      {
      ( (AKiteActivity)activity ).callRunnableWithPermissions( permissions, runnable );
      }
    }


  /*****************************************************
   *
   * Calls the runnable once the supplied permission
   * have been granted.
   *
   *****************************************************/
  protected void requestPermission( String permission, AStartPickerRunnable runnable )
    {
    final String[] permissions = new String[]{ permission };

    requestPermissions( permissions, runnable );
    }


  /*****************************************************
   *
   * Returns picked photos as assets. May call back to the
   * consumer asynchronously or synchronously (i.e. from
   * within this method).
   *
   *****************************************************/
  abstract public void getAssetsFromPickerResult( Activity activity, Intent data, IAssetConsumer assetConsumer );


  /*****************************************************
   *
   * Called to end the customer session. May be used to
   * log out of any social networks.
   *
   *****************************************************/
  public void endCustomerSession( Context context )
    {
    }


  ///// Inner class(es) /////

  /*****************************************************
   *
   * Images sources can be displayed in various different
   * screens. This enum defines the type of layout that
   * may be required.
   *
   *****************************************************/
  public enum LayoutType
    {
    VERTICAL,
    HORIZONTAL
    }


  /*****************************************************
   *
   * An asset consumer.
   *
   *****************************************************/
  public interface IAssetConsumer
    {
    public void isacOnAssets( List<Asset> assetList );
    }


  /*****************************************************
   *
   * A runnable that simply calls the onPick method. Used
   * to call the method once permissions have been granted.
   *
   *****************************************************/
  protected abstract class AStartPickerRunnable implements Runnable
    {
    protected Fragment  mFragment;
    protected int       mMaxImageCount;
    protected int       mAddedAssetCount;
    protected int       mPackSize;
    protected boolean   mSupportsMultiplePacks;

    AStartPickerRunnable( Fragment fragment, int addedAssetCount, boolean supportsMultiplePacks, int packSize, int maxImageCount )
      {
      mFragment      = fragment;
      mMaxImageCount = maxImageCount;
      mAddedAssetCount       = addedAssetCount;
      mPackSize              = packSize;
      mSupportsMultiplePacks = supportsMultiplePacks;
      }

    Activity getActivity()
      {
      return ( mFragment.getActivity() );
      }
    }

  }
