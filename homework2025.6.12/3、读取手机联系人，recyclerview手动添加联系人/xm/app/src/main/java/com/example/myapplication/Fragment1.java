package com.example.myapplication;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.fragment.app.Fragment;

public class Fragment1 extends Fragment {
    private ToggleButton toggleButton;
    private EditText etName, etStudentId;
    private RadioGroup radioGroup;
    private RadioButton rbMale, rbFemale;
    private Button btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        initViews(view);

        setupListeners();

        preventKeyboardOverlap();

        return view;
    }

    private void initViews(View view) {
        toggleButton = view.findViewById(R.id.toggleButton);
        etName = view.findViewById(R.id.etName);
        etStudentId = view.findViewById(R.id.etStudentId);
        radioGroup = view.findViewById(R.id.radioGroup);
        rbMale = view.findViewById(R.id.rbMale);
        rbFemale = view.findViewById(R.id.rbFemale);
        btnSubmit = view.findViewById(R.id.btnSubmit);
    }

    private void setupListeners() {

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getContext(), isChecked ? "已登录" : "未登录", Toast.LENGTH_SHORT).show();
            }
        });


        etStudentId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 确保输入的是数字
                if (!s.toString().matches("^\\d{0,10}$")) {
                    etStudentId.setText(s.toString().replaceAll("[^\\d]", ""));
                    etStudentId.setSelection(etStudentId.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String studentId = etStudentId.getText().toString().trim();
                String gender = rbMale.isChecked() ? "男" : "女";
                boolean isLoggedIn = toggleButton.isChecked();

                if (name.isEmpty() || studentId.isEmpty()) {
                    Toast.makeText(getContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (studentId.length() != 10) {
                    Toast.makeText(getContext(), "学号必须是10位数字", Toast.LENGTH_SHORT).show();
                    return;
                }

                String message = "姓名: " + name + "\n" +
                        "学号: " + studentId + "\n" +
                        "性别: " + gender + "\n" +
                        "登录状态: " + (isLoggedIn ? "已登录" : "未登录");

                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });


        setupEditTextSelection();
    }

    private void setupEditTextSelection() {

        etName.setLongClickable(true);
        etStudentId.setLongClickable(true);


        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && etName.getText().length() > 0) {
                    etName.setSelection(0, etName.getText().length());
                }
            }
        });

        etStudentId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && etStudentId.getText().length() > 0) {
                    etStudentId.setSelection(0, etStudentId.getText().length());
                }
            }
        });
    }

    private void preventKeyboardOverlap() {

        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            );
        }
    }
}

