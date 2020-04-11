package com.example.maskapp;

public class MyItem {

    private String name;        // 판매처 이름
    private String remain;      // 재고 수량
    private String address;     // 판매처 주소
    private String distance;    // 내 위치와 판매처 위치 거리
    private String stock;       // 입고시간

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


    public String getremain(){
        return remain;
    }

    public void setRemain(String remain){
        this.remain = remain;
    }


    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }


    public String getDistance(){
        return distance;
    }

    public void setDistance(String distance){
        this.distance = distance;
    }


    public String getStock(){
        return stock;
    }

    public void setStock(String stock){
        this.stock = stock;
    }

}
