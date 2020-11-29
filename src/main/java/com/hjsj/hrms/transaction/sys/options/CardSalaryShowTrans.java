package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.ykcard.RecordConstant;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class CardSalaryShowTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String a0100 = (String)this.getFormHM().get("a0100");
		String flag = (String)hm.get("flag");
		String pre = (String)hm.get("pre");
		//liuy 2015-3-18 7976：自助服务/员工信息/员工薪酬，显示照片，怎么不是照片墙呢？另外每行的照片个数也不对。
		String view_photo = (String)hm.get("view_photo");
		hm.remove("view_photo");
		//liuy 2015-3-18 end
		String b0110=(String)this.getFormHM().get("b0110");
		
		//zxj 2015-8-29 flag为空是员工薪酬页面点击左侧机构树显示的列表中的人员
        if(!"noself".equals(flag) && !"".equals(flag)){
			a0100=userView.getA0100();
			pre=userView.getDbname();
			b0110=userView.getUserOrgId();
		}else{
			CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
			b0110=checkPrivSafeBo.checkOrg(b0110, "");
			pre=checkPrivSafeBo.checkDb(pre);
			a0100=checkPrivSafeBo.checkA0100(b0110, pre, a0100, "");
		}
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("pre",pre);
		this.getFormHM().put("view_photo",view_photo);
		this.getFormHM().put("b0110",b0110);
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
        RecordConstant recordConstant=new RecordConstant(this.getFrameconn());
		String str_value=recordConstant.searchConstant();
		this.getFormHM().put("recardconstant",str_value);
	}

}
