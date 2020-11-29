package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchPerformPlanTrans.java</p>
 * <p>Description:考核计划查询</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-06-26 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchPerformPlanTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String b_query = this.isNull((String) hm.get("b_query"), "link");
		hm.remove("query");

		String scrollValue = (String)hm.get("scrollValue");
		this.getFormHM().put("scrollValue", scrollValue);
		
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);		
		
		String busitype = "0"; // 业务分类 =0(绩效考核); =1(能力素质)
		String status = "";
		String name = "";
		String method = "";
		String object_type = "";
		String jxmodul = ""; // 1:考核实施 2:绩效评估 3: 数据采集

		if ("query".equalsIgnoreCase(b_query) || "return".equalsIgnoreCase(b_query))
		{
			busitype = (String) this.getFormHM().get("busitype");
			status = (String) this.getFormHM().get("status");
			name = (String) this.getFormHM().get("name");
			if(name.indexOf("'")!=-1)			
				name = "‘";
			if(name!=null)
			    name = name.trim();
			method = (String) this.getFormHM().get("method");
			object_type = (String) this.getFormHM().get("object_type");
			String obtype = (String) this.getFormHM().get("obtype");
			if(!("".equals(obtype) || obtype ==null)){
				object_type = obtype;
				this.getFormHM().remove("obtype");
			}
			jxmodul = (String) this.getFormHM().get("jxmodul");
		} else if ("link".equalsIgnoreCase(b_query))
		{
			busitype = (String) hm.get("busitype");
			hm.remove("busitype");
			jxmodul = (String) hm.get("jxmodul");
			hm.remove("jxmodul");
			if(jxmodul==null || jxmodul.trim().length()<=0)
				jxmodul = "1";
			status = "all";
			name = "";
			method = "all";
			object_type = "all";
		} else
		{
			busitype = "0";
			jxmodul = "1";
			status = "all";
			name = "";
			method = "all";
			object_type = "all";
		}
		
		if(busitype==null || busitype.trim().length()<=0)
			busitype = "0";

		this.getFormHM().put("status", status);
		this.getFormHM().put("name", name);
		this.getFormHM().put("method", method);
		this.getFormHM().put("object_type", object_type);
		this.getFormHM().put("jxmodul", jxmodul);
		this.getFormHM().put("busitype", busitype);

		ArrayList dataList = new ArrayList();
		HashMap map = new HashMap();
		map.put("0", ResourceFactory.getProperty("hire.jp.pos.draftout"));//起草
		map.put("1", ResourceFactory.getProperty("info.appleal.state1"));//报批
		map.put("2", ResourceFactory.getProperty("label.hiremanage.status3"));//已批
		map.put("3", ResourceFactory.getProperty("button.issue"));//发布
		map.put("4", ResourceFactory.getProperty("gz.formula.implementation"));//执行
		map.put("5", ResourceFactory.getProperty("lable.performance.status.pause"));//暂停
		map.put("6", ResourceFactory.getProperty("jx.khplan.Appraisal"));//评估
		map.put("7", ResourceFactory.getProperty("label.hiremanage.status6"));//结束
		map.put("8", ResourceFactory.getProperty("performance.plan.distribute"));//分发

		ArrayList statusList = new ArrayList();
		if ("1".equals(jxmodul))
		{
			statusList.add(new CommonData("3", (String) map.get("3")));
			if(busitype==null || busitype.trim().length()<=0 || "0".equalsIgnoreCase(busitype))
				statusList.add(new CommonData("8", (String) map.get("8")));
			statusList.add(new CommonData("4", (String) map.get("4")));
			statusList.add(new CommonData("5", (String) map.get("5")));
			statusList.add(new CommonData("6", (String) map.get("6")));
			
		} else if ("2".equals(jxmodul))
		{
			statusList.add(new CommonData("4", (String) map.get("4")));
			statusList.add(new CommonData("6", (String) map.get("6")));
			statusList.add(new CommonData("7", (String) map.get("7")));
			
		} else if ("3".equals(jxmodul))
		{
			statusList.add(new CommonData("4", (String) map.get("4")));
			statusList.add(new CommonData("6", (String) map.get("6")));
		}
		this.getFormHM().put("statusList", statusList);

		DbWizard dbw=new DbWizard(this.frameconn);
		if(!dbw.isExistField("per_plan", "feedback",false))
		{
			Table table=new Table("per_plan");
			Field field=new Field("feedback","feedback");
			field.setDatatype(DataType.INT);
			table.addField(field);
			dbw.addColumns(table);	
		}
		
		StringBuffer strSql = new StringBuffer();
		strSql.append("select * from per_plan where 1=1 ");
		
		if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype))
			strSql.append(" and ( busitype is null or busitype = " + busitype + ") ");
		else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			strSql.append(" and busitype = " + busitype + " ");
		
		//条件语句
		StringBuffer whlSql = new StringBuffer();	
		if ("1".equals(jxmodul))// 考核实施
			whlSql.append(" and status in (3,4,5,6,8) ");
		else if ("2".equals(jxmodul))// 绩效评估
			whlSql.append(" and status in (4,6,7) ");
		else if ("3".equals(jxmodul))// 数据采集    修改为只查询 360 计划   JinChunhai  2011.05.17
			whlSql.append(" and status in (4,6) and (method is null or method =1) ");
		 
		if (!"all".equalsIgnoreCase(name.trim()))
			whlSql.append(" and name like '%" + name + "%'");				
		if ((!"3".equals(jxmodul)) && (!"all".equalsIgnoreCase(method.trim())))
		{
			if ("1".equalsIgnoreCase(method.trim()))
				whlSql.append(" and (method is null or method =1) ");
			else
				whlSql.append(" and  method =2 ");
		}		
		if (!"all".equalsIgnoreCase(object_type.trim()))
		{
			if("1".equalsIgnoreCase(object_type.trim()))
				whlSql.append(" and object_type in(1,3,4) ");
			else
				whlSql.append(" and object_type =" + object_type);
		}
		if (!"all".equalsIgnoreCase(status.trim()))
			whlSql.append(" and status =" + status);
		
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);		
		strSql.append(bo.getPlanWhlByObjTypePriv(this.userView,busitype));
		
		strSql.append(whlSql.toString());
		strSql.append(" order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc");

		try
		{
		
//			HashMap planMap = bo.getHaveTempPrivPlans(this.userView);
			HashMap planMap = bo.getPlansByUserView(this.userView, whlSql.toString());
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(strSql.toString());
			while (this.frowset.next())
			{
				if(planMap.get(this.frowset.getString("plan_id"))==null)
					continue;				
				
				if ("3".equals(jxmodul))// 数据采集
				{
					
					String gather_type = this.frowset.getString("gather_type");
					if("1".equals(gather_type) || "2".equals(gather_type)) //机读
						this.addList(dataList, map);
					else
					{	// 打分途径为数据采集 手工启动打分的计划
						String parameter_content = Sql_switcher.readMemo(this.frowset, "parameter_content");
						if (parameter_content.length() > 0)
						{
							LoadXml xmlBo = new LoadXml(this.frameconn, parameter_content, 1);
						    Hashtable ht = xmlBo.getDegreeWhole();
						    if (ht != null && ht.get("scoreWay") != null && "0".equals((String) ht.get("scoreWay")))
						    {						    	
						    	if(ht.get("HandEval") != null && "FALSE".equalsIgnoreCase((String) ht.get("HandEval")))
						    		this.addList(dataList, map);					    	
						    }
						}
					}					
				}else				
					this.addList(dataList, map);							
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("dataList", dataList);
	}

	public String isNull(String str, String repValue)
	{
		if (str == null)
			str = repValue;
		return str;
	}		
	
	public void addList(ArrayList dataList,HashMap map) throws SQLException
	{
		RecordVo vo = new RecordVo("per_plan");
		vo.setString("plan_id", this.frowset.getString("plan_id"));
		String temp="";
		if("1".equals(this.frowset.getString("feedback"))&&"6".equals(this.frowset.getString("status"))){
			temp = "【结果反馈】";
		}
		vo.setString("name", this.frowset.getString("name"));
		vo.setString("object_type", this.frowset.getString("object_type"));
		vo.setString("cycle", this.frowset.getString("cycle"));
		vo.setString("method", !"2".equals(isNull(this.frowset.getString("method"), "")) ? "1" : "2");
		vo.setString("theyear", isNull(this.frowset.getString("theyear"), ""));
		vo.setString("themonth", isNull(this.frowset.getString("themonth"), ""));
		vo.setString("thequarter", isNull(this.frowset.getString("thequarter"), ""));
		vo.setDate("start_date", this.frowset.getDate("start_date"));
		vo.setDate("end_date", this.frowset.getDate("end_date"));
		//haosl start 20170418  vo.setString("status",xxx)会根据status字段的长度来截取xxx字符串，导致前台显示不完整
		HashMap values = vo.getValues();//得到vo存储数据的map
		values.put("status",(String) map.get(this.frowset.getString("status"))+temp);
		vo.setValues(values);//将map重新设回vo,这样可以避免截取status
		//haosl end 20170418 
		dataList.add(vo);
	}
}
