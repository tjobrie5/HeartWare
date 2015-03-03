package com.ryancase.heartware_v2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button b_friends;
    Button b_graphs;
    Button b_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b_graphs = (Button) findViewById(R.id.bGoals);
        b_graphs.setOnClickListener(this);

        b_friends = (Button) findViewById(R.id.bFriends);
        b_friends.setOnClickListener(this);

        b_home = (Button) findViewById(R.id.button3);
        b_home.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bGoals:
                startActivity(new Intent(getApplicationContext(),GoalsActivity.class));
                break;

            case R.id.bFriends:
                startActivity(new Intent(getApplicationContext(),FriendsActivity.class));
                break;

            case R.id.button3:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;

            default:
                break;
        }

    }
}
