package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.log4j.Category;

import java.sql.Connection;
import java.util.ArrayList;
public class ChangeFactoryId  {
	private Connection conn=null;
	
	public ChangeFactoryId(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 初始化id_factory 的状态
	 * @return
	 */
	public  void changeId(ArrayList slist) throws GeneralException
	{

	    StringBuffer sbs=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.conn);
	    try{
	    	for(int m=0;m<slist.size();m++)
	    	{
	    	  sbs.delete(0,sbs.length());
	    	  sbs.append("update id_factory set currentid='0' where UPPER(sequence_name)='");
	    	  sbs.append(slist.get(m).toString().toUpperCase());
	    	  sbs.append("'");	    	 
	    	  dao.update(sbs.toString());
	    	}
	    	
	    }catch(Exception exx)
	     {
	       exx.printStackTrace();
	       throw GeneralExceptionHandler.Handle(exx);
	     }
	}
	/**
	 * 删除全部数据
	 * @return
	 */
	public  void delAll(String name) throws GeneralException
	{
	    StringBuffer del=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.conn);
	    try{
	    	
	    	
	    	del.append("delete from ");
	    	del.append(name);	    	
	    	dao.delete(del.toString(),new ArrayList());

	    }catch(Exception exx)
	     {
	       exx.printStackTrace();
	       throw GeneralExceptionHandler.Handle(exx);
	     }

	}
	/**
	 * 条件删除数据
	 * @return
	 */
	public  void delWhere(String sql,String name,String str1,String str2,String flag) throws GeneralException
	{

	    StringBuffer del=new StringBuffer();
	    ContentDAO dao=new ContentDAO(this.conn);
	    try{
	    	
	    	del.append("delete from ");
	    	del.append(sql);
	    	del.append(" where ");
	    	if("D".equals(flag))
	    	{
	    		/*
	    		 * 因为Q19与Q25表中 Z3字段格式为 2009.10.21 在用 to_tate 就会转成 1929-02-20 会出现 ORA-01861:
	    		 *  文字与格式字符串不匹配 错误;wy
	    		 */ 
	    		if("q19".equalsIgnoreCase(sql)|| "q25".equalsIgnoreCase(sql))
	    		{
	    			del.append(name+"z3");
		    		del.append(">=");
		    		del.append("'"+str1+"'");
		    		del.append(" and ");
		    		del.append(name+"z3");
		    		del.append("<=");
		    		del.append("'"+str2+"'");
	    		}else
	    		{
	    			del.append(name+"z3");
		    		del.append(">=");
		    		del.append(Sql_switcher.dateValue(str1+" 00:00:00"));
		    		del.append(" and ");
		    		del.append(name+"z3");
		    		del.append("<=");
		    		del.append(Sql_switcher.dateValue(str2 +" 23:59:59"));
	    		}
	    	}else
	    	{
	    		if("Q05".equalsIgnoreCase(sql)|| "Q09".equalsIgnoreCase(sql)|| "Q05_ARC".equalsIgnoreCase(sql)|| "Q09_ARC".equalsIgnoreCase(sql))
	    		{
	    			RegisterDate registerDate=new RegisterDate();
	    			ArrayList list=registerDate.getKq_duration(str1,str2,conn);
	    			if(list==null||list.size()<=0)
	    				return;
	    			for(int i=0;i<list.size();i++)
	    			{
	    			   StringBuffer sqlB=new StringBuffer();
	    			   sqlB.append(del.toString());
	    			   sqlB.append(name+"='"+list.get(i).toString()+"'");
	    			   dao.delete(sqlB.toString(),new ArrayList());	  
	    			}
	    			return;
	    		}else if ("Q33".equalsIgnoreCase(sql))
	    		{
	    			del.append("q3303>='");
			    	del.append(str1+"' and ");
			    	del.append("q3303<='");
			    	del.append(str2);
			    	del.append("'");
	    		}else
	    		{
	    			del.append(name+">='");
			    	del.append(str1+"' and ");
			    	del.append(name+"<='");
			    	del.append(str2);
			    	del.append("'");
	    		}
	    		
	    	}
	    	Category cat = Category.getInstance("com.hrms.frame.dao.DAODebug");
	    	cat.error("考勤初始化-->"+del.toString());    	
	    	dao.delete(del.toString(),new ArrayList());
	    }catch(Exception exx)
	     {
	       exx.printStackTrace();
	       throw GeneralExceptionHandler.Handle(exx);
	     }

	}
    public void insertSQLInit(String sql)
    {
      ContentDAO dao=new ContentDAO(this.conn);
      try
      {
    	  dao.insert(sql,new ArrayList());
      }catch(Exception e)
      {
    	  e.printStackTrace();
      }
    }
    public void updateSQLInit(String sql)
    {
    	ContentDAO dao=new ContentDAO(this.conn);
        try
        {
      	  dao.update(sql);
        }catch(Exception e)
        {
      	  e.printStackTrace();
        }
    }

}
