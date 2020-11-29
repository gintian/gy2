package com.hjsj.hrms.servlet;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.StringTokenizer;

/**
 * <p>Title:DisplayOleContent</p>
 * <p>Description:显示多媒体字段内容</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 15, 2005:4:44:48 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DisplayOleContent extends HttpServlet {

	private static final long serialVersionUID = 1359738559859465661L;
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String filename = request.getParameter("filename");
        String filePath = request.getParameter("filePath");
        String perguid = request.getParameter("perguid");
        String imageResize = request.getParameter("imageResize");// 图片大小参数 控制输出的图片大小（用于显示圆形照片） 格式例如55`55 hej 2016-03-11
        String bencrypt = request.getParameter("bencrypt");//wangrd  2014-07-22 filepath 标志加密传输
        //默认要加密，否则会出问题 guodd 2016-01-19
        bencrypt = StringUtils.isEmpty(bencrypt)?"true":bencrypt;
        // yangj 2014-11-13 移动端请求不走加密, 1移动端请求.主要由于ios不识别加密符号^,无法生成照片
        boolean mobile = "1".equals(request.getParameter("mobile"));
        String platform = request.getParameter("platform"); // 访问平台入口  H5 移动端入口  wangb 2019-05-05
        String code = StringUtils.isEmpty(request.getParameter("code"))?"UTF-8":request.getParameter("code");
        final String userAgent = request.getHeader("USER-AGENT"); 
        //是火狐浏览器，传入浏览器标识
	    if(userAgent.contains("Firefox")){
	    	code="Firefox";
	    }
        if(!StringUtils.isEmpty(request.getParameter("platform")) && "H5".equalsIgnoreCase(platform))// H5 端口设置编码为 UTF-8
        	code="UTF-8";
        /** 人员库(nbase), 人员编号(a0100)有值时，直接取得低分辨率图片 chent 20151019 start*/
        String nbase = "";
        String a0100 = "";
        String quality = "";
        String caseNullImg = "";//照片取不到时显示的图片，可以不传 chent 20160111
        caseNullImg = request.getParameter("caseNullImg");//没有图片时需要显示的图片
        if(!StringUtils.isEmpty(caseNullImg)) {
        	caseNullImg = caseNullImg.replace("／", "\\");
        }
        if(!StringUtils.isEmpty(request.getParameter("nbase")) && !StringUtils.isEmpty(request.getParameter("a0100"))){
        	nbase = request.getParameter("nbase");
        	nbase= PubFunc.decrypt((SafeCode.decode(nbase)));
        	a0100 = request.getParameter("a0100");
        	a0100 = PubFunc.decrypt((SafeCode.decode(a0100)));
        	quality = request.getParameter("quality");
        	quality = PubFunc.decrypt((SafeCode.decode(quality)));
        }
        Connection conn = null;
        
        try{
        	if(!StringUtils.isEmpty(request.getParameter("nbase")) && !StringUtils.isEmpty(request.getParameter("a0100"))){
        		conn = AdminDb.getConnection();
    	        PhotoImgBo photoImgBo = new PhotoImgBo(conn);// 图片工具类
    	        String servletPath = "";
    	        if(StringUtils.isEmpty(quality) || "h".equalsIgnoreCase(quality)){//图片质量 "":原图   h:原图   l:低分辨率
    	        	servletPath = photoImgBo.getPhotoPath(nbase, a0100);
    	        }else if("l".equalsIgnoreCase(quality)) {//低分辨率
    	        	servletPath = photoImgBo.getPhotoPathLowQuality(nbase, a0100);
    	        }
    			if(!StringUtils.isEmpty(servletPath)) {
    				int idx = servletPath.indexOf("?");
    				String tmp = servletPath.substring(idx+1);
    				String[] split = tmp.split("&");
    				for(int i=0; i<split.length; i++){
    					if(split[i].startsWith("filePath")){
    						int indx = split[i].indexOf("=");
    						filePath = split[i].substring(indx+1);
    					}else if(split[i].startsWith("filename")){
    						int indx = split[i].indexOf("=");
    						filename = split[i].substring(indx+1);
    					}else if(split[i].startsWith("bencrypt")){
    						int indx = split[i].indexOf("=");
    						bencrypt = split[i].substring(indx+1);
    					}
    				}
    			}
        	}
        	/** 人员库(nbase), 人员编号(a0100)有值时，直接取得低分辨率图片 chent 20151019 end*/
        
	        if ("true".equals(bencrypt)){            
	            request.removeAttribute("bencrypt");
	            if(filePath!=null){
	                filePath=SafeCode.decode(filePath);
	                filePath=PubFunc.decryption(filePath);                
	            }
	        }
	        if(filePath!=null)
	        	filePath = filePath.replace( "`",File.separator);
	        if(perguid != null){
		        	//如果是交易类进入，读取userview中的数据
	        		String byTrans = request.getParameter("byTrans");
	        		if(byTrans!=null){
	        			UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	        			filePath = userView.getHm().get(perguid).toString();
	        			userView.getHm().remove(perguid);
	        		}else{//从session读取数据
	        			filePath = (String)session.getAttribute(perguid);
	        			if(filePath!=null)
	        				session.removeAttribute(perguid);
	        		}
	        }
	        String fromflag=request.getParameter("fromflag");
	        String tempdir=System.getProperty("java.io.tmpdir");
	        if(fromflag!=null&& "relationmap".equalsIgnoreCase(fromflag.trim())){
	           String path = request.getSession().getServletContext().getRealPath("/general/sprelationmap/images");
	  		   if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
	  		   {
	  		  	  path=request.getSession().getServletContext().getResource("/general/sprelationmap/images").getPath();//.substring(0);
	  		      if(path.indexOf(':')!=-1)
	  				 path=path.substring(1);   
	  		  	  else
	  				 path=path.substring(0);
	  		      int nlen=path.length();
	  		  	  StringBuffer buf=new StringBuffer();
	  		   	  buf.append(path);
	  		  	  buf.setLength(nlen-1);
	  		   	  path=buf.toString();
	  		   }
	  		   tempdir=path;
	        } else if(fromflag!=null&& "multimedia".equalsIgnoreCase(fromflag.trim())){
	            tempdir=null; 
	        }else if(fromflag!=null && "ckfinder".equals(fromflag)){// guodd 2016-09-30 ckfinder选择图片后不直接使用路径，通过servlet加载图片，解决中文图片显示不了问题
	          	String path = request.getSession().getServletContext().getRealPath("/com");
	          	if("weblogic".equals(SystemConfig.getPropertyValue("webserver"))){
		  		  	  path=request.getSession().getServletContext().getResource("/com").getPath();//.substring(0);
		  		      if(path.indexOf(':')!=-1)
		  				 path=path.substring(1);   
		  		  	  else
		  				 path=path.substring(0);
		  		      int nlen=path.length();
		  		  	  StringBuffer buf=new StringBuffer();
		  		   	  buf.append(path);
		  		  	  buf.setLength(nlen-1);
		  		   	  path=buf.toString();
		  		 }
	          	
	        	    String realPath = path.replace(File.separator+"com", filePath);//request.getSession().getServletContext().getResource(filePath).getPath();
	        	    filePath = realPath;
	        }
	        if ((filename == null||filename.length()==0)
	        		&&(filePath==null||filePath.length()==0
	        				||(StringUtils.isEmpty(nbase)&&StringUtils.isEmpty(a0100)&&!new File(filePath).exists()/**在传filePath获取图片的时候，有可能传的是非法路径，则继续判断是否显示自定义图片 chent 20160201*/))) {
	        	if(!StringUtils.isEmpty(caseNullImg)){//没有图片时需要显示的图片
	        		String path = this.getServletContext().getRealPath("/");
					if("weblogic".equals(SystemConfig.getPropertyValue("webserver"))){
						path=request.getSession().getServletContext().getResource("/").getPath();//.substring(0);
						if(path.indexOf(':')!=-1)
							path=path.substring(1);
						else
							path=path.substring(0);
						int nlen=path.length();
						StringBuffer buf=new StringBuffer();
						buf.append(path);
						buf.setLength(nlen-1);
						path=buf.toString();
					}
	        		filePath = path + caseNullImg;
	        	}else {
	        		return;
	        		//throw new ServletException("Parameter 'filename' must be supplied");
	        	}
	        } 
	        
	        if (!mobile&&!("ieSet").equals(fromflag)) {
		        //考虑到中文的文件名称
		        filename=SafeCode.decode(filename);
		        /* 薪资 安全问题：任意文件下载漏洞 xiaoyun 2014-9-3 start */
		        filename = PubFunc.decrypt(filename);
	        }
	        filename = PubFunc.filenameReplace(filename);
	        /* 薪资 安全问题：任意文件下载漏洞 xiaoyun 2014-9-3 end */
	        //  Check the file exists
	        //filename=new String(filename.getBytes("GB2312"),"GBK"); // modify by xiaoyun 注释掉gb2312转码，因为会导致生僻字不能导出excel 标识：2294
	        /*System.out.println(System.getProperty("java.io.tmpdir"));
	        Properties p = System.getProperties();
	        for(Iterator i=p.keySet().iterator();i.hasNext();){
	        	String key = (String)i.next();
	        	System.out.print(key);
	        	System.out.println(System.getProperty(key));
	        }*/
	        String[] filepath = filename.split("\\./"); 
	        filename = filepath[filepath.length-1];
	        File file = null;
	        
	       
	        String ieSetFileName="";  //下载ie一键安装执行文件  2015-05-06  dengcan
	        if("ieSet".equals(fromflag)){
	        	String url = request.getLocalAddr();
	        	if("https".equalsIgnoreCase(request.getScheme()))
	        		ieSetFileName = "IE设置(HTTPS_"+url+").exe";
	        	else
	        		ieSetFileName = "IE设置(HTTP_"+url+").exe";
	            tempdir=request.getSession().getServletContext().getRealPath("/cs_deploy"); 
	            if ("weblogic".equals(SystemConfig.getPropertyValue("webserver"))) {
	         	   String pajs = request.getSession().getServletContext().getResource("/templates/template_ajax_info.jsp").getPath();//.substring(0);
	                if (pajs.indexOf(':') != -1) {
	             	   pajs = pajs.substring(1);
	                } else {
	             	   pajs = pajs.substring(0);
	                }
	                int nlen = pajs.length();
	                StringBuffer buf = new StringBuffer();
	                buf.append(pajs);
	                buf.setLength(nlen - 1);
	                pajs = buf.toString();
	                pajs = pajs.replace("/templates/template_ajax_info.jsp", "/cs_deploy");
	                tempdir=pajs; 
	            }
	        	filename="hrpsetie.exe";
	        	filePath="";
	        } 
	        
	        boolean fileExists = false;
	        if(filePath!=null&&filePath.length()>0){
		        	//兼容linux与windows文件路径问题 hej add 20161123
		        	filePath = filePath.replace("\\", File.separator).replace("/", File.separator);
		        	file = new File(filePath);
		        	fileExists = file.exists();
	        }else{
	        		//兼容linux与windows文件路径问题 hej add 20161123
	        		tempdir = tempdir.replace("\\", File.separator).replace("/", File.separator);
	        		file = new File(tempdir, filename);
	        		//guodd 2017-12-04 防止filename参数存在../进行非法遍历下载漏洞，此处判断如果传入的filename参数和file.getName()不一致，中断下载
	        		fileExists = file.exists() && file.getName().equals(filename);
	        }
	        if (!fileExists) {
	            return; 
	        }
	        //if (("photos.zip".equals(filename)||fromflag!=null&&(fromflag.trim().equalsIgnoreCase("relationmap")||fromflag.trim().equalsIgnoreCase("destroy")||fromflag.trim().equalsIgnoreCase("register")))&&session != null) {
	        //临时文件 默认退出会话删除。zhanghua 2017-5-4   
	        registerPhotoForDeletion(file, session);//xucs add 2014-06-09  表明文件需要在session中注册待session销毁时删除临时文件 原有的功能不变
	        //}
	        if(imageResize!=null&&!"".equals(imageResize)){
	        	ServletUtilities.resizeImage(file,response.getOutputStream(), imageResize);
	        	return;
	        }
	        /**显示对象文件*/
	        String openflag=request.getParameter("openflag");
	        
//	        int isDel=0;  //fromflag=register 下载后直接删掉临时文件程序 20160707 
//	        if (fromflag!=null&&fromflag.trim().equalsIgnoreCase("register"))
//	        	isDel=1;
	        
	        String displayfilename=request.getParameter("displayfilename");
	        if (displayfilename!=null){
	        	displayfilename=SafeCode.decode(displayfilename);
	        }
	        else {
	        	displayfilename=filename;
	        }
	        if("true".equalsIgnoreCase(openflag)){ //直接打开 
	            String _filename=filename;
	            if(StringUtils.isBlank(_filename)){
	            	_filename= filePath;
                }
                StringTokenizer Stok = new StringTokenizer(_filename, ".");
                String ext="";
                String MimeType="";
             
                for(;Stok.hasMoreTokens();)
                    ext=Stok.nextToken();
                if("doc".equalsIgnoreCase(ext)||"docx".equalsIgnoreCase(ext))
                    MimeType="application/msword;charset=UTF-8";
                else if("xls".equalsIgnoreCase(ext)||"xlsx".equalsIgnoreCase(ext))
                    MimeType="application/msexcel;charset=UTF-8";
                else if("pdf".equalsIgnoreCase(ext))
                    MimeType="application/pdf;charset=UTF-8";
                else if("bmp".equalsIgnoreCase(ext))
                    MimeType="image/bmp";
                else if("gif".equalsIgnoreCase(ext))
                    MimeType="image/gif";
                else if("jpeg".equalsIgnoreCase(ext))
                    MimeType="image/jpeg";
                else if("jpg".equalsIgnoreCase(ext))
                    MimeType="image/jpeg";
                else if("jpe".equalsIgnoreCase(ext))
                    MimeType="image/jpeg";
                else if("jfif".equalsIgnoreCase(ext))
                    MimeType="image/jpeg";
                else if("pjpeg".equalsIgnoreCase(ext))
                    MimeType="image/jpeg";
                else if("png".equalsIgnoreCase(ext))
                    MimeType="image/png";
                else if("tiff".equalsIgnoreCase(ext))
                    MimeType="image/tiff";
                else if("tiff".equalsIgnoreCase(ext))
                    MimeType="image/tiff";
                if (!"".equals(MimeType))
                    ServletUtilities.sendInlineFile(file, response,displayfilename, MimeType);
                else
                    ServletUtilities.sendTempFileEx(file,response,displayfilename,code);
	        }
	        else { 
	        	if("ieSet".equals(fromflag)){ //如果是下载IE设置插件，文件名需带上IP地址
	        		ServletUtilities.sendTempFileEx(file,response,ieSetFileName,code);
	        	}
	        	else 
	        		ServletUtilities.sendTempFile(file,response,"","","",code);//2个参数 扩充至 6个参数   
	        } 
        }catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception sql) {
				sql.printStackTrace();
			}
		}
    }
    
    /** 在侦听器中注册，会话退出时自动删除临时文件 */
    private void registerPhotoForDeletion(File tempFile,
            HttpSession session) {
        //  Add chart to deletion list in session
        if (session != null) 
        {
            PhotoFileDeleter photoDeleter = (PhotoFileDeleter) session.getAttribute("Ole_Deleter");
            if (photoDeleter == null) 
            {
                photoDeleter = new PhotoFileDeleter();
                session.setAttribute("Ole_Deleter", photoDeleter);
            }
            photoDeleter.addTempFile(tempFile.getName());
        } 
        else 
        {
            System.out.println("Session is null - photo will not be deleted");
        }
    }
    /**
     * 
     */
    public DisplayOleContent() {
        super();
    }

}
