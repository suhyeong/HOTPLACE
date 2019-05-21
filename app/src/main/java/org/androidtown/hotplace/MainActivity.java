package org.androidtown.hotplace;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.ssomai.android.scalablelayout.ScalableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

import static com.naver.maps.map.NaverMap.LAYER_GROUP_TRAFFIC;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    //Toolbar MainToolbar;
    private BackPressCloseHandler backPressCloseHandler;
    private DrawerLayout drawer;
    //ActionBarDrawerToggle dtToggle;
    NavigationView navigationView;
    GoogleMap mMap;

    LocationManager locationManager;
    double mLatitude; //위도
    double mLongitude; //경도
    TextView AddressTextview;
    private String userLocation;

    private FirebaseAuth userAuth = FirebaseAuth.getInstance();; //로그인한 사용자 정보
    final FirebaseUser user = userAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    CircleImageView userprofile;
    private TextView username;

    ImageView WeatherIconImg;
    TextView now_temp;
    TextView max_temp;
    TextView min_temp;

    String name_str;
    int profile_state;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Traffic_MapFragment traffic_mapFragment;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://hot-place-231908.appspot.com/");
    StorageReference storageReference = storage.getReference();
    StorageReference pathReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MainToolbar = (Toolbar) findViewById(R.id.maintoolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        AddressTextview = (TextView) findViewById(R.id.address_textview);

        //setSupportActionBar(MainToolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //dtToggle = new ActionBarDrawerToggle(this, drawer, MainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.addDrawerListener(dtToggle);
        //dtToggle.syncState();
        //drawer.setDrawerListener(dtToggle);
        navigationView.setNavigationItemSelectedListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);


        //GPS가 켜져있는지 확인
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
            finish();
        }

        //마시멜로 이상일 경우
        if(Build.VERSION.SDK_INT >= 23) {
            //권한 없는 경우
            if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION} , 1);
            }
            //권한이 있는 경우
            else {
                requestMyLocation();
            }
        } //마시멜로 아래
        else {
            requestMyLocation();
        }

        //navigation view
        View nav_header = navigationView.getHeaderView(0);
        View nav_header_view = LayoutInflater.from(this).inflate(R.layout.navigation_header, null, false);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.removeHeaderView(nav_header);
        navigationView.addHeaderView(nav_header_view);

        //user info
        username = (TextView) nav_header_view.findViewById(R.id.user_name);
        userprofile = (CircleImageView) nav_header_view.findViewById(R.id.user_profile);

        database.getInstance().getReference("user_info").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user_ = dataSnapshot.getValue(User.class);
                name_str = user_.userName;
                profile_state = user_.userProfile;

                username.setText(name_str);

                if(profile_state == 0)
                    userprofile.setImageResource(R.drawable.user_defaultprofile);
                else {
                    pathReference = storageReference.child(user.getUid()+"/ProfileImage/ProfilePhoto");
                    pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageurl = uri.toString();
                            Glide.with(getApplicationContext())
                                    .load(imageurl)
                                    .into(userprofile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        database.getInstance().getReference("Friends_info").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0)
                    friends_marker();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //navigationview
        ViewGroup nav_head = (ViewGroup) nav_header_view.findViewById(R.id.nav_header_layout);
        nav_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserinfoList(v);
            }
        });

        //weather
        now_temp = (TextView) findViewById(R.id.weather_nowtemp);
        max_temp = (TextView) findViewById(R.id.weather_maxtemp);

        min_temp = (TextView) findViewById(R.id.weather_mintemp);
        WeatherIconImg = (ImageView) findViewById(R.id.weather_icon);

        //구글맵 생성
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);

        //traffic info
        traffic_mapFragment = new Traffic_MapFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.traffic_map, traffic_mapFragment);
        fragmentTransaction.commit();

        //=> 구글맵과 네이버지도 동시에 키기 가능, Toolbar 때문에 켜지지 않음 수정할것 !
    }


    //권한 요청후 응답 콜백
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        //ACCESS_COARSE_LOCATION 권한
        if(requestCode == 1) {
            //권한 받음
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestMyLocation();
            }
            //권한 못받음
            else {
                Toast.makeText(this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    //위치정보 구하기 리스너
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //나의 위치를 한번만 가져오기 위해
            locationManager.removeUpdates(locationListener);

            //위도 경도
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            //위도와 경도로 주소 찾기
            getAddress(mLatitude, mLongitude);

            //날씨 구하기
            find_weather(mLatitude, mLongitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("gps","onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    //나의 위치 요청
    public void requestMyLocation() {
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } //요청
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //지도 타입 - 일반
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 나의 위치 설정
        final LatLng position = new LatLng(mLatitude, mLongitude);

        //화면 중앙의 위치와 카메라 줌비율
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        database.getInstance().getReference("user_info").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User get = dataSnapshot.getValue(User.class);
                String username_for_marker = get.userName;
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(username_for_marker)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                marker.showInfoWindow();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mMap.setOnMarkerClickListener(this);
    }

    public void friends_marker() {

    }

    //위도와 경도로 주소 찾기
    public void getAddress(double mLatitude, double mLongitude) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        String str = null;
        List<Address> address;

        try {
            if(geocoder != null) {
                address = geocoder.getFromLocation(mLatitude, mLongitude, 10);
                if(address != null && address.size() > 0) {
                    str = address.get(0).getAddressLine(0).toString();
                    address.get(0).getLocality();
                }
            }
        } catch (IOException e) {
            Toast.makeText(this, "현재 주소를 확인 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        userLocation = str;
        AddressTextview.setText(str);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_actions, menu);
        return true;
    }

    @Override
    public void onPostCreate(Bundle saveInstanceState) {
        super.onPostCreate(saveInstanceState);
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //search icon selected : click event for action_search item
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                //do nothing
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.memo:
                openMemoList();
                break;
            case R.id.friends:
                openFriends();
                break;
            case R.id.notice:
                break;
            case R.id.setting:
                openSettings();
                break;
            case R.id.share:
                share();
                break;
            case R.id.help:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                AlertDialog.Builder logout_builder = new AlertDialog.Builder(this);
                logout_builder.setMessage("로그아웃 하시겠습니까?").setCancelable(false)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //로그아웃 "네" 클릭시
                                userAuth.signOut();
                                Toast.makeText(MainActivity.this ,"로그아웃되었습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, welcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                //android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //로그아웃 "아니오" 클릭시
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert = logout_builder.create();
                alert.show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //두번 눌렀을 때 종료
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            backPressCloseHandler.onBackPressed();
        }
    }

    //위도와 경도에 맞는 날씨 찾기
    public void find_weather(double mLatitude, double mLongitude) {
        String current_url = "https://api.openweathermap.org/data/2.5/weather?lat="+mLatitude+"&lon="+mLongitude+"&appid=e6b1f229824c3f4eac0cf8000177ecc2";

        JsonObjectRequest jor_current = new JsonObjectRequest(Request.Method.GET, current_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //current weater에서는 오늘 하루 현재의 온도와 습도, 바람 방향, 세기, 아이콘 등을 가져온다.
                try {
                    JSONObject main_object = response.getJSONObject("main"); //객체인 main 가져오기 (temp, pressure, humidity, temp_min, temp_max)
                    JSONArray array = response.getJSONArray("weather"); //배열인 weather 가져오기
                    JSONObject object = array.getJSONObject(0);
                    JSONObject wind_object = response.getJSONObject("wind"); //객체인 wind 가져오기 (speed, deg)

                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String humidity = String.valueOf(main_object.getDouble("humidity"));
                    String temp_max = String.valueOf(main_object.getDouble("temp_max"));
                    String temp_min = String.valueOf(main_object.getDouble("temp_min"));

                    String weather_icon = String.valueOf(object.getString("icon"));
                    ViewweatherIcon(weather_icon);

                    String wind_speed = String.valueOf(wind_object.getDouble("speed"));
                    String wind_deg = String.valueOf(wind_object.getDouble("deg"));

                    double temp_int = Double.parseDouble(temp);
                    double maxtemp_int = Double.parseDouble(temp_max);
                    double mintemp_int = Double.parseDouble(temp_min);

                    double centi1 = (temp_int - 273.15);
                    double centi2 = (maxtemp_int - 273.15);
                    double centi3 = (mintemp_int - 273.15);
                    int i1 = (int)centi1;
                    int i2 = (int)centi2;
                    int i3 = (int)centi3;

                    now_temp.setText(String.valueOf(i1));
                    max_temp.setText(String.valueOf(i2));
                    min_temp.setText(String.valueOf(i3));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor_current);
    }

    //날씨에 맞는 아이콘 출력
    public void ViewweatherIcon(String iconcode) {
        switch (iconcode) {
            case "01d" : //clear sky day
                WeatherIconImg.setImageResource(R.drawable.weather_sun);
                break;
            case "02d" : //few clouds day
                WeatherIconImg.setImageResource(R.drawable.weather_few_clouds_sun);
                break;
            case "03d" : //scattered clouds day
                WeatherIconImg.setImageResource(R.drawable.weather_clouds);
                break;
            case "04d" : //broken clouds day
                WeatherIconImg.setImageResource(R.drawable.weather_broken_clouds);
                break;
            case "09d" : //shower rain day
                WeatherIconImg.setImageResource(R.drawable.weather_shower_rain);
                break;
            case "10d" : //rain day
                WeatherIconImg.setImageResource(R.drawable.weather_rain_day);
                break;
            case "11d" : //thunderstorm day
                WeatherIconImg.setImageResource(R.drawable.weather_thuderstorm);
                break;
            case "13d" : //snow day
                WeatherIconImg.setImageResource(R.drawable.weather_snow);
                break;
            case "50d" : //mist day
                WeatherIconImg.setImageResource(R.drawable.weather_mist);
                break;
            case "01n" : //clear sky night
                WeatherIconImg.setImageResource(R.drawable.weather_night);
                break;
            case "02n" : //few clouds night
                WeatherIconImg.setImageResource(R.drawable.weather_few_clouds_nignt);
                break;
            case "03n" : //scattered clouds night
                WeatherIconImg.setImageResource(R.drawable.weather_clouds);
                break;
            case "04n" : //broken clouds night
                WeatherIconImg.setImageResource(R.drawable.weather_broken_clouds);
                break;
            case "09n" : //shower rain night
                WeatherIconImg.setImageResource(R.drawable.weather_shower_rain);
                break;
            case "10n" : //rain night
                WeatherIconImg.setImageResource(R.drawable.weather_rain_night);
                break;
            case "11n" : //thunderstorm night
                WeatherIconImg.setImageResource(R.drawable.weather_thuderstorm);
                break;
            case "13n" : //snow night
                WeatherIconImg.setImageResource(R.drawable.weather_snow);
                break;
            case "50n" : //mist night
                WeatherIconImg.setImageResource(R.drawable.weather_mist);
                break;
        }
    }

    //개인정보 출력 및 수정 창 띄우기
    public void openUserinfoList(View view) {
        Intent intent = new Intent(this, UserinfolistActivity.class);
        startActivity(intent);
    }

    //사용자가 작성한 메모 리스트 화면 띄우기
    public void openMemoList() {
        Intent intent = new Intent(this, MemoListActivity.class);
        startActivity(intent);
    }

    //친구 화면 띄우기
    public void openFriends() {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    //설정 화면 띄우기
    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    //구글맵 마커 클릭시
    @Override
    public boolean onMarkerClick(Marker marker) {
        AlertDialog.Builder CreateMemo_builder = new AlertDialog.Builder(this);
        CreateMemo_builder.setMessage("현재 위치에 메모를 추가하시겠습니까?").setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //메모생성 '네' 클릭시
                        openMemo();
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //메모생성 '아니요' 클릭시
                dialog.dismiss();
            }
        });
        AlertDialog alert = CreateMemo_builder.create();
        alert.show();
        return true;
    }

    //메모 작성 화면 띄우기
    public void openMemo() {
        Intent intent = new Intent(MainActivity.this, MemoActivity.class);
        intent.putExtra("user location", userLocation + "," + mLatitude + "," + mLongitude);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //공유 기능
    public void share() {
        String subject = "INVITE TO HOT PLACE!";
        String text = name_str+"님이 핫플레이스를 공유하였습니다! 지금 확인해보세요.";
        List targetShareIntent = new ArrayList<>();

        //페이스북
        Intent facebookIntent = getShareIntent("facebook", subject, text);
        if(facebookIntent != null)
            targetShareIntent.add(facebookIntent);

        //트위터
        Intent twitterIntent = getShareIntent("twitter", subject, text);
        if(twitterIntent != null)
            targetShareIntent.add(twitterIntent);

        //Gmail
        Intent gmailIntent = getShareIntent("gmail", subject, text);
        if(gmailIntent != null)
            targetShareIntent.add(gmailIntent);

        //카카오톡
        Intent kakaotalkintent = getShareIntent("com.kakao.talk", subject, text);
        if (kakaotalkintent != null) {
            targetShareIntent.add(kakaotalkintent);
        }

        Intent chooser = Intent.createChooser((Intent) targetShareIntent.remove(0), "친구에게 공유하기");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntent.toArray(new Parcelable[]{}));
        startActivity(chooser);

    }

    private Intent getShareIntent(String name, String subject, String text) {
        boolean found = false;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        List<ResolveInfo> resinfo = getPackageManager().queryIntentActivities(intent, 0);

        if(resinfo == null || resinfo.size() == 0)
            return null;

        for(ResolveInfo info : resinfo) {
            if(info.activityInfo.packageName.toLowerCase().contains(name) || info.activityInfo.name.toLowerCase().contains(name)) {
                intent.putExtra(Intent.EXTRA_TEXT, text);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.setPackage(info.activityInfo.packageName);
                found = true;
                break;
            }
        }

        if (found)
            return intent;

        return null;
    }

    //카카오톡 공유하기
    public void shareKakao() {
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
            kakaoTalkBuilder.addText(name_str+"님이 핫플레이스를 공유하였습니다!");
            kakaoTalkBuilder.addAppButton("앱 실행 혹은 다운로드");
            kakaoLink.sendMessage(kakaoTalkBuilder, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}