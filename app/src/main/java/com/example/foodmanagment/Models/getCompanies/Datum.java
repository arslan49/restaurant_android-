
package com.example.foodmanagment.Models.getCompanies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("distance")
    @Expose
    private Integer distance;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

}
