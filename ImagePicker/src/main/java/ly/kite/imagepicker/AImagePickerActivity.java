/*****************************************************
 *
 * AImagePickerActivity.java
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

package ly.kite.imagepicker;


///// Import(s) /////

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This abstract class is the super class of activities
 * that display the art an image picker.
 *
 *****************************************************/
abstract public class AImagePickerActivity extends Activity implements ImagePickerGridView.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private   final String  LOG_TAG                        = "AImagePickerActivity";

  static protected final String  EXTRA_MAX_IMAGE_COUNT          = "maxImageCount";
  static private   final String  EXTRA_SELECTED_URL_STRING_LIST = "selectedURLStringList";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected ImagePickerGridView  mImagePickerGridView;
  protected ProgressBar          mProgressSpinner;

  private   MenuItem             mDoneActionItem;

  private   ICallback            mCallback;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Adds the max image count to an intent as an extra.
   *
   *****************************************************/
  static protected void addExtras( Intent intent, int maxImageCount )
    {
    intent.putExtra( EXTRA_MAX_IMAGE_COUNT, maxImageCount );
    }


  /*****************************************************
   *
   * Returns a list of image URLs as strings from a result
   * intent.
   *
   *****************************************************/
  static public List<String> getImageURLListFromResult( Intent resultIntent )
    {
    ArrayList<String> imageURLList = (ArrayList<String>)resultIntent.getStringArrayListExtra( EXTRA_SELECTED_URL_STRING_LIST );

    return ( imageURLList );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Get extras

    int maxImageCount = 0;

    Intent intent = getIntent();

    if ( intent != null )
      {
      maxImageCount = intent.getIntExtra( EXTRA_MAX_IMAGE_COUNT, 0 );
      }


    // Set up the screen

    setContentView( R.layout.ip_screen_grid );

    mImagePickerGridView = (ImagePickerGridView)findViewById( R.id.image_picker_grid_view );
    mProgressSpinner     = (ProgressBar)findViewById( R.id.progress_spinner );


    // Set up the grid view
    mImagePickerGridView.setMaxImageCount( maxImageCount );
    mImagePickerGridView.setCallback( this );
    }


  /*****************************************************
   *
   * Called to create the actions.
   *
   *****************************************************/
  @Override
  public boolean onCreateOptionsMenu ( Menu menu )
    {
    MenuInflater menuInflator = getMenuInflater();

    menuInflator.inflate( R.menu.ip_menu, menu );

    mDoneActionItem = menu.findItem( R.id.item_done );

    mDoneActionItem.setVisible( mImagePickerGridView.getSelectedCount() > 0 );

    return ( true );
    }


  /*****************************************************
   *
   * Called when the back key is pressed.
   *
   *****************************************************/
  @Override
  public void onBackPressed()
    {
    onAscend();
    }


  /*****************************************************
   *
   * Called when an action is clicked.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    int itemId = item.getItemId();


    if ( itemId == android.R.id.home )
      {
      onAscend();

      return ( true );
      }

    else if ( itemId == R.id.item_done )
      {
      Intent intent = new Intent();

      intent.putStringArrayListExtra( EXTRA_SELECTED_URL_STRING_LIST, mImagePickerGridView.getSelectedURLStringList() );

      setResult( RESULT_OK, intent );

      finish();

      return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  ////////// AImagePickerActivity Method(s) //////////

  @Override
  public void onSelectedCountChanged( int oldCount, int newCount )
    {
    //Callback only used by FacebookPhotoPicker
    if( mCallback != null )
      {
      mCallback.onSelectedCountChanged( newCount );
      }
    mDoneActionItem.setVisible( newCount > 0 );
    }

  /*****************************************************
   *
   * Registers a callback
   *
   *****************************************************/
  public void setCallback( ICallback callback )
    {
      mCallback = callback;
    }


  ////////// ImagePickerGridView.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called to set the visibility of the loading progress
   * spinner.
   *
   *****************************************************/
  @Override
  public void onItemsLoading( boolean display )
    {
    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( display ? View.VISIBLE : View.GONE );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Ascends the hierarchy. If we are already at the top,
   * then exit.
   *
   *****************************************************/
  private void onAscend()
    {
    // If we can't go up any further - finish and exit
    if ( ! mImagePickerGridView.onAscend() ) finish();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An agent responsible for selection information
   *
   *****************************************************/
  public interface ICallback
    {
    //Facebook is lacking it's own counter so this will be used
    //to set the correct counter for the toolbar
    public void onSelectedCountChanged( int selectedImageCount );
    }

  }