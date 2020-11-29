/*
 * Created on 2005-6-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.tree;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoadBackdateOrgTreeTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		TreeItemView treeItem=new TreeItemView();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String backdate = (String) hm.get("backdate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		String action=(String)hm.get("action"); 
		String target=(String)hm.get("target");
		String treetype=(String)hm.get("treetype");//org,duty,employee,noum
		String loadtype=(String)hm.get("loadtype");	 /**加载选项  * =0（单位|部门|职位）   * =1 (单位|部门)   * =2 (单位) * */
		if(loadtype==null||loadtype.length()<=0)
			loadtype="0";
		String busiPriv = (String)hm.get("busiPriv");
		busiPriv=busiPriv==null?"":busiPriv;
		treeItem.setName("root");
		treeItem.setIcon("/images/unit.gif");	
		String kind="";
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
	    if(userView.isSuper_admin()){
	    	kind="2";
		    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=1&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
	    }else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=0&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue());
		    else
			    treeItem.setLoadChieldAction("/common/vorg/loadtree?busiPriv="+busiPriv+"&params=root&parentid=00&issuperuser=0&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + "no");
			String busi=userView.getUnitIdByBusi("4");
			if(busi.replaceAll("`", "").length()==2)
				kind="2";
			/*if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";*/
		}
	    if(!"org".equals(treetype) && !"duty".equals(treetype))
	        treeItem.setAction(action + "?b_search=link&isroot=1&backdate="+backdate+"&code=&kind=" + kind);//" + userView.getManagePrivCodeValue() + "
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
        this.getFormHM().put("setprv",getEditSetPriv(infoSetList,"A01"));
        this.getFormHM().put("backdate", backdate);
        /****************是否显示岗位附件*************/
		String sql="select str_value from constant where upper(constant)='PS_CARD_ATTACH'";
		/*RecordVo vo = new RecordVo("CONSTANT");
		vo.setString("constant", "PS_CARD_ATTACH");*/
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String value="";
		try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				value=this.frowset.getString("str_value");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(value==null|| "".equals(value))
			 value="false";
	    this.getFormHM().put("ps_card_attach", value);
	    /****************结束*************/
	}
	/**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(List infoSetList,String setname)
	{
		String setpriv="1";
		boolean bflag=false;
		/**先根据子集分析*/
		for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
			{
				setpriv=String.valueOf(fieldset.getPriv_status());
				break;
			}
		}	
	  return setpriv;	
	}
}


