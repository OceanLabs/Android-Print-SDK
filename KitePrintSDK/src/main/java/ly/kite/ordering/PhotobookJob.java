/*****************************************************
 *
 * PhotobookJob.java
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

package ly.kite.ordering;


///// Import(s) /////

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;
import ly.kite.util.UploadableImage;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a photobook job.
 *
 *****************************************************/
public class PhotobookJob extends ImagesJob
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "PhotobookJob";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<PhotobookJob> CREATOR = new Parcelable.Creator<PhotobookJob>()
    {
    public PhotobookJob createFromParcel( Parcel in )
      {
      return ( new PhotobookJob( in ) );
      }

    public PhotobookJob[] newArray( int size )
      {
      return ( new PhotobookJob[ size ] );
      }
    };


  ////////// Member Variable(s) //////////

  private UploadableImage  mFrontCoverUploadableImage;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public PhotobookJob( long jobId, Product product, int orderQuantity, HashMap<String,String> optionsMap, Object frontCoverImage, List<?> contentObjectList )
    {
    super( jobId, product, orderQuantity, optionsMap, contentObjectList, 0, true );

    mFrontCoverUploadableImage = singleUploadableImageFrom( frontCoverImage );
    }

  public PhotobookJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, Object frontCoverImage, List<?> contentObjectList )
    {
    this( 0, product, orderQuantity, optionsMap, frontCoverImage, contentObjectList );
    }

  protected PhotobookJob( Parcel parcel )
    {
    super( parcel );

    mFrontCoverUploadableImage = parcel.readParcelable( AssetFragment.class.getClassLoader() );
    }


  ////////// Parcelable Method(s) //////////

  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    super.writeToParcel( parcel, flags );

    parcel.writeParcelable( mFrontCoverUploadableImage, flags );
    }


  ////////// ImagesJob Method(s) //////////

  /*****************************************************
   *
   * Returns a list of asset fragments that need uploading.
   *
   *****************************************************/
  @Override
  public List<UploadableImage> getImagesForUploading()
    {
    // Create a new list, and add the front cover to it

    ArrayList<UploadableImage> uploadableImageArrayList = new ArrayList<>();

    if ( mFrontCoverUploadableImage != null ) uploadableImageArrayList.add( mFrontCoverUploadableImage );


    // Add any content images
    super.addImagesForUploading( uploadableImageArrayList );

    return ( uploadableImageArrayList );
    }


  /*****************************************************
   *
   * Adds the assets to the supplied JSON object. Photobook
   * orders need assets in the following form:
   *
   * "assets":
   *   {
   *   "back_cover": null,
   *   "inside_pdf": null,
   *   "cover_pdf": null,
   *   "front_cover": null,
   *   "pages":
   *     [
   *       {
   *       "layout": "single_centered",
   *       "asset": "2409887"
   *       },
   *       {
   *       "layout": "single_centered",
   *       "asset": "2409888"
   *       },
   *       ...
   *     ]
   *   }
   *
   *****************************************************/
  @Override
  protected void putAssetsJSON( List<UploadableImage> uploadableImageList, JSONObject jsonObject ) throws JSONException
    {
    JSONObject assetsJSONObject = new JSONObject();

    assetsJSONObject.put( "back_cover", JSONObject.NULL );
    assetsJSONObject.put( "inside_pdf", JSONObject.NULL );
    assetsJSONObject.put( "cover_pdf",  JSONObject.NULL );


    // Add any front cover

    if ( mFrontCoverUploadableImage != null )
      {
      assetsJSONObject.put( "front_cover", String.valueOf( mFrontCoverUploadableImage.getUploadedAssetId() ) );
      }
    else
      {
      assetsJSONObject.put( "front_cover", JSONObject.NULL );
      }


    // Add the content pages

    JSONArray pagesJSONArray = new JSONArray();

    for ( UploadableImage uploadableImage : uploadableImageList )
      {
      pagesJSONArray.put( getPageJSONObject( uploadableImage ) );
      }

    assetsJSONObject.put( "pages", pagesJSONArray );


    jsonObject.put( "assets", assetsJSONObject );
    }


  /*****************************************************
   *
   * Returns a JSON object that represents a page.
   *
   *****************************************************/
  protected JSONObject getPageJSONObject( UploadableImage uploadableImage ) throws JSONException
    {
    JSONObject pageJSONObject = new JSONObject();

    if ( uploadableImage != null )
      {
      pageJSONObject.put( "layout", "single_centered" );
      pageJSONObject.put( "asset", String.valueOf( uploadableImage.getUploadedAssetId() ) );
      }
    else
      {
      pageJSONObject.put( "layout", "blank" );
      }

    return ( pageJSONObject );
    }


  /*****************************************************
   *
   * Returns the number of photos that are part of this job.
   * This quantity is a pain in the arse, because its meaning
   * varies.
   *
   * For photobooks, we need to consider the front cover as
   * well as the content images:
   *   - If all pages are full, we don't want to include the front
   *   cover because the server will think we have another book.
   *   - If just the front cover is occupied we need to add it in,
   *   otherwise the quanity will be 0 and the server will think
   *   there are no photos at all.
   *
   *****************************************************/
  @Override
  public int getQuantity()
    {
    int quantity = super.getQuantity();

    if ( quantity < 1 ) return ( mFrontCoverUploadableImage != null ? 1 : 0 );

    return ( quantity );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns any front cover uploadable image.
   *
   *****************************************************/
  public UploadableImage getFrontCoverUploadableImage()
    {
    return ( mFrontCoverUploadableImage );
    }


  /*****************************************************
   *
   * Returns true if the other object is the same as this
   * photobook job.
   *
   *****************************************************/
  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null || ( !( otherObject instanceof PhotobookJob ) ) ) return ( false );

    PhotobookJob otherPhotobookJob = (PhotobookJob)otherObject;

    if ( ! UploadableImage.areBothNullOrEqual( mFrontCoverUploadableImage, otherPhotobookJob.mFrontCoverUploadableImage ) ) return ( false );

    return ( super.equals( otherObject ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

