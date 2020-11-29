package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
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
		String salaryid = (String)reqhm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		reqhm.remove("salaryid");
		//xcs add @2013-8-5
		String fieldsetid = (String) reqhm.get("fieldsetid");
		fieldsetid=fieldsetid!=null&&fieldsetid.trim().length()>0?fieldsetid:"";
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		ArrayList list = new ArrayList();
		ArrayList dylist = null;
		
		try {
			if(!"-1".equals(salaryid)){
				if("-2".equals(salaryid)){//从数据采集进入 不一样的查询
					String sqlstr = "select itemid,itemdesc from fielditem where fieldsetid='"+fieldsetid+"' and itemid not in('"+fieldsetid.toUpperCase()+"Z0','"+fieldsetid.toUpperCase()+"Z1','"+fieldsetid.toLowerCase()+"z0','"+fieldsetid.toLowerCase()+"z1') and useflag='1'";
					dylist = dao.searchDynaList(sqlstr);
					for(Iterator it=dylist.iterator();it.hasNext();){
						DynaBean dynabean=(DynaBean)it.next();
						String itemid = dynabean.get("itemid").toString();
						String itemdesc = dynabean.get("itemdesc").toString();
						if(!"a0100".equalsIgnoreCase(itemid)&&!"a0000".equalsIgnoreCase(itemid)&&!"a00z2".equalsIgnoreCase(itemid)&&!"a00z3".equalsIgnoreCase(itemid)){
							CommonData dataobj = new CommonData(itemid+":"+itemdesc,itemdesc);
							list.add(dataobj);
						}
					}
					hm.put("fieldsetid",fieldsetid);
				}else{
					String sqlstr = "select itemid,itemdesc from salaryset where salaryid="+salaryid+"  order by sortid";
					dylist = dao.searchDynaList(sqlstr);
					for(Iterator it=dylist.iterator();it.hasNext();){
						DynaBean dynabean=(DynaBean)it.next();
						String itemid = dynabean.get("itemid").toString();
						String itemdesc = dynabean.get("itemdesc").toString();
						if(!"a0100".equalsIgnoreCase(itemid)&&!"a0000".equalsIgnoreCase(itemid)&&!"a00z2".equalsIgnoreCase(itemid)&&!"a00z3".equalsIgnoreCase(itemid)){
							CommonData dataobj = new CommonData(itemid+":"+itemdesc,itemdesc);
							list.add(dataobj);
						}
					}
				}
				
				if(dylist.size()<1){
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
			} else{
				//项目：取自薪资总额子集中的指标和薪资总额临时变量。
				String salaryAmountSet = "";//薪资总额子集
				String shenpiField = "";//审批状态指标
				GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
				HashMap map = bo.getValuesMap();
				if (map != null){
					salaryAmountSet = (String) map.get("setid")==null?"":(String) map.get("setid");
					shenpiField = (String) map.get("sp_flag")==null?"":(String) map.get("sp_flag");
				}
					
				ArrayList fieldList = DataDictionary.getFieldList(salaryAmountSet, Constant.USED_FIELD_SET);
				int n = fieldList.size();
				for(int i=0;i<n;i++){
					FieldItem item = (FieldItem)fieldList.get(i);
					String itemid = item.getItemid();
					if("0".equals(this.userView.analyseFieldPriv(itemid)))
						continue;
					if(itemid==null || "".equals(itemid) || itemid.equalsIgnoreCase(salaryAmountSet+"z0")
							|| itemid.equalsIgnoreCase(salaryAmountSet+"z1") || itemid.equalsIgnoreCase(shenpiField))//如果itemid为空、年月标识、次数、审批状态
						continue;
					String itemdesc = item.getItemdesc();
					CommonData dataobj = new CommonData(itemid+":"+itemdesc,itemid+":"+itemdesc);
					list.add(dataobj);
				}
				if(list.size()<1){
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
			}
			
		} catch(GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hm.put("salaryid",salaryid);
		hm.put("formulaitemid","");
		hm.put("formulaitemlist",list);
	}

}
