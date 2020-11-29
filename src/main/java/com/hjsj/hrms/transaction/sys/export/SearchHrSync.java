package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SearchHrSync.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 4, 2009:11:44:52 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SearchHrSync extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hmap = (HashMap)this.getFormHM().get("requestPamaHM");
		String type = (String)hmap.get("type");
		ArrayList list = new ArrayList();
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		/*HashMap map = new HashMap();
		if(type.equalsIgnoreCase("A"))
			map = hsb.getAppList(HrSyncBo.A);
		else if(type.equalsIgnoreCase("b"))
			map = hsb.getAppList(HrSyncBo.B);
		Set set = map.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			LazyDynaBean bean = new LazyDynaBean();
			String key = it.next().toString();
			String tofield = map.get(key).toString();
			bean.set("field",key);
			FieldItem item=DataDictionary.getFieldItem(key);
			if(item!=null)
				bean.set("fieldname",item.getItemdesc());
			else
				bean.set("fieldname","");
			bean.set("tofield",tofield);
			list.add(bean);
		}*/
		ArrayList fieldlist=new ArrayList();
		if("A".equalsIgnoreCase(type))
			fieldlist = hsb.getAppFieldList(HrSyncBo.A);
		else if("b".equalsIgnoreCase(type))
			fieldlist = hsb.getAppFieldList(HrSyncBo.B);
		else if("k".equalsIgnoreCase(type))
			fieldlist = hsb.getAppFieldList(HrSyncBo.K);
		if(fieldlist!=null&&fieldlist.size()>0)
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				LazyDynaBean bean_1=(LazyDynaBean)fieldlist.get(i);
				String key=(String)bean_1.get("name");
				String tofield=(String)bean_1.get("text");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("field",key);
				FieldItem item=DataDictionary.getFieldItem(key);
				if(item!=null)
					bean.set("fieldname",item.getItemdesc());
				else
					bean.set("fieldname","");
				bean.set("tofield",tofield);
				list.add(bean);
			}
		}
		this.getFormHM().put("setList",list);
		this.getFormHM().put("type",type);
		//System.out.println(type);
	}

}
