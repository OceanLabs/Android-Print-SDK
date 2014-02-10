package co.oceanlabs.pssdk;

/**
 * Created by deonbotha on 09/02/2014.
 */
public enum ProductType {
    POSTCARD(2, "ps_postcard"),
    POLAROIDS(3, "polaroids"),
    MINI_POLAROIDS(4, "polaroids_mini"),
    SQUARES(5, "squares"),
    MINI_SQUARES(6, "squares_mini"),
    MAGNETS(7, "magnets");

    final int value;
    final String defaultTemplate;

    private ProductType(int value, String defaultTemplate) {
        this.value = value;
        this.defaultTemplate = defaultTemplate;
    }
}
