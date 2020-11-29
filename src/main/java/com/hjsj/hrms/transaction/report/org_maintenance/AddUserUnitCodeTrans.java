package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class AddUserUnitCodeTrans extends IBusiness {

	//设置用户填报单位编码
	public void execute() throws GeneralException {
		
		//获得以get方式传递的参数集合
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		//String userName = (String) hm.get("username"); //用户名
		String unitCode = (String) hm.get("unitcode"); //负责的填报单位
		String userName = (String)this.getFormHM().get("content");
	
		try {
			//userName = new String(userName.getBytes("iso8859_1"));
			unitCode = new String(unitCode.getBytes("iso8859_1"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		//System.out.println("userName=" + userName);
		//System.out.println("unitCode=" + unitCode);
		
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		try {
			//将先前设置置为空
			sql.delete(0, sql.length());
			sql.append("update operuser set unitcode = null where unitcode = '");
			sql.append(unitCode);
			sql.append("'");			
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		String un [] = userName.split(",");
		for(int i=0; i< un.length; i++){
			String user = un[i];
			if(user == null || "".equals(user)){}else{
				try {
					//重新设置
					sql.delete(0, sql.length());
					sql.append("update operuser set unitcode = '");
					sql.append(unitCode);
					sql.append("' where username = '");
					sql.append(user);
					sql.append("'");
					dao.update(sql.toString());
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}
			}
		}
		
	
		
		/*
		
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String unitCode = (String)this.getFormHM().get("usunitcode");
	//	System.out.println("usUnitCode=" + unitCode);
		
		try {
			//将先前设置置为空
			sql.delete(0, sql.length());
			sql.append("update operuser set unitcode = null where unitcode = '");
			sql.append(unitCode);
			sql.append("'");			
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		//用户信息列表
		ArrayList adduserlist = (ArrayList) this.getFormHM().get("selectedlist");
		for(int i = 0 ; i< adduserlist.size(); i++){
			RecordVo vo = (RecordVo)adduserlist.get(i);
			String userName = vo.getString("username");
			//将此填报单位的用户对应信息清空
			try {
				//重新设置
				sql.delete(0, sql.length());
				sql.append("update operuser set unitcode = '");
				sql.append(unitCode);
				sql.append("' where username = '");
				sql.append(userName);
				sql.append("'");
				dao.update(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}

		}*/

		//设置确定后的跳转参数，当前填报单位编码
		this.getFormHM().put("unitCodeFalg",unitCode);
		
	}
	

}