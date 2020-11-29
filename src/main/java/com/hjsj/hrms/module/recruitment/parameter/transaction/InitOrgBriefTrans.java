package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:初始化 企业介绍指标定义 界面</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 25, 2007:2:32:42 PM</p> 
 *@author zx
 *@version 4.0
 */
public class InitOrgBriefTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");   // 1: 初始化   2.根据传值进行查询
			String isVisible=(String)hm.get("isVisible");
			String type=(String)hm.get("type");//=0查询=1初始化
            String orgId = (String)this.getFormHM().get("orgId");
            String orgName = (String)this.getFormHM().get("orgName");
			String orgFieldIDs="";
			String contentType="";
			ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
			String IDs=parameterXMLBo.getBriefParaValue();
			//String contentType=(String)this.getFormHM().get("contentType");
			if(IDs!=null&&IDs.trim().length()>0){
				if(IDs.indexOf(",")!=-1){
			       String[] str_a = IDs.split(",");
			       orgFieldIDs=str_a[0];
			       contentType=str_a[1];
				}else{
					orgFieldIDs=IDs;
				}
			}
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
			ArrayList columns = new ArrayList();
			columns = parameterSetBo.getColumnList();//获取表头显示信息
			//获取sql
			String sql = parameterSetBo.getOrgIntroSql(this.getUserView(),orgFieldIDs,contentType,type,orgId,orgName);
			this.getFormHM().put("columns", columns);
			this.getFormHM().put("orderbystr", " order by codeitemid,a0000");
			this.getFormHM().put("str_sql", sql);
			//获取内容形式下拉框值
			ArrayList contentTypeList=parameterSetBo.getContentTypeList();
			ArrayList orgFieldList=parameterSetBo.getOrgFieldList();
			
			String contentTypeListJson = JSONArray.fromObject(contentTypeList).toString();
			String orgFieldListJson = JSONArray.fromObject(orgFieldList).toString();
			
			ArrayList list = new ArrayList();
			list = parameterSetBo.getContentTypeValueList();
			String contentJson = JSONArray.fromObject(list).toString();
			
			
			this.getFormHM().put("contentJson",contentJson);
			this.getFormHM().put("contentTypeListJson",contentTypeListJson);
			this.getFormHM().put("orgFieldListJson",orgFieldListJson);
			this.getFormHM().put("orgFieldList",orgFieldList);
			this.getFormHM().put("contentTypeList",contentTypeList);
			this.getFormHM().put("orgFieldIDs",orgFieldIDs);
			this.getFormHM().put("contentType",contentType);
			this.getFormHM().put("isVisible", isVisible);
			if("0".equals(type))
			{
		    	this.getFormHM().put("orgName",orgName==null?"":orgName);
		    	this.getFormHM().put("hiddenOrgId",orgId==null?"":orgId);
			}else
			{
				this.getFormHM().put("orgName","");
		    	this.getFormHM().put("hiddenOrgId","");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		

	}

}
