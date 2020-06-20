package com.itshareplus.googlemapdemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReviewPage extends AppCompatActivity implements View.OnClickListener {

    private Button submitBtn;
    private EditText problemText;
    private EditText emailText;
    private ProgressDialog submitSuccess;
    private Button cancelBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_page);

        mAuth = FirebaseAuth.getInstance();

        problemText = (EditText) findViewById(R.id.problem_text);
        emailText = (EditText) findViewById(R.id.email_text);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == submitBtn){

            String complaint = problemText.getText().toString().trim();
            String email = emailText.getText().toString().trim();

            if(TextUtils.isEmpty(complaint)){
                Toast.makeText(this , "意見回饋欄位不能空白!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(email)){
                Toast.makeText(this , "聯絡信箱欄位不能空白!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(ReviewPage.this , "已成功送出！" , Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ReviewPage.this , MapsActivity.class));
        }
        else if (view == cancelBtn){
            startActivity(new Intent(ReviewPage.this , MapsActivity.class));
        }
    }
    @Override
    public boolean onKeyDown(int keyCode , KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(ReviewPage.this);
            dialog.setTitle("返回上一頁");
            dialog.setMessage("請透過按鈕 '取消按鈕' 回到上一頁");
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){

            emailText.setText(user.getEmail());
        }
    }
}

