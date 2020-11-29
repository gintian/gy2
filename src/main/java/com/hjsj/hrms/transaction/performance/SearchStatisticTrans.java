/*
 * 创建日期 2005-6-29
 *
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.statistic.StatisticPlan;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * @author luangaojiong
 * 
 * 成绩统计交易类
 */
public class SearchStatisticTrans extends IBusiness {

	HashMap itemht = new HashMap(); // 项目对象

	ArrayList pointlist = new ArrayList();// 所有要素对象

	ArrayList userpointlist = new ArrayList(); // 对用户测评的要素

	ArrayList examinelist = new ArrayList(); // 用户测评次数

	ArrayList itemscorelist = new ArrayList(); // 项目分值

	String flag = "0";

	String resuTableName = "";

	String planId = "0"; // 计划号

	public void execute() throws GeneralException {	
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
		try 
		{
			/**得到计划号*/
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			if (hm.get("planNum") != null) 
			{
				planId = hm.get("planNum").toString();
				this.getFormHM().put("planNum", planId);
				
				Hashtable htxml=new Hashtable();
				LoadXml loadxml=new LoadXml(this.getFrameconn(),planId);
				htxml=loadxml.getDegreeWhole();
				String nodeKnowDegree=(String)htxml.get("NodeKnowDegree");	    //了解程度
				String wholeEval=(String)htxml.get("WholeEval");			    //总体评价
				String showAppraiseExplain=(String)htxml.get("showAppraiseExplain");  ////综合评测表是否显示评测说明
				String isShowStatistic="0";
				if((nodeKnowDegree!=null&& "true".equalsIgnoreCase(nodeKnowDegree))||(wholeEval!=null&& "true".equalsIgnoreCase(wholeEval)))
					isShowStatistic="1";;
				this.getFormHM().put("isShowStatistic",isShowStatistic);
				this.getFormHM().put("showAppraiseExplain",showAppraiseExplain);
				//this.getFormHM().put("isShowStatistic","0");
			} 
			else 
			{
				if (this.getFormHM().get("planNum") == null || "".equals(this.getFormHM().get("planNum").toString()))
				{
					ArrayList enrol_list =new ArrayList();
					this.getFormHM().put("enrol_list",enrol_list);
					return;
				} 
				else 
				{					 
					planId = this.getFormHM().get("planNum").toString();
				}

			}
			if ("#".equals(planId.trim()) || "".equals(planId.trim()))
			{
				this.getFormHM().put("planNum", "");
				ArrayList enrol_list =new ArrayList();
				this.getFormHM().put("enrol_list",enrol_list);
				return;
			}
			
			Hashtable htxml=new Hashtable();
			LoadXml loadxml=new LoadXml(this.getFrameconn(),this.planId);
			htxml=loadxml.getDegreeWhole();
			String GATIShowDegree=(String)htxml.get("GATIShowDegree");			//BS 综合测评表中指标的评分显示为标度						
			this.getFormHM().put("GATIShowDegree",GATIShowDegree);
			
			/**得到模板号*/
			String sqlTemp = "select template_id from per_plan where plan_id="+ planId;
			String tempNum = "0";
			
			this.frowset = dao.search(sqlTemp);
			if (this.frowset.next()) 
				tempNum = this.frowset.getString("template_id"); // 取出模板号
			/**取项目HashMap*/
			StatisticHelpBean shb = new StatisticHelpBean();
			itemht = shb.getAllItem(this.getFrameconn(), tempNum);
			/**取要素对象信息*/
			pointlist = shb.getAllPoint(itemht, this.getFrameconn(), tempNum);

			/**得到用户综合评分*/
			getUserGrade();

			if ("1".equals(flag))
			{
				/**得到上级测评数目*/
				if ("1".equals(hm.get("planFlag").toString()))
				{
					if (this.getFormHM().get("objectId") != null) 
					{
						String objectIdTemp = this.getFormHM().get("objectId").toString();
						examinelist = shb.getMainBody(this.getFrameconn(),objectIdTemp,planId);
					}
					else 
					{
						// throw new GeneralException("","用户的ID为空!","","");
					}
				} 
				else 
				{
					examinelist = shb.getMainBody(this.getFrameconn(),this.userView.getA0100(),planId);
				}
				/**处理空测评项目并且只显示该计划下的主体类别*/
				examinelist = shb.doNullLeaderExamCount(examinelist, this.getFrameconn(),planId);
				/**计算上级测评总数目*/
				int lexamCount = shb.getLeaderExamCount(examinelist);
				this.getFormHM().put("totalCount", Integer.toString(lexamCount));
				this.getFormHM().put("examinelist", examinelist);

				/**显示项目及要素成绩*/
				StatisticGradeBean sttg = new StatisticGradeBean();

				ArrayList itemlstTemp = sttg.getItemwhilelist(pointlist,userpointlist, itemscorelist);
				/**判断项目空的标识*/
				if (itemlstTemp.size() <= 0) 
				{
					this.getFormHM().put("flag", "1");
				} 
				else 
				{
					this.getFormHM().put("flag", "0");
				}
				/**处理总体评价及了解程度*/
				ArrayList selecttick = new ArrayList();
				ArrayList knowlist = new ArrayList();
				/**互评*/
				if ("1".equals(hm.get("planFlag").toString()))
				{
					selecttick = sttg.getSelectTick(this.getFrameconn(), this
							.getFormHM().get("objectId").toString(),
							this.userView.getA0100(), planId);
					knowlist = sttg.getKnowlist(this.getFrameconn(), this
							.getFormHM().get("objectId").toString(),
							this.userView.getA0100(), planId);
				}
				/**
				 * 自评
				 */
				else 
				{
					selecttick = sttg.getSelectTick(this.getFrameconn(),
							this.userView.getA0100(), this.userView
									.getA0100(), planId);
					knowlist = sttg.getKnowlist(this.getFrameconn(),
							this.userView.getA0100(), this.userView
									.getA0100(), planId);
				}
				
				StatisticPlan statisticPlan=new StatisticPlan(this.userView,this.getFrameconn());
				ArrayList enrol_list=statisticPlan.getRnameListFromPlanID(this.planId);
				this.getFormHM().put("enrol_list",enrol_list); 
				this.getFormHM().put("elevellist", selecttick);
				this.getFormHM().put("knowlist", knowlist);
				this.getFormHM().put("itemTotalCount",
						Integer.toString(itemlstTemp.size()));
				this.getFormHM().put("itemwhilelist", itemlstTemp);
				this.getFormHM().put("planNum",this.planId);
			}
		} catch (Exception ex) 
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		

	}

	/**
	 * 
	 * @author luangaojiong
	 * 
	 * 得到用户综合评分及要素对象
	 */
	boolean getUserGrade()throws GeneralException {

		BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn());
		batchGradeBo.getDynaRankInfoMap(this.planId);
		batchGradeBo.getObjectInfoMap(this.planId);
		
		LoadXml loadxml=new LoadXml(this.getFrameconn(),planId);
		Hashtable htxml=new Hashtable();		
		htxml=loadxml.getDegreeWhole();
		String KeepDecimal=(String)htxml.get("KeepDecimal");
		
		boolean booltemp = true;
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		/**得到结果表中的信息*/
		try 
		{
			HashMap pointListMap=getPointListMap(this.planId);
			StringBuffer sb = new StringBuffer();
			{
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				dbmodel.reloadTableModel("per_result_"+planId);
			}
			/**互评*/
			if ("1".equals(hm.get("planFlag").toString()))
			{
				if (this.getFormHM().get("objectId") != null) 
				{
					String objectId = this.getFormHM().get("objectId").toString();
					sb.append("select * from per_result_");
					sb.append(planId);
					sb.append(" where object_id='");
					sb.append(objectId);
					sb.append("'");
				} 
				else 
				{
					// throw new GeneralException("","用户的ID为空!","","");
					// this.getFormHM().put("message","用户的ID为空!");
					// return;
				}
			}
			else /**自评*/
			{
				sb.append("select * from per_result_");
				sb.append(planId);
				sb.append(" where object_id='");
				sb.append(this.userView.getA0100());
				sb.append("'");
			}

			resuTableName = "per_result_" + planId;			
			//ResultSet resultset = st.executeQuery(sb.toString());
			this.frowset=dao.search(sb.toString());
			ResultSetMetaData resultsetmetadata = this.frowset.getMetaData();
			int columnCount = resultsetmetadata.getColumnCount();
			String conlumnName = "";
			ArrayList list = new ArrayList(); // 要素对应的分值列
			ArrayList listitem = new ArrayList(); // 项目对应的分值列
			String totalGrade = "";
			for (int j = 0; j < columnCount; j++) {
				conlumnName = resultsetmetadata.getColumnLabel(j + 1).toString();
				if ((conlumnName.startsWith("C") && "_"
						.equals(conlumnName.substring(1, 2)))
						|| (conlumnName.startsWith("c") && "_".equals(conlumnName
								.substring(1, 2)))) {
					if (!list.contains(conlumnName)) {
						list.add(conlumnName);
					}
				}

				if ((conlumnName.startsWith("T") && "_"
						.equals(conlumnName.substring(1, 2)))
						|| (conlumnName.startsWith("t") && "_".equals(conlumnName
								.substring(1, 2)))) {
					if (!listitem.contains(conlumnName)) {
						listitem.add(conlumnName);
					}
				}
			}

			/**得到分值及pointId 得到项目分值及itemId*/
			String pointIdName = "";
			String pointScoreValue = "";
			if (this.frowset.next()) 
			{
				//chenmengqing added 
				DynaBean result_bean=new LazyDynaBean();
				result_bean.set("B0110",this.frowset.getString("B0110"));
				result_bean.set("E0122",this.frowset.getString("E0122"));
				result_bean.set("A0101",this.frowset.getString("A0101"));
				
				for (int i = 0; i < list.size(); i++) 
				{
					StatisticBean sttb = new StatisticBean();
					pointScoreValue = PubFunc.NullToZero(this.frowset.getString(list.get(i).toString()));
					pointIdName = list.get(i).toString();
					pointIdName = pointIdName.substring(2, pointIdName.length());
					sttb.setPointId(pointIdName);
					DecimalFormat dcom = new DecimalFormat("0.00");					
					sttb.setPointGrade(getGradeCode(pointListMap,pointScoreValue,pointIdName,batchGradeBo)); //设置标度					
					
					sttb.setScore(round(pointScoreValue,Integer.parseInt(KeepDecimal)));
					//sttb.setScore(dcom.format(Double.parseDouble(pointScoreValue)));
					
					userpointlist.add(sttb);
				}
				DecimalFormat dcom2 = new DecimalFormat("0.00");
				//this.getFormHM().put("totalGrade",dcom2.format(Double.parseDouble(resultset.getString("score"))));
				//this.getFormHM().put("grade_id",resultset.getString("grade_id"));
				//this.getFormHM().put("resultdesc",resultset.getString("resultdesc"));
				flag = "1";
				this.getFormHM().put("flag", "0");
				//result_bean.set("score",dcom2.format(Double.parseDouble(this.frowset.getString("score"))));
				result_bean.set("score",round(this.frowset.getString("score"),Integer.parseInt(KeepDecimal)));
				result_bean.set("grade_id",this.frowset.getString("grade_id"));	
				result_bean.set("resultdesc",this.frowset.getString("resultdesc"));	
				this.getFormHM().put("result_bean",result_bean);
			} 
			else 
			{
				clearReset();
				flag = "0";
				return booltemp = false;
			}
			
			/**得到了解程度，总体评价统计*/
			this.frowset =dao.search(sb.toString());           //st.executeQuery(sb.toString());
			if (this.frowset.next()) 
			{
				/**得到项目分值及itemId*/
				String itemIdName = "";
				String itemScoreValue = "";
				if (listitem.size() > 0) 
				{
					for (int j = 0; j < listitem.size(); j++) 
					{
						StatisticBean sttb = new StatisticBean();
						itemScoreValue = PubFunc.NullToZero(this.frowset.getString(listitem.get(j).toString().trim()));
						itemIdName = listitem.get(j).toString();
						itemIdName = itemIdName.substring(2, itemIdName.length());
						sttb.setItemId(itemIdName);
					//	DecimalFormat dcom = new DecimalFormat("0.00");
					//	sttb.setItemScore(dcom.format(Double.parseDouble(itemScoreValue)));
						sttb.setItemScore(round(itemScoreValue,Integer.parseInt(KeepDecimal)));
						itemscorelist.add(sttb);
					}
				}
			} 
			else 
			{
				clearReset();
				flag = "0";
				return booltemp = false;
			}

			

		} catch (Exception ex) {

			clearReset();
			/*System.out
					.println("---->com.hjsj.hrms.transaction.statistic->getUserGrade error");*/
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return booltemp;
	}

	/**
	 * 重置操作
	 * 
	 */
	public void clearReset() {
		this.getFormHM().put("totalGrade", "0");
		this.getFormHM().put("grade_id", "");
		this.getFormHM().put("planNum", "0");
		this.getFormHM().put("elevellist", new ArrayList());
		this.getFormHM().put("knowlist", new ArrayList());
		this.getFormHM().put("examinelist", new ArrayList());
		this.getFormHM().put("totalCount", "0");
		this.getFormHM().put("itemwhilelist", new ArrayList());
		this.getFormHM().put("itemTotalCount", "0");
		this.getFormHM().put("resultdesc", "");
		this.getFormHM().put("flag", "1"); // 项目为0时的标识
		this.getFormHM().put("statisticDrawHm", new HashMap());
		this.getFormHM().put("result_bean",new LazyDynaBean());
	}
	
	
	
	
	public String getGradeCode(HashMap pointListMap,String score,String pointID,BatchGradeBo batchGradeBo)
	{
		String gradeCode="";
		ArrayList pointGradeList=(ArrayList)pointListMap.get(pointID);
		if(pointGradeList==null)
			return gradeCode;
		LazyDynaBean abean=(LazyDynaBean)pointGradeList.get(0);
	    String rank=(String)abean.get("rank");
		String arank=batchGradeBo.getRankByObjectID(rank,this.getFormHM().get("objectId").toString(),pointID);
		BigDecimal b_score=new BigDecimal(score);
		BigDecimal b_rank=new BigDecimal(arank);
		float a_score=b_score.divide(b_rank,1,BigDecimal.ROUND_HALF_UP).floatValue();
		for(int i=0;i<pointGradeList.size();i++)
		{
			LazyDynaBean bean=(LazyDynaBean)pointGradeList.get(i);
			BigDecimal topValue=new BigDecimal((String)bean.get("top_value"));
			BigDecimal bottomValue=new BigDecimal((String)bean.get("bottom_value"));
			
			BigDecimal temp_score=new BigDecimal((String)bean.get("score"));
			String 		gradecode=(String)bean.get("gradecode");
			float topvalue=topValue.multiply(temp_score).floatValue();
			float bottomvalue=bottomValue.multiply(temp_score).floatValue();
			if(i==0&&a_score>topvalue)
			{
				gradeCode=gradecode;
				break;
			}
			if(a_score<=topvalue&&a_score>=bottomvalue)
			{
				gradeCode=gradecode;
				break;
			}
		}
		return gradeCode;
	}
	
	/**
	 * 获得计划信息
	 * @param planid
	 * @return
	 */
	public RecordVo getPlanVo(String planid)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo.setInt("plan_id",Integer.parseInt(planid));
			vo=dao.findByPrimaryKey(vo);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	/**
	 * 取得计划下每个指标的标度
	 * @param planid
	 * @return
	 */
	public HashMap getPointListMap(String planid)throws GeneralException
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			RecordVo planVo = getPlanVo(planid);
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(planVo.getInt("busitype"))!=null && String.valueOf(planVo.getInt("busitype")).trim().length()>0 && planVo.getInt("busitype")==1)
				per_comTable = "per_grade_competence"; // 能力素质标准标度
			String sql="select pp.item_id,po.point_id,po.pointname,po.pointkind,pg.gradecode,pg.top_value,pg.bottom_value,pp.score,pg.gradevalue,pp.rank "
				      +" from per_template_item pi,per_template_point pp,per_point po ,per_grade pg,"+per_comTable+" pgt,per_plan "
				      +" where pi.item_id=pp.item_id and pp.point_id=po.point_id and  po.point_id=pg.point_id and pg.gradecode=pgt.grade_template_id "
				      +" and pi.template_id=per_plan.template_id  and per_plan.plan_id="+planid+"  order by pp.seq ";
			this.frowset=dao.search(sql);
			
			String a_pointid="";
			ArrayList ponitGradelist=new ArrayList();
			while(this.frowset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				String pointid=this.frowset.getString("point_id");
				abean.set("item_id",this.frowset.getString("item_id"));
				abean.set("point_id",this.frowset.getString("point_id"));
				abean.set("pointname",this.frowset.getString("pointname"));
				abean.set("pointkind",this.frowset.getString("pointkind"));
				abean.set("gradecode",this.frowset.getString("gradecode"));
				abean.set("top_value",this.frowset.getString("top_value"));
				abean.set("bottom_value",this.frowset.getString("bottom_value"));
				abean.set("score",this.frowset.getString("score"));
				abean.set("gradevalue",this.frowset.getString("gradevalue"));
				abean.set("rank",this.frowset.getString("rank"));
				
				if(this.frowset.getString("top_value")==null||this.frowset.getString("bottom_value")==null)
					throw GeneralExceptionHandler.Handle(new Exception("该计划下某些指标没设置上下限值！"));
				
				
				if("".equals(a_pointid)||!pointid.equals(a_pointid))
				{
					if(!"".equals(a_pointid))
						map.put(a_pointid,ponitGradelist);
					ponitGradelist=new ArrayList();
					a_pointid=pointid;
				}
				ponitGradelist.add(abean);
			}
			map.put(a_pointid,ponitGradelist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return map;
	}
	
	
	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public String round(String v, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	}
}