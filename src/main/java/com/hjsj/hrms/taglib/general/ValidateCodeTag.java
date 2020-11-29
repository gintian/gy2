/**
 * 
 */
package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.businessobject.sys.SysParamBo;
import com.hjsj.hrms.businessobject.sys.SysParamConstant;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * <p>Title:安全验证码标签</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 11, 200611:32:37 AM
 * @author chenmengqing
 * @version 4.0
 */
public class ValidateCodeTag extends BodyTagSupport {
	/**
	 * 验证码的长度
	 */
	private int codelen=6;
	private int channel=1;//0外网  1 内网  默认内网
	
	public int doEndTag() throws JspException {
		return super.doEndTag();
	}
	
	public int doStartTag() throws JspException {
		try
		{
			//String validatecodelen=SystemConfig.getPropertyValue("validatecodelen");//自定义附加码长度
			String	validatecodelen=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.VALIDATECODELEN);
			//String validatecodeinfo=SystemConfig.getPropertyValue("validatecodeinfo");//自定义附加码长度	
			String	validatecodeinfo=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.VALIDATECODEINFO);
			if(channel==1&&validatecodelen!=null&&validatecodelen.length()>0)//zzk  加以内外网区分
			{
				codelen=Integer.parseInt(validatecodelen);
			}
			StringBuffer strSrc=new StringBuffer();
			if(validatecodeinfo!=null&&validatecodeinfo.length()>0)
			{
				strSrc.append(validatecodeinfo);
			}else
				strSrc.append("QAZWSXEDCRFVTGBYHNUJMIKLP123456789");
			String filename="";
			if(channel==0){
				filename=ServletUtilities.createValidateCodeImage1(codelen,strSrc,pageContext.getSession());	
			}else{
				filename=ServletUtilities.createValidateCodeImage(codelen,strSrc,pageContext.getSession());	
			}
			filename = SafeCode.encode(PubFunc.encrypt(filename));
	        String url=((HttpServletRequest)pageContext.getRequest()).getContextPath();
	        StringBuffer photourl=new StringBuffer();
	        if(!"".equals(filename))
	        {
	        	photourl.append(url);
	        	photourl.append("/servlet/DisplayOleContent?filename=");
	        	photourl.append(filename);
	        }
	        else
	        {
	        	pageContext.getSession().setAttribute("validatecode","CJXG8V");
	        	photourl.append("/images/validatecode.jpg");
	        }
	        
	        StringBuffer str_html=new StringBuffer();
	        str_html.append("<img src=\"");
	        str_html.append(photourl.toString());
	        str_html.append("\" ");
	        //str_html.append(" height=\"");
	        //str_html.append(height);
	        //str_html.append("\" width=\"");
	        //str_html.append(width);
	        str_html.append(" border=0 align=absMiddle>");
	        //System.out.println("---->file="+photourl.toString());
	        pageContext.getOut().println(str_html.toString());
	        return EVAL_BODY_BUFFERED;   				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
            return 0;			
		}
	}

	public int getCodelen() {
		return codelen;
	}

	public void setCodelen(int codelen) {
		this.codelen = codelen;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public ValidateCodeTag() {
		
	}
	
	
}
