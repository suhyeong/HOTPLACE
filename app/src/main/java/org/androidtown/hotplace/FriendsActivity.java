package org.androidtown.hotplace;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener {

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    Button friends_list, friends_search, friends_request;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    FriendsListFragment friends_list_fragment;
    FriendsSearchFragment friends_search_fragment;
    FriendsRequestFragment friends_request_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        friends_list = (Button) findViewById(R.id.friends_list_button);
        friends_search = (Button) findViewById(R.id.friends_search_button);
        friends_request = (Button) findViewById(R.id.friends_request_button);
        friends_list.setOnClickListener(this);
        friends_search.setOnClickListener(this);
        friends_request.setOnClickListener(this);

        friends_list.setBackgroundResource(R.drawable.friends_button_click_shape);
        friends_search.setBackgroundResource(R.drawable.friends_button_shape);
        friends_request.setBackgroundResource(R.drawable.friends_button_shape);

        friends_list_fragment = new FriendsListFragment();
        friends_search_fragment = new FriendsSearchFragment();
        friends_request_fragment = new FriendsRequestFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.friends_main_fragment, friends_list_fragment);
        fragmentTransaction.commit();

        database.getInstance().getReference("Request Friends").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    friends_request.setText("요청" + " (" + dataSnapshot.getChildrenCount() + ")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //툴바 메뉴 선택 시 실행
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                //뒤로가기 버튼 클릭시
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friends_list_button:
                friends_list.setBackgroundResource(R.drawable.friends_button_click_shape);
                friends_search.setBackgroundResource(R.drawable.friends_button_shape);
                friends_request.setBackgroundResource(R.drawable.friends_button_shape);
                setFragment(friends_list_fragment);
                break;
            case R.id.friends_search_button:
                friends_search.setBackgroundResource(R.drawable.friends_button_click_shape);
                friends_list.setBackgroundResource(R.drawable.friends_button_shape);
                friends_request.setBackgroundResource(R.drawable.friends_button_shape);
                setFragment(friends_search_fragment);
                break;
            case R.id.friends_request_button:
                friends_request.setBackgroundResource(R.drawable.friends_button_click_shape);
                friends_search.setBackgroundResource(R.drawable.friends_button_shape);
                friends_list.setBackgroundResource(R.drawable.friends_button_shape);
                setFragment(friends_request_fragment);
                break;
        }
    }

    public void setFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.friends_main_fragment, fragment);
        fragmentTransaction.commit();
    }

}