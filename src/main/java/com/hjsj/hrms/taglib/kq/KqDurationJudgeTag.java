package com.hjsj.hrms.taglib.kq;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 判断开始时间是否在封存期间内，
 * @author Administrator
 *
 */
public class KqDurationJudgeTag extends BodyTagSupport{

	private String startDate;
	private String endDate;
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public int doEndTag() throws JspException {
		return this.EVAL_PAGE;
	}

	
	public int doStartTag() throws JspException {
		boolean flag =false;
		if(startDate!=null && startDate.length() > 0)
			flag = KqUtilsClass.comparentWithKqDuration(startDate);
		if (endDate != null && endDate.length() > 0) {
			flag = flag && KqUtilsClass.comparentWithKqDuration(endDate);
		}
		if (flag) {
			return this.EVAL_BODY_INCLUDE;
		} else {
			return this.SKIP_BODY;
		}
	}
	
}
