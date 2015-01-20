package ly.kite.print;

/**
 * Created by deonbotha on 09/02/2014.
 */
public enum ProductType {
    POSTCARD(2, "ps_postcard", "Postcard"),
    POLAROIDS(3, "polaroids", "Polaroid Style"),
    MINI_POLAROIDS(4, "polaroids_mini", "Polaroid Style XS"),
    SQUARES(5, "squares", "Squares"),
    MINI_SQUARES(6, "squares_mini", "Petite Squares"),
    MAGNETS(7, "magnets", "Magnets"),
    SQUARE_STICKERS(8, "stickers_square","Square Stickers"),
    CIRCLE_STICKERS(9, "stickers_circle","Circle Stickers"),
    A3_POSTER(11, "greeting_cards","Circle Greeting Cards"),
    FRAMES(12, "frames","Frames"),
    FRAMES_2x2(13, "frames_2x2","2x2 Frame"),
    FRAMES_3x3(14, "frames_3x3","3x3 Frame"),
    FRAMES_4x4(15, "frames_4x4","4x4 Frame"),


    //New Frames

    FRAMES_20(12, "frames_20cm","Frame 20cm"),
    FRAMES_30(12, "frames_30cm","Frame 30cm"),
    FRAMES_50(12, "frames_50cm","Frame 50cm"),



    FRAMES_2x2_20(12, "frames_20cm_2x2","Frame 20cm (2x2)"),
    FRAMES_2x2_30(12, "frames_30cm_2x2","Frame 30cm (2x2)"),
    FRAMES_2x2_50(12, "frames_50cm_2x2","Frame 50cm (2x2)"),

    FRAMES_3x3_30(12, "frames_30cm_3x3","Frame 30cm (3x3)"),
    FRAMES_3x3_50(12, "frames_50cm_3x3","Frame 30cm (3x3)"),

    FRAMES_4x4_50(12, "frames_50cm_4x4","Frame 30cm (4x4)"),

    PHOTOS_4x6(16, "photos_4x6","Circle Greeting Cards"),
    A1_POSTER(17, "a1_poster","A1 Collage (54)"),
    A1_POSTER_35(18, "a1_poster_35","A1 Collage (54)"),
    A1_POSTER_54(19, "a1_poster_54","A1 Collage (54)"),
    A1_POSTER_70(20, "a1_poster_70","A1 Collage (70)"),
    A2_POSTER(21, "a2_poster","A1 Collage (54)"),
    A2_POSTER_35(22, "a2_poster_35","A2 Collage (35)"),
    A2_POSTER_54(13, "a2_poster_54","A2 Collage (54)"),
    A2_POSTER_70(34, "a2_poster_70","A2 Collage (70)");



    final int value;

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    final String defaultTemplate;
    final String productName; // TODO: don't do this in final public API.

    private ProductType(int value, String defaultTemplate, String productName) {
        this.value = value;
        this.defaultTemplate = defaultTemplate;
        this.productName = productName;
    }



    public static ProductType productTypeFromTemplate(String template) {
       if (template.equals(POSTCARD.defaultTemplate)) {
           return POSTCARD;
       } else if (template.equals(POLAROIDS.defaultTemplate)) {
           return POLAROIDS;
       } else if (template.equals(MINI_POLAROIDS.defaultTemplate)) {
           return MINI_POLAROIDS;
       } else if (template.equals(SQUARES.defaultTemplate)) {
           return SQUARES;
       } else if (template.equals(MINI_SQUARES.defaultTemplate)) {
           return MINI_SQUARES;
       } else if (template.equals(MAGNETS.defaultTemplate)) {
           return MAGNETS;
       }else if (template.equals(SQUARE_STICKERS.defaultTemplate)) {
           return SQUARE_STICKERS;
       }else if (template.equals(SQUARE_STICKERS.defaultTemplate)) {
           return SQUARE_STICKERS;
       }else if (template.equals(FRAMES_2x2.defaultTemplate)) {
           return FRAMES_2x2;
       }

        throw new IllegalArgumentException("Unrecognized template: " + template);
    }

    public String getProductName() {
        return productName;
    }
}
