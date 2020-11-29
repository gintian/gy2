package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
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
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Hashtable;

public class AddReportWorkTrans extends IBusiness{
	public void execute() throws GeneralException {
		String planid=(String)this.getFormHM().get("dbpre");
		planid = PubFunc.hireKeyWord_filter(planid); // 刘蒙
		if(planid==null|| "".equals(planid))
			return;
		
		ArrayList dblist = new ArrayList();
		/**考评结果表*/
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
		try
		{
		    if(planid == null || planid.length() < 1)
                planid = "0";
		    
		    dblist = getPlanList();
		    if("0".equals(planid)){
		        CommonData cd =  (CommonData) dblist.get(0);
		        planid = cd.getDataValue();
		    }
			ArrayList summaryFileIdsList=new ArrayList();
			String   summary=" ";
			String   summaryState="0";
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from per_article  where plan_id="+planid+" and a0100='"+this.userView.getA0100()+"' " );
			strsql.append(" and lower(nbase)='"+this.userView.getDbname().toLowerCase()+"'  and article_type=2 order by fileflag");
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
			
			this.getFormHM().put("summary",summary);
			this.getFormHM().put("summaryFileIdsList",summaryFileIdsList);
	 
			this.getFormHM().put("summaryState", summaryState);
		
			this.getFormHM().put("dbpre",planid);
			this.getFormHM().put("dblist",getPlanList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		/*
		String tableName = "per_result_"+planid;
		{
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				dbmodel.reloadTableModel(tableName);
		}
		StringBuffer strsql=new StringBuffer();
//		如果没有该字段则动态产生
		updateTable(tableName);
		RecordVo vo=new RecordVo(tableName);
		if(vo.hasAttribute("summarize"))
		{
			strsql.append("select summarize,ext from ");
			strsql.append(tableName);
			strsql.append(" where object_id='");
			strsql.append(userView.getA0100());
			strsql.append("'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				this.frowset=dao.search(strsql.toString());
				if(this.frowset.next())
				{
					//String sdf=Sql_switcher.readMemo(this.frowset,"summarize").replaceAll("#@#","\r\n");
					this.getFormHM().put("summary",Sql_switcher.readMemo(this.frowset,"summarize").replaceAll("#@#","\r\n"));
					if(this.frowset.getString("ext")!=null)
						this.getFormHM().put("isFile","1");
					else
						this.getFormHM().put("isFile","0");
				}
				this.getFormHM().put("dbpre",planid);
				this.getFormHM().put("dblist",getPlanList());
			}
			
			catch(Exception ee)
			{
				ee.printStackTrace();
			    throw GeneralExceptionHandler.Handle(ee);					
			}
		}
		*/
		
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
	public ArrayList getPlanList(){
		ArrayList dblist =new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			 StringBuffer perPlanSql= new StringBuffer();
			 perPlanSql.append("select plan_id,name,status,parameter_content from per_plan where ( status=4 or status=6 ) ");
	         perPlanSql.append("and plan_id in (select plan_id from per_mainbody where  object_id='");
	         perPlanSql.append(userView.getA0100()+"'  )"); 	          
	         perPlanSql.append(" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
	         this.frowset=dao.search(perPlanSql.toString());
	         LoadXml loadXml=null; //new LoadXml();
             while(this.frowset.next())
             {
                 String name=this.getFrowset().getString("name");
                 String plan_id=this.getFrowset().getString("plan_id");
            //     String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
            //     String performanceType=loadXml.getPerformanceType(xmlContent);
                 if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
 				{
 					
 					loadXml = new LoadXml(this.getFrameconn(),plan_id);
 					BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadXml);
 				}
 				else
 				{
 					loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
 				}
 				Hashtable htxml = loadXml.getDegreeWhole();
 				String performanceType=(String)htxml.get("performanceType");  
                 if("1".equals(performanceType))
                 {
                	 CommonData vo=new CommonData(plan_id,name);
                	 dblist.add(vo);
                 }
             }
             
             if(dblist != null && dblist.size() < 1){
                 CommonData vo1=new CommonData("0"," ");
                 dblist.add(vo1); 
             }
            
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return dblist;
	}

}




