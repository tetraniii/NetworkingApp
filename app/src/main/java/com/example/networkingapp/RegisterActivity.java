package com.example.networkingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextPasswordRep;
    SwitchCompat typeOfUserSwitch;
    Button buttonReg;
    FirebaseAuth mAuth;
    TextView textView;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPasswordRep = findViewById(R.id.repeatPassword);
        typeOfUserSwitch = findViewById(R.id.typeOfUser);
        buttonReg = findViewById(R.id.btn_Register);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                database = FirebaseDatabase.getInstance();
                String email, password, repeatPassword;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                repeatPassword = String.valueOf(editTextPasswordRep.getText());
                boolean userIsStartup;
                userIsStartup = typeOfUserSwitch.isChecked();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this,"Введите email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Введите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() > 15 || password.length() < 8)
                {
                    Toast.makeText(RegisterActivity.this,"Пароль должен содержать от 8 до 20 символов", Toast.LENGTH_SHORT).show();
                    return;
                }
                String upperCaseChars = "(.*[A-Z].*)";
                if (!password.matches(upperCaseChars ))
                {
                    Toast.makeText(RegisterActivity.this,"Пароль должен содержать как минимум одну букву верхнего регистра", Toast.LENGTH_SHORT).show();
                    return;
                }
                String lowerCaseChars = "(.*[a-z].*)";
                if (!password.matches(lowerCaseChars ))
                {
                    Toast.makeText(RegisterActivity.this,"Пароль должен содержать как минимум одну букву нижнего регистра", Toast.LENGTH_SHORT).show();
                    return;
                }
                String numbers = "(.*[0-9].*)";
                if (!password.matches(numbers ))
                {
                    Toast.makeText(RegisterActivity.this,"Пароль должен содержать как минимум одну цифру", Toast.LENGTH_SHORT).show();
                    return;
                }
                String specialChars = "(.*[@,#,$,%].*$)";
                if (!password.matches(specialChars ))
                {
                    Toast.makeText(RegisterActivity.this,"Пароль должен содержать как минимум один из данных символов: @ # $ %", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(repeatPassword)){
                    Toast.makeText(RegisterActivity.this,"Повторите пароль", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!repeatPassword.equals(password)){
                    Toast.makeText(RegisterActivity.this,"Пароли не совпадают", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                   HashMap<String, Object> map = new HashMap<>();
                                    map.put("id", currentUser.getUid());
                                    map.put("email", email);
                                    map.put("name",""); //will be added later
                                    map.put("description",""); //will be added later
                                    map.put("image", ""); //will be added later
                                    map.put("userIsStartup", userIsStartup);
                                    map.put("number", "");//will be added later
                                    map.put("additionalNumber", "");//will be added later
                                    map.put("websiteLink", "");//will be added later
                                    map.put("contactEmail", "");//will be added later

                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(currentUser.getUid()).setValue(map);


                                    Toast.makeText(RegisterActivity.this, "Аккаунт успешно создан",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Введите действительный email или аккаунт с таким email уже существует",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
}
