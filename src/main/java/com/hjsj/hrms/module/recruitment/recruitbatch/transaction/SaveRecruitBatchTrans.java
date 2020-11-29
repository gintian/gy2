package com.hjsj.hrms.module.recruitment.recruitbatch.transaction;

import com.hjsj.hrms.module.recruitment.recruitbatch.businessobject.RecruitBatchBo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：SaveRecruitBatchTrans
 * 类描述：保存招聘批次
 * 创建人：sunming 
 * 创建时间：2015-10-30
 * 
 * @version
 */
public class SaveRecruitBatchTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		try {
			/**type=1新增的保存  type=2修改的保存**/
			String type = (String) this.getFormHM().get("type");
			ArrayList list = (ArrayList) this.getFormHM().get("arr");
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			if("1".equals(type)){
			    list.remove(list.size()-1);
			    String id = idg.getId("Z01.Z0101");
			    list.add(id);
			}
			ArrayList<MorphDynaBean> fields = (ArrayList) this.getFormHM().get("fields");
			MorphDynaBean bean = (MorphDynaBean) this.getFormHM().get("fieldsValue");
			RecruitBatchBo bo = new RecruitBatchBo(this.getFrameconn(),this.userView);
			bo.synchroList(type,list,fields,bean);
			this.getFormHM().put("type", type);
			//zxj 20151104 清掉传入的参数，避免返回的无用数据的格式影响ajaxservlet
			this.getFormHM().clear();
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
