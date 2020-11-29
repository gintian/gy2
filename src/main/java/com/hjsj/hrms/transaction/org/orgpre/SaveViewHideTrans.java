package com.hjsj.hrms.transaction.org.orgpre;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 机构编制指标显示隐藏
 * @author xujian
 *
 */
public class SaveViewHideTrans extends IBusiness {

	public void execute() throws GeneralException {

		try{
			String setid = ((String)this.getFormHM().get("setid")).toUpperCase();
			String items = (String)this.getFormHM().get("items");
			String[] itemids = items.split(",");
			if(itemids.length<1){
				return;
			}
			ArrayList valuelist = new ArrayList();
			for(int i=0;i<itemids.length;i++){
				String[] tempstr=itemids[i].split(":");
				if(tempstr.length!=2)
					continue;
				FieldItem fi = DataDictionary.getFieldItem(tempstr[0]);
				if(fi!=null)
					fi.setDisplaywidth(Integer.parseInt(tempstr[1]));
				ArrayList list = new ArrayList();
				list.add(tempstr[1]);
				list.add(setid);
				list.add(tempstr[0].toUpperCase());
				valuelist.add(list);
			}
			String sql = "update fielditem set displaywidth=? where upper(fieldsetid)=? and upper(itemid)=?";
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.batchUpdate(sql, valuelist);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
