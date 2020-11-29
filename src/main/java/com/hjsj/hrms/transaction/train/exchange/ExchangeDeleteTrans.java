package com.hjsj.hrms.transaction.train.exchange;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class ExchangeDeleteTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sels = (String)this.getFormHM().get("sel");
		if(sels!=null&&sels.length()>0){
			checkIsDel(dao, sels);
			try {
				//ContentDAO dao = new ContentDAO(this.getFrameconn());
				dao.delete("delete r57 where r5701 in("+sels+")", new ArrayList());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//检查状态是否为已发布状态
	public boolean checkIsDel(ContentDAO dao,String sels) throws GeneralException{
		boolean tmpFlag = true;
		StringBuffer tmpstring = new StringBuffer();
		String sql = "select r5713,r5703 from r57 where r5701 in ("+sels+")";
		try {
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String r5713 = this.frowset.getString("r5713");
				if("04".equals(r5713)){
					tmpstring.append("\n["+this.frowset.getString("r5703")+"]");
					tmpstring.append("为已发布状态，不能删除，只能删除起草、暂停状态的记录!\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(tmpstring.length()>0)
			throw GeneralExceptionHandler.Handle(new Exception(tmpstring.toString()));
		return tmpFlag;
	}
	
}
