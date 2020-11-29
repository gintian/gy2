package com.hjsj.hrms.businessobject.gz.premium;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;

public class PremiumBo {
	private Connection conn;
	private UserView userView=null;
	private ConstantXml xml=null;
	
	public PremiumBo(Connection con,UserView _userView)
	{
		this.conn=con;
		this.userView=_userView;
		
		xml=new ConstantXml(this.conn, "GZ_BONUS", "Params");
	}
	
	/**
	 * 批量计算
	 * @param where_str
	 * @param itemids
	 * @param year
	 * @param month
	 * @param operateUnitCode
	 */
	public void computing(String where_str,ArrayList itemids,String year,String month,String operateUnitCode)
	{
		try
		{
			/**取得需要的公式列表*/
			ArrayList formulalist=new ArrayList();
			if(itemids.size()>0)
				formulalist=this.getFormulaList(itemids); 
			LazyDynaBean dataBean=new LazyDynaBean();
			dataBean.set("year",year);
			dataBean.set("month",month);
			
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			FieldItem Field=null;
			HashMap allFieldMap=new HashMap();
			for(int i=0;i<allUsedFields.size();i++)
			{
				Field = (FieldItem) allUsedFields.get(i);
				allFieldMap.put(Field.getItemdesc().toLowerCase(),Field);
			}
			
			ArrayList factorList=getFactors(formulalist,allFieldMap);
			if(factorList.size()>0)
				createTempTable(factorList);
			
			for(int i=0;i<formulalist.size();i++)
			{
                DynaBean dbean=(LazyDynaBean)formulalist.get(i);
                String fmode=(String)dbean.get("fmode");
                if("0".equals(fmode))  //计算公式
                {
                	computing(dbean,where_str,dataBean);
                }
                else if("1".equals(fmode))  //导入公式
                {
                	if(dbean.get("rexpr")!=null&&((String)dbean.get("rexpr")).trim().length()!=0)
                		batchImportFromArchive(allFieldMap,dbean,where_str,dataBean,operateUnitCode);
                }
                else if("2".equals(fmode))  //统计公式
                {
                	statisticFormula(dbean,allFieldMap,where_str,dataBean);
                }
                
			}//for i loop end.
			
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算
	 * @param dbean
	 * @param strWhere
	 * @return
	 * @throws GeneralException
	 */
	private boolean computing(DynaBean dbean,String strWhere,LazyDynaBean dataBean)throws GeneralException
	{
		boolean bflag=false;
		try
		{
		
			String cond=(String)dbean.get("cond");
        	String itemname=(String)dbean.get("itemname");
        	String rexpr=(String)dbean.get("rexpr");
        	String premiumSetId=getXml().getNodeAttributeValue("/Params/BONUS_SET","setid"); //"B05";   //奖金子集
        	ArrayList fielditemlist=DataDictionary.getFieldList(premiumSetId,Constant.USED_FIELD_SET);
        	YksjParser yp=null;
			try
			{
				String strfilter="";
				ContentDAO dao=new ContentDAO(this.conn);
				/**先对计算公式的条件进行分析*/
				if(!(cond==null|| "".equalsIgnoreCase(cond)))
				{
					yp = new YksjParser( this.userView ,fielditemlist,
							YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forUnit , "Ht", "");
					yp.run_where(cond);
					strfilter=yp.getSQL();
				}
				
				StringBuffer strcond=new StringBuffer();
				if(!(strWhere==null|| "".equalsIgnoreCase(strWhere)))
					strcond.append(premiumSetId+".B0110 in ( "+strWhere+" ) ");
				if(!("".equalsIgnoreCase(strfilter)))
				{
					if(strcond.length()>0)
						strcond.append(" and ");
					strcond.append(strfilter);
				}
				strcond.append(" and "+Sql_switcher.year(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("year"));
				strcond.append(" and "+Sql_switcher.month(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("month"));
				
				/**单表计算*/
				FieldItem item=DataDictionary.getFieldItem(itemname);
				yp=new YksjParser( this.userView ,fielditemlist,
						YksjParser.forNormal, getDataType(item.getItemtype()),YksjParser.forUnit , "Ht", "");
				yp.run(rexpr,this.conn,strcond.toString(),premiumSetId);
				String strexpr=yp.getSQL();
				
				StringBuffer strsql=new StringBuffer();
				strsql.append("update ");
				strsql.append(premiumSetId);
				strsql.append(" set ");
				strsql.append(itemname);
				strsql.append("=");
				strsql.append(strexpr);
				strsql.append(" where 1=1 ");
				if(strcond.length()>0)
				{
					strsql.append(" and ");
					strsql.append(strcond.toString());
				}
				dao.update(strsql.toString());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
	      	    throw GeneralExceptionHandler.Handle(ex);
			}finally{ 
				yp=null;
			} 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bflag;
	}
	
	
	
	
	//计算统计公式
	private void statisticFormula(DynaBean dbean,HashMap allFieldMap,String where_str,LazyDynaBean dataBean)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String tableName="t#"+this.userView.getUserName();
			String smode=(String)dbean.get("smode");
        	String cond=(String)dbean.get("cond");
        	String itemname=(String)dbean.get("itemname");
        	String rexpr="";
        	//=0 求个数  =1 求和   =2 最小值  =3 最大值  =4 平均值
        	if(!"0".equals(smode))
        		rexpr=(String)dbean.get("rexpr");
        	StringBuffer sql=new StringBuffer(" ");
        	if("0".equals(smode))
        		sql.append(" select count(a0100)");
        	else
        	{
        		ArrayList _list=analyseStatExpr(rexpr,allFieldMap);
        		for(int i=0;i<_list.size();i++)
        		{
        			String itemid=(String)_list.get(i);
        			FieldItem item=DataDictionary.getFieldItem(itemid);
        			rexpr=rexpr.replaceAll(item.getItemdesc(),item.getItemid());
        		}
        		
        		if("1".equals(smode))  // 求和
				{
					sql.append(" select SUM( ");
				}
				else if("4".equals(smode))  // 求平均值
				{
					sql.append(" select avg(");
				}
				else if("3".equals(smode))  // 求最大值
				{
					sql.append(" select MAX( ");
				}
				else if("2".equals(smode))  // 求最小值
				{
					sql.append(" select MIN( ");	
				} 
        		sql.append(rexpr+" ) ");
        	}
        	sql.append(" from "+tableName);
     
        	String premiumSetId=getXml().getNodeAttributeValue("/Params/BONUS_SET","setid");
    
        	StringBuffer buf=new StringBuffer("select * from "+premiumSetId+" where "+premiumSetId+".B0110 in ( ");
			buf.append(where_str+" ) ");
			buf.append(" and "+Sql_switcher.year(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("year"));
			buf.append(" and "+Sql_switcher.month(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("month"));
        	RowSet rowSet=dao.search(buf.toString());
        	
        	String where="";
        	if(cond.trim().length()>0)
        	{
        	//	ArrayList alist=new ArrayList();
        	//	alist.add(cond);
        		where=getMergeTerms(cond,tableName,this.userView.getUserName());
        	}
        	
        	RowSet rowSet2=null;
        	FieldItem _item=DataDictionary.getFieldItem(itemname.toLowerCase());
        	while(rowSet.next())
        	{
        		String b0110=rowSet.getString("b0110");
        		StringBuffer sql_str=new StringBuffer(sql.toString());
        		sql_str.append(" where e0122 like '"+b0110+"%'");
        		if(where.length()>0)
        			sql_str.append(" and "+where);

        		rowSet2=dao.search(sql_str.toString());
        		
        		if(rowSet2.next())
        		{
        			if(rowSet2.getString(1)!=null)
        			{
	        			StringBuffer _sql=new StringBuffer("update "+premiumSetId+" set "+itemname+"="+PubFunc.round(rowSet2.getString(1),_item.getDecimalwidth()));
	        			_sql.append(" where b0110='"+b0110+"' ");
	        			_sql.append(" and "+Sql_switcher.year(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("year"));
	        			_sql.append(" and "+Sql_switcher.month(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("month"));
	        			dao.update(_sql.toString());
        			}
        		}
        	}
        	if(rowSet2!=null)
        		rowSet2.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	

	/**
	 * 合并条件
	 * 
	 * @param lexprFactor
	 * @param tableName
	 * @return
	 */
	public String getMergeTerms(String lexprFactorStr,String tableName,String userName)throws GeneralException
	{
		String whl="";
		try
		{
			String lexpr="";
			String strFactor="";
			// 合并条件表达式
			CombineFactor combinefactor=new CombineFactor();
		//	String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
			StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
			if(Stok.hasMoreTokens())
			{
				lexpr=Stok.nextToken();
				strFactor=Stok.nextToken();
			}
			// 调用陈总提供的表达式分析器的到sql语句
			if(strFactor.length()>12&&strFactor.indexOf("$THISUNIT[]")!=-1)
			{
				
				
				if(this.userView.isSuper_admin())
					strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","*");
				else
				{
					String unit_ids=this.userView.getUnit_id();
					if(unit_ids==null||unit_ids.trim().length()==0|| "UN".equalsIgnoreCase(unit_ids.trim()))
					{
						strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
					}
					else
					{
						String[] temps=unit_ids.split("`");
						StringBuffer un=new StringBuffer("");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								String temp=temps[i];
								String pre=temp.substring(0,2);
								String value=temp.substring(2);
								if("UN".equalsIgnoreCase(pre))
								{
									un.append("|"+value);
								}
								else
								{
									un.append("|"+getUnByUm(value));
								}
							}
							
						}
						if(un.length()>0)
						{
							
							strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]",un.substring(1));
							
						}
						else
							strFactor=strFactor.replaceAll("\\$THISUNIT\\[\\]","##");
						
						
					}
		
				}
			}
			FactorList factorlist=new FactorList(strFactor,lexpr,userName);			
			whl=factorlist.getSingleTableSqlExpression(tableName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return whl;
	}
	
	
	
	
	

	//根据部门找单位
	public String getUnByUm(String umCode)
	{
		String un="##";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=null;
			while(true)
			{
				String sql="select codesetid,codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"+umCode+"')";
				recset=dao.search(sql);
				if(recset.next())
				{
					if("UN".equalsIgnoreCase(recset.getString("codesetid")))
					{
						un=recset.getString("codeitemid");
						break;
					}
					else
						umCode=recset.getString("codeitemid");
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return un;
	}
	
	
	
	
	/**
	 * 创建临时表
	 * @param factorList
	 */
	private void createTempTable(ArrayList factorList)
	{
		try
		{
			Table table=new Table("t#"+this.userView.getUserName());
			table.setCreatekey(false);
			table.addField(getField("A0100","hmuster.label.machineNo","A",8,0,true));		
			table.addField(getField("B0110","hmuster.label.unitNo","A",30,0,false));
			table.addField(getField("E0122","hmuster.label.departmentNo","A",30,0,false));
			table.addField(getField("E01A1","e01a1.label","A",30,0,false));
			table.addField(getField("NBASE","NBASE","A",3,0,true));
			
			for(int b=0;b<factorList.size();b++)
			{
				String itemid=((String)factorList.get(b)).toLowerCase();
				if("a0100".equals(itemid)|| "b0110".equals(itemid)|| "e0122".equals(itemid)|| "e01a1".equals(itemid)|| "nbase".equals(itemid))
					continue;
				FieldItem item=DataDictionary.getFieldItem(itemid);
				table.addField(getField(item.getItemid(),item.getItemdesc(),item.getItemtype(),item.getItemlength(),item.getDecimalwidth(),false));
			}
		
			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable(table.getName(),false))
			{						
				dbWizard.dropTable(table);				
			}
			dbWizard.createTable(table);	
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(table.getName());
			importDataToTempTable(factorList,"t#"+this.userView.getUserName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 往临时表中导入数据
	 * @param factorList
	 * @param tableName
	 */
	private void importDataToTempTable(ArrayList factorList,String tableName)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbw=new DbWizard(this.conn);
			String db_str=getXml().getNodeAttributeValue("/Params/BONUS_SET","stat_dbpre");         //"User";
			HashMap map=new HashMap();
			ArrayList list=new ArrayList();
			list.add("a0100");list.add("b0110");list.add("e0122");list.add("e01a1");
			map.put("A01", list);
			for(int i=0;i<factorList.size();i++)
			{
				String itemid=(String)factorList.get(i);
				if("a0100".equalsIgnoreCase(itemid)|| "b0110".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid)|| "nbase".equalsIgnoreCase(itemid))
					continue;
				FieldItem item=DataDictionary.getFieldItem(itemid);
				String itemSet=item.getFieldsetid().toUpperCase();
				if(map.get(itemSet)!=null)
				{
					list=(ArrayList)map.get(itemSet);
					list.add(itemid);
					map.put(itemSet,list);
				}
				else
				{
					list=new ArrayList();
					list.add(itemid);
					map.put(itemSet,list);
				}
			}
			
			String[] dbs=db_str.split(",");
			for(int i=0;i<dbs.length;i++)
			{
				if(dbs[i].length()==0)
					continue;
				Set keySet=map.keySet();
				list=(ArrayList)map.get("A01");
				StringBuffer _str=new StringBuffer("");
				for(int j=0;j<list.size();j++)
				{
					_str.append(","+(String)list.get(j));
				}
				String sql="insert into "+tableName+"("+_str.substring(1)+",nbase) select "+_str.substring(1)+",'"+dbs[i]+"' from "+dbs[i]+"A01";
				dao.update(sql);
				for(Iterator t=keySet.iterator();t.hasNext();)
				{
					String set=(String)t.next();
					if("A01".equalsIgnoreCase(set))
						continue;
					list=(ArrayList)map.get(set);
					String srcTab=dbs[i]+set;
					_str.setLength(0);
					for(int j=0;j<list.size();j++)
					{
						_str.append("`"+tableName+"."+(String)list.get(j)+"="+srcTab+"."+(String)list.get(j));
					}
					String joinStr=tableName+".A0100="+srcTab+".A0100";
					String buf=" upper("+tableName+".nbase)='"+dbs[i].toUpperCase()+"'";
					srcTab="(select * from "+srcTab+" a where a.i9999=(select max(b.i9999) from "+srcTab+" b where a.a0100=b.a0100  ) ) "+srcTab;
					dbw.updateRecord(tableName,srcTab,joinStr,_str.substring(1), buf.toString(),buf.toString());
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	private Field getField(String name,String describe,String datatype,int length,int decimalwidth,boolean isKey)
	{
		
		String a_describe=describe;
		if(describe.indexOf(".")!=-1)
			a_describe=ResourceFactory.getProperty(describe);
		Field temp=new Field(name,a_describe);
		temp.setKeyable(isKey);			
		if(length!=0)
			temp.setLength(length);
		if("N".equalsIgnoreCase(datatype))
		{
			if(decimalwidth==0)
			{
				temp.setDatatype(DataType.INT);
			}
			else
			{
				temp.setDatatype(DataType.FLOAT);
				temp.setDecimalDigits(decimalwidth);
			}
		}
		if("A".equalsIgnoreCase(datatype))
		{
			temp.setDatatype(DataType.STRING);
		}
		if("D".equalsIgnoreCase(datatype))
		{
			temp.setDatatype(DataType.DATE);
		}
		if("M".equalsIgnoreCase(datatype))
		{
			temp.setDatatype(DataType.CLOB);
		}
		return temp;
	}
	
	
	/**
	 * 取得所有统计公式的因子
	 * @param formulalist
	 * @return
	 */
	private ArrayList getFactors(ArrayList formulalist,HashMap allFieldMap)
	{
		ArrayList list=new ArrayList();
		HashSet set=new HashSet();
		for(int i=0;i<formulalist.size();i++)
		{
            DynaBean dbean=(LazyDynaBean)formulalist.get(i);
            String fmode=(String)dbean.get("fmode");
            if("2".equals(fmode))  //统计公式
            {
            	String smode=(String)dbean.get("smode");
            	String cond=(String)dbean.get("cond");
            	String rexpr="";
            	//=0 求个数  =1 求和   =2 最小值  =3 最大值  =4 平均值
            	if(!"0".equals(smode))
            		rexpr=(String)dbean.get("rexpr");
            	if(rexpr.length()>0)
            	{
            		ArrayList _list=analyseStatExpr(rexpr,allFieldMap);
            		for(int j=0;j<_list.size();j++)
            			set.add((String)_list.get(j));
            	}
            	
            	if(cond!=null&&cond.trim().length()>0)
            	{
            		String[] temp=cond.split("\\|");
            		if(temp.length>0)
            		{
            			ArrayList _list=analyseCondFactor(temp[0]);
                		for(int j=0;j<_list.size();j++)
                			set.add((String)_list.get(j));
            		}            		
            	}
            	
            }
		}
		
		for(Iterator t=set.iterator();t.hasNext();)
			list.add((String)t.next());
		return list;
	}
	
	
	/**
	 * 分析统计公式条件因子
	 * @param cond
	 * @return
	 */
	private ArrayList analyseCondFactor(String cond)
	{
		ArrayList factorList=new ArrayList();
		String[] tempArr=cond.split("`");
		for(int ii=0;ii<tempArr.length;ii++)
		{
		
			int a=0;
			String subTemp=tempArr[ii];
			if(subTemp.indexOf("=")!=-1&&!"<".equals(subTemp.substring(subTemp.indexOf("=")-1,subTemp.indexOf("=")))&&!">".equals(subTemp.substring(subTemp.indexOf("=")-1,subTemp.indexOf("="))))
				a=subTemp.indexOf("=");
			else if(subTemp.indexOf(">")!=-1&&!"<".equals(subTemp.substring(subTemp.indexOf(">")-1,subTemp.indexOf(">")))&&!"=".equals(subTemp.substring(subTemp.indexOf(">")+1,subTemp.indexOf(">")+2)))
				a=subTemp.indexOf(">");
			else if(subTemp.indexOf("<")!=-1&&!"=".equals(subTemp.substring(subTemp.indexOf("<")+1,subTemp.indexOf("<")+2))&&!">".equals(subTemp.substring(subTemp.indexOf("<")+1,subTemp.indexOf("<")+2)))
				a=subTemp.indexOf("<");
			else if(subTemp.indexOf(">=")!=-1)
				a=subTemp.indexOf(">=");
			else if(subTemp.indexOf("<=")!=-1)
				a=subTemp.indexOf("<=");
			else if(subTemp.indexOf("<>")!=-1)
				a=subTemp.indexOf("<>");
			if(a!=0)
			{
				String factor=subTemp.substring(0,a);
				factorList.add(factor.toLowerCase());
			}
		}
		return factorList;
	}
	
	
	
	
	
	
	public static void main(String[] args)
	{
		String s="sdfasd=1|dfsd";
		String[] temp=s.split("\\|");
		
	}
	
	
	/**
	 * 分析出统计表达式中的因子
	 * @param statExpr  统计表达式  exp:(A0110+B0110*2)/32.56-(A0114)
	 * @author dengc
	 * @return 因子列表
	 */
	private ArrayList analyseStatExpr(String statExpr,HashMap allFieldMap)
	{
		String a_statExpr="("+statExpr+")";
		ArrayList factorList=new ArrayList();
		int begIndex=0;
		for(int i=0;i<a_statExpr.length();i++)
		{
			char t=a_statExpr.charAt(i);			
			int endIndex=0;
			if(t=='('||t==')'||t=='+'||t=='-'||t=='*'||t=='/')
			{
				begIndex=i;
			}
			else
			{
				for(int b=i;b<a_statExpr.length();b++)
				{
					char tt=a_statExpr.charAt(b);
					if(tt=='('||tt==')'||tt=='+'||tt=='-'||tt=='*'||tt=='/')
					{
						endIndex=b;
						break;
					}
				}
			}
			if(endIndex>begIndex)
			{
				factorList.add(a_statExpr.substring(begIndex+1,endIndex).trim());
				i=endIndex-1;
			}
		}
		
		ArrayList list=new ArrayList();
		for(int i=0;i<factorList.size();i++)
		{
			if(allFieldMap.get((String)factorList.get(i))!=null)
			{
				FieldItem Field=(FieldItem)allFieldMap.get((String)factorList.get(i));
				list.add(Field.getItemid());
			}
		}
		
		factorList=null;
		factorList=list;
		return factorList;
	}
	
	
	
	
	
	/**
	 * 批量导入
	 * @param formulaBean
	 * @param where_str  导入数据范围sql
	 * @param dataBean   日期条件
	 * @param operateUnitCode 操作单位
	 * @return
	 * @throws GeneralException
	 */
	public boolean batchImportFromArchive(HashMap allFieldMap,DynaBean formulaBean,String where_str,LazyDynaBean dataBean,String operateUnitCode)throws GeneralException
	{
		boolean bflag=true;
		try
		{
			//0:当前记录  1：月内最初第一条  2：月内最近第一条 3：小于本次月内最初第一条  4：小于本次月内最近第一条
			String smode=(String)formulaBean.get("smode");
			String rexpr=((String)formulaBean.get("rexpr")).trim();
			DbWizard dbw=new DbWizard(this.conn);
			if("0".equals(smode)&&(allFieldMap.get(rexpr.trim().toLowerCase())!=null||DataDictionary.getFieldItem(rexpr.trim())!=null))
			{
				
				FieldItem field=(FieldItem)allFieldMap.get(rexpr.trim().toLowerCase());
				if(field==null)
					field=(FieldItem)DataDictionary.getFieldItem(rexpr.trim());
				String tablename=field.getFieldsetid();
				String premiumSetId=getXml().getNodeAttributeValue("/Params/BONUS_SET","setid");
				StringBuffer updStr=new StringBuffer(premiumSetId+"."+(String)formulaBean.get("itemname")+"="+tablename+"."+field.getItemid());
				
				StringBuffer buf=new StringBuffer(" "+premiumSetId+".B0110 in ( ");
				buf.append(where_str+" ) ");
				buf.append(" and "+Sql_switcher.year(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("year"));
				buf.append(" and "+Sql_switcher.month(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("month"));
				
				String srcTab=tablename;
				if(!"B01".equalsIgnoreCase(tablename))
				{
					srcTab="(select * from "+tablename+" a where a.i9999=(select max(b.i9999) from "+tablename+" b where a.b0110=b.b0110  ) ) "+tablename;
				}
				String joinStr=premiumSetId+".B0110="+tablename+".B0110";
				
				dbw.updateRecord(premiumSetId,srcTab,joinStr,updStr.toString(), buf.toString(),buf.toString());
			}
			else
			{
				batchImportFromArchive2(dbw,formulaBean,where_str,dataBean,operateUnitCode);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
			throw GeneralExceptionHandler.Handle(ex);			
		}
		return bflag;	
	}
	
	
	
	
	public void batchImportFromArchive2(DbWizard dbw,DynaBean formulaBean,String where_str,LazyDynaBean dataBean,String operateUnitCode)
	{
		try
		{
			StringBuffer buf=new StringBuffer();
			String smode=(String)formulaBean.get("smode");
			String rexpr=((String)formulaBean.get("rexpr")).trim();
			
			String stry=(String)dataBean.get("year");
			String strm=(String)dataBean.get("month");
			String strc="1";
			ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			YksjParser yp = new YksjParser(this.userView, allUsedFields,
					YksjParser.forSearch, getDataType((String)formulaBean.get("itemtype")),  YksjParser.forUnit, "Ht", "");
			
			String axxz1="";//所属次数指标
			String axxz0="";//所属期指标
			int nheap=Integer.parseInt(smode);
			ArrayList a_fieldlist=yp.getFormulaFieldList(rexpr);
			if(nheap==1||nheap==2||nheap==3||nheap==4)
			{
					HashSet aset=new HashSet();
					String asetid="";
					for(int i=0;i<a_fieldlist.size();i++)
					{
						FieldItem field=(FieldItem)a_fieldlist.get(i);
						aset.add(field.getFieldsetid());
						asetid=field.getFieldsetid();
					}
					if(aset.size()==1)
					{
						axxz1=asetid+"Z1";//所属次数指标
						axxz0=asetid+"Z0";//所属期指标
					}
			}
			if(axxz0.length()>0)
			{
				switch(nheap)
				{
					case 0:// 当前记录
						break;
					case 1:  // 月内最初第一条
						rexpr="SELECT("+rexpr+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry+",FIRST)";
						 break;
					case 2:  // 月内最近第一条
						rexpr="SELECT("+rexpr+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry+",LAST)";
						 break;
					case 3:  // 小于本次月内最初第一条
						rexpr="SELECT("+rexpr+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry
						                          +" AND "+axxz1+"<1,FIRST)";
						break;
					case 4:  // 小于本次月内最近第一条
						rexpr="SELECT("+rexpr+",Month("+axxz0+")="+strm+" AND Year("+axxz0+")="+stry
													+" AND "+axxz1+"<1,LAST)";
						break;
				}
			}
			ContentDAO dao=new ContentDAO(this.conn);
			String tablename="t#"+this.userView.getUserName()+"_gz"; //this.userView.getUserName()+"midtable";
			ArrayList usedlist=new ArrayList();
			FieldItem fielditem=new FieldItem("B01","B0110");
			fielditem.setItemdesc("单位名称");
			fielditem.setCodesetid("UN");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			usedlist.add(fielditem);
			
			/**追加公式中使用的指标*/
			appendUsedFields(a_fieldlist,usedlist);
			/**增加一个计算公式用的临时字段*/
			fielditem=new FieldItem("B01","AAAAA");
			fielditem.setItemdesc("AAAAA");
			fielditem.setCodesetid("0");
			fielditem.setItemtype((String)formulaBean.get("itemtype"));
			FieldItem _item=DataDictionary.getFieldItem(((String)formulaBean.get("itemname")).toLowerCase());
			fielditem.setItemlength(_item.getItemlength());
			fielditem.setDecimalwidth(0);
			usedlist.add(fielditem);
			/**创建计算用临时表*/
			if(createMidTable(usedlist,tablename,"B0110"))
			{
				/**导入单位主集数据B0110*/
				buf.setLength(0);
				buf.append("insert into ");
				buf.append(tablename);
				buf.append("(B0110) select B0110 FROM B01 where b0110 in ("+where_str+")");
				dao.update(buf.toString());
			}// 创建临时表结束.
			String premiumSetId=getXml().getNodeAttributeValue("/Params/BONUS_SET","setid");
			StringBuffer buf0=new StringBuffer(" "+premiumSetId+".B0110 in ( ");
			buf0.append(where_str+" ) ");
			buf0.append(" and "+Sql_switcher.year(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("year"));
			buf0.append(" and "+Sql_switcher.month(premiumSetId+"."+premiumSetId+"Z0")+"="+(String)dataBean.get("month"));
			
			YearMonthCount ymc=new YearMonthCount(Integer.parseInt(stry),Integer.parseInt(strm),Integer.parseInt(strc));
			yp.run(rexpr,ymc,"AAAAA",tablename,dao,"",this.conn,(String)formulaBean.get("itemtype"),_item.getItemlength(),1,_item.getCodesetid());

			dbw.updateRecord(premiumSetId,tablename,premiumSetId+".B0110="+tablename+".B0110", premiumSetId+"."+((String)formulaBean.get("itemname"))+"="+tablename+".AAAAA",buf0.toString(),buf0.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	/**
	 * 创建计算用的临时表
	 * @param fieldlist
	 * @param tablename
	 * @param keyfield
	 * @return
	 */
	private boolean createMidTable(ArrayList fieldlist,String tablename,String keyfield)
	{
		boolean bflag=true;
		try
		{
			DbWizard dbw=new DbWizard(this.conn);
			if(dbw.isExistTable(tablename, false))
				dbw.dropTable(tablename);
			Table table=new Table(tablename);
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem=(FieldItem)fieldlist.get(i);
				Field field=fielditem.cloneField();
				if(field.getName().equalsIgnoreCase(keyfield))
				{
					field.setNullable(false);
					field.setKeyable(true);
				}
				table.addField(field);
			}//for i loop end.
			Field field=new Field("userflag","userflag");
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
			dbw.createTable(table);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	
	
	
	/**
	 * 追加不同的指标
	 * @param slist
	 * @param dlist
	 */
	private void appendUsedFields(ArrayList slist,ArrayList dlist)
	{
		boolean bflag=false;
		for(int i=0;i<slist.size();i++)
		{
			FieldItem fielditem=(FieldItem)slist.get(i);
			String itemid=fielditem.getItemid();
			for(int j=0;j<dlist.size();j++)
			{
				bflag=false;
				FieldItem fielditem0=(FieldItem)dlist.get(j);
				String ditemid=fielditem0.getItemid();
				if(itemid.equalsIgnoreCase(ditemid))
				{
					bflag=true;
					break;
				}

			}//for j loop end.
			if(!bflag)
				dlist.add(fielditem);			
		}//for i loop end.
	}
	
	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	
	
	/**
	 * 取得当前薪资类别计算公式列表
	 * @param flag =1 (有效计算公式) =-1全部的计算公式
	 * 
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getFormulaList(ArrayList itemids)throws GeneralException
	{
		ArrayList _list=new ArrayList();
		StringBuffer buf=new StringBuffer();
		RowSet rset=null;
		try
		{
			
				buf.append("select * from bonusformula  ");
				StringBuffer str=new StringBuffer("");
				for(int i=0;i<itemids.size();i++)
				{
					str.append(","+(String)itemids.get(i));
				}
				if(itemids.size()>0)
					buf.append(" where itemid in ("+str.substring(1)+") and  useflag=1 ");
				else
					buf.append(" where useflag=1 ");
				buf.append(" order by fmode,sortid");
				ContentDAO dao=new ContentDAO(this.conn);
				rset=dao.search(buf.toString());
				ArrayList list=dao.getDynaBeanList(rset);
				if(rset!=null)
					rset.close();
				ArrayList formulaList=new ArrayList();
				LazyDynaBean abean=null;
				for(int i=0;i<list.size();i++)
				{
					abean=(LazyDynaBean)list.get(i);
					String fmode=(String)abean.get("fmode");
					if("0".equals(fmode))
						formulaList.add(abean);
					else
						_list.add(abean);
				}
				_list.addAll(formulaList);
				
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		finally
		{
			try
			{
				if(rset!=null)
					rset.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}			
		return _list;
	}
	


	public ConstantXml getXml() {
		return xml;
	}

	public void setXml(ConstantXml xml) {
		this.xml = xml;
	}
	
	 /**
         * 求当前处理到的最大业务日期
         * 
         * @param state
         * @return
         * @throws GeneralException
         */
    public String getMaxYearMonth(String orgsubset, String orgcode) throws GeneralException
    {
	String busidatefild = orgsubset + "z0";
	String strYm = null;
	String childOrgSqlStr ="select codeitemid from organization where  codesetid in ('UM','UN') and parentid='" + orgcode + "'";
		
	ContentDAO dao = new ContentDAO(this.conn);
	try
	{
		boolean isLeafOrg = false;
	  String sql = "select codeitemid from organization where  codesetid in ('UM','UN') and parentid='"+orgcode+"'";
	   RowSet rset = dao.search(sql);
	   if (rset.next())
	   {}
	   else
		   isLeafOrg = true;
		
	   StringBuffer buf = new StringBuffer();
		buf.append("select max(" + busidatefild + ") aa from ");
		buf.append(orgsubset);
		
		if(isLeafOrg)
			buf.append(" where b0110='"+orgcode+"'");
		else
			buf.append(" where b0110 in ("+childOrgSqlStr+")");
	   
				
	     rset = dao.search(buf.toString());
	    if (rset.next())
		strYm = PubFunc.FormatDate(rset.getDate("aa"), "yyyy-MM-dd");
	    if ("".equalsIgnoreCase(strYm))
	    {

		strYm = DateUtils.format(new Date(), "yyyy-MM-dd");
		String[] tmp = StringUtils.split(strYm, "-");
		strYm = tmp[0] + "-" + tmp[1] + "-01";
	    }
	    rset.close();
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	}
	return strYm;
    }
}
