package com.hjsj.hrms.transaction.kq.register.select;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * 初始化考勤查询
 * */
public class InitSelectKqFieldsTrans  extends IBusiness {

	public void execute() throws GeneralException 
	{
		String select_flag=(String)this.getFormHM().get("select_flag");		
		String nbase=(String)this.getFormHM().get("select_pre");
		
		if(select_flag==null||select_flag.length()<=0)
			select_flag="q03";
		String duration=(String)this.getFormHM().get("duration");
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList fieldlist=newFieldList(fielditemlist,select_flag);
		this.getFormHM().put("fieldlist",fieldlist);
		this.getFormHM().put("select_flag",select_flag);
		this.getFormHM().put("select_pre",nbase);
		if(duration!=null&&duration.length()>0)
			this.getFormHM().put("duration",duration);
	}
	
	/**
	 * 返回显示的所有选择字段
	 * */
	public static ArrayList newFieldList(ArrayList fielditemlist,String select_flag)
    {
    	ArrayList list=new ArrayList();
    	for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			if(!"i9999".equals(fielditem.getItemid())&&!"state".equals(fielditem.getItemid())&&!"q03z3".equals(fielditem.getItemid())&&!"q03z5".equals(fielditem.getItemid())&&!"nbase".equals(fielditem.getItemid())&& "1".equals(fielditem.getState()))
			{
				CommonData fieldvo=null;
				if("q03z0".equalsIgnoreCase(fielditem.getItemid()))
				{
					if("q05".equalsIgnoreCase(select_flag))
					 {
						 fieldvo = new CommonData(fielditem.getItemid(), fielditem.getItemdesc()+"(格式：yyyy-MM)"); 
					 }else if("q55".equalsIgnoreCase(select_flag))
					 {
						 continue;
					 }else
					 {
						 fieldvo = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());
					 }
				}else
				{
					fieldvo = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());
				}
				list.add(fieldvo);
			}
		}    	
    	return list;
    }
}
