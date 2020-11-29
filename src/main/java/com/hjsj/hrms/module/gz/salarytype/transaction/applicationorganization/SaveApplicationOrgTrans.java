package com.hjsj.hrms.module.gz.salarytype.transaction.applicationorganization;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salarytype.businessobject.ApplicationOrgBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class SaveApplicationOrgTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			//获取参数
			String salaryid = PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("salaryid")));
			String gz_module = (String) this.getFormHM().get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
			String type = (String)this.getFormHM().get("type");//0：保存薪资类别数据，1：保存薪资发放下发数据
			ArrayList<MorphDynaBean> list = (ArrayList<MorphDynaBean>)this.getFormHM().get("data");
			
			//初始化类
			ApplicationOrgBo aorgbo = new ApplicationOrgBo(this.getFrameconn(),salaryid,this.userView);
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			
			//是否是共享帐套
			boolean isShare=true;//薪资类别是否共享
			if(StringUtils.isBlank(manager))
			{
				isShare=false;
			}
			
			if("0".equals(type)) {
				//保存数据
				String start_date = this.getFormHM().get("start_date") == null?"":(String)this.getFormHM().get("start_date");//开始时间
				String end_date = this.getFormHM().get("end_date") == null?"":(String)this.getFormHM().get("end_date");//结束时间
				gzbo.getCtrlparam().removeNode(SalaryCtrlParamBo.FILLING_AGENCYS);//先删除所有节点，然后一次性添加
				gzbo.getCtrlparam().setValue(SalaryCtrlParamBo.FILLING_AGENCYS,"start_date",start_date);
				gzbo.getCtrlparam().setValue(SalaryCtrlParamBo.FILLING_AGENCYS,"end_date",end_date);
				//因为有平行节点，这样如果仅仅更新，各种数据出问题，第一次添加节点，第二次相同节点的更新属性
				for(int i = 0; i < list.size(); i++){
					MorphDynaBean bean = list.get(i);
					String org_id = bean.get("org_id")==null?"":(String)bean.get("org_id");
					String[] org_idArray = org_id.split(",");
					StringBuffer org_idBuffer = new StringBuffer();
					for(int j = 0; j < org_idArray.length; j++) {
						org_idBuffer.append(org_idArray[j].substring(0,2)+PubFunc.decrypt(org_idArray[j].substring(2)));
					}
					gzbo.getCtrlparam().setValue(SalaryCtrlParamBo.FILLING_AGENCY,SalaryCtrlParamBo.FILLING_AGENCYS,"enable",bean.get("enable")==null?"":(String)bean.get("enable"),-1);
					gzbo.getCtrlparam().setValue(SalaryCtrlParamBo.FILLING_AGENCY,SalaryCtrlParamBo.FILLING_AGENCYS,"org_id",org_idBuffer.toString(),i);
					gzbo.getCtrlparam().setValue(SalaryCtrlParamBo.FILLING_AGENCY,SalaryCtrlParamBo.FILLING_AGENCYS,"username",bean.get("username")==null?"":(String)bean.get("username"),i);
					gzbo.getCtrlparam().setValue(SalaryCtrlParamBo.FILLING_AGENCY,SalaryCtrlParamBo.FILLING_AGENCYS,"fullname",bean.get("fullname")==null?"":(String)bean.get("fullname"),i);
					
					String username = bean.get("username")==null?"":(String)bean.get("username");
					if(StringUtils.isNotBlank(username)) {
						if(!isShare) {
							RecordVo vo=new RecordVo("operuser");
				    		vo.setString("username", username);
				    		ContentDAO dao=new ContentDAO(this.getFrameconn());
							RecordVo vo1 = dao.findByPrimaryKey(vo);
							String groupid = vo1.getString("groupid");
							if(!"1".equals(groupid))//超级管理员不需要找
								aorgbo.resetResource(gz_module,isShare,bean);
						}else {
							aorgbo.resetResource(gz_module,isShare,bean);
						}
					}
				}
				gzbo.getCtrlparam().saveParameter();
			
			}else if("1".equals(type)) {//保存薪资发放下发数据
				String A00Z2 = (String)this.getFormHM().get("A00Z2");//发放日期
				String A00Z3 = (String)this.getFormHM().get("A00Z3");//发放次数
				String insertOrUpdateFlag = (String)this.getFormHM().get("insertOrUpdateFlag");//是0插入还是1更新
				aorgbo.saveDataToGZReportingLog(list,A00Z2,A00Z3,insertOrUpdateFlag,gz_module);
			}else if("2".equals(type)) {
				String username = this.getFormHM().get("username")==null?"":(String) this.getFormHM().get("username");
				String org_id = this.getFormHM().get("org_id")==null?"":(String) this.getFormHM().get("org_id");
				aorgbo.deleteGzReport(username,org_id);
			}else if("3".equals(type)) {
				ArrayList beanOld = this.getFormHM().get("beanOld")==null?null:(ArrayList) this.getFormHM().get("beanOld");
				ArrayList beanNew = this.getFormHM().get("beanNew")==null?null:(ArrayList) this.getFormHM().get("beanNew");
				aorgbo.resetManageRange(isShare, beanOld, beanNew);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
