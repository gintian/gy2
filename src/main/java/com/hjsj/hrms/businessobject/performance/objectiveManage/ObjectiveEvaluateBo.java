package com.hjsj.hrms.businessobject.performance.objectiveManage;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ObjectiveEvaluateBo {
	private Connection conn;
	private HashMap map;
	private RecordVo plan_vo=null;
	private ArrayList  p04List=new ArrayList();
	private HashMap    p04Map=new HashMap();
	private Hashtable planParam=null;
	private ArrayList planList=new ArrayList();
	private HashMap objectMap=new HashMap();
	private UserView userView;
	public ObjectiveEvaluateBo(Connection conn)
	{
		this.conn=conn;
	}
	public ObjectiveEvaluateBo(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}
	/**
	 * 暂时不用这个处理逻辑
	 * @param view
	 * @return
	 */
	public HashMap getPerMainbodyData(UserView view)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select * from per_mainbody where status='2' and ");
			buf.append(" mainbody_id='"+view.getA0100()+"'");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(buf.toString());
			while(rs.next())
			{
				map.put((rs.getString("object_id")+rs.getString("plan_id")).toLowerCase(), "1");
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 暂时不用这个处理逻辑
	 * @param objectid
	 * @param planid
	 * @param dao
	 * @return
	 */
	/*public int getp04(String objectid,String planid,ContentDAO dao)
	{
		int i=0;
		try
		{
			String sql="select count(*) total from p04 where plan_id="+planid+"  and a0100='"+objectid+"'  and state=-1";
		    RowSet rs =null;
		    rs=dao.search(sql);
		    while(rs.next())
		    {
		    	i=rs.getInt("total");
		    }
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}*/
	private static HashMap p04TzMap = new HashMap();
	public int getp04(String objectid,String planid,ContentDAO dao)
	{
		int i=0;
		if(p04TzMap.get(objectid+planid)==null)
		{
	    	try
	    	{
	    		String sql="select count(*) total,a0100,plan_id from p04 where plan_id="+planid+" and a0100='"+objectid+"' state=-1 group by plan_id,a0100";
	    	    RowSet rs =null;
	    	    rs=dao.search(sql);
	    	    HashMap amap=new HashMap();
	     	    while(rs.next())
	    	    {
	    	    	int j=rs.getInt("total");
	    	    	String a0100=rs.getString("a0100");
	    	    	i=j;
	    	    	amap.put(a0100+planid, j+"");
	    	    }
	    	    rs.close();
 	    	}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
		}
		else
		{
			if(p04TzMap.get(objectid+planid)!=null)
			{
				i=Integer.parseInt((String)p04TzMap.get(objectid+planid));
			}
		}
		return i;
	}
	private static HashMap mainBodyDataMap = new HashMap();
	public HashMap getPerMainBodyData(UserView userview)
	{
		HashMap map = null;
		try
		{
			if(mainBodyDataMap.get(userview.getA0100())!=null)
				map=(HashMap)mainBodyDataMap.get(userview.getA0100());
			else
			{
	     		StringBuffer buf = new StringBuffer();
	    		buf.append("select a.*,");
	    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    		buf.append("b.level_o as lv ");
		    	else
	    			buf.append("b.level as lv ");
	    		buf.append(" from per_mainbody a left join per_mainbodyset b on a.body_id=b.body_id where mainbody_id='"+userview.getA0100()+"'");
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		RowSet rs = dao.search(buf.toString());
	    		map = new HashMap();
	    		while(rs.next())
	    		{
	    			LazyDynaBean bean = new LazyDynaBean();
	    			bean.set("status",rs.getString("status")==null?"":rs.getString("status"));
		    		bean.set("object_id", rs.getString("object_id"));
		    		bean.set("mainbody_id",rs.getString("mainbody_id"));
			    	bean.set("body_id", rs.getString("body_id")==null?"":rs.getString("body_id"));
			    	bean.set("level",rs.getString("lv")==null?"1000":rs.getString("lv"));
			    	map.put(rs.getString("plan_id")+rs.getString("object_id")+rs.getString("mainbody_id"), bean);
		    	}
	    		mainBodyDataMap.put(userview.getA0100(), map);
	    		rs.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getLevelMap(ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select body_id ,";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql+=" level_o ";
			else
				sql+=" level ";
			sql+=" as lv from per_mainbodyset";
			RowSet rs  = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("body_id"),rs.getString("lv"));
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getPlanWhereSQL(String year,String quarter,String month,String status,UserView view,ContentDAO dao,String planid)
	{
		HashMap mp = new HashMap();
		map = new HashMap();
		try
		{
			StringBuffer whereSQL = new StringBuffer("");
			ArrayList yearList  = new ArrayList();
			StringBuffer buf = new StringBuffer("");
			yearList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
			buf.append(" select pp.plan_id,pp.theyear,pp.name,pp.thequarter,pp.themonth,pp.status,pp.cycle,pp.start_date,pp.end_date,");
			/**不考虑不定期的考核计划*/
			buf.append(Sql_switcher.year("pp.start_date")+" as sy,"+Sql_switcher.month("pp.start_date")+" as sm,"+Sql_switcher.day("pp.start_date")+" as sd,");
			buf.append(Sql_switcher.year("pp.end_date")+" as ey,"+Sql_switcher.month("pp.end_date")+" as em,");
			buf.append(Sql_switcher.day("pp.end_date")+" as ed ");
            buf.append(" from per_plan pp where 1=1 and ");
            if("-1".equals(planid))
            {
               buf.append("pp.method='2' ");
               if("-1".equals(status))
                  buf.append(" and (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='7' or pp.status='8') ");// pp.plan_id in (select distinct plan_id from per_mainbody where mainbody_id='"+view.getA0100()+"') and 
               else if("-2".equals(status))
            	  buf.append(" and (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='8') ");
               else
            	   buf.append(" and pp.status="+status);
               if(!"-1".equals(year))
            	   buf.append(" and pp.theyear="+year);   		
                buf.append(" and pp.object_type='2' order by pp.plan_id ");
            }
            else
            {
            	buf.append(" pp.plan_id="+planid);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            RowSet rs = dao.search(buf.toString());
            while(rs.next())
            {
            	String cycle=rs.getString("cycle");
            	int plan_id=rs.getInt("plan_id");
            	LazyDynaBean bean = new LazyDynaBean();
            	if(map.get(rs.getString("theyear"))==null)
    	    	{
    	    		yearList.add(new CommonData(rs.getString("theyear"),rs.getString("theyear")));
    	    		map.put(rs.getString("theyear"), rs.getString("theyear"));
    	    	}
            	if("3".equals(cycle))
            	{
            		int qu=0;
    	    		if(!"-1".equals(quarter))
    	    		{
    	    			qu=this.getFirstMonthInQuarter(quarter);
    	    		}
    	    		String a_month=rs.getString("themonth");
	        		int int_month=Integer.parseInt(a_month);
	        		if("-1".equals(quarter)||(int_month==qu||int_month==(qu+1)||int_month==(qu+2)))
	        		{
	    	    		if("-1".equals(month)||Integer.parseInt(month)==Integer.parseInt(rs.getString("themonth")))
	    	    		{
	    	    			whereSQL.append(" or pm.plan_id="+plan_id);
	    	    			bean.set("planid",rs.getString("plan_id"));
	    	    			bean.set("name", rs.getString("name"));    
	    	    			bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
	    	    			bean.set("status", rs.getString("status"));
	    	    			mp.put(""+plan_id, bean);
	    	    		}
	        		}
	        		
            	}
            	else if("0".equals(cycle))//年度
            	{
            		whereSQL.append(" or pm.plan_id="+plan_id);
            		bean.set("planid",rs.getString("plan_id"));
	    			bean.set("name", rs.getString("name"));    
            		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year"));
            		bean.set("status", rs.getString("status"));
            		mp.put(""+plan_id, bean);
            	}
            	else if("1".equals(cycle))
            	{
            		String a_quarter = rs.getString("thequarter");
	    			if("1".equals(a_quarter))//上半年，算1，2季度
		    		{
		     			if("-1".equals(quarter)||Integer.parseInt(quarter)==1||Integer.parseInt(quarter)==2)
		    			{
		     				if("-1".equals(month)||(1<=Integer.parseInt(month)&&Integer.parseInt(month)<=6))
    						{
		     		    		whereSQL.append(" or pm.plan_id="+plan_id);
		     		    		bean.set("planid",rs.getString("plan_id"));
	    	    		    	bean.set("name", rs.getString("name"));    
	    	    		      	bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.uphalfyear"));
	    	    		    	bean.set("status", rs.getString("status"));
	    	    		    	mp.put(""+plan_id, bean);
    						}	
		    			}
		    		}
	    			else
	    			{
	    				if("-1".equals(quarter)||Integer.parseInt(quarter)==3||Integer.parseInt(quarter)==4)
	    				{
	    					if("-1".equals(month)||(7<=Integer.parseInt(month)&&Integer.parseInt(month)<=12))
    						{
	    			    		whereSQL.append(" or pm.plan_id="+plan_id);
	    			    		bean.set("planid",rs.getString("plan_id"));
	    	    	    		bean.set("name", rs.getString("name"));    
	    	    	    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.downhalfyear"));
	    	    		    	bean.set("status", rs.getString("status"));
	    	    		    	mp.put(""+plan_id, bean);
    						}
	    				}
	    			}
            	}
            	if("7".equals(cycle))
            	{
            		String sy=rs.getString("sy");
        			String sm=rs.getString("sm");
        			String ey=rs.getString("ey");
        			String em=rs.getString("em");
        			int int_year = 0;
        			int int_sy=0;
        			int int_sm=0;
        			int int_ey=0;
        			int int_em=0;
        			
        		    int_year=Integer.parseInt(year);
        		    int_sy=Integer.parseInt(sy);
        		    int_sm=Integer.parseInt(sm);
        		    int_ey=Integer.parseInt(ey);
        		    int_em=Integer.parseInt(em);
     
        			if("-1".equals(year)||(!"-1".equals(year)&&int_sy<=int_year&&int_ey>=int_year))
        			{
        				if("-1".equals(quarter)||(Integer.parseInt(quarter)==1&&((1<=int_sm&&int_sm<=3)||(1<=int_em&&int_em<=3)))
        						||(Integer.parseInt(quarter)==2&&((4<=int_sm&&int_sm<=6)||(4<=int_em&&int_em<=6)))
        						||(Integer.parseInt(quarter)==3&&((7<=int_sm&&int_sm<=9)||(7<=int_em&&int_em<=9)))
        						||(Integer.parseInt(quarter)==4&&((10<=int_sm&&int_sm<=12)||(10<=int_em&&int_em<=12))))
        				{
        			    	int int_month=0;
        		    		if(!"-1".equals(month))
        	    			{
        		    			int_month=Integer.parseInt(month);
        		    		}
        		    		if("-1".equals(month)||(!"-1".equals(month)&&((int_sm<=int_month&&int_em>=int_month))))
        		    		{
        		    			whereSQL.append(" or pm.plan_id="+plan_id);
                				bean.set("planid",rs.getString("plan_id"));
            	    			bean.set("name", rs.getString("name"));    
            	    			bean.set("evaluate",rs.getString("sy")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("sm")+ResourceFactory.getProperty("datestyle.month")+rs.getString("sd")+ResourceFactory.getProperty("datestyle.day")+"  至  "+rs.getString("ey")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("em")+ResourceFactory.getProperty("datestyle.month")+rs.getString("ed")+ResourceFactory.getProperty("datestyle.day"));
            	    			bean.set("status", rs.getString("status"));
            	    			mp.put(""+plan_id, bean);
        		    		}
        				}
        			}
            	}
            	else if("2".equals(cycle))
            	{
        			if("-1".equals(quarter)||Integer.parseInt(quarter)==Integer.parseInt(rs.getString("thequarter")))
        			{
        				whereSQL.append(" or pm.plan_id="+plan_id);
        				bean.set("planid",rs.getString("plan_id"));
    	    			bean.set("name", rs.getString("name"));    
    	    			bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+AdminCode.getCodeName("12",rs.getString("thequarter")));
    	    			bean.set("status", rs.getString("status"));
    	    			mp.put(""+plan_id, bean);
        			}
            	}
            }
            rs.close();
            mp.put("yearList", yearList);
            mp.put("whereSQL", whereSQL.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mp;
	}
	public HashMap getPlanWhereSQL2(String year,String quarter,String month,String status,UserView view,ContentDAO dao,String planid)
	{
		HashMap mp = new HashMap();
		map = new HashMap();
		try
		{
			StringBuffer whereSQL = new StringBuffer("");
			ArrayList yearList  = new ArrayList();
			StringBuffer buf = new StringBuffer("");
			yearList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
			buf.append(" select pp.plan_id,pp.theyear,pp.object_type,pp.name,pp.thequarter,pp.themonth,pp.status,pp.cycle,pp.template_id,pp.start_date,pp.end_date,");
			/**不考虑不定期的考核计划*/
			buf.append(Sql_switcher.year("pp.start_date")+" as sy,"+Sql_switcher.month("pp.start_date")+" as sm,"+Sql_switcher.day("pp.start_date")+" as sd,");
			buf.append(Sql_switcher.year("pp.end_date")+" as ey,"+Sql_switcher.month("pp.end_date")+" as em,");
			buf.append(Sql_switcher.day("pp.end_date")+" as ed ");
            buf.append(" from per_plan pp where 1=1 and ");
            if("-1".equals(planid))
            {
               buf.append("pp.method='2' ");
               if("-1".equals(status))
                  buf.append(" and (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='7') ");// pp.plan_id in (select distinct plan_id from per_mainbody where mainbody_id='"+view.getA0100()+"') and 
               else if("-2".equals(status))
            	  buf.append(" and (pp.status='5'  or pp.status='4' or pp.status='6') ");
               else
            	   buf.append(" and pp.status="+status);
               if(!"-1".equals(year))
            	   buf.append(" and pp.theyear="+year);   		
                buf.append(" and pp.object_type='2' order by pp.plan_id ");
            }
            else
            {
            	buf.append(" pp.plan_id="+planid);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            RowSet rs = dao.search(buf.toString());
            while(rs.next())
            {
            	String cycle=rs.getString("cycle");
            	int plan_id=rs.getInt("plan_id");
            	LazyDynaBean bean = new LazyDynaBean();
            	//田野在bean中添加考核周期，考核模板等信息
            	bean.set("template_id", rs.getString("template_id"));
            	bean.set("cycle", rs.getString("cycle"));
            	bean.set("theyear", rs.getString("theyear"));
            	bean.set("themonth", rs.getString("themonth"));
            	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 
            	if(rs.getDate("start_date")!=null){
            		bean.set("start_date",sdf.format(rs.getDate("start_date")) );
            	}
            	
            	bean.set("object_type", rs.getString("object_type"));
            	bean.set("thequarter", isNull(rs.getString("thequarter")));
            	//添加结束
            	if(map.get(rs.getString("theyear"))==null)
    	    	{
    	    		yearList.add(new CommonData(rs.getString("theyear"),rs.getString("theyear")));
    	    		map.put(rs.getString("theyear"), rs.getString("theyear"));
    	    	}
            	if("3".equals(cycle))
            	{
            		int qu=0;
    	    		if(!"-1".equals(quarter))
    	    		{
    	    			qu=this.getFirstMonthInQuarter(quarter);
    	    		}
    	    		String a_month=rs.getString("themonth");
	        		int int_month=Integer.parseInt(a_month);
	        		if("-1".equals(quarter)||(int_month==qu||int_month==(qu+1)||int_month==(qu+2)))
	        		{
	    	    		if("-1".equals(month)||Integer.parseInt(month)==Integer.parseInt(rs.getString("themonth")))
	    	    		{
	    	    			whereSQL.append(" or pm.plan_id="+plan_id);
	    	    			bean.set("planid",rs.getString("plan_id"));
	    	    			bean.set("name", rs.getString("name"));    
	    	    			bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
	    	    			bean.set("status", rs.getString("status"));
	    	    			mp.put(""+plan_id, bean);
	    	    		}
	        		}
	        		
            	}
            	else if("0".equals(cycle))//年度
            	{
            		whereSQL.append(" or pm.plan_id="+plan_id);
            		bean.set("planid",rs.getString("plan_id"));
	    			bean.set("name", rs.getString("name"));    
            		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year"));
            		bean.set("status", rs.getString("status"));
            		mp.put(""+plan_id, bean);
            	}
            	else if("1".equals(cycle))
            	{
            		String a_quarter = rs.getString("thequarter");
	    			if("1".equals(a_quarter))//上半年，算1，2季度
		    		{
		     			if("-1".equals(quarter)||Integer.parseInt(quarter)==1||Integer.parseInt(quarter)==2)
		    			{
		     				if("-1".equals(month)||(1<=Integer.parseInt(month)&&Integer.parseInt(month)<=6))
    						{
		     		    		whereSQL.append(" or pm.plan_id="+plan_id);
		     		    		bean.set("planid",rs.getString("plan_id"));
	    	    		    	bean.set("name", rs.getString("name"));    
	    	    		      	bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.uphalfyear"));
	    	    		    	bean.set("status", rs.getString("status"));
	    	    		    	mp.put(""+plan_id, bean);
    						}	
		    			}
		    		}
	    			else
	    			{
	    				if("-1".equals(quarter)||Integer.parseInt(quarter)==3||Integer.parseInt(quarter)==4)
	    				{
	    					if("-1".equals(month)||(7<=Integer.parseInt(month)&&Integer.parseInt(month)<=12))
    						{
	    			    		whereSQL.append(" or pm.plan_id="+plan_id);
	    			    		bean.set("planid",rs.getString("plan_id"));
	    	    	    		bean.set("name", rs.getString("name"));    
	    	    	    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.downhalfyear"));
	    	    		    	bean.set("status", rs.getString("status"));
	    	    		    	mp.put(""+plan_id, bean);
    						}
	    				}
	    			}
            	}
            	if("7".equals(cycle))
            	{
            		String sy=rs.getString("sy");
        			String sm=rs.getString("sm");
        			String ey=rs.getString("ey");
        			String em=rs.getString("em");
        			int int_year = 0;
        			int int_sy=0;
        			int int_sm=0;
        			int int_ey=0;
        			int int_em=0;
        			
        		    int_year=Integer.parseInt(year);
        		    int_sy=Integer.parseInt(sy);
        		    int_sm=Integer.parseInt(sm);
        		    int_ey=Integer.parseInt(ey);
        		    int_em=Integer.parseInt(em);
     
        			if("-1".equals(year)||(!"-1".equals(year)&&int_sy<=int_year&&int_ey>=int_year))
        			{
        				if("-1".equals(quarter)||(Integer.parseInt(quarter)==1&&((1<=int_sm&&int_sm<=3)||(1<=int_em&&int_em<=3)))
        						||(Integer.parseInt(quarter)==2&&((4<=int_sm&&int_sm<=6)||(4<=int_em&&int_em<=6)))
        						||(Integer.parseInt(quarter)==3&&((7<=int_sm&&int_sm<=9)||(7<=int_em&&int_em<=9)))
        						||(Integer.parseInt(quarter)==4&&((10<=int_sm&&int_sm<=12)||(10<=int_em&&int_em<=12))))
        				{
        			    	int int_month=0;
        		    		if(!"-1".equals(month))
        	    			{
        		    			int_month=Integer.parseInt(month);
        		    		}
        		    		if("-1".equals(month)||(!"-1".equals(month)&&((int_sm<=int_month&&int_em>=int_month))))
        		    		{
        		    			whereSQL.append(" or pm.plan_id="+plan_id);
                				bean.set("planid",rs.getString("plan_id"));
            	    			bean.set("name", rs.getString("name"));    
            	    			bean.set("evaluate",rs.getString("sy")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("sm")+ResourceFactory.getProperty("datestyle.month")+rs.getString("sd")+ResourceFactory.getProperty("datestyle.day")+"  至  "+rs.getString("ey")+ResourceFactory.getProperty("datestyle.year")+""+rs.getString("em")+ResourceFactory.getProperty("datestyle.month")+rs.getString("ed")+ResourceFactory.getProperty("datestyle.day"));
            	    			bean.set("status", rs.getString("status"));
            	    			mp.put(""+plan_id, bean);
        		    		}
        				}
        			}
            	}
            	else if("2".equals(cycle))
            	{
        			if("-1".equals(quarter)||Integer.parseInt(quarter)==Integer.parseInt(rs.getString("thequarter")))
        			{
        	    		int qu=this.getFirstMonthInQuarter(rs.getString("thequarter"));
        	    		
    	        		if("-1".equals(month)||Integer.parseInt(month)==qu||Integer.parseInt(month)==(qu+1)||Integer.parseInt(month)==(qu+2))
    	        		{
        			    	whereSQL.append(" or pm.plan_id="+plan_id);
        			    	bean.set("planid",rs.getString("plan_id"));
    	    	    		bean.set("name", rs.getString("name"));    
    	    		    	bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+AdminCode.getCodeName("12",rs.getString("thequarter")));
    	    		     	bean.set("status", rs.getString("status"));
    	    		    	mp.put(""+plan_id, bean);
    	        		}
        			}
            	}
            }
            rs.close();
            mp.put("yearList", yearList);
            mp.put("whereSQL", whereSQL.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mp;
	}
	private HashMap levelMap = new HashMap();
	/**
	 * 目标评分中，看不见分发状态的记录了
	 * @param year
	 * @param quarter
	 * @param month
	 * @param status
	 * @param view
	 * @param planid
	 * @param isSort
	 * @param isOrder
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getYearAndPersonList3(String year,String quarter,String month,String status,UserView view,String planid,String isSort,String isOrder)throws GeneralException 
	{
		//田野添加判断前台对是否显示‘总体评价’列 ，当所有计划中有一设置了显示总体评价 ，则前台就显示该列
		String showWholeEvaluate = "false";//默认不显示
		ArrayList list = new ArrayList();
		ArrayList yearList  = new ArrayList();
		HashMap dataMap = new HashMap();
		HashMap planMap = new HashMap();
		dataMap.put("5", planMap);
		try
		{
			StringBuffer buf = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap info=this.getPlanWhereSQL2(year, quarter, month, status, view, dao,planid);
			yearList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
			String planSQL=(String)info.get("whereSQL");
			 if(planSQL.length()>0)
				 planSQL=planSQL.substring(3);
			 else
			 {
				 dataMap.put("3",list);
				 dataMap.put("2",yearList);	
				 dataMap.put("4", showWholeEvaluate);
				 return dataMap;
			 }
			 /*  田野添加代码 开始*/
			 //把所有的总体评价id和内容映射关系存入degreedescMap中避免多次查询数据库
			Map degreedescMap =new HashMap();
			RowSet rset=null;
			rset=dao.search("select id,itemname from per_degreedesc ");			
			while(rset.next())
			{
				degreedescMap.put(rset.getString("id"),rset.getString("itemname"));
			}	
			rset.close();
			//田野修改添加了查询 whole_grade_id 字段
			//源代码为buf.append("select pm.body_id,pm.status,pm.object_id,pm.plan_id,pm.score,pm.seq,ppb.isgrade,pm.reasons from per_mainbody pm left join ");
			/*  田野添加代码 结束*/
			buf.append("select pm.whole_grade_id,pm.body_id,pm.status,pm.object_id,pm.plan_id,pm.score,pm.seq,ppb.isgrade,pm.reasons,pm.whole_score from per_mainbody pm left join ");
			buf.append(" per_plan_body ppb on pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id ");
			buf.append(" where pm.mainbody_id='"+view.getA0100()+"'");
			if(planSQL.length()>0)
		    	buf.append(" and ("+planSQL+")");
    		RowSet rs = null;
    		HashMap levelMap=this.getLevelMap(dao);
    		rs = dao.search(buf.toString());
    		HashMap descMap=new HashMap();
    		HashMap pmMap=new HashMap();
    		StringBuffer objectSQL=new StringBuffer("");
    		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.conn);
			Hashtable ht_table=appb.analyseParameterXml();
       		while(rs.next())
    		{
    			
    			int plan_id=rs.getInt("plan_id");
    			String body_id=rs.getString("body_id")==null?"":rs.getString("body_id");
    			 LoadXml parameter_content = null;
  		        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
  				{			
  		         	parameter_content = new LoadXml(this.conn,plan_id+"");
  					BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
  				}else{
  					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
  				}
  				Hashtable params = parameter_content.getDegreeWhole();
  				String GradeByBodySeq="false";//按考核主体顺序号控制评分流程(True, False默认为False)
  				if(params.get("GradeByBodySeq")!=null)
  					GradeByBodySeq=(String)params.get("GradeByBodySeq");
  				if("true".equalsIgnoreCase(GradeByBodySeq))//把不参加评分的先去掉
  				{
  					String isGrade=rs.getString("isgrade")==null?"0":rs.getString("isgrade");
  					if("1".equals(isGrade))
  						continue;
  				}
    			String level ="1000";
    			if(levelMap.get(body_id)!=null&&!"".equals((String)levelMap.get(body_id)))
    				level=(String)levelMap.get(body_id);
    			String scoreStatus=rs.getString("status")==null?"0":rs.getString("status");
		        LazyDynaBean bean = new LazyDynaBean();
		      //田野添加判断参数内容中，对WholeEval值判断，WholeEval为true时这该条计划是显示总体评价的
  				/*  田野添加代码 开始*/
		    	if(params.get("WholeEval")!=null&& "true".equals(params.get("WholeEval"))){
  					showWholeEvaluate = "true";
  					if(params.get("WholeEvalMode")!=null&& "0".equals(params.get("WholeEvalMode"))){
      					String whole_grade_id = rs.getString("whole_grade_id");
      					if(whole_grade_id!=null){
      						bean.set("wholeEvaluateName",degreedescMap.get(whole_grade_id)==null?"":degreedescMap.get(whole_grade_id));
      					}
  					} else {
  					  String whole_score= rs.getString("whole_score");
  					  String kd = "2";
  					  if( params.get("KeepDecimal")!=null)
  					  {
  						  kd=(String) params.get("KeepDecimal");
  					  }
  					  int KeepDecimal = Integer.parseInt(kd); // 保留小数位
  					  whole_score=PubFunc.round(whole_score,KeepDecimal);
      				  if(whole_score!=null){
                          bean.set("wholeEvaluateName",whole_score);
                      }
  					}
		    	}
  				/*  田野添加代码 结束*/
		        objectSQL.append(" or (a.object_id='"+rs.getString("object_id")+"' and a.plan_id="+plan_id+")");
    	        bean.set("planid",plan_id+"");
    	        bean.set("object_id",rs.getString("object_id"));
    	        bean.set("scoreStatus", scoreStatus);
    	        bean.set("body_id", body_id+"");
        	    bean.set("level", level);
        	    bean.set("score", rs.getString("score")==null?"0":(rs.getFloat("score")+""));
        	    bean.set("seq",rs.getString("seq")==null?"0":rs.getString("seq"));//如果为null可随时打分
        	    bean.set("reasons",Sql_switcher.readMemo(rs, "reasons"));
       	        pmMap.put(plan_id+rs.getString("object_id"), bean);
		   }
    		rs.close();
    		buf.setLength(0);
    		buf.append("select a.b0110,a.e0122,a.e01a1,a.object_id,a.plan_id,a.kh_relations,a.sp_flag,a.a0101,");
    		buf.append(Sql_switcher.isnull("b.a0000", "999999")+"  as norder,b.status,b.theyear,b.cycle,"+Sql_switcher.year("b.start_date")+" as ayear");
    		buf.append(" from per_object a left join per_plan b on a.plan_id=b.plan_id");
    		if(objectSQL.length()>0)
    			buf.append(" where "+objectSQL.substring(3));
    		else 
    		{
    			dataMap.put("3",list);
				dataMap.put("2",yearList);	
				dataMap.put("4", showWholeEvaluate);
				return dataMap;
    		}
    		//buf.append(" ) and pb.mainbody_id='"+view.getA0100()+"' and a.object_id=pb.object_id ") ;
    		buf.append(" order by norder asc,a.plan_id desc,a.A0000 asc");//a.b0110,a.e0122,a.e01a1,
    		RowSet rowSet =dao.search(buf.toString());
    		HashMap tempMap = new HashMap();
    		ArrayList planlist = new ArrayList();
    		boolean priv=false;
			FieldItem aitem  = DataDictionary.getFieldItem("score_org");
			if(aitem!=null&& "1".equals(aitem.getState()))
			{
				priv=true;
			}
			HashMap fieldMap = new HashMap();
			RenderRelationBo bo = new RenderRelationBo(conn,view);

			HashMap yearMap = new HashMap();
		    ArrayList dbnameList = new ArrayList();
			dbnameList.add("USR");
			HashMap eqMap = new HashMap();
			HashMap noScoreMap = new HashMap();
    		while(rowSet.next())
    		{
    			LazyDynaBean bean = new LazyDynaBean();
    			HashMap am=new HashMap();
    			int plan_id=rowSet.getInt("plan_id");
    			String object_id=rowSet.getString("object_id");
    			if(descMap.get(plan_id+"")==null)
    			{
    				am=this.getDegreeDesc(plan_id, "");
    				descMap.put(plan_id+"", am);
    			}
    			else
    			{
    				am=(HashMap)descMap.get(plan_id+"");
    			}
    			 LoadXml parameter_content = null;
 		        if(BatchGradeBo.getPlanLoadXmlMap().get(rowSet.getString("plan_id"))==null)
 				{
 						
 		         	parameter_content = new LoadXml(this.conn,rowSet.getString("plan_id"));
 					BatchGradeBo.getPlanLoadXmlMap().put(rowSet.getString("plan_id"),parameter_content);
 				}
 				else
 				{
 					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(rowSet.getString("plan_id"));
 				}
 				Hashtable params = parameter_content.getDegreeWhole();
 				String allowLeadAdjustCard=(String)params.get("allowLeadAdjustCard");
 	    		if(allowLeadAdjustCard==null)
 	    			allowLeadAdjustCard="false";
    			String market=rowSet.getString("kh_relations");
     		    if(market==null)
 					market="0";
    			LazyDynaBean planBean=(LazyDynaBean)info.get(plan_id+"");
    			String gradedesc=am.get(plan_id+object_id)==null?"":(String)am.get(plan_id+object_id);
         		bean.set("gradedesc",gradedesc);
        		bean.set("planid",rowSet.getString("plan_id"));
        		bean.set("a0100",rowSet.getString("object_id"));
        		bean.set("mda0100",PubFunc.encryption(rowSet.getString("object_id")));
        		bean.set("mdplanid",PubFunc.encryption(rowSet.getString("plan_id")));
        		
	            bean.set("name", (String)planBean.get("name"));     	    	         	
	            String b0110="";
	        	if(!(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))) //中国联通
	        		b0110=AdminCode.getCodeName("UN",rowSet.getString("b0110")==null?"":rowSet.getString("b0110"));
	            String e0122=""; 
	            if(rowSet.getString("e0122")!=null)
	            {   	
	            	if(Integer.parseInt(display_e0122)==0)
					{
						e0122=AdminCode.getCodeName("UM",rowSet.getString("e0122"));
					}
	            	else
					{
						CodeItem item=AdminCode.getCode("UM",rowSet.getString("e0122"),Integer.parseInt(display_e0122));
		    	    	if(item!=null)
		    	    	{
		    	    		e0122=item.getCodename();
		        		}
		    	    	else
		    	    	{
		    	    		e0122=AdminCode.getCodeName("UM",rowSet.getString("e0122"));
		    	    	}	
					}
	            }
	            if(b0110.length()>0&&e0122.length()>0)
	            	e0122="/"+e0122;
	            bean.set("b0110",b0110+e0122);
         		bean.set("evaluate",(String)planBean.get("evaluate"));
     		    bean.set("a0101",rowSet.getString("a0101"));
     		    String sp_flag=rowSet.getString("sp_flag");
     		    if(sp_flag==null|| "".equals(sp_flag))
     		    	sp_flag="01";
     		    LazyDynaBean pmBean=(LazyDynaBean)pmMap.get(plan_id+object_id);
     		    String pstatus=(String)planBean.get("status");
     		    String scoreStatus=(String)pmBean.get("scoreStatus");
     		    String seq=(String)pmBean.get("seq");
     		    String reasons=(String)pmBean.get("reasons");
     		   String isScoreUntread="0";//评分被驳回
				if("1".equals(scoreStatus)&&reasons.length()>0)
				{
					isScoreUntread="1";
				}
     		    String opt="";     		  
				String kd = "2";
				if( params.get("KeepDecimal")!=null)
				{
					kd=(String) params.get("KeepDecimal");
				}
				int KeepDecimal = Integer.parseInt(kd); // 保留小数位				
				String NoApproveTargetCanScore="";
				if(params.get("NoApproveTargetCanScore")!=null)
					NoApproveTargetCanScore=(String)params.get("NoApproveTargetCanScore");
				String GradeByBodySeq="false";//按考核主体顺序号控制评分流程(True, False默认为False)
  				if(params.get("GradeByBodySeq")!=null)
  					GradeByBodySeq=(String)params.get("GradeByBodySeq");
  				boolean isCanScore=true;
  				if("true".equalsIgnoreCase(GradeByBodySeq))//按顺序来评分
  				{
  					HashMap amap=null;
  					if(eqMap.get(plan_id+"")!=null)
  					{
  						amap=(HashMap)eqMap.get(plan_id+"");
  					}else{
  						amap=this.getObjectEvalInfo(plan_id+"",null);
  						eqMap.put(plan_id+"", amap);
  					}
  					if("0".equals(seq))
  						isCanScore=true;
  					else if(amap!=null&&amap.get(object_id)!=null)
  					{
  						String currseq=(String)amap.get(object_id);
  						if(seq.equals(currseq))
  							isCanScore=true;
  						else
  							isCanScore=false;
  					}
  				}
  				
  				// 判断主体类别是否参与评分 JinChunhai 2013.01.08
  				String body_id = (String)pmBean.get("body_id"); 
  				boolean isScoreOrNo = true;
  				HashMap noBodymap = null;
				if(noScoreMap.get(plan_id+"")!=null)
				{
					noBodymap = (HashMap)noScoreMap.get(plan_id+"");
				}else{
					noBodymap = this.getObjectIsScoreOrNo(plan_id+"");
					noScoreMap.put(plan_id+"", noBodymap);
				}
  				if(noBodymap!=null && noBodymap.size()>0)
  				{
  					String noScore = (String)noBodymap.get(body_id);
  					if(noScore!=null && "1".equalsIgnoreCase(noScore))
  						isScoreOrNo = false;
  				}
  				
  				String tmpstatus="";
    		    if(("4".equals(pstatus)|| "6".equals(pstatus))&&("03".equals(sp_flag)|| "true".equalsIgnoreCase(NoApproveTargetCanScore))&&!"2".equals(scoreStatus)&&isCanScore && isScoreOrNo)
    		    {
    		    	tmpstatus="1";
    		        bean.set("opt", "2");
    		        opt="2";
    		    }
    		    else
    		    {
    		    	tmpstatus="0";
    		        bean.set("opt", "0");
    		        opt="0";
    		    }
    		    //田野添加开始
    		    bean.set("wholeEvaluateName", (String)pmBean.get("wholeEvaluateName")==null?"":(String)pmBean.get("wholeEvaluateName"));
    		    bean.set("object_id", rowSet.getString("object_id"));
    		    bean.set("cycle",(String)planBean.get("cycle"));
    		    bean.set("template_id",(String)planBean.get("template_id"));
    		    bean.set("theyear", (planBean.get("theyear")==null)? "":planBean.get("theyear"));
            	bean.set("themonth", (planBean.get("themonth")==null)? "":planBean.get("themonth"));
            	bean.set("start_date", planBean.get("start_date")==null? "":planBean.get("start_date"));
            	bean.set("object_type", planBean.get("object_type")==null? "":planBean.get("object_type"));
            	bean.set("thequarter", planBean.get("thequarter")==null? "":planBean.get("thequarter"));
    		    bean.set("plan_id", rowSet.getString("plan_id"));
    		    bean.set("sp_flag", sp_flag);
    		    if(!planMap.containsKey(rowSet.getString("plan_id"))){
    		    	planMap.put(rowSet.getString("plan_id"), bean);
    		    }
    		    //田野添加结束
    		    bean.set("status", tmpstatus);
    		    bean.set("record", PubFunc.encrypt(rowSet.getString("object_id"))+"-"+PubFunc.encrypt(rowSet.getString("plan_id"))+"-"+tmpstatus);
    		    String score= (String)pmBean.get("score");
    		    score=PubFunc.round(score,KeepDecimal);
    		    bean.set("body_id", (String)pmBean.get("body_id"));
    		    bean.set("level", (String)pmBean.get("level"));
    		    bean.set("score", score);
    		    bean.set("scorestatus", scoreStatus);
    		    bean.set("isScoreUntread", isScoreUntread);
    		    String TargetDefineItem="";
    		    if(ht_table!=null)
    		    {
    		        if(ht_table.get("TargetDefineItem")!=null&&((String)ht_table.get("TargetDefineItem")).trim().length()>0)
    			     	TargetDefineItem=(","+(String)ht_table.get("TargetDefineItem")+",").toUpperCase();
    		    }
    		    if(params.get("TargetTraceEnabled")!=null&& "True".equalsIgnoreCase((String)params.get("TargetTraceEnabled")))
    			{
    				if(params.get("TargetDefineItem")!=null&&((String)params.get("TargetDefineItem")).trim().length()>0)
    					TargetDefineItem=(","+((String)params.get("TargetDefineItem")).trim()+",").toUpperCase();   //目标卡指标
    			}
    		    if(priv&&TargetDefineItem!=null&&TargetDefineItem.trim().length()>0&&TargetDefineItem.toUpperCase().indexOf("SCORE_ORG")!=-1)
				{
					if(bo.isByOrg2(view.getA0100(), object_id, "", Integer.parseInt(pstatus)))
					{
						HashMap amap = null;
         				if(fieldMap.get(plan_id+"")!=null)
         				{
         					amap=(HashMap)fieldMap.get(plan_id+"");
         				}
         				else
         				{
         					amap=bo.getKhOrgField(plan_id+"", userView);
         					fieldMap.put(plan_id+"", amap);
         				}
         				if(amap.get(plan_id+rowSet.getString("object_id"))==null)
         					continue;
					}
				}
    		    /************************************************************/
    		    String theyear = rowSet.getString("theyear");
    		    String cycle = rowSet.getString("cycle");
    		    if("7".equals(cycle))
    		    	theyear = rowSet.getString("ayear");
    		    if(yearMap.get(theyear)==null)
    		    {
    		       yearList.add(new CommonData(theyear,theyear));
    		       yearMap.put(rowSet.getString("theyear"), "1");
    		    }
    		    if(isSort!=null&& "1".equals(isSort))
    		    {
    	    	    if(tempMap.get(plan_id+"")==null)
    		        {
    	    	    	ArrayList alist = new ArrayList();
    		        	alist.add(bean);
    		        	tempMap.put(plan_id+"", alist);
    		        	planlist.add(plan_id+"");
    		        }
    	    	    else
    	    	    {
    		        	ArrayList alist = (ArrayList)tempMap.get(plan_id+"");
    	    	    	alist.add(bean);
    		        	tempMap.put(plan_id+"", alist);
    		        }
    	    	    list.add(bean);
    		    }
    		    else
    		    {
    		      list.add(bean);
    		    }
    		} 
    		rowSet.close();
            rs.close();
            if(isSort!=null&& "1".equals(isSort))
            {
            	if("0".equals(isOrder))
            	{
                    HashMap seqMap= this.SEQ(tempMap,planlist);
                    for(int i=0;i<list.size();i++)
                    {
                     	 LazyDynaBean abean=(LazyDynaBean)list.get(i);
                     	 String a0100=(String)abean.get("a0100");
                    	 String pid=(String)abean.get("planid");
                    	 String seq=(String)seqMap.get(a0100+pid);
                    	 abean.set("seq",seq);
                    }
                    dataMap.put("3", list);
            	}
            	else
            	{
            		ArrayList seqList=SEQList(tempMap,planlist);
            	    dataMap.put("3", seqList);
            	}
            }
            else
            {
            	dataMap.put("3",list);
            }
	    	
		    dataMap.put("2",yearList);	
		    //田野添加是否显示总体评价标记,并取key值为4
		    dataMap.put("4", showWholeEvaluate);
		}
		catch(Exception e)
		{		    
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return dataMap;
	}
	public HashMap getObjectEvalInfo(String plan_id,String object_id)
	{
		RowSet rs = null;	
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select mainbody_id,seq,body_id,status,object_id from per_mainbody where plan_id="+plan_id);
			sql.append(" and body_id in (select body_id from per_plan_body where plan_id="+plan_id+" and (isgrade is null or isgrade=0))");
			sql.append(" and seq is not null  and seq<>0 ");
			if(object_id!=null)
				sql.append(" and object_id ='"+object_id+"'");
			sql.append(" order by object_id,seq ");
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				String aobject_id=rs.getString("object_id");
				if(map.get(aobject_id)==null)
				{
			    	String status=rs.getString("status")==null?"1":rs.getString("status");
			     	String seq=rs.getString("seq");
			    	if(!"2".equals(status))//没提交打分
			    	{
				    	map.put(aobject_id, seq);
			      	}
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public String isNull(String str)
    {

    	if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    return "";
		else
		    return str;

    }
	
	// 是否参与评分
	public HashMap getObjectIsScoreOrNo(String plan_id)
	{
		RowSet rs = null;	
		HashMap map = new HashMap();
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select body_id from per_plan_body where plan_id="+plan_id+" and isgrade=1");			
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql.toString());
			while(rs.next())
			{
				String body_id = rs.getString("body_id")==null?"":rs.getString("body_id");
				map.put(body_id, "1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return map;
	}
	
	
	public HashMap SEQ(HashMap tempMap,ArrayList planlist)
	{
		HashMap map = new HashMap();
		try
		{
			//Iterator iter = tempMap.entrySet().iterator(); 
			for(int index=0;index<planlist.size();index++)
			{ 
				String plan_id=(String)planlist.get(index);
				ArrayList alist=(ArrayList)tempMap.get(plan_id);
				if(alist==null)
					continue;
				LazyDynaBean[] temp= new LazyDynaBean[alist.size()];
				for(int j=0;j<alist.size();j++)
				{
					LazyDynaBean bean=(LazyDynaBean)alist.get(j);
					temp[j]=bean;
				}
				for(int k=0;k<temp.length;k++)
				{
                  /*for(int l=0;l<temp.length-k-1;l++)
                  {
                	  LazyDynaBean a_bean=temp[l];
                      String score=(String)a_bean.get("score");
                      double d_score=Double.parseDouble(score);
                	  LazyDynaBean b_bean=temp[l+1];
                      String bscore=(String)b_bean.get("score");
                      double bd_score=Double.parseDouble(bscore);
                      if(d_score<bd_score)
                      {
                    	  temp[l]=b_bean;
                    	  temp[l+1]=a_bean;
                      }
                  }*/
                  for(int l=0;l<temp.length-k-1;l++)
                  {
                	  LazyDynaBean a_bean=temp[l];
                      String score=(String)a_bean.get("score");
                      //double d_score=Double.parseDouble(score);
                      BigDecimal d_big=new BigDecimal(score);
                	  LazyDynaBean b_bean=temp[l+1];
                      String bscore=(String)b_bean.get("score");
                      //double bd_score=Double.parseDouble(bscore);
                      BigDecimal bd_big=new BigDecimal(bscore);
                      if(d_big.compareTo(bd_big)==-1)
                      {
                    	  temp[l]=b_bean;
                    	  temp[l+1]=a_bean;
                      }
                  }
				}
				int i=1;
				BigDecimal bd1 = null;
				BigDecimal bd2 = null;
				for(int j=0;j<temp.length;j++)
				{
					LazyDynaBean abean=temp[j];
					String a0100=(String)abean.get("a0100");
					String planid=(String)abean.get("planid");
					String score=(String)abean.get("score");
					if(j==0)
						bd1 = new BigDecimal(score);
					bd2 = new BigDecimal(score);
					if(bd1.compareTo(bd2)==1)
					{
						i++;
						bd1=bd2;
					}
					map.put(a0100+planid,i+"");
					
				}
					
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public ArrayList SEQList(HashMap tempMap,ArrayList planList)
	{
		ArrayList list = new ArrayList();
		try
		{
			for(int index=0;index<planList.size();index++)
			  { 
			    
				String plan_id=(String)planList.get(index);
				ArrayList alist=(ArrayList)tempMap.get(plan_id);
				if(alist==null)
					continue;
				LazyDynaBean[] temp= new LazyDynaBean[alist.size()];
				for(int j=0;j<alist.size();j++)
				{
					LazyDynaBean bean=(LazyDynaBean)alist.get(j);
					temp[j]=bean;
				}
				for(int k=0;k<temp.length;k++)
				{
                  for(int l=0;l<temp.length-k-1;l++)
                  {
                	  LazyDynaBean a_bean=temp[l];
                      String score=(String)a_bean.get("score");
                      //double d_score=Double.parseDouble(score);
                      BigDecimal d_big=new BigDecimal(score);
                	  LazyDynaBean b_bean=temp[l+1];
                      String bscore=(String)b_bean.get("score");
                      //double bd_score=Double.parseDouble(bscore);
                      BigDecimal bd_big=new BigDecimal(bscore);
                      if(d_big.compareTo(bd_big)==-1)
                      {
                    	  temp[l]=b_bean;
                    	  temp[l+1]=a_bean;
                      }
                  }
				}
				int i=1;
				BigDecimal bd1 = null;
				BigDecimal bd2 = null;
				for(int j=0;j<temp.length;j++)
				{
					LazyDynaBean abean=temp[j];
					String score=(String)abean.get("score");
					if(j==0)
						bd1 = new BigDecimal(score);
					bd2 = new BigDecimal(score);
					if(bd1.compareTo(bd2)==1)
					{
						i++;
						bd1=bd2;
					}
					abean.set("seq", i+"");
					list.add(abean);
				}
					
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/***
	 * 按照考核主体表来
	 * @param year
	 * @param quarter
	 * @param month
	 * @param status
	 * @param view
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getYearAndPersonList2(String year,String quarter,String month,String status,UserView view)throws GeneralException 
	{
		ArrayList list = new ArrayList();
		ArrayList yearList  = new ArrayList();
		HashMap dataMap = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer();
			//HashMap mainMap=this.getPerMainBodyData(view);
			map = new HashMap();
			buf.append(" select pp.plan_id,pp.theyear,pp.name,pp.thequarter,pp.themonth,pp.status,pp.parameter_content,pp.cycle,");
			buf.append(Sql_switcher.year("pp.start_date")+" as sy,"+Sql_switcher.month("pp.start_date")+" as sm,"+Sql_switcher.day("pp.start_date")+" as sd,");
			buf.append(Sql_switcher.year("pp.end_date")+" as ey,"+Sql_switcher.month("pp.end_date")+" as em,");
			buf.append(Sql_switcher.day("pp.end_date")+" as ed ,po.b0110,po.e0122,po.a0101,po.sp_flag, pm.status as scorestatus,po.object_id,");
			buf.append("pm.mainbody_id,pm.body_id from per_plan pp,per_object po,per_mainbody pm");
            
      
            buf.append(" where pm.mainbody_id='"+view.getA0100()+"'");
			buf.append(" and po.plan_id=pp.plan_id and po.object_id=pm.object_id");
            buf.append(" and pp.plan_id=pm.plan_id");
            buf.append(" and pp.method='2' and pp.cycle<>'7' and (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='7' or pp.status='8') and ");
            buf.append(" pp.object_type='2' order by pp.plan_id,sp_flag");
			ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		HashMap levelMap=this.getLevelMap(dao);
    		rs = dao.search(buf.toString());
    		HashMap descMap=new HashMap();
    	
    		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";

    		
    		
    		while(rs.next())
    		{
    			String cycle=rs.getString("cycle");
    			String object_id = rs.getString("object_id");
    			if(object_id==null|| "".equals(object_id))
    				continue;
    			int plan_id=rs.getInt("plan_id");
    			/*if(!mainMap.containsKey(plan_id+object_id+view.getA0100()))
    				continue;*/
    			if(map.get(rs.getString("theyear"))==null)
    	    	{
    	    		yearList.add(new CommonData(rs.getString("theyear"),rs.getString("theyear")));
    	    		map.put(rs.getString("theyear"), rs.getString("theyear"));
    	    	}
    			String body_id=rs.getString("body_id")==null?"":rs.getString("body_id");
    			String level ="1000";
    			if(levelMap.get(body_id)!=null&&!"".equals((String)levelMap.get(body_id)))
    				level=(String)levelMap.get(body_id);
    			String scoreStatus=rs.getString("scorestatus")==null?"":rs.getString("scorestatus");
    			HashMap am=null;
    			if(descMap.get(plan_id+"")==null)
    			{
    				am=this.getDegreeDesc(plan_id, "");
    				descMap.put(plan_id+"", am);
    			}
    			else
    			{
    				am=(HashMap)descMap.get(plan_id+"");
    			}
    			if("3".equals(cycle))//按月度
    		    {
    				if((!"-1".equals(year)&&rs.getString("theyear").equals(year))|| "-1".equals(year))
    		    	{
	    	    		int qu=0;
	    	    		if(!"-1".equals(quarter))
	    	    		{
	    	    			qu=this.getFirstMonthInQuarter(quarter);
	    	    		}
	    	    		String a_month=rs.getString("themonth");
		        		int int_month=Integer.parseInt(a_month);
		        		if("-1".equals(quarter)||(int_month==qu||int_month==(qu+1)||int_month==(qu+2)))
		        		{
		    	    		if("-1".equals(month)||month.equals(rs.getString("themonth")))
		    	    		{
		    	    			if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    			{
		    	    				LazyDynaBean bean = new LazyDynaBean();
             	             		String gradedesc=am.get(plan_id+object_id)==null?"":(String)am.get(plan_id+object_id);
        	                 		bean.set("gradedesc",gradedesc);
    	                    		bean.set("planid",rs.getString("plan_id"));
    	                    		bean.set("a0100",rs.getString("object_id"));
    	                    		bean.set("record", rs.getString("object_id")+"-"+rs.getString("plan_id"));
         	    	             	bean.set("name", rs.getString("name"));     	    	         	
         	    	            // 	String b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
         	    	            // 	String e0122=AdminCode.getCodeName("UM",rs.getString("e0122")==null?"":rs.getString("e0122"));
         	    	             	
         	    	             	String b0110="";
         	    		        	if(!(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))) //中国联通
         	    		        		b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
         	    		            
         	    		          //  String e0122=AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
         	    		            String e0122=""; //AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
         	    		            if(rs.getString("e0122")!=null)
         	    		            {
         	    		            	
         	    		            	if(Integer.parseInt(display_e0122)==0)
         	    						{
         	    							e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
         	    						}
         	    		            	else
         	    						{
         	    							CodeItem item=AdminCode.getCode("UM",rs.getString("e0122"),Integer.parseInt(display_e0122));
         	    			    	    	if(item!=null)
         	    			    	    	{
         	    			    	    		e0122=item.getCodename();
         	    			        		}
         	    			    	    	else
         	    			    	    	{
         	    			    	    		e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
         	    			    	    	}
         	    			    	    	
         	    						}
         	    		            }
         	    		            if(b0110.length()>0&&e0122.length()>0)
         	    		            	e0122="/"+e0122;
         	    	             	
         	    	             	
         	    	             	bean.set("b0110",b0110+e0122);
        	                 		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month"));
        	             		    bean.set("a0101",rs.getString("a0101"));
        	             		    String sp_flag=rs.getString("sp_flag");
        	             		    if(sp_flag==null|| "".equals(sp_flag))
        	             		    	sp_flag="01";
        	             		    String pstatus=rs.getString("status");
       	             		        if(("4".equals(pstatus)|| "6".equals(pstatus))&& "03".equals(sp_flag)&&!"2".equals(scoreStatus))
       	             		        {
       	             		        	 bean.set("status", "1");
       	             		        	 bean.set("opt", "2");
       	             		        }
       	             		        else
       	             		        {
       	             		          	bean.set("status", "0");
       	             		         	bean.set("opt", "0");
       	             		        }
       	             		        bean.set("body_id", body_id+"");
       	             		        bean.set("level", level);
       	                 	     	list.add(bean);
		    	    			}
		    	    		}
		        		}
    		    	}
    		    }
    			else if("0".equals(cycle))//年度
        		{
    	    		if("-1".equals(year)||rs.getString("theyear").equals(year))
    	    		{
    	    			if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
    	    			{
    	    		    	LazyDynaBean bean = new LazyDynaBean();
    	    		    	String gradedesc=am.get(plan_id+object_id)==null?"":(String)am.get(plan_id+object_id);
                 	    	bean.set("gradedesc",gradedesc);
                	    	bean.set("planid",rs.getString("plan_id"));
	    	            	bean.set("name", rs.getString("name"));
	    	             	String a_status =rs.getString("status");
	    	             	bean.set("a0100",rs.getString("object_id"));
	    	           //  	String b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
	    	          //   	String e0122=AdminCode.getCodeName("UM",rs.getString("e0122")==null?"":rs.getString("e0122"));
	    	             	
	    	             	String b0110="";
 	    		        	if(!(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))) //中国联通
 	    		        		b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
 	    		            
 	    		          //  String e0122=AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
 	    		            String e0122=""; //AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
 	    		            if(rs.getString("e0122")!=null)
 	    		            {
 	    		            	
 	    		            	if(Integer.parseInt(display_e0122)==0)
 	    						{
 	    							e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
 	    						}
 	    		            	else
 	    						{
 	    							CodeItem item=AdminCode.getCode("UM",rs.getString("e0122"),Integer.parseInt(display_e0122));
 	    			    	    	if(item!=null)
 	    			    	    	{
 	    			    	    		e0122=item.getCodename();
 	    			        		}
 	    			    	    	else
 	    			    	    	{
 	    			    	    		e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
 	    			    	    	}
 	    			    	    	
 	    						}
 	    		            }
 	    		            if(b0110.length()>0&&e0122.length()>0)
 	    		            	e0122="/"+e0122;
	    	             	
	    	             	
	    	             	bean.set("b0110",b0110+e0122);
                     		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year"));
                 	    	bean.set("a0101",rs.getString("a0101"));
                     	    String sp_flag=rs.getString("sp_flag");
                 	        bean.set("record", rs.getString("object_id")+"-"+rs.getString("plan_id"));
	             		    if(sp_flag==null|| "".equals(sp_flag))
	             		    	sp_flag="01";
	             		   /**
             		        * 前台展示
             		        * status=0：查看
             		        * status=1：评估
             		        */
             		       String pstatus=rs.getString("status");
             		       if(("4".equals(pstatus)|| "6".equals(pstatus))&& "03".equals(sp_flag)&&!"2".equals(scoreStatus))
             		       {
             		        	bean.set("status", "1");
             		        	bean.set("opt", "2");
             		       }
             		       else
             		       {
             		        	bean.set("status", "0");
             		        	bean.set("opt", "0");
             		       }
                 	       bean.set("body_id", body_id+"");
                 	       bean.set("level", level);
                     	   list.add(bean);
    	    			}
    	    		}
        		}
        		else if("1".equals(cycle))//半年度
        		{
        			if("-1".equals(year)||year.equals(rs.getString("theyear")))
        			{
    	    			String a_quarter = rs.getString("thequarter");
    	    			if("1".equals(a_quarter))//上半年，算1，2季度
    		    		{
    		     			if("-1".equals(quarter)||Integer.parseInt(quarter)==1||Integer.parseInt(quarter)==2)
    		    			{
    		     				if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    			{
    		    			    	LazyDynaBean bean = new LazyDynaBean();
    		    		    		String gradedesc=am.get(plan_id+object_id)==null?"":(String)am.get(plan_id+object_id);
    	                 	    	bean.set("gradedesc",gradedesc);
                        	    	bean.set("planid",rs.getString("plan_id"));
     	    	                	bean.set("name", rs.getString("name"));
     	    	                	bean.set("record", rs.getString("object_id")+"-"+rs.getString("plan_id"));
     	    	                	String sp_flag=rs.getString("sp_flag");
     	    	            	    /**
     	             		         * 前台展示
     	             		         * status=0：查看
     	             		         * status=1：评估
     	             		         */
     	             		        String pstatus=rs.getString("status");
     	             		        if(("4".equals(pstatus)|| "6".equals(pstatus))&& "03".equals(sp_flag)&&!"2".equals(scoreStatus))
     	             		        {
     	                 		    	bean.set("status", "1");
     	                		    	bean.set("opt", "2");
     	                 		    }
  	             	    	        else
  	             		            {
  	             	 	            	bean.set("status", "0");
  	             	    	            bean.set("opt", "0");
  	             		            }
     	         	                bean.set("a0100",rs.getString("object_id"));
     	         	              	String a_status =rs.getString("status");
     	         	           //     String b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
   	    	                 	//    String e0122=AdminCode.getCodeName("UM",rs.getString("e0122")==null?"":rs.getString("e0122"));
     	         	              String b0110="";
	       	    		        	if(!(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))) //中国联通
	       	    		        		b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
	       	    		            
	       	    		          //  String e0122=AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
	       	    		            String e0122=""; //AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
	       	    		            if(rs.getString("e0122")!=null)
	       	    		            {
	       	    		            	
	       	    		            	if(Integer.parseInt(display_e0122)==0)
	       	    						{
	       	    							e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
	       	    						}
	       	    		            	else
	       	    						{
	       	    							CodeItem item=AdminCode.getCode("UM",rs.getString("e0122"),Integer.parseInt(display_e0122));
	       	    			    	    	if(item!=null)
	       	    			    	    	{
	       	    			    	    		e0122=item.getCodename();
	       	    			        		}
	       	    			    	    	else
	       	    			    	    	{
	       	    			    	    		e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
	       	    			    	    	}
	       	    			    	    	
	       	    						}
	       	    		            }
	       	    		            if(b0110.length()>0&&e0122.length()>0)
	       	    		            	e0122="/"+e0122;
     	         	              	
     	         	              	
     	         	              	
   	    	                    	bean.set("b0110",b0110+e0122);
    	                    		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.uphalfyear"));
    	                    		bean.set("a0101",rs.getString("a0101"));
    	                    		bean.set("record", rs.getString("object_id")+"-"+rs.getString("plan_id"));
    	                    		bean.set("body_id", body_id+"");
    	                    		bean.set("level", level);
    	                    	    list.add(bean);
		    	    			}
    			    		}
    	    			}
    	    			else//下半年算3，4季度
    	    			{
    	    				if("-1".equals(quarter)||Integer.parseInt(quarter)==3||Integer.parseInt(quarter)==4)
    	    				{
    	    					if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
		    	    			{
    	    		    			LazyDynaBean bean = new LazyDynaBean();
    	                     		String xmlContent = Sql_switcher.readMemo(rs, "parameter_content");
    	                     		String gradedesc=am.get(plan_id+object_id)==null?"":(String)am.get(plan_id+object_id);
    	                    		bean.set("gradedesc",gradedesc);
                            		bean.set("planid",rs.getString("plan_id"));
     	    	                 	bean.set("name", rs.getString("name"));
     	    	                	bean.set("a0100",rs.getString("object_id"));
     	        	             	String a_status =rs.getString("status");
     	        	            // 	String b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
     	    	                //	String e0122=AdminCode.getCodeName("UM",rs.getString("e0122")==null?"":rs.getString("e0122"));
     	        	             	String b0110="";
         	    		        	if(!(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))) //中国联通
         	    		        		b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
         	    		            
         	    		          //  String e0122=AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
         	    		            String e0122=""; //AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
         	    		            if(rs.getString("e0122")!=null)
         	    		            {
         	    		            	
         	    		            	if(Integer.parseInt(display_e0122)==0)
         	    						{
         	    							e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
         	    						}
         	    		            	else
         	    						{
         	    							CodeItem item=AdminCode.getCode("UM",rs.getString("e0122"),Integer.parseInt(display_e0122));
         	    			    	    	if(item!=null)
         	    			    	    	{
         	    			    	    		e0122=item.getCodename();
         	    			        		}
         	    			    	    	else
         	    			    	    	{
         	    			    	    		e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
         	    			    	    	}
         	    			    	    	
         	    						}
         	    		            }
         	    		            if(b0110.length()>0&&e0122.length()>0)
         	    		            	e0122="/"+e0122;
     	        	             	
     	        	             	
     	    	                	bean.set("b0110",b0110+e0122);
    	                     		bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+ResourceFactory.getProperty("report.pigeonhole.downhalfyear"));
    	                     		bean.set("a0101",rs.getString("a0101"));
    	                    		bean.set("record", rs.getString("object_id")+"-"+rs.getString("plan_id"));
    	                    		String sp_flag=rs.getString("sp_flag");
     	             	    	    if(sp_flag==null|| "".equals(sp_flag))
     	             		        	sp_flag="01";
     	             	        	/**
    	             		         * 前台展示
    	             		         * status=0：查看
    	             	    	     * status=1：评估
    	             	    	     */
    	             	    	   String pstatus=rs.getString("status");
    	                		   if(("4".equals(pstatus)|| "6".equals(pstatus))&& "03".equals(sp_flag)&&!"2".equals(scoreStatus))
    	                		   {
    	             	    		   bean.set("status", "1");
    	             	    		   bean.set("opt", "2");
    	             	    	   }
   	             		           else
   	             		           {
   	             		            	bean.set("status", "0");
   	             		                bean.set("opt", "0");
   	             		           }
    	                     	   bean.set("body_id", body_id+"");
    	                     	   bean.set("level", level);
    	                     	   list.add(bean);
        		    			}
    	    				}
        				}
        			}
        		}
        		else if("7".equals(cycle))//不定期
        		{
    	    		String sy=rs.getString("sy");
    	    		String sm=rs.getString("sm");
    	      		String ey=rs.getString("ey");
    	     		String em=rs.getString("em");
    	     		if(map.get(sy)==null)
	    	    	{
	    	    		yearList.add(new CommonData(sy,sy));
	    	    		map.put(sy, sy);
	    	    	}
    	     		if(map.get(ey)==null)
	    	    	{
	    	    		yearList.add(new CommonData(ey,ey));
	    	    		map.put(ey, ey);
	    	    	}
    	    		int int_year = 0;
    		    	int int_sy=0;
    	    		int int_sm=0;
         			int int_ey=0;
        			int int_em=0;
        			if(!"-1".equals(year))
        			{
    	    			int_year=Integer.parseInt(year);
    	    			int_sy=Integer.parseInt(sy);
    	    			int_sm=Integer.parseInt(sm);
    	    			int_ey=Integer.parseInt(ey);
    	    			int_em=Integer.parseInt(em);
    	    		}
    	 	    	if("-1".equals(year)||(!"-1".equals(year)&&int_sy<=int_year&&int_ey>=int_year))
        			{
        				int int_month=0;
        				if(!"-1".equals(month))
        				{
        					int_month=Integer.parseInt(month);
        				}
    	    			if("-1".equals(month)||(!"-1".equals(month)&&(int_sm==int_month||int_em==int_month)))
    	    			{
    	    				if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    	    			{
    		         			LazyDynaBean bean = new LazyDynaBean();
    		        			String gradedesc=am.get(plan_id+object_id)==null?"":(String)am.get(plan_id+object_id);
	                        	bean.set("gradedesc",gradedesc);
                    	    	bean.set("planid",rs.getString("plan_id"));
 	    	                 	bean.set("name", rs.getString("name"));
 	    	                 	bean.set("a0100",rs.getString("object_id"));
 	        	             	String a_status =rs.getString("status");
	        	         
 	        	          //   	String b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
 	    	              //  	String e0122=AdminCode.getCodeName("UM",rs.getString("e0122")==null?"":rs.getString("e0122"));
 	        	             	
 	        	             	String b0110="";
     	    		        	if(!(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))) //中国联通
     	    		        		b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
     	    		            
     	    		          //  String e0122=AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
     	    		            String e0122=""; //AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
     	    		            if(rs.getString("e0122")!=null)
     	    		            {
     	    		            	
     	    		            	if(Integer.parseInt(display_e0122)==0)
     	    						{
     	    							e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
     	    						}
     	    		            	else
     	    						{
     	    							CodeItem item=AdminCode.getCode("UM",rs.getString("e0122"),Integer.parseInt(display_e0122));
     	    			    	    	if(item!=null)
     	    			    	    	{
     	    			    	    		e0122=item.getCodename();
     	    			        		}
     	    			    	    	else
     	    			    	    	{
     	    			    	    		e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
     	    			    	    	}
     	    			    	    	
     	    						}
     	    		            }
     	    		            if(b0110.length()>0&&e0122.length()>0)
     	    		            	e0122="/"+e0122;
 	        	             	
 	        	             	
 	    	                	bean.set("b0110",b0110+e0122);
	                 	    	bean.set("evaluate",sy+"."+sm+"."+rs.getString("sd")+"-"+ey+"."+em+"."+rs.getString("ed"));
	                 	    	bean.set("a0101",rs.getString("a0101"));
	                 	    	String sp_flag=rs.getString("sp_flag");
	                 	    	bean.set("record", rs.getString("object_id")+"-"+rs.getString("plan_id"));
 	             		        if(sp_flag==null|| "".equals(sp_flag))
 	             	    	    	sp_flag="01";
 	             		       /**
	             		        * 前台展示
	             	    	    * status=0：查看
	             	    	    * status=1：评估
	             		        */
	             		        String pstatus=rs.getString("status");
	             	    	    if(("4".equals(pstatus)|| "6".equals(pstatus))&& "03".equals(sp_flag)&&!"2".equals(scoreStatus))
	             	        	{
	             	        		bean.set("status", "1");
	             	        	    bean.set("opt", "2");
	             	        	}
	             		         else
	             		        {
	             		        	bean.set("status", "0");
	             	    	        bean.set("opt", "0");
	             		        }
	                    		bean.set("body_id", body_id+"");
	                    		bean.set("level", level);
	                    		list.add(bean);
        		    		}
    	    			}
        			}
        		}
        		else if("2".equals(cycle))//季度
        		{
        			if(map.get(rs.getString("theyear"))==null)
	    	    	{
	    	    		yearList.add(new CommonData(rs.getString("theyear"),rs.getString("theyear")));
	    	    		map.put(rs.getString("theyear"), rs.getString("theyear"));
	    	    	}
        			if("-1".equals(year)||year.equals(rs.getString("theyear")))
        			{
        				if("-1".equals(quarter)||quarter.equals(rs.getString("thequarter")))
        				{
        					if("-1".equals(status)||status.equalsIgnoreCase(rs.getString("status"))||("-2".equals(status)&&!"7".equals(rs.getString("status"))))
	    	    			{
        		    			LazyDynaBean bean = new LazyDynaBean();
	                     		String xmlContent = Sql_switcher.readMemo(rs, "parameter_content");
	                     		String gradedesc=am.get(plan_id+object_id)==null?"":(String)am.get(plan_id+object_id);
	                     		bean.set("gradedesc",gradedesc);
                        		bean.set("planid",rs.getString("plan_id"));
 	        	              	bean.set("name", rs.getString("name"));
 	        	            	bean.set("a0100",rs.getString("object_id"));
 	            	          	String a_status =rs.getString("status");
 	            	            bean.set("record", rs.getString("object_id")+"-"+rs.getString("plan_id"));
 	            	     //       String b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
	    	             //    	String e0122=AdminCode.getCodeName("UM",rs.getString("e0122")==null?"":rs.getString("e0122"));
 	            	           String b0110="";
    	    		        	if(!(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))) //中国联通
    	    		        		b0110=AdminCode.getCodeName("UN",rs.getString("b0110")==null?"":rs.getString("b0110"));
    	    		            
    	    		          //  String e0122=AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
    	    		            String e0122=""; //AdminCode.getCodeName("UM",rowSet.getString("e0122")==null?"":rowSet.getString("e0122"));
    	    		            if(rs.getString("e0122")!=null)
    	    		            {
    	    		            	
    	    		            	if(Integer.parseInt(display_e0122)==0)
    	    						{
    	    							e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
    	    						}
    	    		            	else
    	    						{
    	    							CodeItem item=AdminCode.getCode("UM",rs.getString("e0122"),Integer.parseInt(display_e0122));
    	    			    	    	if(item!=null)
    	    			    	    	{
    	    			    	    		e0122=item.getCodename();
    	    			        		}
    	    			    	    	else
    	    			    	    	{
    	    			    	    		e0122=AdminCode.getCodeName("UM",rs.getString("e0122"));
    	    			    	    	}
    	    			    	    	
    	    						}
    	    		            }
    	    		            if(b0110.length()>0&&e0122.length()>0)
    	    		            	e0122="/"+e0122;
 	            	            
 	            	            
 	            	            
	    	                	bean.set("b0110",b0110+e0122);
	                 	    	bean.set("evaluate",rs.getString("theyear")+ResourceFactory.getProperty("datestyle.year")+" "+AdminCode.getCodeName("12",rs.getString("thequarter")));
	                 	    	bean.set("a0101",rs.getString("a0101"));
	                 	    	String sp_flag=rs.getString("sp_flag");
 	             		        if(sp_flag==null|| "".equals(sp_flag))
 	             	    	    	sp_flag="01";
 	             		        /**
	             		         * 前台展示
	             		         * status=0：查看
	             		         * status=1：评估
	             		         */
	             		        String pstatus=rs.getString("status");
	             		        if(("4".equals(pstatus)|| "6".equals(pstatus))&& "03".equals(sp_flag)&&!"2".equals(scoreStatus))
	             	        	{
	             		        	bean.set("status", "1");
	             		        	bean.set("opt", "2");
	             		        }
	             		        else
	             		        {
	             	    	    	bean.set("status", "0");
	             	    	        bean.set("opt", "0");
	             		        }
	                 	    	 bean.set("body_id", body_id+"");
	                 	    	 bean.set("level", level);
	                 	    	 list.add(bean);
	    	    	    		}
        			    	}
        	  		}
          		}
		    }
            rs.close();
	    	dataMap.put("1",list);
		    dataMap.put("2",yearList);	
		}
		catch(Exception e)
		{		    
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return dataMap;
	}
	   /**
     * 取得评估期季度列表
     * @param a0100
     * @param year
     * @return
     */
	private static ArrayList quarterList=null;
    public ArrayList getQuarterList()
    {
    	if(quarterList==null)
    	{
    		quarterList = new ArrayList();
         	try
         	{
        		StringBuffer buf = new StringBuffer();
         		buf.append("select codeitemid,codeitemdesc from codeitem where codesetid='12'");
        		ContentDAO dao = new ContentDAO(this.conn);
    	    	RowSet rs = dao.search(buf.toString());
    	    	quarterList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
    	    	while(rs.next())
         		{
    	    		quarterList.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
        		}
    	    	rs.close();
        	}
        	catch(Exception e)
        	{
         		e.printStackTrace();
        	}
    	}
    	return quarterList;
    }
    private static ArrayList monthList = null;
    public ArrayList getMonthList()
    {
    	if(monthList==null)
    	{
    		monthList = new ArrayList();
        	try
        	{
        		StringBuffer buf = new StringBuffer();
        		buf.append("select codeitemid,codeitemdesc from codeitem where codesetid='13'");
        		ContentDAO dao = new ContentDAO(this.conn);
        		RowSet rs = dao.search(buf.toString());
        		monthList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
        		while(rs.next())
        		{
        			monthList.add(new CommonData(rs.getString("codeitemid"),rs.getString("codeitemdesc")));
        		}
        		rs.close();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
    	}
    	return monthList;
    }
    public int getBody_id(int body_id)
    {
    	int body=0;
    	switch(body_id)
    	{
    	case 0:
    		body=4;
    		break;
    	case 1:
    		body=3;
    		break;
    	case 2:
    		body=2;
    		break;
    	case 3:
    		body=1;
    		break;
    	case 4:
    		body=0;
    		break;
    	case 5:
    		body=5;
    		break;
    	case 6:
    		body=6;
    		break;
    	}
    	return body;
    }
    private static ArrayList statusList=null;
    public ArrayList getStatusList()
    {
    	if(statusList==null)
    	{
    		statusList = new ArrayList();
        	try
        	{
        		statusList.add(new CommonData("-1",ResourceFactory.getProperty("label.all")));
        		statusList.add(new CommonData("-2","执行中"));
        		//statusList.add(new CommonData("8",ResourceFactory.getProperty("org.performance.Published")));
        		statusList.add(new CommonData("4",ResourceFactory.getProperty("org.performance.start")));
        		statusList.add(new CommonData("5",ResourceFactory.getProperty("label.commend.stop")));
        		statusList.add(new CommonData("6",ResourceFactory.getProperty("org.performance.pg")));
        		statusList.add(new CommonData("7",ResourceFactory.getProperty("org.performance.end")));
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
    	}
    	return statusList;
    }
	/**
     * 取考核等级
     * @param degreeid
     * @return
     */
    //private static HashMap degreeDescMap = new HashMap();
    public HashMap getDegreeDesc(int plan_id,String object_id)
    {
    	HashMap map = new HashMap();
        try
         {
        	String tableName = "per_result_"+plan_id;
        	DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(tableName);
        	DbWizard dbWizard=new DbWizard(this.conn);  		
    		Table table=new Table(tableName);
    		if(dbWizard.isExistTable(table.getName(),false))
    		{
    			StringBuffer buf = new StringBuffer();
    			buf.append(" select resultdesc,object_id from ");
    			buf.append(tableName+" ");
    			ContentDAO dao = new ContentDAO(this.conn);
    			RowSet rs = dao.search(buf.toString());
    			while(rs.next())
    			{
    				map.put(plan_id+rs.getString("object_id"),rs.getString("resultdesc")==null?"":rs.getString("resultdesc"));
    			}
    			rs.close();
    		}
         }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    	return map;
    }
    /**
     * 取得每个季度的第一个月
     * @param quarter
     * @return
     */
    public int getFirstMonthInQuarter(String quarter)
    {
    	int month=1;
    	try
    	{
    		int a_quarter = Integer.parseInt(quarter);
    		switch(a_quarter)
    		{
    		case 1:
    			month=1;
    			break;
    		case 2:
    			month=4;
    			break;
    		case 3:
    			month=7;
    			break;
    		case 4:
    			month=10;
    			break;
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return month;
    }
    private static HashMap mainBodyBeanMap = new HashMap();
    public HashMap getMainbodyBean(String plan_id)
    {
    	 HashMap map =null;
  	  try
  	  {
  		  if(mainBodyBeanMap.get(plan_id)!=null)
  			  map=(HashMap)mainBodyBeanMap.get(plan_id);
  		  else
  		  {
     		  String sql = "select a.mainbody_id,a.object_id,";
      		  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    				  sql+="b.level_o ";
     		  else
     			  sql+="b.level ";
     		  sql+=" from per_mainbody a,per_mainbodyset b where plan_id="+plan_id;
     		  sql+=" and a.body_id=b.body_id ";
     		  ContentDAO dao = new ContentDAO(this.conn);
     		  RowSet rs = dao.search(sql);
     		  map = new HashMap();
  	    	  while(rs.next())
  	    	  {
  	    		  LazyDynaBean bean = new LazyDynaBean();
  		    	  if(Sql_switcher.searchDbServer()==Constant.ORACEL)
  	    			  bean.set("level",rs.getString("level_o"));
  	    		  else
  	    			  bean.set("level",rs.getString("level"));
  	    		  map.put(rs.getString("mainbody_id")+rs.getString("object_id"), bean);
      		  }
  	    	  rs.close();
  	    	  mainBodyBeanMap.put(plan_id, map);
  		  }
  	  }
  	  catch(Exception e)
  	  {
  		  e.printStackTrace();
  	  }
  	  return map;
    }
    private HashMap perPointMap = new HashMap();
    private HashMap perPointScoreMap=new HashMap();
    private RecordVo template_vo=null;
    private String scoreflag="";
    private HashMap allScoreMap = new HashMap();
    /*****************取考核总分进行排序********************************/
    public void initSEQData(String objectSQL)
    {
    	try
    	{
    		StringBuffer sql = new StringBuffer("select * from per_object where (");
    		ContentDAO dao = new ContentDAO(this.conn);
    		if(this.planList!=null&&this.planList.size()>0)
    		{
    			
    			for(int i=0;i<planList.size();i++)
    			{
    				String plan_id=(String)this.planList.get(i);
    				this.plan_vo=this.getPlanVo(plan_id, dao);
    				this.template_vo=this.getTemplateVo(dao);
    				LoadXml loadxml=null;
    				if(BatchGradeBo.getPlanLoadXmlMap().get(String.valueOf(this.plan_vo.getInt("plan_id")))==null)
    				{
    					loadxml=new LoadXml(this.conn,String.valueOf(this.plan_vo.getInt("plan_id")));
    					BatchGradeBo.getPlanLoadXmlMap().put(String.valueOf(this.plan_vo.getInt("plan_id")),loadxml);
    				}
    				else
    					loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(String.valueOf(this.plan_vo.getInt("plan_id")));		
    				this.planParam=loadxml.getDegreeWhole();
    				this.scoreflag=(String)this.planParam.get("scoreflag"); 
    				//StringBuffer sql = new StringBuffer("select * from per_object where plan_id="+plan_id);
    				StringBuffer temp=(StringBuffer)this.objectMap.get(plan_id);
    				if(temp==null)
    				{
    					sql.append(" and 1=2 ");
    				}
    				else
    				{
    					String WhereSQL=temp.toString().substring(3);
    					sql.append(" and ("+WhereSQL+")");
    				}
    				RowSet rs = dao.search(sql.toString());
    				while(rs.next())
    				{
    					String object_id=rs.getString("object_id");
    					this.perPointMap= getPerPointMap(object_id,this.plan_vo.getInt("object_type"),plan_id);
    					this.perPointScoreMap=this.getPerPointScoreMap(object_id);
    					this.mainbodyBean=this.getMainbodyBean2(object_id);
    					this.p04List=this.getP04List(object_id);
    					this.itemPrivMap=this.getItemPrivMap(object_id, ((String)this.levelMap.get(plan_id+object_id)));
    					String score=this.getScore(this.userView.getA0100(), object_id);
    					this.allScoreMap.put(plan_id+object_id, score);
    				}
    				rs.close();
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public RecordVo getPlanVo(String plan_id,ContentDAO dao)
    {
    	RecordVo vo = null;
    	try
    	{
    		vo = new RecordVo("per_plan");
    		vo.setInt("plan_id", Integer.parseInt(plan_id));
    		vo=dao.findByPrimaryKey(vo);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return vo;
    }
    public RecordVo getTemplateVo(ContentDAO dao)
    {
    	RecordVo vo = null;
    	try
    	{
    		vo = new RecordVo("per_template");
    		vo.setString("template_id", this.plan_vo.getString("template_id"));
    		vo=dao.findByPrimaryKey(vo);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return vo;
    }
    /**
	 * 取得 工作任务信息表中的记录
	 * @return
	 */
	public ArrayList getP04List(String object_id)
	{
		ArrayList list=new ArrayList();
		HashMap tempMap=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select * from P04 where plan_id="+this.plan_vo.getInt("plan_id"));
			if(this.plan_vo.getInt("object_type")==1||this.plan_vo.getInt("object_type")==3||this.plan_vo.getInt("object_type")==4)
				sql.append(" and b0110='"+object_id+"'");
			else if(this.plan_vo.getInt("object_type")==2)
				sql.append(" and a0100='"+object_id+"'");
			
		//	if(opt==2||(this.mainbodyBean!=null&&((String)this.mainbodyBean.get("status")).equalsIgnoreCase("2"))||(this.plan_vo.getInt("status")>=4&&this.plan_vo.getInt("status")!=8))
			if(this.plan_vo.getInt("status")>=4&&this.plan_vo.getInt("status")!=8)
			{
				if(this.planParam.get("NoShowTargetAdjustHistory")!=null&& "True".equalsIgnoreCase((String)this.planParam.get("NoShowTargetAdjustHistory")))
					sql.append(" and ( ( state=-1 and chg_type!=3 ) or state is null or state<>-1 )");
			}
		/*	if(this.opt!=4)
			{
				if(!(this.opt==2&&!this.userView.getA0100().equalsIgnoreCase(this.object_id)&&this.objectSpFlag.equals("07")))
					sql.append(" and (Chg_type is null or Chg_type<>3  ) ");
			}*/
			sql.append(" order by item_id asc,seq asc");
			if("True".equalsIgnoreCase((String)this.planParam.get("taskAdjustNeedNew")))
			{
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					sql.append(",f_p0400 desc ");
				else
					sql.append(",f_p0400");
				sql.append(",chg_type desc");
			}
			RowSet rowSet=dao.search(sql.toString());
			LazyDynaBean abean=null;
			ArrayList fieldList=DataDictionary.getFieldList("P04",Constant.USED_FIELD_SET);
			SimpleDateFormat fm=new SimpleDateFormat("yyyy-MM-dd");
			//if(this.planParam.get("EvalCanNewPoint")!=null&&((String)this.planParam.get("EvalCanNewPoint")).equalsIgnoreCase("true"))
				//this.itemtypeMap=new HashMap();
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("p0400",rowSet.getString("p0400"));
				abean.set("b0110",rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"");
				abean.set("e0122",rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"");
				abean.set("e01a1",rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"");
				abean.set("nbase",rowSet.getString("nbase")!=null?rowSet.getString("nbase"):"");
				abean.set("a0100",rowSet.getString("a0100")!=null?rowSet.getString("a0100"):"");
				abean.set("a0101",rowSet.getString("a0101")!=null?rowSet.getString("a0101"):"");
				abean.set("p0401",rowSet.getString("p0401"));
				abean.set("p0407",Sql_switcher.readMemo(rowSet,"p0407"));
				
				abean.set("p0413",moveZero(rowSet.getString("p0413")));
				abean.set("p0415",moveZero(rowSet.getString("p0415")));
		
				abean.set("p0421",rowSet.getString("p0421")!=null?rowSet.getString("p0421"):"");
				abean.set("p0423",rowSet.getString("p0423")!=null?rowSet.getString("p0423"):"");
				abean.set("p0424",rowSet.getString("p0424")!=null?rowSet.getString("p0424"):"");
				abean.set("p0425",Sql_switcher.readMemo(rowSet,"p0425"));
				abean.set("chg_type",rowSet.getString("chg_type")!=null?rowSet.getString("chg_type"):"0");
				
				abean.set("plan_id",rowSet.getString("plan_id"));
				abean.set("item_id",rowSet.getString("item_id"));
				abean.set("fromflag",rowSet.getString("fromflag"));
				abean.set("state",rowSet.getString("state")!=null?rowSet.getString("state"):"0");
				abean.set("itemtype",rowSet.getString("itemtype")!=null?rowSet.getString("itemtype"):"0");
				/*if(this.itemtypeMap!=null)
				{
					this.itemtypeMap.put(rowSet.getString("p0400"), rowSet.getString("itemtype")==null?"0":rowSet.getString("itemtype"));
				}*/
				abean.set("processing_state",rowSet.getString("processing_state")==null?"0":rowSet.getString("processing_state"));
				/*if(((String)abean.get("processing_state")).equals("1"))
					processing_state_all="1";*/
		//		if(this.plan_vo.getInt("object_type")==2&&((String)abean.get("state")).equals("-1"))
		//			isAdjustPoint=true;
				
				abean.set("seq",rowSet.getString("seq")!=null?rowSet.getString("seq"):"0");
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem item=(FieldItem)fieldList.get(i);
					String itemid=item.getItemid();
					String str="/NBASE/A0100/A0101/B0110/E0122/E01A1/P0400/ITEM_ID/FROMFLAG/P0401/P0407/P0413/P0415/PLAN_ID/STATE/";
					
					
					
					if(str.indexOf("/"+itemid.toUpperCase()+"/")!=-1)
						continue;
					if("D".equalsIgnoreCase(item.getItemtype()))
					{
							if(rowSet.getDate(itemid)!=null)
								abean.set(itemid,fm.format(rowSet.getDate(itemid)));
							else
								abean.set(itemid,"");
					}
					else if("M".equalsIgnoreCase(item.getItemtype()))
							abean.set(itemid,Sql_switcher.readMemo(rowSet,itemid).replaceAll("\r\n","<br>"));
					else
					{
						if(item.getCodesetid()==null|| "0".equals(item.getCodesetid())||item.getCodesetid().trim().length()==0)
							abean.set(itemid,rowSet.getString(itemid)!=null?rowSet.getString(itemid):"");
						else
						{
							if(rowSet.getString(itemid)==null)
								abean.set(itemid,"");
							else
							{
								abean.set(itemid,AdminCode.getCodeName(item.getCodesetid(), rowSet.getString(itemid)));
								
							}
						}
					}
				}
				tempMap.put(rowSet.getString("p0400"), abean);
				list.add(abean);
			}
			rowSet.close();
			
			if(this.p04Map.size()==0)
				this.p04Map=tempMap;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
    public HashMap getPerPointScoreMap(String object_id)
	{
		HashMap map=new HashMap();
		try
		{
			DbWizard dbWizard=new DbWizard(this.conn);
			ContentDAO dao=new ContentDAO(this.conn);
			if(dbWizard.isExistTable("per_gather_score_"+this.plan_vo.getInt("plan_id"),false))
			{
				
				RowSet rowSet=dao.search("select * from per_gather_score_"+this.plan_vo.getInt("plan_id")+" where 1=2");
				ResultSetMetaData meta=rowSet.getMetaData();
				String sql="select pgs.* from  per_gather_score_"+this.plan_vo.getInt("plan_id")+" pgs,per_gather_"+this.plan_vo.getInt("plan_id")+" pg where pg.gather_id=pgs.gather_id and pg.object_id='"+object_id+"'";
				rowSet=dao.search(sql);
				if(rowSet.next())
				{
					for(int i=0;i<meta.getColumnCount();i++)
					{
						String columnName=meta.getColumnName(i+1);
						String temp=columnName.substring(columnName.length()-2);
					 	if("_S".equalsIgnoreCase(temp))
					 	{
					 		String temp2=columnName.substring(2);
					 		String temp3=temp2.substring(0,temp2.length()-2);
					 		map.put(temp3.toLowerCase(), rowSet.getString(columnName)!=null?rowSet.getString(columnName):"");
					 	}
					}
					
				}
				rowSet.close();
			}
			
			//考核评估中 共享指标 的定量统一打分
			LazyDynaBean point_bean=null;
			if(dbWizard.isExistTable("per_result_"+this.plan_vo.getInt("plan_id"),false))
			{
				RowSet rowSet=dao.search("select * from per_result_"+this.plan_vo.getInt("plan_id")+" where object_id='"+object_id+"'");
				if(rowSet.next())
				{
					ResultSetMetaData mt=rowSet.getMetaData();
					HashMap amap=new HashMap();
					for(int j=0;j<mt.getColumnCount();j++)
						amap.put(mt.getColumnName(j+1).toLowerCase(),"1");
					
					
					for(int i=0;i<this.p04List.size();i++)
					{
						point_bean=(LazyDynaBean)this.p04List.get(i);
						String fromflag=(String)point_bean.get("fromflag");
						String p0400=(String)point_bean.get("p0400");
						String p0401=(String)point_bean.get("p0401");
						
						if("2".equals(fromflag))  //来源KPI指标
						{
							LazyDynaBean pointBean=(LazyDynaBean)this.perPointMap.get(p0401.toLowerCase());
							
							String  point_kind="0";
							String  status="0";
							if(pointBean!=null)
							{
								point_kind=(String)pointBean.get("pointkind");
								status=(String)pointBean.get("status");
							}
							
							//统一打分指标
							if("1".equals(point_kind)&& "1".equals(status))
							{
								String  point_id=(String)pointBean.get("point_id");
								if(amap.get("c_"+point_id.toLowerCase())!=null)
								{
									map.put(point_id.toLowerCase(), rowSet.getString("C_"+point_id)!=null?rowSet.getString("C_"+point_id):"");
								}
							}
						}
					}
				}
				rowSet.close();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
    public String getScore(String mainbody_id,String object_id)
	{
		String score="0";
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			
			if(this.plan_vo.getInt("method")==1) //360度计划
			{
				BatchGradeBo batchGradeBo=new BatchGradeBo(this.conn);
				float _score=batchGradeBo.getObjectTotalScore(this.plan_vo.getInt("plan_id"),mainbody_id,this.plan_vo.getString("template_id"),object_id,this.userView);
				score=String.valueOf(_score);
			}
			else if(this.plan_vo.getInt("method")==2) //目标
			{
				if("4".equals(this.scoreflag)&&this.planParam.get("EvalCanNewPoint")!=null&& "true".equalsIgnoreCase((String)this.planParam.get("EvalCanNewPoint")))//加扣分
				{
					
					score=getScore2(mainbody_id,object_id);
					
				}
				else
				{
					if("0".equals(this.template_vo.getString("status"))) //分值
					{
						rowSet=dao.search("select "+Sql_switcher.isnull("sum(per_target_evaluation.score)","0")+"  from per_target_evaluation,p04 where per_target_evaluation.p0400=p04.p0400 and per_target_evaluation.plan_id="+this.plan_vo.getInt("plan_id")+" and per_target_evaluation.object_id='"+object_id+"' and per_target_evaluation.mainbody_id='"+mainbody_id+"' and ( p04.chg_type<>3 or p04.chg_type is null ) ");
						
					}
					else
					    rowSet=dao.search("select "+Sql_switcher.isnull("sum(per_target_evaluation.score*p04.p0415)","0")+"  from per_target_evaluation,p04 where per_target_evaluation.p0400=p04.p0400 and per_target_evaluation.plan_id="+this.plan_vo.getInt("plan_id")+" and per_target_evaluation.object_id='"+object_id+"' and per_target_evaluation.mainbody_id='"+mainbody_id+"' and ( p04.chg_type<>3 or p04.chg_type is null ) ");
					if(rowSet.next())
					{
						//score=PubFunc.round(rowSet.getString(1),1);
						score=rowSet.getString(1);
						LazyDynaBean point_bean=null;	
						for(int i=0;i<this.p04List.size();i++)
						{
							point_bean=(LazyDynaBean)this.p04List.get(i);
							String fromflag=(String)point_bean.get("fromflag");
							String p0400=(String)point_bean.get("p0400");
							String p0401=(String)point_bean.get("p0401");
							String p0415=(String)point_bean.get("p0415");
							if("2".equals(fromflag))  //来源KPI指标
							{
								LazyDynaBean pointBean=(LazyDynaBean)this.perPointMap.get(p0401.toLowerCase());
								String  point_kind="0";
								String  status="0";
								if(pointBean!=null)
								{
									point_kind=(String)pointBean.get("pointkind");
									status=(String)pointBean.get("status");
								}
								
								//统一打分指标
								if("1".equals(point_kind)&& "1".equals(status))
								{
									if(this.perPointScoreMap.get(p0401.toLowerCase())!=null&&((String)this.perPointScoreMap.get(p0401.toLowerCase())).trim().length()>0)
									{
									//	abean.set("score", PubFunc.round((String)this.perPointScoreMap.get(p0401.toLowerCase()),2));
										
										if("0".equals(this.template_vo.getString("status")))
											score=PubFunc.add(score, (String)this.perPointScoreMap.get(p0401.toLowerCase()),2);
										else  //权重
										{
											String _value=PubFunc.multiple((String)this.perPointScoreMap.get(p0401.toLowerCase()), p0415, 2);
											score=PubFunc.add(score,_value,2);
										}
									
									}
								}
								
							}
							
						}
						score=PubFunc.round(score,1);
						if(Double.parseDouble(score)<0)
							score="0";
						
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rowSet!=null)
			{
				try
				{
					rowSet.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return score;
	}
    public String moveZero(String number)
	{
		DecimalFormat df = new DecimalFormat("###############.#####"); 
		if(number==null||number.length()==0)
			return "";
		return df.format(Double.parseDouble(number));
	}
    /**
	 * 取得 对象计划 下绩效指标信息
	 * @param object_id
	 * @param object_type
	 * @param plan_id
	 * @return
	 */
	public HashMap getPerPointMap(String object_id,int object_type,String plan_id)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer(" select pp.* from per_point pp, ");
			if(object_type==2)
			{
				sql.append(" ( select p0401 from p04 where plan_id="+plan_id+" and a0100='"+object_id+"' and fromflag=2 ) a ");
			}
			else
			{
				sql.append(" ( select p0401 from p04 where plan_id="+plan_id+" and b0110='"+object_id+"' and fromflag=2 ) a ");
			}
			sql.append(" where pp.point_id=a.p0401 ");
			RowSet rowSet=dao.search(sql.toString());
			LazyDynaBean abean=null;
		//	ArrayList pointList=new ArrayList();
			while(rowSet.next())
			{
				String point_id=rowSet.getString("point_id");
				String pointname=rowSet.getString("pointname");
				String visible=rowSet.getString("visible")!=null?rowSet.getString("visible"):"0";
				String Status=rowSet.getString("Status")!=null?rowSet.getString("Status"):"0";
				String pointkind=rowSet.getString("pointkind");
				abean=new LazyDynaBean();
				abean.set("point_id",point_id);
				abean.set("pointname",pointname);
				abean.set("visible",visible);
				abean.set("pointkind",pointkind);
				abean.set("status",Status);

				abean.set("value","");
				
				map.put(point_id.toLowerCase(),abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	private HashMap itemPrivMap = new HashMap();
	private LazyDynaBean mainbodyBean=null;
	public String getScore2(String mainbody_id,String object_id)
	{
		String score="";
		ArrayList _templateItemList=getTemplateItemList();
		HashMap rootNodeMap=getlayItemMap(_templateItemList);
		ArrayList p04List=getP04List(object_id);
		HashMap nodeItemMap=getNodeItemMap(0); //叶子节点
		HashMap topItemMap=getNodeItemMap(1); //根节点
		ArrayList _list=new ArrayList();
		HashMap itemScoreMap=new HashMap();
		LazyDynaBean p04bean=null;
		 
		HashMap p04ValueMap=getEvaluationMap( object_id, mainbody_id,this.plan_vo.getInt("plan_id"));
		
		Set key=nodeItemMap.keySet();
		for(Iterator t=key.iterator();t.hasNext();)
		{
			String _key=(String)t.next();
			LazyDynaBean abean=(LazyDynaBean)nodeItemMap.get(_key);
			String item_id=(String)abean.get("item_id");
			String self_score=(String)abean.get("score");
			double _score=0;
			for(int e=0;e<p04List.size();e++)
			{
				p04bean=(LazyDynaBean)p04List.get(e);
				String p04_item_id=(String)p04bean.get("item_id");
				String itemtype=(String)p04bean.get("itemtype");
				if(p04_item_id.equals(item_id))
				{
					String p0400=(String)p04bean.get("p0400");
					if(p04ValueMap.get(p0400)!=null)
					{
						_score+=Double.parseDouble((String)p04ValueMap.get(p0400));
					}
				}
			}
			
			LazyDynaBean _abean=new LazyDynaBean();
			_abean.set("item_id",item_id);
			_abean.set("score",PubFunc.add(self_score,String.valueOf(_score),2));
			itemScoreMap.put(item_id,_abean);
		}
		
		
		BigDecimal totalScore=new BigDecimal("0");
		key=topItemMap.keySet();
		LazyDynaBean _bean=null;
		for(Iterator t=key.iterator();t.hasNext();)
		{
			String _key=(String)t.next();
			LazyDynaBean abean=(LazyDynaBean)topItemMap.get(_key);
			String item_id=(String)abean.get("item_id");
			String self_score=(String)abean.get("score");
			String rank=(String)abean.get("rank");
			BigDecimal key_score=new BigDecimal("0");
			
			HashMap map=(HashMap)rootNodeMap.get(item_id);
			Set keySet=map.keySet();
			int num=0;
			for(Iterator tt=keySet.iterator();tt.hasNext();)
			{
				String itemid=(String)tt.next();
				if(!(itemPrivMap.get(itemid)!=null&& "0".equals((String)itemPrivMap.get(itemid))))
				{
					num++;
					_bean=(LazyDynaBean)itemScoreMap.get(itemid);
					key_score=key_score.add(new BigDecimal((String)_bean.get("score")));
				}
			}
			if(num>0)
			{
				key_score=key_score.divide(new BigDecimal(num),2, BigDecimal.ROUND_HALF_UP);
				totalScore=totalScore.add(key_score.multiply(new BigDecimal(rank)));
			}	
				
		}
		
		
		score=PubFunc.round(totalScore.toString(),2);
		
		
		return score;
	}
	public ArrayList getTemplateItemList()
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from  per_template_item where template_id='"+this.plan_vo.getString("template_id")+"'  order by seq");
		    LazyDynaBean abean=null;
			while(rowSet.next())
		    {
				abean=new LazyDynaBean();
		    	abean.set("item_id",rowSet.getString("item_id"));
		    	abean.set("parent_id",rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
		    	abean.set("child_id",rowSet.getString("child_id")!=null?rowSet.getString("child_id"):"");
		    	abean.set("template_id",rowSet.getString("template_id"));
		    	abean.set("itemdesc",rowSet.getString("itemdesc"));
		    	abean.set("seq",rowSet.getString("seq"));
		    	abean.set("kind",rowSet.getString("kind")!=null?rowSet.getString("kind"):"1");
		    	
		    	abean.set("score",rowSet.getString("score")!=null?moveZero(rowSet.getString("score")):"");
		    	
		    	abean.set("rank",rowSet.getString("rank")!=null?moveZero(rowSet.getString("rank")):"");
		    	
		    	abean.set("rank_type",rowSet.getString("rank_type")!=null?rowSet.getString("rank_type"):"");
		    	list.add(abean);
		    }
		    rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 取得各层的item
	 * @param _itemList
	 * @return
	 */
	public HashMap getlayItemMap(ArrayList _itemList)
	{
		HashMap rootMap=new HashMap();
		LazyDynaBean abean=null;
		ArrayList tempList=new ArrayList();
		for(int i=0;i<_itemList.size();i++)
		{
			abean=(LazyDynaBean)_itemList.get(i);
			String parent_id=(String)abean.get("parent_id");
			String item_id=(String)abean.get("item_id");
			if(parent_id.trim().length()==0)
			{
				HashMap nodeMap=new HashMap();
				getlayItemList2(_itemList,item_id,nodeMap);
				rootMap.put(item_id, nodeMap);
			}
		}
		return rootMap;
	}
	
	
	public void  getlayItemList2(ArrayList _itemList,String item_id,HashMap map)
	{
	
		LazyDynaBean abean2=null;
		
		ArrayList _list=new ArrayList();
		for(int k=0;k<_itemList.size();k++)
		{
				abean2=(LazyDynaBean)_itemList.get(k);
				String parent_id=(String)abean2.get("parent_id");
				if(parent_id.equals(item_id))
					_list.add(abean2);
		}
		if(_list.size()>0)
		{
			for(int j=0;j<_list.size();j++)
			{
				abean2=(LazyDynaBean)_list.get(j);
				String _item_id=(String)abean2.get("item_id");
				getlayItemList2(_itemList,_item_id,map);
				
			}
		}
		else
			map.put(item_id,"1");
		
	}
	public HashMap getNodeItemMap(int opt)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql=new StringBuffer("select item_id,parent_id,score,rank  from  per_template_item " );
			if(opt==0)
			{
				sql.append(" where item_id not in (select parent_id  from  per_template_item where template_id='"+this.plan_vo.getString("template_id")+"' and parent_id is not null ) ");
				sql.append(" and  template_id='"+this.plan_vo.getString("template_id")+"' ");
			}
			else
				sql.append(" where  template_id='"+this.plan_vo.getString("template_id")+"' and ( parent_id is null or parent_id='') ");
			RowSet rowSet=dao.search(sql.toString());
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("item_id", rowSet.getString("item_id"));
				abean.set("parent_id", rowSet.getString("parent_id")!=null?rowSet.getString("parent_id"):"");
				abean.set("score",rowSet.getString("score")!=null?rowSet.getString("score"):"0");
				abean.set("rank",rowSet.getString("rank")!=null?rowSet.getString("rank"):"0");
				map.put(rowSet.getString("item_id"),abean);
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public  HashMap getEvaluationMap(String object_id,String mainbody_id,int plan_id)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="select * from per_target_evaluation where plan_id="+plan_id+" and object_id='"+object_id+"'  and mainbody_id='"+mainbody_id+"'";
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				map.put(rowSet.getString("p0400"),rowSet.getString("score"));
			}
			rowSet.close();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap getItemPrivMap(String object_id,String level)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable("per_itempriv_"+this.plan_vo.getInt("plan_id"),false))
			{
				String body_id="";
				if(this.mainbodyBean!=null)
					body_id=(String)this.mainbodyBean.get("body_id");
				else
				{
					String sql="select pms.* from per_plan_body ppb,per_mainbodyset pms where ppb.body_id=pms.body_id and  ppb.plan_id="+this.plan_vo.getInt("plan_id")+" and pms.level="+level;
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						sql="select pms.* from per_plan_body ppb,per_mainbodyset pms where ppb.body_id=pms.body_id and  ppb.plan_id="+this.plan_vo.getInt("plan_id")+" and pms.level_o="+level;
					RowSet rowSet=dao.search(sql);
					if(rowSet.next())
						body_id=rowSet.getString("body_id");
					rowSet.close();
				}
				
				if(body_id!=null&&body_id.trim().length()>0)
				{
					RowSet rowSet=dao.search("select * from per_itempriv_"+this.plan_vo.getInt("plan_id")+" where body_id="+body_id+" and object_id='"+object_id+"'");
					if(rowSet.next())
					{
						ResultSetMetaData mt=rowSet.getMetaData();
						for(int i=0;i<mt.getColumnCount();i++)
						{
							String columnName=mt.getColumnName(i+1);
							if(columnName.length()>2&& "C_".equalsIgnoreCase(columnName.substring(0,2)))
							{
								map.put(columnName.substring(2),rowSet.getString(columnName)!=null?rowSet.getString(columnName):"0");
							}
						}
					}
					rowSet.close();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public LazyDynaBean getMainbodyBean2(String object_id)
	{
		LazyDynaBean abean=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select * from per_mainbody where plan_id="+this.plan_vo.getInt("plan_id")+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"'");
			if(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("id",rowSet.getString("id"));
				abean.set("body_id",rowSet.getString("body_id"));
				abean.set("object_id",rowSet.getString("object_id"));
				abean.set("mainbody_id",rowSet.getString("mainbody_id"));
				abean.set("status",rowSet.getString("status")!=null?rowSet.getString("status"):"");
				abean.set("a0101",rowSet.getString("a0101"));
				abean.set("know_id",rowSet.getString("know_id")!=null?rowSet.getString("know_id"):"");
				abean.set("whole_grade_id",rowSet.getString("whole_grade_id")!=null?rowSet.getString("whole_grade_id"):"");
			}
			rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
}
