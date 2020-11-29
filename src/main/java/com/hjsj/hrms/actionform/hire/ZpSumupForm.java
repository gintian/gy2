/*
 * Created on 2005-9-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>Title:ZpSumupForm</p>
 * <p>Description:招聘总结表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class ZpSumupForm extends FrameForm {

	/**
	 * 招聘活动对象
	 */
	private RecordVo zpSumupvo = new RecordVo("ZP_JOB");

	/**
	 * 招聘活动对象列表
	 */
	private PaginationForm zpSumupForm = new PaginationForm();
	/**
	 * 招聘活动明细对象
	 */
	private RecordVo zpSumupDetailsvo = new RecordVo("ZP_JOB_DETAILS");

	/**
	 *招聘活动明细对象列表
	 */
	private PaginationForm zpSumupDetailsForm = new PaginationForm();
	
	private String plan_id_value = "";
	
	private String strSql = "";
	
	private String resource_id_name = ""; 
	
	private ArrayList infoList = null;
	
	@Override
    public void outPutFormHM() {
		this.setZpSumupvo((RecordVo) this.getFormHM().get("zpSumupvo"));
		this.getZpSumupForm().setList(
				(ArrayList) this.getFormHM().get("zpSumuplist"));
		this.setZpSumupDetailsvo((RecordVo) this.getFormHM().get("zpSumupDetailsvo"));
        this.getZpSumupDetailsForm().setList((ArrayList) this.getFormHM().get("zpSumupDetailslist"));
        this.setStrSql((String) this.getFormHM().get("strSql"));
        this.setResource_id_name((String)this.getFormHM().get("resource_id_name"));
        this.setInfoList((ArrayList)this.getFormHM().get("infoList"));
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("zpSumupvo", this.getZpSumupvo());
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getZpSumupDetailsForm().getList());
		this.getFormHM().put("zpSumupDetailsvo", this.getZpSumupDetailsvo());
		this.getFormHM().put("strSql","select * from zp_job where 0 > ?");
		this.getFormHM().put("infoList",this.getInfoList());
		this.getFormHM().put("plan_id_value",this.getPlan_id_value());
	}
	
	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1)
    {
    	
        super.reset(arg0, arg1);
        
    }
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		/**
		 * 判断保存按钮保存提交数据
		 *  
		 */
		if ("/hire/zp_sumup/sum_up".equals(arg0.getPath())
				&& arg1.getParameter("b_save") != null) {				
			doSelectItem(arg1);		
		}		
		return super.validate(arg0, arg1);
	}
	/**
	 * 处理值
	 * @param request
	 */
	public void doSelectItem(HttpServletRequest request)
	{
		Map mp =  request.getParameterMap();
		Set sk = mp.keySet();
		HashMap hmSave = new HashMap();
		Iterator iterator = sk.iterator();
		ArrayList list=new ArrayList();
		while (iterator.hasNext()) {

			String typeKey = iterator.next().toString();
			String typeValue = mp.get(typeKey).toString();
			if(typeKey.length()>1  && typeKey.startsWith("txt"))
			{
				HashMap wf=new HashMap();
				wf.put("typeKey",typeKey.substring(3,typeKey.length()));
				wf.put("typeValue",request.getParameter(typeKey));
				list.add(wf);
			}
		}
		this.getFormHM().put("SelectItemSave",list);
	}

	/**
	 * @return Returns the zpSumupDetailsForm.
	 */
	public PaginationForm getZpSumupDetailsForm() {
		return zpSumupDetailsForm;
	}
	/**
	 * @param zpSumupDetailsForm The zpSumupDetailsForm to set.
	 */
	public void setZpSumupDetailsForm(PaginationForm zpSumupDetailsForm) {
		this.zpSumupDetailsForm = zpSumupDetailsForm;
	}
	/**
	 * @return Returns the zpSumupDetailsvo.
	 */
	public RecordVo getZpSumupDetailsvo() {
		return zpSumupDetailsvo;
	}
	/**
	 * @param zpSumupDetailsvo The zpSumupDetailsvo to set.
	 */
	public void setZpSumupDetailsvo(RecordVo zpSumupDetailsvo) {
		this.zpSumupDetailsvo = zpSumupDetailsvo;
	}
	/**
	 * @return Returns the zpSumupForm.
	 */
	public PaginationForm getZpSumupForm() {
		return zpSumupForm;
	}
	/**
	 * @param zpSumupForm The zpSumupForm to set.
	 */
	public void setZpSumupForm(PaginationForm zpSumupForm) {
		this.zpSumupForm = zpSumupForm;
	}
	/**
	 * @return Returns the zpSumupvo.
	 */
	public RecordVo getZpSumupvo() {
		return zpSumupvo;
	}
	/**
	 * @param zpSumupvo The zpSumupvo to set.
	 */
	public void setZpSumupvo(RecordVo zpSumupvo) {
		this.zpSumupvo = zpSumupvo;
	}
	/**
	 * @return Returns the strSql.
	 */
	public String getStrSql() {
		return strSql;
	}
	/**
	 * @param strSql The strSql to set.
	 */
	public void setStrSql(String strSql) {
		this.strSql = strSql;
	}
	/**
	 * @return Returns the infoList.
	 */
	public ArrayList getInfoList() {
		return infoList;
	}
	/**
	 * @param infoList The infoList to set.
	 */
	public void setInfoList(ArrayList infoList) {
		this.infoList = infoList;
	}
	/**
	 * @return Returns the plan_id_value.
	 */
	public String getPlan_id_value() {
		return plan_id_value;
	}
	/**
	 * @param plan_id_value The plan_id_value to set.
	 */
	public void setPlan_id_value(String plan_id_value) {
		this.plan_id_value = plan_id_value;
	}
	/**
	 * @return Returns the resource_id_name.
	 */
	public String getResource_id_name() {
		return resource_id_name;
	}
	/**
	 * @param resource_id_name The resource_id_name to set.
	 */
	public void setResource_id_name(String resource_id_name) {
		this.resource_id_name = resource_id_name;
	}
}
