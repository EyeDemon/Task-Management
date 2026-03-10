package com.example.chk.taskapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chk.taskapp.R;
import com.example.chk.taskapp.dal.UserDAL;
import com.example.chk.taskapp.utils.Constants;

// Màn hình đăng ký tài khoản
public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;          // Input username
    private EditText etEmail;             // Input email
    private EditText etPassword;          // Input password
    private EditText etConfirmPassword;   // Input xác nhận password
    private Button btnRegister;           // Button đăng ký
    private TextView tvLogin;             // Text chuyển sang màn hình login
    private UserDAL userDAL;              // Để lưu user vào database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Tìm các views
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        userDAL = new UserDAL(this);

        // Click button Register
        btnRegister.setOnClickListener(v -> {
            // 1. Lấy dữ liệu từ UI
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // 2. Validate dữ liệu
            if (validateInput(username, email, password, confirmPassword)) {
                // 3. Đăng ký user vào database
                boolean success = userDAL.registerUser(username, password, email);

                if (success) {
                    // Thành công → Chuyển sang LoginActivity
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    // Thất bại → Username đã tồn tại
                    Toast.makeText(RegisterActivity.this, Constants.MSG_USERNAME_EXISTS, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Click text "Đã có tài khoản? Đăng nhập" → Chuyển sang LoginActivity
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    // Validate dữ liệu đầu vào
    private boolean validateInput(String username, String email, String password, String confirmPassword) {
        // 1. Kiểm tra rỗng
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, Constants.MSG_INVALID_INPUT, Toast.LENGTH_SHORT).show();
            return false;
        }

        // 2. Kiểm tra độ dài username (tối thiểu 3 ký tự)
        if (username.length() < Constants.MIN_USERNAME_LENGTH) {
            Toast.makeText(this, "Tên đăng nhập phải ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 3. Kiểm tra độ dài password (tối thiểu 6 ký tự)
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(this, "Mật khẩu phải ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 4. Kiểm tra password và confirm password khớp nhau
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, Constants.MSG_PASSWORD_MISMATCH, Toast.LENGTH_SHORT).show();
            return false;
        }

        // 5. Kiểm tra email hợp lệ
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;  // Tất cả đều hợp lệ
    }

    // Kiểm tra email hợp lệ bằng regex pattern
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}