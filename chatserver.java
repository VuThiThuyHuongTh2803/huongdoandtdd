import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // Tập hợp các ClientHandler đang kết nối
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        int port = 12345; // Cổng chạy server
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server chat đang chạy trên cổng " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Có kết nối mới từ: " + socket.getInetAddress());
                
                // Tạo đối tượng xử lý cho client và chạy trong một luồng riêng
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi chạy server: " + e.getMessage());
        }
    }
    
    // Phát tin nhắn đến tất cả các client trừ người gửi ban đầu
    public static void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeUser) {
                client.sendMessage(message);
            }
        }
    }
    
    // Loại bỏ client đã ngắt kết nối
    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
        System.out.println("Client đã ngắt kết nối. Số client hiện tại: " + clientHandlers.size());
    }
}

// Class xử lý kết nối của từng client
class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String clientName;
    
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    
    public void run() {
        try {
            // Lấy luồng đầu vào và đầu ra của socket
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            
            // Client gửi tên của mình lần đầu (để làm tên hiển thị)
            this.clientName = reader.readLine();
            System.out.println("Người dùng kết nối: " + clientName);
            ChatServer.broadcast(clientName + " đã kết nối!", this);
            
            String clientMessage;
            // Lắng nghe tin nhắn từ client và broadcast tin nhắn đó
            while ((clientMessage = reader.readLine()) != null) {
                String serverMessage = "[" + clientName + "]: " + clientMessage;
                System.out.println(serverMessage);
                ChatServer.broadcast(serverMessage, this);
            }
        } catch (IOException ex) {
            System.out.println("Lỗi ClientHandler: " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // bỏ qua nếu có lỗi đóng socket
            }
            ChatServer.removeClient(this);
            ChatServer.broadcast(clientName + " đã ngắt kết nối!", this);
        }
    }
    
    // Gửi tin nhắn đến client tương ứng
    void sendMessage(String message) {
        writer.println(message);
    }
}
