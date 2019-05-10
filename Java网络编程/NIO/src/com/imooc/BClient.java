/**
 * 类名: BClient
 * 作者: Ankie
 * 时间: 2019-05-10 18:31
 * 描述:
 */
package com.imooc;

import java.io.IOException;

public class BClient {

    public static void main(String[] args) {


        try {
            new NioClient().start("BClient");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
