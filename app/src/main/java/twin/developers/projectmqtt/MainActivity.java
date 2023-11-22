package twin.developers.projectmqtt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Importa la clase Log
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import twin.developers.projectmqtt.MainActivity2;
import twin.developers.projectmqtt.MainActivity3;
import twin.developers.projectmqtt.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; // Etiqueta para los mensajes de registro
    private Button btnSesion, btnRegistrarse;
    private EditText txtvCorreo, txtvContra;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Cuentas");
        btnSesion = findViewById(R.id.btnSesion);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        txtvCorreo = findViewById(R.id.txtvCorreo);
        txtvContra = findViewById(R.id.txtvContra);

        btnSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String correo = txtvCorreo.getText().toString().trim();
                final String contra = txtvContra.getText().toString().trim();

                if (!TextUtils.isEmpty(correo) && !TextUtils.isEmpty(contra)) {
                    String correoCodificado = correo.replace(".", "_");

                    DatabaseReference userReference = databaseReference.child(correoCodificado);
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String contraseñaAlmacenada = snapshot.child("contraseña").getValue(String.class);
                                String hashedPassword = hashPassword(contra);

                                if (hashedPassword != null && hashedPassword.equals(contraseñaAlmacenada)) {
                                    Log.d(TAG, "Contraseña correcta. Iniciando MainActivity3...");
                                    Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    });

                    txtvCorreo.setText("");
                    txtvContra.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
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
