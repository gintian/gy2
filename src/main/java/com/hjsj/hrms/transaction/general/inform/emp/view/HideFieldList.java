package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:HideFieldList</p> 
 *<p>Description:显示＆隐藏指标</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class HideFieldList extends IBusiness {
	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldList(String setname)
	{
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.frameconn);
		String orgFieldIDs="";
		String contentType="";
		try {
			HashMap map=parameterXMLBo.getAttributeValues();
			/** 过滤掉网址,和单位介绍*/
			if(map.get("org_brief")!=null&&((String)map.get("org_brief")).trim().length()>0){
				String temp=(String)map.get("org_brief");
				String[] temps=temp.split(",");
				orgFieldIDs=temps[0];
				contentType=temps[1];
			}
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//ArrayList list=DataDictionary.getFieldList(setname, Constant.USED_FIELD_SET);
		ArrayList list = gzbo.itemList1(fieldset);
		ArrayList fieldlist=new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			Field item=(Field)list.get(i);
			String itemid=item.getName();
//			if(itemid.equalsIgnoreCase(orgFieldIDs))
//				continue;
//			if(itemid.equalsIgnoreCase(contentType))
//				continue;
			if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
				continue;
			if("a0100".equalsIgnoreCase(itemid) || "a0000".equalsIgnoreCase(itemid))
				continue;
			if(!fieldset.isMainset()){
				if("B0110".equalsIgnoreCase(itemid))
					continue;
				if("E0122".equalsIgnoreCase(itemid))
					continue;
				if("E01A1".equalsIgnoreCase(itemid))
					continue;
				if("A0101".equalsIgnoreCase(itemid))
					continue;
			}
			item.setSortable(true);
			fieldlist.add(item);
		}//i loop end.
		return fieldlist;
	}
	
	public  void execute()throws GeneralException
	{
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			ArrayList templist = new ArrayList();
			String setname = (String)hm.get("setname");
			hm.remove("setname");
			templist = getFieldList(setname);	
			this.getFormHM().put("hidefieldlist", templist );
			this.getFormHM().put("setname",setname);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
