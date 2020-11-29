package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class SetJobTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
        {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=(RecordVo)this.getFormHM().get("jobTemplatevo");
			StringBuffer strsql=new StringBuffer();
			strsql.append("delete from constant where constant='ZP_JOB_TEMPLATE'\n");
			dao.update(strsql.toString());	
			
			String value=vo.getString("str_value");
			//基准岗位说明书，还原特殊字符  jingq add 2014.09.23
			value = PubFunc.keyWord_reback(value);
			value=value!=null&&!"#".equals(value)&&value.length()>0?value:"";
			strsql.delete(0,strsql.length());
			strsql.append("insert into constant(constant,type,str_value,Describe)values('ZP_JOB_TEMPLATE','0','" + value + "','基准岗位职责说明书')");
			dao.update(strsql.toString());	
			
			
	        String ps_c_card_attach=(String)this.getFormHM().get("ps_c_card_attach");
	        if(ps_c_card_attach==null||ps_c_card_attach.length()<=0)
	        	ps_c_card_attach="false";
	        String sql="delete from constant where UPPER(constant)='PS_C_CARD_ATTACH'";
	        dao.update(sql);	
			sql="insert into constant(constant,type,str_value,Describe)values('PS_C_CARD_ATTACH','0','" + ps_c_card_attach+"','显示基准岗位说明书')";
			dao.update(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
