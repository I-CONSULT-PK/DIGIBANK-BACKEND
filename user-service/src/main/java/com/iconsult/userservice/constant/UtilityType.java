package com.iconsult.userservice.constant;

public enum UtilityType {

    GAS("https://drive.google.com/file/d/1dl635VllHCXuzDt89k6VDauGajCEcVG5/view?usp=sharing"),
    WATER("https://drive.google.com/file/d/1NLNcnMP5aBSzw6cEIwx8WZOzmRLHomUZ/view?usp=sharing"),
    ELECTRICITY("https://drive.google.com/file/d/1MVCtrzXrg2iQ_uUZrf_OtsCXHwcoxDNJ/view?usp=sharing"),
    PTCL("https://drive.google.com/file/d/1VbyDsbjWlO5cau16tUrHug5w4iyYUGud/view?usp=sharing"),
    INTERNET("https://drive.google.com/file/d/1P6fXa_dgkODkKeUKTYF3tHEVNhE88VST/view?usp=sharing"),
    CREDIT_CARD("https://drive.google.com/file/d/1CNGXJO4Ru1wwvK0vO4kpz04hD-DN9srF/view?usp=sharing");

    private final String iconUrl;

    UtilityType(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}