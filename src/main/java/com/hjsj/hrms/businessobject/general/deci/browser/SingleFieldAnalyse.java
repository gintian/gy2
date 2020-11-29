
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
 * <p>Description:单指标分析</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 4, 2006:10:18:01 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class SingleFieldAnalyse {

	private Connection conn;	//DB连接
	private String factorid;	//统计指标ID
	private String field_name="";	//代码指标名称
	private String codeitem_value=""; //代码指标值
	private int static_method = 0;    //统计方法
	private String formula = "";      //公式
	private String name;
	private String sqlMethod; //SQL统计函数语句
	private String avgValue;
	private float standard_value = 0;
	private float control_value= 0;
	
	/**
	 * 构造器
	 */
	public SingleFieldAnalyse() {	
	}
	
	/**
	 * 构造器
	 * @param conn
	 */
	public SingleFieldAnalyse(Connection conn , String factorid){	
		this.conn = conn;
		this.factorid = factorid;
		try {
			this.initDB(factorid);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化分析数据
	 * @param factorid
	 * @throws GeneralException
	 */
	public void initDB(String factorid) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		if(factorid == null || "".equals(factorid)){
		}else{
			String sql = "select * from ds_key_factor where factorid ='"+ factorid +"'";
			RowSet rs = null;
			try {
				rs = dao.search(sql);
				if(rs.next()){
				    this.field_name = rs.getString("field_name"); //代码型指标名称
				    this.codeitem_value = rs.getString("codeitem_value"); //代码行指标值（多个逗号分开）
				    this.static_method = rs.getInt("static_method"); //统计方法
				    this.formula = rs.getString("formula"); //计算公式
				    this.name = rs.getString("name");
				    this.standard_value=rs.getFloat("standard_value");
					this.control_value=rs.getFloat("control_value");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			
			if(this.static_method == 1){
				this.sqlMethod="SUM";
			}else if(this.static_method == 2){
				this.sqlMethod="MAX";
			}else if(this.static_method == 3){
				this.sqlMethod ="MIN";
			}else if(this.static_method == 4){
				this.sqlMethod="AVG";
			}
			/*
		    System.out.println("***********代码型指标分析参数信息**************");
		    System.out.println("要分析代码型指标名称="+this.field_name);
		    System.out.println("代码型指标的代码值(分析依据)="+this.codeitem_value);
		    System.out.println("统计方法="+this.static_method);
		    System.out.println("计算公式="+this.formula);
		    System.out.println("*************************");
		    */
		}
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
		String fieldSetID = this.getFieldSetID(fieldItem);
		String changeFlag = this.getChangeFlag(fieldSetID);
		
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
	 * 获得公式SQL语句
	 * @param dbFlag    库标识
	 * @param dbpre     库前缀
	 * @param startYear  起始年
	 * @param startMonth 起始月
	 * @param endYear    结束年
	 * @param endMonth   结束月 
	 * @param codeValues 代码项
	 * @param b 是否是求平均值
	 * @return
	 * @throws GeneralException 
	 */
	public String getFormulaSql(String dbFlag ,String dbpre ,
			String startYear ,String startMonth,String endYear , 
			String endMonth,String codeValues,boolean b) throws GeneralException{
		
		StringBuffer sql = new StringBuffer();
		
		//关联字符串
		String jionStr = "";
		if("A".equalsIgnoreCase(dbFlag)){//人员
			jionStr ="A0100";
		}else if("B".equalsIgnoreCase(dbFlag)){//单位
			jionStr = "B0110";
		}else if("K".equalsIgnoreCase(dbFlag)){//职位
			jionStr = "E01A1";
		}
		
		ArrayList formulaList = this.getFormulaList(this.formula);//指标列表
		if(formulaList.size()==2){//按比例
			String firstItemID = (String)formulaList.get(0);//除数
			String secondItemID= (String)formulaList.get(1);//被除数
			
			//if(dbFlag.equalsIgnoreCase("A")){//人员
			sql.append("select ");
			//除零
			SQL_Util su = new SQL_Util();
			String tsql = su.sqlSwitch(Sql_switcher.charToFloat("e.r")+"/"+Sql_switcher.charToFloat("e.t"));
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
			sql.append(" " +" )a inner join (");//
			//被除数
			sql.append(this.getFieldItemSql(secondItemID,dbpre,dbFlag,startYear,startMonth, endYear ,endMonth,"2"));
			sql.append(" )b on a."+jionStr+" = b."+jionStr+" inner join (");
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
				if("B0110".equalsIgnoreCase(this.field_name)){
					sql.append("b01");
				}else{
					if("E0122".equalsIgnoreCase(this.field_name)){
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
			
			
			if(b){
				String [] tt = this.codeitem_value.split(",");
				for(int t = 0; t<tt.length; t++){
					sql.append(this.field_name);
					sql.append(" like'");
					sql.append(tt[t]);
					sql.append("%' or ");
					
					/*sql.append(" = '");
					sql.append(tt[t]);
					sql.append("' or ");*/
				}
				sql.delete(sql.length()-3,sql.length());

			}else{
				sql.append(this.field_name);
				
				sql.append(" like'");
				sql.append(codeValues);
				sql.append("%' ");	
				
				/*sql.append(" = '");
				sql.append(codeValues);
				sql.append("'");*/

			}
			
			sql.append(" )d on a."+jionStr+" = d."+jionStr+" )e");

	
		}else{//不按比例
			String itemID = this.formula; //公式		
			
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
				if("B0110".equalsIgnoreCase(this.field_name)|| "e01a1".equalsIgnoreCase(this.field_name)
						|| "E0122".equalsIgnoreCase(this.field_name)){//单位 职位 部门
					sql.append("usra01");
				}else{
					sql.append(dbpre);
					sql.append(this.getFieldSetID(this.field_name));
				}
			}else if("B".equalsIgnoreCase(dbFlag)){
				if("B0110".equalsIgnoreCase(this.field_name)){
					sql.append("b01");
				}else{
					if("E0122".equalsIgnoreCase(this.field_name)){
						sql.append(" Usr");
						sql.append(this.getFieldSetID(this.field_name));
					}else{
						sql.append(dbpre);
						sql.append(this.getFieldSetID(this.field_name));
					}
				}
			}else if("K".equalsIgnoreCase(dbFlag)){
				if("E01A1".equalsIgnoreCase(this.field_name)){
					sql.append("K01");
				}else{
					sql.append(dbpre);
					sql.append(this.getFieldSetID(this.field_name));
				}
			}
			sql.append(" where ");

			if(b){//是否是求平均值
				String [] tt = this.codeitem_value.split(",");
				for(int t = 0; t<tt.length; t++){
					sql.append(this.field_name);
					
					sql.append(" like'");
					sql.append(tt[t]);
					sql.append("%' or ");
					
					/*sql.append(" = '");
					sql.append(tt[t]);
					sql.append("' or ");*/
				}
				sql.delete(sql.length()-3,sql.length());
			}else{
				sql.append(this.field_name);
				
				sql.append(" like'");
				sql.append(codeValues);
				sql.append("%' ");	
				
				/*sql.append(" = '");
				sql.append(codeValues);
				sql.append("'");*/
			}
			
			sql.append(" ) c on a."+jionStr+"=c."+jionStr+" )e");
		}
		return sql.toString();
	}
	
	
	/**
	 * 单个指标横向分析(多个单位(或统计指标代码项)在某时间点对某指标的对比分析)
	 * 单个指标纵向分析(同一个或多个单位(或统计项目)，在一定时间范围对某一指标的对比分析图。)
	 * @param analyseType 分析类型 1 横向 2 纵向
	 * @param dbFlag      库标识
	 * @param dbpre       人员库前缀
	 * @param startYear   起始年
	 * @param startMonth  起始月
	 * @param endYear     终止年
	 * @param endMonth    终止月
	 * @return
	 * @throws GeneralException
	*/
	public Object singleFieldAnalyse(String analyseType,String dbFlag ,String dbpre ,
			String startYear,String startMonth,String endYear,String endMonth) throws GeneralException{
		Object obj = null;	
		
		ArrayList codeItemList = this.getTempList(this.codeitem_value);//代码项信息(分析依据)	
		
		if("1".equals(analyseType)){//横向
			//System.out.println("***************横向分析**********************");
			ArrayList list = new ArrayList();
			for(int i=0 ;i<codeItemList.size();i++){
				
				String codeItem = (String)codeItemList.get(i);
				
				//获得SQL
				String sql = this.getFormulaSql(dbFlag,dbpre,startYear,
						startMonth,endYear,endMonth,codeItem,false);
				//执行SQL
				String codeItemValue = this.getCodeItemValue(sql.toString());
				
				//代码项描述信息
				String codeItemDesc="";
				if("B0110".equalsIgnoreCase(this.field_name)){//单位
					codeItemDesc = this.getCodeItemDesc(codeItem,"UN");
				}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
					codeItemDesc = this.getCodeItemDesc(codeItem,"UM");
				}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
					codeItemDesc = this.getCodeItemDesc(codeItem,"@K");
				}else{
					codeItemDesc= this.getCodeItemDesc(codeItem);
				}
				
				/*System.out.println("*********"+codeItem+"对应的数据**********");
				System.out.println(sql);
				System.out.println("值=" + codeItemValue);
				System.out.println("代码项描述信息=" + codeItemDesc);
				System.out.println("*************************************");*/
				
				CommonData cd = new CommonData(codeItemValue,codeItemDesc);
				//CommonData cd = new CommonData("1000000000011",codeItemDesc);//13位
				list.add(cd);
			}
			
			if(this.static_method == 4){
				String sql = this.getFormulaSql(dbFlag,dbpre,startYear,
						startMonth,endYear,endMonth,this.getTempString(this.codeitem_value),true);
				
				this.avgValue=this.getCodeItemValue(sql);

				//CommonData cd = new CommonData(avgValue,"平均值");
				
				/*System.out.println("***********数据平均值***********");
				System.out.println(sql);
				System.out.println("值="+avgValue);
				System.out.println("***********************************");*/
				
				//list.add(cd);
			}
			obj = list;
		}else{//纵向
			
			//System.out.println("***************纵向分析**********************");
			HashMap map = new HashMap();
			
			/*System.out.println("startYear="+startYear);
			System.out.println("startMonth=" + startMonth);
			System.out.println("endYear=" + endYear);
			System.out.println("endMonth="+endMonth);*/
			
			if("".equals(startMonth) && "".equals(endMonth)){//年区间
				for(int i=Integer.parseInt(startYear); i <= Integer.parseInt(endYear); i++){	
					ArrayList list = new ArrayList();
					
					for(int j=0 ;j<codeItemList.size();j++){//分析依据
						
						String codeValues = (String)codeItemList.get(j);
						
						String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(i),
								"",String.valueOf(i),"",codeValues ,false);

						String codeItemValue = this.getCodeItemValue(sql.toString());
						
						//代码项描述信息
						String codeItemDesc="";
						if("B0110".equalsIgnoreCase(this.field_name)){//单位
							codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
						}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
							codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
						}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
							codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
						}else{
							codeItemDesc= this.getCodeItemDesc(codeValues);
						}
						
						
						/*System.out.println("*************"+codeValues+"分析*****************");
						System.out.println(sql);
						System.out.println("分析因子描述="+codeItemDesc);
						System.out.println("分析因子值="+codeItemValue);
						System.out.println();*/
						
						CommonData cd = new CommonData(codeItemValue,codeItemDesc);
						list.add(cd);
						
					}	
					map.put(String.valueOf(i)+"年",list);
				}
				if(this.static_method == 4){				
					for(int i=Integer.parseInt(startYear); i < Integer.parseInt(endYear); i++){							
						for(int j=0 ;j<codeItemList.size();j++){//分析依据	
							String codeValues = (String)codeItemList.get(j);
							//ArrayList list = new ArrayList();
							String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(i),
									"","","",this.getTempString(this.codeitem_value),true);
							String avgValue = this.getCodeItemValue(sql);
							this.avgValue = avgValue;
							//代码项描述信息
							String codeItemDesc="";
							if("B0110".equalsIgnoreCase(this.field_name)){//单位
								codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
							}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
								codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
							}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
								codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
							}else{
								codeItemDesc= this.getCodeItemDesc(codeValues);
							}

							CommonData cd = new CommonData(avgValue,codeItemDesc);
							
							/*System.out.println("***********数据平均值***********");
							System.out.println(sql);
							System.out.println("值="+avgValue);
							System.out.println("***********************************");*/
							
							//list.add(cd);
							//map.put("平均值",list);
						}
					}
				}
			
				
			}else{//月区间
				int syear = Integer.parseInt(startYear);
				int eyear = Integer.parseInt(endYear);
				int smonth = Integer.parseInt(startMonth);
				int emonth = Integer.parseInt(endMonth);

				if(syear == eyear){
					for(int i=smonth;i<=emonth;i++){
						ArrayList list = new ArrayList();
						for(int k=0 ;k<codeItemList.size();k++){							
							String codeValues = (String)codeItemList.get(k);
							String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(syear),
									String.valueOf(i),"","",codeValues ,false);					
							String codeItemValue = this.getCodeItemValue(sql.toString());
							//代码项描述信息
							String codeItemDesc="";
							if("B0110".equalsIgnoreCase(this.field_name)){//单位
								codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
							}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
								codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
							}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
								codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
							}else{
								codeItemDesc= this.getCodeItemDesc(codeValues);
							}

							CommonData cd = new CommonData(codeItemValue,codeItemDesc);
							list.add(cd);
						}
						map.put(syear+"年"+i+"月",list);
					}
					
					if(this.static_method == 4){
											
						for(int i=smonth;i<=emonth;i++){	
						//	ArrayList list = new ArrayList();	
							for(int k=0 ;k<codeItemList.size();k++){
								String codeValues = (String)codeItemList.get(k);
									String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(syear),
											String.valueOf(i),"","",this.getTempString(this.codeitem_value),true);
									String avgValue = this.getCodeItemValue(sql);
									this.avgValue = avgValue;
									//代码项描述信息
									String codeItemDesc="";
									if("B0110".equalsIgnoreCase(this.field_name)){//单位
										codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
									}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
										codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
									}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
										codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
									}else{
										codeItemDesc= this.getCodeItemDesc(codeValues);
									}

									CommonData cd = new CommonData(avgValue,codeItemDesc);
									
									/*System.out.println("***********数据平均值***********");
									System.out.println(sql);
									System.out.println("值="+avgValue);
									System.out.println("***********************************");*/
									
								//	list.add(cd);
								//	map.put("平均值",list);
							}
							
						}
					
					}
					
				}else{
					for(int i = smonth; i<=12; i++){
						ArrayList list = new ArrayList();
						for(int j=0 ;j<codeItemList.size();j++){						
							String codeValues = (String)codeItemList.get(j);
							String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(syear),
									String.valueOf(i),"","",codeValues ,false);
							//System.out.println(sql);					
							String codeItemValue = this.getCodeItemValue(sql.toString());
							//代码项描述信息
							String codeItemDesc="";
							if("B0110".equalsIgnoreCase(this.field_name)){//单位
								codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
							}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
								codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
							}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
								codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
							}else{
								codeItemDesc= this.getCodeItemDesc(codeValues);
							}

							CommonData cd = new CommonData(codeItemValue,codeItemDesc);
							list.add(cd);
						}
						map.put(syear+"年"+i+"月",list);
					}
					
					for(int i =syear ; i< eyear; i++){
						for(int j=1;j<=12;j++){
							ArrayList list = new ArrayList();
							for(int k=0 ;k<codeItemList.size();k++){					
								String codeValues = (String)codeItemList.get(k);
								String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(syear),
										String.valueOf(j),"","",codeValues,false);
								//System.out.println(sql);					
								String codeItemValue = this.getCodeItemValue(sql.toString());
								//代码项描述信息
								String codeItemDesc="";
								if("B0110".equalsIgnoreCase(this.field_name)){//单位
									codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
								}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
									codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
								}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
									codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
								}else{
									codeItemDesc= this.getCodeItemDesc(codeValues);
								}
								CommonData cd = new CommonData(codeItemValue,codeItemDesc);
								list.add(cd);
							}
							map.put(syear+"年"+i+"月",list);
						}
					}
					
					for(int i = 1; i<=emonth; i++){
						ArrayList list = new ArrayList();
						for(int j=0 ;j<codeItemList.size();j++){						
							String codeValues = (String)codeItemList.get(j);
							String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(eyear),
									String.valueOf(i),"","",codeValues,false);
							//System.out.println(sql);					
							String codeItemValue = this.getCodeItemValue(sql.toString());
							//代码项描述信息
							String codeItemDesc="";
							if("B0110".equalsIgnoreCase(this.field_name)){//单位
								codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
							}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
								codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
							}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
								codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
							}else{
								codeItemDesc= this.getCodeItemDesc(codeValues);
							}
							CommonData cd = new CommonData(codeItemValue,codeItemDesc);
							list.add(cd);
						}
						map.put(syear+"年"+i+"月",list);
					}
					if(this.static_method == 4){
						//平均值
						ArrayList list = new ArrayList();
						for(int i = smonth; i<=12; i++){					
							for(int j=0 ;j<codeItemList.size();j++){	
								String codeValues = (String)codeItemList.get(j);
								String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(syear),
										String.valueOf(i),"","",this.getTempString(this.codeitem_value),true);				
								String codeItemValue = this.getCodeItemValue(sql.toString());
								this.avgValue = codeItemValue;
								//代码项描述信息
								String codeItemDesc="";
								if("B0110".equalsIgnoreCase(this.field_name)){//单位
									codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
								}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
									codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
								}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
									codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
								}else{
									codeItemDesc= this.getCodeItemDesc(codeValues);
								}
								CommonData cd = new CommonData(codeItemValue,codeItemDesc);
								list.add(cd);
							}
						}
						int t = eyear-1;
						if(syear == t){
							
						}else{
							for(int i =syear ; i< eyear; i++){
								for(int j=1;j<=12;j++){
									for(int k=0 ;k<codeItemList.size();k++){	
										String codeValues = (String)codeItemList.get(j);
										String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(syear),
												String.valueOf(i),"","",this.getTempString(this.codeitem_value),true);				
										String codeItemValue = this.getCodeItemValue(sql.toString());
										this.avgValue = codeItemValue;
										//代码项描述信息
										String codeItemDesc="";
										if("B0110".equalsIgnoreCase(this.field_name)){//单位
											codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
										}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
											codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
										}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
											codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
										}else{
											codeItemDesc= this.getCodeItemDesc(codeValues);
										}
										CommonData cd = new CommonData(codeItemValue,codeItemDesc);
										list.add(cd);
									}
								}
							}
						}
						for(int i = 1; i<=emonth; i++){
							for(int j=0 ;j<codeItemList.size();j++){	
								String codeValues = (String)codeItemList.get(j);
								String sql = this.getFormulaSql(dbFlag,dbpre,String.valueOf(syear),
										String.valueOf(i),"","",this.getTempString(this.codeitem_value),true);				
								String codeItemValue = this.getCodeItemValue(sql.toString());
								this.avgValue = codeItemValue;
								//代码项描述信息
								String codeItemDesc="";
								if("B0110".equalsIgnoreCase(this.field_name)){//单位
									codeItemDesc = this.getCodeItemDesc(codeValues,"UN");
								}else if("E0122".equalsIgnoreCase(this.field_name)){//部门
									codeItemDesc = this.getCodeItemDesc(codeValues,"UM");
								}else if("E01A1".equalsIgnoreCase(this.field_name)){//职位
									codeItemDesc = this.getCodeItemDesc(codeValues,"@K");
								}else{
									codeItemDesc= this.getCodeItemDesc(codeValues);
								}
								CommonData cd = new CommonData(codeItemValue,codeItemDesc);
								list.add(cd);
							}
						}
						
						//map.put("平均值",list);
						
					}
					
				}
			}
			obj=map;
		}
		
		return obj;
		
	}

	/**
	 * 获取分析指标描述信息
	 * @return
	 * @throws GeneralException
	 */
	public String getFielditemDesc(String factorid) throws GeneralException{
		String fieldItemDesc = "";
		ContentDAO dao=new ContentDAO(this.conn);
		String sql = "select itemdesc from fielditem where itemid =(select field_name from ds_key_factor where factorid ='"+factorid+"')";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				fieldItemDesc = rs.getString("itemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
		
		return fieldItemDesc;
	}
	
	
	/**
	 * 获取一般代码项的描述信息
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
	}
	/**
	 * 获取分析指标代码项的值
	 * @param sql
	 * @return
	 * @throws GeneralException
	 */
	public String getCodeItemValue(String sql) throws GeneralException{
		String fieldItemValue = "";
		//System.out.println(sql);
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
	
	
	public String getName() {
		return name;
	}

	public static void main(String [] args){
	
	}

	public String getAvgValue() {
		return avgValue;
	}

	public float getControl_value() {
		return control_value;
	}

	public float getStandard_value() {
		return standard_value;
	}
	
	
}
