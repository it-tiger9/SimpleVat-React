package com.simplevat.entity.invoice;

/**
 *
 * @author Hiren
 */
public enum DiscountType {

    ABSOLUTE("Absolute Discount"),
    PERCENTAGE("Percentage Discount");

    final private String desc;

    DiscountType(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }

}