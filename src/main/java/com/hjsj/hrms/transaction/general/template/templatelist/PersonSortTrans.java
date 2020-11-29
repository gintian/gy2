package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class PersonSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
		String flag = (String)reqhm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		reqhm.remove("flag");
		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		String tabid = (String)reqhm.get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		reqhm.remove("tabid");
		String table_name = (String)this.getFormHM().get("table_name");
		ArrayList itemlist = new ArrayList();
		ArrayList templateSetList = (ArrayList)this.getFormHM().get("templateSetList");
		String fieldSetSortStr = (String)this.getFormHM().get("fieldSetSortStr");
		String sortitem = (String)this.getFormHM().get("sortitem");
		sortitem=SafeCode.keyWord_reback(sortitem);    //将form中的值由全角转换为半角，liuzy 20150803
		this.getFormHM().put("sortitem", sortitem);
		if(fieldSetSortStr!=null&&fieldSetSortStr.length()>0){

			String temp [] = fieldSetSortStr.split(",");
			for(int i=0;i<temp.length;i++){
				for(int j=0;j<templateSetList.size();j++){
					LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
					if(("0".equals(abean.get("isvar"))&&(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()).equalsIgnoreCase(temp[i]))||
							("1".equals(abean.get("isvar"))&&abean.get("field_name").toString().trim().equalsIgnoreCase(temp[i]))){
						//去掉子集项
						if("1".equals(abean.get("subflag")))
							break;
						if("M".equals(abean.get("field_type")))//去掉备注型指标
							break;
					CommonData dataobj = null;
					if("0".equals(abean.get("isvar"))){
						if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim())!=-1)
							break;
						if("2".equals(abean.get("chgstate"))){
							 dataobj = new CommonData(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim(),
									 "拟["+abean.get("field_hz").toString().trim()+"]");
								}else{
									 dataobj = new CommonData(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim(),
											 abean.get("field_hz").toString().trim());
								}
							}else{
								if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim())!=-1)
									break;
					 dataobj = new CommonData(abean.get("field_name").toString(),
						abean.get("field_hz").toString());
					}
					if("1".equals(abean.get("isvar"))){
						if(!dbWizard.isExistField(table_name, abean.get("field_name")==null?"":abean.get("field_name").toString()))
							break;
					}
					itemlist.add(dataobj);
					break;
			}
			}
			}		
		}else{
		for(int i=0;i<templateSetList.size();i++){
			LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);
			CommonData dataobj = null;
			//去掉子集项
			if("1".equals(abean.get("subflag")))
				continue;
			if("M".equals(abean.get("field_type")))//去掉备注型指标
				continue;
			if("0".equals(abean.get("isvar"))){
				if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim())!=-1)
					continue;
				if("2".equals(abean.get("chgstate"))){
					 dataobj = new CommonData(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim(),
							 "拟["+abean.get("field_hz").toString().trim()+"]");
						}else{
							 dataobj = new CommonData(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim(),
									 abean.get("field_hz").toString().trim());
						}
					}else{
						if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim())!=-1)
							continue;
			 dataobj = new CommonData(abean.get("field_name").toString(),
				abean.get("field_hz").toString());
			}
			if("1".equals(abean.get("isvar"))){
				if(!dbWizard.isExistField(table_name, abean.get("field_name")==null?"":abean.get("field_name").toString()))
					continue;
			}
			itemlist.add(dataobj);
		}
		}
/**安全包改造,刘红梅要求去掉a0100 b0110 E01A1 xcs 2014-11-3**/	
//		String infor_type = (String)this.getFormHM().get("infor_type");
////		if(infor_type.equals("1")){
////			if(sortitem!=null&&sortitem.indexOf("a0100")!=-1){
////			}else{
////			CommonData dataobj = new CommonData("a0100","人员编号");
////			itemlist.add(0, dataobj);
////			}
////		}else if(infor_type.equals("2")){
////			CommonData dataobj = new CommonData("b0110","机构编号");
////			itemlist.add(0, dataobj);
////		}else if(infor_type.equals("3")){
////			CommonData dataobj = new CommonData("e01a1","职位编号");
////			itemlist.add(0, dataobj);
////		}
		this.getFormHM().put("itemid","");
		this.getFormHM().put("itemlist",itemlist);
	
	}

}
