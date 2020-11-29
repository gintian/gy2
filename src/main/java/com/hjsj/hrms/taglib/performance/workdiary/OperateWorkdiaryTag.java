package com.hjsj.hrms.taglib.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;

public class OperateWorkdiaryTag extends BodyTagSupport{

	
	private String p0100;
	private String disabled;
	public String getP0100() {
		return p0100;
	}
	public void setP0100(String p0100) {
		this.p0100 = p0100;
	}
	public String getDisabled() {
		return disabled;
	}
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}
	public int doStartTag() throws JspException
	{
            if(this.p0100==null||this.p0100.length()<=0)
            	return EVAL_BODY_INCLUDE;
            this.p0100 = PubFunc.decrypt(this.p0100);
            UserView uv=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
            
            Connection conn=null;
            try{
        		conn=AdminDb.getConnection();
        		if(uv.getStatus()==0){
        			uv=new UserView(uv.getS_userName(), uv.getS_pwd(), conn);
        			try {uv.canLogin();} catch (Exception e) {e.printStackTrace();}
        		}
        		WorkdiarySelStr wds=new WorkdiarySelStr();
        		String flag=wds.reChaoSongFlag(p0100, uv.getUserName(), uv.getA0100(), uv.getDbname(), conn);
        		if(flag!=null&& "1".equals(flag))
        		{
        			return SKIP_BODY;
        		}else
        			return EVAL_BODY_INCLUDE;
            }catch(Exception e)
    		{
    			e.printStackTrace();
    			
    		}
    		finally
    		{
    			try{    			 
    			 if (conn != null)
    	             conn.close();
    			}catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    	          
    		}
    		return EVAL_BODY_INCLUDE;
	}
}
