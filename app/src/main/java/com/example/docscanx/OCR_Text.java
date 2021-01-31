package com.example.docscanx;

import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class OCR_Text extends AppCompatActivity {

    int position =1;
    String pdfFileName;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_activity);
        toolbar = (Toolbar) findViewById(R.id.tb);

        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        position = getIntent().getIntExtra("position",-1);
        display();
    }

    private void display(){
         pdfFileName = OCR_fragment.filelist1.get(position).getName();
        Toast.makeText(this,pdfFileName,Toast.LENGTH_SHORT).show();
        toolbar.setTitle(pdfFileName);
        StringBuilder text = new StringBuilder();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Notes/"+pdfFileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            e.printStackTrace();
            Toast.makeText(this,"No file available",Toast.LENGTH_SHORT).show();
        }

        //Find the view by its id
        EditText tv = (EditText) findViewById(R.id.ocr_text);

//Set the text
        tv.setText(text);

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(tv.getText().toString().getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
