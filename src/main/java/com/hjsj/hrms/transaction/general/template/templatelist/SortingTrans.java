package com.hjsj.hrms.transaction.general.template.templatelist;


import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author xgq
 *@version 4.0
**/
public class SortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String tabid = (String)reqhm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		reqhm.remove("tabid");
		TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
		ArrayList templateSetList = (ArrayList)this.getFormHM().get("templateSetList");
		ArrayList sortList = new ArrayList();
		String fieldSetSortStr = (String)this.getFormHM().get("fieldSetSortStr");
		if(fieldSetSortStr!=null&&fieldSetSortStr.length()>0){
			String temp [] = fieldSetSortStr.split(",");
			for(int i=0;i<temp.length;i++){
				for(int j=0;j<templateSetList.size();j++){
					LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
					CommonData dataobj = null;
					if(("0".equals(abean.get("isvar"))&&(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()).equalsIgnoreCase(temp[i]))||
							("1".equals(abean.get("subflag").toString().trim())&&(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim()).equals(temp[i]))||("1".equals(abean.get("isvar"))&&abean.get("field_name").toString().trim().equalsIgnoreCase(temp[i]))){
						
					if("0".equals(abean.get("isvar"))){
						if("2".equals(abean.get("chgstate"))){
							if("1".equals(abean.get("subflag").toString().trim())){
								 dataobj = new CommonData(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim(),
										 "拟["+abean.get("hz").toString().replaceAll("`","").trim()+"]");
							}else{
								 dataobj = new CommonData(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim(),
										 "拟["+abean.get("field_hz").toString().trim()+"]");
							}
							
								}else{
									if("1".equals(abean.get("subflag").toString().trim())){
										 dataobj = new CommonData(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim(),
												 ""+abean.get("hz").toString().replaceAll("`","").trim()+"");
									}else{
										String sub_domain_id = "";
										if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){
											sub_domain_id = (String)abean.get("sub_domain_id");
										if(sub_domain_id!=null&&sub_domain_id.length()>0){
											sub_domain_id ="_"+sub_domain_id;
										}else{
											sub_domain_id="";
										}
										}
									 dataobj = new CommonData(abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim(),
											 abean.get("hz").toString().replaceAll("`","").trim());
									}
								}
							}else{
					 dataobj = new CommonData(abean.get("field_name").toString(),
						abean.get("field_hz").toString());
					}
						sortList.add(dataobj);
						break;
					}
				}
				}	
			
		}else{
		for(int i=0;i<templateSetList.size();i++){
			LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);
			CommonData dataobj = null;
			if("0".equals(abean.get("isvar"))){
				if("2".equals(abean.get("chgstate"))){
					if("1".equals(abean.get("subflag").toString().trim())){
						 dataobj = new CommonData(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim(),
								 "拟["+abean.get("hz").toString().replaceAll("`","").trim()+"]");
					}else{
						 dataobj = new CommonData(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim(),
								 "拟["+abean.get("field_hz").toString().trim()+"]");
					}
					
						}else{
							if("1".equals(abean.get("subflag").toString().trim())){
								 dataobj = new CommonData(abean.get("pageid").toString().trim()+"_"+abean.get("gridno").toString().trim(),
										 ""+abean.get("hz").toString().replaceAll("`","").trim()+"");
							}else{
								String sub_domain_id = "";
							if(abean.get("sub_domain_id")!=null&&"1".equals(abean.get("chgstate"))){
								sub_domain_id = (String)abean.get("sub_domain_id");
							if(sub_domain_id!=null&&sub_domain_id.length()>0){
								sub_domain_id ="_"+sub_domain_id;
							}else{
								sub_domain_id="";
							}
							}
								
							 dataobj = new CommonData(abean.get("field_name").toString().trim()+sub_domain_id+"_"+abean.get("chgstate").toString().trim(),
									 abean.get("hz").toString().replaceAll("`","").trim());
							}
						}
					}else{
			 dataobj = new CommonData(abean.get("field_name").toString(),
				abean.get("field_hz").toString());
			}
			sortList.add(dataobj);
			
		}
		}
		hm.put("sortlist",sortList);
		///hm.put("sortlist",sortList(tabid));
		hm.put("tabid",tabid);
	}
	
}
