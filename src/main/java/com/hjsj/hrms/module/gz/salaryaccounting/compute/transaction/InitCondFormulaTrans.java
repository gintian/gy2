package com.hjsj.hrms.module.gz.salaryaccounting.compute.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：InitCondFormulaTrans 
 * 类描述：初始化计算公式列表
 * 创建人：zhaoxg
 * 创建时间：Jun 5, 2015 10:20:54 AM
 * 修改人：zhaoxg
 * 修改时间：Jun 5, 2015 10:20:54 AM
 * 修改备注： 
 * @version
 */
public class InitCondFormulaTrans extends IBusiness {


	@Override
    public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();

		String salaryid = (String)hm.get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		salaryid=salaryid!=null&&salaryid.length()>0?salaryid:"";

		SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
		//是否勾选“非写权限指标参与计算” 0:仅显示有权限指标的计算公式。 1:全部计算公式
		String field_priv=bo.getCtrlparam().getValue(SalaryCtrlParamBo.FIELD_PRIV);
		if(field_priv==null|| "".equals(field_priv))
			field_priv="1";

		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer str = new StringBuffer();
		str.append("select itemid,useflag,hzname,itemname,runflag,itemtype");
		str.append(" from salaryformula where salaryid="+salaryid);
		str.append(" order by sortid,itemid desc");
		ArrayList list = new ArrayList();	
		try {
			RowSet rs = dao.search(str.toString());
			while(rs.next()){
				HashMap map = new HashMap();
				boolean uerflag = false;
				if("1".equals(rs.getString("useflag"))){
					uerflag = true;
				}
				map.put("useflag", uerflag);
				map.put("hzname", rs.getString("hzname"));
				map.put("runflag", rs.getString("runflag"));
				map.put("itemid", rs.getString("itemid"));
				map.put("itemname", rs.getString("itemname"));
				String itemname=rs.getString("itemname");
				if("1".equals(field_priv))
				{
					list.add(map);
				}
				else
				{
					String state=this.userView.analyseFieldPriv(itemname);
					if("0".equals(state))
						state=this.userView.analyseFieldPriv(itemname,0);
					if("2".equals(state)||this.userView.isSuper_admin())
						list.add(map);
				}
			}
			hm.put("data", list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
