package com.example.listec.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.style.StrikethroughSpan;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.listec.R;

public class AcercaDeActivity extends AppCompatActivity {

    private static final String titulo = "Acerca de nosotros";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(titulo);
        setContentView(R.layout.activity_acerca_de);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = findViewById(R.id.isaac_text);

        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();


    }
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}