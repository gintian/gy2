package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * 
 * <p>Title:保存</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 20, 2008:1:39:30 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SaveSortingIndexTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sort = (String)hm.get("displayid");
		
		sort=sort!=null&&sort.trim().length()>0?sort:"";
		String[] fitem = sort.split(",");;
		
		String setid = (String)hm.get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		if(setid.length()>0){
			int length = fitem.length;
			for(int i=0;i<length;i++){
				String sqlstr="";
				if(setid.length()==1){
					sqlstr = "update fieldset set displayorder="+(i+1)+" where fieldsetid='"+fitem[i]+"'";
				}else{
					sqlstr = "update fielditem set displayid="+(i+1)+" where itemid='"+fitem[i]+"'";
				}
				try{
					dao.update(sqlstr);
					if(setid.length()==1){
						FieldSet fset = DataDictionary.getFieldSetVo(fitem[i]);
						if(fset!=null)
							fset.setDisplayorder(i+1);
					}else{
						FieldItem fi = DataDictionary.getFieldItem(fitem[i]);
						if(fi!=null)
							fi.setDisplayid(i+1);
					}
				}catch (SQLException e){
					e.printStackTrace();
				}
			}
	    }
		hm.put("info","ok");
	}

}
