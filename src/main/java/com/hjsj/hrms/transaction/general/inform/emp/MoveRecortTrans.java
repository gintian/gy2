package com.hjsj.hrms.transaction.general.inform.emp;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

/**
 *<p>Title:MoveRecortTrans</p> 
 *<p>Description:移动记录</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:20010-20-16:下午03:48:28</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class MoveRecortTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String setname = (String)this.getFormHM().get("setname");
			String table = (String)this.getFormHM().get("table");
			String select_record_num = (String)this.getFormHM().get("select_record_num");	
			String select_record = (String)this.getFormHM().get("select_record");
			String move_to_num = (String)this.getFormHM().get("move_to_num");
			String a0100 = (String)this.getFormHM().get("a0100");
			if(!(setname==null || "".equalsIgnoreCase(setname)))
			{
				if("a01".equalsIgnoreCase(setname))
				{
					if(!select_record_num.equals(move_to_num.toString()))
					{
						this.moveA01Record(a0100,select_record_num, move_to_num, table, select_record, dao);
					}
				}else// 更新子集
				{
					this.moveSubsetRecord( a0100, select_record_num, move_to_num, table, select_record, dao);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 移动主集记录
	 * @param select_record_num
	 * @param move_to_num
	 * @param table
	 * @param select_record
	 * @param dao
	 */
	public void moveA01Record(String a0100,String select_record_num,String move_to_num,String table,String select_record,ContentDAO dao)
	{

//		 向前移动
		if(Integer.parseInt(select_record_num)>Integer.parseInt(move_to_num))
		{
			if(Integer.parseInt(move_to_num)==1)
			{	// 移动到第一条记录
				this.operA0000( table, select_record, dao);
				this.updateA0000(table,a0100, dao);
			}else{
				this.operA0000(table,select_record, move_to_num, dao);
				this.updateA0000(table,a0100,move_to_num,dao);
			}
		}else // 向后移动
		{
			int recordnum = this.getAllRecordNum(table,dao);
			if(recordnum>0)
			{
				if(Integer.parseInt(move_to_num)==recordnum 
						|| Integer.parseInt(move_to_num)>recordnum )
				{   // 移动到最后一条记录
					String getRecordnum = recordnum+"";
					//最后一条记录的a0000
					this.getA0000Three(table, select_record, dao);
					this.updateA0000Three( table, a0100, getRecordnum, dao);
				}else{
					this.operA0000Two( table,select_record,move_to_num, dao);
					this.updateA0000Two(table,a0100,move_to_num,dao);
				}
			}
		}
	}
	/**
	 * 移动子集记录
	 * @param a0100
	 * @param select_record_num
	 * @param move_to_num
	 * @param table
	 * @param select_record
	 * @param dao
	 */
	public void moveSubsetRecord(String a0100,String select_record_num,String move_to_num,String table,String select_record,ContentDAO dao)
	{
		String getRecordNum=this.getRecordNum(table, a0100, select_record, dao);
		if(!getRecordNum.equals(move_to_num.toString()))
		{
			
			    // 向前移动
			if(Integer.parseInt(getRecordNum)>Integer.parseInt(move_to_num))
			{
				if(Integer.parseInt(move_to_num)==1)
				{	// 移动到第一条记录
					int flag = this.checkSubset(table,a0100,dao);
					if(flag!=0)
					{
						this.operI9999(table, a0100, select_record, dao);
						this.updateI9999(table, a0100, select_record, dao);
						this.operI9999Three(table, a0100, select_record, dao);
					}
				}else{
					int flag = this.checkSubset(table,a0100,dao);
					if(flag!=0)
					{
						String maxI9999 = this.getMaxI9999(table,a0100,dao);
						this.operSelectRecord(table,a0100,maxI9999,select_record, dao);
						String move_to_I9999 = this.getI9999(table,a0100,move_to_num,dao);
						this.operI9999(table,a0100,select_record, move_to_I9999, dao);
						this.updateI9999(table,a0100, maxI9999, move_to_I9999, dao);
						this.operI9999Two(table,a0100,select_record, move_to_num, dao);
					}
				}
			}else// 向后移动
			{
				int recordnum = getAllRecordNum(table,a0100,dao);
				if(recordnum>0)
				{
					if(Integer.parseInt(move_to_num)==recordnum 
							|| Integer.parseInt(move_to_num)>recordnum )
					{  // 移动到最后一条记录
						String maxI9999 = this.getMaxI9999(table,a0100,dao);
						this.operI9999Two(table,a0100,select_record,dao);
						this.updateI9999Two(table,a0100,maxI9999,select_record,dao);
						this.operI9999Four(table,a0100,select_record,dao);
					}else{
						String maxI9999 = this.getMaxI9999(table,a0100,dao);
						this.operSelectRecord( table,a0100,maxI9999,select_record, dao);
						String move_to_I9999 = this.getI9999Two(table,a0100,move_to_num,dao);
						this.operI9999Three(table,a0100,select_record,move_to_I9999,dao);
						this.updateI9999(table,a0100, maxI9999, move_to_I9999, dao);
						this.operI9999Four(table,a0100,select_record,dao);
					}
				}
			}
		}	
	}
	/**
	 * 获得子集记录条数
	 * @param table
	 * @param a0100
	 * @param select_record
	 * @param dao
	 * @return
	 */
	public String getRecordNum(String table,String a0100,String select_record,ContentDAO dao)
	{
		RowSet rs;
		String retstr = "";
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select i9999 from "+table);
		sb.append(" where a0100='"+a0100+"'");
		sb.append(" order by i9999");
		try
		{
			rs = dao.search(sb.toString());
			int i =0;
			while(rs.next())
			{
				i++;
				retint = rs.getInt("i9999");
				if(retint==Integer.parseInt(select_record))
				{			
					break;
				}
			}
			//System.out.println(i);
			retstr = i+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	/**
	 * 获得子集i9999
	 * @param table
	 * @param a0100
	 * @param move_to_num
	 * @param dao
	 * @return
	 */
	public String getI9999(String table,String a0100,String move_to_num,ContentDAO dao)
	{
		RowSet rs;
		String retstr = "";
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select i9999 from "+table);
		sb.append(" where a0100='"+a0100+"'");
		sb.append(" order by i9999");
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			int i =0;
			while(rs.next())
			{
				i++;
				if(i==Integer.parseInt(move_to_num))
				{			
					retint = rs.getInt("i9999");
					break;
				}
			}
			//System.out.println(i);
			retstr = retint+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	public String getI9999Two(String table,String a0100,String move_to_num,ContentDAO dao)
	{
		RowSet rs;
		String retstr = "";
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select i9999 from "+table);
		sb.append(" where a0100='"+a0100+"'");
		sb.append(" order by i9999");
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			int i =0;
			while(rs.next())
			{
				i++;
				if(i==Integer.parseInt(move_to_num)-1)
				{			
					retint = rs.getInt("i9999");
					break;
				}
			}
			//System.out.println(i);
			retstr = retint+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	/**
	 * 在主集表，移动到第一条记录
	 * @param table
	 * @param select_record
	 * @param dao
	 */
	public void operA0000(String table,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set a0000=a0000+1 ");
		sb.append(" where  a0000 < "+select_record);
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 在主集表，移动到第一条记录
	 * @param table
	 * @param select_record
	 * @param dao
	 */
	public void updateA0000(String table,String a0100,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set a0000=1 ");
		sb.append(" where  A0100 = '"+a0100+"'");
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public int checkSubset(String table,String a0100,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select  i9999 from "+table);
		sb.append(" where a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retint = rs.getInt("i9999");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retint;
	}
	/**
	 * 获得记录总数
	 * @param table
	 * @param dao
	 * @return
	 */
	public int getAllRecordNum(String table, ContentDAO dao)
	{
		RowSet rs;
		int i =0;
		StringBuffer sb = new StringBuffer();
		sb.append(" select  max(a0000) as nums from "+table);
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				i=rs.getInt("nums");
			}
			//System.out.println(i);
		}catch(Exception e){
			e.printStackTrace();
		}
		return i;
	}
	/**
	 * 获得记录总数
	 * @param table
	 * @param dao
	 * @return
	 */
	public int getAllRecordNum(String table, String a0100,ContentDAO dao)
	{
		RowSet rs;
		int i =0;
		StringBuffer sb = new StringBuffer();
		sb.append(" select i9999 from "+table);
		sb.append(" where a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			while(rs.next())
			{
				i++;
			}
			//System.out.println(i);
		}catch(Exception e){
			e.printStackTrace();
		}
		return i;
	}
	public String getMaxI9999(String table,String a0100,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		String retstr = "";
		sb.append(" select max(i9999) as i9999 from "+table);
		sb.append(" where a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retint = rs.getInt("i9999");
			}
			retstr = retint+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	public void operI9999(String table,String a0100,String select_record,String move_to_I9999,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999+10000 ");
		sb.append(" where  i9999>="+move_to_I9999);
		sb.append(" and  i9999<"+select_record);
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void operI9999Two(String table,String a0100,String select_record,String move_to_num,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999-9999 ");
		sb.append(" where  i9999>=10000");
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 子集表，移动到第一条记录
	 * @param table
	 * @param a0100
	 * @param select_record
	 * @param dao
	 */
	public void operI9999(String table,String a0100,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999+10000 ");
		sb.append(" where  i9999 < "+select_record);
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void operI9999Three(String table,String a0100,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999-9999 ");
		sb.append(" where  i9999 > 10000");
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 子集表，移动到最后一条记录
	 * @param table
	 * @param a0100
	 * @param select_record
	 * @param dao
	 */
	public void operI9999Two(String table,String a0100,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999+10000 ");
		sb.append(" where  i9999 > "+select_record);
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 子集表，移动到最后一条记录
	 * @param table
	 * @param a0100
	 * @param select_record
	 * @param dao
	 */
	public void operI9999Four(String table,String a0100,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999-10001 ");
		sb.append(" where  i9999 > 10000");
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void operI9999Three(String table,String a0100,String select_record,String move_to_I9999,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999+10000 ");
		sb.append(" where  i9999<="+move_to_I9999);
		sb.append(" and  i9999>"+select_record);
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void operI9999Four(String table,String a0100,String select_record,String move_to_num,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=i9999-10001 ");
		sb.append(" and  i9999>10000");
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void operSelectRecord(String table,String a0100,String maxI9999,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999="+maxI9999+"+10 ");
		sb.append(" where  i9999="+select_record);
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateI9999(String table,String a0100,String maxI9999,String move_to_I9999,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999="+move_to_I9999);
		sb.append(" where  i9999="+maxI9999+"+10");
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 子集表，移动到最后一条记录
	 * @param table
	 * @param a0100
	 * @param maxI9999
	 * @param move_to_num
	 * @param dao
	 */
	public void updateI9999Two(String table,String a0100,String maxI9999,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999="+ maxI9999);
		sb.append(" where  i9999="+select_record);
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 子集表，移动到第一条记录
	 * @param table
	 * @param a0100
	 * @param select_record
	 * @param dao
	 */
	public void updateI9999(String table,String a0100,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set i9999=1");
		sb.append(" where  i9999="+select_record);
		sb.append(" and a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getMaxA0000(String table,ContentDAO dao)
	{
		RowSet rs;
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		String retstr = "";
		sb.append(" select max(a0000) as a0000 from "+table);
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				retint = rs.getInt("a0000");
			}
			retstr = retint+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	
	public void operA0000(String table,String select_record,String move_to_a0000,ContentDAO dao)
	{

		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set A0000=A0000+1 ");
		sb.append(" where  A0000>="+move_to_a0000);
		sb.append(" and  A0000<"+select_record);
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void operA0000Two(String table,String select_record,String move_to_a0000,ContentDAO dao)
	{

		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set a0000=a0000-1 ");
		sb.append(" where  a0000<="+move_to_a0000);
		sb.append(" and  a0000>"+select_record);
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void operSelectA0000Record(String table,String maxA0000,String a0100,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set a0000="+maxA0000+"+100 ");
		sb.append(" where  a0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateA0000(String table,String a0100,String move_to_a0000,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set a0000="+move_to_a0000);
		sb.append(" where  A0100='"+a0100+"'");
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateA0000Two(String table,String a0100,String move_to_num,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set a0000="+move_to_num);
		sb.append(" where  A0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateA0000Three(String table,String a0100,String move_to_a0000,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table+" set a0000="+move_to_a0000);
		sb.append(" where  A0100='"+a0100+"'");
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String getA0000(String table,String move_to_num,ContentDAO dao)
	{
		RowSet rs;
		String retstr = "";
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select a0000 from "+table);
		sb.append(" order by a0000");
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			int i =0;
			while(rs.next())
			{
				i++;
				if(i==Integer.parseInt(move_to_num)-1)
				{			
					retint = rs.getInt("a0000");
					break;
				}
			}
			//System.out.println(i);
			retstr = retint+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	
	public String getA0000Two(String table,String move_to_num,ContentDAO dao)
	{
		RowSet rs;
		String retstr = "";
		StringBuffer sb = new StringBuffer();
		int retint = 0;
		sb.append(" select a0000 from "+table);
		sb.append(" order by a0000");
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			int i =0;
			while(rs.next())
			{
				i++;
				if(i==Integer.parseInt(move_to_num)+1)
				{			
					retint = rs.getInt("a0000");
					break;
				}
			}
			retstr = retint+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return retstr;
	}
	
	public void getA0000Three(String table,String select_record,ContentDAO dao)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" update  "+table);
		sb.append(" set A0000=A0000-1 where A0000>"+select_record);
		//System.out.println(sb.toString());
		try
		{
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
