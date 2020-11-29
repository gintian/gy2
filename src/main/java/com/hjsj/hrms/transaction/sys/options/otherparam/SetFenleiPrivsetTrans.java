package com.hjsj.hrms.transaction.sys.options.otherparam;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 启用分类进行子集及授权机制
 * <p>Title:SetFenleiPrivsetTrans.java</p>
 * <p>Description>:SetFenleiPrivsetTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 24, 2011 3:17:26 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SetFenleiPrivsetTrans extends IBusiness{
	public void execute() throws GeneralException{
		String isf=(String)this.getFormHM().get("isf");
		String itemid=(String)this.getFormHM().get("itemid");		
		String str_value="";
		String checked=(String)this.getFormHM().get("checked");
		String empty=(String)this.getFormHM().get("empty");		
		if(itemid!=null&&itemid.length()>0)
		{
			String values[]=itemid.split("/");//AX/A0107
			if(values.length==2)
			{
				str_value=values[1]+","+values[0];//AXXXX,AX（指标名称,代码类名称
			}
		}
		if(checked!=null&& "1".equals(checked))
		{
			boolean isCorrect=searchConstent(str_value,"1");
			if(isCorrect)
				this.getFormHM().put("ischecked", "1");
			else
				this.getFormHM().put("ischecked", "0");
		}else if(empty!=null&& "1".equals(empty))
		{
			saveConstent(str_value,"0");
		}else
		{
			if(isf!=null&& "1".equals(isf))
			{
				if(str_value.length()>0)
				{
					
					saveConstent(str_value,"1");
				}else
				{
					saveConstent(str_value,"0");
				}			
			}else
				saveConstent(str_value,"0");		
		}
	}
	private boolean searchConstent(String str_value,String type)
    {
    	String sql="select * from constant where upper(constant)='SYS_INFO_PRIV' and type='1'";
    	//System.out.println(sql);
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	boolean isCorrect=false;
    	try {
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				String vv=this.frowset.getString("str_value");
				if(vv!=null&&vv.equalsIgnoreCase(str_value))
				  isCorrect=true;
			}			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isCorrect;
    }
    private void saveConstent(String str_value,String type)
    {
    	String sql="select 1 from constant where upper(constant)='SYS_INFO_PRIV'";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try {
			this.frowset=dao.search(sql);
			if(!this.frowset.next())
			{
				sql="insert into constant (constant,describe) values(?,?)";
				ArrayList list=new ArrayList();
				list.add("SYS_INFO_PRIV");
				list.add("子集及指标授权");
				dao.insert(sql, list);
			}
			sql="update constant set str_value=?,type=? where upper(constant)='SYS_INFO_PRIV'";
			ArrayList list=new ArrayList();
			list.add(str_value);
			list.add(type);
			dao.update(sql, list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
