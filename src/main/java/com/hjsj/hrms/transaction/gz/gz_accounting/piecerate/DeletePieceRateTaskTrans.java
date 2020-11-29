package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

public class DeletePieceRateTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		int i = 0;
		String s0100 = (String) hm.get("s0100");
		String sql = "select sp_flag from s01 where s0100 in ("+s0100+")";
		RowSet rs = dao.search(sql);
		while(rs.next()){
			String sp_flag = rs.getString("sp_flag");
			if("01".equals(sp_flag)|| "07".equals(sp_flag)){
				continue;
			}else{
				i++;
			}
		}
		if(i==0){
			String strSql ="";
			strSql = "delete from s01 where s0100 in ("+s0100+")";
			dao.update(strSql);
			strSql = "delete from S04 where s0100 in ("+s0100+")";
			dao.update(strSql);
			strSql = "delete from S05 where s0100 in ("+s0100+")";
			dao.update(strSql);
		}else{
			throw GeneralExceptionHandler.Handle(new Exception("只能删除起草和驳回的作业单!"));
		}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}

}
