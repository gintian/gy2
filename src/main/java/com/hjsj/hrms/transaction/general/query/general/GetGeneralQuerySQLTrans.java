package com.hjsj.hrms.transaction.general.query.general;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetGeneralQuerySQLTrans extends IBusiness {

	public void execute() throws GeneralException {
		 ArrayList relationList=(ArrayList)this.getFormHM().get("relation");
		 ArrayList fielditemidList=(ArrayList)this.getFormHM().get("fielditemid");
		 ArrayList operateList=(ArrayList)this.getFormHM().get("operate");
		 ArrayList values=(ArrayList)this.getFormHM().get("values");
		 String    tableName=(String)this.getFormHM().get("tableName");
		
		 String sql=getSQL(relationList,fielditemidList,operateList,values,tableName);
		 
		 //zxj 20140917 使用公共方法加密sql
		 this.getFormHM().put("sql",PubFunc.encrypt(sql));
	}
	
	
	
	
	public String getSQL(ArrayList relationList,ArrayList fielditemidList,ArrayList operateList,ArrayList values ,String tableName)
	{	
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet = null;
		StringBuffer sql=new StringBuffer("");
		for(int i=0;i<relationList.size();i++)
		{
			String relation=(String)relationList.get(i);
			String fielditemid=(String)fielditemidList.get(i);
			String operate=(String)operateList.get(i);
			operate=PubFunc.keyWord_reback(operate);
			String hh=(String)values.get(i);
			String value=PubFunc.getStr(SafeCode.decode(hh));
			String sqla0110 = "  select * from UsrA01 where A0101 = '"+value+"' ";
			String hhname = "";
			try {
				rowSet=dao.search(sqla0110);
				while(rowSet.next())
				{
					hhname = rowSet.getString("A0101");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if("".equals(hhname)||hhname.equals(null))
				hhname=value;
			hhname=hhname.replaceAll("\"", "\\\\\"");
			sql.append(getNodeSql(relation,fielditemid,operate,hhname));
		}
		if(sql.length()>3)
			return sql.substring(4);	
		else
			return sql.toString();
	}
	
	
	
	public String getNodeSql(String relation,String fielditemid,String operate,String value)
	{
		
		StringBuffer node_sql=new StringBuffer("");
		fielditemid=fielditemid.replaceAll("unit","codeitemid");
		fielditemid=fielditemid.replaceFirst("unit", "codeitemid");
		fielditemid=fielditemid.replaceFirst("departid","codeitemid");
		String[] fielditems=fielditemid.split("§§");
		if("*".equals(relation))
			node_sql.append(" and ");
		else
			node_sql.append(" or  ");

		if("N".equalsIgnoreCase(fielditems[1]))
		{
			if(value.trim().length()==0)
				value="0";
			node_sql.append(fielditems[0]+operate+value);
		
		}
		else if("A".equalsIgnoreCase(fielditems[1]))
		{
			if("0".equals(fielditems[2]))
			{
		
					if(value.trim().length()==0)
					{
						if("<>".equals(operate))
						{
							node_sql.append("("+fielditems[0]+" is not null or ");
							node_sql.append(fielditems[0]+"<>"+"'"+value+"')");  
						}
						else if("=".equals(operate))
						{
							node_sql.append("("+fielditems[0]+" is null or ");
							node_sql.append(fielditems[0]+"="+"'"+value+"')");
						}
						else if(operate.indexOf("=")!=-1)
						{
							node_sql.append("("+fielditems[0]+" is null or ");
							node_sql.append(fielditems[0]+operate+"'"+value+"')");
						}
						else
						{
							node_sql.append(fielditems[0]+operate+"'"+value+"'");
						}
					}
					else
					{
						
						//node_sql.append(fielditems[0]+" like "+"'%"+value+"%'");
                        if("z05.z0505".equalsIgnoreCase(fielditems[0])|| "z05.z0507".equalsIgnoreCase(fielditems[0]))
                        {
                        	String a0100=this.getA0100ByA0101(value);
                        	if(a0100==null|| "".equals(a0100))
                        	{
                        		if("<>".equals(operate))
        						{
        							node_sql.append("(("+fielditems[0]+" is not null and "+fielditems[0]+"<>'"+value+"') or "+fielditems[0]+"='' or ");
        							node_sql.append(fielditems[0]+" <> "+"'"+value+"')");  
        						}
        						else if("=".equals(operate))
        						{
        							node_sql.append(fielditems[0]+" ="+"'"+value+"'");
        						}
        						else
        						{
        							node_sql.append(fielditems[0]+operate+"'"+value+"'");
        						}
                        	}
                        	else
                        	{
                            	if("=".equals(operate))
                            	{
                            		node_sql.append("("+fielditems[0]+" like '%"+a0100+"%')");
                            	}
                            	else if("<>".equals(operate))
                            	{
                            		node_sql.append("(z0501 not in (select z0501 from z05 where "+fielditems[0]+" like '%"+a0100+"%'))");
                            	}
                            	else
                            	{
                            		node_sql.append(fielditems[0]+operate+"'"+value+"'");
                            	}
                        	}
                        }
                        else if("<>".equals(operate))
						{
							node_sql.append("(("+fielditems[0]+" is not null and "+fielditems[0]+"<>'"+value+"') or "+fielditems[0]+"='' or ");
							node_sql.append(fielditems[0]+" <> "+"'"+value+"')");  
						}
						else if("=".equals(operate))
						{
							node_sql.append(fielditems[0]+" like"+"'%"+value+"%'");
						}
						else
						{
							node_sql.append(fielditems[0]+operate+"'"+value+"'");
						}
					}
			}
			else 
			{
				if(value.trim().length()==0)
				{
					if("<>".equals(operate))
					{
						node_sql.append("("+fielditems[0]+" is not null or ");
						node_sql.append(fielditems[0]+"<>"+"'"+value+"')");  
					}
					else if("=".equals(operate))
					{
						node_sql.append("("+fielditems[0]+" is null or ");
						node_sql.append(fielditems[0]+"="+"'"+value+"')");
					}
					else if(operate.indexOf("=")!=-1)
					{
						node_sql.append("("+fielditems[0]+" is null or ");
						node_sql.append(fielditems[0]+operate+"'"+value+"')");
					}
					else
					{
						node_sql.append(fielditems[0]+operate+"'"+value+"'");
					}
				}
				else
				{
					
					if("<>".equals(operate))
					{
						node_sql.append("(("+fielditems[0]+" is not null and "+fielditems[0]+"<>'"+value+"') or "+fielditems[0]+"='' or ");
						node_sql.append(fielditems[0]+"<>"+"'"+value+"')");  
					}
					else if("=".equals(operate))
					{
						node_sql.append(fielditems[0]+" ="+"'"+value+"'");
					}
					else
					{
						node_sql.append(fielditems[0]+operate+"'"+value+"'");
					}
				}
			}
		}
		else
			node_sql.append(getDataValue(fielditems[0],operate,value));
		return node_sql.toString();
	}

	
	
	public String getDataValue(String fielditemid,String operate,String value)
	{
		StringBuffer a_value=new StringBuffer("");
		try
		{
				if(value==null||value.trim().length()==0)
				{
					a_value.append(" ( ");
					if("=".equals(operate))
						a_value.append(fielditemid+" is null or "+fielditemid+"=''");
					else if("<>".equals(operate))
						a_value.append(fielditemid+" is not null ");
					else
				    	a_value.append(Sql_switcher.year(fielditemid)+"=0");
					a_value.append(" ) "); 
				}
				else
				{
					if("=".equals(operate))
					{
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
						a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
						a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
						a_value.append(" ) ");
					}
					else 
					{
						boolean flag=false;
						if(">=".equalsIgnoreCase(operate))
						{
							flag=true;
							operate=">";
							a_value.append("(");
							a_value.append("(");
							a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4));
							a_value.append( " and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7));
							a_value.append(" and "+Sql_switcher.day(fielditemid)+"="+value.substring(8));
							a_value.append(") or ");
						}
						if("<=".equalsIgnoreCase(operate))
						{
							flag=true;
							operate="<";
							a_value.append("(");
							a_value.append("(");
							a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4));
							a_value.append( " and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7));
							a_value.append(" and "+Sql_switcher.day(fielditemid)+"="+value.substring(8));
							a_value.append(") or ");
						}
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
						a_value.append(") ) ");
						if(flag)
							a_value.append(")");
					}
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_value.toString();
	}
	
   public String getA0100ByA0101(String a0101)
   {
	   String a0100="";
	   try
	   {
		   ContentDAO dao = new ContentDAO(this.getFrameconn());
		   this.frowset=dao.search("select a0100 from usra01 where a0101='"+a0101+"'");
		   while(this.frowset.next())
		   {
			   a0100=this.frowset.getString("a0100");
		   }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return a0100;
   }
	
	public static void main(String[] arg)
	{
		System.out.println("dfasd*dfad".replaceAll("\\*","\\%"));
	}
}
