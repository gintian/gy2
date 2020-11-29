package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.Md5ForHire;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.Counter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SeachPositionListTrans extends IBusiness{
	public void execute() throws GeneralException {
		ContentDAO dao  = new ContentDAO(this.getFrameconn());
		
		try {
			HttpSession session=(HttpSession)this.getFormHM().get("session");
			if(this.userView==null&&session!=null)
				this.userView = (UserView) session.getAttribute(WebConstant.userView);
			if (this.getFormHM().get("selunitcode") != null) {
			    ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
	            HashMap map=xmlBo.getAttributeValues();
	            String unitLevel = "";
	            if(map != null && map.get("unitLevel") != null)
	                unitLevel = (String) map.get("unitLevel");
	            
			    String hireChannel = (String)this.getFormHM().get("hireChannel");
			    String selunitcode = (String)this.getFormHM().get("selunitcode");
			    EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.getFrameconn(),"0");
			    employNetPortalBo.setHireChannel(hireChannel);
			    if(this.userView != null)
			        employNetPortalBo.setLoginUserName(this.userView.getUserName());
			    ArrayList conditiontemp = (ArrayList)this.getFormHM().get("conditiontemp");  
			    ArrayList conditionFieldList = new ArrayList();
			    for(int i=0;i<conditiontemp.size();i++)
	            {
			    	LazyDynaBean labean = new LazyDynaBean();
			    	MorphDynaBean abean = (MorphDynaBean)conditiontemp.get(i);
	                if(abean == null)
	                    continue;
	                
	                String itemid = (String)abean.get("itemid");
	                labean.set("itemid", PubFunc.hireKeyWord_filter(itemid));
	                
	                String value = PubFunc.getReplaceStr((String)abean.get("value"));
	                labean.set("value", PubFunc.hireKeyWord_filter(value));
	                
	                String viewvalue = PubFunc.getReplaceStr((String)abean.get("viewvalue"));
	                labean.set("viewvalue", PubFunc.hireKeyWord_filter(viewvalue));
	                
	                String type = (String)abean.get("itemtype");
	                labean.set("itemtype", PubFunc.hireKeyWord_filter(type));
	                
	                String codesetid = (String)abean.get("codesetid");
	                labean.set("codesetid", PubFunc.hireKeyWord_filter(codesetid));
	                conditionFieldList.add(labean);
	            }
			    ArrayList kunitList = new ArrayList();
                HashMap unitPosMap = employNetPortalBo.getPositionInterviewMap3(selunitcode,conditionFieldList,kunitList,hireChannel,unitLevel);
                ArrayList posList = (ArrayList)unitPosMap.get(selunitcode);
                this.getFormHM().put("poslist", posList==null?new ArrayList():posList);
                this.getFormHM().put("unitid", selunitcode);
                this.getFormHM().put("posFieldList", employNetPortalBo.getPosListField());
                return;
			}

			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b_autoforward=(String) hm.get("b_autoforward");//判断是否是激活邮件自动跳转
			String loginName=(String) hm.get("loginName") == null ? "" : (String) hm.get("loginName");
			String va=(String) hm.get("validate");//判断是否从servlet验证传来
			if((b_autoforward!=null&& "forward".equalsIgnoreCase(b_autoforward))||(va!=null&& "true".equalsIgnoreCase(va))
					||(this.getFormHM().get("a0100")!=null&&!"".equals((String)this.getFormHM().get("a0100")))){
				hm.remove("b_autoforward");
				hm.remove("validate");
			}else{
				this.getFormHM().put("loginName", "");
				this.getFormHM().put("password", "");
			}


            
            String chl_id=(String)hm.get("chl_id");
            String allok=(String)hm.get("isAllPosOk");
            String operate=null;
            String abcd="0";
            String returnType=(String)hm.get("returnType");
            String z0301=(String)hm.get("z0301");
            if(z0301!=null&&z0301.trim().length()>0){
            String rr[] = z0301.split("%");
            
            for(int z=1;z<rr.length;z++){
                if(z==1)
                 z0301 = "";    
                 z0301+="~"+rr[z];
            }
            }
            if(z0301!=null&&z0301.trim().length()>0){
                z0301=SafeCode.decode(z0301);
            }
            String resume=(String)hm.get("resume");
            hm.remove("returnType");
            hm.remove("search");
            if(hm.get("abcd")!=null)
            {
                abcd=(String)hm.get("abcd");
                hm.remove("abcd");
            }
            ArrayList conditionFieldList=null;
            if(hm!=null)
            {
                operate=(String)hm.get("operate");
                String b_query=(String)hm.get("b_query");
                if(b_query==null|| "link0".equals(b_query))
                {
                    operate="init";
                    Counter counter=new Counter(this.getFrameconn(),this.userView);
                    counter.saveCount();
                }
                hm.remove("operate");
            }
            
            ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
            HashMap map=xmlBo.getAttributeValues();
            String acountBeActived="0";//注册帐号需通过邮箱激活才生效
            if(map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0)
                acountBeActived=(String)map.get("acountBeActived");
            String failedTime="3";//最大登录失败次数
            if(map.get("failedTime")!=null&&((String)map.get("failedTime")).length()>0)
                failedTime=(String)map.get("failedTime");
            String unlockTime="60";//解锁时间间隔
            if(map.get("unlockTime")!=null&&((String)map.get("unlockTime")).length()>0)
                unlockTime=(String)map.get("unlockTime");
            String complexPassword="";//0:不使用 1：使用
            String passwordMinLength="";//参数定义密码最小长度
            String passwordMaxLength="";//参数定义密码最大长度
            if(map.get("complexPassword")!=null&&((String)map.get("complexPassword")).length()>0)
                complexPassword=(String)map.get("complexPassword");
            if(map.get("passwordMinLength")!=null&&((String)map.get("passwordMinLength")).length()>0)
                passwordMinLength=(String)map.get("passwordMinLength");
            if(map.get("passwordMaxLength")!=null&&((String)map.get("passwordMaxLength")).length()>0)
                passwordMaxLength=(String)map.get("passwordMaxLength");
            
            String isAttach="0";
            if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
                isAttach=(String)map.get("attach");
            EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			
			if(-1 == EmployNetPortalBo.cacheDataTimeOutMins) {
				EmployNetPortalBo.cacheDataTimeOutMins = 60;
				try {
		        	String cacheDataTimeOutMins = SystemConfig.getPropertyValue("zp_cache_timeout");
		        	if(StringUtils.isNotEmpty(cacheDataTimeOutMins)) {
		        		int mins = Integer.parseInt(cacheDataTimeOutMins);
		        		//EmployNetPortalBo.cacheDataTimeOutMins 为缓存数据更新时间，启动程序时值默认为-1，点击校园招聘或社会招聘时，从system.properties
		        		//中取zp_cache_timeout的值，如果取到并且是非负整数，则将其认为是缓存数据更新的间隔时间，否则缓存数据间隔时间更改为60分钟
		        		if (mins > -1)
		        			EmployNetPortalBo.cacheDataTimeOutMins = mins;
		        		
		        	}
	        	} catch (Exception e) {
	        		
				}
	        	
	        }
			
            String hireChannel="";
			boolean isregetdata=false;
            if(hm.get("hireChannel")!=null)
            {
                isregetdata=true;
                hireChannel=(String)hm.get("hireChannel");
                hm.remove("hireChannel");
            }else
            {
                if(this.getFormHM().get("hireChannel")!=null)
                {
                    hireChannel=(String)this.getFormHM().get("hireChannel");
                }
            }
            hireChannel = PubFunc.hireKeyWord_filter(hireChannel);
            hireChannel=PubFunc.getReplaceStr(hireChannel);
            if(StringUtils.isEmpty(hireChannel)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
				hireChannel = employNetPortalBo.getHireChannelFromTable();
            
            employNetPortalBo.setHireChannel(hireChannel);
            
            if(operate==null)
            {
                conditionFieldList = (ArrayList)this.getFormHM().get("conditionFieldList");
                
            } else {
                if(!isregetdata)
                    conditionFieldList=employNetPortalBo.getPosQueryConditionList(hireChannel,"pos_query");
            }
            
            if(isregetdata)
            {
                conditionFieldList=employNetPortalBo.getPosQueryConditionList(hireChannel,"pos_query");
            }
            
            if(allok!=null&& "1".equalsIgnoreCase(allok)){
                conditionFieldList=(ArrayList)this.getFormHM().get("conditionFieldList");
            }
            String logonName = (String) this.getFormHM().get("loginName");
            if((logonName==null|| "".equals(logonName))&&"headHire".equals(hireChannel)){//当是猎头招聘,并且未登录时不出现快速查询的相关内容
                conditionFieldList = new ArrayList();
            }
            
            if(this.userView != null)
            	logonName = this.userView.getUserName();
            
            if(!(logonName==null|| "".equals(logonName))){
                employNetPortalBo.setLoginUserName(logonName);
            }
            //conditionFieldList  快速查询的指标 
            //zxj 20141231 对conditionFieldList中的数据进行安全过滤，防止跨站脚本等攻击
            for(int i=0;i<conditionFieldList.size();i++)
            {
                LazyDynaBean abean = (LazyDynaBean)conditionFieldList.get(i);
                if(abean == null)
                    continue;
                
                String itemid = (String)abean.get("itemid");
                abean.set("itemid", PubFunc.hireKeyWord_filter(itemid));
                
                String value = PubFunc.getReplaceStr((String)abean.get("value"));
                abean.set("value", PubFunc.hireKeyWord_filter(value));
                
                String viewvalue = PubFunc.getReplaceStr((String)abean.get("viewvalue"));
                abean.set("viewvalue", PubFunc.hireKeyWord_filter(viewvalue));
                
                String type = (String)abean.get("itemtype");
                abean.set("type", PubFunc.hireKeyWord_filter(type));
                
                String codesetid = (String)abean.get("codesetid");
                abean.set("codesetid", PubFunc.hireKeyWord_filter(codesetid));
            }
            
            String hire_object=(String)map.get("hire_object");//招聘对象指标
            if(hire_object==null||hire_object.length()==0)
            {
                throw new GeneralException("对不起,系统正在维护中...");
            }
            
            String dbName=employNetPortalBo.getZpkdbName();
            String zpUnit ="";
            if(hm.get("zpUnitCode")!=null)
            {
                zpUnit = (String)hm.get("zpUnitCode");
                zpUnit = PubFunc.hireKeyWord_filter(zpUnit);
            }
            
            String zpUnitCode="";
            if(hm.get("zpUnitCode")!=null)
            {
                zpUnitCode=(String)hm.get("zpUnitCode");
                hm.remove("zpUnitCode");
            }
            else if(this.getFormHM().get("zpUnitCode")!=null)
            {
                zpUnitCode=(String)this.getFormHM().get("zpUnitCode");
            }
            zpUnitCode = PubFunc.hireKeyWord_filter(zpUnitCode);
            String unitLevel = "";
            if(map != null && map.get("unitLevel") != null)
                unitLevel = (String) map.get("unitLevel");
            
            ArrayList kunitList=new ArrayList();//存放有那些有岗位招聘人员的单位ID
            ArrayList sunitList  = new ArrayList();
            
            if(returnType!=null&&returnType.length()!=0&& "search".equalsIgnoreCase(returnType)){//搜索进来
                String dmlunitcode=(String)hm.get("selunitcode");
				HashMap unitPosMap=employNetPortalBo.getPositionInterviewMap3(zpUnitCode,conditionFieldList,kunitList,hireChannel,unitLevel);
                sunitList  =employNetPortalBo.getUnitList(unitPosMap,kunitList,unitLevel);//如果是岗位搜索的话 只显示搜索的岗位
                String selunit=(String)this.getFormHM().get("selunit");
                if(dmlunitcode!=null){
                    if(selunit!=null&&selunit.length()!=0&&selunit.indexOf(",")!=-1){
                        if(selunit.indexOf(dmlunitcode)!=-1){
                        
                        }else
                            selunit=selunit+dmlunitcode+",";
                    }else{
                        selunit=","+dmlunitcode+",";
                    }
                    this.getFormHM().put("selunit",selunit);
                }else{
                    this.getFormHM().put("selunit", "");
                }
                
            }else{
                this.getFormHM().put("selunit", "");
            }
            hm.remove("selunitcode");
            ArrayList unitList  = new ArrayList();//存放的是左侧组织机构树
            unitList  =employNetPortalBo.getAllZpUnitList(hireChannel, unitLevel);
            //zpUnitCode=(String)((LazyDynaBean)unitList.get(0)).get("codeitemid");
            //if(zpUnitCode==null||zpUnitCode.equals("")&&unitList!=null&&unitList.size()>0)
            //{
            //zpUnit用来处理切换社会招聘和校园招聘时单位介绍不改变
            if("".equals(zpUnit)&&unitList!=null&&unitList.size()>0)
            {
                zpUnitCode=(String)((LazyDynaBean)unitList.get(0)).get("codeitemid");
                    
            }else{
                /*if(unitList!=null&&unitList.size()>0){
                    boolean type=false;
                    for(int i=0;i<kunitList.size();i++){
                        String uni=(String)kunitList.get(i);
                        if(zpUnitCode.equalsIgnoreCase(uni)){
                            type=true;
                            break;
                        }else{
                            continue;
                        }
                    }
                    if(!type){
                        LazyDynaBean bean = (LazyDynaBean)unitList.get(0);
                        zpUnitCode=(String)bean.get("codeitemid");
                        ArrayList ll=(ArrayList)bean.get("list");
                        
                        if(ll!=null&&ll.size()>0){
                            bean=(LazyDynaBean)ll.get(0);
                            zpUnitCode=(String)bean.get("codeitemid");
                        }
                    }
                }*/
                
            }
            ArrayList boardlist = employNetPortalBo.SQLExecute("2", "", hireChannel);
            
            String hasMessage="0";
            
            if(boardlist!=null&&boardlist.size()>0){
                hasMessage="1";
            }
            
            String introduceType="-1";
            String unitIntroduce="";
            HashMap contentMap = employNetPortalBo.getIntroduceContent(map, zpUnitCode);
            if(contentMap.get("type")!=null)
                introduceType=(String)contentMap.get("type");
            if(contentMap.get("content")!=null)
                unitIntroduce=(String)contentMap.get("content");
            String introducelink="";
            if(contentMap.get("link")!=null)
                introducelink=(String)contentMap.get("link");
            String new_pos_date="";
            if(map.get("new_pos_date")!=null)
                new_pos_date=(String)map.get("new_pos_date");
            String isHasNewDate="0";
            if(new_pos_date!=null&&new_pos_date.trim().length()>0)
                isHasNewDate="1";
            String isAll="1";
            if(this.getFormHM().get("isAllPos")!=null)
                isAll = (String)this.getFormHM().get("isAllPos");
            ArrayList zpPosList=new ArrayList();
            ArrayList kunitList2=new ArrayList();//这里面存放的是所有的招聘单位的codeitemid
            /**unitPosMap2中存放着各个招聘职位的相关信息,对应关系单位的codeitemid-->list,一个单位对应一个list,list中的多个bean对应多个职位的招聘详情**/
            
            if(returnType!=null&&returnType.length()!=0&& "resume".equalsIgnoreCase(returnType)){//已浏览岗位
                zpPosList=employNetPortalBo.getPositionByUnitCode(conditionFieldList, hireChannel, zpUnitCode,isAll,z0301);
			}else if(!"search".equalsIgnoreCase(returnType)){
				zpPosList = employNetPortalBo.getZpPostList(kunitList2, hireChannel, unitLevel);
				zpPosList = employNetPortalBo.getShowPostList(zpUnitCode, zpPosList);
            }
            
            if(this.userView != null && zpPosList != null && zpPosList.size() > 0) {
                String a0100 = employNetPortalBo.getA0100(this.userView.getUserName());
                for(int i = 0; i < zpPosList.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) zpPosList.get(i);
                    ArrayList list = (ArrayList) bean.get("list");
                    if(list != null && list.size() > 0) {
                        for(int m = 0; m < list.size(); m++) {
                            LazyDynaBean posBean = (LazyDynaBean) list.get(m);
                            String posid = (String) posBean.get("z0301");
                            if(StringUtils.isNotEmpty(posid))
                                posBean.set("isApplyedPos", String.valueOf(employNetPortalBo.isApplyedPosition(posid, a0100)));
                            
                        }
                    }
                    
                }
                
            }
            
            String lfType="1";
            if(map!=null&&map.get("lftype")!=null)
                lfType=(String)map.get("lftype");
            this.getFormHM().put("isAllPos", isAll);
            this.getFormHM().put("lfType", lfType);
            this.getFormHM().put("isHasNewDate", isHasNewDate);
            this.getFormHM().put("zpPosList", zpPosList);
            this.getFormHM().put("introducelink", introducelink);
            this.getFormHM().put("introduceType", introduceType);
            this.getFormHM().put("unitIntroduce", unitIntroduce);
            this.getFormHM().put("unitList", unitList);
            this.getFormHM().put("zpUnitCode", zpUnitCode);
            this.getFormHM().put("hasMessage", hasMessage);
            this.getFormHM().put("dbName",dbName);
            this.getFormHM().put("conditionFieldList",conditionFieldList);
            this.getFormHM().put("isAttach",isAttach);
            this.getFormHM().put("sunitlist", sunitList);
            this.getFormHM().put("boardlist", boardlist);
            employNetPortalBo.getPageBoardList(boardlist, this.getFormHM());
            String resumeActive="0";
            if(map!=null&&map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
                resumeActive="1";
            this.getFormHM().put("resumeActive", resumeActive);
            String masterName="";
            String tempName=SystemConfig.getPropertyValue("masterName");   //招聘雇主名称
            if(tempName!=null)
                masterName=new String(tempName.getBytes("ISO8859_1"),"GB2312"); 
            this.getFormHM().put("masterName",masterName);
            //许可协议字符串为空时 值为0, 点击注册按钮直接跳转到注册页面
			String isDefinitinn="0";
			ParameterSetBo psb=new ParameterSetBo(this.getFrameconn());
			String licenseAgreement=psb.getLicense_agreement();
			//许可协议字符串不为空时 isDefinitinn值为1, 点击注册按钮跳转到许可协议页面
			if(licenseAgreement!=null&&!"".equals(licenseAgreement))
				isDefinitinn="1";
			
			String regEndTime = RecruitUtilsBo.getRegisterEndTime();
			//判断注册是否已截止
			if(StringUtils.isNotEmpty(regEndTime)) {
				String format = "yyyy-MM-dd HH:mm";
				Date endtime = DateUtils.getDate(regEndTime, format);
				Date now  =  new Date();
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				now = DateUtils.getDate(sdf.format(now), format);
				//isDefinitinn值为2, 注册按钮隐藏
				if(now.after(endtime))
					 isDefinitinn="2";
			}
            
            String hireMajor="-1";
            String hireMajorCode="-1";
            if(map.get("hireMajor")!=null&&!"".equals((String)map.get("hireMajor")))
                hireMajor=(String)map.get("hireMajor");
            if(map.get("hireMajorCode")!=null&&!"".equals((String)map.get("hireMajorCode")))
                hireMajorCode=(String)map.get("hireMajorCode");
            this.getFormHM().put("hireMajor", hireMajor);
            String promptContent=psb.getPrompt_content();
            this.getFormHM().put("promptContent",promptContent==null?"":promptContent);
            String positionNumber="7";
            if(map!=null&&map.get("positionNumber")!=null&&!"".equals(((String)map.get("positionNumber")).trim()))
                positionNumber=(String)map.get("positionNumber");
            this.getFormHM().put("positionNumber", positionNumber);
            this.getFormHM().put("isDefinitinn", isDefinitinn);
            this.getFormHM().put("licenseAgreement", licenseAgreement);
            if(EmployNetPortalBo.netHref==null|| "".equals(EmployNetPortalBo.netHref))
            {
                if(map.get("net_href")!=null)
                {
                    this.getFormHM().put("netHref", (String)map.get("net_href"));
                    EmployNetPortalBo.netHref=(String)map.get("net_href");
                }
            }
            else
            {
                this.getFormHM().put("netHref", EmployNetPortalBo.netHref);
            }
            String isDefinitionActive="2";
            if(map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
                isDefinitionActive="1";
            this.getFormHM().put("isDefinitionActive", isDefinitionActive);
            
            if(this.getFormHM().get("a0100")!=null&&!"".equals((String)this.getFormHM().get("a0100")))
            {
                
                if("1".equals(isDefinitionActive))
                {
                    String field=(String)map.get("active_field");
                    String a0100=(String)this.getFormHM().get("a0100");
                    String sql = "select "+field+" from "+dbName+"a01 where a0100='"+a0100+"'";
                    this.frowset=dao.search(sql);
                    while(this.frowset.next())
                    {
                        this.getFormHM().put("activeValue", this.frowset.getString(field)==null?"1":this.frowset.getString(field));
                    }
                }
                String a0100=(String)this.getFormHM().get("a0100");
                String canPrintResumeStatus=SystemConfig.getPropertyValue("canPrintResumeStatus");
                
                /**当预览简历时，是否可以使用打印和卡片功能，默认是可以的*/
                String canPrint="1";
                String resumeStateFieldIds="";
                if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
                    resumeStateFieldIds=(String)map.get("resume_state");
                String status=employNetPortalBo.getStatus(a0100, dbName, resumeStateFieldIds, dao);
                if(canPrintResumeStatus!=null&&!"".equals(canPrintResumeStatus))
                {
                    canPrintResumeStatus=","+canPrintResumeStatus+",";
                    if(canPrintResumeStatus.indexOf((","+status+","))!=-1)
                        canPrint="1";
                    else
                        canPrint="0";
                }
                this.getFormHM().put("canPrint", canPrint);
                String admissionCard="#";
                if(map.get("admissionCard")!=null&&!"".equals((String)map.get("admissionCard")))
                {
                    admissionCard=(String)map.get("admissionCard");
                }
                /*String canPrintAdmissionCardStatus=SystemConfig.getPropertyValue("canPrintAdmissionCardStatus");
                canPrintAdmissionCardStatus=","+canPrintAdmissionCardStatus+",";
                if(canPrintAdmissionCardStatus.indexOf((","+status+","))==-1)
                {
                    admissionCard="#";
                }*/
                
                boolean canPrintExamno = employNetPortalBo.canPrintExamNo(a0100, admissionCard);
                this.getFormHM().put("canPrintExamno",String.valueOf(canPrintExamno));
                
                //zxj 20141125 根据用户应聘岗位情况取预览简历登记表
                String previewTableId = employNetPortalBo.getResumeTemplateId(dao, map, a0100);
                this.getFormHM().put("previewTableId", previewTableId);
                
                if(previewTableId.trim().length()==0){
                    canPrint="0";
                    this.getFormHM().put("canPrint", canPrint);
                }
                this.getFormHM().put("admissionCard",admissionCard);
                
                //zxj 20151113 增加考试成绩查询
                boolean canQueryScore = employNetPortalBo.canQueryScore(dbName, a0100);
                if (canQueryScore)
                    this.getFormHM().put("canQueryScore", "1");
                else 
                    this.getFormHM().put("canQueryScore", "0");
                
                 String corcode="";
                    String jobname="";
                    String code=employNetPortalBo.getcorcode(a0100);
                    String hasapply="1";
                    if(code==null|| "false".equalsIgnoreCase(code)){
                        hasapply="1";
                    }else{
                        if(code.indexOf("&")!=-1){
                            String[] a=code.split("&");
                            if(a.length<2){
                                hasapply="1";
                            }else{
                                corcode=a[1];
                                jobname=a[0];
                                hasapply="2";
                            }
                        }
                    }
                    String hdt =SystemConfig.getPropertyValue("hdtconnect");
                    if(hdt!=null&& "true".equalsIgnoreCase(hdt)){
                        hasapply="2";
                    }else{
                        hasapply="1";
                    }
                    Calendar cd=Calendar.getInstance();
                    String yy=""+cd.get(Calendar.YEAR);
                    String mm1=cd.get(Calendar.MONTH)+1<=9?"0"+(cd.get(cd.MONTH)+1):(cd.get(cd.MONTH)+1)+"";
                    String dd=cd.get(Calendar.DATE)<=9?"0"+cd.get(Calendar.DATE):cd.get(Calendar.DATE)+"";
                    String partime=yy+mm1+dd;
                    String cer="";
                    Md5ForHire md5 =new Md5ForHire();
                    String keycode="klskuge9723kgs8772k3";
                    this.getFormHM().put("cer",cer);
                    this.getFormHM().put("corcode",corcode);
                    this.getFormHM().put("jobid",corcode);
                    this.getFormHM().put("jobname",jobname);
                    this.getFormHM().put("isapply",hasapply);
                    //String loginName = "";
                    RecordVo avo = new RecordVo(dbName+"A01");
                    avo.setString("a0100", a0100);
                    if(dao.isExistRecordVo(avo)){
                        avo = dao.findByPrimaryKey(avo);
                        loginName=avo.getString("username");
                    }
                    cer=md5.getMD5((loginName+keycode+partime).getBytes());
                    this.getFormHM().put("hdtusername",loginName);
                    this.getFormHM().put("cer",cer);
            }
            
            String max_count = "";
            if(map.get("max_count")!=null)
                max_count = (String)map.get("max_count");
            this.getFormHM().put("max_count", max_count);
            this.getFormHM().put("hireChannel", hireChannel);
            HashMap runMap = employNetPortalBo.getRunHeaderList(dbName);
            ArrayList runHeaderList = new ArrayList();
            ArrayList runDataList = new ArrayList();
            if(runMap.size()>0)
            {
                runHeaderList=(ArrayList)runMap.get("list");
                runDataList = (ArrayList)runMap.get("dataList");
            }
            HashMap requestMap=(HashMap)this.getFormHM().get("requestPamaHM");
            String a0100 = (String)this.getFormHM().get("a0100");
            a0100=PubFunc.getReplaceStr(a0100);
            String nbase = (String)this.getFormHM().get("dbName");
            
            String isResumePerfection="1";
            if(requestMap.get("finished")!=null&& "1".equals((String)requestMap.get("finished")))
            {
                ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
                HashMap _map=parameterXMLBo.getAttributeValues();
                String workExperience=employNetPortalBo.getWorkExperience();
                String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
                String value="";
                if("1".equals(isDefineWorkExperience))
                {
                //  value=(String)this.getFormHM().get("workExperience");
                    String workExperience_item=(String)_map.get("workExperience");
                    this.frowset=dao.search("select "+workExperience_item+" from "+nbase+"a01 where a0100='"+a0100+"'");
                    if(this.frowset.next())
                        value=this.frowset.getString(1)!=null?this.frowset.getString(1):"1";  //(String)this.getFormHM().get("workExperience");
                
                }
                
              //定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
    			//headHire、猎头招聘    01、校园招聘   02、社会招聘
    			if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
    				hireChannel = "02";
    			
    			//判断简历资料必填项是否没填
				isResumePerfection = employNetPortalBo.checkRequired(hireChannel,a0100);
				
                requestMap.remove("finished");
            }
            this.getFormHM().put("runHeaderList", runHeaderList);
            this.getFormHM().put("runDataList", runDataList);
            ArrayList tempPosList = employNetPortalBo.getPosListField();
            ArrayList posFieldList = new ArrayList();//用于存放外网招聘显示的岗位字段
            
            /**猎头招聘特别处理一下**/
            if("headHire".equals(hireChannel)){
                if(tempPosList!=null)
                {
                    for(int i =0;i<tempPosList.size();i++){
                        LazyDynaBean bean = (LazyDynaBean) tempPosList.get(i);
                        LazyDynaBean newBean = new LazyDynaBean();
                        
                        String itemid = (String) bean.get("itemid");
                        String itemdesc = (String) bean.get("itemdesc");
                        if("yprsl".equals(itemid)){
                            itemid = "tjrsl";
                            itemdesc = "推荐人数";
                        }
                        if("ypljl".equals(itemid)){
                            itemid = "tjjl";
                            itemdesc = "推荐";
                        }
                        newBean.set("itemtype", bean.get("itemtype"));
                        newBean.set("codesetid", bean.get("codesetid"));
                        newBean.set("deciwidth", bean.get("deciwidth"));
                        newBean.set("itemid", itemid);
                        newBean.set("itemdesc", itemdesc);
                        posFieldList.add(newBean);
                    }
                }
            }else{
                posFieldList = tempPosList;
            }
            this.getFormHM().put("channelName", employNetPortalBo.getChannelName(hireChannel));
            this.getFormHM().put("posFieldList", posFieldList);
            String cms_chl_no = (String)this.getFormHM().get("cms_chl_no");
            String menuType = (String)this.getFormHM().get("menuType");
            this.getFormHM().put("cms_chl_no", cms_chl_no);
            this.getFormHM().put("menuType", menuType);
            this.getFormHM().put("chl_id",chl_id);
            this.getFormHM().put("isResumePerfection", isResumePerfection);
            this.getFormHM().put("hireMajorCode", hireMajorCode);
            this.getFormHM().put("acountBeActived", acountBeActived);
            this.getFormHM().put("failedTime", failedTime);
            this.getFormHM().put("unlockTime", unlockTime);
            this.getFormHM().put("complexPassword", complexPassword);
            this.getFormHM().put("passwordMinLength", passwordMinLength);
            this.getFormHM().put("passwordMaxLength", passwordMaxLength);
            //是否显示 忘记帐号 
            String accountFlag = employNetPortalBo.checkAccount();
            this.getFormHM().put("accountFlag", accountFlag);
            /**清除无用内容 防止内存溢出**/
//          this.getFormHM().put("uploadFileList", null);
//          this.getFormHM().put("unitIntroduce", null);
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());			
			String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
			seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
			this.getFormHM().put("seprartor", seprartor);
			
			
			{
				this.getFormHM().remove("resumeBrowseSetMap");  //20150728 
				this.getFormHM().remove("fieldSetList");  //20150728 
				this.getFormHM().remove("setShowFieldMap");  //20150728  
				this.getFormHM().remove("applyedPosList");  //20150728  
				this.getFormHM().remove("posDescFiledList");  //20150728   
				this.getFormHM().remove("resumeFieldList");  //20150728    
			}
			
			if(employNetPortalBo.unCacheDataTime == null)
				employNetPortalBo.unCacheDataTime = new Date();
			else{
				if(EmployNetPortalBo.channelMap.get(hireChannel)==null){
                	HashMap infoMap = new HashMap();
                	infoMap.put("cacheDataTime", new Date());
                	EmployNetPortalBo.channelMap.put(hireChannel, infoMap);
                }else{
                	EmployNetPortalBo.channelMap.get(hireChannel).put("cacheDataTime", new Date());
                }
			}
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }
	
	private String getResource(String setlist,String id){
	    String key  = "";
		String[] arr = setlist.split(",");
		for (int i = 0; i < arr.length; i++) {
			if ("".equals(arr[i].trim()))
				continue;
			if (arr[i].indexOf("[")!=-1) {
			    key = arr[i].substring(0, arr[i].indexOf("["));
			    if (key.indexOf("#") > -1)
			        key = key.substring(0, key.indexOf("#"));
			}
			
			if(key.equalsIgnoreCase(id))
				return arr[i];
		}
		return "";
	}
}