package com.hjsj.hrms.servlet.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.RenderRelationBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class UnderlingObjectiveServlet extends HttpServlet{
	private HashMap map ;
	public void init()
	{
		try
		{
	    	super.init();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		doPost(req,resp);
	}
	protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		 String year = req.getParameter("year");
		 String a0100= req.getParameter("a0100");
		 String posid=req.getParameter("posid");
		 String flag = req.getParameter("flag");
		 req.setCharacterEncoding("GBK");
		 StringBuffer XMLTREE = new StringBuffer();
		 try
		 {
			 UserView userView = (UserView) req.getSession().getAttribute(WebConstant.userView);	
			 XMLTREE = this.getXML(year, a0100, posid,flag,userView);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 resp.setContentType("text/xml;charset=UTF-8");
		 resp.getWriter().println(XMLTREE.toString());  
		 
	}
	
	/**
	 * 取得所有符合条件的计划的年份（按考核关系的计划才有效）
	 * @param userView
	 * @param con
	 * @return
	 */
	private HashMap  isCurrentYear(UserView userView,Connection con)
	{
		HashMap map =new HashMap();
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(con);
			StringBuffer buf = new StringBuffer("");
			String column="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				column="level_o";
			buf.append("select T.*,S."+column+",P.theyear,"+Sql_switcher.year("P.start_date")+" as ayear,P.cycle from (select a.plan_id,b.* from PER_OBJECT a,");
			buf.append(" (select body_id,OBJECT_ID from per_mainbody_std where MAINBODY_ID='"+userView.getA0100()+"') b ");
			buf.append(" where a.object_id=b.OBJECT_ID and "+Sql_switcher.isnull("a.kh_relations", "0")+"=0) T left join per_mainbodyset S  on T.body_id=S.body_id");
			buf.append(" left join per_plan P on T.plan_id=P.plan_id  where p.method='2' and p.object_type='2' and (p.status='5'  or p.status='4' or p.status='6' or p.status='7' or p.status='8')");
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				String plan_id=rs.getString("plan_id");
				LoadXml parameter_content = null;
    	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
				{
						
    	         	parameter_content = new LoadXml(con,plan_id+"");
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
				}
				Hashtable params = parameter_content.getDegreeWhole();
				
				String targetAppMode=(String)params.get("targetAppMode"); 
				int type=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
				String year = rs.getString("theyear");
				if(rs.getString("cycle")!=null&& "7".equals(rs.getString("cycle")))
				    year = rs.getString("ayear");
				if(type==1||map.get(year)!=null)//汇报关系，这种方式不灵
					continue;
			    String targetMakeSeries=(String)params.get("targetMakeSeries");
			    String level = rs.getString(column);
			    if("1".equals(targetMakeSeries)&& "1".equals(level))//
			    {
			    	map.put(year, year);
			    }
			    else if("2".equals(targetMakeSeries)&&("1".equals(level)|| "0".equals(level)))
			    {
			    	map.put(year, year);
			    }
			    else if("3".equals(targetMakeSeries)&&("1".equals(level)|| "0".equals(level)|| "-1".equals(level)))
			    {
			    	map.put(year, year);
			    }
			    else if("4".equals(targetMakeSeries)&&("1".equals(level)|| "0".equals(level)|| "-1".equals(level)|| "-2".equals(level)))
			    {
			    	map.put(year, year);
			    }
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try
				{
                   rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	private HashMap  isCurrentPlan(UserView userView,Connection con,String year)
	{
		HashMap map =new HashMap();
		RowSet rs = null;
		try
		{
			ContentDAO dao  = new ContentDAO(con);
			StringBuffer buf = new StringBuffer("");
			String column="level";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				column="level_o";
			buf.append("select T.*,S."+column+" from ((select a.plan_id,b.* from PER_OBJECT a,");
			buf.append(" (select body_id,OBJECT_ID from per_mainbody_std where MAINBODY_ID='"+userView.getA0100()+"') b ");
			buf.append(" where a.object_id=b.OBJECT_ID and "+Sql_switcher.isnull("a.kh_relations", "0")+"=0 and a.plan_id in");
			buf.append("(select plan_id from per_plan where (theyear="+year+" or "+Sql_switcher.year("start_date")+"="+year+") and method='2'");
			buf.append(" and status<>'0' and object_type=2 )) union all (select a.plan_id,a.body_id,a.OBJECT_ID from per_mainbody a left join ");
			buf.append(" per_object b on a.object_id=b.object_id and a.plan_id=b.plan_id where b.Kh_relations=1  and a.mainbody_id='"+userView.getA0100()+"'");
			  buf.append("and a.plan_id in(select plan_id from per_plan where (theyear="+year+" or "+Sql_switcher.year("start_date")+"="+year+") and method='2'");
			  buf.append(" and status<>'0' and object_type=2 )");
			buf.append("))T left join per_mainbodyset S  on T.body_id=S.body_id");
			buf.append(" left join per_plan P on T.plan_id=P.plan_id ");
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				String plan_id=rs.getString("plan_id");
				LoadXml parameter_content = null;
    	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
				{
						
    	         	parameter_content = new LoadXml(con,plan_id+"");
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
				}
				else
				{
					parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
				}
				Hashtable params = parameter_content.getDegreeWhole();
				String targetAppMode=(String)params.get("targetAppMode"); 
				int type=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
				if(type==1||map.get(plan_id)!=null)//汇报关系，这种方式不灵
					continue;
			    String targetMakeSeries=(String)params.get("targetMakeSeries");
			    String level = rs.getString(column);
			    if(level==null)
			    	continue;
			    if("1".equals(targetMakeSeries)&& "1".equals(level))//
			    {
			    	map.put(plan_id, plan_id);
			    }
			    else if("2".equals(targetMakeSeries)&&("1".equals(level)|| "0".equals(level)))
			    {
			    	map.put(plan_id, plan_id);
			    }
			    else if("3".equals(targetMakeSeries)&&("1".equals(level)|| "0".equals(level)|| "-1".equals(level)))
			    {
			    	map.put(plan_id, plan_id);
			    }
			    else if("4".equals(targetMakeSeries)&&("1".equals(level)|| "0".equals(level)|| "-1".equals(level)|| "-2".equals(level)))
			    {
			    	map.put(plan_id, plan_id);
			    }
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
			{
				try
				{
                   rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	
	
	private StringBuffer getXML(String year,String a0100,String posid,String flag2,UserView userView) throws GeneralException
	{
		StringBuffer buf = new StringBuffer();
		Connection con=null;
		RowSet rs = null;
		RowSet rs_sub=null;
		try
		{
			if(flag2!=null&& "view".equals(flag2)){		//目标执行情况 xieguiquan 20101018

				StringBuffer sql = new StringBuffer();
				con=(Connection)AdminDb.getConnection();
				SetUnderlingObjectiveBo suob=new SetUnderlingObjectiveBo(con);
				ExamPlanBo exbo = new ExamPlanBo(con);
				HashMap exmap = exbo.getPlansByUserView(userView, "");
				ArrayList list = new ArrayList();
				/**=0按汇报关系，=1按定义的考核关系*/
				int type=0;
				list.add("USR");
				ContentDAO dao = new ContentDAO(con);
				map = new HashMap();
				HashMap map1=new HashMap();
				HashMap map2=new HashMap();
				RenderRelationBo bo = new RenderRelationBo(con,userView);
				
				String sub_str="";
				if(SystemConfig.getPropertyValue("clientName")!=null&& "zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
					sub_str="  and pp.status<>7 ";
				if("-1".equals(year))
				{
					sql.append(" select * from (");
					sql.append(" select pp.plan_id ,pp.theyear,pp.b0110 from per_plan pp where method='2' and cycle<>'7' and pp.status>=4 "+sub_str+" ");//pp.status<>'0'
			 		sql.append("  ");
			 		
					sql.append(" ) temp order by theyear desc");
					rs = dao.search(sql.toString());
				//	System.out.print(sql);
					Element root = new Element("TreeNode");
					root.setAttribute("id","$$00");
					root.setAttribute("text","root");
					root.setAttribute("title","root");
					Document myDocument = new Document(root);
					String years = "";
					while(rs.next())
					{
//						if (!userView.isSuper_admin())
						{
							if(exmap!=null&&exmap.get(rs.getString("plan_id"))!=null){
								
							}else{
								continue;
							}
						}
						String plan_id = rs.getString("plan_id");
						String theyear=rs.getString("theyear");
						StringBuffer sub_buf = new StringBuffer();
						sub_buf.append(" select kh_relations,object_id from per_object where plan_id='"+plan_id+"'");
					
						rs_sub = dao.search(sub_buf.toString());
					
						if(rs_sub.next())
						{
							if(years.indexOf( theyear+",")==-1)
							years+= theyear+",";
							if(map.get(theyear)==null){
								map.put(theyear,plan_id+",");	
							}else{
								String plan_ids =(String) map.get(theyear);
								plan_ids+=plan_id+",";
								map.put(theyear, plan_ids);
								
							}
						}
						if(rs_sub!=null)
							rs_sub.close();
						
					}
					String theyears[] = years.split(",");
					for(int i=0;i<theyears.length;i++){
						String theyear = theyears[i];
						if(theyear!=null&&!"".equals(theyear)){
							String plan_ids =(String)map.get(theyear);
							if(plan_ids.length()>1)
								plan_ids =plan_ids.substring(0,plan_ids.length()-1);	
							Element child = new Element("TreeNode");
			    	    	child.setAttribute("id",theyear);
			    	    	child.setAttribute("text",theyear+ResourceFactory.getProperty("datestyle.year"));
			    		    child.setAttribute("title",theyear+ResourceFactory.getProperty("datestyle.year"));					
		         		    child.setAttribute("xml","/servlet/performance/UnderlingObjectiveServlet?year="+theyear+"&encryptParam="+PubFunc.encrypt("posid="+posid+"&a0100="+a0100+"&flag=view"));						
		         	        child.setAttribute("target","mil_body");
                            child.setAttribute("href","");
	    			        child.setAttribute("icon","/images/open.png");	
    				    	root.addContent(child);
						}
					}
					
					if(rs!=null)
						rs.close();
					
					
					XMLOutputter outputter = new XMLOutputter();
					Format format=Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					buf.append(outputter.outputString(myDocument));
				}
				else
				{
					HashMap map = new HashMap();
					sql.append(" select * from (");
					sql.append(" select pp.plan_id,pp.themonth,pp.thequarter,pp.name,pp.theyear,pp.cycle,"+Sql_switcher.dateToChar("pp.start_date", "yyyy-mm-dd")+" as start_date,"+Sql_switcher.dateToChar("pp.end_date", "yyyy-mm-dd")+" as end_date ");
					sql.append(" ,"+Sql_switcher.isnull("pp.a0000", "999999")+" as norder,pp.b0110,pp.object_type from per_plan pp where pp.method='2'  and pp.status>=4   "+sub_str+" ");
					sql.append("  and  pp.theyear='"+year+"'");
					sql.append(" ) temp ");
					
					sql.append("order by norder asc,plan_id desc ");
					rs = dao.search(sql.toString());
					Element root = new Element("TreeNode");
					root.setAttribute("id","$$00");
					root.setAttribute("text","root");
					root.setAttribute("title","root");
					Document myDocument = new Document(root);
					while(rs.next())
					{
//						if (!userView.isSuper_admin())
						{
							if(exmap!=null&&exmap.get(rs.getString("plan_id"))!=null){
								
							}else{
								continue;
							}
						}
						String plan_id = rs.getString("plan_id");
						String theyear=rs.getString("theyear");
						String cycle=rs.getString("cycle");
						String name=rs.getString("name");
						String object_type = rs.getString("object_type");
						
							if(map.get(plan_id)!=null)
								continue;
							map.put(plan_id, "1");
							Element child = new Element("TreeNode");
							child.setAttribute("id",plan_id);
							if("3".equals(cycle))//按月度
				    		{
					     		child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month")+")");
					    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month")+")");	
				    		}else if("0".equals(cycle))//年度
			        		{
				    			child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+")");
					    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+")");	
			        		}
				    		else if("1".equals(cycle))//半年度
			        		{
				    			String half_year=rs.getString("thequarter");
				    			if("1".equals(half_year))
				    			{
			    	    			child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.uphalfyear")+")");
				    	    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.uphalfyear")+")");
				    			}else
				    			{
				    				child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.downhalfyear")+")");
				    	    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.downhalfyear")+")");
				    			}
			        		}else if("7".equals(cycle))//不定期
			        		{
			        			String startd=PubFunc.DoFormatDate(rs.getString("start_date"));
			        			String endd=PubFunc.DoFormatDate(rs.getString("end_date"));
			        			child.setAttribute("text",name+"("+startd+"-"+endd+")");
			    	    		child.setAttribute("title",name+"("+startd+"-"+endd+")");
			        		}
			        		else if("2".equals(cycle))
			        		{
			        			String quarter = rs.getString("thequarter");
			        			child.setAttribute("text",name+"("+AdminCode.getCodeName("12",quarter)+")");
			    	    		child.setAttribute("title",name+"("+AdminCode.getCodeName("12",quarter)+")");
			        		}
				     		//child.setAttribute("xml","/servlet/performance/UnderlingObjectiveServlet?year="+theyear+"&posid="+posid+"&a0100="+a0100);						
				     	    child.setAttribute("target","mil_body");
	                        child.setAttribute("href","/performance/objectiveManage/setUnderlingObjective/underling_objective_view_list.do?b_view=init&encryptParam="+PubFunc.encrypt("entranceType=0&opt=1&posid="+posid+"&a0100="+a0100+"&plan_id="+plan_id+"&object_type="+object_type));
						    child.setAttribute("icon","/images/icon_wsx.gif");	
							root.addContent(child);
						}
					
					if(rs!=null)
						rs.close();
					XMLOutputter outputter = new XMLOutputter();
					Format format=Format.getPrettyFormat();
					format.setEncoding("UTF-8");
					outputter.setFormat(format);
					buf.append(outputter.outputString(myDocument));				
				}
					
			}else{
			StringBuffer sql = new StringBuffer();
			con=(Connection)AdminDb.getConnection();
			SetUnderlingObjectiveBo suob=new SetUnderlingObjectiveBo(con,userView);
			ArrayList list = new ArrayList();
			/**=0按汇报关系，=1按定义的考核关系*/
			int type=0;
			list.add("USR");
			ContentDAO dao = new ContentDAO(con);
			map = new HashMap();
			HashMap map1=new HashMap();
			HashMap map2=new HashMap();
			RenderRelationBo bo = new RenderRelationBo(con,userView);
			
			if("-1".equals(year))//初次进来的时候
			{
				ArrayList alist = new ArrayList();
				alist.add(posid);
				
				HashMap currMap = this.isCurrentYear(userView, con); //取你涉及到的年份
				sql.append(" select * from (");
				sql.append(" (select pp.plan_id ,pp.theyear,pp.cycle,"+Sql_switcher.year("pp.start_date")+" as ayear,status from per_plan pp where method='2'  and (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='7' or pp.status='8')");//pp.status<>'0'
		 		sql.append(" and pp.object_type='2' )");
				sql.append(" ) temp order by theyear desc");
				rs = dao.search(sql.toString());//取所有不在起草状态的人员考核计划 和年份
			//	System.out.print(sql);
				Element root = new Element("TreeNode");
				root.setAttribute("id","$$00");
				root.setAttribute("text","root");
				root.setAttribute("title","root");
				Document myDocument = new Document(root);
				HashMap objMap = new HashMap();
				HashMap<String,String> GradeMembersMap=null;
				while(rs.next())
				{
					String plan_id = rs.getString("plan_id");
					String theyear=rs.getString("theyear");//年份
					String cycle=rs.getString("cycle");//考核周期
					if("7".equals(cycle))//如果是不定期考核
						theyear=rs.getString("ayear");//开始年份
					String status=rs.getString("status");//计划状态
					
					if(map.get(theyear)!=null)//排除重复年份
						continue;
					LoadXml parameter_content = null;
	    	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)//取得考核计划参数
					{
							
	    	         	parameter_content = new LoadXml(con,plan_id+"");
						BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
					}
					else
					{
						parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
					}
	    	        
	    	        
	    	      //如果之前都不是 ，则判断是否存在启用了多评价人的情况
					ObjectCardBo objectbo=new ObjectCardBo();
					objectbo.setConn(con);
					objectbo.setPlan_id(plan_id);
					objectbo.setUserView(userView);
					if(objectbo.isOpenGrade_Members()){
						if(GradeMembersMap==null)
							GradeMembersMap=objectbo.getGradeMembersMap();
						if(GradeMembersMap.containsKey(plan_id)){
							Element child = new Element("TreeNode");
				    		child.setAttribute("id",theyear);
				    		child.setAttribute("text",theyear+ResourceFactory.getProperty("datestyle.year"));
				    		child.setAttribute("title",theyear+ResourceFactory.getProperty("datestyle.year"));					
			         		child.setAttribute("xml","/servlet/performance/UnderlingObjectiveServlet?year="+theyear+"&encryptParam="+PubFunc.encrypt("posid="+posid+"&a0100="+a0100));						
			         	    child.setAttribute("target","mil_body");
                            child.setAttribute("href","/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&encryptParam="+PubFunc.encrypt("entranceType=0&opt=4&posid="+posid+"&a0100="+a0100+"&year="+theyear));
		    			    child.setAttribute("icon","/images/open.png");	
	    					map.put(theyear,theyear);
	    					root.addContent(child);
	    					continue;
						}
					}
					
					Hashtable params = parameter_content.getDegreeWhole();
					String SpByBodySeq="False";
	    			if(params.get("SpByBodySeq")!=null)//SpByBodySeq 是否按照考核主体顺序进行审批
	    				SpByBodySeq=(String)params.get("SpByBodySeq");
                    if("true".equalsIgnoreCase(SpByBodySeq)){//按照考试主体顺序审批
                    	HashMap mm=(HashMap)objMap.get(plan_id);
                    	if(mm==null)
                    		mm=suob.getObjectBySeq(plan_id, 1);
                    	objMap.put(plan_id, mm);
                    	if(mm.size()>0){
                    		Element child = new Element("TreeNode");
    		    	    	child.setAttribute("id",theyear);
    		    	    	child.setAttribute("text",theyear+ResourceFactory.getProperty("datestyle.year"));
    		    		    child.setAttribute("title",theyear+ResourceFactory.getProperty("datestyle.year"));					
    	         		    child.setAttribute("xml","/servlet/performance/UnderlingObjectiveServlet?year="+theyear+"&encryptParam="+PubFunc.encrypt("posid="+posid+"&a0100="+a0100));						
    	         	        child.setAttribute("target","mil_body");
                            child.setAttribute("href","/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&encryptParam="+PubFunc.encrypt("entranceType=0&opt=1&posid="+posid+"&a0100="+a0100+"&year="+theyear));
        			        child.setAttribute("icon","/images/open.png");	
    				    	map.put(theyear,theyear);
    				    	root.addContent(child);
    				    	continue;
                    	}else{
                    		continue;
                    	}
                    	
                    }
					if(currMap.get(theyear)!=null)//如果当前年份是该用户参与的
					{
						Element child = new Element("TreeNode");
		    	    	child.setAttribute("id",theyear);
		    	    	child.setAttribute("text",theyear+ResourceFactory.getProperty("datestyle.year"));
		    		    child.setAttribute("title",theyear+ResourceFactory.getProperty("datestyle.year"));					
	         		    child.setAttribute("xml","/servlet/performance/UnderlingObjectiveServlet?year="+theyear+"&encryptParam="+PubFunc.encrypt("posid="+posid+"&a0100="+a0100));						
	         	        child.setAttribute("target","mil_body");
                        child.setAttribute("href","/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&encryptParam="+PubFunc.encrypt("entranceType=0&opt=1&posid="+posid+"&a0100="+a0100+"&year="+theyear));
    			        child.setAttribute("icon","/images/open.png");	
				    	map.put(theyear,theyear);
				    	root.addContent(child);
				    	continue;
					}
					StringBuffer sub_buf = new StringBuffer();
					HashMap mainbodyMap =null;//suob.getMainbodyBean(plan_id);
					if(map2.get(plan_id)!=null)
					{
						mainbodyMap=(HashMap)map2.get(plan_id);
					}
						    			
				    String targetMakeSeries=(String)params.get("targetMakeSeries");// 目标卡制订支持几级审批
					sub_buf.append(" select kh_relations,object_id from per_object where plan_id='"+plan_id+"'");//遍历当前计划所有考核对象 获取考核关系 kh_relations 0标准 1非标准 
					rs_sub = dao.search(sub_buf.toString());
					while(rs_sub.next())
					{
						if(map.get(theyear)!=null)
							break;
						String object_id = rs_sub.getString("object_id");
						String market=rs_sub.getString("kh_relations");
						if(market==null)
							market="0";
						/**非标准关系*/
						if("1".equals(market))
						{
							if(mainbodyMap==null)
							{
								
							    mainbodyMap = suob.getMainbodyBean(plan_id);
								map2.put(plan_id,mainbodyMap);
							}
							if(mainbodyMap.containsKey(userView.getA0100()+object_id)&&map.get(theyear)==null)
					    	{
								if(suob.isCanSP(plan_id, object_id, userView.getA0100()))
								{
					    	    	Element child = new Element("TreeNode");
					    	    	child.setAttribute("id",theyear);
					    	    	child.setAttribute("text",theyear+ResourceFactory.getProperty("datestyle.year"));
					    		    child.setAttribute("title",theyear+ResourceFactory.getProperty("datestyle.year"));					
				         		    child.setAttribute("xml","/servlet/performance/UnderlingObjectiveServlet?year="+theyear+"&encryptParam="+PubFunc.encrypt("posid="+posid+"&a0100="+a0100));						
				         	        child.setAttribute("target","mil_body");
                                    child.setAttribute("href","/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&encryptParam="+PubFunc.encrypt("entranceType=0&opt=1&posid="+posid+"&a0100="+a0100+"&year="+theyear));
			    			        child.setAttribute("icon","/images/open.png");	
		    				    	map.put(theyear,theyear);
		    				    	root.addContent(child);
								}
		    				}
						}
						else
						{
							HashMap infomap=bo.getReportRelationChildren(alist, 4, list);//汇报关系的所有下级
				    		for(int i=0;i<list.size();i++)
				    		{
					    		String nbase = (String)list.get(i);							    		
						    	if(infomap.containsKey(nbase.toUpperCase()+object_id)&&map.get(theyear)==null)
						    	{
						    		LazyDynaBean abean=(LazyDynaBean)infomap.get(nbase.toUpperCase()+object_id);
									String isSP=(String)abean.get("isSP");
									String level = (String)abean.get("level");
									if("1".equals(targetMakeSeries)&&!"1".equals(level))//
									{
									    continue;
									}
									else if("2".equals(targetMakeSeries)&&!"1".equals(level)&&!"0".equals(level))
									{
									    continue;
									}
								    else if("3".equals(targetMakeSeries)&&!"1".equals(level)&&!"0".equals(level)&&!"-1".equals(level))
									{
									   continue;
									}
								    else if("4".equals(targetMakeSeries)&&!"1".equals(level)&&!"0".equals(level)&&!"-1".equals(level)&&!"-2".equals(level))
									{
									    continue;
									}
									if(!"1".equals(isSP))
										continue;
						    		Element child = new Element("TreeNode");
						    		child.setAttribute("id",theyear);
						    		child.setAttribute("text",theyear+ResourceFactory.getProperty("datestyle.year"));
						    		child.setAttribute("title",theyear+ResourceFactory.getProperty("datestyle.year"));					
					         		child.setAttribute("xml","/servlet/performance/UnderlingObjectiveServlet?year="+theyear+"&encryptParam="+PubFunc.encrypt("posid="+posid+"&a0100="+a0100));						
					         	    child.setAttribute("target","mil_body");
                                    child.setAttribute("href","/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&encryptParam="+PubFunc.encrypt("entranceType=0&opt=4&posid="+posid+"&a0100="+a0100+"&year="+theyear));
				    			    child.setAttribute("icon","/images/open.png");	
			    					map.put(theyear,theyear);
			    					root.addContent(child);
			    				}
	    					}
						}
					}
					if(rs_sub!=null)
						rs_sub.close();
					
					
					
					
					
					
				}
				if(rs!=null)
					rs.close();
				XMLOutputter outputter = new XMLOutputter();
				Format format=Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				buf.append(outputter.outputString(myDocument));
			}
			else
			{
				HashMap<String,String> GradeMembersMap=null;
				HashMap currMap = this.isCurrentPlan(userView, con, year);
				HashMap map = new HashMap();
				sql.append(" select * from (");
				sql.append(" (select pp.plan_id,pp.themonth,pp.thequarter,pp.name,pp.theyear,pp.cycle,"+Sql_switcher.dateToChar("pp.start_date", "yyyy-mm-dd")+" as start_date,"+Sql_switcher.dateToChar("pp.end_date", "yyyy-mm-dd")+" as end_date ");
				sql.append(" ,"+Sql_switcher.isnull("pp.a0000", "999999")+" as norder,status from per_plan pp where pp.method='2'  and (pp.status='5'  or pp.status='4' or pp.status='6' or pp.status='7' or pp.status='8')");
				sql.append(" and pp.object_type='2' and ( pp.theyear='"+year+"' or "+Sql_switcher.year("pp.start_date")+"="+year+"))");
				sql.append(" ) temp order by norder asc,plan_id desc ");
				rs = dao.search(sql.toString());
				Element root = new Element("TreeNode");
				root.setAttribute("id","$$00");
				root.setAttribute("text","root");
				root.setAttribute("title","root");
				Document myDocument = new Document(root);
				HashMap objMap = new HashMap();
				objMap=suob.getObjectBySeq();//非自评的记录
			    while(rs.next())
				{
					String plan_id = rs.getString("plan_id");
					String status=rs.getString("status");
					LoadXml parameter_content = null;
	    	        if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"")==null)
					{
							
	    	         	parameter_content = new LoadXml(con,plan_id+"");
						BatchGradeBo.getPlanLoadXmlMap().put(plan_id+"",parameter_content);
					}
					else
					{
						parameter_content=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id+"");
					}
					Hashtable params = parameter_content.getDegreeWhole();
					String targetAppMode=(String)params.get("targetAppMode"); 
					int targetAppModetype=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
					String SpByBodySeq="False";
					
					
					ObjectCardBo objectbo=new ObjectCardBo();
					objectbo.setConn(con);
					objectbo.setPlan_id(plan_id);
					objectbo.setUserView(userView);
					if(objectbo.isOpenGrade_Members()){//如启用了目标卡多评价人 那么 只要当前计划中当前用户被指定为了评价人之一 那么就需要显示 zhanghua 
						
						if(GradeMembersMap==null)
							GradeMembersMap=objectbo.getGradeMembersMap();
						if(GradeMembersMap.containsKey(plan_id)){
							String theyear=rs.getString("theyear");
							String cycle=rs.getString("cycle");
							String name=rs.getString("name").trim();
							root.addContent(this.getElement(plan_id, cycle, name, theyear, posid, a0100, rs));
	    					continue;
						}
					}
					
	    			if(params.get("SpByBodySeq")!=null)
	    				SpByBodySeq=(String)params.get("SpByBodySeq");
                    if("true".equalsIgnoreCase(SpByBodySeq)){
                    	//HashMap mm=(HashMap)objMap.get(plan_id);
                    	//if(mm==null)
                    	 
                    	//objMap.put(plan_id, mm);
                    	if(objMap.get(plan_id)!=null){
                    		String theyear=rs.getString("theyear");
    						String cycle=rs.getString("cycle");
    						String name=rs.getString("name").trim();
    						map.put(plan_id, "1");
    						root.addContent(this.getElement(plan_id, cycle, name, theyear, posid, a0100, rs));
    						continue;
                    	}else{                   		
                    		continue;
                    	}
                    	
                    }
					if(currMap.get(plan_id)!=null&&map.get(plan_id)==null)
					{
						String theyear=rs.getString("theyear");
						String cycle=rs.getString("cycle");
						String name=rs.getString("name").trim();
						map.put(plan_id, "1");
						root.addContent(this.getElement(plan_id, cycle, name, theyear, posid, a0100, rs));
						continue;
					}
					else
					{
						if(targetAppModetype!=1)
						{				
							continue;
						}
							
					}
					HashMap infomap=null;//bo.getPer_MainBodyInfo(list, posid, 3, plan_id);
					if(map1.get(posid+plan_id)!=null)
					{
						infomap=(HashMap)map1.get(posid+plan_id);
					}
					else
					{
		    			infomap =bo.getPer_MainBodyInfo(list, posid, 3, plan_id);
		    			map1.put(posid+plan_id, infomap);
					}
					String theyear=rs.getString("theyear");
					String cycle=rs.getString("cycle");
					String name=rs.getString("name");
				    String targetMakeSeries=(String)params.get("targetMakeSeries");
					StringBuffer sub_buf = new StringBuffer();
					HashMap mainbodyMap =null;//suob.getMainbodyBean(plan_id);
					if(map2.get(plan_id)!=null)
					{
						mainbodyMap=(HashMap)map2.get(plan_id);
					}
					
					sub_buf.append(" select object_id,kh_relations from per_object where plan_id='"+plan_id+"'");
					rs_sub = dao.search(sub_buf.toString());
					boolean flag=false;
					while(rs_sub.next())
					{
						if(flag)
						{
							break;
						}
						String object_id = rs_sub.getString("object_id");
						String market=(rs_sub.getString("kh_relations")==null?"0":rs_sub.getString("kh_relations"));
						if("1".equals(market))
				    	{
							if(mainbodyMap==null)
							{
							   mainbodyMap = suob.getMainbodyBean(plan_id);
							   map2.put(plan_id,mainbodyMap);
							}
							
							if(mainbodyMap.containsKey(userView.getA0100()+object_id))
			    			{
								if(suob.isCanSP(plan_id, object_id, userView.getA0100()))
								{
							    	LazyDynaBean abean=(LazyDynaBean)mainbodyMap.get(userView.getA0100()+object_id);
							    	String level=(String)abean.get("level");
							    	int mlevel=suob.getTargetMakeSeriesLevel(Integer.parseInt(level));
							    	if(mlevel>Integer.parseInt(targetMakeSeries))
							          continue;
			    			    	flag=true;
		    				    	break;
								}
		    				}
				    	}
				    	else
				    	{
				    		for(int i=0;i<list.size();i++)
				    		{
				    			String nbase = (String)list.get(i);
				    			if(infomap.containsKey(nbase+object_id))
				    			{
				    				LazyDynaBean abean=(LazyDynaBean)infomap.get(nbase.toUpperCase()+object_id);
									String isSP=(String)abean.get("isSP");
									if(!"1".equals(isSP))
										continue;
				    				flag=true;
			    					break;
			    				}
			    			}
						}
						
					}
					if(rs_sub!=null)
						rs_sub.close();
					if(flag)
					{
						if(map.get(plan_id)!=null)
							continue;
						map.put(plan_id, "1");
						root.addContent(this.getElement(plan_id, cycle, name, theyear, posid, a0100, rs));
					}
				}
			    if(rs!=null)
					rs.close();
				XMLOutputter outputter = new XMLOutputter();
				Format format=Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				buf.append(outputter.outputString(myDocument));				
			}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try
			{
		    	if(rs!=null)
		    	{
		    		rs.close();
		    	}
		     	if(rs_sub!=null)
		    	{
		    		rs_sub.close();
		    	}
		    	if(con!=null&&!con.isClosed())
		    		con.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return buf;
	}
	
	
	private Element getElement(String plan_id,String cycle,String name,String theyear,String posid,String a0100,RowSet rs){
		Element child = new Element("TreeNode");
		try{
			child.setAttribute("id",plan_id);
			if("3".equals(cycle))//按月度
    		{
	     		child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month")+")");
	    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+" "+rs.getString("themonth")+ResourceFactory.getProperty("datestyle.month")+")");	
    		}else if("0".equals(cycle))//年度
    		{
    			child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+")");
	    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+")");	
    		}
    		else if("1".equals(cycle))//半年度
    		{
    			String half_year=rs.getString("thequarter");
    			if("1".equals(half_year)|| "01".equals(half_year))
    			{
	    			child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.uphalfyear")+")");
    	    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.uphalfyear")+")");
    			}else
    			{
    				child.setAttribute("text",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.downhalfyear")+")");
    	    		child.setAttribute("title",name+"("+theyear+ResourceFactory.getProperty("datestyle.year")+ResourceFactory.getProperty("report.pigeonhole.downhalfyear")+")");
    			}
    		}else if("7".equals(cycle))//不定期
    		{
    			String startd=PubFunc.DoFormatDate(rs.getString("start_date"));
    			String endd=PubFunc.DoFormatDate(rs.getString("end_date"));
    			child.setAttribute("text",name+"("+startd+"-"+endd+")");
	    		child.setAttribute("title",name+"("+startd+"-"+endd+")");
    		}
    		else if("2".equals(cycle))
    		{
    			String quarter = rs.getString("thequarter");
    			child.setAttribute("text",name+"("+AdminCode.getCodeName("12",quarter)+")");
	    		child.setAttribute("title",name+"("+AdminCode.getCodeName("12",quarter)+")");
    		}
    						
     	    child.setAttribute("target","mil_body");
            child.setAttribute("href","/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&encryptParam="+PubFunc.encrypt("entranceType=0&opt=1&posid="+posid+"&a0100="+a0100+"&plan_id="+plan_id));
		    child.setAttribute("icon","/images/icon_wsx.gif");	
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return child;
	}
}
