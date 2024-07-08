package com.example.librarymanagementapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                if (validateLogin(user, pass)) {
                    Intent intent = new Intent(MainActivity.this, BookListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateLogin(String user, String pass) {
        // Tambahkan logika validasi login di sini
        return user.equals("admin") && pass.equals("admin");
    }
}