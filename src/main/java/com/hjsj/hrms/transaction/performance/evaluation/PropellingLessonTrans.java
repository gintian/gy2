package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:PropellingLessonTrans.java</p>
 * <p>Description:能力素质模型推送考核对象考核不合格的指标关联的课程</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-02-17 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class PropellingLessonTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		
		String hjsoft = (String) this.getFormHM().get("hjsoft");	// singlePoint:单人单指标的推送 multiplePoint:所有对象所有指标同时推送
		String plan_id = (String) this.getFormHM().get("plan_id");	
		String objectId = (String) this.getFormHM().get("object_id");	
		String pointId = (String) this.getFormHM().get("point_id");					
	//	String khObjScope = (String) this.getFormHM().get("khObjScope");	// 考核对像范围 
	//  khObjScope = PubFunc.keyWord_reback(khObjScope);	
		ContentDAO dao = new ContentDAO(this.frameconn);	
		String flag = "nook";
		try
		{			
			ArrayList resultList = getPerResultList(plan_id,objectId); 	// 取得范围内考核对象结果信息			
			PostModalBo pmo = new PostModalBo(this.getFrameconn(),this.userView,plan_id);
			ArrayList pointList = pmo.getPointList();  // 获得模板下的指标（按顺序）
			// 获得能力素质各岗位定义的分值、权重和要求的等级
		    HashMap postScoreMap = pmo.getE01a1RankScore();
		    
		    boolean isByModelFlag = SingleGradeBo.getByModel(plan_id, this.getFrameconn());
			//  循环所有人员信息
			for(int i=0;i<resultList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)resultList.get(i);
				String object_id = (String)bean.get("object_id");  // 人员编号
				// 获得考核结果表中某人的记录的主键id
			    String perResultId = pmo.getPerResultId(object_id);
			    RecordVo perResultVo = pmo.getPerResultVo(perResultId); // 获得考核结果表中某人的信息记录
			    
			    if(isByModelFlag){//2013.12.2  pjf
					String e01a1 = pmo.getE01a1(object_id);
					pointList = pmo.getPointListByModel(object_id, e01a1);
			    }
				//  循环所有指标
				for(int j=0;j<pointList.size();j++)
				{
					LazyDynaBean lbean = (LazyDynaBean)pointList.get(j);
				//	String item_id = (String)lbean.get("item_id");  // 项目ID
					String point_id = (String)lbean.get("point_id");  // 指标ID
				//	String pointsetid = (String)lbean.get("pointsetid");  // 指标分类ID
				//	String pointname = (String)lbean.get("pointname"); // 指标名称	
				//	String proposal = (String)lbean.get("proposal"); // 行动建议	
					String score = (String)lbean.get("score");     // 指标得分	
				//	String rank = (String)lbean.get("rank");     // 指标权重	
					String scoreRankDegree = (String)postScoreMap.get(perResultVo.getString("e01a1")+":"+point_id.toLowerCase());
										
					if(point_id!=null && point_id.trim().length()>0)
					{
						// 单人单指标的推送
						if("singlePoint".equalsIgnoreCase(hjsoft) && pointId!=null && pointId.trim().length()>0 && !pointId.equalsIgnoreCase(point_id))
						{
							continue;
						}
						double achveScore = 0.0;
						if(isByModelFlag){//2013.12.2  pjf
							achveScore = pmo.getPointScore(point_id,object_id);
						} else{
							achveScore = perResultVo.getDouble("c_" + point_id.toLowerCase()); // 此指标的考核得分
						}
						if(scoreRankDegree!=null && scoreRankDegree.trim().length()>0)
						{
							score = scoreRankDegree.substring(0,scoreRankDegree.indexOf("`"));	
							String degreeCode = scoreRankDegree.substring(scoreRankDegree.indexOf("&")+1);	
														
							if(score!=null && score.trim().length()>0 && !"0.0".equals(score))
							{								
								double minScore = achveScore/Double.parseDouble(score);										
								String bottom_value = pmo.getFieldGradeCodeList(point_id,degreeCode);		
								String bottom_score=degreeCode.substring(degreeCode.indexOf("@")+1, degreeCode.indexOf("~"));
					    		//if(bottom_value!=null && bottom_value.trim().length()>0 && minScore<Double.parseDouble(bottom_value)) // 此人此指标考核不合格：把此指标关联的学习课程推送给此人
								if(achveScore<Double.parseDouble(bottom_score)) // 此人此指标考核不合格：把此指标关联的学习课程推送给此人
								{	
					    			// 获得能力素质指标是否关联的课程
					    			ArrayList courseList = pmo.getPerpointCourseList(point_id);					    			
					    			if(courseList!=null && courseList.size()>0)
					    			{
					    				// 循环指标关联的所有课程
					    				for(int k=0;k<courseList.size();k++)
					    				{
					    					LazyDynaBean zbean = (LazyDynaBean)courseList.get(k);
					    					String r5000 = (String)zbean.get("r5000");  // 课程ID
					    				
					    					boolean cLesson = insertPerpointCourse(object_id,r5000);
					    					if(cLesson) // 判断学员课程关联信息表中是否已存在此课程
					    					{
					    						flag = "hjsoft";
					    						continue;
					    					}
					    						
						    				RecordVo vo = new RecordVo("tr_selected_lesson");
											IDGenerator idg = new IDGenerator(2, this.getFrameconn());
											int id = Integer.parseInt(idg.getId("tr_selected_lesson.id"));
											vo.setInt("id", id);										
											vo.setInt("r5000", Integer.parseInt(r5000));										
											vo.setString("nbase", "Usr");
											vo.setString("a0100", object_id);
											vo.setString("b0110", perResultVo.getString("b0110"));
											vo.setString("e0122", perResultVo.getString("e0122"));
											vo.setString("e01a1", perResultVo.getString("e01a1"));
											vo.setString("a0101", perResultVo.getString("a0101"));
											vo.setInt("lprogress", 0);
											vo.setInt("learnedhour", 0);
											vo.setInt("pass_state", 0);
											vo.setDouble("exam_result", achveScore);											
											vo.setDate("start_date", PubFunc.getStringDate("yyyy-MM-dd")); // 获取当前时间
											vo.setDate("end_date", "9999-12-31");
											vo.setInt("lesson_from", 2);
											vo.setInt("state", 0);
											
											dao.addValueObject(vo);
											flag = "ok";
											
											/**保存相应课件信息*/
											// 获得培训课程关联的课件
							    			ArrayList courseWareList = pmo.getCourseWareList(r5000);					    			
							    			if(courseWareList!=null && courseWareList.size()>0)
							    			{
							    				// 循环课程关联的所有课件
							    				for(int m=0;m<courseWareList.size();m++)
							    				{
							    					LazyDynaBean ybean = (LazyDynaBean)courseWareList.get(m);
							    					String r5100 = (String)ybean.get("r5100");  // 课件ID
												
													vo = new RecordVo("tr_selected_course");
													vo.setInt("id", id);
													vo.setInt("r5100", Integer.parseInt(r5100));												
													vo.setString("nbase", "Usr");
													vo.setString("a0100", object_id);
													vo.setInt("lprogress", 0);
													vo.setInt("learnedhour", 0);
													vo.setInt("state", 0);
													
													dao.addValueObject(vo);
							    				}
											}
					    				}
					    			}						    									    				
					    		}else{
					    			flag="good";
					    		}					    								    																				
							}
						}
					}					
				}				
			}
			
			this.getFormHM().put("flag", flag);
			
		} catch (Exception e)
		{
			this.getFormHM().put("flag", "error");
			e.printStackTrace();			
			throw GeneralExceptionHandler.Handle(e);
		}		

	}
	
	/**
	 * 判断学员课程关联信息表中是否已存在此课程
	 * @return
	 */
	public boolean insertPerpointCourse(String object_id,String r5000)
	{		
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);			
			
			StringBuffer sql = new StringBuffer("");
			sql.append("select a0100 from tr_selected_lesson where nbase='Usr' and a0100='" + object_id + "' and r5000=" + r5000 + " ");		    
		    sql.append(" and lesson_from=2 ");
						
		    rowSet = dao.search(sql.toString());	    	    	    
		    if (rowSet.next())
		    	return true;
		    					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;				
	}	
	
	/**
	 * 取得范围内考核对象结果信息
	 * @return
	 */
	public ArrayList getPerResultList(String plan_id,String object_id)
	{
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
			String whl = pb.getPrivWhere(userView);// 根据用户权限先得到一个考核对象的范围
			
			StringBuffer sql = new StringBuffer("");
			sql.append("select object_id from per_result_" + plan_id + " where 1=1 ");
			if (object_id != null && object_id.trim().length() > 0)
		    	sql.append(" and object_id='"+ object_id +"' ");
		    if (whl != null && whl.length() > 0)
		    	sql.append(whl);
		    sql.append(" order by a0000");
						
		    rowSet = dao.search(sql.toString());	    	    	    
		    while (rowSet.next())
		    {
		    	LazyDynaBean abean = new LazyDynaBean();				
				abean.set("object_id", isNull(rowSet.getString("object_id")));	
				list.add(abean);
		    }					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public String isNull(String str)
    {
    	if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    return "";
		else
		    return str;
    }

}