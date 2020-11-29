package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 招聘外网首页交易类
 * <p>Title:HomePageTrans.java</p>
 * <p>Description>:HomePageTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 17, 2011  9:38:41 AM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class HomePageTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao  = new ContentDAO(this.getFrameconn());
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			StringBuffer sql=new StringBuffer();
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String chl_id=(String)hm.get("chl_id");
			if(EmployNetPortalBo.netHref==null|| "".equals(EmployNetPortalBo.netHref))
			{
				if(map!=null&&map.get("net_href")!=null)
				{
					this.getFormHM().put("netHref", (String)map.get("net_href"));
					EmployNetPortalBo.netHref=(String)map.get("net_href");
				}
			}
			else
			{
				this.getFormHM().put("netHref", EmployNetPortalBo.netHref);
			}
			String lfType="1";
			String hbType="1";
			if(map!=null&&map.get("lftype")!=null)
				lfType=(String)map.get("lftype");
			if(map!=null&&map.get("hbtype")!=null)
				hbType=(String)map.get("hbtype");
			this.getFormHM().put("lfType", "".equals(lfType.trim())?"1":lfType);
			this.getFormHM().put("hbType", "".equals(hbType.trim())?"1":hbType);
			EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn());
			String zpReportContent=bo.getZP_SY_MESSAGE();
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
			
			this.getFormHM().put("licenseAgreement", licenseAgreement);
			this.getFormHM().put("sy_message", zpReportContent);
			this.getFormHM().put("cms_chl_no", "1");
			this.getFormHM().put("menuType", "1");
			this.getFormHM().put("chl_id", "");
			this.getFormHM().put("menuType", "0");
			this.getFormHM().put("isDefinitinn", isDefinitinn);
			this.getFormHM().put("chl_id",chl_id);
			ArrayList boardlist=bo.SQLExecute("2", "2", "");
			this.getFormHM().put("boardlist", boardlist);
			bo.getPageBoardList(boardlist, this.getFormHM());
			if(boardlist!=null&&boardlist.size()>1){
				this.getFormHM().put("sy_message", "true");
			}
			ArrayList publicityList=bo.SQLExecute("2", "13", "");
			this.getFormHM().put("publicityList", publicityList);
			
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
			String hireChannel="01";
            if(hm.get("hireChannel")!=null)
            {
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
            employNetPortalBo.setHireChannel(hireChannel);
            
            String acountBeActived="0";//注册帐号需通过邮箱激活才生效
            if(map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0)
                acountBeActived=(String)map.get("acountBeActived");
            
            //是否显示 忘记帐号 
            String returnType=(String)hm.get("returnType");
            String accountFlag = employNetPortalBo.checkAccount();
            String dbName=employNetPortalBo.getZpkdbName();
            //增加考试成绩查询
            String a0100=(String)this.getFormHM().get("a0100");
            String admissionCard="#";
            if(map.get("admissionCard")!=null&&!"".equals((String)map.get("admissionCard")))
            {
                admissionCard=(String)map.get("admissionCard");
            }
            boolean canPrintExamno = employNetPortalBo.canPrintExamNo(a0100, admissionCard);
            this.getFormHM().put("canPrintExamno",String.valueOf(canPrintExamno));
            
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
            this.getFormHM().put("isapply",hasapply);
            
            String isDefinitionActive="2";
            if(map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
                isDefinitionActive="1";
            this.getFormHM().put("isDefinitionActive", isDefinitionActive);
            if("1".equals(isDefinitionActive))
            {
            	ArrayList<String> list = new ArrayList<String>();
                String field=(String)map.get("active_field");
                String sqlActive = "select "+field+" from "+dbName+"a01 where a0100=?";
                list.add(a0100);
                this.frowset=dao.search(sqlActive,list);
                while(this.frowset.next())
                {
                    this.getFormHM().put("activeValue", this.frowset.getString(field)==null?"1":this.frowset.getString(field));
                }
            }
            
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
            
            if(map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0)
                acountBeActived=(String)map.get("acountBeActived");
            String isAttach="0";
            if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
                isAttach=(String)map.get("attach");
            
            boolean canQueryScore = employNetPortalBo.canQueryScore(dbName, a0100);
            if (canQueryScore)
                this.getFormHM().put("canQueryScore", "1");
            else 
                this.getFormHM().put("canQueryScore", "0");
            this.getFormHM().put("acountBeActived", acountBeActived);
            this.getFormHM().put("failedTime", failedTime);
            this.getFormHM().put("unlockTime", unlockTime);
            this.getFormHM().put("isAttach",isAttach);
            this.getFormHM().put("complexPassword", complexPassword);
            this.getFormHM().put("passwordMinLength", passwordMinLength);
            this.getFormHM().put("passwordMaxLength", passwordMaxLength);
            this.getFormHM().put("accountFlag", accountFlag);
            this.getFormHM().put("acountBeActived", acountBeActived);
            this.getFormHM().put("dbName", dbName);
            this.getFormHM().put("returnType", returnType);
            this.getFormHM().put("hireChannel", hireChannel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
