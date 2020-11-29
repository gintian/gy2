package com.hjsj.hrms.module.recruitment.resumecenter.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Sql_switcher;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ParseQueryItemsBo {
	/**
	 * @param items	 ArrayList<MorphDynaBean> 
	 * @param dbname 库前缀
	 * @param from 考生管理
	 * @return 返回   and myGridData.a0100 in()
	 *  sql 条件
	 */
	public String queryString(ArrayList<MorphDynaBean> items,String dbname,String from){
		String itemtype = "";	//公共查询
		String itemid = "";	//公共查询
		String codesetid = "";	//公共查询
		String fieldsetid = "";	//公共查询
		String value = "";	//公共查询
		String minDate = "";	//公共查询  起始日期 不填为*
		String maxDate = "";	//公共查询  终止日期 不填为*
		String minValue = "";	//公共查询  起始日期 不填为*
		String maxValue = "";	//公共查询  终止日期 不填为*
		String itemdesc = "";	//公共查询  
		String dateType = "";	//公共查询  日期类型 年、月、日
		StringBuffer querySql = new StringBuffer();
			
		if(items!=null){
			for (MorphDynaBean bean : items) {
				querySql.append(" and ");
				
				HashMap dynaBeanMap = PubFunc.DynaBean2Map(bean);
				itemtype = (String)dynaBeanMap.get("itemtype");
				itemid = (String)dynaBeanMap.get("itemid");
				itemdesc = (String)dynaBeanMap.get("itemdesc");
				value = (String) dynaBeanMap.get("value");
				if(value.contains("custom_")){
					ArrayList<MorphDynaBean> codeDate = (ArrayList<MorphDynaBean>) dynaBeanMap.get("codeData");
					String[] splitValue = value.split(",");
					value="";
					for(int i=0;i<splitValue.length;i++){
						for (MorphDynaBean codebean : codeDate) {
							if(splitValue[i].equalsIgnoreCase((String) codebean.get("codeitemid"))){
								value+= (String) codebean.get("codeitemdesc")+",";
							}
						}
					}
				}
				if("A".equalsIgnoreCase(itemtype)){
					codesetid = (String) dynaBeanMap.get("codesetid");
				}
				
				fieldsetid = (String) dynaBeanMap.get("fieldsetid");
				if("hall_id".equalsIgnoreCase(itemid)||"seat_id".equalsIgnoreCase(itemid)||itemid.contains("subject_")){
					fieldsetid = "zp_exam_assign";
				}
				if("suitable".equalsIgnoreCase(itemid)||"recdate".equalsIgnoreCase(itemid)||"node_id".equalsIgnoreCase(itemid)||"status".equalsIgnoreCase(itemid)){
					fieldsetid = "zp_pos_tache";
					itemid = "myGridData."+itemid;
				}
				if("custom_name".equalsIgnoreCase(itemid)){
					fieldsetid = "zp_flow_status";
					itemid = "myGridData."+itemid;
				}
				if("Z0103".equalsIgnoreCase(itemid)){
					fieldsetid = "Z01";
				}
				if("publishTime".equalsIgnoreCase(itemid)||"depResponsPosi".equalsIgnoreCase(itemid)||"responsPosi".equalsIgnoreCase(itemid)){
					fieldsetid = "zp_members";
					itemid = "myGridData."+itemid;
				}
				String[] split = null;
				if("D".equalsIgnoreCase(itemtype)){//日期
					dateType = (String) dynaBeanMap.get("type");
					String dateFormat = (String) dynaBeanMap.get("dateFormat");
					split = value.split("~");
					minDate = split[0];
					maxDate = split[1];
					if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//人员子集
						querySql.append(" myGridData.a0100 in (select a0100 from ");
						querySql.append(dbname+fieldsetid);
						querySql.append(" where ");
					}else{//其他子集
						if(!"Z03".equalsIgnoreCase(fieldsetid)&&!"zp_members".equalsIgnoreCase(fieldsetid)){
							querySql.append(" myGridData.a0100 in (select a0100 from ");
							querySql.append(fieldsetid);
							querySql.append(" where nbase = myGridData.nbase and ");
						}else{
							if(!"ksgl".equalsIgnoreCase(from)){
								querySql.append(" myGridData.z0301 in(select z0301 from z03 ");
								querySql.append(" where z03.z0301=myGridData.z0301 and ");
							}else{
								querySql.append(" myGridData.a0100 in (select a0100 from z63 ");
								querySql.append(" where nbase = myGridData.nbase and ");
							}
						}
					}
					if("area".equalsIgnoreCase(dateType)){
						String[] minsplit = null;
						String[] maxsplit = null;
						if("Y-m-d H:i:s".equals(dateFormat)){
							if(minDate.length()==19){
								minDate = Sql_switcher.dateValue(minDate);
							}
							if(maxDate.length()==19){
								maxDate = Sql_switcher.dateValue(maxDate);
							}
							if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" between "+minDate+" and "+maxDate+" ");
							}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" <= "+maxDate+"");
							}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" >= "+minDate+" ");
							}
							
						}else if("Y-m-d H:i".equals(dateFormat)){
							if(minDate.length()==16){
								minDate = minDate+":00";
								minDate = Sql_switcher.dateValue(minDate);
							}
							if(maxDate.length()==16){
								maxDate = maxDate+":59";
								maxDate = Sql_switcher.dateValue(maxDate);
							}
							if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" between "+minDate+" and "+maxDate+" ");
							}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" <= "+maxDate+"");
							}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" >= "+minDate+" ");
							}
							
						}else if("Y-m-d".equals(dateFormat)){
							if(minDate.length()==10){
								minDate = minDate+" 00:00:00";
								minDate = Sql_switcher.dateValue(minDate);
							}
							if(maxDate.length()==10){
								maxDate = maxDate+" 23:59:59";
								maxDate = Sql_switcher.dateValue(maxDate);
							}
							if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" between "+minDate+" and "+maxDate+" ");
							}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" <= "+maxDate+"");
							}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate))){
								querySql.append(itemid+" >= "+minDate+" ");
							}
						}else if("Y-m".contains((dateFormat))){
							if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								maxsplit = maxDate.split("-");
								minsplit = minDate.split("-");
								querySql.append( Sql_switcher.year(itemid)+" <= "+maxsplit[0]);
								if(dateFormat.contains("m")&&maxsplit.length>2)
									querySql.append(" and "+Sql_switcher.month(itemid)+" <= "+maxsplit[1]+"");
								querySql.append(" and "+ Sql_switcher.year(itemid)+" >= "+minsplit[0]);
								if(dateFormat.contains("m")&&minsplit.length>2)
									querySql.append(" and "+Sql_switcher.month(itemid)+" >= "+minsplit[1]+"");
							}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate))){
								maxsplit = maxDate.split("-");
								querySql.append( Sql_switcher.year(itemid)+" <= "+maxsplit[0]);
								if(dateFormat.contains("m")&&maxsplit.length>2)
									querySql.append(" and "+Sql_switcher.month(itemid)+" <= "+maxsplit[1]+"");
							}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate))){
								minsplit = minDate.split("-");
								querySql.append( Sql_switcher.year(itemid)+" >= "+minsplit[0]);
								if(dateFormat.contains("m")&&minsplit.length>2)
									querySql.append(" and "+Sql_switcher.month(itemid)+" >= "+minsplit[1]+"");
							}
						}
					}else if(StringUtils.equalsIgnoreCase(dateType, "year"))//年限
					{
						if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.diffYears(Sql_switcher.today(),itemid)+" between '"+minDate+"' and '"+maxDate+"'");
						}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.diffYears(Sql_switcher.today(),itemid)+">='"+minDate+"'");
						}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.diffYears(Sql_switcher.today(),itemid)+"<='"+maxDate+"'");
						}
					}else if(StringUtils.equalsIgnoreCase(dateType, "month"))//月份
					{
						if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.month(itemid)+" between '"+minDate+"' and '"+maxDate+"'");
						}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.month(itemid)+">='"+minDate+"'");
						}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.month(itemid)+"<='"+maxDate+"'");
						}
					}else if(StringUtils.equalsIgnoreCase(dateType, "day"))//天
					{
						if((!"*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.day(itemid)+" between '"+minDate+"' and '"+maxDate+"'");
						}else if((!"*".equalsIgnoreCase(minDate))&&("*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.day(itemid)+">='"+minDate+"'");
						}else if(("*".equalsIgnoreCase(minDate))&&(!"*".equalsIgnoreCase(maxDate)))
						{
							querySql.append(Sql_switcher.day(itemid)+"<='"+minDate+"'");
						}
					}
					querySql.append(")");
				}else if("N".equalsIgnoreCase(itemtype)){//数值
					split = value.split("~");
					minValue = split[0];
					maxValue = split[1];
					if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//人员子集
						querySql.append(" myGridData.a0100 in (select a0100 from ");
						querySql.append(dbname+fieldsetid);
						querySql.append(" where ");
					}else{//其他子集
						if(!"Z03".equalsIgnoreCase(fieldsetid)&&!"zp_members".equalsIgnoreCase(fieldsetid)){
							querySql.append(" myGridData.a0100 in (select a0100 from ");
							querySql.append(fieldsetid);
							querySql.append(" where nbase = myGridData.nbase and ");
						}else{
							if(!"ksgl".equalsIgnoreCase(from)){
								querySql.append(" myGridData.z0301 in(select z0301 from z03 ");
								querySql.append(" where z03.z0301=myGridData.z0301 and ");
							}else{
								querySql.append(" myGridData.a0100 in (select a0100 from z63 ");
								querySql.append(" where nbase = myGridData.nbase and ");
							}
						}
					}
					
					if((!"*".equalsIgnoreCase(minValue))&&(!"*".equalsIgnoreCase(maxValue))){
						querySql.append(Sql_switcher.isnull(itemid, "0")+" between "+minValue+" and "+maxValue);
					}else if(("*".equalsIgnoreCase(minValue))&&(!"*".equalsIgnoreCase(maxValue))){
						querySql.append(Sql_switcher.isnull(itemid, "0")+" <= "+maxValue+"");
					}else if((!"*".equalsIgnoreCase(minValue))&&("*".equalsIgnoreCase(maxValue))){
						querySql.append(Sql_switcher.isnull(itemid, "0")+" >= "+minValue+"");
					}
					querySql.append(")");
				}else{
					if(StringUtils.isNotEmpty(value)&&!"D".equalsIgnoreCase(itemtype)){
						split = value.split(",");
						if(!"zp_flow_status".equalsIgnoreCase(fieldsetid)){
							
							if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//人员子集
									
								querySql.append(" myGridData.a0100 in (select a0100 from ");
								querySql.append(dbname+fieldsetid);
								querySql.append(" a1 where ");
								if(!"A01".equalsIgnoreCase(fieldsetid)) {
									querySql.append(" a1.i9999=(select MAX(b1.I9999) from "); //查询最近一条记录
									querySql.append(dbname+fieldsetid);
									querySql.append(" b1 where b1.A0100=a1.a0100)");
									querySql.append(" and ");
								}
							}else{//其他子集
								if(!"Z03".equalsIgnoreCase(fieldsetid)&&!"zp_members".equalsIgnoreCase(fieldsetid)){
									if("Z01".equalsIgnoreCase(fieldsetid)){
										querySql.append("  myGridData.z0301 in(select z0301 from z03");
										querySql.append(" where  z0101 in(select z0101 from z01 ");
										querySql.append(" where ");
									}else if("zp_pos_tache".equalsIgnoreCase(fieldsetid)) {
										querySql.append(" exists (select a0100,ZP_POS_ID,nbase from zp_pos_tache "); 
										querySql.append(" where a0100 = myGridData.a0100 and nbase = myGridData.nbase and ZP_POS_ID=myGridData.z0301 and ");
									}else{
										querySql.append(" myGridData.a0100 in (select a0100 from ");
										querySql.append(fieldsetid);
										querySql.append(" where nbase = myGridData.nbase and ");
									}
								}else{
									if(!"ksgl".equalsIgnoreCase(from)){
										querySql.append(" myGridData.z0301 in(select z0301 from z03 ");
										querySql.append(" where z03.z0301=myGridData.z0301 and ");
									}else{
										querySql.append(" myGridData.a0100 in (select a0100 from z63 ");
										querySql.append(" where nbase = myGridData.nbase and ");
									}
								}
							}
							for (int i = 0; i < split.length; i++) {
								if(StringUtils.isNotEmpty(codesetid)&&!"0".equalsIgnoreCase(codesetid)){
//									if("UN".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"AM".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)){//单位和部门
										querySql.append(itemid);
										querySql.append(" like '"+split[i]+"%'");
										querySql.append(" or ");
//									}else{
//										querySql.append(itemid);
//										querySql.append(" ='"+split[i]+"'");
//										querySql.append(" or ");
//									}
								}else if("0".equalsIgnoreCase(codesetid)||"".equalsIgnoreCase(codesetid)){
									
									querySql.append(itemid);
									if(!"zp_pos_tache".equalsIgnoreCase(fieldsetid)){
										querySql.append(" like '%"+split[i]+"%'");
									}else{
										querySql.append(" ='"+split[i]+"'");
									}
									querySql.append(" or ");
								}
							}
						}else{
							for(int i = 0;i<split.length;i++){
								querySql.append("( "+ itemid);
								querySql.append(" like '%"+split[i]+"%'");
								querySql.append(" or ");
							}
						}
						querySql.setLength(querySql.length()-3);
						querySql.append(")");
						if("Z01".equalsIgnoreCase(fieldsetid)){
							querySql.append(")");
						}
					}
				}
			}
		}
		return querySql.toString();
	}

}
