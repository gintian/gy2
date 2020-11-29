package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 评委会_获取全部
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetAllCommitteePersonTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {

		try {
			ArrayList<String> EspectList = new ArrayList<String>();// 需要排除人员列表
			EspectList = (ArrayList<String>)this.getFormHM().get("espectlist");
			
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("jobtitle_committee_00001");
			String sql = catche.getTableSql();
			
			ArrayList<String> AllPersonList = new ArrayList<String>();//所有选中人员
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
			AllPersonList = committeeBo.getAllPerson(sql, EspectList);
			
			this.getFormHM().put("allpersonlist", AllPersonList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
