package com.hjsj.hrms.transaction.sys.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchOrg_paramFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list=new ArrayList();
		
		String setname=(String)this.getFormHM().get("tablename");
		String constant = "UNIT_MAINSET_FIELD";		
		String rightstr="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList rightlist=new ArrayList();
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
				// 还要满足是构库指才能在已选指标里出现 27009  wangb1 20170502  and bug 36815 20180423
				if(null != fi && !"0".equalsIgnoreCase(fi.getUseflag())){
					CommonData obj=new CommonData(fi.getItemid(),fi.getItemdesc());
					rightlist.add(obj);
				}
			}
		}
		this.getFormHM().put("rightlist",rightlist);
		/*ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		try
		{
			if(fielditemlist!=null)
		    for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if(this.userView.analyseFieldPriv(fielditem.getItemid()).equals("0"))
		        continue;
		      if(fielditem.getItemtype().equalsIgnoreCase("M"))
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
	   */
	}

}
