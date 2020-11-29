package com.hjsj.hrms.businessobject.org.autostatic.confset;

import com.hjsj.hrms.utils.TimeScope;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Iterator;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */

public class DataScanBo {
	public DataScanBo()
	{}
/**
 * 分析数据联动的条件，并形成sql语句
 * @param uv          UserView
 * @param subsetstr   子集名称
 * @param dao         ContentDAO
 * @param dbname      数据库前缀
 * @param getyear     年
 * @param getmonth    月
 * @param changeflag  changeflag
 * @throws GeneralException
 */
	public void getsql(UserView uv,String subsetstr,ContentDAO dao,String dbname,String getyear,String getmonth,String changeflag) throws GeneralException
	{
		RowSet rs = null;
		String getz0 = this.getz0(getyear,getmonth,changeflag);   //得到时间
		getz0 = Sql_switcher.dateValue(getz0);
		String time = this.gettime(getyear,getmonth,changeflag);  //获得年月标识时间
		String systime = Sql_switcher.sqlNow();  //  获得系统时间
//		System.out.println(getz0);
		String equalz0time = this.getequalz0time(subsetstr,getyear,getmonth,changeflag); 
		String insertz0time = this.getequalz0time(subsetstr+"."+subsetstr,getyear,getmonth,changeflag);
		try{
			 ArrayList fieldlist =new ArrayList();
			 ArrayList itemidlist =new ArrayList();
			 String temp = new String("");       // 组成统计条件,给getPrivSQLExpression方法
			 String id = "";         // 表的标识  eg: B04的B0110列
			 String tablealias =""; // 表别名   eg: B04 b
			 String[] expr;          			 // 获得用"|"分段截出的各个部分
			 String consql = "";     // 统计项目,最终形成的sql
			 String privsql = "";    // 通过getPrivSQLExpression方法生成的from后面的sql    
			 String countsql = "";   // 计算项目,最终形成的sql
			 String collsql = "";    // 汇总项目,最终形成的sql
			 String typestr = "";    // 操作类型 1, 计算项目  2,统计项目  3,汇总项目
			 String factor = "";     // 条件的个数,以及关系
			 String operstr = "";    // 操作类型   0，求个数 1，求和  2，最小值  3，最大值  4，平均值
			 String field = "";      // 指标 eg: b0430
			 String displayid = "";  // 在fielditem表里,用类标识子集
			 String conformula = ""; // 统计公式字段
			 String contable = "";   // 统计公式所用的表
			 String c_expr = "";     // 中文字符串
			 String fieldtype = "";  // 参数类型
			 String FSQL = "";       // 获得将中文转化成的字段
			 String firstpart = "";  // 获得用"|"分段截出的第一个部分
			 String secondpart = ""; // 获得用"|"分段截出的第二个部分
			 String thirdpart = "";  // 获得用"|"分段截出的第三个部分
			 String subclass = "";   // 获得expression里,包含的下级
			 String countsbstr = ""; // 统计项目里,中文转化成的字段
			 String controlz0 = "";  // 给getz0time方法的参数
			 ArrayList conformulastr = new ArrayList();      // 统计项目里,获得所要查询的人员表的A0100字段			 
			 ArrayList conformulatabstr = new ArrayList();   // 统计项目里,获得所要查询的人员表
			 ArrayList congroupbystr = new ArrayList();      // 统计项目里,统计公式需要的groupby的字段
			 ArrayList conwherestr = new ArrayList();        // 统计项目里,统计公式需要的where的字段			 
			 StringBuffer wheresb = new StringBuffer();      // 统计项目里,update子集表所需要用的wheresb的字段
			 StringBuffer groupbysb = new StringBuffer();    // 统计项目里,update子集表所需要用的groupbysb的字段
			 StringBuffer countsb = new StringBuffer();      // 统计项目里,update子集表,对中文转化成的字段所做的处理
			 StringBuffer fieldsb = new StringBuffer();      // 计算项目里,insert子集表,处理要插入的字段
//			 StringBuffer countinsnumsb = new StringBuffer();// 计算项目里,insert子集表,处理要插入的字段的值
//			 StringBuffer coninsnumsb = new StringBuffer();  // 统计项目里,insert子集表,处理要插入的字段的值
			 StringBuffer countsetsb = new StringBuffer();   // 计算项目里,update子集表,set操作所需要的处理
			 StringBuffer confieldsb = new StringBuffer();   // 统计项目里,insert子集表,处理要插入的字段
			 StringBuffer cousetsb = new StringBuffer();     // 统计项目里,update子集表,set操作所需要的处理
			 StringBuffer tablealiasb = new StringBuffer();  // 统计项目里,处理统计公式所得的结果
			 StringBuffer collectionsb = new StringBuffer(); // 统计项目里,获得统计公式所用的表			 
//			 StringBuffer collfieldsb = new StringBuffer();     // 汇总项目里,insert子集表,处理要插入的字段
//			 StringBuffer collinsnumsb = new StringBuffer();    // 汇总项目里,insert子集表,处理要插入的字段的值
			 StringBuffer collsetsb = new StringBuffer();       // 汇总项目里,update子集表,set操作所需要的处理 
			 StringBuffer countsqlsb = new StringBuffer();      // 统计项目里,update子集表的sql		
			 StringBuffer contentsb = new StringBuffer();
			 StringBuffer contentsetsb = new StringBuffer();
			 int conum = 0;         //  获得统计项目的个数
			 int countnum = 0;      //  获得计算项目的个数
			 int collnum = 0;       //  获得汇总项目的个数
			 int consqlnum = 0;     //  控制统计项目
			 int countsqlnum = 0;   //  控制计算项目
			 int collsqlnum = 0;    //  控制汇总项目15901552038
			 int w = 0;             //  控制计算项目循环
			 int y = 0;             //  控制统计项目循环
			 int z = 0;             //  控制汇总项目循环
			 int fieldsplitnum = 0; //  控制对中文字符处理的循环
			 int i = 0;;            //  控制非正确设定的指标
			 int wi = 0;
			 int zi = 0;
			 int c = 0;             //  控制包含下级和逐级汇总
			 String supertable = "";
//			 String e0122 = "";
			String itemidsql = "select displayid,expression from fielditem where expression not like '' and fieldsetid = '"+subsetstr+"' ";
			// 获得所有 expression 不为空的子集,以及该子集对应的display标识
			itemidlist = dao.searchDynaList(itemidsql);
			// 获得给处理中文字符的解析器的参数			
			ArrayList alUsedFields = new ArrayList();
			int infoGroup=0;
			int varType =6; // float
			if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))){
				id = "B0110";        // 单位表的连接字段
				tablealias = "b";	 // 单位表的别名		
				supertable = "B01";
			}
			else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
				id = "E01A1";         // 职位表的连接字段
				tablealias = "k";	  // 职位表的别名		
				supertable = "K01";
			}
//			this.getableinfo(subsetstr, id, tablealias, supertable);
			String delsql = " delete "+subsetstr+" where "+this.getequalz0time(subsetstr,getyear,getmonth,changeflag)+" and state=''"; 			
//			System.out.println(delsql);		
			dao.update(delsql);
			//	判断该表是否有记录
			String judgestr = "";
			String judgetable =  "select * from "+subsetstr; 
			RowSet judgert = dao.search(judgetable);
			if(judgert.next()){
				judgestr = "insert";
			}
			else{
				judgestr = "inserts";
			}
//			System.out.println(judgestr);			
			//  获得计算，统计，汇总各种项目的总数
			for(Iterator it=itemidlist.iterator();it.hasNext();){
				StringBuffer sb = new StringBuffer();
				DynaBean dynabean=(DynaBean)it.next();
				temp = dynabean.get("expression").toString();
				temp = temp.substring(0,1);
				if("1".equals(temp)){
					countnum ++; // 计算项目的总数
				}
				else if("2".equals(temp)){
					conum ++;    // 统计项目的总数
				}
				else if("4".equals(temp)){
					collnum ++;  // 汇总项目的总数
				}
				
			}
			
			//  取出fielditem表的expression字段，进行解析，并形成sql语句的大循环
			for(Iterator it=itemidlist.iterator();it.hasNext();){
				boolean countgo = true;
				boolean congo = true;
				//  判断是单位还是职位表，为对中文字符解析器提供参数
				if("B".equals(subsetstr.substring(0,1))|| "b".equals(subsetstr.substring(0,1))){
					infoGroup = 3; // forUnit 单位
					alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
				}
				else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
					infoGroup = 1; // forPosition 职位
					ArrayList unitFieldList= DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
					ArrayList positionFieldList= DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
					for (Iterator t = unitFieldList.iterator(); t.hasNext();) {
						FieldItem fielditem = (FieldItem) t.next();
						alUsedFields.add(fielditem);
					}	
					
					for (int a=0;a<positionFieldList.size();a++) {
						FieldItem afielditem = (FieldItem)positionFieldList.get(a);					
						alUsedFields.add(afielditem);
					}
				}
				StringBuffer sb = new StringBuffer();     //  获得处理"查询条件"的方法（getPrivSQLExpression）的expression参数
				DynaBean dynabean=(DynaBean)it.next();
				temp = dynabean.get("expression").toString();
				field = dynabean.get("expression").toString();
				displayid = dynabean.get("displayid").toString();
				
				//  获得itemid，itemtype
				String fieldsql = "select itemid,itemtype from fielditem where  fieldsetid like '";
				fieldsql = fieldsql + subsetstr+"' and displayid like '"+displayid+"' and expression like '"+field+"' ";
				rs = dao.search(fieldsql);
				if(rs.next()){
					field = rs.getString("itemid").toString();
					fieldtype = rs.getString("itemtype").toString();
				}
				varType = this.getvarType(fieldtype,varType);
				//  先分三大段截取expression字段
				if(temp.length()>0)
				{
					expr = temp.split("\\|");
					int j = expr.length;
					if(j>0)
					{
						for(int k=1;k<expr.length+1;k++)
						{
							if(k==1)  // 第一段
							{
								firstpart = expr[0].toString();  
								int splitnum = firstpart.split("::").length;
								if(splitnum>0)
								{
									for(int x=1;x<splitnum+1;x++)
									{
										if(x==1){
											// 获得要进行的操作 eg: 统计项目，计算项目，汇总项目
											typestr = firstpart.split("::")[0]; 
											if("1".equals(typestr)){
												typestr = "1";  
												countsqlnum ++;												
											}
											else if("2".equals(typestr)){
												y++;
												typestr = "2";
												consqlnum ++;
												infoGroup=0;
												alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
											}
											else if("3".equals(typestr)){
												typestr = "3";
												collsqlnum ++;
											}
										}
										else if(x==2){
											// 获得要进行的操作 eg: 求个数，求和 等等
											operstr = firstpart.split("::")[1]; 
											operstr = this.getoperstr(operstr);
										}
										else if(x==3)
										{
											c_expr = firstpart.split("::")[2].toString();  // 获得公式
											c_expr=c_expr.trim(); 
											if(!(c_expr==null || "".equals(c_expr)) )
											{
												if(typestr == "1" || "1".equals(typestr))
												{
													try{
														YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,varType,infoGroup,"","");
														yp.run(c_expr);   
												        FSQL=yp.getSQL();//将中文字符转换成要操作的字段
													}
													catch(Exception exc){
														countgo = false;
														exc.printStackTrace();
													}
												}
												
										        // 如果是统计项目，要找出需要操作的相应人员表  eg: 对岗位工资求和，要用usra58表
										        if(typestr == "2" || "2".equals(typestr))
										        {	
										        	try{
														YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,varType,infoGroup,"","");
														yp.run(c_expr);   
												        FSQL=yp.getSQL();//将中文字符转换成要操作的字段
													}
													catch(Exception ex){
														congo = false;
														ex.printStackTrace();
													}
										        	String z0timevalue = "";
										        	fieldsplitnum = FSQL.split(",").length;
										        	 //  处理多个库前缀
										        	String[] dbnamearray =null;
								    				int index  = dbname.split("\\,").length; 
								    				String dbnamestr = "";
								    				if(index>1) // 有多个库前缀,多个中文字符
								    				{
								    					dbnamearray = dbname.split("\\,");
								    					for(int t=0;t<index;t++)
								    					{
								    						dbnamestr = dbnamearray[t].toString();
								    						
								    						if(fieldsplitnum>1) //有一个或者多个中文字符
												        	{
								    							for(int s=0;s<fieldsplitnum-1;s++) //对有多个库前缀，多个中文字符的处理	
												        		{
								    								conformula = FSQL.split(",")[s].toString(); // 用","分开isnull(e5809,0.0),									        			
												        			int templast = conformula.lastIndexOf("(")+1;
												        			conformula = conformula.substring(templast,conformula.length());//得到e5809
												        			FieldItem fi = DataDictionary.getFieldItem(conformula);												        			
												        			contable = dbnamestr+fi.getFieldsetid();												        			
									    							controlz0 = fi.getFieldsetid();
									    							FieldSet fs = DataDictionary.getFieldSetVo(fi.getFieldsetid());
									    							String getchangflag = fs.getChangeflag();
									    							z0timevalue = this.getz0time(controlz0,getyear,getmonth,getchangflag);
									
												        			if(s==0)  //  如果是一个字段
										    		    			{
										    							 congroupbystr.add(t,contable+".a0100,"+contable+"."+conformula);
										    							 conwherestr.add(t,contable+".a0100=x"+y+".a0100 and "+z0timevalue+" ") ;									    						
										    							 conformulastr.add(t,","+contable+".a0100 ");
										    							 conformulatabstr.add(t, contable+",");										   
										    		    			}
										    		    			else if(s>0)  //  如果涉及到多个字段
										    		    			{
										    		    				congroupbystr.add(t,(String)congroupbystr.get(t)+","+contable+"."+conformula);
										    		    				conwherestr.add(t,contable+".a0100=x"+y+".a0100 and "+z0timevalue+" ");
										    		    				conformulatabstr.add(t,(String)conformulatabstr.get(t)+contable+"," );
										    		    			}									        			
										    		    			
												        		}
												        	}
								    						else  //对多个库前缀，一个中文字符的处理
								    						{
								    							if(fieldsplitnum == 1)   //  如果是个固定数值
								    							{
								    								 contable = dbnamestr+"a01";
								    								 congroupbystr.add(t,contable+".a0100 ");
									    							 conwherestr.add(t,contable+".a0100=x"+y+".a0100 ") ;									    								
									    							 conformulastr.add(t,","+contable+".a0100 ");
									    							 conformulatabstr.add(t, contable+",");								    		    												    							 
								    							}											    							
								    						}
								    					}
								    				}
								    				else //  对一个库前缀，多个中文字符的处理
								    				{
								    					dbnamearray = dbname.split("\\,");
								    					dbnamestr = dbnamearray[0].toString();
								    					if(fieldsplitnum>1) //有一个或者多个中文字符
								    					{
								    						for(int s=0;s<fieldsplitnum-1;s++) //对有一个库前缀，多个中文字符的处理
								    						{
								    							conformula = FSQL.split(",")[s].toString(); // 用","分开isnull(e5809,0.0),									        			
											        			int templast = conformula.lastIndexOf("(")+1;
											        			conformula = conformula.substring(templast,conformula.length());//得到e5809
											        			FieldItem fi = DataDictionary.getFieldItem(conformula);
											        			contable = dbnamestr+fi.getFieldsetid();			
											        			controlz0 = fi.getFieldsetid();
											        			FieldSet fs = DataDictionary.getFieldSetVo(fi.getFieldsetid());
								    							String getchangflag = fs.getChangeflag();
								    							z0timevalue = this.getz0time(controlz0,getyear,getmonth,getchangflag);
									    					
										    					if(s==0) //  如果是一个字段
										    	    			{
										    						 congroupbystr.add(0,contable+".a0100,"+contable+"."+conformula);
										    						 conwherestr.add(0,contable+".a0100=x"+y+".a0100 and "+z0timevalue);
										    						 conformulastr.add(0,","+contable+".a0100 ");
										    						 conformulatabstr.add(0,contable+",");
										    	    				
										    	    			}
										    	    			else if(s>0)  //  如果涉及到多个字段
										    	    			{
										    	    				congroupbystr.add(0,(String)congroupbystr.get(0)+","+contable+"."+conformula );
										    	    				conwherestr.add(0,(String)conwherestr.get(0)+" and "+contable+".a0100=x"+y+".a0100 and "+z0timevalue );
										    	    				conformulatabstr.add(0,(String)conformulatabstr.get(0)+contable+"," );
										    	    			}									        													    	    			
								    						}
								    					}
								    					else  //  对一个库前缀，一个中文字符的处理
								    					{
								    						if(fieldsplitnum == 1)  //  如果是个固定数值
								    						{
								    							contable = dbname+"a01";
								    							congroupbystr.add(0,contable+".a0100 ");
									    						conwherestr.add(0,contable+".a0100=x"+y+".a0100 ");
									    						conformulastr.add(0,","+contable+".a0100 ");
									    						conformulatabstr.add(0,contable+",");								    	    											        												    	    								
								    						}
								    						 
									    				}						
								    					
								    				}
							        
										        }   
											}
										}								
									}
								}
							}
							if(k==2) // 第二段
							{
								// 获得操作条件的字段
								secondpart = expr[1].toString();								
								 int splitnum = secondpart.split("::").length;
								 if(splitnum>0)
								 {
									 if(splitnum==1){
										 temp="";
									 }
									 else
									 {
										 for(int x=1;x<splitnum+1;x++)
										 {
											 if(x==2)
											 {
												 temp = secondpart.split("::")[1].toString();	// expression 表达式	
											 }
										 }
									 }									 
								 }	
							}
							if(k==3)
							{
								thirdpart = expr[2].toString();
								 int splitnum = thirdpart.split("::").length;
								 if(splitnum>0){
									 for(int x=1;x<splitnum+1;x++)
									 {
										 if(x==1){
											factor = thirdpart.split("::")[0].toString();     // 获得条件的个数和关系
											if(factor.length()>0)
											{
												sb.append(factor+"|"+temp);
											}
											else{
												sb.append("");
											}
										 }
										 else if(x==2){
											 thirdpart = thirdpart.split("::")[1].toString();   // 是否包含下级
												int splitsub = thirdpart.split("-").length;
												if(splitsub <=1){
													subclass = thirdpart;
												}
										 }
									 }
								 }
							}
						}
					}
					else
					{ 
						temp=""; //  如果是汇总项目，查询条件就为空
					}
				}
			
				fieldlist.add(field);
				String expression = sb.toString();	
						
				if("2".equals(typestr))
				{
					StringBuffer conditionsb = new StringBuffer();  // 统计项目里,子集求个数的sql
					StringBuffer sumconformulasb = new StringBuffer(); // 统计项目里,多个库前缀,子集求和,求最大,最小,平均值的sql
					StringBuffer conditiontempsb = new StringBuffer(); // 统计项目里,多个库前缀,子集求个数的sql
					StringBuffer sumconformulatempsb = new StringBuffer();	// 统计项目里,一个库前缀,子集求和,求最大值数的sql
					String sum = " sum("+field+") ";
					if(congo)
					{
						i++;
						if(y==1){
							wheresb.append( " "+tablealias+"."+id+"=t"+y+"."+id+" " );
							confieldsb.append(field);
							cousetsb.append(field+"="+tablealias+y);
							tablealiasb.append("t"+y+"."+tablealias+y);
							countsbstr = FSQL+" as "+tablealias+y;
//							contentsetsb.append(field+"=t.t"+y);
						}
						else if(y>1){
							wheresb.append( " or "+tablealias+"."+id+"=t"+y+"."+id+" " );						
							confieldsb.append(","+field);
							cousetsb.append(","+field+"="+tablealias+y);
							tablealiasb.append(",t"+y+"."+tablealias+y);
							countsbstr = FSQL+" as "+tablealias+y;
							
						}
						//对条件的解析，From后面的字符串
						groupbysb.append(", t"+y+"."+tablealias+y+" ");
						if("1".equals(subclass))
						{
							c++;
							contentsetsb.append(","+field+"=t.t"+y);
							contentsb.append(this.getcontent( subsetstr, sum, y, equalz0time));
						}
						
						if("count".equals(operstr))  // 统计项目里的求个数
						{
							String[] dbnamearray ;
							int index  = dbname.split(",").length; 
							if(index>1) // 有多个库前缀
							{
								dbnamearray = dbname.split(",");
								for(int t=0;t<index;t++){
									String dbnamestr = dbnamearray[t].toString();
									privsql =uv.getPrivSQLExpression(expression,dbnamestr,false,fieldlist);
									//  统计项目，求个数，多个库前缀，
									conditiontempsb = this.conselectsingle(subsetstr, t, index, conditiontempsb, tablealias, id, privsql, expression, y);
//									System.out.println(conditiontempsb.toString());	
								}
								countsqlsb = this.conselectedbs ( y, conum, conditionsb, countsqlsb, tablealias, conditiontempsb, id);
//								System.out.println(countsqlsb.toString());							
							}
							else // 只有一个库前缀
							{
								privsql =uv.getPrivSQLExpression(expression,dbname,false,fieldlist);
								String fieldcondition = " ";
								String fieldconditiontwo = " ";
								if(expression == null || "".equals(expression)){
									fieldcondition = " ";
									fieldconditiontwo = " ";
								}
								else{
									fieldcondition = " and "+id+" not like '' ";
									fieldconditiontwo = " and e0122 not like '' ";
								}
//							  统计项目，求个数，一个库前缀，
								countsqlsb = this.conselectedb (subsetstr, y, conum, conditionsb, countsqlsb, tablealias, id, fieldcondition, fieldconditiontwo,privsql);
//								System.out.println(countsqlsb.toString());
							}													
						}
						else  // 统计项目里的 求和,求最大,最小,平均值
						{
							String[] dbnamearray ;
							int index  = dbname.split(",").length; 
							if(index>1)   // 有多个库前缀
							{
								dbnamearray = dbname.split(",");
								for(int t=0;t<index;t++)
								{
									String dbnamestr = dbnamearray[t].toString();
									privsql =uv.getPrivSQLExpression(expression,dbnamestr,false,fieldlist);
									sumconformulatempsb = this.conselectkinds( t,index,id, y, tablealias, dbnamestr, sumconformulatempsb, conformulastr, congroupbystr, conformulatabstr, countsbstr, conwherestr, privsql);
									sumconformulatempsb = this.conselectkindstwo( t,index,id, y, tablealias, dbnamestr, sumconformulatempsb, conformulastr, congroupbystr, conformulatabstr, countsbstr, conwherestr, privsql);
								}
								countsqlsb = conselectedbss( y, conum, sumconformulasb, sumconformulatempsb, id, operstr, tablealias, countsqlsb);
//								System.out.println(countsqlsb.toString());
							}
							else  // 只有一个库前缀
							{
								privsql =uv.getPrivSQLExpression(expression,dbname,false,fieldlist);						
								sumconformulatempsb.append(",( select "+countsbstr+conformulastr.get(0)+" from "+conformulatabstr.get(0));
								sumconformulatempsb.append(" ( select "+dbname+"a01.a0100 "+privsql+" ) x"+y);
								sumconformulatempsb.append(" where "+ conwherestr.get(0));
								sumconformulatempsb.append(" group by "+congroupbystr.get(0)+") as s"+y);
								sumconformulatempsb.append(" where a.a0100 = s"+y+".a0100 and "+id+" not like '' ");
								sumconformulatempsb.append(" group by a."+id);
								countsqlsb = this.condbssingle( y, conum, sumconformulasb, sumconformulatempsb, id, operstr, tablealias, dbname, countsqlsb);
//								System.out.println(countsqlsb.toString());
								StringBuffer tempsb = new StringBuffer(" ");
								sumconformulatempsb = tempsb;
								sumconformulatempsb.append(",( select "+countsbstr+conformulastr.get(0)+" from "+conformulatabstr.get(0));
								sumconformulatempsb.append(" ( select "+dbname+"a01.a0100 "+privsql+" ) x"+y);
								sumconformulatempsb.append(" where "+ conwherestr.get(0));
								sumconformulatempsb.append(" group by "+congroupbystr.get(0)+") as s"+y);
								sumconformulatempsb.append(" where a.a0100 = s"+y+".a0100 and a.e0122 not like '' ");
								sumconformulatempsb.append(" group by a.e0122");
								countsqlsb = this.condbssingletwo( y, conum, sumconformulatempsb, id, operstr, tablealias, dbname, countsqlsb);
//								System.out.println(countsqlsb.toString());
							}
							
						}
						
						if(conum==consqlnum ) // 统计
						{
							if("inserts".equals(judgestr)){
								consql = this.inserts( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);
//								System.out.println(consql);
								dao.update(consql);
								consql = this.conupdate( subsetstr, cousetsb, systime, uv, tablealias, id, tablealiasb, wheresb, equalz0time, groupbysb, countsqlsb);
								System.out.println(consql);
								dao.update(consql);
								if(c>0)
								{
									consql = this.getcontentsql( subsetstr, contentsetsb, contentsb, equalz0time); 
//									System.out.println(consql);
									dao.update(consql);
								}								
							}						
							else if("insert".equals(judgestr)){
								consql = this.insert( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);	
//								System.out.println(consql);
								dao.update(consql);
								consql = this.conupdate( subsetstr, cousetsb, systime, uv, tablealias, id, tablealiasb, wheresb, equalz0time, groupbysb, countsqlsb);							
								System.out.println(consql);
								dao.update(consql);		
								if(c>0)
								{
									consql = this.getcontentsql( subsetstr, contentsetsb, contentsb, equalz0time); 
//									System.out.println(consql);
									dao.update(consql);
								}
								
							}						
						}
					}
					else if(countgo==false && countnum!=1 && i!=0)  
					{
						if(conum==consqlnum ){
							if("inserts".equals(judgestr)){
								consql = this.inserts( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);
//								System.out.println(consql);
								dao.update(consql);
								consql = this.conupdate( subsetstr, cousetsb, systime, uv, tablealias, id, tablealiasb, wheresb, equalz0time, groupbysb, countsqlsb);
//								System.out.println(consql);
								dao.update(consql);
								if(c>0)
								{
									consql = this.getcontentsql( subsetstr, contentsetsb, contentsb, equalz0time); 
//									System.out.println(consql);
									dao.update(consql);
								}
								
							}						
							else if("insert".equals(judgestr)){
								consql = this.insert( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);	
//								System.out.println(consql);
								dao.update(consql);
								consql = this.conupdate( subsetstr, cousetsb, systime, uv, tablealias, id, tablealiasb, wheresb, equalz0time, groupbysb, countsqlsb);							
//								System.out.println(consql);
								dao.update(consql);	
								if(c>0)
								{
									consql = this.getcontentsql( subsetstr, contentsetsb, contentsb, equalz0time); 
//									System.out.println(consql);
									dao.update(consql);
								}
								
							}						
						}
					}
				}
				if(w>0 && conum==consqlnum && wi==0 )  // 计算
				{
					if("inserts".equals(judgestr)){
						countsql = this.inserts( subsetstr, id, getz0, time, systime, uv, insertz0time, supertable);
//						System.out.println(countsql);
						dao.update(countsql);
						countsql = this.countupdate( subsetstr, countsetsb, systime, uv, countsb, equalz0time, id);
//						System.out.println(countsql);
						dao.update(countsql);
						
					}						
					else if("insert".equals(judgestr)){
						countsql = this.insert( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);	
//						System.out.println(countsql);					
						dao.update(countsql);
						countsql = this.countupdate( subsetstr, countsetsb, systime, uv, countsb, equalz0time, id);
//						System.out.println(countsql);
						dao.update(countsql);								
					}		
				}
				if("1".equals(typestr))
				{
					if(countgo){
						w++;
						if(w==1)  //  第一个计算的
						{
							countsb.append(FSQL+" as "+tablealias+w);
							fieldsb.append(field);
							countsetsb.append(field+"="+tablealias+w);
						}
						else if(w>1)
						{
							countsb.append(","+FSQL+" as "+tablealias+w);
							fieldsb.append(","+field);
							countsetsb.append(","+field+"="+tablealias+w);
						}
						if(countnum==countsqlnum && conum==consqlnum)  // 计算
						{
							wi = 1;
							if("inserts".equals(judgestr)){
								countsql = this.inserts( subsetstr, id, getz0, time, systime, uv, insertz0time, supertable);
//								System.out.println(countsql);
								dao.update(countsql);
								countsql = this.countupdate( subsetstr, countsetsb, systime, uv, countsb, equalz0time, id);
//								System.out.println(countsql);
								dao.update(countsql);
								
							}						
							else if("insert".equals(judgestr)){
								countsql = this.insert( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);	
//								System.out.println(countsql);					
								dao.update(countsql);
								countsql = this.countupdate( subsetstr, countsetsb, systime, uv, countsb, equalz0time, id);
//								System.out.println(countsql);
								dao.update(countsql);								
							}							
						}
					}
					else if(countgo==false && countnum!=1 && w!=0)
					{
						if(countnum==countsqlnum ){
							if("inserts".equals(judgestr)){
								countsql = this.inserts( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);
//								System.out.println(consql);
								dao.update(countsql);
								countsql = this.countupdate( subsetstr, countsetsb, systime, uv, countsb, equalz0time, id);
//								System.out.println(countsql);
								dao.update(countsql);								
							}						
							else if("insert".equals(judgestr)){
								countsql = this.insert( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);	
//								System.out.println(consql);
								dao.update(countsql);
								countsql = this.countupdate( subsetstr, countsetsb, systime, uv, countsb, equalz0time, id);
//								System.out.println(countsql);
								dao.update(countsql);								
							}							
						}	
					}					
				}
				if(z>0 && conum==consqlnum && countnum==countsqlnum && zi==0)
				{
					if("inserts".equals(judgestr)){
						collsql = this.inserts( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);			
//						System.out.println(consql);
						dao.update(collsql);
						collsql = this.collupdate( subsetstr, collsetsb, systime, uv, collectionsb, id, equalz0time);					
//						System.out.println(collsql);
						dao.update(collsql);						
					}						
					else if("insert".equals(judgestr)){
						collsql = this.insert( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);							
//						System.out.println(consql);
						dao.update(collsql);
						collsql = this.collupdate( subsetstr, collsetsb, systime, uv, collectionsb, id, equalz0time);					
//						System.out.println(collsql);
						dao.update(collsql);							
					}	
				}
				if("3".equals(typestr))
				{
					z++;
					if(z==1){
						collectionsb.append(field+" as "+tablealias+z);
						collsetsb.append(field+"="+tablealias+z);
					}
					else if(z>1){
						collectionsb.append(","+field+" as "+tablealias+z);
						collsetsb.append(","+field+"="+tablealias+z);
					}
					if(collnum==collsqlnum && conum==consqlnum && countnum==countsqlnum){
						zi =1;
						if("inserts".equals(judgestr)){
							collsql = this.inserts( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);			
//							System.out.println(consql);
							dao.update(collsql);
							collsql = this.collupdate( subsetstr, collsetsb, systime, uv, collectionsb, id, equalz0time);					
//							System.out.println(collsql);
							dao.update(collsql);
							
						}						
						else if("insert".equals(judgestr)){
							collsql = this.insert( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);							
//							System.out.println(consql);
							dao.update(collsql);
							collsql = this.collupdate( subsetstr, collsetsb, systime, uv, collectionsb, id, equalz0time);					
//							System.out.println(collsql);
							dao.update(collsql);							
						}						
					}
				}				
				
			}
			
			if("inserts".equals(judgestr)){
				String sql = this.inserts( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);
//				System.out.println(sql);
				dao.update(sql);
			}
			else if("insert".equals(judgestr)){
				String sql = this.insert( subsetstr, id, getz0, time, systime,  uv, insertz0time, supertable);
//				System.out.println(sql);
				dao.update(sql);
			}
		}
		catch(Exception e){
			e.printStackTrace();	
		}

	}
	/**
	 * 得到前台要显示的列
	 * @param subsetstr
	 * @param viewhide
	 * @return
	 */
	public String getdiselelctsql(String subsetstr,String viewhide)
	{
		StringBuffer fieldsb = new StringBuffer();
		String selectstr = "";
		subsetstr = subsetstr.toLowerCase();
		if("k".equals(subsetstr.substring(0,1).trim())){
			selectstr = " select " +viewhide;
		}else{
			selectstr = "select " +viewhide;		
		}
		return selectstr;
	}
	/**
	 * 得到前台sql的where条件
	 * @param subsetstr  子集名称
	 * @param getyear    年
	 * @param getmonth   月 
	 * @param changeflag changeflag
	 * @param grade      部门等级
	 * @param areanum    部门等级：  0：所以部门
	 * @param areavalue  部门范围
	 * @return
	 * @throws GeneralException
	 */
	public String getdiwheresql(String subsetstr,String getyear,String getmonth,String changeflag,String grade,String areanum,String areavalue) throws GeneralException
	{
		StringBuffer wherestr = new StringBuffer();
		String depstr = "";
		String equalz0time = this.getequalz0time(subsetstr,getyear,getmonth,changeflag);
		String getz0 = this.getz0(getyear,getmonth,changeflag);
		if(!"0".equals(areanum)){
			depstr = "  codeitemid like '"+areavalue+"%'";
		}
		if(!"K".equals(subsetstr.substring(0,1).toString())){
			wherestr.append(" from "+subsetstr+" a where "+equalz0time);
			if("0".equals(grade))
			{		
				if(!"0".equals(areanum)){
					wherestr.append(" and b0110 in (select codeitemid  from organization where "+depstr+") and state is null");	
				}else{
					wherestr.append(" and state is null ");
				}
				
			}else{
				if("0".equals(areanum)){
					wherestr.append(" and b0110 in (select codeitemid  from organization where grade in "+gradeSql(grade)+") and state is null");
				}else{
					wherestr.append(" and b0110 in (select codeitemid  from organization where grade in "+gradeSql(grade)+" and "+depstr+") and state is null");
				}
				
			}			
		}else{
			wherestr = wherestr.append(" from "+subsetstr+" a,( select  parentid ,codeitemid  from organization o,(select e01a1 from ");
			if("0".equals(areanum)){
				wherestr.append(subsetstr+" where "+equalz0time+") k where o.codeitemid = k.e01a1 ) t ");
				
			}else{
				wherestr.append(subsetstr+" where "+equalz0time+") k where o.codeitemid = k.e01a1 and "+depstr+") t ");
			}
			wherestr.append(" where "+equalz0time+" and a.e01a1 = t.codeitemid and a.state is null ");
		}
		
		return wherestr.toString();
	}
	/**
	 * 获得年月标识时间
	 * @param getyear
	 * @param getmonth
	 * @param changeflag
	 * @return
	 */
	public String gettime(String getyear,String getmonth,String changeflag)
	{
		String time = "";
		int inputmonth = Integer.parseInt(getmonth);
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			 time = getyear + "";
		}
		else
		{
			 time = getyear+"."+getmonth;
		}	
		return time;
	}
	/**
	 * 得到时间
	 * @param getyear    年
	 * @param getmonth   月
	 * @param changeflag changeflag
	 * @return
	 */
	public String getz0(String getyear,String getmonth,String changeflag)
	{
		String getz0 = "";
		int inputmonth = Integer.parseInt(getmonth);
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			getz0 = getyear + "-01-01";
		}
		else
		{
			getz0 = getyear+"-"+getmonth+"-01";
		}
		
		return getz0;
	}
	/**
	 * 得到时间段条件
	 * @param subsetstr  子集名称
	 * @param getyear    年
	 * @param getmonth   月
	 * @param changeflag changeflag
	 * @return
	 */
	public String getz0time(String subsetstr,String getyear,String getmonth,String changeflag)
	{
		String getz0time = "";
		TimeScope ts = new TimeScope();
		String startime = "";
		String endtime = "";
		int inputmonth = Integer.parseInt(getmonth);
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			startime = getyear+"-01-01";
			endtime = getyear+"-12-31";
		}
		else
		{
			startime = getyear+"-"+getmonth+"-01";
			endtime = getyear+"-"+getmonth+"-31";
		}
		getz0time = ts.gettimeScope(subsetstr+"z0",startime,endtime);
		return getz0time;
	}	
	/**
	 * 得到时间条件
	 * @param subsetstr  子集名称
	 * @param getyear    年
	 * @param getmonth   月
	 * @param changeflag changeflag
	 * @return
	 */
	public String getequalz0time(String subsetstr,String getyear,String getmonth,String changeflag)
	{
		String getequalz0time = "";
		TimeScope ts = new TimeScope();
		String time = "";
		int inputmonth = Integer.parseInt(getmonth);
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			time = getyear+"-01-01";
		}
		else
		{
			time = getyear+"-"+getmonth+"-01";
		}
		getequalz0time = ts.getTimeConditon(subsetstr+"Z0","=",time);
		return getequalz0time;
	}
	/**
	 * 载入上期数据时间
	 * @param subsetstr
	 * @param getyear
	 * @param getmonth
	 * @param changeflag
	 * @return
	 */
	public String getprevtime(String subsetstr,String getyear,String getmonth,String changeflag)
	{
		String getequalz0time = "";
		TimeScope ts = new TimeScope();
		String time = "";
		int inputmonth = Integer.parseInt(getmonth);
		int prevmonth = inputmonth-1;
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			time = Integer.parseInt(getyear)-1+"-01-01";
		}
		else
		{
			if(Integer.parseInt(getmonth)>1)
			{
				if(prevmonth!=0 && prevmonth<10){
					time = getyear+"-0"+prevmonth+"-01";
				}else{
					time = getyear+"-"+prevmonth+"-01";
				}
			}
			else
			{
				time = getyear+"-"+getmonth+"-01";
			}
		}
		getequalz0time = ts.getTimeConditon(subsetstr+"Z0","=",time);
		return getequalz0time;
	}
	/**
	 * 
	 * @param subsetstr
	 * @return
	 */
	public String getfieldstr(String subsetstr){
		String fieldstr = "";
		ArrayList fieldset = DataDictionary.getFieldList(subsetstr,Constant.USED_FIELD_SET);
		for(int i=0;i<fieldset.size();i++){
			FieldItem fielditem = (FieldItem)fieldset.get(i);	
		}
		if(fieldstr.length()>1){
			fieldstr=fieldstr.substring(0,fieldstr.length()-1);
		}
		return fieldstr;
	}
	/**
	 * 
	 * @param subsetstr 子集名称
	 * @param fieldstr  
	 * @return
	 */
	public String getcolumn(String subsetstr,String fieldstr)
	{
		String column = "";
		if(!"K".equals(subsetstr.substring(0,1).toString()))
		{
			column = fieldstr;
		}
		else
		{
			column = fieldstr;
		}
		return column;
	}


	 /**
     * 获取部门等级
     * @param dao
     * @return list
     * @throws GeneralException
     */
	public ArrayList getLevel(ContentDAO dao){
		ArrayList list = new ArrayList();
		String sql="select grade from organization where codesetid ='UN' GROUP BY grade";
		ArrayList dylist = null;
		String[] arr = {"一级","二级","三级","四级","五级","六级","七级","八级","九级","十级"};
		try {
			dylist = dao.searchDynaList(sql);
			int i=0;
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("grade").toString(),arr[i]);
				list.add(obj);
				i++;
			}
			CommonData obj1=new CommonData("0","全部");
			list.add(obj1);
		}catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获得等级sql
	 * @param grade 等级
	 * @return
	 */
	public String gradeSql(String grade){
		String gradestr = "";
		int n=Integer.parseInt(grade);
		for(int i=1;i<=n;i++){
			if(i==n){
				gradestr += i;
			}else{
				gradestr += i+",";
			}
		}
		return "("+gradestr+")";
	}
	/**
	 * 对没有记录的表的插入
	 * @param subsetstr
	 * @param id
	 * @param getz0
	 * @param time
	 * @param systime
	 * @param uv
	 * @param insertz0time
	 * @param supertable
	 * @return
	 */
	public String inserts(String subsetstr,String id,String getz0,String time,String systime, UserView uv,String insertz0time,String supertable)
	{
		String consql = "";
		consql = "insert into "+subsetstr+"("+id+","+subsetstr+"Z0,"+subsetstr+"Z1,i9999,ID,createtime,createusername)";
		consql = consql+" Select Distinct Organization.CodeItemid,"+getz0+",1,1,"+time+","+systime+",";
		consql = consql+" '"+uv.getUserName()+"' from Organization WHERE CodeItemid NOT IN (SELECT ";
		consql = consql+id+" from (select "+id+" from "+subsetstr+" WHERE "+insertz0time+") y) ";
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1)))
		{
			consql = consql+"and  CodeSetId In ('UM','UN') and CodeItemId in (select "+id+" from ";			
		}
		else
		{
			consql = consql+"and  CodeSetId='@K' and CodeItemId in (select "+id+" from ";
		}
		consql = consql+supertable+" )";
		return consql;
	}
	/**
	 * 对没有相应年月记录的表的插入
	 * @param subsetstr
	 * @param id
	 * @param getz0
	 * @param time
	 * @param systime
	 * @param uv
	 * @param insertz0time
	 * @param supertable
	 * @return
	 */
	public String insert(String subsetstr,String id,String getz0,String time,String systime, UserView uv,String insertz0time,String supertable)
	{
		String consql = "";
		consql = "insert into "+subsetstr+"("+id+","+subsetstr+"Z0,"+subsetstr+"Z1,i9999,ID,createtime,createusername)";
		consql = consql+" Select Distinct Organization.CodeItemid,"+getz0+",1,(select max(i9999) from ";
		consql = consql+subsetstr+")+1 as i9999,"+time+","+systime+",";
		consql = consql+" '"+uv.getUserName()+"' from Organization WHERE CodeItemid NOT IN (SELECT ";
		consql = consql+id+" from (select "+id+" from "+subsetstr+" WHERE "+insertz0time+") y) ";
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1)))
		{
			consql = consql+"and  CodeSetId In ('UM','UN') and CodeItemId in (select "+id+" from ";			
		}
		else
		{
			consql = consql+"and  CodeSetId='@K' and CodeItemId in (select "+id+" from ";
		}
		consql = consql+supertable+" )";
		return consql;
	}
	/**
	 * 统计项目，数据更新
	 * @param subsetstr
	 * @param cousetsb
	 * @param systime
	 * @param uv
	 * @param tablealias
	 * @param id
	 * @param tablealiasb
	 * @param wheresb
	 * @param equalz0time
	 * @param groupbysb
	 * @param countsqlsb
	 * @return
	 */
	public String conupdate(String subsetstr,StringBuffer cousetsb,String systime,UserView uv,String tablealias,String id,StringBuffer tablealiasb,StringBuffer wheresb,String equalz0time,StringBuffer groupbysb,StringBuffer countsqlsb)
	{
		String consql = "";
		consql = " update "+subsetstr+ " set "+cousetsb.toString()+",ModTime = "+systime+",ModUserName='"+uv.getUserName()+"' from ";
		String selectstr = " ( select "+tablealias+"."+id+","+tablealiasb.toString()+" from "+subsetstr+" "+tablealias;
		String constr = " where "+wheresb.toString()+" and "+equalz0time;
		String groupbystr = " group by "+tablealias+"."+id  +groupbysb.toString();
		consql = consql + selectstr+ countsqlsb.toString() +constr+groupbystr+") t where ";
		consql = consql +equalz0time+"  and "+subsetstr+"."+id+"=t."+id+" and "+subsetstr+".state=''";
		return consql;
	}
	/**
	 * 计算项目，数据更新
	 * @param subsetstr
	 * @param countsetsb
	 * @param systime
	 * @param uv
	 * @param countsb
	 * @param equalz0time
	 * @param id
	 * @return
	 */
	public String countupdate(String subsetstr,StringBuffer countsetsb,String systime,UserView uv,StringBuffer countsb,String equalz0time,String id)
	{
		String countsql = "";
		countsql = " update "+subsetstr+ " set "+countsetsb.toString()+",ModTime = "+systime+",ModUserName='"+uv.getUserName()+"' ";
		countsql= countsql+" from (select "+countsb.toString()+","+id+" from "+subsetstr;
		countsql= countsql+" where "+equalz0time+") t where ";
		countsql= countsql+ equalz0time +" and "+subsetstr+"."+id+"=t."+id+" and "+subsetstr+".state=''";
		return countsql;
	}
	/**
	 * 汇总项目，数据更新
	 * @param subsetstr
	 * @param collsetsb
	 * @param systime
	 * @param uv
	 * @param collectionsb
	 * @param id
	 * @param equalz0time
	 * @return
	 */
	public String collupdate(String subsetstr,StringBuffer collsetsb,String systime,UserView uv,StringBuffer collectionsb,String id,String equalz0time)
	{
		String collsql = "";
		collsql = " update "+subsetstr+ " set "+collsetsb.toString()+",ModTime = "+systime+",ModUserName='"+uv.getUserName()+"' ";
		collsql= collsql+" from (select "+collectionsb.toString()+","+id+" from "+subsetstr;
		collsql= collsql+" where "+equalz0time+") t where ";
		collsql= collsql+ equalz0time+"and "+subsetstr+"."+id+"=t."+id+" and "+subsetstr+".state=''";		
		return collsql;
	}
	
	/**
	 * 统计项目，求个数，多个库前缀
	 * @param y
	 * @param conum
	 * @param conditionsb
	 * @param countsqlsb
	 * @param tablealias
	 * @param conditiontempsb
	 * @param id
	 * @return
	 */
	public StringBuffer conselectedbs (int y,int conum,StringBuffer conditionsb,StringBuffer countsqlsb,String tablealias,StringBuffer conditiontempsb,String id)
	{
		if(y==1 && y!=conum) // 求个数的第一个，不是统计项目的最后一个
		{
			conditionsb.append(", ( select n."+id+",sum("+tablealias+y+")as "+tablealias+y+" from ("+conditiontempsb.toString()+" )n group by n."+id+") t"+y+" full join ");
			countsqlsb.append(conditionsb.toString());
		}
		else if(y==1 && y==conum) // 求个数的第一个，统计项目的最后一个
		{
			conditionsb.append(", ( select n."+id+",sum("+tablealias+y+")as "+tablealias+y+" from ("+conditiontempsb.toString()+" )n group by n."+id+") t"+y+" ");
			countsqlsb.append(conditionsb.toString());
		}
		else  // 不是求个数的第一个
		{
			if(y==conum) // 不是求个数的第一个，是统计项目的最后一个
			{
				int tempy = y-1;
				conditionsb.append(" ( select n."+id+",sum("+tablealias+y+")as "+tablealias+y+" from ("+conditiontempsb.toString() );
				conditionsb.append(" )n group by n."+id+") t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" " );
				countsqlsb.append(conditionsb.toString());
			}
			else // 不是求个数的第一个，不是统计项目的最后一个
			{
				int tempy = y-1;
				conditionsb.append(" ( select n."+id+",sum("+tablealias+y+")as "+tablealias+y+" from ("+conditiontempsb.toString() );
				conditionsb.append(" )n group by n."+id+") t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" full join ");
				countsqlsb.append(conditionsb.toString());
			}
		}
//		System.out.println(countsqlsb.toString());
		return countsqlsb;
	}
	/**
	 * 统计项目，求个数，一个库前缀
	 * @param y
	 * @param conum
	 * @param conditionsb
	 * @param countsqlsb
	 * @param tablealias
	 * @param id
	 * @param fieldcondition
	 * @param privsql
	 * @return
	 */
	public StringBuffer conselectedb (String subsetstr,int y,int conum,StringBuffer conditionsb,
			StringBuffer countsqlsb,String tablealias,String id,String fieldcondition,String fieldconditiontwo,String privsql)
	{
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))) // 单位表
		{
			if(y==1 && y!=conum) // 求个数的第一个，不是统计项目的最后一个
			{
				conditionsb.append(", ( select count(*)as "+tablealias+y+"," +id+" " +privsql);
				conditionsb.append(fieldcondition+" group by "+id+" union ");
				conditionsb.append("  select count(*)as "+tablealias+y+",e0122 " +privsql);
				conditionsb.append(fieldconditiontwo+" group by e0122 ) t"+y+" full join ");
				countsqlsb.append(conditionsb.toString());
			}
			else if(y==1 && y==conum) // 求个数的第一个，统计项目的最后一个
			{
				conditionsb.append(", ( select count(*)as "+tablealias+y+"," +id+" " +privsql);
				conditionsb.append(fieldcondition+" group by "+id+" union ");
				conditionsb.append(" select count(*)as "+tablealias+y+",e0122 " +privsql);
				conditionsb.append(fieldconditiontwo+" group by e0122 ) t"+y+" ");
				countsqlsb.append(conditionsb.toString());
			}
			else // 不是求个数的第一个
			{
				if(y==conum) // 不是求个数的第一个，是统计项目的最后一个
				{
					int tempy = y-1;
					conditionsb.append(" ( select count(*)as "+tablealias+y+"," +id+" " +privsql);
					conditionsb.append(fieldcondition+" group by "+id+" union " );
					conditionsb.append("  select count(*)as "+tablealias+y+",e0122 " +privsql);
					conditionsb.append(fieldconditiontwo+" group by e0122 ) t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" " );
					countsqlsb.append(conditionsb.toString());
				}
				else // 不是求个数的第一个，不是统计项目的最后一个
				{
					int tempy = y-1;
					conditionsb.append(" ( select count(*)as "+tablealias+y+"," +id+" " +privsql );
					conditionsb.append(fieldcondition+" group by "+id+" union ");
					conditionsb.append("  select count(*)as "+tablealias+y+",e0122 " +privsql );
					conditionsb.append(fieldcondition+" group by e0122 ) t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" full join ");
					countsqlsb.append(conditionsb.toString());
				}
			}
		}
		else
		{
			if(y==1 && y!=conum) // 求个数的第一个，不是统计项目的最后一个
			{
				conditionsb.append(", ( select count(*)as "+tablealias+y+"," +id+" " +privsql);
				conditionsb.append(fieldcondition+" group by "+id+" ) t"+y+" full join ");
				countsqlsb.append(conditionsb.toString());
			}
			else if(y==1 && y==conum) // 求个数的第一个，统计项目的最后一个
			{
				conditionsb.append(", ( select count(*)as "+tablealias+y+"," +id+" " +privsql);
				conditionsb.append(fieldcondition+" group by "+id+" ) t"+y+" ");
				countsqlsb.append(conditionsb.toString());
			}
			else // 不是求个数的第一个
			{
				if(y==conum) // 不是求个数的第一个，是统计项目的最后一个
				{
					int tempy = y-1;
					conditionsb.append(" ( select count(*)as "+tablealias+y+"," +id+" " +privsql);
					conditionsb.append(fieldcondition+" group by "+id+" ) t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" " );
					countsqlsb.append(conditionsb.toString());
				}
				else // 不是求个数的第一个，不是统计项目的最后一个
				{
					int tempy = y-1;
					conditionsb.append(" ( select count(*)as "+tablealias+y+"," +id+" " +privsql );
					conditionsb.append(fieldcondition+" group by "+id+" ) t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" full join ");
					countsqlsb.append(conditionsb.toString());
				}
			}
		}
		
//		System.out.println(countsqlsb.toString());
		return countsqlsb;
	}
	
	
	/**
	 * 统计项目，求和，求最大，最小，平均值，多个库前缀
	 * @param y
	 * @param conum
	 * @param conditionsb
	 * @param countsqlsb
	 * @param tablealias
	 * @param conditiontempsb
	 * @param id
	 * @return
	 */
	public StringBuffer conselectedbss(int y,int conum,StringBuffer sumconformulasb,StringBuffer sumconformulatempsb,String id,String operstr,String tablealias,StringBuffer countsqlsb)
	{
		if(y==1 && y!=conum) // 求和,求最大,最小,平均值的第一个，不是统计项目的最后一个
		{
			sumconformulasb.append(", ( select w."+id+","+operstr+"(w.f"+y+") as "+tablealias+y+" from (");								
			sumconformulasb.append(sumconformulatempsb.toString()+")"+"w group by w."+id+") t"+y+" full join " );
			countsqlsb.append(sumconformulasb.toString());
		}
		else if(y==1 && y==conum) // 求和,求最大,最小,平均值的第一个，并且统计项目的最后一个
		{
			sumconformulasb.append(",( select w."+id+","+operstr+"(w.f"+y+") as "+tablealias+y+" from (");	
			sumconformulasb.append(sumconformulatempsb.toString()+")"+"w group by w."+id+") t"+y+" " );
			countsqlsb.append(sumconformulasb.toString());
		}
		else 
		{
			if(y==conum) // 不是求和,求最大,最小,平均值的第一个，是统计项目的最后一个
			{
				int tempy = y-1;
				sumconformulasb.append("( select w."+id+","+operstr+"(w.f"+y+") as "+tablealias+y+" from (");									
				sumconformulasb.append(sumconformulatempsb.toString()+")"+"w group by w."+id+") t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" " );
				countsqlsb.append(sumconformulasb.toString());
			}
			else // 不是求和,求最大,最小,平均值的第一个，不是统计项目的最后一个
			{
				int tempy = y-1;
				sumconformulasb.append("( select w."+id+","+operstr+"(w.f"+y+") as "+tablealias+y+" from (");
				sumconformulasb.append(sumconformulatempsb.toString()+")"+"w group by w."+id+") t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" full join " );
				countsqlsb.append(sumconformulasb.toString());
			}
		}
//		System.out.println(countsqlsb.toString());
		return countsqlsb;
	}
	
	/**
	 * 获得要进行的操作 eg: 求个数，求和 等等
	 * @param operstr
	 * @return
	 */
	public String getoperstr(String operstr)
	{
		if("0".equals(operstr))
		{
			operstr = "count";
		}
		if("1".equals(operstr))
		{
			operstr = "sum";
		}
		else if("2".equals(operstr))
		{
			operstr = "min";
		}
		else if("3".equals(operstr))
		{
			operstr = "max";
		}
		else if("4".equals(operstr))
		{
			operstr = "avg";
		}
		return operstr;
	}
	/**
	 * 获得数据类型
	 * @param fieldtype
	 * @param varType
	 * @return
	 */
	public int getvarType(String fieldtype,int varType)
	{
		if ("D".equals(fieldtype)) {
            varType = 9;
        } else if ("A".equals(fieldtype) || "M".equals(fieldtype)) {
            varType = 7;
        } else if ("N".equals(fieldtype)) {
            varType = 6;
        } else {
            varType = 6;
        }
		return varType;
	}
	
	/**
	 * 统计项目，求个数，多个库前缀，对多个库处理
	 * @param t
	 * @param index
	 * @param conditiontempsb
	 * @param tablealias
	 * @param id
	 * @param privsql
	 * @param expression
	 * @param y
	 * @return
	 */
	public StringBuffer conselectsingle(String subsetstr,int t,int index,StringBuffer conditiontempsb,String tablealias,String id,String privsql,String expression,int y)
	{		
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))) // 单位表
		{
			if(t!=index-1) // 不是最后一个库前缀
			{
				conditiontempsb.append(" select count(*)as "+tablealias+y+"," +id+" " +privsql);
				if(expression == null || "".equals(expression))
				{
					conditiontempsb.append("  group by "+id+" union ");
					conditiontempsb.append(" select count(*)as "+tablealias+y+",e0122 " +privsql);
					conditiontempsb.append(" where e0122 not like ''  group by e0122 union all ");
				}
				else
				{
					conditiontempsb.append(" and "+id+" not like ''  group by "+id+" union  ");
					conditiontempsb.append(" select count(*)as "+tablealias+y+",e0122 " +privsql);
					conditiontempsb.append(" and e0122 not like ''  group by e0122 union all ");
				}				
			}
			else // 是最后一个库前缀
			{
				conditiontempsb.append(" select count(*)as "+tablealias+y+"," +id+" " +privsql);
				if(expression == null || "".equals(expression))
				{
					conditiontempsb.append("  group by "+id+" union ");
					conditiontempsb.append(" select count(*)as "+tablealias+y+",e0122 " +privsql);
					conditiontempsb.append(" where e0122 not like ''  group by e0122 ");
				}
				else
				{
					conditiontempsb.append(" and "+id+" not like ''  group by "+id+" union ");
					conditiontempsb.append(" select count(*)as "+tablealias+y+",e0122 " +privsql);
					conditiontempsb.append(" and e0122 not like ''  group by e0122 ");
				}									
			}
		}
		else  // 职位表
		{
			if(t!=index-1) // 不是最后一个库前缀
			{
				conditiontempsb.append(" select count(*)as "+tablealias+y+"," +id+" " +privsql);
				if(expression == null || "".equals(expression))
				{
					conditiontempsb.append("  group by "+id+" union all ");
				}
				else
				{
					conditiontempsb.append(" and "+id+" not like ''  group by "+id+" union all ");
				}
				
			}
			else // 是最后一个库前缀
			{
				conditiontempsb.append(" select count(*)as "+tablealias+y+"," +id+" " +privsql);
				if(expression == null || "".equals(expression))
				{
					conditiontempsb.append("  group by "+id+" ");
				}
				else
				{
					conditiontempsb.append(" and "+id+" not like ''  group by "+id+" ");
				}									
			}
		}
		
//		System.out.println(conditiontempsb.toString());
		return conditiontempsb;
		
	}
	/**
	 * 统计项目，求和，求最大，最小，平均值，一个库前缀
	 * @param y
	 * @param conum
	 * @param sumconformulasb
	 * @param sumconformulatempsb
	 * @param id
	 * @param operstr
	 * @param tablealias
	 * @param dbname
	 * @param countsqlsb
	 * @return
	 */
	public StringBuffer condbssingle( int y,int conum,StringBuffer sumconformulasb,StringBuffer sumconformulatempsb,String id,String operstr,String tablealias,String dbname,StringBuffer countsqlsb)
	{
		if(y==1 && y!=conum) // 求和,求最大,最小,平均值的第一个，不是统计项目的最后一个
		{
			sumconformulasb.append(",( select a."+id+","+operstr+"(s"+y+"."+tablealias+y+")"+" as "+tablealias+y+" from "+dbname+"a01 a ");
			sumconformulasb.append(sumconformulatempsb.toString());
//			sumconformulasb.append(" full join " );
			countsqlsb.append(sumconformulasb.toString());
		}
		else if(y==1 && y==conum) // 求和,求最大,最小,平均值的第一个，并且统计项目的最后一个
		{
			sumconformulasb.append(",( select a."+id+","+operstr+"(s"+y+"."+tablealias+y+")"+" as "+tablealias+y+" from "+dbname+"a01 a ");
			sumconformulasb.append(sumconformulatempsb.toString());
//			sumconformulasb.append(") t"+y);
			countsqlsb.append(sumconformulasb.toString());
		}
		else
		{
			if(y==conum)// 不是求和,求最大,最小,平均值的第一个，是统计项目的最后一个
			{
				int tempy = y-1;
				sumconformulasb.append("( select a."+id+","+operstr+"(s"+y+"."+tablealias+y+")"+" as "+tablealias+y+" from "+dbname+"a01 a ");
				sumconformulasb.append(sumconformulatempsb.toString());
//				sumconformulasb.append(") t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" " );
				countsqlsb.append(sumconformulasb.toString());
			}
			else // 不是求和,求最大,最小,平均值的第一个，不是统计项目的最后一个
			{
				int tempy = y-1;
				sumconformulasb.append("( select a."+id+","+operstr+"(s"+y+"."+tablealias+y+")"+" as "+tablealias+y+" from "+dbname+"a01 a ");
				sumconformulasb.append(sumconformulatempsb.toString());
//				sumconformulasb.append(") t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" full join " );
				countsqlsb.append(sumconformulasb.toString());
			}
		}
//		System.out.println(countsqlsb.toString());
		return countsqlsb;
	}
	/**
	 * 统计项目，求和，求最大，最小，平均值，一个库前缀
	 * @param y
	 * @param conum
	 * @param sumconformulatempsb
	 * @param id
	 * @param operstr
	 * @param tablealias
	 * @param dbname
	 * @param countsqlsb
	 * @return
	 */
	public StringBuffer condbssingletwo(int y,int conum,StringBuffer sumconformulatempsb,String id,String operstr,String tablealias,String dbname,StringBuffer countsqlsb)
	{
		StringBuffer sumconformulasb = new StringBuffer();
		if(y==1 && y!=conum) // 求和,求最大,最小,平均值的第一个，不是统计项目的最后一个
		{
			sumconformulasb.append(" union select a.e0122,"+operstr+"(s"+y+"."+tablealias+y+")"+" as "+tablealias+y+" from "+dbname+"a01 a ");
			sumconformulasb.append(sumconformulatempsb.toString());
			sumconformulasb.append(") t"+y+" full join " );
			countsqlsb.append(sumconformulasb.toString());
		}
		else if(y==1 && y==conum) // 求和,求最大,最小,平均值的第一个，并且统计项目的最后一个
		{
			sumconformulasb.append(" union select a.e0122,"+operstr+"(s"+y+"."+tablealias+y+")"+" as "+tablealias+y+" from "+dbname+"a01 a ");
			sumconformulasb.append(sumconformulatempsb.toString());
				sumconformulasb.append(") t"+y);
			countsqlsb.append(sumconformulasb.toString());
		}
		else
		{
			if(y==conum)// 不是求和,求最大,最小,平均值的第一个，是统计项目的最后一个
			{
				int tempy = y-1;
				sumconformulasb.append(" union select a.e0122,"+operstr+"(s"+y+"."+tablealias+y+")"+" as "+tablealias+y+" from "+dbname+"a01 a ");
				sumconformulasb.append(sumconformulatempsb.toString());
				sumconformulasb.append(") t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" " );
				countsqlsb.append(sumconformulasb.toString());
			}
			else // 不是求和,求最大,最小,平均值的第一个，不是统计项目的最后一个
			{
				int tempy = y-1;
				sumconformulasb.append(" union select a.e0122,"+operstr+"(s"+y+"."+tablealias+y+")"+" as "+tablealias+y+" from "+dbname+"a01 a ");
				sumconformulasb.append(sumconformulatempsb.toString());
				sumconformulasb.append(") t"+y+" on t"+tempy+"."+id+"=t"+y+"."+id+" full join " );
				countsqlsb.append(sumconformulasb.toString());
			}
		}
//		System.out.println(countsqlsb.toString());
		return countsqlsb;
	}
	/**
	 * 统计项目，求和，求最大，最小，平均值，多个库前缀，对多个库处理
	 * @param t
	 * @param index
	 * @param id
	 * @param y
	 * @param tablealias
	 * @param dbnamestr
	 * @param sumconformulatempsb
	 * @param conformulastr
	 * @param congroupbystr
	 * @param conformulatabstr
	 * @param countsbstr
	 * @param conwherestr
	 * @param privsql
	 * @return
	 */
	public StringBuffer conselectkinds(int t,int index, String id,int y,String tablealias,String dbnamestr, StringBuffer sumconformulatempsb,ArrayList conformulastr,ArrayList congroupbystr,ArrayList conformulatabstr,String countsbstr,ArrayList conwherestr,String privsql) 
	{
		if(t!=index-1) // 不是最后一个库前缀
		{									
			sumconformulatempsb.append(" select a."+id+","+Sql_switcher.sqlNull("s"+y+"."+tablealias+y,0)+" as f"+y+" from "+dbnamestr+"a01 a ");
			sumconformulatempsb.append(",( select "+countsbstr+conformulastr.get(t)+" from "+conformulatabstr.get(t));
			sumconformulatempsb.append(" ( select "+dbnamestr+"a01.a0100 "+privsql+" ) x"+y);
			sumconformulatempsb.append(" where "+ conwherestr.get(t));
			sumconformulatempsb.append(" group by "+congroupbystr.get(t)+") as s"+y);
			sumconformulatempsb.append(" where a.a0100 = s"+y+".a0100 and "+id+" not like '' ");
			//sumconformulatempsb.append(" group by a."+id+",s"+y+"."+tablealias+y+" union all " );
//			sumconformulatempsb.append(" union all " );
		}
		else  // 是最后一个库前缀
		{
			sumconformulatempsb.append(" select a."+id+","+Sql_switcher.sqlNull("s"+y+"."+tablealias+y,0)+" as f"+y+" from "+dbnamestr+"a01 a ");
			sumconformulatempsb.append(",( select "+countsbstr+conformulastr.get(t)+" from "+conformulatabstr.get(t));
			sumconformulatempsb.append(" ( select "+dbnamestr+"a01.a0100 "+privsql+" ) x"+y);
			sumconformulatempsb.append(" where "+ conwherestr.get(t));
			sumconformulatempsb.append(" group by "+congroupbystr.get(t)+") as s"+y);
			sumconformulatempsb.append(" where a.a0100 = s"+y+".a0100 and "+id+" not like '' ");
			//sumconformulatempsb.append(" group by a."+id+",s"+y+"."+tablealias+y );
		}
//		System.out.println(sumconformulatempsb.toString());
		return sumconformulatempsb;
	}
	/**
	 * 统计项目，求和，求最大，最小，平均值，多个库前缀，对多个库处理
	 * @param t
	 * @param index
	 * @param id
	 * @param y
	 * @param tablealias
	 * @param dbnamestr
	 * @param sumconformulatempsb
	 * @param conformulastr
	 * @param congroupbystr
	 * @param conformulatabstr
	 * @param countsbstr
	 * @param conwherestr
	 * @param privsql
	 * @return
	 */
	public StringBuffer conselectkindstwo(int t,int index, String id,int y,String tablealias,String dbnamestr, StringBuffer sumconformulatempsb,ArrayList conformulastr,ArrayList congroupbystr,ArrayList conformulatabstr,String countsbstr,ArrayList conwherestr,String privsql) 
	{
		if(t!=index-1) // 不是最后一个库前缀
		{									
			sumconformulatempsb.append(" union all select a.e0122,"+Sql_switcher.sqlNull("s"+y+"."+tablealias+y,0)+" as f"+y+" from "+dbnamestr+"a01 a ");
			sumconformulatempsb.append(",( select "+countsbstr+conformulastr.get(t)+" from "+conformulatabstr.get(t));
			sumconformulatempsb.append(" ( select "+dbnamestr+"a01.a0100 "+privsql+" ) x"+y);
			sumconformulatempsb.append(" where "+ conwherestr.get(t));
			sumconformulatempsb.append(" group by "+congroupbystr.get(t)+") as s"+y);
			sumconformulatempsb.append(" where a.a0100 = s"+y+".a0100 and e0122 not like '' ");
			//sumconformulatempsb.append(" group by a."+id+",s"+y+"."+tablealias+y+" union all " );
			sumconformulatempsb.append(" union all " );
		}
		else  // 是最后一个库前缀
		{
			sumconformulatempsb.append(" union all select a.e0122,"+Sql_switcher.sqlNull("s"+y+"."+tablealias+y,0)+" as f"+y+" from "+dbnamestr+"a01 a ");
			sumconformulatempsb.append(",( select "+countsbstr+conformulastr.get(t)+" from "+conformulatabstr.get(t));
			sumconformulatempsb.append(" ( select "+dbnamestr+"a01.a0100 "+privsql+" ) x"+y);
			sumconformulatempsb.append(" where "+ conwherestr.get(t));
			sumconformulatempsb.append(" group by "+congroupbystr.get(t)+") as s"+y);
			sumconformulatempsb.append(" where a.a0100 = s"+y+".a0100 and e0122 not like '' ");
			//sumconformulatempsb.append(" group by a."+id+",s"+y+"."+tablealias+y );
		}
//		System.out.println(sumconformulatempsb.toString());
		return sumconformulatempsb;
	}
	/**
	 * 载入上期数据
	 * @param subsetstr
	 * @param dao
	 * @param str
	 * @param getyear
	 * @param getmonth
	 * @param changeflag
	 */
	public void loadPrevData(String subsetstr,ContentDAO dao,String fieldstr,String getyear,String getmonth,String changeflag)
	{
		String id = "";
		String time = this.getprevtime( subsetstr, getyear, getmonth, changeflag);
		StringBuffer loadsql = new StringBuffer();
		fieldstr = fieldstr.substring(0,fieldstr.length()-1);
		String[] field = null;
		field = fieldstr.split(",");
		String timecondition = getequalz0time( subsetstr, getyear, getmonth, changeflag);
		StringBuffer setsb = new StringBuffer();
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))){
			id = "B0110";        // 单位表的连接字段
		}
		else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
			id = "E01A1";         // 职位表的连接字段
		}
		for(int i=0;i<field.length;i++)
		{
			setsb.append(","+subsetstr+"."+field[i].toString()+"=t."+field[i].toString());
		}
		
		loadsql.append(" Update "+subsetstr+" set "+setsb.substring(1));
		loadsql.append(" from "+subsetstr+" left join ( select "+id+",");
		loadsql.append(fieldstr+" from "+subsetstr+" where "+time+") as t");
		loadsql.append(" on "+subsetstr+"."+id+"=t."+id+" where ");
		loadsql.append(timecondition);
		System.out.println(loadsql.toString());
		try
		{
			dao.update(loadsql.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	/**
	 * 获得表相关信息
	 * @param subsetstr
	 * @param id
	 * @param tablealias
	 * @param supertable
	 */
	public void getableinfo(String subsetstr,String id,String tablealias,String supertable)
	{
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))){
			id = "B0110";        // 单位表的连接字段
			tablealias = "b";	 // 单位表的别名		
			supertable = "B01";
		}
		else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
			id = "E01A1";         // 职位表的连接字段
			tablealias = "k";	  // 职位表的别名		
			supertable = "K01";
		}
	}
	/**
	 * 逐级汇总的部分SQL
	 * @param subsetstr
	 * @param sum
	 * @param y
	 * @param equalz0time
	 * @return
	 */
	public String getcontent(String subsetstr,String sum,int y,String equalz0time)
	{
		StringBuffer retsql = new StringBuffer();
		retsql.append(",(select "+sum+" from "+subsetstr+" a where a.b0110 in ");
		retsql.append(" ( select codeitemid from organization where codeitemid ");
		retsql.append(" like (b.b0110+'%') and (grade=(select grade from organization ");
		retsql.append(" where codeitemid=b.b0110)+1 and (codesetid='UN') or ");
		retsql.append(" grade=(select grade from organization where codeitemid=b.b0110)");
		retsql.append(" and (codesetid='UM' or codesetid='UN')))  and "+equalz0time);
		retsql.append(" and state is  null) as t"+y  );
		return retsql.toString();
	}
	/**
	 * 逐级汇总的SQL
	 * @param subsetstr
	 * @param contentsetsb
	 * @param contentsb
	 * @param equalz0time
	 * @return
	 */
	public String getcontentsql(String subsetstr,StringBuffer contentsetsb,StringBuffer contentsb,String equalz0time)
	{
		StringBuffer retsql = new StringBuffer();
		retsql.append(" update "+subsetstr+" set "+contentsetsb.substring(1).toString()+" from ");
		retsql.append("( select b0110 "+contentsb.toString()+" from ");
		retsql.append(subsetstr+" b where "+equalz0time+" and state is null" );
		retsql.append(")t where "+subsetstr+".b0110=t.b0110 and" );
		retsql.append(equalz0time );
		return retsql.toString();
	}

	
}
