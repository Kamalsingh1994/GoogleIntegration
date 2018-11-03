package com.example.cool.googleintegration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout prof_section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView name, email;
    private ImageView prof_pic;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prof_section = findViewById(R.id.profile_box);
        SignOut = findViewById(R.id.logout);
        SignIn = findViewById(R.id.login);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        prof_pic = findViewById(R.id.profile_id);

        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        prof_section.setVisibility(View.GONE);

        //to configure Google Sign-In to request users’ ID and basic profile information,
        // create a GoogleSignInOptions object with the DEFAULT_SIGN_IN parameter.
        //To request users’ email addresses as well, create the GoogleSignInOptions object with the requestEmail option
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login:
                SignIn();
                break;
            case R.id.logout:
                SignOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void SignIn() {
//        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//        startActivity(intent);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent,REQ_CODE);
    }

    private void SignOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback <Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updatUI(false);

            }

        });
    }

    private void handleResult(GoogleSignInResult result) {
        Log.d("Data",result.toString());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            Log.d("test1",account.getDisplayName());
            String Name = account.getDisplayName();
            String Email = account.getEmail();
            String Img_url = account.getPhotoUrl().toString();

            Log.d("Name",Name);
            Log.d("Email",Email);
            name.setText(Name);
            email.setText(Email);
            Glide.with(this).load(Img_url).into(prof_pic);
            updatUI(true);
        } else {
            updatUI(false);
        }

    }

    private void updatUI(boolean isLogin) {
        if (isLogin) {
            prof_section.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.GONE);
        }
        else {
            prof_section.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("Data1",result.toString());
            handleResult(result);
        }
    }
}
