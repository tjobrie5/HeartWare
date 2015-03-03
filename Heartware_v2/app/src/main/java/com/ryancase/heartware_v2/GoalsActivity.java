package com.ryancase.heartware_v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GoalsActivity extends Activity
{
    private static final String TAG = GoalsActivity.class.getSimpleName();

    ListView list;
    Button bAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_list);

        list = (ListView) findViewById(R.id.listView);

        List<String> graph_List= new ArrayList<String>();

        graph_List.add("steps");
        graph_List.add("calories");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");
        graph_List.add("placeholder");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                graph_List
        );

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView parentView, View childView, int position, long id) {
                // @TODO : goto graph view -- this is broken for some reason
                startActivity(new Intent(getApplicationContext(), GraphsActivity.class));
                Log.d(TAG, "in onItemSelected listener");
            }
            public void onNothingSelected(AdapterView parentView) {

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                // @TODO : delete a goal
                Log.d(TAG, "in the onItemLongClick listener");
                return false;
            }
        });

        list.setAdapter(arrayAdapter);

        bAdd = (Button) findViewById(R.id.bAdd);
        bAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // @TODO : create a new goal
                Log.d(TAG, "in the Add Button onClick");
            }
        });

    }
} // graph_list class
