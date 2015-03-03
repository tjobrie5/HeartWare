package com.ryancase.heartware_v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button b_friends;
    private Button b_graphs;
    private Button b_home;
    private LoginDialogFragment mLoginDialog;
    //private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //dbAdapter = new DBAdapter(this);

        b_graphs = (Button) findViewById(R.id.bGoals);
        b_graphs.setOnClickListener(this);

        b_friends = (Button) findViewById(R.id.bFriends);
        b_friends.setOnClickListener(this);

        b_home = (Button) findViewById(R.id.bHome);
        b_home.setOnClickListener(this);

        mLoginDialog = new LoginDialogFragment();

        boolean profileExists = false;
        if(!profileExists) {
            mLoginDialog.show(getFragmentManager(), TAG);
        }
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

            case R.id.bHome:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;

            default:
                break;
        }

    }
} // MainActivity class
