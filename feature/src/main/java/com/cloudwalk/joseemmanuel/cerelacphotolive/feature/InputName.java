package com.cloudwalk.joseemmanuel.cerelacphotolive.feature;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputName extends AppCompatActivity {
    Intent newIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_name);

        Button button = (Button) findViewById(R.id.submitName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputName = (EditText) findViewById(R.id.inputName);
                String userInput = inputName.getText().toString();

                newIntent = new Intent(getApplicationContext(), MyCameraActivity.class);
                newIntent.putExtra("InputName", userInput);
                if(userInput.equals("") || userInput.isEmpty()) {
                    return;
                }
                startActivity(newIntent);
            }
        });
    }
}
