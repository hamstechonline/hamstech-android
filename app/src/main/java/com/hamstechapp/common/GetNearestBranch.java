package com.hamstechapp.common;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.hamstechapp.utils.UserDataConstants;

import java.util.ArrayList;
import java.util.List;

public class GetNearestBranch {
    Context context;
    double branchLats[] = {17.409634,17.436826,17.438766,17.450836,17.490768,17.442345,17.370879};
    double branchLongs[] = {78.485407,78.453033,78.409774,78.489081,78.389206,78.369605,78.543669};
    String[] branchNames = {"HIMAYATNAGAR - 3RD FLOOR, OM TOWERS, " +
            "OPPOSITE MCDONALD’S,\n" +
            "HIMAYATNAGAR MAIN ROAD\n" +
            "HYDERABAD-29 INDIA\n" +
            "PHONE:+91-7416066555\n" +
            "EMAIL: info@hamstech.com","Punjagutta - II, III & IV Floor," +
            " R.K. Plaza, Punjagutta ‘X’ Road, Punjagutta,\n" +
            "Hyderabad – 82 India\n" +
            "PHONE:+91-40-66684994, +91-40-66684995\n" +
            "EMAIL: info@hamstech.com","Jubilee Hills - Plot No 472, 2nd Floor," +
            " SAI Galleria,Above Nissan Showroom,\n" +
            "Road No 36, Jubilee Hills,\n" + "Hyderabad – 500033 India\n" + "PHONE:+91-7207057291\n" +
            "EMAIL: info@hamstech.com","Secunderabad - II Floor," +
            " Jade Arcade, Paradise ‘X’ Road,\n" +
            "M.G. Road, Secunderabad – 03 India\n" +
            "PHONE:+91-40-66484997, +91-40-66484998\n" +
            "EMAIL: info@hamstech.com","Kukatpally - 3rd and 4th floor, above Neerus, \n" +
            "Forum Mall circle JNTU road, \n" + "Kukatpally Hyderabad – 72 India \n" +
            "PHONE:+91-04023155963 EMAIL: info@hamstech.com",
            "Gachibowli - 4th Floor, Vamsiram builders, Jyothi Imperial\n" + "Above South India Mall,\n" +
            "Gachibowli Main Road, Gachibowli\n" + "Hyderabad – 32 India\n" +
            "PHONE:+91-7207599222\n" + "EMAIL: info@hamstech.com",
            "Kothapet - 4th Floor, Above More Mega Store,\n" +
            "Beside Astalakshmi Temple Arch,\n" +
            "Kothapet.\n" +
            "Hyderabad – 35 India\n" +
            "PHONE:+91 40  2403 4994\n" +
            "EMAIL: info@hamstech.com"};
    List<Double> resultDistance = new ArrayList<>();
    String branchNameResult;

    public GetNearestBranch(Context context){
        this.context = context;
    }

    public void start(){
        resultDistance.clear();
        for (int i = 0; i<branchLats.length; i++){
            resultDistance.add(CalculationByDistance(i));
            if (i == branchLats.length-1){
                getBranchDetails();
            }
        }
    }

    public double CalculationByDistance(int position) {
        LatLng from = new LatLng(UserDataConstants.getLatitude,UserDataConstants.getLongitude);
        LatLng to = new LatLng(branchLats[position],branchLongs[position]);
        Double resultDes = SphericalUtil.computeDistanceBetween(from, to);
        resultDes = (resultDes/100);
        Log.i("resultDes", "" + resultDes);
        return resultDes;
    }
    public String getBranchDetails(){
        double min = resultDistance.get(0);
        //Loop through the array
        for (int i = 0; i < resultDistance.size(); i++) {
            //Compare elements of array with min
            if(resultDistance.get(i) <min) {
                min = resultDistance.get(i);
                branchNameResult = branchNames[i];
                UserDataConstants.branchName = branchNameResult;
                UserDataConstants.resultLat = branchLats[i];
                UserDataConstants.resultLong = branchLongs[i];
                Log.i("branchNameResult", "" + branchNameResult);
            }
        }
        return branchNameResult;
    }
    public void ifDeniedLocationAccess(){
        branchNameResult = branchNames[1];
        UserDataConstants.branchName = branchNameResult;
        UserDataConstants.resultLat = branchLats[1];
        UserDataConstants.resultLong = branchLongs[1];
    }
}
