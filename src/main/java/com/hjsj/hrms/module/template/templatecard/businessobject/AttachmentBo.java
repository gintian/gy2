package com.hjsj.hrms.module.template.templatecard.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.Map.Entry;
/**
 * 项目名称 ：ehr
 * 类名称：AttachmentBo
 * 类描述：上传附件
 * 创建人： lis
 * 创建时间：2016-5-25
 */
public class AttachmentBo {
	private UserView userView = null;
	private Connection conn = null;
	private String tabid = null;
    private String RootDir;//文件根目录
    private String para_maxsize; //文件大小 原始参数
    private float maxFileSize; //参数配置多媒体文件大小
    private String absoluteDir;//相对路径
    private String DestFileName="";//文件名
    private String realFileName = "";//文件真实名称
    private String ext="";//文件后缀
	
	public AttachmentBo(UserView userView,Connection conn,String tabid){
		this.userView = userView;
		this.conn = conn;
		this.tabid = tabid;
	}
	
	
	/**
	 * @param ins_id
	 * @param fileValues
	 * @param object_id
	 * @param infor_type
	 * @param attachmenttype
	 * @param bDelSrcFile 是否删除源文件
	 */
	public void saveAttachment(String ins_id,String fileValues,String object_id,String infor_type,String attachmenttype,boolean bDelSrcFile,ArrayList insIdsList)throws GeneralException
	{
		this.saveAttachment(ins_id, fileValues, object_id, infor_type, attachmenttype, bDelSrcFile, "",insIdsList);
	}
	/**
	 * @param ins_id
	 * @param fileValues
	 * @param object_id
	 * @param infor_type
	 * @param attachmenttype
	 * @param bDelSrcFile
	 * @param moduleId
	 * @throws GeneralException
	 */
	public void saveAttachment(String ins_id,String fileValues,String object_id,String infor_type,String attachmenttype,boolean bDelSrcFile,String moduleId,ArrayList insIdsList)throws GeneralException
	{
		String insIdWhere="";
		if(insIdsList.size()>0){
			 for (int i=0;i<insIdsList.size();i++){
		           String ins_idtemp= (String)insIdsList.get(i);
		           if (i==0)
		        	   insIdWhere=" and ins_id in("+ ins_idtemp;
		           else 
		        	   insIdWhere= insIdWhere+","+ins_idtemp;  
		           if(i==insIdsList.size()-1){
		        	   insIdWhere+=" ) ";
		           }
		       }
		}
		File file = null;
		String fileName = "";//文件新名称
		String srcFileName = "";//文件原名称
		String ext = "";
		String create_user= userView.getUserName();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try {
			if(StringUtils.isNotBlank(moduleId)){//如果是通过自助申请，且是业务用户关联自助用户，就把上传附件人的帐号填入自助的帐号，否则用微信打开看不到。
				if("9".equalsIgnoreCase(moduleId)&&this.userView.getStatus()==0&&StringUtils.isNotBlank(this.userView.getA0100())&&StringUtils.isNotBlank(this.userView.getDbname())){
					String username="";
					DbNameBo db = new DbNameBo(this.conn);
					String loginNameField = db.getLogonUserNameField();
					if(StringUtils.isNotBlank(loginNameField)) {
						loginNameField = loginNameField.toLowerCase();
						String sql="select "+loginNameField+" as username from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"' ";
						rowSet=dao.search(sql);
						while(rowSet.next()){
							username=rowSet.getString("username");
						}
						if(StringUtils.isNotBlank(username)){
							create_user=username;
						}
					}
				}
			}
			String[] object_id_Arr = object_id.split("`");
			String basepre = null;
			String objectid = null; //a0100|b0110|e01a1
			if("1".equals(infor_type)){
				basepre = object_id_Arr[0];
				objectid = object_id_Arr[1]; //a0100|b0110|e01a1
			}else
				objectid = object_id;
			HashMap fileMap=new HashMap();
			if(StringUtils.isBlank(insIdWhere)){//如果是起草状态
				ins_id="0";
				ArrayList paramList=new ArrayList();
				String sql="";
				if ("1".equals(infor_type)) {
					sql="select file_id,i9999,name from t_wf_file where ins_id='0' and objectid=? and basepre=? and tabid=? and create_user=? ";
					paramList.add(objectid);
					paramList.add(basepre);
					paramList.add(tabid);
					paramList.add(create_user);
				}else{
					sql="select file_id,i9999,name from t_wf_file where ins_id='0' and objectid=? and tabid=? and create_user=? ";
					paramList.add(objectid);
					paramList.add(tabid);
					paramList.add(create_user);
				}
				rowSet=dao.search(sql, paramList);
				while(rowSet.next()){
					fileMap.put(rowSet.getString("file_id"), rowSet.getString("name")+":"+rowSet.getString("i9999"));
				}
			}else{//流程中
				ArrayList paramList=new ArrayList();
				String sql="";
				if ("1".equals(infor_type)) {
					sql="select file_id,i9999,name,ins_id from t_wf_file where  objectid=? and basepre=? and tabid=? "+insIdWhere;
					paramList.add(objectid);
					paramList.add(basepre);
					paramList.add(tabid);
				}else{
					sql="select file_id,i9999,name,ins_id from t_wf_file where objectid=? and tabid=? "+insIdWhere;
					paramList.add(objectid);
					paramList.add(tabid);
				}
				rowSet=dao.search(sql, paramList);
				while(rowSet.next()){
					fileMap.put(rowSet.getString("file_id"), rowSet.getString("name")+":"+rowSet.getString("i9999"));
					ins_id=rowSet.getString("ins_id");
				}
			}
			String mediasortid = null;
			String mediaid = null;
			String filePath = null;
			String[] fileValueArry = fileValues.split(",");
			String[] fileArry = null;
			String idtemps="";
			for(String value: fileValueArry){
				if(StringUtils.isNotBlank(value)){//bug 51143 value可能是空，需调过
					fileArry = value.split("\\|",-1);
					fileName = fileArry[0];
					//先判断是否加密再解密  start
					if(fileName.indexOf(".")==-1&&StringUtils.isNotBlank(fileName))
						fileName = PubFunc.decrypt(fileName);
					srcFileName = fileArry[2];
					if(srcFileName.contains(".")){
						int k =srcFileName.lastIndexOf(".");//liuyz bug26658带.的文件名，文件名被截断
						srcFileName= srcFileName.substring(0,k);
					}
					//调用vfs个人附件 公共附件path拿到的就是加密后的fileid 无需再次处理
					String filepath = fileArry[1];
					/*filepath = filepath.replace("\\", File.separator).replace("/", File.separator);
					if(filepath.indexOf(File.separator)==-1&&StringUtils.isNotBlank(filepath)) {
						filepath = PubFunc.decrypt(filepath);
						filepath = filepath.replace("\\", File.separator).replace("/", File.separator);
					}*/
					//先判断是否加密再解密  end
					if (fileArry.length>=4)
						mediasortid = fileArry[3];
					else 
						mediasortid="";
					if (fileArry.length>=5)
						mediaid = fileArry[4];
					else 
						mediaid="-1";
					/*if(filepath.endsWith(File.separator))
						filePath = filepath+fileName;
					else
						filePath = filepath+File.separator+fileName;*/
					String middlepath = "";
					if("\\".equals(File.separator)){//证明是windows
						middlepath = "subdomain\\template_";
					}else if("/".equals(File.separator)){//证明是linux
						middlepath = "subdomain/template_";
					}
					Boolean isNeedInsert=true;
					Iterator iterator = fileMap.entrySet().iterator();
					while(iterator.hasNext()){//如果t_wf_file中存在
						Entry next = (Entry) iterator.next();
						String idTemp = (String) next.getKey();
						String valueTemp=(String) next.getValue();
						String[] values = valueTemp.split(":",2);
						String i999Temp=values[1];
						String nameTemp=values[0];
						if(srcFileName.equalsIgnoreCase(nameTemp)&&(StringUtils.isBlank(i999Temp)||"-1".equalsIgnoreCase(i999Temp))){//文件名相同i9999
							String update="update t_wf_file set i9999=?,state=0,filetype=? where file_id=?";
							ArrayList paramList=new ArrayList();
							paramList.add(mediaid);
							//调用vfs 不会直接删除附件 文件提交归档才会删除附件
							/*file = new File(filePath);
							String filepath_temp="";
							if(file.exists()){//删除附件时会把硬盘上的文件删除。这里需要重新生成保存
								HashMap valueMap = new HashMap();
								//保存文件到指定目录
								valueMap = this.SaveFileToDisk(file, middlepath);
								filepath_temp=(String)valueMap.get("absoluteDir")+File.separator+this.DestFileName;
								 update="update t_wf_file set i9999=?,state=0,filepath=?,filetype=? where file_id=?";
								 paramList.add(filepath_temp);
							}*/
							paramList.add(mediasortid);
							paramList.add(idTemp);
							dao.update(update, paramList);
							isNeedInsert=false;
							iterator.remove();
							break;
						}else if(srcFileName.equalsIgnoreCase(nameTemp)&&mediaid.equalsIgnoreCase(i999Temp)){//如果名字相同，且i9999值相同，更新state状态修改是否需要插入标识
							ArrayList paramList=new ArrayList();
							String update="update t_wf_file set state=0,filetype=? where file_id=?";
							/*file = new File(filePath);
							String filepath_temp="";
							if(file.exists()){//删除附件时会把硬盘上的文件删除。这里需要重新生成保存
								HashMap valueMap = new HashMap();
								//保存文件到指定目录
								valueMap = this.SaveFileToDisk(file, middlepath);
								filepath_temp=(String)valueMap.get("absoluteDir")+File.separator+this.DestFileName;
								 update="update t_wf_file set state=0,filepath=?,filetype=? where file_id=?";
								 paramList.add(filepath_temp);
							}*/
							paramList.add(mediasortid);
							paramList.add(idTemp);
							dao.update(update, paramList);
							isNeedInsert=false;
							iterator.remove();
							break;
						}
					}
					if(!isNeedInsert){
						continue;
					}
					VfsFileEntity vfsFileEntity=VfsService.getFileEntity(filepath);
					//file = new File(filePath);
					if(vfsFileEntity.getFilesize()>0){//文件存在
						HashMap valueMap = new HashMap();
						this.realFileName = srcFileName;
						//保存文件到指定目录 调用vfs不需要另存文件
						//valueMap = this.SaveFileToDisk(file, middlepath);
						RecordVo vo = new RecordVo("t_wf_file");
						vo.setString("ins_id", ins_id);
						vo.setString("tabid", tabid);
						
						vo.setString("attachmenttype", attachmenttype);
						vo.setString("objectid", objectid);
						if ("1".equals(infor_type)) {
							vo.setString("basepre", basepre);
						}
						else {
							vo.setString("basepre", null);
						}
						vo.setString("filepath", filepath);//保存相对路径
						vo.setString("filetype", mediasortid);
						if (file.length() == 0) {
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("lable.resource.upfile.nullError")));
						}
						
						String id = this.getMaxEitId(this.conn);
						if (id == null)
							return;
						vo.setString("name", srcFileName);
						vo.setString("file_id", id);
						ext = (String)valueMap.get("fileExt");
						vo.setString("ext", ext);
						String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH.mm.ss");
						Date cur_d = DateUtils.getDate(cur_date, "yyyy-MM-dd HH.mm.ss");
						vo.setString("create_user", create_user);
						vo.setString("fullname", userView.getUserFullName());
						vo.setDate("create_time", cur_d);
						vo.setString("i9999", mediaid);
						vo.setInt("state", 0);
						dao.addValueObject(vo);
						//删除文件 同步主集附件文件不需要删除 20160918 
						if (bDelSrcFile) {
							file.delete();
						}
					}
				}
			}
			Iterator iterator = fileMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry next = (Entry) iterator.next();
				String idTemp = (String) next.getKey();
				String valueTemp=(String) next.getValue();
				String[] values = valueTemp.split(":",2);
				String i999Temp=values[1];
				String nameTemp=values[0];
				if(!(StringUtils.isBlank(i999Temp)||"-1".equalsIgnoreCase(i999Temp))){
					idtemps+="'"+idTemp+"',";
				}
			}
			if(StringUtils.isNotBlank(idtemps)){
				if(StringUtils.isBlank(insIdWhere)){
					ins_id="0";
					ArrayList paramList=new ArrayList();
					String sql="";
					if ("1".equals(infor_type)) {
						sql="delete from t_wf_file where ins_id='0' and objectid=? and basepre=? and tabid=? and create_user=? and file_id  in ("+idtemps.substring(0,idtemps.length()-1)+")";
						paramList.add(objectid);
						paramList.add(basepre);
						paramList.add(tabid);
						paramList.add(create_user);
					}else{
						sql="delete from t_wf_file where ins_id='0' and objectid=? and tabid=? and create_user=? and file_id  in ("+idtemps.substring(0,idtemps.length()-1)+")";
						paramList.add(objectid);
						paramList.add(tabid);
						paramList.add(create_user);
					}
					dao.delete(sql, paramList);
				}else{
					ArrayList paramList=new ArrayList();
					String sql="";
					if ("1".equals(infor_type)) {
						sql="delete from t_wf_file where  objectid=? and basepre=? and tabid=? "+insIdWhere+ " and file_id  in ("+idtemps.substring(0,idtemps.length()-1)+")";
						paramList.add(objectid);
						paramList.add(basepre);
						paramList.add(tabid);
					}else{
						sql="delete from t_wf_file where objectid=? and tabid=? "+insIdWhere + " and file_id  in ("+idtemps.substring(0,idtemps.length()-1)+")";
						paramList.add(objectid);
						paramList.add(tabid);
					}
					dao.delete(sql, paramList);
				}
			}
		} catch (Exception e) {
			 throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
	}
	public void saveAttachment(String ins_id,String fileValues,String object_id,String infor_type,String attachmenttype,boolean bDelSrcFile,String moduleId)throws GeneralException
	{
		File file = null;
		String fileid = "";//文件新名称
		String filename="";
		String srcFileName = "";//文件原名称
		String ext = "";
		String create_user= userView.getUserName();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try {
			if(StringUtils.isNotBlank(moduleId)){//如果是通过自助申请，且是业务用户关联自助用户，就把上传附件人的帐号填入自助的帐号，否则用微信打开看不到。
				if("9".equalsIgnoreCase(moduleId)&&this.userView.getStatus()==0&&StringUtils.isNotBlank(this.userView.getA0100())&&StringUtils.isNotBlank(this.userView.getDbname())){
					String username="";
					DbNameBo db = new DbNameBo(this.conn);
					String loginNameField = db.getLogonUserNameField();
					if(StringUtils.isNotBlank(loginNameField)) {
						loginNameField = loginNameField.toLowerCase();
						String sql="select "+loginNameField+" as username from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"' ";
						rowSet=dao.search(sql);
						while(rowSet.next()){
							username=rowSet.getString("username");
						}
						if(StringUtils.isNotBlank(username)){
							create_user=username;
						}
					}
				}
			}
			String[] object_id_Arr = object_id.split("`");
			String basepre = null;
			String objectid = null; //a0100|b0110|e01a1
			if("1".equals(infor_type)){
				basepre = object_id_Arr[0];
				objectid = object_id_Arr[1]; //a0100|b0110|e01a1
			}else
				objectid = object_id;
			String mediasortid = null;
			String mediaid = null;
			String filePath = null;
			String[] fileValueArry = fileValues.split(",");
			String[] fileArry = null;
			String idtemps="";
			for(String value: fileValueArry){
				fileArry = value.split("\\|",-1);
				fileid = fileArry[0];
				filename=fileArry[1];
				if(filename.indexOf(".")==-1&&StringUtils.isNotBlank(filename)) {
					filename=PubFunc.decrypt(filename);
				}
				mediasortid=fileArry[2];
				//先判断是否加密再解密  start
				/*if(fileName.indexOf(".")==-1&&StringUtils.isNotBlank(fileName)) {
					fileName = PubFunc.decrypt(fileName);
				}
				srcFileName = fileArry[2];
				if(srcFileName.contains(".")){
					int k =srcFileName.lastIndexOf(".");//liuyz bug26658带.的文件名，文件名被截断
					srcFileName= srcFileName.substring(0,k);
				}
				String filepath = fileArry[1];
				filepath = filepath.replace("\\", File.separator).replace("/", File.separator);
				if(filepath.indexOf(File.separator)==-1&&StringUtils.isNotBlank(filepath)) {
					filepath = PubFunc.decrypt(filepath);
					filepath = filepath.replace("\\", File.separator).replace("/", File.separator);
				}
				//先判断是否加密再解密  end
				if (fileArry.length>=4)
					mediasortid = fileArry[3];
				else 
					mediasortid="";
				if (fileArry.length>=5)
					mediaid = fileArry[4];
				else 
					mediaid="-1";
				if(filepath.endsWith(File.separator))
					filePath = filepath+fileName;
				else
					filePath = filepath+File.separator+fileName;
				String middlepath = "";
				if("\\".equals(File.separator)){//证明是windows
					middlepath = "subdomain\\template_";
				}else if("/".equals(File.separator)){//证明是linux
					middlepath = "subdomain/template_";
				}
				file = new File(filePath);*/
				//文件存在
				//HashMap valueMap = new HashMap();
				this.realFileName = filename;
				//保存文件到指定目录
				//valueMap = this.SaveFileToDisk(file, middlepath);
				RecordVo vo = new RecordVo("t_wf_file");
				vo.setString("ins_id", ins_id);
				vo.setString("tabid", tabid);
				
				vo.setString("attachmenttype", attachmenttype);
				vo.setString("objectid", objectid);
				if ("1".equals(infor_type)) {
					vo.setString("basepre", basepre);
				}
				else {
					vo.setString("basepre", null);
				}
				vo.setString("filepath",fileid);//保存相对路径
				vo.setString("filetype", mediasortid);
				/*if (file.length() == 0) {
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("lable.resource.upfile.nullError")));
				}
				*/
				String id = this.getMaxEitId(this.conn);
				if (id == null)
					return;
				vo.setString("name", filename.substring(0,filename.lastIndexOf(".")));
				vo.setString("file_id", id);
				//ext = (String)valueMap.get("fileExt");
				vo.setString("ext", filename.substring(filename.lastIndexOf("."),filename.length()));
				String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH.mm.ss");
				Date cur_d = DateUtils.getDate(cur_date, "yyyy-MM-dd HH.mm.ss");
				vo.setString("create_user", create_user);
				vo.setString("fullname", userView.getUserFullName());
				vo.setDate("create_time", cur_d);
				vo.setString("i9999", mediaid);
				vo.setInt("state", 0);
				dao.addValueObject(vo);
				idtemps+="'"+id+"',";
				//删除文件 同步主集附件文件不需要删除 20160918 
				/*if (bDelSrcFile) { vfs上传控件已将文件按照设置存放 无需删除临时文件
					file.delete();
				}*/
			
			}
		} catch (Exception e) {
			 throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
	}
	//根据ins_id,object_id,attachmenttype删除t_wf_file中的附件。
	//目前此方法只有通过二维码上传个人附件、公共附件会调用
	public void deleteAttachment(String ins_id,String object_id,String infor_type,String attachmenttype){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		try{
			String basepre = null;
			String objectid = null; //a0100|b0110|e01a1
			String[] object_id_Arr = object_id.split("`");
			if("1".equals(infor_type)){
				basepre = object_id_Arr[0];
				objectid = object_id_Arr[1]; //a0100|b0110|e01a1
			}else
				objectid = object_id;
			String sql="";
			ArrayList paramList=new ArrayList();
			
			if("1".equals(infor_type)){
				paramList.add(ins_id);
				paramList.add(basepre.toLowerCase());
				paramList.add(objectid.toLowerCase());
				paramList.add(attachmenttype);
				//删除附件前先删除对应的附件
				sql="select filepath from t_wf_file where ins_id=? and lower(basepre)=? and lower(objectid)=? and attachmenttype=?";
				rowSet = dao.search(sql, paramList);
				while(rowSet.next()) {
					String fileid=rowSet.getString("filepath");
					if(StringUtils.isNotEmpty(fileid)) {
						this.delFileByVfs(fileid);
					}
				}
				sql="";
				sql="delete from t_wf_file where ins_id=? and lower(basepre)=? and lower(objectid)=? and attachmenttype=? ";
				
			}else{
				paramList.add(ins_id);
				paramList.add(objectid.toLowerCase());
				paramList.add(attachmenttype);
				sql="select filepath from t_wf_file where  ins_id=? and lower(objectid)=? and attachmenttype=? ";
				rowSet = dao.search(sql, paramList);
				while(rowSet.next()) {
					String fileid=rowSet.getString("filepath");
					if(StringUtils.isNotEmpty(fileid)) {
						this.delFileByVfs(fileid);
					}
				}
				sql="";
				sql="delete from t_wf_file where ins_id=? and lower(objectid)=? and attachmenttype=? ";
			}
			dao.delete(sql, paramList);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
	}
	
	/**
	 * 删除 附件
	 * @param fileid
	 * @throws Exception
	 */
	public void delFileByVfs(String fileid) throws Exception{
		try {
			if(StringUtils.isEmpty(fileid)) {
				return;
			}
			VfsFileEntity enty = VfsService.getFileEntity(fileid);
			//附件类型属于人事异动的可以删除
			if(enty.getModuleid().equals(VfsModulesEnum.RS.toString())) {
				//属于人事异动但是已提交入员工管理的附件不删除
				if(StringUtils.isEmpty(enty.getFiletag())||!enty.getFiletag().equals(VfsModulesEnum.YG.toString())) {
					VfsService.deleteFile(this.userView.getUserName(), fileid);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 得到附件表的主键值
	 * 
	 * @param conn
	 * @return
	 */
	public String getMaxEitId(Connection conn) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from id_factory where sequence_name='t_wf_file.file_id'");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			if (!rs.next()) {
				StringBuffer insertSQL = new StringBuffer();
				insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
				insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
				ArrayList list = new ArrayList();
				dao.insert(insertSQL.toString(), list);
			}
			IDGenerator idg = new IDGenerator(2, this.conn);
			String file_id = idg.getId("t_wf_file.file_id");
			return file_id;
		}
		catch (Exception e) {
			return null;
		}

	}
	/**
	 * 多媒体附件保存到t_wf_file表中
	 * @param valueMap
	 * @throws GeneralException
	 */
	public String saveMediaToTwffile(HashMap valueMap) throws GeneralException{
		String id =null;
		 try{
			    RecordVo vo = new RecordVo("t_wf_file");
				vo.setString("ins_id", (String)valueMap.get("ins_id"));
				vo.setString("tabid", tabid);
				
				vo.setString("attachmenttype", (String)valueMap.get("attachmenttype"));
				vo.setString("objectid", (String)valueMap.get("objectid"));
				if ("1".equals((String)valueMap.get("infor_type"))) {
					vo.setString("basepre", (String)valueMap.get("nbase"));
				}
				else {
					vo.setString("basepre", null);
				}
				vo.setString("filepath", (String)valueMap.get("filepath"));
				vo.setString("filetype", (String)valueMap.get("sortid"));
				ContentDAO dao = new ContentDAO(this.conn);
				id = this.getMaxEitId(this.conn);
				if (id == null)
					return "-1";
				vo.setString("name", (String)valueMap.get("title"));
				vo.setString("file_id", id);
				vo.setString("ext", (String)valueMap.get("ext"));
				String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH.mm.ss");
				Date cur_d = DateUtils.getDate(cur_date, "yyyy-MM-dd HH.mm.ss");
				vo.setString("create_user", userView.getUserName());
				vo.setString("fullname", userView.getUserFullName());
				vo.setDate("create_time", cur_d);
				vo.setString("i9999", (String)valueMap.get("I9999"));
				vo.setInt("state", 0);
				dao.addValueObject(vo);
		 }catch(Exception e){
           e.printStackTrace();  
           throw GeneralExceptionHandler.Handle(e);
         }
		 return id;
	}
	/**
	 * @author lis
	 * @Description: 生成文件绝对路径
	 * @date 2016-5-25
	 * @param fileName
	 * @param dirType 目录类别
	 * @return
	 * @throws GeneralException
	 */
    public String getAbsoluteDir(String fileName,String dirType) throws GeneralException
    {
        StringBuffer relative = new StringBuffer();
        String dir = null;
        try{
            String str  = fileName; 
            int iHash = Math.abs(str.hashCode());
            String dir1 = ""+iHash/1000000%500;
            while (dir1.length()<3) dir1 ="0"+dir1;
            String dir2 = ""+iHash/1000%500;
            while (dir2.length()<3) dir2 ="0"+dir2;     
            //relative.append(this.RootDir);
            relative.append(dirType);
            relative.append(tabid);
            relative.append("\\T");
            relative.append(dir1);
            relative.append("\\T");
            relative.append(dir2);
            //创建目录
            dir = relative.toString();
            dir =dir.replace("\\", File.separator);
            relative.insert(0, this.RootDir);
            File tempDir = new File(relative.toString().replace("\\", File.separator));
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
        }
        catch(Exception e){
           e.printStackTrace();  
           throw GeneralExceptionHandler.Handle(e);
        }
        return dir;
         
    }
    
    /**
     * @author lis
     * @Description: 保存文件到文件夹
     * @date 2016-5-25
     * @param file
     * @param dirType 保存新路径类别
     * @return 保存后的新文件信息
     * @throws GeneralException
     */
    public HashMap SaveFileToDisk(File file,String dirType) throws GeneralException
    {
        String fileExt="";
        String srcFilename="";      
        HashMap valueMap = new HashMap();
        if (file.exists()){
            try{
                initParam(true);
                srcFilename = file.getName();
                long size = getFileSizes(file);
                if (size<=0){
                	String realName = srcFilename;
                	if(!"".equals(this.realFileName))
                		realName = this.realFileName;
                    throw new GeneralException("上传文件大小为0:"+realName);   
                }
                if ((this.maxFileSize > 0) && (this.maxFileSize < size)) {
                	throw new GeneralException("上传文件太大, 请不要超过" + para_maxsize);
                }
                if (srcFilename.lastIndexOf(".") > 0)
                    fileExt = srcFilename.substring(srcFilename.lastIndexOf("."));// 扩展名
            
                this.DestFileName = srcFilename;
                // 获取相对目录
                this.absoluteDir = getAbsoluteDir(DestFileName,dirType);
                // 保存
                String filePath =  this.RootDir+this.absoluteDir + File.separator + this.DestFileName;  //完整路径   
                this.copyFile(file.getPath(), filePath); 
              
                valueMap.put("path",  filePath.replace("\\", File.separator).replace("/", File.separator));
                valueMap.put("fileExt", fileExt);
                valueMap.put("absoluteDir",  this.absoluteDir);
                
          } catch(Exception e){        	   
        	   e.printStackTrace();
        	   throw GeneralExceptionHandler.Handle(e);
           } 
        }        
        return valueMap;
    }
    
    /** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public void copyFile(String oldPath, String newPath) throws GeneralException { 
       InputStream inStream = null;
       FileOutputStream fs = null;
       Boolean isHaveError=false;
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) { //文件存在时 
               inStream = new FileInputStream(oldPath); //读入原文件 
               fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1444]; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   fs.write(buffer, 0, byteread); 
               } 
           } 
       } 
       catch (Exception e) { 
    	   isHaveError=true;
           e.printStackTrace(); 
           throw new GeneralException("","复制文件到文件存放目录失败，请联系管理员！" + e.getMessage(),"","");
       } finally {
           PubFunc.closeIoResource(inStream);
           PubFunc.closeIoResource(fs);
           if(isHaveError){//如果上传复制过程中出错，删除生成的文件。
        	   File newfile = new File(newPath); 
        	   if(newfile.exists()){
        		   newfile.delete();
        	   }
           }
       }
   } 
   
   /**
    * @author lis
    * @Description: 得到文件大小
    * @date 2016-5-25
    * @param f
    * @return s （数据类型bytes）
    * @throws Exception
    */
   public long getFileSizes(File f) throws Exception{//取得文件大小
       long s=0;
       FileInputStream fis = null;
       try{
           if (f.exists()) {
               fis = new FileInputStream(f);
              s= fis.available();
           } else {
              ;
           }   
       }catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
       }
       finally{
           PubFunc.closeIoResource(fis);
       }
       return s;
   }
   
   /**
    * @author lis
    * @Description: 初始化参数，得到多媒体限制文件大小（bytes）
    * @date 2016-5-25
    * @param bexcept
    * @throws GeneralException
    */
   public void initParam(boolean bexcept) throws GeneralException
   {
       //取参数 路径 大小
       try{   
           ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
           this.RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
           if(StringUtils.isNotBlank(this.RootDir)){
        	   this.para_maxsize = constantXml.getNodeAttributeValue("filepath/multimedia", "maxsize");
        	   String multimedia_maxsize = para_maxsize;
        	   
        	   this.RootDir=this.RootDir.replace("\\",File.separator);          
        	   if (!this.RootDir.endsWith(File.separator)) this.RootDir =this.RootDir+File.separator;   
        	   
        	   File file = new File(this.RootDir);
        	   if(!file.isDirectory()) {//文件路径不存在
        		   if (bexcept){
        			  // if(!file.mkdir())//创建文件路径  改为无此路径时直接提示出设置有问题
        				   throw new GeneralException("多媒体存储路径无法访问或不存在！请检查【系统管理-应用设置-参数设置-系统参数-文件存放目录】设置是否有误。");
        		   }
        		   else {
        			   return;
        		   }
        	   }
        	
        	   this.RootDir=this.RootDir+"multimedia"+File.separator;
        	   if ((multimedia_maxsize==null) ||("".equals(multimedia_maxsize))){                
        		   multimedia_maxsize="0";
        	   }
        	   float maxSize =0;
        	   multimedia_maxsize= multimedia_maxsize.toUpperCase();
        	   int k=1;
        	   if (multimedia_maxsize.indexOf("K")>0){
        		   k=1024;
        	   }
        	   else if (multimedia_maxsize.indexOf("M")>0){
        		   k=1024*1024;
        	   }
        	   else if (multimedia_maxsize.indexOf("G")>0){
        		   k=1024*1024*1024;
        	   }
        	   else if (multimedia_maxsize.indexOf("T")>0){
        		   k=1024*1024*1024*1024;
        	   } 
        	   multimedia_maxsize =multimedia_maxsize.replaceAll("K", "").replaceAll("M", "")
        	   .replaceAll("G", "").replaceAll("T", "").replaceAll("B", "");
        	   if ("".equals(multimedia_maxsize)){
        		   multimedia_maxsize="0";
        	   }
        	   try{
        		   maxSize = Float.parseFloat(multimedia_maxsize);
        		   maxSize= maxSize*k; 
        		   this.maxFileSize = maxSize;
        	   }
        	   catch (Exception e){
        		   if (bexcept)
        			   throw new GeneralException("多媒体大小有非法字符！请检查"); 
        	   }
           }else {
        	   this.RootDir = "";
		}
   
       }  
       catch(Exception e)
       {            
           throw GeneralExceptionHandler.Handle(e);
       }
   }
   
   /**
    * @author lis
    * @Description: 下载文件
    * @date 2016-5-25
    * @param file_id
    * @return
    * @throws GeneralException
    */
   public HashMap downloadFile(String file_id)throws GeneralException {    
       String path="";
       String ext = "";
       HashMap map = new HashMap();
       InputStream in = null;
       ContentDAO dao = new ContentDAO(conn);
       try{
           initParam(true);
           RowSet frowset;
           String sql = "select * from t_wf_file where file_id ="+file_id;             
           frowset =dao.search(sql);
           if(frowset.next()){
        	   path= frowset.getString("filepath");
        	   in = frowset.getBinaryStream("content");
        	   if(StringUtils.isNotBlank(path)) {
        		   if(!path.startsWith(this.RootDir)) {
        			   path = this.RootDir+path;
        		   }
        		   path = path.replace("\\", File.separator);
        	   }
               String filename= frowset.getString("name");
               ext= frowset.getString("ext");    
               if(ext.indexOf(".")==-1)
            	   this.ext="."+ext;
               else
               	   this.ext=ext;
               this.DestFileName = filename + this.ext;//回传用
           } 
           map.put("filepath", path);
           map.put("ole", in);
       }catch(Exception e)
       {
           throw GeneralExceptionHandler.Handle(e);
       }
       return map;
   }

   //File转换成二进制，mysql数据库
   public byte[] getBytes(File file){  
	   FileInputStream fis = null;
       byte[] buffer = new byte[0];  
          try {     
        	  if(file.exists()){
        		  fis = new FileInputStream(file);  
        		  ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
        		  byte[] b = new byte[1000];  
        		  int n;  
        		  while ((n = fis.read(b)) != -1) {  
        			  bos.write(b, 0, n);  
        		  }  
        		  fis.close();  
        		  bos.close();  
        		  buffer = bos.toByteArray();  
        	  }
          } catch (FileNotFoundException e) {  
              e.printStackTrace();  
          } catch (IOException e) {  
              e.printStackTrace();  
          } finally {
        	  PubFunc.closeResource(fis);
          } 
        return buffer;  
 }
   /**
    * 获得模板附件信息
    * @param ins_id 实例ID
    * @param objectid 对象ID
    * @param basepre 
    * @param username 用户名
    * @param attachmenttype //附件类型 ，个人：0 ，    公共： 1
    * @param rwPriv 附件是否可编辑
    * @param pageid_gridno 附件的pageid 和 gridno属性，来获取设置的附件归属
    * @return
    * @throws GeneralException
    */
   public ArrayList getAttachmentList(String ins_id,String objectid,String basepre,String username,String attachmenttype,String rwPriv)throws GeneralException{
	  return this.getAttachmentList(ins_id, objectid, basepre, username, attachmenttype, rwPriv, null);
   }
   public ArrayList getAttachmentList(String ins_id,String objectid,String basepre,String username,String attachmenttype,String rwPriv,String pageid_gridno)throws GeneralException
   {
	   ArrayList attachmentList=new ArrayList();
	   RowSet frowset = null;
	   ContentDAO dao = new ContentDAO(conn);
	   try
	   {
		   StringBuffer sb = new StringBuffer("");
		   if(ins_id!=null&&!"0".equals(ins_id)){//进入了审批流
					if("0".equals(attachmenttype)){//公共附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null)  and (state=0 or state is null) ");//state删除标识，state=1标识被删除，不应查出显示
					}else if("1".equals(attachmenttype)&&objectid.length()>0){//个人附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and t.attachmenttype=1");
						sb.append(" and t.objectid='");
						sb.append(objectid);
						sb.append("'  and (state=0 or state is null) ");//state删除标识，state=1标识被删除，不应查出显示
						if(StringUtils.isNotBlank(basepre)){//infor_type=1
							sb.append(" and t.basepre='");
							sb.append(basepre);
							sb.append("'");
						}
						if(StringUtils.isNotBlank(pageid_gridno)){//如果pageid_gridno不是空，就查询默认的附件归档分类设置
							String[] split = pageid_gridno.split("_");
							if(split.length==2){
								String pageid=split[0];
								String gridno=split[1];
								String sql="select sub_domain from template_set where tabid=? and pageid=? and gridno=?";
								ArrayList paramList=new ArrayList();
								paramList.add(tabid);
								paramList.add(pageid);
								paramList.add(gridno);
								frowset = dao.search(sql,paramList);
								if(frowset.next()){
									String sub_domain=frowset.getString("sub_domain");
									if(StringUtils.isNotBlank(sub_domain)){
										Document doc=null;
										Element element=null;
										doc=PubFunc.generateDom(sub_domain);
										String xpath="/sub_para/para";
										XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
										List childlist=findPath.selectNodes(doc);	
										if(childlist!=null&&childlist.size()>0)
										{
											element=(Element) childlist.get(0);
											String file_type=(String)element.getAttributeValue("file_type");
											if(StringUtils.isNotBlank(file_type)){
												sb.append(" and m.flag='"+file_type+"'");
											}
										}
									}
								}
							}
						}
					}
				}else{//还未进入审批流
					if("0".equals(attachmenttype)){//公共附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null)");
						sb.append(" and t.create_user='");
						sb.append(username);
						sb.append("'  and (state=0 or state is null) ");//state删除标识，state=1标识被删除，不应查出显示
					}else if("1".equals(attachmenttype)&&objectid.length()>0){//个人附件
						sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
						sb.append(ins_id);
						sb.append(" and t.tabid=");
						sb.append(tabid);
						sb.append(" and t.state<>1 ");
						sb.append(" and t.attachmenttype=1");
						sb.append(" and t.create_user='");
						sb.append(username);
						sb.append("' and t.objectid='");
						sb.append(objectid);
						sb.append("'  and (state=0 or state is null) ");//state删除标识，state=1标识被删除，不应查出显示
						
						if(StringUtils.isNotBlank(basepre)){//infor_type=1
							sb.append(" and t.basepre='");
							sb.append(basepre);
							sb.append("'");
						}
						if(StringUtils.isNotBlank(pageid_gridno)){//如果pageid_gridno不是空，就查询默认的附件归档分类设置
							String[] split = pageid_gridno.split("_");
							if(split.length==2){
								String pageid=split[0];
								String gridno=split[1];
								String sql="select sub_domain from template_set where tabid=? and pageid=? and gridno=?";
								ArrayList paramList=new ArrayList();
								paramList.add(tabid);
								paramList.add(pageid);
								paramList.add(gridno);
								frowset = dao.search(sql,paramList);
								if(frowset.next()){
									String sub_domain=frowset.getString("sub_domain");
									if(StringUtils.isNotBlank(sub_domain)){
										Document doc=null;
										Element element=null;
										doc=PubFunc.generateDom(sub_domain);
										String xpath="/sub_para/para";
										XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
										List childlist=findPath.selectNodes(doc);	
										if(childlist!=null&&childlist.size()>0)
										{
											element=(Element) childlist.get(0);
											String file_type=(String)element.getAttributeValue("file_type");
											if(StringUtils.isNotBlank(file_type)){
												sb.append(" and m.flag='"+file_type+"'");
											}
										}
									}
								}
							}
						}
					}
				}
			
			
				sb.append(" order by file_id");
				frowset = dao.search(sb.toString());
				while (frowset.next()) {
					LazyDynaBean bean = new LazyDynaBean();
					//**安全平台改造,将file_id进行加密处理**//*
					bean.set("file_id", SafeCode.encode(PubFunc.encrypt(frowset.getString("file_id"))));
					bean.set("attachmentname", frowset.getString("name"));
					bean.set("sortname", frowset.getString("sortname")==null?"":frowset.getString("sortname"));
					bean.set("filetype", frowset.getString("filetype"));
					bean.set("filepath", frowset.getString("filepath"));
					bean.set("ext", frowset.getString("ext"));
					bean.set("ins_id", frowset.getString("ins_id"));
					Date d_create=frowset.getDate("create_time");
					String d_str=DateUtils.format(d_create,"yyyy.MM.dd");
					bean.set("create_time", d_str);
					String name = frowset.getString("fullname");
					String user_name = frowset.getString("create_user");//下载不要
					if(StringUtils.isBlank(name))
						name = user_name;
					bean.set("fullname", name);
					bean.set("candelete", "0");//单个附件确定是否需要删除
					if("2".equals(rwPriv)){//可以编辑
						if(username!=null&&username.equals(frowset.getString("create_user"))){
							bean.set("candelete", "1");
						}else{
							bean.set("candelete", "0");
						}
					}
					attachmentList.add(bean);
				} //while loop end
			
	   
	   
	   }
	   catch(Exception e)
       {
           throw GeneralExceptionHandler.Handle(e);
       } 
	   finally
	   {
		   PubFunc.closeDbObj(frowset);
	   }
	   return attachmentList;
   }   
   public HashMap saveSignatureAttachment(String fileValues) throws GeneralException {

		File file = null;
		String fileName = "";//文件新名称
		String srcFileName = "";//文件原名称
		String ext = "";
		HashMap valueMap = new HashMap();
		try {
			String mediasortid = null;
			String filePath = null;
			String[] fileValueArry = fileValues.split(",");
			String[] fileArry = null;
			for(String value: fileValueArry){
				fileArry = value.split("\\|");
				fileName = fileArry[0];
				//先判断是否加密再解密  start
				if(fileName.indexOf(".")==-1&&StringUtils.isNotBlank(fileName))
					fileName = PubFunc.decrypt(fileName);
				srcFileName = fileArry[2];
				
				String filepath = fileArry[1];
				filepath = filepath.replace("\\", File.separator).replace("/", File.separator);
				if(filepath.indexOf(File.separator)==-1&&StringUtils.isNotBlank(filepath)) {
					filepath = PubFunc.decrypt(filepath);
					filepath = filepath.replace("\\", File.separator).replace("/", File.separator);
				}
				//先判断是否加密再解密  end
				if (fileArry.length>=4)
					mediasortid = fileArry[3];
				else 
					mediasortid="";
				
				if(filepath.endsWith(File.separator))
					filePath = filepath+fileName;
				else
					filePath = filepath+File.separator+fileName;
				String middlepath = "";
				if("\\".equals(File.separator)){//证明是windows
					middlepath = "subdomain\\template_";
				}else if("/".equals(File.separator)){//证明是linux
					middlepath = "subdomain/template_";
				}
				file = new File(filePath);
				if(file.exists()){//文件存在
					this.realFileName = srcFileName;
					//保存文件到指定目录
					valueMap = this.SaveFileToDisk(file, middlepath);
				}
			}
		} catch (Exception e) {
			 throw GeneralExceptionHandler.Handle(e);
		}
		return valueMap;
	}
	public String getDestFileName() {
		return DestFileName;
	}
	
	public void setDestFileName(String destFileName) {
		DestFileName = destFileName;
	}
	
	public String getPara_maxsize() {
		return para_maxsize;
	}
	
	public void setPara_maxsize(String para_maxsize) {
		this.para_maxsize = para_maxsize;
	}
	
	public float getMaxFileSize() {
		return maxFileSize;
	}
	
	public void setMaxFileSize(float maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	
	public String getRootDir() {
		return RootDir;
	}
	
	public void setRootDir(String rootDir) {
		RootDir = rootDir;
	}
	
	public String getAbsoluteDir() {
		return absoluteDir;
	}
	
	public void setAbsoluteDir(String absoluteDir) {
		this.absoluteDir = absoluteDir;
	}
	
	public String getExt() {
		return ext;
	}
	
	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getRealFileName() {
		return realFileName;
	}

	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}
	
}
