/**
 * 
 */
package com.hjsj.hrms.businessobject.general.deci.browser;

import com.hjsj.hrms.businessobject.report.SQL_Util;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 12, 2006:10:47:15 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class MuchFieldAnalyse {
	
	private Connection conn;
	private String itemid;
	private String itemname;
	private String field_name;
	private String flag;
	private String codeitem_value;
	private String key_factors;
	private ArrayList factorIDList = new ArrayList(); //关键指标集合
	
	public MuchFieldAnalyse() {
	}

	/**
	 * 构造器 
	 * @param conn   DB连接
	 * @param itemid 指标ID
	 */
	public MuchFieldAnalyse(Connection conn ,String itemid) {
		this.conn = conn;
		this.itemid = itemid;
		try {
			this.initDB();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化数据
	 * @throws GeneralException
	 */
	public void initDB() throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);

		String sql = "select * from ds_key_item where itemid ='"+ this.itemid +"'";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
			    this.field_name = rs.getString("field_name"); //代码型指标名称
			    this.flag = rs.getString("flag");
			    this.codeitem_value = rs.getString("codeitem_value"); //代码行指标值（多个逗号分开）
			    this.key_factors = rs.getString("key_factors");//对应的关键指标
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.factorIDList = this.getTempList(this.key_factors);
	}
	
	/**
	 * 获得分析数据的按年/月变化标识
	 * @return
	 * @throws GeneralException
	 */
	public String getChangeFlag() throws GeneralException{
		String changeFlag = "0";
		StringBuffer cfs = new StringBuffer();
		for(int i=0; i< this.factorIDList.size();i++){
			HashMap m = this.getFactorInfo((String)this.factorIDList.get(i));
			String formula = (String) m.get("formula");
			ArrayList list = this.getFormulaList(formula);
			for(int j=0; j<list.size(); j++){
				String f = (String) list.get(j);
				String fieldSetID = this.getFieldSetID(f);//指标集ID
				String cf = this.getChangeFlag(fieldSetID);//按月变化标志
				cfs.append(cf);
			}
		}
		if(cfs.indexOf("1")!=-1){
			changeFlag="1";
		}else if(cfs.indexOf("2")!= -1){
			changeFlag="2";
		}else{
			changeFlag="0";
		}
		return changeFlag;
	}

	/**
	 * 获得一个关键指标的信息（公式/统计方法）
	 * @param factorid
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getFactorInfo(String factorid) throws GeneralException{
		HashMap map = new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		String sql = "select * from ds_key_factor where factorid='"+ factorid+"'";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
			    map.put("formula",rs.getString("formula"));
			    map.put("method",rs.getString("static_method"));
			    map.put("name",rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	
	/**
	 * 多指标分析
	 * @param dbFlag     库标志
	 * @param dbpre      人员库前缀
	 * @param changeFlag 按月变化标志
	 * @param startYear  起始年
	 * @param startMonth 起始月
	 * @param endYear    终止年
	 * @param endMonth   终止月
	 * @return
	 * @throws GeneralException
	 */
	public HashMap muchFieldAnalyse(String analyseType ,String dbFlag, String dbpre, String changeFlag, 
			String startYear, String startMonth, String endYear, String endMonth) throws GeneralException{
		HashMap map = new HashMap();
		
		//分析依据代码值列表
		ArrayList codeValueList = this.getTempList(this.codeitem_value);
		
		if("1".equals(analyseType)){//横向
			
			for(int i = 0 ; i<codeValueList.size(); i++){ //要分析的代码项
				//一个代码项
				String codeValue = (String) codeValueList.get(i);
				
				ArrayList list = new ArrayList();
				
				//System.out.println("***************"+codeValue+"代码项分析***************");
				for(int j = 0 ; j< this.factorIDList.size(); j++){//对应的关键指标
					
					//关键指标
					String factorID = (String) this.factorIDList.get(j);					
					//特定关键指标信息
					HashMap fmap = this.getFactorInfo(factorID);
					String formula = (String) fmap.get("formula"); //公式
					String method = (String) fmap.get("method"); //统计方法
					String name = (String)fmap.get("name");      //指标名称
					
					//获得取值的SQL语句
					String sql = this.getMuchFieldAnalyseSQL(dbFlag,dbpre,startYear,
							startMonth,endYear,endMonth,codeValue,formula,Integer.parseInt(method));	
					
					//System.out.println(sql);
					
					String value = this.getCodeItemValue(sql);	
					
					
					//System.out.println("值="+value);
					
					CommonData cd = new CommonData(value,name);
					list.add(cd);				
				}
				
				String codeItemDesc="";
				if("B0110".equalsIgnoreCase(this.field_name)){//单位
					codeItemDesc = this.getCodeItemDesc(codeValue,"UN");
				}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
					codeItemDesc = this.getCodeItemDesc(codeValue,"UM");
				}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
					codeItemDesc = this.getCodeItemDesc(codeValue,"@K");
				}else{
					codeItemDesc= this.getCodeItemDesc(codeValue);
				}
				
				map.put(codeItemDesc,list);
			}
		}else{//纵向
			/*System.out.println("**********************************");
			System.out.println("startYear="+startYear);
			System.out.println("startMonth=" + startMonth);
			System.out.println("endYear=" + endYear);
			System.out.println("endMonth="+endMonth);
			System.out.println("**********************************");*/
			
			int syear = Integer.parseInt(startYear);
			int eyear = Integer.parseInt(endYear);
			
			if("".equals(startMonth)&& "".equals(endMonth)){
				for(int k = syear ; k<=eyear ; k++){
					for(int j = 0 ; j< this.factorIDList.size(); j++){//对应的关键指标	
						ArrayList list = new ArrayList();
						for(int i = 0 ; i<codeValueList.size(); i++){ //要分析的代码项											
							String codeValue = (String) codeValueList.get(i);										
							//关键指标
							String factorID = (String) this.factorIDList.get(j);							
							//特定关键指标信息
							HashMap fmap = this.getFactorInfo(factorID);
							String formula = (String) fmap.get("formula"); //公式
							String method = (String) fmap.get("method"); //统计方法
							String name = (String)fmap.get("name");      //指标名称						
							//获得取值的SQL语句
							String sql = this.getMuchFieldAnalyseSQL(dbFlag,dbpre,startYear,
									startMonth,endYear,endMonth,codeValue,formula,Integer.parseInt(method));	
							
							//System.out.println(sql);
							
							String value = this.getCodeItemValue(sql);						
							String codeItemDesc="";
							if("B0110".equalsIgnoreCase(this.field_name)){//单位
								codeItemDesc = this.getCodeItemDesc(codeValue,"UN");
							}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
								codeItemDesc = this.getCodeItemDesc(codeValue,"UM");
							}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
								codeItemDesc = this.getCodeItemDesc(codeValue,"@K");
							}else{
								codeItemDesc= this.getCodeItemDesc(codeValue);
							}
							CommonData cd = new CommonData(value,codeItemDesc);							
							list.add(cd);
							map.put(k+"年"+name,list);
						}
					}
					
				}
	
			}else{
				int smonth = Integer.parseInt(startMonth);
				int emonth = Integer.parseInt(endMonth);
			
				if(syear == eyear){//同一年中月份比较
					for(int k = smonth ; k<=emonth ; k++){						
						String name = "";						
						for(int j = 0 ; j< this.factorIDList.size(); j++){//对应的关键指标								
							ArrayList list = new ArrayList();
							for(int i = 0 ; i<codeValueList.size(); i++){ //要分析的代码项												
								String codeValue = (String) codeValueList.get(i);						
								//关键指标
								String factorID = (String) this.factorIDList.get(j);							
								//特定关键指标信息
								HashMap fmap = this.getFactorInfo(factorID);
								String formula = (String) fmap.get("formula"); //公式
								String method = (String) fmap.get("method"); //统计方法
								name = (String)fmap.get("name");      //指标名称	
		
								//获得取值的SQL语句
								String sql = this.getMuchFieldAnalyseSQL(dbFlag,dbpre,startYear,
										startMonth,endYear,endMonth,codeValue,formula,Integer.parseInt(method));							
								String value = this.getCodeItemValue(sql);						
								String codeItemDesc="";
								
								if("B0110".equalsIgnoreCase(this.field_name)){//单位
									codeItemDesc = this.getCodeItemDesc(codeValue,"UN");
								}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
									codeItemDesc = this.getCodeItemDesc(codeValue,"UM");
								}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
									codeItemDesc = this.getCodeItemDesc(codeValue,"@K");
								}else{
									codeItemDesc= this.getCodeItemDesc(codeValue);
								}
								/*
								System.out.println("value1=" + value);
								System.out.println("name=" + name);
								System.out.println("codeitemDesc=" + codeItemDesc);
								*/
								CommonData cd = new CommonData(value,codeItemDesc);

								list.add(cd);
								map.put(syear+"年"+k+"月"+name,list);
							}
						}
					}
				}else{
					for(int k=smonth; k<=12; k++){
						for(int j = 0 ; j< this.factorIDList.size(); j++){//对应的关键指标		
							ArrayList list = new ArrayList();
							for(int i = 0 ; i<codeValueList.size(); i++){ //要分析的代码项												
								String codeValue = (String) codeValueList.get(i);									
								//关键指标
								String factorID = (String) this.factorIDList.get(j);							
								//特定关键指标信息
								HashMap fmap = this.getFactorInfo(factorID);
								String formula = (String) fmap.get("formula"); //公式
								String method = (String) fmap.get("method"); //统计方法
								String name = (String)fmap.get("name");      //指标名称						
								//获得取值的SQL语句
								String sql = this.getMuchFieldAnalyseSQL(dbFlag,dbpre,startYear,
										startMonth,endYear,endMonth,codeValue,formula,Integer.parseInt(method));							
								String value = this.getCodeItemValue(sql);						
								String codeItemDesc="";
								if("B0110".equalsIgnoreCase(this.field_name)){//单位
									codeItemDesc = this.getCodeItemDesc(codeValue,"UN");
								}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
									codeItemDesc = this.getCodeItemDesc(codeValue,"UM");
								}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
									codeItemDesc = this.getCodeItemDesc(codeValue,"@K");
								}else{
									codeItemDesc= this.getCodeItemDesc(codeValue);
								}
								
								CommonData cd = new CommonData(value,codeItemDesc);								
								list.add(cd);
								map.put(syear+"年"+k+"月"+name,list);
							}
						}
						
					}

					for(int k = syear ; k<= eyear; k++){
						for(int m = 1; m <=12 ; m++){
							for(int j = 0 ; j< this.factorIDList.size(); j++){//对应的关键指标		
								ArrayList list = new ArrayList();
								for(int i = 0 ; i<codeValueList.size(); i++){ //要分析的代码项														
									String codeValue = (String) codeValueList.get(i);									
									//关键指标
									String factorID = (String) this.factorIDList.get(j);							
									//特定关键指标信息
									HashMap fmap = this.getFactorInfo(factorID);
									String formula = (String) fmap.get("formula"); //公式
									String method = (String) fmap.get("method"); //统计方法
									String name = (String)fmap.get("name");      //指标名称						
									//获得取值的SQL语句
									String sql = this.getMuchFieldAnalyseSQL(dbFlag,dbpre,startYear,
											startMonth,endYear,endMonth,codeValue,formula,Integer.parseInt(method));							
									String value = this.getCodeItemValue(sql);						
									String codeItemDesc="";
									if("B0110".equalsIgnoreCase(this.field_name)){//单位
										codeItemDesc = this.getCodeItemDesc(codeValue,"UN");
									}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
										codeItemDesc = this.getCodeItemDesc(codeValue,"UM");
									}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
										codeItemDesc = this.getCodeItemDesc(codeValue,"@K");
									}else{
										codeItemDesc= this.getCodeItemDesc(codeValue);
									}
									CommonData cd = new CommonData(value,codeItemDesc);								
									list.add(cd);
									map.put(k+"年"+m+"月"+name,list);
								}
							}
							
						}
					}
					
					for(int k=1; k<= emonth; k++){
						for(int j = 0 ; j< this.factorIDList.size(); j++){//对应的关键指标	
							ArrayList list = new ArrayList();
							for(int i = 0 ; i<codeValueList.size(); i++){ //要分析的代码项											
								String codeValue = (String) codeValueList.get(i);						
														
								//关键指标
								String factorID = (String) this.factorIDList.get(j);							
								//特定关键指标信息
								HashMap fmap = this.getFactorInfo(factorID);
								String formula = (String) fmap.get("formula"); //公式
								String method = (String) fmap.get("method"); //统计方法
								String name = (String)fmap.get("name");      //指标名称						
								//获得取值的SQL语句
								String sql = this.getMuchFieldAnalyseSQL(dbFlag,dbpre,startYear,
										startMonth,endYear,endMonth,codeValue,formula,Integer.parseInt(method));							
								String value = this.getCodeItemValue(sql);						
								String codeItemDesc="";
								if("B0110".equalsIgnoreCase(this.field_name)){//单位
									codeItemDesc = this.getCodeItemDesc(codeValue,"UN");
								}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
									codeItemDesc = this.getCodeItemDesc(codeValue,"UM");
								}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
									codeItemDesc = this.getCodeItemDesc(codeValue,"@K");
								}else{
									codeItemDesc= this.getCodeItemDesc(codeValue);
								}
								CommonData cd = new CommonData(value,codeItemDesc);								
								list.add(cd);
								map.put(eyear+"年"+k+"月"+name,list);
							}
						}
						
					}
				}
			}
			
		}
		return map;
	}
	
	
	/**
	 * 获取分析SQL语句
	 * @param dbFlag  库标志
	 * @param dbpre   人员库标志
	 * @param startYear  起始年
	 * @param startMonth 起始月
	 * @param endYear    终止年
	 * @param endMonth   终止月
	 * @param codeValues 分析的代码值
	 * @param formula    公式
	 * @param method     统计方法
	 * @return
	 * @throws GeneralException
	 */
	public String getMuchFieldAnalyseSQL(String dbFlag ,String dbpre ,
			String startYear ,String startMonth,String endYear ,
			String endMonth,String codeValues,String formula,int method) throws GeneralException{
		StringBuffer sql = new StringBuffer();
		
		//关联字符串：人员/单位/职位
		String jionStr = "";
		if("A".equalsIgnoreCase(dbFlag)){
			jionStr ="A0100";
		}else if("B".equalsIgnoreCase(dbFlag)){
			jionStr = "B0110";
		}else if("K".equalsIgnoreCase(dbFlag)){
			jionStr = "E01A1";
		}
		
		String sqlMethod="";  //统计方法
		if(method == 1){
			sqlMethod="SUM";
		}else if(method == 2){
			sqlMethod="MAX";
		}else if(method == 3){
			sqlMethod ="MIN";
		}else if(method == 4){
			sqlMethod="AVG";
		}
		
		//特定关键指标公式列表
		ArrayList formulaList = this.getFormulaList(formula);//指标列表
		
		if(formulaList.size()==2){//按比例
			String firstItemID = (String)formulaList.get(0);//除数
			String secondItemID= (String)formulaList.get(1);//被除数

			sql.append("select ");
			//除零
			SQL_Util su = new SQL_Util();
			String tsql = su.sqlSwitch(Sql_switcher.charToFloat("e.r")+"/"+Sql_switcher.charToFloat("e.t"));
			//String tsql = su.sqlSwitch("e.r/e.t");
			String temp = sqlMethod+"("+tsql+")";
			sql.append(Sql_switcher.isnull(temp ,"0"));
			sql.append(" value from (");
			
			sql.append(" select ");
			sql.append(Sql_switcher.isnull("a."+firstItemID,"0"));
			sql.append(" r ,a."+jionStr+",");
			sql.append(Sql_switcher.isnull("b."+secondItemID,"0"));
			sql.append(" t from (");
			
			//除数
			sql.append(this.getFieldItemSql(firstItemID,dbpre,dbFlag,startYear,startMonth, endYear ,endMonth,"2"));
			sql.append(" " +" )a inner join (");
			
			//被除数
			sql.append(this.getFieldItemSql(secondItemID,dbpre,dbFlag,startYear,startMonth, endYear ,endMonth,"2"));
			sql.append(" )b on a."+jionStr+" = b."+jionStr+" inner join (");
			
			//System.out.println(this.field_name);
			//代码值限制
			sql.append(" select "+jionStr+" from ");
			if("A".equalsIgnoreCase(dbFlag)){
				if("b0110".equalsIgnoreCase(this.field_name)|| "e01a1".equalsIgnoreCase(this.field_name)){
					sql.append("usra01");
				}else{
					sql.append(dbpre);
					sql.append(this.getFieldSetID(this.field_name));
				}
			}else if("B".equalsIgnoreCase(dbFlag)){
				if("b0110".equalsIgnoreCase(this.field_name)){
					sql.append("b01");
				}else{
					if("E0122".equalsIgnoreCase(this.field_name)|| "E01A1".equalsIgnoreCase(this.field_name)){
						sql.append(" Usr");
						sql.append(this.getFieldSetID(this.field_name));
					}else{
						sql.append(dbpre);
						sql.append(this.getFieldSetID(this.field_name));
					}					
				}
			}else if("K".equalsIgnoreCase(dbFlag)){
				if("e01a1".equalsIgnoreCase(this.field_name)){
					sql.append("K01");
				}else{
					sql.append(dbpre);
					sql.append(this.getFieldSetID(this.field_name));
				}
			}
			sql.append(" where ");
			sql.append(this.field_name);		
			sql.append(" like'");
			sql.append(codeValues);
			sql.append("%' ");			
			sql.append(" )d on a."+jionStr+" = d."+jionStr+" )e");
	
		}else{//不按比例
			String itemID = formula; //公式		

			sql.append("select ");			
			String tsql = Sql_switcher.isnull("e.r","0");
			String temp = sqlMethod+"("+tsql+")";
			sql.append(Sql_switcher.isnull(temp ,"0"));			
			sql.append(" value from (");
			
			//公式条件限制
			sql.append(this.getFieldItemSql(itemID,dbpre,dbFlag,startYear,startMonth, endYear ,endMonth,"1"));
			sql.append(" a inner join ( ");			

			//代码值限制
			sql.append(" select "+jionStr+" from ");
			if("A".equalsIgnoreCase(dbFlag)){
				if("b0110".equalsIgnoreCase(this.field_name)|| "e01a1".equalsIgnoreCase(this.field_name)){
					sql.append("usra01");
				}else{
					sql.append(dbpre);
					sql.append(this.getFieldSetID(this.field_name));
				}
			}else if("B".equalsIgnoreCase(dbFlag)){
				if("b0110".equalsIgnoreCase(this.field_name)){
					sql.append("b01");
				}else{
					if("E0122".equalsIgnoreCase(this.field_name)|| "E01A1".equalsIgnoreCase(this.field_name)){
						sql.append(" Usr");
						sql.append(this.getFieldSetID(this.field_name));
					}else{
						sql.append(dbpre);
						sql.append(this.getFieldSetID(this.field_name));
					}		
				}
			}else if("K".equalsIgnoreCase(dbFlag)){
				if("e01a1".equalsIgnoreCase(this.field_name)){
					sql.append("K01");
				}else{
					sql.append(dbpre);
					sql.append(this.getFieldSetID(this.field_name));
				}
			}
			sql.append(" where ");
			sql.append(this.field_name);
			sql.append(" like'");
			sql.append(codeValues);
			sql.append("%' ");			
			sql.append(" ) c on a."+jionStr+"=c."+jionStr+" )e");
			
		}

		return sql.toString();
		
	}
	
	
	/**
	 * 获取一个代码型指标的SQL片段
	 * @param fieldItem
	 * @param dbpre
	 * @param dbFlag
	 * @param startYear
	 * @param startMonth
	 * @param endYear
	 * @param endMonth
	 * @return
	 * @throws GeneralException
	 */
	public String getFieldItemSql(String fieldItem ,String dbpre,String dbFlag ,String startYear,
			String startMonth,String endYear ,String endMonth ,String flag) throws GeneralException{
		StringBuffer itemSql = new StringBuffer();
		
		String fieldSetID = this.getFieldSetID(fieldItem);//指标集ID
		String changeFlag = this.getChangeFlag(fieldSetID);//按月变化标志
		
		String tempitem = fieldItem;
		if(this.isDateType(fieldItem)){//如果指标时日期类型
			tempitem = "("+Sql_switcher.diffDays(Sql_switcher.sqlNow(),fieldItem)+")/365.00000000";
		}
		
		//关联字符串
		String jionStr = "";
		if("A".equalsIgnoreCase(dbFlag)){
			jionStr ="A0100";
		}else if("B".equalsIgnoreCase(dbFlag)){
			jionStr = "B0110";
		}else if("K".equalsIgnoreCase(dbFlag)){
			jionStr = "E01A1";
		}

		if("0".equals(changeFlag)){//不按月变化
	
			if("1".equals(flag)){//单个指标
				itemSql.append(" select ");
				itemSql.append(Sql_switcher.isnull("a."+fieldItem,"0"));
				itemSql.append(" r ,a."+jionStr+" from ( ");
			}

			if(fieldSetID.equalsIgnoreCase(dbFlag+"01")){//如果是A01/B01/K01
				itemSql.append(" select ");
				itemSql.append(Sql_switcher.isnull(tempitem,"0"));
				itemSql.append(" "+fieldItem);
				itemSql.append(" , "+jionStr+" from ");
				itemSql.append(dbpre+fieldSetID);				
			}else{
				itemSql.append(" select ");
				itemSql.append(Sql_switcher.isnull(tempitem,"0"));
				itemSql.append(" "+fieldItem);
				itemSql.append(" , "+jionStr+" from ");
				itemSql.append(dbpre+fieldSetID);
				itemSql.append(" d where d.i9999=(select max(i9999) from ");
				itemSql.append(dbpre+fieldSetID);
				itemSql.append(" e where d."+jionStr+" = e."+jionStr+" )");
			}
			if("1".equals(flag)){
				itemSql.append(" )");
			}			
		}else{//按月变化

			if("1".equals(flag)){
				itemSql.append(" select ");
				itemSql.append(Sql_switcher.isnull("a."+fieldItem,"0"));
				itemSql.append(" r ,a."+jionStr+" from ( ");
			}	
			
			
			itemSql.append("select ");
			String temp = Sql_switcher.isnull(tempitem,"0");
			String temp1 =  Sql_switcher.isnull("sum("+temp+")","0");
			itemSql.append(temp1+" ");
			itemSql.append(fieldItem);
			itemSql.append(" ,"+jionStr+" from ");
			itemSql.append(dbpre+fieldSetID);
			itemSql.append(" where ");
			
			//年月限制
			itemSql.append(Sql_switcher.year(fieldSetID+"Z0"));
			itemSql.append("='");
			itemSql.append(startYear);
			itemSql.append("'");
			
			if("".equals(startMonth)){
			}else{
				itemSql.append(" and ");
				itemSql.append(Sql_switcher.month(fieldSetID+"Z0"));
				itemSql.append("='");
				itemSql.append(startMonth);
				itemSql.append("'");
			}

			itemSql.append("group by "+jionStr+" ,");
			itemSql.append(Sql_switcher.year(fieldSetID+"Z0"));
			itemSql.append(",");
			itemSql.append(Sql_switcher.month(fieldSetID+"Z0"));
			
			if("1".equals(flag)){
				itemSql.append(" )");
			}	
		}
		return itemSql.toString();
	}
	
	
	
	/**
	 * 获取指标集按月变化标始
	 * @param fieldsetid
	 * @return
	 * @throws GeneralException
	 */
	public String getChangeFlag(String fieldsetid) throws GeneralException{
		String changeFalg = "";
		ContentDAO dao=new ContentDAO(this.conn);
		String sql = "select changeflag from fieldset where fieldsetid ='"+fieldsetid+"'";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				changeFalg = rs.getString("changeflag");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		
		return changeFalg;
	}
	
	
	
	
	
	
	
	/**
	 * 获取指标集ID
	 * @param fieldItemID 指标项ID
	 * @return
	 * @throws GeneralException 
	 */
	public String getFieldSetID(String fieldItemID) throws GeneralException{
		String fieldSetID = "";
		ContentDAO dao=new ContentDAO(this.conn);
		String sql = "select fieldsetid from fielditem where itemid ='"+fieldItemID+"'";
		
		//System.out.println(sql);

		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
			  fieldSetID = rs.getString("fieldsetid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		
		return fieldSetID;
		
	}
	
	/**
	 * 获得公式集合
	 * @param formula 公式字符串 a0425/a0425
	 * @return
	 */
	public ArrayList getFormulaList(String formula){
		ArrayList list = new ArrayList();
		int n = formula.indexOf("/");
		if(n == -1){
			list.add(formula);
		}else{
			list.add(formula.substring(0,n));
			list.add(formula.substring(n+1,formula.length()));
		}
		
		return list;
	}
	
	/**
	 * 获得特殊指标描述信息(单位/职位/部门)
	 * @param codeItemID
	 * @param flag
	 * @return
	 * @throws GeneralException
	 */
	public String getCodeItemDesc(String codeItemID ,String flag) throws GeneralException{
		String fieldItemDesc = "";
		ContentDAO dao=new ContentDAO(this.conn);
		
		String sql1 ="select codeitemdesc from organization where codeitemid " +
				"=(select parentid from organization where  codeitemid='"+codeItemID+"')";
		
		/*System.out.println("********************************");
		System.out.println(sql);
		System.out.println("*********************************");*/
		

		//System.out.println(sql1);
		
		String temp1 ="";
		RowSet rs = null;
		try {
			rs = dao.search(sql1);
			if(rs.next()){
				temp1 = rs.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		//System.out.println("temp1="+temp1);
	
		
		String sql = "select codeitemdesc from organization where codesetid='"+flag+"' and codeitemid='"+codeItemID+"' ";
		//System.out.println(sql);
		String temp = "";
		RowSet rs1 = null;
		try {
			rs1 = dao.search(sql);
			if(rs1.next()){
				temp = rs1.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		//System.out.println("temp="+temp);
		
		return temp1+temp;
		
		/*String fieldItemDesc = "";
		ContentDAO dao=new ContentDAO(this.conn);
		String sql = "select codeitemdesc from organization where codesetid='"+flag+"' and codeitemid='"+codeItemID+"' ";
		
		System.out.println("********************************");
		System.out.println(sql);
		System.out.println("*********************************");
		
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				fieldItemDesc = rs.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		
		return fieldItemDesc;*/
	}
	/**
	 * 获取代码项的描述信息
	 * @param codeItemID
	 * @return
	 * @throws GeneralException
	 */
	public String getCodeItemDesc(String codeItemID) throws GeneralException{
		String fieldItemDesc = "";
		ContentDAO dao=new ContentDAO(this.conn);
		
		String codesetid="";
		String sql1="select codesetid  from fielditem  where itemid ='"+this.field_name +"'";

		RowSet rs = null;
		try {
			rs = dao.search(sql1);
			if(rs.next()){
				codesetid = rs.getString("codesetid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
		
		if("@k".equalsIgnoreCase(codesetid)){
			fieldItemDesc = this.getCodeItemDesc(codeItemID,"@K");
		}else if("UM".equalsIgnoreCase(codesetid)){
			fieldItemDesc = this.getCodeItemDesc(codeItemID,"UM");
		}else if("UN".equalsIgnoreCase(codesetid)){
			fieldItemDesc = this.getCodeItemDesc(codeItemID,"UN");
		}else{
			String sql = "select codeitemdesc from codeitem where codeitemid ='"+codeItemID+"' and codesetid= (";
			sql+="select codesetid  from fielditem  where itemid ='"+this.field_name +"')";

			RowSet rs1 = null;
			try {
				rs1 = dao.search(sql);
				if(rs1.next()){
					fieldItemDesc = rs1.getString("codeitemdesc");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}		
			
		}
		return fieldItemDesc;
	}
	
	/**
	 * 获取分析指标代码项的值
	 * @param sql
	 * @return
	 * @throws GeneralException
	 */
	public String getCodeItemValue(String sql) throws GeneralException{
		String fieldItemValue = "";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				fieldItemValue = rs.getString("value");
				fieldItemValue = this.formatValue(fieldItemValue,2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		
		return fieldItemValue;
	}
	/**
	 * 获得临时集合
	 * @param temp 逗号分割的字符串
	 * @return
	 */
	public ArrayList getTempList(String temp){
		ArrayList list = new ArrayList();
		temp = this.getTempString(temp);
		String [] tt = temp.split(",");
		for(int i = 0 ; i< tt.length ; i++){
			list.add(tt[i]);
		}
		return list;
	}
	
	
	/**
	 * 去掉字符串末尾的逗号
	 * @param temp
	 * @return
	 */
	public String getTempString(String temp){
		if(temp.charAt(temp.length()-1)==','){
			temp= temp.substring(0,temp.length()-1);
		}
		return temp;
	}
	
	/**
	 * 判断一个指标是否时日期类型
	 * @param fielditemid
	 * @return
	 * @throws GeneralException 
	 */
	public boolean isDateType(String fielditemid) throws GeneralException{
		boolean b = false;
		String sql = "select itemtype  from fielditem where itemid='"+fielditemid+"'";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				String temp = rs.getString("itemtype");
				if("D".equalsIgnoreCase(temp)){
					b = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		return b;
	}
	
	/**
	 * 获取规范的表达式的值,自动四舍五入
	 * @param exprValue 表达式值
	 * @param flag      小数位
	 * @return  规范后的值
	 */
	public String formatValue(String exprValue , int flag){
		
		StringBuffer sb = new StringBuffer();	
		if(flag == 0){
			sb.append("####");
		}else{
			sb.append("####.");
			for(int i = 0 ; i < flag ; i++){
				sb.append("0");
			}
		}	
		DecimalFormat df = new DecimalFormat(sb.toString());
		String dstr = df.format(Double.parseDouble(exprValue));
		return dstr;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
