package com.hjsj.hrms.businessobject.performance.interview;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class PerformanceInterviewBo {
	
	private Connection conn;
	public PerformanceInterviewBo(Connection conn)
	{
		this.conn=conn;
	}
	public HashMap getPlanNameMap(ContentDAO dao)
	{
      	 HashMap map = new HashMap();
      	 try
      	 {
      		 String sql = "select plan_id,name from per_plan ";
      		 RowSet rs= dao.search(sql);
      		 while(rs.next())
      		 {
      			 map.put(rs.getString("plan_id"),rs.getString("name"));
      		 }
      	 }
      	 catch(Exception e)
      	 {
      		 e.printStackTrace();
      	 }
    	 return map;
	}
	public ArrayList getUnderlingEmployeeList(String posID,ArrayList dbname,UserView view,String plan_id,String type,String khObjWhere)
	{
		ArrayList list = new ArrayList();
		try
		{
			ExamPlanBo ebo = new ExamPlanBo(this.conn);
			String controlByKHMoudle = ebo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			
			if("0".equals(type))
			{
	    		StringBuffer sql = new StringBuffer();
	    		StringBuffer buf = new StringBuffer("");
		    	String str=this.getLeaderSql(view,plan_id);
	    		sql.append("select T.*,a.template_id,"+Sql_switcher.isnull("a.a0000", "999999")+" as norder from ((");
	    		sql.append(" select '0' as TT,a.object_id ,a.mainbody_id,a.plan_id,a.body_id");
	    		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql.append(",b.level_o as lv ");
                } else {
                    sql.append(",b.level as lv ");
                }
	    		sql.append(" from per_mainbody a left join per_mainbodyset b on a.body_id=b.body_id");
		    	sql.append(" where a.mainbody_id='"+view.getA0100()+"' and a.object_id<>'"+view.getA0100()+"' and a.body_id<>-1");
		    	if(!"-1".equals(plan_id)) {
                    sql.append(" and a.plan_id="+plan_id+")");
                } else {
                    sql.append(" and a.plan_id in (select plan_id from per_plan where status='6' or status='7'))");
                }
	    		sql.append(" union (");
	    		sql.append(" select '1' as TT,a.object_id ,a.mainbody_id,a.plan_id,a.body_id");
	        	if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    sql.append(",b.level_o as lv ");
                } else {
                    sql.append(",b.level as lv ");
                }
		    	sql.append(" from per_mainbody a left join per_mainbodyset b on a.body_id=b.body_id");
	    		sql.append(" where ((a.object_id='"+view.getA0100()+"')");
	    		if(!"".equals(str)) {
                    sql.append(" or ("+str+")");
                }
	    		sql.append(")");
	    		if(!"-1".equals(plan_id)) {
                    sql.append(" and a.plan_id="+plan_id);
                } else {
                    sql.append(" and a.plan_id in (select plan_id from per_plan where status='6' or status='7')");
                }
                sql.append(")) T left join per_plan a on T.plan_id=a.plan_id order by norder asc,a.plan_id desc ");
    			ContentDAO dao = new ContentDAO(this.conn);
    			RowSet rs = null;
    			HashMap planMap = this.getPlanInfo(plan_id, type);
    			HashMap objectMap = this.getPerObjectInfo(plan_id, type);
    			HashMap interviewMap = this.getPerInterviewInfo(plan_id, type);
    			HashMap leaderMap = this.getOrgLeaderMap(plan_id,type);
	    		rs = dao.search(sql.toString());
    			/**登录用户给面谈的考核对象*/
    			int i=0;
    			String planiidd="";
	     		String objectiidd="";
			
	    		while(rs.next())
	    		{
	    			if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
					{
						String template_id = rs.getString("template_id");				
						if(!(view.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
						{
							//  写权限 template_id  读权限 template_id+"R"
							if(!view.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                                continue;
                            }
						}
					}
	    			String tt=rs.getString("TT");
		    		if("0".equals(tt))
	    			{
	    	    		String planid=rs.getString("plan_id");
		        		String a_a0100=rs.getString("object_id");
		        		String mainbody=rs.getString("mainbody_id");
		        		/*if(finishPlanMap.get(planid)!=null)
			        	{
		    	    		if(interviewedMap.get(a_a0100+mainbody+planid)!=null)
		    	    		{
		    	    			continue;
		    	     		}
		    	    	}*/
		     	    	String level=rs.getString("lv")==null?"":rs.getString("lv");
		    	    	/**同事和下属不可看*/
		        		if(!"5".equals(level)&&!"0".equals(level)&&!"1".equals(level)&&!"-1".equals(level)&&!"-2".equals(level)) {
                            continue;
                        }
		        		/**自己*/
		        		//if(a_a0100.equalsIgnoreCase(mainbody))
	        				//continue;
		  		
    		    		LazyDynaBean planBean=(LazyDynaBean)planMap.get(planid);
	        			String planname="";
	    	    		/**默认考核对象类型为人员*/
	    	    		String object_type="2";
	    	    		if(planBean!=null)
		        		{
		        			planname=(String)planBean.get("name");
	        				object_type=(String)planBean.get("object_type");
	        			}
	        			String leader="";
	    	    		if(!"2".equals(object_type))
	    	    		{
	        				LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(a_a0100+planid);
	        				if(leaderBean!=null)
		        			{
		         				leader=(String)leaderBean.get("a0101");
	        				}
		        			else {
                                continue;
                            }
	         			}
		        		LazyDynaBean objectBean=(LazyDynaBean)objectMap.get(planid+a_a0100);
		        		String b0110="";
		        		String e0122="";
	    	    		String e01a1="";
	    	    		String a0101="";
	    	    		if(objectBean!=null)
	    	    		{
	     	    			b0110=(String)objectBean.get("b0110");
	    	     			e0122=(String)objectBean.get("e0122");
	    	    			e01a1=(String)objectBean.get("e01a1");
	    	    			a0101=(String)objectBean.get("a0101");
	    	     		}
	    	    		//pmo
	    	    		String interviewID=(String)interviewMap.get(planid+mainbody+a_a0100);
	    	    		String id="-1";
	    	    		String status="0";
	    	    		if(interviewID!=null&&!"".equals(interviewID.trim()))
	    	    		{
	    	    			String[] arr=interviewID.split("#");
	    	    			id=arr[0];
	    	    			status=arr[1];
	    	    		}
	    	    		LazyDynaBean bean = new LazyDynaBean();
	    	    		bean.set("b0110",AdminCode.getCodeName("UN",b0110));
		        		bean.set("e01a1",AdminCode.getCodeName("@K",e01a1));
	    	    		bean.set("e0122",AdminCode.getCodeName("UM",e0122));
	    	    		if(!"2".equals(object_type))
	    	    		{
		        			if(leader.length()>0) {
                                a0101+="(负责人："+leader+")";
                            }
	    	    		}
	    	    		bean.set("a0101",a0101);
		        		bean.set("planid",PubFunc.encrypt(planid));
		        		bean.set("a0100",PubFunc.encrypt(a_a0100));
		             	bean.set("name",planname);
	        			bean.set("body", "5".equals(level)?"1":"0");
	        			bean.set("id",id);
	    	    		bean.set("oper", "1");
	    	    		bean.set("status", status);
	        			list.add(bean);	
		    		}
		    		else
    				{
					
    					if(i==0)
		    			{
		    				planiidd=rs.getString("plan_id");
		    				objectiidd=rs.getString("object_id");
		    			}
		    			if(i!=0&&planiidd.equals(rs.getString("plan_id"))&&objectiidd.equals(rs.getString("object_id"))) {
                            continue;
                        } else
		    			{
			    			planiidd=rs.getString("plan_id");
			    			objectiidd=rs.getString("object_id");
			    		}
			     		String a_a0100=rs.getString("object_id");
			     		String mainbody=rs.getString("mainbody_id");
			     		String planid=rs.getString("plan_id");
					
				    	LazyDynaBean objectBean=(LazyDynaBean)objectMap.get(planid+a_a0100);
				     	String b0110="";
			    		String e0122="";
			    		String e01a1="";
			    		String a0101="";
			    		if(objectBean!=null)
			    		{
				    		b0110=(String)objectBean.get("b0110");
				    		e0122=(String)objectBean.get("e0122");
			     			e01a1=(String)objectBean.get("e01a1");
			    			a0101=(String)objectBean.get("a0101");
			    		}
			    		LazyDynaBean planBean=(LazyDynaBean)planMap.get(planid);
			    		String planname="";
			    		String object_type="2";
			    		if(planBean!=null)
			    		{
			    			planname=(String)planBean.get("name");
			    			object_type=(String)planBean.get("object_type");
			    		}
			    		String leader="";
		    			if(!"2".equals(object_type))
		    			{
			    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(a_a0100+planid);
		    				if(leaderBean!=null)
		    				{
		    					leader=(String)leaderBean.get("a0101");
		    				}
			    		}
		    			if(!"2".equals(object_type))
		    			{
		    				if(leader.length()>0) {
                                a0101+="(负责人："+leader+")";
                            }
		    			}
	    			    LazyDynaBean bean = new LazyDynaBean();
		    			bean.set("b0110",AdminCode.getCodeName("UN",b0110));
	     				bean.set("e01a1",AdminCode.getCodeName("@K",e01a1));
	    				bean.set("e0122",AdminCode.getCodeName("UM",e0122));
	    				bean.set("a0101",a0101);
	    				if(planBean!=null) {
                            planname=(String)planBean.get("name");
                        }
	    				bean.set("planid",PubFunc.encrypt(planid));
	    				bean.set("a0100",PubFunc.encrypt(a_a0100));
	    				bean.set("name",planname);
	    				bean.set("body","1");
	    				String id="-1";
			    		String interviewID=(String)interviewMap.get(planid+mainbody+a_a0100);
			    		String status="0";
	    	    		if(interviewID!=null&&!"".equals(interviewID.trim()))
	    	    		{
	    	    			String[] arr=interviewID.split("#");
	    	    			id=arr[0];
	    	    			status=arr[1];
	    	    		}
	    	    		bean.set("status", status);
			    		bean.set("id",id);
		    			bean.set("oper","0");
	    				list.add(bean);
	    				i++;
	    			}
    			}
			}else
			{
				/*select * from per_object left join per_interview on per_object.plan_id=per_interview.plan_id and
				 per_object.object_id=per_interview.object_id where per_object.plan_id=54*/
				StringBuffer buf = new StringBuffer("");
				buf.append(" select b0110,e0122,e01a1,object_id,a0101 from ");
				buf.append(" per_object  ");
				buf.append(" where ");
				buf.append(" plan_id="+plan_id);
				if(khObjWhere!=null&&!"".equals(khObjWhere)) {
                    buf.append(SafeCode.decode(khObjWhere));
                }
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = dao.search(buf.toString());
				RecordVo vo = new RecordVo("per_plan");
				vo.setInt("plan_id", Integer.parseInt(plan_id));
				vo=dao.findByPrimaryKey(vo);
				String object_type=vo.getString("object_type");
				HashMap leaderMap = this.getOrgLeaderMap(plan_id,type);
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					String a0101=rs.getString("a0101")==null?"":rs.getString("a0101");
					String b0110=rs.getString("b0110")==null?"":rs.getString("b0110");
					String e0122=rs.getString("e0122")==null?"":rs.getString("e0122");
					String e01a1=rs.getString("e01a1")==null?"":rs.getString("e01a1");
		    		bean.set("b0110",AdminCode.getCodeName("UN",b0110));
	     			bean.set("e01a1",AdminCode.getCodeName("@K",e01a1));
	    			bean.set("e0122",AdminCode.getCodeName("UM",e0122));
	    			String a_a0100=rs.getString("object_id");
	    			String leader="";
	    			if(!"2".equals(object_type))
	    			{
		    			LazyDynaBean leaderBean=(LazyDynaBean)leaderMap.get(a_a0100+plan_id);
	    				if(leaderBean!=null)
	    				{
	    					leader=(String)leaderBean.get("a0101");
	    				}
		    		}
	    			if(!"2".equals(object_type))
	    			{
	    				if(leader.length()>0) {
                            a0101+="(负责人："+leader+")";
                        }
	    			}
	    			bean.set("a0101",a0101);
	    			bean.set("planid",PubFunc.encrypt(plan_id));
    				bean.set("a0100",PubFunc.encrypt(a_a0100));
    				bean.set("name",vo.getString("name"));
    				bean.set("body","1");
    				String id="-1";
		    		bean.set("id",id);
	    			bean.set("oper","0");
	    			list.add(bean);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String  getLeaderSql(UserView view,String plan_id)
	{
	  String str="";
	  try
	  {
		  StringBuffer sql = new StringBuffer("");
		  StringBuffer buf = new StringBuffer("");
		  buf.append("select object_id,mainbody_id,plan_id");
		  buf.append(" from per_mainbody where mainbody_id=");
		  buf.append("'"+view.getA0100()+"' and body_id=-1 ");
		  if(!"-1".equals(plan_id)) {
              buf.append(" and plan_id="+plan_id);
          }
		  ContentDAO dao = new ContentDAO(this.conn);
		  RowSet rs = dao.search(buf.toString());
		  while(rs.next())
		  {
			  sql.append(" or (a.mainbody_id='"+rs.getString("mainbody_id")+"' and a.object_id='"+rs.getString("object_id")+"' and a.plan_id="+rs.getString("plan_id")+" ) ");
		  }
		  if(sql.toString().length()>0) {
              str=sql.toString().substring(3);
          }
		  
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  return str;
	}
	public ArrayList getPlanList(UserView view)
	{
		ArrayList list = new ArrayList();
		try
		{
			ExamPlanBo ebo = new ExamPlanBo(this.conn);
			String controlByKHMoudle = ebo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			
			StringBuffer sql = new StringBuffer();
			sql.append("select plan_id,name,template_id,"+Sql_switcher.isnull("a0000", "999999")+" as norder from per_plan where plan_id in(");
			sql.append(" select distinct plan_id  from per_mainbody where mainbody_id='"+view.getA0100()+"'");
			sql.append(" or object_id='"+view.getA0100()+"') and (status=6 or status=7) order by norder asc,plan_id desc");
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs = dao.search(sql.toString());
            CommonData commondata = new CommonData("-1","全部");
            list.add(commondata);
            while(rs.next())
            {
            	if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rs.getString("template_id");				
					if(!(view.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!view.isHaveResource(IResourceConstant.KH_MODULE,template_id)) {
                            continue;
                        }
					}
				}
            	CommonData cd = new CommonData(rs.getString("plan_id"),rs.getString("name"));
            	list.add(cd);
            }

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public HashMap getInterviewContentById(String id)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select interview,status from per_interview where id="+id;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs= dao.search(sql);
			while(rs.next())
			{
				String str=Sql_switcher.readMemo(rs,"interview");
				map.put("str",str);
				String status=rs.getString("status")==null?"0":rs.getString("status");
				map.put("status", status);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public String getInterviewContent(String plan_id,String object_id)
	{
		StringBuffer buf = new StringBuffer("");
		try
		{
			StringBuffer sql = new StringBuffer("");
			ContentDAO dao =new ContentDAO(this.conn);
			sql.append(" select interview,create_date,a0101 from per_interview,usra01 " +
					"where " +"usra01.a0100=per_interview.mainbody_id " );
			sql.append(" and per_interview.plan_id="+plan_id+" and per_interview.object_id='"+object_id+"'");
			RowSet rs = dao.search(sql.toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next())
			{
				buf.append("面谈人："+rs.getString("a0101")+"        ");
				buf.append("面谈时间："+format.format(rs.getDate("create_date"))+"\r\n");
				buf.append("内容： \r\n");
				buf.append(Sql_switcher.readMemo(rs, "interview")+"\r\n");
				buf.append("\r\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 取得考核计划信息
	 * @return
	 */
	public HashMap getPlanInfo(String planid,String type )
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select plan_id,name,object_type,method,status from per_plan where status='6' or status='7'";
			if("1".equals(type)) {
                sql+=" and plan_id="+planid;
            }
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String plan_id = rs.getString("plan_id");
				String name=rs.getString("name");
				String object_type=rs.getString("object_type");
				String method=rs.getString("method");
				String status=rs.getString("status");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("plan_id", plan_id);
				bean.set("name", name);
				bean.set("object_type", object_type);
				bean.set("method",method);
				bean.set("status", status);
				map.put(plan_id, bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 取得考核对象信息
	 * @return
	 */
	public HashMap getPerObjectInfo(String plan_id,String type)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select b0110,e0122,e01a1,a0101,object_id,plan_id from per_object where ";
			if("1".equals(type)) {
                sql+=" plan_id = "+plan_id;
            } else {
                sql+="plan_id in (select plan_id from per_plan where status='6' or status ='7')";
            }
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("b0110",rs.getString("b0110")==null?"":rs.getString("b0110"));
				bean.set("e0122", rs.getString("e0122")==null?"":rs.getString("e0122"));
				bean.set("e01a1",rs.getString("e01a1")==null?"":rs.getString("e01a1"));
				bean.set("a0101", rs.getString("a0101")==null?"":rs.getString("a0101"));
				bean.set("object_id",rs.getString("object_id")==null?"":rs.getString("object_id"));
				bean.set("plan_id",rs.getString("plan_id"));
				map.put(rs.getString("plan_id")+rs.getString("object_id"),bean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 面谈记录
	 * @return
	 */
	public HashMap getPerInterviewInfo(String plan_id,String type)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select id,object_id,mainbody_id,plan_id,status from per_interview where  ";
			if("1".equals(type)) {
                sql+=" plan_id="+plan_id;
            } else {
                sql+="plan_id in (select plan_id from per_plan where status='6' or status='7')";
            }
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String status=rs.getString("status");
				if(status==null) {
                    status="0";
                }
				map.put(rs.getString("plan_id")+rs.getString("mainbody_id")+rs.getString("object_id"), rs.getString("id")+"#"+status);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 得到团队负责人
	 * @return
	 */
	 public HashMap getOrgLeaderMap(String plan_id,String type)
	    {
	    	HashMap map = new HashMap();
	        try
	    	{
	    		StringBuffer buf = new StringBuffer();
	    		buf.append("select object_id,plan_id,mainbody_id,e01a1,b0110,e0122,a0101 from per_mainbody  where ");
	    		buf.append(" body_id='-1' ");
	    		if("0".equals(type))
	    		{
	    	    	buf.append(" and plan_id in (select plan_id from per_plan pp where ");
	    	    	buf.append(" pp.status='6' or pp.status='7')");
	    		}
	    		else
	    		{
	    			buf.append(" and plan_id="+plan_id);
	    		}
	    		//buf.append(" and per_mainbody.mainbody_id=u.a0100 ");
	    		ContentDAO dao = new ContentDAO(this.conn);
	    		RowSet rs = dao.search(buf.toString());
	    		while(rs.next())
	    		{
	    			String object_id = rs.getString("object_id");
	    			String planid = rs.getString("plan_id");
	    			String mainbody_id = rs.getString("mainbody_id");
	    			String e01a1=rs.getString("e01a1");
	    			LazyDynaBean bean = new LazyDynaBean();
	    			bean.set("a0100",mainbody_id);
	    			bean.set("e01a1",e01a1);
	    			bean.set("b0110",rs.getString("b0110"));
	    			bean.set("e0122",rs.getString("e0122"));
	    			bean.set("a0101", rs.getString("a0101"));
	    			map.put(object_id+planid,bean);
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return map;
	    }
	public ArrayList  getTabids(String plan_id)
	{
		ArrayList list  = new ArrayList();
		try
		{
			String ret="";
			StringBuffer sql = new StringBuffer();
			sql.append("select tabids from per_template where template_id=( select template_id from ");
			sql.append("per_plan where plan_id="+plan_id+")");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				if(rs.getString("tabids")!=null) {
                    ret=rs.getString("tabids");
                }
			}
			String[] arr=ret.split(",");
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				buf.append(","+arr[i]);
			}
			if(buf.toString().length()>0)
			{
	    		sql.setLength(0);
		    	sql.append("select tabid,name from rname where tabid in ("+buf.toString().substring(1)+")");
		    	RowSet rowSet =dao.search(sql.toString());
		    	while(rowSet.next())
		    	{
		    		LazyDynaBean bean = new LazyDynaBean();
		    		bean.set("id",rowSet.getString("tabid"));
		    		bean.set("name", rowSet.getString("name"));
		    		list.add(bean);
		    	}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public ArrayList  getTabids(String plan_id,UserView userView)
	{
		ArrayList list  = new ArrayList();
		try
		{
			String ret="";
			StringBuffer sql = new StringBuffer();
			sql.append("select tabids from per_template where template_id=( select template_id from ");
			sql.append("per_plan where plan_id="+plan_id+")");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql.toString());
			while(rs.next())
			{
				if(rs.getString("tabids")!=null) {
                    ret=rs.getString("tabids");
                }
			}
			String[] arr=ret.split(",");
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i])) {
                    continue;
                }
				if(userView!=null&&!userView.isSuper_admin()&&!userView.isHaveResource(IResourceConstant.CARD,arr[i].trim())) {
                    continue;
                }
				buf.append(","+arr[i]);
			}
			if(buf.toString().length()>0)
			{
	    		sql.setLength(0);
		    	sql.append("select tabid,name from rname where tabid in ("+buf.toString().substring(1)+")");
		    	RowSet rowSet =dao.search(sql.toString());
		    	while(rowSet.next())
		    	{
		    		LazyDynaBean bean = new LazyDynaBean();
		    		bean.set("id",rowSet.getString("tabid"));
		    		bean.set("name", rowSet.getString("name"));
		    		list.add(bean);
		    	}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	/**
	 * 取得已结束的计划
	 * @return
	 */
	public HashMap getFinishPlan()
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select status,plan_id from per_plan where status='7'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("plan_id"),"1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getInterviewedObject(UserView view)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = " select object_id,mainbody_id,plan_id from per_interview where mainbody_id='"+view.getA0100()+"' and plan_id in (select plan_id from per_plan where status='7')";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("object_id")+rs.getString("mainbody_id")+rs.getString("plan_id"),"1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
