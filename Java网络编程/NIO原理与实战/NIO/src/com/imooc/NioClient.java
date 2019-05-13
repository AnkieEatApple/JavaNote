package com.imooc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * 类名: NioClient
 * 作者: Ankie
 * 时间: 2019-05-10 16:18
 * 描述:
 */
public class NioClient {


    /**
     * start
     */
    public void start(String nickname) throws IOException {

        Selector selector = Selector.open();

        // connect server
        SocketChannel socketChannel = SocketChannel.open(
                new InetSocketAddress("127.0.0.1", 8000));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();

        // send server to data
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && request.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(nickname + " : " + request));
            }
        }

        // receive server message response
        // new Thread for receive



    }

    public static void main(String[] args) throws IOException {
//       new NioClient().start();
    }
}
