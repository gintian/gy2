package com.hjsj.hrms.transaction.sys.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchOrg_paramFieldTrans1 extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list=new ArrayList();
		ArrayList rightlist=new ArrayList();
		String setname=(String)this.getFormHM().get("tablename");
		String constant = "UNIT_MAINSET_FIELD";
		if("Y01".equalsIgnoreCase(setname)){
			constant = "PARTY_MAINSET_FIELD";
		}
		if("V01".equalsIgnoreCase(setname)){
			constant = "CORPS_MAINSET_FIELD";
		}
		String rightstr="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			RowSet rs=dao.search("select str_value from Constant where Upper(Constant)='"+constant+"'");
			if(rs.next())
				rightstr=rs.getString("str_value");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(rightstr==null||rightstr.length()<=0)
			rightstr="";		
		String[] rightss=rightstr.split(",");
		for(int m=0;m<rightss.length;m++){
			if(rightss[m].length()>0){
				FieldItem fi=DataDictionary.getFieldItem(rightss[m]);
				CommonData obj=new CommonData(fi.getItemid(),fi.getItemdesc());
				rightlist.add(obj);
			}
		}
		ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		try
		{
			if(fielditemlist!=null)
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      if("M".equalsIgnoreCase(fielditem.getItemtype()))
		    	  continue;
		      CommonData dataobj = new CommonData();
		      dataobj = new CommonData(fielditem.getItemid(),  fielditem.getItemdesc());
		      list.add(dataobj);
		    }
		    this.getFormHM().clear();		    
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
		this.getFormHM().put("fieldlist",list);
	    this.getFormHM().put("rightlist",rightlist);
	}

}
