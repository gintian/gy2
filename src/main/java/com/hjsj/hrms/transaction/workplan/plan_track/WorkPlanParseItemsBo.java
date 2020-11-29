package com.hjsj.hrms.transaction.workplan.plan_track;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.utils.Sql_switcher;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkPlanParseItemsBo {
	
	public String queryString(ArrayList<MorphDynaBean> items,WorkPlanUtil workPlanUtil){
		String[] dbnames = workPlanUtil.getHrSelfUserDbs();
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
			
		if(items!=null && items.size()>0){//haosl 20170315 update 删除筛选条件后，items不为null但是长度为0 querySql 拼接错误(出现 “ or or ”)
			for (int index=0;index<dbnames.length;index++){
				int j=0;
				for (MorphDynaBean bean : items) {
					if(index > 0 && j==0){
						
					}else {
						querySql.append(" and ");
					}
					HashMap dynaBeanMap = PubFunc.DynaBean2Map(bean);
					itemtype = (String)dynaBeanMap.get("itemtype");
					itemid = (String)dynaBeanMap.get("itemid");
					itemdesc = (String)dynaBeanMap.get("itemdesc");
					value = (String) dynaBeanMap.get("value");
					if("A".equalsIgnoreCase(itemtype)){
						codesetid = (String) dynaBeanMap.get("codesetid");
					}
					if(!"AD".equalsIgnoreCase(codesetid)){
						fieldsetid = (String) dynaBeanMap.get("fieldsetid");
					}
					String[] split = null;
					if("D".equalsIgnoreCase(itemtype)){//日期
						dateType = (String) dynaBeanMap.get("type");
						split = value.split("~");
						minDate = split[0];
						maxDate = split[1];
						if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//主集
							querySql.append(" F.a0100 in (select a0100 from ");
							querySql.append(dbnames[index]+fieldsetid);
							querySql.append(" where ");
						}
						if("area".equalsIgnoreCase(dateType)){
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
						if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//主集
							querySql.append(" F.a0100 in (select a0100 from ");
							querySql.append(dbnames[index]+fieldsetid);
							querySql.append(" where ");
						}
						
						if((!"*".equalsIgnoreCase(minValue))&&(!"*".equalsIgnoreCase(maxValue))){
							querySql.append(Sql_switcher.isnull(itemid, "0")+" between '"+minValue+"' and '"+maxValue+"'");
						}else if(("*".equalsIgnoreCase(minValue))&&(!"*".equalsIgnoreCase(maxValue))){
							querySql.append(Sql_switcher.isnull(itemid, "0")+" <= '"+maxValue+"'");
						}else if((!"*".equalsIgnoreCase(minValue))&&("*".equalsIgnoreCase(maxValue))){
							querySql.append(Sql_switcher.isnull(itemid, "0")+" >= '"+minValue+"'");
						}
						querySql.append(")");
					}else{
						if(StringUtils.isNotEmpty(value)&&!"D".equalsIgnoreCase(itemtype)){
							split = value.split(",");
							if(!"zp_flow_status".equalsIgnoreCase(fieldsetid)){
								
								if("A".equalsIgnoreCase(fieldsetid.substring(0,1))){//主集
										querySql.append(" F.a0100 in (select a0100 from ");
										querySql.append(dbnames[index]+fieldsetid);
										querySql.append(" where ");
								}
								for (int i = 0; i < split.length; i++) {
									if(StringUtils.isNotEmpty(codesetid)&&!"0".equalsIgnoreCase(codesetid)){
										if("UN".equalsIgnoreCase(codesetid)||"UM".equalsIgnoreCase(codesetid)||"AM".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)){//单位和部门
											querySql.append(itemid);
											querySql.append(" like '"+split[i]+"%'");
											querySql.append(" or ");
										}else{
											querySql.append(itemid);
											querySql.append(" ='"+split[i]+"'");
											querySql.append(" or ");
										}
									}else if(StringUtils.isNotEmpty(codesetid)&&"0".equalsIgnoreCase(codesetid)){
										
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
						}
					}
					j++;
				}
					
				querySql.append(" or ");
			}
			if(querySql.length()>0)
				querySql.setLength(querySql.length()-3);
		}
		if(" or  ".equals(querySql.toString())){
			querySql.setLength(0);
		}
		return querySql.toString();
	}

}
