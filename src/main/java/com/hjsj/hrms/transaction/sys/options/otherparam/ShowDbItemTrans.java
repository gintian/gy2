
package com.hjsj.hrms.transaction.sys.options.otherparam;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.options.otherparam.Sys_OTH_PARAMSqlStr;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.*;

public class ShowDbItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		
//		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		Sys_OTH_PARAMSqlStr sop=new Sys_OTH_PARAMSqlStr();
		try {
			OtherParam op=new OtherParam(this.getFrameconn());
			Map vmap=op.serachAtrr("/param/base_fields");
			String valid="false";
			if(vmap!=null){
				valid=(String) vmap.get("valid");
			}
			hm.put("dbvalid",valid);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		String[] sqlstr=sop.getDbnameSQL();
		hm.put("sql",sqlstr[0]);
		hm.put("where",sqlstr[1]);
		hm.put("column",sqlstr[2]);
		hm.put("orderby",sqlstr[3]);
		hm.put("dbMap",this.getDbItem());
	}
	public Map getDbItem(){
		Map myMap=new HashMap();
		boolean isCorrect=false;
		try {
			OtherParam op=
				op=new OtherParam(this.getFrameconn());
			
			myMap=op.getBaseFieldMap();
			for(Iterator it=myMap.keySet().iterator();it.hasNext();){
				String dbname=(String) it.next();
				Map dbMap=(Map) myMap.get(dbname);
				String table=(String) dbMap.get("table");
//				bug
//				if(table!=null&&table.length()>0){
				String field=(String) dbMap.get("field");				
				String[] tablestr=table.split(",");
				String[] fieldstr=field.split(",");
				List myList=new ArrayList();
				for(int i=0;i<tablestr.length;i++){
					String tid=tablestr[i];
					FieldSet fs=DataDictionary.getFieldSetVo(tid);
					if(fs!=null){
					String zhujiname=fs.getCustomdesc();				

					myList.add(zhujiname);
					for(int j=0;j<fieldstr.length;j++){
						String fi=fieldstr[j];
						FieldItem fis=DataDictionary.getFieldItem(fi);
						if(fis!=null&&(fs.getFieldsetid()).equalsIgnoreCase(fis.getFieldsetid())){
							String finame=fis.getItemdesc();
							myList.add("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+finame);
							isCorrect=true;
						}
					}
					}
			}
				
			dbMap.put("value",myList);
			dbMap.remove("table");
			dbMap.remove("field");
//				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if(isCorrect)
			this.getFormHM().put("view_check","true");
		else
			this.getFormHM().put("view_check","false");
		return myMap;
	}

}