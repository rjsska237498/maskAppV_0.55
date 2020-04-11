package com.example.maskapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GMap extends FragmentActivity implements OnMapReadyCallback {
    Double lat, lng;
    GoogleMap GMap;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        lat = intent.getExtras().getDouble("lat");
        lng = intent.getExtras().getDouble("lng");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GMap = googleMap;
        GMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(lat, lng);
        GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

        /* 이름(name), 남은수량(remain_stat), 주소(addr), 입고시간(stock_at), 위도(lat), 경도(lng) */
        dbHelper = new DBHelper(this);
        sqLiteDb = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDb.rawQuery("SELECT * FROM MaskTBL;", null);
        cursor.moveToFirst();

        String name;
        String remain_stat;
        /*String addr;
        String stock_at;*/
        Double lat;
        Double lng;

        while (cursor.moveToNext()){
            name = cursor.getString(0);
            remain_stat = cursor.getString(1);
            /*addr = cursor.getString(2);
            stock_at = cursor.getString(3);*/
            lat = cursor.getDouble(4);
            lng = cursor.getDouble(5);

            LatLng markLatLng = new LatLng(lat, lng);
            if(remain_stat.equals("100개 이상")){
                GMap.addMarker(new MarkerOptions().position(markLatLng)
                        .title(name)
                        .snippet("남은 수량 : " + remain_stat)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }else if(remain_stat.equals("30개 이상 ~ 100개 미만")){
                GMap.addMarker(new MarkerOptions().position(markLatLng)
                        .title(name)
                        .snippet("남은 수량 : " + remain_stat)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            }else if(remain_stat.equals("2개 이상 ~ 30개 미만")){
                GMap.addMarker(new MarkerOptions().position(markLatLng)
                        .title(name)
                        .snippet("남은 수량 : " + remain_stat)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }else{      /* 판매완료, 판매중지, 알수없음 */
                GMap.addMarker(new MarkerOptions().position(markLatLng)
                        .title(name)
                        .snippet("남은 수량 : " + remain_stat)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            }

        }
    }
}