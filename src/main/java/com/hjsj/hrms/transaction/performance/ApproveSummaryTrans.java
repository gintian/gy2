package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
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

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:批准 绩效/目标 </p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 15, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class ApproveSummaryTrans extends IBusiness {


	public void execute() throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("b_opt");  // opt1:批准 opt2:驳回 opt3:删除附件 opt4:保存报告  opt5:提交报告 opt6:上传附件
			String object_id=(String)hm.get("objectId");
			String plan_id=(String)hm.get("planNum");
			String optUrl=(String)hm.get("optUrl");
			String a0100=this.userView.getA0100();
			String nbase=this.userView.getDbname();
			
			if(object_id!=null && object_id.trim().length()>0 && "~".equalsIgnoreCase(object_id.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
	        { 
	        	String _temp = object_id.substring(1); 
	        	object_id = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
	        }			
			
			if(optUrl.indexOf("2")!=-1)
			    object_id=getUnManager(plan_id,object_id);
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String Article_type="2"; //绩效报告
			if(optUrl!=null&&("goal".equals(optUrl)|| "goal2".equals(optUrl)))
				Article_type="1";    //绩效目标
			if("opt1".equals(opt))
			{
				RowSet rowSet=dao.search("select count(*) from  per_article where plan_id="+plan_id+" and fileflag=1 and Article_type="+Article_type+" and  a0100='"+object_id+"'");
				int count=0;
				if(rowSet.next())
					count=rowSet.getInt(1);
				if(count==0)
				{
					insertPerArticleRecord(plan_id,Integer.parseInt(Article_type),1,object_id);
				}
				dao.update("update per_article set state=2 where plan_id="+plan_id+" and fileflag=1 and Article_type="+Article_type+" and  a0100='"+object_id+"'");	
			}
			else if("opt2".equals(opt))  //驳回
			{
				String rejectCause=(String)this.getFormHM().get("rejectCause");
				rejectCause = PubFunc.hireKeyWord_filter(rejectCause); // 刘蒙
				String sql = "update per_article set state=3,description=? where plan_id="+plan_id+" and fileflag=1 and Article_type="+Article_type+" and  a0100='"+object_id+"'";
				try(
				PreparedStatement pt=this.getFrameconn().prepareStatement(sql);
				) {
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
						Reader clobReader = new StringReader(rejectCause);
						pt.setCharacterStream(1, clobReader, rejectCause.length());
					} else
						pt.setString(1, rejectCause);
					// 打开Wallet
					dbS.open(this.getFrameconn(), sql);
					pt.execute();
				}
			}
			else if("opt3".equals(opt)) //驳回附件
			{ 
				String article_id = (String) hm.get("article_id");
				dao.delete("delete from per_article where article_id="
						+ article_id, new ArrayList());
			}
			else if("opt4".equals(opt)|| "opt5".equals(opt))
			{
				String _str="目标";
				int article_type=1;
				String context ="";
				if(optUrl!=null&&("goal".equals(optUrl)|| "goal2".equals(optUrl)))
				{
					context=(String) this.getFormHM().get("goalContext");
					context = PubFunc.hireKeyWord_filter(context); // 刘蒙
					article_type=1;
				}
				else
				{
					_str="报告";
					context=(String) this.getFormHM().get("summary");
					context = PubFunc.hireKeyWord_filter(context); // 刘蒙
					article_type=2;
				}
				int article_id=0;
				this.frowset = dao
				.search("select * from per_article where plan_id="
						+ plan_id + " and a0100='"
						+ a0100
						+ "' and lower(nbase)='"
						+ nbase.toLowerCase()
						+ "'  and article_type="+article_type+" and fileflag=1 ");
				if (this.frowset.next()) {
					article_id=this.frowset.getInt("article_id");
				}
				else
					article_id=insertPerArticleRecord(plan_id,article_type, 1,a0100);
				RecordVo vo=new RecordVo("per_article");
				vo.setInt("article_id",article_id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("content", context); 
				boolean isSub=true;
				if("opt5".equals(opt))
				{
					isSub=isSub(context,plan_id,a0100,nbase,article_type);
					if(isSub)
						vo.setInt("state",1);
					else
						vo.setInt("state",0);
				}
				if("opt4".equals(opt))
				{
					vo.setInt("state",0);
				}
				dao.updateValueObject(vo); 
				if(!isSub)
				{
					throw GeneralExceptionHandler.Handle(new Exception("不允许提交没有内容的绩效"+_str+"!"));	 
				}
			}
			else if("opt6".equals(opt))
			{
				
				FormFile form_file =null; 
				String fileName="";
				int article_type=1;
				if(optUrl!=null&&("goal".equals(optUrl)|| "goal2".equals(optUrl)))
				{
					form_file = (FormFile) getFormHM().get("goalfile");
					fileName=(String)this.getFormHM().get("goalfileName");
					fileName = PubFunc.hireKeyWord_filter(fileName); // 刘蒙
					article_type=1;
				}
				else
				{
					form_file =(FormFile) getFormHM().get("file");
					fileName=(String)this.getFormHM().get("fileName");
					fileName = PubFunc.hireKeyWord_filter(fileName); // 刘蒙
					article_type=2;
				}
				
				String file_max_size="512";
				if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
				{
					file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
					if(file_max_size.toLowerCase().indexOf("k")!=-1)
						file_max_size=file_max_size.substring(0,file_max_size.length()-1);
				}
				
				
				if (form_file != null && form_file.getFileData().length > 0
						&& form_file.getFileData().length < Integer.parseInt(file_max_size)*1024) {

					String fname = form_file.getFileName();
					int indexInt = fname.lastIndexOf(".");
					String ext = fname.substring(indexInt + 1, fname.length());
					int article_id = insertPerArticleRecord(plan_id, article_type, 2,a0100);
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
				
				
				
				String context ="";
				if(optUrl!=null&&("goal".equals(optUrl)|| "goal2".equals(optUrl)))
				{
					context=(String) this.getFormHM().get("goalContext");
					context = PubFunc.hireKeyWord_filter(context); // 刘蒙
				}
				else
				{
					context=(String) this.getFormHM().get("summary");
					context = PubFunc.hireKeyWord_filter(context); // 刘蒙
				}
				int article_id=0;
				this.frowset = dao
				.search("select * from per_article where plan_id="
						+ plan_id + " and a0100='"
						+ a0100
						+ "' and lower(nbase)='"
						+ nbase.toLowerCase()
						+ "'  and article_type="+article_type+" and fileflag=1 ");
				if (this.frowset.next()) {
					article_id=this.frowset.getInt("article_id");
				}
				else
					article_id=insertPerArticleRecord(plan_id,article_type, 1,a0100);
				RecordVo vo=new RecordVo("per_article");
				vo.setInt("article_id",article_id);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("content", context); 
				vo.setInt("state",0);
				dao.updateValueObject(vo); 
				
				
				
				
				
				
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);					
		}

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
	
//	新建个人目标记录
	private int insertPerArticleRecord(String planid,int article_type,int fileflag,String object_id)
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
			String b0110="";String e0122="";String e01a1="";String a0101="";
			this.frowset=dao.search("select b0110,e0122,e01a1,a0101 from UsrA01 where a0100='"+object_id+"'");
			if(this.frowset.next())
			{
				b0110=this.frowset.getString("b0110")!=null?this.frowset.getString("b0110"):"";
				e0122=this.frowset.getString("e0122")!=null?this.frowset.getString("e0122"):"";
				e01a1=this.frowset.getString("e01a1")!=null?this.frowset.getString("e01a1"):"";
				a0101=this.frowset.getString("a0101")!=null?this.frowset.getString("a0101"):"";
			}
			avo.setString("b0110",b0110);
			avo.setString("e0122", e0122);
			avo.setString("e01a1", e01a1);
			avo.setString("nbase","Usr");
			avo.setString("a0100",object_id);
			avo.setString("a0101",a0101);
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
		InputStream is = null;
		Blob blob =null;
		try{
			is = file.getInputStream();
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
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
		blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),is); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(is);
		}
		return blob;
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
	
}
