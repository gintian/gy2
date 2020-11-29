package com.hjsj.hrms.utils.components.fileupload.servlet;

import com.hjsj.hrms.servlet.PhotoFileDeleter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileUploadServlet extends HttpServlet {


	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response) throws ServletException, IOException {

		UserView userView = (UserView)request.getSession().getAttribute(WebConstant.userView);
		boolean sessionFlag=false;
		//火狐和safari 取不到session 上传控件初始化时 走rpc 判断当前操作人是否为空
		String safariORFoxType=request.getParameter("safariORFoxType");
		if(safariORFoxType!=null&&safariORFoxType.length()>8&&request.getParameter("datems")!=null){
			Long times=Long.parseLong(request.getParameter("datems"));
			Long nowTime=new Date().getTime();
			if((nowTime-times)<=120000&&"true".equals(PubFunc.decrypt(safariORFoxType))){//判断火狐或者safari 是否点击文件上传是否超时 2分钟之内不超时
				sessionFlag=true;
			}
		}

		/*登陆校验 guodd 2018-11-01*/
		if(userView==null && !sessionFlag)
			return;
		if(userView!=null && userView.getHm().containsKey("isEmployee"))
			return;

		//删除
		String deleteflag = request.getParameter("deleteflag");
		if("true".equals(deleteflag)){
			String path = request.getParameter("path");
			path = PubFunc.decrypt(path);
			String fileName = request.getParameter("filename");
			fileName = PubFunc.decrypt(fileName);
			
			String fileid = request.getParameter("fileid");
			try{
				if(StringUtils.isNotBlank(fileid)) {
					VfsService.deleteFile(userView.getUserName(), fileid);
				}
//				path = path.replaceAll("\\\\", "/");
//				File dirs = new File(path,fileName);
//				if(dirs.exists()) {
//					dirs.delete();
//				}
			}catch(Exception e){
				e.printStackTrace();
			}

			return;
		}
		/**
		 * 下载
		 */
		String down = request.getParameter("down");
		if ("true".equals(down)) {
			String path = request.getParameter("path");
			path = PubFunc.decrypt(path);
			String fileName = request.getParameter("filename");
			fileName = PubFunc.decrypt(fileName);

			if("".equals(path) || "".equals(fileName))
				return;

			String localname = request.getParameter("localname");

			localname = SafeCode.decode(localname);
			localname = PubFunc.hireKeyWord_filter_reback(localname);

			String name ="";
			String ext ="";
			if (localname.lastIndexOf(".")>-1){
				name = localname.substring(0, localname.lastIndexOf("."));
				ext = localname.substring(localname.lastIndexOf(".") + 1);
			}
			else {
				name = localname;
			}

			InputStream inputstream = null;
			ServletOutputStream servletoutputstream = null;
			try {
				servletoutputstream = response.getOutputStream();
				response.setContentType(ServletUtilities.getMimeType("."+ext));

				name=new String((name+"_"+userView.getUserName()+"."+ext).getBytes("gb2312"),"ISO8859_1");
				response.setHeader("Content-disposition", "attachment;filename=\"" + name + "\"");
				response.addHeader("Content-description",  name);
				path = path.replace("\\", File.separator);//liunx和window下盘符分隔符不同，lis 20160714
				File file = new File(path,fileName);
				inputstream = new FileInputStream(file);
				int len;
				byte buf[] = new byte[1024];
				while ((len = inputstream.read(buf)) != -1) {
					servletoutputstream.write(buf,0,len);
				}
				response.setStatus(HttpServletResponse.SC_OK);
				response.flushBuffer();

			}catch (Exception exception) {
				exception.printStackTrace();
			}
			finally {
				PubFunc.closeIoResource(inputstream);
				try
				{
					if (servletoutputstream != null)
						servletoutputstream.flush();
					if (servletoutputstream != null)
						servletoutputstream.close();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			return;
		}


		/**
		 * 上传
		 */
		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new IllegalArgumentException(
					"请求不是multipart，请将form的enctype属性设置为'multipart/form-data'");
		}
		HashMap paraMap = new HashMap();
		ServletFileUpload uploadHandler = new ServletFileUpload(
				new DiskFileItemFactory());
		// 解决乱码问题
		//uploadHandler.setHeaderEncoding("UTF-8");
		response.setContentType("text/plain;charset=utf-8");
		Writer writer = response.getWriter();
		FileItem item = null;
		InputStream input = null;
		InputStream tempInput = null;
		// 文件扩展名
		String ext = "";
		String ext1 = "";
		// 文件名
		String fileName = "";
		long fileSize = 0;

		try {
			String fileListId = "";
			String fileNameMaxLength = "";
			String maxFileSize = "";
			Long MaxFileSizeNum = 0L;
			int maxLength = 0;
			String path= "";
			//zhangh 2020-1-7 items有值，则按照原有逻辑解析items；items没值，再解析part
			List items = uploadHandler.parseRequest(request);
			if(items!=null && items.size()>0){
				//servlet是低版本,按照原有的方式上传文件
				for (int i = 0; i < items.size(); i++) {
					item = (FileItem) items.get(i);
					if (item!=null&&item.isFormField()) {
						paraMap.put(item.getFieldName(), item.getString("utf-8"));//获取前台传递的参数
					}
				}
				path=paraMap.containsKey("savePath")?(String)paraMap.get("savePath"):"";
				fileListId=paraMap.containsKey("fileListId")?(String)paraMap.get("fileListId"):"null";

				//文件名称长度限制
				fileNameMaxLength = paraMap.containsKey("fileNameMaxLength")&&StringUtils.isNotBlank((String)paraMap.get("fileNameMaxLength"))?(String)paraMap.get("fileNameMaxLength"):"100";
				fileNameMaxLength = fileNameMaxLength!=null&&fileNameMaxLength.length()>0?fileNameMaxLength:"100";

				//文件大小限制
				maxFileSize=paraMap.containsKey("maxFileSize")&&StringUtils.isNotBlank((String)paraMap.get("maxFileSize")) ?(String)paraMap.get("maxFileSize"):"52428800";//默认50M
				MaxFileSizeNum=Long.parseLong(maxFileSize);
				maxLength = Integer.parseInt(fileNameMaxLength);

				for (int i = 0; i < items.size(); i++) {
					item = (FileItem) items.get(i);
					if (!item.isFormField()) {
						if(paraMap.containsKey("fileName"))//由于ie不会转码文件名，谷歌会将文件名转码，所以直接获取前台加密传入的，不再使用浏览器默认的。
							fileName=SafeCode.decode((String)paraMap.get("fileName"));
						else
							fileName = URLDecoder.decode(item.getName(),"UTF-8");

						ext = fileName.substring(fileName.lastIndexOf("."));
						ext1 = fileName.substring(fileName.lastIndexOf(".")+1);
						input = item.getInputStream();
						fileSize = item.getSize();
					}
				}
			}else{
				//servlet是3.0版本，需要按照新的方式获取文件
				Class<?> tClass =request.getClass();
				Method declaredMethod = tClass.getDeclaredMethod("getPart",String.class);
				Object result = declaredMethod.invoke(request,"files[]");
				Method getInputStream= result.getClass().getDeclaredMethod("getInputStream");
				input= (InputStream) getInputStream.invoke(result);
				tempInput = (InputStream) getInputStream.invoke(result);
				Method getSize= result.getClass().getDeclaredMethod("getSize");
				fileSize= (Long) getSize.invoke(result);
				fileListId = request.getParameter("fileListId");
				fileName = request.getParameter("fileName");
				fileName=SafeCode.decode(fileName);
				ext = fileName.substring(fileName.lastIndexOf("."));
				ext1 = fileName.substring(fileName.lastIndexOf(".")+1);
				path = request.getParameter("savePath");
				//文件名称长度限制
				fileNameMaxLength = request.getParameter("fileNameMaxLength");
				fileNameMaxLength = fileNameMaxLength!=null&&fileNameMaxLength.length()>0?fileNameMaxLength:"100";
				//文件大小限制
				maxFileSize=request.getParameter("maxFileSize");
				MaxFileSizeNum=Long.parseLong(maxFileSize);
				maxLength = Integer.parseInt(fileNameMaxLength);
			}

			//判断文件名长度是否超出限制的长度
			int fileNameLength = fileName.replaceAll("[^\\x00-\\xff]", "**").length();
			if(fileNameLength>maxLength) {
				item=null;
				JSONObject json=new JSONObject();
				json.put("successed", false);
				json.put("msg", "文件名长度超过"+maxLength+"个字符(1个汉字占2个字符),不允许上传!");
				json.put("fileListId",fileListId);
				writer.write(json.toString());
				throw GeneralExceptionHandler.Handle(new Exception("文件名长度超过"+maxLength+"个字符,不允许上传!"));
			}
			//判断文件后缀是否跟文件类型相符合
			boolean isOk = FileTypeUtil.isFileTypeEqual(input,ext1);
			/*上传添加白名单校验 guodd 2020-04-21*/
			String extControl = SystemConfig.getAllowExt();
			if(!isOk || extControl.indexOf(","+ext1.toLowerCase()+",")==-1) {
				JSONObject json=new JSONObject();
				json.put("successed", false);
				json.put("msg", ResourceFactory.getProperty("error.common.upload.invalid"));
				json.put("fileListId",fileListId);
				writer.write(json.toString());
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}

			if(0==fileSize){
				item=null;
				JSONObject json=new JSONObject();
				json.put("successed", false);
				json.put("msg", "文件大小为0kb,不允许上传！");
				json.put("fileListId",fileListId);
				writer.write(json.toString());
				throw GeneralExceptionHandler.Handle(new Exception("文件大小为0kb,不允许上传！"));
			}
			if(MaxFileSizeNum<fileSize){
				item=null;
				JSONObject json=new JSONObject();
				json.put("successed", false);
				json.put("msg", "文件过大，超过"+MaxFileSizeNum/1024+"kb,不允许上传！");
				json.put("fileListId",fileListId);
				writer.write(json.toString());
				throw GeneralExceptionHandler.Handle(new Exception("文件过大，超过"+MaxFileSizeNum/1024+"kb,不允许上传！"));
			}
			if(items==null||items.size()==0){
				//前面有操作把流给关闭了，需要重新打开流
				input =  tempInput;
			}else{
				input =  item.getInputStream();
			}
			// 长生UUID
			UUID uuid = UUID.randomUUID();
			String id = uuid.toString();
			OutputStream output = null;
			try {

				//path = new String(path.getBytes("GBK"),"UTF-8");//获取路径没有走拦截器 中文乱码 手动转义  haosl 2017-07-10
				if (path == null || path.length() <= 0) {
					path = System.getProperty("java.io.tmpdir")+File.separator;  //职称评审上传附件 转存时（默认保存路径）添加文件分隔符
					path = path.replaceAll("\\\\", "/");
				} else {
					path = PubFunc.decrypt(path);
					path = path.replaceAll("\\\\", "/");
					File dirs = new File(path);
					if(!dirs.exists()) {
						dirs.mkdirs();
					}
				}


				// 保存文件
				File file = new File(path, id + ext);

				registerPhotoForDeletion(file,request.getSession());
				output = new FileOutputStream(file);
				byte[] bt = new byte[1024];
				int read = 0;
				while ((read = input.read(bt)) != -1) {
					output.write(bt, 0, read);
				}// session消失时自动删除临时文件
				registerPhotoForDeletion(file,request.getSession());

				path = PubFunc.encrypt(path);
				fileName = file.getName();
				fileName = PubFunc.encrypt(fileName);
				String fullpath = PubFunc.encrypt(file.getAbsolutePath());

				writer.write("{\"successed\":\"" + System.currentTimeMillis() + "\",\"path\":\"" + path + "\",\"filename\":\""+fileName+"\",\"id\":\""+id+"\",\"fullpath\":\""+fullpath+"\",\"fileListId\":\""+fileListId+"\"}");
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (output != null) {
					output.close();
				}

				if (input != null) {
					input.close();
				}
			}


		} catch (FileUploadException e) {
			throw new RuntimeException("上传过程中发生异常中断：" + e);
		} catch (Exception e) {
			if (item != null) {
				JSONObject json=new JSONObject();
				json.put("successed", false);
				json.put("msg", e.getMessage());
				writer.write(json.toString());

//				writer.write(SafeCode.encode("{文件名:\"" + fileName + "\",类型:\""
//						+ ext + "\",大小:\"" + fileSize + "\",失败原因:\""
//						+  + "\"}"));
			}
			throw new RuntimeException(e);

		} finally {
			if (input != null) {
				input.close();
			}
			if(tempInput !=null){
				tempInput.close();
			}

			writer.close();

		}

	}

	/** 在侦听器中注册，会话退出时自动删除临时文件 */
	private void registerPhotoForDeletion(File tempFile,
										  HttpSession session) {
		//  Add chart to deletion list in session
		if (session != null){
			PhotoFileDeleter photoDeleter = (PhotoFileDeleter) session.getAttribute("Ole_Deleter");
			if (photoDeleter == null){
				photoDeleter = new PhotoFileDeleter();
				session.setAttribute("Ole_Deleter", photoDeleter);
			}
			photoDeleter.addTempFile(tempFile.getName());
		}
		else {
			System.out.println("Session is null - photo will not be deleted");
		}
	}
}
