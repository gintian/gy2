package com.hjsj.hrms.servlet.train.media;

import com.hjsj.hrms.businessobject.general.ftp.FtpMediaBo;
import com.hjsj.hrms.businessobject.train.MediaServerParamBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.zip.ZipEntry;
import com.hjsj.hrms.businessobject.train.zip.ZipInputStream;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.*;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

public class UploadMediaFileServlet extends HttpServlet {

	// 定义可以上传文件的后缀数组,默认"*"，代表所有
	private String[] filePostfixs = { "flv", "f4v" };
	// 上传文件的最大长度，默认2G
	private long maxFileSize = 1024 * 1024 * 1024 * 2;
	// ftp服务器ip地址
	private String ftpServer = "192.192.100.254";
	// ftp服务器端口
	private String ftpPort = "21";
	// ftp服务器用户名
	private String ftpUserName = "sanjiaolong";
	// ftp服务器密码
	private String ftpPassWord = "123456";
	// ftp流媒体保存目录
	private String ftpMediaPath = "/media/flv";
	// 1为普通类型，3为多媒体类型，4为SCORM标准课件
	private String courseType = "";
	// 是否使用ftp
	private boolean isFtp = true;
	// 是否是流媒体服务器
	private boolean isMedia = true;

	private HashMap paraMap = new HashMap();
	//课件允许上传的文件类型
	private String fileTypes = "";
	
	private UserView userView = null;

	public UploadMediaFileServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new IllegalArgumentException(
					"请求不是multipart，请将form的enctype属性设置为'multipart/form-data'");
		}
		
		Writer writer = response.getWriter();
		FileItem item = null;
		InputStream input = null;
		// 文件扩展名
		String ext = "";
		// 文件名
		String fileName = "";
		long fileSize = 0;
		String id = "";
		try {
		    if (request.getSession().getAttribute("userView") != null)
		        this.userView = (UserView) request.getSession().getAttribute("userView");
		    
		    if(this.userView == null)
		        throw new Exception("登录超时，请重新登录！");
		    
		    ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
		    // 解决乱码问题
		    uploadHandler.setHeaderEncoding("UTF-8");
		    response.setContentType("text/plain");
		    
			List items = uploadHandler.parseRequest(request);
			String coursewareFileTpye = SystemConfig.getPropertyValue("courseware_filetype").toLowerCase();
			if(StringUtils.isEmpty(coursewareFileTpye))
			    this.fileTypes = ",doc,docx,ppt,pptx,xls,xlsx,pdf,txt,mov,mp3,mp4,rmvb,wav,wma,flv,f4v,asf,zip,";
			else
			    this.fileTypes = coursewareFileTpye.replace("'", ",");
			
			if(!this.fileTypes.startsWith(","))
			    this.fileTypes = "," + this.fileTypes;
			
			if(!this.fileTypes.endsWith(","))
                this.fileTypes = this.fileTypes + ",";
			
			StringBuffer msg = new StringBuffer();
			for (int i = 0; i < items.size(); i++) {
				item = (FileItem) items.get(i);
				if (!item.isFormField()) {
					fileName = item.getName();
					ext = fileName.substring(fileName.lastIndexOf("."));

					input = item.getInputStream();
					if(!FileTypeUtil.isFileTypeEqual(input, ext.substring(1)))
                        throw new Exception(ResourceFactory.getProperty("error.fileuploaderror"));

					input = item.getInputStream();
					fileSize = item.getSize();
					if(StringUtils.isNotEmpty(this.fileTypes) && this.fileTypes.indexOf("," + ext.toLowerCase() + ",") < 0
					        && this.fileTypes.indexOf("," + ext.toLowerCase().substring(1) + ",") < 0) {
					    msg.append("{文件名:\"" + fileName + "\",");
                        msg.append("类型:\"" + ext + "\",");
                        msg.append("大小:\"" + fileSize + "\",");
                        msg.append("失败原因:此文件类型不允许上传！}");
					    continue;
					}

				} else {
					paraMap.put(item.getFieldName(), item.getString("utf-8"));
				}
			}

			if(StringUtils.isNotEmpty(msg.toString())) {
			    writer.write(SafeCode.encode(msg.toString()));
			    throw new RuntimeException("上传文件失败！");
			}
			
			String keyCode = (String) paraMap.get("keyCode");
			if ("61".equalsIgnoreCase(keyCode)) {
				dataInit();
				//linbz 20170418 6813 每个课件名称下只能保存一个课件，故连续上传多个文件时，每次需吧之前的文件删除
				String newPath = (String) paraMap.get("newPath");
				if(StringUtils.isNotEmpty(newPath)) {
				    if(newPath.indexOf("id:") > -1)
				        newPath = newPath.substring(newPath.indexOf("id:") + 3);
				    
				    if(StringUtils.isNotEmpty(newPath))
				        newPath = SafeCode.decode(PubFunc.decrypt(newPath));	
				    
				    File file = new File(newPath);
					if (file.exists()) {
						file.delete();
					}
				}
				String lessonid = (String) paraMap.get("lessonid");
				lessonid = PubFunc.decrypt(SafeCode.decode(lessonid));
				String a_code = (String) paraMap.get("acode");
				courseType = (String) paraMap.get("fileType");
				lessonid = lessonid == null ? "" : lessonid;
				if(lessonid!=null && lessonid.length()>0)
				    a_code = getCode(lessonid);
				fileName = (String) paraMap.get("fileName");
				if(fileName == null || fileName.length() == 0)
					fileName = (String)paraMap.get("Filename");
				fileName = SafeCode.decode(fileName);
				fileName = PubFunc.keyWord_filter(fileName);
				String uplodName = TrainCourseBo.createGuid();
				//【 59787】VFS+UTF-8:培训管理、培训课程、课程中的课件，下载的名称不对
                id = uploadMediaFile(fileName,uplodName, ext, input, a_code, request);
				paraMap = new HashMap();
			}

			writer.write("successed:" + System.currentTimeMillis() + ",id:"
					+ PubFunc.encrypt(SafeCode.encode(id)));
		} catch (FileUploadException e) {
			throw new RuntimeException("上传过程中发生异常中断："+e);
		} catch (Exception e) {
			if (item != null) {
				writer.write(SafeCode.encode("{文件名:\"" + fileName + "\",类型:\"" + ext
						+ "\",大小:\"" + fileSize + "\",失败原因:\""
						+ e.getMessage() + "\"}"));
			}
			
			throw new RuntimeException(e);
		} finally {
			if (input != null)
				input.close();

			writer.close();
		}

	}

	/**
	 * 上传多媒体文件
	 */
	private String uploadMediaFile(String vfsUplodName,String fileName, String ext,
			InputStream input, String a_code, HttpServletRequest request)
			throws Exception {

		String fileId = "";
		String filePath = "";
		String realurl = "";
		OutputStream out = null;
		boolean isZip = false;
		File f = null;
		String sep = System.getProperty("file.separator");
		if (a_code != null && a_code.length() > 0) {
			for (int i = 0; i < a_code.length() / 2; i++) {
				filePath += a_code.substring(0, 2 * (i + 1)) + sep;
			}
		}

		if (this.ftpMediaPath != null && this.ftpMediaPath.endsWith("/")) {
			this.ftpMediaPath = this.ftpMediaPath.substring(0,
					this.ftpMediaPath.length() - 1);
		}

		try {

			// 保存到本地
			realurl = request.getSession().getServletContext().getRealPath(
					"/coureware");
			if ("weblogic".equals(SystemConfig.getPropertyValue("webserver"))) {
				realurl = request.getSession().getServletContext().getResource(
						"/").getPath();
				File pa = new File(realurl+"/coureware");
				if (!pa.exists()) {
					pa.mkdir();
				}
				if (realurl.indexOf(':') != -1) {
					realurl = realurl.substring(1);
				}
				
				realurl = URLDecoder.decode(realurl + "coureware/");
			} else {
				realurl = URLDecoder.decode(realurl) + sep;
			}

			// 创建目录
			f = new File(realurl + filePath);
			if (!f.exists()) {
				f.mkdirs();
			}

			byte[] bt = new byte[1024];
			int read = 0;
			String userName = this.userView.getUserName();
	        VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.videostreams;
			VfsModulesEnum vfsModulesEnum = VfsModulesEnum.PX;
			VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
			fileId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
					"", input, vfsUplodName + ext, "", false);
			
			if(",.ppt,.pptx,.doc,.docx,.xls,.xlsx,.pdf,".contains("," + ext.toLowerCase() + ",")) {
				input = VfsService.getFile(fileId);
				out = new FileOutputStream(realurl + filePath + fileName + ext);
				while ((read = input.read(bt)) != -1) {
					out.write(bt, 0, read);
				}
				if (out != null) {
					out.close();
				}
			}
			// 将文件保存到本地目录
			StringBuffer msg = new StringBuffer();
			if (".zip".equalsIgnoreCase(ext) && "1".equals(this.courseType)) {// 非标准zip课件
				ZipInputStream in = null;
				try {
					input = VfsService.getFile(fileId);
					in = new ZipInputStream(input);
					ZipEntry entry = null;
					String tempFileTpye = SystemConfig.getPropertyValue("courseware_filetype").toLowerCase();
					if(StringUtils.isEmpty(tempFileTpye))
					    tempFileTpye = this.fileTypes + "html,";
					else
					    tempFileTpye = tempFileTpye.replace("'", ",");
	                    
					if(!tempFileTpye.startsWith(","))
					    tempFileTpye = "," + tempFileTpye;
		            
		            if(!tempFileTpye.endsWith(","))
		                tempFileTpye = tempFileTpye + ",";
		            
					while ((entry = in.getNextEntry()) != null) {
						if (entry.isDirectory()) {
							continue;
						} else {
							String entryName = entry.getName();
							in.closeEntry();
							String fileType = entryName.substring(entryName.indexOf("."));
							if(StringUtils.isNotEmpty(this.fileTypes) && this.fileTypes.indexOf("," + fileType.toLowerCase() + ",") < 0
							        && this.fileTypes.indexOf("," + ext.toLowerCase().substring(1) + ",") < 0) {
		                        msg.append("{文件名:\"" + fileName + "\",");
		                        msg.append("类型:\"" + ext + "\",");
		                        msg.append("失败原因:此压缩文件中包含不允许上传的文件类型！}");
		                        break;
		                    }
							// 判断入口文件是否含有default.html或default.htm
							if ("default.html".equalsIgnoreCase(entryName)
									|| "default.htm".equalsIgnoreCase(entryName)
									|| "index.htm".equalsIgnoreCase(entryName)
									|| "index.html".equalsIgnoreCase(entryName)) {
								isZip = true;
								break;
							}
						}

					}

				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception("zip文件错误");
				} finally {
					PubFunc.closeIoResource(in); // 关闭资源 guodd 2014-12-29
					if (input != null) {
						input.close();
					}
				}
			} else if (".zip".equalsIgnoreCase(ext) && "4".equals(this.courseType)) {// SCORM标准课件
				ZipInputStream in = null;
				try {
				    String filterFileTpye = ",jsp,jspx,bat,exe,jsf,jspf,server,setup,sql,sqlpage,tag,tagf,tagx,class,java,cmd,shs,msi,asp,aspx,net,";
					input = VfsService.getFile(fileId);
				    in = new ZipInputStream(input);
					ZipEntry entry = null;
					while ((entry = in.getNextEntry()) != null) {
						if (entry.isDirectory()) {
							continue;
						} else {
							String entryName = entry.getName();	
							String fileType = entryName.substring(entryName.indexOf("."));
							if(filterFileTpye.indexOf("," + fileType.toLowerCase() + ",") > -1
							        && filterFileTpye.indexOf("," + fileType.toLowerCase().substring(1) + ",") > -1) {
		                        msg.append("{文件名:\"" + fileName + "\",");
		                        msg.append("类型:\"" + ext + "\",");
		                        msg.append("失败原因:此压缩文件中包含不允许上传的文件类型！}");
		                        break;
		                    }
							// 判断入口文件是否含有imsmanifest.xml
							if (entryName.toLowerCase().indexOf("imsmanifest.xml") != -1) {
								isZip = true;
								Document doc = PubFunc.generateDom(in);
								// 是否包含organizations节点
								isZip = isZip && isHashNode(doc);
								
								break;
							}
							in.closeEntry();
						}

					}

				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception("zip文件错误");
				} finally {
					PubFunc.closeIoResource(in); // 关闭资源 guodd 2014-12-29
					if (input != null) {
						input.close();
					}
				}
			}
			
			if(StringUtils.isNotEmpty(msg.toString()))
			    throw new Exception(msg.toString());
			// 如果zip文件不是需要的zip文件，需要删除该文件
			if (".zip".equalsIgnoreCase(ext)&& "1".equals(this.courseType) && !isZip) {// 非标准zip文件没有入口文件
				// 删除zip文件
				VfsService.deleteFile(userName, fileId);
				fileId = "";
				throw new Exception("zip文件找不到入口文件default.htm或default.html或index.htm或index.html");

			} else if (".zip".equalsIgnoreCase(ext)&& "4".equals(this.courseType) && !isZip) {// Scorm标准课件xml文件错误
				// 删除zip文件
				VfsService.deleteFile(userName, fileId);
				fileId = "";
				throw new Exception("zip文件找不到imsmanifest.xml文件或xml文件格式错误！");
			}

			// 将文件保存或解压
			if (isZip) {

			} else {
				if (this.isMedia && "3".equals(courseType)) {
					this.ftpMediaPath = this.ftpMediaPath.replaceAll("/",
							Matcher.quoteReplacement(sep));
					if (!this.ftpMediaPath.endsWith(sep)) {
						this.ftpMediaPath += sep;
					}

					if (this.isFtp) {
						// ftp上的文件路径
						input = VfsService.getFile(fileId);
						FtpMediaBo bo = new FtpMediaBo(this.ftpServer, Integer.parseInt(this.ftpPort), this.ftpUserName,
								this.ftpPassWord);
						// 上传成功后将信息保存到数据库
						bo.uploadFile(this.ftpMediaPath + filePath, fileName + ext, input);
					} else {
						f = new File(this.ftpMediaPath + filePath);
						if (!f.exists()) {
							f.mkdirs();
						}
						// 不同目录，需要复制文件
						if (StringUtils.isNotEmpty(this.ftpMediaPath)) {
							input = VfsService.getFile(fileId);
							out = new FileOutputStream(this.ftpMediaPath + filePath + fileName + ext);
							while ((read = input.read(bt)) != -1) {
								out.write(bt, 0, read);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			PubFunc.closeIoResource(input);
			PubFunc.closeIoResource(out);
		}

		return realurl + filePath + fileName + ext + "|" + fileId;
	}
	
	/**
	 * 是否含有某节点
	 * @param path
	 * @param doc
	 * @return
	 */
	private boolean isHashNode(Document doc) {
		try {
			boolean org = false;
			boolean res = false;
			Element root = doc.getRootElement();
			if ("manifest".equalsIgnoreCase(root.getName())) {
				List list = root.getChildren();
				for (int i = 0; i < list.size(); i++) {
					Element el = (Element) list.get(i);
					if ("organizations".equalsIgnoreCase(el.getName())) {
						org = true;
					}
					
					if ("resources".equalsIgnoreCase(el.getName())) {
						res = true;
					}
				}
				
				if (org && res) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
/**
	public boolean unzip(InputStream input, String saveFilePath) {
		boolean succeed = true;
		ZipInputStream zin = null;
		ZipEntry entry;
		String sep = System.getProperty("file.separator");
		try {

			zin = new ZipInputStream(input);
			if (!saveFilePath.endsWith(sep)) {
				saveFilePath += sep;
			}
			// iterate ZipEntry in zip
			while ((entry = zin.getNextEntry()) != null) {
				// if file,unzip it
				if (!entry.isDirectory()) {
					int index = entry.getName().lastIndexOf("/");
					File myFile = null;
					if (index == -1) {
						myFile = new File(saveFilePath);
					} else {
						myFile = new File(saveFilePath
								+ entry.getName().substring(0, index));
					}
					if (!myFile.exists()) {
						myFile.mkdirs();
					}
					FileOutputStream fout = new FileOutputStream(saveFilePath
							+ entry.getName());
					DataOutputStream dout = new DataOutputStream(fout);
					byte[] b = new byte[1024];
					int len = 0;
					while ((len = zin.read(b)) != -1) {
						dout.write(b, 0, len);
					}
					dout.close();
					fout.close();
					zin.closeEntry();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			succeed = false;
		} finally {
			if (null != zin) {
				try {
					zin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return succeed;
	}
*/
	/**
	 * 初始化
	 */
	public void dataInit() {

		String fileSize = MediaServerParamBo.getFileSize();
		try {
			Float.parseFloat(fileSize);
		} catch (Exception e) {
			fileSize = "500";
		}

		// 文件大小限制
		if (fileSize == null || fileSize.length() <= 0
				|| Float.parseFloat(fileSize) < 0
				|| Float.parseFloat(fileSize) > Long.MAX_VALUE) {
			maxFileSize = 1024 * 1024 * 500L;
		} else {
			maxFileSize = (long) (1024 * 1024 * Float.parseFloat(fileSize));
		}

		// 流媒体服务器类型
		if ("red5".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) {
			this.isMedia = true;
			filePostfixs = new String[] { ".mp3", ".mp4", ".flv", ".f4v" };

		} else if ("microsoft".equalsIgnoreCase(MediaServerParamBo
				.getMediaServerType())) {
			this.isMedia = true;
			filePostfixs = new String[] { ".asf", ".wma", ".wmv" };
		} else {
			this.isMedia = false;
			filePostfixs = new String[] { ".*" };
		}

		// ftp 服务器类型
		String ftpIp = MediaServerParamBo.getFtpServerAddress();

		if (ftpIp == null || ftpIp.length() <= 0) {
			this.isFtp = false;
		} else {
			this.isFtp = true;
		}

		// ftp存放目录
		this.ftpMediaPath = MediaServerParamBo.getFilePath();

		this.ftpPassWord = MediaServerParamBo.getFtpServerPwd();

		this.ftpPort = MediaServerParamBo.getFtpServerPort();

		this.ftpServer = MediaServerParamBo.getFtpServerAddress();

		this.ftpUserName = MediaServerParamBo.getFtpServerUserName();
	}

	//获取课程分类
    public String getCode(String id) {
        RowSet rs = null;
        Connection con = null;
        String acode = "";
        String sql = "select r5004 from r50 where r5000='" + id + "'";
        try {
            con = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(con);
            rs = dao.search(sql);
            if (rs.next())
                acode = rs.getString("r5004");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return acode;
    }
}
