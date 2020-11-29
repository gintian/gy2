package com.hjsj.hrms.transaction.general.query.common;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchAllTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sql = (String)this.getFormHM().get("sql");
		sql=sql!=null?sql:"";
		sql=SafeCode.decode(sql);

		String infor = (String)this.getFormHM().get("infor");
		infor=infor!=null?infor:"1";
		String row_num=(String)this.getFormHM().get("row_num");
		row_num=row_num!=null?row_num:"";
		ContentDAO dao  = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		sql=PubFunc.keyWord_reback(sql);
		try {
			this.frowset = dao.search(sql);
			int count=0;
			while(this.frowset.next()){
				if("1".equals(infor)){
					list.add(this.frowset.getString("dbase")+this.frowset.getString("A0100"));
				}else if("2".equals(infor)){
					list.add(this.frowset.getString("B0110"));
				}else if("9".equals(infor)){  // 基准岗位
					list.add(this.frowset.getString("H0100"));
				}else{
					list.add(this.frowset.getString("E01A1"));
				}
				count++;
				if(row_num!=null&&row_num.length()>0)
				{
                   if(count>Integer.parseInt(row_num))
                	   break;
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("listvalue",list);
	}

}
