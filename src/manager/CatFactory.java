package manager;

import java.util.Arrays;
import java.util.List;
import model.Cat;
import model.LunaCat;
import model.MangoCat;
import model.MidnightCat;

/**
 * factory pattern the one place that knows how to build cats
 * screens ask for cats here and never write "new MangoCat()" themselves
 */
public final class CatFactory {

    private CatFactory() { }

    /** all playable cats, in the order they appear on the Player Setup screen */
    public static List<Cat> createAll() {
        return Arrays.asList(new MangoCat(), new MidnightCat(), new LunaCat());
    }

    public static Cat create(String name) {
        if (name != null) {
            switch (name.trim().toLowerCase()) {
                case "mango":
                    return new MangoCat();
                case "midnight":
                    return new MidnightCat();
                case "luna":
                    return new LunaCat();
                default:
                    break;
            }
        }
        throw new IllegalArgumentException("Unknown cat: " + name);
    }
}
