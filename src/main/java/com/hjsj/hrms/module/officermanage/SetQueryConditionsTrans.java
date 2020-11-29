package com.hjsj.hrms.module.officermanage;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
/**通用查询*/
public class SetQueryConditionsTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String flag=(String)this.getFormHM().get("flag");
			TableDataConfigCache cache = (TableDataConfigCache) this.userView.getHm().get("OfficerManage_OfficerView");
			if(StringUtils.isNotEmpty(flag)) {
				if("treeFlag".equals(flag)) {
					String id=(String)this.getFormHM().get("id");
					String b0110=(String)this.getFormHM().get("b0110");
					String postStat=(String)this.getFormHM().get("postStat");
					String codesetId=(String)this.getFormHM().get("codesetid");
					boolean isHave_subSet=true;
					if(StringUtils.isEmpty(postStat)) {
						isHave_subSet=false;
						if("UN".equalsIgnoreCase(codesetId)) {
							b0110="b0110";
						}else if("UM".equalsIgnoreCase(codesetId)) {
							b0110="E0122";
						}else if("@K".equalsIgnoreCase(codesetId)) {
							b0110="E01A1";
						}
					}
					String tableSql=cache.getTableSql();
					boolean rootFlag="root".equalsIgnoreCase(id)?true:false;//选中根节点
					if(isHave_subSet) {
						if(tableSql.indexOf(b0110)>-1) {
							if(tableSql.indexOf(" and 2=2 ")<0) {//第一次点击树节点
								if(!rootFlag) {
									if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
										tableSql=tableSql.replace(" ) as "+b0110, " and 2=2 and "+b0110+"='"+id+"' and 2=2 ) as "+b0110);
									}else {
										tableSql=tableSql.replace("order by "+b0110+" ) as "+b0110, " and 2=2 and "+b0110+"='"+id+"' and 2=2 order by "+b0110+" ) as "+b0110);
									}
								}
							}else {//第二次点击树节点 移除之前添加sql 替换新组装sql
								String firstSql=tableSql.substring(tableSql.indexOf(" and 2=2 ")+9);
								String fielterSql=firstSql.substring(0, firstSql.indexOf(" and 2=2 ")+9);
								tableSql=tableSql.replace(" and 2=2 "+fielterSql, " and 2=2 "+(rootFlag?"":("and "+b0110+"='"+id+"'"))+" and 2=2 ");
							}
							
							//过滤人员
							String aSql=tableSql.substring(tableSql.indexOf(" 'a'='a' ")+9);
							String sql=aSql.substring(0, aSql.indexOf("and 'a'='a' ")+12);
							tableSql=tableSql.replace(" 'a'='a' "+sql, " 'a'='a' and "+postStat+"=2 "+(rootFlag?"":("and "+b0110+"='"+id+"'"))+" and 'a'='a' ");
							
							//序号过滤
							String aa_sql=tableSql.substring(tableSql.indexOf(" 'c'='c' ")+9);
							String a_sql=aa_sql.substring(0,aa_sql.indexOf("and 'c'='c' ")+12);
							tableSql=tableSql.replace(" 'c'='c' "+a_sql, " 'c'='c' "+(rootFlag?"":("and "+b0110+"='"+id+"'"))+" and 'c'='c' ");
//							tableSql=tableSql.replace(" and 'c'='c' and", "");
							
							//职务名称显示过滤
							String jobSql=tableSql.substring(tableSql.indexOf(" and 'b'='b' ")+13);
							String subJobSql=jobSql.substring(0, jobSql.indexOf(" and 'b'='b' ")+13);
							tableSql=tableSql.replace(" and 'b'='b' "+subJobSql, " and 'b'='b' and "+postStat+"=2 "+(rootFlag?"":("and "+b0110+"='"+id+"'"))+" and 'b'='b' ");
							cache.setTableSql(tableSql);
						}
					}else {
						StringBuffer sbf=new StringBuffer();
						sbf.append(" and " +b0110+" like '"+id+"%'");
						if(tableSql.indexOf(" and 'd' = 'd' ")>-1) {
							tableSql=tableSql.subSequence(0, tableSql.indexOf(" and 'd' = 'd' ")+15)+sbf.toString();
						}else {//第一次点击树节点
							tableSql+=" and 'd' = 'd' "+sbf.toString();
						}
						cache.setTableSql(tableSql);
					}
				}
			}else {
				StringBuffer sbf=new StringBuffer();
				ArrayList<MorphDynaBean> list=(ArrayList<MorphDynaBean>)this.getFormHM().get("params");
				for(int i=0;i<list.size();i++) {
					HashMap map=PubFunc.DynaBean2Map(list.get(i));
					String itemid=map.get("itemid").toString();
					String codesetid=map.get("codesetid").toString();
					String value=map.get("value").toString();
					String itemtype=(String)map.get("itemtype");
					sbf.append(" AND ");
					if(StringUtils.isNotEmpty(codesetid)&&!"0".equals(codesetid)) {
						String[] arry=value.split(",");
						sbf.append(" (");
						for (int j = 0; j < arry.length; j++) {
							sbf.append(itemid);
							sbf.append("='"+arry[j]+"'");
							if(j<arry.length-1)
								sbf.append(" OR ");
						}
						sbf.append(" )");
					}else {
						
						if("D".equalsIgnoreCase(itemtype)) {
							String dateType=map.get("type").toString();
							String mindate=value.split("~")[0];
							String maxdate=value.split("~")[1];
							if("area".equals(dateType)) {
								if(mindate.length()==10) {
									mindate=Sql_switcher.dateValue(mindate);
								}
								if(maxdate.length()==10) {
									maxdate=Sql_switcher.dateValue(maxdate);
								}
								if((!"*".equals(mindate))&&(!"*".equals(maxdate))) {
									sbf.append(Sql_switcher.charToDate(itemid)+" between "+mindate+" and "+maxdate+" ");
								}else if("*".equals(mindate)&&(!"*".equals(maxdate))) {
									sbf.append(Sql_switcher.charToDate(itemid)+" <= "+maxdate+" ");
								}else if((!"*".equals(mindate))&&("*".equals(maxdate))) 
									sbf.append(Sql_switcher.charToDate(itemid)+" >= "+mindate+" ");
							}else if("year".equalsIgnoreCase(dateType)) {
								if((!"*".equals(mindate))&&(!"*".equals(maxdate))) {
									sbf.append(itemid+" between "+Sql_switcher.year(mindate)+" and "+Sql_switcher.year(maxdate)+" ");
								}else if("*".equals(mindate)&&(!"*".equals(maxdate))) {
									sbf.append(itemid+" <= "+Sql_switcher.year(maxdate)+" ");
								}else if((!"*".equals(mindate))&&("*".equals(maxdate))) 
									sbf.append(itemid+" >= "+Sql_switcher.year(mindate)+" ");
							}else if("month".equalsIgnoreCase(dateType)) {
								if((!"*".equals(mindate))&&(!"*".equals(maxdate))) {
									sbf.append(itemid+" between "+Sql_switcher.month(mindate)+" and "+Sql_switcher.month(maxdate)+" ");
								}else if("*".equals(mindate)&&(!"*".equals(maxdate))) {
									sbf.append(itemid+" <= "+Sql_switcher.month(maxdate)+" ");
								}else if((!"*".equals(mindate))&&("*".equals(maxdate))) 
									sbf.append(itemid+" >= "+Sql_switcher.month(mindate)+" ");
							}else if("day".equalsIgnoreCase(dateType)) {
								if((!"*".equals(mindate))&&(!"*".equals(maxdate))) {
									sbf.append(itemid+" between "+Sql_switcher.day(mindate)+" and "+Sql_switcher.day(maxdate)+" ");
								}else if("*".equals(mindate)&&(!"*".equals(maxdate))) {
									sbf.append(itemid+" <= "+Sql_switcher.day(maxdate)+" ");
								}else if((!"*".equals(mindate))&&("*".equals(maxdate))) 
									sbf.append(itemid+" >= "+Sql_switcher.day(mindate)+" ");
							}
							
						}else {
							
							String[] nbases=value.split(",");
							if(nbases.length>1){
								sbf.append(" (");
								for (int k = 0; k < nbases.length; k++) {
									if(k!=0)
										sbf.append(" or ");
									if("Dbtype".equals(itemid)) {//筛选人员库无需模糊查询，人员库标识转大写
										sbf.append("UPPER("+itemid+")");
										sbf.append(" = UPPER('"+nbases[k]+"')");
									}else {
										sbf.append(itemid);
										sbf.append(" like '%"+nbases[k]+"%'");
									}
									
								}
								sbf.append(" ) ");
							}else {
								if("Dbtype".equals(itemid)) {//筛选人员库无需模糊查询，人员库标识转大写
									sbf.append("UPPER("+itemid+")");
									sbf.append(" = UPPER('"+value+"')");
								}else {
									sbf.append(itemid);
									sbf.append(" like '%"+value+"%'");
								}
								
							}
						}
					}
				}
				cache.setQuerySql(sbf.toString());
			}
			
			
			this.getFormHM().put("typeFlag", true);			
		} catch (Exception e) {
			this.getFormHM().put("typeFlag", false);
			this.getFormHM().put("errMsg", e.getMessage());
			e.printStackTrace();
		}
	}

}
