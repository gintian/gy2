package com.hjsj.hrms.transaction.ht.inform;

import com.hjsj.hrms.businessobject.ht.inform.ContracInforBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:SaveContractInfoTrans.java
 * </p>
 * <p>
 * Description:保存合同相关子集
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-03-16 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveContractInfoTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	String a0100 = (String) this.getFormHM().get("a0100");
	String i9999 = (String) this.getFormHM().get("i9999");
	String itemtable = (String) this.getFormHM().get("itemtable");

	ArrayList fieldlist = (ArrayList) this.getFormHM().get("subFlds");

	RecordVo vo = new RecordVo(itemtable);
	vo.setString("a0100", a0100);
	for (int i = 0; i < fieldlist.size(); i++)
	{
	    FieldItem fieldItem = (FieldItem) fieldlist.get(i);

	    String itemid = fieldItem.getItemid();
	    String value = fieldItem.getValue();

//	    if (value.equals(""))
//		continue;

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
	boolean isadd = false;
	if ("0".equals(i9999))
	{
	    ContracInforBo bo = new ContracInforBo(this.getFrameconn());
	    i9999 = bo.getI9999(itemtable, a0100);
	    isadd = true;
	}
	vo.setInt("i9999", Integer.parseInt(i9999));
	ContentDAO dao = new ContentDAO(this.getFrameconn());

	if (isadd)
	{
	    dao.addValueObject(vo);
	} else
	    try
	    {
		dao.updateValueObject(vo);
	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }

    }

}
