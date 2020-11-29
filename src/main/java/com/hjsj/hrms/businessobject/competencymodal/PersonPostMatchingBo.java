package com.hjsj.hrms.businessobject.competencymodal;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PersonPostMatchingBo {
	
	private Connection conn;
	private UserView userView;
    public PersonPostMatchingBo(Connection conn,UserView userView)
    {
    	this.conn = conn;
    	this.userView = userView;
    }
    
    public PersonPostMatchingBo()
    {
    	
    }
    public ArrayList getPlanList()
    {
    	ArrayList list = new ArrayList();
    	RowSet rs = null;
    	try
    	{
    		StringBuffer buf = new StringBuffer("");
    		buf.append("select plan_id,name from per_plan ");
    		buf.append(" where method=1 ");
    		buf.append(" and busitype=1" );
    		buf.append(" and status=7 and object_type=2");
    		buf.append(" order by ");
    		buf.append(Sql_switcher.isnull("a0000", "999999"));
    		buf.append(" asc,plan_id desc,create_date ");
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search(buf.toString());
    		while(rs.next())
    		{
    			list.add(new CommonData(rs.getString("plan_id"),rs.getString("plan_id")+"."+rs.getString("name")));
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
    	return list;
    	
    }
    /**
     * 得到考核计划等级分类参数
     * @param plan_id
     * @return
     */
    public String getGradeClass(String plan_id)
    {
    	String gradeClass="";
    	try
    	{
    		LoadXml loadxml=null;
    		if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
    		{
    			loadxml=new LoadXml(this.conn,plan_id);
    			BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
    		}
    		else
    			loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);		
    		Hashtable planParam=loadxml.getDegreeWhole(); 
    		gradeClass = (String)planParam.get("GradeClass");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return gradeClass;
    }
    /**
     * 取得等级分类下的所有等级项目
     * @param gradeClass
     * @return
     */
    public ArrayList getDegreeDetailInfo(String gradeClass,int type)
    {
    	ArrayList degreeList = new ArrayList();
    	RowSet rs = null;
    	try
    	{
    		//String gradeClass=this.getGradeClass(plan_id);
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(" select id,degree_id,itemname,topscore,bottomscore from per_degreedesc where degree_id="+gradeClass+" order by topscore desc");
    		if(type==1)
    			degreeList.add(new CommonData("","全部"));
    		while(rs.next())
    		{
    			if(type==1)
    			{
    				degreeList.add(new CommonData(rs.getString("id"),rs.getString("itemname")+"(以上)"));
    			}else
    			{
    		    	LazyDynaBean bean = new LazyDynaBean();
    	    		bean.set("id",rs.getString("id"));
    		    	bean.set("itemname",rs.getString("itemname"));
    			    bean.set("topscore",rs.getString("topscore")==null?"1000":rs.getString("topscore"));
    	    		bean.set("bottomscore",rs.getString("bottomscore")==null?"-1000":rs.getString("bottomscore"));
    	    		bean.set("degree_id",rs.getString("degree_id"));
    		    	degreeList.add(bean);
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
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return degreeList;
    }
    /**
     * 得到考核等级分类表等级标识
     * @param gradeClass
     * @return
     */
    public String getDegreeFlag(String gradeClass){
    	String degreeflag = "";
    	RowSet rs = null;
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs = dao.search(" select flag from per_degree where degree_id ="+gradeClass);
    		if(rs.next()){
    			degreeflag = rs.getString("flag");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
    			if(rs!=null)
    				rs.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	return degreeflag;
    }
    
    public ArrayList getMatchingList(String object_id,String plan_id,String post_id,String gradeClass,String degreeGradeId,String objType,String objE01A1)
    {
    	ArrayList matchingList = new ArrayList();
    	RowSet rs = null;
    	StringBuffer sql = new StringBuffer("");
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
			RecordVo gradeVo = null;
			if(degreeGradeId!=null&&!"".equals(degreeGradeId))
			{
				gradeVo = new RecordVo("per_degreedesc");
				gradeVo.setInt("id", Integer.parseInt(degreeGradeId));
				gradeVo = dao.findByPrimaryKey(gradeVo);
			}
			String ssql="select domainflag from per_degree where degree_id='"+gradeClass+"'";
			ResultSet res=dao.search(ssql);
			int domainflag=0;
			while(res.next()){//取得匹配度等级分类的封闭标识
				domainflag=res.getInt("domainflag");//0:上限封闭 	1:下限封闭
			}						
			if(objType!=null && objType.trim().length()>0 && "2".equals(objType))
			{	
				PerformanceImplementBo pb = new PerformanceImplementBo(this.conn);	
				
				if(post_id==null || post_id.trim().length()<=0 || "".equals(post_id))//默认，找与考核对象同一岗位序列的岗位，
				{									
					sql.append("select b0110,e0122,e01a1,object_id,a0101,resultdesc from per_result_" + plan_id + " where 1=1 ");	
					sql.append(pb.getPrivWhere(this.userView)); // 根据用户权限先得到一个考核对象的范围
					sql.append(" order by a0000 ");
					
				}else
				{
					//sql.append("select b0110,e0122,e01a1,a0100 object_id,a0101,resultdesc from usra01 where 1=1 ");	
					//haosl 20170417 update resultdesc列需要关联per_result_xxx表
					sql.append("select u.b0110,u.e0122,u.e01a1,u.a0100 object_id,u.a0101,p.resultdesc from usra01 u,per_result_"+plan_id+" p where  u.a0100=p.object_id ");	
					sql.append(pb.getPrivWhere(this.userView)); // 根据用户权限先得到一个考核对象的范围
					if ("root".equalsIgnoreCase(post_id))
						sql.append(" and 1=2 ");
					else if ("UN".equalsIgnoreCase(post_id.substring(0, 2)))
						sql.append(" and u.b0110 like '" + post_id.substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(post_id.substring(0, 2)))
						sql.append(" and u.e0122 like '" + post_id.substring(2) + "%'");
					else if ("@K".equalsIgnoreCase(post_id.substring(0, 2)))
						sql.append(" and u.e01a1 like '" + post_id.substring(2) + "%'");					
					
					sql.append(" order by u.b0110,u.e0122,u.e01a1,u.a0100 ");
					
				}
				rs = dao.search(sql.toString());
				/**当前岗位的指标总分*/
				//String postScore = this.getE01a1TotalScore(objE01A1);
				String postScore = this.getE01a1TotalScore(plan_id,objE01A1);
				/**每个考核对象的考核得分*/
				HashMap objectsMap = this.getPerObjectsScore(plan_id);
				/**考核等级列表*/
				ArrayList gradeList = this.getDegreeDetailInfo(gradeClass,0);
			    double ots= Double.parseDouble(postScore);
			    String bottomvalue=null;
			    if(gradeVo!=null)
				{
					bottomvalue=gradeVo.getString("bottomscore");
					if(bottomvalue==null|| "".equals(bottomvalue))
						bottomvalue="-10000";
				}				
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("b0110",AdminCode.getCodeName("UN", isNull(rs.getString("b0110"))));
					bean.set("e0122",AdminCode.getCodeName("UM", isNull(rs.getString("e0122"))));
					bean.set("e01a1",AdminCode.getCodeName("@K", isNull(rs.getString("e01a1"))));
					bean.set("codeitemid", isNull(rs.getString("object_id")));
					bean.set("a0101", isNull(rs.getString("a0101")));
										
					String score = "";
					String degree="";
					String degreedesc="";
					if(objectsMap.get(rs.getString("object_id"))!=null && !"0".equals((String)objectsMap.get(rs.getString("object_id"))))
					{
						score = (String)objectsMap.get(rs.getString("object_id"));
						double ascore = Double.parseDouble(score);
						if(ots!=0 && ots!=0.0)
							degree = PubFunc.round((ascore/ots)+"",4);
						if(bottomvalue!=null)
						{
							String degreeXi = "0.0000";
							if(degree!=null && degree.trim().length()>0)
								degreeXi = degree;
							BigDecimal dg = new BigDecimal(degreeXi);
							BigDecimal bv = new BigDecimal(bottomvalue);
							if(dg.compareTo(bv)<0)
								continue;
						}
						degreedesc=this.getGradeDesc(degree, gradeList,domainflag);//domainflag 0:上限封闭 	1:下限封闭
					}else
					{
						if(bottomvalue!=null)
							continue;
					}
					if(degree!=null && degree.trim().length()>0)
						degree=PubFunc.round((Double.parseDouble(degree)*100)+"", 2)+"%";
					bean.set("score", PubFunc.round(score, 2));
					bean.set("level", isNull(rs.getString("resultdesc")));
					bean.set("degree", degree);
					bean.set("degreedesc", degreedesc);
					matchingList.add(bean);
				}
				
			}else if(objType!=null && objType.trim().length()>0 && "1".equals(objType))
			{
			
	    		String field="";
	    		RecordVo ps_c_job_vo=ConstantParamter.getRealConstantVo("PS_C_JOB",this.conn);
				if(ps_c_job_vo!=null)
				{
					field=ps_c_job_vo.getString("str_value");
				}			
				
				StringBuffer privSql = new StringBuffer("");
				String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
				sql.append("select codeitemid,codeitemdesc,parentid from organization ");
				privSql.append("select codeitemid from organization");
				sql.append(" where codesetid='@K' ");
				privSql.append(" where codesetid='@K'");
				sql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
				privSql.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
				if(post_id==null || post_id.trim().length()<=0 || "".equals(post_id))//默认，找与考核对象同一岗位序列的岗位，
				{
					if(field==null||"".equals(field)|| "#".equals(field))
					{
						sql.append(" and 1=2 ");
						privSql.append(" and 1=2 ");
					}
					else
					{
						sql.append(" and codeitemid in(select e01a1 from k01 where ");
						privSql.append(" and codeitemid in(select e01a1 from k01 where ");
						String str=this.getObjectPostSeq(plan_id,object_id, field);
						sql.append(field+"='"+str+"')");
						privSql.append(field+"='"+str+"')");
					}
				}else
				{
					sql.append(" and codeitemid like '"+post_id.substring(2)+"%'");
					privSql.append(" and codeitemid like '"+post_id.substring(2)+"%'");
				}
				if(!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId()))
				{
					String privCode = this.userView.getManagePrivCode();
					String privCodeValue = this.userView.getManagePrivCodeValue();
					if(privCode!=null&&!"".equals(privCode))
					{
						privCodeValue=privCodeValue==null?"":privCodeValue;
						sql.append(" and codeitemid like '"+privCodeValue+"%'");
						privSql.append(" and codeitemid like '"+privCodeValue+"%'");
					}
					else
					{
						sql.append(" and 1=2 ");
						privSql.append(" and 1=2 ");
					}
				}
				rs = dao.search(sql.toString());
				/**考核对象得分*/
				String objectTotalScore=this.getObjectScore(plan_id, object_id);
				/**考核对象级别*/
				String objectTotalResultdesc=this.getObjectResultdesc(plan_id, object_id);
				/**每个岗位序列的总分*/
				HashMap map =this.getPostScore(privSql.toString(),plan_id);
				/**考核等级列表*/
				ArrayList gradeList = this.getDegreeDetailInfo(gradeClass,0);
			    double ots= Double.parseDouble(objectTotalScore);
			    String bottomvalue=null;
			    if(gradeVo!=null)
				{
					bottomvalue=gradeVo.getString("bottomscore");
					if(bottomvalue==null|| "".equals(bottomvalue))
						bottomvalue="-10000";
				}
				while(rs.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("postname",AdminCode.getCodeName("UM", rs.getString("parentid"))+"/"+rs.getString("codeitemdesc"));
					bean.set("codeitemid",rs.getString("codeitemid"));
					String degree="";
					String degreedesc="";
					if(map.get(rs.getString("codeitemid"))!=null&&!"0".equals((String)map.get(rs.getString("codeitemid"))))
					{
						String score=(String)map.get(rs.getString("codeitemid"));
						double ascore=Double.parseDouble(score);
						if(ascore!=0 && ascore!=0.0)
							degree=PubFunc.round((ots/ascore)+"",4);
						if(bottomvalue!=null)
						{
							String degreeXi = "0.0000";
							if(degree!=null && degree.trim().length()>0)
								degreeXi = degree;
							BigDecimal dg = new BigDecimal(degreeXi);
							BigDecimal bv = new BigDecimal(bottomvalue);
							if(dg.compareTo(bv)<0)
								continue;
						}
						degreedesc=this.getGradeDesc(degree, gradeList,domainflag);//domainflag 0:上限封闭 	1:下限封闭
						if(degree!=null && degree.trim().length()>0)
							degree=PubFunc.round((Double.parseDouble(degree)*100)+"", 2)+"%";
						bean.set("score", PubFunc.round(score, 2));
						bean.set("level", objectTotalResultdesc);
						bean.set("degree", degree);
						bean.set("degreedesc", degreedesc);
					}else
					{
						if(bottomvalue!=null)
							continue;
						bean.set("score", "");
						bean.set("level", "");
						bean.set("degree", "");
						bean.set("degreedesc", "");
					}
					
					matchingList.add(bean);
				}			
			}			
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		try
    		{
    			if(rs!=null)
    				rs.close();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return matchingList;
    }
    
    public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
    
	/**获得能力素质当前岗位的指标总分*/
    public String getE01a1TotalScore(String objE01A1)
    {
		String postScore = "0.0";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获取当前时间
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select pc.object_id,sum(pc.score*pg.gradevalue*pc.rank) postScore ");
			sql.append(" from per_competency_modal pc,per_grade pg ");
			sql.append(" where pc.object_type = 3 and pc.object_id = '"+ objE01A1 +"' ");
			sql.append(" and pc.point_id=pg.point_id and pc.gradecode=pg.gradecode ");
			sql.append(" and "+Sql_switcher.dateValue(creatDate)+" between start_date and end_date");
			sql.append(" group by pc.object_id ");
						
		    rowSet = dao.search(sql.toString());
		    if (rowSet.next())
		    {		    	
		    	postScore = rowSet.getString("postScore")==null?"0.0":rowSet.getString("postScore");		    	
		    }
		    if(rowSet!=null)
		    	rowSet.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return postScore;
    }
    /**
     * 获得计划的启动时间 或者 计划归档时间
     * @param plan_id
     * @return
     */
    public static  String getPlanStartDate(ContentDAO dao,String plan_id){
		RowSet rowSet = null;
		Date date =null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startDate = "";
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select execute_date from per_plan where plan_id="+plan_id+"");
			rowSet=dao.search(sql.toString());
			if(rowSet.next()){
				date=rowSet.getTimestamp("execute_date");
			}
			
			
			if(date==null){
				sql.setLength(0);
				sql.append(" select distinct archive_date from per_history_result where plan_id="+plan_id+"");
				rowSet=dao.search(sql.toString());
				if(rowSet.next()){
					date=rowSet.getTimestamp("archive_date");
				}
			}
			if(date!=null)
				startDate=sdf.format(date);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return startDate;
    }
    
    
	/**
	 * 获得能力素质当前岗位的指标总分
	 * @param plan_id
	 * @param objE01A1
	 * @return
	 */
    public String getE01a1TotalScore(String plan_id,String objE01A1)
    {
		String postScore = "0.0";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"); // 获取当前时间
		try
		{
			String startDate=getPlanStartDate(dao,plan_id);
			if(!"".equals(startDate)){
				creatDate=startDate;
			}
			StringBuffer sql = new StringBuffer("");
			sql.append("select pc.object_id,sum(pc.score*pg.gradevalue*pc.rank) postScore ");
			sql.append(" from per_competency_modal pc,per_grade pg ");
			sql.append(" where pc.object_type = 3 and pc.object_id = '"+ objE01A1 +"' ");
			sql.append(" and pc.point_id=pg.point_id and pc.gradecode=pg.gradecode ");
			sql.append(" and "+Sql_switcher.dateValue(creatDate)+" between start_date and end_date");
			sql.append(" group by pc.object_id ");
						
		    rowSet = dao.search(sql.toString());
		    if (rowSet.next())
		    {		    	
		    	postScore = rowSet.getString("postScore")==null?"0.0":rowSet.getString("postScore");		    	
		    }
		    if(rowSet!=null)
		    	rowSet.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return postScore;
    }
    /**每个考核对象的考核得分*/
    public HashMap getPerObjectsScore(String plan_id)
    {
		HashMap map = new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			StringBuffer sql = new StringBuffer("");
			sql.append("select object_id,score from per_result_" + plan_id + " ");	
			sql.append(" order by a0000 ");								
		    rowSet = dao.search(sql.toString());
		    while(rowSet.next())
		    {		    	
		    	String object_id = rowSet.getString("object_id")==null?"object_id":rowSet.getString("object_id");
		    	String score = rowSet.getString("score")==null?"0.0":rowSet.getString("score");
		    	
		        map.put(object_id,score);
		    }
		    if(rowSet!=null)
		    	rowSet.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return map;
    }
    /**
     * 取得匹配度等级分类
     * @param value
     * @param gradeList
     * @param domainflag 0:上限封闭 	1:下限封闭
     * @return
     */
    public String getGradeDesc(String value,ArrayList gradeList,int domainflag )
    {
    	String str="";
    	String degreeXi = "0.0000";
    	if(value!=null && value.trim().length()>0)
    		degreeXi = value;
    	BigDecimal vb = new BigDecimal(degreeXi);
    	for(int i=0;i<gradeList.size();i++)
    	{
    		LazyDynaBean bean = (LazyDynaBean)gradeList.get(i);
    		String bottomscore=(String)bean.get("bottomscore");
    		BigDecimal bs = new BigDecimal(bottomscore);
    		//String topscore = (String)bean.get("topscore");
    		//BigDecimal ts = new BigDecimal(topscore);
    		String itemname=(String)bean.get("itemname");
    		if(domainflag==1){
        		if(vb.compareTo(bs)>=0)
        		{
        			str=itemname;
        			break;
        		}
    		}else if(domainflag==0){
        		if(vb.compareTo(bs)>0)
        		{
        			str=itemname;
        			break;
        		}
    			if(i==gradeList.size()-1){
    				str=itemname;
    			}
    				
    		}

    	}
    	return str;
    }
    public String getGradeDesc(String value,ArrayList gradeList)
    {
    	String str="";
    	String degreeXi = "0.0000";
    	if(value!=null && value.trim().length()>0)
    		degreeXi = value;
    	BigDecimal vb = new BigDecimal(degreeXi);
    	for(int i=0;i<gradeList.size();i++)
    	{
    		LazyDynaBean bean = (LazyDynaBean)gradeList.get(i);
    		String bottomscore=(String)bean.get("bottomscore");
    		BigDecimal bs = new BigDecimal(bottomscore);
    		//String topscore = (String)bean.get("topscore");
    		//BigDecimal ts = new BigDecimal(topscore);
    		String itemname=(String)bean.get("itemname");
    		if(vb.compareTo(bs)>=0)
    		{
    			str=itemname;
    			break;
    		}
    	}
    	return str;
    }
    /**
     * 取得匹配度等级分类下拉列表
     */
    public ArrayList getAllGeadeClass()
    {
       ArrayList list = new ArrayList();
       RowSet rs = null;
       try
       {
    	   ContentDAO dao = new ContentDAO(this.conn);
    	   rs = dao.search("select degree_id,degreename from per_degree where flag=4 and used=1 order by degree_id");
    	   while(rs.next())
    	   {
    		   CommonData cd = new CommonData(rs.getString("degree_id"),rs.getString("degreename"));
    		   list.add(cd);
    	   }
       }
       catch(Exception e)
       {
    	   e.printStackTrace();
       }finally{
    	   try{
    		   if(rs!=null)
    			   rs.close();
    	   }catch(Exception e)
    	   {
    		   e.printStackTrace();
    	   }
       }
       return list;
    }
    /**
     * 取得考核对象所在岗位所属的岗位序列
     * @param object_id
     * @param field
     * @return
     */
    public String getObjectPostSeq(String object_id,String field)
    {
    	String str="";
    	RowSet rs = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search("select "+field+" from k01 where e01a1=(select e01a1 from usra01 where a0100='"+object_id+"')");
    		while(rs.next())
    		{
    			str=rs.getString(1);
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
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return str;
    }
    
    /**
     * 取得考核对象所在岗位所属的岗位序列  兼容历史数据分析
     * @param object_id
     * @param field
     * @return
     */
    public String getObjectPostSeq(String plan_id,String object_id,String field)
    {
    	String str="";
    	RowSet rs = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search("select "+field+" from k01 where e01a1=(select e01a1 from per_result_"+plan_id+" where object_id='"+object_id+"')");
    		while(rs.next())
    		{
    			str=rs.getString(1);
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
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return str;
    }
    /**
     * 取得考核对象考核结果分数
     * @param plan_id
     * @param object_id
     * @return
     */
    public String getObjectScore(String plan_id,String object_id)
    {
    	String score="";
    	RowSet rs = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search("select score from per_result_"+plan_id+" where object_id='"+object_id+"'");
    		while(rs.next())
    		{
    			score=rs.getString(1)==null?"0":rs.getString(1);
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
    	return score;
    }
    
    /**
     * 取得考核对象考核结果级别
     * @param plan_id
     * @param object_id
     * @return
     */
    public String getObjectResultdesc(String plan_id,String object_id)
    {
    	String resultdesc="";
    	RowSet rs = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search("select resultdesc from per_result_"+plan_id+" where object_id='"+object_id+"'");
    		while(rs.next())
    		{
    			resultdesc=rs.getString(1)==null?"0":rs.getString(1);
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
    	return resultdesc;
    }
    
    /**
     * 取得每个岗位序列的总分
     * @return
     */
    public HashMap getPostScore(String privSql,String plan_id)
    {
    	HashMap map = new HashMap();
    	RowSet rs = null;
    	try
    	{
    		RecordVo vo = new RecordVo("per_plan");
    		vo.setInt("plan_id",Integer.parseInt(plan_id));
    		ContentDAO dao = new ContentDAO(this.conn);
    		vo = dao.findByPrimaryKey(vo);
    		RecordVo vo2=new RecordVo("per_template");
    		vo2.setString("template_id",vo.getString("template_id"));
			vo2=dao.findByPrimaryKey(vo2);
			String status=vo2.getString("status");
    		StringBuffer buf = new StringBuffer("");
    		buf.append("select SUM(");
    		buf.append(Sql_switcher.isnull("a.score","0"));
    	//	if(status.equals("0"))
    		{
    	    	buf.append("*");
    	    	buf.append(Sql_switcher.isnull("a.rank", "0"));
    		}
    		buf.append("*");
    		buf.append(Sql_switcher.isnull("b.gradevalue", "0"));
    		buf.append(")");
    		buf.append(" as totalscore,a.object_id ");
    		buf.append(" from per_competency_modal a ");
    		buf.append(" left join per_grade b on a.point_id=b.point_id and a.gradecode=b.gradecode ");
    		buf.append(" where a.object_type=3 ");
    		buf.append(" and a.object_id in("+privSql+")");
    		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd HH:mm:ss"); // 获取当前时间
			String startDate=getPlanStartDate(dao,plan_id);
			if(!"".equals(startDate)){
				bosdate=startDate;
			}
    		buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between a.start_date and a.end_date ");
    		buf.append(" group by a.object_id ");
    		
    		rs = dao.search(buf.toString());
    		while(rs.next())
    		{
    			String object_id= rs.getString("object_id");
    			String totalscore = rs.getString("totalscore")==null?"0":rs.getString("totalscore");
    			map.put(object_id, totalscore);
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
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return map;
    }
    /**
     * 人岗 岗人匹配分析数据
     * @param object_id
     * @param post_id
     * @param plan_id
     * @return
     */
    public HashMap getDataMap(String object_id,String post_id,String plan_id,String isShowPercentVal)
    {
    	HashMap map = new HashMap();
    	RowSet rs = null;
    	try{
    		ContentDAO dao = new ContentDAO(this.conn);
    		 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 //liuy 2015-12-04
			 int maxLevel = 0;
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 maxLevel = perGradeCompetenceList.size();
			 //HashMap map1 = new HashMap();//所有指标的总分
			 //liuy 2015-12-04
			 RecordVo vo = new RecordVo("per_plan");
	    	 vo.setInt("plan_id",Integer.parseInt(plan_id));
	    	 vo = dao.findByPrimaryKey(vo);
	    	 RecordVo vo2=new RecordVo("per_template");
	    	 vo2.setString("template_id",vo.getString("template_id"));
			 vo2=dao.findByPrimaryKey(vo2);
			 String status=vo2.getString("status");
    		StringBuffer buf = new StringBuffer("");
    		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd HH:mm:ss");
			String startDate=getPlanStartDate(dao,plan_id);
			if(!"".equals(startDate)){
				bosdate=startDate;
			}
    		buf.append("select ");
    		buf.append(Sql_switcher.isnull("a.score","0"));
    		if("0".equals(status))
	    	{
	    	    buf.append("*");
	    	    buf.append(Sql_switcher.isnull("a.rank", "0"));
	    	}
    		buf.append("*");
    		buf.append(Sql_switcher.isnull("b.gradevalue", "0"));
    		buf.append(" as totalscore,a.object_id,a.point_id,c.pointname");
    		buf.append(" from per_competency_modal a ");
    		buf.append(" left join per_grade b on a.point_id=b.point_id and a.gradecode=b.gradecode ");
    		buf.append(" left join per_point c on a.point_id=c.point_id");
    		buf.append(" where a.object_type=3 ");
    		buf.append(" and a.object_id='"+post_id+"'");
    		buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date order by a.point_id");
    		rs = dao.search(buf.toString());
    		ArrayList postList = new ArrayList();
    		int i=0;
    		while(rs.next())
    		{
    			if(i==0)
    			{
    				minRadarScore=Float.parseFloat(rs.getString("totalscore"));
    				maxRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			}
    			if(Float.parseFloat(rs.getString("totalscore"))<minRadarScore)
    				minRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			if(Float.parseFloat(rs.getString("totalscore"))>maxRadarScore)
    				maxRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			//liuy 2015-12-04
    			int level = 0;
				if("2".equals(isShowPercentVal)){
					float tempScore = Float.parseFloat(rs.getString("totalscore"));
					float gradevalue = getGradeValue(rs.getString("point_id"), post_id);
					float theScore = 0f;
					if(gradevalue!=0){
						theScore = tempScore / gradevalue;
					}
					//map1.put(rs.getString("point_id").toLowerCase(), new Float(theScore));
					level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(plan_id));
					postList.add(new CommonData(level+"",this.warpRowStr(rs.getString("pointname"))));
				}else 
					postList.add(new CommonData(rs.getString("totalscore"),this.warpRowStr(rs.getString("pointname"))));
    			//liuy 2015-12-04
    			i++;
    		}
    		String e01a1 = AdminCode.getCodeName("@K",post_id);
    		map.put(e01a1, postList);
    		String sql="select po.point_id,po.pointname from per_template_item pi,per_template_point pp,per_point po,per_plan "
			 	   +" where pi.item_id=pp.item_id and pp.point_id=po.point_id  and pi.template_id=per_plan.template_id and per_plan.plan_id="+plan_id+" order by pi.seq,pp.seq";
    		if(rs!=null)
    			rs.close();
    		buf.setLength(0);
    		buf.append(" select a0101");
    		LinkedHashMap pointNameMap = new LinkedHashMap();
    		rs = dao.search(sql);
    		while(rs.next())
    		{
    			buf.append(",C_"+rs.getString("point_id"));
    			pointNameMap.put(rs.getString("point_id"),rs.getString("pointname"));
    		}
    		if(rs!=null)
    			rs.close();
    		rs = dao.search(buf.toString()+" from per_result_"+plan_id+" where object_id='"+object_id+"'");
    		Set keySet = pointNameMap.keySet();
    		ArrayList objectList = new ArrayList();
    		while(rs.next())
    		{
    			int j=0;
    			for(Iterator it = keySet.iterator();it.hasNext();)
    			{
    				String key=(String)it.next();
    				String score = rs.getString("C_"+key)==null?"0":rs.getString("C_"+key);
    				String pointname=(String)pointNameMap.get(key);
    				pointname=this.warpRowStr(pointname);
    				//liuy 2015-12-04
    				int level = 0;
    				//Float theScore = (Float)map1.get(key.toLowerCase());
    				if("2".equals(isShowPercentVal)){
    					float tempScore = Float.parseFloat(score);
    					float gradevalue = getGradeValue(key.toLowerCase(), post_id);
    					float theScore = tempScore / gradevalue; 
						level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(plan_id));
						objectList.add(new CommonData(level+"",pointname));
					}else 
						objectList.add(new CommonData(score,pointname));
        			//liuy 2015-12-04
        			if(Float.parseFloat(score)<minRadarScore)
        				minRadarScore=Float.parseFloat(score);
        			if(Float.parseFloat(score)>maxRadarScore)
        				maxRadarScore=Float.parseFloat(score);
        			j++;
    			}
    			map.put(rs.getString("a0101"),objectList);
    		}
    	//	map.put("minmax",minRadarScore+","+maxRadarScore);
    		//liuy 2015-12-04
    		if("2".equals(isShowPercentVal))
    			map.put("minmax",0+","+maxLevel);
    		else
    			map.put("minmax",0+","+maxRadarScore);
    		//liuy 2015-12-04
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
    /**
     * 按岗位素质模型测评 获得人岗datamap
     * @param object_id
     * @param post_id
     * @param plan_id
     * @return
     */
    public HashMap getDataMapByModel1(String object_id,String post_id,String plan_id,String isShowPercentVal)
    {
    	HashMap map = new HashMap();
    	RowSet rs = null;
    	try{
    		 ContentDAO dao = new ContentDAO(this.conn);
    		 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 //liuy 2015-12-04
			 int maxLevel = 0;
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 maxLevel = perGradeCompetenceList.size();
			 HashMap map2 = new HashMap();//所有指标的总分
			 //liuy 2015-12-04
			 RecordVo vo = new RecordVo("per_plan");
	    	 vo.setInt("plan_id",Integer.parseInt(plan_id));
	    	 vo = dao.findByPrimaryKey(vo);
	    	String ssql="select per_point.point_id from per_history_result , per_point   where plan_id='"+plan_id+"' and object_id='"+object_id+"' and per_history_result.status='0' and per_history_result.point_id=per_point.point_id ";
	    	ArrayList orderPostList=new ArrayList();//排序用
	    	rs=dao.search(ssql);
	    	HashMap map1=new HashMap();
	    	///取得要展现的指标及顺序
	    	while(rs.next()){
    			String point_id=rs.getString("point_id")==null?"":rs.getString("point_id");
    			point_id=point_id.toLowerCase();
	    		orderPostList.add(point_id);
	    		map1.put(point_id,"1");
	    	}
    		StringBuffer buf = new StringBuffer("");
    		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
    		buf.append("select ");
    		buf.append(Sql_switcher.isnull("a.score","0"));
    		buf.append("*");
    		buf.append(Sql_switcher.isnull("b.gradevalue", "0"));
    		buf.append(" as totalscore,a.object_id,a.point_id,c.pointname");
    		buf.append(" from per_competency_modal a ");
    		buf.append(" left join per_grade b on a.point_id=b.point_id and a.gradecode=b.gradecode ");
    		buf.append(" left join per_point c on a.point_id=c.point_id");
    		buf.append(" where a.object_type=3 ");
    		buf.append(" and a.object_id='"+post_id+"'");
    		buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
    		rs = dao.search(buf.toString());
    		ArrayList postList = new ArrayList();
    		int i=0;
    		HashMap mapOrder=new HashMap();
    		while(rs.next())
    		{	
    			String point_id=rs.getString("point_id")==null?"":rs.getString("point_id");
    			point_id=point_id.toLowerCase();
    			if(map1.get(point_id)==null){
    				continue;
    			};
    			if(i==0)
    			{
    				minRadarScore=Float.parseFloat(rs.getString("totalscore"));
    				maxRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			}
    			if(Float.parseFloat(rs.getString("totalscore"))<minRadarScore)
    				minRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			if(Float.parseFloat(rs.getString("totalscore"))>maxRadarScore)
    				maxRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			//liuy 2015-12-04
    			int level = 0;
				if("2".equals(isShowPercentVal)){
					float tempScore = Float.parseFloat(rs.getString("totalscore"));
					float gradevalue = getGradeValue(rs.getString("point_id"), post_id);
					float theScore = 0f;
					if(gradevalue!=0){
						theScore = tempScore / gradevalue;
					}
					map2.put(rs.getString("point_id").toLowerCase(), new Float(theScore));
					level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(plan_id));
					mapOrder.put(rs.getString("point_id").toLowerCase(), new CommonData(level+"",this.warpRowStr(rs.getString("pointname"))));
				}else 
    				mapOrder.put(rs.getString("point_id").toLowerCase(), new CommonData(rs.getString("totalscore"),this.warpRowStr(rs.getString("pointname"))));
    			//liuy 2015-12-04
    			i++;
    		}
    		for(int a=0;a<orderPostList.size();a++){
    			if(mapOrder.get(orderPostList.get(a))!=null){
    				postList.add(mapOrder.get(orderPostList.get(a)));
    			}
    		}
    		String e01a1 = AdminCode.getCodeName("@K",post_id);
    		map.put(e01a1, postList);
    		String sql=" select per_history_result.score ,per_point.point_id,per_point.pointname, per_history_result.a0101 from per_history_result , per_point   where plan_id='"+plan_id+"' and object_id='"+object_id+"' and per_history_result.status='0' and per_history_result.point_id=per_point.point_id";
    		if(rs!=null)
    			rs.close();
    
    		rs = dao.search(sql);
    		ArrayList objectList = new ArrayList();
    		String a0101="";
    		mapOrder=new HashMap();
    		while(rs.next())
    		{
    				String score = rs.getString("score");
        			String point_id=rs.getString("point_id")==null?"":rs.getString("point_id");
        			point_id=point_id.toLowerCase();
    				String pointname=rs.getString("pointname");
    				pointname=this.warpRowStr(pointname);
    				a0101=rs.getString("a0101");
        			if(map1.get(point_id)==null){
        				continue;
        			};
        			//liuy 2015-12-04
        			int level = 0;
    				Float theScore = (Float)map2.get(point_id);
    				if("2".equals(isShowPercentVal)){
						float tempScore = Float.parseFloat(score);
						level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(plan_id));
						mapOrder.put(point_id, new CommonData(level+"",pointname));
					}else
						mapOrder.put(point_id, new CommonData(score,pointname));
        			//liuy 2015-12-04
        			if(Float.parseFloat(score)<minRadarScore)
        				minRadarScore=Float.parseFloat(score);
        			if(Float.parseFloat(score)>maxRadarScore)
        				maxRadarScore=Float.parseFloat(score);
    		}
    		for(int a=0;a<orderPostList.size();a++){
    			if(mapOrder.get(orderPostList.get(a))!=null){
    				objectList.add(mapOrder.get(orderPostList.get(a)));
    			}
    		}
    			map.put(a0101,objectList);
    	//	map.put("minmax",minRadarScore+","+maxRadarScore);
			//liuy 2015-12-04
			if("2".equals(isShowPercentVal))
    			map.put("minmax",0+","+maxLevel);
    		else
    			map.put("minmax",0+","+maxRadarScore);
    		//liuy 2015-12-04
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
    /**
     * 按岗位素质模型测评 获得岗人datamap
     * @param object_id
     * @param post_id
     * @param plan_id
     * @return
     */
    public HashMap getDataMapByModel2(String object_id,String post_id,String plan_id,String isShowPercentVal)
    {
    	HashMap map = new HashMap();
    	RowSet rs = null;
    	try{
    		 ContentDAO dao = new ContentDAO(this.conn);
    		 float minRadarScore = 0;  //  所有线的最低值
			 float maxRadarScore = 0;  //  所有线的最高值
			 //liuy 2015-12-04
			 int maxLevel = 0;
			 ArrayList perGradeCompetenceList = getPerGradeCompetenceList();
			 maxLevel = perGradeCompetenceList.size();
			 HashMap map2 = new HashMap();//所有指标的总分
			 //liuy 2015-12-04
			 RecordVo vo = new RecordVo("per_plan");
	    	 vo.setInt("plan_id",Integer.parseInt(plan_id));
	    	 vo = dao.findByPrimaryKey(vo);
	    	String ssql="select per_point.point_id from per_history_result , per_point   where plan_id='"+plan_id+"' and object_id='"+object_id+"' and per_history_result.status='0' and per_history_result.point_id=per_point.point_id ";


	    	///取得要展现的指标及顺序
	    	
	    	HashMap map1=new HashMap();
	    	ArrayList orderPostList=new ArrayList();//排序用
    		StringBuffer buf = new StringBuffer("");
    		String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
    		buf.append("select ");
    		buf.append(Sql_switcher.isnull("a.score","0"));
    		buf.append("*");
    		buf.append(Sql_switcher.isnull("b.gradevalue", "0"));
    		buf.append(" as totalscore,a.object_id,a.point_id,c.pointname");
    		buf.append(" from per_competency_modal a ");
    		buf.append(" left join per_grade b on a.point_id=b.point_id and a.gradecode=b.gradecode ");
    		buf.append(" left join per_point c on a.point_id=c.point_id");
    		buf.append(" where a.object_type=3 ");
    		buf.append(" and a.object_id='"+post_id+"'");
    		buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
    		rs = dao.search(buf.toString());
    		ArrayList postList = new ArrayList();
    		int i=0;
    		HashMap mapOrder=new HashMap();
    		while(rs.next())
    		{	
    			String point_id=rs.getString("point_id")==null?"":rs.getString("point_id");
    			point_id=point_id.toLowerCase();
	    		map1.put(point_id,"1");
	    		orderPostList.add(point_id);
    			if(i==0)
    			{
    				minRadarScore=Float.parseFloat(rs.getString("totalscore"));
    				maxRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			}
    			if(Float.parseFloat(rs.getString("totalscore"))<minRadarScore)
    				minRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			if(Float.parseFloat(rs.getString("totalscore"))>maxRadarScore)
    				maxRadarScore=Float.parseFloat(rs.getString("totalscore"));
    			//liuy 2015-12-04
    			int level = 0;
				if("2".equals(isShowPercentVal)){
					float tempScore = Float.parseFloat(rs.getString("totalscore"));
					float gradevalue = getGradeValue(rs.getString("point_id"), post_id);
					float theScore = 0f;
					if(gradevalue!=0){
						theScore = tempScore / gradevalue;
					}
					map2.put(rs.getString("point_id").toLowerCase(), new Float(theScore));
					level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(plan_id));
					mapOrder.put(rs.getString("point_id").toLowerCase(), new CommonData(level+"",this.warpRowStr(rs.getString("pointname"))));
				}else {
					mapOrder.put(rs.getString("point_id").toLowerCase(), new CommonData(rs.getString("totalscore"), this.warpRowStr(rs.getString("pointname"))));
				}
    			//liuy 2015-12-04
    			i++;
    		}
    		
	    	rs=dao.search(ssql);
	    	while(rs.next()){
    			String point_id=rs.getString("point_id")==null?"":rs.getString("point_id");
    			point_id=point_id.toLowerCase();
	    	}
	    	
    		for(int a=0;a<orderPostList.size();a++){
    			if(mapOrder.get(orderPostList.get(a))!=null){
    				postList.add(mapOrder.get(orderPostList.get(a)));
    			}
    		}
    		String e01a1 = AdminCode.getCodeName("@K",post_id);
    		map.put(e01a1, postList);
    		String sql=" select per_history_result.score ,per_point.point_id,per_point.pointname, per_history_result.a0101 from per_history_result , per_point   where plan_id='"+plan_id+"' and object_id='"+object_id+"' and per_history_result.status='0' and per_history_result.point_id=per_point.point_id";
    		if(rs!=null)
    			rs.close();
    
    		rs = dao.search(sql);
    		ArrayList objectList = new ArrayList();
    		String a0101="";
    		mapOrder=new HashMap();
    		while(rs.next())
    		{
    				String score = rs.getString("score");
        			String point_id=rs.getString("point_id")==null?"":rs.getString("point_id");
        			point_id=point_id.toLowerCase();
    				String pointname=rs.getString("pointname");
    				pointname=this.warpRowStr(pointname);
    				a0101=rs.getString("a0101");
        			if(map1.get(point_id)==null){
        				continue;
        			};
        			//liuy 2015-12-04
        			int level = 0;
    				Float theScore = (Float)map2.get(point_id);
    				if("2".equals(isShowPercentVal)){
						float tempScore = Float.parseFloat(score);
						level = getLevel(tempScore, theScore, perGradeCompetenceList, getDomainFlag(plan_id));
						mapOrder.put(point_id, new CommonData(level+"",pointname));
					}else 
						mapOrder.put(point_id, new CommonData(score,pointname));
        			//liuy 2015-12-04
        			if(Float.parseFloat(score)<minRadarScore)
        				minRadarScore=Float.parseFloat(score);
        			if(Float.parseFloat(score)>maxRadarScore)
        				maxRadarScore=Float.parseFloat(score);
    		}
    		for(int a=0;a<orderPostList.size();a++){
    			if(mapOrder.get(orderPostList.get(a))!=null){
    				objectList.add(mapOrder.get(orderPostList.get(a)));
    			}
    		}
    			map.put(a0101,objectList);
    	//	map.put("minmax",minRadarScore+","+maxRadarScore);
			//liuy 2015-12-04
			if("2".equals(isShowPercentVal))
    			map.put("minmax",0+","+maxLevel);
    		else
    			map.put("minmax",0+","+maxRadarScore);
    		//liuy 2015-12-04
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
    public String warpRowStr(String name)
	{
    	if(name.indexOf(":")!=-1)
    	{
    		name=name.substring(0,name.indexOf(":"));
    	}else if(name.indexOf("：")!=-1)
    	{
    		name=name.substring(0,name.indexOf("："));
    	}
		if(name.length()>15)
		{
			int div = name.length()/15;
			String temp = "";
			for(int index=0;index<div;index++)
			{
				temp+=name.substring(index*15, (index+1)*15)+"\r";
			}
			temp+=name.substring(div*15);
			name=temp;
		}
		return name;
	}
    public String getE01a1(String object_id)
    {
    	String e01a1="";
    	RowSet rs = null;
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		rs=dao.search("select e01a1 from usrA01 where a0100='"+object_id+"'");
    		while(rs.next())
    		{
    			e01a1=rs.getString(1);
    		}
    	}catch(Exception e)
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
    	return e01a1;
    }
    
    /**
     * 得到标准模板所有级别
     * @author liuy
     * @return
     */
    private ArrayList getPerGradeCompetenceList(){
		String startLevel = SystemConfig.getPropertyValue("startLevel");
		ArrayList perGradeCompetenceList=new ArrayList();
		String sql = "select grade_template_id,top_value,bottom_value from per_grade_competence";
		if(StringUtils.isNotEmpty(startLevel))
			sql += " where gradevalue >= (select gradevalue from per_grade_competence where grade_template_id = '"+ startLevel +"') order by gradevalue";
		else
			sql += " order by gradevalue";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("grade_template_id", rs.getString("grade_template_id"));
				abean.set("top_value",rs.getString("top_value"));
				abean.set("bottom_value",rs.getString("bottom_value"));
				perGradeCompetenceList.add(abean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null)
				PubFunc.closeResource(rs);
		}
		return perGradeCompetenceList;
	}
	
    /**
     * 得到标准模板比例
     * @param point_id
     * @param object_id
     * @author liuy
     * @return
     */
    private float getGradeValue(String point_id, String object_id){
    	float gradevalue = 0;
    	String sql = "select gradevalue from per_grade where point_id='"+ point_id +"' and gradecode = ";
    	sql += "(select gradecode from per_competency_modal where point_id='"+ point_id +"' and object_id='"+ object_id +"')";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs =null;
		try {
			rs = dao.search(sql);
			if(rs.next()){
				gradevalue = rs.getFloat("gradevalue");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null)
				PubFunc.closeResource(rs);
		}
    	
    	return gradevalue;
    }
    
    /**
     * 得到当前指标级别
     * @param tempScore 当前分数
     * @param theScore 指标总分
     * @param perGradeCompetenceList 指标等级集合
     * @param domainflag 0:上限封闭 	1:下限封闭
     * @author liuy
     * @return
     */
	private int getLevel(float tempScore, float theScore, ArrayList perGradeCompetenceList, int domainflag){
		int level = 0;
		for(int k = 0;k < perGradeCompetenceList.size();k++){
			LazyDynaBean abean = (LazyDynaBean)perGradeCompetenceList.get(k);
			String grade_template_id = (String)abean.get("grade_template_id");
			float top_value = Float.parseFloat(abean.get("top_value").toString());
			float bottom_value = Float.parseFloat(abean.get("bottom_value").toString());
			if(domainflag==0){//上限封闭				
				if(k==0&&tempScore < bottom_value*theScore)
					level = 0;
				else if(k==perGradeCompetenceList.size()-1&&tempScore >= top_value*theScore)
					level = perGradeCompetenceList.size();
				else if(tempScore <= top_value*theScore && tempScore > bottom_value*theScore){
					level = k+1;
					break;
				}
			}else if(domainflag==1){//下限封闭
				if(k==0&&tempScore < bottom_value*theScore)
					level = 0;
				else if(k==perGradeCompetenceList.size()-1&&tempScore >= top_value*theScore)
					level = perGradeCompetenceList.size();
				else if(tempScore < top_value*theScore && tempScore >= bottom_value*theScore){
					level = k+1;
					break;
				}
			}
		}
		return level;
	}
	
	/**
	 * 得到等级分类的封闭标识
	 * @param plan_id
	 * @return
	 */
	private int getDomainFlag(String plan_id){
		int domainflag=0;
		ContentDAO dao = new ContentDAO(this.conn);
		String ssql="select domainflag from per_degree where degree_id='"+getGradeClass(plan_id)+"'";
		ResultSet res = null;
		try {
			res = dao.search(ssql);
			if(res.next()){//取得匹配度等级分类的封闭标识
				domainflag=res.getInt("domainflag");//0:上限封闭 	1:下限封闭
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(res!=null)
				PubFunc.closeResource(res);
		}
		return domainflag;
	}
}
