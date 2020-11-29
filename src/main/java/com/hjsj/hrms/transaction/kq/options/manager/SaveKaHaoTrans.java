package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
/**
 * 
 * <p>Title:读卡时卡号为空；读取工号</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 6, 2009:1:16:46 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SaveKaHaoTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String a0100s=(String)this.getFormHM().get("a0100s");
		String select_pres=(String)this.getFormHM().get("select_pres");
		String code="";
 		if(this.userView.isSuper_admin())
 		{
 			code="UN";
 		}else
 		{
 			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
 			code=managePrivCode.getPrivOrgId();  		 			
 		}
		KqParameter para=new KqParameter(this.getFormHM(),this.userView,code,this.getFrameconn());
		String stbs=para.getG_no();
		
		String zjnumber = getzjnumber(a0100s,select_pres,stbs);
		this.getFormHM().put("flag", zjnumber);
	}
	
	public String getzjnumber(String a0100s,String select_pres,String stbs){
		String scode=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		try {
			String sql = "select "+stbs+" from "+select_pres+"A01 where a0100='"+a0100s+"'";
			rs = dao.search(sql);
			while(rs.next()){
				scode = rs.getString(stbs);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return scode;
	}
}
