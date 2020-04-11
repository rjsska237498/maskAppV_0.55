package com.example.maskapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
    Double lat, lng, latitude, longitude;
    GoogleMap GMap;
    GpsTracker gpsTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        lat = intent.getExtras().getDouble("lat");
        lng = intent.getExtras().getDouble("lng");

        gpsTracker = new GpsTracker(this);

        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GMap = googleMap;
        GMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(lat, lng);
        GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        LatLng myLatLng = new LatLng(latitude, longitude);
        GMap.addMarker(new MarkerOptions().position(myLatLng)
                        .title("현재 위치"));

        for(int i=0; i<MaskVO.name.size(); i++){
            LatLng markLatLng = new LatLng(MaskVO.getlat(i), MaskVO.getlng(i));
            if(MaskVO.getremain(i).equals("100개 이상")){
                if(lat.equals(MaskVO.getlat(i))) {        // 목록에서 클릭한 판매처인 경우
                    GMap.addMarker(new MarkerOptions().position(markLatLng)
                            .title(MaskVO.getName(i))
                            .snippet("남은 수량 : " + MaskVO.getremain(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).showInfoWindow();
                }else{  // 목록에서 클릭한 판매처가 아닌경우
                    GMap.addMarker(new MarkerOptions().position(markLatLng)
                            .title(MaskVO.getName(i))
                            .snippet("남은 수량 : " + MaskVO.getremain(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }


            }else if(MaskVO.getremain(i).equals("30개 이상 ~ 100개 미만")){
                if(lat.equals(MaskVO.getlat(i))) {        // 목록에서 클릭한 판매처인 경우
                    GMap.addMarker(new MarkerOptions().position(markLatLng)
                            .title(MaskVO.getName(i))
                            .snippet("남은 수량 : " + MaskVO.getremain(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))).showInfoWindow();
                }else{  // 목록에서 클릭한 판매처가 아닌경우
                    GMap.addMarker(new MarkerOptions().position(markLatLng)
                            .title(MaskVO.getName(i))
                            .snippet("남은 수량 : " + MaskVO.getremain(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }


            }else if(MaskVO.getremain(i).equals("2개 이상 ~ 30개 미만")){
                if(lat.equals(MaskVO.getlat(i))) {        // 목록에서 클릭한 판매처인 경우
                    GMap.addMarker(new MarkerOptions().position(markLatLng)
                            .title(MaskVO.getName(i))
                            .snippet("남은 수량 : " + MaskVO.getremain(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                }else{  // 목록에서 클릭한 판매처가 아닌경우
                    GMap.addMarker(new MarkerOptions().position(markLatLng)
                            .title(MaskVO.getName(i))
                            .snippet("남은 수량 : " + MaskVO.getremain(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }


            }else{      /* 판매완료, 판매중지, 알수없음 */
                if(lat.equals(MaskVO.getlat(i))) {        // 목록에서 클릭한 판매처인 경우
                    GMap.addMarker(new MarkerOptions().position(markLatLng)
                            .title(MaskVO.getName(i))
                            .snippet("남은 수량 : " + MaskVO.getremain(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))).showInfoWindow();
                }else{  // 목록에서 클릭한 판매처가 아닌경우
                    GMap.addMarker(new MarkerOptions().position(markLatLng)
                            .title(MaskVO.getName(i))
                            .snippet("남은 수량 : " + MaskVO.getremain(i))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                }
            }
        }
    }


}