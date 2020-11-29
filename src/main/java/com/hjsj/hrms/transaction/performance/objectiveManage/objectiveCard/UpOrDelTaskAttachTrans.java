package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
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
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 上传 或 删除任务附件
 * @author dengc
 *
 */
public class UpOrDelTaskAttachTrans extends IBusiness {


	public void execute() throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("_opt");   // 1:保存附件 2删除附件 
			String planid=(String)this.getFormHM().get("planid");
			String object_id=(String)this.getFormHM().get("object_id");
			String p0400=(String)hm.get("_p0400");
			
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),this.userView,planid);
			RecordVo plan_vo=bo.getPlan_vo();
			String a_objectID=object_id;
			if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
			{
				LazyDynaBean un_functionaryBean=bo.getMainbodyBean(planid,object_id);
				if(un_functionaryBean!=null)
					a_objectID=(String)un_functionaryBean.get("mainbody_id");
			}
			
			
			if("1".equals(opt))//保存附件
			{
				
				FormFile form_file = (FormFile) getFormHM().get("file");
				String fileName=(String)this.getFormHM().get("fileName");
				fileName = PubFunc.hireKeyWord_filter(fileName); // 刘蒙
				
				String file_max_size="512";
				if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").length()>0)
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
					int article_id = insertPerArticleRecord(planid, 3, 2,a_objectID,p0400);
	
					String sql = "update per_article set ext=?,affix=?,Article_name=? where article_id=?";
					try(
					PreparedStatement pt = this.getFrameconn()
							.prepareStatement(sql);
					) {
						pt.setString(1, ext);
						// blob字段保存,数据库中差异
						switch (Sql_switcher.searchDbServer()) {
							case Constant.ORACEL:
								Blob blob = getOracleBlob(form_file, "per_article",
										article_id);
								pt.setBlob(2, blob);
								pt.setString(3, fileName);
								pt.setInt(4, article_id);
								break;
							default:
								byte[] data = form_file.getFileData();
								// a_vo.setObject("affix",data);
								pt.setBytes(2, data);
								pt.setString(3, fileName);
								pt.setInt(4, article_id);
								break;
						}
						// 打开Wallet
						dbS.open(this.getFrameconn(), sql);
						pt.execute();
					}
					
					
				}else {
					formHM.put("errorInfo", "上传文件大小超过"+file_max_size+"K，请重新上传！");
					formHM.remove("file");
					formHM.remove("fileName");
					return;
				}
				
				
				
			}
			else if("2".equals(opt)) //删除附件
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String article_id=(String)hm.get("article_id");
				dao.delete("delete from per_article where article_id="
						+ article_id, new ArrayList());
				  
			}
			
			
			ArrayList attachList=new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer strsql=new StringBuffer("select * from per_article  where plan_id="+planid+" and a0100='"+a_objectID+"' " );
			strsql.append(" and lower(nbase)='usr' and task_id="+p0400+"  and article_type=3 order by Article_id");  
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				if(this.frowset.getInt("fileflag")==2)  //附件
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("id", this.frowset.getString("Article_id"));
					abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
					attachList.add(abean);
				}
			}
			this.getFormHM().put("attachList",attachList);
			
			
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private int insertPerArticleRecord(String planid,int article_type,int fileflag,String a_objectid,String p0400)
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
			this.frowset=dao.search("select b0110,e0122,e01a1,a0101 from "+(this.userView.getDbname()==null||this.userView.getDbname().length()==0?"Usr":this.userView.getDbname())+"A01 where a0100='"+a_objectid+"'");
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
			avo.setString("a0100",a_objectid);
			avo.setString("a0101",a0101);
			avo.setInt("article_type", article_type);
			avo.setInt("task_id", Integer.parseInt(p0400));
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
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    InputStream fileStrem = null;
	    Blob blob = null;
	    try{
	    	fileStrem = file.getInputStream();
		    blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),fileStrem); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	        PubFunc.closeIoResource(fileStrem);//关闭资源 guodd 2014-12-19
	    }
		return blob;
	}
}
