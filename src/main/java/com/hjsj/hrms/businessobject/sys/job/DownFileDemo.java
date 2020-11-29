package com.hjsj.hrms.businessobject.sys.job;

import com.dcfs.fts.client.FtpClientConfig;
import com.dcfs.fts.client.download.FtpGet;
import com.dcfs.fts.common.error.FtpException;

import java.io.IOException;

public class DownFileDemo {
    public static void main(String[] args) {
        try {
            boolean flag = getFile("/hrs/hrs001/data/20200928/EMP_20200928.csv", "C:/Users/chunyu/Desktop/EMP_20200918.csv", "hrs001");
            System.out.println("flag:" + flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param remoteFileName 该路径由文件生产系统提供或者提前约定，路径格式如：/用户名/传输交易码/test/a.txt
     * @param localFileName  文件消费系统将文件下载到本地的绝对路径，例如：/home/zyb/apps/test/a.txt；
     * @param tranCode
     * @return
     * @throws FtpException
     * @throws IOException
     */
    public static boolean getFile(String remoteFileName, String localFileName, String tranCode) throws FtpException, IOException {

        FtpClientConfig config = FtpClientConfig.getInstance();
        FtpGet ftpGet = new FtpGet(remoteFileName, localFileName, tranCode, config);
        boolean flag = ftpGet.doGetFile();
        return flag;
    }
}
