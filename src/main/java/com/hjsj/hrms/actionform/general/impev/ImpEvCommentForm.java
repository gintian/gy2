/**
 * 
 */
package com.hjsj.hrms.actionform.general.impev;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:ImpEvCommentForm
 * </p>
 * <p>
 * Description:重要信息审阅
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class ImpEvCommentForm extends FrameForm {

	private List fieldlist = new ArrayList();
	private String a_code; 
	private String p0600;
	private String content;
	private String flag;
	
	private String a0100;

	/**
	 * 
	 */
	public ImpEvCommentForm() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("p0600", this.getP0600());
		this.getFormHM().put("content", this.getContent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */

	@Override
    public void outPutFormHM() {
		this.setFieldlist((List) this.getFormHM().get("fieldlist"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setP0600((String)this.getFormHM().get("p0600"));
		this.setFlag((String)this.getFormHM().get("flag"));
	}

	public List getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(List fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getP0600() {
		return p0600;
	}

	public void setP0600(String p0600) {
		this.p0600 = p0600;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

}
