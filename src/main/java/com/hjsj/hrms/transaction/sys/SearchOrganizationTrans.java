package com.hjsj.hrms.transaction.sys;

import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SearchOrganizationTrans extends IBusiness {

	public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        String a_code=(String)hm.get("a_code");
        StringBuffer cond_str=new StringBuffer();
        String codevalue="";
        /**相关代码类及代码值*/
        if(a_code==null|| "".equals(a_code))
        {
        	if(userView.isSuper_admin()&&(!userView.isBThreeUser()))
        		a_code="UN";
        	else
        	{
        		a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
        	}
        }
        if(a_code.length()>=2)
        	codevalue=a_code.substring(2);
        if("UN".equals(a_code))
        {
            cond_str.append(" where codeitemid=parentid");
        }
        else 
        {
            cond_str.append(" where parentid ='");
            cond_str.append(codevalue);
            cond_str.append("' and codeitemid<>'");      
            cond_str.append(codevalue);
            cond_str.append("'");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		cond_str.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
        /**查询条件*/
        this.getFormHM().put("cond_str",cond_str.toString());
        StringBuffer strsql=new StringBuffer();
        strsql.append("select codeitemid,codeitemdesc from organization ");
        this.getFormHM().put("sql_str",strsql.toString());
        strsql.setLength(0);
        strsql.append("codeitemid,codeitemdesc,");
        this.getFormHM().put("columns",strsql.toString());
	}

}
