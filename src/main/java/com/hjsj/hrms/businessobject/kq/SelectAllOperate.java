package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.kq.machine.ReconstructionKqField;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;

public class SelectAllOperate {
    private Connection conn;
    private UserView userView;
	public SelectAllOperate(){};
	public SelectAllOperate(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}
    public void allOperate(String table)throws GeneralException
    {
    	ReconstructionKqField reconstructionKqField=new ReconstructionKqField(this.conn);
    	ArrayList list=new ArrayList();
    	Field temp = new Field("state","状态标志");
		temp.setDatatype(DataType.STRING);	
		temp.setLength(2);		
		temp.setKeyable(false);			
		temp.setVisible(false);
		list.add(temp);
		if(!reconstructionKqField.checkFieldSave(table,"state"))
		{
			if(!reconstructionKqField.ceaterField_originality(list,table))
				throw GeneralExceptionHandler.Handle(new GeneralException("","重构数据表错误","","")); 
		}
    }
    /**
     * 申请表的全选
     * @param kind
     * @param code
     * @param table
     * @param where_is
     * @param sql_db_list
     * @param state
     */
    public void allSelectApp(String kind,String code,String table,String where_is,ArrayList sql_db_list,String state)
	{
				
		StringBuffer sql=new StringBuffer();
		String strWhere=null;
	    /**指定的考勤人员库，从参数取得*/
		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.conn);
	    if(code==null||code.length()<=0)
	    {
	    	LazyDynaBean bean=RegisterInitInfoData.getKqPrivCodeAndKind(userView);
	    	code=(String)bean.get("code");
	    	kind=(String)bean.get("kind");	    	
	    }		
	    ContentDAO dao=new ContentDAO(this.conn);
		ArrayList fieldlist=new ArrayList();
		try
		{
			for(int i=0;i<sql_db_list.size();i++)//for i loop end.
			{
				sql=new StringBuffer();
				String expr="1";
		        String factor="";
		        if("2".equals(kind))
		        {
		    		 factor="B0110=";
		    		 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 factor+="%`";
					 }
					 else
					 {
					    expr="1+2";
					   factor+=code;
					   factor+="%`B0110=`";	
					 }
		        }
				else if("1".equals(kind)){
				   	    factor="E0122="; 
				   	 if(code!=null && code.length()>0)
					 {
						 factor+=code;
						 factor+="%`";
					 }
					 else
					 {
					    expr="1+2";
					   factor+=code;
					   factor+="%`E0122=`";	
					 }
				 }
				 else if("0".equals(kind)){
				   	    factor="E01A1=";
				   	    if(code!=null && code.length()>0)
					    {
						 factor+=code;
						 factor+="%`";
					    }
			            else
				        {
					      expr="1+2";
					      factor+=code;
					      factor+="%`E01A1=`";	
				        }
				 }else{
					 expr="1+2";
						factor="B0110=";
					    kind="2";
						if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
							factor+=userView.getManagePrivCodeValue();
						factor+="%`B0110=`";
				}		        
				/*strWhere=userView.getPrivSQLExpression(expr+"|"+factor,(String)sql_db_list.get(i),false,fieldlist);*/
				if(userView.getKqManageValue()!=null&&!"".equals(userView.getKqManageValue()))
					strWhere=userView.getKqPrivSQLExpression("",(String)sql_db_list.get(i),fieldlist);
			    else
			        strWhere=userView.getPrivSQLExpression(expr+"|"+factor,(String)sql_db_list.get(i),false,fieldlist);
				sql.append("update  "+table+" set state='"+state+"' where  nbase='"+(String)sql_db_list.get(i)+"'");
				sql.append(" and a0100 in(select a0100 "+strWhere+")");
				sql.append(" and "+where_is);
				//System.out.println(sql.toString());				
				dao.update(sql.toString());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
    public void allSelectApp(String table,ArrayList sql_db_list)
	{
				
		StringBuffer sql=new StringBuffer();
		String strWhere=null;
	    /**指定的考勤人员库，从参数取得*/		
	    ContentDAO dao=new ContentDAO(this.conn);		
		try
		{
			for(int i=0;i<sql_db_list.size();i++)//for i loop end.
			{
				sql=new StringBuffer();				
				strWhere=RegisterInitInfoData.getWhereINSql(userView,(String)sql_db_list.get(i));
				sql.append("update  "+table+" set state='0' where state='1' and nbase='"+(String)sql_db_list.get(i)+"'");
				//sql.append(" and a0100 in(select a0100 "+strWhere+")");	
				if(!userView.isSuper_admin())
					if(strWhere!=null&&(strWhere.indexOf("WHERE")!=-1||strWhere.indexOf("where")!=-1))
					  sql.append(" and  EXISTS(select a0100 "+strWhere+" and "+table+".a0100="+(String)sql_db_list.get(i)+"A01.a0100)");
					else
					  sql.append(" and  EXISTS(select a0100 "+strWhere+" where "+table+".a0100="+(String)sql_db_list.get(i)+"A01.a0100)");
				dao.update(sql.toString());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
    public void operateQ05State(ArrayList kq_dbase_list,String kq_duration,String code,String kind,String showtype,String where_c,String state)
    {
    	StringBuffer wheresql=new StringBuffer();
    	wheresql.append(" Q03Z0 = '"+kq_duration+"'");	   	
   		if("1".equals(kind))
   		{
   			wheresql.append(" and e0122 like '"+code+"%'");
   		}else if("0".equals(kind))
   		{
   			wheresql.append(" and e01a1 like '"+code+"%'");	
   		}else
   		{
   			wheresql.append(" and b0110 like '"+code+"%'");	
   		}
   		if(!"all".equals(showtype))
   		{
   			wheresql.append(" and q03z5='"+showtype+"'");	
   		}
   		if(where_c!=null&&where_c.length()>0)
   			wheresql.append(" "+where_c+"");
       try
       {
    	ContentDAO dao=new ContentDAO(this.conn);
   		StringBuffer condition=null;
   		for(int i=0;i<kq_dbase_list.size();i++)
   		{
   			condition=new StringBuffer();
   			String userbase=kq_dbase_list.get(i).toString();
   			String whereIN=RegisterInitInfoData.getWhereINSql(userView,userbase);
   			condition.append("update q05 set state='"+state+"' where ");
   			condition.append(wheresql.toString());
   			condition.append(" and UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
   		    condition.append(" and a0100 in(select a0100 "+whereIN+") "); 
   		    dao.update(condition.toString());
   		}
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }
    }
    public void operateQ03State(ArrayList kq_dbase_list,String start_date,String end_date,String code,String kind,String showtype,String where_c,String state)
    {
    	StringBuffer wheresql=new StringBuffer();    		
    	wheresql.append(" q03z0>='"+start_date+"' and q03z0<='"+end_date+"' ");	
   		if("1".equals(kind))
   		{
   			wheresql.append(" and e0122 like '"+code+"%'");
   		}else if("0".equals(kind))
   		{
   			wheresql.append(" and e01a1 like '"+code+"%'");	
   		}else
   		{
   			wheresql.append(" and b0110 like '"+code+"%'");	
   		}
   		if(!"all".equals(showtype))
   		{
   			wheresql.append(" and q03z5='"+showtype+"'");	
   		}
   		if(where_c!=null&&where_c.length()>0)
   			wheresql.append(" "+where_c+"");
       try
       {
    	ContentDAO dao=new ContentDAO(this.conn);
   		StringBuffer condition=null;
   		for(int i=0;i<kq_dbase_list.size();i++)
   		{
   			condition=new StringBuffer();
   			String userbase=kq_dbase_list.get(i).toString();
   			String whereIN=RegisterInitInfoData.getWhereINSql(userView,userbase);
   			condition.append("update q03 set state='"+state+"' where ");
   			condition.append(wheresql.toString());
   			condition.append(" and UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
   			if(!userView.isSuper_admin())
				if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1))
					condition.append(" and  EXISTS(select a0100 "+whereIN+" and q03.a0100="+userbase+"A01.a0100)");
				else
					condition.append(" and  EXISTS(select a0100 "+whereIN+" where q03.a0100="+userbase+"A01.a0100)");
   			//condition.append(" and  EXISTS(select a0100 "+whereIN+" and q03.a0100="+userbase+"A01.a0100)");
   		    //condition.append(" and a0100 in(select a0100 "+whereIN+") "); 
   		    dao.update(condition.toString());
   		}
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }
    }
    /**
     * 操作权限下的人的状态
     * @param kq_dbase_list
     * @param kq_duration
     * @param state
     */
    public void operateQ05State(ArrayList kq_dbase_list,String kq_duration)
    {
       try
       {
    	ContentDAO dao=new ContentDAO(this.conn);
   		StringBuffer condition=null;
   		for(int i=0;i<kq_dbase_list.size();i++)
   		{
   			condition=new StringBuffer();
   			String userbase=kq_dbase_list.get(i).toString();
   			String whereIN=RegisterInitInfoData.getWhereINSql(userView,userbase);
   			condition.append("update q05 set state='0' where state='1' and");
   			condition.append(" Q03Z0 = '"+kq_duration+"'");
   			condition.append(" and UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
   		    //condition.append(" and a0100 in(select a0100 "+whereIN+") "); 
   			if(!userView.isSuper_admin())
				if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1))
					condition.append(" and  EXISTS(select a0100 "+whereIN+" and q05.a0100="+userbase+"A01.a0100)");
				else
					condition.append(" and  EXISTS(select a0100 "+whereIN+" where q05.a0100="+userbase+"A01.a0100)");
   			//condition.append(" and  EXISTS(select a0100 "+whereIN+" and q05.a0100="+userbase+"A01.a0100)");
   		    dao.update(condition.toString());
   		}
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }
    }  
    /**
     * 操作权限下的人的状态
     * @param kq_dbase_list
     * @param kq_duration
     * @param state
     */
    public void operateQ03State(ArrayList kq_dbase_list,String start_date,String end_date)
    {
       try
       {
    	ContentDAO dao=new ContentDAO(this.conn);
   		StringBuffer condition=null;
   		for(int i=0;i<kq_dbase_list.size();i++)
   		{
   			condition=new StringBuffer();
   			String userbase=kq_dbase_list.get(i).toString();
   			String whereIN=RegisterInitInfoData.getWhereINSql(userView,userbase);
   			condition.append("update q03 set state='0' where state='1' and");
   			condition.append(" q03z0>='"+start_date+"' and q03z0<='"+end_date+"' ");	
   			condition.append(" and UPPER(nbase)='"+kq_dbase_list.get(i).toString().toUpperCase()+"'");
   		    //condition.append(" and a0100 in(select a0100 "+whereIN+") ");
   			if(!userView.isSuper_admin())
				if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1))
					condition.append(" and  EXISTS(select "+userbase+"A01.a0100 "+whereIN+" and q03.a0100="+userbase+"A01.a0100)");
				else
					condition.append(" and  EXISTS(select "+userbase+"A01.a0100 "+whereIN+" where q03.a0100="+userbase+"A01.a0100)");
   			//condition.append(" and  EXISTS(select a0100 "+whereIN+" and q03.a0100="+userbase+"A01.a0100)");
   		    dao.update(condition.toString());
   		}
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }
    } 
}
