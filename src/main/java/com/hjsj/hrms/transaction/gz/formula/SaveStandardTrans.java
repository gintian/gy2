package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SaveStandardTrans extends IBusiness {

	public void execute() throws GeneralException {		
		String hfactor=(String)this.getFormHM().get("hfactor");
		hfactor=hfactor!=null&&hfactor.trim().length()>0?hfactor:"";
		
		String s_hfactor=(String)this.getFormHM().get("s_hfactor");
		s_hfactor=s_hfactor!=null&&s_hfactor.trim().length()>0?s_hfactor:"";
		
		String vfactor=(String)this.getFormHM().get("vfactor");
		vfactor=vfactor!=null&&vfactor.trim().length()>0?vfactor:"";
		
		String s_vfactor=(String)this.getFormHM().get("s_vfactor");
		s_vfactor=s_vfactor!=null&&s_vfactor.trim().length()>0?s_vfactor:"";
		
		String item=(String)this.getFormHM().get("item");
		item=item!=null&&item.trim().length()>0?item:"";
		
		String hcontent=(String)this.getFormHM().get("hcontent");
		hcontent=hcontent!=null&&hcontent.trim().length()>0?hcontent:"";
		
		String vcontent=(String)this.getFormHM().get("vcontent");
		vcontent=vcontent!=null&&vcontent.trim().length()>0?vcontent:"";
		
		String gzStandardName=(String)this.getFormHM().get("gzStandardName");
		gzStandardName=gzStandardName!=null&&gzStandardName.trim().length()>0?gzStandardName:"";
		
		GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn(),hfactor,s_hfactor,hcontent,vfactor,s_vfactor,vcontent,item,"","new","0");
		bo.init();
		bo.setUserView(userView);
		GzStandardItemVo vo=bo.getGzStandardItemVo();
		FormulaBo formulabo = new FormulaBo();
		String pkg_id=formulabo.pkgId(this.frameconn,"");//计算公式那建薪资标准表，此时默认建到启动的历史沿革下面  zhaoxg add 2014-11-21
		bo.saveSalaryStandard(vo,pkg_id,gzStandardName,"new","");
	}

}
