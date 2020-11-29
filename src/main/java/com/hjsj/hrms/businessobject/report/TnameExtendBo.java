package com.hjsj.hrms.businessobject.report;


import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;





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


public class TnameExtendBo {
	Connection conn=null;
	private TgridBo    tgridBo=null;
	private TnameBo    tnameBo=null;
	private HashMap tparamMap=null; 
	private String scopeid ="0";
	private Hashtable tparamHashTable=null;
	
	public TnameExtendBo(Connection conn)
	{
		this.conn=conn;
		tgridBo=new TgridBo(conn);
		tnameBo=new TnameBo(conn);
		this.tparamMap=getParamMap();
		this.tparamHashTable =getParamHashtable();
	}
	
	
	

			
	public HashMap getParamMap()
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=dao.search("select paramename from tparam");
			while(recset.next()) {
                map.put(recset.getString(1).toLowerCase(),"1");
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	public Hashtable getParamHashtable()
	{
		Hashtable map=new Hashtable();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet recset=dao.search("select paramename from tparam");
			while(recset.next()) {
                map.put(recset.getString(1).toLowerCase(),"1");
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	//取得查询条件下涉及到的指标信息
	public ArrayList getFieldInfo(ArrayList infoList)
	{	
		ArrayList fieldSet=new ArrayList();
		boolean isOther=false;
		for(int i=0;i<infoList.size();i++)
		{
			String[] temp=(String[])infoList.get(i);
			if("4".equals(temp[1])&& "0".equals(temp[9])) {
                continue;
            }
			
			if(!"2".equals(temp[1]))
			{
				isOther=true;
				break;
			}
			/*   JinChunhai 2012.10.15
			else
			{				
				if(!tgridBo.getCexpr2Context(4,temp[5]).equals("主集"))
				{
					isOther=true;
					break;
				}				
			}
			*/
			//wangcq 2014-11-26 没有条件支持反查
//			if(temp[3].trim().equals(""))
//				continue;
			if("6".equals(temp[0]) && !"1".equals(temp[2])){//wangcq 历史时点非个数反查过滤
				isOther=true;
				break;
			}
			String[] a_temp=temp[3].split("`");
			ArrayList itemidSet=getField(a_temp);;			
			for(Iterator t=itemidSet.iterator();t.hasNext();)
			{
				String a_itemid=(String)t.next();
				if(!fieldSet.contains(a_itemid)) {
                    fieldSet.add(a_itemid);
                }
			}
		
		}
		if(isOther) {
            return new ArrayList();
        }
		
		return fieldSet;
	}
	
	
	
	//取得某 公式涉及到的指标 及 指标与值的对应字符串
	public ArrayList getField(String[] expr_arr)
	{
		ArrayList itemidSet=new ArrayList();
		for(int i=0;i<expr_arr.length;i++)
		{
			String expr=expr_arr[i];
			String itemid="";
			
			if(expr.indexOf("=")!=-1&&!"<".equals(expr.substring(expr.indexOf("=")-1,expr.indexOf("=")))&&!">".equals(expr.substring(expr.indexOf("=")-1,expr.indexOf("="))))
			{
				itemid=expr.substring(0,expr.indexOf("="));		
			}
			else if(expr.indexOf(">")!=-1&&!"<".equals(expr.substring(expr.indexOf(">")-1,expr.indexOf(">")))&&!"=".equals(expr.substring(expr.indexOf(">")+1,expr.indexOf(">")+2)))
			{
				itemid=expr.substring(0,expr.indexOf(">"));			
			}
			else if(expr.indexOf("<")!=-1&&!"=".equals(expr.substring(expr.indexOf("<")+1,expr.indexOf("<")+2))&&!">".equals(expr.substring(expr.indexOf("<")+1,expr.indexOf("<")+2)))
			{
				itemid=expr.substring(0,expr.indexOf("<"));
			}
			else if(expr.indexOf("<>")!=-1)
			{				
				itemid=expr.substring(0,(expr.indexOf("<>")));
			}
			else if(expr.indexOf(">=")!=-1)
			{
				itemid=expr.substring(0,expr.indexOf(">="));
			}
			else if(expr.indexOf("<=")!=-1)
			{
				itemid=expr.substring(0,expr.indexOf("<="));
			}
			
			if(!itemidSet.contains(itemid)) {
                itemidSet.add(itemid);
            }
				
		}		
		return itemidSet;
	}
	
	
	//得到代码指标对应的代码值 map
	public HashMap getFieldCodeMap(ArrayList fieldSet)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			StringBuffer in_str=new StringBuffer("");
			for(Iterator t=fieldSet.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				in_str.append(",'"+temp+"'");
			}
			recset=dao.search("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid in (select codesetid from fielditem where itemid in ("+in_str.substring(1)+") and codesetid<>'0' )");
			while(recset.next()) {
                map.put(recset.getString("codesetid")+"##"+recset.getString("codeitemid"),recset.getString("codeitemdesc"));
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	//得到指标对应的 类型 和 代码集
	public ArrayList  getFieldTypeMap(ArrayList fieldSet)
	{
		ArrayList list=new ArrayList();
		HashMap typeMap=new HashMap();
		HashMap setMap=new HashMap();
		setMap.put("E01A1","@K");
		setMap.put("B0110","UN");
		HashMap nameMap=new HashMap();
		nameMap.put("E01A1", "职位");
		nameMap.put("B0110", "单位");
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			StringBuffer in_str=new StringBuffer("");
			for(Iterator t=fieldSet.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				in_str.append(",'"+temp+"'");
			}
			recset=dao.search("select itemid,itemtype,codesetid,itemdesc from fielditem where itemid in ("+in_str.substring(1)+")");
			while(recset.next())
			{
				typeMap.put(recset.getString("itemid"),recset.getString("itemtype"));
				setMap.put(recset.getString("itemid"),recset.getString("codesetid"));
				nameMap.put(recset.getString("itemid"),recset.getString("itemdesc"));
			}
			list.add(typeMap);
			list.add(setMap);
			list.add(nameMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] arg)
	{
		String ss="E0122=0102`";
		String[] a=ss.split("`");
		System.out.println(a.length);;
		
		
	}
	
	
	
	
	
	
	
	////////////////////////////////////////////////-------------归档---------------/////////////////////////////////////
	
	
	
	/**
	 * 报表归档
	 * @param resultList
	 * @param paramValue
	 * @param tabid
	 * @param rows
	 * @param cols
	 * @param userid
	 * @param userName
	 * @param unitcode
	 * @param year  年份
	 * @param count 
	 * @param selfType 本身的报表类型
	 * @param reportTYpe 保存的报表类型
	 * @return  1:归档成功 2：表内、表间校验错误 3：报表保存错误，归档不成功 4：归档不成功
	 */
	public String ReportPigeonholeTrans(TnameBo tnameBo,ArrayList resultList,String paramValue,String tabid,int rows,int cols,String userid,String userName,String unitcode,String year,String count,String selfType,String reportType,String operateObject,String week)
	{
		String info="1";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			TgridBo bo = new TgridBo(this.conn);
			ArrayList list = getPigeonholeCS(bo,unitcode,tabid);
			for(int i=0;i<list.size();i++){
				if(!"0".equals(list.get(i))&&!list.get(i).equals(reportType)){
					info="9";
					return info;
				}
			}
			if("1".equals(operateObject)){
			//保存编辑后的报表数据
			if("0".equals(tnameBo.saveReportInfo(resultList,paramValue,tabid,rows,cols,userid,userName,0,operateObject,"")))
			{
				info="3";
				return info;
			}
			}else{
				if("0".equals(tnameBo.saveReportInfo(resultList,paramValue,tabid,rows,cols,userid,userName,0,operateObject,unitcode)))
				{
					info="3";
					return info;
				}
			}
			//归档
			this.tnameBo=tnameBo;          //
			execute_Ta_table(tabid,tnameBo);	//判断是否存在统计结果归档表，如果没有则产生一个
			compareTaTable(tnameBo,tabid);		//比较统计结果表与当前的报表格式是否一致,如果不一致，则动态添加
			
			if(!selfType.equals(reportType))	//如果报表该用了新类型，则删除以前的所有记录
			{
				// "delete from ta_"+tabid+" where unitcode='"+unitcode+"'"
				//dao.delete("delete from ta_"+tabid+" where unitcode='"+unitcode+"'",new ArrayList());yuan laide 
				dao.delete("delete from ta_"+tabid, new ArrayList());//dml 2011-02-26
				dao.update("update tname set narch="+reportType+" where tabid="+tabid );
			}
		
			//产生次数
			int countid=0;
			if("1".equals(reportType))          //如果为一般类型的报表
			{
				if(this.scopeid!=null&&!"0".equals(this.scopeid)){
					recset=dao.search("select max(countid) from ta_"+tabid+"  where unitcode='"+unitcode+"' and yearid="+year+" and scopeid="+this.scopeid);
				}else{
					recset=dao.search("select max(countid) from ta_"+tabid+"  where unitcode='"+unitcode+"' and yearid="+year);
				}
				
				if(recset.next()) {
                    countid=recset.getInt(1)+1;
                } else {
                    countid++;
                }
			}
			dealwithData(reportType,year,count,unitcode,tabid,week);
			//插入数据
			for(int i=0;i<resultList.size();i++)
			{
				RecordVo vo=(RecordVo)tnameBo.getColInfoBGrid().get(i);	
				StringBuffer sql_1=new StringBuffer("unitcode,secid,yearid,countid,row_item");
				StringBuffer sql_2=new StringBuffer("'"+unitcode+"',"+(i+1)+","+year+",");
				if("1".equals(reportType)) {
                    sql_2.append(countid);
                } else if("2".equals(reportType)) {
                    sql_2.append(1);
                } else {
                    sql_2.append(count);
                }
				
				String row_item=String.valueOf(i+1);
				if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                    row_item=vo.getString("archive_item");
                }
				sql_2.append(",'"+row_item+"'");
				
				if("6".equals(reportType))
				{
					sql_1.append(",weekid");
					sql_2.append(","+week);
				}
				if(this.scopeid!=null&&!"0".equals(this.scopeid)){
					sql_1.append(",scopeid");
					sql_2.append(","+this.scopeid);
				}
				
				String result=(String)resultList.get(i);
				String[] result_arr=result.split("/");
				for(int j=0;j<result_arr.length;j++)
				{
					RecordVo a_vo=(RecordVo)tnameBo.getRowInfoBGrid().get(j);
					String fieldname="C"+(j+1);
					if(a_vo.getString("archive_item")!=null&&!"".equals(a_vo.getString("archive_item"))&&!" ".equals(a_vo.getString("archive_item"))) {
                        fieldname=a_vo.getString("archive_item");
                    }
					sql_1.append(","+fieldname);
					sql_2.append(","+result_arr[j]);
				}	
		//		System.out.println("insert into ta_"+tabid+" ( "+sql_1.toString()+" ) values ("+sql_2.toString()+" )");
				dao.insert("insert into ta_"+tabid+" ( "+sql_1.toString()+" ) values ("+sql_2.toString()+" )",new ArrayList());
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			info="4";
		}
	
		return info;
	}
	public ArrayList getPigeonholeCS(TgridBo tnameBo,String unitcode,String tabid){
		ArrayList templist = new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		try{
			String sql = "select cexpr2 from tgrid2 where  tabid = "+tabid+"";
			rs=dao.search(sql);
			while(rs.next()){
				String cexpr22 = tnameBo.getCexpr2Context(15,rs.getString("cexpr2"));
				templist.add(cexpr22);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return templist;
	}
	
	//判断数据是否已归档
	public boolean isExistData(String reportType,String year,String count,String unitcode,String tabid,String week)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		boolean is=false;
		try
		{
			String sql="";
			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable("ta_"+tabid, false))
			{
				if(!"1".equals(reportType))
				{
					if(!"0".equals(this.scopeid)){
						if("2".equals(reportType)) {
                            sql="select * from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and scopeid="+this.scopeid;
                        }
						if("3".equals(reportType)|| "4".equals(reportType)|| "5".equals(reportType)) {
                            sql="select * from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and countid="+count+" and scopeid="+this.scopeid;
                        }
						if("6".equals(reportType)) {
                            sql="select * from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and countid="+count+" and weekid="+week+" and scopeid="+this.scopeid;
                        }
						
					}else{
						if("2".equals(reportType)) {
                            sql="select * from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year;
                        }
						if("3".equals(reportType)|| "4".equals(reportType)|| "5".equals(reportType)) {
                            sql="select * from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and countid="+count;
                        }
						if("6".equals(reportType)) {
                            sql="select * from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and countid="+count+" and weekid="+week;
                        }
						
					}
					recset=dao.search(sql);
					if(recset.next()) {
                        is=true;
                    }
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return is;
		
	}
	
	
	//如果需保存的数据已存在，则删除
	public void dealwithData(String reportType,String year,String count,String unitcode,String tabid,String week)
	{
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			if(this.scopeid!=null&&!"0".equals(this.scopeid)){
				if("2".equals(reportType)) {
                    dao.delete("delete from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and scopeid="+this.scopeid,new ArrayList());
                }
				if("3".equals(reportType)|| "4".equals(reportType)|| "5".equals(reportType)) {
                    dao.delete("delete from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and countid="+count+" and scopeid="+this.scopeid,new ArrayList());
                }
				if("6".equals(reportType)) {
                    dao.delete("delete from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and countid="+count+" and weekid="+week+" and scopeid="+this.scopeid,new ArrayList());
                }

			}else{
				if("2".equals(reportType)) {
                    dao.delete("delete from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year,new ArrayList());
                }
				if("3".equals(reportType)|| "4".equals(reportType)|| "5".equals(reportType)) {
                    dao.delete("delete from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and countid="+count,new ArrayList());
                }
				if("6".equals(reportType)) {
                    dao.delete("delete from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+year+" and countid="+count+" and weekid="+week,new ArrayList());
                }

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	//比较统计结果表与当前的报表格式是否一致,如果不一致，则动态添加,删除
	public void compareTaTable(TnameBo tnameBo,String tabid)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select * from ta_"+tabid+" where 1=2");
			ResultSetMetaData data=recset.getMetaData();
			DbWizard dbWizard=new DbWizard(this.conn);
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			Table table=new Table("ta_"+tabid);
			//动态添加
			int isAdd=0;
			for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
		    {
		    	RecordVo vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
		    	String fieldname="C"+(i+1);
		    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                    fieldname=vo.getString("archive_item");
                }
				boolean isExistColumn=false;
		    	for(int a=0;a<data.getColumnCount();a++)
				{
					String columnName=((String)data.getColumnName(a+1)).toLowerCase();
					if(columnName.equals(fieldname.toLowerCase())) {
                        isExistColumn=true;
                    }
					if(isExistColumn) {
                        break;
                    }
				}
				if(!isExistColumn)
				{
					Field obj=tgridBo.getField2(fieldname,fieldname,"N");
					table.addField(obj);
					isAdd++;
				}
			}
			if(isAdd>0)
			{
				dbWizard.addColumns(table);
				dbmodel.reloadTableModel(table.getName());
			}
			
			RecordVo vo2=new RecordVo(table.getName());
			if(!vo2.hasAttribute("weekid"))
			{
				
				table=new Table("ta_"+tabid);
				Field obj=new Field("weekid","weekid");
				obj.setDatatype(DataType.INT);
				obj.setAlign("left");				
				table.addField(obj);
				dbWizard.addColumns(table);
				dbmodel.reloadTableModel("ta_"+tabid);
			}
			
			//动态删除列
			Table table2=new Table("ta_"+tabid);
			int isdelete=0;
		    for(int a=0;a<data.getColumnCount();a++)
			{
					String columnName=((String)data.getColumnName(a+1)).toLowerCase();
					if(!"unitcode".equals(columnName)&&!"secid".equals(columnName)&&!"yearid".equals(columnName)&&!"weekid".equals(columnName)&&!"countid".equals(columnName)&&!"row_item".equals(columnName)&&!"scopeid".equals(columnName))
					{
						boolean isExistColumn=false;
						for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
					    {
					    	RecordVo vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
					    	String fieldname="C"+(i+1);
					    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                                fieldname=vo.getString("archive_item").toLowerCase();
                            }
					    	if(columnName.equals(fieldname.toLowerCase())) {
                                isExistColumn=true;
                            }
					    	if(isExistColumn) {
                                break;
                            }
					    }
						if(!isExistColumn)
						{
							Field obj=tgridBo.getField2(columnName,columnName,"N");
							table2.addField(obj);
							isdelete++;
						}
					}
					
			}
		    if(data!=null) {
                data=null;
            }
			if(isdelete>0)	
			{
				dbWizard.dropColumns(table2);
				dbmodel.reloadTableModel(table2.getName());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
// 判断是否存在统计结果归档表，如果没有则产生一个
	public boolean execute_Ta_table(String tabid,TnameBo tnameBo)throws GeneralException
	{
		boolean flag=true;
		try
		{
			Table table=new Table("ta_"+tabid);
			DbWizard dbWizard=new DbWizard(this.conn);
			if(!dbWizard.isExistTable(table.getName(),false))
			{
				flag=false;
				ArrayList fieldList=getTa_TableFields(tnameBo);
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					Field temp=(Field)t.next();
					table.addField(temp);
				}
				table.setCreatekey(false);	
				dbWizard.createTable(table);	
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(table.getName());
			
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	
	
	
	
	
	
	

	/**
	 * 得到统计结果归档表中列的集合
	 * 
	 * @param
	 * @param
	 * @param
	 * @return
	 */
	public ArrayList getTa_TableFields(TnameBo tnameBo)
	{
		
		ArrayList fieldsList=new ArrayList();	
		fieldsList.add(tgridBo.getField1("unitcode",ResourceFactory.getProperty("ttOrganization.unit.unitcode"),"DataType.STRING",30));
		Field temp21=new Field("secid",ResourceFactory.getProperty("ttOrganization.record.secid"));
		temp21.setDatatype(DataType.INT);
		temp21.setKeyable(true);			
		temp21.setVisible(false);			
		fieldsList.add(temp21);
		Field temp22=new Field("yearid",ResourceFactory.getProperty("edit_report.year"));
		temp22.setDatatype(DataType.INT);
		temp22.setKeyable(true);			
		temp22.setVisible(false);			
		fieldsList.add(temp22);
		Field temp23=new Field("countid",ResourceFactory.getProperty("hmuster.label.counts"));
		temp23.setDatatype(DataType.INT);
		temp23.setKeyable(true);			
		temp23.setVisible(false);			
		fieldsList.add(temp23);	
		
		Field temp33=new Field("weekid","weekid");
		temp33.setDatatype(DataType.INT);
		temp33.setKeyable(true);			
		temp33.setVisible(false);			
		fieldsList.add(temp33);	
		
		fieldsList.add(tgridBo.getField1("row_item","行别名","DataType.STRING",8));
		
	    for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
	    {
	    	RecordVo vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
	    	String fieldname="C"+(i+1);
	    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                fieldname=vo.getString("archive_item");
            }
	    	
			Field obj=tgridBo.getField2(fieldname,fieldname,"N");
			fieldsList.add(obj);
		}
		
	    return fieldsList;
	}
	
	private String weekid="";
	
	/**得到统计结果表中的数据
	  *reportType =1，一般 =2，年 =3，半年 =4，季报 =5，月报 =6,周报  8:年汇总
	  */
	
	public ArrayList getReportAnalyseResult(String unitcode,String yearid,String countid,String tabid,TnameBo tnameBo,String reportType)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		ArrayList listInfo=new ArrayList();
		try
		{
			StringBuffer sql=new StringBuffer("");
			
			StringBuffer s_sql=new StringBuffer("");
			for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
			 {
			    	RecordVo vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
			    	String fieldname="C"+(i+1);
			    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                        fieldname=vo.getString("archive_item");
                    }
			    	s_sql.append(","+fieldname);
			}
			if(s_sql.length()==0){
				return new ArrayList();
			}
			if("2".equals(reportType)) {
                countid="1";
            }
			String subString="select "+s_sql.substring(1)+" from ta_"+tabid+" where unitcode='"+unitcode+"' and yearid="+yearid+" and countid="+countid;
			if("6".equals(reportType))
			{
				if(weekid.length()>0) {
                    subString+=" and weekid="+weekid;
                } else {
                    subString+=" and weekid=1";
                }
			}
			if(tnameBo.getScopeid()!=null&&!"0".equals(tnameBo.getScopeid())){
				subString+=" and scopeid="+tnameBo.getScopeid();
			}
			
			int[][] digitalResults=null;
			tnameBo.setDigitalResults();
			digitalResults=tnameBo.getDigitalResults();		
			
			for(int i=0;i<tnameBo.getColInfoBGrid().size();i++)
			{
				sql.setLength(0);
				RecordVo vo=(RecordVo)tnameBo.getColInfoBGrid().get(i);	
				String row_item=String.valueOf(i+1);
				if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                    row_item=vo.getString("archive_item");
                }
				sql.append(subString+" and row_item='"+row_item+"'");
				if("8".equals(reportType))
				{
					sql.setLength(0);
					sql.append(getCollectSql(i,tnameBo,yearid,unitcode,tabid));
				}
				recset=dao.search(sql.toString());	
				if(recset.next())
				{
					String[] temp=new String[tnameBo.getRowInfoBGrid().size()];
					for(int j=0;j<tnameBo.getRowInfoBGrid().size();j++)
					{
						//按小数位取数			
						int digital=digitalResults[i][j];
//						System.out.println(recset.getString(j+1)+"||"+digital);
						temp[j]=PubFunc.round(recset.getString(j+1),digital);
					}
					listInfo.add(temp);
				}
				else
				{
					String[] temp=new String[tnameBo.getRowInfoBGrid().size()];
					for(int j=0;j<tnameBo.getRowInfoBGrid().size();j++)
					{
						temp[j]="0";
					}
					listInfo.add(temp);
				}
				
			}			
					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listInfo;
	}
	
	

	/**取得列定义归档数据汇总标记
	 *1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数,默认为求和
	 */
	public String getSumFlag(String cexpr2)
	{
		String flag="1";
		if(cexpr2!=null&&cexpr2.trim().length()>0&&cexpr2.toUpperCase().indexOf("<SUMFLAG>")!=-1)
		{
			cexpr2=cexpr2.toUpperCase();
			int fromIndex=cexpr2.indexOf("<SUMFLAG>");
			int toIndex=cexpr2.indexOf("</SUMFLAG>");
			flag=cexpr2.substring(fromIndex+9,toIndex).trim();
			if(flag.length()==0) {
                flag="1";
            }
		}
		return flag;
	}
	
	public String getCollectSql(int j,TnameBo tnameBo,String yearid,String unitcode,String tabid)
	{
		StringBuffer sql=new StringBuffer("");
		
		StringBuffer subString=new StringBuffer(" where yearid="+yearid+" and unitcode='"+unitcode+"'");
		RecordVo col_vo=(RecordVo)tnameBo.getColInfoBGrid().get(j);	
		String cexpr2=col_vo.getString("cexpr2");
		int c_flag=Integer.parseInt(getSumFlag(cexpr2));  // 1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数,默认为求和
		
		String row_item=String.valueOf(j+1);
		if(col_vo.getString("archive_item")!=null&&!"".equals(col_vo.getString("archive_item"))&&!" ".equals(col_vo.getString("archive_item"))) {
            row_item=col_vo.getString("archive_item");
        }
		subString.append(" and row_item='"+row_item+"'");
		
		
		StringBuffer s_sql=new StringBuffer("");
		for(int i=0;i<tnameBo.getRowInfoBGrid().size();i++)
		 {
		    	RecordVo vo=(RecordVo)tnameBo.getRowInfoBGrid().get(i);
		    	String fieldname="C"+(i+1);
		    	if(vo.getString("archive_item")!=null&&!"".equals(vo.getString("archive_item"))&&!" ".equals(vo.getString("archive_item"))) {
                    fieldname=vo.getString("archive_item");
                }
		    	
		    	String r_cexpr2=vo.getString("cexpr2");
		    	int r_flag=Integer.parseInt(getSumFlag(r_cexpr2));  // 1:求和, 2:求均值, 3:求最大值, 4:求最小值, 5:平均人数,默认为求和
		    	int flag=r_flag>c_flag?r_flag:c_flag;
		    	switch(flag)
		    	{
		    		case 1:
		    			fieldname="sum("+fieldname+")";
		    			break;
		    		case 2:
		    			fieldname="avg("+fieldname+")";
		    			break;
		    		case 3:
		    			fieldname="max("+fieldname+")";
		    			break;
		    		case 4:
		    			fieldname="min("+fieldname+")";
		    			break;
		    		case 5:
		    			fieldname="avg("+fieldname+")";
		    			break;
		    	}
		    	s_sql.append(","+fieldname);
		    	
		    	
		}
		sql.append("select "+s_sql.substring(1)+" from ta_"+tabid+subString.toString());
		
		
		return sql.toString();
	}
	
	
	/**
	 * 判断是否存在上报参数表，如果不存在，则生成
	 * @param flag  1：全局 2：表类  3：表
	 * @param tabid	
	 * @param sortid
	 */
	public boolean isExistAppealParamTable(int flag,String tabid,String sortid,DbWizard dbWizard)
	{
		boolean is=true;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		Table a_table=null;	//汇总参数表
		Table o_table=null; //原参数表
		String sql="";
		String tableName="";
		try
		{
			if(flag==1)
			{				
				a_table=new Table("tt_p");
				o_table=new Table("tp_p");
				if(Sql_switcher.searchDbServer()==2) {
                    sql="create table tt_p as select * from tp_p where 1=2 ";
                } else {
                    sql="select *  into tt_p  from tp_p where 1=2 ";
                }
				tableName="tt_p";
			}
			else if(flag==2)
			{		
				a_table=new Table("tt_s"+sortid);
				o_table=new Table("tp_s"+sortid);
				if(Sql_switcher.searchDbServer()==2) {
                    sql="create table tt_s"+sortid+" as select * from tp_s"+sortid+" where 1=2 ";
                } else {
                    sql="select *  into tt_s"+sortid+"  from tp_s"+sortid+" where 1=2 ";
                }
				tableName="tt_s";
			}
			else if(flag==3)
			{
				a_table=new Table("tt_t"+tabid);
				o_table=new Table("tp_t"+tabid);
				if(Sql_switcher.searchDbServer()==2) {
                    sql="create table tt_t"+tabid+" as select * from tp_t"+tabid+" where 1=2 ";
                } else {
                    sql="select *  into tt_t"+tabid+"  from tp_t"+tabid+" where 1=2 ";
                }
				tableName="tt_t"+tabid;
			}
			if(dbWizard.isExistTable(o_table.getName(),false)&&!dbWizard.isExistTable(a_table.getName(),false))
			{					
				dao.update(sql);
				DBMetaModel dbmodel=new DBMetaModel(this.conn);
				dbmodel.reloadTableModel(tableName);
			
				
			}
			if(!dbWizard.isExistTable(o_table.getName(),false)) {
                is=false;
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
		return is;
	}
	
	
	
	//参数上报
	public boolean insertParam(String unitcode,String fromTableName,String toTableName,String userName)
	{
		boolean issuccess=true;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
				String deleteSql="delete from "+toTableName+" where unitcode='"+unitcode+"'";
				recset=dao.search("select * from "+fromTableName+" where 1=2");
				ResultSetMetaData data=recset.getMetaData();
				StringBuffer insertSql=new StringBuffer("insert into "+toTableName+" (unitcode");	
				StringBuffer subSql=new StringBuffer("select '"+unitcode+"'");
				
				for(int i=0;i<data.getColumnCount();i++)
				{
					String temp=data.getColumnName(i+1).toLowerCase().trim();
					if(!"unitcode".equals(temp))
					{
						if(tparamHashTable.get(temp)!=null)
						{
							insertSql.append(","+data.getColumnName(i+1));
							subSql.append(","+data.getColumnName(i+1));
						}
					}
				}
				insertSql.append(" )"+subSql);
				insertSql.append(" from "+fromTableName+" where unitcode='"+userName+"'");			
				dao.delete(deleteSql,new ArrayList());			
				//System.out.println(insertSql.toString());
				dao.insert(insertSql.toString(),new ArrayList());
				
				if(data!=null) {
                    data=null;
                }
					
		}
		catch(Exception e)
		{
			e.printStackTrace();
			issuccess=false;
		}
	
		return issuccess;
	}
	
	
//	参数上报
	public boolean insertParam(String unitcode,String fromTableName,String toTableName,String userName,String sortid,String tabid,int flag)
	{
		boolean issuccess=true;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
				String deleteSql="delete from "+toTableName+" where unitcode='"+unitcode+"'";
				if(flag==1)  //表类
                {
                    recset=dao.search("select paramename from tparam where paramscope=1 and paramname in ( "
                                +" select hz from tpage where flag=9 and tabid in ( "
                                +" select tabid from tname where tsortid="+sortid+" ) )");
                } else if(flag==2) //表
                {
                    recset=dao.search("select paramename from tparam where paramscope=2 and paramname in ( "
                            +" select hz from tpage where flag=9 and tabid="+tabid+" )");
                }
				
				StringBuffer insertSql=new StringBuffer("insert into "+toTableName+" (unitcode");	
				StringBuffer subSql=new StringBuffer("select '"+unitcode+"'");
				while(recset.next())
				{
					String temp=recset.getString("paramename");
					insertSql.append(","+temp);
					subSql.append(","+temp);
					
				}
				insertSql.append(" )"+subSql);
				insertSql.append(" from "+fromTableName+" where unitcode='"+userName+"'");			
				dao.delete(deleteSql,new ArrayList());			
				//System.out.println(insertSql.toString());
				dao.insert(insertSql.toString(),new ArrayList());
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
			issuccess=false;
		}
	
		return issuccess;
	}
	
	
	
	
	
	
	
	
	/**
	 * 根据 表类id(3,4,5)得到相关的tab信息 
	 * @param sortId_str
	 * @return
	 */
	public ArrayList getTableNameList(String sortId_str,UserView userView)
	{
		ArrayList tableList=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			recset=dao.search("select * from tname where tsortid in ("+sortId_str+") order by tsortid");
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				if(!userView.isHaveResource(IResourceConstant.REPORT,recset.getString("tabid"))) {
                    continue;
                }
				
				
				bean.set("tabid",recset.getString("tabid"));
				bean.set("tsortid",recset.getString("tsortid"));
				bean.set("name",recset.getString("name"));
				tableList.add(bean);
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
		return tableList;	
	}
	
	
	
	/**
	 * 根据 表类id(3,4,5)得到相关的tab信息 
	 * @param sortId_str
	 * @return
	 */
	public ArrayList getTableNameList2(String sortId,UserView userView)
	{
		ArrayList tableList=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;
		try
		{
			TnameBo tnamebo  = new TnameBo(this.conn);
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0) {
                tabids=tabids.substring(0,tabids.length()-1);
            }
			StringBuffer sql = new StringBuffer("select * from tname where tsortid="+sortId+" ");
			if(tabids.length()>0) {
                sql.append(" and tabid not in("+tabids+") ");
            }
			sql.append("order  by tsortid,tabid");
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
			String reports =",";
			recset=dao.search("select t.* from tt_organization t,operuser o where o.unitcode=t.unitcode  and o.username='"+userView.getUserName()+"' "+ext_sql+"");
			while(recset.next()){
			reports+=Sql_switcher.readMemo(recset,"report");
			}
			reports+=",";
		
			recset=dao.search(sql.toString());
			while(recset.next())
			{
				DynaBean bean = new LazyDynaBean();
				if(!userView.isHaveResource(IResourceConstant.REPORT,recset.getString("tabid"))) {
                    continue;
                }
				if(reports.indexOf(","+recset.getString("tabid")+",")!=-1) {
                    continue;
                }
				bean.set("tabid",recset.getString("tabid"));
				bean.set("tsortid",recset.getString("tsortid"));
				bean.set("name",recset.getString("name"));
				tableList.add(bean);
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
		return tableList;	
	}





	public String getWeekid() {
		return weekid;
	}





	public void setWeekid(String weekid) {
		this.weekid = weekid;
	}





	public String getScopeid() {
		return scopeid;
	}





	public void setScopeid(String scopeid) {
		this.scopeid = scopeid;
	}
	
	
	
	
	

}
