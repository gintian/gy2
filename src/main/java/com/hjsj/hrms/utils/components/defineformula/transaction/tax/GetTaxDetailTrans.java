package com.hjsj.hrms.utils.components.defineformula.transaction.tax;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableXMLBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：GetTaxDetailTrans 
 * 类描述： 获取税率表详细信息
 * 创建人：zhaoxg
 * 创建时间：Nov 27, 2015 11:56:11 AM
 * 修改人：zhaoxg
 * 修改时间：Nov 27, 2015 11:56:11 AM
 * 修改备注： 
 * @version
 */
public class GetTaxDetailTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String taxid = (String) this.getFormHM().get("taxid");
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String itemid = (String) this.getFormHM().get("itemid");
	        String income="";//应纳税所得额
	        String mode="0";//0：正算 1：反算
	        DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
	        if(salaryid.trim().length()>0&&itemid.trim().length()>0){
	        	SalaryCtrlParamBo salarybo = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
	        	income=salarybo.getValue(SalaryCtrlParamBo.YS_FIELDITEM,"id",itemid);
	        	mode=salarybo.getValue(SalaryCtrlParamBo.YS_FIELDITEM,"id",itemid,"mode");
	        	mode=mode!=null&&mode.trim().length()>0?mode:"0";
	        }
	        TaxTableXMLBo xmlBo = new TaxTableXMLBo(this.getFrameconn());
	        String param = xmlBo.getParamValue(taxid);
			//计税方式
	        String k_base=bo.getK_base(taxid);//基数
	        this.getFormHM().put("k_base",k_base);
	        this.getFormHM().put("income",income);
	        this.getFormHM().put("mode",mode);
	        this.getFormHM().put("param",param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
