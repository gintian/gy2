package com.hjsj.hrms.transaction.browse.history;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:SelectExportFieldsTrans.java</p>
 * <p>Description>:SelectExportFieldsTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 23, 2010 11:59:41 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class SelectExportFieldsTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		List leftlist=new ArrayList();
		//List rightlist=new ArrayList();
		ArrayList browsefields = (ArrayList)this.getFormHM().get("browsefields");
		for (int i = 0; i < browsefields.size(); i++) {
			if(i==0){
				CommonData obj=new CommonData("create_date","生成日期");
				leftlist.add(obj);
			}
			FieldItem fielditem = (FieldItem)browsefields.get(i);
			CommonData obj=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
			leftlist.add(obj);
		}
//		String sql="select * from hr_emp_hisdata where 1=2";
//		try {
//			this.frowset = dao.search(sql.toString());
//			ResultSetMetaData rsmd = this.frowset.getMetaData();
//			int size = rsmd.getColumnCount();
//			StringBuffer columnstr = new StringBuffer();
//			for (int i = 1; i <= size; i++) {
//				String field = rsmd.getColumnName(i);
//				if(field.equalsIgnoreCase("id")){
//					CommonData obj=new CommonData("create_date","生成日期");
//					leftlist.add(obj);
//					continue;
//				}
//				if(field.equalsIgnoreCase("A0000"))
//					continue;
//				if(field.equalsIgnoreCase("A0100"))
//					continue;
//				if(field!=null&&field.length()>0){
//					FieldItem fi=DataDictionary.getFieldItem(field);
//					CommonData obj=new CommonData(fi.getItemid(),fi.getItemdesc());
//					leftlist.add(obj);
//				}
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.getFormHM().put("leftlist",leftlist);
		this.getFormHM().put("rightlist",new ArrayList());
	}

}
