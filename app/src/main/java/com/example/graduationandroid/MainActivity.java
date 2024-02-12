package com.example.graduationandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DB db;
    private ListView list_links;
    private EditText link, short_link;
    private TextView infotext;
    private  Button short_link_r;
    private ArrayAdapter<String> arrayAdapter;
    private Button addButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);
        list_links = findViewById(R.id.list_links);
        link = findViewById(R.id.link_text);
        short_link = findViewById(R.id.short_link_text);
        short_link_r = findViewById(R.id.short_link_r);
        infotext = findViewById(R.id.infotext);
        addButton = findViewById(R.id.add_link_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String original_link = String.valueOf(link.getText());
                String short_original_link = String.valueOf(short_link.getText());
                if (original_link.isEmpty() || short_original_link.isEmpty())
                    infotext.setText("Заполните все поля!");
                else {
                    if (db.isExistsShortLink(short_original_link)) {
                        infotext.setText("Короткая ссылка занята!");
                    } else {
                        db.insertLink(original_link, short_original_link);
                        loadAllLinks();
                        infotext.setText("");
                    }
                }
            }
        });

        loadAllLinks();


    }


    private void loadAllLinks() {
        ArrayList<String> allTask = db.getAllLinks();
        if(arrayAdapter == null) {
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.links_row, R.id.short_link_r, allTask);
            list_links.setAdapter(arrayAdapter);
        } else {
            arrayAdapter.clear();
            arrayAdapter.addAll(allTask);
            arrayAdapter.notifyDataSetChanged();
        }
    }
    public void deleteLink(View button) {
        final View parent = (View) button.getParent();
        Button list_link = parent.findViewById(R.id.short_link_r);
        final String link = String.valueOf(list_link.getText());
        db.deleteLink(link);
        loadAllLinks();
    }

    public void openLink(View button) {
        final View parent = (View) button.getParent();
        Button list_link = parent.findViewById(R.id.short_link_r);
        final String link = String.valueOf(list_link.getText());
        String url = db.findLink(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}