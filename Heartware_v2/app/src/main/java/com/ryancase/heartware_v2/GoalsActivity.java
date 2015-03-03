package com.ryancase.heartware_v2;

import android.app.ListActivity;
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

public class GoalsActivity extends ListActivity
{
    private static final String TAG = GoalsActivity.class.getSimpleName();
    private Button bAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_list);

        ListView list = (ListView) findViewById(android.R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // @TODO : goto graph view based on int position in list
                startActivity(new Intent(getApplication(), GraphsActivity.class));
                Log.d(TAG, "in onItemSelected listener");
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // @TODO : delete a goal
                Log.d(TAG, "in the onItemLongClick listener");
                return false;
            }
        });

        List<String> graph_List = new ArrayList<String>();

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

        setListAdapter(arrayAdapter);

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
