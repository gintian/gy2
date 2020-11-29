package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * <p>
 * Title:UpdateBonusStatusTrans.java
 * </p>
 * <p>
 * Description:更新处理状态
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-11 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class UpdateBonusStatusTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String paramStr = (String) this.getFormHM().get("paramStr");
	String[] temp = paramStr.split(",");
	String bonusSet = (String)this.getFormHM().get("bonusSet");
	String doStatusFld = (String)this.getFormHM().get("doStatusFld");
	ContentDAO dao = new ContentDAO(this.frameconn);
	for(int i=0;i<temp.length;i++)
	{
	    String[] temp1 = temp[i].split(":");
	    String dbpri = temp1[2];
	    String a0100 =  temp1[0];
	    String i9999 =  temp1[1];
	    String status = temp1[3];
	    String sql = "update "+dbpri+bonusSet+" set "+doStatusFld+"='"+status+"' where a0100='"+a0100+"' and i9999="+i9999;
	    try
	    {
		dao.update(sql);
	    } catch (SQLException e)
	    {
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	    }
	}

    }

}
