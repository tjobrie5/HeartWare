package heartware.com.heartware_master;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.jawbone.upplatformsdk.utils.UpPlatformSdkConstants;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment
{
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private Button bRecommend;
    private Button bUserInfo;
    private GraphView mGraph;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        createButtons(rootView);
        createGraph(rootView);

        return rootView;
    }

    private void createButtons(View view)
    {
        bRecommend = (Button) view.findViewById(R.id.bRecommend);
        bRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongRunningGetIO().execute();
            }
        });
        bUserInfo = (Button) view.findViewById(R.id.bUserInfo);
        bUserInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //mProfileDialog.show(getFragmentManager(), TAG);
            }
        });
    }

    private void createGraph(View view)
    {
        // Build the graph from user's data
        mGraph = (GraphView) view.findViewById(R.id.userDataGraph);
        mGraph.setTitle("Your Workouts");
        //mGraph.setTitleTextSize(14.0f);
        mGraph.setTitleColor(Color.YELLOW);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setColor(Color.RED);
        mGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
        mGraph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        mGraph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.YELLOW);
        mGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.YELLOW);
        mGraph.getGridLabelRenderer().setVerticalAxisTitle("Space");
        mGraph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.YELLOW);
        mGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.YELLOW);
        mGraph.addSeries(series);
        mGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // @TODO : send user to a DIALOG comprehensive data view
                Log.d(TAG, "The Graph got clicked");
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        triggerAnimation();
    }

    /**
     * start the animation on the graph
     */
    private void triggerAnimation()
    {
        // set up animation
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.animated_view);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        mGraph.startAnimation(animation);
    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String>
    {
        String body = "nada";
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException
        {
            InputStream in = entity.getContent();


            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];
                n =  in.read(b);


                if (n>0) out.append(new String(b, 0, n));
            }


            System.out.println("First string: "+out.toString());
            return out.toString();
        }


        @Override
        protected String doInBackground(Void... params) {
            HttpClient client= new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://qqroute.com:8080/getWorkout");
            ResponseHandler<String> handler = new BasicResponseHandler();
            Log.d(TAG," inside do in background");
            try {
                HttpResponse response = client.execute(httpGet);
                body = handler.handleResponse(response);
                Log.d(TAG, body + " got to try");
                //Toast.makeText(getApplicationContext(), body, Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),
                //"You haven't take that many steps today. Why don't you " + body + "?", Toast.LENGTH_LONG)
                //.show();
            }
            catch(IOException ex) {
                Log.d(TAG, ex.getMessage().toString());
            }
            return "ayooo";
        }

        @Override
        protected void onPostExecute(String results){
            Toast.makeText(getActivity(), "You have not been very active... " + body, Toast.LENGTH_LONG).show();
        }
    } // LongRunningGetIO class

} // ProfileFragment class
