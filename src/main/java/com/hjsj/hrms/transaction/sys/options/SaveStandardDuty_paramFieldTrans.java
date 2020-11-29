package com.hjsj.hrms.transaction.sys.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveStandardDuty_paramFieldTrans extends IBusiness {
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String[] right_fields=(String[])this.getFormHM().get("right_fields");
		String fieldstr="";
		ArrayList browseFields=new ArrayList();
		ArrayList listfield=DataDictionary.getFieldList("h01",Constant.USED_FIELD_SET);
		for(int i=0;right_fields!=null && i<right_fields.length;i++)
		{
			fieldstr+="," + right_fields[i];
			for(int j=0;listfield != null && j<listfield.size();j++){
				FieldItem fielditem=(FieldItem)listfield.get(j);
				if(fielditem.getItemid().equals( right_fields[i])){
					CommonData aCommonData=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					browseFields.add(aCommonData);
				}
				
			}
		}		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			dao.delete("delete from Constant where Upper(Constant)='SPOST_MAINSET_FIELD'", new ArrayList());
			String sql="insert into Constant(Constant,str_value)values(?,?)";
			ArrayList list=new ArrayList();
			list.add("SPOST_MAINSET_FIELD");
			list.add(fieldstr);
			dao.insert(sql, list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("browsefields",browseFields);
	}
}
