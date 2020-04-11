package com.example.maskapp;

import java.util.ArrayList;

public class MaskVO {

    static ArrayList<String> name = new ArrayList<String>();
    static ArrayList<String> remain = new ArrayList<String>();
    static ArrayList<String> address = new ArrayList<String>();
    static ArrayList<String> distance = new ArrayList<String>();
    static ArrayList<String> stock = new ArrayList<String>();
    static ArrayList<Double> lat = new ArrayList<Double>();
    static ArrayList<Double> lng = new ArrayList<Double>();

    public MaskVO(String name, String remain, String address, String distance, String stock, Double lat, Double lng){
        this.name.add(name);
        this.remain.add(remain);
        this.address.add(address);
        this.distance.add(distance);
        this.stock.add(stock);
        this.lat.add(lat);
        this.lng.add(lng);
    }

    public static String getName(int position){
        return name.get(position);
    }

    public static String getremain(int position){
        return remain.get(position);
    }

    public static String getAddress(int position){
        return address.get(position);
    }

    public static String getDistance(int position){
        return distance.get(position);
    }

    public static String getStock(int position){
        return stock.get(position);
    }

    public static Double getlat(int position){
        return lat.get(position);
    }

    public static Double getlng(int position){
        return lng.get(position);
    }

}
