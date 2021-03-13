package com.cyrus.dabbawala;

import java.util.List;

public class SellerLab {

    private static List<Seller> sMSellers;

    public static void setMrSellers(List<Seller> mSellers) {
        SellerLab.sMSellers = mSellers;
    }

    public static List<Seller> getMrSellers() {
        return SellerLab.sMSellers;
    }

    public static Seller getMrSeller(String id)
    {
        for (Seller seller : sMSellers)
        {
            if(seller.getId().equals(id))
                return seller;
        }
        return null;
    }
}
