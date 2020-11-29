package com.hjsj.hrms.transaction.general.inform.org;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchVOrgTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub 
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String droit=(String)hm.get("droit");//权限标记,自主平台的组织机构/信息浏览不用权限过滤 
		if(droit==null||droit.length()<=0)
			droit="1";
		String busiPriv = (String)hm.get("busiPriv");
		busiPriv=busiPriv==null?"":busiPriv;
		hm.remove("busiPriv");
		TreeItemView treeItem=new TreeItemView();
		String action="searchorgbrowse.do"; 
		String target="mil_body";
		String treetype="employee";//org,duty,employee,noum
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
		treeItem.setTitle(rootdesc);
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
		treeItem.setText(rootdesc); 
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&droit="+droit+"&params=root&parentid=00&issuperuser=1&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&droit="+droit+"&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		    else
			    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&droit="+droit+"&params=root&parentid=00&issuperuser=0&treetype=" + treetype + "&action=" + action + "&target=" + target + "&manageprive=" + userView.getManagePrivCode() + "no");
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	   	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    
	    String date = DateStyle.getSystemDate().getDataStringToDate();
	    StringBuffer sqlstr=new StringBuffer();
	    sqlstr.append(" select  b0110 from b01 B left join organization O on B.b0110=O.codeitemid where ");
	    sqlstr.append(Sql_switcher.dateValue(date)+" between O.start_date and O.end_date ");
	    String busi = this.getBusi_org_dept(busiPriv);
	    
	    if(busi.length()>2){
	    	StringBuffer sb = new StringBuffer();
	    	sb.append("  and ( 1=2 ");
	    	String[] org_depts = busi.split("`");
			for(int i=0;i<org_depts.length;i++){
				String org_dept = org_depts[i];
				if(org_dept.length()>2){
					sb.append(" or b0110='"+org_dept.substring(2)+"'");
				}else{
					sb.append("or 1=1 ");
				}
			}
			sb.append(" ) order by a0000");
	    	sqlstr.append(sb.toString());
	    }
	    else if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
	    	sqlstr.append(" and b0110 like '" + userView.getManagePrivCodeValue() + "' order by a0000");
	    else{
	    	if(this.userView.isSuper_admin()){
	    		
	    		//如果调整顺序了，此sql不适用。应按照a0000排序
	    		//sqlstr="select min(b0110) as b0110 from b01 B left join organization O on B.b0110=O.codeitemid where "+Sql_switcher.dateValue(date)+" between O.start_date and O.end_date and parentid=codeitemid";
	    		sqlstr.append(" and parentid=codeitemid order by a0000 ");
	    	}else
	    		sqlstr.append("  and  1=2");
	    }
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    String code="";
	    try{
	    	this.frowset=dao.search(sqlstr.toString());
	    	if(this.frowset.next())
	    		code=this.frowset.getString("b0110");
	    }catch(Exception e)
		{
	    	e.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(e);
	    }	   
	    this.getFormHM().put("nid",code);
	    this.getFormHM().put("infokind","2");
	}

	private String getBusi_org_dept(String busiPriv){
		String busi="";
		if("1".equals(busiPriv)){
		int status = this.userView.getStatus();
		if (!this.userView.isSuper_admin() /*&& 0 == status*/) {// 非超级用户组下业务用户
			String busi_org_dept = "";
			try {
				/*ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "select busi_org_dept from operuser where username='"
						+ this.userView.getUserName() + "'";
				this.frecset = dao.search(sql);
				while (this.frecset.next()) {
					busi_org_dept = Sql_switcher.readMemo(this.frecset,
							"busi_org_dept");
				}*/
				busi_org_dept = this.userView.getUnitIdByBusi("4");
				if (busi_org_dept.length() > 0) {
					/*String str[] = busi_org_dept.split("\\|");
					for (int i = 0; i < str.length; i++) {// 1,UNxxx`UM9191`
						String tmp = str[i];
						String ts[] = tmp.split(",");
						if (ts.length == 2) {
							if("4".equals(ts[0])){
							busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(ts[1]);
								break;
							}
						}
					}*/
					busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`

			}
		}
		}
		return busi;
	}
}
