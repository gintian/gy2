package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SelectChangeFieldSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String fieldsetid = (String) this.getFormHM().get("fieldsetid");
		String fieldItemFlag = (String) this.getFormHM().get("fielditemflag");
		String party = (String)this.getFormHM().get("party");
		party = party!=null&&party.length()>0?party:"";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList fielditemlist = new ArrayList();
		String sql="select * from fielditem where useflag=1 and (" ;
		
		String type = "";
		String [] itemType = fieldItemFlag.split(",");
		for(int i =0; i< itemType.length; i++){
			type+=" itemtype ='" +itemType[i]+ "'";
			type+=" or";
		}
		type = type.substring(0,type.length()-2);
		
		sql += type;
		
		sql+=" ) and fieldsetid ='"+fieldsetid+"'";
		
		//指定指标集中构库的非代码型指标集合
		if("NC".equalsIgnoreCase(fieldItemFlag)){
			sql ="select * from fielditem where  useflag=1 and itemtype ='A' and codesetid ='0' and fieldsetid ='"+fieldsetid+"'";
		}
		if("AC".equalsIgnoreCase(fieldItemFlag)){
			sql="select * from fielditem where  useflag=1 and itemtype ='A' and (codesetid <>'0' or codesetid<>null) and fieldsetid ='"+fieldsetid+"'";
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
		
		this.getFormHM().put("fielditemlist",fielditemlist);

	}

}
