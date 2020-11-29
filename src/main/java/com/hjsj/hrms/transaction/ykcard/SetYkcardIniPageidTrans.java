/*
 * Created on 2005-8-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetYkcardIniPageidTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String a0100=(String)hm.get("a0100");
		String pre=(String)hm.get("pre");
		String tabid=(String)this.getFormHM().get("tabid");
		if(tabid==null||tabid.length()<=0)
			tabid="";
		this.getFormHM().put("tabid",tabid);
		String b0110=(String)hm.get("b0110");
		CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
		b0110=checkPrivSafeBo.checkOrg(b0110, "");
		pre=checkPrivSafeBo.checkDb(pre);
		a0100=checkPrivSafeBo.checkA0100(b0110, pre, a0100, "");
		this.getFormHM().put("firstFlag", "1");//员工薪酬第一次进入标记
		this.getFormHM().put("b0110",b0110);
		this.getFormHM().put("a0100",a0100);
        this.getFormHM().put("currentpage","0");
        this.getFormHM().put("pre",pre);
        //是否显示薪酬明细按钮
        VersionControl ver_ctrl=new VersionControl();
        boolean bflag1 = ver_ctrl.searchFunctionId("070202");
        boolean bflag2 = ver_ctrl.searchFunctionId("30014");
        
       // System.out.println("bflag1=" + bflag1 + "  bflag2=" + bflag2);
        
        if(bflag1 || bflag2 ){
        	this.getFormHM().put("showflag","show");
        }else{
        	this.getFormHM().put("showflag","hidden");
        }       
        /*
        XmlParameter xml=new XmlParameter("UN",this.userView.getUserOrgId(),"00");
		xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn());	
		String cardid=xml.getCard_id();
		if(cardid == null || cardid.equals("")||cardid.equals("#")||cardid.equals("0")){
			this.getFormHM().put("showflag","hidden");
		}else{
			this.getFormHM().put("showflag","show");
		}
		*/
		
	}

}
