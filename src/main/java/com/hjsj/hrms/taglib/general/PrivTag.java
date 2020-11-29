package com.hjsj.hrms.taglib.general;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

//import com.hjsj.hrms.businessobject.sys.VersionControl;

/**
 * <p>Title:PrivTag</p>
 * <p>Description:主要为了根据权限或模块加密进行分析,是否显示</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 4, 2005:3:47:25 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class PrivTag extends TagSupport {
    /**功能号*/
    private String func_id;
    /**功能模块列表,for examples 1,2,*/
    private String module_id;
    /**分析用户是否购了此模块或有登录用户是否拥有此功能权限和操作此模块的权限*/
    private boolean haveFunction()
    {
        boolean bfunc=true,bmodule=true;
        UserView userview=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
        
		if (userview.getHm().get("moduleFlag")!=null){//职称评审投票系统不是使用Ehr的用户，无权限 。
			if ("jobtitleVote".equals((String)userview.getHm().get("moduleFlag"))){
			  return false;
			}
		}
		
        /**版本功能控制*/
        VersionControl ver_ctrl=new VersionControl();
        EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
        VersionControl.ver=lock.getVersion();
        //ver_ctrl.setVer(lock.getVersion());
        if(!(module_id==null|| "".equals(module_id)))
        {
        	String[] modules =StringUtils.split(module_id,",");
            for(int i=0;i<modules.length;i++)
            {
            	module_id=modules[i];
            	bmodule=lock.isBmodule(Integer.parseInt(module_id),userview.getUserName());
            	if(bmodule)
            		break;
            }
        }
        if(!(func_id==null|| "".equals(func_id)))
        {
           String[] funcs =StringUtils.split(func_id,","); 
           for(int i=0;i<funcs.length;i++)
           {
        	   //System.out.println("-->begin...");
        	   //System.out.println("-->user="+userview.getUserName());
        	   //System.out.println("-->end...");
       		   bfunc= userview.hasTheFunction(funcs[i])&&haveCustomFunc(funcs[i])&&ver_ctrl.searchFunctionId(funcs[i])&&haveVersionFunc(userview,funcs[i], lock.getVersion_flag());
               if(bfunc)
            	   break;
           }
        }
        return (bfunc&&bmodule);
    }
    
    /**
     * 标准版、专业版功能区分
     * @param funcid
     * @param ver_s =1专业版 =0标准版
     * @return
     */
    private boolean haveVersionFunc(UserView userview,String funcid,int ver_s)
    {
    	/**专业版控制功能号列表*//*
    	String sfunc=",,";    	
    	*//**普通版控制功能号列表*//*
    	//cmq ,changed  3001D,3001F 标准版放开数据平台及集成功能 at 2014.02.08
    	StringBuffer cfunc= new StringBuffer(",23064,2315,23057,23056,25059,23058,23062,25066,33101026,360,26012,2601004,3260103,32307,32306A,32306B,32332,32363,27053,27054,27052,27044,32416,324021501,3240214,324073,32411,324021502,3240214,32516,32203,2906,9A4,9A51,080809,3001E,080808,3001G,030701,1113,060803,0610,060605,0B26,0B11,0C346,0C33,0C36,0C39,0C40,0C347,32026,37025,37125,37225,37325,324010123,325010123,32405,");
    	cfunc.append("2306007,23151,0501021,230511,23110107,231706,231707,2602306,04010106,2602308,2311036,2311037,3221002,2602307,326040503,04010107,326030106,326030127,326030134,326030130,3237303,270302,27030a,27030c,27082,060708,");
    	cfunc.append("3240315,3260415,3260413,32405,324073,32411,3250313,32516,3206,9A52,290207,324080802,230500,230501,2706012,2703503,");
    	if(ver_s==0){
	    	int idx=cfunc.indexOf(","+funcid+",");
	    	if(idx==-1)
	    		return true;
	    	else
	    		return false;
    	}else if(ver_s==1){
    		int idx=sfunc.indexOf(","+funcid+",");
	    	if(idx==-1)
	    		return true;
	    	else
	    		return false;
    	}
    	return true;*/
    	return PubFunc.haveVersionFunc(userview, funcid, ver_s);
    }
    
    /**
     * 客户定制的个性化需求
     * @param funcid
     * @return
     */
    private boolean haveCustomFunc(String funcid)
    {
    	String cfunc=",080804,30044,";
    	boolean isldap=SystemConfig.isLdap();
    	if(cfunc.indexOf(","+funcid+",")==-1)
    		return true;
    	if(cfunc.indexOf(","+funcid+",")!=-1&&isldap)
    		return true;
    	return false;
    }
    
    public int doEndTag() throws JspException {
        return (EVAL_PAGE);
    }
    
    public int doStartTag() throws JspException {
        if(haveFunction())
            return (EVAL_BODY_INCLUDE);
        else
            return (SKIP_BODY);            
    }
    public String getFunc_id() {
        return func_id;
    }
    public void setFunc_id(String func_id) {
        this.func_id = func_id;
    }
    public String getModule_id() {
        return module_id;
    }
    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }
}
