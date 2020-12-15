package com.hamstechapp.datamodel;

public class OnBoardingDataModel {
    String categoryId;
    String category_Title;
    String category_description;
    String cat_image_url;
    String categoryname;
    String status;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory_Title() {
        return category_Title;
    }

    public void setCategory_Title(String category_Title) {
        this.category_Title = category_Title;
    }

    public String getCategory_description() {
        return category_description;
    }

    public void setCategory_description(String category_description) {
        this.category_description = category_description;
    }

    public String getCat_image_url() {
        return cat_image_url;
    }

    public void setCat_image_url(String cat_image_url) {
        this.cat_image_url = cat_image_url;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
