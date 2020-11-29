package com.hjsj.hrms.module.system.personalsoduku.boxreport.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
* <p>Title:SaveBoxReportTitleTrans </p>
* <p>Description: 保存九宫格每个格的标题描述</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 21, 2015 5:56:17 PM
 */
public class SaveBoxReportTitleTrans extends IBusiness{
	
	@Override
    public void execute() throws GeneralException {
		
		String cassetteid = (String)this.getFormHM().get("cassetteid");
		
		String xy = (String)this.getFormHM().get("xy");
		
		String desc = (String)this.getFormHM().get("desc");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			int index  = xy.indexOf("_");
			String x = xy.substring(0,index);
			String y = xy.substring(index+1,xy.length());
			ArrayList values = new ArrayList();
			int max = 0;
			String existsql = "select * from t_sys_box_cell where h_field_value='"+x+"' and v_field_value='"+y+"' and  box_id='"+cassetteid+"'";
			this.frowset=dao.search(existsql);
			if(this.frowset.next()){
				int cell_id = this.frowset.getInt("cell_id");
				RecordVo resultVo = new RecordVo("t_sys_box_cell");
				resultVo.setInt("cell_id", cell_id);
				resultVo.setString("description", desc);
				dao.updateValueObject(resultVo);
			}else{
				String sql = "select max(cell_id) as max from t_sys_box_cell";
				this.frowset = dao.search(sql);
				while(this.frowset.next()){
					max = this.frowset.getInt("max");
				}
				RecordVo resultVo = new RecordVo("t_sys_box_cell");
				resultVo.setInt("cell_id", max+1);
				resultVo.setInt("box_id", Integer.valueOf(cassetteid));
				resultVo.setString("h_field_value", x);
				resultVo.setString("v_field_value", y);
				resultVo.setString("description", desc);
				dao.addValueObject(resultVo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

}
