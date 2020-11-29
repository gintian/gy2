package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.train.resource.UnicodeReader;
import com.hjsj.hrms.businessobject.train.zip.ZipEntry;
import com.hjsj.hrms.businessobject.train.zip.ZipInputStream;
import com.hjsj.hrms.utils.Office2Swf;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:SaveCoursewareTrans
 * </p>
 * <p>
 * Description:保存添加的培训课程课件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class SaveCoursewareTrans extends IBusiness {

	/** 1为普通课件，2为文本课件，3为视频音频课件，4为scorm课件*/
	private String fileType = "";
	
	private String xmlContent = "";
	/**
	 * 
	 */
	public SaveCoursewareTrans() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			List itemlist = (List) this.getFormHM().get("itemlist");
			FieldItem fieldItem = null;
			RecordVo rv = new RecordVo((String) this.getFormHM().get("tablename"));
			String id = (String) this.getFormHM().get("r5100");
			id = id != null && id.trim().length() > 0 ? id : "";
			id = PubFunc.decrypt(SafeCode.decode(id));
			this.getFormHM().remove("r5100");
			String filepath = (String) hm.get("filepath");
			filepath = PubFunc.decrypt(SafeCode.decode(filepath));
			String fileId = "";
			String newPath = (String) this.getFormHM().get("newPath");
			if(StringUtils.isNotEmpty(newPath)) {
			    if(newPath.indexOf("id:") > -1)
			        newPath = newPath.substring(newPath.indexOf("id:") + 3);
			    
			    if(StringUtils.isNotEmpty(newPath))
			        newPath = SafeCode.decode(PubFunc.decrypt(newPath));	
			    
			    String[] temp = newPath.split("[|]");
			    newPath = temp[0];
			    if(temp.length == 2) {
			    	fileId = temp[1];
			    }
			}
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			//20170418	linbz 6813 更改附件后删除之前的文件
			String name = (String) this.getFormHM().get("path");
			String name_old = (String) this.getFormHM().get("path_old");
			if(!StringUtils.isEmpty(id) && !StringUtils.isEmpty(newPath) && !name.equalsIgnoreCase(name_old)){
				rv.setString("r5100", id);
				try {
					RecordVo rv1 = dao.findByPrimaryKey(rv);
					String urlold = rv1.getString("r5113");
					String rootPath = newPath.substring(0, newPath.indexOf(File.separator + "coureware" + File.separator));
					//保证原有文件路径不为空再进行删除
					if(StringUtils.isNotEmpty(urlold)){
						File file = new File(rootPath + urlold);
						if (file.exists()) {
							file.delete();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			String str = "";
			if ("".equals(id)) {
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				str = idg.getId("R51.R5100");
			} else {
				str = id;
			}
			
			// 获取课件名称
			String filename = null;
			for (int i = 0; i < itemlist.size(); i++) {
				fieldItem = (FieldItem) itemlist.get(i);
				if ("R5103".equalsIgnoreCase(fieldItem.getItemid())) {
					filename = fieldItem.getValue();
					filename = PubFunc.keyWord_filter(filename);
					break;
				}
			}
			
			for (int i = 0; i < itemlist.size(); i++) {
				fieldItem = (FieldItem) itemlist.get(i);
				if ("D".equals(fieldItem.getItemtype())) {
					String tmp = fieldItem.getValue();
					tmp = tmp.replaceAll("\\.", "-");
					rv.setDate(fieldItem.getItemid(), tmp);
				} else {
					if ("r5113".equalsIgnoreCase(fieldItem.getItemid())) {
						continue;
					}
					rv.setString(fieldItem.getItemid(), PubFunc.keyWord_reback(fieldItem.getValue()));
					// 课件类型
					if ("r5105".equalsIgnoreCase(fieldItem.getItemid())) {
						this.fileType = fieldItem.getValue();
					}
					// 当上传课件为非纯文本时
					if ("r5105".equalsIgnoreCase(fieldItem.getItemid())
							&& !"2".equalsIgnoreCase(fieldItem.getValue())) {
						
						if(newPath!=null&&newPath.length()>0&&!newPath.equals(name_old)){//附件为空是不作处理 避免把原有的数据冲掉
							
							// 将office文档转为flash文档
							String officeFile = newPath.toLowerCase();
							if (officeFile != null && (officeFile.endsWith(".doc") || officeFile.endsWith(".docx") 
									|| officeFile.endsWith(".xls") || officeFile.endsWith(".xlsx") 
									|| officeFile.endsWith(".pdf") || officeFile.endsWith(".ppt") 
									|| officeFile.endsWith(".pptx"))) {
								String outputFilePath = newPath.substring(0,newPath.lastIndexOf(File.separator) + 1) +Integer.parseInt(str)+ ".swf";
								Office2Swf.office2Swf(newPath, outputFilePath);
								
								File file = new File(newPath);
								if(file.exists()) {
									file.delete();
								}
							}
						}
					}
					
					//某些来回转库的数据库，int变成了numeric,导致""保存不了
					if("r5117".equalsIgnoreCase(fieldItem.getItemid()) || "r5119".equalsIgnoreCase(fieldItem.getItemid()))
					{
						if ("".equals(fieldItem.getValue()))
							rv.setNumber(fieldItem.getItemid(), "0");
					}
					
				}
			}
			
			if(StringUtils.isNotEmpty(newPath)&&!"".equals(id)) {
				String searchSql = "select fileid from r51 where r5100="+id;
				this.frowset = dao.search(searchSql);
				if(this.frowset.next()) {
					String oldFileId = this.frowset.getString("fileid");
					if(StringUtils.isNotEmpty(oldFileId) && !oldFileId.equalsIgnoreCase(fileId)) {
						VfsService.deleteFile(this.userView.getUserName(), oldFileId);
					}
				}
			}
			
			rv.setString("fileid", fileId);
			//------------------------------新增课件记入日志  chenxg add 2017-12-13--------------------
	        StringBuffer context = new StringBuffer();
	        context.append("新增课件:" + filepath);
	        this.getFormHM().put("@eventlog", context.toString());
	        //-------------------------------------------------------------------------------------
			if (!"".equals(id)) {
				rv.setString("r5100", id);
				String r5113 = isZip(newPath,id, fileId);//解压ZIP
				
				if(r5113!=null&&r5113.length()>0)
					rv.setString("r5113", r5113);
				
				if("6".equals(rv.getString("r5105"))){
					String url = (String)this.getFormHM().get("url");
					rv.setString("r5113", url);
				}
				
				if(name!=null&&name.length()>0&&!name.equals(name_old) && "4".equals(this.fileType)){
					rv.setObject("xmlcontent", this.xmlContent);
				}
				try {
					dao.updateValueObject(rv);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				rv.setString("r5100", str);
				String r5000 = (String) hm.get("id");
				r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
				rv.setString("r5000", r5000);
				String r5113 = isZip(newPath,str, fileId);//解压ZIP
				
				if(r5113!=null&&r5113.length()>0)
					rv.setString("r5113", r5113);
					
				if("6".equals(rv.getString("r5105"))){
					String url = (String)this.getFormHM().get("url");
					rv.setString("r5113", url);
				}
				
				if(name!=null&&name.length()>0&&!name.equals(name_old) && "4".equals(this.fileType)){
					rv.setObject("xmlcontent", this.xmlContent);
				}
				dao.addValueObject(rv);
				// 陈旭光修改：增加课件时，若该课程被选择，新课件是否被学员学习flag=1时是学习
				String flag = (String)hm.get("slect");
				hm.remove("slect");
				if("1".equals(flag)){
					String sql = "select nbase,a0100,id from tr_selected_lesson where state<>2 and  r5000 = "+r5000;
					this.frowset = dao.search(sql);
					while(this.frowset.next()){
						int sid = this.frowset.getInt("id");
						String nbase = this.frowset.getString("nbase");
						String a0100 = this.frowset.getString("a0100");
						RecordVo vo = new RecordVo("tr_selected_course");
						vo.setInt("r5100", Integer.parseInt(str));
						vo.setString("nbase", nbase);
						vo.setString("a0100", a0100);
						vo.setInt("id", sid);
						vo.setInt("lprogress", 0);
						vo.setInt("learnedhour", 0);
						vo.setInt("state", 0);
						dao.addValueObject(vo);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String isZip(String path,String id, String fileid){
		String tmpPath = path;
		String sep = System.getProperty("file.separator");
		tmpPath = tmpPath.replace("``", sep);
		///tmpPath = URLDecoder.decode(tmpPath);  ///zhangcq 2016-4-21 解决上传课件 文件名有%
		if (path!=null&&path.length()>0&&path.toLowerCase().endsWith(".zip")) {
			InputStream input = null;
			try {
				id = Integer.parseInt(id)+"";
				tmpPath=path.substring(0, path.lastIndexOf(sep)+sep.length())+id+sep;
				input = VfsService.getFile(fileid);
				if(!unzip(input, tmpPath))
					tmpPath="";
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(path.indexOf(sep+"coureware")>-1)
			path = path.substring(path.indexOf(sep+"coureware"));
		else
			path="";
		return path;
	}

	public boolean unzip(InputStream input, String saveFilePath) {
		boolean succeed = true;
		ZipInputStream zin = null;
		ZipEntry entry;
		String sep = System.getProperty("file.separator");
		FileOutputStream fout = null;
		DataOutputStream dout = null;
		try {

			zin = new ZipInputStream(input);
			if (!saveFilePath.endsWith(sep)) {
				saveFilePath += sep;
			}

			while ((entry = zin.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					int index = entry.getName().lastIndexOf("/");
					File myFile = null;
					if (index == -1) {
						myFile = new File(saveFilePath);
					} else {
						myFile = new File(saveFilePath + entry.getName().substring(0, index));
					}
					if (!myFile.exists()) {
						myFile.mkdirs();
					}
					
					// 保存imsmanifest.xml文件内容
					if ("4".equals(this.fileType) && entry.getName().toLowerCase().indexOf("imsmanifest.xml") != -1) {
						UnicodeReader r = new UnicodeReader(zin, "utf-8");
						 BufferedReader reader = new BufferedReader(r);
						 StringBuffer buffer = new StringBuffer();
						 
						 String str = null;
						 while ((str = reader.readLine()) != null) {
							 buffer.append(str);
							 buffer.append("\r\n");
						 }
						 this.xmlContent = buffer.toString();
					}
					
					fout = new FileOutputStream(saveFilePath
							+ entry.getName());
					dout = new DataOutputStream(fout);
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
			PubFunc.closeIoResource(dout);
			PubFunc.closeIoResource(fout);
		}

		return succeed;
	}
}
