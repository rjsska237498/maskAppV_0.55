package com.example.maskapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MyAdapter extends BaseAdapter {
    public static final int sub = 1001; /*다른 액티비티를 띄우기 위한 요청코드(상수)*/

    /* 아이템을 세트로 담기 위한 ArrayList */
    private ArrayList<MyItem> myItemList = new ArrayList<MyItem>();

    @Override
    public int getCount() {
        return myItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return myItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final Context context = viewGroup.getContext();

        /* 'listview_custom' layout을 inflate하여 convertView 참조 획득 */
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, viewGroup, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        final TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        TextView remainText = (TextView) convertView.findViewById(R.id.remainText);
        TextView addressText = (TextView) convertView.findViewById(R.id.addressText);
        TextView distanceText = (TextView) convertView.findViewById(R.id.distanceText);
        TextView stockText = (TextView) convertView.findViewById(R.id.stockText);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 myItems 재활용 */
        MyItem myItem = myItemList.get(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        nameText.setText(myItem.getName());
        remainText.setText(myItem.getremain());
        addressText.setText(myItem.getAddress());
        distanceText.setText(myItem.getDistance());
        stockText.setText(myItem.getStock());

        /* 조건에 따른 재고수량Text 색 변경 */
        if(myItem.getremain().equals("100개 이상")){
            remainText.setTextColor(Color.parseColor("#04B404"));
        }else if(myItem.getremain().equals("30개 이상 ~ 100개 미만")){
            remainText.setTextColor(Color.parseColor("#D7DF01"));
        }else if(myItem.getremain().equals("2개 이상 ~ 30개 미만")){
            remainText.setTextColor(Color.parseColor("#FFBF00"));
        }else if(myItem.getremain().equals("판매완료")){
            remainText.setTextColor(Color.parseColor("#585858"));
        }else if(myItem.getremain().equals("판매중지")){
            remainText.setTextColor(Color.parseColor("#FF0000"));
        }else if(myItem.getremain().equals("알수없음")){
            remainText.setTextColor(Color.parseColor("#FF0000"));
        }

        /* 위젯에 대한 이벤트리스너는 여기에 작성 */


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 지도로 이동하기 전, 북마크 기능과 지도이동 여부를 물어주는 대화상자를 생성 */

                System.out.println("테스트용테스트용테스트용테스트용테스트용테스트용테스트용테스트용테스트용테스트용테스트용테스트용테스트용테스트용테스트용");

                Intent intent = new Intent(context.getApplicationContext(), GMap.class);
                intent.putExtra("lat", MaskVO.getlat(position));
                intent.putExtra("lng", MaskVO.getlng(position));
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    /* 아이템 데이터 추가를 위한 함수 */
    public void additem(String name, String remain, String address, String distance, String stock){

        MyItem myitem = new MyItem();

        /* MyItem에 아이템을 setting한다 */
        myitem.setName(name);
        myitem.setRemain(remain);
        myitem.setAddress(address);
        myitem.setDistance(distance);
        myitem.setStock(stock);

        /* myItems에 myitem을 추가한다 */
        myItemList.add(myitem);

    }



    public void clearItem(){
        myItemList.clear();
    }

}
