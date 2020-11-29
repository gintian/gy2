package com.hjsj.hrms.transaction.police;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:PoliceSetDateTrans
 * </p>
 * <p>
 * Description:设置狱情动态
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-13
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class PoliceSetYqdtTrans extends IBusiness {

	public void execute() throws GeneralException {
		TreeItemView treeItem=new TreeItemView();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String backdate = (String) hm.get("backdate");
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		String action=(String)hm.get("action"); 
		String target=(String)hm.get("target");
		String treetype=(String)hm.get("treetype");//org,duty,employee,noum
		String loadtype=(String)hm.get("loadtype");	 /**加载选项  * =0（单位|部门|职位）   * =1 (单位|部门)   * =2 (单位) * */
		if(loadtype==null||loadtype.length()<=0)
			loadtype="0";
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
		this.getFormHM().put("root", rootdesc);
	    treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
	    treeItem.setText(rootdesc);
	    treeItem.setTitle(rootdesc);
	    if(userView.isSuper_admin())
		    treeItem.setLoadChieldAction("/common/vorg/loadtreeyqdt?params=root&parentid=00&issuperuser=1&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/vorg/loadtreeyqdt?params=root&parentid=00&issuperuser=0&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		    else
			    treeItem.setLoadChieldAction("/common/vorg/loadtreeyqdt?params=root&parentid=00&issuperuser=0&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + "no");
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	    if(!"org".equals(treetype) && !"duty".equals(treetype))
	        treeItem.setAction("javascript:void(0)");//" + userView.getManagePrivCodeValue() + "
	    else
	    	treeItem.setAction("javascript:void(0)");
	    this.getFormHM().put("treeCode",treeItem.toJS());
	    /**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        String userbase="";
        if(dblist.size()>0){
        	userbase=dblist.get(0).toString();      
        }
        else
        	userbase="usr";
        for(int i=0;i<dblist.size();i++)
        {
        	
        	if(i!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        cond.append(" order by dbid");
        /**应用库前缀过滤条件*/
        cat.debug("-----userbase------>" + userbase);
        this.getFormHM().put("userbase",userbase);
        this.getFormHM().put("dbcond",cond.toString());
        this.getFormHM().put("policeConstant", selectPoliceConstant());
	}
	
	private String selectPoliceConstant() {
		String checkvalues = "";
		String sql = "select * from constant where constant='POLICE_SETYQDT'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			frowset = dao.search(sql);
			if (frowset.next()) {
				checkvalues = frowset.getString("str_value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return checkvalues;
	}

}
