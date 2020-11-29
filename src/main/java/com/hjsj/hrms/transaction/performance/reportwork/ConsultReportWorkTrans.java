package com.hjsj.hrms.transaction.performance.reportwork;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ConsultReportWorkTrans extends IBusiness{

	public void execute() throws GeneralException {
		
ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		try
		{
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			String plain="";
			String id="";
			String object_id="";
			if(map != null){
				plain=(String)map.get("plain");
				id=(String)map.get("id");
				object_id=(String)map.get("object_id");
			}
			
			ArrayList summaryFileIdsList=new ArrayList();
			String   summary=" ";
			String   summaryState="0";
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from per_article  where plan_id="+plain+" and a0100='"+object_id+"' " );
			strsql.append(" and lower(nbase)='usr'  and article_type=2 order by fileflag");
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				if(this.frowset.getInt("fileflag")==1)  //文本
				{
					summary=Sql_switcher.readMemo(this.frowset,"Content");
					summaryState=this.frowset.getString("state");
					
				}
				else if(this.frowset.getInt("fileflag")==2)  //附件
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("id", this.frowset.getString("Article_id"));
					abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
					summaryFileIdsList.add(abean);
				}
			}
			
			this.getFormHM().put("content",summary);
			this.getFormHM().put("summaryFileIdsList",summaryFileIdsList);
			this.getFormHM().put("summaryState", summaryState);
			this.getFormHM().put("plain_id",plain);
			this.getFormHM().put("id",object_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		/*
		
		
		try{
			HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
			String plain="";
			String id="";
			String object_id="";
			if(map != null){
				plain=(String)map.get("plain");
				id=(String)map.get("id");
				object_id=(String)map.get("object_id");
			}
			updateTable("per_result_"+plain);
			String content="";
			 String isFile="0";
			if(plain != null && plain.trim().length()>0){
			     StringBuffer sql = new StringBuffer();
			     sql.append("select summarize, ext from per_result_");
			     sql.append(plain);
			     sql.append(" where id ='");
			     sql.append(id+"'");
			    
			     ContentDAO dao = new ContentDAO(this.getFrameconn());
			     this.frowset=dao.search(sql.toString());
			     while(this.frowset.next()){
			    	 if(this.frowset.getString("summarize")!=null&&this.frowset.getString("summarize").trim().length()>=0)
				         content=this.frowset.getString("summarize").replaceAll("#@#","\r\n");
			    	 else
			    	 {
			    		 content=this.frowset.getString("summarize");
			    	 }
				     if(this.frowset.getString("ext")!=null)
				    	 isFile="1";
			     }
			}		
			this.getFormHM().put("plain_id",plain);
			this.getFormHM().put("id",object_id);
			this.getFormHM().put("content",content);
			this.getFormHM().put("isFile",isFile);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		*/
	}
	public void  updateTable(String tableName)
	{
		RecordVo vo=new RecordVo(tableName);
		try
		{
			
			Table table=new Table(tableName);
			int num=0;
			if(!vo.hasAttribute("summarize"))	//个人总结
			{
				Field obj=new Field("summarize","summarize");
				obj.setDatatype(DataType.CLOB);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");				
				table.addField(obj);
				num++;
			}
			
			if(!vo.hasAttribute("affix"))      //个人总结附件
			{
				Field obj=new Field("affix","affix");
				obj.setDatatype(DataType.BLOB);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");				
				table.addField(obj);
				num++;
			}
			if(!vo.hasAttribute("ext"))		  //附件扩展名
			{
				Field obj=new Field("ext","ext");
				obj.setDatatype(DataType.STRING);
				obj.setKeyable(false);			
				obj.setVisible(false);
				obj.setAlign("left");	
				obj.setLength(10);
				table.addField(obj);
				num++;
			}
			if(num>0)
			{
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				dbWizard.addColumns(table);
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				dbmodel.reloadTableModel(tableName);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	


}
