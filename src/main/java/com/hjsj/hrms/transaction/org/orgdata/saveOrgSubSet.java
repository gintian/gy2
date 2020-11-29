package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>
 * Title:saveOrgSubSet.java
 * </p>
 * <p>
 * Description:机构相关子集的保存操作
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-04-11 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class saveOrgSubSet extends IBusiness
{
    public void execute() throws GeneralException
    {
	String priFldVal = (String) this.getFormHM().get("itemid");
	String i9999 = (String) this.getFormHM().get("i9999");
	String subset = (String) this.getFormHM().get("subset");
	String infor = (String) this.getFormHM().get("infor");
	String curri9999 = (String)this.getFormHM().get("curri9999");
	
	String mainitem="";
	if("2".equals(infor)){
		mainitem = "B0110";
	}else if("3".equals(infor)){
		mainitem = "E01A1";

	}else{
		mainitem = "B0110";
	}

	if(curri9999!=null && "0".equals(i9999))//插入方式新增记录
	{
	    GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
	    i9999 = gzbo.insertSubSet(subset,mainitem,priFldVal,curri9999);
	}
	
	ArrayList fieldlist = (ArrayList) this.getFormHM().get("subFlds");

	RecordVo vo = new RecordVo(subset);	
	vo.setString(mainitem.toLowerCase(), priFldVal);
	boolean isadd = false;
	if ("0".equals(i9999))
	{
	    GzDataMaintBo bo = new GzDataMaintBo(this.frameconn,this.userView);
	    i9999 = bo.getI9999(subset,mainitem, priFldVal);
	    isadd = true;
	}
	vo.setInt("i9999", Integer.parseInt(i9999));
	ContentDAO dao = new ContentDAO(this.getFrameconn());

	    try
	    {
		if(!isadd)
		    vo=dao.findByPrimaryKey(vo);
	    } catch (SQLException e1)
	    {
		e1.printStackTrace();
	    }
	
	for (int i = 0; i < fieldlist.size(); i++)
	{
	    FieldItem fieldItem = (FieldItem) fieldlist.get(i);

	    String itemid = fieldItem.getItemid();
	    String value = fieldItem.getValue();

	    if("".equals(value.trim()))
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
