package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * <p>Title:GetQuerySQLTrans.java</p>
 * <p>Description:考核关系/查询</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-04-20 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class GetQuerySQLTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		ArrayList relationList = (ArrayList) this.getFormHM().get("relation");
		ArrayList fielditemidList = (ArrayList) this.getFormHM().get("fielditemid");
		ArrayList operateList = (ArrayList) this.getFormHM().get("operate");
		ArrayList values = (ArrayList) this.getFormHM().get("values");
		// 查询类型
		String like = (String) this.getFormHM().get("like");
		String sql = getSQL(relationList, fielditemidList, operateList, values, like);

        this.getFormHM().put("sql", sql);
    }
    
    public String getSQL(ArrayList relationList, ArrayList fielditemidList, ArrayList operateList, ArrayList values, String like)
    {

		StringBuffer sql_where = new StringBuffer("");
		StringBuffer sql_head = new StringBuffer("");
		StringBuffer sql_foot = new StringBuffer("");
		// 把表放到集合里
		String tablename = "";
		for (int i = 0; i < fielditemidList.size(); i++)
		{
		    String fielditemid = (String) fielditemidList.get(i);
		    fielditemid = fielditemid.replaceAll("unit", "codeitemid");
		    fielditemid = fielditemid.replaceAll("departid", "codeitemid");
		    String[] fielditems = fielditemid.split("§§");
		    tablename = fielditems[3];
		}
	
		sql_head.append("select object_id FROM " + tablename);
		sql_where.append(" WHERE (");
		StringBuffer tempStr = new StringBuffer();
		for (int i = 0; i < relationList.size(); i++)
		{
		    String relation = (String) relationList.get(i);
		    relation = PubFunc.keyWord_reback(relation);
		    String fielditemid = (String) fielditemidList.get(i);
		    String operate = (String) operateList.get(i);
		    operate = PubFunc.keyWord_reback(operate);
		    String value = (String) values.get(i);
		    tempStr.append(getNodeSql(relation, fielditemid, operate, value, like));
		}
		if (tempStr.length() > 3)
		    tempStr = new StringBuffer(tempStr.substring(4).toString());
		sql_where.append(tempStr.toString());
		sql_where.append(") ");
	
		return (sql_head.toString() + sql_where.toString() + sql_foot.toString());
    }

    public String getNodeSql(String relation, String fielditemid, String operate, String value, String like)
    {

		StringBuffer node_sql = new StringBuffer("");
		fielditemid = fielditemid.replaceAll("unit", "codeitemid");
		fielditemid = fielditemid.replaceAll("departid", "codeitemid");
	
		String[] fielditems = fielditemid.split("§§");
		if ("*".equals(relation))
		    node_sql.append(" and (");
		else
		    node_sql.append(" or  (");
	
		String itemid = fielditems[0];
		String itemtype = fielditems[1];
		String itemsetid = fielditems[2];
	
		if ("N".equalsIgnoreCase(itemtype))
		{
		    if (value.trim().length() == 0)
		    {
		    	value = "0";
		    	if("=".equalsIgnoreCase(operate))
		    		node_sql.append((itemid + operate + value) + " or " + itemid + " is null");
		    	else if(("<>".equalsIgnoreCase(operate)) || ("!=".equalsIgnoreCase(operate)))
		    		node_sql.append((itemid + operate + value) + " or " + itemid + " is not null");		    		
		    }else
		    {
		    	node_sql.append(itemid + operate + value);
		    }	
		} else if ("A".equalsIgnoreCase(itemtype))
		{
		    if ("0".equals(itemsetid))// 非代码型
		    {
				if ("1".equals(like))// 模糊查询
				{
				    if (value.trim().length() > 0)
				    	node_sql.append(itemid + " like " + "'%" + value + "%'");
				    else
				    	node_sql.append("1=1");
				} else
				{
				    if (value.trim().length() == 0)
				    {		//oracle库中<>'' 用 is not null 代替
						if("=".equalsIgnoreCase(operate))
						    node_sql.append("("+itemid + "='' or "+itemid +" is null)");					
						else if("<>".equalsIgnoreCase(operate))
							node_sql.append("("+itemid + "<>'' or "+itemid +" is not null)");
						else
						    node_sql.append(itemid + operate + "''");
				    }			
				    else
				    	node_sql.append(itemid + operate + "'" + value + "'");
				}
	
		    } else
		    // 代码型
		    {
		    	if (value.trim().length() == 0)
				{//oracle库中<>'' 用 is not null 代替
		    		if("=".equalsIgnoreCase(operate))
		    			node_sql.append("("+itemid + "='' or "+itemid +" is null)");
					else if("<>".equalsIgnoreCase(operate))
						node_sql.append("("+itemid + "<>'' or "+itemid +" is not null)");
					else
						node_sql.append(itemid + operate + "''");
				}else
				    node_sql.append(itemid + operate + "'" + value + "'");
		    }
		    
		} else if ("D".equalsIgnoreCase(itemtype))
		    node_sql.append(getDataValue(itemid, operate, value));
	
		node_sql.append(")");
		return node_sql.toString();
    }

    public String getDataValue(String fielditemid, String operate, String value)
    {

		StringBuffer a_value = new StringBuffer("");
		GregorianCalendar d = new GregorianCalendar();
	
		try
		{
		    if (value == null || value.trim().length() == 0)
		    {
				a_value.append(" ( ");
				a_value.append(Sql_switcher.year(fielditemid) + "=0");
				a_value.append(" ) ");
				
		    } else
		    {
				if ("=".equals(operate))
				{
				    a_value.append("(");
				    a_value.append(Sql_switcher.year(fielditemid) + operate + value.substring(0, 4) + " and ");
				    a_value.append(Sql_switcher.month(fielditemid) + operate + value.substring(5, 7) + " and ");
				    a_value.append(Sql_switcher.day(fielditemid) + operate + value.substring(8));
				    a_value.append(" ) ");
				    
				} else
				{
				    a_value.append("(");
				    a_value.append(Sql_switcher.year(fielditemid) + operate + value.substring(0, 4) + " or ( ");
				    a_value.append(Sql_switcher.year(fielditemid) + "=" + value.substring(0, 4) + " and " + Sql_switcher.month(fielditemid) + operate + value.substring(5, 7) + " ) or ( ");
				    a_value.append(Sql_switcher.year(fielditemid) + "=" + value.substring(0, 4) + " and " + Sql_switcher.month(fielditemid) + "=" + value.substring(5, 7) + " and "
					    + Sql_switcher.day(fielditemid) + operate + value.substring(8));
				    a_value.append(") ) ");
				}
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return a_value.toString();
    }

}
