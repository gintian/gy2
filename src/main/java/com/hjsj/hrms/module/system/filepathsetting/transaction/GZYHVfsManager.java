package com.hjsj.hrms.module.system.filepathsetting.transaction;

import com.dcfs.fts.client.FtpClientConfig;
import com.dcfs.fts.client.upload.FtpPut;
import com.dcfs.fts.common.error.FtpException;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.bankgz.utils.MacAddressUtil;
import com.hjsj.hrms.bankgz.utils.SendKafkaGlblSrvNo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.virtualfilesystem.VFSUtil;
import com.hrms.virtualfilesystem.VfsParam;
import com.hrms.virtualfilesystem.manager.VfsManager;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GZYHVfsManager implements VfsManager {
    private Logger log = LoggerFactory.getLogger(GZYHVfsManager.class);
    private static int sequence = 0;
    private static int length = 6;

    /**
     * 影像下载
     *
     * @param fileId   获取影像平台唯一id
     * @param vfsParam
     * @return
     * @throws Exception
     */
    @Override
    public InputStream getFile(String fileId, VfsParam vfsParam) throws Exception {
        String filepath = "";
        //1、调用ESC服务接口
        String sNo = MacAddressUtil.getGlbSrvNo();
        SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "NIA2000103");//登记流水号
        String escMessage = packMediaOtherJson("2002", "NIA2000103", fileId, sNo);
        String escDownloadUrl = SystemConfig.getPropertyValue("escDownloadUrl");//http://ip:port/v1/nia/6022000103(esc请求转发地址，当前IP信息为ESC系统提供的服务器IP和端口。)
        if (StringUtils.isBlank(escDownloadUrl)) {
            log.error("影像平台-->esc影像下载地址未配置，请配置escDownloadUrl参数！");
            throw GeneralExceptionHandler.Handle(new Exception("影像平台-->esc影像下载地址未配置，请配置escDownloadUrl参数！"));
        }
        filepath = callEscDownload(escMessage, escDownloadUrl, sNo);

        InputStream inputStream = null;
        FileObject fileObject = null;
        //文件系统管理器接口
        FileSystemManager fsManager = null;
        try {
            fsManager = VFS.getManager();
            fileObject = fsManager.resolveFile(filepath);
            inputStream = fileObject.getContent().getInputStream();
        } catch (Exception e) {
            throw e;
        }
        return inputStream;
    }

    /**
     * 影像上传
     *
     * @param inputStream     文件流数据
     * @param vfsParam        参数
     * @param vfsFiletypeEnum 文件类型
     * @param vfsModulesEnum  所属模块
     * @param vfsCategoryEnum 文件所属类型
     * @param CategoryGuidKey 所属类型guidkey
     * @param extension       文件扩展名
     */
    @Override
    public String addFile(String id, String operator, InputStream inputStream, VfsParam vfsParam, VfsFiletypeEnum vfsFiletypeEnum, VfsModulesEnum vfsModulesEnum, VfsCategoryEnum vfsCategoryEnum, String CategoryGuidKey, String extension) throws Exception {
        OutputStream outputStream = null;
        String tempdir = null;
        String remoteFileName = "";
        String fileId = "";//影像ID
        try {
            //1、在临时目录生成对应文件
            tempdir = System.getProperty("java.io.tmpdir");//获得临时目录路径
            remoteFileName = "/" + VFSUtil.getUUID() + "." + extension; //文件传输系统保存的文件名 例如：test/a.txt
            String fullpath = tempdir + remoteFileName;
            File destFile = new File(fullpath);
            outputStream = new FileOutputStream(destFile);
            FileUtil.write2Out(inputStream, outputStream);
            //2、调用文件传输平台接口(存放到指定目录)
            String tranCode = "hrs001";//传输交易码，该字段内容请向文件传输系统管理员申请
            String escFilePath = putFile(fullpath, remoteFileName, tranCode);
            //3、调用ESC服务接口
            String sNo = MacAddressUtil.getGlbSrvNo();
            SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "NIA2000102");//登记流水号
            String escMessage = packMediaUploadJson("2001", id, escFilePath, extension, sNo);
            String escUploadUrl = SystemConfig.getPropertyValue("escUploadUrl");//http://ip:port/v1/nia/6022000102(esc请求转发地址，当前IP信息为ESC系统提供的服务器IP和端口。)
            if (StringUtils.isBlank(escUploadUrl)) {
                log.error("影像平台-->esc影像上传地址未配置，请配置escUploadUrl参数！");
                throw GeneralExceptionHandler.Handle(new Exception("影像平台-->esc影像上传地址未配置，请配置escUploadUrl参数！"));
            }
            fileId = callEscUpload(escMessage, escUploadUrl, sNo);
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != tempdir) {
                //临时文件删除
                FileUtil.delfile(tempdir, remoteFileName);
            }
            PubFunc.closeResource(inputStream);
            PubFunc.closeResource(outputStream);
        }
        return fileId;
    }

    /**
     * 影像删除
     *
     * @param id       删除影像平台唯一id
     * @param operator
     * @param vfsParam
     * @param fileId
     * @return
     * @throws Exception
     */
    @Override
    public boolean deleteFile(String id, String operator, VfsParam vfsParam, String fileId) throws Exception {
        //1、调用ESC服务接口
        String sNo = MacAddressUtil.getGlbSrvNo();
        SendKafkaGlblSrvNo.registerGlbSrlNo(sNo, "NIA2000104");//登记流水号
        String escMessage = packMediaOtherJson("2003", "NIA2000104", fileId, sNo);
        String escDeleteUrl = SystemConfig.getPropertyValue("escDeleteUrl");//http://ip:port/v1/nia/6022000104(esc请求转发地址，当前IP信息为ESC系统提供的服务器IP和端口。)
        if (StringUtils.isBlank(escDeleteUrl)) {
            log.error("影像平台-->esc影像删除地址未配置，请配置escDeleteUrl参数！");
            throw GeneralExceptionHandler.Handle(new Exception("影像平台-->esc影像删除地址未配置，请配置escDeleteUrl参数！"));
        }
        boolean isSucceed = callEscDelete(escMessage, escDeleteUrl, sNo);

        return isSucceed;
    }

    /**
     * 影像更新
     *
     * @param id
     * @param operator
     * @param fileId
     * @param inputStream
     * @param vfsParam
     * @param vfsFiletypeEnum
     * @param vfsModulesEnum
     * @param vfsCategoryEnum
     * @param CategoryGuidKey
     * @param extension
     * @return
     * @throws Exception
     */
    @Override
    public String saveFile(String id, String operator, String fileId, InputStream inputStream, VfsParam vfsParam, VfsFiletypeEnum vfsFiletypeEnum, VfsModulesEnum vfsModulesEnum, VfsCategoryEnum vfsCategoryEnum, String CategoryGuidKey, String extension) throws Exception {
        String retfileId = "";
        deleteFile(id, operator, vfsParam, fileId);
        retfileId = addFile(id, operator, inputStream, vfsParam, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidKey, extension);
        return retfileId;
    }

    public static String putFile(String localFileName, String remoteFileName, String tranCode) throws FtpException, IOException {
        FtpClientConfig config = FtpClientConfig.getInstance();
        FtpPut ftpPut = new FtpPut(localFileName, remoteFileName, tranCode, true, false, true, config);
        String filePath = ftpPut.doPutFile();
        return filePath;
    }


    /**
     * 组装影像平台上传 JSON报文
     *
     * @param tranCode
     * @param id
     * @param escFilePath
     * @param extension
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    private String packMediaUploadJson(String tranCode, String id, String escFilePath, String extension, String sNo) throws SQLException, GeneralException {
        //增加流水号字段存储流水号
        //生成唯一流水号
        String busiNo = getLocalTrmSeqNum();//流水号
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            DbWizard db = new DbWizard(conn);
            if (!db.isExistField("vfs_file_mapping", "busiNo", false)) {
                Table t = new Table("vfs_file_mapping");
                Field f = new Field("busiNo", DataType.STRING);
                f.setNullable(true);
                f.setLength(38);
                t.addField(f);
                db.addColumns(t);
            }
            ContentDAO dao = new ContentDAO(conn);
            RecordVo vo = new RecordVo("vfs_file_mapping");
            vo.setString("id", id);
            vo.setString("busino", busiNo);
            dao.updateValueObject(vo);
        } catch (Exception e) {
            log.error("影像平台-->根据id更新流水号出现问题:ErrorMessage:{},id:{}", e, id);
            throw e;
        } finally {
            PubFunc.closeDbObj(conn);
        }
        JSONObject escMessage = new JSONObject();
        JSONObject requestData = new JSONObject();

        JSONObject systemHeader = new JSONObject();//业务数据系统头
        systemHeader.put("sourceSystemCode", "HRS");//请求方系统码
        systemHeader.put("sinkSystemCode", "ias");//目标方系统码
        systemHeader.put("actionId", "NIA2000102");//交易码
        systemHeader.put("actionVersion", "v1");//交易码版本 默认v1

        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String timestamp = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeader.put("sourceJnlNo", sourceJnlNo);//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）
        systemHeader.put("glblSrvNo", sNo);//全局流水号 雪花算法
        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS

        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        requestData.put("systemHeader", systemHeader);

        JSONObject body = new JSONObject();
        body.put("tranCode", tranCode);//交易码
        body.put("sysId", "HRS");//系统来源
        body.put("busiNo", busiNo);//业务流水号  全行唯一流水号+子序号 todo 5
        body.put("operaId", "su"); //操作员编号
//      body.put("branchNo", "");
//      body.put("fileName", "");//文件名
        body.put("fileType", extension);//文件类型
//      body.put("billType", "");//影像类型
        body.put("isOcr", "0");//是否识别
        body.put("templateNo", "");//OCR模板编号
//      body.put("remark", "");//备注信息
        body.put("escFilePath", escFilePath);//esc文件路径

        requestData.put("body", body);

        escMessage.put("requestData", requestData);
        return escMessage.toString();
    }

    /**
     * 组装影像平台下载、删除 JSON报文
     *
     * @param tranCode
     * @param actionId
     * @param fileId
     * @return
     * @throws SQLException
     * @throws GeneralException
     */
    private String packMediaOtherJson(String tranCode, String actionId, String fileId, String sNo) throws SQLException, GeneralException {
        //根据fiedldc查询业务流水号
        Connection conn = null;
        RowSet rs = null;
        String busiNo = "";
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            List list = new ArrayList<>();
            list.add(fileId);
            rs = dao.search("select busiNo from vfs_file_mapping where filelocation = ?", list);
            if (rs.next()) {
                busiNo = rs.getString("busino");
            }
        } catch (Exception e) {
            log.error("影像平台-->组装报文获取业务流水号出现问题 ErrorMessage:{} fileId:{}", e.getMessage(), fileId);
            throw e;
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(conn);
        }
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
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        systemHeader.put("sourceJnlNo", sourceJnlNo);//请求方流水号 交易日期(8位：YYYYMMDD) + 请求方流水序号（12 位）
        systemHeader.put("glblSrvNo", sNo);//全局流水号 雪花算法
        systemHeader.put("timestamp", timestamp);//请求发送时间戳 yyyy-MM-dd HH:mm:ss.SSS
        systemHeader.put("ip", SystemConfig.getPropertyValue("hrpserver"));//发送请求机器ip
        requestData.put("systemHeader", systemHeader);

        JSONObject body = new JSONObject();
        body.put("tranCode", tranCode);//交易码
        body.put("sysId", "HRS");//系统来源
        body.put("busiNo", busiNo);//业务流水号  全行唯一流水号
        if ("2002".equals(tranCode)) {//影像获取
            body.put("fileId", fileId);//影像ID
            body.put("tarSysId", "");//影像ID
        } else if ("2003".equals(tranCode)) {//影像删除
            body.put("fileId", fileId);//影像ID
            body.put("operaId", "su");
        }/* else if ("2004".equals(tranCode)) {//影像替换
            body.put("operaId", "su");
            body.put("fileName", "");
            body.put("fileType", "");
            body.put("sourceFileId", "");
            body.put("targetFile", "");
        }*/
        requestData.put("body", body);

        escMessage.put("requestData", requestData);
        return escMessage.toString();
    }

    /**
     * 调用ESC影像上传接口
     *
     * @param escMessage
     * @param url
     * @return
     * @throws Exception
     */
    private String callEscUpload(String escMessage, String url, String sNo) throws Exception {
        String fileId = "";
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        HttpClient httpClient = new HttpClient();
        PostMethod httpPost = new PostMethod(url);
        httpPost.addRequestHeader("Content-type", "application/json");
        httpPost.addRequestHeader("Charset", "UTF-8");
        httpPost.addRequestHeader("Accept", "application/json");
        httpPost.addRequestHeader("Accept-Charset", "UTF-8");
        String macVal = MacAddressUtil.getMacAddress(escMessage, "GZYH.NIAA_node.zak");
        httpPost.addRequestHeader("X-GZB-mac", macVal);//请求数据 mac 值(从全密码平台获取)
        httpPost.addRequestHeader("X-GZB-sourceSystemCode", "HRS");//请求方系统代码
        httpPost.addRequestHeader("X-GZB-jnlNo", sourceJnlNo);//业务流水号
        httpPost.addRequestHeader("X-GZB-actionId", "NIA2000102");//交易码
        String string = "";//返回报文
        try {
            httpPost.setRequestEntity(new StringRequestEntity(escMessage, "application/json", "UTF-8"));
            int status = httpClient.executeMethod(httpPost);//发送请求
            if (status == HttpStatus.SC_OK) {
                string = httpPost.getResponseBodyAsString();
                if (httpPost.getResponseHeader("X-GZB-mac") != null) {
                    String X_GZB_mac = httpPost.getResponseHeader("X-GZB-mac").getValue();
                    MacAddressUtil.verifyMacAddress(string, "GZYH.HRSA_node.zak", X_GZB_mac);
                }
                JSONObject jsonObject = JSONObject.fromObject(string);
                String responseCode = jsonObject.getString("responseCode");
                String responseMessage = jsonObject.getString("responseMessage");
                if ("000000".equals(responseCode)) {
                    JSONObject body = jsonObject.getJSONObject("responseData").getJSONObject("body");
                    //String filelocation = (String) body.get("fileUrl");
                    fileId = (String) body.get("fileId");
                } else {
                    SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, jsonObject.toString());
                    throw new Exception(responseMessage);
                }

            } else {
                throw new Exception("影像平台-->调用ESC上传接口失败!");
            }
        } catch (Exception e) {
            log.error("影像平台-->文件上传失败:ErrorMessage{}", e.getMessage());
            log.error("影像平台-->文件上传请求地址url:{},请求报文:param:{},返回报文:string:{}", url, escMessage, string);
            throw e;
        }
        return fileId;
    }

    /**
     * 调用ESC影像下载接口
     *
     * @param escMessage
     * @param url
     * @return
     * @throws Exception
     */
    private String callEscDownload(String escMessage, String url, String sNo) throws Exception {
        String filelocation = "";
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        HttpClient httpClient = new HttpClient();
        PostMethod httpPost = new PostMethod(url);
        httpPost.addRequestHeader("Content-type", "application/json");
        httpPost.addRequestHeader("Charset", "UTF-8");
        httpPost.addRequestHeader("Accept", "application/json");
        httpPost.addRequestHeader("Accept-Charset", "UTF-8");
        String macVal = MacAddressUtil.getMacAddress(escMessage, "GZYH.NIAA_node.zak");
        httpPost.addRequestHeader("X-GZB-mac", macVal);//请求数据 mac 值(从全密码平台获取)
        httpPost.addRequestHeader("X-GZB-sourceSystemCode", "HRS");//请求方系统代码
        httpPost.addRequestHeader("X-GZB-jnlNo", sourceJnlNo);//业务流水号
        httpPost.addRequestHeader("X-GZB-actionId", "NIA2000103");//交易码
        String string = "";//返回报文
        try {
            httpPost.setRequestEntity(new StringRequestEntity(escMessage, "application/json", "UTF-8"));
            int status = httpClient.executeMethod(httpPost);//发送请求
            if (status == HttpStatus.SC_OK) {
                string = httpPost.getResponseBodyAsString();
                if (httpPost.getResponseHeader("X-GZB-mac") != null) {
                    String X_GZB_mac = httpPost.getResponseHeader("X-GZB-mac").getValue();
                    MacAddressUtil.verifyMacAddress(string, "GZYH.HRSA_node.zak", X_GZB_mac);
                }
                JSONObject jsonObject = JSONObject.fromObject(string);
                String responseCode = jsonObject.getString("responseCode");
                String responseMessage = jsonObject.getString("responseMessage");
                if ("000000".equals(responseCode)) {
                    JSONObject body = jsonObject.getJSONObject("responseData").getJSONObject("body");
                    String fileInfo = (String) body.get("fileInfo");
                    JSONObject obj = JSONArray.fromObject(fileInfo).getJSONObject(0);
                    filelocation = obj.getString("fileUrl");
                } else {
                    SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, jsonObject.toString());
                    throw new Exception(responseMessage);
                }

            } else {
                throw new Exception("影像平台-->调用ESC下载接口失败!");
            }
        } catch (Exception e) {
            log.error("影像平台-->文件下载失败ErrorMessage:{}", e.getMessage());
            log.error("影像平台-->文件下载请求地址为 url:{},调用报文:escMessage:{},返回报文string:{}", url, escMessage, string);
            throw e;
        }
        return filelocation;
    }

    /**
     * 调用ESC影像删除接口
     *
     * @param escMessage
     * @param url
     * @return
     * @throws Exception
     */
    private boolean callEscDelete(String escMessage, String url, String sNo) throws Exception {
        boolean isSucceed = false;
        Date date = new Date();
        String datetime = DateUtils.format(date, "yyyyMMddHHmmssSSS");
        String sourceJnlNo = "HRS0" + datetime.substring(4, 8) + Math.round(Math.ceil(Math.random() * 1000)) + datetime.substring(8, 17) + Math.round(Math.ceil(Math.random() * 1000));
        HttpClient httpClient = new HttpClient();
        PostMethod httpPost = new PostMethod(url);
        httpPost.addRequestHeader("Content-type", "application/json");
        httpPost.addRequestHeader("Charset", "UTF-8");
        httpPost.addRequestHeader("Accept", "application/json");
        httpPost.addRequestHeader("Accept-Charset", "UTF-8");
        String macVal = MacAddressUtil.getMacAddress(escMessage, "GZYH.NIAA_node.zak");
        httpPost.addRequestHeader("X-GZB-mac", macVal);//请求数据 mac 值(从全密码平台获取)
        httpPost.addRequestHeader("X-GZB-sourceSystemCode", "HRS");//请求方系统代码
        httpPost.addRequestHeader("X-GZB-jnlNo", sourceJnlNo);//业务流水号
        httpPost.addRequestHeader("X-GZB-actionId", "NIA2000104");//交易码
        String string = "";//返回报文
        try {
            httpPost.setRequestEntity(new StringRequestEntity(escMessage, "application/json", "UTF-8"));
            int status = httpClient.executeMethod(httpPost);//发送请求
            if (status == HttpStatus.SC_OK) {
                string = httpPost.getResponseBodyAsString();
                if (httpPost.getResponseHeader("X-GZB-mac") != null) {
                    String X_GZB_mac = httpPost.getResponseHeader("X-GZB-mac").getValue();
                    MacAddressUtil.verifyMacAddress(string, "GZYH.HRSA_node.zak", X_GZB_mac);
                }
                JSONObject jsonObject = JSONObject.fromObject(string);
                String responseCode = jsonObject.getString("responseCode");
                String responseMessage = jsonObject.getString("responseMessage");
                if ("000000".equals(responseCode)) {
                    isSucceed = true;
                } else if ("ESCERR0041".equals(responseCode)) {//代表服务提供方不可用
                    isSucceed = false;
                    log.error("影像平台-->文件删除失败,请求地址为 url:{},请求报文escMessage:{},返回报文string:{}", url, escMessage, string);
                } else {
                    SendKafkaGlblSrvNo.updateGlbSrlNo(sNo, "1", responseCode, jsonObject.toString());
                    log.info("影像平台-->文件删除请求地址为 url:{},请求报文escMessage:{},返回报文string:{}", url, escMessage, string);
                    //删除影像 返回其他状态表示影像不存在，也返回true对本地记录进行删除
                    //VFS_FILE_MAPPING  status   0:上传中、1:上传失败、2:已完成、3:已删除
                    isSucceed = true;
                }

            } else {
                throw new Exception("影像平台-->删除调用ESC接口失败!");
            }
        } catch (Exception e) {
            log.error("影像平台-->文件删除失败:ErrorMessage:{}", e.getMessage());
            log.error("影像平台-->文件删除请求地址为 url:{},请求报文escMessage:{},返回报文string:{}", url, escMessage, string);
            throw e;
        }
        return isSucceed;
    }

    /**
     * YYYYMMDDHHMMSS+6位自增长码(20位)
     *
     * @return
     */
    public static synchronized String getLocalTrmSeqNum() {
        sequence = sequence >= 999999 ? 1 : sequence + 1;
        String datetime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String s = Integer.toString(sequence);
        return datetime + addLeftZero(s, length);
    }

    /**
     * 左填0
     *
     * @param s
     * @param length
     * @return
     */
    public static String addLeftZero(String s, int length) {
        int old = s.length();
        if (length > old) {
            char[] c = new char[length];
            char[] x = s.toCharArray();
            if (x.length > length) {
                throw new IllegalArgumentException(
                        "Numeric value is larger than intended length: " + s
                                + " LEN " + length);
            }
            int lim = c.length - x.length;
            for (int i = 0; i < lim; i++) {
                c[i] = '0';
            }
            System.arraycopy(x, 0, c, lim, x.length);
            return new String(c);
        }
        return s.substring(0, length);

    }

}