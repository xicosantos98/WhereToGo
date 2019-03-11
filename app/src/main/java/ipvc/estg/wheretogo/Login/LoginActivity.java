package ipvc.estg.wheretogo.Login;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import ipvc.estg.wheretogo.Admin.AdminMapActivity;
import ipvc.estg.wheretogo.Classes.MyUser;
import ipvc.estg.wheretogo.Classes.TipoUser;
import ipvc.estg.wheretogo.R;
import ipvc.estg.wheretogo.Tecnico.TecMapActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email, password;
    ProgressBar progressBar;
    Button btnLogin;
    DatabaseReference refUsers, refLocalizacao, refTipo, refServico;
    MyUser userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        refUsers = FirebaseDatabase.getInstance().getReference("users");
        refLocalizacao = FirebaseDatabase.getInstance().getReference("localizacao");
        refTipo = FirebaseDatabase.getInstance().getReference("tipo_servico");
        refServico = FirebaseDatabase.getInstance().getReference("servico");

        mAuth = FirebaseAuth.getInstance();
        //FirebaseAuth.getInstance().signOut();

        email = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progress_bar);

        email.setText("franciscosantos@ipvc.pt");
        password.setText("12345678");



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().length() != 0 && password.getText().length() != 0) {
                    loginUser(email.getText().toString(), password.getText().toString());
                }

            }
        });


        //createAccount("franciscosantos@ipvc.pt", "12345678");

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            // User já está logado
            FirebaseUser u = mAuth.getCurrentUser();
            Toast.makeText(this, u.getEmail(), Toast.LENGTH_SHORT).show();
            Query query = FirebaseDatabase.getInstance().getReference("users")
                    .orderByChild("id")
                    .equalTo(u.getUid());

            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }


    public void loginUser(String email, String password) {

        btnLogin.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    final FirebaseUser user = mAuth.getCurrentUser();

                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            Log.d("TOKEN", "TOKEN: " + task.getResult().getToken());
                            refUsers.child(user.getUid()).child("token").setValue(task.getResult().getToken());
                        }
                    });

                    Query query = FirebaseDatabase.getInstance().getReference("users")
                            .orderByChild("id")
                            .equalTo(user.getUid());

                    query.addListenerForSingleValueEvent(valueEventListener);


                } else {
                    Toast.makeText(LoginActivity.this, "Credenciais incorretas",
                            Toast.LENGTH_SHORT).show();
                    btnLogin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void createAccount(final String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            refUsers.child(user.getUid()).setValue(new MyUser(user.getUid(), "José Silva", email, "968345678", TipoUser.Tecnico, ""));
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    userLogin = d.getValue(MyUser.class);
                }

                if (userLogin.getTipo() == TipoUser.Administrador){
                    redirect("Administrador");
                }else{
                    redirect("Técnico");
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void redirect(String tipo){
        Intent intent;
        if(tipo.equals("Administrador")){
            intent = new Intent(this, AdminMapActivity.class);
        }else{
            intent = new Intent(this, TecMapActivity.class);
        }
        intent.putExtra("USER", userLogin.getNome());
        startActivity(intent);
    }
}
