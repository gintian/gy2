package com.hjsj.hrms.businessobject.gz;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:SendEmailBo.java</p>
 * <p>Decsription:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-7 13:17:56</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class SendEmailBo {
	private Connection conn;
	public SendEmailBo()
	{
		
	}
	public SendEmailBo(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 取得所有薪资发放的邮件模板
	 * @return ArrayList
	 */
	public ArrayList getEmailTemplateList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select id,name from email_name where nmodule=2 order by id";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs=dao.search(sql);
			while(rs.next())
			{
				list.add(new CommonData(rs.getString("id"),rs.getString("name")));
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 删除模板
	 * @param id
	 * @param tableName
	 */
	public void deleteTemplate(String id,String tableName)
	{
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("delete from ");
			sql.append(tableName);
			sql.append(" where id in (");
			sql.append(id);
			sql.append(")");
			ContentDAO dao= new ContentDAO(this.conn);
			dao.delete(sql.toString(),new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 判断当前模板是否已经有当前部门或单位的数据
	 * @param id
	 * @param codevalue
	 * @param tableName
	 * @return
	 */
	public boolean hasData(String id,String codevalue,String tableName)
	{
		boolean flag=false;
		try
		{
			StringBuffer buf= new StringBuffer();
			if(!(codevalue==null|| "".equals(codevalue)))
			{
				String code=codevalue.substring(0,2);
				String value=codevalue.substring(2);
				buf.append(" select e.a0100 from email_content e,");
				buf.append(tableName);
				buf.append(" s where e.a0100=s.a0100 and e.pre=s.nbase ");
				if("UN".equalsIgnoreCase(code))
				{
					buf.append(" and  (e.b0110 like '");
					buf.append(value);
					buf.append("%' ");
					if(value==null|| "".equals(value))
					{
						buf.append(" or e.b0110 is null");
					}
					buf.append(")");
				
				}
				if("UM".equalsIgnoreCase(code))
				{
					buf.append(" and s.e0122 like '");
					buf.append(value+"%' ");
				}
			}
			buf.append(" and e.id=");
			buf.append(id);
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs= null;
			rs=dao.search(buf.toString());
			while(rs.next())
			{
				flag=true;
				break;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
