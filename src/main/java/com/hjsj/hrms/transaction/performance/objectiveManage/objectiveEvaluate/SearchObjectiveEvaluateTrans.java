package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveEvaluate;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectiveEvaluateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SearchObjectiveEvaluateTrans extends IBusiness{

	
	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			//非在职人员不允许使用改功能
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
			}
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String year=/*Calendar.getInstance().get(Calendar.YEAR)+*/"-1";
			String status="-2";
			String quarter="-1";
			String month="-1";
			String planid="-1";
			String isSort="1";
			String isOrder="0";
			String posID = this.userView.getUserPosId();
			ArrayList dbnameList = new ArrayList();
			dbnameList.add("USR");
			String entranceType=(String)hm.get("entranceType");
			String opt=(String)hm.get("opt");
			
			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag",returnflag);
			
			//把pendingCode存到from中  2013.12.28 pjf
			String pendingCode=(String)hm.get("pendingCode");
			this.getFormHM().put("pendingCode",pendingCode);
			hm.remove("pendingCode");
			
			
			
			
			if("1".equals(opt))
			{
				
				if(returnflag!=null&&returnflag.trim().length()>0)
				{
					hm.remove("returnflag");
					hm.remove("isOrder");
					if("menu".equalsIgnoreCase(returnflag))
						planid="-1";
				}
				
				//从首页我的任务进入，需自动匹配计划的考核期间
				if(returnflag!=null&&("8".equals(returnflag)|| "10".equals(returnflag)))
				{
					String plan_id=(String)hm.get("plan_id");
					//planid="-1";
					planid=plan_id;
					if(plan_id!=null)
					{
						RecordVo plan_vo=new RecordVo("per_plan");
						plan_vo.setInt("plan_id",Integer.parseInt(plan_id));
						plan_vo=dao.findByPrimaryKey(plan_vo);
						int cycle=plan_vo.getInt("cycle"); //(0|1|2|3|7)=(年度|半年|季度|月度|不定期)
						if(cycle!=7)
						{
							String theyear=plan_vo.getString("theyear");
							if(cycle==0||cycle==1)
							{
								
							}
							else if(cycle==2) //季度
							{
								//haosl 此处select下拉数据中的值当为1-9的时候前面是有0的，这里得到的值前面没有0，所以为了匹配下拉项的值，前面要加0
								String thequarter=plan_vo.getString("thequarter");
								quarter=thequarter.length()<2?"0"+thequarter:thequarter;
							}
							else if(cycle==3)
							{
								//haosl 此处select下拉数据中的值当为1-9的时候前面是有0的，这里得到的值前面没有0，所以为了匹配下拉项的值，前面要加0
								String themonth=plan_vo.getString("themonth");
								month=themonth.length()<2?"0"+themonth:themonth;
							}
							year=theyear;
							status="-2";
							opt="2";
						}
					}
				}
			}
			else if("2".equals(opt))
			{
				year = (String)this.getFormHM().get("year");
				status = (String)this.getFormHM().get("status");
				quarter = (String)this.getFormHM().get("quarter");
				month=(String)this.getFormHM().get("month");
			}else if("3".equals(opt))
			{
				year = (String)hm.get("year");
				status = (String)hm.get("status");
				quarter = (String)hm.get("quarter");
				month=(String)hm.get("month");
			}
			else if("4".equals(opt))
			{
				planid=(String)hm.get("planid");

				if(returnflag!=null&&("8".equals(returnflag)|| "10".equals(returnflag)))
				{
					String plan_id=(String)hm.get("planid");
					//planid="-1";
					planid=plan_id;
					if(plan_id!=null)
					{
						RecordVo plan_vo=new RecordVo("per_plan");
						plan_vo.setInt("plan_id",Integer.parseInt(plan_id));
						plan_vo=dao.findByPrimaryKey(plan_vo);
						int cycle=plan_vo.getInt("cycle"); //(0|1|2|3|7)=(年度|半年|季度|月度|不定期)
						if(cycle!=7)
						{
							String theyear=plan_vo.getString("theyear");
							if(cycle==0||cycle==1)
							{

							}
							else if(cycle==2) //季度
							{
								//haosl 此处select下拉数据中的值当为1-9的时候前面是有0的，这里得到的值前面没有0，所以为了匹配下拉项的值，前面要加0
								String thequarter=plan_vo.getString("thequarter");
								quarter=thequarter.length()<2?"0"+thequarter:thequarter;
							}
							else if(cycle==3)
							{
								//haosl 此处select下拉数据中的值当为1-9的时候前面是有0的，这里得到的值前面没有0，所以为了匹配下拉项的值，前面要加0
								String themonth=plan_vo.getString("themonth");
								month=themonth.length()<2?"0"+themonth:themonth;
							}
							year=theyear;
							status="-2";
							opt="2";
						}
					}
				}
			}
			/*if(hm.get("isSort")!=null)
			{
				isSort=(String)hm.get("isSort");
				if(hm.get("planid")!=null)
			    	planid=(String)hm.get("planid");
			}*/
			if(hm.get("isOrder")!=null)
			{
				isOrder=(String)hm.get("isOrder");
		 		if(hm.get("planid")!=null&&!(returnflag!=null&&("8".equals(returnflag)|| "10".equals(returnflag))))
		    		planid=(String)hm.get("planid");
			}
			ObjectiveEvaluateBo bo = new ObjectiveEvaluateBo(this.getFrameconn(),this.getUserView());
			HashMap map = bo.getYearAndPersonList3(year, quarter, month, status, this.userView,planid,isSort,isOrder);
			//ArrayList personList = (ArrayList)map.get("1");
			ArrayList personList = (ArrayList)map.get("3");
			//田野添加开始
			String clientName = SystemConfig.getPropertyValue("clientName");
			if(clientName!= null && "gwyjy".equalsIgnoreCase(clientName)){
				this.getFormHM().put("showAccumulativRank", "true");
				HashMap planMap = (HashMap)map.get("5");
				personList = getObjectAccumulativeRank(personList,planMap);
			}else{
				this.getFormHM().put("showAccumulativRank", "false");
			}
			String showWholeEvaluate = (String)map.get("4");
			this.getFormHM().put("showWholeEvaluate", showWholeEvaluate);
			//田野添加结束
			
			// 查询当前用户所有计划里每个主体类别对应的打分确认标识opt by 刘蒙
			Map optMap = new HashMap();
			StringBuffer optSql = new StringBuffer();
			optSql.append("SELECT mb.plan_id as plan_id,mb.body_id as body_id,pb.opt as opt");
			optSql.append(" FROM per_mainbody mb LEFT JOIN per_plan_body pb");
			optSql.append(" ON mb.body_id = pb.body_id AND mb.plan_id = pb.plan_id");
			optSql.append(" WHERE mb.mainbody_id='");
			optSql.append(this.userView.getA0100().trim());
			optSql.append("'");
			
			RowSet rs = null;
			rs = dao.search(optSql.toString());
			while(rs.next()){
				// 以plan_id+body_id作为key,opt对应的“评估”或“确认”为value
				String body_id = rs.getString("body_id");
				if(StringUtils.isEmpty(body_id)){
					body_id = "";
				}
				String key = rs.getString("plan_id") + body_id;
				int optValue = rs.getInt("opt");
				// 如果opt为0或空，则value为评估，否则为确认，来自大属性文件
				String value = optValue == 0 ? "lable.performance.assessment" : "lable.performance.confirm";
				optMap.put(key, value);
			}
			if (rs != null) {
				rs.close();
			}
			ArrayList yearList = (ArrayList)map.get("2");
			ArrayList statusList = bo.getStatusList();
			ArrayList quarterList = bo.getQuarterList();
			ArrayList monthList = bo.getMonthList();
			this.getFormHM().put("year",year);
			this.getFormHM().put("yearList",yearList);
			this.getFormHM().put("month",month);
			this.getFormHM().put("quarter",quarter);
			this.getFormHM().put("monthList",monthList);
			this.getFormHM().put("quarterList",quarterList);
			this.getFormHM().put("personList",personList);
			this.getFormHM().put("status",status);
			this.getFormHM().put("statusList",statusList);
			this.getFormHM().put("entranceType", entranceType);
			this.getFormHM().put("plan_id", planid);
			this.getFormHM().put("isSort", isSort);
			this.getFormHM().put("isOrder", isOrder);
			this.formHM.put("optMap", optMap);
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 田野 2013-3-9
	 * 获取考核对象当年年初到上次为止的类似考核中分数的平均分的名次
	 * @param personList
	 * @return
	 */
	public ArrayList getObjectAccumulativeRank(ArrayList personList,Map planMap){
		//用于存放根据登录人参与打分的每个考核计划所涉及到的考核对象的所有人排名情况
		Map planobjectRankMap = new HashMap();
		Iterator it = planMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry) it.next();
			LazyDynaBean abean =(LazyDynaBean)pairs.getValue();
         	//根据计划查出相类似的计划（周期相同，模板相同，计划的相关信息）
         	ArrayList similarPlanList = getThisYearSimilarPlan( abean);
         	if(similarPlanList.size()!= 0){//没有相似的计划部进行下面操作，则没有累计排名信息
         		ContentDAO dao = new ContentDAO(this.getFrameconn());   
    			StringBuffer strSql = new StringBuffer();
    			RowSet rs = null;
    			strSql.append("select avg(score) avgScore,object_id from ");
    			for(int i = 0;i<similarPlanList.size();i++){
    				if(i!=0){
    					strSql.append("union all ");
    				}else{
    					strSql.append("( ");
    				}
    				strSql.append("select object_id ,score from per_result_"+similarPlanList.get(i));
    				strSql.append(" where object_id in (select object_id from per_mainbody where plan_id="+pairs.getKey());
    				strSql.append(" and mainbody_id = '"+this.userView.getA0100()+"' )");
    			}
    			strSql.append(") a group by object_id order by avgScore desc");
    			Map objectRank = new HashMap();
    			try {
					rs = dao.search(strSql.toString());
	    			while(rs.next()){
	    				//先让objectRank中存放的是object_id---avgScoreSeq
	    				objectRank.put(rs.getString("object_id"), rs.getString("avgScore")==null?"0":rs.getString("avgScore"));
	    			}
	    			
	    			if(!objectRank.isEmpty()){
	    				//排序让objectRank中存放的是object_id---avgScoreSeq
		    			LazyDynaBean[] temp= new LazyDynaBean[objectRank.size()];
		    			Iterator iter = objectRank.entrySet().iterator();
		    			int m = 0;
		    			while(iter.hasNext()){
		    				Map.Entry pair = (Map.Entry) iter.next();
		    				LazyDynaBean bean=new LazyDynaBean();
		    				bean.set("object_id", pair.getKey());
		    				bean.set("avgScore", pair.getValue());
		    				temp[m++] = bean;
		    			}
		    			
		    			for(int k=0;k<temp.length;k++)
		    			{
			               for(int l=0;l<temp.length-k-1;l++)
			               {
			             	  LazyDynaBean a_bean=temp[l];
			                   String score=(String)a_bean.get("avgScore");
			                   BigDecimal d_big=new BigDecimal(score);
			             	  LazyDynaBean b_bean=temp[l+1];
			                   String bscore=(String)b_bean.get("avgScore");
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
		    				LazyDynaBean bean=temp[j];
		    				String score=(String)bean.get("avgScore");
		    				if(j==0)
		    					bd1 = new BigDecimal(score);
		    				bd2 = new BigDecimal(score);
		    				if(bd1.compareTo(bd2)==1)
		    				{
		    					i++;
		    					bd1=bd2;
		    				}
		    				bean.set("avgScoreSeq", i+"");
		    				objectRank.put((String)bean.get("object_id"),(String)bean.get("avgScoreSeq"));
		    			}
		    			planobjectRankMap.put(pairs.getKey(),objectRank);
	    			}
	    			
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally{
					if(rs!=null){
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
         	}
		}
		 for(int i=0;i<personList.size();i++)
         {
			 LazyDynaBean bean =(LazyDynaBean)personList.get(i);
			 String plan_id =(String)bean.get("plan_id");
			 String object_id =(String)bean.get("object_id");
			 Map objectRankMap =(Map) planobjectRankMap.get(plan_id);
			 if(objectRankMap!=null){
				 String avgScoreSeq = (String)objectRankMap.get(object_id);
				 if(avgScoreSeq != null){
					 bean.set("avgScoreSeq", avgScoreSeq);
				 }else{
					 //和考核对象没有参加类似的考核计划
					 bean.set("avgScoreSeq", " ");
				 }
			 }else{
				 //没有相类似的计划
				 bean.set("avgScoreSeq", " ");
			 }
         }
		return personList;
		
	}
	/**
	 * 田野 2013-3-9
	 * 获取考核想类似的当年年初到上次为止考核计划
	 * @return 计划编号集合
	 * @throws SQLException 
	 */
	public ArrayList getThisYearSimilarPlan(LazyDynaBean abean){
			String cycle =(String)abean.get("cycle");
	     	String template_id =(String)abean.get("template_id");
	     	int year =Integer.parseInt((String)abean.get("theyear"));
	     	int month =Integer.parseInt((String)abean.get("themonth"));
	     	String start_date =(String)abean.get("start_date");
	     	String object_type =(String)abean.get("object_type");
	     	String thequarter =(String)abean.get("thequarter");
		 	ContentDAO dao = new ContentDAO(this.getFrameconn());   
		 	ArrayList similarPlanList = new ArrayList() ;
			StringBuffer strSql = new StringBuffer();
			RowSet rs = null;
			strSql.append("select plan_id  from per_plan where 1=1 ");
			if("1".equals(cycle)){//半年
				if("01".equals(thequarter)){
					return similarPlanList;
				}else{
					strSql.append(" and thequarter=01");
				}
			}
			if("2".equals(cycle)){//季度
				if("01".equals(thequarter)){
					return similarPlanList;
				}else{
					for(int i = 1;i<Integer.parseInt(thequarter);i++){
						if(i == 1){
							strSql.append(" and (Thequarter='0"+i+"'");
						}else{
							strSql.append(" or Thequarter='0"+i+"'");
						}
					}
					strSql.append(" )");
				}
			}
			if("3".equals(cycle)){//月度
				strSql.append(" and themonth<'"+month+"'");
			}
			if("7".equals(cycle)){//不定期
				strSql.append(" and Start_date<'"+start_date+"'");
				
			}else{
				strSql.append(" and theyear='"+year+"'");
			}
			strSql.append(" and Status=7");
			strSql.append(" and cycle='"+cycle+"'");
			strSql.append(" and template_id='"+template_id+"'");
			strSql.append(" and object_type='"+object_type+"'");
			strSql.append(" and Method='"+2+"'");
			
			try {
				rs = dao.search(strSql.toString());
				while(rs.next()){
					String plan_id =rs.getString("plan_id");
					similarPlanList.add(plan_id);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally{
				if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
			}
			return similarPlanList;	
	}
	
}
