package com.example.maskapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.HttpsURLConnection;

public class SearchActivity extends AppCompatActivity {

    ListView customListView;
    public static MyAdapter myAdapter;
    Double mLatitude, mLongitude;
    FloatingActionButton floatingActionButton;

    ArrayList<Double> distanceSort = new ArrayList<Double>();   // sort용 배열
    ArrayList<MaskVO> p = new ArrayList<>();               // MaskVO에 자료들을 넣기 위한 배열
    ArrayList<String> addr = new ArrayList<String>();           // 주소
    ArrayList<Double> lat = new ArrayList<Double>();            // 위도
    ArrayList<Double> lng = new ArrayList<Double>();            // 경도
    ArrayList<Double> distance = new ArrayList<Double>();       // 거리
    ArrayList<String> name = new ArrayList<String>();           // 이름
    ArrayList<String> stock_at = new ArrayList<String>();       // 입고시간
    ArrayList<String> remain_stat = new ArrayList<String>();    // 재고 상태(100개이상=plenty, 30~99개=some, 2~29개=few, 1개=empty, 판매중지=break;)

    @Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /* 위젯과 멤버변수 참조 획득 */
        customListView = findViewById(R.id.customListView);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        setList();

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_refresh);
        /* 새로고침 버튼 */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionButton.startAnimation(animation);
                p.removeAll(p);
                setList();
            }
        });

    }


    /* API로 자료를 받아와 List에 추가. */
    public void setList(){
        myAdapter = new MyAdapter();

        Runnable apiThread = new Runnable() {
            @Override
            public void run() {
                try {
                    /* MainActivivty로 부터 자신의 위치정보를 받아옴. */
                    Intent intent = getIntent();
                    mLatitude = intent.getExtras().getDouble("latitude");
                    mLongitude = intent.getExtras().getDouble("longitude");

                    System.out.println(mLatitude + " : " + mLongitude);

                    StringBuilder urlBuilder = new StringBuilder("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json");
                    urlBuilder.append("?" + URLEncoder.encode("lat", "UTF-8") + "=" + mLatitude);
                    urlBuilder.append("&" + URLEncoder.encode("lng", "UTF-8") + "=" + mLongitude);
                    urlBuilder.append("&" + URLEncoder.encode("m", "UTF-8") + "=5000");

                    //API의 Link URL을 저장한 변수를 URL로 변환
                    URL url = new URL(urlBuilder.toString());

                    //Http에 URL로 연결할 connection을 생성
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/json");

                    BufferedReader rd;
                    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    }

                    //API에서 자료를 얻어올 변수들을 선언
                    StringBuilder sb = new StringBuilder();
                    String line;
                    //선언한 변수에 while문으로 자료값이 null값이 될 떄까지 받아온다.
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.e("SB : ", sb.toString());


                    //API에서 받아온 자료를 파싱
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(sb.toString());

                    /*파싱한 jsonObject 로 부터 자료를 받아오기.
                    (stores라는 key값의 대괄호 안에 value값들이 있으므로 JSONArray를 사용*/
                    JSONArray Mask = (JSONArray) jsonObject.get("stores");
                    System.out.println("사이즈" + Mask.size());

                    for (int i = 0; i < Mask.size(); i++) {
                        JSONObject value = (JSONObject) Mask.get(i);
                        name.add(value.get("name").toString());
                        addr.add(value.get("addr").toString());
                        lat.add((double) value.get("lat"));
                        lng.add((double) value.get("lng"));

                        try {//재고 상태값이 null이 아닐 경우
                            if ((value.get("remain_stat").toString()).equals("plenty")) {
                                remain_stat.add("100개 이상");
                            } else if (value.get("remain_stat").toString().equals("some")) {
                                remain_stat.add("30개 이상 ~ 100개 미만");
                            } else if (value.get("remain_stat").toString().equals("few")) {
                                remain_stat.add("2개 이상 ~ 30개 미만");
                            } else if (value.get("remain_stat").toString().equals("empty")) {
                                remain_stat.add("판매완료");
                            } else if (value.get("remain_stat").toString().equals("break")) {
                                remain_stat.add("판매중지");
                            }
                        } catch (NullPointerException e) {
                            //재고 상태값이 null일 경우
                            remain_stat.add("알수없음");
                        }

                        try {
                            stock_at.add(value.get("stock_at").toString());
                        } catch (NullPointerException e) {
                            //재고 상태값이 null일 경우
                            stock_at.add("알수없음");
                        }

                        double dd = getDistance(mLatitude, mLongitude, lat.get(i), lng.get(i));
                        distanceSort.add(dd);   //Arrays.Sort할 배열
                        distance.add(dd);
                    }

                    endWait();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t1 = new Thread(apiThread);
        t1.start();


        try {Thread.sleep(1000);} catch (Exception e) {}

        /* sort 작업들 */
        Comparator<Double> comparator = new Comparator<Double>() {
            @Override
            public int compare(Double Double1, Double Double2) {
                return Double1.compareTo(Double2);
            }};

        Collections.sort(distanceSort, comparator);

        /* 가까운 거리에 따라서 List 순서 변경작업 */
        for (int k=0; k<distanceSort.size(); k++){
            for (int l=0; l<distanceSort.size(); l++){
                if(distanceSort.get(k).equals(distance.get(l))){
                    String dis = "";
                    if(distance.get(l) < 1000){
                        dis = String.format("%.1f", distance.get(l)) + "m";
                    }else {
                        dis = String.format("%.1f", distance.get(l)/1000) + "km";
                    }
                    p.add(new MaskVO(name.get(l), remain_stat.get(l), addr.get(l), dis, stock_at.get(l), lat.get(l), lng.get(l)));
                }
            }
        }


        for (int i = 0; i < p.size(); i++) {
            myAdapter.additem(MaskVO.getName(i), MaskVO.getremain(i), MaskVO.getAddress(i), MaskVO.getDistance(i).toString(), "(공적마스크)입고시간 : " + MaskVO.getStock(i));
        }

        /* 리스트뷰에 어댑터 등록 */
        customListView.setAdapter(myAdapter);

    }

    /* 거리 계산 */
    public double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double distance;

        Location locationA = new Location("pointA");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);


        Location locationB = new Location("pointB");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    private synchronized void endWait(){
        notify();
    }


}
