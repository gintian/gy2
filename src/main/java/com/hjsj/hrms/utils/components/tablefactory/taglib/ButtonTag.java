package com.hjsj.hrms.utils.components.tablefactory.taglib;

import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import java.util.ArrayList;

/**
 * 
 * @author guodd
 * @Description:表格控件工具栏按钮标签。
 * @date 2015-3-23
 * 
 */
public class ButtonTag extends BodyTagSupport{

	String buttonsProperty;
	
	public int doEndTag() throws JspException {
		TableFactoryTag t = getParentTag(getParent());
		
		if(t==null)
			return EVAL_BODY_BUFFERED;
		
		String formName = t.getFormName();
		ArrayList buttons = (ArrayList)TagUtils.getInstance().lookup(this.pageContext,formName,this.buttonsProperty,null);
		t.setButtons(buttons);
		return EVAL_BODY_BUFFERED;
	}
	
	
	private TableFactoryTag getParentTag(Tag currobj){
	  
		TableFactoryTag parentobj = null;
	    if ((currobj == null) || ((currobj instanceof TableFactoryTag))) {
	      return (TableFactoryTag)currobj;
	    }

	    Tag p = currobj.getParent();
	    parentobj = getParentTag(p);

	    return parentobj;
	}




	public void setButtonsProperty(String buttonsProperty) {
		this.buttonsProperty = buttonsProperty;
	}
	
	
	
}
