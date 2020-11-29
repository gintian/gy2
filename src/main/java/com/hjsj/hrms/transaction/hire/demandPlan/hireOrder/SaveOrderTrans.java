package com.hjsj.hrms.transaction.hire.demandPlan.hireOrder;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:SaveOrderTrans.java</p>
 * <p>Description:保存订单</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-05-13 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveOrderTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String orderid = (String)this.getFormHM().get("orderid");
	ArrayList fieldslist = (ArrayList)this.getFormHM().get("fieldslist");
	ArrayList list = DataDictionary.getFieldList("Z04", Constant.USED_FIELD_SET);
	
	RecordVo vo = new RecordVo("z04");
	vo.setString("z0400", orderid);
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    vo=dao.findByPrimaryKey(vo);
	    for(int i=0;i<fieldslist.size();i++)
	    {
	        FieldItem fieldItem = (FieldItem) fieldslist.get(i);

	        String itemid = fieldItem.getItemid();
	        String value = fieldItem.getValue();
	        if("z0400".equalsIgnoreCase(itemid))
	    	continue;
	        if ("D".equals(fieldItem.getItemtype()))
	        {
	    	vo.setDate(itemid, value);
	        } else if ("N".equals(fieldItem.getItemtype()))// 对于数值类型，在前后台都要进行控制,前台验证是整数还是小数类型，后台修正小数位数
	        {
	    	value = PubFunc.round(value, fieldItem.getDecimalwidth());
	    	vo.setString(itemid, value);
	        } else
	    	vo.setString(itemid, value);
	        
	    }
	    
	    dao.updateValueObject(vo);
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	
    }

}
