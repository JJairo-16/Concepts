package com.example.concepts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MenuI3Activity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_i3);

        setListener(R.id.btn_return);
        setListener(R.id.btn_activity_1);
    }

    private void setListener(int id) {
        findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        Intent intent;
        if (id == R.id.btn_return) {
            intent = new Intent(this, MainActivity.class);
        } else if (id == R.id.btn_activity_1) {
            // pendent
            return;
        } else if (id == R.id.btn_activity_2) {
            // pendent
            return;
        } else return;

        startActivity(intent);
    }
}
