package com.hamstechapp.datamodel;

import java.util.ArrayList;

public class HeaderInfo {

    private String name;
    private ArrayList<HomePageDatamodel> productList = new ArrayList<>();

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<HomePageDatamodel> getProductList() {
        return productList;
    }
    public void setProductList(ArrayList<HomePageDatamodel> productList) {
        this.productList = productList;
    }

}
