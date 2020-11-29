package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkSummaryMethodBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 发送邮件，提醒写周报
 * 
 * @author guoby
 *
 */
public class SendEmailTrans extends IBusiness {
    private String type;
    private String summaryCycle;
    private String summaryYear;
    private String summaryMonth;
    private String summaryWeek;
    private String remindType;
    private String isHR;
    
    private WorkPlanSummaryBo workPlanSummaryBo;

    public void execute() throws GeneralException {

        try {
            workPlanSummaryBo = new WorkPlanSummaryBo(this.userView, this.frameconn);
            
            isHR = (String) this.getFormHM().get("isHR");
            type = (String) this.getFormHM().get("type");
            remindType = (String) this.getFormHM().get("remindType");
            summaryCycle = (String) this.getFormHM().get("cycle");
            summaryYear = (String) this.getFormHM().get("year");
            summaryMonth = (String) this.getFormHM().get("month");
            summaryWeek = (String) this.getFormHM().get("week");
            if(StringUtils.isEmpty(summaryMonth) || StringUtils.isEmpty(summaryWeek))
            	return;
            
            String[] summaryDates = workPlanSummaryBo.getSummaryDates(summaryCycle, summaryYear, summaryMonth, Integer.parseInt(summaryWeek));
            // 设置标题，内容
            String topic = "提醒写工作总结";
            String stateSign="p011501"; 
            if ("contents".equals(remindType))
			{
				topic = "工作总结评价提醒";
				stateSign = "hrscoreremind";
			}else if ("approve".equals(remindType))
			{
				topic = "工作总结批准提醒";
				stateSign = "p011503";
			}
			else if ("publish".equals(remindType))
			{
				topic = "工作总结审批提醒";
			}else if("reject".equals(remindType))	{
				topic = "工作总结驳回提醒";
			}
			if ("team".equalsIgnoreCase(type))
				type = "person";
			else if ("sub_org".equalsIgnoreCase(type))
				type = "org";
            // 获取人员编号a0100
            StringBuffer acceptA0100 = new StringBuffer((String) this.getFormHM().get("a0100"));
            StringBuffer acceptE0122 = new StringBuffer((String) this.getFormHM().get("e0122"));
            //hr全部提醒时需要从后台查人
            if ("HR".equals(acceptA0100.toString())) {
            	acceptA0100.setLength(0);
            	WorkSummaryMethodBo wsmBo = new WorkSummaryMethodBo(this.userView, this.getFrameconn());
    			ArrayList list = new ArrayList();
    			String para = (String) this.getFormHM().get("para");
    			para=(para==null)?"":para;  
    			para =SafeCode.decode(para);
    			//联合查询条件
    			String commonpara = (String) this.getFormHM().get("commonpara");
    			commonpara = (commonpara==null)?"":commonpara;  
    			//commonpara = SafeCode.decode(commonpara);
    			if ("person".equalsIgnoreCase(type))
    				list = wsmBo.selectTeamWeekly(this.userView.getDbname(), this.userView.getA0100(), summaryDates[0], summaryDates[1], summaryCycle, stateSign,para,commonpara , true,"");
    			else if ("org".equalsIgnoreCase(type))
    			{
    				ArrayList e01a1list = new ArrayList();
    				e01a1list = wsmBo.getHre01a1list(para,commonpara);
    				list = wsmBo.getMySubDeptPerson(e01a1list, summaryDates[0], summaryDates[1], summaryCycle,stateSign,true);
    			}
    			for (int i = 0; i < list.size(); i++) {
    				HashMap map = (HashMap) list.get(i);//获取人员信息
    				acceptA0100.append(map.get("nbaseA0100")+",");
    				if ("org".equalsIgnoreCase(type))
    				acceptE0122.append(map.get("e0122")+",");
				}
			}

            String[] arrayA0100 = {};
            if( acceptA0100.toString().length() > 0 )	
            arrayA0100 = acceptA0100.toString().split(",");
            
            String[] arrayE0122 = acceptE0122.toString().split(",");
            AsyncEmailBo wpEmail = new AsyncEmailBo(this.frameconn, this.userView);
            
            ArrayList emails = new ArrayList();
            ArrayList pendings = new ArrayList();
            for (int i = 0; i < arrayA0100.length; i++) {
            	String usrA0100 = WorkPlanUtil.decryption(arrayA0100[i]);
            	 if (!"HR".equals(isHR) && "publish".equals(remindType) )
                 {	
                 	  WorkPlanUtil workPlanUtil=new WorkPlanUtil(this.frameconn,userView);
                 	  String superid=workPlanUtil.getMyDirectSuperPerson(usrA0100.substring(0, 3),usrA0100.substring(3));
                       if (superid!=null && !"".equals(superid)){
                           usrA0100 = superid ;
                       }  
                 }
                String[] aMan = splitNbaseA0100(usrA0100);
                if("HR".equals(isHR) && !"".equals(remindType))
                {
                	//usrA0100=workPlanSummaryBo.getSuper(aMan[0], aMan[1]);
                	WorkPlanUtil workPlanUtil=new WorkPlanUtil(this.frameconn,userView);
                	usrA0100=workPlanUtil.getMyApprovedSuperPerson(aMan[0], aMan[1]);
                	if ("".equals(usrA0100)) {
						//即没有上级
                		break;
					}
                	aMan = splitNbaseA0100(usrA0100);
                	
                }
                String e0122sString= "";
                if ("org".equalsIgnoreCase(type))
                	e0122sString = arrayE0122[i];
                String bodyText = getEmailBodyText(aMan[0], aMan[1], summaryDates ,WorkPlanUtil.decryption(arrayA0100[i]));
                String href = getSummaryHref(aMan[0], aMan[1], type ,WorkPlanUtil.decryption(arrayA0100[i]),e0122sString);
                String subject = topic;
                if((!"HR".equals(isHR) &&!"".equals(remindType)) || "publish".equals(remindType)){
                	subject= getEmailSubject(aMan[0], aMan[1], summaryDates ,WorkPlanUtil.decryption(arrayA0100[i]));
                }
                LazyDynaBean email = new LazyDynaBean();
                email.set("subject", subject);
                email.set("objectId", usrA0100);
                email.set("href", href);
                email.set("bodyText", bodyText);
                email.set("bodySubject",topic);
                String hrefDesc = "去填写总结";
                if ("contents".equals(remindType) || "approve".equals(remindType) || "publish".equals(remindType) || "reject".equals(remindType))
    			{
                	hrefDesc = "去查看总结";
    			}
                email.set("hrefDesc", hrefDesc);
                
                emails.add(email);                
                
                String infostr = "reject".equals(remindType)?"(驳回)":"(审批)";
                // 发布总结增加待办 chent 20170320
                StringBuilder pendingSubject  = new StringBuilder();
                if("reject".equals(remindType)) {
                	pendingSubject  = new StringBuilder(subject.substring(subject.indexOf("您的")).replaceAll("，", "").replaceAll("请查看", infostr));
                }else {
                	pendingSubject  = new StringBuilder(subject.replaceAll("已经发布了", "").replaceAll("，", "").replaceAll("请批准", infostr));
                	
                }
               
                int _index = pendingSubject.indexOf("(");
                int index_ = pendingSubject.indexOf(")");
                //haosl 2017-07-27
                if(_index>-1 && index_>-1) 
                	pendingSubject.delete(_index, index_+1);
                
                LazyDynaBean pending = new LazyDynaBean();
                pending.set("subject", pendingSubject.toString());
                pending.set("objectId", usrA0100);
                pending.set("href", getSummaryHrefForPending(aMan[0], aMan[1], type ,WorkPlanUtil.decryption(arrayA0100[i]),e0122sString));
                String p0100 = (String) this.getFormHM().get("p0100");
                if(StringUtils.isNotEmpty(p0100)){
                	pending.set("p0100", WorkPlanUtil.decryption(p0100));
                }
                pendings.add(pending);
            }
            
            if (emails.size() > 0){
            	wpEmail.send(emails);
            	WorkPlanUtil workPlanUtil = new WorkPlanUtil(this.frameconn,this.userView);
            	workPlanUtil.sendWeixinMessageFromEmail(emails);            	
            	// 发布总结增加待办 chent 20170320 start
            	if( "publish".equals(remindType)){
            		workPlanUtil.sendPending_publishSummary(pendings);
            	}else if("contents".equals(remindType)){//haosl add 【32101】 陈总提：周报有的批过，怎么还在待办任务列表中？如：志强，李群，关瑞的周报
            		workPlanUtil.updatePending_approveSummary("1",pendings);
            	}else if("approve".equals(remindType)){
            		workPlanUtil.updatePending_approveSummary("1", pendings);
            	}else if("reject".equals(remindType)){
            		workPlanUtil.sendPending_rejectSummary(pendings);
            	}
            	// 发布总结增加待办 chent 20170320 end
            }

            // 返回信息，提醒用户
            this.getFormHM().put("msg", "邮件发送成功！");
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private String[] splitNbaseA0100(String nbaseA0100) {
        String nbase = nbaseA0100.substring(0, 3);
        String a0100 = nbaseA0100.substring(3);
        String[] aMan = {nbase, a0100};
        return aMan;
    }
    
    private String getSummaryHref(String nbase, String a0100, String type, String olda0100,String e0122s) {
        // 获取etoken的值
        AttestationUtils attestUtils = new AttestationUtils();
        String etoken = attestUtils.getetoken(nbase, a0100, this.frameconn);
        String[] aMan = splitNbaseA0100(olda0100);
        StringBuffer url = new StringBuffer();
        url.append(this.userView.getServerurl());
        url.append("/workplan/work_summary.do?b_query=link&type="+type);
        String belong_type = "0";
        if ("org".equals(type)) {
        	belong_type = "2";
		}
        url.append("&belong_type=" + belong_type);
        url.append("&cycle=" + this.summaryCycle);
        url.append("&year=" + this.summaryYear);
        url.append("&month=" + this.summaryMonth);
        url.append("&week=" + this.summaryWeek);
        url.append("&e0122=" + e0122s);
        url.append("&isemail=true");
        if ("HR".equals(isHR)
        		||"publish".equals(remindType)) {
        	url.append("&nbase=" + WorkPlanUtil.encryption(aMan[0]));
        	url.append("&a0100=" + WorkPlanUtil.encryption(aMan[1]));
		}
        url.append("&appfwd=1&etoken=" + etoken);
        
        return url.toString();
    }
    private String getSummaryHrefForPending(String nbase, String a0100, String type, String olda0100,String e0122s) {
    	// 获取etoken的值
    	AttestationUtils attestUtils = new AttestationUtils();
    	String etoken = attestUtils.getetoken(nbase, a0100, this.frameconn);
    	String[] aMan = splitNbaseA0100(olda0100);
    	StringBuffer url = new StringBuffer();
    	// 工作总结的待办链接删除ip信息:1、与工作计划一致 2、加了ip后可能引起iframe报错  chent 20171026 delete start 
    	// url.append(this.userView.getServerurl());
    	// 工作总结的待办链接删除ip信息:1、与工作计划一致 2、加了ip后可能引起iframe报错  chent 20171026 delete end
    	url.append("/workplan/work_summary.do?b_query=link&type="+type);
    	String belong_type = "0";
    	if ("org".equals(type)) {
    		belong_type = "2";
    	}
    	url.append("&belong_type=" + belong_type);
    	url.append("&cycle=" + this.summaryCycle);
    	url.append("&year=" + this.summaryYear);
    	url.append("&month=" + this.summaryMonth);
    	url.append("&week=" + this.summaryWeek);
    	url.append("&e0122=" + e0122s);
    	if ("HR".equals(isHR)
    			||"publish".equals(remindType)) {
    		url.append("&nbase=" + WorkPlanUtil.encryption(aMan[0]));
    		url.append("&a0100=" + WorkPlanUtil.encryption(aMan[1]));
    	}
    	url.append("&appfwd=1&etoken=" + etoken);
    	
    	return url.toString();
    }
    /**
     * 获取周报的内容 
     * @param string 
     * @param acceptA0100
     * @return
     */
    private String getEmailBodyText(String nbase, String a0100, String[] summaryDates, String olda0100) {
        // 获取用户信息
        NewWorkPlanBo newWorkplanBo = new NewWorkPlanBo(frameconn, userView);
        String userName = newWorkplanBo.getUserNameByCode(nbase+a0100);

        StringBuffer buf = new StringBuffer();
        buf.append(userName + "，您好：<br /><br />");
        String messString = "";
 
	    if(!"HR".equals(isHR)){
	        	messString = "&nbsp;&nbsp;&nbsp;&nbsp;请填写并提交";
	        if ("contents".equals(remindType))
			{
	        	messString=userView.getUserFullName()+"已经评价了";
			}else if ("approve".equals(remindType))
			{
				messString = userView.getUserFullName()+"已经批准了";
			}else if("reject".equals(remindType)) {
				messString = userView.getUserFullName()+"已经驳回了";
			}else if("publish".equals(remindType)) {
				messString = userView.getUserFullName()+"已经发布了";
			}
	        buf.append(messString);
	        if(!"publish".equals(remindType)) {
		        if ("person".equals(type) || "team".equals(type))
		            buf.append("您的");
		        else
		            buf.append("部门");
	        }
	    }else {
	    	//通过总结监控发送提醒加人事提醒的提示以做区分  haosl 2018-6-29
	    	buf.append("【人事提醒】"+newWorkplanBo.getUserNameByCode(olda0100));
	    	buf.append("已经发布了");
		}
        buf.append(workPlanSummaryBo.getSummaryCycleDesc(summaryCycle, summaryYear, summaryMonth, summaryWeek));
        buf.append("(");
        buf.append(summaryDates[0].substring(5).replace('-', '月')+"日");
        buf.append("-");
        buf.append(summaryDates[1].substring(5).replace('-', '月')+"日");
        buf.append(")");
        if("HR".equals(isHR) && "org".equals(type))
        	buf.append("部门");
        buf.append("的工作总结。");
        if ("HR".equals(isHR)) {
        	buf.append("请批准或评价其工作总结。");
		}
  
        return buf.toString();
    }
    
    private String getEmailSubject(String nbase, String a0100, String[] summaryDates, String olda0100) {
        // 获取用户信息
        StringBuffer buf = new StringBuffer();
        PlanTaskBo planTaskBo = new PlanTaskBo(frameconn, getUserView());
        if(!"".equals(remindType)){
	    	String messString="";
	        if ("contents".equals(remindType))
			{
	        	messString=userView.getUserFullName()+"已经评价了";
			}else if ("approve".equals(remindType))
			{
				messString = userView.getUserFullName()+"已经批准了";
			}else if ("reject".equals(remindType))
			{
				messString = userView.getUserFullName()+"已经驳回了";
			}
			else {
				messString =("HR".equals(isHR)?"【人事提醒】":"")+planTaskBo.getA0101(olda0100)+"已经发布了";
			}
	        buf.append(messString);
	        
	        if ("contents".equals(remindType)
	        		|| "reject".equals(remindType))
			{
	        	   if ("person".equals(type) || "team".equals(type))
	   	            buf.append("您的");
	   	        else
	   	            buf.append("部门");
			}else if ("approve".equals(remindType))
			{
				   if ("person".equals(type) || "team".equals(type))
			            buf.append("您的");
			        else
			            buf.append("部门");
			}
			else {
				
			}
	     
	    }else {
	  
		}
        buf.append(workPlanSummaryBo.getSummaryCycleDesc(summaryCycle, summaryYear, summaryMonth, summaryWeek));
        buf.append("(");
        buf.append(summaryDates[0].substring(5).replace('-', '月')+"日");
        buf.append("-");
        buf.append(summaryDates[1].substring(5).replace('-', '月')+"日");
        buf.append(")");
        if("HR".equals(isHR) && "org".equals(type))
        	buf.append("部门");
        buf.append("的工作总结，");
        if ("contents".equals(remindType)
        		||"reject".equals(remindType))
		{
        	buf.append("请查看");
		}else if ("approve".equals(remindType))
		{
			buf.append("请查看");
		}
		else {
			buf.append("请批准");
		}
    
        return buf.toString();
    }

}
