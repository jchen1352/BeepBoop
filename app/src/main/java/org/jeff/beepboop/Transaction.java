package org.jeff.beepboop;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

public class Transaction {
    public static final String BOUGHT = "Bought";
    public static final String SOLD = "Sold";
    public static final String PENDING = "Pending";
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({BOUGHT, SOLD, PENDING})
    public @interface Status {}

    public Date date;
    public int credits;
    public int cents;
    public @Status String status;

    public Transaction(Date date, int credits, int cents, @Status String status) {
        this.date = date;
        this.credits = credits;
        this.cents = cents;
        this.status = status;
    }
}
