/*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.businessobject.stat;

import com.hjsj.hrms.businessobject.parse.parsebusiness.Factor;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatCondAnalyse {	
	//代查询范围的
	 /**
	   * 日志跟踪器
	   */
	  protected Category cat = Category.getInstance(this.getClass());
	
	public String getCondQueryString(String lexpr,String strFactor,String userbase,boolean ishistory,String username,String scopeQuery,UserView userView,String infokind,boolean bresult)
	throws GeneralException{
		strFactor=strFactor.trim();
		strFactor = PubFunc.keyWord_reback(strFactor);
		if (scopeQuery != null && scopeQuery.trim().length()>0 && !"null".equalsIgnoreCase(scopeQuery)&&"1".equalsIgnoreCase(infokind)) {
			String[] scopeField = scopeQuery.split(",");
			if (scopeField.length > 0) {
				ArrayList lexprFactor=new ArrayList();
				lexprFactor.add(getScopeFactor(scopeField));
				lexprFactor.add(lexpr + "|" + strFactor);
				CombineFactor combinefactor=new CombineFactor();
				String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
				StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
				if(Stok.hasMoreTokens())
				{
					lexpr=Stok.nextToken();
					strFactor=Stok.nextToken();
				}
			}
		}		
		String strwhere="";
	    ArrayList fieldlist=new ArrayList();
		 /**非超级用户且对人员库进行查询*/
	    cat.debug("-----infokind----->" + infokind);
	    if("1".equals(infokind))
	    {
	    	
	    }else if("2".equals(infokind)) {
            userbase="B";
        } else if("3".equals(infokind)) {
            userbase="K";
        }
       /*if((!userView.isSuper_admin()))
        {
            strwhere=userView.getPrivSQLExpression(lexpr+"|"+strFactor,userbase,ishistory,bresult,fieldlist);
        }
        else
        {               	 
           FactorList factorlist=new FactorList(lexpr,strFactor,userbase,ishistory,true,bresult,Integer.parseInt(infokind),username);
           strwhere=factorlist.getSqlExpression();
        }*/
        //System.out.println(lexpr+"|"+strFactor+"---"+userbase+"---"+ishistory+"---"+false+"---"+bresult+"---"+fieldlist);
	    
        strwhere=userView.getPrivSQLExpression(lexpr+"|"+strFactor,userbase,ishistory,false,bresult,fieldlist);
        if(strwhere!=null&&strwhere.length()>0)
    	{
        	if("2".equals(infokind))
            {
        		String strdate=DateUtils.format(new Date(), "yyyy-MM-dd");
        		strwhere=strwhere+" and EXISTS (select codeitemid from organization where "+Sql_switcher.dateValue(strdate)+" between start_date and end_date and codeitemid=b01.b0110";
        		if (scopeQuery != null && scopeQuery.trim().length()>0 && !"null".equalsIgnoreCase(scopeQuery)) {
        			strwhere +=" and codeitemid like '"+scopeQuery.substring(2)+"%'";
        		}
        		strwhere +=")";
        	}else if("3".equals(infokind))
        	{
        		String strdate=DateUtils.format(new Date(), "yyyy-MM-dd");
        		strwhere=strwhere+" and EXISTS (select codeitemid from organization where "+Sql_switcher.dateValue(strdate)+" between start_date and end_date and codeitemid=k01.e01a1";
        		if (scopeQuery != null && scopeQuery.trim().length()>0 && !"null".equalsIgnoreCase(scopeQuery)) {
        			strwhere +=" and codeitemid like '"+scopeQuery.substring(2)+"%'";
        		}
        		strwhere +=")";
        	}
        }
        return strwhere;
	}
	/*带模糊查询的*/
	public String getCondQueryString(String lexpr,String strFactor,String userbase,boolean ishistory,String username,String scopeQuery,UserView userView,String infokind,boolean bresult,boolean blike)
	throws GeneralException{
		strFactor=strFactor.trim();
		if (scopeQuery != null && scopeQuery.trim().length()>0 && !"null".equalsIgnoreCase(scopeQuery)) {
			String[] scopeField = scopeQuery.split(",");
			if (scopeField.length > 0) {
				ArrayList lexprFactor=new ArrayList();
				lexprFactor.add(lexpr + "|" + strFactor);
				lexprFactor.add(getScopeFactor(scopeField));
				CombineFactor combinefactor=new CombineFactor();
				String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
				StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
				if(Stok.hasMoreTokens())
				{
					lexpr=Stok.nextToken();
					strFactor=Stok.nextToken();
				}
			}
		}
		String strwhere="";
	    ArrayList fieldlist=new ArrayList();
		 /**非超级用户且对人员库进行查询*/
	   try
	   {
//		   if(userView.getStatus()!=0)
//			   bresult=true; 
		   strwhere=userView.getPrivSQLExpression(lexpr+"|"+strFactor,userbase,ishistory,blike,bresult,fieldlist);
		   if(strwhere!=null&&strwhere.length()>0)
	    	{
	        	if("B".equalsIgnoreCase(userbase))
	            {
	        		String strdate=DateUtils.format(new Date(), "yyyy-MM-dd");
	        		strwhere=strwhere+" and EXISTS (select codeitemid from organization where "+Sql_switcher.dateValue(strdate)+" between start_date and end_date and codeitemid=b01.b0110)";
	        	}else if("K".equalsIgnoreCase(userbase))
	        	{
	        		String strdate=DateUtils.format(new Date(), "yyyy-MM-dd");
	        		strwhere=strwhere+" and EXISTS (select codeitemid from organization where "+Sql_switcher.dateValue(strdate)+" between start_date and end_date and codeitemid=k01.e01a1)";
	        	}
	        }
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return strwhere;
	}
	public String getScopeFactor(String[] scopeField) {
		String codesetidstr = "";
		String codeitemidstr = "";
		String lexprstr="";
		String strfactor="";
		String result;
		for (int i = 0,j=1; i < scopeField.length; i++,j++) {
			if (scopeField[i] != null && scopeField[i].length() > 2) {
				codesetidstr = scopeField[i].substring(0, 2);
				codeitemidstr = scopeField[i].substring(2);
				if(i!=0) {
                    lexprstr+="+" + j;
                } else {
                    lexprstr="1";
                }
				if ("UN".equals(codesetidstr)) {
					strfactor+="B0110=" + codeitemidstr + "%`";			
				}else if("UM".equals(codesetidstr)) {
					strfactor+="E0122=" + codeitemidstr + "%`";	
				}else{
					strfactor+="E01A1=" + codeitemidstr + "%`";	
				}				
			  }
			}
		  result=lexprstr +"|" + strfactor;
	   return result;	
	}
		

	
	public StatCondAnalyse() {}
	StringBuffer query =new StringBuffer(); //生成的最终sql从from开始的sql语句
	private String[] factors; //保存各个因子
	private String[] itemId;  //目的是保存各个因子
	private String[] newSet = null;
	Vector v = new Vector(); //保存各个子集
	boolean haveSubSet = false; //标志是否有子集
	int history = -1; //是否统计历史纪录
	private String userBase; //表示在那个库
	private String queryId; //统计表sname 中的id查询各个图例的
	private String lexpr; //保存各个图例中的表达式项
	private String strFactor; //获得条件因子
	int year = Calendar.getInstance().get(Calendar.YEAR); //获得当前年
	int month = Calendar.getInstance().get(Calendar.MONTH) + 1; //获得当前月
	int day = Calendar.getInstance().get(Calendar.DATE); //获得当前日
	String curDate = year + "." + month + "." + day; //生成当前日期
	
	/*从数据库中根据queryid获得各个图例项获得表达式和条件因子
	  并把各个因子分割开来保存到factors数组
	  初始化数组signArry保存表达式的逻辑运算符
	  **/
	public void getFectors(
		String userBase,
		String strLexpr,
		String strFactors,
		int flag) {
		try {
			this.userBase = userBase;
			lexpr = strLexpr;
			strFactor = strFactors + "`";
			history = flag;
			factors = mySplit(strFactor, "`"); //分解各个因子给factors数组
			itemId = new String[factors.length];
		} catch (Exception e) {
			System.out.println("从数据库中获得表达式和条件因子出错");
		}
	}
	/* 分解条件表达式把各个逻辑运算符保存到signArry树组
	 * 并获得各个子集保存到v向量
	 * */
	public void setSetID() {
		try {
			for (int i = 0; i < factors.length; i++) {				
				Factor factor = new Factor(userBase, factors[i]);
				String tmp = factor.getSet();
				if (!v.contains(tmp) && !isMainSet(tmp)) {
					haveSubSet = true;
					v.add(tmp);
				}
			}
			newSet = (String[]) v.toArray(new String[0]);
		} catch (Exception e) {
			System.out.println("分解表达式和获得各个子集出错");
		}
	}
	//获得到from的后的各个项即各个子集以及主集
	public void getFromStr() {
		try {
			query.append(" from ");
			for (int i = 0; i < newSet.length; i++) {
				query.append(newSet[i] + ",");
			}
			query.append(userBase + "A01 ");
		} catch (Exception e) {
			System.out.println("生成From后的各个数据原即子集主集出错!");
		}
	}
	/*生成where后的各个项
	 * */
	public void getWhereStr() {
		try {
			query.append("where (");
			for (int i = 0; i < factors.length; i++) {
				Factor factor = new Factor(userBase, factors[i]);
/////				factor.setSet(userBase);
				if (!((userBase + "A01").equalsIgnoreCase(factor.getSet())))
				{
					query.append(userBase+ "A01.A0100=");
				    query.append(factor.getSet());
				    query.append(".A0100");
				    query.append(" and ");
				}
			}
			if (history == 0) {
				for (int i = 0; i < newSet.length; i++) {
					if (!"A01".equals(newSet[i].substring(3, 6))) {
						query.append(" (" + newSet[i] + ".I9999 in (select max(");
						query.append(newSet[i]);
						query.append(".I9999) from ");
						query.append(newSet[i]);
						query.append(" where ");
						query.append(newSet[i].substring(0, 3));
						query.append("A01.A0100=");
						query.append(newSet[i]);
						query.append(".A0100 group by ");
						query.append(newSet[i]);
						query.append(".A0100)) and ");
					}
				}
			}
			int L = 0;
			query.append("(");
			for (int j = 0; j < lexpr.length(); j++) {
				if ("(".equals(lexpr.substring(j, j + 1))) {
					query.append("(");
				} else if (")".equals(lexpr.substring(j, j + 1))) {
					query.append(")");
				} else if ("+".equals(lexpr.substring(j, j + 1))) {
					query.append(" OR ");
				} else if ("*".equals(lexpr.substring(j, j + 1))) {
					query.append(" AND ");
				} else {
					Factor factor = new Factor(userBase, factors[L]);
					query.append(factor.getCresult());
					L += 1;
				}
			}
			query.append(")");
			query.append(")");
		} catch (Exception e) {
			System.out.println("生成where后的各个项出错!");
		}
	}
	//分解取得各个条件项；把分解的各项放入到Vector对象中
	public String[] mySplit(String src, String division) {
		try {
			Vector vFactors = new Vector();
			int beginIndex = 0;
			int endIndex = 0;
			if (src != null) {
				while (true) {
					endIndex = src.indexOf(division, beginIndex);
					if (endIndex == -1 || endIndex >= src.length()) {
						break;
					}
					vFactors.add(src.substring(beginIndex, endIndex));
					beginIndex = endIndex + 1;
				}
			}
			return (String[]) vFactors.toArray(new String[0]);
		} catch (Exception e) {
			System.out.println("分解各个因子出错!");
		}
		return null;
	}
	/*
	 * 带有查询范围的统计sql语句
	 * */
	public void getSelectStr(String[] sqlSelectField) {
		String codesetidstr = "";
		String codeitemidstr = "";
		boolean blfirst = true;
		for (int i = 0; i < sqlSelectField.length; i++) {
			if (sqlSelectField[i] != null && sqlSelectField[i].length() > 2) {
				codesetidstr = sqlSelectField[i].substring(0, 2);
				codeitemidstr = sqlSelectField[i].substring(2);
				if ("UN".equals(codesetidstr)) {
					if (blfirst == true) {
						query.append(" AND (");
						query.append(userBase);
						query.append("A01.B0110 like '");
						query.append(codeitemidstr);
						query.append("%'");
						blfirst = false;
					} else {
						query.append(" OR ");
						query.append(userBase);
						query.append("A01.B0110 like '");
						query.append(codeitemidstr);
						query.append("%'");
					}
				}
				if ("UM".equals(codesetidstr)) {
					if (blfirst == true) {
						blfirst = false;
						query.append(" AND (");
						query.append(userBase);
						query.append("A01.E0122 like '");
						query.append(codeitemidstr);
						query.append("%'");
					} else {
						query.append(" OR ");
						query.append(userBase);
						query.append("A01.E0122 like '");
						query.append(codeitemidstr);
						query.append("%'");
					}
				}
				if ("AK".equals(codesetidstr)) {
					if (blfirst == true) {
						blfirst = false;
						query.append(" AND (");
						query.append(userBase);
						query.append("A01.E01A1 like '");
						query.append(codeitemidstr);
						query.append("%'");
					} else {
						query.append(" OR ");
						query.append(userBase);
						query.append("A01.E01A1 like '");
						query.append(codeitemidstr);
						query.append("%'");
					}
				}

			}
		}
		if (blfirst == false) {
			query.append(")");
		}
	}
	/*
	 * 生成from 开始的WHERE的语句；
	 */
	public String generateQuerySql(
		String userBase,
		String strLexpr,
		String strFactor,
		int Flag,
		String sqlSelect,
		String username,
		String manageprive)
		throws Exception {
		getFectors(userBase, strLexpr, strFactor, Flag);
		setSetID();
		getFromStr();
		getWhereStr();
		///System.out.println("sql " + query);
		if (sqlSelect != null) {
			String[] sqlSelectField = sqlSelect.split(",");
			if (sqlSelectField.length > 0) {
				getSelectStr(sqlSelectField);
			}
		}
		if (!"su".equalsIgnoreCase(username)) {
			if (manageprive != null
				&& "UN".equals(manageprive.substring(0, 2))) {
				query.append(" AND (");
				query.append(userBase);
				query.append("A01.B0110 like '");
				query.append(manageprive.substring(
						2,
						manageprive.length()));
				query.append("%')");
			}
			if (manageprive != null
				&& "UM".equals(manageprive.substring(0, 2))) {
				query.append(" AND (");
				query.append(userBase);
				query.append("A01.E0122 like '");
				query.append(manageprive.substring(
						2,
						manageprive.length()));
				query.append("%')");
			}
			if (manageprive != null
				&& "@K".equals(manageprive.substring(0, 2))) {
				query.append(" AND (");
				query.append(userBase);
				query.append("A01.E01A1 like '");
				query.append(manageprive.substring(
						2,
						manageprive.length()));
				query.append("%')");
			}
		}
       // System.out.println("query " + query);
		return query.toString();
	}
	/*
	 * 判断是否式主集的函数
	 * */
	private boolean isMainSet(String setName) {
		boolean isMainSetBl = false;
		try {
			if ("A01".equals(setName.substring(3, 6))) {
				isMainSetBl = true;
			}
			return isMainSetBl;
		} catch (Exception e) {
			System.out.println("判断是否式主集出错!");
		}
		return isMainSetBl;
	}
	/**
	 * 得到统计项
	 * @param dao
	 * @param userView
	 * @param id
	 * @param norder
	 * @return
	 */
	public HashSet getStatFieldItem(ContentDAO dao,UserView userView,String id,String norder)
	{
		HashSet fieldItemSet = new HashSet();
		StringBuffer sql =new StringBuffer();
		sql.append("select factor from SLegend where id="+id);
		sql.append(" and norder='"+norder+"'");
		RowSet rs=null;
		try {
			rs=dao.search(sql.toString());
			String factors="";
			String factorstr="";			
			while(rs.next())
			{
				factors=rs.getString("factor");
				if(factors!=null&&factors.length()>0)
				{
					String factorArr[]=factors.split("`");
					for(int i=0;i<factorArr.length;i++)
					{
						factorstr=factorArr[i];
						Factor factor = new Factor(userView.getDbname(), factorstr);
						String item=factor.getItem();
						if(item!=null&&item.length()>0)
						{
							fieldItemSet.add(item);
						}						
					}
				}	
			}			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
		return fieldItemSet;
	}
	
}
