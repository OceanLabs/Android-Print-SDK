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

import java.util.HashMap;
import java.util.Locale;


///// Class Declaration /////

/*****************************************************
 *
 * This enum represents a country.
 *
 *****************************************************/
public enum Country
  {
    ALAND_ISLANDS                     ( "Åland Islands", "AX", "ALA", "EUR", true ),
    AFGHANISTAN                       ( "Afghanistan", "AF", "AFG", "AFN", false ),
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
    BURKINA_FASO                      ( "Burkina Faso", "BF", "BFA", "XOF", false ),
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
    CURACAO                           ( "Curaçao", "CW", "CUW", "ANG", false ),
    CYPRUS                            ( "Cyprus", "CY", "CYP", "EUR", false ),
    CZECH_REPUBLIC                    ( "Czech Republic", "CZ", "CZE", "CZK", true ),
    IVORY_COAST                       ( "Côte d'Ivoire", "CI", "CIV", "XOF", false ),
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
    REUNION                           ( "Réunion", "RE", "REU", "EUR", false ),
    SAINT_BARTS                       ( "Saint Barthélemy", "BL", "BLM", "EUR", false ),
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
  private static final String  LOG_TAG = "Country";


  ////////// Static Variable(s) //////////

  private static HashMap<String,Country>  sCodeCountryTable;


  ////////// Member Variable(s) //////////

  private String   mDisplayName;
  private String   mISO2Code;
  private String   mISO3Code;
  private String   mISO3CurrencyCode;
  private boolean  mIsInEurope;


  ////////// Static Initialiser(s) //////////

  static
    {
    sCodeCountryTable = new HashMap<>();

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
    if (isoCode != null && isoCode.equals("LG")){ isoCode = "ES"; }

    Country country =  sCodeCountryTable.get( isoCode );
    if(country == null) {
      return Country.USA;
    }
    return country;
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

    if (locale.getCountry().equals("LG"))
      {
        locale = new Locale(locale.getLanguage(), "ES");
      }

    Country country =  Country.getInstance( locale.getISO3Country() );
    if(country == null) {
      return Country.USA;
    }
    return country;
    }


  /*****************************************************
   *
   * Returns the country for the default locale.
   *
   *****************************************************/
  public static Country getInstance()
    {
    Country country =  getInstance( Locale.getDefault() );
    if(country == null) {
      return Country.USA;
    }
    return country;
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


  /*****************************************************
   *
   * Returns true if the both countries are both null,
   * or equal.
   *
   *****************************************************/
  static public boolean areBothNullOrEqual( Country country1, Country country2 )
    {
    return ( country1 == country2 );
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
    return ( mDisplayName );
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
    return ( mDisplayName );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
