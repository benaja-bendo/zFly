package cg.essengogroup.zfly.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import cg.essengogroup.zfly.R;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email,password;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    public static final String CHANNEL_ID = "simplified_coding";
    private static final String CHANNEL_NAME = "Simplified Coding";
    private static final String CHANNEL_DESC = "Simplified Coding Notifications";


    @Override
    public void onStart() {
        super.onStart();
       if (mAuth.getCurrentUser()!=null){
           startActivity(new Intent(LoginActivity.this,AccueilActivity.class));
           finish();
       }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth=FirebaseAuth.getInstance();

        progressBar=findViewById(R.id.progress);
        email=findViewById(R.id.login);
        password=findViewById(R.id.password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        TextView registerBtn=findViewById(R.id.goToRegister);
        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            finish();
        });

        CardView cardView=findViewById(R.id.btnCnx);
        cardView.setOnClickListener(v ->signInMethode());
    }


    private void signInMethode(){
        String emailValue=email.getText().toString().trim();
        String passwordValue=password.getText().toString().trim();

        if (TextUtils.isEmpty(emailValue)){
            email.setError("veuillez entré votre adresse mail");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()){
            email.setError("veuillez entré un adresse mail valide");
            email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passwordValue)){
            password.setError("veuillez entré un mot de passe");
            password.requestFocus();
            return;
        }

        if (password.length()<6){
            password.setError("veuillez entré un mot de passe superieur ou égal à 6 caracteres ");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(task -> {

            if (progressBar.getVisibility()==View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }

           if (task.isSuccessful()){
               startActivity(new Intent(getApplicationContext(),AccueilActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
           }else {
               Toast.makeText(LoginActivity.this, task.getException().getMessage()+"", Toast.LENGTH_SHORT).show();
           }
        });


    }
}
