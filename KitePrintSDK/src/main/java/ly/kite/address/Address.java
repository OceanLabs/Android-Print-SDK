/*****************************************************
 *
 * Address.java
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

package ly.kite.address;


///// Import(s) /////

import android.os.Parcelable;
import android.os.Parcel;

import ly.kite.util.StringUtils;


///// Class Declaration /////

/*****************************************************
 *
 * This class holds an address
 *
 *****************************************************/
public class Address implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "Address";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>()
    {
    public Address createFromParcel( Parcel in )
      {
      return new Address( in );
      }

    public Address[] newArray( int size )
      {
      return new Address[ size ];
      }
    };


  ////////// Member Variable(s) //////////

  private String  mId;
  private String  mRecipientName;
  private String  mLine1;
  private String  mLine2;
  private String  mCity;
  private String  mStateOrCounty;
  private String  mZIPOrPostalCode;
  private Country mCountry;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true if the supplied string is non null and
   * contains non-white space character(s).
   *
   *****************************************************/
  private static boolean isPopulated( String testString )
    {
    return ( testString != null && testString.trim().length() > 0 );
    }


  /*****************************************************
   *
   * Returns true if both the addresses are null, or equal.
   *
   *****************************************************/
  static public boolean areBothNullOrEqual( Address address1, Address address2 )
    {
    if ( address1 == null && address2 == null ) return ( true );
    if ( address1 == null || address2 == null ) return ( false );

    return ( address1.equals( address2 ) );
    }


  /*****************************************************
   *
   * Returns Kite's address.
   *
   *****************************************************/
  public static Address getKiteTeamAddress()
    {
    return ( new Address(
            "Kite Tech Ltd.",
            "6-8 Bonhill Street",
            null,
            "London",
            null,
            "EC2A 4BX",
            Country.getInstance( "GBR" ) ) );
    }


  ////////// Constructor(s) //////////

  public Address()
    {
    }

  public Address( String recipientName, String line1, String line2, String city, String stateOrCounty, String zipOrPostalCode, Country country )
    {
    mRecipientName   = recipientName;
    mLine1           = line1;
    mLine2           = line2;
    mCity            = city;
    mStateOrCounty   = stateOrCounty;
    mZIPOrPostalCode = zipOrPostalCode;
    mCountry         = country;
    }

  private Address( Parcel parcel )
    {
    mId              = parcel.readString();
    mRecipientName   = parcel.readString();
    mLine1           = parcel.readString();
    mLine2           = parcel.readString();
    mCity            = parcel.readString();
    mStateOrCounty   = parcel.readString();
    mZIPOrPostalCode = parcel.readString();
    mCountry         = Country.getInstance( parcel.readString() );
    }


  ////////// Parcelable Method(s) //////////

  @Override
  public int describeContents()
    {
    return ( 0 );
    }

  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    parcel.writeString( mId );
    parcel.writeString( mRecipientName );
    parcel.writeString( mLine1 );
    parcel.writeString( mLine2 );
    parcel.writeString( mCity );
    parcel.writeString( mStateOrCounty );
    parcel.writeString( mZIPOrPostalCode );
    parcel.writeString( mCountry.iso2Code() );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/
  void setId( String id )
    {
    mId = id;
    }


  String getId()
    {
    return ( mId );
    }


  public String getRecipientName()
    {
    return ( mRecipientName );
    }

  public String getLine1()
    {
    return ( mLine1 );
    }

  public String getLine2()
    {
    return ( mLine2 );
    }

  public String getCity()
    {
    return ( mCity );
    }

  public String getStateOrCounty()
    {
    return ( mStateOrCounty );
    }

  public String getZipOrPostalCode()
    {
    return ( mZIPOrPostalCode );
    }

  public Country getCountry()
    {
    return ( mCountry );
    }

  public void setRecipientName( String mRecipientName )
    {
    this.mRecipientName = mRecipientName;
    }

  public void setLine1( String line1 )
    {
    mLine1 = line1;
    }

  public void setLine2( String line2 )
    {
    mLine2 = line2;
    }

  public void setCity( String city )
    {
    mCity = city;
    }

  public void setStateOrCounty( String stateOrCounty )
    {
    mStateOrCounty = stateOrCounty;
    }

  public void setZipOrPostalCode( String zipOrPostalCode )
    {
    mZIPOrPostalCode = zipOrPostalCode;
    }

  public void setCountry( Country country )
    {
    mCountry = country;
    }


  public String toMultiLineText()
    {
    return ( toDisplayText( "\n" ) );
    }

  public String toSingleLineText()
    {
    return ( toDisplayText( ", " ) );
    }

  public String toDisplayText( String newlineString )
    {
    //if ( displayName != null ) return ( displayName );

    StringBuilder stringBuilder = new StringBuilder();

    String separator = "";

    if ( isPopulated( mRecipientName ) )
      {
      stringBuilder.append( mRecipientName );

      separator = newlineString;
      }

    if ( isPopulated( mLine1 ) )
      {
      stringBuilder
              .append( separator )
              .append( mLine1 );

      separator = ", ";
      }

    if ( isPopulated( mLine2 ) )
      {
      stringBuilder
              .append( separator )
              .append( mLine2 );

      separator = ", ";
      }

    if ( isPopulated( mCity ) )
      {
      stringBuilder
              .append( separator )
              .append( mCity );

      separator = ", ";
      }

    if ( isPopulated( mStateOrCounty ) )
      {
      stringBuilder
              .append( separator )
              .append( mStateOrCounty );

      separator = ", ";
      }

    if ( isPopulated( mZIPOrPostalCode ) )
      {
      stringBuilder
              .append( separator )
              .append( mZIPOrPostalCode );

      separator = newlineString;
      }


    String countryDisplayName;

    if ( mCountry != null && isPopulated( countryDisplayName = mCountry.displayName() ) )
      {
      stringBuilder
              .append( separator )
              .append( countryDisplayName );
      }


    return ( stringBuilder.toString() );
    }


  String getDisplayAddressWithoutRecipient()
    {
    StringBuilder strBuilder = new StringBuilder();

    if ( mLine1 != null && mLine1.trim().length() > 0 )
      strBuilder.append( strBuilder.length() > 0 ? ", " : "" ).append( mLine1 );
    if ( mLine2 != null && mLine2.trim().length() > 0 )
      strBuilder.append( strBuilder.length() > 0 ? ", " : "" ).append( mLine2 );
    if ( mCity != null && mCity.trim().length() > 0 )
      strBuilder.append( strBuilder.length() > 0 ? ", " : "" ).append( mCity );
    if ( mStateOrCounty != null && mStateOrCounty.trim().length() > 0 )
      strBuilder.append( strBuilder.length() > 0 ? ", " : "" ).append( mStateOrCounty );
    if ( mZIPOrPostalCode != null && mZIPOrPostalCode.trim().length() > 0 )
      strBuilder.append( strBuilder.length() > 0 ? ", " : "" ).append( mZIPOrPostalCode );
    if ( mCountry != null && mCountry.displayName().trim().length() > 0 )
      strBuilder.append( strBuilder.length() > 0 ? ", " : "" ).append( mCountry.displayName() );

    return strBuilder.toString();
    }


  @Override
  public String toString()
    {
    StringBuilder strBuilder = new StringBuilder();

    if ( mRecipientName != null && mRecipientName.trim().length() > 0 )
      strBuilder.append( mRecipientName );
    String addressWithoutRecipient = getDisplayAddressWithoutRecipient();
    if ( addressWithoutRecipient != null && addressWithoutRecipient.trim().length() > 0 )
      strBuilder.append( strBuilder.length() > 0 ? ", " : "" ).append( addressWithoutRecipient );

    return strBuilder.toString();
    }


  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null || ( !( otherObject instanceof Address ) ) ) return ( false );

    Address otherAddress = (Address)otherObject;

    return ( StringUtils.areBothNullOrEqual( mRecipientName, otherAddress.mRecipientName ) &&
             StringUtils.areBothNullOrEqual( mLine1, otherAddress.mLine1 ) &&
             StringUtils.areBothNullOrEqual( mLine2, otherAddress.mLine2 ) &&
             StringUtils.areBothNullOrEqual( mCity, otherAddress.mCity ) &&
             StringUtils.areBothNullOrEqual( mStateOrCounty, otherAddress.mStateOrCounty ) &&
             StringUtils.areBothNullOrEqual( mZIPOrPostalCode, otherAddress.mZIPOrPostalCode ) &&
             Country.areBothNullOrEqual( mCountry, otherAddress.mCountry ) );
    }

    public boolean isFilledIn() {
      if(mRecipientName == null || mRecipientName.length() < 1) {
        return false;
      }

      if(mLine1 == null || mLine1.length() < 1) {
        return false;
      }

      if(mCity == null || mCity.length() < 1) {
        return false;
      }

      if(mZIPOrPostalCode == null || mZIPOrPostalCode.length() < 1) {
        return false;
      }

      return true;
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
