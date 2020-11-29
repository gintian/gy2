package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>
 * Title:SaveRecordTrans
 * </p>
 * <p>
 * Description:保存的记录
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2009-7-8:下午03:48:28
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 */
public class SaveRecordTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = this.getFormHM();
	ArrayList list = (ArrayList) hm.get("data_table_record");	
	String bonusSet = (String)hm.get("data_table_table");
	if(bonusSet!=null && bonusSet.length()>3)
	    bonusSet=bonusSet.substring(3);
	try
	{
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    ArrayList volist = new ArrayList();
	    for(int i=0;i<list.size();i++)
	    {
		RecordVo vo = (RecordVo)list.get(i);
		String dbpri = vo.getString("dbase");
		String tablename = dbpri+bonusSet;
		String a0100 = vo.getString("a0100");
		int i9999 = vo.getInt("i9999");
		RecordVo bonusSetVo = new RecordVo(tablename);
		bonusSetVo.setString("a0100",a0100);
		bonusSetVo.setInt("i9999", i9999);
		bonusSetVo = dao.findByPrimaryKey(bonusSetVo);
		ArrayList fieldlist = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
		for (int j = 0; j < fieldlist.size(); j++)
		{
		    FieldItem fielditem = (FieldItem) fieldlist.get(j);
		    String itemType = fielditem.getItemtype();
		    String itemid = fielditem.getItemid();
		    int decimalwidth= fielditem.getDecimalwidth();
		    if("D".equals(itemType))
			bonusSetVo.setDate(itemid, vo.getDate(itemid));
		    else if ("N".equals(itemType) && decimalwidth==0)
			bonusSetVo.setInt(itemid, vo.getInt(itemid));
		    else if("N".equals(itemType) && decimalwidth>0)
			bonusSetVo.setDouble(itemid, vo.getDouble(itemid));
		    else 
			bonusSetVo.setString(itemid, vo.getString(itemid));

		}
		
		volist.add(bonusSetVo);
	    }
	   
	    dao.updateValueObject(volist);

	} catch (Exception ex)
	{
	    throw GeneralExceptionHandler.Handle(ex);
	}
    }
}
