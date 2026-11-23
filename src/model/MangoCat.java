package model;

/** Orange tabby. Perk: more time to answer. */
public class MangoCat extends Cat {

    public MangoCat() {
        super("Mango", "Orange Tabby", 0xF2A65A);
    }

    @Override
    public int getPointsRgb() {
        return 0xD98A3D;
    }

    @Override
    public String getPerkDescription() {
        return "+5 seconds answer time";
    }

    @Override
    public int getBonusSeconds() {
        return 5;
    }
}
