package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleCheckBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.*;

public class AllPlansSingleGradeTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		try
		{
			if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			//非在职人员不允许使用改功能
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
			}
			//获取参数
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String model=(String)hm.get("model"); //  0：绩效考核  1：民主评测   2:团队考核 3：人员考核  4：单位
			if(model==null || "".equals(model))
				model="0";
			String optObject=(String)hm.get("optObject");   // 1：领导班子  2：班子成员
			hm.remove("model");
			hm.remove("optObject");
			
			ArrayList dblist = new ArrayList();//取得所有的计划 辅助作用
			HashMap planObjectMap = new HashMap();//计划号为键，该计划下所有考核对象的list为键值   为了循环所有计划所有对象用的。
			String isHasSaveButton = "0";//是否有保存、提交按钮  如果所有计划都是已评状态，那么就没有这两个按钮
			StringBuffer gradeHtml = new StringBuffer("");//在后台生成的表格
			String mainbody = this.getUserView().getA0100();//考核主体编号
			StringBuffer jsonStr = new StringBuffer("[");//所有数据组成json串，用于在前台循环用
			int altogether = 0;//总共有多少个打分表
			int current = 0;//当前是第几个打分表
			
			dblist = this.getDbList(model, optObject);
			SingleGradeBo tempSingleGradeBo=new SingleGradeBo(this.frameconn,this.userView);
			ArrayList aList=new ArrayList();
			if(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))
				aList=tempSingleGradeBo.getBatchGradeBo().addGradeStaus(dblist,this.getUserView().getA0100(),1,0);
			else
				aList=tempSingleGradeBo.getBatchGradeBo().addGradeStaus(dblist,this.getUserView().getA0100(),2,1);
			dblist=aList;//形式 ：多个vo。每个vo的value是planid，name是计划名称加状态

			//开始生成planObjectMap
			planObjectMap = getPlanObjectMap(dblist,model);
			//开始生成altogether
			altogether = getAltogether(planObjectMap);
			
			//开始循环所有计划
			int plancount = dblist.size();
			for(int i=0;i<plancount;i++){
				CommonData vo = (CommonData)dblist.get(i);
				String tempplan_id = vo.getDataValue();
				String tempplan_name = vo.getDataName();
				if("0".equals(tempplan_id)){
					continue;
				}
				SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn,this.userView);
				singleGradeBo.getPlanVo(tempplan_id);//为singleGradeBo里面的全局变量planVo赋值
				String titlename = vo.getDataName();
				
				//获取该计划的一些信息
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				int object_type=2; // 1:部门 2：人员
				String template_id = "";//模板号
				String status = "0";//权重分值表识 0：分值 1：权重
				this.frowset=dao.search("select pp.template_id,pt.status,pp.object_type,pp.descript from per_plan pp,per_template pt where pp.template_id=pt.template_id and pp.plan_id="+tempplan_id);
				if(frowset.next()){
					template_id=this.frowset.getString(1);
					status=this.frowset.getString(2);		
					if(status==null|| "".equals(status))
						status="0";
					object_type=this.frowset.getInt(3);
				}
				
				//加载一下激活参数
				BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),tempplan_id);
				singleGradeBo.setBatchGradeBo(batchGradeBo);
				LoadXml loadxml=null;
				Hashtable htxml=new Hashtable();
				if(BatchGradeBo.getPlanLoadXmlMap().get(tempplan_id)==null){
					loadxml = new LoadXml(this.getFrameconn(),tempplan_id);
					BatchGradeBo.getPlanLoadXmlMap().put(tempplan_id,loadxml);
				}
				else{
					loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(tempplan_id);
				}
				htxml=loadxml.getDegreeWhole();
				//以下获取考核计划的一些参数
				String _KeepDecimal = "1";
				if(htxml.get("KeepDecimal")!=null&&((String)htxml.get("KeepDecimal")).trim().length()>0){
					_KeepDecimal=(String) htxml.get("KeepDecimal"); // 小数位
				}
				String PointEvalType = (String)htxml.get("PointEvalType");//360指标评分型式  0：下拉（默认）   1：单选
				String scoreflag=(String)htxml.get("scoreflag");		// =2混合，=1标度(默认值=混合)  4=打分按加扣分处理
				String DegreeShowType = (String)htxml.get("DegreeShowType");// 标度显示形式(1-标准标度内容 2-指标标度内容 3-采集标准标度,显示指标标度内容）
				String RadioDirection=(String)htxml.get("RadioDirection"); //排列方式 1. 横排 0.竖排
				String isEntireysub = ((String)htxml.get("isEntireysub")).toLowerCase();// 提交是否需要必填
				String evalOutLimitStdScore = ((String)htxml.get("EvalOutLimitStdScore")).toLowerCase();
				String ShowEmployeeRecord=(String)htxml.get("ShowEmployeeRecord");           //显示员工日志
				String showDayWeekMonth=(String)htxml.get("ShowDayWeekMonth");
				String totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
			    if(totalAppFormula==null || totalAppFormula.trim().length()<=0)
			    	totalAppFormula = "";
			    else
			    	totalAppFormula = "1";
				String  noteIdioGoal=((String)htxml.get("noteIdioGoal")).toLowerCase();	  //显示个人目标
				String showOneMark=(String)htxml.get("ShowOneMark");
				String isShowTotalScore=(String)htxml.get("ShowTotalScoreSort");
				//获取考核计划的一些参数 结束
				
				ArrayList innerObjectlist = (ArrayList)planObjectMap.get(tempplan_id);//得到该计划下所有考核对象
				int objectcount = innerObjectlist.size();
				for(int j=0;j<objectcount;j++){
					current++;
					String description = "第"+current+"张&nbsp;&nbsp;共"+altogether+"张";
					String complexObjectid = (String)innerObjectlist.get(j);
					if("".equals(complexObjectid)){
						continue;
					}
					String innerObjectid = complexObjectid.replaceAll("／", "/").split("/")[0];
					if("".equals(innerObjectid))
						continue;
					//以下代码  准备form数据
					String a_status=complexObjectid.replaceAll("／", "/").split("/")[1];//打分状态
					String fillctrl="0";//是否必须打分    =1必打分
					String objectName = "";//考核对象的名字
					this.frowset=dao.search("select po.object_id,po.a0101,pm.status,pm.fillctrl,po.a0101 from per_mainbody pm,per_object po  where pm.object_id=po.object_id  and  pm.plan_id="+tempplan_id+" and po.plan_id="+tempplan_id+" and pm.mainbody_id='"+this.getUserView().getA0100()+"' and po.object_id='"+innerObjectid+"'");
					if(this.frowset.next()){
						a_status=this.frowset.getString("status");	
						objectName = this.frowset.getString("a0101");
						if(this.frowset.getString("fillctrl")!=null)
							fillctrl=this.frowset.getString("fillctrl");
					}
					
					singleGradeBo.setFillctrl(fillctrl);
					singleGradeBo.setShowOneMark(showOneMark);
					//获取存放所有数据的list
					ArrayList list=singleGradeBo.getSingleGradeHtml(template_id,tempplan_id,status,this.getUserView().getA0100(),innerObjectid,a_status,titlename,1,this.userView);
					
					//以下6个参数获得指标说明（targetDeclare）绩效数据（individualPerformance）员工日志（employRecordUrl）绩效目标（goalComment）绩效报告（personalComment），填表说明(fillTableExplain)
				    String targetDeclare=singleGradeBo.getTargetDeclare(tempplan_id,loadxml);//指标说明(已OK)
				    String individualPerformance=singleGradeBo.getIndividualPerformance(tempplan_id,innerObjectid,this.getUserView().getA0100(),loadxml,object_type,0);//绩效数据（已OK）
				    String employRecordUrl = "";//员工日志的链接（已OK）
				    if("True".equalsIgnoreCase(ShowEmployeeRecord)){ //显示员工日志
						singleGradeBo.getBatchGradeBo().setPlanid(tempplan_id);
				    	LazyDynaBean timeBean=singleGradeBo.getBatchGradeBo().getPlanKhTime();
				    	String a0100=singleGradeBo.getBatchGradeBo().getA0100(innerObjectid,tempplan_id);
				    	String desc=ResourceFactory.getProperty("performance.singlegrade.seeDiary"); 
				    	if(a0100.length()>0&&showDayWeekMonth !=null&&showDayWeekMonth.trim().length()>0){
				    		a0100=SafeCode.encode(PubFunc.convertTo64Base(a0100));
				    		employRecordUrl = "<a href='javascript:showWordDiary(\""+tempplan_id+"\",\""+a0100+"\",\""+(String)timeBean.get("start_date")+"\",\""+(String)timeBean.get("end_date")+"\")'   >"+desc+"</a>";
				    	}
				    }
				    String goalComment = "";//绩效目标（待检查）
				    if(((String)list.get(11)).length()>0&&(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))&&this.userView.getA0100().equals(innerObjectid))
				    {
				    	goalComment = "<a href='/selfservice/performance/selfGrade.do?b_querySummary=link&optUrl=goal' target='_blank'>"+ResourceFactory.getProperty("lable.performance.perGoal")+"</a>";
				    }
				    else
				    	goalComment = (String)list.get(11);
				    String personalComment = "";//绩效报告（已OK）
				    if(((String)list.get(11)).length()>0&&(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))&&this.userView.getA0100().equals(innerObjectid)){
				    	personalComment = "<a href='/selfservice/performance/selfGrade.do?b_querySummary=link' target='_blank'>"+ResourceFactory.getProperty("lable.performance.perSummary")+"</a>";
				    }
				    else
				    	personalComment = (String)list.get(1);
				    String fillTableExplain = "";
				    
				    //以下参数为表体中的数据。如不打分原因，总体评价 等。
				    String notMark="";//不打分原因
					notMark=singleGradeBo.getNotMark(complexObjectid,tempplan_id,this.getUserView().getA0100(),loadxml);


					String tempGradeHtml = "";//再生成具体的打分模板
				    if(("1".equals(PointEvalType)) && ("1".equals(scoreflag))   && "0".equals(RadioDirection)){ //如果评分方式是单选，按标度，竖排
					    SingleCheckBo singleCheckBo=new SingleCheckBo(this.frameconn,this.userView,tempplan_id,innerObjectid,this.getUserView().getA0100());
					    ArrayList htmlList=singleCheckBo.getSingleGradeHtml(a_status,titlename,status,a_status);				    				    				  				    
					    tempGradeHtml = (String)htmlList.get(0);
					    if("0".equals(isHasSaveButton)){
							isHasSaveButton = singleCheckBo.getIsHasSaveButton();
						}
					    fillTableExplain = singleCheckBo.getFillTableExplain();
				    }else{//如果不是单选按钮
				    	if("0".equals(isHasSaveButton)){
							isHasSaveButton = singleGradeBo.getIsHasSaveButton();
						}
				    	tempGradeHtml = (String)list.get(0);
				    	fillTableExplain = singleGradeBo.getFillTableExplain();
				    }
				    //生成考核对象及他后面的一连串链接
				    StringBuffer sbObject = new StringBuffer("");//先生成考核对象，以及考核对象后面的那一堆打分参考
				    String tempKey = tempplan_id+innerObjectid;
				    sbObject.append("<table width=\"55%\" border='0' cellspacing='0' cellpadding='0'><tr><td>"+"<img id=\""+tempKey+"img\" src=\"/images/Rminus.gif\" border=\"0\" style=\"cursor:pointer\" onclick=\"hideShowTable('"+tempKey+"');\">"+"</td><td>&nbsp;考核对象："+objectName);
				    //加上 绩效数据、员工日志 等几个链接
				    if(!"".equals(targetDeclare.trim())){
				    	sbObject.append("&nbsp;&nbsp;"+targetDeclare);
				    }
				    if(!"".equals(individualPerformance.trim())){
				    	sbObject.append("&nbsp;&nbsp;"+individualPerformance);
				    }
				    if(!"".equals(employRecordUrl.trim())){
				    	sbObject.append("&nbsp;&nbsp;"+employRecordUrl);
				    }
				    if(!"".equals(goalComment.trim())){
				    	sbObject.append("&nbsp;&nbsp;"+goalComment);
				    }
				    if(!"".equals(personalComment.trim())){
				    	sbObject.append("&nbsp;&nbsp;"+personalComment);
				    }
				    if(!"".equals(fillTableExplain.trim())){
				    	sbObject.append("&nbsp;&nbsp;"+fillTableExplain);
				    }
				    sbObject.append("</td>");
				    sbObject.append("<td align=\"right\">"+description+"</td>");
				    sbObject.append("</tr></table>");
				    
				    //合成整个打分页面
				    gradeHtml.append(sbObject.toString());
				    gradeHtml.append(tempGradeHtml);
				    gradeHtml.append("<table><tr height=\"20px;\"><td>&nbsp;</td></tr></table>");
				    
				    //get(1) 绩效报告
				    String isNull = (String)list.get(2);//指标范围是否为空 0：不为空  1：为空
				    String dataArea = "";//数值范围
				    if(((String)list.get(4)).length()>0)
				    	dataArea = ((String)list.get(4)).substring(1);
				    else
				    	dataArea = "";
				    String nodeKnowDegree = (String)list.get(5);//了解程度
				    String wholeEval = (String)list.get(6);//总体评价
				    String limitation = (String)list.get(7);//=-1不转换,模板中最高标度的数目 (大于0小于1为百分比，大于1为绝对数)
				    String gradeClass = (String)list.get(8);//等级分类
				    String scoreBySumup = (String)list.get(10);//BS个人总结没填写，主体为其打分时不能提交
				    //list.get(11) 绩效目标
				    String pointIDs = (String)list.get(12);//所有指标
				    String pointContrl = (String)list.get(13);
				    String noGradeItem = singleGradeBo.getNoGradeItem();//没有设置上下限值的指标名称
				    //数据全部准备完毕
				    //以下开始向json中存储数据  json的格式为[{"mainbody_id":00000009,"_KeepDecimal":3,"PointEvalType":45,"wholeEval":郭峰芦苇},{"mainbody_id":00000009,"_KeepDecimal":3,"PointEvalType":45,"wholeEval":郭峰芦苇}]
				    //首先存储最重要的变量
				    jsonStr.append("\r\n{\"plan_id\":\""+tempplan_id+"\",\"object_id\":\""+innerObjectid+"\",\"mainbody_id\":\""+mainbody+"\",\"plan_name\":\""+tempplan_name+"\",\"object_name\":\""+objectName+"\",\r\n");
				    //再存储整个计划的变量
				    jsonStr.append("\"keepDecimal\":\""+_KeepDecimal+"\",\"pointEvalType\":\""+PointEvalType+"\",\"scoreflag\":\""+scoreflag+"\",\"degreeShowType\":\""+DegreeShowType+"\",\r\n");
				    jsonStr.append("\"noteIdioGoal\":\""+noteIdioGoal+"\",\"radioDirection\":\""+RadioDirection+"\",\"totalAppFormula\":\""+totalAppFormula+"\",\"isEntireysub\":\""+isEntireysub+"\",\r\n");
				    jsonStr.append("\"evalOutLimitStdScore\":\""+evalOutLimitStdScore+"\",\"template_id\":\""+template_id+"\",\"status\":\""+status+"\",\"isShowTotalScore\":\""+isShowTotalScore+"\",\r\n");
				    //最后存储与考核对象息息相关的变量
				    jsonStr.append("\"dataArea\":\""+dataArea+"\",\"notMark\":\""+notMark+"\",\"isNull\":\""+isNull+"\",\r\n");
				    jsonStr.append("\"nodeKnowDegree\":\""+nodeKnowDegree+"\",\"wholeEval\":\""+wholeEval+"\",\"limitation\":\""+limitation+"\",\"gradeClass\":\""+gradeClass+"\",\r\n");
				    jsonStr.append("\"pointContrl\":\""+pointContrl+"\",\"pointIDs\":\""+pointIDs+"\",\"scoreBySumup\":\""+scoreBySumup+"\",\"noGradeItem\":\""+noGradeItem+"\",\"scoreStatus\":\""+a_status+"\"");
				    jsonStr.append("},");

				} //内层循环结束
				
			} //外层循环结束
			if(jsonStr.length()>1){//去掉最后一个逗号
				jsonStr.setLength(jsonStr.length()-1);
			}
			jsonStr.append("]");
			//System.out.println(jsonStr);
			//向form中放值
			this.getFormHM().put("isHasSaveButton", isHasSaveButton);
			this.getFormHM().put("gradeHtml", gradeHtml.toString());
			this.getFormHM().put("jsonStr", jsonStr.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
	}
	
	/////////////////////////辅助函数////////////////////////
	

	/**得到考核计划和该计划下所有考核对象的map*/

	public HashMap getPlanObjectMap(ArrayList planlist,String model){
		HashMap map = new HashMap();
		try{
			int n = planlist.size();
			for(int i=0;i<n;i++){
				CommonData vo = (CommonData) planlist.get(i);
				String planid = vo.getDataValue();
				if("0".equals(planid)){
					continue;
				}
				ArrayList objectlist = new ArrayList();
				objectlist = getObjectList(planid,model);
				map.put(planid, objectlist);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}

	/** 得到具体考核计划下的所有考核对象（还包括打分状态） 形式：00000049/3 */
	public ArrayList getObjectList(String planid,String model){
		ArrayList list=new ArrayList();
		try{
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),planid);
			ArrayList objectList=batchGradeBo.getPerplanObjects(Integer.parseInt(planid),this.userView.getA0100(),"2"/*model*/);
			for(Iterator t=objectList.iterator();t.hasNext();)
			{
				String[] temp=(String[])t.next();	
				String temporary = temp[0]+"/"+temp[2];
				list.add(temporary);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return list;		
	}
	
	/**获得所有考核计划*/
	public ArrayList getDbList(String model,String optObject){
		
		ArrayList dblist = new ArrayList();
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String perPlanSql = "select plan_id,name,status,parameter_content,object_type,template_id from per_plan where ( status=4 or status=6 ) ";
			if (!userView.isSuper_admin())
			{	
				perPlanSql += "and plan_id in (select plan_id from per_mainbody where ";
	/*			if(model!=null&&(model.equals("2")||model.equals("3")||model.equals("4")))
				{
					
				}
				else
					perPlanSql += " mainbody_id<>object_id and ";
	*/		
				perPlanSql+=" mainbody_id='"+ userView.getA0100() + "' )";
			
				if(!"USR".equalsIgnoreCase(userView.getDbname()))
					perPlanSql+=" and 1=2 ";
			}
			
			perPlanSql += " and ( Method=1 or method is null ) order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
			this.frowset = dao.search(perPlanSql);

			int i = 0;
			LoadXml loadXml=null; //new LoadXml();
			
			while (this.frowset.next()) 
			{
			/*	
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && controlByKHMoudle.equalsIgnoreCase("True"))
				{
					String template_id = this.getFrowset().getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id))					
							continue;					
					}
				}
			*/	
				String name = this.getFrowset().getString("name");
				String plan_id = this.getFrowset().getString("plan_id");
				//if(plan_id!=null && !plan_id.equals("87"))
				//if(!(plan_id.equals("225") || plan_id.equals("273")))
				//	continue;
				String object_type=this.frowset.getString("object_type");
				
				if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
				{
					
					loadXml = new LoadXml(this.getFrameconn(),plan_id);
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadXml);
				}
				else
				{
					loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
				}
				Hashtable htxml = loadXml.getDegreeWhole();
				String performanceType=(String)htxml.get("performanceType");
			//	String performanceType=loadXml.getPerformanceType(xmlContent);
	            if("1".equals(model))
	            {
	            	if(optObject!=null && "1".equals(optObject) && !("1".equals(object_type) || "3".equals(object_type) || "4".equals(object_type))){
	            		continue;
	            	}
	            	if(optObject!=null && "2".equals(optObject) && !"2".equals(object_type)){
	            		continue;
	            	}
	            }
	            
	            if(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))
	            {
	            	if("2".equals(model))
	            	{
	            		if(!"4".equals(object_type)&&!"1".equals(object_type))
	            			continue;
	            		else
	            		{
	            			CommonData vo = new CommonData(plan_id, name);
		            		dblist.add(vo);
		            		i++;
	            		}
	            	}
	            	else
	            	{
		            	int objectType=Integer.parseInt(model)-1;
		            	if(objectType!=Integer.parseInt(object_type))
		            		continue;
		            	else
		            	{
		            		CommonData vo = new CommonData(plan_id, name);
		            		dblist.add(vo);
		            		i++;
		            	}
	            	}
	            }
	            else if(model.equals(performanceType))
	            {
	            	
	            	//if(loadXml.getHandEval(xmlContent).equalsIgnoreCase("FALSE"))
	            	if("FALSE".equalsIgnoreCase((String)htxml.get("HandEval")))
	            	{
	            		CommonData vo = new CommonData(plan_id, name);
	            		dblist.add(vo);
	            		i++;
	            	}
	            }
			} //遍历完所有的计划
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return dblist;
	}
	/**得到总共有多少张打分表*/
	public int getAltogether(HashMap planObjectMap){
		int count = 0;
		Set set =  planObjectMap.keySet();
		for (Iterator t = set.iterator(); t.hasNext();){
			String key = (String) t.next();
			ArrayList objectlist = (ArrayList)planObjectMap.get(key);//得到该计划下所有考核对象
			int objectcount = objectlist.size();
			count+=objectcount;
		}
		return count;
	}
	
	
}
