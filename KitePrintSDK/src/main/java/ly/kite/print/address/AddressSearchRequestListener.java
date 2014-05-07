package ly.kite.print.address;

import java.util.List;

/**
 * Created by deonbotha on 29/01/2014.
 */
public interface AddressSearchRequestListener {
    void onMultipleChoices(AddressSearchRequest req, List<Address> options);
    void onUniqueAddress(AddressSearchRequest req, Address address);
    void onError(AddressSearchRequest req, Exception error);
}
