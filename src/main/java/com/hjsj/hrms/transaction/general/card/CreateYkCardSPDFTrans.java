package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.ykcard.YkcardPdf;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class CreateYkCardSPDFTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 ArrayList nid=new ArrayList();
		if(this.getFormHM().get("nid")!=null){
			  nid=(ArrayList)this.getFormHM().get("nid");
		}
		String cyear=(String)this.getFormHM().get("cyear");	
		String querytype=(String)this.getFormHM().get("querytype");	
		String cmonth=(String)this.getFormHM().get("cmonth");	
		String userpriv=(String)this.getFormHM().get("userpriv");	
		String istype=(String)this.getFormHM().get("istype");              /*0代表薪酬1登记表*/	
		String season=(String)this.getFormHM().get("season");	
		String ctimes=(String)this.getFormHM().get("ctimes");	
		String cdatestart=(String)this.getFormHM().get("cdatestart");	
		String cdateend=(String)this.getFormHM().get("cdateend");	
		YkcardPdf   ykcardPdf=new YkcardPdf(this.getFrameconn());
		String cardid=(String)this.getFormHM().get("cardid");
		String infokind=(String)this.getFormHM().get("infokind");
		String userbase=(String)this.getFormHM().get("userbase");
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
				String paramA0100=(String)nid.get(0);//这里的A0100尚未进行验证
				if(StringUtils.isNotEmpty(paramA0100)){//liuy 2015-3-24 8205：首页/登记表/员工工作证，切换到劳务人员库，没有人员时，批量生成PDF，提示：人员权限越权，操作被终止				
					String realA0100=safeBo.checkA0100(realManapriv, paramPre, paramA0100, "");
					if(realA0100.trim().length()>0&&!realA0100.equals(paramA0100)){
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.user.ultra.vires"));
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
		    String url=ykcardPdf.executePdfS(Integer.parseInt(cardid),nid,userbase,this.userView,cyear,querytype,cmonth,userpriv,istype,season,ctimes,cdatestart,cdateend,infokind,fieldpurv);
		    url = SafeCode.encode(PubFunc.encrypt(url));
		    this.getFormHM().put("url",url);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		

	}

}
