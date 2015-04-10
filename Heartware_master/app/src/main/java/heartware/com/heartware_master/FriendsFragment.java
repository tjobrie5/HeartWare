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
// Description: Handles Facebook API calls and social networking functionality.
//  Example: http://javatechig.com/android/using-facebook-sdk-in-android-example
///////////////////////////////////////////////////////////////////////////////////////////

/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package heartware.com.heartware_master;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookGraphResponseException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphResponse;
import com.facebook.internal.Utility;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FriendsFragment extends Fragment
{
    private static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String EXERCISE_OBJECT_TYPE = "heartware:exercise"; // note - must be lower case
    private static final String EX_ACTION_TYPE = "heartware:exercise";

    private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
    private static final int USER_GENERATED_MIN_SIZE = 480;
    private static final float MAX_TEXTURE_SIZE = 1024f;

    private static final String PERMISSION = "publish_actions";

    private Button bMeetup;
    private ShareButton shareButton;
    private LoginButton bLoginButton;
    private ListView listView;
    private List<FB_BaseListElement> listElements;
    private ProfilePictureView profilePictureView;
    private boolean pendingAnnounce;

    private Uri photoUri;
    private ImageView photoThumbnail;
    private ScaleAndSetImageTask runningImageTask;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private FacebookCallback<Sharer.Result> shareCallback =
            new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {
                    processDialogResults(null, true);
                }

                @Override
                public void onError(FacebookException error) {
                    if (error instanceof FacebookGraphResponseException) {
                        FacebookGraphResponseException graphError =
                                (FacebookGraphResponseException) error;
                        if (graphError.getGraphResponse() != null) {
                            handleError(graphError.getGraphResponse());
                            return;
                        }
                    }
                    processDialogError(error);
                }

                @Override
                public void onSuccess(Sharer.Result result) {
                    processDialogResults(result.getPostId(), false);
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                updateWithToken(currentAccessToken);
            }
        };
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            tokenUpdated(currentAccessToken);
            profilePictureView.setProfileId(currentAccessToken.getUserId());
            bMeetup.setVisibility(View.VISIBLE);
        } else {
            profilePictureView.setProfileId(null);
            bMeetup.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        bLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        bLoginButton.setReadPermissions("user_friends");
        bLoginButton.setFragment(this);
        bLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel()
            {
                Toast.makeText(getActivity(), "Login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e)
            {
                Toast.makeText(getActivity(), "Login error", Toast.LENGTH_SHORT).show();
            }
        });

        bMeetup = (Button) view.findViewById(R.id.create_meetup);
        bMeetup.getBackground().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
        shareButton = (ShareButton) view.findViewById(R.id.share_button);
        listView = (ListView) view.findViewById(R.id.selection_list);
        photoThumbnail = (ImageView) view.findViewById(R.id.selected_image);

        bMeetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMeetup();
            }
        });

        shareButton.registerCallback(callbackManager, shareCallback);
        shareButton.setFragment(this);

        init(savedInstanceState);
        updateWithToken(AccessToken.getCurrentAccessToken());

        return view;
    } // onCreateView

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode >= 0 && requestCode < listElements.size()) {
            listElements.get(requestCode).onActivityResult(data);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        for (FB_BaseListElement listElement : listElements) {
            listElement.onSaveInstanceState(bundle);
        }
        bundle.putBoolean(PENDING_ANNOUNCE_KEY, pendingAnnounce);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    /**
     * Save the meetups details in SQL
     */
    private void createMeetup()
    {
        String exText = listElements.get(0).getText2();
        String locText = listElements.get(0).getText2();
        String friendsText = listElements.get(0).getText2();
        String photoText = listElements.get(0).getText2();
    }

    private void processDialogError(FacebookException error) {
        if (error != null) {
            new AlertDialog.Builder(getActivity())
                    .setPositiveButton(R.string.error_dialog_button_text, null)
                    .setTitle(R.string.error_dialog_title)
                    .setMessage(error.getLocalizedMessage())
                    .show();
        }
    }

    private void processDialogResults(String postId, boolean isCanceled) {
        boolean resetSelections = true;
        if (isCanceled) {
            // Leave selections alone if user canceled.
            resetSelections = false;
            showCancelResponse();
        } else {
            showSuccessResponse(postId);
        }

        if (resetSelections) {
            init(null);
        }
    }

    private void showRejectedPermissionError() {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.error_dialog_button_text, null)
                .setTitle(R.string.error_dialog_title)
                .setMessage(R.string.rejected_publish_permission)
                .show();
    }

    /**
     * Notifies that the token has been updated.
     */
    private void tokenUpdated(AccessToken currentAccessToken) {
        if (pendingAnnounce) {
            Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
            if (currentAccessToken == null
                    || !currentAccessToken.getPermissions().contains(PERMISSION)) {
                pendingAnnounce = false;
                showRejectedPermissionError();
                return;
            }
            handleAnnounce();
        }
    }

    private void updateShareContent() {
        ShareContent content = createOpenGraphContent();
        if (content != null) {
            bMeetup.setEnabled(true);
            shareButton.setEnabled(true);
        } else {
            bMeetup.setEnabled(false);
            shareButton.setEnabled(false);
        }

        shareButton.setShareContent(content);
    }

    /**
     * Resets the view to the initial defaults.
     */
    private void init(Bundle savedInstanceState) {
        bMeetup.setEnabled(false);

        listElements = new ArrayList<FB_BaseListElement>();

        listElements.add(new ExerciseListElement(0));
        listElements.add(new LocationListElement(1));
        listElements.add(new PeopleListElement(2));
        listElements.add(new PhotoListElement(3));

        if (savedInstanceState != null) {
            for (FB_BaseListElement listElement : listElements) {
                listElement.restoreState(savedInstanceState);
            }
            pendingAnnounce = savedInstanceState.getBoolean(PENDING_ANNOUNCE_KEY, false);
        }
        ActionListAdapter listAdapter = new ActionListAdapter(
                getActivity(),
                R.id.selection_list,
                listElements);
        listAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                updateShareContent();
            }
        });
        listView.setAdapter(listAdapter);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            profilePictureView.setProfileId(accessToken.getUserId());
        }

        updateShareContent();
    }

    private void handleAnnounce() {
        Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
        if (!permissions.contains(PERMISSION)) {
            pendingAnnounce = true;
            requestPublishPermissions();
            return;
        } else {
            pendingAnnounce = false;
        }

        ShareApi.share(createOpenGraphContent(), shareCallback);
    }

    private ShareOpenGraphContent createOpenGraphContent() {
        ShareOpenGraphAction.Builder actionBuilder = createExerciseActionBuilder();

        boolean userGenerated = false;
//        if (photoUri != null) {
//            String photoUriString = photoUri.toString();
//            Pair<File, Integer> fileAndMinDimension = getImageFileAndMinDimension();
//            userGenerated = fileAndMinDimension.second >= USER_GENERATED_MIN_SIZE;
//
//            // If we have a content: URI, we can just use that URI, otherwise we'll need to add it
//            // as an attachment.
//            if (fileAndMinDimension != null && photoUri.getScheme().startsWith("content")) {
//                final SharePhoto actionPhoto = new SharePhoto.Builder()
//                        .setImageUrl(Uri.parse(photoUriString))
//                        .setUserGenerated(userGenerated)
//                        .build();
//                actionBuilder.putPhotoArrayList("image", new ArrayList<SharePhoto>() {{
//                    add(actionPhoto);
//                }});
//            }
//        }

        return new ShareOpenGraphContent.Builder()
                .setAction(actionBuilder.build())
                .setPreviewPropertyName("exercise")
                .build();
    } // createOpenGraphContent

    private File getTempPhotoStagingDirectory() {
        File photoDir = new File(getActivity().getCacheDir(), "photoFiles");
        photoDir.mkdirs();

        return photoDir;
    }

    private Pair<File, Integer> getImageFileAndMinDimension() {
        File photoFile = null;
        String photoUriString = photoUri.toString();
        if (photoUriString.startsWith("file://")) {
            photoFile = new File(photoUri.getPath());
        } else if (photoUriString.startsWith("content://")) {
            FileOutputStream photoOutputStream = null;
            InputStream contentInputStream = null;
            try {
                Uri photoUri = Uri.parse(photoUriString);
                photoFile = new File(
                        getTempPhotoStagingDirectory(),
                        URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8"));

                photoOutputStream = new FileOutputStream(photoFile);
                contentInputStream = getActivity()
                        .getContentResolver().openInputStream(photoUri);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = contentInputStream.read(buffer)) > 0) {
                    photoOutputStream.write(buffer, 0, len);
                }
            } catch (FileNotFoundException fnfe) {
                Log.e(TAG, "photo not found", fnfe);
            } catch (UnsupportedEncodingException uee) {
                Log.e(TAG, "bad photo name", uee);
            } catch (IOException ioe) {
                Log.e(TAG, "can't copy photo", ioe);
            } finally {
                try {
                    if (photoOutputStream != null) {
                        photoOutputStream.close();
                    }
                    if (contentInputStream != null) {
                        contentInputStream.close();
                    }
                } catch (IOException ioe) {
                    Log.e(TAG, "can't close streams");
                }
            }
        }

        if (photoFile != null) {
            InputStream is = null;
            try {
                is = new FileInputStream(photoFile);

                // We only want to get the bounds of the image, rather than load the whole thing.
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, options);

                return new Pair<>(
                        photoFile, Math.min(options.outWidth, options.outHeight));
            } catch (Exception e) {
                return null;
            } finally {
                Utility.closeQuietly(is);
            }
        }
        return null;
    } // getImageFileAndMinDimension

    private ShareOpenGraphAction.Builder createExerciseActionBuilder() {
        ShareOpenGraphAction.Builder builder = new ShareOpenGraphAction.Builder()
                .setActionType(EX_ACTION_TYPE);
        for (FB_BaseListElement element : listElements) {
            element.populateOpenGraphAction(builder);
        }

        return builder;
    }

    private void requestPublishPermissions() {
        LoginManager.getInstance()
                .setDefaultAudience(DefaultAudience.FRIENDS)
                .logInWithPublishPermissions(this, Arrays.asList(PERMISSION));
    }

    private void showSuccessResponse(String postId) {
        String dialogBody;
        if (postId != null) {
            dialogBody = String.format(getString(R.string.result_dialog_text_with_id), postId);
        } else {
            dialogBody = getString(R.string.result_dialog_text_default);
        }
        showResultDialog(dialogBody);
    }

    private void showCancelResponse() {
        showResultDialog(getString(R.string.result_dialog_text_canceled));
    }

    private void showResultDialog(String dialogBody) {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.result_dialog_button_text, null)
                .setTitle(R.string.result_dialog_title)
                .setMessage(dialogBody)
                .show();
    }

    private void handleError(GraphResponse response) {
        FacebookRequestError error = response.getError();
        DialogInterface.OnClickListener listener = null;
        String dialogBody = null;

        if (error == null) {
            dialogBody = getString(R.string.error_dialog_default_text);
        } else {
            switch (error.getCategory()) {
                case LOGIN_RECOVERABLE:
                    // There is a login issue that can be resolved by the LoginManager.
                    LoginManager.getInstance().resolveError(this, response);
                    return;

                case TRANSIENT:
                    dialogBody = getString(R.string.error_transient);
                    break;

                case OTHER:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
                    break;
            }
        }

        String title = error.getErrorUserTitle();
        String message = error.getErrorUserMessage();
        if (message == null) {
            message = dialogBody;
        }
        if (title == null) {
            title = getResources().getString(R.string.error_dialog_title);
        }

        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.error_dialog_button_text, listener)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    private void startPickerActivity(Uri data, int requestCode) {
        Intent intent = new Intent();
        intent.setData(data);
        intent.setClass(getActivity(), FB_PickerActivity.class);
        startActivityForResult(intent, requestCode);
    }

    private class ExerciseListElement extends FB_BaseListElement
    {
        private static final String EXERCISE_KEY = "exericse";
        private static final String EXERCISE_URL_KEY = "exercise_url";

        private final String[] exerciseChoices;
        private final String[] exerciseUrls;
        private String exerciseChoiceUrl = null;
        private String exerciseChoice = null;

        public ExerciseListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.add_exercise_black),
                    getActivity().getResources().getString(R.string.action_exercising),
                    null,
                    requestCode);
            exerciseChoices = getActivity().getResources().getStringArray(R.array.exercise_types);
            exerciseUrls = getActivity().getResources().getStringArray(R.array.exercise_og_urls);
        }

        @Override
        protected View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showExerciseOptions();
                }
            };
        }

        @Override
        protected void populateOpenGraphAction(ShareOpenGraphAction.Builder actionBuilder) {
            if (exerciseChoice != null && exerciseChoice.length() > 0) {
//                if (exerciseChoiceUrl != null && exerciseChoiceUrl.length() > 0) {
//                    actionBuilder.putString("exercise", exerciseChoiceUrl);
//                } else {
                    ShareOpenGraphObject exObject = new ShareOpenGraphObject.Builder()
                            .putString("og:type", EXERCISE_OBJECT_TYPE)
                            .putString("og:title", exerciseChoice)
                            .build();
                    actionBuilder.putObject("exercise", exObject);
//                }
            }
        }

        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            if (exerciseChoice != null && exerciseChoiceUrl != null) {
                bundle.putString(EXERCISE_KEY, exerciseChoice);
                bundle.putString(EXERCISE_URL_KEY, exerciseChoiceUrl);
            }
        }

        @Override
        protected boolean restoreState(Bundle savedState) {
            String food = savedState.getString(EXERCISE_KEY);
            String foodUrl = savedState.getString(EXERCISE_URL_KEY);
            if (food != null && foodUrl != null) {
                exerciseChoice = food;
                exerciseChoiceUrl = foodUrl;
                setExerciseText();
                return true;
            }
            return false;
        }

        private void showExerciseOptions() {
            String title = getActivity().getResources().getString(R.string.select_exercise);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title).
                    setCancelable(true).
                    setItems(exerciseChoices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            exerciseChoiceUrl = exerciseUrls[i];
                            if (exerciseChoiceUrl.length() == 0) {
                                getCustomExercise();
                            } else {
                                exerciseChoice = exerciseChoices[i];
                                setExerciseText();
                                notifyDataChanged();
                            }
                        }
                    });
            builder.show();
        }

        private void getCustomExercise() {
            String title = getActivity().getResources().getString(R.string.enter_exercise);
            final EditText input = new EditText(getActivity());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)
                    .setCancelable(true)
                    .setView(input)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            exerciseChoice = input.getText().toString();
                            setExerciseText();
                            notifyDataChanged();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            AlertDialog dialog = builder.create();
            // always popup the keyboard when the alert dialog shows
            dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
        }

        private void setExerciseText() {
            if (exerciseChoice != null && exerciseChoice.length() > 0) {
                setText2(exerciseChoice);
                bMeetup.setEnabled(true);
            } else {
                setText2(getActivity().getResources().getString(R.string.action_exercising_default));
                bMeetup.setEnabled(false);
            }
        }
    } // ExerciseListElement

    private class PeopleListElement extends FB_BaseListElement
    {

        private static final String FRIENDS_KEY = "friends";

        private List<JSONObject> selectedUsers;

        public PeopleListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.add_friends_black),
                    getActivity().getResources().getString(R.string.action_people),
                    null,
                    requestCode);
        }

        @Override
        protected View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        startPickerActivity(FB_PickerActivity.FRIEND_PICKER, getRequestCode());
                    } else {
//                        activity.showSplashFragment();
                    }
                }
            };
        }

        @Override
        protected void onActivityResult(Intent data) {
            selectedUsers = ((HeartwareApplication) getActivity().getApplication())
                    .getSelectedUsers();
            setUsersText();
            notifyDataChanged();
        }

        @Override
        protected void populateOpenGraphAction(ShareOpenGraphAction.Builder actionBuilder) {
            if (selectedUsers != null && !selectedUsers.isEmpty()) {
                String tags = "";
                for (JSONObject user : selectedUsers) {
                    tags += "," + user.optString("id");
                }
                tags = tags.substring(1);
                actionBuilder.putString("tags", tags);
            }
        }

        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            if (selectedUsers != null) {
                bundle.putByteArray(FRIENDS_KEY, getByteArray(selectedUsers));
            }
        }

        @Override
        protected boolean restoreState(Bundle savedState) {
            byte[] bytes = savedState.getByteArray(FRIENDS_KEY);
            if (bytes != null) {
                selectedUsers = restoreByteArray(bytes);
                setUsersText();
                return true;
            }
            return false;
        }

        private void setUsersText() {
            String text = null;
            if (selectedUsers != null) {
                if (selectedUsers.size() == 1) {
                    text = String.format(getResources().getString(R.string.single_user_selected),
                            selectedUsers.get(0).optString("name"));
                } else if (selectedUsers.size() == 2) {
                    text = String.format(getResources().getString(R.string.two_users_selected),
                            selectedUsers.get(0).optString("name"),
                            selectedUsers.get(1).optString("name"));
                } else if (selectedUsers.size() > 2) {
                    text = String.format(getResources().getString(R.string.multiple_users_selected),
                            selectedUsers.get(0).optString("name"), (selectedUsers.size() - 1));
                }
            }
            if (text == null) {
                text = getResources().getString(R.string.action_people_default);
            }
            setText2(text);
        }

        private byte[] getByteArray(List<JSONObject> users) {
            // convert the list of GraphUsers to a list of String where each element is
            // the JSON representation of the GraphUser so it can be stored in a Bundle
            List<String> usersAsString = new ArrayList<String>(users.size());

            for (JSONObject user : users) {
                usersAsString.add(user.toString());
            }
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                new ObjectOutputStream(outputStream).writeObject(usersAsString);
                return outputStream.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, "Unable to serialize users.", e);
            }
            return null;
        }

        private List<JSONObject> restoreByteArray(byte[] bytes) {
            try {
                @SuppressWarnings("unchecked")
                List<String> usersAsString =
                        (List<String>) (new ObjectInputStream(
                                new ByteArrayInputStream(bytes))).readObject();
                if (usersAsString != null) {
                    List<JSONObject> users = new ArrayList<JSONObject>(usersAsString.size());
                    for (String user : usersAsString) {
                        users.add(new JSONObject(user));
                    }
                    return users;
                }
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            } catch (IOException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            } catch (JSONException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            }
            return null;
        }
    } // PeopleListElement

    private class LocationListElement extends FB_BaseListElement
    {
        private static final String PLACE_KEY = "place";

        private JSONObject selectedPlace = null;

        public LocationListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.add_location_black),
                    getActivity().getResources().getString(R.string.action_location),
                    null,
                    requestCode);
        }

        @Override
        protected View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        startPickerActivity(FB_PickerActivity.PLACE_PICKER, getRequestCode());
                    } else {
//                        activity.showSplashFragment();
                    }
                }
            };
        }

        @Override
        protected void onActivityResult(Intent data) {
            selectedPlace = ((HeartwareApplication) getActivity().getApplication())
                    .getSelectedPlace();
            setPlaceText();
            notifyDataChanged();
        }

        @Override
        protected void populateOpenGraphAction(ShareOpenGraphAction.Builder actionBuilder) {
            if (selectedPlace != null) {
                actionBuilder.putString("place", selectedPlace.optString("id"));
            }
        }

        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            if (selectedPlace != null) {
                bundle.putString(PLACE_KEY, selectedPlace.toString());
            }
        }

        @Override
        protected boolean restoreState(Bundle savedState) {
            String place = savedState.getString(PLACE_KEY);
            if (place != null) {
                try {
                    selectedPlace = new JSONObject(place);
                    setPlaceText();
                    return true;
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to deserialize place.", e);
                }
            }
            return false;
        }

        private void setPlaceText() {
            String text = selectedPlace != null ? selectedPlace.optString("name") : null;
            if (text == null) {
                text = getResources().getString(R.string.action_location_default);
            }
            setText2(text);
        }
    } // LocationListElement

    private class PhotoListElement extends FB_BaseListElement
    {
        private static final int CAMERA = 0;
        private static final int GALLERY = 1;
        private static final String PHOTO_URI_KEY = "photo_uri";
        private static final String TEMP_URI_KEY = "temp_uri";
        private static final String FILE_PREFIX = "heartware_img_";
        private static final String FILE_SUFFIX = ".jpg";

        private Uri tempUri = null;

        public PhotoListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.add_photo_black),
                    getActivity().getResources().getString(R.string.action_photo),
                    null,
                    requestCode);
            photoUri = null;
            photoThumbnail.setImageDrawable(
                    getActivity().getResources().getDrawable(R.drawable.placeholder_image_black));
            photoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPhotoChoice();
                }
            });
        }

        @Override
        protected View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPhotoChoice();
                }
            };
        }

        @Override
        protected void onActivityResult(Intent data) {
            if (tempUri != null) {
                photoUri = tempUri;
            } else if (data != null) {
                photoUri = data.getData();
            }
            setPhotoThumbnail();
            setPhotoText();
        }

        @Override
        protected void populateOpenGraphAction(ShareOpenGraphAction.Builder actionBuilder) {
        }

        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            if (photoUri != null) {
                bundle.putParcelable(PHOTO_URI_KEY, photoUri);
            }
            if (tempUri != null) {
                bundle.putParcelable(TEMP_URI_KEY, tempUri);
            }
        }

        @Override
        protected boolean restoreState(Bundle savedState) {
            photoUri = savedState.getParcelable(PHOTO_URI_KEY);
            tempUri = savedState.getParcelable(TEMP_URI_KEY);
            setPhotoText();
            return true;
        }

        private void showPhotoChoice() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            CharSequence camera = getResources().getString(R.string.action_photo_camera);
            CharSequence gallery = getResources().getString(R.string.action_photo_gallery);
            builder.setCancelable(true).
                    setItems(new CharSequence[]{camera, gallery},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == CAMERA) {
                                        startCameraActivity();
                                    } else if (i == GALLERY) {
                                        startGalleryActivity();
                                    }
                                }
                            });
            builder.show();
        }

        private void setPhotoText() {
            if (photoUri == null) {
                setText2(getResources().getString(R.string.action_photo_default));
            } else {
                setText2(getResources().getString(R.string.action_photo_ready));
            }
        }

        private void setPhotoThumbnail() {
            // The selected image may be too big so scale here
            if (runningImageTask != null &&
                    runningImageTask.getStatus() != AsyncTask.Status.FINISHED) {
                runningImageTask.cancel(true);
            }

            runningImageTask = new ScaleAndSetImageTask(photoUri);
            runningImageTask.execute();
        }

        private void startCameraActivity() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            tempUri = getTempUri();
            if (tempUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
            }
            startActivityForResult(intent, getRequestCode());
        }

        private void startGalleryActivity() {
            tempUri = null;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            String selectPicture = getResources().getString(R.string.select_picture);
            startActivityForResult(Intent.createChooser(intent, selectPicture), getRequestCode());
        }

        private Uri getTempUri() {
            String imgFileName = FILE_PREFIX + System.currentTimeMillis() + FILE_SUFFIX;

            // Note: on an emulator, you might need to create the "Pictures" directory in
            //         /mnt/sdcard first
            //       % adb shell
            //       % mkdir /mnt/sdcard/Pictures
            File image = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    imgFileName);
            return Uri.fromFile(image);
        }
    } // PhotoListElement

    private class ActionListAdapter extends ArrayAdapter<FB_BaseListElement>
    {
        private List<FB_BaseListElement> listElements;

        public ActionListAdapter(
                Context context, int resourceId, List<FB_BaseListElement> listElements) {
            super(context, resourceId, listElements);
            this.listElements = listElements;
            for (int i = 0; i < listElements.size(); i++) {
                listElements.get(i).setAdapter(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getActivity().getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.fb_listitem, null);
            }

            FB_BaseListElement listElement = listElements.get(position);
            if (listElement != null) {
                view.setOnClickListener(listElement.getOnClickListener());
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                if (icon != null) {
                    icon.setImageDrawable(listElement.getIcon());
                }
                if (text1 != null) {
                    text1.setText(listElement.getText1());
                }
                if (text2 != null) {
                    if (listElement.getText2() != null) {
                        text2.setVisibility(View.VISIBLE);
                        text2.setText(listElement.getText2());
                    } else {
                        text2.setVisibility(View.GONE);
                    }
                }
            }
            return view;
        }
    } // ActionListAdapter

    private class ScaleAndSetImageTask extends AsyncTask<Void, Void, Bitmap>
    {
        private final Uri uri;

        public ScaleAndSetImageTask(Uri uri) {
            this.uri = uri;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        FacebookSdk.getApplicationContext().getContentResolver(), uri);
                if (bitmap.getHeight() > MAX_TEXTURE_SIZE || bitmap.getWidth() > MAX_TEXTURE_SIZE) {
                    // We need to scale the image
                    float scale = Math.min(
                            MAX_TEXTURE_SIZE / bitmap.getHeight(),
                            MAX_TEXTURE_SIZE / bitmap.getWidth());
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);
                    bitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            0,
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            matrix,
                            false);
                }
                return bitmap;
            } catch (Exception ex) {
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                photoThumbnail.setImageBitmap(result);
            } else {
                // If we fail just try to set the image from the uri
                photoThumbnail.setImageURI(photoUri);
            }
        }
    } // ScaleAndSetImageTask
} // FriendsActivity class
