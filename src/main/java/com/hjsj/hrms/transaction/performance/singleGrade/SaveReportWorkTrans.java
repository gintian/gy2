package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveReportWorkTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
		String type=(String)map.get("type");//=1 content,=2 attach
		String planid=(String)this.getFormHM().get("dbpre");
		planid = PubFunc.hireKeyWord_filter(planid); // 刘蒙
		if(planid==null|| "".equals(planid))
			return;
		 
		
		
		FormFile form_file = (FormFile) getFormHM().get("file");
		String fileName=(String)this.getFormHM().get("fileName");
		fileName = PubFunc.hireKeyWord_filter(fileName); // 刘蒙
		
		String file_max_size="512";
		if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
		{
			file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
			if(file_max_size.toLowerCase().indexOf("k")!=-1)
				file_max_size=file_max_size.substring(0,file_max_size.length()-1);
		}
		
		if("2".equals(type))
		{
			if (form_file != null && form_file.getFileData().length > 0
					&& form_file.getFileData().length < Integer.parseInt(file_max_size)*1024) {
	
				String fname = form_file.getFileName();
				int indexInt = fname.lastIndexOf(".");
				String ext = fname.substring(indexInt + 1, fname.length());
				int article_id = insertPerArticleRecord(planid, 2, 2,userView.getDbname(),userView.getA0100());
				ArrayList paramList = new ArrayList();
				String sql = "update per_article set ext=?,affix=?,Article_name=? where article_id=?";
				paramList.add(ext);
				// blob字段保存,数据库中差异
				switch (Sql_switcher.searchDbServer()) {
				case Constant.ORACEL:
					Blob blob = getOracleBlob(form_file, "per_article",
							article_id);
					paramList.add(blob);
					paramList.add(fileName);
					paramList.add(article_id);
					break;
				default:
					byte[] data = form_file.getFileData();
					// a_vo.setObject("affix",data);
					paramList.add(data);
					paramList.add(fileName);
					paramList.add(article_id);
					break;
				}
				dao.update(sql,paramList);
			}
		}
		 String summary=(String)this.getFormHM().get("summary");
		 summary = PubFunc.hireKeyWord_filter(summary); // 刘蒙
		int article_id=0;
		this.frowset = dao.search("select * from per_article where plan_id="
				+ planid + " and a0100='"
				+ userView.getA0100()
				+ "' and lower(nbase)='"
				+ userView.getDbname().toLowerCase()
				+ "'  and article_type=2 and fileflag=1 ");
		if (this.frowset.next()) {
			article_id=this.frowset.getInt("article_id");
		}
		else
			article_id=insertPerArticleRecord(planid,2, 1,userView.getDbname(),userView.getA0100());
		RecordVo vo=new RecordVo("per_article");
		vo.setInt("article_id",article_id);
		vo=dao.findByPrimaryKey(vo);
		vo.setString("content", summary);
		if("3".equals(type))
		{
			vo.setInt("state",1);
		}
		if("1".equals(type))
		{
			vo.setInt("state",0);
		}
		dao.updateValueObject(vo);
		
		
		
		/*
		
		String tableName = "per_result_" + planid;
		RecordVo vo=new RecordVo(tableName);
		StringBuffer strsql=new StringBuffer();
		if(Integer.parseInt(type)==1)
		{
		  String summary=(String)this.getFormHM().get("summary");
		  if(summary==null||summary.equals(""))
			  summary="";
		  else
		     summary=summary.replaceAll("\r\n","#@#");
		
		  if(vo.hasAttribute("summarize"))
		  {
			strsql.append("update ");
			strsql.append(tableName);
			strsql.append(" set summarize=?");
			strsql.append(" where object_id='");
			strsql.append(userView.getA0100());
			strsql.append("'");
			ArrayList list = new ArrayList();
			list.add(summary);
			dao.update(strsql.toString(),list);
		  }
		}
		  if(Integer.parseInt(type)==2)
		  {
				FormFile form_file = (FormFile) getFormHM().get("file");
				if(form_file!=null&&form_file.getFileData().length>0&&form_file.getFileData().length<524288)
				{
					
					
				   	 String fname=form_file.getFileName();
				   	 int indexInt=fname.lastIndexOf(".");
				   	 int id=0;
				   	 this.frowset=dao.search("select id from "+tableName+" where object_id='"+userView.getA0100()+"'");
				   	 if(this.frowset.next())
				   		 id=this.frowset.getInt(1);
				   	 String ext=fname.substring(indexInt+1,fname.length());
				   	 vo.setInt("id",id);
				   	 RecordVo a_vo=dao.findByPrimaryKey(vo);
				     a_vo.setString("ext",ext);
					 // blob字段保存,数据库中差异
					 switch(Sql_switcher.searchDbServer())
					 {
					 	   case Constant.ORACEL:
					 			break;
					 	   default:
								byte[] data=form_file.getFileData();				
					 	   		a_vo.setObject("affix",data);
					 			break;
					 }				
								
					dao.updateValueObject(a_vo);
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					{
						RecordVo updatevo=dao.findByPrimaryKey(vo);
					 	Blob blob = getOracleBlob(form_file,tableName,userView.getA0100());
					 	updatevo.setObject("affix",blob);			
						dao.updateValueObject(updatevo);
					}
					
				}
		  }
		  */
		  
		  
		  
		  
				this.getFormHM().put("dbpre",planid);
				
				
			}
			
			catch(Exception ee)
			{
				ee.printStackTrace();
			    throw GeneralExceptionHandler.Handle(ee);					
			}
	}
	
	
	//新建个人目标记录
	private int insertPerArticleRecord(String planid,int article_type,int fileflag,String nbase,String a0100)
	{
		int article_id=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
			dbmodel.reloadTableModel("per_article");
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
	private Blob getOracleBlob(FormFile file,String tablename,int article_id) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select affix from ");
		strSearch.append(tablename);
		strSearch.append(" where article_id=");
		strSearch.append(article_id);		
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set affix=EMPTY_BLOB() where article_id=");
		strInsert.append(article_id);
		Blob blob = null;
		InputStream inputStream = null;
	    try{
	    	inputStream = file.getInputStream();
	    	OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    	blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),inputStream); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
	    } catch (Exception e){
	    	e.printStackTrace();
	    } finally {
	    	PubFunc.closeResource(inputStream);//资源释放 jingq 2014.12.29
	    }
		return blob;
	}

}
