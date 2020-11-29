/**
 * 
 */
package com.hjsj.hrms.module.recruitment.resumecenter.businessobject;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.utils.Office2Swf;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** 简历附件类
 * <p>Title: ResumeFileBo </p>
 * <p>Description: 简历附件类，负责处理简历相关各类附件的保存、查询等操作</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-7-27 上午09:25:17</p>
 * @author zhaoxj
 * @version 1.0
 */
public class ResumeFileBo {
    
    private Connection conn;
    private UserView userView;
    private ContentDAO dao;
    private String fileId;
    
    public ResumeFileBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.dao = new ContentDAO(conn);
    }
    
    /**
     * 新增简历附件信息
     * @Title: AddFile   
     * @Description: 新增简历附件信息   
     * @param fileInfoMap 附件信息，如文件名...
     * @return
     */
    public String addFile(HashMap fileInfoMap) {
        String url = "";
        
        try{
	        String linkid = (String) fileInfoMap.get("linkid");
	        String fileid = (String) fileInfoMap.get("fileid");
	        String filename = (String) fileInfoMap.get("filename");
	        String nbase = (String) fileInfoMap.get("nbase");
	        String a0100 = (String) fileInfoMap.get("a0100");
	        String nodeid = this.getNodeId(linkid);//获取nodeid
			//保存记录
	        saveAttachment(a0100, nodeid, nbase, filename, filename, fileid, linkid);
	        
			url="savesuccess";
        }catch(Exception e){
        	e.printStackTrace();
        }
        return url;
    }
    
    /**
     * 插入文件记录
     * @param a0100
     * @param nodeid
     * @param nbase
     * @param filename
     * @param fileOldName
     * @param fileid vfs改造后文件id
     * @param linkid
     */
    private void saveAttachment(String a0100, String nodeid, String nbase, String filename, String fileOldName,
			String fileid, String linkid) {
    	String create_user = this.userView.getUserName();//登录名 
		String create_fullname = this.userView.getUserFullName();
        create_fullname = StringUtils.isEmpty(create_fullname) ? create_user : create_fullname;//用户全名为空则为登录名
        
        IDGenerator idg = new IDGenerator(2, this.conn);
        String id;
		try {
			//参数从系统管理-应用管理-参数设置-序号维护中获取
			id = idg.getId("zp_attachment.id");
	        String guidkey = this.getGuidkey(nbase, a0100);
	        fileId = id;
			RecordVo vo = new RecordVo("zp_attachment");
			/**
			 * 处理文件重命名后文件名小于4的文件后缀名,避免保存为格式为ppt(1)类似的格式
			 */
			String ext = fileOldName.substring(fileOldName.lastIndexOf(".")+1);
			if(ext.indexOf("(")!=-1)
				ext = ext.substring(0, ext.indexOf("("));
			
			vo.setString("id", id);
			vo.setString("node_id", nodeid);
			vo.setString("guidkey", guidkey);
			vo.setString("path", fileid);
			vo.setString("file_name", filename);
			vo.setString("file_name_old", fileOldName);
			vo.setString("ext", ext);
			vo.setDate("create_time", new java.sql.Date(new java.util.Date().getTime()));
			vo.setString("create_user", create_user);
			vo.setString("create_fullname", create_fullname);
			if(StringUtils.isNotEmpty(linkid))
				vo.setString("link_id", linkid);
				
			
			ContentDAO dao = new ContentDAO(this.conn);
			dao.addValueObject(vo);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
	}

	/**
     * 删除简历附件信息
     * @Title: deleteFile   
     * @Description: 删除简历附件信息   
     * @param id    文件主键
     * @param path	文件绝对路径
     * @param fileName	文件服务器名称
     * @return
     */
    @Deprecated
    public boolean deleteFile(String id,String path,String fileName) {
        boolean isOK = false;
        try{
        	//防止误删文件，没有id不允许删除
        	if(StringUtils.isBlank(id))
        		return false;
        	StringBuffer sql = new StringBuffer();
        	sql.append("delete from zp_attachment ");
	        // 删除简历附件记录
        	ArrayList sqlVal = new ArrayList();
        	/*if(!StringUtils.isEmpty(fileName)){
        		sql.append("where file_name like ?");
        		sqlVal.add(fileName+"%");
        	}else{*/
        		sql.append("where id=?");
        		sqlVal.add(id);
        	/*} */
	        dao.update(sql.toString(),sqlVal);
	        //删除简历附件文件
	        File file = new File(path);
			if(file.exists())
				file.delete();
			isOK = true;
        }catch(Exception e){
        	e.printStackTrace();
        }
        return isOK;
    }
    /**
     * 删除指定人员所有的文件附件
     * @param a0100
     * @param nbase
     * @return
     */
    public boolean deleteAllFiles(String a0100,String nbase,String linkid) {
    	boolean isOK = false;
    	try{
    		//删除数据库记录
    		StringBuffer sql = new StringBuffer();
    		//先删除文件，后删除数据库记录
    		sql.append("select path from zp_attachment ");
    		sql.append("where guidkey=? and create_user=? and link_id=?");
    		ArrayList sqlVal = new ArrayList();
    		
    		String guidkey = this.getGuidkey(nbase, a0100); 
    		sqlVal.add(guidkey);
    		sqlVal.add(this.userView.getUserName());
    		sqlVal.add(linkid);
    		
    		ResultSet rs = null;
    		try{
    			rs = dao.search(sql.toString(), sqlVal);
    			while(rs.next()){
    				String fileid = rs.getString("path");
    				VfsService.deleteFile(this.userView.getUserName(), fileid);
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		//删除数据库记录
    		sql = new StringBuffer();
    		sql.append("delete from zp_attachment ");
    		sql.append("where guidkey=? and create_user=? and link_id=?");
    		dao.update(sql.toString(),sqlVal);
    		isOK = true;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return isOK;
    }
    /**
     * 获取本地文件名
     * @param fileName
     * @return
     */
    public String getFileLocalName(String fileName) {
    	String isOK = "";
    	ResultSet rs = null;
    	try{
    		StringBuffer sql = new StringBuffer();
    		sql.append("select * from zp_attachment ");
    		ArrayList sqlVal = new ArrayList();
    		if(!StringUtils.isEmpty(fileName)){
    			sql.append("where file_name like ?");
    			sqlVal.add(fileName+"%");
    		}
    		rs = dao.search(sql.toString(),sqlVal);
    		if(rs.next())
    			isOK = rs.getString("file_name_old");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
		    PubFunc.closeDbObj(rs);
		}
    	return isOK;
    }
    /**
     * 获取人员GUIDKEY
     * @param nbase
     * @param a0100
     * @return
     */
    private String getGuidkey(String nbase, String a0100){
    	nbase = StringUtils.isEmpty(nbase) ? "Usr" : nbase;
    	String tablename = nbase+"A01";
    	
    	PhotoImgBo photoImagBo = new PhotoImgBo(this.conn);
    	return photoImagBo.getGuidKey(tablename,a0100);
    }
    /**
     * 获得文件路径（不包括文件名）
     * @param nbase
     * @param a0100
     * @return
     */
    public String getPath(String nbase, String a0100){
    	String path = "";
    	try{
    		String guidkey = this.getGuidkey(nbase, a0100);
    		TemplateBo tb = new TemplateBo(this.conn, dao, this.userView);
			String rootPath = tb.getRootDir();
    		path = rootPath+"doc"+File.separator+"resume"+File.separator+this.getGuidDir(guidkey)+File.separator+guidkey+File.separator;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return path;
    }
    /**
     * 取得某人简历附件信息
     * @Title: getFiles   
     * @Description:    
     * @param nbase 人员库
     * @param a0100 人员编号
     * @param scope 附件范围 0：全部附件； 1：应聘人个人或猎头上传的简历附件
     * @return
     */
    public ArrayList getFiles(String nbase, String a0100, String scope) {
        ArrayList files = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        
        String guidkey = this.getGuidkey(nbase, a0100);
        String linkids = this.getAllLinkId(guidkey);//获取当前人所有linkid 
        
        RowSet rs = null;
        try {
        	StringBuffer sql = new StringBuffer();
        	if("1".equals(scope)){
        		sql.append("select '简历' custom_name,'0' seq,zp_attachment.*");
        		sql.append(" from zp_attachment");
        		sql.append(" where guidkey=? and node_id=?");
        		sql.append(" order by create_time asc");
        	}
        	else if("0".equals(scope)){
        		sql.append("select att.*,lin.custom_name,lin.seq as seq from zp_attachment att"); 
        		sql.append(" left join zp_flow_links lin");
        		sql.append(" on lin.id=att.link_id");
        		if(!"()".equals(linkids))	
        			sql.append(" and lin.id in "+linkids); 
        		sql.append(" where att.guidkey=? and att.node_id<>?");
        		sql.append(" order by seq,create_user,create_time asc");
        	}
        	ArrayList sqlVal = new ArrayList();
        	sqlVal.add(guidkey);
    		sqlVal.add("00");
			
			rs = dao.search(sql.toString(),sqlVal);
			while (rs.next()) {
				dealFileResult(nbase, a0100, files, sdf, sdf1, rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		    PubFunc.closeDbObj(rs);
		}
        return files;
    }
    /**
     * 获取刚上传的文件
     * @param id  zp_attachment中id
     * @param nbase 人员库
     * @param a0100 人员编号
     * @return
     */
    public ArrayList getCurrentFile(String nbase,String a0100) {
    	ArrayList files = new ArrayList();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
    	
    	
    	RowSet rs = null;
    	try {
    		String sql = "select * from zp_attachment att,zp_flow_links lin where att.link_id=lin.id and att.id=?";
    		ArrayList sqlVal = new ArrayList();
    		sqlVal.add(fileId);
    		
    		rs = dao.search(sql.toString(),sqlVal);
    		while (rs.next()) {
    			dealFileResult(nbase, a0100, files, sdf, sdf1, rs);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}finally{
    		PubFunc.closeDbObj(rs);
    	}
    	return files;
    }
    /**
     * 处理查询出的文件结果
     * @param nbase
     * @param a0100
     * @param files
     * @param sdf
     * @param sdf1
     * @param rs
     * @throws Exception 
     */
	private void dealFileResult(String nbase, String a0100, ArrayList files,
			SimpleDateFormat sdf, SimpleDateFormat sdf1, RowSet rs)
			throws Exception {
		String fileName = rs.getString("file_name_old");
		Date tempTime = rs.getTimestamp("create_time");
		String createTime = sdf1.format(tempTime);
		String createUser = rs.getString("create_fullname");
		String ext = rs.getString("ext");
		String seq = StringUtils.isNotEmpty(rs.getString("seq")) ? rs.getString("seq") : "0";
		String linkid = StringUtils.isEmpty(rs.getString("link_id")) ? "" : rs.getString("link_id");
		
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("id", PubFunc.encrypt(rs.getString("id")));
		bean.set("a0100", a0100);
		bean.set("nbase", nbase);
		bean.set("fileName", fileName);
		bean.set("encryptFileName", PubFunc.encrypt(fileName));
		
		bean.set("createUserName", rs.getString("create_user"));
		bean.set("fileType", ext);
		bean.set("createTime", createTime);
		bean.set("createUser", createUser);
		bean.set("seq", seq);
		bean.set("fileId", rs.getString("id"));
		bean.set("linkid", linkid);
		bean.set("nodeid", rs.getString("node_id"));
		bean.set("nodename", rs.getString("custom_name")+"相关附件");
		bean.set("title", createUser+"于"+createTime+"上传的附件");
		VfsFileEntity fileEntity = VfsService.getFileEntity(rs.getString("path"));
		bean.set("fileSize", (fileEntity==null? 0 : fileEntity.getFilesize()/1024)+"KB");
		bean.set("path", rs.getString("path"));
		
		String imageUrl = "";
		if("xls".equalsIgnoreCase(ext)||"xlsx".equalsIgnoreCase(ext)){
			imageUrl="/images/excell.png";
			bean.set("preview", "display:block;");
		}else if("doc".equalsIgnoreCase(ext)||"docx".equalsIgnoreCase(ext)||"dot".equalsIgnoreCase(ext)){
			imageUrl="/images/word.png";
			bean.set("preview", "display:block;");
		}else if("pdf".equalsIgnoreCase(ext)){
			imageUrl="/images/PDF.png";
			bean.set("preview", "display:none;");
		}else if("ppt".equalsIgnoreCase(ext)||"pptx".equalsIgnoreCase(ext)){
			imageUrl="/images/ppt.png";
			bean.set("preview", "display:block;");
		}else if("txt".equalsIgnoreCase(ext)){
			imageUrl="/images/txt.png";
			bean.set("preview", "display:none;");
		}else if("jpg;jpeg;png;bmp".indexOf(ext)!=-1){
			imageUrl="/workplan/image/file.jpg";
			bean.set("preview", "display:block;");
		}else{
			imageUrl = "/workplan/image/file.jpg";
			bean.set("preview", "display:none;");
		} 
		bean.set("imageUrl", imageUrl);
		/**
		 * 判断当前人员是否具有预览、删除、下载功能的权限
		 */
		if(!this.userView.hasTheFunction("3110701"))
			bean.set("previews", "display:none;");
		else
			bean.set("previews", "display:block;");
		if(!this.userView.hasTheFunction("3110702"))
			bean.set("download", "display:none;");
		else
			bean.set("download", "");
		//有删除权限且是本人上传的东西方可删除
		if(this.userView.getUserFullName().equals(createUser)&&this.userView.hasTheFunction("3110703"))
			bean.set("del", "");
		else
			bean.set("del", "display:none;");
		
		files.add(bean);
	}
    /**
	 * 获取文件的大小
	 * @param filepath 文件路径
	 * @return
	 */
	@Deprecated
	public String getFileSize(String filepath){
		String size = "";
		if(!StringUtils.isEmpty(filepath)){
			File file = new File(filepath);
			long len = file.length();
			double res = (double)len/1024;
			/**
			 * String.format("%.2f", res)将res格式化为保留两位小数
			 */
			if(len<1024)
				size = String.format("%.2f", (double)len)+"B";
			else if(res/1024<1)
				size = String.format("%.2f", res)+"KB";
			else
				size = String.format("%.2f", res/1024)+"MB";
		}
		return size;
	}
	/**
	 * 根据guid生成文件上传路径
	 * @param guid
	 * @return
	 */
	private String getGuidDir(String guid){
		StringBuffer dir = new StringBuffer();
		
		int iHash = Math.abs(guid.hashCode());
		dir.append("P").append(String.format("%04d", iHash/1000000%500));
		dir.append(File.separator);
		dir.append("P").append(String.format("%04d", iHash/1000%500));
		return dir.toString();
	}
	/**
	 * 获取流程环节的nodeid
	 * @param linkid
	 * @return
	 */
	private String getNodeId(String linkid){
		String nodeid = "";
		ResultSet rs = null;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select node_id ");
			sql.append(" from zp_flow_links");
			sql.append(" where id=?");
			ArrayList sqlVal = new ArrayList();
			sqlVal.add(linkid);
			rs = dao.search(sql.toString(),sqlVal);
			if(rs.next()){
				nodeid = rs.getString("node_id");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		    PubFunc.closeDbObj(rs);
		}
		return nodeid;
	}
	/**
	 * 根据guidkey获取指定人员所有经过的流程环节id
	 * @param guidkey
	 * @return
	 */
	private String getAllLinkId(String guidkey){
		StringBuffer linkids = new StringBuffer("(");
		ResultSet rs = null;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select link_id ");
			sql.append(" from zp_attachment ");
			sql.append(" where guidkey=?");
			ArrayList sqlVal = new ArrayList();
			sqlVal.add(guidkey);
			rs = dao.search(sql.toString(),sqlVal);
			while(rs.next()){
				if(StringUtils.isNotEmpty(rs.getString("link_id"))&&linkids.indexOf(rs.getString("link_id"))==-1)
					linkids.append(rs.getString("link_id")+",");
			}
			if(linkids.length()>1)
				linkids.setLength(linkids.length()-1);
			linkids.append(")");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return linkids.toString();
	}
	
	/**
	 * 外网上传简历附件
	 * @Title: createResumeFile   
	 * @Description:    
	 * @param path 文件完整绝对路径
	 * @param formFile 文件
	 * @param attach_codeset 
	 * @param string 
	 * @return 
	 * @return boolean
	 */
	public boolean createResumeFile(String a0100,String nbase,String path,FormFile formFile, String realName, String attach_codeset){
		boolean flag = true;
		InputStream is = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File tempdir = new File(path);
			if(!tempdir.exists())
				tempdir.mkdirs();
			String filename = formFile.getFileName();
			String filetype = filename.substring(filename.lastIndexOf(".")+1);
			String name = getFileName(path,filename);
			if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset)) {
				name = realName + "." + filetype;
				checkFileName(path, realName, a0100, nbase);
			}
			file = new File(path+name);
			fos = new FileOutputStream(file);
			int len = 0;
			if(",jpg,jpeg,png,bmp,".contains(","+filetype.toLowerCase()+",")){
				is = ImageBO.imgStream(formFile, filetype);
			} else {
				is = formFile.getInputStream();
			}
			if(is!=null){
				byte[] buf = new byte[1024];
				while ((len = is.read(buf, 0, 1024)) != -1) {
            		fos.write(buf, 0, len);
            	}
				fos.flush();
				fos.close();
				
				saveAttachment(a0100,"00" ,nbase, name,name,"");
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(fos);
			PubFunc.closeIoResource(is);
			PubFunc.closeResource(file);
		}
		return flag;
	}
	/**
	 * 插入文件记录
	 * @param a0100
	 * @param nbase
	 * @param name
	 * @throws GeneralException
	 */
	private void saveAttachment(String a0100,String nodeid ,String nbase, String filename,String fileOldName,String linkid)
			throws GeneralException {
		String create_user = this.userView.getUserName();//登录名 
		String create_fullname = this.userView.getUserFullName();
        create_fullname = StringUtils.isEmpty(create_fullname) ? create_user : create_fullname;//用户全名为空则为登录名
        
        IDGenerator idg = new IDGenerator(2, this.conn);
        String id = idg.getId("zp_attachment.id");//参数从系统管理-应用管理-参数设置-序号维护中获取
        String guidkey = this.getGuidkey(nbase, a0100);
        String guidDir = this.getGuidDir(guidkey);
        fileId = id;
		
		RecordVo vo = new RecordVo("zp_attachment");
		/**
		 * 处理文件重命名后文件名小于4的文件后缀名,避免保存为格式为ppt(1)类似的格式
		 */
		String ext = fileOldName.substring(fileOldName.lastIndexOf(".")+1);
		if(ext.indexOf("(")!=-1)
			ext = ext.substring(0, ext.indexOf("("));
		
		vo.setString("id", id);
		vo.setString("node_id", nodeid);
		vo.setString("guidkey", guidkey);
		vo.setString("path", "doc"+File.separator+"resume"+File.separator+guidDir+File.separator+guidkey);
		vo.setString("file_name", filename);
		vo.setString("file_name_old", fileOldName);
		vo.setString("ext", ext);
		vo.setDate("create_time", new java.sql.Date(new java.util.Date().getTime()));
		vo.setString("create_user", create_user);
		vo.setString("create_fullname", create_fullname);
		if(StringUtils.isNotEmpty(linkid))
			vo.setString("link_id", linkid);
			
		
		ContentDAO dao = new ContentDAO(this.conn);
		dao.addValueObject(vo);
	}
	
	/**
	 * 当文件夹下已有名为name的文件，获取新文件名
	 * 将文件名按照name，name(1),name(2)…命名
	 * @Title: getFileName   
	 * @Description: 
	 * @param path 文件夹完整绝对路径
	 * @param name 文件名
	 * @return 
	 * @return filename 新文件名
	 */
	public String getFileName(String path,String name){
		File filedir = new File(path);
		String filename = name;
		if (filedir.exists()) {
			File[] filelist = filedir.listFiles();
			for (int i = 0; i < filelist.length+1; i++) {
				File file = new File(path+File.separator+filename);
				if(!file.exists())
					break;
				else
					filename = name.substring(0, name.lastIndexOf("."))+"("+(i+1)+")"+name.substring(name.lastIndexOf("."));
			}

		}
		return filename;
	}
	
	/**
	 * 获取当前登录人给指定人员在指定流程环节中已上传的文件
	 * @param a0100
	 * @param nbase
	 * @param link_id
	 * @return
	 */
	public ArrayList getCurrentUserFiles(String a0100,String nbase,String link_id){
		ArrayList list = new ArrayList();
		ResultSet rs = null;
		try{
			String guidkey = this.getGuidkey(nbase, a0100);
			String username = this.userView.getUserName();
			
			StringBuffer sql = new StringBuffer();
			sql.append("select * ");
			sql.append(" from zp_attachment ");
			sql.append(" where guidkey=?");
			sql.append(" and create_user=?");
			sql.append(" and link_id=?");
			ArrayList sqlVal = new ArrayList();
			sqlVal.add(guidkey);
			sqlVal.add(username);
			sqlVal.add(link_id);
			rs = dao.search(sql.toString(),sqlVal);
			VfsFileEntity fileEntity = null;
			while(rs.next()){
				fileEntity = VfsService.getFileEntity(rs.getString("path"));
				String localname = rs.getString("file_name_old");
				HashMap bean = new HashMap();
				bean.put("id", PubFunc.encrypt(rs.getString("id")));
				bean.put("filename", rs.getString("path"));
				bean.put("localname", localname);
				bean.put("path", rs.getString("path"));
				bean.put("size", (fileEntity==null? 0 : fileEntity.getFilesize()/1024)+"KB");
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/**
	 * 查询数据库中指定模板所有已存在文件 
	 * @param templateId
	 * @param server_filename
	 * @return
	 */
	public HashMap getAllBaseFile(String a0100,String nbase,String link_id){
		HashMap map = new HashMap();
		ResultSet rs = null;
		try{
			String guidkey = this.getGuidkey(nbase, a0100);
			String username = this.userView.getUserName();
			
			StringBuffer sql = new StringBuffer();
			sql.append("select id,path,file_name ");
			sql.append(" from zp_attachment ");
			sql.append(" where guidkey=?");
			sql.append(" and create_user=?");
			sql.append(" and link_id=?");
			ArrayList sqlVal = new ArrayList();
			sqlVal.add(guidkey);
			sqlVal.add(username);
			sqlVal.add(link_id);
			
			rs = dao.search(sql.toString(),sqlVal);
			while(rs.next()){
				String filename =  rs.getString("file_name");
				map.put(""+rs.getString("id")+"",rs.getString("path"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		    PubFunc.closeDbObj(rs);
		}
		return map;
	}
	/**
	 * 
	 * 将office文档转为flash文档
	 * @param filepath
	 * @param filename
	 */
	public void switchFileToSWF(String filepath,String filename){
		try{
			String officeFile = filepath.toLowerCase();
			if (officeFile != null && (officeFile.endsWith(".doc") || officeFile.endsWith(".docx") 
					|| officeFile.endsWith(".xls") || officeFile.endsWith(".xlsx") 
					||officeFile.endsWith(".pdf") || officeFile.endsWith(".ppt") 
					|| officeFile.endsWith(".pptx"))) {
				String outputFilePath = filepath.substring(0,filepath.lastIndexOf(filename)) +filename.substring(0, filename.lastIndexOf("."))+ ".swf";
				Office2Swf.office2Swf(filepath, outputFilePath);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 当文件夹下已有名为name的文件则删掉原来的
	 * @Title: getFileName   
	 * @Description: 
	 * @param path 文件夹完整绝对路径
	 * @param name 文件名
	 * @param a0100 
	 * @param nbase 
	 * @return 
	 * @return String 新文件名
	 * @throws SQLException 
	 */
	public void checkFileName(String path,String name, String a0100, String nbase) throws SQLException{
		StringBuffer sql = new StringBuffer();
		ArrayList value = new ArrayList();
		sql.append("select file_name, guidkey from zp_attachment ");
		sql.append(" where guidkey=(select guidkey from "+nbase+"A01 where a0100=?) ");
		sql.append(" and file_name like '"+name+"%' ");
		value.add(a0100);
		RowSet rs = dao.search(sql.toString(),value);
		while(rs.next()) {
			String file_name = rs.getString("file_name");
			String guidkey = rs.getString("guidkey");
			if(name.equals(file_name.substring(0, file_name.lastIndexOf(".")))) {
				File file = new File(path + file_name);
				if(file.exists())
					file.delete();
				
				sql.setLength(0);
				value.clear();
				sql.append("delete from zp_attachment ");
				sql.append(" where guidkey=? ");
				sql.append(" and file_name=? ");
				value.add(guidkey);
        		value.add(file_name);
        		dao.update(sql.toString(),value);
			}
		}
	}
	
	public String addFile(String fileid, String fileName, String linkid, String nbase, String a0100) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("fileid", fileid);
		hm.put("filename", fileName);
		hm.put("linkid", linkid);
		hm.put("nbase", nbase);
		hm.put("a0100", a0100);
		String isOK = this.addFile(hm);//保存附件
		return isOK;
	}

	/**
	 * 删除附件信息
	 * @param id
	 * @param fileid 
	 * @return
	 */
	public boolean deleteFile(String id, String fileid) {
		boolean isOK = false;
        try{
        	//防止误删文件，没有id不允许删除
        	if(StringUtils.isBlank(id))
        		return false;
        	VfsService.deleteFile(this.userView.getUserName(), fileid);
        	StringBuffer sql = new StringBuffer();
        	sql.append("delete from zp_attachment ");
	        // 删除简历附件记录
        	ArrayList sqlVal = new ArrayList();
    		sql.append("where id=?");
    		sqlVal.add(id);
	        dao.update(sql.toString(),sqlVal);
			isOK = true;
        }catch(Exception e){
        	e.printStackTrace();
        }
        return isOK;
	}
}
