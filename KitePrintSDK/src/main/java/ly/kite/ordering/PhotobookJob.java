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

import java.util.HashMap;
import java.util.List;

import ly.kite.catalogue.Product;
import ly.kite.util.Asset;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a photobook job.
 *
 *****************************************************/
public class PhotobookJob extends AssetListJob
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

  private Asset  mFrontCoverAsset;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public PhotobookJob( long jobId, Product product, int orderQuantity, HashMap<String,String> optionsMap, Asset frontCoverAsset, List<Asset> contentAssetList )
    {
    super( jobId, product, orderQuantity, optionsMap, contentAssetList );

    mFrontCoverAsset = frontCoverAsset;
    }

  public PhotobookJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, Asset frontCoverAsset, List<Asset> contentAssetList )
    {
    this( 0, product, orderQuantity, optionsMap, frontCoverAsset, contentAssetList );
    }

  protected PhotobookJob( Parcel parcel )
    {
    super( parcel );

    mFrontCoverAsset = parcel.readParcelable( Asset.class.getClassLoader() );
    }


  ////////// Parcelable Method(s) //////////

  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    super.writeToParcel( parcel, flags );

    parcel.writeParcelable( mFrontCoverAsset, flags );
    }


  ////////// PrintsPrintJob Method(s) //////////

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
  protected void putAssetsJSON( List<Asset> assetList, JSONObject jsonObject ) throws JSONException
    {
    JSONObject assetsJSONObject = new JSONObject();

    assetsJSONObject.put( "back_cover", JSONObject.NULL );
    assetsJSONObject.put( "inside_pdf", JSONObject.NULL );
    assetsJSONObject.put( "cover_pdf",  JSONObject.NULL );


    // Add the front cover
    assetsJSONObject.put( "front_cover", getPageJSONObject( assetList, 0 ) );


    // Add the remaining pages

    JSONArray pagesJSONArray = new JSONArray();

    for ( int assetIndex = 1; assetIndex < assetList.size(); assetIndex ++ )
      {
      pagesJSONArray.put( getPageJSONObject( assetList, assetIndex ) );
      }

    assetsJSONObject.put( "pages", pagesJSONArray );


    jsonObject.put( "assets", assetsJSONObject );
    }


  /*****************************************************
   *
   * Returns a JSON object that represents a page.
   *
   *****************************************************/
  protected JSONObject getPageJSONObject( List<Asset> assetList, int assetIndex ) throws JSONException
    {
    JSONObject pageJSONObject = new JSONObject();

    Asset asset = assetList.get( assetIndex );

    if ( asset != null )
      {
      pageJSONObject.put( "layout", "single_centered" );
      pageJSONObject.put( "asset", "" + asset.getId() );
      }
    else
      {
      pageJSONObject.put( "layout", "blank" );
      }

    return ( pageJSONObject );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the front cover asset.
   *
   *****************************************************/
  public Asset getFrontCoverAsset()
    {
    return ( mFrontCoverAsset );
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

    if ( ! Asset.areBothNullOrEqual( mFrontCoverAsset, otherPhotobookJob.mFrontCoverAsset ) ) return ( false );

    return ( super.equals( otherObject ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

