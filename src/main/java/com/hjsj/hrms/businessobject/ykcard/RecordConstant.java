package com.hjsj.hrms.businessobject.ykcard;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;

import javax.sql.RowSet;
import java.sql.Connection;

public class RecordConstant {
    private Connection conn;
    private String constant="SS_SETCARD_RECORD";
	public RecordConstant()
	{
		
	}
	public RecordConstant(Connection conn)
	{
		this.conn=conn;
	}
	public String  searchConstant()
	{
		String str_value="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select str_value from constant where constant='"+this.constant+"'";
			RowSet rs=dao.search(sql);
			if(rs.next())
			{
				str_value=rs.getString("str_value");
				if(str_value==null||str_value.length()<=0)
				{
					str_value="0";
					save("up",str_value);
				}
			}else
			{
				str_value="0";
				save("add",str_value);
			}
		}catch(Exception ex)		
		{
			
			ex.printStackTrace();
		}
		return str_value;
	}
    public void save(String flag,String str_value)
    {
    	RecordVo vo=new RecordVo("constant");
		vo.setString("constant",this.constant);	
		vo.setString("str_value",str_value);
		vo.setString("describe","薪酬表记录方式");
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if("add".equals(flag))
			{
				dao.addValueObject(vo);
			}else if("up".equals(flag))
			{
				dao.updateValueObject(vo);
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    }
}
