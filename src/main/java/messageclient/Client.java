package messageclient;

import messageclient.ui.ClientWindow;
import messageclient.ui.ServerPrompt;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The Message Client
 */
public class Client {
    private final InputStream in;
    private final ClientWindow frame;

    public Client(InputStream in, ClientWindow frame) {
        this.in = in;
        this.frame = frame;
    }

    public static Client create(InputStream in, OutputStream out) {
        return new Client(in, new ClientWindow(new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))));
    }

    public void run() {
        var scanner = new Scanner(in, "UTF-8");
        try {
            while (true) {
                frame.append(scanner.nextLine() + "\n");
            }
        } catch (NoSuchElementException e) {
            frame.setVisible(false);
            JOptionPane.showMessageDialog(null, "Connection To Server Lost");
            System.exit(-1);
        }
    }

    public static InetSocketAddress interactivelyGetAddress() throws InterruptedException {
        return new ServerPrompt("localhost", 2222).waitForAddress();
    }

    public static void main(String[] args) throws InterruptedException {
        InetSocketAddress address = args.length == 2
                ? Utils.parseInetAddress(args[0], args[1])
                : interactivelyGetAddress();

        try (Socket socket = new Socket()) {
            socket.connect(address);
            Client.create(socket.getInputStream(), socket.getOutputStream()).run();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(-1);
        }
    }
}