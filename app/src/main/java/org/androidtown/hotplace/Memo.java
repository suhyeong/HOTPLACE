package org.androidtown.hotplace;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Memo {
    public String contents;
    public String date;
    public int photo_exist;

    public Memo() {
        // Default constructor required for calls to DataSnapshot.getValue(Memo.class)
    }

    public Memo(String contents, String date, int photo_exist) {
        this.contents = contents;
        this.date = date;
        this.photo_exist = photo_exist;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Memo Contents", contents);
        result.put("Memo Date", date);
        result.put("Memo Photo exist", photo_exist);
        return result;
    }
}
