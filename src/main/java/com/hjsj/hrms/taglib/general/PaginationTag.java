/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.valueobject.Pagination;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * <p>Title:</p>
 * <p>Description:输入分页标签  第x页　共x条　共x页 每页x条　刷新</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 23, 2008:5:43:45 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PaginationTag extends BodyTagSupport {
	/**FrameForm实例名*/
    protected String name;
    /**分页类实例*/
    protected String property;
    /**每页的记录数*/
    private int pagerows;
    
    protected String scope;
    /**重新设置每页行数*/
    private boolean refresh=false;
    
	public int doEndTag() throws JspException {
        Pagination pagination = (Pagination) TagUtils.getInstance().lookup(pageContext, property, scope);
        if(pagination==null)
        	pagination = (Pagination) TagUtils.getInstance().lookup(pageContext, name, property, scope);
        
        String url = "this.document." + name + ".submit()";
        
        StringBuffer buf=new StringBuffer();
        buf.append(ResourceFactory.getProperty("label.page.serial"));
        buf.append("&nbsp;");          
        buf.append(pagination.getCurrent());
        buf.append("&nbsp;");          
        buf.append(ResourceFactory.getProperty("label.page.sum"));
        buf.append("&nbsp;");          
        buf.append(pagination.getCount());
        buf.append("&nbsp;");          
        buf.append(ResourceFactory.getProperty("label.page.row"));
        buf.append("&nbsp;");          
        buf.append(pagination.getPages());
        buf.append("&nbsp;");          
        buf.append(ResourceFactory.getProperty("label.page.page"));
        buf.append("&nbsp;");    
        /**重新设置每页的记录条数*/
        if(isRefresh())
        {
	        buf.append(ResourceFactory.getProperty("label.every.page"));
	        buf.append("&nbsp<input type='text' size='4' name='pagerows' ");
	        buf.append(" value='");
	        buf.append(pagerows);
	        //【55673】分页标签可以通过复制粘帖的方式输入负数，因此添加onchange事件，校验输入的数字是否是正整数
	        buf.append("' onkeypress='checkNumber(this,event)' onChange='checkIsPositiveNum(this)' class='TEXT4'>&nbsp");
	        buf.append("<a href=\"javascript:" + url);
	        buf.append("\">");
	        buf.append(ResourceFactory.getProperty("label.page.refresh"));
	        buf.append(" </a>&nbsp");
        }
       
        TagUtils.getInstance().write(pageContext, buf.toString());
		return super.doEndTag();
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}


	public int getPagerows() {
		return pagerows;
	}


	public void setPagerows(int pagerows) {
		this.pagerows = pagerows;
	}


	public boolean isRefresh() {
		return refresh;
	}


	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

}
