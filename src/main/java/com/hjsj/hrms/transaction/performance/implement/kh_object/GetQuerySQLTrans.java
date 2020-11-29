package com.hjsj.hrms.transaction.performance.implement.kh_object;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.*;

/**
 * 条件选择组装sql
 * 
 * @author: JinChunhai
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
		String history = (String) this.getFormHM().get("history");
		String db = (String) this.getFormHM().get("dbpre");
		if (db == null|| "".equals(db))
		    db = "Usr";
	//	String sql = getSQL(relationList, fielditemidList, operateList, values, like, history, db);
		
		//zgd 2015-1-13 条件选择类型 general=通用查询 start
		String sql = "";
		String expression = (String) this.getFormHM().get("expression");
		expression=PubFunc.hireKeyWord_filter_reback(expression);
		String selecttype = (String) this.getFormHM().get("selectType");
		selecttype = selecttype == null?"":selecttype;
	    if("general".equalsIgnoreCase(selecttype)){
	    	sql = getSQL3(expression, fielditemidList, operateList, values, like, history, db);
	    }else{
	    	StringBuffer sexpr=new StringBuffer();
	    	for (int i = 0; i < fielditemidList.size(); i++){
	    		String relation = (String) relationList.get(i);
	    		relation = PubFunc.keyWord_reback(relation);
	    		/**组合表达式串*/
	    		if(i==0){
	    			sexpr.append(1);
	    		}else{
	    			if("".equals(relation))
	    				sexpr.append("*");
	    			else
	    				sexpr.append(relation);
	    			sexpr.append(i+1);    
	    		}
	    	}
	    	if(sexpr!=null){
	    		expression = sexpr.toString();
	    	}else{
	    		expression = "";
	    	}
	    	sql = getSQL3(expression, fielditemidList, operateList, values, like, history, db);
	    }
		//zgd 2015-1-13 条件选择类型 general=通用查询 end
		
		//zgd 2015-1-13 delete sql = getSQL2(relationList, fielditemidList, operateList, values, like, history, db);
	//	StringBuffer buf = new StringBuffer();
	//	String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
	//	if (a_code != null && a_code.trim().length() > 0)
	//	{
	//	    String codesetid = a_code.substring(0, 2);
	//	    String value = a_code.substring(2);
	//	    if (value.length() > 0)
	//	    {
	//
	//		if (codesetid.equalsIgnoreCase("UN"))
	//		{
	//		    buf.append("B0110 like '");
	//		    buf.append(value);
	//		    buf.append("%' ");
	//		} else if (codesetid.equalsIgnoreCase("UM"))
	//		{
	//		    buf.append("E0122 like'");
	//		    buf.append(value);
	//		    buf.append("%' ");
	//		} else if (codesetid.equalsIgnoreCase("@K"))
	//		{
	//		    buf.append("E01A1 like'");
	//		    buf.append(value);
	//		    buf.append("%' ");
	//		}
	//	    }
	//	}


	    if (sql.indexOf("WHERE") > -1)
	    	sql += " and " ;
	    else
	    	sql += " where ";
	    
	    sql += db+"A01.a0100 in (" + this.getPrivEmpStr(db)+")";
	    sql=SafeCode.encode(PubFunc.encrypt(sql));//加密
        this.getFormHM().put("sql", sql);
        this.getFormHM().put("dbpre", db);
    }

    private String getSQL3(String expression, ArrayList fielditemidList,
			ArrayList operateList, ArrayList values, String like,
			String history, String db) throws GeneralException {
    	StringBuffer sql = new StringBuffer();
    	StringBuffer sfactor=new StringBuffer();
    	try {
    		int j=0;
        	for (int i = 0; i < fielditemidList.size(); i++)
        	{
        		j=i+1;
        		String fielditemid = (String) fielditemidList.get(i);
        		String operate = (String) operateList.get(i);
        		operate = PubFunc.keyWord_reback(operate);
        		String value = (String) values.get(i);
    	    
        		fielditemid = fielditemid.replaceAll("unit", "codeitemid");
        		fielditemid = fielditemid.replaceAll("departid", "codeitemid");

        		String[] fielditems = fielditemid.split("§§");
        		String itemid = fielditems[0];
        		String itemtype = fielditems[1];
        		String itemsetid = fielditems[2];	    
    	
        		sfactor.append(itemid.toUpperCase());
        		sfactor.append(operate);
        		String q_value = PubFunc.getStr(value.trim());
        		if("undefined".equalsIgnoreCase(q_value))
        		{
        			q_value="";
        		}
        		sfactor.append(q_value);
//        		if(((itemsetid.equalsIgnoreCase("0")) || (itemsetid.equalsIgnoreCase("UN")) || (itemsetid.equalsIgnoreCase("UM")) || (itemsetid.equalsIgnoreCase("@K"))) && like.equals("1") && (itemtype.equals("A")||itemtype.equals("M")))
        		if("1".equals(like) && ("A".equals(itemtype)|| "M".equals(itemtype)))//市政集团  要求学历等代码型也要支持模糊查询 所以废除原来的UM、UN等代码限制 zhaoxg add 2014-10-21
        		{
        			if(!(q_value==null|| "".equals(q_value)))
        				sfactor.append("*");            
        		}
    	    
        		sfactor.append("`"); 
    	    
        		/**组合表达式串*/
           
        	}
        	if(expression==null|| "".equals(expression))
        		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistexpr"),"",""));
        	/**为了分析用*/
        	if(!isHaveExpression(expression,fielditemidList.size()))
        		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
        	expression=expression.replaceAll("!","-");
        	TSyntax syntax=new TSyntax();
        	if(!syntax.Lexical(expression))
        		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
        	if(!syntax.DoWithProgram())
        		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
    	
        
        	/**表过式分析*/
        	/**非超级用户且对人员库进行查询*/
        	String strwhere="";
        	ArrayList fieldlist=new ArrayList();
        	boolean bhis=false;
        	if("1".equals(history))
        		bhis=true;
        	fieldlist=privFieldList(fieldlist,"1","0");//type=1
//      	if((!userView.isSuper_admin()))
//        	{
//            	strwhere=userView.getPrivSQLExpression(sexpr.toString()+"|"+sfactor.toString(),"Usr",bhis,false,true,fieldlist);   
//        	}
//        	else
        	{																																										
        		FactorList factorslist=new FactorList(expression,sfactor.toString(),db,bhis ,false,true,1,userView.getUserId());
                fieldlist=factorslist.getFieldList();
                strwhere=factorslist.getSqlExpression();
        	}
    			
        	sql.append("select distinct "+db+"A01.a0100  ");
        	sql.append(strwhere);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	return sql.toString();
	}

	/**获得管理权限范围内的人员
     * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
     * 
     * +db  选择人员库
     */
    public String getPrivEmpStr(String db)
    {
		StringBuffer buf = new StringBuffer();
		String operOrg = this.userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
		
		buf.append("select "+db+"A01.A0100  from "+db+"A01");
		
		if (operOrg!=null && operOrg.length() > 3)
		{
			buf.setLength(0);
			StringBuffer tempSql = new StringBuffer("");
			String[] temp = operOrg.split("`");
			for (int i = 0; i < temp.length; i++)
			{
			    if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
			    	tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
			    else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
			    	tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");

			}
			buf.append(" select "+db+"A01.A0100 from "+db+"A01 where  ( " + tempSql.substring(3) + " ) ");
		}
		else if((!this.userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
		{
			buf.setLength(0);
			String priStrSql = InfoUtils.getWhereINSql(this.userView, "Usr");
			buf.append("select "+db+"A01.A0100 ");
			if(priStrSql.length()>0)
				buf.append(priStrSql);
			else
				buf.append(" from "+db+"A01");
		}
		return buf.toString();
    }
    
    public String getSQL(ArrayList relationList, ArrayList fielditemidList, ArrayList operateList, ArrayList values, String like, String history, String db)
    {

		StringBuffer sql_where = new StringBuffer("");
		StringBuffer sql_head = new StringBuffer("");
		StringBuffer sql_foot = new StringBuffer("");
		// 把表放到集合里
		Set tables = new HashSet();
		for (int i = 0; i < fielditemidList.size(); i++)
		{
		    String fielditemid = (String) fielditemidList.get(i);
		    fielditemid = fielditemid.replaceAll("unit", "codeitemid");
		    fielditemid = fielditemid.replaceAll("departid", "codeitemid");
		    String[] fielditems = fielditemid.split("§§");
		    String table_name = fielditems[3];
		    table_name = db + fielditems[3];
		    tables.add(table_name);
		}
	
		sql_head.append("select distinct " + db + "a01.a0100  FROM " + db + "A01 ");
		for (Iterator iter = tables.iterator(); iter.hasNext();)
		{
		    String table_name = (String) iter.next();
		    if (table_name.equals(db + "A01"))
		    	continue;
		    sql_head.append("LEFT JOIN ");
		    sql_head.append(table_name);
		    sql_head.append(" ON UsrA01.A0100=");
		    sql_head.append(table_name);
		    sql_head.append(".A0100 ");
	
		    // 不按历史查询
		    if ("0".equals(history))
		    {
				sql_foot.append(" AND (");
				sql_foot.append(table_name);
				sql_foot.append(".I9999=(select max(I9999) from ");
				sql_foot.append(table_name);
				sql_foot.append(" WHERE ");
				sql_foot.append(table_name);
				sql_foot.append(".A0100=UsrA01.A0100) OR ");
				sql_foot.append(table_name);
				sql_foot.append(".I9999 IS NULL) ");
		    }
		}
	
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
		    tempStr.append(getNodeSql(relation, fielditemid, operate, value, like, db));
		}
		if (tempStr.length() > 3)
		    tempStr = new StringBuffer(tempStr.substring(4).toString());
		sql_where.append(tempStr.toString());
		sql_where.append(") ");
	
		return (sql_head.toString() + sql_where.toString() + sql_foot.toString());
    }

    public String getNodeSql(String relation, String fielditemid, String operate, String value, String like, String db)
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
		String table_name = fielditems[3];
	
		itemid = db + table_name + "." + itemid;
	
		if ("N".equalsIgnoreCase(itemtype))
		{
		    if (value.trim().length() == 0)
		    	value = "0";
		    node_sql.append(itemid + operate + value);
	
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
				    {//oracle库中<>'' 用 is not null 代替
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
				}			
				else
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
    /**调用底层方法 支持自动模糊查询
     *  ?  表示通配 1位 
     *  *  表示通配 n位，就是一串
     * @throws GeneralException 
     */
    public String getSQL2(ArrayList relationList, ArrayList fielditemidList, ArrayList operateList, ArrayList values, String like, String history, String db) throws GeneralException
    {

    	StringBuffer sql = new StringBuffer();
    	StringBuffer sfactor=new StringBuffer();
    	StringBuffer sexpr=new StringBuffer();
    	int j=0;
    	for (int i = 0; i < fielditemidList.size(); i++)
    	{
    		j=i+1;
    		String relation = (String) relationList.get(i);
    		relation = PubFunc.keyWord_reback(relation);
    		String fielditemid = (String) fielditemidList.get(i);
    		String operate = (String) operateList.get(i);
    		operate = PubFunc.keyWord_reback(operate);
    		String value = (String) values.get(i);
	    
    		fielditemid = fielditemid.replaceAll("unit", "codeitemid");
    		fielditemid = fielditemid.replaceAll("departid", "codeitemid");

    		String[] fielditems = fielditemid.split("§§");
    		String itemid = fielditems[0];
    		String itemtype = fielditems[1];
    		String itemsetid = fielditems[2];	    
	
    		sfactor.append(itemid.toUpperCase());
    		sfactor.append(operate);
    		String q_value = PubFunc.getStr(value.trim());
    		if("undefined".equalsIgnoreCase(q_value))
    		{
    			q_value="";
    		}
    		sfactor.append(q_value);
//    		if(((itemsetid.equalsIgnoreCase("0")) || (itemsetid.equalsIgnoreCase("UN")) || (itemsetid.equalsIgnoreCase("UM")) || (itemsetid.equalsIgnoreCase("@K"))) && like.equals("1") && (itemtype.equals("A")||itemtype.equals("M")))
    		if("1".equals(like) && ("A".equals(itemtype)|| "M".equals(itemtype)))//市政集团  要求学历等代码型也要支持模糊查询 所以废除原来的UM、UN等代码限制 zhaoxg add 2014-10-21
    		{
    			if(!(q_value==null|| "".equals(q_value)))
    				sfactor.append("*");            
    		}
	    
    		sfactor.append("`"); 
	    
    		/**组合表达式串*/
       
            if(j==1)
            {
            	sexpr.append(1);
            }else
            {
            	if("".equals(relation))
            		sexpr.append("*");
            	else
            		sexpr.append(relation);
            	sexpr.append(j);    
            }
    	}
    	String expression = sexpr.toString();
    	sexpr.setLength(0);
    	if(expression==null|| "".equals(expression))
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistexpr"),"",""));
    	/**为了分析用*/
    	if(!isHaveExpression(expression,fielditemidList.size()))
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
    	expression=expression.replaceAll("!","-");
    	TSyntax syntax=new TSyntax();
    	if(!syntax.Lexical(expression))
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
    	if(!syntax.DoWithProgram())
    		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
    	sexpr.append(expression);
	
    
    	/**表过式分析*/
    	/**非超级用户且对人员库进行查询*/
    	String strwhere="";
    	ArrayList fieldlist=new ArrayList();
    	boolean bhis=false;
    	if("1".equals(history))
    		bhis=true;
    	fieldlist=privFieldList(fieldlist,"1","0");//type=1
//  	if((!userView.isSuper_admin()))
//    	{
//        	strwhere=userView.getPrivSQLExpression(sexpr.toString()+"|"+sfactor.toString(),"Usr",bhis,false,true,fieldlist);   
//    	}
//    	else
    	{																																										
    		FactorList factorslist=new FactorList(sexpr.toString(),sfactor.toString(),db,bhis ,false,true,1,userView.getUserId());
            fieldlist=factorslist.getFieldList();
            strwhere=factorslist.getSqlExpression();
    	}
			
    	sql.append("select distinct "+db+"A01.a0100  ");
    	sql.append(strwhere);

    	return sql.toString();
    }
    /**
     * 分析表达式的合法式
     * @param expression
     * @param nmax　最大表达式因子号
     * @return
     */
    private boolean isHaveExpression(String expression,int nmax)
    {
        boolean bflag=true;
        String strlastno="";
        int ncurr=0;
        for(int i=0;i<expression.length();i++)
        {
        	char v =expression.charAt(i);
        	if(((i+1)!=expression.length())&&(v>='0'&&v<='9'))
        	{
        		strlastno=strlastno+v;
        	}
        	else
        	{
        		if(v>='0'&&v<='9')
        		{
        			strlastno=strlastno+v;
        		}
        		if(!"".equals(strlastno))
        		{
        			ncurr=Integer.parseInt(strlastno);
        			if(ncurr>nmax)
        			{
        				bflag=false;
        				break;
        			}
        		}
        		strlastno="";
        	}
        }        
        return bflag;
    }
    /**
     * 取得查询条件中的指标和固定指标不同的项目串
     * @param list
     * @return
     */
    private String getFilterFields(ArrayList list,String history)
    {
    	StringBuffer strfield=new StringBuffer();
    	for(int i=0;i<list.size();i++)
    	{
    		FieldItem item=(FieldItem)list.get(i);
    		if("1".equals(history)&&(!item.isMainSet()))
    			continue;	    		
    		strfield.append(item.getItemid().toLowerCase());
    		strfield.append(",");
    	}
    	if(strfield.length()>0)
    		strfield.setLength(strfield.length()-1);
    	return strfield.toString();
    }
    
    /**
     * @param fieldlist
     * @param flag 1:人员2：单位3：职位
     * @return
     */
	private ArrayList privFieldList(ArrayList fieldlist,String flag,String history) 
	{
		ArrayList list=new ArrayList();
		/**权限分析*/
		for(int j=0;j<fieldlist.size();j++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(j);
    		if("1".equals(history)&&(!fielditem.isMainSet()))
    			continue;			
			String fieldname=fielditem.getItemid();
			if("e01a1".equals(fieldname)&& "3".equals(flag))
				continue;
			else if("b0110".equals(fieldname)&& "2".equals(flag))
				continue;
			else 
			{
				if("b0110,e0122,e01a1,a0101".indexOf(fieldname)!=-1)
					continue;
			}
			cat.debug("priv_field="+fieldname);
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname.toUpperCase())));
			list.add(fielditem);
		}
		return list;
	}  
}
