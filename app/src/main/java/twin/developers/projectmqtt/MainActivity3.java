package twin.developers.projectmqtt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity3 extends AppCompatActivity {

    private Mqtt mqttManager;
    private EditText texto;
    private Button btnEnviar;
    private TextView receivedMessagesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        texto = findViewById(R.id.txtMessage);
        btnEnviar = findViewById(R.id.btnPublish);
        receivedMessagesTextView = findViewById(R.id.receivedMessagesTextView);


        mqttManager = new Mqtt(getApplicationContext(), new Mqtt.MqttMessageListener() {
            @Override
            public void onMessageReceived(String message) {
                runOnUiThread(() -> {
                    appendToReceivedMessages(message);

                    showNotification("Nuevo mensaje", message);
                });
            }
        });
        mqttManager.connectToMqttBroker();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    private void appendToReceivedMessages(String message) {
        receivedMessagesTextView.append(message + "\n");
    }

    private void sendMessage() {
        String messageToSend = texto.getText().toString();
        if (!messageToSend.isEmpty()) {
            mqttManager.publishMessage(messageToSend);
            texto.setText("");
        }
    }

    private void showNotification(String title, String message) {
        // Configurar el NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity3.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {0, 500, 100, 500};
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notification_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(pattern)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        notificationManager.notify(1, builder.build());
    }
}
