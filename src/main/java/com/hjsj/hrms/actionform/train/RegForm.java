package com.hjsj.hrms.actionform.train;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title:学员报名Form</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-18:16:00:39</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class RegForm extends FrameForm {

	/**
	 * 活动编号
	 */
	String movementNum="0";
	String flag="0";
	    
    String userDepartNum="0"; 
    
    /**
     * 
     * 提交用名验证标识
     */
	String moveFlag="0";

	/** 
	 * 用户单位编码
	 */
	
	public void setUserDepartNum(String userDepartNum)
	{
		this.userDepartNum=userDepartNum;
	}
	public String getUserDepartNum()
	{
		
		return this.userDepartNum;
	}
	
	public void setFlag(String flag)
	{
		this.flag=flag;
	}
	
	public String getFlag()
	{
		return this.flag;
	}
	public String getMoveFlag()
	{
		return this.moveFlag;
	}
	
	public void setMoveFlag(String moveFlag)
	{
		this.moveFlag=moveFlag;
	}
	
	/* 
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		
		this.setFlag(this.getFormHM().get("flag").toString());
		this.setMoveFlag(this.getFormHM().get("moveFlag").toString());
		this.setUserDepartNum(this.getFormHM().get("depid").toString());
		
		
	}
	
	@Override
    public void inPutTransHM() {
		
		this.getFormHM().put("movementNum",this.getMovementNum());
		this.getFormHM().put("flag",this.getFlag());
		this.getFormHM().put("moveFlag",this.getMoveFlag());
	}
	
	/**
	 * 
	 * 活动编号属性
	 */
	public String getMovementNum()
	{
		return this.movementNum;
	}
	
	public void setMovementNum(String movementNum)
	{
		this.movementNum=movementNum;
	}
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 {
	 	String userdepid="0";
	 	  if("/selfservice/educate/edulesson/reg".equals(arg0.getPath()) && arg1.getParameter("b_query")!=null)
	        {
	        	  this.setFlag("0");
	        	  this.setMoveFlag("0");
	                	
	        }
		
	 	 return super.validate(arg0, arg1);
	 }
}
