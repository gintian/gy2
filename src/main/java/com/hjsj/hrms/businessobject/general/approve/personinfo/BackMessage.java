package com.hjsj.hrms.businessobject.general.approve.personinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;

public class BackMessage {
	public ArrayList getMessage(UserView uv,String a0100,String pdbflag,ContentDAO dao) throws GeneralException{
		ArrayList mylist=new ArrayList();
		ArrayList setlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);
		for(int i=0;i<setlist.size();i++){
			FieldSet fs=(FieldSet) setlist.get(i);
			String fieldsetid=fs.getFieldsetid();
			String sql="select * from "+pdbflag+fieldsetid+" where a0100='"+a0100+"'";
			ArrayList dynabean=dao.searchDynaList(sql);
			if(dynabean.size()>0){
				mylist.add(fs.getFieldsetdesc());
			}
		}
		return mylist;
		
	}
}
