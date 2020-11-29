package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectFieldItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String flag=(String)hm.get("object");
		String type=(String)hm.get("type");
		String party = (String)hm.get("party");
		hm.remove("party");
		party = party!=null&&party.length()>0?party:"";
		String set = (String)hm.get("set");
		set = set!=null&&set.length()>0?set:"";
		hm.remove("set");
		/*
		System.out.println("***************************************");
		System.out.println("flag=" + flag +  "  type=" + type );
		System.out.println("*****************************************");*/
		/*String str="";
		if(type == null || type.equals("")){
			
		}else{
			String [] t = type.split("/");
			for(int i=0; i<t.length ; i++){
				
			}
		}*/
		String fristSetid = "";
		int k=0;
		ArrayList fieldsetlist = new ArrayList();
		ArrayList fieldSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.ALL_FIELD_SET);		
		for (int i = 0; i < fieldSetList.size(); i++) {			
			FieldSet fieldset = (FieldSet) fieldSetList.get(i);
			String temp = fieldset.getFieldsetid();
			String desc = fieldset.getCustomdesc();
			if(desc == null && "".equals(desc)){
				desc = fieldset.getFieldsetdesc();
			}
			if(this.checkFieldSet(temp,type)){ 
				if("ALL".equalsIgnoreCase(flag)){
					if(k==0){
						fristSetid = temp;
					}
					k++;
					CommonData dataobj = new CommonData(temp, desc);
					fieldsetlist.add(dataobj);
				}else{
					if ("A".equals(flag)) {
						if (!"A00".equals(temp)&&!"B".equals(temp.substring(0, 1))&& !"K".equals(temp.substring(0, 1))) {
							if(k==0){
								fristSetid = temp;
							}
							k++;
							CommonData dataobj = new CommonData(temp, desc);
							fieldsetlist.add(dataobj);
						}
					} else if ("B".equals(flag)) {
						if ("B".equals(temp.substring(0, 1))) {
							if(k==0){
								fristSetid = temp;
							}
							k++;
							CommonData dataobj = new CommonData(temp, desc);
							fieldsetlist.add(dataobj);
						}
					} else {
						
						if ("K".equals(temp.substring(0, 1))) {
							if(k==0){
								fristSetid = temp;
							}
							k++;
							CommonData dataobj = new CommonData(temp, desc);
							fieldsetlist.add(dataobj);
						}
					}
				}
			}	
		}
		
		//默认指标
		ArrayList fielditemlist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if(set.length()>0){//用于党团工会设置处的选择政治面貌指标定位所选处
			fristSetid=set;
		}
		String sql="";
		sql="select * from  fielditem where useflag=1 and fieldsetid='"+fristSetid+"' and ( " ;
		String temp="";
		String [] itemType = type.split(",");
		for(int i =0; i< itemType.length; i++){
			temp+=" itemtype ='" +itemType[i]+ "'";
			temp+=" or";
		}
		temp = temp.substring(0,temp.length()-2);
		
		sql += temp;
		
		sql+=" )";
		
		if("NC".equalsIgnoreCase(type)){
			sql="select * from fielditem where  useflag=1 and itemtype ='A' and codesetid ='0' and fieldsetid ='"+fristSetid+"'";
			//sql="select * from fielditem where  codesetid='0' and itemtype <>'D' and itemtype <>'N' and decimalwidth=0 and fieldsetid ='"+fristSetid+"'";
		}
		if("AC".equalsIgnoreCase(type)){
			sql="select * from fielditem where  useflag=1 and itemtype ='A' and (codesetid <>'0' or codesetid<>null) and fieldsetid ='"+fristSetid+"'";
			//sql="select * from fielditem where  codesetid='0' and itemtype <>'D' and itemtype <>'N' and decimalwidth=0 and fieldsetid ='"+fristSetid+"'";
		}
		if("party".equals(party)){
			CommonData dataobj = new CommonData("", "");
			fielditemlist.add(dataobj);
		}
		try {
			this.frowset= dao.search(sql);
			while(this.frowset.next()){
				CommonData dataobj = new CommonData(this.frowset.getString("itemid"), this.frowset.getString("itemdesc"));
				fielditemlist.add(dataobj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//System.out.println(fieldsetlist.size());
		//System.out.println(fielditemlist.size());
		
		this.getFormHM().put("setlist", fieldsetlist);
		this.getFormHM().put("itemlist",fielditemlist);	
		this.getFormHM().put("party", party);
	}

	/**
	 * 判断指标集中是否包括数值型或日期型指标
	 * @param fieldSetID
	 * @return
	 */
	public boolean checkFieldSet(String fieldSetID,String type){
		boolean b = false;
		
		String sql="select * from  fielditem where useflag=1 and fieldsetid='"+fieldSetID+"' and ( " ;
		String temp="";
		String [] itemType = type.split(",");
		for(int i =0; i< itemType.length; i++){
			temp+=" itemtype ='" +itemType[i]+ "'";
			temp+=" or";
		}
		
		temp = temp.substring(0,temp.length()-2);

		
		sql += temp;
		
		sql+=" )";
		
		if("NC".equalsIgnoreCase(type)){//字符型非代码型
			sql="select * from fielditem where useflag=1 and itemtype ='A' and codesetid ='0' and fieldsetid ='"+fieldSetID+"'";
			//sql="select * from fielditem where codesetid='0' and itemtype <>'D' and itemtype <>'N' and decimalwidth=0 and fieldsetid ='"+fieldSetID+"'";
		}
		if("AC".equalsIgnoreCase(type)){//代码型指标
			sql="select * from fielditem where  useflag=1 and itemtype ='A' and (codesetid <>'0' or codesetid<>null) and fieldsetid ='"+fieldSetID+"'";
			//sql="select * from fielditem where  codesetid='0' and itemtype <>'D' and itemtype <>'N' and decimalwidth=0 and fieldsetid ='"+fristSetid+"'";
		}
	//	System.out.println(sql);
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset= dao.search(sql);
			if(this.frowset.next()){
				b=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}
	
}
