package org.androidtown.hotplace;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapsInitializer;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import static com.naver.maps.map.NaverMap.LAYER_GROUP_TRAFFIC;

public class Traffic_MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    NaverMap nMap;

    public Traffic_MapFragment() {
        // Required empty public constructor
    }

    public static Traffic_MapFragment newInstance() {
        Traffic_MapFragment fragment = new Traffic_MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_traffic_map, container, false);
        mapView = (MapView)layout.findViewById(R.id.map_view);
        mapView.getMapAsync(this);
        return layout;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        nMap = naverMap;
        naverMap.setMapType(NaverMap.MapType.Basic);
        LatLng position = new LatLng(36.62904040000001, 127.45633909999992);
        naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(position, 13));
        naverMap.setLayerGroupEnabled(LAYER_GROUP_TRAFFIC, true);
        Marker marker = new Marker();
        marker.setPosition(position);
        marker.setMap(naverMap);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
        }
    }
}
