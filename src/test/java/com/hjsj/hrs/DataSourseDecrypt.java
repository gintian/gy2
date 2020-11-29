package com.hjsj.hrs;

import com.hrms.hjsj.sys.Des;

public class DataSourseDecrypt {
    public static void main(String[] args) {
        String username = "yksoft";
        String password = "yksoft1919";
        Des des = new Des();
        //注：配置到system中的加密用户名和密码，前面都需加@符号
        String encryUsername = des.EncryPwdStr(username);
        String encryPassword = des.EncryPwdStr(password);
        System.out.println("加密后的用户名："+encryUsername);
        System.out.println("加密后的密码："+encryPassword);
        System.out.println("解密后的用户名："+des.DecryPwdStr(encryUsername));
        System.out.println("解密后的密码："+des.DecryPwdStr(encryPassword));
    }
}
