package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.*;
import com.hjsj.hrms.businessobject.train.TrParamXmlBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * <p>Title:GetTableColumnTrans.java</p>
 * <p>Description:取得表列信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 20, 2006 8:52:52 AM</p>
 * @author dengcan
 * @version 2.0
 *
 */
public class GetTableColumnTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String tableName=(String)this.getFormHM().get("tableName");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList fieldItemList=new ArrayList();
			
			if("zp_pos_tache".equalsIgnoreCase(tableName))
			{
				EmployActualize employActualize=new EmployActualize(this.getFrameconn());
				
				ArrayList list=employActualize.getTableColumn_headNameList();
				ArrayList tableColumnList=(ArrayList)list.get(1);
				for(int i=0;i<tableColumnList.size();i++)
				{
					LazyDynaBean aBean=(LazyDynaBean)tableColumnList.get(i);
					String itemid=(String)aBean.get("itemid");
					String itemtype=(String)aBean.get("itemtype");
					String codesetid=(String)aBean.get("codesetid");
					String itemdesc=(String)aBean.get("itemdesc");
					if(!"id".equals(itemid))
					{
						if("M".equalsIgnoreCase(itemtype))
							continue;
						StringBuffer fielditemInfo=new StringBuffer("");
						fielditemInfo.append("<@>"+itemid);
						fielditemInfo.append("<@>"+itemdesc);
						fielditemInfo.append("<@>"+itemtype);
						fielditemInfo.append("<@>"+codesetid);
						fieldItemList.add(fielditemInfo.substring(3));
					}
				}
			}
			else if("z05".equalsIgnoreCase(tableName))	//面试安排信息表
			{
				fieldItemList=getInterViewArrangeFields();
			}
			else if("zp_test_template".equalsIgnoreCase(tableName))	//面试考核
			{
				fieldItemList=getInterViewExamineField();
			}
			else if("personnelEmploy".equalsIgnoreCase(tableName))  //员工录用
			{
				fieldItemList=getPersonnelEmployField();
			}
			else if("engagePlan".equalsIgnoreCase(tableName))  //招聘计划
			{
				fieldItemList=getEngagePlanField();
			}
			else if("R31".equalsIgnoreCase(tableName))  //培训活动
			{
				fieldItemList=getTrainMovementField();
			}
			else
			{
				this.frowset=dao.search("select * from t_hr_busiField where fieldsetid='"+tableName+"'");
				while(this.frowset.next())
				{
					 //数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
					if("M".equalsIgnoreCase(this.frowset.getString("itemtype")))
						continue;
					if(this.frowset.getString("state")!=null&& "0".equals(this.frowset.getString("state")))
						continue;
					
					boolean flag=true;
					if("Z03".equalsIgnoreCase(tableName))
					{
						String opt=(String)this.getFormHM().get("opt");
						if(opt!=null&& "query".equals(opt)&&("z0301".equalsIgnoreCase(this.frowset.getString("itemid"))|| "z0101".equalsIgnoreCase(this.frowset.getString("itemid"))))
							flag=false;
					}
					if(flag)
					{
						StringBuffer fielditemInfo=new StringBuffer("");
						fielditemInfo.append("<@>"+this.frowset.getString("fieldsetid")+"."+this.frowset.getString("itemid"));
						fielditemInfo.append("<@>"+this.frowset.getString("itemdesc"));
						fielditemInfo.append("<@>"+this.frowset.getString("itemtype"));
						fielditemInfo.append("<@>"+this.frowset.getString("codesetid"));
						fieldItemList.add(fielditemInfo.substring(3));
					}
				}		
			}
			this.getFormHM().put("fields",fieldItemList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	
	/**
	 * 得到面试考核指标
	 * @return
	 */
	public ArrayList getInterViewExamineField() 
	{
		
		ArrayList fieldItemList=new ArrayList();
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String dbname=employActualize.getZP_DB_NAME();
		InterviewExamine interviewExamine=new InterviewExamine(this.getFrameconn());
		ArrayList tableColumnsList=new ArrayList();
		String columns=interviewExamine.getTableColumns(dbname,tableColumnsList,"0");
		ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
		HashMap map1;
		try {
			map1 = bo2.getAttributeValues();
		
		String hireMajor="";
		if(map1.get("hireMajor")!=null)
			hireMajor=(String)map1.get("hireMajor");  
		FieldItem hireMajoritem=null;
		if(hireMajor.length()>0)
		{
			hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
			if(hireMajoritem!=null){
			LazyDynaBean lazyDynaBean3=new LazyDynaBean();
			lazyDynaBean3.set("itemid",hireMajoritem.getItemid());
			lazyDynaBean3.set("itemtype",hireMajoritem.getItemtype());
			lazyDynaBean3.set("codesetid",hireMajoritem.getCodesetid());
			lazyDynaBean3.set("itemdesc",hireMajoritem.getItemdesc());//ResourceFactory.getProperty("hire.employActualize.interviewProfessional"));
			lazyDynaBean3.set("fieldsetid",hireMajoritem.getFieldsetid());
			tableColumnsList.add(lazyDynaBean3);
			}
		}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		for(Iterator t=tableColumnsList.iterator();t.hasNext();)
		{
			LazyDynaBean abean=(LazyDynaBean)t.next();
			String itemid=(String)abean.get("itemid");
			String fieldsetid=(String)abean.get("fieldsetid");
			
			//zxj 20150325 只判断是否是“A”开头是不严密的，如果人员库前缀本身也是A开头的就会多加人员库。
		    //由于外部传进来的指标有的加了前缀，有的没加，所以再增加判断是否开头是人员库前缀或和本身人员库相同
			if(fieldsetid.length()>0&&fieldsetid.charAt(0)=='A' && (!fieldsetid.startsWith(dbname)|| fieldsetid.equals(dbname)))
				fieldsetid=dbname+fieldsetid;
			
			if("particular".equals(itemid))
				continue;
			
			if("a0100".equals(itemid))
				continue;
			
				//abean.set("itemdesc","人员编号");
			else if("zp_pos_id".equals(itemid))
			{
				itemid="z0311";
				fieldsetid="z03";
			}
			if("departId".equals(itemid))
			{
				itemid="z0325";
				fieldsetid="z03";
			}
			
			if("M".equalsIgnoreCase((String)abean.get("itemtype")))
				continue;
			
			//数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
			StringBuffer fielditemInfo=new StringBuffer("");
			fielditemInfo.append("<@>"+fieldsetid+"."+itemid);
			fielditemInfo.append("<@>"+(String)abean.get("itemdesc"));
			fielditemInfo.append("<@>"+(String)abean.get("itemtype"));
			fielditemInfo.append("<@>"+(String)abean.get("codesetid"));
			fieldItemList.add(fielditemInfo.substring(3));
		}
		return fieldItemList;
	}
	
	public ArrayList getEngagePlanField()
	{
		ArrayList list=DataDictionary.getFieldList("Z01",Constant.USED_FIELD_SET);	
		ArrayList fieldItemList=new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			
			FieldItem item=(FieldItem)list.get(i);
			String itemid=item.getItemid();
			String itemdesc=item.getItemdesc();
			if("z0101".equalsIgnoreCase(itemid))
			{
				continue;
			}
			if("M".equalsIgnoreCase(item.getItemtype()))
				continue;
			StringBuffer fielditemInfo=new StringBuffer("");
			fielditemInfo.append("<@>"+item.getItemid());
			fielditemInfo.append("<@>"+item.getItemdesc());
			fielditemInfo.append("<@>"+item.getItemtype());
			fielditemInfo.append("<@>"+item.getCodesetid());
			fieldItemList.add(fielditemInfo.substring(3));
		}
		return fieldItemList;
	}
	
	
	//得到培训活动指标
	public ArrayList getTrainMovementField()
	{
		ArrayList fieldList=new ArrayList();
		String opt=(String)this.getFormHM().get("opt");
		try
		{
			ArrayList list=DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
			
			TrParamXmlBo trParamXmlBo=new TrParamXmlBo(this.getFrameconn());
			HashMap para_map=trParamXmlBo.getAttributeValues();
			String  fieldStr=(String)para_map.get("plan_mx");       //常量表  参数TR_PARAM (  R3121,R3124,R3125)
			if(fieldStr==null)
			{
				//fieldStr="";
				throw GeneralExceptionHandler.Handle(new Exception("请在cs中设置活动明细指标！"));
			}
			else
			{
				fieldStr=fieldStr.toLowerCase();
			//	if(fieldStr.indexOf("r3127")==-1)
			//		fieldStr+=",r3127";
				if(fieldStr.indexOf("r3101")==-1)
					fieldStr="r3101,"+fieldStr;
			}
			
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				Field field=(Field)item.cloneField();
				if(fieldStr.length()>0)
				{
					if(fieldStr.indexOf(item.getItemid().toLowerCase())==-1)
						continue;
				}
			
			//	if(opt.equals("query")&&item.getItemid().equalsIgnoreCase("r3101"))
			//		continue;
				
				if("M".equalsIgnoreCase(item.getItemtype()))
					continue;
				
				StringBuffer fielditemInfo=new StringBuffer("");
				
				fielditemInfo.append("<@>"+item.getFieldsetid()+"."+item.getItemid());
				fielditemInfo.append("<@>"+item.getItemdesc());
				fielditemInfo.append("<@>"+item.getItemtype());
				fielditemInfo.append("<@>"+item.getCodesetid());
				fieldList.add(fielditemInfo.substring(3));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return fieldList;
	}
	
	
	//得到员工录用 指标
	public ArrayList getPersonnelEmployField()
	{
		ArrayList fieldItemList=new ArrayList();
		
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String dbname=employActualize.getZP_DB_NAME();  //应用库前缀	
		InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
		String email_phone=interviewEvaluatingBo.getEmail_PhoneField();
		String isPhoneField=email_phone.split("/")[1];
		String isMailField=email_phone.split("/")[0];
		PersonnelEmploy personnelEmploy=new PersonnelEmploy(this.getFrameconn());
		ArrayList columnsList=personnelEmploy.getColumnList(isMailField,isPhoneField,dbname,"0");
		//columnList.add(getLazyDynaBean("Z0321","A","UN","Z03",ResourceFactory.getProperty("hire.interviewExamine.interviewUnit")));	
		try
		{
	    	ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
	    	HashMap map=xmlBo.getAttributeValues();
	    	String hireMajor="";
	    	if(map.get("hireMajor")!=null)
	    		hireMajor=(String)map.get("hireMajor");
	    	if(hireMajor!=null&&hireMajor.trim().length()>0)
	    	{
	    		FieldItem item = DataDictionary.getFieldItem(hireMajor.toLowerCase());
	    		if(item!=null)
	    	    	columnsList.add(personnelEmploy.getLazyDynaBean(item.getItemid(),item.getItemtype(),item.getCodesetid(),item.getFieldsetid(),item.getItemdesc()));	
	    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		for(Iterator t=columnsList.iterator();t.hasNext();)
		{
			LazyDynaBean abean=(LazyDynaBean)t.next();
			String itemid=(String)abean.get("itemid");
			String fieldsetid=(String)abean.get("fieldsetid");
			
			if("departId".equals(itemid))
				itemid="parentid";
			if("z05_state".equals(itemid))
				itemid="state";
			 if("z0311".equalsIgnoreCase(itemid))
				 fieldsetid="Z03";
//			数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
			if("M".equalsIgnoreCase((String)abean.get("itemtype")))
				continue;
			
			StringBuffer fielditemInfo=new StringBuffer("");
			
			fielditemInfo.append("<@>"+fieldsetid+"."+itemid);
			fielditemInfo.append("<@>"+(String)abean.get("itemdesc"));
			fielditemInfo.append("<@>"+(String)abean.get("itemtype"));
			fielditemInfo.append("<@>"+(String)abean.get("codesetid"));
			fieldItemList.add(fielditemInfo.substring(3));
		}
		
		return fieldItemList;
	}
	
	

	/**
	 * 面试安排信息表
	 */
	public ArrayList getInterViewArrangeFields()
	{
		ArrayList fieldItemList=new ArrayList();
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String dbname=employActualize.getZP_DB_NAME();  //应用库前缀
		InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
		String email_phone=interviewEvaluatingBo.getEmail_PhoneField();
		String isPhoneField=email_phone.split("/")[1];
		String isMailField=email_phone.split("/")[0];
		
		ArrayList list=interviewEvaluatingBo.getColumnList(DataDictionary.getFieldList("Z05",Constant.USED_FIELD_SET),isMailField,isPhoneField,dbname);
		list.remove(list.size()-1);
		ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
		HashMap map1;
		try {
			map1 = bo2.getAttributeValues();
		
		String hireMajor="";
		if(map1.get("hireMajor")!=null)
			hireMajor=(String)map1.get("hireMajor");  
		FieldItem hireMajoritem=null;
		if(hireMajor.length()>0)
		{
			hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
			if(hireMajoritem!=null){
			LazyDynaBean lazyDynaBean3=new LazyDynaBean();
			lazyDynaBean3.set("itemid",hireMajoritem.getItemid());
			lazyDynaBean3.set("itemtype",hireMajoritem.getItemtype());
			lazyDynaBean3.set("codesetid",hireMajoritem.getCodesetid());
			lazyDynaBean3.set("itemdesc",hireMajoritem.getItemdesc());//ResourceFactory.getProperty("hire.employActualize.interviewProfessional"));
			lazyDynaBean3.set("fieldsetid",hireMajoritem.getFieldsetid());
			list.add(lazyDynaBean3);
			}
		}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean aBean=(LazyDynaBean)list.get(i);
			 //数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
			StringBuffer fielditemInfo=new StringBuffer("");
			String a_itemid=(String)aBean.get("itemid");
			if("sendmail".equalsIgnoreCase(a_itemid))
				continue;
			if(!"z0501_html".equals(a_itemid)&&!"M".equals((String)aBean.get("itemtype")))
			{
				
				if("codeitemdesc".equals(a_itemid))
				{
					a_itemid="z0311";
					aBean.set("fieldsetid","z03");
				}
				if(!a_itemid.equalsIgnoreCase(isPhoneField)&&!a_itemid.equalsIgnoreCase(isMailField))
				{
					fielditemInfo.append("<@>"+(String)aBean.get("fieldsetid")+"."+a_itemid);
				}
				else
					fielditemInfo.append("<@>"+a_itemid);
				fielditemInfo.append("<@>"+(String)aBean.get("itemdesc"));
				fielditemInfo.append("<@>"+(String)aBean.get("itemtype"));
				fielditemInfo.append("<@>"+(String)aBean.get("codesetid"));
				fieldItemList.add(fielditemInfo.substring(3));
			}
		}
		return fieldItemList;
	}
	

}
