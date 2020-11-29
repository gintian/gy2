package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SaveSummaryAffixBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:初始化个人总结页面</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 1, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SearchCommentTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String planid=(String)hm.get("_plan_id");   //   (String)this.getFormHM().get("planid");
			
			//查询模板是否存在
			SaveSummaryAffixBo isnullaffix = new SaveSummaryAffixBo(this.getFrameconn());
			String temp = isnullaffix.isnullArticle_name(planid);
			this.getFormHM().put("isnull", temp);
			
			
			if(hm.get("b_searchComment")!=null&& "link".equalsIgnoreCase((String)hm.get("b_searchComment")))
			{	
				this.getFormHM().put("errorInfo","");
				hm.remove("b_searchComment");
			}

			String _plan_id_o=(String)hm.get("_plan_id_o");
			String object_id=(String)this.getFormHM().get("object_id");
			ArrayList summaryFileIdsList=new ArrayList();
			String summaryState="0"; //绩效报告状态 0：编辑 1：提交  2:批准  3：驳回
			String summary="";
			String rejectCauseDesc="";
			String isUnderLeader="0";
			String planStatus="0";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer strsql=new StringBuffer();
			
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),this.userView,planid);
			isUnderLeader=bo.isUnderLeader("Usr",object_id,planid,this.userView.getA0100());
			
			String  model_opt=(String)hm.get("model_opt");
			
			
		 
			RecordVo plan_vo=bo.getPlan_vo();
			String a_objectID=object_id;
			if(plan_vo.getInt("object_type")==1||plan_vo.getInt("object_type")==3||plan_vo.getInt("object_type")==4)
			{
				LazyDynaBean un_functionaryBean=bo.getMainbodyBean(planid,object_id);
				if(un_functionaryBean!=null)
					a_objectID=(String)un_functionaryBean.get("mainbody_id");
			}
			
			String cycle="";
			String value="";
			ArrayList summary_planList=new ArrayList();
			String summary_planID="";
			if(SystemConfig.getPropertyValue("show_all_reports")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("show_all_reports")))
			{
				if("read".equals(model_opt)&& "1".equals(isUnderLeader))
				{
					this.frowset=dao.search("select * from per_plan where plan_id="+planid);
					if(this.frowset.next())
					{
						cycle=this.frowset.getString("cycle");
						if("0".equals(cycle)) //by year
							value=this.frowset.getString("theyear");
						if("1".equals(cycle)|| "2".equals(cycle)) //by half year
							value=this.frowset.getString("Thequarter");
						if("3".equals(cycle)) //by month
							value=this.frowset.getString("themonth");
					}
					summary_planList=bo.getSummaryList(_plan_id_o,cycle,value,object_id);
					
					if(summary_planList.size()>0&&!hasValue(summary_planList,planid))
					{
						planid=(String)((LazyDynaBean)summary_planList.get(0)).get("plan_id");
					}
					summary_planID=planid;
				}
			}
			
			
			
			
			
			strsql.append("select * from per_article  where plan_id="+planid+" and a0100='"+a_objectID+"' " );
			strsql.append(" and lower(nbase)='usr'  and article_type=2 order by fileflag");
			
			this.frowset=dao.search("select * from per_plan where plan_id="+planid);
			if(this.frowset.next())
				planStatus=this.frowset.getString("status");
			
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				if(this.frowset.getInt("fileflag")==1)  //文本
				{
					summary=Sql_switcher.readMemo(this.frowset,"Content");
					summaryState=this.frowset.getString("state");
					rejectCauseDesc=Sql_switcher.readMemo(this.frowset,"description");
				}
				else if(this.frowset.getInt("fileflag")==2)  //附件
				{
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("id", this.frowset.getString("Article_id"));
					abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
					summaryFileIdsList.add(abean);
				}
			}
			
			if(summary.length()==0)
				summary="";
			
			//RenderRelationBo rbo=new RenderRelationBo(this.getFrameconn(),this.userView);
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
	        
			
			 this.getFormHM().put("summary_planList",summary_planList);
			 this.getFormHM().put("summary_planID",summary_planID);
			 this.getFormHM().put("planStatus",planStatus);
			 
			 this.getFormHM().put("isUnderLeader",isUnderLeader);
			 this.getFormHM().put("rejectCauseDesc",rejectCauseDesc);
			 this.getFormHM().put("summary",summary);
			 this.getFormHM().put("summaryFileIdsList",summaryFileIdsList);
			 this.getFormHM().put("summaryState",summaryState);
			 this.getFormHM().put("allowUploadFile",allowUploadFile);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		/**
		String tableName = "per_result_" + planid;
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
			strsql.append(object_id);
			strsql.append("'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try
			{
				this.frowset=dao.search(strsql.toString());
				if(this.frowset.next())
				{
					//String sdf=Sql_switcher.readMemo(this.frowset,"summarize").replaceAll("#@#","\r\n");
					this.getFormHM().put("summary",Sql_switcher.readMemo(this.frowset,"summarize"));
					if(this.frowset.getString("ext")!=null)
						this.getFormHM().put("isFile","1");
					else
						this.getFormHM().put("isFile","0");
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			    throw GeneralExceptionHandler.Handle(ee);					
			}
		}*/

	}
	
	
	public boolean hasValue(ArrayList planList,String plan_id)
	{
		boolean flag=false;
		for(int i=0;i<planList.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)planList.get(i);
			String _plan_id=(String)abean.get("plan_id");
			if(plan_id.equals(_plan_id))
			{
				flag=true;
				break;
			}
		}
		return flag;
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
