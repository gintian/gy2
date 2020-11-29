package com.hjsj.hrms.module.system.personalsoduku.boxreport.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * 
* <p>Title:DelBoxReportDataTrans </p>
* <p>Description: 删除盒式报表</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 12, 2015 1:11:11 PM
 */
public class DelBoxReportDataTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		String idlist  = (String)this.getFormHM().get("idlist");
		String[] idarr = idlist.split(",");
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rst = null;
		try {
			for(int i=0;i<idarr.length;i++){
				String cassette_id = idarr[i];
				String sql = "select status from t_sys_box_report where box_id='"+cassette_id+"'";
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					String status = this.frowset.getString("status");
					if("1".equals(status)){//运行状态
						String sb = "select * from t_sys_box_cell where box_id = '"+cassette_id+"'";
						rst = dao.search(sb);
						if(rst.next()){
							List values = new ArrayList();
							String delsqlcell = "delete from t_sys_box_cell where box_id = '"+cassette_id+"'";
							dao.delete(delsqlcell, values);
						}
					}
				}
				List values = new ArrayList();
				String delsql = "delete from t_sys_box_report where box_id='"+cassette_id+"'";
				dao.delete(delsql, values);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
