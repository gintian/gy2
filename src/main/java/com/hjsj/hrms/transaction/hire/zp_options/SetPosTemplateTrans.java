/*
 * Created on 2005-9-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SetPosTemplateTrans</p>
 * <p>Description:岗位模板设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SetPosTemplateTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try
        {
			RecordVo vo=(RecordVo)this.getFormHM().get("posTemplatevo");
			StringBuffer strsql=new StringBuffer();
			strsql.append("delete from constant where constant='ZP_POS_TEMPLATE'\n");
			new ExecuteSQL().execUpdate(strsql.toString());	
        }catch(Exception e){
           //e.printStackTrace();
        }
    	try
        {
			RecordVo vo=(RecordVo)this.getFormHM().get("posTemplatevo");
			StringBuffer strsql=new StringBuffer();
			String value=vo.getString("str_value");
			//岗位说明书，还原特殊字符 jingq add 2014.09.23
			value = PubFunc.keyWord_reback(value);
			value=value!=null&&!"#".equals(value)&&value.length()>0?value:"";
			strsql.delete(0,strsql.length());
			strsql.append("insert into constant(constant,type,str_value,Describe)values('ZP_POS_TEMPLATE','0','" + value + "','岗位职责说明书')");
			new ExecuteSQL().execUpdate(strsql.toString());	
        }catch(Exception e){
           e.printStackTrace();
        }
        String ps_card_attach=(String)this.getFormHM().get("ps_card_attach");
        if(ps_card_attach==null||ps_card_attach.length()<=0)
        	ps_card_attach="false";
        String sql="delete from constant where UPPER(constant)='PS_CARD_ATTACH'";
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try {
			dao.delete(sql, new ArrayList());
			sql="insert into constant(constant,type,str_value,Describe)values('PS_CARD_ATTACH','0','" + ps_card_attach+"','显示岗位说明书附件')";
			dao.insert(sql,  new ArrayList());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
