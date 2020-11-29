package com.hjsj.hrms.utils.ckfinder;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 * @author hej
 * createdate 2017-05-10
 * ckfinder上传图片到服务器,解决中文文件名乱码问题
 * 
 */
public class CKEditorUploadServlet extends HttpServlet {

	private static String baseDir;//CKEditor的根目录
	private static boolean enabled = false;//用户是否登录才能开启CKEditor上传 
	private static String AllowedExtensionsImage = ",bmp,gif,jpeg,jpg,png,";//图片允许的格式
	private static String AllowedExtensionsFlash = "";//Flash允许的格式
	private static Hashtable allowedExtensions;//允许的上传文件扩展名 
	private static SimpleDateFormat dirFormatter;//目录命名格式:yyyyMM 
	private static SimpleDateFormat fileFormatter;//文件命名格式:yyyyMMddHHmmssSSS 
	/**
	 * Servlet初始化方法 
	 */ 
	@SuppressWarnings("unchecked") 
	public void init() throws ServletException { 
		//格式化目录和文件命名方式
		dirFormatter = new SimpleDateFormat("yyyyMM");
		fileFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS"); 
		if (baseDir == null) baseDir = "/UserFiles/"; 
		String realBaseDir = getServletContext().getRealPath(baseDir); 
		File baseFile = new File(realBaseDir); 
		if (!baseFile.exists()) { 
			baseFile.mkdirs(); 
		} 
		//实例化允许的扩展名 
		allowedExtensions = new Hashtable(3); 
		
		/*上传添加读取配置白名单校验 guodd 2018-11-1*/
		allowedExtensions.put("File", SystemConfig.getAllowExt());
		allowedExtensions.put("Image", AllowedExtensionsImage); 
		allowedExtensions.put("Flash", AllowedExtensionsFlash); 
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		doPost(request, response); 
	} 
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache"); 
		PrintWriter out = response.getWriter(); 
		//从请求参数中获取上传文件的类型：File/Image/Flash 
		String typeStr = request.getParameter("type");
		//CKEditorFuncNum是回调时显示的位置，这个参数必须有 
		String callback = request.getParameter("CKEditorFuncNum"); 
		if (typeStr == null)
		{ 
			typeStr = "File"; 
		}   
		//实例化dNow对象，获取当前时间
		Date dNow = new Date(); 
		//设定上传文件路径 
		String currentPath = baseDir + typeStr + "/" + dirFormatter.format(dNow); 
		//获得web应用的上传路径 
		String currentDirPath = getServletContext().getRealPath(currentPath); 
		// 判断文件夹是否存在，不存在则创建
		File dirTest = new File(currentDirPath); 
		if (!dirTest.exists()) 
		{ 
			dirTest.mkdirs(); 
		} 
		//将路径前加上web应用名
		currentPath = request.getContextPath() + currentPath;  
		//文件名和文件真实路径 
		String newName = ""; 
		String fileUrl = ""; 
		String fileName = "";
		InputStream input = null;
		UserView userView = (UserView)request.getSession().getAttribute(WebConstant.userView);
		enabled = userView==null || userView.getHm().containsKey("isEmployee")?false:true;
		if (enabled) {
			//使用Apache Common组件中的fileupload进行文件上传
			FileItemFactory factory = new DiskFileItemFactory(); 
			ServletFileUpload upload = new ServletFileUpload(factory); 
			try { 
				List items = upload.parseRequest(request); 
				Map fields = new HashMap(); 
				Iterator iter = items.iterator();
				while (iter.hasNext()) { 
					FileItem item = (FileItem) iter.next(); 
					if (item.isFormField()) 
						fields.put(item.getFieldName(), item.getString()); 
					else 
						fields.put(item.getFieldName(), item); 
				} 
				//CEKditor中file域的name值是upload 
				FileItem uplFile = (FileItem) fields.get("upload"); 
				//获取文件名并做处理  
				String fileNameLong = uplFile.getName(); 
				fileNameLong = fileNameLong.replace("\\", "/"); 
				String[] pathParts = fileNameLong.split("/"); 
				fileName = pathParts[pathParts.length - 1]; 
				//获取文件扩展名
				String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
				//获取文件流
				input = uplFile.getInputStream();
				//判断文件后缀是否跟文件类型相符合  执行后会关闭文件流
				boolean isOk = FileTypeUtil.isFileTypeEqual(input,ext);
				if(!isOk || fileName.split("\\.").length>2) {
					uplFile=null;
					out.println("<script type=\"text/javascript\">");    
		            out.println("window.parent.CKEDITOR.tools.callFunction(" + callback + ",''," +"'文件上传失败！您上传的文件为非法文件！');");   
		            out.println("</script>"); 
		            out.flush();
		    		out.close();
				}else{
					//再次获取文件流
					input=uplFile.getInputStream();
					//设置上传文件名
					fileName = java.util.UUID.randomUUID().toString()+"-"+fileFormatter.format(dNow) + "." + ext; 
					//获取文件名(无扩展名) 
					//String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf("."));
					//保存文件
					//File pathToSave = new File(currentDirPath, fileName);
					//fileUrl = currentPath + "/" + fileName;
					if (extIsAllowed(typeStr, ext)) {
						//改为存到vfs中,设置为多媒体文件，不需要登录可以访问 guodd 2020-04-20
						String fileid = VfsService.addFile(userView.getUserName(), VfsFiletypeEnum.multimedia, VfsModulesEnum.NOLOGIN, VfsCategoryEnum.other,null,input,fileName,"ckeditor",false);
						fileUrl = "/servlet/vfsservlet?fileid="+fileid;
						/*
						int counter = 1;
						while (pathToSave.exists()) {
							newName = nameWithoutExt + "_" + counter + "." + ext; 
							fileUrl = currentPath + "/" + newName; 
							pathToSave = new File(currentDirPath, newName); 
							counter++; 
						}
						if("".equals(newName))
							newName = fileName;
						//uplFile.write(pathToSave); 
						pathToSave = this.inputStreamToFile(input,currentDirPath+File.separator+newName);
						//复制一张图片
						input = ImageBO.imgStream(pathToSave, ext);
						this.inputStreamToFile(input,currentDirPath+File.separator+newName);
						 */
					}else{
						String errorMsg = this.toErrorMsg(typeStr);
						out.println("<script type=\"text/javascript\">");    
			            out.println("window.parent.CKEDITOR.tools.callFunction(" + callback + ",''," + "'文件格式不正确（必须为"+errorMsg+"）');");   
			            out.println("</script>"); 
			            out.flush();
			    		out.close();
					} 
				}
			} catch (Exception ex) {
				ex.printStackTrace(); 
			}finally {
	            PubFunc.closeResource(input);
	        }
		} else {
		} 
		out.println("<script type=\"text/javascript\">"); 
		out.println("window.parent.CKEDITOR.tools.callFunction(" + callback + ",'" + fileUrl + "','')"); 
		out.println("</script>"); 
		out.flush();
		out.close();
	} 
	/**
	 * 获得错误信息
	 * @param typeStr
	 * @return
	 */
	private String toErrorMsg(String typeStr) {
		String errorMsg = "";
		String allowExt = (String) allowedExtensions.get(typeStr);
		allowExt = allowExt.replace(",", "/");
		errorMsg = allowExt;
		return errorMsg;
	}
	/**
	 * 将文件流转成file
	 * @param ins
	 * @param fileName
	 * @return
	 */
	private File inputStreamToFile(InputStream ins, String fileName) {
		if(ins == null || StringUtils.isEmpty(fileName))
            return null;
        
        File file = new File(fileName);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = ins.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(os);
            PubFunc.closeResource(ins);
        }
        return file;
	}
	/** * 
	 * 判断扩展名是否允许的方法 */ 
	private boolean extIsAllowed(String fileType, String ext) {
		boolean isallowed = false;
		ext = ext.toLowerCase(); 
		String allowList = (String) allowedExtensions.get(fileType); 
		
		if (allowList.contains(","+ext+","))
			isallowed= true; 
		/*
		if (allowList.size() > 0) { 
			if (!allowList.contains(ext)) { 
				isallowed= false; 
			} else {
				isallowed= true; 
			}
		} else
			isallowed= true;
		*/
		return isallowed; 
	}
}
