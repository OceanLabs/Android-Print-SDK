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
        COUNTRIES.add(new Country("Åland Islands", "AX", "ALA", "EUR"));
        COUNTRIES.add(new Country("Afghanistan", "AF", "AFG", "AFN"));
        COUNTRIES.add(new Country("Albania", "AL", "ALB", "ALL"));
        COUNTRIES.add(new Country("Algeria", "DZ", "DZA", "DZD"));
        COUNTRIES.add(new Country("American Samoa", "AS", "ASM", "USD"));
        COUNTRIES.add(new Country("Andorra", "AD", "AND", "EUR"));
        COUNTRIES.add(new Country("Angola", "AO", "AGO", "AOA"));
        COUNTRIES.add(new Country("Anguilla", "AI", "AIA", "XCD"));
        COUNTRIES.add(new Country("Antarctica", "AQ", "ATA", ""));
        COUNTRIES.add(new Country("Antigua and Barbuda", "AG", "ATG", "XCD"));
        COUNTRIES.add(new Country("Argentina", "AR", "ARG", "ARS"));
        COUNTRIES.add(new Country("Armenia", "AM", "ARM", "AMD"));
        COUNTRIES.add(new Country("Aruba", "AW", "ABW", "AWG"));
        COUNTRIES.add(new Country("Australia", "AU", "AUS", "AUD"));
        COUNTRIES.add(new Country("Austria", "AT", "AUT", "EUR"));
        COUNTRIES.add(new Country("Azerbaijan", "AZ", "AZE", "AZN"));
        COUNTRIES.add(new Country("Bahamas", "BS", "BHS", "BSD"));
        COUNTRIES.add(new Country("Bahrain", "BH", "BHR", "BHD"));
        COUNTRIES.add(new Country("Bangladesh", "BD", "BGD", "BDT"));
        COUNTRIES.add(new Country("Barbados", "BB", "BRB", "BBD"));
        COUNTRIES.add(new Country("Belarus", "BY", "BLR", "BYR"));
        COUNTRIES.add(new Country("Belgium", "BE", "BEL", "EUR"));
        COUNTRIES.add(new Country("Belize", "BZ", "BLZ", "BZD"));
        COUNTRIES.add(new Country("Benin", "BJ", "BEN", "XOF"));
        COUNTRIES.add(new Country("Bermuda", "BM", "BMU", "BMD"));
        COUNTRIES.add(new Country("Bhutan", "BT", "BTN", "INR"));
        COUNTRIES.add(new Country("Bolivia, Plurinational State of", "BO", "BOL", "BOB"));
        COUNTRIES.add(new Country("Bonaire, Sint Eustatius and Saba", "BQ", "BES", "USD"));
        COUNTRIES.add(new Country("Bosnia and Herzegovina", "BA", "BIH", "BAM"));
        COUNTRIES.add(new Country("Botswana", "BW", "BWA", "BWP"));
        COUNTRIES.add(new Country("Bouvet Island", "BV", "BVT", "NOK"));
        COUNTRIES.add(new Country("Brazil", "BR", "BRA", "BRL"));
        COUNTRIES.add(new Country("British Indian Ocean Territory", "IO", "IOT", "USD"));
        COUNTRIES.add(new Country("Brunei Darussalam", "BN", "BRN", "BND"));
        COUNTRIES.add(new Country("Bulgaria", "BG", "BGR", "BGN"));
        COUNTRIES.add(new Country("Burkina Faso", "BF", "BFA", "XOF"));
        COUNTRIES.add(new Country("Burundi", "BI", "BDI", "BIF"));
        COUNTRIES.add(new Country("Cambodia", "KH", "KHM", "KHR"));
        COUNTRIES.add(new Country("Cameroon", "CM", "CMR", "XAF"));
        COUNTRIES.add(new Country("Canada", "CA", "CAN", "CAD"));
        COUNTRIES.add(new Country("Cape Verde", "CV", "CPV", "CVE"));
        COUNTRIES.add(new Country("Cayman Islands", "KY", "CYM", "KYD"));
        COUNTRIES.add(new Country("Central African Republic", "CF", "CAF", "XAF"));
        COUNTRIES.add(new Country("Chad", "TD", "TCD", "XAF"));
        COUNTRIES.add(new Country("Chile", "CL", "CHL", "CLP"));
        COUNTRIES.add(new Country("China", "CN", "CHN", "CNY"));
        COUNTRIES.add(new Country("Christmas Island", "CX", "CXR", "AUD"));
        COUNTRIES.add(new Country("Cocos (Keeling) Islands", "CC", "CCK", "AUD"));
        COUNTRIES.add(new Country("Colombia", "CO", "COL", "COP"));
        COUNTRIES.add(new Country("Comoros", "KM", "COM", "KMF"));
        COUNTRIES.add(new Country("Congo", "CG", "COG", "XAF"));
        COUNTRIES.add(new Country("Congo, the Democratic Republic of the", "CD", "COD", "CDF"));
        COUNTRIES.add(new Country("Cook Islands", "CK", "COK", "NZD"));
        COUNTRIES.add(new Country("Costa Rica", "CR", "CRI", "CRC"));
        COUNTRIES.add(new Country("Croatia", "HR", "HRV", "HRK"));
        COUNTRIES.add(new Country("Cuba", "CU", "CUB", "CUP"));
        COUNTRIES.add(new Country("Curaçao", "CW", "CUW", "ANG"));
        COUNTRIES.add(new Country("Cyprus", "CY", "CYP", "EUR"));
        COUNTRIES.add(new Country("Czech Republic", "CZ", "CZE", "CZK"));
        COUNTRIES.add(new Country("Côte d'Ivoire", "CI", "CIV", "XOF"));
        COUNTRIES.add(new Country("Denmark", "DK", "DNK", "DKK"));
        COUNTRIES.add(new Country("Djibouti", "DJ", "DJI", "DJF"));
        COUNTRIES.add(new Country("Dominica", "DM", "DMA", "XCD"));
        COUNTRIES.add(new Country("Dominican Republic", "DO", "DOM", "DOP"));
        COUNTRIES.add(new Country("Ecuador", "EC", "ECU", "USD"));
        COUNTRIES.add(new Country("Egypt", "EG", "EGY", "EGP"));
        COUNTRIES.add(new Country("El Salvador", "SV", "SLV", "USD"));
        COUNTRIES.add(new Country("Equatorial Guinea", "GQ", "GNQ", "XAF"));
        COUNTRIES.add(new Country("Eritrea", "ER", "ERI", "ERN"));
        COUNTRIES.add(new Country("Estonia", "EE", "EST", "EUR"));
        COUNTRIES.add(new Country("Ethiopia", "ET", "ETH", "ETB"));
        COUNTRIES.add(new Country("Falkland Islands (Malvinas)", "FK", "FLK", "FKP"));
        COUNTRIES.add(new Country("Faroe Islands", "FO", "FRO", "DKK"));
        COUNTRIES.add(new Country("Fiji", "FJ", "FJI", "FJD"));
        COUNTRIES.add(new Country("Finland", "FI", "FIN", "EUR"));
        COUNTRIES.add(new Country("France", "FR", "FRA", "EUR"));
        COUNTRIES.add(new Country("French Guiana", "GF", "GUF", "EUR"));
        COUNTRIES.add(new Country("French Polynesia", "PF", "PYF", "XPF"));
        COUNTRIES.add(new Country("French Southern Territories", "TF", "ATF", "EUR"));
        COUNTRIES.add(new Country("Gabon", "GA", "GAB", "XAF"));
        COUNTRIES.add(new Country("Gambia", "GM", "GMB", "GMD"));
        COUNTRIES.add(new Country("Georgia", "GE", "GEO", "GEL"));
        COUNTRIES.add(new Country("Germany", "DE", "DEU", "EUR"));
        COUNTRIES.add(new Country("Ghana", "GH", "GHA", "GHS"));
        COUNTRIES.add(new Country("Gibraltar", "GI", "GIB", "GIP"));
        COUNTRIES.add(new Country("Greece", "GR", "GRC", "EUR"));
        COUNTRIES.add(new Country("Greenland", "GL", "GRL", "DKK"));
        COUNTRIES.add(new Country("Grenada", "GD", "GRD", "XCD"));
        COUNTRIES.add(new Country("Guadeloupe", "GP", "GLP", "EUR"));
        COUNTRIES.add(new Country("Guam", "GU", "GUM", "USD"));
        COUNTRIES.add(new Country("Guatemala", "GT", "GTM", "GTQ"));
        COUNTRIES.add(new Country("Guernsey", "GG", "GGY", "GBP"));
        COUNTRIES.add(new Country("Guinea", "GN", "GIN", "GNF"));
        COUNTRIES.add(new Country("Guinea-Bissau", "GW", "GNB", "XOF"));
        COUNTRIES.add(new Country("Guyana", "GY", "GUY", "GYD"));
        COUNTRIES.add(new Country("Haiti", "HT", "HTI", "USD"));
        COUNTRIES.add(new Country("Heard Island and McDonald Mcdonald Islands", "HM", "HMD", "AUD"));
        COUNTRIES.add(new Country("Holy See (Vatican City State)", "VA", "VAT", "EUR"));
        COUNTRIES.add(new Country("Honduras", "HN", "HND", "HNL"));
        COUNTRIES.add(new Country("Hong Kong", "HK", "HKG", "HKD"));
        COUNTRIES.add(new Country("Hungary", "HU", "HUN", "HUF"));
        COUNTRIES.add(new Country("Iceland", "IS", "ISL", "ISK"));
        COUNTRIES.add(new Country("India", "IN", "IND", "INR"));
        COUNTRIES.add(new Country("Indonesia", "ID", "IDN", "IDR"));
        COUNTRIES.add(new Country("Iran, Islamic Republic of", "IR", "IRN", "IRR"));
        COUNTRIES.add(new Country("Iraq", "IQ", "IRQ", "IQD"));
        COUNTRIES.add(new Country("Ireland", "IE", "IRL", "EUR"));
        COUNTRIES.add(new Country("Isle of Man", "IM", "IMN", "GBP"));
        COUNTRIES.add(new Country("Israel", "IL", "ISR", "ILS"));
        COUNTRIES.add(new Country("Italy", "IT", "ITA", "EUR"));
        COUNTRIES.add(new Country("Jamaica", "JM", "JAM", "JMD"));
        COUNTRIES.add(new Country("Japan", "JP", "JPN", "JPY"));
        COUNTRIES.add(new Country("Jersey", "JE", "JEY", "GBP"));
        COUNTRIES.add(new Country("Jordan", "JO", "JOR", "JOD"));
        COUNTRIES.add(new Country("Kazakhstan", "KZ", "KAZ", "KZT"));
        COUNTRIES.add(new Country("Kenya", "KE", "KEN", "KES"));
        COUNTRIES.add(new Country("Kiribati", "KI", "KIR", "AUD"));
        COUNTRIES.add(new Country("Korea, Democratic People's Republic of", "KP", "PRK", "KPW"));
        COUNTRIES.add(new Country("Korea, Republic of", "KR", "KOR", "KRW"));
        COUNTRIES.add(new Country("Kuwait", "KW", "KWT", "KWD"));
        COUNTRIES.add(new Country("Kyrgyzstan", "KG", "KGZ", "KGS"));
        COUNTRIES.add(new Country("Lao People's Democratic Republic", "LA", "LAO", "LAK"));
        COUNTRIES.add(new Country("Latvia", "LV", "LVA", "LVL"));
        COUNTRIES.add(new Country("Lebanon", "LB", "LBN", "LBP"));
        COUNTRIES.add(new Country("Lesotho", "LS", "LSO", "ZAR"));
        COUNTRIES.add(new Country("Liberia", "LR", "LBR", "LRD"));
        COUNTRIES.add(new Country("Libya", "LY", "LBY", "LYD"));
        COUNTRIES.add(new Country("Liechtenstein", "LI", "LIE", "CHF"));
        COUNTRIES.add(new Country("Lithuania", "LT", "LTU", "LTL"));
        COUNTRIES.add(new Country("Luxembourg", "LU", "LUX", "EUR"));
        COUNTRIES.add(new Country("Macao", "MO", "MAC", "MOP"));
        COUNTRIES.add(new Country("Macedonia, the Former Yugoslav Republic of", "MK", "MKD", "MKD"));
        COUNTRIES.add(new Country("Madagascar", "MG", "MDG", "MGA"));
        COUNTRIES.add(new Country("Malawi", "MW", "MWI", "MWK"));
        COUNTRIES.add(new Country("Malaysia", "MY", "MYS", "MYR"));
        COUNTRIES.add(new Country("Maldives", "MV", "MDV", "MVR"));
        COUNTRIES.add(new Country("Mali", "ML", "MLI", "XOF"));
        COUNTRIES.add(new Country("Malta", "MT", "MLT", "EUR"));
        COUNTRIES.add(new Country("Marshall Islands", "MH", "MHL", "USD"));
        COUNTRIES.add(new Country("Martinique", "MQ", "MTQ", "EUR"));
        COUNTRIES.add(new Country("Mauritania", "MR", "MRT", "MRO"));
        COUNTRIES.add(new Country("Mauritius", "MU", "MUS", "MUR"));
        COUNTRIES.add(new Country("Mayotte", "YT", "MYT", "EUR"));
        COUNTRIES.add(new Country("Mexico", "MX", "MEX", "MXN"));
        COUNTRIES.add(new Country("Micronesia, Federated States of", "FM", "FSM", "USD"));
        COUNTRIES.add(new Country("Moldova, Republic of", "MD", "MDA", "MDL"));
        COUNTRIES.add(new Country("Monaco", "MC", "MCO", "EUR"));
        COUNTRIES.add(new Country("Mongolia", "MN", "MNG", "MNT"));
        COUNTRIES.add(new Country("Montenegro", "ME", "MNE", "EUR"));
        COUNTRIES.add(new Country("Montserrat", "MS", "MSR", "XCD"));
        COUNTRIES.add(new Country("Morocco", "MA", "MAR", "MAD"));
        COUNTRIES.add(new Country("Mozambique", "MZ", "MOZ", "MZN"));
        COUNTRIES.add(new Country("Myanmar", "MM", "MMR", "MMK"));
        COUNTRIES.add(new Country("Namibia", "NA", "NAM", "ZAR"));
        COUNTRIES.add(new Country("Nauru", "NR", "NRU", "AUD"));
        COUNTRIES.add(new Country("Nepal", "NP", "NPL", "NPR"));
        COUNTRIES.add(new Country("Netherlands", "NL", "NLD", "EUR"));
        COUNTRIES.add(new Country("New Caledonia", "NC", "NCL", "XPF"));
        COUNTRIES.add(new Country("New Zealand", "NZ", "NZL", "NZD"));
        COUNTRIES.add(new Country("Nicaragua", "NI", "NIC", "NIO"));
        COUNTRIES.add(new Country("Niger", "NE", "NER", "XOF"));
        COUNTRIES.add(new Country("Nigeria", "NG", "NGA", "NGN"));
        COUNTRIES.add(new Country("Niue", "NU", "NIU", "NZD"));
        COUNTRIES.add(new Country("Norfolk Island", "NF", "NFK", "AUD"));
        COUNTRIES.add(new Country("Northern Mariana Islands", "MP", "MNP", "USD"));
        COUNTRIES.add(new Country("Norway", "NO", "NOR", "NOK"));
        COUNTRIES.add(new Country("Oman", "OM", "OMN", "OMR"));
        COUNTRIES.add(new Country("Pakistan", "PK", "PAK", "PKR"));
        COUNTRIES.add(new Country("Palau", "PW", "PLW", "USD"));
        COUNTRIES.add(new Country("Palestine, State of", "PS", "PSE", ""));
        COUNTRIES.add(new Country("Panama", "PA", "PAN", "USD"));
        COUNTRIES.add(new Country("Papua New Guinea", "PG", "PNG", "PGK"));
        COUNTRIES.add(new Country("Paraguay", "PY", "PRY", "PYG"));
        COUNTRIES.add(new Country("Peru", "PE", "PER", "PEN"));
        COUNTRIES.add(new Country("Philippines", "PH", "PHL", "PHP"));
        COUNTRIES.add(new Country("Pitcairn", "PN", "PCN", "NZD"));
        COUNTRIES.add(new Country("Poland", "PL", "POL", "PLN"));
        COUNTRIES.add(new Country("Portugal", "PT", "PRT", "EUR"));
        COUNTRIES.add(new Country("Puerto Rico", "PR", "PRI", "USD"));
        COUNTRIES.add(new Country("Qatar", "QA", "QAT", "QAR"));
        COUNTRIES.add(new Country("Romania", "RO", "ROU", "RON"));
        COUNTRIES.add(new Country("Russian Federation", "RU", "RUS", "RUB"));
        COUNTRIES.add(new Country("Rwanda", "RW", "RWA", "RWF"));
        COUNTRIES.add(new Country("Réunion", "RE", "REU", "EUR"));
        COUNTRIES.add(new Country("Saint Barthélemy", "BL", "BLM", "EUR"));
        COUNTRIES.add(new Country("Saint Helena, Ascension and Tristan da Cunha", "SH", "SHN", "SHP"));
        COUNTRIES.add(new Country("Saint Kitts and Nevis", "KN", "KNA", "XCD"));
        COUNTRIES.add(new Country("Saint Lucia", "LC", "LCA", "XCD"));
        COUNTRIES.add(new Country("Saint Martin (French part)", "MF", "MAF", "EUR"));
        COUNTRIES.add(new Country("Saint Pierre and Miquelon", "PM", "SPM", "EUR"));
        COUNTRIES.add(new Country("Saint Vincent and the Grenadines", "VC", "VCT", "XCD"));
        COUNTRIES.add(new Country("Samoa", "WS", "WSM", "WST"));
        COUNTRIES.add(new Country("San Marino", "SM", "SMR", "EUR"));
        COUNTRIES.add(new Country("Sao Tome and Principe", "ST", "STP", "STD"));
        COUNTRIES.add(new Country("Saudi Arabia", "SA", "SAU", "SAR"));
        COUNTRIES.add(new Country("Senegal", "SN", "SEN", "XOF"));
        COUNTRIES.add(new Country("Serbia", "RS", "SRB", "RSD"));
        COUNTRIES.add(new Country("Seychelles", "SC", "SYC", "SCR"));
        COUNTRIES.add(new Country("Sierra Leone", "SL", "SLE", "SLL"));
        COUNTRIES.add(new Country("Singapore", "SG", "SGP", "SGD"));
        COUNTRIES.add(new Country("Sint Maarten (Dutch part)", "SX", "SXM", "ANG"));
        COUNTRIES.add(new Country("Slovakia", "SK", "SVK", "EUR"));
        COUNTRIES.add(new Country("Slovenia", "SI", "SVN", "EUR"));
        COUNTRIES.add(new Country("Solomon Islands", "SB", "SLB", "SBD"));
        COUNTRIES.add(new Country("Somalia", "SO", "SOM", "SOS"));
        COUNTRIES.add(new Country("South Africa", "ZA", "ZAF", "ZAR"));
        COUNTRIES.add(new Country("South Georgia and the South Sandwich Islands", "GS", "SGS", ""));
        COUNTRIES.add(new Country("South Sudan", "SS", "SSD", "SSP"));
        COUNTRIES.add(new Country("Spain", "ES", "ESP", "EUR"));
        COUNTRIES.add(new Country("Sri Lanka", "LK", "LKA", "LKR"));
        COUNTRIES.add(new Country("Sudan", "SD", "SDN", "SDG"));
        COUNTRIES.add(new Country("Suriname", "SR", "SUR", "SRD"));
        COUNTRIES.add(new Country("Svalbard and Jan Mayen", "SJ", "SJM", "NOK"));
        COUNTRIES.add(new Country("Swaziland", "SZ", "SWZ", "SZL"));
        COUNTRIES.add(new Country("Sweden", "SE", "SWE", "SEK"));
        COUNTRIES.add(new Country("Switzerland", "CH", "CHE", "CHF"));
        COUNTRIES.add(new Country("Syrian Arab Republic", "SY", "SYR", "SYP"));
        COUNTRIES.add(new Country("Taiwan, Province of China", "TW", "TWN", "TWD"));
        COUNTRIES.add(new Country("Tajikistan", "TJ", "TJK", "TJS"));
        COUNTRIES.add(new Country("Tanzania, United Republic of", "TZ", "TZA", "TZS"));
        COUNTRIES.add(new Country("Thailand", "TH", "THA", "THB"));
        COUNTRIES.add(new Country("Timor-Leste", "TL", "TLS", "USD"));
        COUNTRIES.add(new Country("Togo", "TG", "TGO", "XOF"));
        COUNTRIES.add(new Country("Tokelau", "TK", "TKL", "NZD"));
        COUNTRIES.add(new Country("Tonga", "TO", "TON", "TOP"));
        COUNTRIES.add(new Country("Trinidad and Tobago", "TT", "TTO", "TTD"));
        COUNTRIES.add(new Country("Tunisia", "TN", "TUN", "TND"));
        COUNTRIES.add(new Country("Turkey", "TR", "TUR", "TRY"));
        COUNTRIES.add(new Country("Turkmenistan", "TM", "TKM", "TMT"));
        COUNTRIES.add(new Country("Turks and Caicos Islands", "TC", "TCA", "USD"));
        COUNTRIES.add(new Country("Tuvalu", "TV", "TUV", "AUD"));
        COUNTRIES.add(new Country("Uganda", "UG", "UGA", "UGX"));
        COUNTRIES.add(new Country("Ukraine", "UA", "UKR", "UAH"));
        COUNTRIES.add(new Country("United Arab Emirates", "AE", "ARE", "AED"));
        COUNTRIES.add(new Country("United Kingdom", "GB", "GBR", "GBP"));
        COUNTRIES.add(new Country("United States", "US", "USA", "USD"));
        COUNTRIES.add(new Country("United States Minor Outlying Islands", "UM", "UMI", "USD"));
        COUNTRIES.add(new Country("Uruguay", "UY", "URY", "UYU"));
        COUNTRIES.add(new Country("Uzbekistan", "UZ", "UZB", "UZS"));
        COUNTRIES.add(new Country("Vanuatu", "VU", "VUT", "VUV"));
        COUNTRIES.add(new Country("Venezuela, Bolivarian Republic of", "VE", "VEN", "VEF"));
        COUNTRIES.add(new Country("Viet Nam", "VN", "VNM", "VND"));
        COUNTRIES.add(new Country("Virgin Islands, British", "VG", "VGB", "USD"));
        COUNTRIES.add(new Country("Virgin Islands, U.S.", "VI", "VIR", "USD"));
        COUNTRIES.add(new Country("Wallis and Futuna", "WF", "WLF", "XPF"));
        COUNTRIES.add(new Country("Western Sahara", "EH", "ESH", "MAD"));
        COUNTRIES.add(new Country("Yemen", "YE", "YEM", "YER"));
        COUNTRIES.add(new Country("Zambia", "ZM", "ZMB", "ZMW"));
        COUNTRIES.add(new Country("Zimbabwe", "ZW", "ZWE", "ZWL"));

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

    public Country(String name, String code2, String code3, String currencyCode) {
        this.name = name;
        this.code2 = code2;
        this.code3 = code3;
        this.currencyCode = currencyCode;
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
