package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SaveSummaryAffixBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:SearchMutualSummaryTrans.java</p>
 * <p>Description>:查找绩效报告</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-05-31 下午03:56:27</p>
 * <p>@version: 1.0</p>
 * <p>@author: JinChunhai
 */

public class SearchMutualSummaryTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String planid = (String)this.getFormHM().get("planNum");
		String objectid = (String)this.getFormHM().get("objectId");
		Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(planid);
        if(!isNum.matches()) {
        	planid=PubFunc.decryption(planid);
        }
        if(objectid != null && (!"".equals(objectid)) && (!"~".equalsIgnoreCase(objectid.substring(0,1)))){
        	isNum = pattern.matcher(objectid);
            if(!isNum.matches()) {
            	objectid=PubFunc.decryption(objectid);
            }
        }
		
			//查询模板是否存在
			SaveSummaryAffixBo isnullaffix = new SaveSummaryAffixBo(this.getFrameconn());
			String temp = isnullaffix.isnullArticle_name(planid);
			this.getFormHM().put("isnull", temp);
		
		if(objectid!=null && objectid.trim().length()>0 && "~".equalsIgnoreCase(objectid.substring(0,1))) // JinChunhai 2012-06-26 如果是通过转码传过来的需解码
        { 
        	String _temp = objectid.substring(1); 
        	objectid = PubFunc.convert64BaseToString(SafeCode.decode(_temp));
        }
		String isUnderLeader="";
		try
		{
			
			//ObjectCardBo bbo=new ObjectCardBo(this.getFrameconn(),this.userView);
			isUnderLeader=isUnderLeader(objectid,planid);//     bbo.isUnderLeader("Usr",objectid,planid,this.userView.getA0100());
			
			String planStatus="0";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(planid==null|| "".equals(planid))
			{
				this.getFormHM().put("summary"," ");			
				return;
			}
			if(objectid==null|| "".equals(objectid))
			{
				this.getFormHM().put("summary"," ");				
				return;		
			}
			
			this.frowset=dao.search("select * from per_plan where plan_id="+planid);
			if(this.frowset.next())
				planStatus=this.frowset.getString("status");
			
			String rejectCauseDesc="";
			String optUrl=(String)hm.get("optUrl");
			
			String a0100=objectid;
			if(optUrl!=null&&optUrl.indexOf("2")!=-1)
				a0100=getUnManager(planid,objectid);
			if(a0100.equalsIgnoreCase(this.userView.getA0100()))
				this.getFormHM().put("isSelf","true");
			else
				this.getFormHM().put("isSelf","false");
			
			if("goal".equals(optUrl)|| "goal2".equals(optUrl))
			{
				
				String goalContext=" ";   //目标内容
				
			
				ArrayList goalFileIdsList=new ArrayList();
				StringBuffer strsql=new StringBuffer();
				String goalState="0";
				strsql.append("select * from per_article  where plan_id="+planid+" and a0100='"+a0100+"' " );
				strsql.append(" and lower(nbase)='usr'  and article_type=1 order by fileflag");
				
				this.frowset=dao.search(strsql.toString());
				while(this.frowset.next())
				{
					if(this.frowset.getInt("fileflag")==1)  //文本
					{
						goalContext=Sql_switcher.readMemo(this.frowset,"Content");
						goalState=this.frowset.getString("state");
						rejectCauseDesc=Sql_switcher.readMemo(this.frowset,"description");
					}
					else if(this.frowset.getInt("fileflag")==2)  //附件
					{
						LazyDynaBean abean=new LazyDynaBean();
						abean.set("id", this.frowset.getString("Article_id"));
						abean.set("name", this.frowset.getString("Article_name")!=null?this.frowset.getString("Article_name"):"");
						goalFileIdsList.add(abean);
					}
				}
				this.getFormHM().put("rejectCauseDesc",rejectCauseDesc);
				this.getFormHM().put("goalState",goalState);
				this.getFormHM().put("goalContext",goalContext);
				this.getFormHM().put("goalFileIdsList",goalFileIdsList);
				
			}
			else
			{
				
					LoadXml loadxml=new LoadXml(this.getFrameconn(),planid);
					Hashtable htxml=new Hashtable();		
					htxml=loadxml.getDegreeWhole();
					String performanceType=(String)htxml.get("performanceType");		//考核形式  0：绩效考核  1：民主评测
					this.getFormHM().put("performanceType",performanceType);
					
					String summaryState="0";
					ArrayList summaryFileIdsList=new ArrayList();
					String   summary=" ";
					StringBuffer strsql=new StringBuffer();
					strsql.append("select * from per_article  where plan_id="+planid+" and a0100='"+a0100+"' " );
					strsql.append(" and lower(nbase)='usr'  and article_type=2 order by fileflag");
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
					this.getFormHM().put("rejectCauseDesc",rejectCauseDesc);
					this.getFormHM().put("summaryState",summaryState);
					this.getFormHM().put("summary",summary);
					this.getFormHM().put("summaryFileIdsList",summaryFileIdsList);
					
			}
			this.getFormHM().put("planStatus",planStatus);
			this.getFormHM().put("isUnderLeader",isUnderLeader);
			
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
			this.getFormHM().put("allowUploadFile", allowUploadFile);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);					
		}
		
	}
	
	
	//判断是否是考核对象的直接领导
	public String isUnderLeader(String object_id,String plan_id)
	{
		String isUnderLeader="0";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql="select ";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql+=" level_o";
			else
				sql+=" level ";
			sql+=" from per_mainbodyset where body_id=(select body_id from per_mainbody  where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"')";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				int level=rowSet.getInt(1);
				if(level==1)
					isUnderLeader="1";
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isUnderLeader;
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
