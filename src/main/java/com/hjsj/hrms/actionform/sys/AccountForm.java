package com.hjsj.hrms.actionform.sys;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 13, 2005:9:31:18 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class AccountForm extends FrameForm {
	
	/**查询条件串*/
    private String cond_str="";
    /**查询串*/
    private String sql_str;
    /**需显示的指标串*/
    private String columns;
    private String dbpre="";    
    private ArrayList dblist=new ArrayList();
    /**生成随机密码的账户列表*/
    private ArrayList accountlist=new ArrayList();
    private PaginationForm accountlistForm=new PaginationForm();
    /**员工自助的表名*/
    private String tablename="UsrA01";
    /**控制返回按钮*/
    private String ret_ctrl="0";
    private String ip_addr;
    /**部门显示层数*/
    private String uplevel="0";
    
    private String lockfield;   
    private String factor;
	private String expr;
    private String history;
    private String likeflag;
    
    /**重复用户排除yang：j2013-10-31*/
    // 重复角色验证时的下拉列表
    private List repeatList = new ArrayList();
    // 重复角色的ID
    private String repeatID;
    // 重复角色验证时的表
    private String repeatTable; 
    // 查询SQL语句前部分
    private String repeatSql_str;
    // 重复角色查询条件串
    private String repeatWhere_str;
    // 重复角色需显示的指标串
    private String repeatColumns;
    // 排序字符串
    private String repeatOrder_by;
    // 重复页面是否要多显示一列 登录账号
    private String repeatFlag;    
	
	//repeatSql_str的getter和setter方法
    public String getRepeatSql_str() {
		return repeatSql_str;
	}
	public String getRepeatFlag() {
		return repeatFlag;
	}
	//searchRepeatFlag的getter和setter方法	
	public void setRepeatFlag(String repeatFlag) {
		this.repeatFlag = repeatFlag;
	}
	public void setRepeatSql_str(String repeatSql_str) {
		this.repeatSql_str = repeatSql_str;
	}
	//repeatWhere_str的getter和setter方法
	public String getRepeatWhere_str() {
		return repeatWhere_str;
	}
	public void setRepeatWhere_str(String repeatWhere_str) {
		this.repeatWhere_str = repeatWhere_str;
	}
	//repeatColumns的getter和setter方法
	public String getRepeatColumns() {
		return repeatColumns;
	}
	public void setRepeatColumns(String repeatColumns) {
		this.repeatColumns = repeatColumns;
	}
	//repeatList的getter和setter方法
    public List getRepeatList() {
		return repeatList;
	}
	public void setRepeatList(List repeatList) {
		this.repeatList = repeatList;
	}
	//repeatID的getter和setter方法
    public String getRepeatID() {
		return repeatID;
	}
	public void setRepeatID(String repeatID) {
		this.repeatID = repeatID;
	}
	//repeatTable的getter和setter方法
    public String getRepeatTable() {
		return repeatTable;
	}
	public void setRepeatTable(String repeatTable) {
		this.repeatTable = repeatTable;
	}
	//order_by的getter和setter方法
	public String getRepeatOrder_by() {
		return repeatOrder_by;
	}
	public void setRepeatOrder_by(String repeatOrder_by) {
		this.repeatOrder_by = repeatOrder_by;
	}
	public String getLockfield() {
		return lockfield;
	}
	public void setLockfield(String lockfield) {
		this.lockfield = lockfield;
	}
	public String getIp_addr() {
		return ip_addr;
	}
	public void setIp_addr(String ip_addr) {
		this.ip_addr = ip_addr;
	}
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		//this.setRet_ctrl("0");
    	this.setA0101("");
		super.reset(arg0, arg1);
	}
	public ArrayList getDblist() {
        return dblist;
    }
    public void setDblist(ArrayList dblist) {
        this.dblist = dblist;
    }
    /**登录用户对象*/
    private RecordVo user_vo=new RecordVo("UsrA01");
    /**重复用户对象*/
    private RecordVo reportUser_vo=new RecordVo("reportUser_vo");
    /**角色对象列表*/
    private PaginationForm roleListForm=new PaginationForm();
    /**用户号标识*/
    private String user_id;
    /**状态标识，用于标识返回的页面的*/
    private String status="0";
    /**登录用户指标名*/
    private String loguser;
    /**登录用名及口令的输入长度*/
    private String userlen="20";
    private String pwdlen="20";
    private String a_roleid="";
    /**姓名，按姓名快速查找定位*/
    private String a0101;
    private int maxpage=1;
    //reportuser_vo的getter和setter方法
    public RecordVo getReportUser_vo() {
		return reportUser_vo;
	}
	public void setReportUser_vo(RecordVo reportUser_vo) {
		this.reportUser_vo = reportUser_vo;
	}
	public String getA0101() {
		return a0101;
	}
	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
	public String getA_roleid() {
		return a_roleid;
	}
	public void setA_roleid(String a_roleid) {
		this.a_roleid = a_roleid;
	}
	public String getPwdlen() {
		return pwdlen;
	}
	public void setPwdlen(String pwdlen) {
		this.pwdlen = pwdlen;
	}
	public String getUserlen() {
		return userlen;
	}
	public void setUserlen(String userlen) {
		this.userlen = userlen;
	}
	public String getLoguser() {
		return loguser;
	}
	public void setLoguser(String loguser) {
		this.loguser = loguser;
	}
	public String getColumns() {
        return columns;
    }
    public void setColumns(String columns) {
        this.columns = columns;
    }
    public String getSql_str() {
        return sql_str;
    }
    public void setSql_str(String sql_str) {
        this.sql_str = sql_str;
    }    
    /**
     * @return Returns the cond_str.
     */
    public String getCond_str() {
        return cond_str;
    }
    /**
     * @param cond_str The cond_str to set.
     */
    public void setCond_str(String cond_str) {
        this.cond_str = cond_str;
    }
    /**
     * @return Returns the user_vo.
     */
    public RecordVo getUser_vo() {
        return user_vo;
    }
    /**
     * @param user_vo The user_vo to set.
     */
    public void setUser_vo(RecordVo user_vo) {
        this.user_vo = user_vo;
        
    }
    public String getDbpre() {
        return dbpre;
    }
    public void setDbpre(String dbpre) {
        this.dbpre = dbpre;
    }  
	/* 
     * @see com.hrms.struts.action.FrameForm#outPutFormHM()
     */
    @Override
    public void outPutFormHM() {
       this.setUser_vo((RecordVo)this.getFormHM().get("user_vo"));
       this.setCond_str((String)this.getFormHM().get("cond_str"));           
       this.getAccountlistForm().setList((ArrayList)this.getFormHM().get("accountlist"));
       ArrayList rolelist = (ArrayList)this.getFormHM().get("rolelist");
       this.getRoleListForm().setList(rolelist);
       if(rolelist!=null){
    	   for(int i=0;i<rolelist.size();i++){
    		   RecordVo vo = (RecordVo)rolelist.get(i);
    		   String valid = vo.getString("valid");
	           	if("1".equals(valid)){
	           		this.getRoleListForm().setSelect(i, true);
	           	}
    	   }
       }
       this.setUser_id((String)this.getFormHM().get("user_id"));
       this.setStatus((String)this.getFormHM().get("userflag"));
       this.setSql_str((String)this.getFormHM().get("sql_str"));
       this.setColumns((String)this.getFormHM().get("columns"));
       this.setDblist((ArrayList)this.getFormHM().get("dblist"));
       this.setTablename((String)this.getFormHM().get("tablename"));
       this.setLoguser((String)this.getFormHM().get("loguser"));
       this.setUserlen((String)this.getFormHM().get("userlen"));
       this.setPwdlen((String)this.getFormHM().get("pwdlen"));
       this.setAccountlist((ArrayList)this.getFormHM().get("accountlist"));
       this.setDbpre((String)this.getFormHM().get("dbpre"));
       this.setIp_addr((String)this.getFormHM().get("ip_addr"));
       this.setUplevel((String)this.getFormHM().get("uplevel"));
       this.setLockfield((String)this.getFormHM().get("lockfield"));
         
       this.setFactor("");
       this.setExpr("");
       this.setHistory("");
       this.setLikeflag("");
       
       //重复用户展示
       this.setRepeatTable((String) this.getFormHM().get("repeatTable"));
       this.setRepeatID((String) this.getFormHM().get("repeatID"));
       this.setRepeatList((List) this.getFormHM().get("repeatList"));
       this.setRepeatSql_str((String) this.getFormHM().get("repeatSql_str"));
       this.setRepeatWhere_str((String) this.getFormHM().get("repeatWhere_str"));
       this.setRepeatOrder_by((String)this.getFormHM().get("repeatOrder_by"));
       this.setRepeatColumns((String) this.getFormHM().get("repeatColumns"));
       this.setRepeatFlag((String) this.getFormHM().get("repeatFlag"));
				
		//重复用户账户展示
		this.setReportUser_vo((RecordVo) this.getFormHM().get("reportUser_vo"));
    }

    /* 
     * @see com.hrms.struts.action.FrameForm#inPutTransHM()
     */
    @Override
    public void inPutTransHM() {
    	//this.getFormHM().put("selectedlist", this.getAccountlistForm().getSelectedList());
        this.getFormHM().put("user_id",this.getUser_id());
        this.getFormHM().put("dbpre",this.getDbpre());
        this.getFormHM().put("a_roleid",this.getA_roleid());
        this.getFormHM().put("a0101",this.getA0101());
       
 	    if(this.getPagination()!=null)
 	    {
          this.getFormHM().put("selectedaccount",this.getPagination().getSelectedList());
 	    }
 	    if(this.getAccountlistForm()!=null)
 	    	this.getFormHM().put("accountlist", this.getAccountlistForm().getAllList());
        //this.setAccountlist((ArrayList)this.getFormHM().get("accountlist")); 	    
        this.getFormHM().put("ip_addr",this.getIp_addr());
        this.getFormHM().put("sql_str", this.getSql_str());
        this.getFormHM().put("cond_str", this.getCond_str());
        this.getFormHM().put("columns", this.getColumns());
        this.getFormHM().put("loguser", this.getLoguser());
        this.getFormHM().put("repeatTable", this.getRepeatTable());
        this.getFormHM().put("repeatID", this.getRepeatID());
        
        this.getFormHM().put("factor", this.factor);
        this.getFormHM().put("expr", this.expr);
        this.getFormHM().put("likeflag", this.likeflag);
        this.getFormHM().put("history", this.history);
        
    }
    
  
    /* 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {

        if("/system/security/assign_login".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            if(this.getPagination()!=null)
              this.getPagination().firstPage();//?
            this.pagerows=21;//zgd 2014-5-5 初始化每页21条数据
        }
        if(("/system/security/assign_role".equals(arg0.getPath())|| "/system/security/assign_role_org".equals(arg0.getPath()))&&(arg1.getParameter("b_query")!=null))
        {
            if(this.roleListForm.getPagination()!=null)
              this.roleListForm.getPagination().firstPage();//?
        }  
        if(("/system/security/assign_role".equals(arg0.getPath())|| "/system/security/assign_role_org".equals(arg0.getPath()))&&(arg1.getParameter("b_save")!=null||arg1.getParameter("b_o_save")!=null||arg1.getParameter("b_save_user")!=null||arg1.getParameter("b_save_org")!=null))
        {
        	ArrayList selectlist = this.getRoleListForm().getSelectedList();
           /* int pagesize=this.getPagerows();
            ArrayList alllist = this.getRoleListForm().getAllList();
            for(int n=maxpage*pagesize;n<alllist.size();n++){
            	RecordVo vo=(RecordVo)alllist.get(n);
            	String valid = vo.getString("valid");
            	if("1".equals(valid)){
            		boolean flag=true;
            		for(int i=selectlist.size()-1;i>=0;i--){
            			RecordVo tvo=(RecordVo)selectlist.get(i);
            			String role_id=tvo.getString("role_id");
            			if(role_id.equals(vo.getString("role_id"))){
            				flag=false;
            				break;
            			}
            		}
            		if(flag)
            			selectlist.add(vo);
            	}
            }*/
        	this.getFormHM().put("selectedlist",selectlist);
        	maxpage=1;
        }  
        if("/system/security/assign_org_login".equals(arg0.getPath())&&(arg1.getParameter("b_query")!=null||arg1.getParameter("b_o_save")!=null))
        {
            if(this.roleListForm.getPagination()!=null)
              this.roleListForm.getPagination().firstPage();//?
            if(this.getPagination()!=null)
                this.getPagination().firstPage();//?
        } 
   	 	if("/system/security/role_repeat".equals(arg0.getPath())&&arg1.getParameter("b_repeat")!=null){
		 if(this.getPagination()!=null)
			 this.getPagination().firstPage();
		 this.pagerows=10;
   	 	}
   	 	
   	 	if("/system/security/assign_role".equals(arg0.getPath())&&arg1.getParameter("b_o_save")!=null){
   	 		arg1.setAttribute("targetWindow", "1");
   	 	}
   	 	return super.validate(arg0, arg1);
    }
    
    public PaginationForm getRoleListForm() {
        return roleListForm;
    }
    public void setRoleListForm(PaginationForm roleListForm) {
        this.roleListForm = roleListForm;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public ArrayList getAccountlist() {
		return accountlist;
	}
	public void setAccountlist(ArrayList accountlist) {
		this.accountlist = accountlist;
	}
	public String getRet_ctrl() {
		return ret_ctrl;
	}
	public void setRet_ctrl(String ret_ctrl) {
		this.ret_ctrl = ret_ctrl;
	}
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public PaginationForm getAccountlistForm() {
		return accountlistForm;
	}
	public void setAccountlistForm(PaginationForm accountlistForm) {
		this.accountlistForm = accountlistForm;
	}
	public int getMaxpage() {
		return maxpage;
	}
	public void setMaxpage(int maxpage) {
		this.maxpage = maxpage;
	}
	
	public String getFactor() {
		return factor;
	}
	public void setFactor(String factor) {
		this.factor = factor;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	public String getHistory() {
		return history;
	}
	public void setHistory(String history) {
		this.history = history;
	}
	public String getLikeflag() {
		return likeflag;
	}
	public void setLikeflag(String likeflag) {
		this.likeflag = likeflag;
	}
}
