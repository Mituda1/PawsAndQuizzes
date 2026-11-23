package model;

/** Black cat. Perk: bigger speed bonus. */
public class MidnightCat extends Cat {

    public MidnightCat() {
        super("Midnight", "Black Cat", 0x4A3A36);
    }

    @Override
    public int getEyeRgb() {
        return 0xFFD97A;
    }

    @Override
    public String getPerkDescription() {
        return "1.5x speed bonus points";
    }

    @Override
    public double getSpeedBonusMultiplier() {
        return 1.5;
    }
}
