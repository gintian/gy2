package com.hjsj.hrms.transaction.gz.premium.param;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class AddFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String setid = (String)reqhm.get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		reqhm.remove("setid");
		String fmode =(String)hm.get("fmode");
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		ArrayList list = new ArrayList();
		String sqlstr = "select itemname,hzname from bonusformula where setid='"+setid+"'";
		ArrayList dylist = new ArrayList();;
		ArrayList fieldList2 = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
	    	for (int j = 0; j < fieldList2.size(); j++)
			{
			    FieldItem fieldItem = (FieldItem) fieldList2.get(j);
			    String itemid = fieldItem.getItemid();
			   String itemdesc = fieldItem.getItemdesc();
			  String type = fieldItem.getItemtype();
			    if(!"B00".equalsIgnoreCase(itemid)&&!"B01".equalsIgnoreCase(itemid)){
			    	if(!"1".equals(fmode)&& "A".equalsIgnoreCase(type))
			    		continue;
			    	if("0".equals(fmode)){
			    		if("次数".equals(itemdesc)|| "年月标识".equals(itemdesc))
			    		continue;
			    	}
					CommonData dataobj = new CommonData(itemid+":"+itemdesc,itemdesc);
					list.add(dataobj);
					dylist.add(dataobj);
				}
			}
	
			if(dylist.size()<1){
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
			}
	
		
		hm.put("setid",setid);
		hm.put("formulaitemid","");
		hm.put("formulaitemlist",list);
	}

}
