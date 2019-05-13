package com.imooc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 类名: NioServer
 * 作者: Ankie
 * 时间: 2019-05-10 16:17
 * 描述: NIO 服务端
 */
public class NioServer {

    /**
     * 启动
     */
    public void start() throws IOException {
        /**
         * 1. create a selector
         */
        Selector selector = Selector.open();

        /**
         * 2. create a channel by ServerSocketChannel
         */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        /**
         * 3. bind listen port for channel
         */
        serverSocketChannel.bind(new InetSocketAddress(8000));

        /**
         * 4. set channel become non-blocking
         */
        serverSocketChannel.configureBlocking(false);

        /**
         * 5. register channel into selector, and listen the connect event
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server start success!!!!");

        /**
         * 6. Loop waiting for new a connection
         */
        while (true) {
            /**
             * TODO get can use channel number
             */
            int readyChannels = selector.select();      // this is a blocking methods

            // TODO why judgment this?
            if (readyChannels == 0) {
                continue;
            }

            // can use channel collection
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                // selectionkey instance
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                // remove this selectionKey, because this selectionKey has select, can't use it again.
                iterator.remove();

                /**
                 * 7. base on the ready state, Adjust the corresponding method processing logic
                 */

                // if access the event
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }

                // if read event
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }
            }
        }


    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel,
                               Selector selector)
            throws IOException{

        // if access evnet, create socketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();

        // set socketChannel non-blocking
        socketChannel.configureBlocking(false);

        // register channel into selector, listen read event
        socketChannel.register(selector, SelectionKey.OP_READ);

        // answer client message
        socketChannel.write(Charset.forName("UTF-8")
                .encode("You are not has friendship with others, please pay attention to private!"));
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException{

        // get ready channel from selectionkey
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        // create buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // pool read client request message
        String request = "";
        while (socketChannel.read(byteBuffer) > 0) {
            // switch read mode
            byteBuffer.flip();

            // read buffer
            request += Charset.forName("UtF-8").decode(byteBuffer);
        }

        // set channel register selector, listen other read event
        socketChannel.register(selector, SelectionKey.OP_READ);

        // send the message that receive form client, send broadcast to other clients
        if (request.length() > 0) {
            // broadcast to other clients
//            System.out.println("::" + request);
            broadCast(selector, socketChannel, request);
        }

    }

    private void broadCast(Selector selector, SocketChannel sourceChannel, String request) {
        // get all connect client channel
        Set<SelectionKey> selectionKeys = selector.keys();

        // loop send message broadcast
        selectionKeys.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();

            // exclude client had send msg
            if (targetChannel instanceof SocketChannel && targetChannel!= sourceChannel) {
                try {
                    // send msg  to targetChannel client
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void main(String[] args) throws IOException{

        NioServer nioServer = new NioServer();
        nioServer.start();
    }
}
