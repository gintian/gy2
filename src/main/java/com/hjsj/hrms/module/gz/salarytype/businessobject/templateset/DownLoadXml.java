package com.hjsj.hrms.module.gz.salarytype.businessobject.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.definetempvar.businessobject.DefineTempVarBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.*;
import java.util.*;

/**
 * 
 *<p>Title:DownLoadXml.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 7, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class DownLoadXml {
	public static HashMap<String, String> nameMap = new HashMap<String, String>();
	public DownLoadXml(){
		
	}
	
//****************************导入工资类别********************************************* */	
	public static HashMap getAllSalarytemplate(Connection conn)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select salaryid,cname from salarytemplate";
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("salaryid"),rs.getString("salaryid"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 插入  表数据
	 * @param flag   0:无关联关系 1有关联关系  3:else
	 * @param con
	 * @param oKeyTOnKeyMap 主键 更改映射map
	 * @param fileContext   文件内容
	 * @param tableName     导入表名称
	 * @param keyName       表主键 名称
	 * @param foreignKeyName 外键名称
	 * @param foreignKeyMap  外键 更改映射map
	 * @param salaryids      导入数据约束
	 */
		private HashMap sMap = new HashMap();
		public static void importTableData(int flag,Connection con,HashMap oKeyTOnKeyMap,String fileContext,String tableName,String keyName,String foreignKeyName,HashMap foreignKeyMap,String salaryids,HashMap hm)
		{
				try
				{
					ContentDAO dao=new ContentDAO(con);
					int maxID=0;
					RowSet rowSet=dao.search("select max("+keyName+") from "+tableName);
					if(rowSet.next())
						maxID=rowSet.getInt(1);
					int rsdtlid = DbNameBo.getPrimaryKey("reportdetail","rsdtlid",con);
					Document doc = PubFunc.generateDom(fileContext);
					
					//获得xml中的数据
					ArrayList dataList=getTableData(doc,null,"4");
					
					HashMap templatemap =null;
					if("salaryid".equalsIgnoreCase(keyName))
						templatemap= getAllSalarytemplate(con);//追加导入，但是存在类别相同的类别
					ArrayList list=new ArrayList();
					ArrayList tempVar = new ArrayList();
					for(int i=0;i<dataList.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
						String salaryid=(String)abean.get(foreignKeyName);
						if(salaryids!=null)
						{
							
							if("midvariable".equals(tableName))
							{
								if(salaryid!=null&&salaryid.length()>0)
								{
									if(salaryids.indexOf("#"+salaryid+"#")==-1)
										continue;
								}
								String cname=(String)abean.get("cname");
								String chz=(String)abean.get("chz");
								String cstate=(String)abean.get("cstate");
								int type=executeEqualRecord(cname,chz,salaryid,dao,cstate,2,con,tempVar);
									if(type==2||type==1||type==3)
									{
										continue;
									}
							}
							else
							{
								if(salaryids.indexOf("#"+salaryid+"#")==-1)
									continue;
							}
						}
						if(flag!=3)
						{	
							int id=Integer.parseInt((String)abean.get(keyName.toLowerCase()));
							if("salaryid".equalsIgnoreCase(keyName))
							{
						    	if(hm.get(salaryid)==null)//追加导入的，
						    	{
						    		if(templatemap.get(salaryid)!=null)
						    		{
						    			if(DownLoadXml.nameMap.containsKey(salaryid)){//追加，修改薪资类别名称 lis 20160328
						    				abean.set("cname", DownLoadXml.nameMap.get(salaryid));
						    			}
						    			oKeyTOnKeyMap.put(String.valueOf(id),String.valueOf(++maxID));
			    			        	abean.set(keyName.toLowerCase(),String.valueOf(maxID));
						    		}
						    		else
						    		{
			    			        	oKeyTOnKeyMap.put(String.valueOf(id),String.valueOf(id));
			    			        	abean.set(keyName.toLowerCase(),String.valueOf(id));
						    		}
						    	}
						     	else
						    	{
						    		oKeyTOnKeyMap.put(String.valueOf(id),String.valueOf((String)hm.get(salaryid)));
			    		    		abean.set(keyName.toLowerCase(),String.valueOf((String)hm.get(salaryid)));
						    	}
							}else if("stid".equalsIgnoreCase(keyName)){//薪资报表  zhaoxg add 2014-11-10
						    	if(hm.get(salaryid)==null)//追加导入的，
						    	{	foreignKeyMap.put("rsdtlid"+abean.get("rsdtlid"), String.valueOf(rsdtlid));//记住追加后报表编号  为马上导入工资报表项目表所用
						    		foreignKeyMap.put("stid"+abean.get("stid"), (String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));//记住薪资类别号
		    			        	abean.set("rsdtlid",String.valueOf((rsdtlid++)));				    		
		    			        	abean.set(keyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
						    	}else{
		    			        	abean.set(keyName.toLowerCase(),String.valueOf(id));
						    	}							

							}else if("rsdtlid".equalsIgnoreCase(keyName)){//工资报表项目表   zhaoxg add 2014-11-17
						    	if(foreignKeyMap.get("rsdtlid"+abean.get("rsdtlid"))!=null)//追加导入的，
						    	{
		    			        	abean.set("rsdtlid",foreignKeyMap.get("rsdtlid"+abean.get("rsdtlid")));		
		    			        	abean.set("stid",foreignKeyMap.get("stid"+abean.get("stid")));		
						    	}else{
		    			        	abean.set(keyName.toLowerCase(),String.valueOf(id));
						    	}							

							}else
							{
								oKeyTOnKeyMap.put(String.valueOf(id),String.valueOf(++maxID));
		    			    	abean.set(keyName.toLowerCase(),String.valueOf(maxID));
							}
							if(flag==1)
							{
								if("salaryid".equalsIgnoreCase(foreignKeyName))
								{
									if(hm.get(salaryid)==null)
							    	{
										abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
							    	}
							     	else
							    	{
				    		    		abean.set(foreignKeyName.toLowerCase(),String.valueOf((String)hm.get(salaryid)));
							    	}
								}
								else{
						     		abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
								}
							}
						}
						else
						{
							if("salaryid".equalsIgnoreCase(foreignKeyName))
							{
								if(hm.get(salaryid)==null)
						    	{
									abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
						    	}
						     	else
						    	{
			    		    		abean.set(foreignKeyName.toLowerCase(),String.valueOf((String)hm.get(salaryid)));
						    	}
							}
							else
							{
					    		abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
							}
						}
						list.add(abean);
					}
					HashMap salary_column_type=getColumnTypeMap(con,tableName);
					importData(con,list,salary_column_type,tableName);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
		
		/**
		 * 取得表列类型
		 * @param tableName
		 * @return
		 */
		public static HashMap getColumnTypeMap(Connection con,String tableName)
		{
			HashMap columnTypeMap=new HashMap();
			try
			{
				ContentDAO dao = new ContentDAO(con);
				ResultSet resultSet=dao.search("select * from "+tableName+" where 1=2");
				ResultSetMetaData metaData=resultSet.getMetaData();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String name=metaData.getColumnName(i).toLowerCase();
					String type="A";
					int  columnType=metaData.getColumnType(i);
					switch (columnType) {
					case Types.LONGVARCHAR:
					{
						type="M";
						break;
					}
					case Types.TINYINT:
					{
						type="N";
						break;
					}
					case Types.SMALLINT:
					{
						type="N";
						break;
					}
					case Types.INTEGER:
					{
						type="N";
						break;
					}
					case Types.BIGINT:
					{
						type="N";
						break;
					}
					case Types.FLOAT:
					{
						type="F";
						break;
					}
					case Types.DOUBLE:
					{
						type="F";
						break;
					}
					case Types.DECIMAL:
					{
						type="F";
						break;
					}
					case Types.NUMERIC:
					{
						type="F";
						break;
					}
					case Types.REAL: 
					{
						type="F";
						break;
					}
					case Types.DATE:
					{
						type="D";
						break;
					}
					case Types.TIME:
					{
						type="D";
						break;
					}
					case Types.TIMESTAMP: {
						type="D";
						break;
					}
					default:
						type="A";
						break;
					}
					columnTypeMap.put(name,type);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return columnTypeMap;
		}
		
		//导入数据
		public static void importData(Connection con,ArrayList dataList,HashMap columnMap,String tableName)
		{
			Set keySet=columnMap.keySet();
			ArrayList list=new ArrayList();
			try
			{
				ContentDAO dao=new ContentDAO(con);
				Calendar d=Calendar.getInstance();
				for(int i=0;i<dataList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
					RecordVo record_vo=new RecordVo(tableName);
					for(Iterator t=keySet.iterator();t.hasNext();)
					{
						String columnName=((String)t.next()).toLowerCase();					
						{
							String type=(String)columnMap.get(columnName);
							String value=(String)abean.get(columnName);
							if(value!=null&&value.length()>0)
							{
								value = value.replaceAll("<br>","\r\n");//把br转成回车符入库，由于xml文件不能识别回车符，所以导出时候回车符转成<br>了  zhaoxg add 2014-7-18
								if("D".equals(type))
								{
									String[] values=value.split("-");
									Calendar dd=Calendar.getInstance();
									dd.set(Calendar.YEAR,Integer.parseInt(values[0]));
									dd.set(Calendar.MONTH,Integer.parseInt(values[1])-1);
									dd.set(Calendar.DATE,Integer.parseInt(values[2]));
									record_vo.setDate(columnName,dd.getTime());
									
								}
								else if("F".equals(type))
								{
									record_vo.setDouble(columnName,Double.parseDouble(value));
									
								}
								else if("N".equals(type))
								{
									record_vo.setInt(columnName,Integer.parseInt(value));
								}
								else
								{
									record_vo.setString(columnName,value);
								}
								
							}
							
						}
					}
					list.add(record_vo);
				}
				dao.addValueObject(list);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		/**
		 * //取得导入数据
		 * @param doc
		 * @param importStandardIds  需导入的数据id
		 * @param type    1：gz_stand_pkg  2:gz_stand   3:gz_item   4: midvariable
		 * @return
		 */
		public static ArrayList getTableData(Document doc,String[] importStandardIds,String type)
		{
			ArrayList list=new ArrayList();
			try
			{
				StringBuffer allowIDs=new StringBuffer("");

				if(importStandardIds!=null)
				{
					for(int i=0;i<importStandardIds.length;i++)
					{
						allowIDs.append("#"+importStandardIds[i]);
					}
				}
				allowIDs.append("#");
			
				Element root=doc.getRootElement();
				List childrenList=root.getChildren();
				LazyDynaBean a_bean=null;
				Attribute att = null;
				for(Iterator t=childrenList.iterator();t.hasNext();)
				{
					Element record=(Element)t.next();
					
					a_bean =new LazyDynaBean();
					List attributes=record.getAttributes();
					for(int i=0;i<attributes.size();i++)
					{
						att=(Attribute)attributes.get(i);
						a_bean.set(att.getName().toLowerCase(),att.getValue());					
					}
					List children=record.getChildren();
					for(int i=0;i<children.size();i++)
					{
						Element element = (Element)children.get(i);
						String attName = element.getName().toLowerCase();
						if(!"4".equals(type) && "cname".equals(attName)){
							if(nameMap != null && nameMap.containsKey(att.getValue())){
								a_bean.set(attName,nameMap.get(att.getValue()));	
							}
						}else
							a_bean.set(attName,element.getValue());					
					}
					
					boolean isAdd=false;  //判断是否是需导入的数据
					if(importStandardIds==null)  //所有
					{
						isAdd=true;
					}
					else if("1".equals(type)) //gz_stand_pkg
					{
						String id=(String)a_bean.get("pkg_id");
						if(allowIDs.indexOf("#"+id+"#")!=-1)
							isAdd=true;
						
					}
					else if("2".equals(type)|| "3".equals(type)) //gz_stand
					{
						String id=(String)a_bean.get("id");
						
						if(allowIDs.indexOf("#"+id+"#")!=-1)
							isAdd=true;
					}
					
					if(isAdd)
						list.add(a_bean);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return list;
		}
		
		public static int executeEqualRecord(String cname,String chz,String salaryid,ContentDAO dao,String cstate,int opt,Connection con,ArrayList tempList)
		{
			int type=0;
			RowSet rs = null;
			try
			{
				//删掉重复的，第一次进入问题
				rs=dao.search("select nid,cstate,cname, chz from MidVariable where (UPPER(cname)='"+cname.toUpperCase()+"' or UPPER(chz)='"+chz.toUpperCase()+"') " +
						" and nflag=0 and templetID=0");
				while(rs.next())
				{
					String cstateValue=rs.getString("cstate");
					String tempcname = rs.getString("cname");
					String tempchz = rs.getString("chz");
					int nid=rs.getInt("nid");
					if(chz.equals(tempchz)){//chz重名
						if(cstate==null|| "".equals(cstate))//源共享临时变量查找库中所有(不导入)
						{
							type=1;
							if(!tempList.contains(chz)){
								tempList.add(chz);
								error1.append("  "+chz);
							}
						} else {
							if(cstateValue==null|| "".equals(cstateValue.trim()))//源私有，在库中共享的有chz重名
					    	{
								type=2;
								if(!tempList.contains(chz)){
									tempList.add(chz);
									error1.append("  "+chz);
								}
					    	}
						}
					}
					else {//cname重名，生成新的cname
						DefineTempVarBo tempvarbo = new DefineTempVarBo();
						int maxnid = tempvarbo.getid(con);
						//插入临时表之前先判断CName的值
						StringBuffer sqlquery = new StringBuffer("");
						String cname_new = "yk"+maxnid;
						sqlquery.append(" select count(*) from midvariable where cname='"+cname_new+"' ");
						try {
							rs = dao.search(sqlquery.toString());
							int num = 0;
							if(rs.next()){
								num = rs.getInt(1);
							}
							if(num > 0){
								cname_new = "ykC"+maxnid;
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						dao.update("update MidVariable set cname='"+cname_new+"' where nid="+nid);
						error1.append("  "+chz);
						error2.append("   源临时变量编号："+cname+", 新增临时变量编号："+cname_new);
						error2.append("\r\n");
						
					}
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally 
			{
				if(rs!=null)
				{
					try
					{
						rs.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			return type;
		}
		
		/**
		 * 插入  薪资公式  salaryformula 表数据
		 */
		public static void importSpecialTableData(Connection con,HashMap salaryTStandKeyMap,String salaryStandKey,String fileContext,String tableName,String keyName,String taxKeyName,HashMap taxKeyMap,String foreignKeyName,HashMap foreignKeyMap,String selectSalaryids,HashMap map)
		{
				try
				{
					ContentDAO dao=new ContentDAO(con);
					int maxID=0;
					RowSet rowSet=dao.search("select max("+keyName+") from "+tableName);
					if(rowSet.next())
						maxID=rowSet.getInt(1);
					
					Document doc = PubFunc.generateDom(fileContext);
					ArrayList dataList=getTableData(doc,null,"");
					
					HashMap amap = new HashMap();
					ArrayList list=new ArrayList();
					for(int i=0;i<dataList.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
						int id=Integer.parseInt((String)abean.get(keyName.toLowerCase()));
						++maxID;
						String salaryid=(String)abean.get("salaryid");
						if(selectSalaryids.indexOf("#"+salaryid+"#")==-1)
							continue;
					
						abean.set(keyName.toLowerCase(),String.valueOf(++maxID));

						String runflag=(String)abean.get("runflag");  //计算公式=0,执行标准=1, 执行税表=2,
						if("1".equals(runflag))
						{
							abean.set(salaryStandKey.toLowerCase(),(String)salaryTStandKeyMap.get((String)abean.get(salaryStandKey.toLowerCase())));
						}
						else if("2".equals(runflag))
						{
							abean.set(taxKeyName.toLowerCase(),(String)taxKeyMap.get((String)abean.get(taxKeyName.toLowerCase())));
						}
						String dd=(String)abean.get(foreignKeyName.toLowerCase());
						abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
						if("salaryformula".equalsIgnoreCase(tableName)&& "salaryid".equalsIgnoreCase(foreignKeyName))
						{
							if("2".equals(runflag))
							{
					    		String salarynewid=(String)foreignKeyMap.get(dd);
					    		int newid=maxID;
					    		int j=0;
					    		if(amap.get(salarynewid)!=null)
					    			j=Integer.parseInt(((String)amap.get(salarynewid)));
						    	SalaryCtrlParamBo salarybo = new SalaryCtrlParamBo(con,Integer.parseInt(salarynewid));
						    	salarybo.updateXML_IDValue(SalaryCtrlParamBo.YS_FIELDITEM, newid+"",j);
						    	salarybo.saveParameter();
						    	j++;
						    	amap.put(salarynewid, j+"");
							}
						}
						list.add(abean);
					}
					HashMap salary_column_type=getColumnTypeMap(con,tableName);
					importData(con,list,salary_column_type,tableName);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
		
		/**
		 * 导入审核公式  
		 * @param con
		 * @param fileContext
		 * @param tableName
		 * @param keyName
		 * @param selectSalaryids
		 */
		public static void importSpFormulaData(Connection con,String fileContext,String tableName,String keyName,String selectSalaryids,String foreignKeyName,HashMap foreignKeyMap)
		{
				try
				{
					ContentDAO dao=new ContentDAO(con);
					String maxID="";
					IDGenerator idg = new IDGenerator(2, con);
					int seq = 0;
					String sql = "select max(seq) seq from hrpchkformula ";
					RowSet rs = dao.search(sql);
					while(rs.next())
					{
						seq = rs.getInt("seq");
					}				
					Document doc = PubFunc.generateDom(fileContext);
					ArrayList dataList=getTableData(doc,null,"");
					
					ArrayList list=new ArrayList();
					for(int i=0;i<dataList.size();i++)
					{
						LazyDynaBean abean=(LazyDynaBean)dataList.get(i);
						String tabid=(String)abean.get("tabid");
						if(selectSalaryids.indexOf("#"+tabid+"#")==-1)
							continue;
						maxID = idg.getId("hrpchkformula.chkid");
						abean.set(keyName.toLowerCase(),maxID);
						seq++;
						abean.set("seq", seq+"");
						abean.set(foreignKeyName.toLowerCase(),(String)foreignKeyMap.get((String)abean.get(foreignKeyName.toLowerCase())));
						list.add(abean);
					}
					HashMap salary_column_type=getColumnTypeMap(con,tableName);
					importData(con,list,salary_column_type,tableName);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}
		
		static StringBuffer error1 = new StringBuffer("");
		static StringBuffer error2 = new StringBuffer("");
		public static StringBuffer getError1() {
			return error1;
		}

		public static void setError1(StringBuffer error1) {
			DownLoadXml.error1 = error1;
		}
		
		public static StringBuffer getError2() {
			return error2;
		}

		public static void setError2(StringBuffer error2) {
			DownLoadXml.error2 = error2;
		}

		public static HashMap<String, String> getNameMap() {
			return nameMap;
		}

		public static void setNameMap(HashMap<String, String> nameMap) {
			DownLoadXml.nameMap = nameMap;
		}
}
