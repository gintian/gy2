package com.hjsj.hrms.transaction.police;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.sql.SQLException;
import java.util.*;
/**
 * 
 *<p>Title:PoliceOrgTree</p> 
 *<p>Description:加载组织机构树</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:2010-02-07</p> 
 *@author wangzhongjun
 *@version 1.0
 */
public class PoliceOrgTree extends IBusiness {

	public void execute() throws GeneralException {
//		if(this.userView.getManagePrivCode().equalsIgnoreCase("UN"))
//			this.getFormHM().put("loadtype","2");
//		else if(this.userView.getManagePrivCode().equalsIgnoreCase("UM"))
//			this.getFormHM().put("loadtype","1");
//		else
//			this.getFormHM().put("loadtype","0");
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String cyclename = (String) map.get("cyclename");
		String cycle = "1";
		if (cyclename != null && "team".equals(cyclename)) {
			cycle = this.querryCycleValue("employ");
		} else {
			cycle = this.querryCycleValue(cyclename);
		}
		
		if (cyclename!= null && "yqdt".equals(cyclename)) {
			loadTree();
		}
		this.getFormHM().put("cycle", cycle);
		this.getFormHM().put("cyclename", cyclename);
		//去除年，月，周期
		this.getFormHM().remove("taskyear");
		this.getFormHM().remove("taskmonth");
		this.getFormHM().remove("taskweek");
		this.getFormHM().remove("yearlist");
		this.getFormHM().remove("monthlist");
		this.getFormHM().remove("weeklist");
	}
	
	/**
	 * 查询所需要的周期值
	 * @param name
	 * @return
	 */
	private String querryCycleValue(String name) {
		String cycle = "";
		if (this.isExist("JYZY_CYCLE_PARAM")) {
			String str_value = this.selectStr_Value("JYZY_CYCLE_PARAM");
			this.analysis(str_value);
			cycle = (String) this.getFormHM().get(name);
		} else {
			GeneralExceptionHandler.Handle(new GeneralException("未设置周期！请首先设置周期"));
		}
		return cycle;
		
	}
	
	/**
	 * 查询周期的值
	 * 
	 * @param constant
	 * @return
	 */
	public String selectStr_Value(String constant) {
		// 查询所获得字符窜
		String str_value = "";

		if (this.isExist(constant)) {
			// sql语句
			StringBuffer sql = new StringBuffer();
			sql.append("select Str_Value from constant where constant='");
			sql.append(constant);
			sql.append("'");

			// 查询操作
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search(sql.toString());
				if (this.frowset.next()) {
					str_value = frowset.getString("Str_Value");
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return str_value;
	}

	/**
	 * 判断常量是否存在
	 * @param constant 常量名称
	 * @return
	 */
	private boolean isExist(String constant) {

		// 是否存在该常量
		boolean flag = false;

		// sql语句
		StringBuffer sql = new StringBuffer();
		sql.append("select * from constant where constant='");
		sql.append(constant);
		sql.append("'");

		// 查询操作
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				flag = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	/**
	 * 解析xml文件
	 * @param str_value
	 * @return
	 */
	private boolean analysis(String str_value) {
		//是否解析成功
		boolean flag = true;
		//初始化xml
		try {
			Document doc = PubFunc.generateDom(str_value);
			String xpath = "/param/data";
			// 取得子集结点
			XPath reportPath = XPath.newInstance(xpath);
			List childlist = reportPath.selectNodes(doc);
			Iterator it = childlist.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				this.getFormHM().put(el.getAttributeValue("name"), el.getAttributeValue("cycle"));
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		
		return flag;
	}
	
	private void loadTree() {
		String deptCode = this.userView.getUserDeptId();
		if (deptCode == null) {
			deptCode = "";
		}
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
		    treeItem.setLoadChieldAction("/common/vorg/loadtreeyq?params=root&parentid=00&issuperuser=1&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&deptCode="+deptCode);
		else
		{
			if(userView.getStatus()==4 || userView.getStatus()==0)
				treeItem.setLoadChieldAction("/common/vorg/loadtreeyq?params=root&parentid=00&issuperuser=0&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + userView.getManagePrivCodeValue()+"&deptCode="+deptCode);
		    else
			    treeItem.setLoadChieldAction("/common/vorg/loadtreeyq?params=root&parentid=00&issuperuser=0&loadtype="+loadtype+"&treetype=" + treetype + "&action=" + action + "&target=" + target + "&backdate="+backdate+"&manageprive=" + userView.getManagePrivCode() + "no"+"&deptCode="+deptCode);
			if("UN".equals(userView.getManagePrivCode()))
		    	kind="2";
		    else if("UM".equals(userView.getManagePrivCode()))
		    	kind="1";
		    else if("@K".equals(userView.getManagePrivCode()))
		    	kind="0";
		}
	    if(!"org".equals(treetype) && !"duty".equals(treetype))
	        treeItem.setAction("/pos/police/jqdt.do?b_search=link&tofirst=yes&isroot=1&backdate="+backdate+"&a_code=UN&kind=" + kind);//" + userView.getManagePrivCodeValue() + "
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
