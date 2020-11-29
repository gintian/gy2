package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.valueobject.Pagination;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.LinkTag;
import org.apache.struts.util.MessageResources;

import javax.servlet.jsp.JspException;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 26, 2005:3:54:01 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PaginationDbLink extends LinkTag {

    private String nameId;
    private String propertyId;
    private String firstValue;
    private String nextValue;
    private String previoValue;
    private String lastValue;
    private String gotoPage1;

    protected String text;
    protected String anchor;
    protected String forward;
    protected String href;
    protected String linkName;
    protected String name;
    protected String page;
    protected String paramId;
    protected String paramName;
    protected String paramProperty;
    protected String paramScope;
    protected String property;
    protected String scope;
    protected String target;
    protected boolean transaction;  
    protected String isMobile;
    protected static MessageResources messages = MessageResources.getMessageResources(
        "org.apache.struts.taglib.html.LocalStrings");
    
    /**
     * @return Returns the firstValue.
     */
    public String getFirstValue() {
        return firstValue;
    }
    /**
     * @param firstValue The firstValue to set.
     */
    public void setFirstValue(String firstValue) {
        this.firstValue = firstValue;
    }
    /**
     * @return Returns the lastValue.
     */
    public String getLastValue() {
        return lastValue;
    }
    /**
     * @param lastValue The lastValue to set.
     */
    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }
    /**
     * @return Returns the nameId.
     */
    public String getNameId() {
        return nameId;
    }
    /**
     * @param nameId The nameId to set.
     */
    public void setNameId(String nameId) {
        this.nameId = nameId;
    }
    /**
     * @return Returns the nextValue.
     */
    public String getNextValue() {
        return nextValue;
    }
    /**
     * @param nextValue The nextValue to set.
     */
    public void setNextValue(String nextValue) {
        this.nextValue = nextValue;
    }
    /**
     * @return Returns the previoValue.
     */
    public String getPrevioValue() {
        return previoValue;
    }
    /**
     * @param previoValue The previoValue to set.
     */
    public void setPrevioValue(String previoValue) {
        this.previoValue = previoValue;
    }
    /**
     * @return Returns the propertyId.
     */
    public String getPropertyId() {
        return propertyId;
    }
    /**
     * @param propertyId The propertyId to set.
     */
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
 
    /**
     * @return Returns the anchor.
     */
    public String getAnchor() {
        return anchor;
    }
    /**
     * @param anchor The anchor to set.
     */
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }
    /**
     * @return Returns the forward.
     */
    public String getForward() {
        return forward;
    }
    /**
     * @param forward The forward to set.
     */
    public void setForward(String forward) {
        this.forward = forward;
    }
    /**
     * @return Returns the href.
     */
    public String getHref() {
        return href;
    }
    /**
     * @param href The href to set.
     */
    public void setHref(String href) {
        this.href = href;
    }
    /**
     * @return Returns the linkName.
     */
    public String getLinkName() {
        return linkName;
    }
    /**
     * @param linkName The linkName to set.
     */
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the page.
     */
    public String getPage() {
        return page;
    }
    /**
     * @param page The page to set.
     */
    public void setPage(String page) {
        this.page = page;
    }
    /**
     * @return Returns the paramId.
     */
    public String getParamId() {
        return paramId;
    }
    /**
     * @param paramId The paramId to set.
     */
    public void setParamId(String paramId) {
        this.paramId = paramId;
    }
    /**
     * @return Returns the paramName.
     */
    public String getParamName() {
        return paramName;
    }
    /**
     * @param paramName The paramName to set.
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
    /**
     * @return Returns the paramProperty.
     */
    public String getParamProperty() {
        return paramProperty;
    }
    /**
     * @param paramProperty The paramProperty to set.
     */
    public void setParamProperty(String paramProperty) {
        this.paramProperty = paramProperty;
    }
    /**
     * @return Returns the paramScope.
     */
    public String getParamScope() {
        return paramScope;
    }
    /**
     * @param paramScope The paramScope to set.
     */
    public void setParamScope(String paramScope) {
        this.paramScope = paramScope;
    }
    /**
     * @return Returns the property.
     */
    public String getProperty() {
        return property;
    }
    /**
     * @param property The property to set.
     */
    public void setProperty(String property) {
        this.property = property;
    }
    /**
     * @return Returns the scope.
     */
    public String getScope() {
        return scope;
    }
    /**
     * @param scope The scope to set.
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
    /**
     * @return Returns the target.
     */
    public String getTarget() {
        return target;
    }
    /**
     * @param target The target to set.
     */
    public void setTarget(String target) {
        this.target = target;
    }
    /**
     * @return Returns the transaction.
     */
    public boolean isTransaction() {
        return transaction;
    }
    /**
     * @param transaction The transaction to set.
     */
    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }
    /**
     * 
     */
    public PaginationDbLink() {
        firstValue = "首页";
        nextValue = "下页";
        previoValue = "上页";
        lastValue = "末页";
        gotoPage1 = "前往";
    }
    
    
    
    /* 
     * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
     */
    public int doAfterBody() throws JspException {
        if (bodyContent != null)
        {
          String value = bodyContent.getString().trim();
          if (value.length() > 0)
          {
            text = value;
          }
        }
        return 0;
    }
    /* 
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
        return 6;
    }
    /* 
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws JspException {
        String paginationActionTag = Pagination.paginationDbActionTag;
        String currentTag = Pagination.currentTag;
        //extendProcess();
        String results = "";
        if(isMobile!=null&&("1".equals(isMobile)|| "2".equals(isMobile))){
        	results+="<div data-role=\"footer\">";
        }
        Pagination pagination = (Pagination) TagUtils.getInstance().lookup(pageContext, property, scope);
        if(isMobile!=null&& "2".equals(isMobile)){
        	pagination = (Pagination) TagUtils.getInstance().lookup(pageContext,this.name, property, scope);
        }
        results = results + "<input type=\"hidden\" name=\"listpagination\" value=\"" + nameId + "\">";
        results = results + "<input type=\"hidden\" name=\"" + currentTag + "\" value=\"" + pagination.getCurrent() + "\">";
        if(isMobile!=null&& "2".equals(isMobile)){
        	results = results + "<input type=\"hidden\" name=\"" + Pagination.paginationActionTag + "\" value=\"\">";
        }else{
        	results = results + "<input type=\"hidden\" name=\"" + paginationActionTag + "\" value=\"\">";
        }
        String url = "this.document." + name + ".submit()";
        if(isMobile!=null&& "1".equals(isMobile)){//和这个<hrms:paginationdb标签使用  如：myteam.jsp
	        if (pagination.previoEnable())
	        {
	          results = results + "<a data-role=\"button\" data-icon=\"arrow-l\" href=\"javascript:this.document." + name + "." + paginationActionTag + ".value='" + Pagination.previoAction + "';"+url+"\"";

	          //results = results + prepareStyles();
	          //results = results + prepareEventHandlers();
	          results = results + "title=\""+previoValue+"\">&nbsp;</a>";
	        }
	        else
	        {
	        	
	        }
	        
	        if (pagination.nextEnable())
	        {
	          results = results + "<a data-role=\"button\" data-icon=\"arrow-r\"  href=\"javascript:this.document." + name + "." +paginationActionTag + ".value='" + Pagination.nextAction + "';"+url+"\"";
	          //results = results + prepareStyles();
	          //results = results + prepareEventHandlers();
	          results = results + "title=\""+nextValue+"\" >&nbsp;</a>";
	        }
	        else
	        {
	        }
	        results+="&nbsp;&nbsp;&nbsp;";
	        results = results +ResourceFactory.getProperty("label.page.serial");
	        results = results + "<input type=text class='text4' size='4' name='" + Pagination.pageSelect + "' value='' onkeypress='checkNumber(this,event)' class=\"ui-input-text ui-body-null ui-corner-all ui-shadow-inset ui-body-c ui-slider-input\"/>";
	        results = results + ResourceFactory.getProperty("label.page.page");
	        /**兼容其它浏览器onclik事件 webkit*/
	        results = results + "&nbsp<a data-role=\"button\" data-icon=\"refresh\" href=\"javascript:if(this.document." + name + "." +Pagination.pageSelect + ".value==''){ alert('必须输入页码');} else {this.document." + name + "." + paginationActionTag + ".value='" + Pagination.gotoPageAction + "';"+url+";}\"";    /*去掉了 return false; xuj 2011-1-5*/    
		    
		    //results = results + prepareStyles();
		    //results = results + prepareEventHandlers();
		    results = results + "title=\""+this.gotoPage1+"\" >&nbsp;</a>"; 
		    
		    //<a href="index.html" data-role="button" data-icon="arrow-u" title="回到页头">Up</a>
		    results+="</div>";
        }else if(isMobile!=null&& "2".equals(isMobile)){//和这个<hrms:extenditerate标签使用 如：generalquery.jsp
	        if (pagination.previoEnable())
	        {
	          results = results + "<a data-role=\"button\" data-icon=\"arrow-l\" href=\"javascript:this.document." + name + "." + Pagination.paginationActionTag + ".value='" + Pagination.previoAction + "';"+url+"\"";

	          //results = results + prepareStyles();
	          //results = results + prepareEventHandlers();
	          results = results + "title=\""+previoValue+"\">&nbsp;</a>";
	        }
	        else
	        {
	        	
	        }
	        
	        if (pagination.nextEnable())
	        {
	          results = results + "<a data-role=\"button\" data-icon=\"arrow-r\"  href=\"javascript:this.document." + name + "." +Pagination.paginationActionTag + ".value='" + Pagination.nextAction + "';"+url+"\"";
	          //results = results + prepareStyles();
	          //results = results + prepareEventHandlers();
	          results = results + "title=\""+nextValue+"\" >&nbsp;</a>";
	        }
	        else
	        {
	        }
	        results+="&nbsp;&nbsp;&nbsp;";
	        results = results +ResourceFactory.getProperty("label.page.serial");
	        results = results + "<input class='text4' type=text size='4' name='" + Pagination.pageSelect + "' value='' onkeypress='checkNumber(this,event)' class=\"ui-input-text ui-body-null ui-corner-all ui-shadow-inset ui-body-c ui-slider-input\"/>";
	        results = results + ResourceFactory.getProperty("label.page.page");
	        /**兼容其它浏览器onclik事件 webkit*/
	        results = results + "&nbsp<a data-role=\"button\" data-icon=\"refresh\" href=\"javascript:if(this.document." + name + "." +Pagination.pageSelect + ".value==''){ alert('必须输入页码');} else {this.document." + name + "." + Pagination.paginationActionTag + ".value='" + Pagination.gotoPageAction + "';"+url+";}\"";    /*去掉了 return false; xuj 2011-1-5*/    
		    
		    //results = results + prepareStyles();
		    //results = results + prepareEventHandlers();
		    results = results + "title=\""+this.gotoPage1+"\" >&nbsp;</a>"; 
		    
		    //<a href="index.html" data-role="button" data-icon="arrow-u" title="回到页头">Up</a>
		    results+="</div>";
        }else{
	        if (pagination.firstEnable())
	        {
	          //results = results + "<a href=\"javascript:" + url + "\" onclick=\"this.document." + name + "." + paginationActionTag + ".value='" + Pagination.firstAction + "';\"";
	          results = results + "<a href=\"javascript:this.document." + name + "." + paginationActionTag + ".value='" + Pagination.firstAction + "';"+url+"\"";
	
	          if (target != null)
	          {
	            results = results + " target=\"";
	            results = results + target;
	            results = results + "\"";
	          }
	          results = results + prepareStyles();
	          results = results + prepareEventHandlers();
	          results = results + ">";
	          results = results + firstValue + "</a>&nbsp";
	        }
	        else
	        {
	          results = results + firstValue + "&nbsp";
	        }
	        if (pagination.previoEnable())
	        {
	          //results = results + "<a href=\"javascript:" + url + "\" onclick=\"this.document." + name + "." + paginationActionTag + ".value='" + Pagination.previoAction + "';\"";
	          results = results + "<a href=\"javascript:this.document." + name + "." + paginationActionTag + ".value='" + Pagination.previoAction + "';"+url+"\"";
	
	          if (target != null)
	          {
	            results = results + " target=\"";
	            results = results + target;
	            results = results + "\"";
	          }
	          results = results + prepareStyles();
	          results = results + prepareEventHandlers();
	          results = results + ">";
	          results = results + previoValue + "</a>&nbsp";
	        }
	        else
	        {
	          results = results + previoValue + "&nbsp";
	        }
	        
	        if (pagination.nextEnable())
	        {
	          //results = results + "<a href=\"javascript:" + url + "\" onclick=\"this.document." + name + "." +paginationActionTag + ".value='" + Pagination.nextAction + "';\"";
	          results = results + "<a href=\"javascript:this.document." + name + "." +paginationActionTag + ".value='" + Pagination.nextAction + "';"+url+"\"";
	
	          if (target != null)
	          {
	            results = results + " target=\"";
	            results = results + target;
	            results = results + "\"";
	          }
	          results = results + prepareStyles();
	          results = results + prepareEventHandlers();
	          results = results + ">";
	          results = results + nextValue + "</a>" + "&nbsp";
	        }
	        else
	        {
	          results = results + nextValue + "&nbsp";
	        }
	        if (pagination.lastEnable())
	        {
	          //results = results + "<a href=\"javascript:" + url + "\" onclick=\"this.document." + name + "." + paginationActionTag + ".value='" + Pagination.lastAction + "';\"";
	          results = results + "<a href=\"javascript:this.document." + name + "." + paginationActionTag + ".value='" + Pagination.lastAction + "';"+url+"\"";
	
	          if (target != null)
	          {
	            results = results + " target=\"";
	            results = results + target;
	            results = results + "\"";
	          }
	          results = results + prepareStyles();
	          results = results + prepareEventHandlers();
	          results = results + ">";
	          results = results + lastValue + "</a>&nbsp";
	        }
	        else
	        {
	          results = results + lastValue + "&nbsp";
	        }
	        results = results +ResourceFactory.getProperty("label.page.serial");
	        results = results + "<input class='text4' type=text size='4' name='" + Pagination.pageSelect + "' value='' onkeypress='checkNumber(this,event)'  class='TEXT4'/>";
	        results = results + ResourceFactory.getProperty("label.page.page");
	        /**兼容其它浏览器onclik事件 webkit*/
	        results = results + "&nbsp<a href=\"javascript:" + url + "\" onclick=\"if(" + name + "." +Pagination.pageSelect + ".value==''){ alert('必须输入页码');return false;} else " + name + "." + paginationActionTag + ".value='" + Pagination.gotoPageAction + "';\"";
	       //results = results + "&nbsp<a href=\"javascript:if(this.document." + name + "." +Pagination.pageSelect + ".value==''){ alert('必须输入页码');} else {this.document." + name + "." + paginationActionTag + ".value='" + Pagination.gotoPageAction + "';"+url+";}\"";    /*去掉了 return false; xuj 2011-1-5*/    // dml 2011年11月10日16:06:20 注释掉换回原来上一行 如果用href触发在招聘管理的 岗位分析中不输入数字点击go会丢失图片，请有关做webkit人员的完善
		    if (target != null)
		    {
		      results = results + " target=\"";
		      results = results + target;
		      results = results + "\"";
		    }
		    results = results + prepareStyles();
		    results = results + prepareEventHandlers();
		    results = results + ">";
		    //results = results + gotoPage1 + "</a>";
		    results = results + "<img src=\"/images/go.gif\" border=0 align=\"absmiddle\">" + "</a>";        
        }
        TagUtils.getInstance().write(pageContext, results.toString());
        text = null;
        return 2;
    }

    private void extendProcess() throws JspException
    {
      String propertyTemp = null;
      String nameTemp = null;
      if (propertyId != null)
      {
        propertyTemp = (String) pageContext.getRequest().getAttribute(propertyId);
      }
      if (nameId != null)
      {
        nameTemp = (String) pageContext.getRequest().getAttribute(nameId);
      }
      if (propertyTemp != null)
      {
        property = propertyTemp + "." + property;
      }
      if (nameTemp != null)
      {
        name = nameTemp;
      }
    }
    
    public void release()
    {
      super.release();
      anchor = null;
      forward = null;
      href = null;
      linkName = null;
      name = null;
      page = null;
      paramId = null;
      paramName = null;
      paramProperty = null;
      paramScope = null;
      property = null;
      scope = null;
      target = null;
      text = null;
      transaction = false;
    }
	public String getIsMobile() {
		return isMobile;
	}
	public void setIsMobile(String isMobile) {
		this.isMobile = isMobile;
	}    

}
