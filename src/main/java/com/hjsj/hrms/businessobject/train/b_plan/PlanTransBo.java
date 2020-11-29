package com.hjsj.hrms.businessobject.train.b_plan;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.train.TrainBudgetBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
/**
 * <p>Title:GzAmountXMLBo.java</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class PlanTransBo {
	private Connection conn;
	private String model; //1.需求征集 2.需求审批 4.培训自助计划制订 5.培训自助计划审核
	public PlanTransBo(Connection conn,String model){
		this.conn=conn;
		this.model=model;
	}
	public PlanTransBo(){
		
	}
	/**
	 * 前台显示字段
	 * @return
	 */
	public ArrayList itemList(){
		ArrayList list=DataDictionary.getFieldList("r25",Constant.USED_FIELD_SET);
		ArrayList itemlist = new ArrayList();
		for(int i=0;i<list.size();i++){
			FieldItem item=(FieldItem)list.get(i);
			Field field=(Field)item.cloneField();
			if("B0110".equalsIgnoreCase(item.getItemid())){
				field.setReadonly(true);  //此字段为只读状态	
			}else if("E0122".equalsIgnoreCase(item.getItemid())){
				field.setReadonly(true);  //此字段为只读状态	
			}else if("r2509".equalsIgnoreCase(item.getItemid())){
				field.setReadonly(true);  //此字段为只读状态	
			}else if("r2513".equalsIgnoreCase(item.getItemid())&& "1".equals(model)){
				field.setVisible(false);
			}else if("r2501".equalsIgnoreCase(item.getItemid())){
				field.setVisible(false);
			}else if("r2512".equalsIgnoreCase(item.getItemid())){
				field.setVisible(false);  //培训计划制定和计划审批，不应该出现计划表审批方式指标
			}
			if("0".equals(item.getState())){
				field.setVisible(false);
			}
			itemlist.add(field);
		}
		Field field= new Field("model");
		field.setLabel("model");
		field.setDatatype(DataType.STRING);
		field.setReadonly(true);  //此字段为只读状态	
		field.setVisible(false);  //此字段隐藏	
		itemlist.add(field);
		
		return itemlist;
	}
	/**
	 * 前台显示字段
	 * @return
	 */
	public ArrayList itemPDFList(){
		ArrayList list=DataDictionary.getFieldList("r25",Constant.USED_FIELD_SET);
		ArrayList itemlist = new ArrayList();
		for(int i=0;i<list.size();i++){
			FieldItem item=(FieldItem)list.get(i);
			if("r2513".equalsIgnoreCase(item.getItemid())&& "1".equals(model)){
				continue;
			}else if("r2501".equalsIgnoreCase(item.getItemid())){
				continue;
			}
			if("0".equals(item.getState())){
				continue;
			}
			itemlist.add(item);
		}
		
		return itemlist;
	}
	/**
	 * 查询列
	 * 例如:select xxx,xxx,xxx,
	 */
	public String sqlColum(){
		TrainBudgetBo bo = new TrainBudgetBo(conn);
		StringBuffer sqlcloum = new StringBuffer();
		sqlcloum.append("select ");
		ArrayList list = itemList();
		for(int i=0;i<list.size();i++){
			Field field=(Field)list.get(i);
			if(field.getName().equalsIgnoreCase(bo.getBudget())){//显示剩余费用列
				sqlcloum.append("CASE "+Sql_switcher.isnull(field.getName(), "-999999")+" WHEN -999999 THEN r2506 ELSE "+field.getName()+" END "+field.getName());
			}else if("model".equalsIgnoreCase(field.getName())) {
                sqlcloum.append("'"+model+"' as model");
            } else {
                sqlcloum.append(field.getName());
            }
			if(i<list.size()-1){
				sqlcloum.append(",");
			}
		}
		return sqlcloum.toString();
	}
	/**
	 * 查询条件
	 * @param search 查询条件
	 * @param a_code  机构代码
	 * @param time  时间间隔
	 * @param spflag 审批标识
	 * @return
	 */
	public String sqlWhere(String search,String a_code,String time,String spflag){
		StringBuffer sqlwhere = new StringBuffer();
		sqlwhere.append(" from r25 where 1=1 ");
		if(a_code!=null&&a_code.trim().length()>2){
//			if(a_code.substring(0,2).equalsIgnoreCase("UN"))
//				sqlwhere.append(" and B0110 like '"+a_code.substring(2,a_code.length())+"%'");
//			if(a_code.substring(0,2).equalsIgnoreCase("UM"))
//				sqlwhere.append(" and E0122 like '"+a_code.substring(2,a_code.length())+"%'");
			String tmp[] = a_code.split("`");
			sqlwhere.append(" and (");
			for (int i = 0; i < tmp.length; i++) {
				String t = tmp[i];
				if("UN".equalsIgnoreCase(t.substring(0,2))) {
                    sqlwhere.append("B0110 like '"+t.substring(2)+"%' or ");
                }
				if("UM".equalsIgnoreCase(t.substring(0,2))) {
                    sqlwhere.append("E0122 like '"+t.substring(2)+"%' or ");
                }
			}
			sqlwhere.setLength(sqlwhere.length()-3);
			sqlwhere.append(")");
		}
		if(time!=null&&time.trim().length()>2){
			sqlwhere.append(" and "+time);
		}
		if(spflag!=null&&spflag.trim().length()>1){
			sqlwhere.append(" and R2509='"+spflag+"'");
		}
		if(search!=null&&search.trim().length()>0){
			String searcharr[] = search.split("::");
			if(searcharr.length==3){
				String sexpr=searcharr[0];
				String sfactor=searcharr[1];
				try{
					boolean blike = false;
					blike=searcharr[2]!=null&& "1".equals(searcharr[2])?true:false;
					FactorList factor = new FactorList(sexpr, sfactor,
							"", true, blike, true, 1, "su");
					String wherestr = factor.getSqlExpression();
					if(wherestr.indexOf("WHERE")!=-1) {
                        wherestr=wherestr.substring(wherestr.indexOf("WHERE")+5,wherestr.length());
                    }
					if(wherestr.indexOf("where")!=-1) {
                        wherestr=wherestr.substring(wherestr.indexOf("where")+5,wherestr.length());
                    }
					wherestr = wherestr.replaceAll("A01","r25");
					sqlwhere.append(" and "+wherestr);
				}catch (GeneralException e) {
					e.printStackTrace();
				}
			}
		}
		return sqlwhere.toString();
	}
	/**
	 * 查询条件
	 * @param search 查询条件
	 * @param a_code  机构代码
	 * @param time  时间间隔
	 * @param spflag 审批标识
	 * @return
	 */
	public String sqlWhere(UserView userview,String search,String a_code,String time,String spflag){
		StringBuffer sqlwhere = new StringBuffer();
		try {
		    sqlwhere.append(" from r25 where 1=1 ");
		    if(a_code!=null&&a_code.trim().length()>2){
		        sqlwhere.append(getWhereTmp(a_code));
		    }else{
		        if(!userview.isSuper_admin()){
		            TrainCourseBo bo = new TrainCourseBo(userview);
		            a_code = bo.getUnitIdByBusi();
		            sqlwhere.append(getWhereTmp(a_code));
		        }
		    }
		    if(time!=null&&time.trim().length()>2){
		        sqlwhere.append(" and "+time);
		    }
		    if(spflag!=null&&spflag.trim().length()>1){
		        sqlwhere.append(" and R2509='"+spflag+"'");
		    }
		    if(search!=null&&search.trim().length()>0){
		        String searcharr[] = search.split("::");
		        if(searcharr.length==3){
		            String sexpr=searcharr[0];
		            sexpr = PubFunc.keyWord_reback(sexpr);
		            String sfactor=searcharr[1];
		            sfactor = PubFunc.keyWord_reback(sfactor);
		            if(!"".equals(sfactor) && sfactor != null){
		                
		                
		                try{
		                    boolean blike = false;
		                    blike=searcharr[2]!=null&& "1".equals(searcharr[2])?true:false;
		                    FactorList factor = new FactorList(sexpr, sfactor,
		                            "", true, blike, true, 1, "su");
		                    String wherestr = factor.getSqlExpression();
		                    if(wherestr.indexOf("WHERE")!=-1) {
                                wherestr=wherestr.substring(wherestr.indexOf("WHERE")+5,wherestr.length());
                            } else if(wherestr.indexOf("where")!=-1) {
                                wherestr=wherestr.substring(wherestr.indexOf("where")+5,wherestr.length());
                            }
		                    wherestr = wherestr.replaceAll("A01","r25").replaceAll("r2500", "r2501");
		                    sqlwhere.append(" and "+wherestr);
		                }catch (GeneralException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
        }
		return sqlwhere.toString();
	}
	
	private String getWhereTmp(String a_code){
		StringBuffer sqlwhere = new StringBuffer();
		String unitarr[] = a_code.split("`"); 
		String b0110str = "";
		String e0122str = "";
		for(int i=0;i<unitarr.length;i++){
			if(unitarr[i]!=null&&unitarr[i].startsWith("UN")){
				if(unitarr[i].trim().length()>2){
					b0110str +=" b0110 like '"+unitarr[i].substring(2)+"%' or";
				}
			}else{
				if(unitarr[i].trim().length()>2){
					e0122str +=" e0122 like '"+unitarr[i].substring(2)+"%' or";
				}
			}
		}
		if(b0110str.trim().length()>0){
			if(b0110str.endsWith("or")) {
                b0110str = b0110str.substring(0, b0110str.length()-3);
            }
			sqlwhere.append(" and (");
			sqlwhere.append(b0110str);
			if(e0122str.trim().length()>0){
				if(e0122str.endsWith("or")) {
                    e0122str = e0122str.substring(0, e0122str.length()-3);
                }
				sqlwhere.append(" or ");
				sqlwhere.append(e0122str);
			}
			sqlwhere.append(")");
		}else{
			if(e0122str.trim().length()>0){
				if(e0122str.endsWith("or")) {
                    e0122str = e0122str.substring(0, e0122str.length()-3);
                }
				sqlwhere.append(" and ("+e0122str+")");
			}
		}
		return sqlwhere.toString();
	}
	
	public ArrayList spFlagList(){
		ArrayList spflaglist = new ArrayList();
		CommonData dataobj = new CommonData("00","全部"); 
		spflaglist.add(dataobj);
		if("1".equals(model)){
			dataobj = new CommonData("01","起草"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("02","已报批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("03","已批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("04","已发布"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("06","结束"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("07","驳回"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("09","暂停"); 
			spflaglist.add(dataobj);
		}else if("2".equals(model)){
			dataobj = new CommonData("02","已报批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("03","已批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("04","已发布"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("06","结束"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("09","暂停"); 
			spflaglist.add(dataobj);
		}else if("3".equals(model)){
			dataobj = new CommonData("03","已批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("05","执行中"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("06","结束"); 
			spflaglist.add(dataobj);
		}else if("4".equals(model)){
			dataobj = new CommonData("01","起草"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("02","已报批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("03","已批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("04","已发布"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("07","驳回"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("08","报审"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("09","暂停"); 
			spflaglist.add(dataobj);
		}else if("5".equals(model)){
			dataobj = new CommonData("02","已报批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("03","已批"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("04","已发布"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("06","结束"); 
			spflaglist.add(dataobj);
			dataobj = new CommonData("09","暂停"); 
			spflaglist.add(dataobj);
		}
		return spflaglist;
	}
	public ArrayList timeFlagList(){
		ArrayList timeflaglist = new ArrayList();
		CommonData dataobj = new CommonData("00","全部"); 
		timeflaglist.add(dataobj);
		dataobj = new CommonData("01","本年度"); 
		timeflaglist.add(dataobj);
		dataobj = new CommonData("02","本季度"); 
		timeflaglist.add(dataobj);
		dataobj = new CommonData("03","本月份"); 
		timeflaglist.add(dataobj);
		dataobj = new CommonData("04","某时间段"); 
		timeflaglist.add(dataobj);
		return timeflaglist;
	}
	public ArrayList timeFlagList0(){
		ArrayList timeflaglist = new ArrayList();
		CommonData dataobj = new CommonData("1","全部"); 
		timeflaglist.add(dataobj);
		dataobj = new CommonData("2","本年度"); 
		timeflaglist.add(dataobj);
		dataobj = new CommonData("3","本季度"); 
		timeflaglist.add(dataobj);
		dataobj = new CommonData("4","本月份"); 
		timeflaglist.add(dataobj);
		dataobj = new CommonData("5","某时间段"); 
		timeflaglist.add(dataobj);
		return timeflaglist;
	}
	public String timesSql(String timeflag,String startime,String endtime){
		StringBuffer timesql = new StringBuffer();
		WeekUtils wu = new WeekUtils();
		if("01".equals(timeflag)){
			timesql.append("R2503='"+DateUtils.getYear(wu.strTodate(wu.strDate()))+"'");
		}else if("02".equals(timeflag)){
			timesql.append("R2503='"+DateUtils.getYear(wu.strTodate(wu.strDate()))+"'");
			timesql.append(" and R2504='0"+DateUtils.getQuarter(new Date())+"'");
		}else if("03".equals(timeflag)){
			int month = DateUtils.getMonth(wu.strTodate(wu.strDate())) ;
			timesql.append("R2503='"+DateUtils.getYear(wu.strTodate(wu.strDate()))+"'");
			if(month>9) {
                timesql.append(" and R2505='"+month+"'");
            } else {
                timesql.append(" and R2505='0"+month+"'");
            }
		}else if("04".equals(timeflag)){
			 boolean f = false;
			if(startime!=null&&startime.trim().length()>3){
				f = true;
				String[] date=startime.split("-");
				if(date[1].length() == 1){//判断月份长度，当月份输入为“1”、“2”的情况转换为“01”、“02”
					date[1]="0"+date[1];
				}
				if(Sql_switcher.searchDbServer() == Constant.ORACEL){
					timesql.append(" concat(R2503,nvl(R2505,"+date[1]+"))>="+date[0]+date[1]);
				}else{
					timesql.append(" (R2503+isnull(R2505,"+date[1]+"))>="+date[0]+date[1]);
				}
			}
			if(f&&endtime!=null&&endtime.trim().length()>3){
				timesql.append(" and ");
			}
			if(endtime!=null&&endtime.trim().length()>3){
				String[] date=endtime.split("-");
				if(date[1].length() == 1){//判断月份长度，当月份输入为“1”、“2”的情况转换为“01”、“02”
					date[1]="0"+date[1];
				}
				if(Sql_switcher.searchDbServer() == Constant.ORACEL){
					timesql.append(" concat(R2503,nvl(R2505,"+date[1]+"))<="+date[0]+date[1]);
				}else{
					timesql.append(" (R2503+isnull(R2505,"+date[1]+"))<="+date[0]+date[1]);
				}
			}
		}
		
		return timesql.toString();
	}
	public String getDataValue(String fielditemid,String operate,String value){
		StringBuffer a_value=new StringBuffer("");	
		if(value.length()>0){
			String[] tempvalue=value.split("-");
			if(tempvalue.length==1){
				value=value+"-01-01";
			}
			if(tempvalue.length==2){
				if(tempvalue[1].length()==1){
					value=tempvalue[0]+"-0"+tempvalue[1]+"-01";
				}else{
					value=value+"-01";
				}
			}
			if(tempvalue.length==3){
				if(tempvalue[1].length()==1){
					tempvalue[1]="0"+tempvalue[1];
				}
				if(tempvalue[2].length()==1){
					tempvalue[2]="0"+tempvalue[2];
				}
				value=tempvalue[0]+"-"+tempvalue[1]+"-"+tempvalue[2];
			}
			try{
				if("=".equals(operate)){
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
					a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
					a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(" ) ");
				}else {	
					if(">=".equals(operate)){
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+" ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+">="+value.substring(8));
						a_value.append(") ) ");
					}else if("<=".equals(operate)){
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid)+"<"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"<"+value.substring(5,7)+" ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+"<="+value.substring(8));
						a_value.append(") ) ");
					}else{
						a_value.append("(");
						a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
						a_value.append(") ) ");	
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return a_value.toString();
	}
}
