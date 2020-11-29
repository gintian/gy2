package com.hjsj.hrms.transaction.train.request;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.math.BigDecimal;

/**
 * <p>
 * Title:AddTrainResourceTrans.java
 * </p>
 * <p>
 * Description:费用分摊交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-08-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class CostShareTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String classid = (String) this.getFormHM().get("classid");
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	StringBuffer sql = new StringBuffer();
	sql.append("select count(*) n from R40  Where R4005='");
	sql.append(classid);
	sql.append("' and "+Sql_switcher.isnull("R4013", "01")+"='03'");
	
	try
	{
	    RowSet rs = dao.search(sql.toString());
	    int count = 0;
	    if (rs.next())
	    {
		 count = rs.getInt("n");
		if (count == 0)
		    return;
	    }

	    sql.setLength(0);
	    sql.append("select r3111/"+count+" from r31 where r3101='");
	    sql.append(classid);
	    sql.append("'");
	    
	    rs = dao.search(sql.toString());
	    float temp=0;
	    if (rs.next())	    
	    	temp = rs.getFloat(1);
	    FieldItem item = DataDictionary.getFieldItem("r4010");
	    int d = item.getDecimalwidth();
	    temp=new BigDecimal(temp).setScale(d, BigDecimal.ROUND_HALF_UP).floatValue();
	    
	    sql = new StringBuffer();
	    sql.append("Update R40 set R4010="+temp+" Where R4005='");
	    sql.append(classid);
	    sql.append("' and R4013='03'");
	    
	    dao.update(sql.toString());
	    
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}

    }

}
