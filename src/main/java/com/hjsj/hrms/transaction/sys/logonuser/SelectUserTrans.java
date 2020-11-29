/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

/**
 * <p>Title:查询用户</p>
 * <p>Description:operuser</p> 
 * <p>Company:hjsj</p> 
 * create time at:Nov 22, 200612:03:19 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SelectUserTrans extends IBusiness {


	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");	
		String priv=(String)hm.get("priv");
		String report=(String)hm.get("report");//报表填报单位~~~zhaoxg 2013-6-7
		if(report==null|| "".equals(report)){
			report = "0";
		}
		String isself=(String)hm.get("isself");  //是否显示自己   1:显示  0:不显示
		String isShowFullName="0";  //树节点是否显示全名 0：不显示  1：显示
		String salaryid="";
		if(hm.get("isShowFullName")!=null&&((String)hm.get("isShowFullName")).length()>0)
		{
			isShowFullName=(String)hm.get("isShowFullName");
			hm.remove("isShowFullName");
		}
		if(hm.get("salaryid")!=null&&((String)hm.get("salaryid")).length()>0&&!"undefined".equalsIgnoreCase(salaryid))
		{
			salaryid=(String)hm.get("salaryid");
			hm.remove("salaryid");
		}
		
		
		/**radio ,checkbox,树形选择框类型*/
		String treeselectType = (String)hm.get("treeselecttype");
		if(treeselectType == null || "".equals(treeselectType)){
			treeselectType = "0";
		}
		if(isself == null || "".equals(isself)){
			isself = "1";
		}

		this.getFormHM().put("treeselecttype",treeselectType);
	
		String groupid=this.userView.getGroupId();
		if(priv!=null && "1".equals(priv))
			groupid="1";
		TreeItemView treeItem=new TreeItemView();
		treeItem.setName("root");
		treeItem.setRootdesc("root");
		treeItem.setTitle("root");
		treeItem.setIcon("/images/group.gif");	
		treeItem.setTarget("il_body");
		String rootdesc=ResourceFactory.getProperty("label.user.group");
	    treeItem.setRootdesc(rootdesc);
		treeItem.setText(rootdesc); 
		if(!"0".equals(isself))
			treeItem.setLoadChieldAction("/system/logonuser/search_user_servlet?flag=0&salaryid="+salaryid+"&isShowFullName="+isShowFullName+"&level0=0&groupid="+groupid+"&treeselecttype="+treeselectType);
		else
			treeItem.setLoadChieldAction("/system/logonuser/search_user_servlet?flag=0&salaryid="+salaryid+"&isShowFullName="+isShowFullName+"&level0=0&groupid="+groupid+"&treeselecttype="+treeselectType+"&selfusername="+this.userView.getUserName());
		
		treeItem.setAction("javascript:void(0)");	   
	    try
	    {
			//-----------------获取报表负责人，返回其username  zhaoxg 2013-6-7---------------
	    	if("1".equals(report)){
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String uc=(String)hm.get("uc");
				RowSet rs  = dao.search("select username from operuser where unitcode = '"+uc+"'");
				RowSet rs1  = dao.search("select groupname from usergroup where groupid in (select groupid from operuser where unitcode = '"+uc+"')");
				StringBuffer reportUser = new StringBuffer();
				StringBuffer groupid1 = new StringBuffer();
				reportUser.append(",");
				while(rs.next()){
					reportUser.append(rs.getString("username"));
					reportUser.append(",");
				}
				while(rs1.next()){
					groupid1.append(rs1.getString("groupname"));
					groupid1.append(",");
				}
				this.getFormHM().put("reportUser",reportUser.toString());
				this.getFormHM().put("groupid1",groupid1.toString());
				this.getFormHM().put("report",report);
	    	}

			
		    this.getFormHM().put("usertree",treeItem.toJS());	//cmq 
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	throw GeneralExceptionHandler.Handle(ex);
	    }
	}

}
