package com.example.masterchef.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.masterchef.R;
import com.example.masterchef.ui.Validation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout lay_email,lay_password;
    private TextInputEditText emailEt,passwordEt;
    private MaterialButton signInBtn,signUpBtn;

    //strings
    private String username,email,password;

    private Validation validation;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        validation = new Validation(this);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);

        init();
        signInBtn.setOnClickListener(this);
    }

    private void init(){

        lay_email = findViewById(R.id.layoutEmail);
        lay_password = findViewById(R.id.layoutPassword);

        emailEt = findViewById(R.id.email_et);
        passwordEt = findViewById(R.id.password_et);

        signInBtn = findViewById(R.id.signIn_btn);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.signIn_btn:
                inputValidation();
                break;
        }

    }

    private void inputValidation() {



        if (!validation.emailValidation(emailEt,lay_email))
        {
            return;
        }
        else {
            loginUser();
        }
    }

    private void loginUser() {
        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();

        progressDialog.setMessage("Logging In....");
        progressDialog.show();

        //login with email and password in firebase
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeMeOnline() {
        progressDialog.setMessage("Checking User.....");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online","true");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkUserType() {
        // check admin or user

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                           String accountType = ""+ds.child("accountType").getValue();

                           if (accountType.equals("user")){
                               progressDialog.dismiss();
                               startActivity(new Intent(SignInActivity.this, MainActivity.class));
                               finish();
                           }else {
                               progressDialog.dismiss();
                               startActivity(new Intent(SignInActivity.this, AdminActivity.class));
                               finish();
                           }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}