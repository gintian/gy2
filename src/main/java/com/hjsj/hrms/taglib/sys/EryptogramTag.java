package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.sys.Eryptogram;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class EryptogramTag extends BodyTagSupport {

  private String decrypt;
  private String encrypt;
  private String key;
  public int doEndTag() throws JspException 
  {
	  if(decrypt==null||decrypt.length()<=0)
	  {
		  pageContext.setAttribute(encrypt, "");	
		  pageContext.setAttribute(key, "");	
		  return SKIP_BODY;
	  }  
	  Eryptogram eryptogram=new Eryptogram();
	  try
	  {
		  String keyStr=eryptogram.getSecretKey();
		  String encryptStr=eryptogram.encryptData (decrypt ,keyStr);
		  pageContext.setAttribute(encrypt, encryptStr);	
		  pageContext.setAttribute(key, keyStr);	
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }
      return SKIP_BODY;
  }
}
