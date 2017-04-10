package com.example.lbstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public LocationClient mLocalClient;
    TextView positionText;
    MapView mapview;
    BaiduMap baiduMap;
    Button back;
    int i=0;
    BDLocation locationa;
    private static final String TAG="LBSTest";
    BmobQuery<Loc> query;
    double latitude;
    double longitude;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this,"dd9cb0fddcdd80936bb70c0b305104a1");
        Log.d(TAG,"onCreate");
      //百度地图需要的代码
        mLocalClient=new LocationClient(getApplicationContext());
        mLocalClient.registerLocationListener(new MyLocationListener());
        //先要显示界面必须初始化
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        query=new BmobQuery<Loc>();
        mapview= (MapView) findViewById(R.id.bmapView);
        positionText= (TextView) findViewById(R.id.position_text_view);
        back= (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        baiduMap=mapview.getMap();
        baiduMap.setMyLocationEnabled(true);
        List<String>permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(!permissionList.isEmpty()){
            String []permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permissions,1);
        }else{
            requestLocation();
//            Toast.makeText(this,"未知错误1",Toast.LENGTH_LONG).show();
        }



    }

    private void requestLocation() {
//        Toast.makeText(this,"requestLocation",Toast.LENGTH_LONG).show();
        initLocation();
        mLocalClient.start();
        Mylog.mylog("lbsmytest","requestLocation-end");
    }

    private void initLocation() {
        Mylog.mylog("lbsmytest","initLocation-start");
        LocationClientOption option=new LocationClientOption();
        //每隔3秒调用一下
        option.setScanSpan(5000);
        //定位模式分三种，选取定位模式
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //支持具体位置
        option.setIsNeedAddress(true);
        //使用百度坐标
        option.setCoorType("bd09ll");//注意是ll
        mLocalClient.setLocOption(option);
        Mylog.mylog("lbsmytest","initLocation-end");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result:grantResults) {
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
//                    Toast.makeText(this,"未知错误2",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(this,"未知错误",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if(latitude!=0)
        backMylocation();
    }

    private void backMylocation() {
        Mylog.mylog("lbsmytest","navigateTo-dingwei-start");


        //首次定位必定先到天安门
        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(16f);
        baiduMap.animateMapStatus(update);
        Mylog.mylog("lbsmytest","navigateTo-dingwei-end");

    }

    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location) {


            query.getObject("46b41fa5c1", new QueryListener<Loc>() {
                @Override
                public void done(Loc loc, BmobException e) {
                    if(e==null){
                        Log.d("myTest","成功"+loc.getAddStreet());
                        Mylog.mylog("lbsmytest", "onReceiveLocation-start");
                        Toast.makeText(MainActivity.this, "持续更新中", Toast.LENGTH_SHORT).show();
                        latitude= Double.parseDouble(loc.getLatitude());
                        longitude= Double.parseDouble(loc.getLonggitude());
                        StringBuilder currentPosition = new StringBuilder();
                        currentPosition.append("纬度:").append(loc.getLatitude()).append("\n");
                        currentPosition.append("经度:").append(loc.getLonggitude()).append("\n");
                        currentPosition.append("地址:").append(loc.getAddStreet()).append("\n");
                        currentPosition.append("时间:").append(loc.getUpdate()).append("\n");
//                        currentPosition.append("速度:").append(location.getSpeed()).append("\n");
                        currentPosition.append("定位方式:").append(loc.getLocType());
                        positionText.setText(currentPosition);
                    }else {
                        Log.d("myTest","失败");
                    }
                }
            });



                if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    navigateTo();
                }
                Mylog.mylog("lbsmytest", "onReceiveLocation-end");
            }

    }

    private void navigateTo() {
        Mylog.mylog("lbsmytest","navigateTo-start");

        if(i<2) {
            i++;
            backMylocation();
        }
        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
        locationBuilder.latitude(latitude);
        locationBuilder.longitude(longitude);
        MyLocationData locationData=locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
        Mylog.mylog("lbsmytest","navigateTo-end");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        mLocalClient.stop();
        mapview.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        mapview.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart");
    }
}
