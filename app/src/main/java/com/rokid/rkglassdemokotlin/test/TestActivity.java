package com.rokid.rkglassdemokotlin.test;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.rokid.rkglassdemokotlin.R;


public class TestActivity extends AppCompatActivity {

    private com.rokid.rkglassdemokotlin.databinding.ActivityTestBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        TestBean data = binding.getData();
        binding.setData(data);
    }
}
