package com.joe.hutool;

import cn.hutool.core.util.ZipUtil;

import java.io.File;

/**
 * Hutool测试
 *
 * @author qkh
 * @version 1.0
 * @date 2021/5/19 10:08
 */
public class HutoolTest {

    public static void main(String[] args) {
        File zip = ZipUtil.zip("C:\\Users\\89\\Desktop\\iworksApp1.14.020210308", "C:\\Users\\89\\Desktop\\zipTest1.zip", true);
        System.out.println(File.separator);
    }
}
