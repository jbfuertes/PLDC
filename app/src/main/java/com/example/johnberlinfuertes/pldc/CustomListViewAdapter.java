package com.example.johnberlinfuertes.pldc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by berlin on 12/16/2016.
 */

class CustomListViewAdapter extends ArrayAdapter<String>{

    public CustomListViewAdapter(Context context, String[] wList) {
        super(context,R.layout.custom_row, wList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater wListInflater = LayoutInflater.from(getContext());

        if(convertView == null)
            convertView = wListInflater.inflate(R.layout.custom_row, parent , false);

        String wifiList = getItem(position);
        TextView wifiName = (TextView) convertView.findViewById(R.id.wifiName);
        ImageView wifiImage = (ImageView) convertView.findViewById(R.id.wifiImage);
        wifiName.setText(wifiList);
        int level = Integer.parseInt(wifiList.substring(wifiList.length()-4,wifiList.length()-2));
        if(level<=60){
            wifiImage.setImageResource(R.drawable.strongsignals);
        }
        if((level<=80)&&(level>60)){
            wifiImage.setImageResource(R.drawable.goodsignals);
        }
        if((level>80)&&(level<=90)) {
            wifiImage.setImageResource(R.drawable.weaksignals);
        }
        if(level>90){
            wifiImage.setImageResource(R.drawable.nosignals);
        }
        return convertView;
    }
}
