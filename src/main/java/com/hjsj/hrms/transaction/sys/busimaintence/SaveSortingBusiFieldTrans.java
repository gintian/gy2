package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * 
 * <p>Title:业务字典(指标排序保存)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 10, 2008:5:49:41 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SaveSortingBusiFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sort = (String)hm.get("displayid");
		
		sort=sort!=null&&sort.trim().length()>0?sort:"";
		String[] fitem = sort.split(",");;
		
		String fsetid = (String)hm.get("fsetid");
		fsetid=fsetid!=null&&fsetid.trim().length()>0?fsetid:"";
		if(fsetid.length()>0){
			int length = fitem.length;
			for(int i=0;i<length;i++){
				String sqlstr = "update t_hr_busifield set displayid="+(i+1)+" where itemid='"+fitem[i]+"'";
				try{
					dao.update(sqlstr);
				}catch (SQLException e){
					e.printStackTrace();
				}
			}
	    }
		hm.put("info","ok");
	}

}
