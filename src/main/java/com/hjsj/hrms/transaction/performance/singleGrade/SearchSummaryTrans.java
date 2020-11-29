package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SaveSummaryAffixBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class SearchSummaryTrans extends IBusiness {
	public void execute() throws GeneralException {
		try
		{
		
		String planid=(String)this.getFormHM().get("dbpre");
		planid = PubFunc.hireKeyWord_filter(planid); // 刘蒙
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		if(planid==null|| "".equals(planid))
			return;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		//查询模板是否存在
		SaveSummaryAffixBo isnullaffix = new SaveSummaryAffixBo(this.getFrameconn());
		String temp = isnullaffix.isnullArticle_name(planid);
		this.getFormHM().put("isnullAffix", temp);
			
		//是否隐藏附件
		// 获得需要的计划参数
	    LoadXml loadXml = null; //new LoadXml();
    	if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
		{						
			loadXml = new LoadXml(this.getFrameconn(),planid);
			BatchGradeBo.getPlanLoadXmlMap().put(planid,loadXml);
		}
		else
		{
			loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);
		}
        Hashtable htxml = loadXml.getDegreeWhole();
        String allowUploadFile = (String)htxml.get("AllowUploadFile"); 
        
		if(hm.get("optUrl")!=null&&("goal".equals((String)hm.get("optUrl"))|| "goal2".equals((String)hm.get("optUrl"))))
		{
			String optUrl=(String)hm.get("optUrl");
			String goalContext=" ";   //目标内容
			String   goalState="0";
			String   g_rejectCause="";
			ArrayList goalFileIdsList=new ArrayList();
			
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from per_article  where plan_id="+planid);
			if("goal".equals(optUrl))
				strsql.append(" and a0100='"+this.userView.getA0100()+"'  and lower(nbase)='"+this.userView.getDbname().toLowerCase()+"'" );
			else
			{	
				String a0100=this.userView.getA0100();
				this.frowset=dao.search("select object_type from per_plan where plan_id="+planid);
				int object_type=2;
				if(this.frowset.next())
					object_type=this.frowset.getInt("object_type");
				if(object_type!=2)
				{
					String _tmp = (String)this.getFormHM().get("object_id");
					_tmp = PubFunc.hireKeyWord_filter(_tmp); // 刘蒙
					String objectid=_tmp.replaceAll("／", "/").split("/")[0];
					a0100=getUnManager(planid,objectid);
				}
				strsql.append(" and a0100='"+a0100+"'  and lower(nbase)='usr'" );
			}
			strsql.append("  and article_type=1 order by fileflag");
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				if(this.frowset.getInt("fileflag")==1)  //文本
				{
					goalContext=Sql_switcher.readMemo(this.frowset,"Content");
					goalState=this.frowset.getString("state");
					g_rejectCause=Sql_switcher.readMemo(this.frowset,"description");
				}
				else if(this.frowset.getInt("fileflag")==2)  //附件
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("id", this.frowset.getString("Article_id"));
					abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
					goalFileIdsList.add(abean);
				}
			}
			
			if(goalContext.length()==0)
				goalContext=" ";
			 this.getFormHM().put("g_rejectCause",g_rejectCause);
			 this.getFormHM().put("goalContext",goalContext);
			 this.getFormHM().put("goalFileIdsList",goalFileIdsList);
			 this.getFormHM().put("goalState",goalState);
			
		}
		else
		{
			String optUrl=(String)hm.get("optUrl");
			String   summaryState="0";
			ArrayList summaryFileIdsList=new ArrayList();
			String   summary=" ";
			String   s_rejectCause="";
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from per_article  where plan_id="+planid);
			if("summary2".equals(optUrl))
			{
				String a0100=this.userView.getA0100();
				this.frowset=dao.search("select object_type from per_plan where plan_id="+planid);
				int object_type=2;
				if(this.frowset.next())
					object_type=this.frowset.getInt("object_type");
				if(object_type!=2)
				{
					String _tmp = (String)this.getFormHM().get("object_id");
					_tmp = PubFunc.hireKeyWord_filter(_tmp); // 刘蒙
					String objectid=_tmp.replaceAll("／", "/").split("/")[0];
					a0100=getUnManager(planid,objectid);
				}
				strsql.append(" and a0100='"+a0100+"'  and lower(nbase)='usr'" );
			}
			else
				strsql.append(" and a0100='"+this.userView.getA0100()+"' and lower(nbase)='"+this.userView.getDbname().toLowerCase()+"' " );
			strsql.append("  and article_type=2 order by fileflag");
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				if(this.frowset.getInt("fileflag")==1)  //文本
				{
					summary=Sql_switcher.readMemo(this.frowset,"Content");
					summaryState=this.frowset.getString("state");
					s_rejectCause=Sql_switcher.readMemo(this.frowset,"description");
					
				}
				else if(this.frowset.getInt("fileflag")==2)  //附件
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("id", this.frowset.getString("Article_id"));
					abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
					summaryFileIdsList.add(abean);
				}
			}
			this.getFormHM().put("s_rejectCause",s_rejectCause);
			this.getFormHM().put("summary",summary);
			this.getFormHM().put("summaryFileIdsList",summaryFileIdsList);
			this.getFormHM().put("summaryState", summaryState);
			this.getFormHM().put("allowUploadFile", allowUploadFile);
		}
		
		
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);					
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
	
	
	/**
	 * 动态产生表中没有的字段
	 * @param tableName
	 */
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
