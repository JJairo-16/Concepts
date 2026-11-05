package com.example.concepts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Initializes the activity and sets up its user interface.
     *
     * @param savedInstanceState the previously saved state of the activity,
     *                           or {@code
     *                           null} if no saved state exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListener(R.id.btn_start);

        setListener(R.id.btn_i3);
        setListener(R.id.btn_i4);
        setListener(R.id.btn_i5);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_start) {
            finish();
            System.exit(0);
            return;
        }

        Intent intent;
        if (id == R.id.btn_i3) {
            intent = new Intent(this, MenuI3Activity.class);
        } else if (id == R.id.btn_i4) {
            intent = new Intent(this, MenuI4Activity.class);
        } else if (id == R.id.btn_i5) {
            intent = new Intent(this, MenuI5Activity.class);
        } else return;

        startActivity(intent);
    }

    private void setListener(int id) {
        findViewById(id).setOnClickListener(this);
    }
}