/**
 * 类名: AClient
 * 作者: Ankie
 * 时间: 2019-05-10 18:31
 * 描述:
 */
package com.imooc;

import java.io.IOException;

public class AClient {

    public static void main(String[] args) {


        try {
            new NioClient().start("AClient");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
