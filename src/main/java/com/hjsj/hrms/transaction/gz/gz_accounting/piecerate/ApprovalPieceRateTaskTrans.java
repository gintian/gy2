package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

public class ApprovalPieceRateTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String s0100 = (String) hm.get("s0100");
			String flag = (String) hm.get("flag");
			RowSet rs = null;
			int i = 0;
			if ("approval".equals(flag)) {
				String sql = "select sp_flag from s01 where s0100 in ("+s0100+")";
				rs = dao.search(sql);
				while(rs.next()){
					String sp_flag = rs.getString("sp_flag");
					if("01".equals(sp_flag)|| "07".equals(sp_flag)&&!"03".equals(sp_flag)){
						continue;
					}else{
						i++;
					}
				}
				if(i==0){
					String sqll = "update s01 set sp_flag = '02' where s0100 in ("+s0100+")";
					dao.update(sqll);
				}else{
					throw GeneralExceptionHandler.Handle(new Exception("只能对起草和驳回的作业单报批!"));
				}
			}else if("reporting".equals(flag)){
				String sql = "select sp_flag from s01 where s0100 in ("+s0100+")";
				rs = dao.search(sql);
				while(rs.next()){
					String sp_flag = rs.getString("sp_flag");
					if("02".equals(sp_flag)){
						continue;
					}else{
						i++;
					}
				}
				if(i==0){
					String sqll = "update s01 set sp_flag = '03' where s0100 in ("+s0100+")";
					dao.update(sqll);
				}else{
					throw GeneralExceptionHandler.Handle(new Exception("只能对已报批的单位批准!"));
				}
			}else if("reject".equals(flag)){
				String sql = "select sp_flag from s01 where s0100 in ("+s0100+")";
				rs = dao.search(sql);
				while(rs.next()){
					String sp_flag = rs.getString("sp_flag");
					if("02".equals(sp_flag)){
						continue;
					}else{
						i++;
					}
				}
				if(i==0){
					String sqll = "update s01 set sp_flag = '07' where s0100 in ("+s0100+")";
					dao.update(sqll);
				}else{
					throw GeneralExceptionHandler.Handle(new Exception("只能对已报批的单位驳回!"));
				}
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}

}
