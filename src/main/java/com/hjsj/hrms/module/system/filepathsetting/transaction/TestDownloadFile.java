package com.hjsj.hrms.module.system.filepathsetting.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.bankgz.utils.MacAddressUtil;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class TestDownloadFile {
    public static void main(String[] args) {
        getFile("2c9f068874a629390174a9f622840026", "123");
    }

    /**
     * 影像下载
     *
     * @param fileId 获取影像平台唯一id
     * @return
     * @throws Exception
     */
    public static void getFile(String fileId, String busiNo) {
        String filepath = "";
        //1、调用ESC服务接口
        String escMessage = packMediaOtherJson("2002", "NIA2000103", fileId, busiNo);
        String escDownloadUrl = SystemConfig.getPropertyValue("escDownloadUrl");//http://ip:port/v1/nia/6022000103(esc请求转发地址，当前IP信息为ESC系统提供的服务器IP和端口。)
        filepath = callEscDownload(escMessage, escDownloadUrl);
        System.out.println("filepath:" + filepath);
    }

    /**
     * 组装影像平台下载JSON报文
     *
     * @param tranCode
     * @param actionId
     * @param fileId
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    private static String packMediaOtherJson(String tranCode, String actionId, String fileId, String busiNo) {
        //根据fiedldc查询业务流水号
        Connection conn = null;
        RowSet rs = null;
        JSONObject escMessage = new JSONObject();
        JSONObject requestData = new JSONObject();

        JSONObject systemHeader = new JSONObject();//业务数据系统头
        systemHeader.put("sourceSystemCode", "HRS");//请求方系统码
        systemHeader.put("sinkSystemCode", "ias");//目标方系统码
        systemHeader.put("actionId", actionId);//交易码
        systemHeader.put("actionVersion", "v1");//交易码版本 默认v1

        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String sourceJnlNo = datetime.substring(0, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17);
        systemHeader.put("sourceJnlNo", MacAddressUtil.getGlbSrvNo());//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）

        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS

        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        requestData.put("systemHeader", systemHeader);

        JSONObject body = new JSONObject();
        body.put("tranCode", tranCode);//交易码
        body.put("sysId", "HRS");//系统来源
        body.put("busiNo", busiNo);//业务流水号  全行唯一流水号
        body.put("fileId", fileId);//影像ID
        body.put("tarSysId", "");//影像ID
        requestData.put("body", body);

        escMessage.put("requestData", requestData);
        return escMessage.toString();
    }

    /**
     * 调用ESC影像下载接口
     *
     * @param escMessage
     * @param url
     * @return
     * @throws Exception
     */
    private static String callEscDownload(String escMessage, String url) {
        String filelocation = "";

        HttpClient httpClient = new HttpClient();
        PostMethod httpPost = new PostMethod(url);
        httpPost.addRequestHeader("Content-type", "application/json");
        httpPost.addRequestHeader("Charset", "UTF-8");
        httpPost.addRequestHeader("Accept", "application/json");
        httpPost.addRequestHeader("Accept-Charset", "UTF-8");

        String macVal = MacAddressUtil.getMacAddress(escMessage, "GZYH.NIAA_node.zak");
        if (StringUtils.isBlank(macVal)) {
//            throw GeneralExceptionHandler.Handle(new Exception("影像平台-->下载调用全密码平台获取mac为空!"));
        }
        httpPost.addRequestHeader("X-GZB-mac", macVal);//请求数据 mac 值(从全密码平台获取)
        String string = "";//返回报文
        try {
            httpPost.setRequestEntity(new StringRequestEntity(escMessage, "application/json", "UTF-8"));
            int status = httpClient.executeMethod(httpPost);//发送请求
            if (status == HttpStatus.SC_OK) {
                string = httpPost.getResponseBodyAsString();
                String X_GZB_mac = httpPost.getResponseHeader("X-GZB-mac").getValue();
                MacAddressUtil.verifyMacAddress(string, "GZYH.HRSA_node.zak", X_GZB_mac);
                JSONObject jsonObject = JSONObject.fromObject(string);
                String responseCode = jsonObject.getString("responseCode");
                if ("000000".equals(responseCode)) {
                    JSONObject body = jsonObject.getJSONObject("responseData").getJSONObject("body");
                    String fileInfo = (String) body.get("fileInfo");
                    JSONObject obj = JSONArray.fromObject(fileInfo).getJSONObject(0);
                    filelocation = obj.getString("fileUrl");
                } else {
                    throw GeneralExceptionHandler.Handle(new Exception("影像平台-->调用ESC下载接口失败，返回结果为" + string));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filelocation;
    }

    /**
     * 在指定目录生成对应文件
     *
     * @param inputStream
     */
    private static void writeFile(InputStream inputStream) {
        OutputStream outputStream = null;
        String tempdir = null;
        String fileId = "";//影像ID
        try {
            //1、在临时目录生成对应文件
            tempdir = System.getProperty("java.io.tmpdir");//获得临时目录路径
            String fullpath = tempdir + "/person.jpg";
            File destFile = new File(fullpath);
            outputStream = new FileOutputStream(destFile);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != tempdir) {
                //临时文件删除
                FileUtil.delfile(tempdir, "person.jpg");
            }
            PubFunc.closeResource(inputStream);
            PubFunc.closeResource(outputStream);
        }
    }
}
