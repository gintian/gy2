/**
 * 
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * @author Administrator
 *
 */
public class InitPrivDateTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		String start_date = (String)this.getFormHM().get("start_date");
		String end_date = (String)this.getFormHM().get("end_date");
		start_date = start_date!=null?start_date:"";
		end_date = end_date!=null?end_date:"";
		
		String vflag=SystemConfig.getPropertyValue("vorganization");
		if(vflag==null|| "false".equals(vflag)|| "".equals(vflag)){
			vflag="0";
		}else{
			vflag="1";
		}
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    if(userView.isSuper_admin()){
		    	sql = "update organization set start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue(end_date)+"";
		    	dao.update(sql);
		    	if("1".equalsIgnoreCase(vflag)){
		    		sql = "update vorganization set start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue(end_date)+"";
		    		dao.update(sql);
		    	}
		    }
			else
			{
				if(userView.getStatus()==4 || userView.getStatus()==0){
					String busi = getBusi_org_dept(this.userView);
					
					StringBuffer strsql = new StringBuffer("update organization set start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue(end_date)/*+" where codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'"*/);
					if(busi.length()>2){
						if(busi.indexOf("`")!=-1){
							strsql.append(" where (");
							String[] tmps=busi.split("`");
							for(int i=0;i<tmps.length;i++){
								String a_code=tmps[i];
								if(a_code.length()>2){
									strsql.append(" codeitemid like '"+a_code.substring(2)+"%' or");
								}
							}
							strsql.append(" 1=2) ");
						}else{
							strsql.append(" where codeitemid like '"+busi.substring(2)+"%' ");
						}
					}else{
						strsql.append(" where 1=2 ");
					}
					dao.update(strsql.toString());
			    	if("1".equalsIgnoreCase(vflag)){
			    		strsql.setLength(0);
			    		strsql.append("update vorganization set start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue(end_date)/*+" where codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'"*/);
			    		if(busi.length()>2){
							if(busi.indexOf("`")!=-1){
								strsql.append(" where (");
								String[] tmps=busi.split("`");
								for(int i=0;i<tmps.length;i++){
									String a_code=tmps[i];
									if(a_code.length()>2){
										strsql.append(" codeitemid like '"+a_code.substring(2)+"%' or");
									}
								}
								strsql.append(" 1=2) ");
							}else{
								strsql.append(" where codeitemid like '"+busi.substring(2)+"%' ");
							}
						}else{
							strsql.append(" where 1=2 ");
						}
			    		dao.update(strsql.toString());
			    	}
				}
			    else{
			    	
			    }
				
			}
		    this.getFormHM().put("msg", "1");
		}catch(Exception e){
			this.getFormHM().put("msg", "0");
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
	}

	private String getBusi_org_dept(UserView userView) {
		String busi = "";
				String busi_org_dept = "";
				Connection conn = null;
				RowSet rs = null;
				try {
					
					busi_org_dept = userView.getUnitIdByBusi("4");
					if (busi_org_dept.length() > 0) {
						busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
					}else{
						busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
		return busi;
	}
}
