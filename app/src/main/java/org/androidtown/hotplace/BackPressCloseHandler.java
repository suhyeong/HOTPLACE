package org.androidtown.hotplace;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finishAffinity();
            toast.cancel();
        }
    }

    private void showGuide() {
        toast = Toast.makeText(activity, "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
