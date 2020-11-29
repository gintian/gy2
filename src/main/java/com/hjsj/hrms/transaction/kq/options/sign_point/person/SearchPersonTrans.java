package com.hjsj.hrms.transaction.kq.options.sign_point.person;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author szk
*
*/
public class SearchPersonTrans extends IBusiness {

	public void execute() throws GeneralException {
		RowSet rs = null;
		try{
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String byname = (String) hm.get("byname");
        String pid = (String) hm.get("pid");
        if (pid==null || pid.length()==0)
        {
        	pid=(String) this.getFormHM().get("pid");
        }
        this.getFormHM().put("pid", pid);
        pid=pid.substring(1);
        hm.remove("byname");
        KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
        ArrayList nbase=kqUtilsClass.getKqPreList();
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());

        if (nbase.size() == 0) {
            this.getFormHM().put("sql_str", "");
            this.getFormHM().put("columns", "");
            this.getFormHM().put("cond_order", "");
            throw new GeneralException("未设置人员库，请在参数中设置后再操作！");
        }
        /**相关代码类及代码值*/
        
        try {
            String privDestTab = "kq_sign_point_emp";
            
            //人员库sql  
            StringBuffer strsql = new StringBuffer();
            //姓名、拼音简码、唯一指标模糊查询条件
            if (byname != null && byname.length() > 0) {
                byname = SafeCode.decode(byname);
                
            	//获取拼音简码的字段
            	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
            	String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
            	//获取唯一性指标的字段
            	String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
            	
            	strsql.append("select nbase,a0100,b0110,e0122,e01a1,a0101 from (");
            	for (int i = 0; i < nbase.size(); i++)
    			{
                	strsql.append("select nbase,p.a0100,p.b0110,p.e0122,p.e01a1,p.a0101 from kq_sign_point_emp p left JOIN "+nbase.get(i)+"A01 u on u.A0100=p.A0100");
                	strsql.append(" where p.pid="+pid+" and (");
                	
                	if(pinyin_field!=null && pinyin_field.trim().length()>0)
                	    strsql.append("u."+pinyin_field+" ='"+byname+"' or");
                	
                	if(onlyname!=null && onlyname.trim().length()>0)
                	    strsql.append(" u."+onlyname+" ='"+byname+"' or ");
                	
                	strsql.append(" u.a0101 LIKE '" + byname + "%'  ) and p.nbase='"+nbase.get(i)+"' union ");
    			}
            	strsql.setLength(strsql.length() - 7);
            	strsql.append(") a where 1=1 ");
            	
            	privDestTab = "a";
            }else {
            	strsql.append("select nbase,a0100,b0110,e0122,e01a1,a0101 from kq_sign_point_emp where pid="+pid);
			}
    
            //权限
            String privcode = RegisterInitInfoData.getKqPrivCode(userView);
            String codevalue = RegisterInitInfoData.getKqPrivCodeValue(userView);
            if ("UM".equalsIgnoreCase(privcode))
            	strsql.append(" and e0122 like '" + codevalue + "%'");
            else if ("@K".equalsIgnoreCase(privcode))
            	strsql.append(" and e01a1 like '" + codevalue + "%'");
            else if ("UN".equalsIgnoreCase(privcode))
            	strsql.append(" and b0110 like '" + codevalue + "%'");
            
            strsql.append(" and ").append(RegisterInitInfoData.getKqEmpPrivWhr(this.getFrameconn(), userView, privDestTab));
          
               
            this.getFormHM().put("sql_str", strsql.toString());
            /**字段列表*/
            strsql.setLength(0);
            strsql.append("nbase,a0100,b0110,e0122,e01a1,a0101");

            this.getFormHM().put("cond_str", "");
            this.getFormHM().put("columns", strsql.toString());
            this.getFormHM().put("cond_order", " order by b0110,e0122");

        } catch (Exception ee) {
            ee.printStackTrace();
        }

        //显示部门层数
        Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
        if (uplevel == null || uplevel.length() == 0)
            uplevel = "0";
        this.getFormHM().put("uplevel", uplevel);
        this.getFormHM().put("viewPost", para.getKq_orgView_post());
    }
    catch (Exception e) {
    	 e.printStackTrace();
         throw GeneralExceptionHandler.Handle(e);
	}
    finally {
        KqUtilsClass.closeDBResource(rs);
    }
    }


   
}
