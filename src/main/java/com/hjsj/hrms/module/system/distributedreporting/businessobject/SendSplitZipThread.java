package com.hjsj.hrms.module.system.distributedreporting.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class SendSplitZipThread implements Callable {
    private String fileName;
    private String sjwsdl;
    private String pkgPath;
    private DrLogger drLogger;
    public SendSplitZipThread(String fileName, String sjwsdl, String pkgPath, DrLogger drLogger) {
        this.fileName = fileName;
        this.pkgPath = pkgPath;
        this.sjwsdl = sjwsdl;
        this.drLogger = drLogger;
    }

    @Override
    public String call() throws Exception {
        String reslut = "false";
        FileInputStream in = null;
        try{
            File file = new File(pkgPath+fileName);
            in = new FileInputStream(file);
            String hashCode = DigestUtils.md5Hex(in).toUpperCase();
            JSONObject resultJson = sendSplitZip(hashCode);
            drLogger.write("分布同步：分卷压缩数据包"+fileName+"返回值："+resultJson);
            if (null != resultJson)
            {
                String flag = resultJson.getString("flag");
                if("1".equals(flag)){ // flag=1为数据包接收成功
                    reslut = "true";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            PubFunc.closeIoResource(in);
        }
        return reslut;
    }

    /**
     * 发送分卷压缩包到上级系统
     * @param hashCode
     */
    private JSONObject sendSplitZip(String hashCode) {
        JSONObject jsonObject = null;
        DataInputStream in = null;
        OutputStream out = null;
        BufferedReader reader = null;
        try{
            // 服务器的域名
            URL url = new URL(sjwsdl+"?fileName="+fileName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "GBK");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("Content-Type", "application/zip");
            conn.setRequestProperty("Hash",hashCode);
            conn.setRequestProperty("Finish","false");
            out = new DataOutputStream(conn.getOutputStream());
            // 上传文件
            File file = new File(pkgPath+fileName);
            // 数据输入流,用于读取文件数据
            in = new DataInputStream(new FileInputStream(
                    file));
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            // 每次读1KB数据,并且将文件数据写入到输出流中
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.flush();
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonObject = JSONObject.fromObject(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        	PubFunc.closeResource(in);
        	PubFunc.closeResource(out);
        	PubFunc.closeResource(reader);
        	
        }
        return jsonObject;
    }
}
