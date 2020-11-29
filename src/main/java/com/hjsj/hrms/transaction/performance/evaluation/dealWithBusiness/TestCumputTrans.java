package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:TestCumputTrans.java</p>
 * <p>Description:归档前进行检验</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-09 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class TestCumputTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		String planid = (String) this.getFormHM().get("planID");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String objs = "";
		LoadXml parameter_content = new LoadXml(this.getFrameconn(), planid);
		Hashtable params = parameter_content.getDegreeWhole();
		String gradeClass = (String) params.get("GradeClass");
		boolean flag = false;
		String type = "1";
		int count = 0;
		HashMap map = new HashMap();
		try
		{
		    String sql = "";
		    if (gradeClass != null && !"-1".equals(gradeClass))// 该计划使用等级分类
		    	sql = "select a0101 from per_result_" + planid + " where resultdesc is null ";
		    else
		    	sql = "select a0101 from per_result_" + planid + " where score is null";
		    RowSet rs = dao.search(sql);
	
		    while (rs.next())
		    {
				type = "2";
				flag = true;
				if (count < 3)
				{
					if(map.get(rs.getString("a0101"))==null)
						objs += "、" + rs.getString("a0101");
				}
				    
				if(map.get(rs.getString("a0101"))==null)
				{
					count++;
					map.put(rs.getString("a0101"), rs.getString("a0101"));
				}
		    }
	
		    StringBuffer info = new StringBuffer();
		    info.append((objs.length() > 0 ? objs.substring(1) : " "));
		    if (count > 3)
		    	info.append("等");
		    if (gradeClass != null && !"-1".equals(gradeClass) && count>0)// 该计划使用等级分类
		    	info.append(count + "个考核对象没有考核等级！");
		    else if(count>0)
		    	info.append(count + "个考核对象没有考核得分！");
		    info.append("\\n是否继续进行归档操作？");
		    RecordVo vo = this.getPerPlanVo(planid);
		    // 检查人员或者机构是否在人员库中
		    if (count == 0)
		    {
		    	map = new HashMap();
				String objectType = "2";
				if (vo.getString("object_type") != null)
				    objectType = vo.getString("object_type");
				StringBuffer buf = new StringBuffer();
				buf.append("select a0101 from per_result_" + planid);
				if ("2".equals(objectType))
				    buf.append(" WHERE NOT (object_id IN (SELECT A0100 FROM USRA01))");
				else
				    buf.append(" WHERE NOT (object_id IN (SELECT B0110 FROM B01))");
		
				rs = dao.search(buf.toString());
				objs="";
				while (rs.next())
				{
				    type = "2";
				    flag = true;
				    if (count < 3)
				    {
				    	if(map.get(rs.getString("a0101"))==null)
				    		objs += "、" + rs.getString("a0101");
				    }
					
					if(map.get(rs.getString("a0101"))==null)
					{
						count++;
						map.put(rs.getString("a0101"), rs.getString("a0101"));
					}
				}
				info.setLength(0);
				if(objs.length() > 0)
				{
				    if("2".equals(objectType))
				    	info.append("以下人员已经不在在职人员库中，他们不能归档：\\n"+objs.substring(1));
				    else
				    	info.append("以下机构没有维护主集信息，它们不能归档：\\n"+objs.substring(1));
				    info.append("\\n是否继续进行归档操作？");
				}		
		    }
		    //检查是否提交绩效报告  能力素质没有绩效报告 郭峰修改
		    String busitype = vo.getString("busitype")==null?"":vo.getString("busitype");
		    String summaryFlag = (String) params.get("SummaryFlag");//判断是否勾选了显示绩效报告参数
		    if (count == 0 && !"1".equals(busitype) && "True".equalsIgnoreCase(summaryFlag))
		    {
		    	map = new HashMap();
		    	sql="select a0101,Article_name,content from per_article where plan_id="+planid+" and Article_type=2 and state!=1 and state!=2 and fileflag=1";//state =1提交  =2批准
			    rs = dao.search(sql);
			    objs="";
			    while (rs.next())
			    {
			    	String content = rs.getString("content");    //  绩效报告内容
			    	String Article_name = rs.getString("Article_name"); // 上传附件名称			    	
			    	
					type = "2";
//					flag = true;
					if (count < 3)
					{
						if(map.get(rs.getString("a0101"))==null)
							objs += "、" + rs.getString("a0101");
					}
					if((map.get(rs.getString("a0101"))==null))
					{
						count++;
						flag = true;
						map.put(rs.getString("a0101"), rs.getString("a0101"));
					}			
			    }
	
			    info.setLength(0);
			    info.append((objs.length() > 0 ? objs.substring(1) : " "));
			    if (count > 3)
			    	info.append("等");
			    if (count>0)
			    	info.append(count + "人的绩效报告还没有提交，不能归档绩效报告。是否继续归档？");
			 
		    }
		    this.getFormHM().put("isExist", (flag ? "true" : "false"));
		    this.getFormHM().put("info", info.toString());
		    this.getFormHM().put("type", type);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}

    }

    public RecordVo getPerPlanVo(String plan_id){
    	RecordVo vo = new RecordVo("per_plan");
		try
		{
			vo.setString("plan_id", plan_id);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
		    vo = dao.findByPrimaryKey(vo);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return vo;
    }
    
}
