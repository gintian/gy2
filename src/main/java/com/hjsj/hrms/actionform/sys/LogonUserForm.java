/**
 * 
 */
package com.hjsj.hrms.actionform.sys;

import com.hjsj.hrms.valueobject.sys.UserInfo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * <p>Title:LogonUserForm</p>
 * <p>Description:用户管理</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-6:14:21:43</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class LogonUserForm extends FrameForm {
	
	/**用户组或用户树代码*/
	private String userTree;
	private String user_name;
	/**选择类型check radio世间0、1、2*/
	private String selecttype="1";
	/**加载人0 、1*/
	private String flag="1";
	/**加载用户库
	 * =0 权限范围内的库
	 * =1 权限范围内的登录用户库
	 * */
	private String dbtype="0";
	/**权限过滤*/
	private String priv="1"; 
	/**其他过滤条件*/
	private String isfilter="0";
    /**加载选项
     * =0（单位|部门|职位）
     * =1 (单位|部门)
     * =2 (单位)
     */ 
	private String loadtype="0";
	
	
	private RecordVo user_vo=new RecordVo("operuser");
	
	private UserInfo userinfo=new UserInfo();
	
	/**用户树显示类型(=0(default),=1(checkbox),=2(radio))*/
	private String treeSelectType;
	
	private String busi_org_dept1;//1:工资发放
	private String busi_org_dept1view;
	private String busi_org_dept2;//2:工资总额
	private String busi_org_dept2view;
	private String busi_org_dept3;//3:所得税
	private String busi_org_dept3view;
	private String busi_org_dept4;//4:组织机构
	private String busi_org_dept4view;
	/**
	 *  5: 绩效管理
		6：培训管理
		7：招聘管理
		8：业务模板
	 */
	private String busi_org_dept5;
	private String busi_org_dept5view;
	private String busi_org_dept6;
	private String busi_org_dept6view;
	private String busi_org_dept7;
	private String busi_org_dept7view;
	private String busi_org_dept8;
	private String busi_org_dept8view;
	private String checkvalue;
	//bug 36668 获取选中节点对应title wangb 20180420
	private String checktitle;
	public UserInfo getUserinfo() {
		return userinfo;
	}
	//------------取得报表负责人的username zhaoxg 2013-6-7---------
	private String reportUser;
	private String groupid1;
	private String report;
	
	//-----------修改用户组名称 jingq 2014.4.28---------------
	private String oldname;
	private String newname;
	private ArrayList namelist = new ArrayList();	//查询出的用户名集合
	
	private String usrlist;	//A0101集合

	public String getUsrlist() {
		return usrlist;
	}

	public void setUsrlist(String usrlist) {
		this.usrlist = usrlist;
	}

	public ArrayList getNamelist() {
		return namelist;
	}

	public void setNamelist(ArrayList namelist) {
		this.namelist = namelist;
	}

	public String getOldname() {
		return oldname;
	}


	public void setOldname(String oldname) {
		this.oldname = oldname;
	}


	public String getNewname() {
		return newname;
	}


	public void setNewname(String newname) {
		this.newname = newname;
	}


	public void setUserinfo(UserInfo userinfo) {
		if(userinfo==null)
			this.userinfo=new UserInfo();
		else
			this.userinfo=userinfo;
	}


	public RecordVo getUser_vo() {
		return user_vo;
	}


	public void setUser_vo(RecordVo user_vo) {
		if(user_vo==null)
			user_vo=new RecordVo("operuser");
		this.user_vo = user_vo;
	}


	public String getUserTree() {
		return userTree;
	}


	public void setUserTree(String userTree) {
		this.userTree = userTree;
	}


	public String getReportUser() {
		return reportUser;
	}


	public void setReportUser(String reportUser) {
		this.reportUser = reportUser;
	}


	public String getGroupid1() {
		return groupid1;
	}


	public void setGroupid1(String groupid1) {
		this.groupid1 = groupid1;
	}


	public String getReport() {
		return report;
	}


	public void setReport(String report) {
		this.report = report;
	}


	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		if(this.user_vo!=null)
			this.user_vo.clearValues();
		this.userinfo.setA0100("");
		this.userinfo.setB0110("");
		this.userinfo.setE0122("");
		this.userinfo.setE01a1("");
		this.userinfo.setName("");
		this.userinfo.setNbase("");
		this.checkvalue="";
		this.checktitle="";
	}


	@Override
    public void outPutFormHM() {
		this.setUserTree((String)this.getFormHM().get("usertree"));
		this.setUser_vo((RecordVo)this.getFormHM().get("user_vo"));
		this.setUserinfo((UserInfo)this.getFormHM().get("userinfo"));
		this.setTreeSelectType((String)this.getFormHM().get("treeselecttype"));
		this.setBusi_org_dept1((String)this.getFormHM().get("busi_org_dept1"));
		this.getFormHM().remove("busi_org_dept1");
		this.setBusi_org_dept1view((String)this.getFormHM().get("busi_org_dept1view"));
		this.getFormHM().remove("busi_org_dept1view");
		this.setBusi_org_dept2((String)this.getFormHM().get("busi_org_dept2"));
		this.getFormHM().remove("busi_org_dept2");
		this.setBusi_org_dept2view((String)this.getFormHM().get("busi_org_dept2view"));
		this.getFormHM().remove("busi_org_dept2view");
		this.setBusi_org_dept3((String)this.getFormHM().get("busi_org_dept3"));
		this.getFormHM().remove("busi_org_dept3");
		this.setBusi_org_dept3view((String)this.getFormHM().get("busi_org_dept3view"));
		this.getFormHM().remove("busi_org_dept3view");
		this.setBusi_org_dept4((String)this.getFormHM().get("busi_org_dept4"));
		this.getFormHM().remove("busi_org_dept4");
		this.setBusi_org_dept4view((String)this.getFormHM().get("busi_org_dept4view"));
		this.getFormHM().remove("busi_org_dept4view");
		this.setCheckvalue((String)this.getFormHM().get("checkvalue"));
		this.setChecktitle((String)this.getFormHM().get("checktitle"));
		this.setReportUser((String) this.getFormHM().get("reportUser"));
		this.setGroupid1((String) this.getFormHM().get("groupid1"));
		this.setReport((String) this.getFormHM().get("report"));
		//修改用户组所需要的参数
		this.setOldname((String) this.getFormHM().get("oldname"));
		this.setNewname((String) this.getFormHM().get("newname"));
		this.setNamelist((ArrayList) this.getFormHM().get("namelist"));
		//A0101集合  判断树中节点显示的是否为A0101是需要
		this.setUsrlist((String) this.getFormHM().get("usrlist"));
	}


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("username",this.getUser_name());
		this.getFormHM().put("busi_org_dept1", busi_org_dept1);
		this.getFormHM().put("busi_org_dept2", busi_org_dept2);
		this.getFormHM().put("busi_org_dept3", busi_org_dept3);
		this.getFormHM().put("busi_org_dept4", busi_org_dept4);
		this.getFormHM().put("reportUser", reportUser);
		this.getFormHM().put("groupid1", groupid1);
		this.getFormHM().put("report", report);
		
		this.getFormHM().put("oldname", this.getOldname());
		this.getFormHM().put("newname", this.getNewname());
		this.getFormHM().put("namelist", this.getNamelist());
		
		this.getFormHM().put("usrlist", this.getUsrlist());
	}


	public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}


	public String getFlag() {
		return flag;
	}


	public void setFlag(String flag) {
		this.flag = flag;
	}


	public String getSelecttype() {
		return selecttype;
	}


	public void setSelecttype(String selecttype) {
		this.selecttype = selecttype;
	}


	public String getDbtype() {
		return dbtype;
	}


	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}


	public String getIsfilter() {
		return isfilter;
	}


	public void setIsfilter(String isfilter) {
		this.isfilter = isfilter;
	}


	public String getTreeSelectType() {
		return treeSelectType;
	}


	public void setTreeSelectType(String treeSelectType) {
		this.treeSelectType = treeSelectType;
	}


	public String getLoadtype() {
		return loadtype;
	}


	public void setLoadtype(String loadtype) {
		this.loadtype = loadtype;
	}


	public String getBusi_org_dept1() {
		return busi_org_dept1;
	}


	public void setBusi_org_dept1(String busi_org_dept1) {
		this.busi_org_dept1 = busi_org_dept1;
	}


	public String getBusi_org_dept2() {
		return busi_org_dept2;
	}


	public void setBusi_org_dept2(String busi_org_dept2) {
		this.busi_org_dept2 = busi_org_dept2;
	}


	public String getBusi_org_dept3() {
		return busi_org_dept3;
	}


	public void setBusi_org_dept3(String busi_org_dept3) {
		this.busi_org_dept3 = busi_org_dept3;
	}


	public String getBusi_org_dept1view() {
		return busi_org_dept1view;
	}


	public void setBusi_org_dept1view(String busi_org_dept1view) {
		this.busi_org_dept1view = busi_org_dept1view;
	}


	public String getBusi_org_dept2view() {
		return busi_org_dept2view;
	}


	public void setBusi_org_dept2view(String busi_org_dept2view) {
		this.busi_org_dept2view = busi_org_dept2view;
	}


	public String getBusi_org_dept3view() {
		return busi_org_dept3view;
	}


	public void setBusi_org_dept3view(String busi_org_dept3view) {
		this.busi_org_dept3view = busi_org_dept3view;
	}


	public String getBusi_org_dept4() {
		return busi_org_dept4;
	}


	public void setBusi_org_dept4(String busi_org_dept4) {
		this.busi_org_dept4 = busi_org_dept4;
	}


	public String getBusi_org_dept4view() {
		return busi_org_dept4view;
	}


	public void setBusi_org_dept4view(String busi_org_dept4view) {
		this.busi_org_dept4view = busi_org_dept4view;
	}


	public String getBusi_org_dept5() {
		return busi_org_dept5;
	}


	public void setBusi_org_dept5(String busi_org_dept5) {
		this.busi_org_dept5 = busi_org_dept5;
	}


	public String getBusi_org_dept5view() {
		return busi_org_dept5view;
	}


	public void setBusi_org_dept5view(String busi_org_dept5view) {
		this.busi_org_dept5view = busi_org_dept5view;
	}


	public String getBusi_org_dept6() {
		return busi_org_dept6;
	}


	public void setBusi_org_dept6(String busi_org_dept6) {
		this.busi_org_dept6 = busi_org_dept6;
	}


	public String getBusi_org_dept6view() {
		return busi_org_dept6view;
	}


	public void setBusi_org_dept6view(String busi_org_dept6view) {
		this.busi_org_dept6view = busi_org_dept6view;
	}


	public String getBusi_org_dept7() {
		return busi_org_dept7;
	}


	public void setBusi_org_dept7(String busi_org_dept7) {
		this.busi_org_dept7 = busi_org_dept7;
	}


	public String getBusi_org_dept7view() {
		return busi_org_dept7view;
	}


	public void setBusi_org_dept7view(String busi_org_dept7view) {
		this.busi_org_dept7view = busi_org_dept7view;
	}


	public String getBusi_org_dept8() {
		return busi_org_dept8;
	}


	public void setBusi_org_dept8(String busi_org_dept8) {
		this.busi_org_dept8 = busi_org_dept8;
	}


	public String getBusi_org_dept8view() {
		return busi_org_dept8view;
	}


	public void setBusi_org_dept8view(String busi_org_dept8view) {
		this.busi_org_dept8view = busi_org_dept8view;
	}


	public String getCheckvalue() {
		return checkvalue;
	}

	public void setCheckvalue(String checkvalue) {
		this.checkvalue = checkvalue;
	}

	public void setChecktitle(String checktitle) {
		this.checktitle = checktitle;
	}
	
	public String getChecktitle() {
		return checktitle;
	}


	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1){
		//错误验证，操作失败时点击返回弹出框关闭		jingq add 2014.6.13
        if("/system/logonuser/add_edit_user".equals(arg0.getPath())&& arg1.getParameter("b_query")!=null){
        	arg1.setAttribute("targetWindow", "1");
        }	
        return super.validate(arg0, arg1);
   }
	
}
