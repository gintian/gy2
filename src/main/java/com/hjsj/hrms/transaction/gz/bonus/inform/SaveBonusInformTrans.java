package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>
 * Title:SaveBonusInformTrans.java
 * </p>
 * <p>
 * Description:保存奖金数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-10 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveBonusInformTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String a0100 = (String) hm.get("a0100");
	hm.remove("a0100");
	String bonusSet =(String) this.getFormHM().get("bonusSet");	// 奖金子集
	RecordVo vo = new RecordVo(a0100.substring(0, 3)+bonusSet);
	ArrayList fieldlist = (ArrayList) this.getFormHM().get("fieldInfoList");
	for (int i = 0; i < fieldlist.size(); i++)
	{
	    FieldItem fieldItem = (FieldItem) fieldlist.get(i);
	    
	    String itemid = fieldItem.getItemid();
	    String value = fieldItem.getValue();
	    String itemType = fieldItem.getItemtype();
	    int decimalwidth= fieldItem.getDecimalwidth();
	    
	    if("CreateUserName".equalsIgnoreCase(itemid))
		continue;
	    if("D".equals(itemType))
	    {
		value = PubFunc.replace(value, ".", "-");
		vo.setDate(itemid, value);
	    }	
	    else if ("N".equals(itemType) && decimalwidth==0)
		vo.setInt(itemid, Integer.parseInt(value.length()==0?"0":value));
	    else if("N".equals(itemType) && decimalwidth>0)
		vo.setDouble(itemid, Double.parseDouble(value.length()==0?"0":value));
	    else 
		vo.setString(itemid, value);
	}
	String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
	String CreateUserName = this.getUserView().getUserFullName();
//	vo.setDate("CreateTime", creatDate);
	int i9999=this.getI9999(a0100,bonusSet);
	vo.setInt("i9999", i9999);
	vo.setString("a0100", a0100.substring(3));
	ContentDAO dao = new ContentDAO(this.frameconn);	
	dao.addValueObject(vo);
	
	try
	{
	    String sql = "update "+a0100.substring(0, 3)+bonusSet+ " set CreateUserName=? , CreateTime=? where a0100='"+ a0100.substring(3)+"' and i9999="+Integer.toString(i9999);
	    ArrayList list = new ArrayList();
	    list.add(CreateUserName);
	    list.add(java.sql.Date.valueOf(creatDate));
	    dao.update(sql, list);
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
    }
    public int getI9999(String a0100,String bonusSet)
    {

	int i9999 = 1;
	ContentDAO dao = new ContentDAO(this.frameconn);
	StringBuffer strSql = new StringBuffer();
	strSql.append("select "+Sql_switcher.isnull("max(i9999)", "0")+" n  from ");
	strSql.append(a0100.substring(0, 3)+bonusSet);
	strSql.append(" where a0100='");
	strSql.append(a0100.substring(3));
	strSql.append("'");
	int count = 1;
	try
	{
	    RowSet rs = dao.search(strSql.toString());
	    if (rs.next())
		count = rs.getInt(1) + 1;
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}
	i9999 = new Integer(count).intValue();
	return i9999;
    }
}
