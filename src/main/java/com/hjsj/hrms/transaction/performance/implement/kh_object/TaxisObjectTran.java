package com.hjsj.hrms.transaction.performance.implement.kh_object;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:考核对象排序功能</p>
 * <p>Description:判断object_type值,set不同得数据传到前台展现</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 13, 2008:11:37:53 AM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class TaxisObjectTran extends IBusiness
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		String object_type =(String)this.getFormHM().get("object_type");
		
		ArrayList taxisList=gettaxisList(object_type);
		
		this.getFormHM().put("taxisList", taxisList);
		this.getFormHM().put("taxisid", "");
	}

	public ArrayList gettaxisList(String object_type)
	{
		ArrayList list=new ArrayList();
		CommonData da=new CommonData();
		FieldItem fielditem = DataDictionary.getFieldItem("E0122");
		if("1".equals(object_type))
		{
			da=new CommonData();
			da.setDataName(ResourceFactory.getProperty("b0110.label"));
			da.setDataValue("B0110");
			list.add(da);
			da=new CommonData();
			da.setDataName(ResourceFactory.getProperty("label.codeitemid.un")+"/"+ResourceFactory.getProperty("label.codeitemid.um"));
			da.setDataValue("E0122");
			list.add(da);
			
		}else if("2".equals(object_type))
		{
			da=new CommonData();
			da.setDataName(ResourceFactory.getProperty("b0110.label"));
			da.setDataValue("B0110");
			list.add(da);
			da=new CommonData();
			da.setDataName(fielditem.getItemdesc());
			da.setDataValue("E0122");
			list.add(da);	
			da=new CommonData();
			da.setDataName(ResourceFactory.getProperty("e01a1.label"));
			da.setDataValue("E01A1");
			list.add(da);	
			da=new CommonData();
			da.setDataName(ResourceFactory.getProperty("label.commend.p_name"));
			da.setDataValue("A0101");
			list.add(da);	
			
		}else if("3".equals(object_type))
		{
			da=new CommonData();
			da.setDataName(ResourceFactory.getProperty("b0110.label"));
			da.setDataValue("B0110");
			list.add(da);
			da=new CommonData();
			da.setDataName(fielditem.getItemdesc());
			da.setDataValue("E0122");
			list.add(da);
			
		}else if("4".equals(object_type))
		{
			da=new CommonData();
			da.setDataName(ResourceFactory.getProperty("b0110.label"));
			da.setDataValue("B0110");
			list.add(da);
			da=new CommonData();
			da.setDataName(fielditem.getItemdesc());
			da.setDataValue("E0122");
			list.add(da);	
			
		}
		da=new CommonData();
		da.setDataName(ResourceFactory.getProperty("performance.implement.objecttype"));
		da.setDataValue("body_id");
		list.add(da);
		return list;

	}	
	
}
