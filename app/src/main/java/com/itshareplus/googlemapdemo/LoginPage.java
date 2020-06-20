package com.itshareplus.googlemapdemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

    private TextView title;
    private EditText loginEmail;
    private EditText loginPassword;
    private TextView forgotPassword;
    private Button btnLogin;
    private Button btnCancel;

    private Button loginWithGoogle;
    private TextView textViewRegister;
    private EditText holdName;
    private EditText holdAddress;
    private Button saveGoogleInfo;
    private EditText holdProvider;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRefMember;
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 1;
    final List<String> memberEmail = new ArrayList<>();
    final List<String> memberProvider = new ArrayList<>();
    final List<String> memberEmailForGoogle = new ArrayList<>();
    final List<String> memberProviderForGoogle = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        title = (TextView) findViewById(R.id.title);
        loginEmail = (EditText) findViewById(R.id.getLoginEmail);
        loginPassword = (EditText) findViewById(R.id.getLoginPassword);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);

        loginWithGoogle = (Button) findViewById(R.id.loginWithGoogle);
        saveGoogleInfo = (Button) findViewById(R.id.saveGoogleInfo);
        holdName =(EditText) findViewById(R.id.holdName);
        holdAddress = (EditText) findViewById(R.id.holdAddress);
        findViewById(R.id.holdName).setVisibility(View.GONE);
        findViewById(R.id.holdAddress).setVisibility(View.GONE);
        findViewById(R.id.saveGoogleInfo).setVisibility(View.GONE);

        holdProvider = (EditText) findViewById(R.id.holdProvider);
        findViewById(R.id.holdProvider).setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        databaseRefMember = FirebaseDatabase.getInstance().getReference("member");
        progressDialog = new ProgressDialog(this);


        forgotPassword.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        loginWithGoogle.setOnClickListener(this);
        saveGoogleInfo.setOnClickListener(this);


        //google 登入
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginPage.this, "錯誤" , Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //google 登入結束
    }

    private void userlogin() {
        final String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "請輸入您的信箱", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this , "請輸入您的密碼", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("登入中...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginPage.this, MapsActivity.class));
                        } else {

                            checkIfAuthExist(email);
                            memberEmail.clear();
                            memberProvider.clear();
                        }
                    }
                });
    }

    private void resetPassword(){
        final String email = loginEmail.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "請輸入您的信箱!", Toast.LENGTH_SHORT).show();
            //Stopping the function
            return;
        }

//        findViewById(R.id.forgotPassword).setVisibility(View.GONE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(LoginPage.this, "請至信箱重設密碼",Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            final GoogleSignInAccount account = result.getSignInAccount();
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(account);
            } else {}

        }
    }


    private void googleSignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        String providers = String.valueOf(user.getProviders());
                        String removeMark = providers.replace("[" , " ");
                        removeMark = removeMark.replace("]" , " ");
                        holdProvider.setText(removeMark);
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());

                        String googleUserName = account.getDisplayName();
                        holdName.setText(googleUserName);
                        findViewById(R.id.getLoginEmail).setVisibility(View.GONE);
                        findViewById(R.id.getLoginPassword).setVisibility(View.GONE);
                        findViewById(R.id.forgotPassword).setVisibility(View.GONE);
                        findViewById(R.id.btnLogin).setVisibility(View.GONE);
                        findViewById(R.id.btnCancel).setVisibility(View.GONE);
                        findViewById(R.id.loginWithGoogle).setVisibility(View.GONE);

                        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference AddressRef = RootRef.child("member").child(user.getUid()).child("address");
                        AddressRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String checkAddress = dataSnapshot.getValue(String.class);
                                if(checkAddress == null){
                                    title.setText("歡迎，" + holdName.getText().toString().trim() );
                                    holdAddress.setVisibility(View.VISIBLE);
                                    saveGoogleInfo.setVisibility(View.VISIBLE);
                                } else {
                                    startActivity(new Intent(LoginPage.this , MapsActivity.class));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        if(!task.isSuccessful()){
                            Log.w("TAG", "signInWithCredential", task.getException());
                            Toast.makeText(LoginPage.this, "Authentication failed. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void saveInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
        String name = holdName.getText().toString().trim();
        String address = holdAddress.getText().toString().trim();
        String provider = holdProvider.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this , "請輸入您的姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(address)){
            Toast.makeText(this , "請輸入您的地址", Toast.LENGTH_SHORT).show();
            return;
        }


        MemberInfo memberInfo = new MemberInfo(name , address , user.getEmail() ,provider);
        databaseRefMember.child(user.getUid()).setValue(memberInfo);

        Toast.makeText(this, "資料儲存成功",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginPage.this , MapsActivity.class));
    }

    @Override
    public void onStart(){
        super.onStart();

        findViewById(R.id.forgotPassword).setVisibility(View.VISIBLE);
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(LoginPage.this , MapsActivity.class));
        }

    }

    @Override
    public boolean onKeyDown(int keyCode , KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginPage.this);
            dialog.setTitle("返回上一頁");
            dialog.setMessage("請透過按鈕 '返回' 回到上一頁");
            dialog.setPositiveButton("好", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();

        }
        return super.onKeyDown(keyCode,event);
    }

    private void checkIfAuthExist(final String email) {

        databaseRefMember.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot memberSnapShot : dataSnapshot.getChildren()) {
                    MemberInfo memberinfo = memberSnapShot.getValue(MemberInfo.class);

                    String userEmail = memberinfo.getEmail();
                    String userProvider = memberinfo.getProvider();
                    memberEmail.add(userEmail);
                    memberProvider.add(userProvider);
                }
                Log.d("email", String.valueOf(memberEmail));
                Log.d("provider", String.valueOf(memberProvider));

                if(memberEmail.contains(email)){

                    int index = memberEmail.indexOf(email);
                    Log.d("index " , String.valueOf(index));
                    String providerOfUser = memberProvider.get(index);
                    String passwordStringToCompare = "password";
                    String googleStringToCompare = "google.com";
                    Log.d("Provider of user" , providerOfUser);
                    if(providerOfUser.equals(googleStringToCompare)){

                        Log.d("msg2" , "該帳已連結至google");
                        AlertDialog.Builder warmningDialog = new AlertDialog.Builder(LoginPage.this);
                        warmningDialog.setTitle("注意");
                        warmningDialog.setMessage("該信箱已和google連結，請至登入頁面，透過google登入");
                        warmningDialog.setPositiveButton("好", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        warmningDialog.show();
                    }

                } else{

                    AlertDialog.Builder warmningDialog = new AlertDialog.Builder(LoginPage.this);
                    warmningDialog.setTitle("注意");
                    warmningDialog.setMessage("你的信箱或是密碼有誤!請再試一次");
                    warmningDialog.setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    warmningDialog.show();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    @Override
    public void onClick(View view) {

        if(view == btnLogin){
            userlogin();
        }

        if(view == forgotPassword){
            resetPassword();
        }

        if(view == textViewRegister){
            startActivity(new Intent(LoginPage.this, RegisterPage.class));
        }

        if(view ==btnCancel){
            startActivity(new Intent(LoginPage.this , MapsActivity.class));
        }

        if(view == loginWithGoogle){
            textViewRegister.setVisibility(View.GONE);
            forgotPassword.setVisibility(View.GONE);
            googleSignIn();
        }

        if(view == saveGoogleInfo){
            saveInformation();
        }

    }
}
