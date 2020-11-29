package com.hjsj.hrms.transaction.report.report_isApprove;

import com.hjsj.hrms.businessobject.hire.SendEmail;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Report_bohuiAndshangbaoTran extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String unitcode = (String) this.getFormHM().get("unitcode1");
		String flag = (String) this.getFormHM().get("flag");
		String tabid=(String)this.getFormHM().get("tabid");	
		String content1=(String)this.getFormHM().get("content1");
		ReportApproveTrans reportApproveTrans = new ReportApproveTrans();
		String appuser = "";
		String username = "";
		String description = "";
		String content = "";
		String sqll = "select appuser,username,description from treport_ctrl where unitcode = '"+unitcode+"' and tabid = "+tabid+"";
		RowSet rs = dao.search(sqll);
		if(rs.next()){
			appuser = rs.getString("appuser");
			username = rs.getString("username");
			description = rs.getString("description");
		}
		if("1".equals(flag)){
			//update by wangchaoqun on 2014-9-16 begin
			if(description == null){
				description = reportApproveTrans.getTime()+" "+"被"+" "+this.userView.getUserFullName()+" "+"驳回，"+"驳回原因："+content1;
			}else{
				description = description +";"+ reportApproveTrans.getTime()+" "+"被"+" "+this.userView.getUserFullName()+" "+"驳回，"+"驳回原因："+content1;
			}
			//update by wangchaoqun on 2014-9-16 end
			appuser = appuser +";"+getFullName(username);
			content = "你报批的表："+tabid+reportApproveTrans.getTime()+" "+"被"+" "+this.userView.getUserFullName()+" "+"驳回，"+"驳回原因："+content1;
		}else if("2".equals(flag)){
			description = description +";"+ reportApproveTrans.getTime()+"由"+this.userView.getUserFullName()+"批准，"+"批准原因："+content1;
			content = "你报批的表："+tabid+reportApproveTrans.getTime()+"由"+this.userView.getUserFullName()+"批准，"+"批准原因："+content1;
		}	

		
		RecordVo vo=new RecordVo("treport_ctrl");
		vo.setString("unitcode", unitcode);
		vo.setInt("tabid", Integer.parseInt(tabid));
		if("1".equals(flag)){
			vo.setInt("status", 2);
		}else if("2".equals(flag)){
			vo.setInt("status", 1);
		}
		vo.setString("currappuser", username);
		vo.setString("username", username);
		vo.setString("appuser", appuser);
		vo.setString("description", description);
		dao.updateValueObject(vo);
		
//		StringBuffer sql = new StringBuffer();
//		sql.append("update treport_ctrl set description ='"+description+"',appuser = '"+appuser+"',currappuser = '"+username+"',username='"+username+"' ");
//		if(flag.equals("1")){
//			sql.append(",status='2'");
//		}else if(flag.equals("2")){
//			sql.append(",status='1'");
//		}
//		
//		sql.append(" where unitcode = '"+unitcode+"' and tabid = '"+tabid+"'");
//		dao.update(sql.toString());
		ArrayList unitslist=this.getUnitsList(unitcode);
		if(unitslist!=null){
			SendEmail sendEmail=new SendEmail();
			sendEmail.setInfo();
			for(int i=0;i<unitslist.size();i++){
				LazyDynaBean abean1=(LazyDynaBean)unitslist.get(i);
				String sendTo=(String)abean1.get("email");
				
				if(sendTo.length()>0){
					try{
						String title = "报表审批结果";
						sendEmail.send(sendTo,title,content);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}

			
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 获取单位邮箱地址
	 */
	public ArrayList getUnitsList(String unitcode){
		ArrayList unitslist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		sql.append("select * from  (select * from operuser where (email is not null and "+Sql_switcher.length("email")+"!=0)or (phone is not null and "+Sql_switcher.length("email")+"!=0) ) o inner join (select * from tt_organization where unitcode like '");
		sql.append(unitcode);
		sql.append("%') tt on o.unitcode=tt.unitcode");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next()){
				LazyDynaBean bean=new LazyDynaBean();
				if(this.frowset.getString("FullName")!=null&&this.frowset.getString("FullName").length()!=0){
					bean.set("usrName", this.frowset.getString("FullName"));
				}else{
					bean.set("usrName", this.frowset.getString("UserName"));
				}
				bean.set("unitcode", this.frowset.getString("unitcode"));
				bean.set("phone", this.frowset.getString("phone"));
				bean.set("email", this.frowset.getString("email"));
				unitslist.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return unitslist;
	}
	/**
	 * 获取报表填报人姓名
	 */
	public String getFullName(String username){
		String fullname = "";
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select fullname from operuser where username = '"+username+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				fullname = rs.getString("fullname");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return fullname;
	}
}
