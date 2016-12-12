package ru.pravvich.lesson_6.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.String.format;

class Server {

    private ServerSocket serverSocket;
    private Socket socket;

    public static void main(String[] args) {
        new Server().startServer();
    }

    private void initServerSocket() {
        try {
            this.serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void socketAccept() {
        try {
            this.socket = this.serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        this.initServerSocket();
        System.out.println("Wait connections...");
        this.socketAccept();
        System.out.println("Connections accept.");
        String command;

        try (InputStream in = this.socket.getInputStream();
             OutputStream out = this.socket.getOutputStream()) {

            // Вот тут передаю in
            command = this.getMassage(in);

            while (!"q".equals(command)) {
                System.out.println(command);

                // загружаем на сервер
                if (command.contains("u -f ")) {
                    String serverPath = toServerPath(command);
                    if (this.download(serverPath, (FileInputStream) in)) { // true если удалось
                        System.out.println(format("Файл:\n%s\nуспешно создан.",serverPath));
                    }
                } else if (command.contains("d -f ")) {
                    System.out.println(command.replace("d -f ",""));
                    this.upload(command.replace("d -f ",""), (FileOutputStream) out);
                }

                out.flush();
                command = this.getMassage(in);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // отправка файла в сокет
    private void upload(String path, FileOutputStream out) {
        try (FileInputStream in = new FileInputStream(new File(path))) {

            int data;
            while ((data = in.read()) != -1) {
                out.write(data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMassage(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        return dis.readUTF();
    }

    // загружаем файл из сокета и записываем на свой диск
    private boolean download(String path, FileInputStream in) {
        File target;
        try (FileOutputStream out = new FileOutputStream(target = new File(path))) {

            int data;
            while ((data = in.read()) != -1) {
                out.write(data);
            }

            if (target.exists())
                return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String toServerPath(String clientPath) {
        String[] arr = clientPath.split("/");
        return format("/Users/pavel/Desktop/test/server/%s",arr[arr.length - 1]);
    }
}