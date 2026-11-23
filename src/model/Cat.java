package model;

/**
 * Parent of every playable cat. Each cat has a name, a breed, a coat color and a perk.
 * The perk methods have "no perk" defaults, and each child overrides only the one it needs.
 * The quiz engine can call them on any Cat without knowing which cat it is.
 */
public abstract class Cat {

    private final String name;
    private final String breed;
    private final int furRgb;

    protected Cat(String name, String breed, int furRgb) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Cat name cannot be empty");
        }
        if (breed == null || breed.trim().isEmpty()) {
            throw new IllegalArgumentException("Cat breed cannot be empty");
        }
        this.name = name.trim();
        this.breed = breed.trim();
        this.furRgb = furRgb;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    // ----- looks (colors as 0xRRGGBB numbers, so the model needs no graphics code) -----

    public int getFurRgb() {
        return furRgb;
    }

    /** Color of ears and paws. Same as the fur unless a cat overrides it. */
    public int getPointsRgb() {
        return furRgb;
    }

    public int getEyeRgb() {
        return 0x2B1B17;
    }

    // ----- perks -----

    /** Short text shown on the cat card. */
    public abstract String getPerkDescription();

    /** Extra seconds added to every question timer. */
    public int getBonusSeconds() {
        return 0;
    }

    /** Multiplier applied to the speed bonus points. 1.0 means no change. */
    public double getSpeedBonusMultiplier() {
        return 1.0;
    }

    /** How many questions the player may skip for free in one run. */
    public int getFreeSkips() {
        return 0;
    }

    @Override
    public String toString() {
        return name + " (" + breed + ")";
    }
}
