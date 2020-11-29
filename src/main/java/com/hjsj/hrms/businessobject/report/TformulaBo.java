package com.hjsj.hrms.businessobject.report;

import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;



/**
 * 
 * <p>Title:TformulaBo</p>
 * <p>Description:对计算公式表的一些操作</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 5, 2006:8:48:05 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class TformulaBo {
	Connection conn=null;
	
	public TformulaBo(Connection conn)
	{
		this.conn=conn;
	}
	
	
	/**
	 * 得到报表的计算公式
	 * @param tabid  表id
	 * @param flag	 1:表内公式  2:表间公式  
	 */
	public ArrayList getFormulaList(ArrayList tabids,String flag)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			StringBuffer sql=new StringBuffer("");
			LazyDynaBean abean=new LazyDynaBean();
			for(int i=0;i<tabids.size();i++)
			{
				String tabid=(String)tabids.get(i);
				sql.setLength(0);
				sql.append("select * from tformula where tabid="+tabid);
				if("1".equals(flag))
				{
					sql.append(" and ( colrow=0 or colrow=1 )");
				}
				else if("2".equals(flag))
				{
					sql.append(" and ( colrow=2 or colrow=3 or colrow=4 )");
				}
				sql.append(" order by  expid ");
				recset=dao.search(sql.toString());
				while(recset.next())
				{
					abean=new LazyDynaBean(); 
					String str="";
					if(recset.getInt("colrow")==0||recset.getInt("colrow")==2) {
                        str=ResourceFactory.getProperty("reportanalyse.row")+ResourceFactory.getProperty("hmuster.label.expressions")+"：" ;
                    } else if(recset.getInt("colrow")==1||recset.getInt("colrow")==3) {
                        str=ResourceFactory.getProperty("reportanalyse.column")+ResourceFactory.getProperty("hmuster.label.expressions")+"：" ;
                    } else if(recset.getInt("colrow")==4) {
                        str=ResourceFactory.getProperty("edit_report.grid")+ResourceFactory.getProperty("hmuster.label.expressions")+"：" ;
                    }
					 
					
					StringBuffer value=new StringBuffer(recset.getString(1));
					value.append("§§");
					value.append(recset.getString(2));
					value.append("§§");
					value.append(Sql_switcher.readMemo(recset,"lexpr"));
					value.append("§§");
					value.append(Sql_switcher.readMemo(recset,"rexpr"));
					value.append("§§");
					value.append(recset.getString(5));
					value.append("§§");
					value.append(recset.getString(6));
					
					abean.set("value",value);
					abean.set("name", tabid+":"+str+recset.getString("cname"));
					list.add(abean);
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	 	finally
		{
			try
			 {
				 if(recset!=null) {
                     recset.close();
                 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		} 
		return list;
	}
	
	
	
	
	/**
	 * 得到报表的计算公式
	 * @param tabid  表id
	 * @param flag	 a:表内公式  b:表间公式 c:表内表间公式  =0,表内行公式  =1,表内列公式  =2,表间行公式  =3,表间列公式 =4,表间格公式 =5,汇总公式
	 */
	public ArrayList getFormulaList(String tabid,String flag)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			StringBuffer sql=new StringBuffer("select * from tformula where tabid="+tabid);
			if("a".equals(flag))
			{
				sql.append(" and ( colrow=0 or colrow=1 )");
			}
			else if("b".equals(flag))
			{
				sql.append(" and ( colrow=2 or colrow=3 or colrow=4 )");
			}
			else if("c".equals(flag))
			{
				sql.append(" and ( colrow=0 or colrow=1 or colrow=2 or colrow=3 or colrow=4  )");
			}
			else
			{
				sql.append(" and colrow="+flag);
			}
			if("c".equals(flag)) {
                sql.append(" order by expid ");
            } else {
                sql.append(" order by  expid ");
            }
			recset=dao.search(sql.toString());
			int i=0;
			while(recset.next())
			{
				int colrow=recset.getInt("colrow");
				i++;
				String[] temp=new String[2];
				StringBuffer value=new StringBuffer(recset.getString(1));
				value.append("§§");
				value.append(recset.getString(2));
				value.append("§§");
				value.append(Sql_switcher.readMemo(recset,"lexpr"));
				value.append("§§");
				value.append(Sql_switcher.readMemo(recset,"rexpr"));
				value.append("§§");
				value.append(recset.getString(5));
				value.append("§§");
				value.append(recset.getString(6));
				StringBuffer text=new StringBuffer(i+".");
				if(recset.getInt("colrow")==0||recset.getInt("colrow")==2) {
                    text.append(ResourceFactory.getProperty("reportanalyse.row")+ResourceFactory.getProperty("hmuster.label.expressions")+"：");
                } else if(recset.getInt("colrow")==1||recset.getInt("colrow")==3) {
                    text.append(ResourceFactory.getProperty("reportanalyse.column")+ResourceFactory.getProperty("hmuster.label.expressions")+"：");
                } else if(recset.getInt("colrow")==4) {
                    text.append(ResourceFactory.getProperty("edit_report.grid")+ResourceFactory.getProperty("hmuster.label.expressions")+"：");
                } else if(recset.getInt("colrow")==5) {
                    text.append(ResourceFactory.getProperty("report_collect.collect")+ResourceFactory.getProperty("hmuster.label.expressions")+"：");
                }
				text.append(recset.getString("cname"));
				temp[0]=value.toString();
				temp[1]=text.toString();
				if("c".equals(flag))
				{
					if(colrow==0||colrow==1)
					{
						temp[1]+="  (表内公式)";
					}
					else if(colrow==2||colrow==3||colrow==4 )
					{
						temp[1]+="  (表间公式)";
					}
				}
				list.add(temp);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	/*	finally
		{
			try
			 {
				 if(recset!=null)
					 recset.close();		 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}*/
		return list;
	}
	
	
	
	
	/**
	 * 得到某表的计算公式
	 * @param tabid	表id
	 * @param flag	=3,表间列公式 =2,表间行公式 =0,表内行公式 =1,表内列公式 =5,汇总公式 =4,表间格公式 =6:表间 列，格公式  a:表内计算公式  b:表间计算公式
	 * @return
	 */
	public ArrayList  getFormula(String tabid,String flag)
	{
		ArrayList list=new ArrayList();
		StringBuffer sql=new StringBuffer("select * from tformula where tabid="+tabid+" and ( colrow=");
		if("a".equals(flag))
		{
			sql.append(0);
			sql.append(" or colrow=");
			sql.append(1);
		}
		else if("b".equals(flag))
		{
			sql.append(3);
			sql.append(" or colrow=");
			sql.append(2);
			sql.append(" or colrow=");
			sql.append(4);
		}
		else
		{
			if("6".equals(flag))
			{
				sql.append(3+" or colrow=4 ");
			}else if("7".equals(flag))
			{
				sql.append(2+" or colrow=4 ");
			}
			else {
                sql.append(flag);
            }
		}
		sql.append(" ) order by expid");
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search(sql.toString());
			while(recset.next())
			{
				RecordVo vo=new RecordVo("tformula");
				vo.setInt("expid",recset.getInt("expid"));
				vo.setString("cname",recset.getString("cname"));
				vo.setString("lexpr",Sql_switcher.readMemo(recset,"lexpr"));
				vo.setString("rexpr",Sql_switcher.readMemo(recset,"rexpr"));
				vo.setInt("colrow",recset.getInt("colrow"));
				vo.setInt("tabid",recset.getInt("tabid"));
				list.add(vo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	/*	finally
		{
			try
			 {
				 if(recset!=null)
					 recset.close();
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		}*/
		return list;
	}
	
	
	
	
	/**
	 * 得到公式中左表达式涉及到的信息
	 * @param rowFormulaList
	 * @return
	 */
	public HashSet getLexprNum(ArrayList FormulaList)
	{
		HashSet lexprSet=new HashSet();
		for(Iterator t=FormulaList.iterator();t.hasNext();)
		{
			RecordVo vo=(RecordVo)t.next();
			String lexpr = vo.getString("lexpr");
			if(lexpr.indexOf("|")!=-1) {
                lexpr=lexpr.substring(0,lexpr.indexOf("|"));
            }
			lexprSet.add(lexpr);
		}
		return lexprSet;
	}
	
	
	/**
	 * 得到公式表达式中涉及到的行或列信息
	 * @param FormulaList
	 * @param map  行列号对应二位数组的实际下标值(map)
	 * @return
	 */
	public HashSet getIndexStr(ArrayList FormulaList,HashMap map)
	{
		HashSet exprSet=new HashSet();
		for(Iterator t=FormulaList.iterator();t.hasNext();)
		{
			RecordVo vo=(RecordVo)t.next();
			ArrayList list=new ArrayList();
			String lexpr=vo.getString("lexpr");
			if(lexpr.indexOf("|")!=-1) {
                lexpr=lexpr.substring(0,lexpr.indexOf("|"));
            }
			if(map.get(lexpr)==null) {
                continue;
            }
			list.add((String)map.get(lexpr));
			String rexpr=ExprUtil.getExpr((String)vo.getString("rexpr"));
			ExprUtil exprUtil=new ExprUtil();
			ArrayList exprList=exprUtil.analyseStatExpr(rexpr);
			int flag=0;
			for(Iterator t1=exprList.iterator();t1.hasNext();)
			{
				String temp=((String)t1.next()).toLowerCase();
				if(temp.charAt(0)!='c')
				{
					if(map.get(temp)==null) {
                        flag++;
                    } else {
                        list.add((String)map.get(temp));
                    }
				}
			}
			
			//公式涉及到的行列号为真时，才加载
			if(flag==0)
			{
				for(int i=0;i<list.size();i++)
				{
					exprSet.add((String)list.get(i));
				}
			}
		}
		return exprSet;
	}
	
	
	
	
	
	
	
	/**
	 * 找到该单元格涉及到的计算公式(包括联动)
	 * @param i	            横坐标位置
	 * @param j             纵坐标位置
	 * @param formulaList	表内计算公式
	 * @return
	 */
	public ArrayList findFormulaList(String i,String j,ArrayList formulaList,HashMap rowMap,HashMap colMap)
	{
		ArrayList a_list=new ArrayList();
		StringBuffer sb=new StringBuffer("/"+i+"&"+j);
	//	boolean isLeftExpr=false;
		for(Iterator t=formulaList.iterator();t.hasNext();)
		{
			RecordVo vo=(RecordVo)t.next();
			HashMap map=null;
			boolean flag=false;
			if(vo.getInt("colrow")==0)			//表内行公式
			{
				map=rowMap;
			}
			else if(vo.getInt("colrow")==1)		//表内列公式
			{
				map=colMap;
			}

			
			String lexpr=vo.getString("lexpr");
			if(lexpr.indexOf("|")!=-1) {
                lexpr=lexpr.substring(0,lexpr.indexOf("|"));
            }
			if(map.get(lexpr)==null) {
                continue;
            }
			
			if(vo.getInt("colrow")==0)
			{
				if(((String)rowMap.get(lexpr)).equals(i))
				{
					flag=true;			
				}
			}
			else
			{
				if(((String)colMap.get(lexpr)).equals(j))
				{
					flag=true;
				}
			}

			String rexpr=ExprUtil.getExpr((String)vo.getString("rexpr"));
			ExprUtil exprUtil=new ExprUtil();
			ArrayList exprList=exprUtil.analyseStatExpr(rexpr);
			int f=0;
			for(Iterator t1=exprList.iterator();t1.hasNext();)
			{
				String temp=((String)t1.next()).toLowerCase();
				if(temp.charAt(0)!='c')
				{
					if(map.get(temp)==null) {
                        f++;
                    } else
					{
						String[] value=null;
						if(sb.substring(1).indexOf("/")==-1)
						{
							value=new String[1];
							value[0]=sb.substring(1);
						}
						else {
                            value=sb.substring(1).split("/");
                        }
						for(int a=0;a<value.length;a++)
						{
							String [] a_value=value[a].split("&");
							if(vo.getInt("colrow")==0)
							{
								if(((String)rowMap.get(temp)).equals(a_value[0])) {
                                    flag=true;
                                }
							}
							else
							{
								if(((String)colMap.get(temp)).equals(a_value[1])) {
                                    flag=true;
                                }
							}
						}
					}
				}
			}
			
			//公式涉及到的行列号为真时，才加载
			if(f==0&&flag)
			{
				a_list.add(vo);
				
				if(vo.getInt("colrow")==0)
				{
					sb.append("/"+lexpr+"&"+j);
				}
				else
				{
					sb.append("/"+i+"&"+lexpr);
				}
				//如果修改的是公式的左表达式，则恢复原值，不进行其他计算
		/*		if(isLeftExpr==true)
				{
					a_list=new ArrayList();
					a_list.add(vo);
					break;
				} */
			}
		}	
		return a_list;
	}
	
	
	
	
	
	
	

}
