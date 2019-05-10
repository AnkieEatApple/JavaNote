/**
 * 类名: NioClientHandler
 * 作者: Ankie
 * 时间: 2019-05-10 18:05
 * 描述:
 */
package com.imooc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioClientHandler implements Runnable {

    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (true) {

                int readyChannels = selector.select();      // this is a blocking methods

                if (readyChannels == 0) continue;

                // can use channel collection
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    // selectionkey instance
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    // remove this selectionKey, because this selectionKey has select, can't use it again.
                    iterator.remove();

                    // base on the ready state, Adjust the corresponding method processing logic
                    // if read event
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {

        // get ready channel from selectionkey
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        // create buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // pool read server response message
        String response = "";
        while (socketChannel.read(byteBuffer) > 0) {
            // switch read mode
            byteBuffer.flip();

            // read buffer
            response += Charset.forName("UtF-8").decode(byteBuffer);
        }

        // set channel register selector, listen other read event
        socketChannel.register(selector, SelectionKey.OP_READ);

        // write server response message to local
        if (response.length() > 0) {
            System.out.println(response);
        }

    }

}
