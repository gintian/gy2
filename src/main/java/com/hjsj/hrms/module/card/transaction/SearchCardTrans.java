package com.hjsj.hrms.module.card.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

public class SearchCardTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		// 登记表类型: 1人员,2机构,4职位,6基准岗位
	    //String tabid=(String)hm.get("tabid");
	   // if(tabid==null)
	    //	tabid=(String)this.getFormHM().get("tabid");
		String tabid=(String)this.getFormHM().get("tabid");
		if(StringUtils.isNotEmpty(tabid)) {
			this.getFormHM().put("tabid", tabid);
		}
		String inforkind = (String)hm.get("inforkind");
	    String a0100 = (String)hm.get("a0100");
	    String pre = (String)this.getFormHM().get("userbase");
	    pre = pre==null?"":pre;
	   // this.getFormHM().put("tabid",tabid);
	    if (null != a0100 && !"".equals(a0100))
	    {
	        CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
	        if("1".equals(inforkind)){
	        	a0100 = cps.checkA0100("",pre, a0100, "");
	        	if(a0100==null||"".equals(a0100)&&this.userView.getStatus()==4)//自助用户对于无人员范围权限的 只查自己
	        		a0100=this.userView.getA0100();
	        }else if("2".equals(inforkind)||"4".equals(inforkind))
	        	a0100 = cps.checkOrg(a0100, "4");
	    }
	    String selfInfo=(String) hm.get("selfInfo");///general/card/searchcard.do?b_showcard=link&tabid=9&inforkind=1&selfInfo=selfInfo 链接加selfInfo参数控制只显示当前操作人登记表
	    if("selfInfo".equalsIgnoreCase(selfInfo)){//链接添加selfInfo 参数时只显示自己信息的登记表  去除selfInfo参数的链接显示查询导出功能的登记表
	    	a0100=this.userView.getA0100();
	    	this.getFormHM().put("nbase", this.userView.getDbname());
	    	this.getFormHM().put("cardtype", "");
	    	hm.put("selfInfo", "");
	    }else{
	    	this.getFormHM().put("cardtype", this.getFormHM().get("cardtype"));
	    }
	    this.getFormHM().put("a0100",a0100);
	    String pageid=(String)hm.get("pageid");
	    if(StringUtils.isNotEmpty(pageid)) {//页面使用tabpanel翻页
	    	this.getFormHM().put("currentpage",pageid);
	    }else
	    	this.getFormHM().put("currentpage","0");

	}

}
