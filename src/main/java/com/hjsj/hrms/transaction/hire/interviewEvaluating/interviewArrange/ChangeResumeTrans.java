package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ChangeResumeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			String resumeStateFieldIds="";
			if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
				resumeStateFieldIds=(String)map.get("resume_state");
			String z0501=(String)this.getFormHM().get("z0501");
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn());
			String dbName=bo.getZpkdbName();	
			String arr[] =z0501.split("#");
			StringBuffer sql = new StringBuffer("");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
					continue;
				
				sql.append(",'"+ PubFunc.decrypt(arr[i])+"'");
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select a0100 from z05 where z0501 in ("+sql.toString().substring(1)+")");
			StringBuffer a0100 = new StringBuffer("");
			while(this.frowset.next())
			{
				a0100.append(",'"+this.frowset.getString("a0100")+"'");
			}
			String active_field="";
			if(map!=null&&map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
			{
				active_field=(String)map.get("active_field");
				active_field=","+active_field+"='1'";
			}
			/**将打分表里面的数据清空,等于重新来过hm.put("testTemplateID",tempLateIDSet);**/
			Set tempLateSet =(Set) map.get("testTemplateID");//得到普通的测评表ID
			ArrayList testTemplatAdvance=(ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
			ArrayList zpList=new ArrayList();//存放招聘中已经涉及到的模版表id
			Iterator it=tempLateSet.iterator();
			while(it.hasNext()){
				String templateId=(String) it.next();
				if(zpList.contains(templateId)){
					continue;
				}
				zpList.add(templateId);
			}
			for(int i=0;i<testTemplatAdvance.size();i++){
				 HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
		         String  templateId=(String) advanceMap.get("templateId");//得到测评结果对应模版表ID
		         if(zpList.contains(templateId)){
						continue;
					}
					zpList.add(templateId);
			}
			dao.update("update zp_pos_tache set resume_flag='11' where a0100 in ("+a0100.toString().substring(1)+") and resume_flag='12'");
			dao.update("update "+dbName+"A01 set "+resumeStateFieldIds+"=11 where a0100 in ("+a0100.toString().substring(1)+")");
			dao.delete("delete from z05 where z0501 in ("+sql.toString().substring(1)+")", new ArrayList());
			DbWizard dbWizard=new DbWizard(this.getFrameconn());
			
			/**开始删除模版表中的数据**/
			ArrayList sqlList=new ArrayList();
			String deleteSql="";
			for(int i=0;i<zpList.size();i++){
				String templateId=(String) zpList.get(i);
				String tableName="zp_test_result_"+templateId;
				if(!dbWizard.isExistTable(tableName,false)){
					deleteSql="delete from "+tableName+" where a0100 in("+a0100.toString().substring(1)+")";
					sqlList.add(deleteSql);
				}
			}
			sqlList.add("delete from zp_test_template where a0100 in("+a0100.toString().substring(1)+")");
			dao.batchUpdate(sqlList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
