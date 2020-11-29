package com.hjsj.hrms.module.hire.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CheckLinksTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try 
		{
			//链接--/hire/hireNetPortal/resetPassword.do?action=resetpassword&amp; 定义忘记密码username=fffd345&; 需要修改密码的账号active=df71753df5c8811340561de6d8f80a0b 验证是否发起过修改密码请求
			String info = "ok";
			long between = 0;
			
			//需要修改密码的账号
			String username = "";
			if(this.getFormHM().get("emailName")!=null)
            {
            	username=(String)this.getFormHM().get("emailName");
            }
			username = PubFunc.decrypt(username);
			//验证是否发起过修改密码请求
			String active = "";
			if(this.getFormHM().get("active")!=null)
            {
            	active=(String)this.getFormHM().get("active");
            }
			
			String guidkey = isnull(username);
			if("".equalsIgnoreCase(guidkey)){
				info = "用户名不存在";
				this.getFormHM().put("info", info);
				return;
			}
			HashMap map = getActive(guidkey);
			String isnull = (String) map.get("isnull");
			if(isnull!=null && !"".equalsIgnoreCase(isnull) && isnull.length() > 0){
				info = "链接失效，请重新找回密码";
				this.getFormHM().put("info", info);
				return;
			}
			//从库中获取--发起重新设置密码的时间
			String activetime = (String) map.get("activetime");
			//从库中获取--验证是否发起过修改密码请求
			String active_s = (String) map.get("active");
			//获取当前时间
			Date date = new Date();
			SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dfs.format(date);
			Date end = dfs.parse(time);
			
			Date begin = dfs.parse(activetime);
			between = (end.getTime()-begin.getTime());
			long day = between / (24 * 60 * 60 * 1000);
			//相差小时数，不满一小时为0
			int hour = (int) (between / (60 * 60 * 1000) - day * 24);
			if(day == 0 && hour >= 2){
				info = "重置密码链接失效，请重新找回密码";
				this.getFormHM().put("info", info);
				return;
			}
			
			if(!active.equalsIgnoreCase(active_s)){
				info = "链接无效，请重新找回密码";
				this.getFormHM().put("info", info);
				return;
			}
			this.getFormHM().put("guidkey", guidkey);
			this.getFormHM().put("info", info);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	//获得active,Activetime
	public HashMap getActive(String guidkey){
		HashMap map = new HashMap();
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = null;
			StringBuffer sql = new StringBuffer();
			sql.append("select active,");
			sql.append(Sql_switcher.dateToChar("Activetime", "yyyy-mm-dd hh24:mi:ss"));
			sql.append(" Activetime from t_sys_resetpassword");
			sql.append(" where Activetime=(");
			sql.append(" select max(Activetime) from t_sys_resetpassword");
			sql.append("  where guidkey='"+guidkey+"'");
			sql.append(")");
			rs = dao.search(sql.toString());
			if(rs.next()){
				String active = rs.getString("active");//发起的标示
                String activetime = rs.getString("Activetime");//发起的时间
                map.put("active", active);
                map.put("activetime", activetime);
			}else{
				map.put("isnull", "isnull");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	//获得guidkey
	public String isnull(String username) throws GeneralException{
		String guidkey = "";
		try {
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = null;
			StringBuffer sql = new StringBuffer();
			sql.append("select guidkey");
			sql.append(" from "+dbname+"A01");
			sql.append(" where UserName=?");
			ArrayList<String> list = new ArrayList<String>();
			list.add(username);
			rs = dao.search(sql.toString(), list);
			if(rs.next()){
				guidkey = rs.getString("guidkey");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return guidkey;
		
	}
}
