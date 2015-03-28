///////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) Heartware Group Fall 2014 - Spring 2015
// @license
// @purpose ASU Computer Science Capstone Project
// @app a smart health application
// @authors Mark Aleheimer, Ryan Case, Tyler O'Brien, Amy Mazzola, Zach Mertens, Sri Somanchi
// @mailto zmertens@asu.edu
// @version 1.0
//
// Source code: github.com/tjobrie5/HeartWare
//
// Description: Invisible fragment that helps handle OAutho connections with Jawbone Up
//  device and the Android device.
///////////////////////////////////////////////////////////////////////////////////////////

package heartware.com.heartware_master;

import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jawbone.upplatformsdk.api.ApiManager;
import com.jawbone.upplatformsdk.oauth.OauthUtils;
import com.jawbone.upplatformsdk.oauth.OauthWebViewActivity;
import com.jawbone.upplatformsdk.utils.UpPlatformSdkConstants;
import com.jawbone.upplatformsdk.api.response.OauthAccessTokenResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class JawboneUpHelper extends Fragment
{
    public static final String TAG = JawboneUpHelper.class.getSimpleName();
    // Jawbone stuff
    private static final String CLIENT_ID = "7cXqsS_BjH8";
    private static final String CLIENT_SECRET = "eba2d19923c18c57393b289653302ff633817012";
    // This has to be identical to the OAuth redirect url setup in Jawbone Developer Portal
    private static final String OAUTH_CALLBACK_URL = "http://localhost/helloup?";
    private List<UpPlatformSdkConstants.UpPlatformAuthScope> mAuthScope;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // grant all required levels of permissions
        mAuthScope = new ArrayList<UpPlatformSdkConstants.UpPlatformAuthScope>();
        mAuthScope.add(UpPlatformSdkConstants.UpPlatformAuthScope.ALL);
    }

    /**
     * This class is an invisible UI worker for the MainActivity
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return null no view for this fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return null;
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     * Called from MainActivity when user clicks the 'sync' button on the main UI view
     */
    public void sync()
    {
        Intent intent = getIntentForWebView();
        startActivityForResult(intent, UpPlatformSdkConstants.JAWBONE_AUTHORIZE_REQUEST_CODE);
    }

    /**
     * Called to stop the syncing of data between Jawbone UP and Android devices.
     * Called externally in MainActivity when user logouts out or stops.
     */
    public void stop()
    {

    }

    private Intent getIntentForWebView()
    {
        Uri.Builder builder = OauthUtils.setOauthParameters(
                CLIENT_ID, OAUTH_CALLBACK_URL, mAuthScope);
        Intent intent = new Intent(OauthWebViewActivity.class.getName());
        intent.putExtra(UpPlatformSdkConstants.AUTH_URI, builder.build());
        return intent;
    }

    /**
     * Launches OAuth
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UpPlatformSdkConstants.JAWBONE_AUTHORIZE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            String code = data.getStringExtra(UpPlatformSdkConstants.ACCESS_CODE);
            if (code != null) {
                 //first clear older accessToken, if it exists..
                 ApiManager.getRequestInterceptor().clearAccessToken();
                 ApiManager.getRestApiInterface().getAccessToken(
                         CLIENT_ID,
                         CLIENT_SECRET,
                         code,
                         accessTokenRequestListener);
            }
        }
    }

    private Callback accessTokenRequestListener = new Callback<OauthAccessTokenResponse>()
    {
        @Override
        public void success(OauthAccessTokenResponse result, Response response)
        {
            if (result.access_token != null) {
                // store the access token for easy access
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(UpPlatformSdkConstants.UP_PLATFORM_ACCESS_TOKEN, result.access_token);
                editor.putString(UpPlatformSdkConstants.UP_PLATFORM_REFRESH_TOKEN, result.refresh_token);
                editor.commit();

                TokenToServer tokenToServer = (TokenToServer) new TokenToServer().execute(
                        new String(result.access_token));

                Toast.makeText(getActivity(), "Connected with Jawbone UP Device", Toast.LENGTH_SHORT).show();
                Log.d(TAG, result.access_token + " THIS");
                Log.d(TAG, "accessToken:" + result.access_token);
            }
            else {
                Log.d(TAG, "accessToken not returned by Oauth call, exiting...");
            }
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            Log.d(TAG, "failed to get accessToken: " + retrofitError.getMessage());
        }
    };

    /**
     * Used to manage the synchornization of data between Android and Jawbone UP
     * devices. It is used internally by this class.
     */
    private class JawboneUpDataSyncer extends Service {
        @Override
        public void onDestroy()
        {
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent intent)
        {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId)
        {
            return super.onStartCommand(intent, flags, startId);
        }
    }

    private class TokenToServer extends AsyncTask<String, Void, String> {
        private static final String URL = "http://qqroute.com:8080/sendToken";
        @Override
        protected String doInBackground(String... params)
        {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                nameValuePair.add(new BasicNameValuePair("token", params[0]));

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                }
                catch(UnsupportedEncodingException ex) {
                    Log.d(TAG, ex.getMessage().toString());
                }

                try {
                    HttpResponse res = httpClient.execute(httpPost);
                    Log.d(TAG, "Http Post Response: " + res.toString());
                }
                catch(ClientProtocolException ex) {
                    Log.d(TAG, ex.getMessage().toString());
                }
                catch(IOException ex) {
                    Log.d(TAG, ex.getMessage().toString());
                }
            }
            catch(Exception e) {
                Log.d(TAG, e.getMessage().toString());
                return e.getMessage().toString();
            }

            return "doInBackground() -- TokenToServer";
        }
    }
} // JawboneUpHelper class
