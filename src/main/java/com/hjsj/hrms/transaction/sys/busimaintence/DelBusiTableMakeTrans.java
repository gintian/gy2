package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class DelBusiTableMakeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
//		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String tabname=(String) hm.get("tabname");
		ArrayList contractedFieldList=new ArrayList();
		ArrayList	uncontractedFiledList=new ArrayList();
		String[] temp=tabname.split("/");
//		String sysvalue=temp[1];
		String zijivalue=temp[0];
		String sql1="select * from t_hr_busifield where  fieldsetid='"+zijivalue+"'  order by displayid";
		ArrayList dynabean=dao.searchDynaList(sql1);
		for(int i=0;i<dynabean.size();i++){
			DynaBean dyna=(DynaBean)dynabean.get(i);
			String fieldsetid=(String) dyna.get("fieldsetid");
			String itemid=(String) dyna.get("itemid");
			String itemdesc=(String)dyna.get("itemdesc");
			String useflag=(String)dyna.get("useflag");
			String id=fieldsetid+"/"+itemid;
			CommonData dataobj = new CommonData(id, itemdesc);
			if("1".equals(useflag)){
				contractedFieldList.add(dataobj);
			}else{
				uncontractedFiledList.add(dataobj);
			}
		}
		hm.put("uncontractedFiledList",uncontractedFiledList);
		hm.put("contractedFieldList",contractedFieldList);
	}

}
