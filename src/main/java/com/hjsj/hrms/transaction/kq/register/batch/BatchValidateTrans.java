package com.hjsj.hrms.transaction.kq.register.batch;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class BatchValidateTrans extends IBusiness{
	private String unit_HOUR = "01";
	private String unit_DAY = "02";
	private String unit_ONCE = "04";
	private String unit_MINUTE = "03";
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			String msg = "1";
			String settype = (String)this.getFormHM().get("settype");
			String value = (String)this.getFormHM().get("value");
			
			String table = (String) this.getFormHM().get("table");
			
			if(checkcode(settype,value)){
				msg="字符类型或者长度不对!";
			}
			if ("1".equals(msg)) 
			{
				msg = CheckValue(settype,value,table);
				if (msg == null || "".equals(msg) || msg.length() <= 0) 
				{
					msg = "1";
				}
			}
			this.getFormHM().put("msg",msg);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public boolean checkcode(String settype,String value)
	{
		boolean flag = false;
		RowSet rs = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			String sql = "select itemtype from t_hr_busifield where fieldsetid='Q03' and itemid = '"+settype+"'";
			rs = dao.search("select itemtype,Decimalwidth,itemlength from t_hr_busifield where fieldsetid='Q03' and itemid = '"+settype+"'");
			String itemtype="";
			String Decimalwidth="";
			String itemlength="";
			int dd = value.length();
			while(rs.next())
			{
				itemtype = rs.getString("itemtype");
				Decimalwidth = rs.getString("Decimalwidth");
				itemlength = rs.getString("itemlength");
			}
			if("N".equals(itemtype))
			{
//				boolean mat = value.matches("\\d+");//返回true为纯数字,否则就不是纯数字 
//				if(!mat)
//				{
//					flag=true;
//				}
				if("0".equals(Decimalwidth))
				{
					boolean mat = value.matches("^[+-]?[\\d]+$");  //全部是数字
					if(!mat||dd>10)
					{
						flag=true;
					}
				}else 
				{
					boolean mats=true;
					if(dd>1)
					{
						mats = value.matches("^[+-]?[\\d]*[.]?[\\d]+"); //浮点
					}
					if(!mats||dd>18)
					{
						flag=true;
					}
				}
			}else if("A".equals(itemtype))
			{
				int ss = Integer.parseInt(itemlength);
				if(dd>ss)
				{
					flag=true;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
    		if(rs!=null){
    			try {
    				rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
		return flag;
	}
	
	private String CheckValue(String kq_item,String kq_value,String table){
		String mess = "";
		StringBuffer sql = new StringBuffer();
		sql.append("select item_name,item_unit from kq_item");
		sql.append(" where Upper(fielditemid)='" + kq_item.toUpperCase() + "'");
		String item_unit = "";
		String item_name = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {

			this.frowset = dao.search(sql.toString());
			if (this.frowset.next()) {
				item_unit = this.frowset.getString("item_unit");
				item_name = this.frowset.getString("item_name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (item_unit == null || item_unit.length() <= 0) {
			item_unit = unit_MINUTE;
		}
		float unit = Float.parseFloat(kq_value);
		
		ArrayList list = KqUtilsClass.getcurrKq_duration();
		Date start = OperateDate.strToDate(((String)list.get(0)).replace(".", "-") + " 12:00", "yyyy-MM-dd HH:mm");
		Date end = OperateDate.strToDate(((String)list.get(1)).replace(".", "-") + " 12:00", "yyyy-MM-dd HH:mm");
		int days = OperateDate.getDayCountByDate(end, start);
		days ++;
		if (item_unit.equals(unit_HOUR)) 
		{
			if ("q03".equalsIgnoreCase(table) && unit > 24) 
			{
				mess = item_name + "指标单位为小时，你输入的值大于24小时";
			}else if(unit > (24 * days))
			{
				mess = item_name + "指标单位为小时，你输入的值大于"+(24 * days)+"小时";
			}
		} else if (item_unit.equals(unit_MINUTE)) {
			if ("q03".equalsIgnoreCase(table) && unit > 1440) {
				mess = item_name + "指标单位为分钟，你输入的值大于1440分钟";
			}else if (unit > (1440 * days)) 
			{
				mess = item_name + "指标单位为分钟，你输入的值大于"+(1440 * days)+"分钟";
			}
		} else if (item_unit.equals(unit_DAY)) {
			if ("q03".equalsIgnoreCase(table) && unit > 1) {
				mess = item_name + "指标单位为天，你输入的值大于1天";
			}else if(unit > days)
			{
				mess = item_name + "指标单位为天，你输入的值大于"+ days +"天";
			}
		} else if (item_unit.equals(unit_ONCE)) {

		}
		return mess;
	}
}
