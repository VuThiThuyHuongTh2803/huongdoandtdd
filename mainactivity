package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import java.io.*;
import java.net.*;

public class MainActivity extends AppCompatActivity {

    private EditText editUserName, editMessage;
    private Button btnConnect, btnSend;
    private TextView textChat;
    
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread thread;

    // Cập nhật SERVER_IP theo địa chỉ IP của máy chạy server
    // Lưu ý: Nếu dùng Android Emulator kết nối đến máy cục bộ, dùng "10.0.2.2"
    private final String SERVER_IP = "10.0.2.2";  
    private final int SERVER_PORT = 12345;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         editUserName = findViewById(R.id.editUserName);
         editMessage  = findViewById(R.id.editMessage);
         btnConnect   = findViewById(R.id.btnConnect);
         btnSend      = findViewById(R.id.btnSend);
         textChat     = findViewById(R.id.textChat);

         // Nút kết nối server
         btnConnect.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String userName = editUserName.getText().toString().trim();
                 if (userName.isEmpty()) {
                     Toast.makeText(MainActivity.this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 connectToServer(userName);
             }
         });

         // Nút gửi tin nhắn
         btnSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String message = editMessage.getText().toString().trim();
                 if (message.isEmpty()) {
                     return;
                 }
                 sendMessage(message);
                 editMessage.setText("");
             }
         });
    }

    // Kết nối đến server và lắng nghe tin nhắn
    private void connectToServer(final String userName) {
         thread = new Thread(new Runnable() {
             @Override
             public void run() {
                 try {
                     socket = new Socket(SERVER_IP, SERVER_PORT);
                     reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     writer = new PrintWriter(socket.getOutputStream(), true);
                     
                     // Gửi tên người dùng tới server lần đầu
                     writer.println(userName);
                     
                     // Lắng nghe tin nhắn từ server
                     while (socket != null && !socket.isClosed()) {
                         final String message = reader.readLine();
                         if (message != null) {
                             handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     textChat.append(message + "\n");
                                 }
                             });
                         }
                     }
                 } catch (IOException e) {
                     e.printStackTrace();
                     handler.post(new Runnable() {
                         @Override
                         public void run() {
                             Toast.makeText(MainActivity.this, "Lỗi kết nối đến server", Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
             }
         });
         thread.start();
    }

    // Gửi tin nhắn từ client đến server
    private void sendMessage(String message) {
         if (writer != null) {
              writer.println(message);
         }
    }

    @Override
    protected void onDestroy() {
         super.onDestroy();
         try {
             if (socket != null) {
                 socket.close();
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
}
