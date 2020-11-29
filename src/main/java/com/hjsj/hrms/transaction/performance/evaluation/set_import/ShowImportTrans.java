package com.hjsj.hrms.transaction.performance.evaluation.set_import;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:ShowImportTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 19, 2008:4:48:58 PM</p> 
 *@author JinChunhai
 *@version 5.0
 */

public class ShowImportTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String busitype = (String) hm.get("busitype"); // 业务分类字段 =0(绩效考核); =1(能力素质)
		
		if(busitype==null || busitype.trim().length()<=0)
			busitype = "0";
		
		// TODO Auto-generated method stub
		String planid = (String)this.getFormHM().get("planid");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
		boolean _flag = _bo.isHavePriv(this.userView, planid);
		if(!_flag){
			return;
		}
		LoadXml loadxml = new LoadXml(this.frameconn,planid,"");
		ArrayList planlist = loadxml.getRelatePlanValue("Plan","ID");
		HashMap map = new HashMap();
		for(int i=0;i<planlist.size();i++)
		{
			String relaPlan = planlist.get(i).toString();
			String planMenus = loadxml.getRelatePlanMenuValue(relaPlan);
			map.put(relaPlan, planMenus);
		}		
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		RecordVo vo = new RecordVo("per_plan");
		ArrayList searchlist = new ArrayList();
		ArrayList choicelist = new ArrayList();
		ArrayList relatelist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from per_plan where status in ('6','7') ");
		
		if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype))
			sql.append(" and ( busitype is null or busitype='' or busitype = '" + busitype + "') ");
		else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			sql.append(" and busitype = '" + busitype + "' ");
		
		sql.append(" and theyear>(" + Sql_switcher.toYear() + "-2) ");// 中科实业需求-引入时仅显示本年度和上年度已结束的考核计划 chent 20161014
		
		sql.append(" order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc ");
		try 
		{
			int object_type=2;
			this.frowset = dao.search("select object_type from per_plan where plan_id="+planid);
			if(this.frowset.next())
				object_type = this.frowset.getInt(1);			
			
			this.frowset = dao.search(sql.toString());
			
			ExamPlanBo bo = new ExamPlanBo(this.frameconn);
			HashMap hasmap = bo.getPlansByUserView(this.userView, "");
									
			while(this.frowset.next())
			{
				int x = 0;
				for(int i=0;i<planlist.size();i++)
				{
					if(this.frowset.getString("plan_id").equalsIgnoreCase(planlist.get(i).toString()))
					{
						x=1;
					}
				}
				if(this.frowset.getString("plan_id").equalsIgnoreCase(planid))
					x=1;
				if(x==0)
				{
					//人员类型计划可以引入所有类型的计划 非人员类型的计划只能引入非人员类型的计划
					if(hasmap.get(this.frowset.getString("plan_id"))==null)
			    		continue;
					
					if(object_type!=2)
					{
						if(this.frowset.getInt("object_type")!=2)
							searchlist.add(this.frowset.getString("plan_id"));
					}else if(object_type==2)
						searchlist.add(this.frowset.getString("plan_id"));
				}
					
			}
			
//		降序排planlist
	
			for(int i=0;i<planlist.size();i++)
			{
				for(int j=0;j<planlist.size()-1-i;j++)
				{
					if( Integer.parseInt((String)planlist.get(j+1))>Integer.parseInt((String)planlist.get(j)))
					{
						String temp = (String)planlist.get(j+1);
						planlist.set(j+1, (String)planlist.get(j));
						planlist.set(j,temp);
					}
				}
			}
			
			
			relatelist = this.getList(vo,planlist,dao,map);
			choicelist = this.getList(vo,searchlist,dao,new HashMap());
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("relatelist",relatelist);
		this.getFormHM().put("choicelist",choicelist);
		//System.out.println(planid);
		
	}
	
	private ArrayList getList(RecordVo vo,ArrayList list,ContentDAO dao,HashMap map) throws SQLException,GeneralException
	{
		ArrayList clist = new ArrayList();
		try{
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);			
		for(int i=0;i<list.size();i++)
		{
		    if(!bo.isExist(list.get(i).toString()))
			continue;
		    
		    String planid = list.get(i).toString();			    		    
			vo.setString("plan_id",planid);
			vo = dao.findByPrimaryKey(vo);
			LazyDynaBean bean = new LazyDynaBean();
			String plan_id = String.valueOf(vo.getInt("plan_id"));
			String planMenus = map.get(plan_id)==null?"":(String)map.get(plan_id);
			bean.set("plan_id",String.valueOf(vo.getInt("plan_id")));
			bean.set("name",vo.getString("name")==null?"":vo.getString("name"));
			String cycle = vo.getString("cycle")==null?"":vo.getString("cycle");
			bean.set("cycle",cycle);
			bean.set("planMenus",planMenus);
			if("0".equalsIgnoreCase(cycle))
				bean.set("cyclegb","年度");
			else if("1".equalsIgnoreCase(cycle))
				bean.set("cyclegb","半年");
			else if("2".equalsIgnoreCase(cycle))
				bean.set("cyclegb","季度");
			else if("3".equalsIgnoreCase(cycle))
				bean.set("cyclegb","月度");
			else if("7".equalsIgnoreCase(cycle))
				bean.set("cyclegb","不定期");
			String thequarter = vo.getString("thequarter")==null?"":vo.getString("thequarter");
			String theYear = vo.getString("theyear");
			bean.set("thequarter",thequarter);
			if("01".equalsIgnoreCase(thequarter))
				bean.set("thequartergb",theYear+"年 第一季度");
			else if("02".equalsIgnoreCase(thequarter))
				bean.set("thequartergb",theYear+"年 第二季度");
			else if("03".equalsIgnoreCase(thequarter))
				bean.set("thequartergb",theYear+"年 第三季度");
			else if("04".equalsIgnoreCase(thequarter))
				bean.set("thequartergb",theYear+"年 第四季度");
			else if("1".equalsIgnoreCase(thequarter))
				bean.set("halfyeargb",theYear+"年 上半年");
			else if("2".equalsIgnoreCase(thequarter))
				bean.set("halfyeargb",theYear+"年 下半年");
			
			bean.set("theyeargb",vo.getString("theyear")+"年");
			
			//xuj update  themoth值有些入口会保存为例如6不是06，导致用先前借钱方式则出错 2014-12-16
			//String month = Integer.parseInt(vo.getString("themonth").substring(0,1))==1?vo.getString("themonth"):vo.getString("themonth").substring(1);
			String themonth = vo.getString("themonth");
			themonth=themonth==null||themonth.length()==0?"0":themonth;
			String month = ("0".equals(themonth.substring(0,1))&&themonth.length()==2)?themonth.substring(1):themonth;
			String[] bigMonth = {"","一","二","三","四","五","六","七","八","九","十","十一","十二"};
			bean.set("themonthgb",vo.getString("theyear")+"年 "+bigMonth[Integer.parseInt(month)]+"月");
			
			String start_date="";
			String end_date="";
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
			{
			    start_date = vo.getDate("start_date")==null?"":(vo.getDate("start_date")).toString();
			    end_date = vo.getDate("end_date")==null?"":(vo.getDate("end_date")).toString();
			}else
			{
			    start_date = vo.getString("start_date")==null?"":vo.getString("start_date");
			    end_date = vo.getString("end_date")==null?"":vo.getString("end_date");
			}
			
			
			start_date = PubFunc.replace(start_date, "-", ".");
			end_date = PubFunc.replace(end_date, "-", ".");
			bean.set("yeartoyear",start_date.length()==0?"":start_date.substring(0,10)+"-"+(end_date.length()==0?"":end_date.substring(0,10)));
//			bean.set("yeartoyear",vo.getDate("start_date")==null?"":String.valueOf(vo.getDate("start_date"))+"-"+vo.getDate("end_date")==null?"":String.valueOf(vo.getDate("end_date")));
			if("1".equalsIgnoreCase(vo.getString("object_type")))
				bean.set("object_typegb","团队");
			else if("2".equalsIgnoreCase(vo.getString("object_type")))
				bean.set("object_typegb","人员");
			else if("3".equalsIgnoreCase(vo.getString("object_type")))
				bean.set("object_typegb","单位");
			else if("4".equalsIgnoreCase(vo.getString("object_type")))
				bean.set("object_typegb","部门");
			bean.set("object_type",vo.getString("object_type"));
			clist.add(bean);
		}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return clist;
	}


}
