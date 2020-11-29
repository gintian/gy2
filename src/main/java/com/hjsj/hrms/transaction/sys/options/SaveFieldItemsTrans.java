package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveFieldItemsTrans extends IBusiness {

	public void execute() throws GeneralException {
		String info="";
		String mysalarys = "";
		String sumitem = "";
		String items = (String) this.getFormHM().get("items");	
		String fieldsetidss=(String)this.getFormHM().get("fieldsetid");
		String one_Array[]=fieldsetidss.split("`");
		String fieldsetid=one_Array[0];
		String old_title="";
		if(one_Array.length>1)
			old_title=one_Array[1];
		String new_title=(String)this.getFormHM().get("title");
		if(new_title!=null||new_title.length()>0)
			new_title=new_title.replaceFirst("\"","'");
		String query_field=(String)this.getFormHM().get("query_field");
		if(fieldsetid==""||fieldsetid.length()<=0)
		{
			this.getFormHM().put("info","false");
			return;
		}
		if(query_field==null||query_field.length()<=0)
			query_field="";		
		String [] item = items.split("`");
		for(int i = 0; i<item.length; i++){
			String itemid = item[i];
			if(itemid.endsWith("$")){
				itemid = itemid.substring(0,itemid.length()-1);
				sumitem += itemid;
				sumitem += ",";
			}
			mysalarys +=itemid;
			mysalarys += ",";
		}
		
		if(!"".equals(mysalarys)){
			mysalarys = mysalarys.substring(0,mysalarys.length()-1);
		}
		
		if(!"".equals(sumitem)){
			sumitem = sumitem.substring(0,sumitem.length()-1);
		}
		if(check())
		{
			Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
			//sop.setValue(Sys_Oth_Parameter.MYSALARYS,mysalarys);
			//sop.setSumItemValue(Sys_Oth_Parameter.MYSALARYS,sumitem);
			sop.setValue(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",fieldsetid,"title",old_title,"query_field",query_field);
			sop.setValue(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",fieldsetid,"title",old_title,"",mysalarys);
			sop.setChildText(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",fieldsetid,"title",old_title,"sumitem",sumitem);
			sop.setValue(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname",fieldsetid,"title",old_title,"title",new_title);
			sop.saveParameter();
		}else
		{
			insert();
			info="false";
		}
		
		//sop.saveParameter();
		
		info="ok";
		this.getFormHM().put("fieldsetid",fieldsetid+"`"+new_title);
		this.getFormHM().put("title",new_title);
		this.getFormHM().put("info",info);
	}
	
	/**
	 * 判断常量表中是否有SYS_OTH_PARAM字段
	 * @return
	 */
	public boolean check(){
		boolean b = false;
		String sql="select * from constant where constant='SYS_OTH_PARAM'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}
	/**
	 * 判断常量表中是否有SYS_OTH_PARAM字段
	 * @return
	 */
	public boolean insert(){
		boolean b = false;
		String sql="insert into constant(constant,type,describe ) values('SYS_OTH_PARAM','A','系统参数') ";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.insert(sql,new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}

}
