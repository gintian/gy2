package com.hjsj.hrms.transaction.performance.scoreAjust;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchPlanObjListTrans.java</p>
 * <p>Description:评分调整</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-15 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class SearchPlanObjListTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		
		String year="all";
		String quarter="all";
		String month="all";
		String object_type="2";
		String objectname = "";//按名称值查询
		ArrayList dataList = new ArrayList();
		ArrayList yearList = new ArrayList();
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String b_query = this.isNull((String) hm.get("b_query"), "link");
		hm.remove("query");	

		if ("query".equalsIgnoreCase(b_query) || "return".equalsIgnoreCase(b_query))
		{
			year = (String) this.getFormHM().get("year");
			quarter = (String) this.getFormHM().get("quarter");
			month = (String) this.getFormHM().get("month");
			object_type = (String) this.getFormHM().get("object_type");
			objectname = (String) this.getFormHM().get("objectname");
		} else if ("link".equalsIgnoreCase(b_query))
		{
			year = "all";
			month = "all";
			quarter = "all";
			object_type = "2";
			objectname = "";
		}

		this.getFormHM().put("year", year);
		this.getFormHM().put("quarter", quarter);
		this.getFormHM().put("month", month);
		this.getFormHM().put("object_type", object_type);
		this.getFormHM().put("objectname", objectname);
		
		//条件语句
		StringBuffer whlSql = new StringBuffer(" and status in (4,6) ");	//评估和执行状态的计划(包括目标和360)
		if ("2".equalsIgnoreCase(object_type.trim()))
			whlSql.append(" and object_type=2");
		else
			whlSql.append(" and object_type in (1,3,4)");
		if (!"all".equalsIgnoreCase(year.trim()))
			whlSql.append(" and theyear='" + year+"' ");
		if (!"all".equalsIgnoreCase(month.trim()))
		{
			whlSql.append(" and (( cycle<>7 and themonth='" + month+"' ) or ( cycle=7 and "+Sql_switcher.month("start_date")+"<="+month+" and "+Sql_switcher.month("end_date")+">="+month+"  ))");
		}
		if (!"all".equalsIgnoreCase(quarter.trim()))
		{
			whlSql.append(" and ( (cycle<>7 and ( thequarter='" + quarter+"' ");
			if("01".equals(quarter.trim()))
			{
				whlSql.append(" or themonth in ('01','02','03'))) "); 
				whlSql.append(" or  (cycle=7 and ( "+Sql_switcher.month("start_date")+" in (1,2,3) or "+Sql_switcher.month("end_date")+" in (1,2,3) ))  ) ");
			}
			else if("02".equals(quarter.trim()))
			{
				whlSql.append(" or themonth in ('04','05','06'))) ");
				whlSql.append(" or  (cycle=7 and ( "+Sql_switcher.month("start_date")+" in (4,5,6) or "+Sql_switcher.month("end_date")+" in (4,5,6) ))  ) ");
			}
			else if("03".equals(quarter.trim()))
			{
				whlSql.append(" or themonth in ('07','08','09'))) ");
				whlSql.append(" or  (cycle=7 and ( "+Sql_switcher.month("start_date")+" in (7,8,9) or "+Sql_switcher.month("end_date")+" in (7,8,9) ))  ) ");
			}
			else if("04".equals(quarter.trim()))
			{
				whlSql.append(" or themonth in ('10','11','12'))) ");
				whlSql.append(" or  (cycle=7 and ( "+Sql_switcher.month("start_date")+" in (10,11,12) or "+Sql_switcher.month("end_date")+" in (10,11,12) ))  ) ");
			}
		}
			
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			ExamPlanBo bo = new ExamPlanBo(this.frameconn,this.userView,"");
		    ArrayList planList = bo.getScoreAjustPlans(whlSql.toString());
			StringBuffer buf = new StringBuffer();
			for (int i=0;i<planList.size();i++)
			{
				String planid = (String) planList.get(i);
				
				/**获取考核结果表 per_result_xxx 的表结构 */	
				boolean flag = false;
				RowSet rowSet =null;									
				rowSet = dao.search("Select * from per_result_"+planid+" where 1=2");		           
				ResultSetMetaData metaData = rowSet.getMetaData();			
				for (int k = 1; k <= metaData.getColumnCount(); k++) 
				{				    
					String colnames = metaData.getColumnName(k);				
					if("ex_GrpNum".equalsIgnoreCase(colnames))
					{
						flag = true;
						break;
					}				
				}								
				if(flag)						
					buf.append(" or plan_id="+planid);
			}				
		
			String sql = "select distinct theyear from per_plan where theyear is not null and ";
			if(buf.length()==0)
				sql+="1=2";
			else
				sql+="("+buf.substring(4)+")";
			sql+=" order by theyear";
			this.frowset = dao.search(sql);
			while (this.frowset.next())
			{
				String theyear = this.frowset.getString("theyear");
				if(theyear.trim().length()>0)
					yearList.add(new CommonData(theyear,theyear));
			}
			this.getFormHM().put("yearList", yearList);
			
			
			sql=" select * from per_plan where 1=1 and ";
			if(buf.length()==0)
				sql+="1=2";
			else
				sql+="("+buf.substring(4)+")";
			sql+=" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";			
			this.frowset = dao.search(sql);
			boolean showGrpOrder=true;
			while (this.frowset.next())
			{
				String cycle = this.frowset.getString("cycle");			
				String theyear = this.frowset.getString("theyear");
				String themonth = this.frowset.getString("themonth");
				String thequarter = this.frowset.getString("thequarter");
				java.sql.Date start_date=this.frowset.getDate("start_date");
				java.sql.Date end_date=this.frowset.getDate("end_date");
				String khPeriod = ExamPlanBo.getKhPeriod(cycle, theyear, themonth, thequarter, start_date, end_date);
				String plan_id = this.frowset.getString("plan_id");
				String name = this.frowset.getString("name");
				
				String keepDecimal ="2";
				String parameter_content = Sql_switcher.readMemo(this.frowset, "parameter_content");
				if (parameter_content.length() > 0)
				{
					LoadXml xmlBo = new LoadXml(this.frameconn, parameter_content, 1);
				    Hashtable ht = xmlBo.getDegreeWhole();
				    keepDecimal = (String) ht.get("KeepDecimal");
				    if(showGrpOrder && ht.get("ShowGrpOrder")!=null && "false".equalsIgnoreCase((String) ht.get("ShowGrpOrder")))
				    	showGrpOrder=false;
				}				
				
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("plan_id", plan_id);
				abean.set("name", name);
				abean.set("khPeriod", khPeriod);
				abean.set("KeepDecimal",keepDecimal);
				dataList.add(abean);
			}
			
			PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
			String khObjWhl = pb.getPrivWhere(userView);// 根据用户权限先得到一个考核对象的范围
			String str = "";
			if(objectname!=null&&objectname.length()>0){
				str = " and a0101 like '%"+objectname+"%' ";
			}
			ArrayList dataList_temp = new ArrayList();
			for(int i=0;i<dataList.size();i++)
			{
				LazyDynaBean abean = (LazyDynaBean)dataList.get(i);
				String plan_id = (String)abean.get("plan_id");
				String name = (String)abean.get("name");
				String khPeriod = (String)abean.get("khPeriod");
				int keepDecimal = Integer.parseInt((String)abean.get("KeepDecimal"));
					
				this.testFields(plan_id);
				sql="select * from per_result_"+plan_id+" where 1=1 "+khObjWhl+" "+str+"  order by a0000";
				this.frowset = dao.search(sql);
				while (this.frowset.next())
				{
					String object_id = this.frowset.getString("object_id")==null?"":this.frowset.getString("object_id");
					String b0110 = this.frowset.getString("b0110")==null?"":this.frowset.getString("b0110");
					String e0122 = this.frowset.getString("e0122")==null?"":this.frowset.getString("e0122");
					if(b0110.length()!=0)
						b0110=AdminCode.getCodeName("UN",b0110);
					if(e0122.length()!=0)
						e0122=AdminCode.getCodeName("UM",e0122);
					String a0101 = this.frowset.getString("a0101")==null?"":this.frowset.getString("a0101");
					String resultdesc = this.frowset.getString("resultdesc")==null?"":this.frowset.getString("resultdesc");
					String score = PubFunc.round(this.frowset.getString("score"), keepDecimal);
					String ordering = this.frowset.getString("ordering")==null?"":this.frowset.getString("ordering");
					String ex_GrpNum = this.frowset.getString("ex_GrpNum")==null?"":this.frowset.getString("ex_GrpNum");
					String score_adjust = (this.frowset.getString("score_adjust")==null || "".equals(this.frowset.getString("score_adjust")))?"0":this.frowset.getString("score_adjust");
					if("0".equals(score_adjust)|| "1".equals(score_adjust))
						score_adjust="adjust";//调整
					else
						score_adjust="view";//查看
					LazyDynaBean _abean = new LazyDynaBean();
					_abean.set("plan_id", SafeCode.encode(PubFunc.encrypt(plan_id)));
					_abean.set("name", name);
					_abean.set("khPeriod", khPeriod);
					_abean.set("b0110_e0122", b0110+"/"+e0122);
					_abean.set("a0101", a0101);
					_abean.set("object_id", SafeCode.encode(PubFunc.encrypt(object_id)));
					_abean.set("resultdesc", resultdesc);
					_abean.set("score", score);
					_abean.set("score_adjust", score_adjust);	
					_abean.set("ordering", ordering+"/"+ex_GrpNum);
					dataList_temp.add(_abean);
				}
			}
			dataList=dataList_temp;
			this.getFormHM().put("dataList", dataList);
			this.getFormHM().put("showGrpOrder", showGrpOrder?"1":"0");
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public String isNull(String str, String repValue)
	{
		if (str == null)
			str = repValue;
		return str;
	}
	public void testFields(String planid) throws GeneralException
	{
		try
		{
			String tablename = "per_result_" + planid;
			Table table = new Table(tablename);
			DbWizard dbWizard = new DbWizard(this.frameconn);
			boolean flag = false;
			if (!dbWizard.isExistField(tablename, "score_adjust", false))
			{
				Field obj = new Field("score_adjust");	
				obj.setDatatype(DataType.INT);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (flag)
				dbWizard.addColumns(table);// 更新列

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
