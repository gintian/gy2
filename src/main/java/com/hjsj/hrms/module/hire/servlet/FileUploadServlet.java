package com.hjsj.hrms.module.hire.servlet;

import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.servlet.ParameterRequestWrapper;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * 招聘外网上传简历附件servlet
 * 2019/11/2
 * @author Administrator
 */
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        UserView userView = (UserView)request.getSession().getAttribute(WebConstant.userView);
        boolean sessionFlag=false;
        //火狐和safari 取不到session 上传控件初始化时 走rpc 判断当前操作人是否为空
        String safariOrFoxType=request.getParameter("safariORFoxType");
        //标识上传照片
        String option="";
        String filenameEncode = "";
        try {
        	option=request.getParameter("option");
        	option = SafeCode.decode(option);
        	option = PubFunc.hireKeyWord_filter_reback(option);
            //避免金蝶中间件文件名乱码
            filenameEncode = request.getParameter("filenameEncode");
            filenameEncode = SafeCode.decode(filenameEncode);
            filenameEncode = PubFunc.hireKeyWord_filter_reback(filenameEncode);
        } catch (Exception e) {
            //兼容前端没有单独传文件名参数的情况
        }
        
        if(safariOrFoxType!=null&&safariOrFoxType.length()>8&&request.getParameter("datems")!=null){
            Long times=Long.parseLong(request.getParameter("datems"));
            Long nowTime=System.currentTimeMillis();
            //判断火狐或者safari 是否点击文件上传是否超时 2分钟之内不超时
            if((nowTime-times)<=120000&&"true".equals(PubFunc.decrypt(safariOrFoxType))){
                        sessionFlag=true;
            }
        }
        
        /*登陆校验 guodd 2018-11-01*/
        if(userView==null && !sessionFlag) {
            return;
        }

        /**
         * 上传
         */
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException(
                    "请求不是multipart，请将form的enctype属性设置为'multipart/form-data'");
        }
        ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
        // 解决乱码问题
        response.setContentType("text/plain;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        Writer writer = response.getWriter();
        FileItem item = null;
        Object partItem = null;
        InputStream input = null;
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ResumeBo bo = new ResumeBo(conn, userView);
            //文件名称长度限制
            int maxLength = 100;
            //文件大小限制MB
            int maxFileSize = 0;
            //上传照片大小 KB
            int photo_size = 512;
            ParameterXMLBo parmeXmlBo=new ParameterXMLBo(conn);
            HashMap map = parmeXmlBo.getAttributeValues();
            if(map != null && map.get("maxFileSize") != null) {
                maxFileSize = Integer.parseInt((String)map.get("maxFileSize"));
            }
            
            maxFileSize = maxFileSize == 0 ? 10 : maxFileSize;
            long fileSize = 0;
            //String fileName = "";
            String ext = "";
            //招聘附件分类
            String attachCodeItemid = "";
            
            boolean isServlet3 = false;
            
            List items = uploadHandler.parseRequest(request);
            
            isServlet3 = items==null || items.size()==0;
            if (isServlet3) {
                //servlet是3.0版本，需要按照新的方式获取文件    
                ServletRequest requestWrapper = ((ParameterRequestWrapper) request).getRequest();
                Class<?> tClass = requestWrapper.getClass();
                Method declaredMethod = tClass.getDeclaredMethod("getParts");
                items = (List)declaredMethod.invoke(requestWrapper);
            }
            
            
            for (int i = 0; i < items.size(); i++) {
                String fileName = "";
                if (!isServlet3) {
                    item = (FileItem) items.get(i);
                    if (item.isFormField())
                        continue;
                    
                    attachCodeItemid = item.getFieldName();
                    //兼容老版本外网没有传入转码的文件名情况
                    fileName = item.getName();
                    if (StringUtils.isBlank(fileName)) {
                        fileName = filenameEncode;
                    }
                    
                    fileSize = item.getSize();
                    input = item.getInputStream();
                } else {
                    partItem = items.get(i);
                    
                    Method getFieldNameMethod = partItem.getClass().getMethod("getFieldName");
                    attachCodeItemid = (String)getFieldNameMethod.invoke(partItem);
                    
                    Method isFieldFormMethod = partItem.getClass().getMethod("isFormField");
                    boolean isFormField = (Boolean)isFieldFormMethod.invoke(partItem);
                    //不是文件
                    if (isFormField) {
                        continue;
                    }
                    
                    //兼容老版本外网没有传入转码的文件名情况
                    if (StringUtils.isBlank(fileName)) {
                        Method getFileNameMethod = partItem.getClass().getMethod("getFileName");
                        fileName = (String)getFileNameMethod.invoke(partItem);
                        //招聘外网为utf-8编码
                        fileName = new String(fileName.getBytes("GBK"), "utf-8");
                    }
                    
                    Method getSizeMethod = partItem.getClass().getMethod("getSize");
                    fileSize = (Long)getSizeMethod.invoke(partItem);
                    
                    Method getInputStream= partItem.getClass().getDeclaredMethod("getInputStream");
                    input= (InputStream) getInputStream.invoke(partItem);
                }
                
                fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
                ext = fileName.substring(fileName.lastIndexOf(".")+1);
                
                //判断文件名长度是否超出限制的长度
                int fileNameLength = fileName.replaceAll("[^\\x00-\\xff]", "**").length();
                if(fileNameLength>maxLength) {
                    item=null;
                    JSONObject json=new JSONObject();
                    json.put("successed", "fail");
                    json.put("msg", "文件名长度超过"+maxLength+"个字符(1个汉字占2个字符),不允许上传!");
                    writer.write(json.toString());
                    throw GeneralExceptionHandler.Handle(new Exception("文件名长度超过"+maxLength+"个字符,不允许上传!"));
                }
                
                if(0==fileSize){
                    item=null;
                    JSONObject json=new JSONObject();
                    json.put("successed", "fail");
                    json.put("msg", "文件大小为0kb,不允许上传！");
                    writer.write(json.toString());
                    throw GeneralExceptionHandler.Handle(new Exception("文件大小为0kb,不允许上传！"));
                }
                
                if(maxFileSize*1024*1024<fileSize&&!"uploadPhoto".equalsIgnoreCase(option)){
                    item=null;
                    JSONObject json=new JSONObject();
                    json.put("successed", "fail");
                    json.put("msg", "文件过大，超过"+maxFileSize+"MB,不允许上传！");
                    writer.write(json.toString());
                    throw GeneralExceptionHandler.Handle(new Exception("文件过大，超过"+maxFileSize+"MB,不允许上传！"));
                }else if(photo_size*1024<fileSize&&"uploadPhoto".equalsIgnoreCase(option)){
                	item=null;
                    JSONObject json=new JSONObject();
                    json.put("successed", "fail");
                    json.put("msg", "文件过大，超过"+photo_size+"KB,不允许上传！");
                    writer.write(json.toString());
                    throw GeneralExceptionHandler.Handle(new Exception("文件过大，超过"+photo_size+"KB,不允许上传！"));
                }
                //判断文件后缀是否跟文件类型相符合 并且  进行白名单校验 guodd 2020-04-21*/
                boolean isOk = FileTypeUtil.isFileTypeEqual(input,ext);
                if(!isOk || SystemConfig.getAllowExt().indexOf(","+ext.toLowerCase()+",")==-1) {
                    item=null;
                    JSONObject json=new JSONObject();
                    json.put("successed", "fail");
                    json.put("msg", ResourceFactory.getProperty("error.common.upload.invalid"));
                    writer.write(json.toString());
                    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
                }
                
                String successed = "";
                //上面获取完文件类型输入流会被关掉
                if (!isServlet3) {
                    input = item.getInputStream();
                } else {
                    Method getInputStream= partItem.getClass().getDeclaredMethod("getInputStream");
                    input= (InputStream) getInputStream.invoke(partItem);
                }
                //上传照片
                if("uploadPhoto".equalsIgnoreCase(option)) {
                	successed = bo.savePhoto(fileName, input, "1");
                }else {
                	//上传简历附件
                	if(!"file".equalsIgnoreCase(attachCodeItemid)) {
                		successed = bo.uploadAttachCodeSetFiles(fileName, input, attachCodeItemid);
                	} else {
                		successed = bo.uploadOthFiles(fileName, input);
                	}
                }
                
                JSONObject json=new JSONObject();
                json.put("successed", successed);
                writer.write(json.toString());
            }
            
        } catch (FileUploadException e) {
            throw new RuntimeException("上传过程中发生异常中断：" + e);
        } catch (Exception e) {
            if (item != null) {
                JSONObject json=new JSONObject();
                json.put("successed", "fail");
                json.put("msg", e.getMessage());
                writer.write(json.toString());
            }
            throw new RuntimeException(e);
        } finally {
            PubFunc.closeResource(input);
            PubFunc.closeResource(conn);
            writer.close();
        }

    }

}
