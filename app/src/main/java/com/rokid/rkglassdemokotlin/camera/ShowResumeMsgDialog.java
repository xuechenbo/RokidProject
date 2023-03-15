package com.rokid.rkglassdemokotlin.camera;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.rokid.rkglassdemokotlin.R;
import com.rokid.utils.ToastUtils;


public class ShowResumeMsgDialog extends DialogFragment {
    private String url;

    public static ShowResumeMsgDialog getInstance(String url) {
        ShowResumeMsgDialog showResumeMsgDialog = new ShowResumeMsgDialog();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        showResumeMsgDialog.setArguments(bundle);
        return showResumeMsgDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.MyDialog);
        url = (String) getArguments().getString("templateId");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = View.inflate(getActivity(), R.layout.dialog_register, container);
        initData(inflate);
        return inflate;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.85), (int) (dm.heightPixels * 0.6));
        }
        dialog.setCanceledOnTouchOutside(false);
    }

    private void initData(View view) {
        Dialog dialog = getDialog();
//        view.findViewById()
        EditText editName = view.findViewById(R.id.et_content);
        view.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editName.getText().toString().isEmpty()) {
                    ToastUtils.makeText(getActivity(), "请输入名称");
                    return;
                }
                registerPhone();
            }
        });
//        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
    }


    public void registerPhone() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
