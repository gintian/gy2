package com.hjsj.hrms.module.gz.salarytype.transaction.applicationorganization;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.ApplicationOrgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class InitApplicationOrgTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		String salaryid = PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("salaryid")));
		String type = (String)this.getFormHM().get("type");//0：查询初始化薪资类别应用机构数据，1：查询初始化薪资发放下发数据
		
		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
		safeBo.isSalarySetResource(salaryid,null);
		
		String gz_module = (String) this.getFormHM().get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
		ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.getFrameconn(),salaryid,this.userView);
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		DbNameBo db = new DbNameBo(frameconn);
		
		String manager =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		boolean isShare=true;//薪资类别是否共享
		if(StringUtils.isBlank(manager))
		{
			isShare=false;
		}
		
		//获取认证用户名指标，找到a0100
		String loginNameField = db.getLogonUserNameField();
		@SuppressWarnings("rawtypes")
		ArrayList dbnameList = db.getAllLoginDbNameList();
		String start_date=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FILLING_AGENCYS,"start_date");
		String end_date=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FILLING_AGENCYS,"end_date");
		
		ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
		//查询
		if("0".equals(type)) {
			
			@SuppressWarnings("rawtypes")
			List elementList = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FILLING_AGENCY,SalaryCtrlParamBo.FILLING_AGENCYS);
			dataList = aorgbo.getCtrlXmlAgencyList(null,elementList, dbnameList, loginNameField,gz_module,type);
			
			this.getFormHM().put("start_date",StringUtils.isBlank(start_date)?"":start_date);
			this.getFormHM().put("end_date",StringUtils.isBlank(end_date)?"":end_date);
			
			StringBuffer unitcodes = new StringBuffer();
            //1 走人员范围加高级
            String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
            //手工引入是否走人员范围加高级 0不走 1走
            String handImportScope = "0";
         // 人员范围权限过滤标志  1：有
			String priv_mode_flag=gzbo.getCtrlparam().getValue(com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.PRIV_MODE,"flag");  
            if ("1".equals(priv_mode_flag)) {
            	// 对于共享账套，非共享管理员设置了该参数才只走人员范围+高级
                if (StringUtils.isNotBlank(gzbo.getManager()) && !gzbo.getManager().equalsIgnoreCase(this.userView.getUserName()) 
                		&& !this.userView.isSuper_admin() && "1".equals(unitIdByBusiOutofPriv)) {
                    handImportScope="1";
                } else  {
                    String unitcode = this.userView.getUnitIdByBusiOutofPriv("1");
                    if (StringUtils.isNotBlank(unitcode) && !"UN`".equalsIgnoreCase(unitcode)) {
                        String[] temps = unitcode.split("`");
                        for (int i = 0; i < temps.length; i++) {
                            if (temps[i].trim().length() > 0) {
                                unitcodes.append("," + temps[i].substring(2));
                            }
                        }
                        unitcodes.deleteCharAt(0);
                    }
                    handImportScope = "0";
                    // 如果业务范围+操作单位没有值，走高级
                    if(unitcodes.length() == 0) {
                    	handImportScope = "1";
                    }
                }
            }

            this.getFormHM().put("handImportScope", handImportScope);
            this.getFormHM().put("orgid", unitcodes.toString());//业务范围
			
		}else if("1".equals(type)){
			String a00z2 = this.getFormHM().get("a00z2")==null?"":PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("a00z2"))); //业务日期
		    
			//判断在gz_reporting_log表中是否存在对应的数据
			ArrayList<LazyDynaBean> dataLists = aorgbo.gzReportingLogData(a00z2,gz_module);
			if(dataLists.size() == 0) {
				this.getFormHM().put("isSelect",true);
			}else {
				this.getFormHM().put("isSelect",false);
			}
			@SuppressWarnings("rawtypes")
			List elementList = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FILLING_AGENCY,SalaryCtrlParamBo.FILLING_AGENCYS);
			dataList = aorgbo.getCtrlXmlAgencyList(dataLists,elementList, dbnameList, loginNameField,gz_module,type);
			this.getFormHM().put("start_date",StringUtils.isBlank(start_date)?"":start_date);
			this.getFormHM().put("end_date",StringUtils.isBlank(end_date)?"":end_date);
		}
		this.getFormHM().put("appOrgDataList",dataList);
		this.getFormHM().put("isShare",isShare);
	}
}
