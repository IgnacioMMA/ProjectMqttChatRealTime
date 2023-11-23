package twin.developers.projectmqtt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    Button btnRegistrar;
    EditText txtvCorreoR, txtvConfCorreoR, txtvContraR, txtvConfContraR;
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        databaseReference = FirebaseDatabase.getInstance().getReference("Cuentas");
        txtvCorreoR = findViewById(R.id.txtvCorreoR);
        txtvConfCorreoR = findViewById(R.id.txtvConfCorreoR);
        txtvContraR = findViewById(R.id.txtvContraR);
        txtvConfContraR = findViewById(R.id.txtvConfContraR);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Correo = txtvCorreoR.getText().toString().trim();
                String Contra = txtvContraR.getText().toString().trim();
                String confirmarCorreo = txtvConfCorreoR.getText().toString().trim();
                String confirmarContra = txtvConfContraR.getText().toString().trim();

                if (!TextUtils.isEmpty(Correo) && !TextUtils.isEmpty(Contra) &&
                        !TextUtils.isEmpty(confirmarCorreo) && !TextUtils.isEmpty(confirmarContra)) {
                    if (Correo.equals(confirmarCorreo) && Contra.equals(confirmarContra)) {
                        String hashedPassword = hashPassword(Contra);

                        Map<String, Object> usuarioMap = new HashMap<>();
                        usuarioMap.put("correo", Correo);
                        usuarioMap.put("contraseña", hashedPassword);

                        String correoCodificado = Correo.replace(".", "_");

                        databaseReference.child(correoCodificado).setValue(usuarioMap);

                        Toast.makeText(MainActivity2.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity2.this, "Las contraseñas o correos no coinciden", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity2.this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder builder = new StringBuilder();
            for (byte b : hashedBytes) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}