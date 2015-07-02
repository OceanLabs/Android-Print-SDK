/*****************************************************
 *
 * TemplateClass.java
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

package ly.kite.address;


///// Import(s) /////

import java.util.Hashtable;
import java.util.Locale;


///// Class Declaration /////

/*****************************************************
 *
 * This enum represents a country.
 *
 *****************************************************/
public enum Country
  {
    ALAND_ISLANDS                     ( "Ã…land Islands", "AX", "ALA", "EUR", true ),
    AFGANISTAN                        ( "Afghanistan", "AF", "AFG", "AFN", false ),
    ALBANIA                           ( "Albania", "AL", "ALB", "ALL", true ),
    ALGERIA                           ( "Algeria", "DZ", "DZA", "DZD", false ),
    AMERICAN_SAMOA                    ( "American Samoa", "AS", "ASM", "USD", false ),
    ANDORRA                           ( "Andorra", "AD", "AND", "EUR", true ),
    ANGOLA                            ( "Angola", "AO", "AGO", "AOA", false ),
    ANGUILLA                          ( "Anguilla", "AI", "AIA", "XCD", false ),
    ANTIGUA_AND_BARBUDA               ( "Antigua and Barbuda", "AG", "ATG", "XCD", false ),
    ARGENTINA                         ( "Argentina", "AR", "ARG", "ARS", false ),
    ARMENIA                           ( "Armenia", "AM", "ARM", "AMD", false ),
    ARUBA                             ( "Aruba", "AW", "ABW", "AWG", false ),
    AUSTRALIA                         ( "Australia", "AU", "AUS", "AUD", false ),
    AUSTRIA                           ( "Austria", "AT", "AUT", "EUR", true ),
    AZERBAIJAN                        ( "Azerbaijan", "AZ", "AZE", "AZN", false ),
    BAHAMAS                           ( "Bahamas", "BS", "BHS", "BSD", false ),
    BAHRAIN                           ( "Bahrain", "BH", "BHR", "BHD", false ),
    BANGLADESH                        ( "Bangladesh", "BD", "BGD", "BDT", false ),
    BARBADOS                          ( "Barbados", "BB", "BRB", "BBD", false ),
    BELARUS                           ( "Belarus", "BY", "BLR", "BYR", true ),
    BELGIUM                           ( "Belgium", "BE", "BEL", "EUR", true ),
    BELIZE                            ( "Belize", "BZ", "BLZ", "BZD", false ),
    BENIN                             ( "Benin", "BJ", "BEN", "XOF", false ),
    BERMUDA                           ( "Bermuda", "BM", "BMU", "BMD", false ),
    BHUTAN                            ( "Bhutan", "BT", "BTN", "INR", false ),
    BOLIVIA                           ( "Bolivia, Plurinational State of", "BO", "BOL", "BOB", false ),
    BONAIRE                           ( "Bonaire, Sint Eustatius and Saba", "BQ", "BES", "USD", false ),
    BOSNIA_AND_HERZEGOVINA            ( "Bosnia and Herzegovina", "BA", "BIH", "BAM", true ),
    BOTSWANA                          ( "Botswana", "BW", "BWA", "BWP", false ),
    BOUVET_ISLAND                     ( "Bouvet Island", "BV", "BVT", "NOK", false ),
    BRAZIL                            ( "Brazil", "BR", "BRA", "BRL", false ),
    BRITISH_INDIAN_OCEAN_TERRITORY    ( "British Indian Ocean Territory", "IO", "IOT", "USD", false ),
    BRUNEI_DARUSSALAM                 ( "Brunei Darussalam", "BN", "BRN", "BND", false ),
    BULGARIA                          ( "Bulgaria", "BG", "BGR", "BGN", true ),
    BURKINO_FASO                      ( "Burkina Faso", "BF", "BFA", "XOF", false ),
    BURUNDI                           ( "Burundi", "BI", "BDI", "BIF", false ),
    CAMBODIA                          ( "Cambodia", "KH", "KHM", "KHR", false ),
    CAMEROON                          ( "Cameroon", "CM", "CMR", "XAF", false ),
    CANADA                            ( "Canada", "CA", "CAN", "CAD", false ),
    CAPE_VERDE                        ( "Cape Verde", "CV", "CPV", "CVE", false ),
    CAYMAN_ISLANDS                    ( "Cayman Islands", "KY", "CYM", "KYD", false ),
    CENTRAL_AFRICAN_REPUBLIC          ( "Central African Republic", "CF", "CAF", "XAF", false ),
    CHAD                              ( "Chad", "TD", "TCD", "XAF", false ),
    CHILE                             ( "Chile", "CL", "CHL", "CLP", false ),
    CHINA                             ( "China", "CN", "CHN", "CNY", false ),
    CHRISTMAS_ISLANDS                 ( "Christmas Island", "CX", "CXR", "AUD", false ),
    COCOS_ISLANDS                     ( "Cocos (Keeling) Islands", "CC", "CCK", "AUD", false ),
    COLOMBIA                          ( "Colombia", "CO", "COL", "COP", false ),
    COMOROS                           ( "Comoros", "KM", "COM", "KMF", false ),
    CONGO                             ( "Congo", "CG", "COG", "XAF", false ),
    DEMOCRATIC_REPUBLIC_OF_CONGO      ( "Congo, the Democratic Republic of the", "CD", "COD", "CDF", false ),
    COOK_ISLANDS                      ( "Cook Islands", "CK", "COK", "NZD", false ),
    COSTA_RICA                        ( "Costa Rica", "CR", "CRI", "CRC", false ),
    CROATIA                           ( "Croatia", "HR", "HRV", "HRK", true ),
    CUBA                              ( "Cuba", "CU", "CUB", "CUP", false ),
    CURACAO                           ( "CuraÃ§ao", "CW", "CUW", "ANG", false ),
    CYPRUS                            ( "Cyprus", "CY", "CYP", "EUR", false ),
    CZECH_REPUBLIC                    ( "Czech Republic", "CZ", "CZE", "CZK", true ),
    IVORY_COAST                       ( "CÃ´te d'Ivoire", "CI", "CIV", "XOF", false ),
    DENMARK                           ( "Denmark", "DK", "DNK", "DKK", true ),
    DJIBOUTI                          ( "Djibouti", "DJ", "DJI", "DJF", false ),
    DOMINICA                          ( "Dominica", "DM", "DMA", "XCD", false ),
    DOMINICAN_REPUBLIC                ( "Dominican Republic", "DO", "DOM", "DOP", false ),
    ECUADOR                           ( "Ecuador", "EC", "ECU", "USD", false ),
    EGYPT                             ( "Egypt", "EG", "EGY", "EGP", false ),
    EL_SALVADOR                       ( "El Salvador", "SV", "SLV", "USD", false ),
    EQUATORIAL_GUINEA                 ( "Equatorial Guinea", "GQ", "GNQ", "XAF", false ),
    ERITREA                           ( "Eritrea", "ER", "ERI", "ERN", false ),
    ESTONIA                           ( "Estonia", "EE", "EST", "EUR", true ),
    ETHIOPIA                          ( "Ethiopia", "ET", "ETH", "ETB", false ),
    FALKLAND_ISLANDS                  ( "Falkland Islands (Malvinas)", "FK", "FLK", "FKP", false ),
    FAROE_ISLANDS                     ( "Faroe Islands", "FO", "FRO", "DKK", true ),
    FIJI                              ( "Fiji", "FJ", "FJI", "FJD", false ),
    FINLAND                           ( "Finland", "FI", "FIN", "EUR", true ),
    FRANCE                            ( "France", "FR", "FRA", "EUR", true ),
    FRENCH_GUIANA                     ( "French Guiana", "GF", "GUF", "EUR", false ),
    FRENCH_POLYNESIA                  ( "French Polynesia", "PF", "PYF", "XPF", false ),
    FRENCH_SOUTHERN_TERRITORIES       ( "French Southern Territories", "TF", "ATF", "EUR", false ),
    GABON                             ( "Gabon", "GA", "GAB", "XAF", false ),
    GAMBIA                            ( "Gambia", "GM", "GMB", "GMD", false ),
    GEORGIA                           ( "Georgia", "GE", "GEO", "GEL", false ),
    GERMANY                           ( "Germany", "DE", "DEU", "EUR", true ),
    GHANA                             ( "Ghana", "GH", "GHA", "GHS", false ),
    GIBRALTAR                         ( "Gibraltar", "GI", "GIB", "GIP", true ),
    GREECE                            ( "Greece", "GR", "GRC", "EUR", true ),
    GREENLAND                         ( "Greenland", "GL", "GRL", "DKK", false ),
    GRENEDA                           ( "Grenada", "GD", "GRD", "XCD", false ),
    GUADELOUPE                        ( "Guadeloupe", "GP", "GLP", "EUR", false ),
    GUAM                              ( "Guam", "GU", "GUM", "USD", false ),
    GUATEMALA                         ( "Guatemala", "GT", "GTM", "GTQ", false ),
    GUERNSEY                          ( "Guernsey", "GG", "GGY", "GBP", true ),
    GUINEA                            ( "Guinea", "GN", "GIN", "GNF", false ),
    GUINEA_BISSAU                     ( "Guinea-Bissau", "GW", "GNB", "XOF", false ),
    GUYANA                            ( "Guyana", "GY", "GUY", "GYD", false ),
    HAITI                             ( "Haiti", "HT", "HTI", "USD", false ),
    HEARD_ISLAND_AND_MCDONALD_ISLANDS ( "Heard Island and Mcdonald Islands", "HM", "HMD", "AUD", false ),
    HOLY_SEE                          ( "Holy See (Vatican City State)", "VA", "VAT", "EUR", true ),
    HONDURAS                          ( "Honduras", "HN", "HND", "HNL", false ),
    HONG_KONG                         ( "Hong Kong", "HK", "HKG", "HKD", false ),
    HUNGARY                           ( "Hungary", "HU", "HUN", "HUF", true ),
    ICELAND                           ( "Iceland", "IS", "ISL", "ISK", true ),
    INDIA                             ( "India", "IN", "IND", "INR", false ),
    INDONESIA                         ( "Indonesia", "ID", "IDN", "IDR", false ),
    IRAN                              ( "Iran, Islamic Republic of", "IR", "IRN", "IRR", false ),
    IRAQ                              ( "Iraq", "IQ", "IRQ", "IQD", false ),
    IRELAND                           ( "Ireland", "IE", "IRL", "EUR", true ),
    ISLE_OF_MAN                       ( "Isle of Man", "IM", "IMN", "GBP", true ),
    ISRAEL                            ( "Israel", "IL", "ISR", "ILS", false ),
    ITALY                             ( "Italy", "IT", "ITA", "EUR", true ),
    JAMAICA                           ( "Jamaica", "JM", "JAM", "JMD", false ),
    JAPAN                             ( "Japan", "JP", "JPN", "JPY", false ),
    JERSEY                            ( "Jersey", "JE", "JEY", "GBP", true ),
    JORDAN                            ( "Jordan", "JO", "JOR", "JOD", false ),
    KAZAKHSTAN                        ( "Kazakhstan", "KZ", "KAZ", "KZT", false ),
    KENYA                             ( "Kenya", "KE", "KEN", "KES", false ),
    KIRIBATI                          ( "Kiribati", "KI", "KIR", "AUD", false ),
    NORTH_KOREA                       ( "Korea, Democratic People's Republic of", "KP", "PRK", "KPW", false ),
    SOUTH_KOREA                       ( "Korea, Republic of", "KR", "KOR", "KRW", false ),
    KUWAIT                            ( "Kuwait", "KW", "KWT", "KWD", false ),
    KYRGYZSTAN                        ( "Kyrgyzstan", "KG", "KGZ", "KGS", false ),
    LAO                               ( "Lao People's Democratic Republic", "LA", "LAO", "LAK", false ),
    LATVIA                            ( "Latvia", "LV", "LVA", "LVL", true ),
    LEBANON                           ( "Lebanon", "LB", "LBN", "LBP", false ),
    LESOTHO                           ( "Lesotho", "LS", "LSO", "ZAR", false ),
    LIBERIA                           ( "Liberia", "LR", "LBR", "LRD", false ),
    LIBYA                             ( "Libya", "LY", "LBY", "LYD", false ),
    LIECHTENSTEIN                     ( "Liechtenstein", "LI", "LIE", "CHF", true ),
    LITHUANIA                         ( "Lithuania", "LT", "LTU", "LTL", true ),
    LUXEMBOURG                        ( "Luxembourg", "LU", "LUX", "EUR", true ),
    MACAO                             ( "Macao", "MO", "MAC", "MOP", false ),
    FYR_MACEDONIA                     ( "Macedonia, the Former Yugoslav Republic of", "MK", "MKD", "MKD", true ),
    MADAGASCAR                        ( "Madagascar", "MG", "MDG", "MGA", false ),
    MALAWI                            ( "Malawi", "MW", "MWI", "MWK", false ),
    MALAYSIA                          ( "Malaysia", "MY", "MYS", "MYR", false ),
    MALDIVES                          ( "Maldives", "MV", "MDV", "MVR", false ),
    MALI                              ( "Mali", "ML", "MLI", "XOF", false ),
    MALTA                             ( "Malta", "MT", "MLT", "EUR", true ),
    MARSHALL_ISLANDS                  ( "Marshall Islands", "MH", "MHL", "USD", false ),
    MARTINIQUE                        ( "Martinique", "MQ", "MTQ", "EUR", false ),
    MAURITANIA                        ( "Mauritania", "MR", "MRT", "MRO", false ),
    MAURITIUS                         ( "Mauritius", "MU", "MUS", "MUR", false ),
    MAYOTTE                           ( "Mayotte", "YT", "MYT", "EUR", false ),
    MEXICO                            ( "Mexico", "MX", "MEX", "MXN", false ),
    MICRONESIA                        ( "Micronesia, Federated States of", "FM", "FSM", "USD", false ),
    MOLDOVA                           ( "Moldova, Republic of", "MD", "MDA", "MDL", true ),
    MONACO                            ( "Monaco", "MC", "MCO", "EUR", true ),
    MONGOLIA                          ( "Mongolia", "MN", "MNG", "MNT", false ),
    MONTENEGRO                        ( "Montenegro", "ME", "MNE", "EUR", true ),
    MONTSERRAT                        ( "Montserrat", "MS", "MSR", "XCD", false ),
    MOROCCO                           ( "Morocco", "MA", "MAR", "MAD", false ),
    MOZAMBIQUE                        ( "Mozambique", "MZ", "MOZ", "MZN", false ),
    MYANMAR                           ( "Myanmar", "MM", "MMR", "MMK", false ),
    NAMIBIA                           ( "Namibia", "NA", "NAM", "ZAR", false ),
    NAURU                             ( "Nauru", "NR", "NRU", "AUD", false ),
    NEPAL                             ( "Nepal", "NP", "NPL", "NPR", false ),
    NETHERLANDS                       ( "Netherlands", "NL", "NLD", "EUR", true ),
    NEW_CALEDONIA                     ( "New Caledonia", "NC", "NCL", "XPF", false ),
    NEW_ZEALAND                       ( "New Zealand", "NZ", "NZL", "NZD", false ),
    NICARAGUA                         ( "Nicaragua", "NI", "NIC", "NIO", false ),
    NIGER                             ( "Niger", "NE", "NER", "XOF", false ),
    NIGERIA                           ( "Nigeria", "NG", "NGA", "NGN", false ),
    NIUE                              ( "Niue", "NU", "NIU", "NZD", false ),
    NORFOLK_ISLAND                    ( "Norfolk Island", "NF", "NFK", "AUD", false ),
    NORTHERN_MARIANA_ISLANDS          ( "Northern Mariana Islands", "MP", "MNP", "USD", false ),
    NORWAY                            ( "Norway", "NO", "NOR", "NOK", true ),
    OMAN                              ( "Oman", "OM", "OMN", "OMR", false ),
    PAKISTAN                          ( "Pakistan", "PK", "PAK", "PKR", false ),
    PALAU                             ( "Palau", "PW", "PLW", "USD", false ),
    PANAMA                            ( "Panama", "PA", "PAN", "USD", false ),
    PAPUA_NEW_GUINEA                  ( "Papua New Guinea", "PG", "PNG", "PGK", false ),
    PARAGUAY                          ( "Paraguay", "PY", "PRY", "PYG", false ),
    PERU                              ( "Peru", "PE", "PER", "PEN", false ),
    PHILIPPINES                       ( "Philippines", "PH", "PHL", "PHP", false ),
    PITCAIRN                          ( "Pitcairn", "PN", "PCN", "NZD", false ),
    POLAND                            ( "Poland", "PL", "POL", "PLN", true ),
    PORTUGAL                          ( "Portugal", "PT", "PRT", "EUR", true ),
    PUERTO_RICO                       ( "Puerto Rico", "PR", "PRI", "USD", false ),
    QATAR                             ( "Qatar", "QA", "QAT", "QAR", false ),
    ROMANIA                           ( "Romania", "RO", "ROU", "RON", true ),
    RUSSIA                            ( "Russian Federation", "RU", "RUS", "RUB", true ),
    RWANDA                            ( "Rwanda", "RW", "RWA", "RWF", false ),
    REUNION                           ( "RÃ©union", "RE", "REU", "EUR", false ),
    SAINT_BARTS                       ( "Saint BarthÃ©lemy", "BL", "BLM", "EUR", false ),
    SAINT_HELENA                      ( "Saint Helena, Ascension and Tristan da Cunha", "SH", "SHN", "SHP", false ),
    SAINT_KITTS_AND_NEVIS             ( "Saint Kitts and Nevis", "KN", "KNA", "XCD", false ),
    SAINT_LUCIA                       ( "Saint Lucia", "LC", "LCA", "XCD", false ),
    SAINT_MARTIN                      ( "Saint Martin (French part)", "MF", "MAF", "EUR", false ),
    SAINT_PIERRE_AND_MIQUELON         ( "Saint Pierre and Miquelon", "PM", "SPM", "EUR", false ),
    SAINT_VINCENT_AND_GRENADINES      ( "Saint Vincent and the Grenadines", "VC", "VCT", "XCD", false ),
    SAMOA                             ( "Samoa", "WS", "WSM", "WST", false ),
    SAN_MARINO                        ( "San Marino", "SM", "SMR", "EUR", true ),
    SAO_TOME_AND_PRINCIPE             ( "Sao Tome and Principe", "ST", "STP", "STD", false ),
    SAUDI_ARABIA                      ( "Saudi Arabia", "SA", "SAU", "SAR", false ),
    SENEGAL                           ( "Senegal", "SN", "SEN", "XOF", false ),
    SERBIA                            ( "Serbia", "RS", "SRB", "RSD", true ),
    SEYCHELLES                        ( "Seychelles", "SC", "SYC", "SCR", false ),
    SIERRA_LEONE                      ( "Sierra Leone", "SL", "SLE", "SLL", false ),
    SINGAPORE                         ( "Singapore", "SG", "SGP", "SGD", false ),
    SINT_MAARTEN                      ( "Sint Maarten (Dutch part)", "SX", "SXM", "ANG", false ),
    SLOVAKIA                          ( "Slovakia", "SK", "SVK", "EUR", true ),
    SLOVENIA                          ( "Slovenia", "SI", "SVN", "EUR", true ),
    SOLOMON_ISLANDS                   ( "Solomon Islands", "SB", "SLB", "SBD", false ),
    SOMALIA                           ( "Somalia", "SO", "SOM", "SOS", false ),
    SOUTH_AFRICA                      ( "South Africa", "ZA", "ZAF", "ZAR", false ),
    SOUTH_SUDAN                       ( "South Sudan", "SS", "SSD", "SSP", false ),
    SPAIN                             ( "Spain", "ES", "ESP", "EUR", true ),
    SRI_LANKA                         ( "Sri Lanka", "LK", "LKA", "LKR", false ),
    SUDAN                             ( "Sudan", "SD", "SDN", "SDG", false ),
    SURINAME                          ( "Suriname", "SR", "SUR", "SRD", false ),
    SVALBARD_AND_JAN_MAYEN            ( "Svalbard and Jan Mayen", "SJ", "SJM", "NOK", true ),
    SWAZILAND                         ( "Swaziland", "SZ", "SWZ", "SZL", false ),
    SWEDEN                            ( "Sweden", "SE", "SWE", "SEK", true ),
    SWITZERLAND                       ( "Switzerland", "CH", "CHE", "CHF", true ),
    SYRIA                             ( "Syrian Arab Republic", "SY", "SYR", "SYP", false ),
    TAIWAN                            ( "Taiwan", "TW", "TWN", "TWD", false ),
    TAJIKISTAN                        ( "Tajikistan", "TJ", "TJK", "TJS", false ),
    TANZANIA                          ( "Tanzania, United Republic of", "TZ", "TZA", "TZS", false ),
    THAILAND                          ( "Thailand", "TH", "THA", "THB", false ),
    TIMOR_LESTE                       ( "Timor-Leste", "TL", "TLS", "USD", false ),
    TOGO                              ( "Togo", "TG", "TGO", "XOF", false ),
    TOKELAU                           ( "Tokelau", "TK", "TKL", "NZD", false ),
    TONGA                             ( "Tonga", "TO", "TON", "TOP", false ),
    TRINIDAD_AND_TOBAGO               ( "Trinidad and Tobago", "TT", "TTO", "TTD", false ),
    TUNISIA                           ( "Tunisia", "TN", "TUN", "TND", false ),
    TURKEY                            ( "Turkey", "TR", "TUR", "TRY", false ),
    TURKMENISTAN                      ( "Turkmenistan", "TM", "TKM", "TMT", false ),
    TURKS_AND_CAICOS_ISLANDS          ( "Turks and Caicos Islands", "TC", "TCA", "USD", false ),
    TUVALU                            ( "Tuvalu", "TV", "TUV", "AUD", false ),
    UGANDA                            ( "Uganda", "UG", "UGA", "UGX", false ),
    UKRAINE                           ( "Ukraine", "UA", "UKR", "UAH", true ),
    UAE                               ( "United Arab Emirates", "AE", "ARE", "AED", false ),
    UK                                ( "United Kingdom", "GB", "GBR", "GBP", true ),
    USA                               ( "United States", "US", "USA", "USD", false ),
    USA_MINOR_OUTLYING_ISLANDS        ( "United States Minor Outlying Islands", "UM", "UMI", "USD", false ),
    URUGUAY                           ( "Uruguay", "UY", "URY", "UYU", false ),
    UZBEKISTAN                        ( "Uzbekistan", "UZ", "UZB", "UZS", false ),
    VANUATU                           ( "Vanuatu", "VU", "VUT", "VUV", false ),
    VENEZUELA                         ( "Venezuela, Bolivarian Republic of", "VE", "VEN", "VEF", false ),
    VIET_NAM                          ( "Viet Nam", "VN", "VNM", "VND", false ),
    BRITISH_VIRGIN_ISLANDS            ( "Virgin Islands, British", "VG", "VGB", "USD", false ),
    US_VIRGIN_ISLANDS                 ( "Virgin Islands, U.S.", "VI", "VIR", "USD", false ),
    WALLIS_AND_FUTUNA                 ( "Wallis and Futuna", "WF", "WLF", "XPF", false ),
    WESTERN_SAHARA                    ( "Western Sahara", "EH", "ESH", "MAD", false ),
    YEMEN                             ( "Yemen", "YE", "YEM", "YER", false ),
    ZAMBIA                            ( "Zambia", "ZM", "ZMB", "ZMW", false ),
    ZIMBABWE                          ( "Zimbabwe", "ZW", "ZWE", "ZWL", false);


  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "TemplateClass";


  ////////// Static Variable(s) //////////

  private static Hashtable<String,Country>  sCodeCountryTable;


  ////////// Member Variable(s) //////////

  private String   mDisplayName;
  private String   mISO2Code;
  private String   mISO3Code;
  private String   mISO3CurrencyCode;
  private boolean  mIsInEurope;


  ////////// Static Initialiser(s) //////////

  static
    {
    sCodeCountryTable = new Hashtable<>();

    for ( Country country : values() )
      {
      // Insert entries in the code-country table: one for the 2-character code
      // and one for the 3-character code.
      sCodeCountryTable.put( country.mISO2Code, country );
      sCodeCountryTable.put( country.mISO3Code, country );
      }


    }


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns the country with the same 2 or 3-character
   * code.
   *
   *****************************************************/
  public static Country getInstance( String isoCode )
    {
    // Try and find the country in the look-up table we built in the
    // static initialiser
    return ( sCodeCountryTable.get( isoCode ) );
    }


  /*****************************************************
   *
   * Returns the country for the locale.
   *
   *****************************************************/
  public static Country getInstance( Locale locale )
    {
    if ( locale == null )
      {
      throw ( new IllegalArgumentException( "Please provide a non null locale" ) );
      }

    return ( Country.getInstance( locale.getISO3Country() ) );
    }


  /*****************************************************
   *
   * Returns the country for the default locale.
   *
   *****************************************************/
  public static Country getInstance()
    {
    return ( getInstance( Locale.getDefault() ) );
    }


  /*****************************************************
   *
   * Returns true if the code exists, i.e. there is a
   * country that matches either the 2 or 3-character code.
   *
   *****************************************************/
  public static boolean existsForISOCode( String isoCode )
    {
    return ( sCodeCountryTable.containsKey( isoCode ) );
    }


  ////////// Constructor(s) //////////

  private Country( String displayName, String iso2Code, String iso3Code, String iso3CurrencyCode, boolean isInEurope )
    {
    mDisplayName      = displayName;
    mISO2Code         = iso2Code;
    mISO3Code         = iso3Code;
    mISO3CurrencyCode = iso3CurrencyCode;
    mIsInEurope       = isInEurope;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the 2-character ISO country code.
   *
   *****************************************************/
  public String iso2Code()
    {
    return ( mISO2Code );
    }


  /*****************************************************
   *
   * Returns the 3-character ISO country code.
   *
   *****************************************************/
  public String iso3Code()
    {
    return ( mISO3Code );
    }


  /*****************************************************
   *
   * Returns true if the supplied code matches either the
   * 2 or 3-character code.
   *
   *****************************************************/
  public boolean usesISOCode( String isoCode )
    {
    if ( mISO2Code.equals( isoCode ) || mISO3Code.equals( isoCode ) ) return ( true );

    return ( false );
    }


  /*****************************************************
   *
   * Returns the country name.
   *
   *****************************************************/
  public String displayName()
    {
    return (mDisplayName);
    }


  /*****************************************************
   *
   * Returns the code of the main currency used by the country.
   *
   *****************************************************/
  public String iso3CurrencyCode()
    {
    return ( mISO3CurrencyCode );
    }


  /*****************************************************
   *
   * Returns true if the country is in Europe for shipping
   * purposes.
   *
   *****************************************************/
  public boolean isInEurope()
    {
    return ( mIsInEurope );
    }


  /*****************************************************
   *
   * Returns a string representation of the country. Currently
   * this is the country name.
   *
   *****************************************************/
  @Override
  public String toString()
    {
    return (mDisplayName);
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }


//public static List<Country> COUNTRIES = new ArrayList<Country>();
//static {
//List<Country> COUNTRIES = new ArrayList<Country>();
//COUNTRIES.add(new Country("Ã…land Islands", "AX", "ALA", "EUR", true));
//COUNTRIES.add(new Country("Afghanistan", "AF", "AFG", "AFN", false));
//COUNTRIES.add(new Country("Albania", "AL", "ALB", "ALL", true));
//COUNTRIES.add(new Country("Algeria", "DZ", "DZA", "DZD", false));
//COUNTRIES.add(new Country("American Samoa", "AS", "ASM", "USD", false));
//COUNTRIES.add(new Country("Andorra", "AD", "AND", "EUR", true));
//COUNTRIES.add(new Country("Angola", "AO", "AGO", "AOA", false));
//COUNTRIES.add(new Country("Anguilla", "AI", "AIA", "XCD", false));
//COUNTRIES.add(new Country("Antigua and Barbuda", "AG", "ATG", "XCD", false));
//COUNTRIES.add(new Country("Argentina", "AR", "ARG", "ARS", false));
//COUNTRIES.add(new Country("Armenia", "AM", "ARM", "AMD", false));
//COUNTRIES.add(new Country("Aruba", "AW", "ABW", "AWG", false));
//COUNTRIES.add(new Country("Australia", "AU", "AUS", "AUD", false));
//COUNTRIES.add(new Country("Austria", "AT", "AUT", "EUR", true));
//COUNTRIES.add(new Country("Azerbaijan", "AZ", "AZE", "AZN", false));
//COUNTRIES.add(new Country("Bahamas", "BS", "BHS", "BSD", false));
//COUNTRIES.add(new Country("Bahrain", "BH", "BHR", "BHD", false));
//COUNTRIES.add(new Country("Bangladesh", "BD", "BGD", "BDT", false));
//COUNTRIES.add(new Country("Barbados", "BB", "BRB", "BBD", false));
//COUNTRIES.add(new Country("Belarus", "BY", "BLR", "BYR", true));
//COUNTRIES.add(new Country("Belgium", "BE", "BEL", "EUR", true));
//COUNTRIES.add(new Country("Belize", "BZ", "BLZ", "BZD", false));
//COUNTRIES.add(new Country("Benin", "BJ", "BEN", "XOF", false));
//COUNTRIES.add(new Country("Bermuda", "BM", "BMU", "BMD", false));
//COUNTRIES.add(new Country("Bhutan", "BT", "BTN", "INR", false));
//COUNTRIES.add(new Country("Bolivia, Plurinational State of", "BO", "BOL", "BOB", false));
//COUNTRIES.add(new Country("Bonaire, Sint Eustatius and Saba", "BQ", "BES", "USD", false));
//COUNTRIES.add(new Country("Bosnia and Herzegovina", "BA", "BIH", "BAM", true));
//COUNTRIES.add(new Country("Botswana", "BW", "BWA", "BWP", false));
//COUNTRIES.add(new Country("Bouvet Island", "BV", "BVT", "NOK", false));
//COUNTRIES.add(new Country("Brazil", "BR", "BRA", "BRL", false));
//COUNTRIES.add(new Country("British Indian Ocean Territory", "IO", "IOT", "USD", false));
//COUNTRIES.add(new Country("Brunei Darussalam", "BN", "BRN", "BND", false));
//COUNTRIES.add(new Country("Bulgaria", "BG", "BGR", "BGN", true));
//COUNTRIES.add(new Country("Burkina Faso", "BF", "BFA", "XOF", false));
//COUNTRIES.add(new Country("Burundi", "BI", "BDI", "BIF", false));
//COUNTRIES.add(new Country("Cambodia", "KH", "KHM", "KHR", false));
//COUNTRIES.add(new Country("Cameroon", "CM", "CMR", "XAF", false));
//COUNTRIES.add(new Country("Canada", "CA", "CAN", "CAD", false));
//COUNTRIES.add(new Country("Cape Verde", "CV", "CPV", "CVE", false));
//COUNTRIES.add(new Country("Cayman Islands", "KY", "CYM", "KYD", false));
//COUNTRIES.add(new Country("Central African Republic", "CF", "CAF", "XAF", false));
//COUNTRIES.add(new Country("Chad", "TD", "TCD", "XAF", false));
//COUNTRIES.add(new Country("Chile", "CL", "CHL", "CLP", false));
//COUNTRIES.add(new Country("China", "CN", "CHN", "CNY", false));
//COUNTRIES.add(new Country("Christmas Island", "CX", "CXR", "AUD", false));
//COUNTRIES.add(new Country("Cocos (Keeling) Islands", "CC", "CCK", "AUD", false));
//COUNTRIES.add(new Country("Colombia", "CO", "COL", "COP", false));
//COUNTRIES.add(new Country("Comoros", "KM", "COM", "KMF", false));
//COUNTRIES.add(new Country("Congo", "CG", "COG", "XAF", false));
//COUNTRIES.add(new Country("Congo, the Democratic Republic of the", "CD", "COD", "CDF", false));
//COUNTRIES.add(new Country("Cook Islands", "CK", "COK", "NZD", false));
//COUNTRIES.add(new Country("Costa Rica", "CR", "CRI", "CRC", false));
//COUNTRIES.add(new Country("Croatia", "HR", "HRV", "HRK", true));
//COUNTRIES.add(new Country("Cuba", "CU", "CUB", "CUP", false));
//COUNTRIES.add(new Country("CuraÃ§ao", "CW", "CUW", "ANG", false));
//COUNTRIES.add(new Country("Cyprus", "CY", "CYP", "EUR", false));
//COUNTRIES.add(new Country("Czech Republic", "CZ", "CZE", "CZK", true));
//COUNTRIES.add(new Country("CÃ´te d'Ivoire", "CI", "CIV", "XOF", false));
//COUNTRIES.add(new Country("Denmark", "DK", "DNK", "DKK", true));
//COUNTRIES.add(new Country("Djibouti", "DJ", "DJI", "DJF", false));
//COUNTRIES.add(new Country("Dominica", "DM", "DMA", "XCD", false));
//COUNTRIES.add(new Country("Dominican Republic", "DO", "DOM", "DOP", false));
//COUNTRIES.add(new Country("Ecuador", "EC", "ECU", "USD", false));
//COUNTRIES.add(new Country("Egypt", "EG", "EGY", "EGP", false));
//COUNTRIES.add(new Country("El Salvador", "SV", "SLV", "USD", false));
//COUNTRIES.add(new Country("Equatorial Guinea", "GQ", "GNQ", "XAF", false));
//COUNTRIES.add(new Country("Eritrea", "ER", "ERI", "ERN", false));
//COUNTRIES.add(new Country("Estonia", "EE", "EST", "EUR", true));
//COUNTRIES.add(new Country("Ethiopia", "ET", "ETH", "ETB", false));
//COUNTRIES.add(new Country("Falkland Islands (Malvinas)", "FK", "FLK", "FKP", false));
//COUNTRIES.add(new Country("Faroe Islands", "FO", "FRO", "DKK", true));
//COUNTRIES.add(new Country("Fiji", "FJ", "FJI", "FJD", false));
//COUNTRIES.add(new Country("Finland", "FI", "FIN", "EUR", true));
//COUNTRIES.add(new Country("France", "FR", "FRA", "EUR", true));
//COUNTRIES.add(new Country("French Guiana", "GF", "GUF", "EUR", false));
//COUNTRIES.add(new Country("French Polynesia", "PF", "PYF", "XPF", false));
//COUNTRIES.add(new Country("French Southern Territories", "TF", "ATF", "EUR", false));
//COUNTRIES.add(new Country("Gabon", "GA", "GAB", "XAF", false));
//COUNTRIES.add(new Country("Gambia", "GM", "GMB", "GMD", false));
//COUNTRIES.add(new Country("Georgia", "GE", "GEO", "GEL", false));
//COUNTRIES.add(new Country("Germany", "DE", "DEU", "EUR", true));
//COUNTRIES.add(new Country("Ghana", "GH", "GHA", "GHS", false));
//COUNTRIES.add(new Country("Gibraltar", "GI", "GIB", "GIP", true));
//COUNTRIES.add(new Country("Greece", "GR", "GRC", "EUR", true));
//COUNTRIES.add(new Country("Greenland", "GL", "GRL", "DKK", false));
//COUNTRIES.add(new Country("Grenada", "GD", "GRD", "XCD", false));
//COUNTRIES.add(new Country("Guadeloupe", "GP", "GLP", "EUR", false));
//COUNTRIES.add(new Country("Guam", "GU", "GUM", "USD", false));
//COUNTRIES.add(new Country("Guatemala", "GT", "GTM", "GTQ", false));
//COUNTRIES.add(new Country("Guernsey", "GG", "GGY", "GBP", true));
//COUNTRIES.add(new Country("Guinea", "GN", "GIN", "GNF", false));
//COUNTRIES.add(new Country("Guinea-Bissau", "GW", "GNB", "XOF", false));
//COUNTRIES.add(new Country("Guyana", "GY", "GUY", "GYD", false));
//COUNTRIES.add(new Country("Haiti", "HT", "HTI", "USD", false));
//COUNTRIES.add(new Country("Heard Island and McDonald Mcdonald Islands", "HM", "HMD", "AUD", false));
//COUNTRIES.add(new Country("Holy See (Vatican City State)", "VA", "VAT", "EUR", true));
//COUNTRIES.add(new Country("Honduras", "HN", "HND", "HNL", false));
//COUNTRIES.add(new Country("Hong Kong", "HK", "HKG", "HKD", false));
//COUNTRIES.add(new Country("Hungary", "HU", "HUN", "HUF", true));
//COUNTRIES.add(new Country("Iceland", "IS", "ISL", "ISK", true));
//COUNTRIES.add(new Country("India", "IN", "IND", "INR", false));
//COUNTRIES.add(new Country("Indonesia", "ID", "IDN", "IDR", false));
//COUNTRIES.add(new Country("Iran, Islamic Republic of", "IR", "IRN", "IRR", false));
//COUNTRIES.add(new Country("Iraq", "IQ", "IRQ", "IQD", false));
//COUNTRIES.add(new Country("Ireland", "IE", "IRL", "EUR", true));
//COUNTRIES.add(new Country("Isle of Man", "IM", "IMN", "GBP", true));
//COUNTRIES.add(new Country("Israel", "IL", "ISR", "ILS", false));
//COUNTRIES.add(new Country("Italy", "IT", "ITA", "EUR", true));
//COUNTRIES.add(new Country("Jamaica", "JM", "JAM", "JMD", false));
//COUNTRIES.add(new Country("Japan", "JP", "JPN", "JPY", false));
//COUNTRIES.add(new Country("Jersey", "JE", "JEY", "GBP", true));
//COUNTRIES.add(new Country("Jordan", "JO", "JOR", "JOD", false));
//COUNTRIES.add(new Country("Kazakhstan", "KZ", "KAZ", "KZT", false));
//COUNTRIES.add(new Country("Kenya", "KE", "KEN", "KES", false));
//COUNTRIES.add(new Country("Kiribati", "KI", "KIR", "AUD", false));
//COUNTRIES.add(new Country("Korea, Democratic People's Republic of", "KP", "PRK", "KPW", false));
//COUNTRIES.add(new Country("Korea, Republic of", "KR", "KOR", "KRW", false));
//COUNTRIES.add(new Country("Kuwait", "KW", "KWT", "KWD", false));
//COUNTRIES.add(new Country("Kyrgyzstan", "KG", "KGZ", "KGS", false));
//COUNTRIES.add(new Country("Lao People's Democratic Republic", "LA", "LAO", "LAK", false));
//COUNTRIES.add(new Country("Latvia", "LV", "LVA", "LVL", true));
//COUNTRIES.add(new Country("Lebanon", "LB", "LBN", "LBP", false));
//COUNTRIES.add(new Country("Lesotho", "LS", "LSO", "ZAR", false));
//COUNTRIES.add(new Country("Liberia", "LR", "LBR", "LRD", false));
//COUNTRIES.add(new Country("Libya", "LY", "LBY", "LYD", false));
//COUNTRIES.add(new Country("Liechtenstein", "LI", "LIE", "CHF", true));
//COUNTRIES.add(new Country("Lithuania", "LT", "LTU", "LTL", true));
//COUNTRIES.add(new Country("Luxembourg", "LU", "LUX", "EUR", true));
//COUNTRIES.add(new Country("Macao", "MO", "MAC", "MOP", false));
//COUNTRIES.add(new Country("Macedonia, the Former Yugoslav Republic of", "MK", "MKD", "MKD", true));
//COUNTRIES.add(new Country("Madagascar", "MG", "MDG", "MGA", false));
//COUNTRIES.add(new Country("Malawi", "MW", "MWI", "MWK", false));
//COUNTRIES.add(new Country("Malaysia", "MY", "MYS", "MYR", false));
//COUNTRIES.add(new Country("Maldives", "MV", "MDV", "MVR", false));
//COUNTRIES.add(new Country("Mali", "ML", "MLI", "XOF", false));
//COUNTRIES.add(new Country("Malta", "MT", "MLT", "EUR", true));
//COUNTRIES.add(new Country("Marshall Islands", "MH", "MHL", "USD", false));
//COUNTRIES.add(new Country("Martinique", "MQ", "MTQ", "EUR", false));
//COUNTRIES.add(new Country("Mauritania", "MR", "MRT", "MRO", false));
//COUNTRIES.add(new Country("Mauritius", "MU", "MUS", "MUR", false));
//COUNTRIES.add(new Country("Mayotte", "YT", "MYT", "EUR", false));
//COUNTRIES.add(new Country("Mexico", "MX", "MEX", "MXN", false));
//COUNTRIES.add(new Country("Micronesia, Federated States of", "FM", "FSM", "USD", false));
//COUNTRIES.add(new Country("Moldova, Republic of", "MD", "MDA", "MDL", true));
//COUNTRIES.add(new Country("Monaco", "MC", "MCO", "EUR", true));
//COUNTRIES.add(new Country("Mongolia", "MN", "MNG", "MNT", false));
//COUNTRIES.add(new Country("Montenegro", "ME", "MNE", "EUR", true));
//COUNTRIES.add(new Country("Montserrat", "MS", "MSR", "XCD", false));
//COUNTRIES.add(new Country("Morocco", "MA", "MAR", "MAD", false));
//COUNTRIES.add(new Country("Mozambique", "MZ", "MOZ", "MZN", false));
//COUNTRIES.add(new Country("Myanmar", "MM", "MMR", "MMK", false));
//COUNTRIES.add(new Country("Namibia", "NA", "NAM", "ZAR", false));
//COUNTRIES.add(new Country("Nauru", "NR", "NRU", "AUD", false));
//COUNTRIES.add(new Country("Nepal", "NP", "NPL", "NPR", false));
//COUNTRIES.add(new Country("Netherlands", "NL", "NLD", "EUR", true));
//COUNTRIES.add(new Country("New Caledonia", "NC", "NCL", "XPF", false));
//COUNTRIES.add(new Country("New Zealand", "NZ", "NZL", "NZD", false));
//COUNTRIES.add(new Country("Nicaragua", "NI", "NIC", "NIO", false));
//COUNTRIES.add(new Country("Niger", "NE", "NER", "XOF", false));
//COUNTRIES.add(new Country("Nigeria", "NG", "NGA", "NGN", false));
//COUNTRIES.add(new Country("Niue", "NU", "NIU", "NZD", false));
//COUNTRIES.add(new Country("Norfolk Island", "NF", "NFK", "AUD", false));
//COUNTRIES.add(new Country("Northern Mariana Islands", "MP", "MNP", "USD", false));
//COUNTRIES.add(new Country("Norway", "NO", "NOR", "NOK", true));
//COUNTRIES.add(new Country("Oman", "OM", "OMN", "OMR", false));
//COUNTRIES.add(new Country("Pakistan", "PK", "PAK", "PKR", false));
//COUNTRIES.add(new Country("Palau", "PW", "PLW", "USD", false));
//COUNTRIES.add(new Country("Panama", "PA", "PAN", "USD", false));
//COUNTRIES.add(new Country("Papua New Guinea", "PG", "PNG", "PGK", false));
//COUNTRIES.add(new Country("Paraguay", "PY", "PRY", "PYG", false));
//COUNTRIES.add(new Country("Peru", "PE", "PER", "PEN", false));
//COUNTRIES.add(new Country("Philippines", "PH", "PHL", "PHP", false));
//COUNTRIES.add(new Country("Pitcairn", "PN", "PCN", "NZD", false));
//COUNTRIES.add(new Country("Poland", "PL", "POL", "PLN", true));
//COUNTRIES.add(new Country("Portugal", "PT", "PRT", "EUR", true));
//COUNTRIES.add(new Country("Puerto Rico", "PR", "PRI", "USD", false));
//COUNTRIES.add(new Country("Qatar", "QA", "QAT", "QAR", false));
//COUNTRIES.add(new Country("Romania", "RO", "ROU", "RON", true));
//COUNTRIES.add(new Country("Russian Federation", "RU", "RUS", "RUB", true));
//COUNTRIES.add(new Country("Rwanda", "RW", "RWA", "RWF", false));
//COUNTRIES.add(new Country("RÃ©union", "RE", "REU", "EUR", false));
//COUNTRIES.add(new Country("Saint BarthÃ©lemy", "BL", "BLM", "EUR", false));
//COUNTRIES.add(new Country("Saint Helena, Ascension and Tristan da Cunha", "SH", "SHN", "SHP", false));
//COUNTRIES.add(new Country("Saint Kitts and Nevis", "KN", "KNA", "XCD", false));
//COUNTRIES.add(new Country("Saint Lucia", "LC", "LCA", "XCD", false));
//COUNTRIES.add(new Country("Saint Martin (French part)", "MF", "MAF", "EUR", false));
//COUNTRIES.add(new Country("Saint Pierre and Miquelon", "PM", "SPM", "EUR", false));
//COUNTRIES.add(new Country("Saint Vincent and the Grenadines", "VC", "VCT", "XCD", false));
//COUNTRIES.add(new Country("Samoa", "WS", "WSM", "WST", false));
//COUNTRIES.add(new Country("San Marino", "SM", "SMR", "EUR", true));
//COUNTRIES.add(new Country("Sao Tome and Principe", "ST", "STP", "STD", false));
//COUNTRIES.add(new Country("Saudi Arabia", "SA", "SAU", "SAR", false));
//COUNTRIES.add(new Country("Senegal", "SN", "SEN", "XOF", false));
//COUNTRIES.add(new Country("Serbia", "RS", "SRB", "RSD", true));
//COUNTRIES.add(new Country("Seychelles", "SC", "SYC", "SCR", false));
//COUNTRIES.add(new Country("Sierra Leone", "SL", "SLE", "SLL", false));
//COUNTRIES.add(new Country("Singapore", "SG", "SGP", "SGD", false));
//COUNTRIES.add(new Country("Sint Maarten (Dutch part)", "SX", "SXM", "ANG", false));
//COUNTRIES.add(new Country("Slovakia", "SK", "SVK", "EUR", true));
//COUNTRIES.add(new Country("Slovenia", "SI", "SVN", "EUR", true));
//COUNTRIES.add(new Country("Solomon Islands", "SB", "SLB", "SBD", false));
//COUNTRIES.add(new Country("Somalia", "SO", "SOM", "SOS", false));
//COUNTRIES.add(new Country("South Africa", "ZA", "ZAF", "ZAR", false));
//COUNTRIES.add(new Country("South Sudan", "SS", "SSD", "SSP", false));
//COUNTRIES.add(new Country("Spain", "ES", "ESP", "EUR", true));
//COUNTRIES.add(new Country("Sri Lanka", "LK", "LKA", "LKR", false));
//COUNTRIES.add(new Country("Sudan", "SD", "SDN", "SDG", false));
//COUNTRIES.add(new Country("Suriname", "SR", "SUR", "SRD", false));
//COUNTRIES.add(new Country("Svalbard and Jan Mayen", "SJ", "SJM", "NOK", true));
//COUNTRIES.add(new Country("Swaziland", "SZ", "SWZ", "SZL", false));
//COUNTRIES.add(new Country("Sweden", "SE", "SWE", "SEK", true));
//COUNTRIES.add(new Country("Switzerland", "CH", "CHE", "CHF", true));
//COUNTRIES.add(new Country("Syrian Arab Republic", "SY", "SYR", "SYP", false));
//COUNTRIES.add(new Country("Taiwan", "TW", "TWN", "TWD", false));
//COUNTRIES.add(new Country("Tajikistan", "TJ", "TJK", "TJS", false));
//COUNTRIES.add(new Country("Tanzania, United Republic of", "TZ", "TZA", "TZS", false));
//COUNTRIES.add(new Country("Thailand", "TH", "THA", "THB", false));
//COUNTRIES.add(new Country("Timor-Leste", "TL", "TLS", "USD", false));
//COUNTRIES.add(new Country("Togo", "TG", "TGO", "XOF", false));
//COUNTRIES.add(new Country("Tokelau", "TK", "TKL", "NZD", false));
//COUNTRIES.add(new Country("Tonga", "TO", "TON", "TOP", false));
//COUNTRIES.add(new Country("Trinidad and Tobago", "TT", "TTO", "TTD", false));
//COUNTRIES.add(new Country("Tunisia", "TN", "TUN", "TND", false));
//COUNTRIES.add(new Country("Turkey", "TR", "TUR", "TRY", false));
//COUNTRIES.add(new Country("Turkmenistan", "TM", "TKM", "TMT", false));
//COUNTRIES.add(new Country("Turks and Caicos Islands", "TC", "TCA", "USD", false));
//COUNTRIES.add(new Country("Tuvalu", "TV", "TUV", "AUD", false));
//COUNTRIES.add(new Country("Uganda", "UG", "UGA", "UGX", false));
//COUNTRIES.add(new Country("Ukraine", "UA", "UKR", "UAH", true));
//COUNTRIES.add(new Country("United Arab Emirates", "AE", "ARE", "AED", false));
//COUNTRIES.add(new Country("United Kingdom", "GB", "GBR", "GBP", true));
//COUNTRIES.add(new Country("United States", "US", "USA", "USD", false));
//COUNTRIES.add(new Country("United States Minor Outlying Islands", "UM", "UMI", "USD", false));
//COUNTRIES.add(new Country("Uruguay", "UY", "URY", "UYU", false));
//COUNTRIES.add(new Country("Uzbekistan", "UZ", "UZB", "UZS", false));
//COUNTRIES.add(new Country("Vanuatu", "VU", "VUT", "VUV", false));
//COUNTRIES.add(new Country("Venezuela, Bolivarian Republic of", "VE", "VEN", "VEF", false));
//COUNTRIES.add(new Country("Viet Nam", "VN", "VNM", "VND", false));
//COUNTRIES.add(new Country("Virgin Islands, British", "VG", "VGB", "USD", false));
//COUNTRIES.add(new Country("Virgin Islands, U.S.", "VI", "VIR", "USD", false));
//COUNTRIES.add(new Country("Wallis and Futuna", "WF", "WLF", "XPF", false));
//COUNTRIES.add(new Country("Western Sahara", "EH", "ESH", "MAD", false));
//COUNTRIES.add(new Country("Yemen", "YE", "YEM", "YER", false));
//COUNTRIES.add(new Country("Zambia", "ZM", "ZMB", "ZMW", false));
//COUNTRIES.add(new Country("Zimbabwe", "ZW", "ZWE", "ZWL", false));

// Quick sanity check to only add countries that have all the details we want
//for (Country c : COUNTRIES) {
//if (c.code2.trim().length() == 2 && c.code3.trim().length() == 3 && c.currencyCode.trim().length() > 0) {
//Country.COUNTRIES.add(c);
//}
//}
//}
