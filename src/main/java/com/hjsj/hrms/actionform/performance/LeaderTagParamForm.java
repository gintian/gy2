/*
 * 创建日期 2005-7-4
 *
 */
package com.hjsj.hrms.actionform.performance;

import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author luangaojiong
 * 部门领导浏览员工考核结果
 */
public class LeaderTagParamForm extends FrameForm {

	CardTagParamView cardparam=new CardTagParamView();
	private String treeCode="";
	private String strsql="";
	private String cond_str="";
	private String a0100="";
	private String userbase="Usr";
	private String code="";
	private String kind="";
	private String flag="";
	private String objectId="0";
	
	private String columns="";
	private ArrayList columnsList=new ArrayList();
	
	/**
	 * @return 返回 dbcond。
	 */
	public String getDbcond() {
		return dbcond;
	}
	/**
	 * @param dbcond 要设置的 dbcond。
	 */
	public void setDbcond(String dbcond) {
		this.dbcond = dbcond;
	}
    private String dbcond;
	/**
	 * 用户对象Id
	 * @return
	 */
	public void setObject(String objectId)
	{
		this.objectId=objectId;
	}
	public String getObject()
	{
		return this.objectId;
	}
	/**
	 * @return Returns the flag.
	 */
	public String getFlag() {
		return flag;
	}
	/**
	 * @param flag The flag to set.
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return Returns the kind.
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @param kind The kind to set.
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	/**
	 * @return Returns the userbase.
	 */
	public String getUserbase() {
		return userbase;
	}
	/**
	 * @param userbase The userbase to set.
	 */
	public void setUserbase(String userbase) {
		this.userbase = userbase;
	}
	/**
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
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
	 * @return Returns the strsql.
	 */
	public String getStrsql() {
		return strsql;
	}
	/**
	 * @param strsql The strsql to set.
	 */
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	/**
	 * @return Returns the treeCode.
	 */
	public String getTreeCode() {
		return treeCode;
	}
	/**
	 * @param treeCode The treeCode to set.
	 */
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	/* 
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setColumnsList((ArrayList)this.getFormHM().get("columnsList"));
		
		// TODO Auto-generated method stub	
		this.setTreeCode((String)this.getFormHM().get("treeCode"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setCond_str((String)this.getFormHM().get("cond_str"));
		this.setKind((String)this.getFormHM().get("kind"));
		this.setCode((String)this.getFormHM().get("code"));
		this.setObject(this.getFormHM().get("objectId").toString());
	    this.setDbcond((String)this.getFormHM().get("dbcond"));
	}

	/* 
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub	
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("objectId",this.getObject());
	}

	/**
	 * @return Returns the cardparam.
	 */
	public CardTagParamView getCardparam() {
		return cardparam;
	}
	/**
	 * @param cardparam The cardparam to set.
	 */
	public void setCardparam(CardTagParamView cardparam) {
		this.cardparam = cardparam;
	}
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	   {
	        if("/selfservice/performance/leaderexamine/showinfodata".equals(arg0.getPath()) && arg1.getParameter("b_search")!=null)
	        {
	            if(this.getPagination()!=null)
	              this.getPagination().firstPage();
	        }	
	      
            return super.validate(arg0, arg1);
	    }
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public ArrayList getColumnsList() {
		return columnsList;
	}
	public void setColumnsList(ArrayList columnsList) {
		this.columnsList = columnsList;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
}
