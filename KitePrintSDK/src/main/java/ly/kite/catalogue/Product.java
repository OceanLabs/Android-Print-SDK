/*****************************************************
 *
 * Product.java
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

package ly.kite.catalogue;


///// Import(s) /////

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ly.kite.KiteSDK;
import ly.kite.address.Country;
import ly.kite.journey.UserJourneyType;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a print product.
 *
 *****************************************************/
public class Product implements IGroupOrProduct, Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  static private final String       LOG_TAG                           = "Product";

  // Dummy product - used for testing
  static public  final Product      DUMMY_PRODUCT                     = new Product( "product_id", "product_code", "Product Name", "Product type","Product category", 0xffff0000, UserJourneyType.RECTANGLE, 1 );

  static public  final float        MINIMUM_SENSIBLE_SIZE_CENTIMETERS = 0.5f;
  static public  final float        MINIMUM_SENSIBLE_SIZE_INCHES      = 0.2f;
  static public  final float        MINIMUM_SENSIBLE_SIZE_PIXELS      = 10f;

  static public  final float        DEFAULT_IMAGE_ASPECT_RATIO        = SingleUnitSize.DEFAULT_ASPECT_RATIO;

  static private final UnitOfLength FALLBACK_UNIT_1                   = UnitOfLength.CENTIMETERS;
  static private final UnitOfLength FALLBACK_UNIT_2                   = UnitOfLength.INCHES;

  static private final String       DESTINATION_CODE_EUROPE           = "europe";
  static private final String       DESTINATION_CODE_REST_OF_WORLD    = "rest_of_world";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<Product> CREATOR =
          new Parcelable.Creator<Product>()
          {
          public Product createFromParcel( Parcel sourceParcel )
            {
            return (new Product( sourceParcel ));
            }

          public Product[] newArray( int size )
            {
            return (new Product[ size ]);
            }
          };


  ////////// Member Variable(s) //////////

  private String                            mId;
  private String                            mCode;
  private String                            mName;
  private String                            mDescription;
  private String                            mType;
  private String                            mCategory;
  private UserJourneyType                   mUserJourneyType;
  private int                               mQuantityPerSheet;
  private int                               mGridCountX;
  private int                               mGridCountY;

  private MultipleCurrencyAmounts           mCost;
  private MultipleDestinationShippingCosts  mShippingCosts;
  private URL                               mHeroImageURL;
  private int                               mLabelColour;
  private ArrayList<URL>                    mImageURLList;
  private URL                               mMaskURL;
  private Bleed                             mMaskBleed;
  private MultipleUnitSize                  mSize;
  private float                             mImageAspectRatio;
  private BorderF                           mImageBorderF;

  private ArrayList<URL>                    mUnderImageURLList;
  private ArrayList<URL>                    mOverImageURLList;
  private String                            mMaskBlendMode;

  private List<ProductOption>               mOptionList;

  private int                               mFlags;

  private ArrayList<String>                 mCalendarImageURLStringList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true, if the dimension is a sensible product
   * size, false otherwise.
   *
   *****************************************************/
  static public boolean isSensibleSize( SingleUnitSize size )
    {
    if ( size == null ) return ( false );

    UnitOfLength unit = size.getUnit();

    float minimumSensibleSize = 1f;

    switch ( unit )
      {
      case CENTIMETERS:
        minimumSensibleSize = MINIMUM_SENSIBLE_SIZE_CENTIMETERS;
        break;

      case INCHES:
        minimumSensibleSize = MINIMUM_SENSIBLE_SIZE_INCHES;
        break;

      case PIXELS:
        minimumSensibleSize = MINIMUM_SENSIBLE_SIZE_PIXELS;
        break;

      default:
      }


    // Check that both dimensions are sensible

    if ( size.getWidth() >= minimumSensibleSize  &&
         size.getHeight() >= minimumSensibleSize )
      {
      return ( true );
      }


    return ( false );
    }


  /*****************************************************
   *
   * Writes an array list of URLs to a parcel.
   *
   *****************************************************/
  static private void writeToParcel( ArrayList<URL> urlList, Parcel targetParcel )
    {
    if ( urlList != null )
      {
      targetParcel.writeInt( urlList.size() );

      for ( URL url : urlList )
        {
        targetParcel.writeSerializable( url );
        }
      }
    else
      {
      targetParcel.writeInt( 0 );
      }
    }


  /*****************************************************
   *
   * Reads an array list of URLs from a parcel.
   *
   *****************************************************/
  static private ArrayList<URL> readURLList( Parcel sourceParcel )
    {
    int urlCount = sourceParcel.readInt();

    ArrayList<URL> urlList = new ArrayList<>();

    for ( int index = 0; index < urlCount; index++ )
      {
      urlList.add( (URL)sourceParcel.readSerializable() );
      }

    return ( urlList );
    }


  /*****************************************************
   *
   * Appends a URL to a list if it is not null.
   *
   *****************************************************/
  static private void appendIfNotNull( URL url, List<URL> targetURLList )
    {
    if ( url != null ) targetURLList.add( url );
    }

  /*****************************************************
   *
   * Appends any non-null URLs to a list.
   *
   *****************************************************/
  static private void appendNonNull( List<URL> sourceURLList, List<URL> targetURLList )
    {
    if ( sourceURLList != null )
      {
      for ( URL sourceURL : sourceURLList )
        {
        if ( sourceURL != null ) targetURLList.add( sourceURL );
        }
      }
    }


  ////////// Constructor(s) //////////

  private Product()
    {
    mUnderImageURLList = new ArrayList<>( 0 );
    mOverImageURLList  = new ArrayList<>( 0 );

    mOptionList = new ArrayList<>( 0 );
    }


  public Product( String productId, String productCode, String productName, String productType, String productCategory, int labelColour, UserJourneyType userJourneyType, int quantityPerSheet )
    {
    this();

    mId               = productId;
    mCode             = productCode;
    mName             = productName;
    mType             = productType;
    mCategory         = productCategory;
    mLabelColour      = labelColour;
    mUserJourneyType  = userJourneyType;
    mQuantityPerSheet = quantityPerSheet;
    }


  // Constructor used by parcelable interface
  private Product( Parcel sourceParcel )
    {
    this();

    mId                         = sourceParcel.readString();
    mCode                       = sourceParcel.readString();
    mName                       = sourceParcel.readString();
    mDescription                = sourceParcel.readString();
    mType                       = sourceParcel.readString();
    mCategory                   = sourceParcel.readString();

    String userJourneyString    = sourceParcel.readString();
    mUserJourneyType            = (userJourneyString != null ? UserJourneyType.valueOf( userJourneyString ) : null);

    mQuantityPerSheet           = sourceParcel.readInt();
    mGridCountX                 = sourceParcel.readInt();
    mGridCountY                 = sourceParcel.readInt();

    mCost                       = (MultipleCurrencyAmounts) sourceParcel.readParcelable( MultipleCurrencyAmounts.class.getClassLoader() );
    mShippingCosts              = (MultipleDestinationShippingCosts) sourceParcel.readParcelable( MultipleDestinationShippingCosts.class.getClassLoader() );
    mHeroImageURL               = (URL)sourceParcel.readSerializable();
    mLabelColour                = sourceParcel.readInt();

    mImageURLList               = readURLList( sourceParcel );

    mMaskURL                    = (URL)sourceParcel.readSerializable();
    mMaskBleed                  = (Bleed)sourceParcel.readParcelable( Bleed.class.getClassLoader() );
    mSize                       = (MultipleUnitSize)sourceParcel.readParcelable( MultipleUnitSize.class.getClassLoader() );
    mImageAspectRatio           = sourceParcel.readFloat();
    mImageBorderF               = (BorderF)sourceParcel.readParcelable( BorderF.class.getClassLoader() );

    mUnderImageURLList          = readURLList( sourceParcel );
    mOverImageURLList           = readURLList( sourceParcel );
    mMaskBlendMode              = sourceParcel.readString();

    mOptionList                 = new ArrayList<>();
    sourceParcel.readList( mOptionList, ProductOption.class.getClassLoader() );

    mFlags                      = sourceParcel.readInt();

    mCalendarImageURLStringList = sourceParcel.createStringArrayList();
    }


  ////////// Parcelable Method(s) //////////

  /*****************************************************
   *
   * Describes the contents of this parcelable.
   *
   *****************************************************/
  @Override
  public int describeContents()
    {
    return ( 0 );
    }


  /*****************************************************
   *
   * Write the contents of this product to a parcel.
   *
   *****************************************************/
  @Override
  public void writeToParcel( Parcel targetParcel, int flags )
    {
    targetParcel.writeString( mId );
    targetParcel.writeString( mCode );
    targetParcel.writeString( mName );
    targetParcel.writeString( mDescription );
    targetParcel.writeString( mType );
    targetParcel.writeString( mCategory );
    targetParcel.writeString( mUserJourneyType != null ? mUserJourneyType.name() : null );
    targetParcel.writeInt( mQuantityPerSheet );
    targetParcel.writeInt( mGridCountX );
    targetParcel.writeInt( mGridCountY );

    targetParcel.writeParcelable( mCost, flags );
    targetParcel.writeParcelable( mShippingCosts, flags );
    targetParcel.writeSerializable( mHeroImageURL );
    targetParcel.writeInt( mLabelColour );

    writeToParcel( mImageURLList, targetParcel );

    targetParcel.writeSerializable( mMaskURL );

    targetParcel.writeParcelable( mMaskBleed, flags );
    targetParcel.writeParcelable( mSize, flags );
    targetParcel.writeFloat( mImageAspectRatio );
    targetParcel.writeParcelable( mImageBorderF, flags );

    writeToParcel( mUnderImageURLList, targetParcel );
    writeToParcel( mOverImageURLList,  targetParcel );
    targetParcel.writeString( mMaskBlendMode );

    targetParcel.writeList( mOptionList );

    targetParcel.writeInt( mFlags );

    targetParcel.writeStringList( mCalendarImageURLStringList );
    }


  ////////// IGroupOrProduct Method(s) //////////

  /*****************************************************
   *
   * Returns the display image URL.
   *
   *****************************************************/
  @Override
  public URL getDisplayImageURL()
    {
    return ( mHeroImageURL );
    }


  /*****************************************************
   *
   * Returns the gravity for the display image.
   *
   *****************************************************/
  @Override
  public int getDisplayImageAnchorGravity( Context context )
    {
    return ( KiteSDK.getInstance( context ).getCustomiser().getChooseProductImageAnchorGravity( mId ) );
    }


  /*****************************************************
   *
   * Returns the display label.
   *
   *****************************************************/
  @Override
  public String getDisplayLabel()
    {
    return ( mName );
    }


  /*****************************************************
   *
   * Returns the display label colour.
   *
   *****************************************************/
  @Override
  public int getDisplayLabelColour()
    {
    return ( mLabelColour );
    }


  /*****************************************************
   *
   * Returns a display price.
   *
   *****************************************************/
  @Override
  public String getDisplayPrice( String preferredCurrency )
    {
    return ( mCost.getDefaultDisplayAmountWithFallback( preferredCurrency ) );
    }


  /*****************************************************
   *
   * Returns a description, or null if there is no
   * description.
   *
   *****************************************************/
  public String getDescription()
    {
    return ( mDescription );
    }


  /*****************************************************
   *
   * Returns true or false according to whether a flag is
   * set.
   *
   *****************************************************/
  public boolean flagIsSet( Flag flag )
    {
    if ( flag != null )
      {
      return ( flag.isSet( mFlags ) );
      }

    return ( false );
    }


  /*****************************************************
   *
   * Returns true or false according to whether a flag is
   * set.
   *
   *****************************************************/
  @Override
  public boolean flagIsSet( String tag )
    {
    return ( flagIsSet( Flag.fromTag( tag ) ) );
    }


  /*****************************************************
   *
   * Returns the user journey type.
   *
   *****************************************************/
  public UserJourneyType getUserJourneyType()
    {
    return ( mUserJourneyType );
    }


  /*****************************************************
   *
   * Sets the description.
   *
   * @return The product. so calls can be chained.
   *
   *****************************************************/
  public Product setDescription( String description )
    {
    mDescription = description;

    return ( this );
    }


  /*****************************************************
   *
   * Sets the quantity per sheet.
   *
   *****************************************************/
  public void setQuantityPerSheet( int quantityPerSheet )
    {
    mQuantityPerSheet = quantityPerSheet;
    }


  /*****************************************************
   *
   * Returns the quantity per sheet.
   *
   *****************************************************/
  public int getQuantityPerSheet()
    {
    return ( mQuantityPerSheet );
    }


  /*****************************************************
   *
   * Sets the grid size.
   *
   *****************************************************/
  public Product setGridSize( int gridCountX, int gridCountY )
    {
    mGridCountX = gridCountX;
    mGridCountY = gridCountY;

    return ( this );
    }


  /*****************************************************
   *
   * Returns the X grid count.
   *
   *****************************************************/
  public int getGridCountX()
    {
    return ( mGridCountX );
    }


  /*****************************************************
   *
   * Returns the Y grid count.
   *
   *****************************************************/
  public int getGridCountY()
    {
    return ( mGridCountY );
    }


  /*****************************************************
   *
   * Returns false, because a product does not contain
   * multiple prices.
   *
   *****************************************************/
  public boolean containsMultiplePrices()
    {
    return ( false );
    }


  /*****************************************************
   *
   * Returns a display price.
   *
   *****************************************************/
  public String getDisplayPriceMultipliedBy( String preferredCurrency, int quantity )
    {
    return ( mCost.getDisplayAmountWithFallbackMultipliedBy( preferredCurrency, quantity ) );
    }


  /*****************************************************
   *
   * Returns a display price.
   *
   *****************************************************/
  public String getDisplayOriginalPriceMultipliedBy( String preferredCurrency, int quantity )
    {
    return ( mCost.getDisplayOriginalAmountWithFallbackMultipliedBy( preferredCurrency, quantity ) );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the id.
   *
   *****************************************************/
  public String getId()
    {
    return ( mId );
    }


  /*****************************************************
   *
   * Returns the name.
   *
   *****************************************************/
  public String getName()
    {
    return ( mName );
    }


  /*****************************************************
   *
   * Returns the name and category in one string
   *
   *****************************************************/
  public String getDisplayName()
    {
      // If name already contains the category don't add it
      if( mName.contains( mCategory ) )
        {
        return mName;
        }
      return mName + " ( " + mCategory + " )";
    }


  /*****************************************************
   *
   * Sets the cost.
   *
   *****************************************************/
  Product setCost( MultipleCurrencyAmounts cost )
    {
    mCost = cost;

    return (this);
    }


  /*****************************************************
   *
   * Sets the shipping costs.
   *
   *****************************************************/
  Product setShippingCosts( MultipleDestinationShippingCosts shippingCosts )
    {
    mShippingCosts = shippingCosts;

    return ( this );
    }


  /*****************************************************
   *
   * Returns the shipping costs.
   *
   *****************************************************/
  public MultipleDestinationShippingCosts getShippingCosts()
    {
    return ( mShippingCosts );
    }


  /*****************************************************
   *
   * Sets the image URLs.
   *
   *****************************************************/
  Product setImageURLs( URL heroImageURL, ArrayList<URL> imageURLList )
    {
    mHeroImageURL = heroImageURL;
    mImageURLList = imageURLList;

    return ( this );
    }


  /*****************************************************
   *
   * Returns the image URLs.
   *
   *****************************************************/
  public ArrayList<URL> getImageURLList()
    {
    return ( mImageURLList );
    }


  /*****************************************************
   *
   * Sets the label colour.
   *
   *****************************************************/
  Product setLabelColour( int labelColour )
    {
    mLabelColour = labelColour;

    return ( this );
    }


  /*****************************************************
   *
   * Sets the mask.
   *
   *****************************************************/
  Product setMask( URL url, Bleed bleed )
    {
    mMaskURL   = url;
    mMaskBleed = bleed;

    return ( this );
    }


  /*****************************************************
   *
   * Returns the mask URL.
   *
   *****************************************************/
  public URL getMaskURL()
    {
    return ( mMaskURL );
    }


  /*****************************************************
   *
   * Returns the mask bleed.
   *
   *****************************************************/
  public Bleed getMaskBleed()
    {
    return ( mMaskBleed );
    }


  /*****************************************************
   *
   * Sets the size.
   *
   *****************************************************/
  Product setSize( MultipleUnitSize size )
    {
    mSize = size;

    return ( this );
    }


  /*****************************************************
   *
   * Returns the size, falling back if the size is not
   * known in the requested units.
   *
   *****************************************************/
  public SingleUnitSize getSizeWithFallback( UnitOfLength unit )
    {
    SingleUnitSize size;

    // First try the requested unit
    if ( ( size = mSize.get( unit ) ) != null ) return ( size );

    // Next try falling back through major currencies
    if ( ( size = mSize.get( FALLBACK_UNIT_1 ) ) != null ) return ( size );
    if ( ( size = mSize.get( FALLBACK_UNIT_2 ) ) != null ) return ( size );

    // Lastly try and getCost the first supported currency
    if ( ( size = mSize.get( 0 ) ) != null ) return ( size );

    return ( null );
    }


  /*****************************************************
   *
   * Returns the aspect ratio of the image required for
   * producing the product - *not* the product itself.
   *
   *****************************************************/
  public float getImageAspectRatio()
    {
    return ( mImageAspectRatio >= KiteSDK.FLOAT_ZERO_THRESHOLD ? mImageAspectRatio : DEFAULT_IMAGE_ASPECT_RATIO );
    }

  /*****************************************************
   *
   * Returns the product type
   *
   *****************************************************/
  public String getType ()
    {
    return mType;
    }

  /*****************************************************
   *
   * Returns the product category
   *
   *****************************************************/
  public String getCategory ()
    {
    return mCategory;
    }


  /*****************************************************
   *
   * Sets the properties of the creation image and border.
   *
   *****************************************************/
  Product setCreationImage( float aspectRatio, BorderF border )
    {
    mImageAspectRatio = aspectRatio;
    mImageBorderF     = border;

    return ( this );
    }


  /*****************************************************
   *
   * Adds an under image.
   *
   *****************************************************/
  Product addUnderImage( URL underImageURL )
    {
    mUnderImageURLList.add( underImageURL );

    return ( this );
    }


  /*****************************************************
   *
   * Returns the under images.
   *
   *****************************************************/
  public ArrayList<URL> getUnderImageURLList()
    {
    return ( mUnderImageURLList );
    }


  /*****************************************************
   *
   * Adds an over image.
   *
   *****************************************************/
  Product addOverImage( URL overImageURL )
    {
    mOverImageURLList.add( overImageURL );

    return ( this );
    }

  /*****************************************************
   *
   * Adds the mask blend mode
   *
   *****************************************************/
  Product setMaskBlendMode( String maskBlendMode )
    {
      mMaskBlendMode = maskBlendMode;

      return ( this );
    }


  /*****************************************************
   *
   * Returns the over images.
   *
   *****************************************************/
  public ArrayList<URL> getOverImageURLList()
    {
    return ( mOverImageURLList );
    }

  /*****************************************************
   *
   * Returns the mask blend mode
   *
   *****************************************************/
  public String getMaskBlendMode()
    {
      return ( mMaskBlendMode );
    }


  /*****************************************************
   *
   * Sets the options.
   *
   *****************************************************/
  Product setProductOptions( List<ProductOption> optionList )
    {
    if ( optionList != null && optionList.size() > 0 )
      {
      mOptionList = optionList;
      }

    return ( this );
    }


  /*****************************************************
   *
   * Returns the options.
   *
   *****************************************************/
  public List<ProductOption> getOptionList()
    {
    return ( mOptionList );
    }


  /*****************************************************
   *
   * Sets a flag.
   *
   *****************************************************/
  public Product setFlag( Flag flag, boolean set )
    {
    if ( set ) mFlags = flag.set( mFlags );
    else       mFlags = flag.clear( mFlags );

    return ( this );
    }


  /*****************************************************
   *
   * Sets any calendar images.
   *
   *****************************************************/
  public Product setCalendarImages( ArrayList<String> imageURLStringList )
    {
    mCalendarImageURLStringList = imageURLStringList;

    return ( this );
    }


  /*****************************************************
   *
   * Returns any calendar images.
   *
   *****************************************************/
  public ArrayList<String> getCalendarImages()
    {
    return ( mCalendarImageURLStringList );
    }


  /*****************************************************
   *
   * Appends all the product images used by this product,
   * to the supplied list.
   *
   *****************************************************/
  public void appendAllImages( List<URL> targetURLList )
    {
    appendIfNotNull( mHeroImageURL,    targetURLList );
    appendNonNull( mImageURLList,      targetURLList );
    appendIfNotNull( mMaskURL,         targetURLList );
    appendNonNull( mUnderImageURLList, targetURLList );
    appendNonNull( mOverImageURLList,  targetURLList );
    }


  /*****************************************************
   *
   * Returns the image creation border for this product.
   *
   *****************************************************/
  public BorderF getImageBorder()
    {
    return ( mImageBorderF != null ? mImageBorderF : new BorderF() );
    }


  /*****************************************************
   *
   * Returns the cost in a specific currency, falling back
   * if the cost is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmounts getCostWithFallback( String preferredCurrencyCode )
    {
    return ( mCost.getAmountsWithFallback( preferredCurrencyCode ) );
    }


  /*****************************************************
   *
   * Returns the cost in the currency for the supplied locale,
   *
   *****************************************************/
  public SingleCurrencyAmounts getCostWithFallback( Locale locale )
    {
    return ( mCost.getAmountsWithFallback( locale ) );
    }


  /*****************************************************
   *
   * Returns the cost in multiple currencies.
   *
   *****************************************************/
  public MultipleCurrencyAmounts getCost()
    {
    return ( mCost );
    }


  /*****************************************************
   *
   * Returns the cost in a specific currency.
   *
   *****************************************************/
  public BigDecimal getCost( String currencyCode )
    {
    SingleCurrencyAmounts cost = mCost.get( currencyCode );

    if ( cost == null )
      {
      throw ( new IllegalArgumentException( "No cost found for currency " + currencyCode ) );
      }

    return ( cost.getAmount() );
    }


  /*****************************************************
   *
   * Returns a set of supported currency codes.
   *
   *****************************************************/
  public Set<String> getCurrenciesSupported()
    {
    return ( mCost.getAllCurrencyCodes() );
    }


  /*****************************************************
   *
   * Returns the shipping cost to a destination country.
   *
   *****************************************************/
  public MultipleCurrencyAmounts getShippingCost( Country country )
    {
    SingleDestinationShippingCost shippingCost = mShippingCosts.getCost( country );

    if ( shippingCost != null ) return ( shippingCost.getCost() );


    return ( null );
    }


  /*****************************************************
   *
   * Returns the shipping costs, but sorted by relevance
   * to the supplied country code.
   *
   *****************************************************/
  public List<SingleDestinationShippingCost> getSortedShippingCosts( Country country )
    {
    // Get the shipping costs as a list
    List<SingleDestinationShippingCost> shippingCostList = mShippingCosts.asList();

    // Sort the list in order of relevance
    Collections.sort( shippingCostList, new ShippingCostRelevanceComparator( country ) );

    return ( shippingCostList );
    }


  /*****************************************************
   *
   * Returns TRUE if the product gets split into multiple
   * packs if more images that a pack contains are added
   *
   *****************************************************/
  public boolean hasMultiplePackSupport()
    {
    switch (mUserJourneyType)
      {
      case PHOTOBOOK:
      case POSTER:
      case CALENDAR:
      case PHONE_CASE:
        return false;

      case GREETINGCARD:
        return true;

      default:
        return true;
      }
    }

  /*****************************************************
   *
   * Returns a log-displayable string representing this
   * product.
   *
   *****************************************************/
  public String toLogString( String groupLabel )
    {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append( "Group              : " ).append( groupLabel != null ? groupLabel : "?" ).append( "\n" );
    stringBuilder.append( "Id                 : " ).append( mId ).append( "\n" );
    stringBuilder.append( "Code               : " ).append( mCode ).append( "\n" );
    stringBuilder.append( "Name               : " ).append( mName ).append( "\n" );
    stringBuilder.append( "Description        : " ).append( mDescription ).append( "\n" );
    stringBuilder.append( "Type               : " ).append( mType ).append( "\n" );
    stringBuilder.append( "Category           : " ).append( mCategory ).append( "\n" );
    stringBuilder.append( "User Journey Type  : " ).append( mUserJourneyType.name() ).append( "\n" );
    stringBuilder.append( "Quantity Per Sheet : " ).append( mQuantityPerSheet ).append( "\n" );
    stringBuilder.append( "Hero Image URL     : " ).append( mHeroImageURL.toString() ).append( "\n" );

    stringBuilder.append( "Prices :" ).append( "\n" );
    stringBuilder.append( mCost != null ? mCost.toString() : "null" ).append( "\n" );

    stringBuilder.append( "  ..." ).append( "\n" );

    stringBuilder.append( "Mask URL           : " ).append( mMaskURL != null ? mMaskURL.toString() : null ).append( "\n" );
    stringBuilder.append( "Mask Bleed         : " ).append( mMaskBleed != null ? mMaskBleed.toString() : null ).append( "\n" );

    stringBuilder.append( "  ..." ).append( "\n" );

    stringBuilder.append( "Sizes :" ).append( "\n" );

    for ( SingleUnitSize singleUnitSize : mSize.getAll() )
      {
      stringBuilder
              .append( "  " )
              .append( singleUnitSize.getWidth() )
              .append( " x " )
              .append( singleUnitSize.getHeight() )
              .append( " " )
              .append( singleUnitSize.getUnit().name() )
              .append( "\n" );
      }

    stringBuilder.append( "Image Aspect Ratio : " ).append( mImageAspectRatio >= KiteSDK.FLOAT_ZERO_THRESHOLD ? mImageAspectRatio : String.valueOf( mImageAspectRatio ) ).append( "\n" );
    stringBuilder.append( "Image Border       : " ).append( mImageBorderF != null ? mImageBorderF.toString() : null ).append( "\n" );


    if ( mUnderImageURLList != null && mUnderImageURLList.size() > 0 )
      {
      stringBuilder.append( "Under images:" ).append( "\n" );

      for ( URL underImageURL : mUnderImageURLList )
        {
        stringBuilder.append( "  " ).append( underImageURL.toString() ).append( "\n" );
        }
      }


    if ( mOverImageURLList != null && mOverImageURLList.size() > 0 )
      {
      stringBuilder.append( "Over images:" ).append( "\n" );

      for ( URL overImageURL : mOverImageURLList )
        {
        stringBuilder.append( "  " ).append( overImageURL.toString() ).append( "\n" );
        }
      }


    if ( mOptionList != null && mOptionList.size() > 0 )
      {
      stringBuilder.append( "Options:" ).append( "\n" );

      for ( ProductOption option : mOptionList )
        {
        stringBuilder.append( "  " ).append( option.getName() ).append( " ( " ).append( option.getCode() ).append( " )\n" );

        List<ProductOption.Value> valueList = option.getValueList();

        for ( ProductOption.Value value : valueList )
          {
          stringBuilder.append( "    " ).append( value.getName() ).append( " ( " ).append( value.getCode() ).append( " )\n" );
          }
        }
      }


    return ( stringBuilder.toString() );
    }


  /*****************************************************
   *
   * Returns a log-displayable string representing this
   * product.
   *
   *****************************************************/
  public String toLogString()
    {
    return ( toLogString( null ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An enum of the different flags that may be set for
   * this product.
   *
   *****************************************************/
  public enum Flag
    {
    PRINT_IN_STORE          ( "print_in_store",          0x0001 ),
    SUPPORTS_TEXT_ON_BORDER ( "supports_text_on_border", 0x0002 );


    public String  tag;
    public int     bit;


    static Flag fromTag( String searchTag )
      {
      for ( Flag candidateFlag : values() )
        {
        if ( candidateFlag.tag.equalsIgnoreCase( searchTag ) ) return ( candidateFlag );
        }

      return ( null );
      }


    private Flag ( String tag, int bit )
      {
      this.tag = tag;
      this.bit = bit;
      }


    int set( int flags )
      {
      return ( flags | this.bit );
      }


    int clear( int flags )
      {
      return ( flags & ~ this.bit );
      }


    boolean isSet( int flags )
      {
      return ( ( flags & this.bit ) != 0 );
      }
    }


  /*****************************************************
   *
   * A shipping cost comparator. This is used to sort a
   * list of shipping costs into relevance order.
   *
   *****************************************************/
  private class ShippingCostRelevanceComparator implements Comparator<SingleDestinationShippingCost>
    {
    private static final boolean DEBUG_COMPARISONS = false;


    private Country  mCountry;


    ShippingCostRelevanceComparator( Country country )
      {
      mCountry = country;
      }


    /*****************************************************
     *
     * Compares two destinations, and returns a value indicating
     * how they should be sorted in relevance.
     *
     * Note that we can be a bit lazy with this, since we shouldn't
     * have any duplicate destinations (i.e. we should never have to
     * return 0).
     *
     *****************************************************/
    @Override
    public int compare( SingleDestinationShippingCost leftShippingCost, SingleDestinationShippingCost rightShippingCost )
      {
      String leftDestinationCode  = leftShippingCost.getDestinationCode();
      String rightDestinationCode = rightShippingCost.getDestinationCode();


      int returnValue = 1;

      // Always put the country we're in first
      if      ( mCountry != null && mCountry.usesISOCode( leftDestinationCode  ) ) returnValue = -1;
      else if ( mCountry != null && mCountry.usesISOCode( rightDestinationCode ) ) returnValue = +1;


      // Default order if we don't know what country we're in:
      // UK
      // USA
      // Any country code
      // Europe
      // Rest of world

      else if ( Country.UK.usesISOCode( leftDestinationCode )                 ) returnValue = -1;
      else if ( Country.UK.usesISOCode( rightDestinationCode )                ) returnValue = +1;

      else if ( Country.USA.usesISOCode( leftDestinationCode )                ) returnValue = -1;
      else if ( Country.USA.usesISOCode( rightDestinationCode )               ) returnValue = +1;

      else if ( Country.existsForISOCode( leftDestinationCode )               ) returnValue = -1;
      else if ( Country.existsForISOCode( rightDestinationCode )              ) returnValue = +1;

      else if ( DESTINATION_CODE_EUROPE.equals( leftDestinationCode )         ) returnValue = -1;
      else if ( DESTINATION_CODE_EUROPE.equals( rightDestinationCode )        ) returnValue = +1;

      else if ( DESTINATION_CODE_REST_OF_WORLD.equals( leftDestinationCode )  ) returnValue = -1;
      else if ( DESTINATION_CODE_REST_OF_WORLD.equals( rightDestinationCode ) ) returnValue = +1;


      if ( DEBUG_COMPARISONS ) Log.d( LOG_TAG, "Compare: " + leftDestinationCode + "/" + rightDestinationCode + " = " + returnValue );

      return ( returnValue );
      }
    }

  }
