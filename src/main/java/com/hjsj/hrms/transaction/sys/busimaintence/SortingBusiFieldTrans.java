package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 
 * <p>Title:业务字典(指标排序)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 10, 2008:5:13:07 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SortingBusiFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String fieldsetid=(String)reqhm.get("fieldsetid");
		fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
		reqhm.remove("fieldsetid");
		if(fieldsetid.length()>0){
			hm.put("sortlist", this.sortList(fieldsetid));
		}
		hm.put("fsetid", fieldsetid);
	}
	/*
	 * 根据fieldsetid字段取出list
	 */
	public ArrayList sortList(String fieldsetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select itemid,itemdesc from t_hr_busifield where fieldsetid = '"+fieldsetid+"'or fieldsetid is null and useflag='1' order by displayid";
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sql);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean = (DynaBean)it.next();
				CommonData dataobj = new CommonData(dynabean.get("itemid").toString(),
						dynabean.get("itemdesc").toString());
				list.add(dataobj);
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}
