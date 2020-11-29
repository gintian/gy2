package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SortFieldList.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 17, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SortFieldList extends IBusiness {
	
	public  void execute()throws GeneralException
	{
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String setname = (String)hm.get("setname");
			hm.remove("setname");
			
			String flag = (String)hm.get("flag");
			flag=flag!=null&&flag.trim().length()>0?flag:"0";
			hm.remove("flag");
			
			ArrayList templist = new ArrayList();
			if("1".equals(flag))
				templist=getFieldobjListB01(setname);	
			else
				templist=getFieldobjList(setname);
			this.getFormHM().put("sortfieldlist", templist );
			this.getFormHM().put("setname", setname );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldobjList(String setname)
	{
		ArrayList list=getFieldList(setname);
		ArrayList fieldlist=new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			Field item=(Field)list.get(i);
			String itemid=item.getName();
			if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
				continue;	
			
			CommonData fieldcd = new CommonData(itemid,item.getLabel());
			fieldlist.add(fieldcd);
		}//i loop end.
		return fieldlist;
	}
	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldobjListB01(String setname)
	{
		PosparameXML pos = new PosparameXML(this.frameconn);
		String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag"); 
		ArrayList list=getFieldList(setname);
		ArrayList fieldlist=new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			Field item=(Field)list.get(i);
			String itemid=item.getName();
			if("0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid)))
				continue;	
			if(itemid.equalsIgnoreCase(sp_flag))
				continue;
			CommonData fieldcd = new CommonData(itemid,item.getLabel());
			fieldlist.add(fieldcd);
		}//i loop end.
		return fieldlist;
	}
	private ArrayList getFieldList(String setname){		
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
		ArrayList list = gzbo.itemList1(fieldset);
		ArrayList fieldlist=new ArrayList();
		for(int i=0;i<list.size();i++){
			Field field=(Field)list.get(i);
			String itemid=field.getName();
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
			field.setSortable(true);
			fieldlist.add(field);
		}
		return fieldlist;
	
	}

}
