package com.example.mytrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignFragment extends Fragment {
    private FirebaseAuth mAuth = null;
    private CallbackManager mCallbackManager;
    private LoginButton facebook;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sign_fragment, container, false);
        define_signIn_layout(view);

        return view;
    }


    public void define_signIn_layout(View view){
        //read attributes and handle it

        SignInButton google = view.findViewById(R.id.google_SignBtn);
        TextView textView = (TextView) google.getChildAt(0);    //for changing the google signIn text
        textView.setText("sign in with Google");

        facebook = view.findViewById(R.id.facebook_signBtn);
        facebook.setFragment(this);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn_with_google();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn_with_facebook();
            }
        });
    }

    public void signIn_with_google(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext() , gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public void firebase_authWith_google(Intent data){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth = FirebaseAuth.getInstance();

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity() , new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() ) {
                                // Sign in success, update UI with the signed-in user's information
                                print_message("signed in successfully");

                                show_mainPage();
                            } else {
                                // If sign in fails, display a message to the user.
                                print_message("sign in failed, try again later");
                            }
                        }
                    });

        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            print_message("Google sign in failed");
        }
    }


    public void signIn_with_facebook(){

        mCallbackManager = CallbackManager.Factory.create();
        facebook.setReadPermissions("email", "public_profile");

        facebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                firebase_authWith_facebook(loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {
                print_message("Facebook login is canceled");
            }

            @Override
            public void onError(FacebookException error) {
                print_message("sign in failed, try again later");
            }
        });
    }


    public void firebase_authWith_facebook(AccessToken token){

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken() );
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity() , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            print_message("signed in successfully");

                            show_mainPage();

                        } else {
                            // If sign in fails, display a message to the user.
                            print_message("sign in failed, try again later");
                        }
                    }
                });
    }


    public void call_callBack_manager(int requestCode, int resultCode, @Nullable Intent data){
        if (mCallbackManager != null)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void print_message(String mess){
        Toast.makeText(getActivity() ,mess ,Toast.LENGTH_LONG).show();
    }


    public void show_mainPage(){

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.main_fragment ,new MainFragment() )
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {     //signed with Google
            firebase_authWith_google(data);
        }
        else {      //signed with Facebook

            //Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}