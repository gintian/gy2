/**
 * 
 */
package com.hjsj.hrms.actionform.sys;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 24, 20061:42:13 PM
 * @author chenmengqing
 * @version 4.0
 */
public class ResourceForm extends FrameForm {
	/**对象类型*/
	private String flag;
	/**对象号*/
	private String roleid;
	/**对象名*/
	private String role_name;

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	/**资源类型*/
	private String res_flag;
    /**当前页*/
    private int current=1;
    /**规章制度目录号,资源串序列，包括规章制度、模板等*/
	private String law_dir;
	/**弹转来源
	 *0:用户管理
	 *1：角色管理
	 *2：帐号分配 
	 */
	private String fromflag="0";
	/**模板树*/
	private String template_tree;
	/**模板类型*/
	/**业务类型
	 * =1,日常管理
	 * =2,工资管理
	 * =3,警衔管理
	 * =8,保险管理
	 */	
	private String type;
	/**模板类型*/
	
    private PaginationForm reslistform=new PaginationForm();
    /**
     * 操作成功标识    jingq   add   2014.5.7
     */
    private String opt;
	/**
	 * 查询参数工资和保险名称和编号 hej add 2015.7.15
	 */
	private String searchparambx;
	private String searchparamgz;	
	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getRes_flag() {
		return res_flag;
	}

	public void setRes_flag(String res_flag) {
		this.res_flag = res_flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	@Override
    public void outPutFormHM() {

        this.getReslistform().setList((ArrayList)this.getFormHM().get("list"));
	    /**重新定位到当前页*/
	    this.getReslistform().getPagination().gotoPage(current);	
	    this.setLaw_dir((String)this.getFormHM().get("law_dir"));
	    this.setTemplate_tree((String)this.getFormHM().get("bs_tree"));
	    
	    this.setOpt((String) this.getFormHM().get("opt"));
	    this.setSearchparambx((String)this.getFormHM().get("searchparambx"));
	    this.setSearchparamgz((String)this.getFormHM().get("searchparamgz"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("roleid",this.getRoleid());
		this.getFormHM().put("role_name",this.getRole_name());
		this.getFormHM().put("res_flag",this.getRes_flag());
        this.getFormHM().put("alllist",this.getReslistform().getAllList());		
        this.getFormHM().put("type",this.getType());
        
        this.getFormHM().put("opt", this.getOpt());
        this.getFormHM().put("searchparambx", this.getSearchparambx());
        this.getFormHM().put("searchparamgz", this.getSearchparamgz());
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if(reslistform.getPagination()!=null)
        {            
        	current=reslistform.getPagination().getCurrent();    
        }	
        /**
         * 每次请求清空opt  jingq   add   2014.5.7
         */
        if(opt!=null){
        	this.setOpt("");
        }
        //【7184】系统管理-权限管理-账号分配-资源分配-常用统计（会出现一进去就是第二页的情况） jingq add 2015.01.30
        if("/system/security/open_resource".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
        	current = 1;
        }
		return super.validate(arg0, arg1);
	}

	public PaginationForm getReslistform() {
		return reslistform;
	}

	public void setReslistform(PaginationForm reslistform) {
		this.reslistform = reslistform;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        DynaBean dbean=null;		
 	    if(this.getReslistform().getPagination()!=null)
 	    {
	    	ArrayList list=this.getReslistform().getPagination().getCurr_page_list(); 	  
	    	if(list!=null)
	    	{
	    		for(int i=0;i<list.size();i++)
	    		{
	    			dbean=(LazyDynaBean)list.get(i); 	    		
	    			dbean.set("c0","0");
	    		}
	    	}
 	    }		
 	    this.setFromflag("0");
		super.reset(arg0, arg1);
	}

	public String getLaw_dir() {
		return law_dir;
	}

	public void setLaw_dir(String law_dir) {
		this.law_dir = law_dir;
	}


	public String getTemplate_tree() {
		return template_tree;
	}

	public void setTemplate_tree(String template_tree) {
		this.template_tree = template_tree;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromflag() {
		return fromflag;
	}

	public void setFromflag(String fromflag) {
		this.fromflag = fromflag;
	}

	public String getSearchparambx() {
		return searchparambx;
	}

	public void setSearchparambx(String searchparambx) {
		this.searchparambx = searchparambx;
	}

	public String getSearchparamgz() {
		return searchparamgz;
	}

	public void setSearchparamgz(String searchparamgz) {
		this.searchparamgz = searchparamgz;
	}
}
