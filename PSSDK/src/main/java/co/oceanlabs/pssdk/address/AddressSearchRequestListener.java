package co.oceanlabs.pssdk.address;

import java.util.List;

/**
 * Created by deonbotha on 29/01/2014.
 */
public interface AddressSearchRequestListener {
    void onMultipleChoices(List<Address> options);
    void onUniqueAddress(Address address);
    void onError(Exception error);
}
