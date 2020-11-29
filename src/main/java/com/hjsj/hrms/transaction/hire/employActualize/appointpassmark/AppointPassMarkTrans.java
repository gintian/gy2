package com.hjsj.hrms.transaction.hire.employActualize.appointpassmark;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 0521010023
 * <p>Title:AppointPassMarkTrans.java</p>
 * <p>Description>:AppointPassMarkTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 16, 2010 1:38:33 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class AppointPassMarkTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String field=SystemConfig.getPropertyValue("OfficialExamField");
			FieldItem item = DataDictionary.getFieldItem(field.toLowerCase());
			if(item==null)
				throw GeneralExceptionHandler.Handle(new Exception("分数指标设置不正确！"));
			if("0".equals(item.getUseflag()))
				throw GeneralExceptionHandler.Handle(new Exception("分数指标未构库！"));
			if(!"N".equalsIgnoreCase(item.getItemtype()))
				throw GeneralExceptionHandler.Handle(new Exception("分数指标设置不正确，必须为数值型指标！"));
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String score="0";
			if(!"0".equals(opt))
				score=(String)this.getFormHM().get("score");
			ArrayList fieldlist = this.getColumnList();
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			ArrayList dataList = this.getDataList(field, Integer.parseInt(score), dbname, fieldlist);
			this.getFormHM().put("score", score);
			this.getFormHM().put("columnList", fieldlist);
			this.getFormHM().put("dataList", dataList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public ArrayList getDataList(String field,int score,String dbname,ArrayList fieldlist)
	{
		ArrayList list = new ArrayList();
		try
		{
			/*select count(a0100) as num,z0311,z0321,z0325,p,um,un, z0315 from (
					select z0311,z0301,z0315,z0321,z0325,org1.codeitemdesc as p,org2.codeitemdesc as um,org3.codeitemdesc as un,zpt.a0100 from z03 left join 
					 (select a.zp_pos_id,a.a0100 from zp_pos_tache a,otha01 b where a.a0100=b.a0100 and b.c0106>=90 and resume_flag='12') 

					zpt on z03.z0301=zpt.zp_pos_id left join (select codeitemdesc,codeitemid from  organization where codesetid='@K') org1 
					on z03.z0311=org1.codeitemid left join (select codeitemdesc,codeitemid from organization where codesetid='UM') org2
					on z03.z0325=org2.codeitemid left join (select codeitemdesc,codeitemid from organization where codesetid='UN') org3
					on z03.z0321=org3.codeitemid 


					) a group by z0311,z0321,z0325,p,um,un,z0315*/
			// ArrayList fieldlist = this.getColumnList();
			 StringBuffer column= new StringBuffer("");
			 for(int j=0;j<fieldlist.size();j++)
			 {
				 FieldItem item = (FieldItem)fieldlist.get(j);
				 column.append(","+item.getItemid());
			 }
			 StringBuffer buf = new StringBuffer("");
			 buf.append("select count(a0100) as num,z0311,z0321,z0325,p,um,un "+(column.toString().length()>0?column.toString():"")+" from ");
			 buf.append("(select z0311,z0301,z0321,z0325 "+(column.toString().length()>0?column.toString():"")+",org1.codeitemdesc as p,org2.codeitemdesc as um,org3.codeitemdesc as un,zpt.a0100 from z03 left join ");
			 buf.append("(select a.zp_pos_id,a.a0100 from zp_pos_tache a,"+dbname+"a01 b where ");
			 buf.append("a.a0100=b.a0100 and b."+field+">="+score+" and resume_flag='12'");
			 buf.append(") zpt on z03.z0301=zpt.zp_pos_id ");
			 buf.append("left join (select codeitemdesc,codeitemid from  organization where codesetid='@K') org1 ");
			 buf.append("on z03.z0311=org1.codeitemid left join (select codeitemdesc,codeitemid from organization where codesetid='UM') org2 ");
			 buf.append("on z03.z0325=org2.codeitemid left join (select codeitemdesc,codeitemid from organization where codesetid='UN') org3 ");
			 buf.append("on z03.z0321=org3.codeitemid ");
			 buf.append(") a group by z0311,z0321,z0325,p,um,un"+(column.toString().length()>0?column.toString():"")+"");
			 ContentDAO dao = new ContentDAO(this.getFrameconn());
			 this.frowset=dao.search(buf.toString());
			 while(this.frowset.next())
			 {
				 LazyDynaBean bean = new LazyDynaBean();
				 bean.set("num",this.frowset.getString("num") );
				 bean.set("un", this.frowset.getString("un"));
				 bean.set("um", this.frowset.getString("um"));
				 bean.set("p", this.frowset.getString("p"));
				 for(int j=0;j<fieldlist.size();j++)
				 {
					 FieldItem item = (FieldItem)fieldlist.get(j);
					 if("A".equalsIgnoreCase(item.getItemtype()))
					 {
						 if(!"0".equals(item.getCodesetid()))
						 {
							 String value=this.frowset.getString(item.getItemid());
							 bean.set(item.getItemid().toLowerCase(), AdminCode.getCodeName(item.getCodesetid(),value));
						 }
						 else
						 {
							 String value=this.frowset.getString(item.getItemid())==null?"":this.frowset.getString(item.getItemid());;
							 bean.set(item.getItemid().toLowerCase(),value);
						 }
					 }else if("N".equalsIgnoreCase(item.getItemtype()))
					 {
						 bean.set(item.getItemid().toLowerCase(),this.frowset.getString(item.getItemid())==null?"":this.frowset.getString(item.getItemid()));
					 }
					 else if("D".equalsIgnoreCase(item.getItemtype()))
					 {
						 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						 Date value=this.frowset.getDate(item.getItemid());
						 if(value==null)
							 bean.set(item.getItemid(), "");
						 else
							 bean.set(item.getItemid(), format.format(value));
					 }
					 else
						 bean.set(item.getItemid().toLowerCase(),this.frowset.getString(item.getItemid())==null?"":this.frowset.getString(item.getItemid()));
				 }
				 list.add(bean);
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getColumnList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String fieldStr=SystemConfig.getPropertyValue("OfficialExamField_COLUMN");
			if(fieldStr==null|| "".equals(fieldStr))
			{
				return list;
			}
			String[] fieldArr=fieldStr.split(",");
			for(int i=0;i<fieldArr.length;i++)
			{
				String fieldid=fieldArr[i];
				if(fieldid==null|| "".equals(fieldid))
					continue;
				FieldItem item=DataDictionary.getFieldItem(fieldid.toLowerCase());
				if(item==null|| "0".equals(item.getUseflag())||!"z03".equalsIgnoreCase(item.getFieldsetid()))
					continue;
				list.add(item);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

}
