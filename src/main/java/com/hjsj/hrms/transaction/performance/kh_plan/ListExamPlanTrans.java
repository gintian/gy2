package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 考核计划交易类
 * 
 * @author: JinChunhai
 */

public class ListExamPlanTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		//动态增加排序字段
		editArticleA0000();	
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String a_planid = (String) hm.get("a_planid");
		hm.remove("a_planid");
		String move = (String) hm.get("move");
		hm.remove("move");
		if(move!=null)//先移动顺序
		{			
			ArrayList dataList = (ArrayList)this.getFormHM().get("setlist");
			moveRecord(move,a_planid,dataList);
			this.getFormHM().put("planSelect", a_planid);
		}			
		
		/*if(hm.get("b_query")!=null && ((String)hm.get("b_query")).equals("link"))
			this.getFormHM().put("planSelect", "");*/
		//记录当前的计划
		String currentPlanId = (String) hm.get("currentPlan");
		this.getFormHM().put("planSelect", currentPlanId);
		hm.remove("currentPlan");
		
		//  此参数控制审批方式  JinChunhai 2011.08.24
		String spmodel = (String)hm.get("spmodel");
		hm.remove("spmodel");
		if(spmodel!=null && spmodel.trim().length()>0)
			this.getFormHM().put("model", spmodel);
		
		String scrollValue = (String) hm.get("scrollValue");
		this.getFormHM().put("scrollValue", scrollValue);
		hm.remove("scrollValue");
		
		String scrollTopValue = (String) hm.get("scrollTopValue");
		this.getFormHM().put("scrollTopValue", scrollTopValue);
		hm.remove("scrollTopValue");
		
		// 查询的参数
		String busitype=(String)this.getFormHM().get("busitype"); // 业务分类 =0(绩效考核); =1(能力素质)
		String spStatus = (String) this.getFormHM().get("spStatus");		
		String timeFw = (String) this.getFormHM().get("timeInterval");
		String startTime = (String) this.getFormHM().get("startDate");
		String endTime = (String) this.getFormHM().get("endDate");
		String name = (String) this.getFormHM().get("qname");
		if(name!=null && name.trim().length()>0)
		        name = name.trim();
		if(name!=null && name.trim().length()>0 && name.indexOf("'")!=-1){	
			name = name.replaceAll("'","‘");
		}
		this.getFormHM().put("qname", name);
		String method = (String) this.getFormHM().get("qmethod");
		String object_type = (String) this.getFormHM().get("qobject_type");
		method=method.trim().length()==0?"all":method;
		object_type=object_type.trim().length()==0?"all":object_type;
		String orgCode = (String) hm.get("a_code");
		hm.remove("a_code");
		orgCode=orgCode==null?"":orgCode;
		if("".equals(orgCode))
			orgCode=(String)this.getFormHM().get("a_code");
		orgCode=orgCode==null?"":orgCode;
		// 过虑机构编码前面的字母标识
		if (orgCode.length() > 1)
			orgCode = orgCode.substring(2, orgCode.length());
	
		try
		{
			ArrayList setlist = this.searchExamPlanList(timeFw, startTime, endTime, spStatus, orgCode,name,method,object_type,busitype);
			this.getFormHM().put("setlist", setlist);
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		String returnflag=(String)hm.get("returnflag");
		if(returnflag==null || returnflag.trim().length()<=0)
			returnflag=(String)this.getFormHM().get("returnflag");
		
		
		this.getFormHM().put("busitype", busitype);		
		this.getFormHM().put("returnflag",returnflag);
	}

	/**
	 * @param timeFw
	 *            时间范围[全部 all][本年 1][本季 2][时间段 3]
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param spSatus
	 *            审批状态:all 所有 |0 起草|1 报批|2 已批|3 已发布|4 正在执行|5 暂停|6 评估|7 结束
	 *            实际查询的时候把评估的记录也作为正在执行的查询结果,因而查询条件中没有评估这一项
	 * @param orgCode
	 *            单位编码
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList searchExamPlanList(String timeFw, String startTime, String endTime,
	    String spSatus, String orgCode,String name,String method,String object_type,String busitype) throws GeneralException
    {
	    ContentDAO dao = new ContentDAO(this.getFrameconn());   
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		ArrayList list = new ArrayList();
		StringBuffer strSql = new StringBuffer("select * from per_plan where 1=1 ");
		DbWizard dbw=new DbWizard(this.frameconn);
		if(!dbw.isExistField("per_plan", "feedback",false))
		{
			Table table=new Table("per_plan");
			Field field=new Field("feedback","feedback");
			field.setDatatype(DataType.INT);
			table.addField(field);
			dbw.addColumns(table);	
		}
		if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype))
			strSql.append(" and ( busitype is null  or busitype = '" + busitype + "') ");
		else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			strSql.append(" and busitype = '" + busitype + "' ");
		
		StringBuffer whlSql = new StringBuffer();	
		whlSql.append(" and ((B0110 = 'HJSJ' or B0110 = '' ) ");
		whlSql.append(" or (B0110 like '" + orgCode + "%' ");
		whlSql.append(")) ");	
		if (!"".equalsIgnoreCase(name.trim()))
			whlSql.append(" and name like '%" + name + "%' ");
		if (!"all".equalsIgnoreCase(method.trim()))
		{
			if ("1".equalsIgnoreCase(method.trim()))
				whlSql.append(" and (method is null or method =1) ");
			else
				whlSql.append(" and method = 2 ");
		}		
		if (!"all".equalsIgnoreCase(object_type.trim()))
		{
			if("1".equalsIgnoreCase(object_type.trim()))
				whlSql.append(" and object_type in(1,3,4) ");
			else
				whlSql.append(" and object_type =" + object_type+" ");
		}
		try
		{   
			whlSql.append(bo.getPlanWhlByObjTypePriv(this.userView,busitype));
			
			// 时间范围查询
			if (timeFw != null && !"".equals(timeFw)  && !"all".equals(timeFw))
			{
			    if ("1".equals(timeFw))
			    	whlSql.append("and (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+" OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+")) ");
			    if ("2".equals(timeFw))
			    {  
					int jd = this.getCurrentJD();
					switch (jd)
					{
						case 1:
						    whlSql.append("and (( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (thequarter = '01') ) OR ( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND themonth IN ('01', '02', '03') ) OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("start_date")+">=1 AND "+Sql_switcher.month("end_date")+"<=3))  ");
						    break;
						case 2:
						    whlSql.append("and (( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (thequarter = '02') ) OR ( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND themonth IN ('04', '05', '06') ) OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("start_date")+">=4 AND "+Sql_switcher.month("end_date")+"<=6))  ");
						    break;
						case 3:
						    whlSql.append("and (( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (thequarter = '03') ) OR ( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND themonth IN ('07', '08', '09') ) OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("start_date")+">=7 AND "+Sql_switcher.month("end_date")+"<=9))  ");
						    break;
						default:
						    whlSql.append("and (( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND (thequarter = '04') ) OR ( (theyear = "+Sql_switcher.year(Sql_switcher.sqlNow())+") AND themonth IN ('10', '11', '12') ) OR ( "+Sql_switcher.year("start_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.year("end_date")+"="+Sql_switcher.year(Sql_switcher.sqlNow())+" AND "+Sql_switcher.month("start_date")+">=10 AND "+Sql_switcher.month("end_date")+"<=12))  ");
					}
			    }
			    if ("3".equals(timeFw))
			    {
					if(startTime!=null && !"".equals(startTime))
					{
						// whlSql.append("and create_date >= '"+startTime+"' ");
					    StringBuffer buf = new StringBuffer();
					    buf.append(Sql_switcher.year("create_date")+ ">"+ getDatePart(startTime,"y") +" or ");
					    buf.append("("+Sql_switcher.year("create_date")+ "="+ getDatePart(startTime,"y")+" and ");
					    buf.append(Sql_switcher.month("create_date")+ ">"+ getDatePart(startTime,"m") +") or ");
					    buf.append("("+Sql_switcher.year("create_date")+ "="+ getDatePart(startTime,"y")+" and ");
					    buf.append(Sql_switcher.month("create_date")+ "="+ getDatePart(startTime,"m") +" and ");
					    buf.append(Sql_switcher.day("create_date")+ ">="+ getDatePart(startTime,"d") +")");
					    whlSql.append(" and ("+buf.toString()+") ");
					}		   
					if(endTime!=null && !"".equals(endTime))
					{
						// whlSql.append("and create_date <= '"+endTime+"' ");
					    
					    StringBuffer buf = new StringBuffer();
					    buf.append(Sql_switcher.year("create_date")+ "<"+ getDatePart(endTime,"y") +" or ");
					    buf.append("("+Sql_switcher.year("create_date")+ "="+ getDatePart(endTime,"y")+" and ");
					    buf.append(Sql_switcher.month("create_date")+ "<"+ getDatePart(endTime,"m") +") or ");
					    buf.append("("+Sql_switcher.year("create_date")+ "="+ getDatePart(endTime,"y")+" and ");
					    buf.append(Sql_switcher.month("create_date")+ "="+ getDatePart(endTime,"m") +" and ");
					    buf.append(Sql_switcher.day("create_date")+ "<="+ getDatePart(endTime,"d") +")");
					    whlSql.append(" and ("+buf.toString()+") ");
					}		    		
			    }
			}
			// 审批状态查询
			if (spSatus != null && !"".equals(spSatus) && !"all".equals(spSatus))
			{
			    if("4".equals(spSatus))
				whlSql.append("and status in ('4','6')");
			    else
				whlSql.append("and status = '"+spSatus+"'  ");
			}
			
			strSql.append(whlSql.toString());
			strSql.append("order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc");
	
			HashMap map =null;
			map=bo.getPlansUserView(this.userView, whlSql.toString());
	
			// 将空排序字段按计划号的倒序自动赋值   
			this.frowset=dao.search("select plan_id,a0000 from per_plan where a0000 is null order by plan_id desc ");
			int maxA0000=1;
			while (this.frowset.next())
			{
				dao.update("update per_plan set a0000=" + maxA0000 + " where plan_id=" + this.frowset.getString("plan_id"));  
				maxA0000++;
			}		   		    		    
		    
		    this.frowset = dao.search(strSql.toString());
		    int count = 1;
		    while (this.frowset.next())
		    {     
		    	if(map.get(this.frowset.getString("plan_id"))==null)
		    		continue;
								
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("count", String.valueOf(count));
				abean.set("plan_id", isNull(this.frowset.getString("plan_id")));
				abean.set("status", isNull(this.frowset.getString("status")));
				abean.set("name", isNull(this.frowset.getString("name")));
				abean.set("plan_type", isNull(this.frowset.getString("plan_type")));				
				abean.set("b0110", isNull(this.frowset.getString("b0110")));				
				abean.set("object_type", isNull(this.frowset.getString("object_type")));
				abean.set("cycle", isNull(this.frowset.getString("cycle")));
				abean.set("gather_type", isNull(this.frowset.getString("gather_type")));
				abean.set("method", !"2".equals(isNull(this.frowset.getString("method")))?"1":"2");
				abean.set("start_date", isNull(PubFunc.FormatDate(this.frowset.getDate("start_date")))); 
				abean.set("end_date", isNull(PubFunc.FormatDate(this.frowset.getDate("end_date"))));	
				abean.set("template_id", isNull(this.frowset.getString("template_id")));				
				abean.set("agree_user", isNull(this.frowset.getString("agree_user")));  
				abean.set("agree_date", isNull(PubFunc.FormatDate(this.frowset.getDate("agree_date"))));
				abean.set("approve_result", isNull(this.frowset.getString("approve_result")));				  
				abean.set("agree_idea", PubFunc.toHtml(isNull(this.frowset.getString("agree_idea")).length()>20?this.frowset.getString("agree_idea").substring(0, 20)+"...":isNull(this.frowset.getString("agree_idea"))));
				abean.set("descript", PubFunc.toHtml(isNull(this.frowset.getString("descript")).length()>20?this.frowset.getString("descript").substring(0, 20)+"...":isNull(this.frowset.getString("descript"))));		
				abean.set("target", PubFunc.toHtml(isNull(this.frowset.getString("target")).length()>20?this.frowset.getString("target").substring(0, 20)+"...":isNull(this.frowset.getString("target"))));				
				abean.set("content", PubFunc.toHtml(isNull(this.frowset.getString("content")).length()>20?this.frowset.getString("content").substring(0, 20)+"...":isNull(this.frowset.getString("content"))));
				abean.set("flow", PubFunc.toHtml(isNull(this.frowset.getString("flow")).length()>20?this.frowset.getString("flow").substring(0, 20)+"...":isNull(this.frowset.getString("flow"))));
				abean.set("result", PubFunc.toHtml(isNull(this.frowset.getString("result")).length()>20?this.frowset.getString("result").substring(0, 20)+"...":isNull(this.frowset.getString("result"))));				
				abean.set("create_user", isNull(this.frowset.getString("create_user")));
				abean.set("parameter_content", isNull(this.frowset.getString("parameter_content")));
				abean.set("create_date", isNull(PubFunc.FormatDate(this.frowset.getDate("create_date"))));
				abean.set("plan_visibility", this.frowset.getString("plan_visibility")==null?"0": this.frowset.getString("plan_visibility"));				
				abean.set("theyear", isNull(this.frowset.getString("theyear")));
				abean.set("themonth", isNull(this.frowset.getString("themonth")));
				abean.set("thequarter", isNull(this.frowset.getString("thequarter")));
				String byModel = isNull(this.frowset.getString("bymodel"));
				if(byModel!=null && byModel.trim().length()>0 && "1".equalsIgnoreCase(byModel))
					abean.set("byModelName", "素质模板");
				abean.set("byModel", byModel);
				abean.set("a0000",  isNull(this.frowset.getString("a0000")));
				abean.set("feedback", isNull(this.frowset.getString("feedback")));
				count++;
				
				list.add(abean);
		    }
	
		   
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		return list;
    }

	/**
	 * 取得当前的季度
	 * 
	 * @return
	 */
	public int getCurrentJD()
	{
		int jd = 1;
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		month = month + 1;
		switch (month)
		{
			case 1:
			case 2:
			case 3:
				jd = 1;
				break;
			case 4:
			case 5:
			case 6:
				jd = 2;
				break;
			case 7:
			case 8:
			case 9:
				jd = 3;
				break;
			default:
				jd = 4;
		}
		return jd;
	}

	public String isNull(String str)
	{
		if (str == null)
			str = "";
		return str;
	}

	public String getDatePart(String mydate, String datepart)
	{
		String str = "";
		if ("y".equalsIgnoreCase(datepart))
			str = mydate.substring(0, 4);
		else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6)))
				str = mydate.substring(6, 7);
			else
				str = mydate.substring(5, 7);
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9)))
				str = mydate.substring(9, 10);
			else
				str = mydate.substring(8, 10);
		}
		return str;
	}
	
	public void moveRecord(String move,String a_planid,ArrayList dataList) throws GeneralException
	{
		HashMap map = new HashMap();
    	HashMap map2 = new HashMap();
    	int count = 0;
    	for(int i=0;i<dataList.size();i++)
    	{
    		count++;
    		LazyDynaBean bean = (LazyDynaBean)dataList.get(i);
    		String plan_id = (String)bean.get("plan_id");
    		String a0000 = (String)bean.get("a0000");
    		if(a0000.trim().length()==0)
    			continue;
    		map.put("plan_"+plan_id, Integer.toString(count));
	    	map.put(Integer.toString(count), a0000); 
	    	map2.put("a0000_"+a0000, plan_id);
    	}
    	
        String a_index  =(String)map.get("plan_"+a_planid);
	    String a_a0000=(String)map.get(a_index);
	    if(a_index==null || a_a0000==null)
	    	throw new GeneralException("排序字段为空值,无法重新排序！");
	    if("up".equalsIgnoreCase(move) && "1".equals(a_index))
	    	throw new GeneralException("已经是第一条记录,不允许上移！");
	    if("down".equalsIgnoreCase(move) && Integer.parseInt(a_index)==count)
	    	throw new GeneralException("已经是最后一条记录,不允许下移！");
	    
	    String objid2="";//用于交换的对象
	    String objid2_seq="";
	    
	    if("up".equalsIgnoreCase(move))
	    	objid2_seq = (String)map.get(Integer.toString(Integer.parseInt(a_index)-1));    	    
	    else  if("down".equalsIgnoreCase(move))
	    	objid2_seq = (String)map.get(Integer.toString(Integer.parseInt(a_index)+1));    	
	  
	    objid2 = (String)map2.get("a0000_"+objid2_seq);
	    //相邻对象交换排序字段 a0000
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    try
		{
			dao.update("update per_plan set a0000="+objid2_seq+" where plan_id='"+a_planid+"' ");
		    dao.update("update per_plan set a0000="+a_a0000+" where plan_id='"+objid2+"' ");   
		} catch (SQLException e)		
		{			
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   	   	
	}
	
	// 检查per_plan表中有没有A0000字段，若没有就创建  
    public void editArticleA0000() throws GeneralException
	{
		try
		{			
			Table table = new Table("per_plan");
			DbWizard dbWizard = new DbWizard(this.frameconn);
			DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
			boolean flag = false;
			
			if (!dbWizard.isExistField("per_plan", "A0000",false))
			{
			    Field obj = new Field("A0000");
			    obj.setDatatype(DataType.INT);
			    obj.setKeyable(false);
			    table.addField(obj);
			    flag = true;			    			    
			}
			if (!dbWizard.isExistField("per_plan", "execute_user", false))
			{
				Field obj = new Field("execute_user");	
				obj.setDatatype(DataType.STRING);
				obj.setLength(50);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (!dbWizard.isExistField("per_plan", "execute_date", false))
			{
				Field obj = new Field("execute_date");	
				obj.setDatatype(DataType.DATE);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (!dbWizard.isExistField("per_plan", "distribute_user", false))
			{
				Field obj = new Field("distribute_user");	
				obj.setDatatype(DataType.STRING);
				obj.setLength(50);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (!dbWizard.isExistField("per_plan", "distribute_date", false))
			{
				Field obj = new Field("distribute_date");	
				obj.setDatatype(DataType.DATE);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (!dbWizard.isExistField("per_plan", "ByModel",false))
			{
			    Field obj = new Field("ByModel");
			    obj.setDatatype(DataType.INT);
			    obj.setKeyable(false);
			    table.addField(obj);
			    flag = true;			    			    
			}
			
			if(flag)
			{
				dbWizard.addColumns(table);// 更新列		
			    dbmodel.reloadTableModel("per_plan");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    }
	
}
