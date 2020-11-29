package com.hjsj.hrms.actionform.sys;

import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.action.FrameForm;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 4, 2005:4:04:02 PM
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 *  
 */
public class AboutHrpForm extends FrameForm {
	/**
	 * test legend
	 */
	private ArrayList list = new ArrayList();
	/**业务平台用户*/
	private ArrayList fieldlist=new ArrayList();
	private String strsql;
	/**自助平台用户*/
	private ArrayList fieldlist_e=new ArrayList();
	private String strsql_e;
	/**条件*/
	private String strwhere;
	/**需要显示字段列表*/
	private String columns;
	
	private String flag="0";
	/**授权模块显示控制界面*/
	private String e_module="true";
	private String p_module="true";
	
	/**产品号*/
	private String productno;
	/**平台标识
	 * =1业务平台
	 * =2自助平台，根据登录进入点，区分
	 * */
	private String status;
	
	/**子集集合*/
	private List setList;
	/**默认选中的子集*/
	private String fieldSetId;
	/**子集描述*/
	private String fieldSetDesc;
	/**人员库集合*/
	private List preList;
	/**人员库*/
	private String pre;
	
	private String t_flag;//培训计划用
	
	
	public String getPre() {
		return pre;
	}

	public void setPre(String pre) {
		this.pre = pre;
	}

	public List getPreList() {
		return preList;
	}

	public void setPreList(List preList) {
		this.preList = preList;
	}

	public List getSetList() {
		return setList;
	}

	public void setSetList(List setList) {
		this.setList = setList;
	}

	public String getFieldSetId() {
		return fieldSetId;
	}

	public void setFieldSetId(String fieldSetId) {
		this.fieldSetId = fieldSetId;
	}

	public String getFieldSetDesc() {
		return fieldSetDesc;
	}

	public void setFieldSetDesc(String fieldSetDesc) {
		this.fieldSetDesc = fieldSetDesc;
	}

	public String getE_module() {
		return e_module;
	}

	public void setE_module(String e_module) {
		this.e_module = e_module;
	}

	public String getP_module() {
		return p_module;
	}

	public void setP_module(String p_module) {
		this.p_module = p_module;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getStrsql_e() {
		return strsql_e;
	}

	public void setStrsql_e(String strsql_e) {
		this.strsql_e = strsql_e;
	}

	public ArrayList getFieldlist_e() {
		return fieldlist_e;
	}

	public void setFieldlist_e(ArrayList fieldlist_e) {
		this.fieldlist_e = fieldlist_e;
	}

	/**
	 *  
	 */
	public AboutHrpForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		this.setList((ArrayList) this.getFormHM().get("list"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setStrsql_e((String)this.getFormHM().get("strsql_e"));
		this.setStrwhere((String)this.getFormHM().get("strwhere"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setProductno((String)this.getFormHM().get("productno"));
		
		this.setSetList((List) this.getFormHM().get("setList"));
		this.setFieldSetId((String) this.getFormHM().get("fieldSetId"));
		this.setFieldSetDesc((String) this.getFormHM().get("fieldSetDesc"));
		this.setPreList((List) this.getFormHM().get("preList"));
		this.setT_flag((String)this.getFormHM().get("t_flag"));
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("flag",this.getFlag());
 	    if(this.getPagination()!=null)        
 	          this.getFormHM().put("list",this.getPagination().getAllList());
	}
	/**重新设置*/
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		FieldItem item=null;
		String fieldname=null;
        DynaBean dbean=null;		
 	    if(this.getPagination()!=null)
 	    {
	    	ArrayList list=this.getPagination().getCurr_page_list(); 	    	
 	    	for(int i=0;i<list.size();i++)
 	    	{
				dbean=(LazyDynaBean)list.get(i); 	    		
 	    		for(int j=0;j<this.fieldlist.size();j++)
 	    		{
					item=(FieldItem)fieldlist.get(j);
					fieldname=item.getItemid();
					dbean.set(fieldname,"0");
 	    		}
 	    	}
 	    }
		super.reset(arg0, arg1);
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if("/templates/banner/banner_employee".equals(arg0.getPath())&&arg1.getParameter("b_exit.y")!=null)
        {
        	arg1.getSession().invalidate();
        }
        if("/system/security/about_hrp".equals(arg0.getPath())&&arg1.getParameter("b_priv")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        } 
        if("/system/security/operuser_module".equals(arg0.getPath()))
        {
        	this.setFlag("2");
        }   
        if("/system/security/employ_module".equals(arg0.getPath()))
        {
        	this.setFlag("1");
        }      
        /**加密锁*/
        this.getFormHM().put("lock",arg1.getSession().getServletContext().getAttribute("lock"));
		return super.validate(arg0, arg1);
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getStrsql() {
		return strsql;
	}

	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProductno() {
		return productno;
	}

	public void setProductno(String productno) {
		this.productno = productno;
	}

	public String getT_flag() {
		return t_flag;
	}

	public void setT_flag(String t_flag) {
		this.t_flag = t_flag;
	}
}