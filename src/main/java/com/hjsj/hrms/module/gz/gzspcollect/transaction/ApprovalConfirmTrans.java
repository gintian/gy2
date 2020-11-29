package com.hjsj.hrms.module.gz.gzspcollect.transaction;

import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：ApprovalConfirmTrans 
 * 类描述： 审批确认
 * 创建人：zhaoxg
 * 创建时间：Jan 25, 2016 4:34:46 PM
 * 修改人：zhaoxg
 * 修改时间：Jan 25, 2016 4:34:46 PM
 * 修改备注： 
 * @version
 */
public class ApprovalConfirmTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			//是否来自待办 0 否 1是。
			String fromPending = this.getFormHM().containsKey("fromPending")?(String) this.getFormHM().get("fromPending"):"0";

			String subNoShowUpdateFashion=(String)this.getFormHM().get("subNoShowUpdateFashion");  //1不显示数据操作方式  0:显示数据操作方式
			SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			SalaryTemplateBo gzbo=bo.getSalaryTemplateBo();
			GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
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
			ff_bosdate = ff_bosdate.replaceAll("\\.", "-");
			String ff_count = (String) this.getFormHM().get("count");
			ff_count = PubFunc.decrypt(SafeCode.decode(ff_count));
	    	String collectPoint = (String) this.getFormHM().get("collectPoint");//汇总指标
	    	String selectID = (String) this.getFormHM().get("selectID");//选择的记录
	    	String cound = (String) this.getFormHM().get("cound");//人员筛选
			StringBuffer history_where = new StringBuffer();
			history_where.append(spbo.getCollectSubmitPriv(gzbo, selectID,cound, collectPoint,salaryid,ff_bosdate,ff_count));
			bo.submitGzDataFromHistory(setlist, typelist, items, uptypes, ff_bosdate, ff_count, history_where.toString());
			//如果从待办进入 则返回剩余可审批的数据条数
			if("1".equals(fromPending)){
				int pengdingNum=spbo.getRemainderNumber("salaryhistory",salaryid,ff_bosdate,ff_count);
				this.getFormHM().put("lastNumber",pengdingNum);
			}

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
