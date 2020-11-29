/*
 * Created on 2006-3-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.org.map;


import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchHistoryOrgmapsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String code=(String)hm.get("code"); 
		//String kind=(String)hm.get("kind");
		String catalog_id=(String)hm.get("catalog_id"); 
		try{
			if(code!=null && code.length()>0)
			{
				StringBuffer sqlstr=new StringBuffer();
				sqlstr.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,grade,a0000,'org' as infokind from hr_org_history where codeitemid<>'");
				sqlstr.append(code);
				sqlstr.append("' and parentid='");
				sqlstr.append(code);
				sqlstr.append("' and catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("'");
			    List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
			    if(!rs.isEmpty())
			    {
			   	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			   	  this.getFormHM().put("grades",rec.get("grade"));
			    }		  
			    this.getFormHM().put("childslist",rs);
		    }
			else
			{
				StringBuffer sqlstr=new StringBuffer();
				sqlstr.append("select codesetid,codeitemid,codeitemdesc,grade,childid,'org' as infokind from hr_org_history ");
				if(userView.isSuper_admin()){
					sqlstr.append(" where codesetid='UN' and codeitemid=parentid");
				}else{
					String busi=this.getBusi_org_dept(userView);
					if(busi.length()>2){
						if(busi.indexOf("`")!=-1){
							StringBuffer sb = new StringBuffer();
							String[] tmps=busi.split("`");
							for(int i=0;i<tmps.length;i++){
								String a_code=tmps[i];
								if(a_code.length()>2){
									sb.append("','"+a_code.substring(2));
								}
							}
							if(sb.length()>3)
								sqlstr.append(" where codeitemid in('"+sb.substring(3)+"') ");
                            else if("UN".equalsIgnoreCase(tmps[0].toUpperCase()))
								sqlstr.append(" where codesetid='UN' and codeitemid=parentid");
							else
								sqlstr.append(" where 1=2 ");
						}else{
							sqlstr.append(" where codeitemid='"+busi.substring(2)+"' ");
						}
					}else{
						sqlstr.append(" where 1=2 ");
					}
				}
				//sqlstr.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,grade,a0000,'org' as infokind from hr_org_history where parentid=codeitemid");
				sqlstr.append(" and catalog_id='");
				sqlstr.append(catalog_id);
				sqlstr.append("'");
			    List rs=ExecuteSQL.executeMyQuery(sqlstr.toString());
			    if(!rs.isEmpty())
			    {
			   	  LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			   	  this.getFormHM().put("grades",rec.get("grade"));
			    }	
			    this.getFormHM().put("childslist",rs);			 
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
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


   

