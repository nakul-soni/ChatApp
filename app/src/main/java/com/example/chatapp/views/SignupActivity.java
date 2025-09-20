package com.example.chatapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    EditText newuserNameET,newPasswordET,newEmailET, reEnterPasswordET;
    Button registerBtn;
    TextView loginLink;

    //FireBase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase Connection
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        newuserNameET = findViewById(R.id.new_username_edt);
        newPasswordET = findViewById(R.id.new_password_edt);
        newEmailET = findViewById(R.id.new_email_edt);
        registerBtn = findViewById(R.id.register_btn);
        loginLink = findViewById(R.id.loginLink);

        firebaseAuth =FirebaseAuth.getInstance();

        //Listening for the changes in the authentication state and respond accordingly
        //when the state changed(if user sign in or sign out)
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                //Check if the user is logged in or not
                if (currentUser != null) {
                    //The user is already signed in
                } else {
                    //The user is signed out
                }
            }
        };

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ( !TextUtils.isEmpty(newEmailET.getText().toString())
                        &&!TextUtils.isEmpty(newuserNameET.getText().toString())
                        &&!TextUtils.isEmpty(newPasswordET.getText().toString())
                )
                {
                    String email = newEmailET.getText().toString().trim();
                    String name = newuserNameET.getText().toString().trim();
                    String password = newPasswordET.getText().toString().trim();
                    CreateNewAccount(name,password,email);
                }else{
                    Toast.makeText(SignupActivity.this, "No Empty Fields are Allowed", Toast.LENGTH_SHORT).show();
                }

            }

            public void CreateNewAccount(
                    String name,
                    String password,
                    String email
            ){

                //Checking if the strings we initialized are not empty using !(NOT operator) and
                //TextUtils class
                //Because we are not going to create an account with empty email,name,or,password.
                if (!TextUtils.isEmpty(name)
                        &&!TextUtils.isEmpty(password)
                        &&!TextUtils.isEmpty(email)){
                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this, "The account is created successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, GroupsActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(SignupActivity.this, "Account Already Exists", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

     loginLink.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
             startActivity(intent);
             finish();
         }
     });
    }


}
