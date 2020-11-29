package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchTableCardConstantSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		//表格方式
		ArrayList fieldSetList = new ArrayList();//主集和按月变化子集(人员)
		ArrayList fieldSetTempList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		for(int i=0; i< fieldSetTempList.size(); i++){
			FieldSet fieldSet = (FieldSet) fieldSetTempList.get(i);
			String setid = fieldSet.getFieldsetid();
			String setdesc = fieldSet.getFieldsetdesc();
			String setChangeFlag = fieldSet.getChangeflag();
			if(setChangeFlag == null){			
			}else{
				if("A01".equalsIgnoreCase(setid) || "1".equalsIgnoreCase(setChangeFlag)){
					CommonData dataobj = new CommonData(setid, setdesc);
					fieldSetList.add(dataobj);
				}
			}
		}
		ArrayList fielditemlist = new ArrayList();//全部指标
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql="select * from  fielditem where useflag=1 and fieldsetid='A01' ";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String itemid = this.frowset.getString("itemid");
				String itemdesc = this.frowset.getString("itemdesc");
				CommonData dataobj = new CommonData(itemid, itemdesc);
				fielditemlist.add(dataobj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//选中的默认值
		ArrayList selectedList = new ArrayList();
		if(check()){
			Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
			String mysalarys = sop.getValue(Sys_Oth_Parameter.MYSALARYS);
			String sumItemValue = sop.getSumItemValue(Sys_Oth_Parameter.MYSALARYS);
			String [] salary = mysalarys.split(",");
			String [] sumItem = sumItemValue.split(",");
			for(int i=0; i<salary.length; i++){
				String itemid = salary[i].trim();
				String itemdesc = this.getItemDesc(itemid).trim();
				if("".equalsIgnoreCase(itemid)){
					continue;
				}
				if(this.checkFieldItem(itemid,sumItem)){
					itemid +="$";
					itemdesc +="(∑)";
				}
				CommonData dataobj = new CommonData(itemid, itemdesc);
				selectedList.add(dataobj);
			}
		}
		
		
		
		this.getFormHM().put("fieldSetList" , fieldSetList);
		this.getFormHM().put("fielditemlist" , fielditemlist);
		this.getFormHM().put("selectedList" , selectedList);
		
	}
	
	/**
	 * 判断常量表中是否有SYS_OTH_PARAM字段
	 * @return
	 */
	public boolean check(){
		boolean b = false;
		String sql="select * from constant where constant='SYS_OTH_PARAM'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}
	/**
	 * 指标是否行合计
	 * @param itemId
	 * @param sumItemIds
	 * @return
	 */
	public boolean checkFieldItem(String itemId,String [] sumItemIds){
		boolean bflag = false;
		if(sumItemIds == null || sumItemIds.length == 0){
			return bflag;
		}
		for(int i = 0; i< sumItemIds.length; i++){
			String itemid = sumItemIds[i];
			if(itemid.equalsIgnoreCase(itemId)){
				bflag = true;
				break;
			}
		}
		return bflag;
	}
	/**
	 * 效验指标是否是数值型
	 * @param itemId
	 * @return
	 */
	public String getItemDesc(String itemId){
		String itemDesc = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql="select itemdesc from  fielditem where useflag=1 and itemid='"+itemId.trim()+"'";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				itemDesc = this.frowset.getString("itemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
	
	
	
	
	

}
