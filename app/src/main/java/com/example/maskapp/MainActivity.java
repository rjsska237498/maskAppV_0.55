package com.example.maskapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView gpsText;
    Button btnSearch, btnRefresh;
    GpsTracker gpsTracker;
    Double latitude, longitude;
    DBHelper dbHelper;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSION_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsText = findViewById(R.id.gpsText);
        btnSearch = findViewById(R.id.btnSearch);
        btnRefresh = findViewById(R.id.btnRefresh);


        //myDBHelper 인스턴스를 생성.(myDBHelper클래스의 myDBHelper생성자가 실행되어 gruoupDB 파일이 생성된다)
        dbHelper = new DBHelper(this);



        if(!checkLocationServicesStatus()){
            showDialogForLocationServiceSetting();
        }else{
            checkRunTimePermission();
        }


        /* 내 위치정보 조회 기능 */
        gpsTracker = new GpsTracker(MainActivity.this);

        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        gpsText.setText(address);


        /* 검색 버튼 클릭 이벤트 */
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(permsRequestCode == PERMISSION_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length){
            /* 요청 코드가 PERMISSION_REQUEST_CODE이고, 요청한 퍼미션 개수만큼 수신되었다면 */
            boolean check_result = true;

            /* 모든 권한을 허용했는지 체크합니다. */
            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if(check_result){
                /* 위치 값을 가져올 수 있음 */
            }else{
                /* 거부한 권한이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다. 2가지 경우가 있습니다. */
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])){
                    Toast.makeText(MainActivity.this, "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "권한이 거부되었습니다. 설정(앱 정보)에서 권한을 허용해야합니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    /* 런타임 권한 처리 */
    void checkRunTimePermission(){
        /* 1. 위치 권한을 가지고 있는지 체크합니다. */
        int hasFineLoacationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLoacationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLoacationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLoacationPermission == PackageManager.PERMISSION_GRANTED){
            /* 2. 이미 권한을 가지고 있다면 위치 값을 가져올 수 있다.
            (안드로이드 6.0이하 버전은 런타임 퍼미션이 필요없음) */

        }else{  /* 2. 권한 요청을 허용한 적이 없다면 권한 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다. */

            /* 3-1. 사용자가 권한 거부를 한 적이 있는 경우에는 */
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])){
                /* 3-2. 요청을 진행하기 전, 사용자에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다. */
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();

                /* 3.3. 사용자에게 권한 요청을 합니다. 요청 결과는 onRequestPermissionResult()에서 수신됩니다. */
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);

            }else{
                /* 4.1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                *  요청 결과는 onRequestPermissionResult에서 수신됩니다.*/
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        }
    }




    public String getCurrentAddress(double latitude, double longitude){

        /* 지오코더 (GPS를 주소로 변환) */
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7
            );
        }catch (IOException e){
            /* 네트워크 문제 */
            Toast.makeText(this, "주소변환 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "주소변환 서비스 사용불가";
        }catch (IllegalArgumentException e){
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }


        if(addresses == null || addresses.size() == 0){
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_SHORT).show();
            return "주소 미발견";
        }


        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }



    /* 여기부터는 GPS 활성화를 위한 메소드들 */
    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GPS_ENABLE_REQUEST_CODE :
                /* 사용자가 GPS 활성 시켰는지 검사 */
                if(checkLocationServicesStatus()){
                    Log.d("@@@", "onActivityResult : GPS 활성화 되어있음.");
                    checkRunTimePermission();
                    return;
                }
                break;
        }
    }



    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



}
