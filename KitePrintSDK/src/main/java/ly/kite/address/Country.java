package ly.kite.address;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by deonbotha on 29/01/2014.
 */
public class Country {

public static List<Country> COUNTRIES = new ArrayList<Country>();
static {
List<Country> COUNTRIES = new ArrayList<Country>();
COUNTRIES.add(new Country("Ã…land Islands", "AX", "ALA", "EUR", true));
COUNTRIES.add(new Country("Afghanistan", "AF", "AFG", "AFN", false));
COUNTRIES.add(new Country("Albania", "AL", "ALB", "ALL", true));
COUNTRIES.add(new Country("Algeria", "DZ", "DZA", "DZD", false));
COUNTRIES.add(new Country("American Samoa", "AS", "ASM", "USD", false));
COUNTRIES.add(new Country("Andorra", "AD", "AND", "EUR", true));
COUNTRIES.add(new Country("Angola", "AO", "AGO", "AOA", false));
COUNTRIES.add(new Country("Anguilla", "AI", "AIA", "XCD", false));
COUNTRIES.add(new Country("Antigua and Barbuda", "AG", "ATG", "XCD", false));
COUNTRIES.add(new Country("Argentina", "AR", "ARG", "ARS", false));
COUNTRIES.add(new Country("Armenia", "AM", "ARM", "AMD", false));
COUNTRIES.add(new Country("Aruba", "AW", "ABW", "AWG", false));
COUNTRIES.add(new Country("Australia", "AU", "AUS", "AUD", false));
COUNTRIES.add(new Country("Austria", "AT", "AUT", "EUR", true));
COUNTRIES.add(new Country("Azerbaijan", "AZ", "AZE", "AZN", false));
COUNTRIES.add(new Country("Bahamas", "BS", "BHS", "BSD", false));
COUNTRIES.add(new Country("Bahrain", "BH", "BHR", "BHD", false));
COUNTRIES.add(new Country("Bangladesh", "BD", "BGD", "BDT", false));
COUNTRIES.add(new Country("Barbados", "BB", "BRB", "BBD", false));
COUNTRIES.add(new Country("Belarus", "BY", "BLR", "BYR", true));
COUNTRIES.add(new Country("Belgium", "BE", "BEL", "EUR", true));
COUNTRIES.add(new Country("Belize", "BZ", "BLZ", "BZD", false));
COUNTRIES.add(new Country("Benin", "BJ", "BEN", "XOF", false));
COUNTRIES.add(new Country("Bermuda", "BM", "BMU", "BMD", false));
COUNTRIES.add(new Country("Bhutan", "BT", "BTN", "INR", false));
COUNTRIES.add(new Country("Bolivia, Plurinational State of", "BO", "BOL", "BOB", false));
COUNTRIES.add(new Country("Bonaire, Sint Eustatius and Saba", "BQ", "BES", "USD", false));
COUNTRIES.add(new Country("Bosnia and Herzegovina", "BA", "BIH", "BAM", true));
COUNTRIES.add(new Country("Botswana", "BW", "BWA", "BWP", false));
COUNTRIES.add(new Country("Bouvet Island", "BV", "BVT", "NOK", false));
COUNTRIES.add(new Country("Brazil", "BR", "BRA", "BRL", false));
COUNTRIES.add(new Country("British Indian Ocean Territory", "IO", "IOT", "USD", false));
COUNTRIES.add(new Country("Brunei Darussalam", "BN", "BRN", "BND", false));
COUNTRIES.add(new Country("Bulgaria", "BG", "BGR", "BGN", true));
COUNTRIES.add(new Country("Burkina Faso", "BF", "BFA", "XOF", false));
COUNTRIES.add(new Country("Burundi", "BI", "BDI", "BIF", false));
COUNTRIES.add(new Country("Cambodia", "KH", "KHM", "KHR", false));
COUNTRIES.add(new Country("Cameroon", "CM", "CMR", "XAF", false));
COUNTRIES.add(new Country("Canada", "CA", "CAN", "CAD", false));
COUNTRIES.add(new Country("Cape Verde", "CV", "CPV", "CVE", false));
COUNTRIES.add(new Country("Cayman Islands", "KY", "CYM", "KYD", false));
COUNTRIES.add(new Country("Central African Republic", "CF", "CAF", "XAF", false));
COUNTRIES.add(new Country("Chad", "TD", "TCD", "XAF", false));
COUNTRIES.add(new Country("Chile", "CL", "CHL", "CLP", false));
COUNTRIES.add(new Country("China", "CN", "CHN", "CNY", false));
COUNTRIES.add(new Country("Christmas Island", "CX", "CXR", "AUD", false));
COUNTRIES.add(new Country("Cocos (Keeling) Islands", "CC", "CCK", "AUD", false));
COUNTRIES.add(new Country("Colombia", "CO", "COL", "COP", false));
COUNTRIES.add(new Country("Comoros", "KM", "COM", "KMF", false));
COUNTRIES.add(new Country("Congo", "CG", "COG", "XAF", false));
COUNTRIES.add(new Country("Congo, the Democratic Republic of the", "CD", "COD", "CDF", false));
COUNTRIES.add(new Country("Cook Islands", "CK", "COK", "NZD", false));
COUNTRIES.add(new Country("Costa Rica", "CR", "CRI", "CRC", false));
COUNTRIES.add(new Country("Croatia", "HR", "HRV", "HRK", true));
COUNTRIES.add(new Country("Cuba", "CU", "CUB", "CUP", false));
COUNTRIES.add(new Country("CuraÃ§ao", "CW", "CUW", "ANG", false));
COUNTRIES.add(new Country("Cyprus", "CY", "CYP", "EUR", false));
COUNTRIES.add(new Country("Czech Republic", "CZ", "CZE", "CZK", true));
COUNTRIES.add(new Country("CÃ´te d'Ivoire", "CI", "CIV", "XOF", false));
COUNTRIES.add(new Country("Denmark", "DK", "DNK", "DKK", true));
COUNTRIES.add(new Country("Djibouti", "DJ", "DJI", "DJF", false));
COUNTRIES.add(new Country("Dominica", "DM", "DMA", "XCD", false));
COUNTRIES.add(new Country("Dominican Republic", "DO", "DOM", "DOP", false));
COUNTRIES.add(new Country("Ecuador", "EC", "ECU", "USD", false));
COUNTRIES.add(new Country("Egypt", "EG", "EGY", "EGP", false));
COUNTRIES.add(new Country("El Salvador", "SV", "SLV", "USD", false));
COUNTRIES.add(new Country("Equatorial Guinea", "GQ", "GNQ", "XAF", false));
COUNTRIES.add(new Country("Eritrea", "ER", "ERI", "ERN", false));
COUNTRIES.add(new Country("Estonia", "EE", "EST", "EUR", true));
COUNTRIES.add(new Country("Ethiopia", "ET", "ETH", "ETB", false));
COUNTRIES.add(new Country("Falkland Islands (Malvinas)", "FK", "FLK", "FKP", false));
COUNTRIES.add(new Country("Faroe Islands", "FO", "FRO", "DKK", true));
COUNTRIES.add(new Country("Fiji", "FJ", "FJI", "FJD", false));
COUNTRIES.add(new Country("Finland", "FI", "FIN", "EUR", true));
COUNTRIES.add(new Country("France", "FR", "FRA", "EUR", true));
COUNTRIES.add(new Country("French Guiana", "GF", "GUF", "EUR", false));
COUNTRIES.add(new Country("French Polynesia", "PF", "PYF", "XPF", false));
COUNTRIES.add(new Country("French Southern Territories", "TF", "ATF", "EUR", false));
COUNTRIES.add(new Country("Gabon", "GA", "GAB", "XAF", false));
COUNTRIES.add(new Country("Gambia", "GM", "GMB", "GMD", false));
COUNTRIES.add(new Country("Georgia", "GE", "GEO", "GEL", false));
COUNTRIES.add(new Country("Germany", "DE", "DEU", "EUR", true));
COUNTRIES.add(new Country("Ghana", "GH", "GHA", "GHS", false));
COUNTRIES.add(new Country("Gibraltar", "GI", "GIB", "GIP", true));
COUNTRIES.add(new Country("Greece", "GR", "GRC", "EUR", true));
COUNTRIES.add(new Country("Greenland", "GL", "GRL", "DKK", false));
COUNTRIES.add(new Country("Grenada", "GD", "GRD", "XCD", false));
COUNTRIES.add(new Country("Guadeloupe", "GP", "GLP", "EUR", false));
COUNTRIES.add(new Country("Guam", "GU", "GUM", "USD", false));
COUNTRIES.add(new Country("Guatemala", "GT", "GTM", "GTQ", false));
COUNTRIES.add(new Country("Guernsey", "GG", "GGY", "GBP", true));
COUNTRIES.add(new Country("Guinea", "GN", "GIN", "GNF", false));
COUNTRIES.add(new Country("Guinea-Bissau", "GW", "GNB", "XOF", false));
COUNTRIES.add(new Country("Guyana", "GY", "GUY", "GYD", false));
COUNTRIES.add(new Country("Haiti", "HT", "HTI", "USD", false));
COUNTRIES.add(new Country("Heard Island and McDonald Mcdonald Islands", "HM", "HMD", "AUD", false));
COUNTRIES.add(new Country("Holy See (Vatican City State)", "VA", "VAT", "EUR", true));
COUNTRIES.add(new Country("Honduras", "HN", "HND", "HNL", false));
COUNTRIES.add(new Country("Hong Kong", "HK", "HKG", "HKD", false));
COUNTRIES.add(new Country("Hungary", "HU", "HUN", "HUF", true));
COUNTRIES.add(new Country("Iceland", "IS", "ISL", "ISK", true));
COUNTRIES.add(new Country("India", "IN", "IND", "INR", false));
COUNTRIES.add(new Country("Indonesia", "ID", "IDN", "IDR", false));
COUNTRIES.add(new Country("Iran, Islamic Republic of", "IR", "IRN", "IRR", false));
COUNTRIES.add(new Country("Iraq", "IQ", "IRQ", "IQD", false));
COUNTRIES.add(new Country("Ireland", "IE", "IRL", "EUR", true));
COUNTRIES.add(new Country("Isle of Man", "IM", "IMN", "GBP", true));
COUNTRIES.add(new Country("Israel", "IL", "ISR", "ILS", false));
COUNTRIES.add(new Country("Italy", "IT", "ITA", "EUR", true));
COUNTRIES.add(new Country("Jamaica", "JM", "JAM", "JMD", false));
COUNTRIES.add(new Country("Japan", "JP", "JPN", "JPY", false));
COUNTRIES.add(new Country("Jersey", "JE", "JEY", "GBP", true));
COUNTRIES.add(new Country("Jordan", "JO", "JOR", "JOD", false));
COUNTRIES.add(new Country("Kazakhstan", "KZ", "KAZ", "KZT", false));
COUNTRIES.add(new Country("Kenya", "KE", "KEN", "KES", false));
COUNTRIES.add(new Country("Kiribati", "KI", "KIR", "AUD", false));
COUNTRIES.add(new Country("Korea, Democratic People's Republic of", "KP", "PRK", "KPW", false));
COUNTRIES.add(new Country("Korea, Republic of", "KR", "KOR", "KRW", false));
COUNTRIES.add(new Country("Kuwait", "KW", "KWT", "KWD", false));
COUNTRIES.add(new Country("Kyrgyzstan", "KG", "KGZ", "KGS", false));
COUNTRIES.add(new Country("Lao People's Democratic Republic", "LA", "LAO", "LAK", false));
COUNTRIES.add(new Country("Latvia", "LV", "LVA", "LVL", true));
COUNTRIES.add(new Country("Lebanon", "LB", "LBN", "LBP", false));
COUNTRIES.add(new Country("Lesotho", "LS", "LSO", "ZAR", false));
COUNTRIES.add(new Country("Liberia", "LR", "LBR", "LRD", false));
COUNTRIES.add(new Country("Libya", "LY", "LBY", "LYD", false));
COUNTRIES.add(new Country("Liechtenstein", "LI", "LIE", "CHF", true));
COUNTRIES.add(new Country("Lithuania", "LT", "LTU", "LTL", true));
COUNTRIES.add(new Country("Luxembourg", "LU", "LUX", "EUR", true));
COUNTRIES.add(new Country("Macao", "MO", "MAC", "MOP", false));
COUNTRIES.add(new Country("Macedonia, the Former Yugoslav Republic of", "MK", "MKD", "MKD", true));
COUNTRIES.add(new Country("Madagascar", "MG", "MDG", "MGA", false));
COUNTRIES.add(new Country("Malawi", "MW", "MWI", "MWK", false));
COUNTRIES.add(new Country("Malaysia", "MY", "MYS", "MYR", false));
COUNTRIES.add(new Country("Maldives", "MV", "MDV", "MVR", false));
COUNTRIES.add(new Country("Mali", "ML", "MLI", "XOF", false));
COUNTRIES.add(new Country("Malta", "MT", "MLT", "EUR", true));
COUNTRIES.add(new Country("Marshall Islands", "MH", "MHL", "USD", false));
COUNTRIES.add(new Country("Martinique", "MQ", "MTQ", "EUR", false));
COUNTRIES.add(new Country("Mauritania", "MR", "MRT", "MRO", false));
COUNTRIES.add(new Country("Mauritius", "MU", "MUS", "MUR", false));
COUNTRIES.add(new Country("Mayotte", "YT", "MYT", "EUR", false));
COUNTRIES.add(new Country("Mexico", "MX", "MEX", "MXN", false));
COUNTRIES.add(new Country("Micronesia, Federated States of", "FM", "FSM", "USD", false));
COUNTRIES.add(new Country("Moldova, Republic of", "MD", "MDA", "MDL", true));
COUNTRIES.add(new Country("Monaco", "MC", "MCO", "EUR", true));
COUNTRIES.add(new Country("Mongolia", "MN", "MNG", "MNT", false));
COUNTRIES.add(new Country("Montenegro", "ME", "MNE", "EUR", true));
COUNTRIES.add(new Country("Montserrat", "MS", "MSR", "XCD", false));
COUNTRIES.add(new Country("Morocco", "MA", "MAR", "MAD", false));
COUNTRIES.add(new Country("Mozambique", "MZ", "MOZ", "MZN", false));
COUNTRIES.add(new Country("Myanmar", "MM", "MMR", "MMK", false));
COUNTRIES.add(new Country("Namibia", "NA", "NAM", "ZAR", false));
COUNTRIES.add(new Country("Nauru", "NR", "NRU", "AUD", false));
COUNTRIES.add(new Country("Nepal", "NP", "NPL", "NPR", false));
COUNTRIES.add(new Country("Netherlands", "NL", "NLD", "EUR", true));
COUNTRIES.add(new Country("New Caledonia", "NC", "NCL", "XPF", false));
COUNTRIES.add(new Country("New Zealand", "NZ", "NZL", "NZD", false));
COUNTRIES.add(new Country("Nicaragua", "NI", "NIC", "NIO", false));
COUNTRIES.add(new Country("Niger", "NE", "NER", "XOF", false));
COUNTRIES.add(new Country("Nigeria", "NG", "NGA", "NGN", false));
COUNTRIES.add(new Country("Niue", "NU", "NIU", "NZD", false));
COUNTRIES.add(new Country("Norfolk Island", "NF", "NFK", "AUD", false));
COUNTRIES.add(new Country("Northern Mariana Islands", "MP", "MNP", "USD", false));
COUNTRIES.add(new Country("Norway", "NO", "NOR", "NOK", true));
COUNTRIES.add(new Country("Oman", "OM", "OMN", "OMR", false));
COUNTRIES.add(new Country("Pakistan", "PK", "PAK", "PKR", false));
COUNTRIES.add(new Country("Palau", "PW", "PLW", "USD", false));
COUNTRIES.add(new Country("Panama", "PA", "PAN", "USD", false));
COUNTRIES.add(new Country("Papua New Guinea", "PG", "PNG", "PGK", false));
COUNTRIES.add(new Country("Paraguay", "PY", "PRY", "PYG", false));
COUNTRIES.add(new Country("Peru", "PE", "PER", "PEN", false));
COUNTRIES.add(new Country("Philippines", "PH", "PHL", "PHP", false));
COUNTRIES.add(new Country("Pitcairn", "PN", "PCN", "NZD", false));
COUNTRIES.add(new Country("Poland", "PL", "POL", "PLN", true));
COUNTRIES.add(new Country("Portugal", "PT", "PRT", "EUR", true));
COUNTRIES.add(new Country("Puerto Rico", "PR", "PRI", "USD", false));
COUNTRIES.add(new Country("Qatar", "QA", "QAT", "QAR", false));
COUNTRIES.add(new Country("Romania", "RO", "ROU", "RON", true));
COUNTRIES.add(new Country("Russian Federation", "RU", "RUS", "RUB", true));
COUNTRIES.add(new Country("Rwanda", "RW", "RWA", "RWF", false));
COUNTRIES.add(new Country("RÃ©union", "RE", "REU", "EUR", false));
COUNTRIES.add(new Country("Saint BarthÃ©lemy", "BL", "BLM", "EUR", false));
COUNTRIES.add(new Country("Saint Helena, Ascension and Tristan da Cunha", "SH", "SHN", "SHP", false));
COUNTRIES.add(new Country("Saint Kitts and Nevis", "KN", "KNA", "XCD", false));
COUNTRIES.add(new Country("Saint Lucia", "LC", "LCA", "XCD", false));
COUNTRIES.add(new Country("Saint Martin (French part)", "MF", "MAF", "EUR", false));
COUNTRIES.add(new Country("Saint Pierre and Miquelon", "PM", "SPM", "EUR", false));
COUNTRIES.add(new Country("Saint Vincent and the Grenadines", "VC", "VCT", "XCD", false));
COUNTRIES.add(new Country("Samoa", "WS", "WSM", "WST", false));
COUNTRIES.add(new Country("San Marino", "SM", "SMR", "EUR", true));
COUNTRIES.add(new Country("Sao Tome and Principe", "ST", "STP", "STD", false));
COUNTRIES.add(new Country("Saudi Arabia", "SA", "SAU", "SAR", false));
COUNTRIES.add(new Country("Senegal", "SN", "SEN", "XOF", false));
COUNTRIES.add(new Country("Serbia", "RS", "SRB", "RSD", true));
COUNTRIES.add(new Country("Seychelles", "SC", "SYC", "SCR", false));
COUNTRIES.add(new Country("Sierra Leone", "SL", "SLE", "SLL", false));
COUNTRIES.add(new Country("Singapore", "SG", "SGP", "SGD", false));
COUNTRIES.add(new Country("Sint Maarten (Dutch part)", "SX", "SXM", "ANG", false));
COUNTRIES.add(new Country("Slovakia", "SK", "SVK", "EUR", true));
COUNTRIES.add(new Country("Slovenia", "SI", "SVN", "EUR", true));
COUNTRIES.add(new Country("Solomon Islands", "SB", "SLB", "SBD", false));
COUNTRIES.add(new Country("Somalia", "SO", "SOM", "SOS", false));
COUNTRIES.add(new Country("South Africa", "ZA", "ZAF", "ZAR", false));
COUNTRIES.add(new Country("South Sudan", "SS", "SSD", "SSP", false));
COUNTRIES.add(new Country("Spain", "ES", "ESP", "EUR", true));
COUNTRIES.add(new Country("Sri Lanka", "LK", "LKA", "LKR", false));
COUNTRIES.add(new Country("Sudan", "SD", "SDN", "SDG", false));
COUNTRIES.add(new Country("Suriname", "SR", "SUR", "SRD", false));
COUNTRIES.add(new Country("Svalbard and Jan Mayen", "SJ", "SJM", "NOK", true));
COUNTRIES.add(new Country("Swaziland", "SZ", "SWZ", "SZL", false));
COUNTRIES.add(new Country("Sweden", "SE", "SWE", "SEK", true));
COUNTRIES.add(new Country("Switzerland", "CH", "CHE", "CHF", true));
COUNTRIES.add(new Country("Syrian Arab Republic", "SY", "SYR", "SYP", false));
COUNTRIES.add(new Country("Taiwan", "TW", "TWN", "TWD", false));
COUNTRIES.add(new Country("Tajikistan", "TJ", "TJK", "TJS", false));
COUNTRIES.add(new Country("Tanzania, United Republic of", "TZ", "TZA", "TZS", false));
COUNTRIES.add(new Country("Thailand", "TH", "THA", "THB", false));
COUNTRIES.add(new Country("Timor-Leste", "TL", "TLS", "USD", false));
COUNTRIES.add(new Country("Togo", "TG", "TGO", "XOF", false));
COUNTRIES.add(new Country("Tokelau", "TK", "TKL", "NZD", false));
COUNTRIES.add(new Country("Tonga", "TO", "TON", "TOP", false));
COUNTRIES.add(new Country("Trinidad and Tobago", "TT", "TTO", "TTD", false));
COUNTRIES.add(new Country("Tunisia", "TN", "TUN", "TND", false));
COUNTRIES.add(new Country("Turkey", "TR", "TUR", "TRY", false));
COUNTRIES.add(new Country("Turkmenistan", "TM", "TKM", "TMT", false));
COUNTRIES.add(new Country("Turks and Caicos Islands", "TC", "TCA", "USD", false));
COUNTRIES.add(new Country("Tuvalu", "TV", "TUV", "AUD", false));
COUNTRIES.add(new Country("Uganda", "UG", "UGA", "UGX", false));
COUNTRIES.add(new Country("Ukraine", "UA", "UKR", "UAH", true));
COUNTRIES.add(new Country("United Arab Emirates", "AE", "ARE", "AED", false));
COUNTRIES.add(new Country("United Kingdom", "GB", "GBR", "GBP", true));
COUNTRIES.add(new Country("United States", "US", "USA", "USD", false));
COUNTRIES.add(new Country("United States Minor Outlying Islands", "UM", "UMI", "USD", false));
COUNTRIES.add(new Country("Uruguay", "UY", "URY", "UYU", false));
COUNTRIES.add(new Country("Uzbekistan", "UZ", "UZB", "UZS", false));
COUNTRIES.add(new Country("Vanuatu", "VU", "VUT", "VUV", false));
COUNTRIES.add(new Country("Venezuela, Bolivarian Republic of", "VE", "VEN", "VEF", false));
COUNTRIES.add(new Country("Viet Nam", "VN", "VNM", "VND", false));
COUNTRIES.add(new Country("Virgin Islands, British", "VG", "VGB", "USD", false));
COUNTRIES.add(new Country("Virgin Islands, U.S.", "VI", "VIR", "USD", false));
COUNTRIES.add(new Country("Wallis and Futuna", "WF", "WLF", "XPF", false));
COUNTRIES.add(new Country("Western Sahara", "EH", "ESH", "MAD", false));
COUNTRIES.add(new Country("Yemen", "YE", "YEM", "YER", false));
COUNTRIES.add(new Country("Zambia", "ZM", "ZMB", "ZMW", false));
COUNTRIES.add(new Country("Zimbabwe", "ZW", "ZWE", "ZWL", false));

// Quick sanity check to only add countries that have all the details we want
for (Country c : COUNTRIES) {
if (c.code2.trim().length() == 2 && c.code3.trim().length() == 3 && c.currencyCode.trim().length() > 0) {
Country.COUNTRIES.add(c);
}
}
}

public static Country getInstance(String code) {
for (Country c : COUNTRIES) {
if (c.code2.equals(code) || c.code3.equals(code)) {
return c;
}
}

return null;
}

public static Country getInstance(Locale locale) {
if (locale == null) {
throw new IllegalArgumentException("Please provide a non null locale");
}
return Country.getInstance(locale.getISO3Country());
}

private final String name;
private final String code2;
private final String code3;
private final String currencyCode;
private final boolean inEurope;

public Country(String name, String code2, String code3, String currencyCode, boolean inEurope) {
this.name = name;
this.code2 = code2;
this.code3 = code3;
this.currencyCode = currencyCode;
this.inEurope = inEurope;
}

public String getCodeAlpha2() {
return this.code2;
}

public String getCodeAlpha3() {
return this.code3;
}

public String getName() {
return name;
}

public String getCurrencyCode() {
return currencyCode;
}

public boolean isInEurope() {
return inEurope;
}

@Override
public String toString() {
return name;
}

@Override
public boolean equals(Object o) {
if (!(o instanceof  Country)) {
return false;
}

Country c = (Country) o;
return this.name.equals(c.name) && this.code2.equals(c.code2) && this.code3.equals(c.code3) && this.currencyCode.equals(c.currencyCode);
}

@Override
public int hashCode() {
return this.code3.hashCode();
}
}