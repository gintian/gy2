/**
 * 
 */
package com.hjsj.hrms.transaction.ht.inform;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 *<p>Title:SearchPrivDataTrans</p> 
 *<p>Description:查询权限数据交易</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-15:下午02:25:00</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchPrivDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			/**库前缀列表*/
			ArrayList list=this.userView.getPrivDbList();
			ConstantXml csxml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
			String dbstr=csxml.getTextValue("/Params/nbase");
			dbstr=dbstr!=null?dbstr:"";
			ArrayList dblist=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				String pre=(String)list.get(i);
				if(dbstr.length()>0){
					if(dbstr.toLowerCase().indexOf(pre.toLowerCase())==-1){
						continue;
					} else {
						CommonData data=new CommonData(pre,AdminCode.getCodeName("@@", pre));
						dblist.add(data);
					}
				}
				
			}//for i loop end.
			if(dblist.size()==0)
				throw new GeneralException(ResourceFactory.getProperty("workbench.stat.noprivdbname"));
			this.getFormHM().put("dblist",dblist);
			
			/**应用库前缀*/
			String nbase = "," +dbstr.toLowerCase() + ",";
			String dbname=(String)this.getFormHM().get("dbname");
			if(dbname==null|| "".equalsIgnoreCase(dbname) || !dbstr.contains(","+dbname.toLowerCase()+",")) {
				CommonData comm = (CommonData) dblist.get(0);
				dbname= comm.getDataValue();
			}
			this.getFormHM().put("dbname",dbname);

			list=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			if(list.size()==0)
				throw new GeneralException(ResourceFactory.getProperty("workbench.stat.noprivset"));
			ArrayList setlist=new ArrayList();
			
			String htmain = csxml.getTextValue("/Params/htmain");
			htmain=htmain!=null&&htmain.trim().length()>0?htmain:"";
			if(htmain.trim().length()<1){
				htmain = csxml.getConstantValue("HETONGMAIN");
				htmain=htmain!=null&&htmain.trim().length()>0?htmain:"";
			}
			
			String htset = csxml.getTextValue("/Params/htset");
			htset=htset!=null&&htset.trim().length()>0?htset:"";
			if(htset.trim().length()<1){
				htset = csxml.getConstantValue("HETONGSET");
				htset=htset!=null&&htset.trim().length()>0?htset:"";
			}
			FieldSet setmain = null;
			for(int i=0;i<list.size();i++){
				FieldSet fieldset=(FieldSet)list.get(i);
				/**未构库不加进来*/
				if("0".equalsIgnoreCase(fieldset.getUseflag()))
					continue;
				if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
					continue;
				if("A01".equalsIgnoreCase(fieldset.getFieldsetid()))
					continue;
				ArrayList checklist=this.userView.getPrivFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
				if(checklist.size()<1)
					continue;
				if(htmain.length()>0){
					if(htmain.toLowerCase().indexOf(fieldset.getFieldsetid().toLowerCase())!=-1){
						setmain = fieldset;
						continue;
					}
				}
				//合同相关子集为空时，跳出循环，注：此处循环中需要判断子集是否是配置的合同子集，故将合同相关子集是否为空的判断加到循环中    add  chenxg  2015-08-05
				if(htset == null || htset.length() < 1)
				    continue;
				
				if(htset.length()>0){
					if(htset.toLowerCase().indexOf(fieldset.getFieldsetid().toLowerCase())==-1)
						continue;
				}
				CommonData temp=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
				setlist.add(temp);
			}//for i loop end.
			if(setmain!=null){
				CommonData temp=new CommonData(setmain.getFieldsetid(),setmain.getCustomdesc());
				setlist.add(0,temp);
			}
			this.getFormHM().put("setlist",setlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
