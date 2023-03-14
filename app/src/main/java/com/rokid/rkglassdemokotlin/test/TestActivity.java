package com.rokid.rkglassdemokotlin.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.rokid.rkglassdemokotlin.R;

import java.util.HashMap;
import java.util.Map;


public class TestActivity extends AppCompatActivity {

    private com.rokid.rkglassdemokotlin.databinding.ActivityTestBinding binding;
    Map<String, String> map = new HashMap<>();
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        TestBean data = binding.getData();
        binding.setData(data);
        Message message = new Message();
    }


}
