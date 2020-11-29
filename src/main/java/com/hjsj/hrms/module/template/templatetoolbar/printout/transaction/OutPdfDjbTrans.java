package com.hjsj.hrms.module.template.templatetoolbar.printout.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.ykcard.YkcardOutWord;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.ykcard.CardTagParamView;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Title:OutPdfTrans.java</p>
 * <p>Description>:导出登记表PDF文件</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2017-2-6 下午03:38:40</p>
 * <p>@author:liuyz</p>
 * <p>@version: 7.x</p>
 */
public class OutPdfDjbTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		HashMap formMap= this.getFormHM();
		TemplateFrontProperty frontProperty =new TemplateFrontProperty(formMap);
		/*String sysType = frontProperty.getSysType();
		String moduleId = frontProperty.getModuleId();
		String returnFlag = frontProperty.getReturnFlag();
		String tabId = frontProperty.getTabId();
		String task_id = frontProperty.getTaskId();*/
		String infor_type=frontProperty.getInforType();
		String istype=(String)this.getFormHM().get("flag"); /*2登记表*/	
		String cardid=(String)this.getFormHM().get("cardid");
		String object_id=(String)this.getFormHM().get("object_id");
		object_id = PubFunc.decrypt(object_id);
		ArrayList objlist=new ArrayList();
		String a0100="";
		String userbase="";
		if("2".equals(infor_type)){
			a0100=object_id;
			objlist.add(a0100);
		}else if("3".equals(infor_type)){
			a0100=object_id;
			objlist.add(a0100);
		}else{
			int i = object_id.indexOf("`");
			if (i>0){
				a0100=object_id.substring(i+1);
				userbase=object_id.substring(0,i);
			}
			objlist.add(a0100);
		}
        CardTagParamView cardTagParamView=new CardTagParamView();
		String cyear=String.valueOf(cardTagParamView.getCyear());
		String querytype=String.valueOf(cardTagParamView.getQueryflagtype());
		String cmonth=String.valueOf(cardTagParamView.getCmonth());
		String userpriv=String.valueOf(cardTagParamView.getUserbase());
		String season=String.valueOf(cardTagParamView.getSeason());
		String ctimes=String.valueOf(cardTagParamView.getCtimes());
		String cdatestart=String.valueOf(cardTagParamView.getCdatestart());
		String cdateend=String.valueOf(cardTagParamView.getCdateend());
		//YkcardPdf   ykcardPdf=new YkcardPdf(this.getFrameconn());
		YkcardOutWord outWord=new YkcardOutWord(this.userView,this.frameconn);
		String infokind=(String)this.getFormHM().get("infor_type");
		String fieldpurv=(String)this.getFormHM().get("fieldpurv");
		/**安全信息改造，当选人时,判断是否存在不在用户范围内的人员begin**/
		CheckPrivSafeBo safeBo = new CheckPrivSafeBo(this.frameconn,this.userView);
		if(infokind!=null && "1".equalsIgnoreCase(infokind)){//liuy 2014-10-23 只有人员才需要判断人员库
			if(!this.userView.isSuper_admin()){
				String paramBasePre=userbase;
				String returnBasePre =safeBo.checkDb(paramBasePre);//这个方法当不越权时返回传进去的人员库，越权时返回当前人员的第一个人员库
				/**当返回的人员库值的长度大于0并且不等于传进去的人员库时说明越权**/
				if(returnBasePre.trim().length()>0&&!paramBasePre.equals(returnBasePre)){//如果当前用户的人员库没有这个选中人员的人员库，终止导入
					throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
				}
			}
			if(!this.userView.isSuper_admin()){
				/**验证管理范围，如果越权则返回实有的管理范围**/
				String paramManapriv=this.userView.getManagePrivCodeValue();
				String realManapriv=safeBo.checkOrg(paramManapriv, "");
				String paramPre=userbase;//这里所有的人员库都进行了验证，如果越权的人员库，在上面就结束了
				String paramA0100=(String)objlist.get(0);//这里的A0100尚未进行验证
				if(StringUtils.isNotEmpty(paramA0100)){//liuy 2015-3-24 8205：首页/登记表/员工工作证，切换到劳务人员库，没有人员时，批量生成PDF，提示：人员权限越权，操作被终止				
					String realA0100=safeBo.checkA0100(realManapriv, paramPre, paramA0100, "");
					if(realA0100.trim().length()>0&&!realA0100.equals(paramA0100)){
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.change.dbname"));//bug 32388 单子中人员已经移库。修改提示语句。
					}
				}
			}
		}
        /**安全信息改造，当选人时,判断是否存在不在用户范围内的人员end**/
		if("0".equals(istype))
		{
		  XmlParameter xml=new XmlParameter("UN",userView.getUserOrgId(),"00");
		  xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn());	
		  cardid=xml.getCard_id();
		  String type=xml.getType();              //0条件1时间
		  if("0".equals(type))
			 querytype="0";
		}else if("1".equals(istype)){
		   querytype="0";
		}
		try{
			outWord.setQueryTypeTime(cyear, cmonth, cyear, cmonth, season, cyear, ctimes, cdatestart, cdateend);
			String url=outWord.outWordYkcard(Integer.parseInt(cardid), objlist, querytype, infokind, userbase, userpriv, userpriv, fieldpurv);
			url=outWord.wordToPdf(System.getProperty("java.io.tmpdir")+File.separator+url, url);
		    //String url=ykcardPdf.executePdfS(Integer.parseInt(cardid),objlist,userbase,this.userView,cyear,querytype,cmonth,userpriv,istype,season,ctimes,cdatestart,cdateend,infokind,fieldpurv);
		    url = PubFunc.encrypt(url);
		    this.getFormHM().put("filename",url);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
	}
}
