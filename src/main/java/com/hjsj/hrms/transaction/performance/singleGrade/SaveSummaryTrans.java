package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * 
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 5, 2006:5:27:12 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class SaveSummaryTrans  extends IBusiness  {
	public void execute() throws GeneralException {
		InputStream io = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");   // 1:保存附件  2：保存报告 7提交报告  4保存目标附件 8提交目标  5保存目标内容 6:删除目标附件 
			String optUrl=(String)hm.get("optUrl");
			String summary=(String)this.getFormHM().get("summary");
			summary = PubFunc.hireKeyWord_filter(summary); // 刘蒙
			String planid=(String)this.getFormHM().get("dbpre");
			planid = PubFunc.hireKeyWord_filter(planid); // 刘蒙
			if(planid==null|| "".equals(planid))
				return;
			/**考评结果表*/
			
			StringBuffer strsql=new StringBuffer();
			
			String a0100=this.userView.getA0100();
			String nbase=this.userView.getDbname();
			
			this.frowset=dao.search("select object_type from per_plan where plan_id="+planid);
			int object_type=2;
			if(this.frowset.next())
				object_type=this.frowset.getInt("object_type");
			if(object_type!=2)
			{
				if(optUrl!=null&&optUrl.indexOf("2")!=-1)
				{
					String _tmp = (String)this.getFormHM().get("object_id");
					_tmp = PubFunc.hireKeyWord_filter(_tmp); // 刘蒙
					String objectid=_tmp.replaceAll("／", "/").split("/")[0];
					a0100=getUnManager(planid,objectid);	 
				}
			}
			hm.remove("optUrl");
			if ("2".equals(opt)|| "7".equals(opt)) {
				int article_id=0;
				this.frowset = dao.search("select * from per_article where plan_id="
						+ planid + " and a0100='"
						+ a0100
						+ "' and lower(nbase)='"
						+ nbase.toLowerCase()
						+ "'  and article_type=2 and fileflag=1 ");
				if (this.frowset.next()) {
					article_id=this.frowset.getInt("article_id");
				}
				else
					article_id=insertPerArticleRecord(planid,2, 1,nbase,a0100);
				RecordVo vo=new RecordVo("per_article");
				vo.setInt("article_id",article_id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("content", summary);
				boolean isSub=true;
				if("7".equals(opt))
				{
					isSub=isSub(summary,planid,a0100,nbase,2);
					if(isSub){
						vo.setInt("state",1);		
						this.formHM.put("summaryState", "1");//提交和保存后刷新对应form里的状态，以免页面还能继续提交   zhaoxg 2014-4-15
					}
					else{
						vo.setInt("state",0);
						this.formHM.put("summaryState", "0");
					}

				}
				if("2".equals(opt))
				{
					vo.setInt("state",0);
					this.formHM.put("summaryState", "0");
				}
				dao.updateValueObject(vo);
				
				//20150624  chentong 设定了自我评价界面不显示打分模板 && 没设定多人打分时同时显示自我评价的场合,提交绩效报告时自动将自我评分状态置为已评分，并更新待办表
				if("7".equals(opt)){
					updateMainbodyStatus(dao,planid,a0100,nbase);
				}
				
				if(!isSub) {
					formHM.put("errorMsg", "不允许提交没有内容的绩效报告!"); // 传递提示信息代替抛异常，防止出现返回到过期页面的问题 lium
//					throw GeneralExceptionHandler.Handle(new Exception("不允许提交没有内容的绩效报告!"));				
				}
				
			} else if ("1".equals(opt)) // 上传附件
			{
				//20/3/10 xus vfs改造
				FormFile form_file = (FormFile) getFormHM().get("file");
				io = form_file.getInputStream();
				
				VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
				
				VfsModulesEnum vfsModulesEnum = VfsModulesEnum.ZC;
				
				VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
				
				String CategoryGuidkey = "";
				
				String fileName = form_file.getFileName();
				
				String fileid = VfsService.addFile(this.userView.getUserName(), vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidkey, io, fileName, "", false);
				
				if(StringUtils.isNotBlank(fileid)) {
					int indexInt = fileName.lastIndexOf(".");
					String ext = fileName.substring(indexInt + 1, fileName.length());
					int article_id = insertPerArticleRecord(planid, 2, 2, nbase, a0100);
					ArrayList paramList = new ArrayList();
					String sql = "update per_article set ext=?,fileid=?,Article_name=? where article_id=?";
					paramList.add(ext);
					paramList.add(fileid);
					paramList.add(fileName);
					paramList.add(article_id);
					dao.update(sql, paramList);
					this.getFormHM().put("summaryFileIdsList",
							getSummaryFileIdsList(planid, "2"));
				}
				
//				boolean flag = FileTypeUtil.isFileTypeEqual(form_file);
//				if(!flag){
//					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
//				}
//
//				String fileName=(String)this.getFormHM().get("fileName");
//				fileName = PubFunc.hireKeyWord_filter(fileName); // 刘蒙
//				
//				String file_max_size="512";
//				if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
//				{
//					file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
//					if(file_max_size.toLowerCase().indexOf("k")!=-1)
//						file_max_size=file_max_size.substring(0,file_max_size.length()-1);
//				}
//				
//				
//				if (form_file != null && form_file.getFileData().length > 0
//						&& form_file.getFileData().length < Integer.parseInt(file_max_size)*1024) {
//
//					String fname = form_file.getFileName();
//
//
//					if(fileName.getBytes("utf-8").length>50){
//						formHM.put("errorMsg", "文件名称需在20个字之内，请重新上传！");
//						formHM.remove("file");
//						formHM.remove("fileName");
//					}else {
//						int indexInt = fname.lastIndexOf(".");
//						String ext = fname.substring(indexInt + 1, fname.length());
//						int article_id = insertPerArticleRecord(planid, 2, 2, nbase, a0100);
//						ArrayList paramList = new ArrayList();
//						String sql = "update per_article set ext=?,affix=?,Article_name=? where article_id=?";
//						paramList.add(ext);
//						// blob字段保存,数据库中差异
//						switch (Sql_switcher.searchDbServer()) {
//							case Constant.ORACEL:
//								Blob blob = getOracleBlob(form_file, "per_article",
//										article_id);
//								paramList.add(blob);
//								paramList.add(fileName);
//								paramList.add(article_id);
//								break;
//							default:
//								byte[] data = form_file.getFileData();
//								// a_vo.setObject("affix",data);
//								paramList.add(data);
//								paramList.add(fileName);
//								paramList.add(article_id);
//								break;
//						}
//						dao.update(sql, paramList);
//						this.getFormHM().put("summaryFileIdsList",
//								getSummaryFileIdsList(planid, "2"));
//					}
//
//				} else {
//					formHM.put("errorMsg", "上传文件大小超过"+file_max_size+"K，请重新上传！");
//					formHM.remove("file");
//					formHM.remove("fileName");
//				}
				
			} else if ("3".equals(opt)) // 删除上传附件
			{
				//20/3/10 xus vfs改造
				//删除文件系统中的文件
				//获取文件fileid
				String article_id = (String) hm.get("article_id");
				String plan_id = (String) hm.get("plan_id");
				String sql = "select fileid from per_article where article_id = ? ";
				ArrayList values = new ArrayList();
				values.add(article_id);
				this.frowset = dao.search(sql, values);
				String fileid = "";
				if(this.frowset.next()) {
					fileid = this.frowset.getString("fileid");
				}
				if(StringUtils.isNotBlank(fileid)) {
					VfsService.deleteFile(this.userView.getUserName(), fileid);
				}
				dao.delete("delete from per_article where article_id="
						+ article_id, new ArrayList());
				this.getFormHM().put("summary", summary);
				this.getFormHM().put("summaryFileIdsList",
						getSummaryFileIdsList(plan_id, "2"));

			}

			if ("4".equals(opt) || "5".equals(opt) || "6".equals(opt)|| "8".equals(opt)) {
				//20/3/10 xus vfs改造
				String fileid = "";
				int articleid = -1;
				this.frowset = dao
						.search("select * from per_article where plan_id="
								+ planid + " and a0100='"
								+ a0100
								+ "' and lower(nbase)='"
								+ nbase.toLowerCase()
								+ "'  and article_type=1 ");
				if (!this.frowset.next()) {
					articleid = insertPerArticleRecord(planid, 1, 1,nbase,a0100);
				}else {
					if(this.frowset.getObject("fileid") != null) {
						fileid = this.frowset.getString("fileid");
					}
				}
				if(StringUtils.isBlank(fileid)) {
					if(articleid != -1) {
						String sql = "select fileid from per_article where article_id = ? ";
						ArrayList values = new ArrayList();
						values.add(articleid);
						this.frowset = dao.search(sql,values);
						if(this.frowset.next()) {
							if(this.frowset.getObject("fileid") != null) {
								fileid = this.frowset.getString("fileid");
							}
						}
					}
				}
				if ("4".equals(opt)) {
					FormFile form_file = (FormFile) getFormHM().get("goalfile");
					String fileName=(String)this.getFormHM().get("goalfileName");
					fileName = PubFunc.hireKeyWord_filter(fileName); // 刘蒙
					io = form_file.getInputStream();
					
					VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
					
					VfsModulesEnum vfsModulesEnum = VfsModulesEnum.ZC;
					
					VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
					
					String CategoryGuidkey = "";
					if(StringUtils.isBlank(fileid)) {
						//新增
						fileid = VfsService.addFile(this.userView.getUserName(), vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidkey, io, fileName, "", false);
					}else {
						//修改
						fileid = VfsService.saveFile(this.userView.getUserName(), fileid, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, CategoryGuidkey, io, fileName, "", false);
					}

					int article_id = insertPerArticleRecord(planid, 1, 2,nbase,a0100);
					String fname = form_file.getFileName();
					int indexInt = fname.lastIndexOf(".");
					
					String ext = fname.substring(indexInt + 1, fname
							.length());
					if(StringUtils.isBlank(fileName))
						fileName=fname;
					if(StringUtils.isNotBlank(fileid)) {
						String _name = fname.substring(0, indexInt);
						String _ext = fname.substring(indexInt + 1, fname.length());
						_name = PerEvaluationBo.substrChinese(_name, 50 - _ext.length() - 1);//去掉后缀长度 .xls .doc等等
						fname = _name + "." + _ext;
						ArrayList paramList = new ArrayList();
						String sql = "update per_article set ext=?,fileid=?,Article_name=?  where article_id=?";
						paramList.add(ext);
						paramList.add(fileid);
						paramList.add(fileName);
						paramList.add(article_id);
						dao.update(sql, paramList);
						this.getFormHM().put("goalFileIdsList",
								getSummaryFileIdsList(planid, "1"));
					}
					
//					String file_max_size="512";
//					if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").length()>0)
//					{
//						file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
//						if(file_max_size.toLowerCase().indexOf("k")!=-1)
//							file_max_size=file_max_size.substring(0,file_max_size.length()-1);
//					}
//					
//					
//					FormFile form_file = (FormFile) getFormHM().get("goalfile");
//					String fileName=(String)this.getFormHM().get("goalfileName");
//					fileName = PubFunc.hireKeyWord_filter(fileName); // 刘蒙
//					if (form_file != null && form_file.getFileData().length > 0
//							&& form_file.getFileData().length < Integer.parseInt(file_max_size)*1024) {
//						int article_id = insertPerArticleRecord(planid, 1, 2,nbase,a0100);
//						String fname = form_file.getFileName();
//						int indexInt = fname.lastIndexOf(".");
//						
//						String ext = fname.substring(indexInt + 1, fname
//								.length());
//						if(StringUtils.isBlank(fileName))
//							fileName=fname;
//
//						if(fileName.getBytes("utf-8").length>50){
//							formHM.put("errorMsg", "文件名称需在20个字之内，请重新上传！");
//							formHM.remove("file");
//							formHM.remove("fileName");
//						}else {
//							String _name = fname.substring(0, indexInt);
//							String _ext = fname.substring(indexInt + 1, fname.length());
//							_name = PerEvaluationBo.substrChinese(_name, 50 - _ext.length() - 1);//去掉后缀长度 .xls .doc等等
//							fname = _name + "." + _ext;
//							ArrayList paramList = new ArrayList();
//							String sql = "update per_article set ext=?,affix=?,Article_name=?  where article_id=?";
//							paramList.add(ext);
//							/** blob字段保存,数据库中差异 */
//							switch (Sql_switcher.searchDbServer()) {
//								case Constant.ORACEL:
//									Blob blob = getOracleBlob(form_file, "per_article", article_id);
//									paramList.add(blob);
//									paramList.add(fileName);
//									paramList.add(article_id);
//									break;
//								default:
//									byte[] data = form_file.getFileData();
//									// a_vo.setObject("affix",data);
//									paramList.add(data);
//									paramList.add(fileName);
//									paramList.add(article_id);
//									break;
//							}
//							dao.update(sql, paramList);
//							this.getFormHM().put("goalFileIdsList",
//									getSummaryFileIdsList(planid, "1"));
//						}
//					}else {
//						formHM.put("errorMsg", "上传文件大小超过"+file_max_size+"K，请重新上传！");
//						formHM.remove("file");
//						formHM.remove("fileName");
//					}
				}
				if ("5".equals(opt)|| "8".equals(opt)) {
					String goalContext = (String) this.getFormHM().get("goalContext");
					goalContext = PubFunc.hireKeyWord_filter(goalContext); // 刘蒙
				/*	if (goalContext == null || goalContext.equals(""))
						return;*/
					
					
					int article_id=0;
					this.frowset = dao
					.search("select * from per_article where plan_id="
							+ planid + " and a0100='"
							+ a0100
							+ "' and lower(nbase)='"
							+ nbase.toLowerCase()
							+ "'  and article_type=1 and fileflag=1 ");
					if (this.frowset.next()) {
						article_id=this.frowset.getInt("article_id");
					}
					else
						article_id=insertPerArticleRecord(planid,1, 1,nbase,a0100);
					RecordVo vo=new RecordVo("per_article");
					vo.setInt("article_id",article_id);
					vo=dao.findByPrimaryKey(vo);
					vo.setString("content", goalContext);
					
					boolean isSub=true;
					if("8".equals(opt))
					{
						isSub=isSub(goalContext,planid,a0100,nbase,1);
						if(isSub)
							vo.setInt("state",1);
						else
							vo.setInt("state",0);
					}
					if("5".equals(opt))
					{
						vo.setInt("state",0);
					}
					dao.updateValueObject(vo);
					
					if(!isSub)
						 throw GeneralExceptionHandler.Handle(new Exception("不允许提交没有内容的绩效目标!"));	
					
					if("8".equals(opt))
						this.getFormHM().put("goalState", "1");
					if("5".equals(opt))
						this.getFormHM().put("goalState", "0");
				}
				if ("6".equals(opt)) // 删除上传目标附件
				{
					//20/3/10 xus vfs删除文件系统中的文件
					if(StringUtils.isNotBlank(fileid)) {
						VfsService.deleteFile(this.userView.getUserName(), fileid);
					}
					String goalContext = (String) this.getFormHM().get("goalContext");
					goalContext = PubFunc.hireKeyWord_filter(goalContext); // 刘蒙
					String article_id = (String) hm.get("article_id");
					dao.delete("delete from per_article where article_id="
							+ article_id, new ArrayList());
					this.getFormHM().put("goalContext", goalContext); 
					this.getFormHM().put("goalFileIdsList",
							getSummaryFileIdsList(planid, "1"));
				}
				
				
			}
		
		
			if ("1".equals(opt)|| "4".equals(opt))
			{ 
				int article_id=0;
				int article_type=2;
				String context=summary;
				if("4".equals(opt))
				{
					article_type=1;
					context = (String) this.getFormHM().get("goalContext");
					context = PubFunc.hireKeyWord_filter(context); // 刘蒙
				}
				this.frowset = dao.search("select * from per_article where plan_id="
						+ planid + " and a0100='"
						+ a0100
						+ "' and lower(nbase)='"
						+ nbase.toLowerCase()
						+ "'  and article_type="+article_type+" and fileflag=1 ");
				if (this.frowset.next()) {
					article_id=this.frowset.getInt("article_id");
				}
				else
					article_id=insertPerArticleRecord(planid,article_type, 1,nbase,a0100);
				RecordVo vo=new RecordVo("per_article");
				vo.setInt("article_id",article_id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("content", context);
				vo.setInt("state",0); dao.updateValueObject(vo); 
			}
		
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);					
		}finally {
			PubFunc.closeIoResource(io);
		}
		
	}
	
	//是否可提交 
	public boolean isSub(String context,String plan_id,String a0100,String nbase,int article_type)
	{
		boolean flag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select count(*) from per_article where article_type="+article_type+" and  plan_id="+plan_id+" and fileflag=2 and a0100='"+a0100+ "' and lower(nbase)='"+nbase.toLowerCase()+"'");
			int n=0;
			if(rowSet.next())
				n=rowSet.getInt(1);
			if(n==0&&(context==null||context.trim().length()==0))
				flag=false;
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	public String getUnManager(String plan_id,String object_id)
	{
		String a0100="";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			RowSet rowSet=dao.search("select mainbody_id from per_mainbody where plan_id="+plan_id+" and object_id='"+object_id+"' and body_id=-1   ");
			if(rowSet.next())
				a0100=rowSet.getString(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a0100;
	}
	
	private ArrayList getSummaryFileIdsList(String plan_id,String article_type)
	{
		ArrayList summaryFileIdsList=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer strsql=new StringBuffer("");
			strsql.append("select * from per_article  where plan_id="+plan_id+" and a0100='"+this.userView.getA0100()+"' " );
			strsql.append(" and lower(nbase)='"+this.userView.getDbname().toLowerCase()+"'  and article_type="+article_type+" order by fileflag");
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				
				if(this.frowset.getInt("fileflag")==2)  //附件
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("id", this.frowset.getString("Article_id"));
					abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
					summaryFileIdsList.add(abean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return summaryFileIdsList;
	}
	
	//新建个人目标记录
	private int insertPerArticleRecord(String planid,int article_type,int fileflag,String nbase,String a0100)
	{
		int article_id=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
	//		DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
	//		dbmodel.reloadTableModel("per_article");
			RecordVo avo=new RecordVo("per_article");
			article_id= DbNameBo.getPrimaryKey("per_article","article_id",this.frameconn);
			avo.setInt("article_id", article_id);
			avo.setInt("plan_id",Integer.parseInt(planid));
			String b0110="";String e0122="";String e01a1="";
			this.frowset=dao.search("select b0110,e0122,e01a1,a0101 from "+nbase+"A01 where a0100='"+a0100+"'");
			if(this.frowset.next())
			{
				b0110=this.frowset.getString("b0110")!=null?this.frowset.getString("b0110"):"";
				e0122=this.frowset.getString("e0122")!=null?this.frowset.getString("e0122"):"";
				e01a1=this.frowset.getString("e01a1")!=null?this.frowset.getString("e01a1"):"";
				avo.setString("a0101",this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"");
			}
			avo.setString("b0110",b0110);
			avo.setString("e0122", e0122);
			avo.setString("e01a1", e01a1);
			avo.setString("nbase",nbase);
			avo.setString("a0100",a0100);
			avo.setInt("article_type", article_type);
			avo.setInt("fileflag",fileflag);
			avo.setInt("state",0);
			dao.addValueObject(avo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return article_id;
	}
	
	
	
	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
    private Blob getOracleBlob(FormFile file, String tablename, int article_id) throws FileNotFoundException, IOException {
        InputStream in = null;
        Blob blob = null;
        try {
            StringBuffer strSearch = new StringBuffer();
            strSearch.append("select affix from ");
            strSearch.append(tablename);
            strSearch.append(" where article_id=");
            strSearch.append(article_id);
            strSearch.append(" FOR UPDATE");

            StringBuffer strInsert = new StringBuffer();
            strInsert.append("update  ");
            strInsert.append(tablename);
            strInsert.append(" set affix=EMPTY_BLOB() where article_id=");
            strInsert.append(article_id);
            OracleBlobUtils blobutils = new OracleBlobUtils(this.getFrameconn());
            in = file.getInputStream();
            blob = blobutils.readBlob(strSearch.toString(), strInsert.toString(), in); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null)
                PubFunc.closeResource(in);
        }
        return blob;
    }
    
    
    /**
     * 设定了自我评价界面不显示打分模板 && 没设定多人打分时同时显示自我评价的场合,提交绩效报告时自动将自我评分状态置为已评分，并更新待办表
     * @param dao
     * @param planid
     * @param a0100
     * @param nbase
     */
    private void updateMainbodyStatus(ContentDAO dao,String planid,String a0100,String nbase)
    {
    	RowSet rs=null;
    	try
    	{ 
			// 获得需要的计划参数
		    LoadXml loadXml = null; //new LoadXml();
	    	if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null){						
				loadXml = new LoadXml(this.getFrameconn(),planid);
			}else{
				loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
			}
	        Hashtable htxml = loadXml.getDegreeWhole();
	        String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");  // 【自我评价界面不显示打分模板】参数
	        String mitiScoreMergeSelfEval=(String)htxml.get("mitiScoreMergeSelfEval");  // 【多人打分时同时显示自我评价】参数
	        if("true".equalsIgnoreCase(SelfEvalNotScore) && "false".equalsIgnoreCase(mitiScoreMergeSelfEval)){//设定了自我评价界面不显示打分模板 && 没设定多人打分时同时显示自我评价的场合
	        	/** 更新<<考核主体信息表>>为已评价 */
	        	int id = 0;
	        	String sql ="update per_mainbody set status=2 where plan_id = ? and Object_id = ? and mainbody_id = ?"; 
	        	ArrayList list = new ArrayList();
	        	list.add(planid);
	        	list.add(a0100);
	        	list.add(a0100);
	        	dao.update(sql,list);
	        	
	        	/** 更新<<待办表>> */
	        	//<<考核主体信息表>>中，当前计划下本人为考核主体的场合，是否有未打分的记录。有的话就不更新待办表。
	        	sql = "select id from per_mainbody where plan_id = ? and mainbody_id = ? and status in(0,1)"; 
	        	list = new ArrayList();
	        	list.add(planid);
	        	list.add(a0100);
	        	rs = dao.search(sql,list);
	        	//没有未打分的记录，更新待办表。
	        	if(!rs.next()){
	        		// 查询待办ID
	        		String objectId = nbase + a0100;
	        		sql = "select pending_id from t_hr_pendingtask where lower(receiver) = ? and ext_flag = ?";
	        		list = new ArrayList();
	            	list.add(objectId.toLowerCase());
	            	list.add("PERPF_" + planid);
	            	rs = dao.search(sql,list);
	            	int pending_id = 0;//待办ID
	            	if(rs.next()){
	            		pending_id = Integer.parseInt(rs.getString("pending_id"));
	            	}
	        		// 更新待办
	            	if(pending_id>0)
	            	{
		            	sql = "update t_hr_pendingtask set pending_status = 2 where pending_id = ?"; 
		            	list = new ArrayList();
		            	list.add(pending_id);
		            	dao.update(sql,list); 
		            	PendingTask pt = new PendingTask();
		        		pt.updatePending("P","PER"+rs.getInt(1), 1, "评分已提交！",this.userView);
	            	}
	        	}
	        	
	        }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		try
    		{
	    		if(rs!=null)
	    			rs.close();
    		}
    		catch(Exception ee)
    		{
    			ee.printStackTrace();
    		}
    	}
    	
    	
    }
    
    
}
