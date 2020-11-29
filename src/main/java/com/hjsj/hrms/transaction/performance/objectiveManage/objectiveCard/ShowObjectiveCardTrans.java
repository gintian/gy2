package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.interview.PerformanceInterviewBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.MyObjectiveBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectiveDecisionBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ParseXmlBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ShowObjectiveCardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			 
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");//0 查询 1操作 2打分 3评价
			String planid = (String)hm.get("planid");
			Pattern pattern = Pattern.compile("[0-9]*");
	        Matcher isNum = pattern.matcher(planid);
	        //因为表单中的planid数据格式不统一，需要判断出如果是加密plan_id再解密 haosl add 2018-3-9
	        if( !isNum.matches()){
	        	planid=PubFunc.decryption(planid);
	        }
			String object_id=PubFunc.decryption((String)hm.get("object_id"));
			String model=(String)hm.get("model");
			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag",returnflag);
			
			String isShowHistoryTask=(String)this.getFormHM().get("isShowHistoryTask");  //0： 不显示历史任务 1：显示历史任务
			if(hm.get("showHistoryTask")!=null)
			{
				isShowHistoryTask="0";
				hm.remove("showHistoryTask");
			}
			this.getFormHM().put("isShowHistoryTask",isShowHistoryTask);
			
			String lt="0";
			if(hm.get("zglt")!=null)
			{
				lt=(String)hm.get("zglt");
			}
			String optDesc="";
			String txw = "0";//铁血网绩效 标记
			 String clientName=SystemConfig.getPropertyValue("clientName");
			 if(clientName==null)
			    	clientName="";
			 if("zglt".equalsIgnoreCase(clientName))
			 {
	    		if("1".equals(lt))
		    	{
		    		optDesc="目标制订";
		    	}
		    	else if("2".equals(lt))
	    		{
	     			optDesc="完成情况";
		    	}
		    	else if("3".equals(lt))
		    	{
	    			optDesc="目标审批";
	    		}
		    	else if("4".equals(lt))
		    	{
	     			optDesc="目标评分";
		    	}
			 }else if("txw".equalsIgnoreCase(clientName)){
				 txw = "1";
			 }
			 this.getFormHM().put("txw", txw);
			String body_id=(String)hm.get("body_id");
			String isCopyScore = "false";
			if(hm.get("isCopy")!=null)
			{
				isCopyScore=(String)hm.get("isCopy");
				hm.remove("isCopy");
			}
			/**   普天代办任务 id          */
			String pendingCode=(String)hm.get("pendingCode");
			if(pendingCode!=null&&pendingCode.length()>0)
			{
				this.getFormHM().put("pendingCode", pendingCode);
				PendingTask imip=new PendingTask();
				String pendingType="目标制订";
				if("2".equals(opt)|| "4".equals(opt))
					pendingType="目标评估";
				//将旧的代办信息置为已阅状态  
				imip.updatePending("P",pendingCode,2,pendingType,this.userView); 
			}
			
			// 获得唯一性指标  为了让起草状态的目标卡可以导入  郭峰
			String onlyField = "";
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			if("2".equals(model))//如果是“我的目标”
			{
				onlyField = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			}else if("1".equals(model))//如果是团队绩效
			{
				RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.frameconn);
				if(unit_code_field_constant_vo!=null)
				{
					onlyField=unit_code_field_constant_vo.getString("str_value");	
				}
				if(onlyField==null || onlyField.trim().length()<=0 || "#".equals(onlyField))
					onlyField = "b0110";
			}
			this.getFormHM().put("onlyField",onlyField);
			
			//ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),Integer.parseInt(opt),model);		
			 
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,opt); 
			//获得考核对象列表  郭峰
			ArrayList personList = bo.getObjectA0100(object_id);//把object_id变成bean的形式
			this.getFormHM().put("personList", personList);
			
			

			
			bo.setIsShowHistoryTask(isShowHistoryTask);
			boolean isFromRz=false;
			if(hm.get("fromflag")!=null&& "rz".equals(hm.get("fromflag")))
			{
				bo.setIsPerformanceShow("1");
				bo.setFromModel("rz");
				hm.remove("fromflag");
				isFromRz=true;
			}
			
			
            if("zglt".equalsIgnoreCase(clientName))
            {
            	bo.setTt4CssClassName("tt4");
            	bo.setTt3CssClassName("tt3");
            	bo.setChangeCssClassName("change");
            }
			bo.setBody_id(body_id);
			if("true".equalsIgnoreCase(isCopyScore))
			{
			//	bo.setIsCopyScore(isCopyScore);   20141204 dengcan
				bo.copyLowerGrade(Integer.parseInt(body_id),object_id, planid);
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());  
			if("True".equalsIgnoreCase((String)bo.getPlanParam().get("TaskSupportAttach"))) //任务支持附件上传
			{
				bo.executeTaskField(dao);
			}
			//生成目标卡HTML
			String html=bo.getObjectCardHtml(); 
			//opt=String.valueOf(bo.getOpt());
			
			// 查询当前计划指定主体对应的打分确认标识,用于在页面判断“确认”按钮的出现与否 by 刘蒙
			if(!"3".equals(model) && !"6".equals(model) && !"7".equals(model)) {// 3:目标制订 6:目标执行情况
				LazyDynaBean bean=bo.getMainbodyBean();
				if(bean!=null){
					String temp=(String) bean.get("body_id");
					if(temp!=null&&temp.length()>0)
						this.formHM.put("planBodyOpt", bo.getPlanBodyOpt(temp) + "");
				}			
			}
			
			MyObjectiveBo mob = new MyObjectiveBo(this.frameconn);//zzk 计划说明
			String planDescription = mob.getDescript(planid);
			planDescription=planDescription.trim().replaceAll(" ", "&nbsp;&nbsp;");//2013.11.09 pjf
			String a_code="";
			if("2".equals(String.valueOf(bo.getPlan_vo().getInt("object_type"))))
			{
				a_code="USR"+object_id;	
			}
			else
			{
				if(AdminCode.getCodeName("UM", object_id)!=null&&AdminCode.getCodeName("UM", object_id).length()>0)
				{
					a_code="UM"+object_id;
				}
				if(AdminCode.getCodeName("UN", object_id)!=null&&AdminCode.getCodeName("UN", object_id).length()>0)
				{
					a_code="UN"+object_id;
				}			
			}
			//desc+="  "+planInfo;
			
			String personalComment="";  //个人总结脚本
			if(isFromRz)
				personalComment=bo.getPersonalCommentScript(2);
			else
				personalComment=bo.getPersonalCommentScript(0);
			String personalComment2="";  //个人总结脚本
			
			if(isFromRz)
				personalComment2=bo.getPersonalCommentScript(2);
			else
				personalComment2=bo.getPersonalCommentScript(1);
			String targetDeclare="";    //指标说明脚本
			targetDeclare=bo.getTargetDeclareScript();
			
			String grade_template_id_str=",";
			ArrayList pointGradeList=(ArrayList)bo.getPointDescMap().get("per_grade_desc");
			 LazyDynaBean _abean=null;
			if(pointGradeList!=null)
			{
				for(int i=0;i<pointGradeList.size();i++)
				{
					_abean=(LazyDynaBean)pointGradeList.get(i);
					String grade_template_id=(String)_abean.get("grade_template_id");
					grade_template_id_str+=grade_template_id+",";
				}
			}
			
			String workDiaryButton_html=bo.getWordDiaryButton_html();  //显示员工日志 
			if(isFromRz)
				this.getFormHM().put("workDiaryButton_html", "");
			else
				this.getFormHM().put("workDiaryButton_html",workDiaryButton_html);
			
			String tabids = bo.getTabids(planid);
			this.getFormHM().put("isCard", "".equals(tabids.trim())?"0":"1");
			PerformanceInterviewBo ab = new PerformanceInterviewBo(this.getFrameconn());
			ArrayList tabList = ab.getTabids(planid,this.userView);
			
			if(bo.isOpenGrade_Members()){//拼写选人控件所需的json zhanghua
				ArrayList<String> p0400List=new ArrayList<String>();
				Iterator iter = bo.getP04Map().entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String)entry.getKey();
					p0400List.add(key);
				}
				JSONObject jsonObject=new JSONObject();
				if(p0400List.size()>0)
					jsonObject = JSONObject.fromObject(bo.getCardGradeMembersA0101Map(p0400List));
				
				this.getFormHM().put("objectCardGradeMembersJson",jsonObject.toString());
			}
			
			this.getFormHM().put("isRejectFunc", String.valueOf(bo.getIsRejectFunc(object_id,this.userView.getA0100())));
			this.getFormHM().put("numberMap",bo.getNumberMap());
			this.getFormHM().put("tabList", tabList);
			this.getFormHM().put("tabIDs", tabids);
			this.getFormHM().put("processing_state_all",bo.getProcessing_state_all());
			this.getFormHM().put("grade_template_id_str",grade_template_id_str);
			this.getFormHM().put("creatCard_mail", bo.getCreatCard_mail());
			this.getFormHM().put("evaluateCard_mail", bo.getEvaluateCard_mail());
			this.getFormHM().put("AllowLeaderTrace",bo.getAllowLeaderTrace());
			 
			
			this.getFormHM().put("isAllowAppealTrancePoint",bo.getIsAllowAppealTrancePoint()); //是否允许报批跟踪指标
			this.getFormHM().put("isAllowApproveTrancePoint",bo.getIsAllowApproveTrancePoint()); //是否满足条件可以批准跟踪指标
			this.getFormHM().put("noApproveTargetCanScore",bo.getNoApproveTargetCanScore()); //允许对未批准的目标卡进行评分
			
			 
			this.getFormHM().put("desc",bo.getObjectInfoHtml(clientName, optDesc));
			 
		    this.getFormHM().put("realSpFlag", bo.getRealSpFlag());
			this.getFormHM().put("personalComment", personalComment);
			this.getFormHM().put("personalComment2",personalComment2);
			this.getFormHM().put("targetDeclare",targetDeclare);
			
			this.getFormHM().put("wholeEvalScore",bo.getWholeEvalScore());   //总体评价得分
			
			String objFlag = bo.getObjectSpFlag(object_id,planid);			
			// 已报批或已批并且是目标卡代制订  JinChunhai 2013.03.19
			if("7".equals(model) && "02".equalsIgnoreCase(objFlag) && (opt!=null && opt.trim().length()>0 && "1".equalsIgnoreCase(opt)))
			{
				this.getFormHM().put("objectSpFlag",objFlag);
			}else
			{
				this.getFormHM().put("objectSpFlag",bo.getObjectSpFlag());
			}
						
			this.getFormHM().put("editOpt",bo.getMainbodyBean()!=null&& "2".equals((String)bo.getMainbodyBean().get("status"))?"0":"1");
			this.getFormHM().put("perPointNoGrade",bo.getPerPointNoGrade());
			this.getFormHM().put("noGradeItem",bo.getNoGradeItem());
			this.getFormHM().put("status",bo.get_TemplateVo().getString("status"));
			this.getFormHM().put("leafItemList", bo.getLeafItemList());
			this.getFormHM().put("plan_objectType",String.valueOf(bo.getPlan_vo().getInt("object_type")));
			this.getFormHM().put("model",model);  // 1:团对  2:我的目标   3:目标制订  4.目标评估
			this.getFormHM().put("opt", String.valueOf(bo.getOpt()));
			this.getFormHM().put("planid", planid);
			this.getFormHM().put("object_id", object_id);
			this.getFormHM().put("mdplanid",PubFunc.encryption(planid));
			this.getFormHM().put("mdobject_id",PubFunc.encryption(object_id));
			this.getFormHM().put("cardHtml",html);
			this.getFormHM().put("isEntireysub",((String)bo.getPlanParam().get("isEntireysub")).toLowerCase());
			this.getFormHM().put("allowLeadAdjustCard", ((String)bo.getPlanParam().get("allowLeadAdjustCard")).toLowerCase());
			this.getFormHM().put("body_id",body_id);
			this.getFormHM().put("planParam", bo.getPlanParam());
			this.getFormHM().put("un_functionary",bo.getUn_functionary());
			String _mainbodyScoreStatus=bo.getMainbodyBean()==null?"0":(String)bo.getMainbodyBean().get("status");
			this.getFormHM().put("mainbodyScoreStatus",bo.getMainbodyBean()==null?"0":(String)bo.getMainbodyBean().get("status"));
			
			if(bo.getPerObject_vo()!=null)
			{
				if(bo.getPerObject_vo().getString("currappuser")!=null&&bo.getPerObject_vo().getString("currappuser").length()>0)
					this.getFormHM().put("currappuser",bo.getPerObject_vo().getString("currappuser"));
				else
					this.getFormHM().put("currappuser","");
			}
			this.getFormHM().put("per_objectVo",bo.getPerObject_vo());
			this.getFormHM().put("per_planVo",bo.getPlan_vo());
			this.getFormHM().put("isApprove",bo.getIsApproveFlag());
			this.getFormHM().put("planStatus",String.valueOf(bo.getPlan_vo().getInt("status")));
			if(bo.getIsAdjustPoint())
				this.getFormHM().put("isAdjustPoint","True");
			else
				this.getFormHM().put("isAdjustPoint","False");
			//页面是否显示引入职责指标=false不显示=true显示
			if(bo.getSubSet()!=null&&!"".equals(bo.getSubSet())&&bo.getTargetPostDuty()!=null&&bo.getTargetPostDuty().size()>0&&bo.getTargetItem()!=null&&!"".equals(bo.getTargetItem()))
				this.getFormHM().put("importPositionField", "true");
			else
				this.getFormHM().put("importPositionField", "false");
			if(bo.getDeptDutySubSet()!=null&&bo.getDeptDutyTextValue()!=null&&!"".equals(bo.getDeptDutySubSet())&&!"".equals(bo.getDeptDutyTextValue()))
			{
				this.getFormHM().put("importDeptField", "true");
			}else{
				this.getFormHM().put("importDeptField", "false");
			}
			this.getFormHM().put("a_code", a_code);
			this.getFormHM().put("targetTraceEnabled", bo.getTargetTraceEnabled());
			this.getFormHM().put("targetCollectItem", bo.getTargetCollectItem());
			String targetAppMode=(String)bo.getPlanParam().get("targetAppMode"); //目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0 
			
		    String SpByBodySeq="False";
			if(bo.getPlanParam().get("SpByBodySeq")!=null)
				SpByBodySeq=(String)bo.getPlanParam().get("SpByBodySeq");
			if(("2".equals(model)|| "3".equals(model)|| "1".equals(model))&&("5".equals(body_id)|| "0".equals(body_id)|| "1".equals(body_id)|| "-1".equals(body_id)|| "-2".equals(body_id)|| "true".equalsIgnoreCase(SpByBodySeq))){
				
				ParseXmlBo pxo = new ParseXmlBo(this.getFrameconn());
				HashMap map=pxo.getObjectSpDetailInfo(planid, object_id);
				this.getFormHM().put("sp_flow", (String)map.get("detail"));
			
			}
			else
			{
				this.getFormHM().put("sp_flow", "");
			}
			if("4".equals(model)|| "1".equals(model)|| "3".equals(model)|| "2".equals(model)){//我的目标加评分过程  zhaoxg
				ParseXmlBo pxo = new ParseXmlBo(this.getFrameconn());
				HashMap map=pxo.getObjectSpDetailInfo(planid, object_id);
				this.getFormHM().put("pfopinion", (String)map.get("pfopinion"));
			}else{
				this.getFormHM().put("pfopinion", "");
			}
			this.getFormHM().put("editableTaskList", bo.getEditableTaskList());
			this.getFormHM().put("clientName", clientName);
		    
		    
		    // 出现"评分细则"按钮的限制条件  JinChunhai  2011.03.16
		    if(("1".equals(model) || "4".equals(model)) && ("2".equals(opt) || "2".equals(_mainbodyScoreStatus)))
		    {
				ObjectiveDecisionBo dbo = new ObjectiveDecisionBo(this.getFrameconn(),this.userView,planid);
				this.getFormHM().put("scoreManual",dbo.ScoreManual(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,opt));	
		    }
		    else
		    {
		    	this.getFormHM().put("scoreManual","false");
		    }
		    
		    //得到引入上级目标卡的考核对象字符串
		    if(("1".equals(model) || "2".equals(model))&& "1".equals(opt)&&(this.userView.hasTheFunction("06070212")||this.userView.hasTheFunction("06070110")||this.userView.hasTheFunction("06070213")||this.userView.hasTheFunction("06070111")) )
		    {
			    body_id="1";  //1：直接上级 
				String posID="";
				LazyDynaBean functionary=bo.getMainbodyBean(planid,object_id);
				if("1".equals(targetAppMode))
					posID=this.userView.getUserPosId();
				String aObject_id=object_id;
		//		HashMap teamKhObjs = new HashMap(); 
				ArrayList appealObjectList=bo.getAppealObjectInfo(body_id, "Usr", aObject_id, planid, posID, targetAppMode,object_id); 
				String appealObjectStr="";
				appealObjectStr=getAppealObjectStr(appealObjectList,bo.getPlan_vo(),bo.getTemplate_vo());
				 
				// planid和objectid加密: appealObjectStr = &#&emp^海淀烟草专卖局^营销网建科^营销网建科长^杨振国^00000096/1/224 lium
				String[] _tmp = appealObjectStr.split("\\^");
				StringBuffer _appealObjectStr = new StringBuffer(); // 部分加密后的appealObjectStr
				if (_tmp.length == 6) {
					_appealObjectStr.append(_tmp[0]).append("^").append(_tmp[1]).append("^")
							.append(_tmp[2]).append("^").append(_tmp[3]).append("^")
							.append(_tmp[4]).append("^");
					String _str = _tmp[5]; // 00000096/1/224
					String[] _ids = _str.split("/");
					if (_ids.length == 3) {
						String encrypted_objectId = PubFunc.encryption(_ids[0]);
						String encrypted_planId = PubFunc.encryption(_ids[2]);

						_appealObjectStr.append(encrypted_objectId).append("/").append(_ids[1])
								.append("/").append(encrypted_planId);
					}
				}

				String _str = _appealObjectStr.toString();
				if (_str.length() > 0) {
					this.getFormHM().put("appealObjectStr", _str.substring(3));
				} else {
					this.getFormHM().put("appealObjectStr", _str.toString());
				}
				
				/** 改用上面加密后的数据 lium
				if(appealObjectStr.length()>0)
					this.getFormHM().put("appealObjectStr", appealObjectStr.substring(3));
				else
					this.getFormHM().put("appealObjectStr",appealObjectStr.toString());
				*/	    
		    }
		    else
		    	this.getFormHM().put("appealObjectStr","");
		    if("3".equals(opt)){
		    	this.getFormHM().put("seqCondition","false");
		    }else
		    	this.getFormHM().put("seqCondition", bo.getSeqCondition());
		    this.getFormHM().put("includeOperateCloumn", bo.getIncludeOperateCloumn());
		    this.getFormHM().put("planDescription",planDescription);
		    	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取查看上级目标卡的信息
	 * @param appealObjectList
	 * @param _planVo
	 * @param _templateVo
	 * @return
	 */
	public String getAppealObjectStr(ArrayList appealObjectList,RecordVo _planVo,RecordVo _templateVo)
	{
		StringBuffer _str=new StringBuffer("");
		try
		{
			if(appealObjectList.size()==0)
				return _str.toString();
			StringBuffer obj_str=new StringBuffer("");
			for(int i=0;i<appealObjectList.size();i++)
			{
				String obj = (String)appealObjectList.get(i);
				String[] x = obj.replaceAll("／", "/").split("/");
				String[] y = x[0].split("\\^");
				String _object_id=y[4];
				obj_str.append(",'"+_object_id+"'");
			}
			ContentDAO dao = new ContentDAO(this.frameconn);  
			String where_str="";
			int cycle=_planVo.getInt("cycle");
			if(cycle==0)
			{
				where_str=" and Theyear='"+_planVo.getString("theyear")+"' ";
			}
			else if(cycle==1||cycle==2)
			{
				where_str=" and Theyear='"+_planVo.getString("theyear")+"' and Thequarter='"+_planVo.getString("thequarter")+"' ";
			} 
			else if(cycle==3)
			{
				where_str=" and Theyear='"+_planVo.getString("theyear")+"' and themonth='"+_planVo.getString("themonth")+"' ";
			}
			else if(cycle==7)
			{
				Date s_dd=_planVo.getDate("start_date");
				Calendar s_c=Calendar.getInstance();
				s_c.setTime(s_dd);
				Date e_dd=_planVo.getDate("end_date");
				Calendar e_c=Calendar.getInstance();
				e_c.setTime(e_dd); 
				where_str=" and Theyear='"+_planVo.getString("theyear")+"' and "+Sql_switcher.year("start_date")+"="+s_c.get(Calendar.YEAR)+" and "+Sql_switcher.month("start_date")+"="+(s_c.get(Calendar.MONTH)+1);		
				where_str+=" and "+Sql_switcher.year("end_date")+"="+e_c.get(Calendar.YEAR)+" and "+Sql_switcher.month("end_date")+"="+(e_c.get(Calendar.MONTH)+1);		
			}
			
			RowSet rowSet=dao.search("select * from per_plan where plan_id="+_planVo.getInt("plan_id")); // method=2 and cycle="+cycle+" and template_id='"+_planVo.getString("template_id")+"' "+where_str+" and Status<>0  and Status<>1  and Status<>2");
			RowSet rowSet2=null;
			HashMap map=new HashMap();
			while(rowSet.next())
			{
				int plan_id=rowSet.getInt("plan_id");
				int object_type=rowSet.getInt("object_type");
				if(object_type==2)
				{
					String sql="select object_id from per_object where plan_id="+plan_id+" and ( sp_flag='03' or object_id in (select object_id from per_mainbody where plan_id="+plan_id+" and ( status=1 or status=2 ) )    ) ";
						   sql+=" and object_id in ("+obj_str.substring(1)+") ";
					rowSet2=dao.search(sql);
					while(rowSet2.next())
					{	
						if(map.get(rowSet2.getString(1))==null)
						{
							map.put(rowSet2.getString(1), "");		 
							for(int i=0;i<appealObjectList.size();i++)
							{
								String obj = (String)appealObjectList.get(i);
								String[] x = obj.split("/");
								String[] y = x[0].split("\\^");
								String _object_id=y[4];
								if(_object_id.trim().equalsIgnoreCase(rowSet2.getString(1)))
									_str.append("&#&emp^"+(String)appealObjectList.get(i)+"/"+plan_id);	
							}
						}
					} 
					 
				}else 
				{
					String sql="select per_object.object_id,per_object.a0101,per_mainbody.mainbody_id from per_object,per_mainbody where per_object.plan_id="+plan_id+" and ( per_object.sp_flag='03' or per_object.object_id in (select object_id from per_mainbody where plan_id="+plan_id+" and ( status=1 or status=2 ) )  ) ";
					       sql+=" and per_object.object_id=per_mainbody.object_id and  per_mainbody.plan_id="+plan_id+" and per_mainbody.body_id=-1 and mainbody_id in ("+obj_str.substring(1)+") ";
					rowSet2=dao.search(sql);
					while(rowSet2.next())					
					{
						if(map.get(rowSet2.getString(1))==null)
						{
							map.put(rowSet2.getString(1), "");		 
							for(int i=0;i<appealObjectList.size();i++)
							{
								String obj = (String)appealObjectList.get(i);
								String[] x = obj.split("/");
								String[] y = x[0].split("\\^");
								String _object_id=y[4];
								if(_object_id.trim().equalsIgnoreCase(rowSet2.getString(3)))
									_str.append("&#&team^"+rowSet2.getString(1)+"^"+rowSet2.getString(2)+"^"+y[3]+"/1/"+plan_id);		
							}
						} 	
					}
				}
				
				
			}
			
			if(rowSet!=null)
				rowSet.close();
			if(rowSet2!=null)
				rowSet2.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return _str.toString();
	}
	
	
	
	
	
	
}
