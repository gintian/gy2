/*
 * Created on 2006-2-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.inform.pos;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPosTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String backdate = (String)this.formHM.get("backdate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		String busiPriv = (String)hm.get("busiPriv");
		busiPriv=busiPriv==null?"":busiPriv;
		// 自助平台组织机构的信息浏览改为控制权限（bug:32512 新都化工：员工自助中，机构信息浏览中权限问题） chent 20171108 delete start
		/*String droit=(String)hm.get("droit");//权限标记,自主平台的组织机构/信息浏览不用权限过滤 
		if(droit==null||droit.length()<=0)*/
		// 自助平台组织机构的信息浏览改为控制权限（bug:32512 新都化工：员工自助中，机构信息浏览中权限问题） chent 20171108 delete end
		String droit="1";
		TreeItemView treeItem=new TreeItemView();
		String action="searchorgbrowse.do"; 
		String target="mil_body";
		String treetype="duty";//org,duty,employee,noum
		treeItem.setName("root");		
		treeItem.setIcon("/images/unit.gif");	
		String kind="2";
		treeItem.setTarget(target);
		String rootdesc="";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		rootdesc=sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
		if(rootdesc==null||rootdesc.length()<=0)
		{
			rootdesc=ResourceFactory.getProperty("tree.orgroot.orgdesc");
		}
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&droit="+droit+"&params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&backdate="+backdate);
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&droit="+droit+"&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&backdate="+backdate);
		    else
			    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&droit="+droit+"&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no&backdate="+backdate);
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	   	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    String sqlstr="";	
	    StringBuffer select_str = new StringBuffer();
	    String infokind="3";
	    if("0".equals(droit)){
	    	infokind="2";
	    	String busi=this.getBusi_org_dept(userView);
	    	 if(busi.length()>2){
	 	    	StringBuffer sb = new StringBuffer();
	 	    	sb.append("select min(b0110) as b0110 from b01 where 1=2");
	 	    	String[] org_depts = busi.split("`");
	 			for(int i=0;i<org_depts.length;i++){
	 				String org_dept = org_depts[i];
	 				if(org_dept.length()>2){
	 					sb.append(" or b0110='"+org_dept.substring(2)+"'");
	 				}else{
	 					sb.append("or 1=1");
	 				}
	 			}
	 	    	sqlstr=sb.toString();
	 	    }
	 	    else if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
	 	    	sqlstr="select min(b0110) as b0110 from b01 where b0110 like '" + userView.getManagePrivCodeValue() + "'";
	 	    else{
	 	    	if(this.userView.isSuper_admin())
	 	    		sqlstr="select min(b0110) as b0110 from b01";
	 	    }
	    }else{
		    if(!userView.isSuper_admin()){
		    	select_str.append("select e01a1 as b0110 from k01 left join organization on codeitemid=e01a1");
			    String busi=this.getBusi_org_dept(userView);
				if(busi.length()>2){
					if(busi.indexOf("`")!=-1){
						select_str.append(" where (");
						String[] tmps=busi.split("`");
						for(int i=0;i<tmps.length;i++){
							String a_code=tmps[i];
							if(a_code.length()>2){
							 	select_str.append(" codeitemid like '"+a_code.substring(2)+"%' or");
							}else if(a_code.length()==2){
								select_str.append(" 1=1 or");
							}
						}
						select_str.append(" 1=2) ");
					}else{
						select_str.append(" where codeitemid like '"+busi.substring(2)+"%'");
					}
				}else{
					select_str.append(" where 1=2 ");
				}
				  select_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date  and codesetid='@K'");
				  sqlstr=select_str.toString();
			}else{
		    	sqlstr="select e01a1 as b0110 from k01 left join organization on codeitemid=e01a1 where "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and codesetid='@K'";
			}
		    sqlstr = sqlstr + " order by A0000,codeitemid,flag";
	    }
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    String code="";
	    try{
	    	this.frowset=dao.search(sqlstr);
	    	if(this.frowset.next())
	    		code=this.frowset.getString("b0110");
	    }catch(Exception e)
		{
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }	   
	    this.getFormHM().put("nid",code);
	    this.getFormHM().put("infokind",infokind);
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
