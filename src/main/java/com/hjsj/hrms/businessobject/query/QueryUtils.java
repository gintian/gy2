package com.hjsj.hrms.businessobject.query;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryUtils {
	Connection conn;
	
	//zxj 20150708 系统支持的几种日期格式
    SimpleDateFormat formatY = new SimpleDateFormat("yyyy");
    SimpleDateFormat formatYM = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatYMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat formatYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	public  QueryUtils() {
		
	}
	
	public QueryUtils(Connection conn) {
		this.conn=conn;
	}
	
	public String getCodeValue(FieldItem item) {
		if(item==null) {
            return "";
        }
		String re="";
		if(!"0".equals(item.getCodesetid()))
		{
			String q_v=item.getValue();
			
			CodeItem code=AdminCode.getCode(item.getCodesetid(),q_v);
			if(code==null)
			//if((q_v==null||q_v.equals("")))
			{
				String v_v=item.getViewvalue();
				if(v_v!=null&&v_v.length()>0)
				{
					re=AdminCode.getCodeName(item.getCodesetid(),v_v);
					
				}
			}
		}
		return re;
	}
	/**
	 * 得到单位，职位，人员子集列表
	 * @param infokind
	 * @return
	 */
	public static ArrayList getFieldSetListByInfokind(int infokind,UserView userView)
	{
		ArrayList list = new ArrayList();
		try
		{
			ArrayList fieldsetlist = null;
			if(infokind==Constant.EMPLOY_FIELD_SET) {
                fieldsetlist = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);//DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
            } else if(infokind==Constant.UNIT_FIELD_SET) {
                fieldsetlist = userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);//DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
            } else if(infokind==Constant.POS_FIELD_SET) {
                fieldsetlist = userView.getPrivFieldSetList(Constant.POS_FIELD_SET);//DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);
            } else {
                fieldsetlist = new ArrayList();
            }
			for(int i=0;i<fieldsetlist.size();i++)
			{
				FieldSet set=(FieldSet)fieldsetlist.get(i);
				if("A00".equalsIgnoreCase(set.getFieldsetid())|| "K00".equalsIgnoreCase(set.getFieldsetid())|| "B00".equalsIgnoreCase(set.getFieldsetid())||!"1".equals(set.getUseflag())) {
                    continue;
                }
				CommonData cd= new CommonData();
				cd.setDataName(set.getCustomdesc());
				cd.setDataValue(set.getFieldsetid());
				list.add(cd);
			}
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public static ArrayList getFieldItemList(String fieldsetid,UserView userView)
	{
		ArrayList list = new ArrayList();
		ArrayList fieldlist=userView.getPrivFieldList(fieldsetid.toUpperCase(), Constant.USED_FIELD_SET);
		
		if(fieldlist!=null)
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fielditem = (FieldItem)fieldlist.get(i);
				CommonData cd = new CommonData();
				cd.setDataName(fielditem.getItemdesc());
				cd.setDataValue(fielditem.getItemid());
				list.add(cd);
			}
		}
		return list;
	}

	public String exportExcel(String infokind,String fields,String isHistory,UserView userView,String querytype,ArrayList dbpreList) {
	    return exportExcel(infokind, fields, isHistory, userView, querytype, dbpreList, "");
	}
	
	public String exportExcel(String infokind,String fields,String isHistory,UserView userView,
	        String querytype,ArrayList dbpreList, String whereStr) {
		String fileName= userView.getUserName() + "_hr.xls";
		//boolean selectsubset = false;   //选中的指标中有无子集内指标，如果有子集内指标，则selectsubset为true，没有则为false  赵国栋 2013-11-05
		String orderByI9999="";//tiany 田野修改使用orderByI9999代替selectsubset直接拼写排序的i9999 
		RowSet rs = null;
		RowSet rowSet = null;
		DbWizard db = new DbWizard(this.conn);
		try {
			if(fields!=null&&fields.trim().length()>0) {
				ContentDAO dao = new ContentDAO(this.conn);
				rowSet=dao.search("select dbid,pre from dbname order by dbid");
				HashMap dbidMap = new HashMap();
				boolean fal = false;
				if(dbpreList.get(0)==null||"".equals(dbpreList.get(0))||dbpreList.get(0)==""){
					if(dbpreList.size()==1) {
                        dbpreList.removeAll(dbpreList);
                    }
					
					fal = true;
				}
				
				while(rowSet.next()) {
					if(fal) {
                        dbpreList.add(rowSet.getString("pre").toUpperCase());
                    }
					
					dbidMap.put(rowSet.getString("pre").toUpperCase(), rowSet.getString("dbid"));
				}
				
				String[] arr=fields.split("`");
				StringBuffer buf = new StringBuffer();
				ArrayList itemList = new ArrayList();
				if("1".equals(infokind)) {
					/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 start */
					// 外层select查询
					StringBuffer outerSelect = new StringBuffer();
					/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 end */
					for(int j=0;j<dbpreList.size();j++) {
						
						String dbpre=(String)dbpreList.get(j);
						StringBuffer select = new StringBuffer(" select "+Sql_switcher.isnull(dbpre+"A01.a0000", "99999")+" as a0000,"+((String)dbidMap.get(dbpre.toUpperCase()))+" as dbid,");
						
						/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 start */
						if(j == 0) {
                            outerSelect.append("select a0000, dbid,");
                        }
						/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 end */
						
						HashMap setMap = new HashMap();
						setMap.put("A01", "1");
						StringBuffer from  = new StringBuffer(" from "+dbpre+"A01 ");
						StringBuffer where = new StringBuffer();
						if(userView.getStatus()==4) {
							String tabldName = "t_sys_result";
							Table table = new Table(tabldName);
							DbWizard dbWizard = new DbWizard(conn);
							if (!dbWizard.isExistTable(table)) {
                                where.append(" where 1=2 ");
                            } else {
                                where.append(" where "+dbpre+"A01.A0100 in (select obj_id from t_sys_result where flag=0 and UPPER(username)='"+userView.getUserName().toUpperCase()+"' and UPPER(nbase)='"+dbpre.toUpperCase()+"')");
                            }
							
						} else {
                            where.append(" where "+dbpre+"A01.A0100 in (select a0100 from "+userView.getUserName()+dbpre+"result) ");
                        }
						
						/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 start */
						// i9999下标排序标号
		        		int index = 0;
						/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 end */
						
			        	for(int i=0;i<arr.length;i++) {
			        		if(arr[i]==null|| "".equals(arr[i])) {
                                continue;
                            }
			        		
				          	FieldItem item = DataDictionary.getFieldItem(arr[i].toLowerCase());
				        	if(item!=null) {
				        		if(j==0) {
                                    itemList.add(item);
                                }
				        		/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 start */
				        		//select.append(dbpre+item.getFieldsetid()+"."+item.getItemid()+",");
				        		select.append(item.getItemid()+",");
				        		if(j == 0) {
                                    outerSelect.append(item.getItemid()+",");
                                }
				        		/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 end */
				        		if(setMap.get(item.getFieldsetid().toUpperCase())==null) {
				        			//selectsubset = true;
				        			/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 start */
				        			//orderByI9999+=","+dbpre+item.getFieldsetid()+".i9999";
				        			select.append(dbpre + item.getFieldsetid()+".i9999 as i9999_" + index + ",");
				        			if(j == 0) {
				        				orderByI9999 += ",i9999_" + index;
				        			}
				        			
				        			index++;
				        			/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 end */
				        			from.append(" left join "+dbpre+item.getFieldsetid() + " on "+dbpre+"A01.A0100="+dbpre+item.getFieldsetid()+".A0100");
				        			if("2".equals(isHistory) || "3".equals(isHistory)) {
				        			    String strWhere = "1=1";
				        			    if(StringUtils.isNotEmpty(whereStr)) {
                                            strWhere = whereStr.replace(item.getFieldsetid() + ".", dbpre + item.getFieldsetid() + ".");
                                        }
				        			    
				        			    where.append(" and ("+dbpre+item.getFieldsetid()+".I9999 in (select I9999 from "+dbpre+item.getFieldsetid());
				        			    where.append(" where " + strWhere);
                                        where.append(" and "+dbpre+item.getFieldsetid()+".A0100="+dbpre+"A01.A0100))");
				        			} else {
				        			    where.append(" and ("+dbpre+item.getFieldsetid()+".I9999=(select max(I9999) from "+dbpre+item.getFieldsetid());
				        			    where.append(" where "+dbpre+item.getFieldsetid()+".A0100="+dbpre+"A01.A0100) or "+dbpre+item.getFieldsetid()+".I9999 is null)");
				        			}
				        			
				        			setMap.put(item.getFieldsetid().toUpperCase(), "1");
				        		}
				        	}
	    		    	}
			        	/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 start */
			        	//select.setLength(select.length()-1);
			        	if(j == 0) {
			        		outerSelect.deleteCharAt(outerSelect.length() - 1);
			        		outerSelect.append(" from ");
			        		if(orderByI9999.endsWith(",")) {
			        			orderByI9999 = orderByI9999.substring(0, orderByI9999.length() - 1);
			        		}
			        	}
			        	if(select.toString().endsWith(",")) {
			        		select.deleteCharAt(select.length() - 1);
			        	}
			        	/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 end */
			        	
			        	buf.append(select.toString()+from+where);
			        	if(j!=dbpreList.size()-1) {
                            buf.append(" union all ");
                        }
					}
					String str=buf.toString();
					buf.setLength(0);
					/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 start */
					buf.append(outerSelect).append("(").append(str+") A order by dbid,a0000");
					/* 标识：1802 全部人员库 选择了主集和子集 导出excel问题(V62发现的，VX上同步修改) xiaoyun 2014-5-23 end */
				}
				else if("2".equals(infokind))
				{

					StringBuffer select = new StringBuffer(" select "+Sql_switcher.isnull("T.a0000", "99999")+" as a0000,");
					HashMap setMap = new HashMap();
					setMap.put("B01", "1");
					StringBuffer from  = new StringBuffer(" from B01 left join (select codeitemid, a0000 from organization where (codesetid='UM' or codesetid='UN')) T on B01.b0110=T.codeitemid ");
					StringBuffer where = new StringBuffer();
					if(userView.getStatus()==4)
					{
						String tabldName = "t_sys_result";
						Table table = new Table(tabldName);
						DbWizard dbWizard = new DbWizard(conn);
						if (!dbWizard.isExistTable(table)) {
							where.append(" where 1=2 ");
						}
						else
						{
							where.append(" where B01.B0110 in (select obj_id from t_sys_result where flag=1 and UPPER(username)='"+userView.getUserName().toUpperCase()+"')");
						}
					}
					else
					{
						where.append(" where B01.B0110 in (select B0110 from "+userView.getUserName()+"Bresult) ");
					}
		        	for(int i=0;i<arr.length;i++)
		        	{
		        		if(arr[i]==null|| "".equals(arr[i])) {
                            continue;
                        }
			          	FieldItem item = DataDictionary.getFieldItem(arr[i].toLowerCase());
			        	if(item!=null)
			        	{
			        		itemList.add(item);
			        		String fieldsetid=item.getFieldsetid().toUpperCase();
			        		if("A01".equalsIgnoreCase(fieldsetid)) {
                                fieldsetid="B01";
                            }
			        		select.append(fieldsetid+"."+item.getItemid()+",");
			        		
			        		if(setMap.get(fieldsetid)==null)
			        		{
			        			//selectsubset = true;
			        			orderByI9999+=","+item.getFieldsetid()+".i9999";
			        			from.append(" left join "+item.getFieldsetid()+" on B01.B0110="+item.getFieldsetid()+".B0110");
			        			if("0".equals(isHistory))
			        			{
			        				where.append(" and ("+item.getFieldsetid()+".I9999=(select max(I9999) from "+item.getFieldsetid());
			        				where.append(" where "+item.getFieldsetid()+".B0110=B01.B0110) or "+item.getFieldsetid()+".I9999 is null)");
			        			}
			        			setMap.put(item.getFieldsetid().toUpperCase(), "1");
			        		}
			        	}
		        	}
		        	select.setLength(select.length()-1);
		        	buf.append(select.toString()+from+where+" order by a0000");
				}
				else if("3".equals(infokind))
				{
					StringBuffer select = new StringBuffer(" select "+Sql_switcher.isnull("T.a0000", "a0000")+" as a0000,");
					HashMap setMap = new HashMap();
					setMap.put("K01", "1");
					StringBuffer from  = new StringBuffer(" from K01 left join (select codeitemid, a0000 from organization where codesetid='@K') T on K01.e01a1=T.codeitemid ");
					StringBuffer where = new StringBuffer();
					if(userView.getStatus()==4)
					{
						String tabldName = "t_sys_result";
						Table table = new Table(tabldName);
						DbWizard dbWizard = new DbWizard(conn);
						if (!dbWizard.isExistTable(table)) {
							where.append(" where 1=2 ");
						}
						else
						{
							where.append(" where K01.E01A1 in (select obj_id from t_sys_result where flag=3 and UPPER(username)='"+userView.getUserName().toUpperCase()+"')");
						}
					}
					else
					{
						where.append(" where K01.E01A1 in (select E01A1 from "+userView.getUserName()+"Kresult) ");
					}
		        	for(int i=0;i<arr.length;i++)
		        	{
		        		if(arr[i]==null|| "".equals(arr[i])) {
                            continue;
                        }
			          	FieldItem item = DataDictionary.getFieldItem(arr[i].toLowerCase());
			        	if(item!=null)
			        	{
			        		itemList.add(item);
			        		String fieldsetid=item.getFieldsetid().toUpperCase();
			        		if("A01".equalsIgnoreCase(fieldsetid)|| "B01".equalsIgnoreCase(fieldsetid)) {
                                fieldsetid="K01";
                            }
			        		select.append(fieldsetid+"."+item.getItemid()+",");
			        		
			        		if(setMap.get(fieldsetid)==null)
			        		{
			        		  //selectsubset = true;
			        			orderByI9999+=","+item.getFieldsetid()+".i9999";
			        			from.append(" left join "+item.getFieldsetid()+" on K01.E01A1="+item.getFieldsetid()+".E01A1");
			        			if("0".equals(isHistory))
			        			{
			        				where.append(" and ("+item.getFieldsetid()+".I9999=(select max(I9999) from "+item.getFieldsetid());
			        				where.append(" where "+item.getFieldsetid()+".E01A1=K01.E01A1) or "+item.getFieldsetid()+".I9999 is null)");
			        			}
			        			setMap.put(item.getFieldsetid().toUpperCase(), "1");
			        		}
			        	}
		        	}
		        	select.setLength(select.length()-1);
		        	buf.append(select.toString()+from+where+" order by a0000 ");
				}
				
                buf.append(orderByI9999);
                ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>(); 
                for(int i = 0; i < itemList.size(); i++) {
                	FieldItem fi = (FieldItem) itemList.get(i);
                	LazyDynaBean bean = new LazyDynaBean();
                	// 列头名称
                	bean.set("content", fi.getItemdesc());
                	bean.set("itemid", fi.getItemid());// 列头代码
                	bean.set("codesetid", fi.getCodesetid());// 列头代码
                	bean.set("colType", fi.getItemtype());// 该列数据类型
                	bean.set("decwidth", fi.getDecimalwidth() + "");// 列小数点后面位数
                	if("D".equalsIgnoreCase(fi.getItemtype())) {
                		String dateFormat = "yyyy-MM-dd";
                		int itemLength = fi.getItemlength();
                		if(itemLength == 4) {
                            dateFormat = "yyyy";
                        } else if(itemLength == 7) {
                            dateFormat = "yyyy-MM";
                        } else if(itemLength == 17) {
                            dateFormat = "yyyy-MM-dd HH:mm";
                        } else if(itemLength == 20) {
                            dateFormat = "yyyy-MM-dd HH:mm:ss";
                        }
                		
                		bean.set("dateFormat", dateFormat);
                	}

                	headList.add(bean);
                }
                
                ExportExcelUtil exportExcelUtil = new ExportExcelUtil(this.conn, userView);
                exportExcelUtil.exportExcelBySql(fileName, "", null, headList, buf.toString(), null, 0);
                fileName = exportExcelUtil.getFileName();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			if(db.isExistTable(userView.getUserName() + "ExcleTemp", false)) {
                db.dropTable(userView.getUserName() + "ExcleTemp");
            }
		}
		return fileName;
	}
	
	public String exportExcel1(String infokind,String fields,String isHistory,UserView userView,String querytype,
	        ArrayList dbpreList,String sql) {
	    return exportExcel1(infokind, fields, isHistory, userView, querytype, dbpreList, sql, "");
	}
	
	public String exportExcel1(String infokind,String fields,String isHistory,UserView userView,String querytype,
	        ArrayList dbpreList,String sql, String whereStr) {
	    String fileName= userView.getUserName() + "_hr.xls";
		//boolean selectsubset = false;   //选中的指标中有无子集内指标，如果有子集内指标，则selectsubset为true，没有则为false  赵国栋 2013-11-05
		String orderByI9999="";//tiany 田野修改使用orderByI9999代替selectsubset直接拼写排序的i9999 
		RowSet rs = null;
		RowSet rowSet = null;
		DbWizard db = new DbWizard(this.conn);
		try {
			if(fields!=null&&fields.trim().length()>0)
			{
				ContentDAO dao = new ContentDAO(this.conn);
				rowSet=dao.search("select dbid,pre from dbname order by dbid");
				HashMap dbidMap = new HashMap();
				boolean fal = false;
				if(dbpreList.get(0)==null||"".equals(dbpreList.get(0))||dbpreList.get(0)==""){
					dbpreList.remove(0);
					fal = true;
				}
				while(rowSet.next())
				{
					if(fal) {
                        dbpreList.add(rowSet.getString("pre").toUpperCase());
                    }
					dbidMap.put(rowSet.getString("pre").toUpperCase(), rowSet.getString("dbid"));
				}
				String[] arr=fields.split("`");
				StringBuffer buf = new StringBuffer();
				ArrayList itemList = new ArrayList();
				if("1".equals(infokind)) {
				    StringBuffer outerSelect = new StringBuffer();
					for(int j=0;j<dbpreList.size();j++) {
						String dbpre=(String)dbpreList.get(j);
						StringBuffer select = new StringBuffer(" select "+Sql_switcher.isnull(dbpre+"A01.a0000", "99999")+" as a0000,"+((String)dbidMap.get(dbpre.toUpperCase()))+" as dbid,");
						if(j == 0) {
                            outerSelect.append("select a0000, dbid,");
                        }
						
						HashMap setMap = new HashMap();
						setMap.put("A01", "1");
						StringBuffer from  = new StringBuffer(" from "+dbpre+"A01 ");
						StringBuffer where = new StringBuffer();
						if(sql.toUpperCase().indexOf("AS DB")!=-1)//liuy 2015-1-16 6748：员工管理-统计分析-常用统计-学历分布（在设置统计范围中设置两个人员库），统计后反查人员个数是对的，但是输出Excel后会多,不对了
                        {
                            where.append(" where "+dbpre+"A01.A0100 in (select a0100 "+sql+" where upper(db) like '%"+dbpre.toUpperCase()+"') ");
                        } else {
						    //zxj 20150710 jazz10936传入的sql变量中有时是多表查询，多表中都有a0100,需要明确a0100的表名
						    where.append(" where "+dbpre+"A01.A0100 in (select "+dbpre+"A01.a0100 "+sql+") ");
						}
						
						int index = 0;
			        	for(int i=0;i<arr.length;i++) {
			        		if(arr[i]==null|| "".equals(arr[i])) {
                                continue;
                            }
			        		
				          	FieldItem item = DataDictionary.getFieldItem(arr[i].toLowerCase());
				        	if(item!=null) {
				        		if(j==0) {
				            		itemList.add(item);
				            		outerSelect.append(item.getItemid()+",");
				        		}
				        		
				        		select.append(dbpre+item.getFieldsetid()+"."+item.getItemid()+",");
				        		if(setMap.get(item.getFieldsetid().toUpperCase())==null) {
				        		    select.append(dbpre + item.getFieldsetid()+".i9999 as i9999_" + index + ",");
                                    if(j == 0) {
                                        orderByI9999 += ",i9999_" + index;
                                    }
                                    
                                    index++;
				        			from.append(" left join "+dbpre+item.getFieldsetid()+" on "+dbpre+"A01.A0100="+dbpre+item.getFieldsetid()+".A0100");
				        			
				        			if("2".equals(isHistory) || "3".equals(isHistory)) {
                                        String strWhere = "";
                                        if(StringUtils.isNotEmpty(whereStr)) {
                                            strWhere = whereStr.replace(item.getFieldsetid() + ".", dbpre + item.getFieldsetid() + ".");
                                        }
                                        
                                        where.append(" and ("+dbpre+item.getFieldsetid()+".I9999 in (select I9999 from "+dbpre+item.getFieldsetid());
                                        where.append(" where " + strWhere);
                                        where.append(" and "+dbpre+item.getFieldsetid()+".A0100="+dbpre+"A01.A0100))");
                                    } else {
                                        where.append(" and ("+dbpre+item.getFieldsetid()+".I9999=(select max(I9999) from "+dbpre+item.getFieldsetid());
                                        where.append(" where "+dbpre+item.getFieldsetid()+".A0100="+dbpre+"A01.A0100) or "+dbpre+item.getFieldsetid()+".I9999 is null)");
                                    }
				        			
				        			setMap.put(item.getFieldsetid().toUpperCase(), "1");
				        		}
				        	}
	    		    	}
			        	
			        	if(j == 0) {
                            outerSelect.deleteCharAt(outerSelect.length() - 1);
                            outerSelect.append(" from ");
                            if(orderByI9999.endsWith(",")) {
                                orderByI9999 = orderByI9999.substring(0, orderByI9999.length() - 1);
                            }
                        }
			        	
			        	select.setLength(select.length()-1);
			        	buf.append(select.toString()+from+where);
			        	if(j!=dbpreList.size()-1) {
                            buf.append(" union all ");
                        }
					}
					
					String str=buf.toString();
                    buf.setLength(0);
                    buf.append(outerSelect).append("(").append(str+") A order by dbid,a0000");
				}
				else if("2".equals(infokind))
				{

					StringBuffer select = new StringBuffer(" select "+Sql_switcher.isnull("T.a0000", "99999")+" as a0000,");
					HashMap setMap = new HashMap();
					setMap.put("B01", "1");
					StringBuffer from  = new StringBuffer(" from B01 left join (select codeitemid, a0000 from organization where (codesetid='UM' or codesetid='UN')) T on B01.b0110=T.codeitemid ");
					StringBuffer where = new StringBuffer();
					if(userView.getStatus()==4)
					{
						String tabldName = "t_sys_result";
						Table table = new Table(tabldName);
						DbWizard dbWizard = new DbWizard(conn);
						if (!dbWizard.isExistTable(table)) {
							where.append(" where 1=2 ");
						}
						else
						{
							where.append(" where B01.B0110 in (select obj_id from t_sys_result where flag=1 and UPPER(username)='"+userView.getUserName().toUpperCase()+"')");
						}
					}
					else
					{
						where.append(" where B01.B0110 in (select B0110 from "+userView.getUserName()+"Bresult) ");
					}
		        	for(int i=0;i<arr.length;i++)
		        	{
		        		if(arr[i]==null|| "".equals(arr[i])) {
                            continue;
                        }
			          	FieldItem item = DataDictionary.getFieldItem(arr[i].toLowerCase());
			        	if(item!=null)
			        	{
			        		itemList.add(item);
			        		String fieldsetid=item.getFieldsetid().toUpperCase();
			        		if("A01".equalsIgnoreCase(fieldsetid)) {
                                fieldsetid="B01";
                            }
			        		select.append(fieldsetid+"."+item.getItemid()+",");
			        		
			        		if(setMap.get(fieldsetid)==null)
			        		{
			        			//selectsubset = true;
			        		    //orderByI9999+=","+item.getFieldsetid()+".i9999";
			        			from.append(" left join "+item.getFieldsetid()+" on B01.B0110="+item.getFieldsetid()+".B0110");
			        			if("0".equals(isHistory))
			        			{
			        				where.append(" and ("+item.getFieldsetid()+".I9999=(select max(I9999) from "+item.getFieldsetid());
			        				where.append(" where "+item.getFieldsetid()+".B0110=B01.B0110) or "+item.getFieldsetid()+".I9999 is null)");
			        			}
			        			setMap.put(item.getFieldsetid().toUpperCase(), "1");
			        		}
			        	}
		        	}
		        	select.setLength(select.length()-1);
		        	buf.append(select.toString()+from+where+" order by a0000");
				}
				else if("3".equals(infokind))
				{
					StringBuffer select = new StringBuffer(" select "+Sql_switcher.isnull("T.a0000", "a0000")+" as a0000,");
					HashMap setMap = new HashMap();
					setMap.put("K01", "1");
					StringBuffer from  = new StringBuffer(" from K01 left join (select codeitemid, a0000 from organization where codesetid='@K') T on K01.e01a1=T.codeitemid ");
					StringBuffer where = new StringBuffer();
					if(userView.getStatus()==4)
					{
						String tabldName = "t_sys_result";
						Table table = new Table(tabldName);
						DbWizard dbWizard = new DbWizard(conn);
						if (!dbWizard.isExistTable(table)) {
							where.append(" where 1=2 ");
						}
						else
						{
							where.append(" where K01.E01A1 in (select obj_id from t_sys_result where flag=3 and UPPER(username)='"+userView.getUserName().toUpperCase()+"')");
						}
					}
					else
					{
						where.append(" where K01.E01A1 in (select E01A1 from "+userView.getUserName()+"Kresult) ");
					}
		        	for(int i=0;i<arr.length;i++)
		        	{
		        		if(arr[i]==null|| "".equals(arr[i])) {
                            continue;
                        }
			          	FieldItem item = DataDictionary.getFieldItem(arr[i].toLowerCase());
			        	if(item!=null)
			        	{
			        		itemList.add(item);
			        		String fieldsetid=item.getFieldsetid().toUpperCase();
			        		if("A01".equalsIgnoreCase(fieldsetid)|| "B01".equalsIgnoreCase(fieldsetid)) {
                                fieldsetid="K01";
                            }
			        		select.append(fieldsetid+"."+item.getItemid()+",");
			        		
			        		if(setMap.get(fieldsetid)==null)
			        		{
			        			//selectsubset = true;
			        		   // orderByI9999+=","+item.getFieldsetid()+".i9999";
			        			from.append(" left join "+item.getFieldsetid()+" on K01.E01A1="+item.getFieldsetid()+".E01A1");
			        			if("0".equals(isHistory))
			        			{
			        				where.append(" and ("+item.getFieldsetid()+".I9999=(select max(I9999) from "+item.getFieldsetid());
			        				where.append(" where "+item.getFieldsetid()+".E01A1=K01.E01A1) or "+item.getFieldsetid()+".I9999 is null)");
			        			}
			        			setMap.put(item.getFieldsetid().toUpperCase(), "1");
			        		}
			        	}
		        	}
		        	select.setLength(select.length()-1);
		        	buf.append(select.toString()+from+where+" order by a0000 ");
				}
				
				ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>(); 
                for(int i = 0; i < itemList.size(); i++) {
                	FieldItem fi = (FieldItem) itemList.get(i);
                	LazyDynaBean bean = new LazyDynaBean();
                	// 列头名称
                	bean.set("content", fi.getItemdesc());
                	bean.set("itemid", fi.getItemid());// 列头代码
                	bean.set("codesetid", fi.getCodesetid());// 列头代码
                	bean.set("colType", fi.getItemtype());// 该列数据类型
                	bean.set("decwidth", fi.getDecimalwidth() + "");// 列小数点后面位数
                	if("D".equalsIgnoreCase(fi.getItemtype())) {
                		String dateFormat = "yyyy-MM-dd";
                		int itemLength = fi.getItemlength();
                		if(itemLength == 4) {
                            dateFormat = "yyyy";
                        } else if(itemLength == 7) {
                            dateFormat = "yyyy-MM";
                        } else if(itemLength == 17) {
                            dateFormat = "yyyy-MM-dd HH:mm";
                        } else if(itemLength == 20) {
                            dateFormat = "yyyy-MM-dd HH:mm:ss";
                        }
                		
                		bean.set("dateFormat", dateFormat);
                	}

                	headList.add(bean);
                }
                
                ExportExcelUtil exportExcelUtil = new ExportExcelUtil(this.conn, userView);
                exportExcelUtil.exportExcelBySql(fileName, "", null, headList, buf.toString(), null, 0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
            db.dropTable(userView.getUserName() + "ExcleTemp");
		}
		return fileName;
	}
	
	/**
	 * 按指标定义的长度格式化日期时间数据
	 * @param aDate
	 * @param dateLen
	 * @return
	 * @author zhaoxj
	 */
	private String formatDate(java.sql.Date aDate, int dateLen) {
	    if (aDate == null) {
            return "";
        }
	        
	    SimpleDateFormat format = null;
        
        switch(dateLen) {
        case  4: format = formatY; break;
        case  6:
        case  7: format = formatYM; break;
        case 10: format = formatYMD; break;
        case 16: 
        case 17:format = formatYMDHM; break;
        case 18:
        case 19:
        case 20: format = formatYMDHMS; break;
        default: format = formatYMD;
        }
        
        return format.format(aDate);
	}
	
	public HSSFCellStyle getCellStyle(HSSFWorkbook workbook,int scale,HSSFDataFormat df)
	{
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		try
		{
			HSSFFont afont = workbook.createFont();
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			cellStyle.setFont(afont);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setWrapText(false);
			if(scale==0)
			{
				
			}else
			{
				StringBuffer buf = new StringBuffer();
				for(int i=0;i<scale;i++)
				{
					buf.append("0");
				}
				String format="0."+buf.toString()+"_ ";
				cellStyle.setDataFormat(df.getFormat(format));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cellStyle;
	}

}
