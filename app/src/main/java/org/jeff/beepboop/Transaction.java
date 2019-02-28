package org.jeff.beepboop;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

public class Transaction {
    public static final String BOUGHT = "Bought";
    public static final String SOLD = "Sold";
    public static final String PENDING = "Pending";

    public int credits;
    public int cash;
    public String status;
    public String listingId;

    public Transaction(int credits, int cash, String listingId, String status) {
        this.credits = credits;
        this.cash = cash;
        this.listingId = listingId;
        this.status = status;
    }
}
