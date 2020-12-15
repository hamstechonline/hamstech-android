package com.hamstechapp.datamodel;

public class AffiliationDataModel {
    String affiliationImage;

    public String getAffiliationTitle() {
        return affiliationTitle;
    }

    public void setAffiliationTitle(String affiliationTitle) {
        this.affiliationTitle = affiliationTitle;
    }

    String affiliationTitle;

    public String getAffiliationImage() {
        return affiliationImage;
    }

    public void setAffiliationImage(String affiliationImage) {
        this.affiliationImage = affiliationImage;
    }

    public String getAffiliationDescription() {
        return affiliationDescription;
    }

    public void setAffiliationDescription(String affiliationDescription) {
        this.affiliationDescription = affiliationDescription;
    }

    String affiliationDescription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String date;
}
