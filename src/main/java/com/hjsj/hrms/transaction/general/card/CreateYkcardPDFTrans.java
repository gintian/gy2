/*
 * Created on 2006-4-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.businessobject.ykcard.YkcardPdf;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateYkcardPDFTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		System.gc();
		// 移动获取卡片
		boolean isMobile = "1".equals(this.getFormHM().get("isMobile"));
		String nid=(String)this.getFormHM().get("nid");	
		String cyear=(String)this.getFormHM().get("cyear");	
		String querytype=(String)this.getFormHM().get("querytype");	
		String cmonth=(String)this.getFormHM().get("cmonth");	
		String userpriv=(String)this.getFormHM().get("userpriv");	
		String istype=(String)this.getFormHM().get("istype");              /*0代表薪酬1登记表*/	
		String season=(String)this.getFormHM().get("season");	
		String ctimes=(String)this.getFormHM().get("ctimes");	
		String cdatestart=(String)this.getFormHM().get("cdatestart");	
		String cdateend=(String)this.getFormHM().get("cdateend");
		String cardid=(String)this.getFormHM().get("cardid");
		String infokind=(String)this.getFormHM().get("infokind");
		String module=(String)this.getFormHM().get("module");
		String userbase=(String)this.getFormHM().get("userbase");
		String tabid=(String)this.getFormHM().get("tabid");
		String b0110=(String)this.getFormHM().get("b0110");
		String pre=(String)this.getFormHM().get("pre");
		String fieldpurv=(String)this.getFormHM().get("fieldpurv");
		String platform = (String)this.getFormHM().get("platform");
		platform=platform==null?"":platform;
		
		//控制人员数据范围权限 zxj
		if(infokind!=null && "1".equalsIgnoreCase(infokind)&&StringUtils.isEmpty(module))
		{
    		if(userpriv!=null)
    		{
    			if("selfinfo".equalsIgnoreCase(userpriv)){
    				userbase = this.userView.getDbname();
    				nid = this.userView.getA0100();
    			}else if("zpselfinfo".equalsIgnoreCase(userpriv)){//招聘外网用户(由于userview获取不到dbname 所以单独处理)
    				EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),"0");
    				userbase=employNetPortalBo.getZpkdbName();
    				nid = this.userView.getUserId();
    			}
    		}
    		else
    		{
    		    CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
    		    b0110 = cps.checkOrg(b0110, "");
                userbase = cps.checkDb(userbase);
                if(StringUtils.isNotEmpty(nid))//liuy 2015-3-24 8204：首页/登记表/员工工作证，切换到劳务人员库，没有人员时，生成PDF，结果生成PDF仍有人，不对。
                	nid = cps.checkA0100(b0110, userbase, nid, "");
    		}
		}
		
		if("0".equals(istype))
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());	
			String relating=cardConstantSet.getSearchRelating(dao);			
			b0110=cardConstantSet.getRelatingValue(dao,this.userView.getA0100(),this.userView.getDbname(),relating,this.userView.getUserOrgId());
			XmlParameter xml=new XmlParameter("UN",b0110,"00");
			xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn());	
			if(tabid==null||tabid.length()<=0)
		    {
		    	String flag=xml.getFlag();
				/*if(this.b0110==null||this.b0110.length()<=0)
					this.setB0110(userview.getUserOrgId());	*/	
				ArrayList cardidlist=new ArrayList();
				if(userpriv!=null&& "selfinfo".equals(userpriv))
				{
					cardidlist=cardConstantSet.setCardidSelectSelfinfo(this.getFrameconn(),this.userView,flag,userbase,nid,b0110);
				}else
				{
					cardidlist=cardConstantSet.setCardidSelect(this.getFrameconn(),this.userView,flag,userbase,nid,b0110);
				}
				if(cardidlist!=null&&cardidlist.size()>0)
				{
					CommonData dataobj=(CommonData)cardidlist.get(0);
					tabid=dataobj.getDataValue();
					cardid=tabid;
				}
		    }else
		    {
		    	cardid=tabid;
		    }
		    String type=xml.getType();              //0条件1时间
		    if("0".equals(type))
			 querytype="0";
		}else if("1".equals(istype)){
		   querytype="0";
		}
		YkcardPdf ykcardPdf=new YkcardPdf(this.getFrameconn());
		if("5".equalsIgnoreCase(infokind))
		{
			String plan_id=(String)this.getFormHM().get("plan_id");
			ykcardPdf.setPlan_id(plan_id);
		}
		try{
		    String url=ykcardPdf.executePdf(Integer.parseInt(cardid),nid,userbase,this.userView,cyear,querytype,cmonth,userpriv,istype,season,ctimes,cdatestart,cdateend,infokind,fieldpurv,platform);
		    if(!isMobile)
		    	url = PubFunc.encrypt(url);
		    this.getFormHM().put("url",url);
		}catch(Exception e)
		{
			String message = e.getMessage();
			message = message == null ? "" : message;
			this.getFormHM().put("message", message);
	        this.cat.error(e.getMessage());
	        throw GeneralExceptionHandler.Handle(e);
		}finally{
			System.gc();
		}
	}
}
