package com.hjsj.hrms.businessobject.sys.job;


import com.hjsj.hrms.utils.PubFunc;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FTP服务工具类
 *
 * @author：zhiyh
 * @time： 2019年5月28日 14:36:37
 * @version： V1.0.0
 */
public class FtpUtilBo {

    private Logger log = LoggerFactory.getLogger(FtpUtilBo.class);
    /**
     * FTP地址
     **/
    private String FTP_ADDRESS = "";

    /**
     * FTP端口
     **/
    private int FTP_PORT = 0;

    /**
     * FTP用户名
     **/
    private String FTP_USERNAME = "";

    /**
     * FTP密码
     **/
    private String FTP_PASSWORD = "";

    /**
     * FTP基础目录
     **/
    private String BASE_PATH = "";
    /**
     * 初始化登录ftp 默认false 登录成功返回true
     **/
    private Boolean b = false;

    public Boolean getB() {
        return b;
    }

    /**
     * 2018-6-13 12:39:55
     * 新添，初始化登录ftp，连接失败 返回b 为：false ,成功 为 ：true
     *
     * @param FTP_USERNAME 用户名
     * @param FTP_PASSWORD 密码
     * @param BASE_PATH    路径
     */
    public FtpUtilBo(String FTP_ADDRESS, int FTP_PORT, String FTP_USERNAME, String FTP_PASSWORD, String BASE_PATH) {
        this.FTP_ADDRESS = FTP_ADDRESS;
        this.FTP_PORT = FTP_PORT;
        this.FTP_USERNAME = FTP_USERNAME;
        this.FTP_PASSWORD = FTP_PASSWORD;
        this.BASE_PATH = BASE_PATH;
        b = login(FTP_ADDRESS, FTP_PORT, this.FTP_USERNAME, this.FTP_PASSWORD);
    }

    /**
     * 本地字符编码
     **/
    private static String localCharset = "GBK";

    /**
     * FTP协议里面，规定文件名编码为iso-8859-1
     **/
    private static String serverCharset = "ISO-8859-1";

    /**
     * UTF-8字符编码
     **/
    private static final String CHARSET_UTF8 = "UTF-8";

    /**
     * OPTS UTF8字符串常量
     **/
    private static final String OPTS_UTF8 = "OPTS UTF8";

    /**
     * 设置缓冲区大小4M
     **/
    private static final int BUFFER_SIZE = 1024 * 1024 * 4;

    /**
     * FTPClient对象
     **/
    private static FTPClient ftpClient = null;

    /**
     * 下载指定文件到本地
     *
     * @param fileName 要下载的文件名，例如：test.txt
     * @param savePath 保存文件到本地的路径，例如：D:/test
     * @return 成功返回true，否则返回false
     */
    public boolean downloadFile(String fileName, String savePath) {
        // 登录
        OutputStream os = null;
        boolean flag = false;
        if (ftpClient != null) {
            try {
                String path = changeEncoding(BASE_PATH);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    return flag;
                }
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File localFile = new File(savePath + File.separatorChar + fileName);
                os = new FileOutputStream(localFile);
                ftpClient.retrieveFile(fileName, os);
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                PubFunc.closeIoResource(os);
            }
        }
        return flag;
    }

    /**
     * 下载该目录下所有文件到本地
     *
     * @param ftpPath  FTP服务器上的相对路径，例如：test/123
     * @param savePath 保存文件到本地的路径，例如：D:/test
     * @return 成功返回true，否则返回false
     */
    public boolean downloadFiles(String ftpPath, String savePath) {
        // 登录
        boolean flag = false;
        if (ftpClient != null) {
            try {
                String path = changeEncoding(BASE_PATH + ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    return flag;
                }
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    return flag;
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    File file = new File(savePath + '/' + ftpName);
                    try {
                        OutputStream os = new FileOutputStream(file);
                        ftpClient.retrieveFile(ff, os);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 获取该目录下所有文件,以字节数组返回
     *
     * @param ftpPath FTP服务器上文件所在相对路径，例如：test/123
     * @return Map<String, Object> 其中key为文件名，value为字节数组对象
     */
    public Map<String, byte[]> getFileBytes(String ftpPath) {
        // 登录
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        if (ftpClient != null) {
            try {
                String path = changeEncoding(BASE_PATH + ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    return map;
                }
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    return map;
                }
                for (String ff : fs) {
                    try {
                        InputStream is = ftpClient.retrieveFileStream(ff);
                        String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int readLength = 0;
                        while ((readLength = is.read(buffer, 0, BUFFER_SIZE)) > 0) {
                            byteStream.write(buffer, 0, readLength);
                        }
                        map.put(ftpName, byteStream.toByteArray());
                        ftpClient.completePendingCommand(); // 处理多个文件
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnect();
            }
        }
        return map;
    }

    /**
     * 根据名称获取文件，以字节数组返回
     *
     * @param ftpPath  FTP服务器文件相对路径，例如：test/123
     * @param fileName 文件名，例如：test.xls
     * @return byte[] 字节数组对象
     */
    public byte[] getFileBytesByName(String ftpPath, String fileName) {
        // 登录
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        if (ftpClient != null) {
            try {
                String path = changeEncoding(BASE_PATH + ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    return byteStream.toByteArray();
                }
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    return byteStream.toByteArray();
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    if (ftpName.equals(fileName)) {
                        try {
                            InputStream is = ftpClient.retrieveFileStream(ff);
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int len = -1;
                            while ((len = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                byteStream.write(buffer, 0, len);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnect();
            }
        }
        return byteStream.toByteArray();
    }

    /**
     * 获取该目录下所有文件,以输入流返回
     *
     * @param ftpPath FTP服务器上文件相对路径，例如：test/123
     * @return Map<String, InputStream> 其中key为文件名，value为输入流对象
     */
    public Map<String, InputStream> getFileInputStream(String ftpPath) {
        // 登录
        Map<String, InputStream> map = new HashMap<String, InputStream>();
        if (ftpClient != null) {
            try {
                String path = changeEncoding(BASE_PATH + ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    return map;
                }
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    return map;
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    InputStream is = ftpClient.retrieveFileStream(ff);
                    map.put(ftpName, is);
                    ftpClient.completePendingCommand(); // 处理多个文件
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnect();
            }
        }
        return map;
    }

    /**
     * 根据名称获取文件，以输入流返回
     *
     * @param ftpPath  FTP服务器上文件相对路径，例如：test/123
     * @param fileName 文件名，例如：test.txt
     * @return InputStream 输入流对象
     */
    public InputStream getInputStreamByName(String ftpPath, String fileName) {
        // 登录
        InputStream input = null;
        if (ftpClient != null) {
            try {
                String path = changeEncoding(BASE_PATH + ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    return input;
                }
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    return input;
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    if (ftpName.equals(fileName)) {
                        input = ftpClient.retrieveFileStream(ff);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnect();
            }
        }
        return input;
    }

    /**
     * 根据文件夹，文件 名称，判断是否存在
     *
     * @param ftpPath  FTP服务器上文件相对路径，例如：test/123
     * @param fileName 文件名，例如：test.txt
     * @return map
     */
    public Map checkoutFtpPathAndFileName(String ftpPath, String fileName) {
        // 登录
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("filePath", false);
        map.put("fileName", false);
        if (ftpClient != null) {
            try {
                String path = changeEncoding(BASE_PATH + ftpPath);
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(path)) {
                    System.out.println(BASE_PATH + ftpPath + "该目录不存在");
                    map.put("filePath", false);
                } else {
                    map.put("filePath", true);
                }
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String[] fs = ftpClient.listNames();
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    System.out.println(BASE_PATH + ftpPath + "该目录下没有文件");
                    map.put("fileName", false);
                }
                for (String ff : fs) {
                    String ftpName = new String(ff.getBytes(serverCharset), localCharset);
                    if (ftpName.equals(fileName)) {
                        map.put("fileName", true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 删除指定文件
     *
     * @param path     文件相对路径，例如：test/123/test.txt
     * @param fileName
     * @return 成功返回true，否则返回false
     */
    public boolean deleteFile(String path, String fileName) {
        boolean flag = false;
        try {
            //切换FTP目录
            ftpClient.changeWorkingDirectory(path);
            ftpClient.dele(fileName);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除目录下所有文件
     *
     * @param dirPath 文件相对路径，例如：test/123
     * @return 成功返回true，否则返回false
     */
    public boolean deleteFiles(String dirPath) {
        // 登录
        boolean flag = false;
        if (ftpClient != null) {
            try {
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                String path = changeEncoding(BASE_PATH + dirPath);
                String[] fs = ftpClient.listNames(path);
                // 判断该目录下是否有文件
                if (fs == null || fs.length == 0) {
                    return flag;
                }
                for (String ftpFile : fs) {
                    ftpClient.deleteFile(ftpFile);
                }
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnect();
            }
        }
        return flag;
    }

    /**
     * 连接FTP服务器
     *
     * @param address  地址，如：127.0.0.1
     * @param port     端口，如：21
     * @param username 用户名，如：root
     * @param password 密码，如：root
     */
    private Boolean login(String address, int port, String username, String password) {
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(address, port);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                log.error("ftp服务器连接失败....ip: {} ", FTP_ADDRESS);
                closeConnect();
            } else {
                log.debug("ftp服务器连接成功....ip: {} ", FTP_ADDRESS);
                b = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * @param address
     * @param port
     * @param username
     * @param password
     * @return
     */
    public static Boolean testConnect(String address, int port, String username, String password) {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(address, port);
            ftpClient.login(username, password);
            int reply = ftpClient.getReplyCode();
            if (reply == 230 || reply == 200) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient != null && ftpClient.isConnected()) {
                    ftpClient.logout();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 关闭FTP连接
     */
    public void closeConnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * FTP服务器路径编码转换
     *
     * @param ftpPath FTP服务器路径
     * @return String
     */
    private static String changeEncoding(String ftpPath) {
        String directory = null;
        try {
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(OPTS_UTF8, "ON"))) {
                localCharset = CHARSET_UTF8;
            }
            directory = new String(ftpPath.getBytes(localCharset), serverCharset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory;
    }

    /**
     * 在服务器上递归创建目录
     *
     * @param dirPath 上传目录路径
     * @return
     */
    private void createDirectorys(String dirPath) {
        try {
            if (!dirPath.endsWith("/")) {
                dirPath += "/";
            }
            String directory = dirPath.substring(0, dirPath.lastIndexOf("/") + 1);
            ftpClient.makeDirectory("/");
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(dirPath.substring(start, end));
                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        return;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                //检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     *
     * @param fileName
     * @param path
     * @return
     */
    public boolean uploadFtp(String fileName, String path) {
        FileInputStream fis = null;
        boolean flag = false;
        try {
            if (ftpClient != null) {
                String zipFileName = path + File.separator + fileName;
                File uploadfile = new File(zipFileName);
                String chpath = changeEncoding(this.BASE_PATH);
                if (uploadfile.exists()) {
                    boolean workdir = ftpClient.changeWorkingDirectory(chpath);
                    if (!workdir) {
                        ftpClient.makeDirectory(chpath);
                        ftpClient.changeWorkingDirectory(chpath);
                    }
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.enterLocalPassiveMode();
                    fis = new FileInputStream(uploadfile);
                    flag = ftpClient.storeFile(fileName, fis);
                }
            }
        } catch (Exception e) {
            log.error("uploadFtp:上传ftp服务器文件出错!,desc", e);
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(fis);
            //closeConnect();
        }
        return flag;
    }

    /**
     * 获取FTP的文件名
     *
     * @return
     */
    public List<String> getFileNameList() {
        List<String> fileNameList = new ArrayList<String>();
        // 获得指定目录下所有文件名
        FTPFile[] ftpFiles = null;
        try {
            //ftpClient.changeWorkingDirectory(new String(path.getBytes(),FTP.DEFAULT_CONTROL_ENCODING));
            String path = changeEncoding(BASE_PATH);
            ftpFiles = ftpClient.listFiles(path);
            for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) {
                FTPFile file = ftpFiles[i];
                if (file.isFile()) {
                    fileNameList.add(file.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNameList;
    }
}