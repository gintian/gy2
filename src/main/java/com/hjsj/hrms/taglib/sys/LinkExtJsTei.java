package com.hjsj.hrms.taglib.sys;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class LinkExtJsTei extends TagExtraInfo{
	public VariableInfo[] getVariableInfo(TagData data)
	  {
	    String id = (String)data.getAttribute("frameDegradeId");
	    if(id !=null && id.length()>0)
	    		return new VariableInfo[] { new VariableInfo(id,"java.lang.String", true, 2) };
	    return new VariableInfo[0];
	  }
}
