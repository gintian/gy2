package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.utils.SalaryPageLayoutBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SubmitGzDataTrans 
 * 类描述： 薪资发放提交操作
 * 创建人：zhaoxg
 * 创建时间：Oct 15, 2015 3:34:56 PM
 * 修改人：zhaoxg
 * 修改时间：Oct 15, 2015 3:34:56 PM
 * 修改备注： 
 * @version
 */
public class SubmitGzDataTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String subNoShowUpdateFashion=(String)this.getFormHM().get("subNoShowUpdateFashion");  //1不显示数据操作方式  0:显示数据操作方式
		SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
		
		String setid = "";
		String type = "";
		ArrayList setlist = new ArrayList();
		ArrayList typelist = new ArrayList();
		String items = "";
		String uptypes = "";
		if("0".equalsIgnoreCase(subNoShowUpdateFashion)){
			setid = (String) this.getFormHM().get("setid");
			type = (String) this.getFormHM().get("type");
			String[] _setid = setid.substring(1).split("/");
			String[] _type = type.substring(1).split("/");
			
			for(int i=0;i<_setid.length;i++){
				setlist.add(_setid[i]);
			}
			for(int i=0;i<_type.length;i++){
				typelist.add(_type[i]);
			}
			items = (String) this.getFormHM().get("items");
			uptypes = (String) this.getFormHM().get("uptypes");
		}
		String ff_bosdate = (String) this.getFormHM().get("appdate");
		ff_bosdate = PubFunc.decrypt(SafeCode.decode(ff_bosdate));
		String ff_count = (String) this.getFormHM().get("count");
		ff_count = PubFunc.decrypt(SafeCode.decode(ff_count));
		bo.submitGzData(setlist, typelist, items, uptypes, ff_bosdate, ff_count);

		SalaryPageLayoutBo pageLayoutBo=new SalaryPageLayoutBo(this.getFrameconn(),this.getUserView());

		LazyDynaBean bean ;
		String manager=bo.getSalaryTemplateBo().getManager();
		if(bo.getSalaryTemplateBo().getManager()==null||manager.length()==0){
			bean = pageLayoutBo.getGzLog(ff_bosdate,ff_count,this.getUserView().getUserName(),salaryid);
		}else{
			bean = pageLayoutBo.getGzLog(ff_bosdate,ff_count,manager,salaryid);
		}
		//提交完成后获取当前状态
		this.getFormHM().put("sp_flag",bean.get("sp_flag"));


	}

}
