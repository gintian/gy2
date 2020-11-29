package com.hjsj.hrms.transaction.gz.gz_amount.tax;


import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 *<p>Title:</p> 
 *<p>Description</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String salaryid = (String)reqhm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		reqhm.remove("salaryid");

		hm.put("sortlist",sortList(salaryid));
		hm.put("salaryid",salaryid);
	}
	/**
	 * 根据条件获取指标List
	 * @param where
	 * @param orderby
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList sortList(String salaryid){
		ArrayList list = new ArrayList();
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  			
		
		if(salaryid!=null&&salaryid.trim().length()>0){
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select itemid,itemdesc from  salaryset where salaryid=");
			sqlstr.append(salaryid);
			sqlstr.append(" order by sortid");
			ArrayList dylist = null;
			try {
				dylist = dao.searchDynaList(sqlstr.toString());
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					
					if("a01z0".equalsIgnoreCase(dynabean.get("itemid").toString())&&(a01z0Flag==null|| "0".equals(a01z0Flag)))
						continue;
					
					if("A0100".equalsIgnoreCase(dynabean.get("itemid").toString()))
						continue;
					if("A0000".equalsIgnoreCase(dynabean.get("itemid").toString()))
						continue;
					FieldItem fielditem = DataDictionary.getFieldItem(dynabean.get("itemid").toString()); 
					
					if("NBASE".equalsIgnoreCase(dynabean.get("itemid").toString()))
					{
						String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
						if(!this.userView.isSuper_admin()&&manager.length()>0&&!this.userView.getUserName().equalsIgnoreCase(manager))
						{
							continue;
						}
					}
					else
					{
						if(fielditem!=null){
							if(this.userView.analyseFieldPriv(dynabean.get("itemid").toString())==null)
								continue;
							if("0".equals(this.userView.analyseFieldPriv(dynabean.get("itemid").toString())))
								continue;
							if("".equals(this.userView.analyseFieldPriv(dynabean.get("itemid").toString())))
								continue;
						}
					}
					CommonData dataobj = new CommonData(dynabean.get("itemid").toString(),
							dynabean.get("itemdesc").toString());
					list.add(dataobj);
				}
				if(dylist.size()<1){
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
				
			} catch(GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
}
