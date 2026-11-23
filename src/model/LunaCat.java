package model;

/** Siamese. Perk: one free skip per run. */
public class LunaCat extends Cat {

    public LunaCat() {
        super("Luna", "Siamese", 0xFFF4E0);
    }

    @Override
    public int getPointsRgb() {
        return 0x7A5A4A;
    }

    @Override
    public String getPerkDescription() {
        return "1 free question skip";
    }

    @Override
    public int getFreeSkips() {
        return 1;
    }
}
