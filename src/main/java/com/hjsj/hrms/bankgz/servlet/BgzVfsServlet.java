package com.hjsj.hrms.bankgz.servlet;

import com.hjsj.hrms.bankgz.utils.BankGzUtils;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.hjadmin.api.ResponseCodeEnum;
import com.hrms.hjsj.hjadmin.api.ResponseFactory;
import com.hrms.hjsj.hjadmin.api.RetResult;
import com.hrms.hjsj.hjadmin.cache.CacheUtil;
import com.hrms.hjsj.hjadmin.cache.FrameworkCacheKeysEnum;
import com.hrms.hjsj.hjadmin.util.JwtUtil;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * 文件处理 servlet
 * url： /servlet/vfsservlet
 *
 * @author zhangh
 */
public class BgzVfsServlet extends HttpServlet {
    // 日志文件
    private Logger log = LoggerFactory.getLogger(BgzVfsServlet.class);
    /**
     * 使用get请求进行文件下载操作
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
        InputStream inputStream1  = request.getInputStream();
        if (userView == null) {
            String authorization = request.getParameter("auth");
            if (StringUtils.isNotBlank(authorization)) {
                BankGzUtils bankGzUtils = new BankGzUtils();
                userView = bankGzUtils.getUserViewByCache(authorization);
                if (userView == null) {//如果还是为null,则意味着该被清理了,根据贵银的数据重新创建吧
                    String onlyLogonFieldValue = request.getParameter("logonFiledValue");
                    try {
//                        String isSuccess = bankGzUtils.checkBankToken(bankToken, ip, mac, onlyLogonFieldValue);
                        if (true) {
                            Map<String, Object> authorResult = bankGzUtils.authorByBank(onlyLogonFieldValue);
                            boolean error = (boolean) authorResult.get("error");
                            if (!error) {
                                Map<String, String> returnUserInfoMap = (Map<String, String>) authorResult.get("userInfo");
                                authorization = returnUserInfoMap.get("tkaccount");
                                //刚生成的是肯定有的
                                userView = (UserView) CacheUtil.get(FrameworkCacheKeysEnum.userViewCache, JwtUtil.parseJWT(authorization, JwtUtil.TOKENTYPE_ACCESS_TOKEN).getSubject());
                            }else{
                                //如果出现错误信息,则向前台抛出错误结果，错误由前端处理展现
                                String msg = (String) authorResult.get("msg");
                                RetResult retResult = new RetResult(msg, ResponseCodeEnum.businessException, null);
                                ResponseFactory.buildResponseSuccess(response, retResult);
                                return;
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        String userName = "";
        if (userView != null) {
            userName = userView.getUserName();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean sessionFlag = false;
        //火狐和safari 取不到session 上传控件初始化时 走rpc 判断当前操作人是否为空
        String safariORFoxType = request.getParameter("safariORFoxType");
        if (safariORFoxType != null && safariORFoxType.length() > 8 && request.getParameter("datems") != null) {
            Long times = Long.parseLong(request.getParameter("datems"));
            Long nowTime = System.currentTimeMillis();
            if ((nowTime - times) <= 120000 && "true".equals(PubFunc.decrypt(safariORFoxType))) {//判断火狐或者safari 是否点击文件上传是否超时 2分钟之内不超时
                sessionFlag = true;
            }
        }

        //获取需要下载的文件id（也有可能是本地文件路径，为了兼容旧程序）
        String fileid = "";
        //文件名
        String fileName = "";
        //文件路径
        String filePath = "";
        VfsFileEntity vfsFileEntity = null;
        //模块id
        String moduleid = "";
        try {
            fileid = request.getParameter("fileid");
            //解密fileid，判断传入的是文件id，还是相对路径
            filePath = PubFunc.decrypt(fileid);
            if (StringUtils.isBlank(fileid) || StringUtils.isBlank(filePath)) {
                //没有传入文件id时，后续操作也就不用做了，直接返回
                throw GeneralExceptionHandler.Handle(new Exception("文件id或路径不能为空！"));
            }
            //检查传入的路径中是否包含..，避免恶意切换到其他路径
            if (fileid.contains("..") || filePath.contains("..")) {
                throw GeneralExceptionHandler.Handle(new Exception("文件路径不允许包含.."));
            }
            //首先根据传入的文件id获取模块id，判断是否是不需要登录就可以下载的文件
            if (filePath.contains("/") || filePath.contains("\\") || filePath.contains(".")) {
                filePath = filePath.replace("\\", "/");
                String[] arr = filePath.split("/");
                if (arr != null && arr.length > 0) {
                    fileName = arr[arr.length - 1];
                }
            } else {
                vfsFileEntity = VfsService.getFileEntity(fileid);
                if (vfsFileEntity != null) {
                    moduleid = vfsFileEntity.getModuleid();
                    fileName = vfsFileEntity.getName();
                }
            }
            //NOLOGIN模块不需要登录就可以下载
            if (StringUtils.isBlank(moduleid) || !"NOLOGIN".equalsIgnoreCase(moduleid)) {
                //验证用户有没有登录
                if (userView == null && !sessionFlag) {
                    throw GeneralExceptionHandler.Handle(new Exception("用户未登录，请先登录后再进行操作！"));
                }
                if (userView != null && userView.getHm().containsKey("isEmployee")) {
                    throw GeneralExceptionHandler.Handle(new Exception("用户未登录，请先登录后再进行操作！"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //是否是java.io.temdir临时目录中的文件
        String fromjavafolder = request.getParameter("fromjavafolder");
        //是否是日志文件
        String islog = request.getParameter("islog");
        //文件扩展名
        String extension = "";
        String imageResize = request.getParameter("imageResize");// 图片大小参数 控制输出的图片大小（用于显示圆形照片） 格式例如55`55 hej 2016-03-11
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            //日志需要单独下载，无需走VFS
            if (StringUtils.isNotBlank(islog) && "true".equalsIgnoreCase(islog)) {
                //获取system中配置的日志路径
                filePath = SystemConfig.getPropertyValue("sql_log_file");
                //当前系统路径分割符号
                String sysFileSep = System.getProperty("file.separator");

                getPath:
                if (filePath != null && !"".equals(filePath)) {
                    filePath = filePath.replace("\\", "/");
                    filePath = filePath.replace("/", sysFileSep);
                } else {
                    //读取 log4j 中配置的日志文件路径
                    ResourceBundle bundle = ResourceBundle.getBundle("log4j");
                    if (!bundle.containsKey("log4j.appender.file.File"))
                        break getPath;
                    filePath = bundle.getString("log4j.appender.file.File");
                    filePath = new String(filePath.getBytes("ISO-8859-1"), "gb2312");
                    filePath = filePath.replace("\\", "/");
                    filePath = filePath.replace("/", sysFileSep);
                }

                if (filePath != null && !"".equals(filePath)) {
                    //如果使用了变量，解析变量
                    if (filePath.startsWith("${catalina.base}")) {
                        filePath = System.getProperty("catalina.base") + filePath.substring(16);
                    }
                    if (filePath.startsWith("${catalina.home}")) {
                        filePath = System.getProperty("catalina.home") + filePath.substring(16);
                    }
                    //获取文件所在路径，并判断路径是否合法
                    String pruePath = filePath.substring(0, filePath.lastIndexOf(sysFileSep) + 1);
                    pruePath += fileName;
                    File file = new File(pruePath);
                    if (file.exists()) {
                        inputStream = new FileInputStream(file);
                    }
                }
            } else {
                //调用VFS获取文件
                if (StringUtils.isNotBlank(fromjavafolder) && "true".equalsIgnoreCase(fromjavafolder)) {
                    inputStream = VfsService.getTempFile(fileid);
                } else {
                    inputStream = VfsService.getFile(fileid);
                }
            }
            //如果指定的文件不存在，或者没有拿到流，抛出异常信息
            if (inputStream == null) {
                throw GeneralExceptionHandler.Handle(new Exception("文件不存在或者下载文件失败！"));
            }
            outputStream = response.getOutputStream();
            //对图片进行压缩
            if (StringUtils.isNotBlank(imageResize)) {
                resizeImage(fileName, inputStream, outputStream, imageResize);
            } else {
                //根据文件名获取文件扩展名
                extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                //设置输出的文件类型流
                response.setContentType(ServletUtilities.getMimeType("." + extension));
                //告诉浏览器，要下载的文件名,并且避免中文名称乱码
                //fileName = new String(fileName.getBytes("gb2312"),"ISO8859_1");
                fileName = URLEncoder.encode(fileName, "UTF-8");
                response.setHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");
                response.addHeader("Content-description", fileName);
                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = inputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, len);
                }
            }

            //下载成功，记录成功状态
            response.setStatus(HttpServletResponse.SC_OK);
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                PubFunc.closeIoResource(inputStream);
            }
            if (outputStream != null) {
                outputStream.flush();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }

    /**
     * 使用post请求进行文件上传操作
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("请求不是multipart，请将form的enctype属性设置为'multipart/form-data'");
        }
        UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
        if (userView == null) {
            String authorization = request.getHeader(JwtUtil.DEFAULT_JWT_PARAM);
            if (StringUtils.isNotBlank(authorization)) {
                BankGzUtils bankGzUtils = new BankGzUtils();
                userView = bankGzUtils.getUserViewByCache(authorization);
                log.info("统一门户传入token: {}",authorization);
                if (userView == null) {//如果还是为null,则意味着该被清理了,根据贵银的数据重新创建吧
                    String onlyLogonFieldValue = request.getParameter("logonFiledValue");
                    String bankToken = request.getHeader("bankToken");
                    String ip = request.getHeader("ip");
                    String mac = request.getHeader("mac");
                    log.info("vfs_request onlyLogonFieldValue: {},bankToken{},ip{},mac{}", onlyLogonFieldValue,bankToken,ip,mac);
                    try {
                        String isSuccess = bankGzUtils.checkBankToken(bankToken, ip, mac, onlyLogonFieldValue);
                        if ("".equals(isSuccess)) {
                            Map<String, Object> authorResult = bankGzUtils.authorByBank(onlyLogonFieldValue);
                            boolean error = (boolean) authorResult.get("error");
                            if (!error) {
                                Map<String, String> returnUserInfoMap = (Map<String, String>) authorResult.get("userInfo");
                                authorization = returnUserInfoMap.get("tkaccount");
                                userView = (UserView) CacheUtil.get(FrameworkCacheKeysEnum.userViewCache, JwtUtil.parseJWT(authorization, JwtUtil.TOKENTYPE_ACCESS_TOKEN).getSubject());
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        String userName = "";
        if (userView != null) {
            userName = userView.getUserName();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Writer writer = response.getWriter();
        ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
        FileItem item = null;
        FileItem inputItem = null;
        HashMap paraMap = new HashMap();
        InputStream inputStream = null;
        InputStream tempStream = null;
        //文件名
        String fileName = "";
        //扩展名
        String ext = "";
        //文件大小
        long fileSize = 0L;
        Long MaxFileSizeNum = 0L;
        //文件大小上限
        int maxLength = 0;
        String fileNameMaxLength = "";
        String maxFileSize = "";
        String fileListId = "";
        String path = "";
        //加密的文件id
        String fileid = "";
        String username = "";
        String vfsFiletypeStr = "";
        String vfsModulesStr = "";
        String vfsCategoryStr = "";
        VfsFiletypeEnum vfsFiletypeEnum = null;
        VfsModulesEnum vfsModulesEnum = null;
        VfsCategoryEnum vfsCategoryEnum = null;
        String CategoryGuidKey = "";
        String filetag = "";
        String isTempFileStr = "";
        boolean isTempFile = false;
        try {
            username = userView.getUserName();
            List items = uploadHandler.parseRequest(request);
            if (items != null && items.size() > 0) {
                //servlet是低版本,按照原有的方式上传文件
                for (int i = 0; i < items.size(); i++) {
                    item = (FileItem) items.get(i);
                    if (item != null && item.isFormField()) {
                        //获取前台传递的参数
                        paraMap.put(item.getFieldName(), item.getString("utf-8"));
                    }
                    if (item != null && !item.isFormField()) {
                        fileName = URLDecoder.decode(item.getName(), "UTF-8");
                        ext = fileName.substring(fileName.lastIndexOf(".") + 1);
                        inputStream = item.getInputStream();
                        fileSize = item.getSize();
                        inputItem = item;
                    }
                }
                //获取文件id
                fileid = paraMap.containsKey("fileid") ? (String) paraMap.get("fileid") : "";
                vfsFiletypeStr = paraMap.containsKey("VfsFiletype") ? (String) paraMap.get("VfsFiletype") : "";
                vfsModulesStr = paraMap.containsKey("VfsModules") ? (String) paraMap.get("VfsModules") : "";
                vfsCategoryStr = paraMap.containsKey("VfsCategory") ? (String) paraMap.get("VfsCategory") : "";
                if (StringUtils.isBlank(vfsFiletypeStr) || StringUtils.isBlank(vfsModulesStr) || StringUtils.isBlank(vfsCategoryStr)) {
                    JSONObject json = new JSONObject();
                    json.put("successed", false);
                    json.put("msg", "文件类型、模块号、文件所属类型都不能为空!");
                    json.put("fileListId", fileListId);
                    writer.write(json.toString());
                    throw GeneralExceptionHandler.Handle(new Exception("文件类型、模块号、文件所属类型都不能为空!"));
                }
                CategoryGuidKey = paraMap.containsKey("CategoryGuidKey") ? (String) paraMap.get("CategoryGuidKey") : "";
                filetag = paraMap.containsKey("filetag") ? (String) paraMap.get("filetag") : "";
                isTempFileStr = paraMap.containsKey("isTempFile") ? (String) paraMap.get("isTempFile") : "";
                if ("true".equalsIgnoreCase(isTempFileStr)) {
                    //是否是临时文件
                    isTempFile = true;
                }
                vfsFiletypeEnum = VfsFiletypeEnum.valueOf(vfsFiletypeStr);
                vfsModulesEnum = VfsModulesEnum.valueOf(vfsModulesStr);
                vfsCategoryEnum = VfsCategoryEnum.valueOf(vfsCategoryStr);
                path = paraMap.containsKey("savePath") ? (String) paraMap.get("savePath") : "";
                fileListId = paraMap.containsKey("fileListId") ? (String) paraMap.get("fileListId") : "null";
                //由于ie不会转码文件名，谷歌会将文件名转码，所以直接获取前台加密传入的，不再使用浏览器默认的。
                if (paraMap.containsKey("fileName")) {
                    fileName = SafeCode.decode((String) paraMap.get("fileName"));
                }
                //文件名称长度限制
                fileNameMaxLength = paraMap.containsKey("fileNameMaxLength") && StringUtils.isNotBlank((String) paraMap.get("fileNameMaxLength")) ? (String) paraMap.get("fileNameMaxLength") : "100";
                fileNameMaxLength = fileNameMaxLength != null && fileNameMaxLength.length() > 0 ? fileNameMaxLength : "100";
                maxLength = Integer.parseInt(fileNameMaxLength);

                //文件大小限制
                if (paraMap.containsKey("maxFileSize") && StringUtils.isNotBlank((String) paraMap.get("maxFileSize"))) {
                    maxFileSize = (String) paraMap.get("maxFileSize");
                    MaxFileSizeNum = Long.parseLong(maxFileSize);
                }
            } else {
                //servlet是3.0版本，需要按照新的方式获取文件
                Class<?> tClass = request.getClass();
                Method declaredMethod = tClass.getMethod("getParts");
                Collection parts = (Collection) declaredMethod.invoke(request);
                for (Object part : parts) {
                    //是否是表单字段
                    boolean isFormField = true;
                    Method getHeader = part.getClass().getMethod("getHeader", String.class);
                    String content = (String) getHeader.invoke(part, "content-disposition");
                    String[] header = content.split(";");
                    for (String headerStr : header) {
                        //前面默认多个空格，需要去除一下再判断
                        if (headerStr.trim().startsWith("filename=")) {
                            isFormField = false;
                            //fileName = headerStr.trim().replace("filename=","").replace("\"","");
                        }
                    }
                    if (!isFormField) {
                        Method getInputStream = part.getClass().getMethod("getInputStream");
                        inputStream = (InputStream) getInputStream.invoke(part);
                        tempStream = inputStream;
                        Method getSize = part.getClass().getMethod("getSize");
                        fileSize = (long) getSize.invoke(part);
                    }
                }
                fileid = request.getParameter("fileid");
                vfsFiletypeStr = request.getParameter("VfsFiletype");
                vfsModulesStr = request.getParameter("VfsModules");
                vfsCategoryStr = request.getParameter("VfsCategory");
                CategoryGuidKey = request.getParameter("CategoryGuidKey");
                filetag = request.getParameter("filetag");
                isTempFileStr = request.getParameter("isTempFile");
                log.info("统一门户上传附件传入参数fileid:{},VfsFiletype:{},VfsModules:{},VfsCategory:{},CategoryGuidKey:{},filetag:{},isTempFile:{}",fileid,vfsFiletypeStr,vfsModulesStr,vfsCategoryStr,CategoryGuidKey,filetag,isTempFileStr);
                if ("true".equalsIgnoreCase(isTempFileStr)) {
                    //是否是临时文件
                    isTempFile = true;
                }
                if (StringUtils.isBlank(vfsFiletypeStr) || StringUtils.isBlank(vfsModulesStr) || StringUtils.isBlank(vfsCategoryStr)) {
                    JSONObject json = new JSONObject();
                    json.put("successed", false);
                    json.put("msg", "文件类型、模块号、文件所属类型都不能为空!");
                    json.put("fileListId", fileListId);
                    writer.write(json.toString());
                    throw GeneralExceptionHandler.Handle(new Exception("文件类型、模块号、文件所属类型都不能为空!"));
                }
                vfsFiletypeEnum = VfsFiletypeEnum.valueOf(vfsFiletypeStr);
                vfsModulesEnum = VfsModulesEnum.valueOf(vfsModulesStr);
                vfsCategoryEnum = VfsCategoryEnum.valueOf(vfsCategoryStr);
                fileListId = request.getParameter("fileListId");
                fileName = request.getParameter("fileName");
                fileName = SafeCode.decode(fileName);
                ext = fileName.substring(fileName.lastIndexOf(".") + 1);
                path = request.getParameter("savePath");
                //文件名称长度限制
                fileNameMaxLength = request.getParameter("fileNameMaxLength");
                fileNameMaxLength = fileNameMaxLength != null && fileNameMaxLength.length() > 0 ? fileNameMaxLength : "100";
                maxLength = Integer.parseInt(fileNameMaxLength);
                //文件大小限制
                maxFileSize = request.getParameter("maxFileSize");
                if (StringUtils.isNotBlank(maxFileSize)) {
                    MaxFileSizeNum = Long.parseLong(maxFileSize);
                }
            }
            //判断文件名长度是否超出限制的长度
            int fileNameLength = fileName.replaceAll("[^\\x00-\\xff]", "**").length();
            if (fileNameLength > maxLength) {
                JSONObject json = new JSONObject();
                json.put("successed", false);
                json.put("msg", "文件名长度超过" + maxLength + "个字符(1个汉字占2个字符),不允许上传!");
                json.put("fileListId", fileListId);
                writer.write(json.toString());
                throw GeneralExceptionHandler.Handle(new Exception("文件名长度超过" + maxLength + "个字符,不允许上传!"));
            }
            //判断文件后缀是否跟文件类型相符合
            boolean isOk = FileTypeUtil.isFileTypeEqual(inputStream, ext);
            /*上传添加白名单校验 guodd 2020-04-21*/
            String extControl = SystemConfig.getAllowExt();
            if (!isOk || extControl.indexOf("," + ext.toLowerCase() + ",") == -1) {
                JSONObject json = new JSONObject();
                json.put("successed", false);
                json.put("msg", ResourceFactory.getProperty("error.common.upload.invalid"));
                json.put("fileListId", fileListId);
                writer.write(json.toString());
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
            }
            if (0 == fileSize) {
                JSONObject json = new JSONObject();
                json.put("successed", false);
                json.put("msg", "文件大小为0kb,不允许上传！");
                json.put("fileListId", fileListId);
                writer.write(json.toString());
                throw GeneralExceptionHandler.Handle(new Exception("文件大小为0kb,不允许上传！"));
            }
            //前端上传有文件大小限制时，判断文件大小是否超过限制;
            if (MaxFileSizeNum != 0L && MaxFileSizeNum < fileSize) {
                JSONObject json = new JSONObject();
                json.put("successed", false);
                json.put("msg", "文件过大，超过" + MaxFileSizeNum / 1024 + "kb,不允许上传！");
                json.put("fileListId", fileListId);
                writer.write(json.toString());
                throw GeneralExceptionHandler.Handle(new Exception("文件过大，超过" + MaxFileSizeNum / 1024 + "kb,不允许上传！"));
            }
            if (items == null || items.size() == 0) {
                //判断文件后缀是否跟文件类型相符合的操作把流给关掉了，需要重新打开流
                inputStream = tempStream;
            } else {
                inputStream = inputItem.getInputStream();
            }
            if (StringUtils.isNotBlank(fileid)) {
                fileid = VfsService.saveFile(username, fileid, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidKey, inputStream, fileName, filetag, isTempFile);
            } else {
                fileid = VfsService.addFile(username, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidKey, inputStream, fileName, filetag, isTempFile);
            }
            if (fileid.contains("error")) {
                //Vfs服务层新增、保存文件时也会有校验，判断是否校验出错，并返回给前台校验出错信息
                String errMsg = fileid.replace("error:", "");
                JSONObject json = new JSONObject();
                json.put("successed", false);
                json.put("msg", errMsg);
                json.put("fileListId", fileListId);
                writer.write(json.toString());
                throw GeneralExceptionHandler.Handle(new Exception(errMsg));
            }
            //解决中文名称传到前台乱码问题
            fileName = PubFunc.encrypt(fileName);
            writer.write("{\"successed\":\"" + System.currentTimeMillis() + "\",\"fileid\":\"" + fileid + "\",\"filename\":\"" + fileName + "\",\"fileListId\":\"" + fileListId + "\"}");
        } catch (Exception e) {
            JSONObject json = new JSONObject();
            json.put("successed", false);
            json.put("msg", e.getMessage());
            writer.write(json.toString());
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (tempStream != null) {
                tempStream.close();
            }
            writer.close();
        }
    }

    /**
     * 输出大小固定的图片（用于圆形图片显示）
     *
     * @param fileName
     * @param input
     * @param out
     * @param imageResize
     * @throws IOException
     */
    private void resizeImage(String fileName, InputStream input, OutputStream out, String imageResize) throws IOException {
        String fileExtName = fileName.substring((fileName.indexOf(".") + 1), fileName.length());
        int index = imageResize.indexOf('`');
        int width = Integer.parseInt(imageResize.substring(0, index));
        int height = Integer.parseInt(imageResize.substring(index + 1, imageResize.length()));
        BufferedImage prevImage = ImageIO.read(input);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.createGraphics();
        graphics.drawImage(prevImage, 0, 0, width, height, null);
        ImageIO.write(image, fileExtName, out);

    }
}
