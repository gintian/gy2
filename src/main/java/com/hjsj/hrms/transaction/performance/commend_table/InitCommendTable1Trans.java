package com.hjsj.hrms.transaction.performance.commend_table;

import com.hjsj.hrms.businessobject.performance.commend_table.CommendTableBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
public class InitCommendTable1Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			int num=0;
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String recommend_time=(String)map.get("D0150");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rowSet=null;
			ArrayList recommend_person_list=new ArrayList();
			LazyDynaBean abean=new LazyDynaBean();
			int limitNum=0;         //限定人数
			String un_limit_item="";   
			String recommend_unit="";    //推荐单位
			String recommend_flag_item="";   //推荐班子指标
			String table1_status="0";
			if(SystemConfig.getPropertyValue("recommend_unit_item")!=null&&SystemConfig.getPropertyValue("recommend_unit_item").trim().length()>0)
			{
				String item_id=SystemConfig.getPropertyValue("recommend_unit_item").trim();
				rowSet=dao.search("select "+item_id+" from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"'");
				if(rowSet.next())
					recommend_unit=rowSet.getString(1)!=null?rowSet.getString(1):"";
			}
			if(recommend_unit.length()==0)
				throw GeneralExceptionHandler.Handle(new Exception("当前人员的推荐单位没有设置，请与系统管理员联系!"));
			
			if(SystemConfig.getPropertyValue("un_limit_item")!=null&&SystemConfig.getPropertyValue("un_limit_item").trim().length()>0)
				un_limit_item=SystemConfig.getPropertyValue("un_limit_item").trim();
			
			if(un_limit_item.length()==0)
				throw GeneralExceptionHandler.Handle(new Exception("单位班子职数指标没在配置文件中定义!"));
			rowSet=dao.search("select "+un_limit_item+" from B01 where b0110='"+recommend_unit+"'");
			if(rowSet.next())
				limitNum=rowSet.getInt(1);
			if(limitNum==0)
				throw GeneralExceptionHandler.Handle(new Exception("单位领导班子推荐数没有设置，请与系统管理员联系!"));
			
		/*//	System.out.println("select a0101_1,a0100_1 from per_recommend_result where a0100='"+this.userView.getA0100()+"' and upper(nbase)='"+this.userView.getDbname().toUpperCase()+"' and a0101_1!='无合适人员'  and c11=1 order by order_num");
			rowSet=dao.search("select a0101_1,a0100_1 from per_recommend_result where a0100='"+this.userView.getA0100()+"' and upper(nbase)='"+this.userView.getDbname().toUpperCase()+"' and a0101_1!='无合适人员'  and c11=1 order by order_num");
			
			while(rowSet.next())
			{
				num++;
				abean=new LazyDynaBean();
				abean.set("a0100",rowSet.getString("a0100_1"));
				abean.set("a0101",rowSet.getString("a0101_1"));
				recommend_person_list.add(abean);
			}
			
			for(int i=++num;i<=limitNum;i++)
			{
				abean=new LazyDynaBean();
				abean.set("a0100","");
				abean.set("a0101","");
				recommend_person_list.add(abean);
			}*/
		
			if(SystemConfig.getPropertyValue("recommend_flag_item")!=null&&SystemConfig.getPropertyValue("recommend_flag_item").trim().length()>0)
			{
				recommend_flag_item=SystemConfig.getPropertyValue("recommend_flag_item").trim();
			}
			
			rowSet=dao.search("select distinct flag from per_recommend_result  where a0100='"+userView.getA0100()+"' and upper(nbase)='"+userView.getDbname().toUpperCase()+"'   and c11=1");
			if(rowSet.next())
				table1_status=rowSet.getString("flag");
			CommendTableBo bo = new CommendTableBo(this.getFrameconn(),this.getUserView(),2);
			recommend_person_list=bo.getCommendList2(recommend_unit, recommend_flag_item);
			
			this.getFormHM().put("table1_status",table1_status);
			this.getFormHM().put("limitNum",String.valueOf(limitNum));
			this.getFormHM().put("recommend_person_list",recommend_person_list);
			this.getFormHM().put("recommend_flag_item",recommend_flag_item.trim());
			this.getFormHM().put("recommend_unit",recommend_unit);
			this.getFormHM().put("recommend_time", recommend_time);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
