package com.hjsj.hrms.module.recruitment.recruitprocess.transaction;

import com.hjsj.hrms.module.recruitment.recruitprocess.businessobject.CodeItemBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchCodeitemTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String flag = (String)this.getFormHM().get("flag");
		/**
		 * 根据ajax传过来的codeitemId进行查询父节点ID，查询出当前传入的codeitem的codesetId是单位UN，部门UM，岗位@K，
		 * 当传入为部门时，查询出单位，当传入为岗位时，则查询出部门和单位，岗位分两种情况，在部门下或者单位下，因此查询出父节点时，
		 * 也需查出父节点codesetID进行判断，当并将父节点标志保存至from中，页面js进行取值判断，赋值
		 * */
		try {
			String codeitemId = (String)this.getFormHM().get("codeitemId");
			CodeItemBo bo = new CodeItemBo(this.frameconn, this.userView);
			ArrayList codeInfo = (ArrayList)bo.getCodeInfo(codeitemId);
			String getCodeSet = (String)codeInfo.get(0);
			String codeUNName = "";//单位
			String codeUMName = "";//部门
			//当传入的为部门时，只进行查询单位
			if("UM".equalsIgnoreCase(getCodeSet))
			{
				ArrayList codeUNInfo = bo.getCodeParenteInfo((String)codeInfo.get(1),getCodeSet);
//				codeUNName = (String)codeInfo.get(1)+"`"+(String)codeUNInfo.get(2);
				codeUNName = (String)codeUNInfo.get(1)+"`"+(String)codeUNInfo.get(2);
			}else if("@K".equalsIgnoreCase(getCodeSet))
			{
				//当传入的为岗位时，需进行查询部门及单位
				ArrayList codeParentInfo = bo.getCodeParenteInfo((String)codeInfo.get(1),getCodeSet);
				String codeSet = (String)codeParentInfo.get(0);
				String codeItem = (String)codeParentInfo.get(1);
				codeUMName = (String)codeInfo.get(1)+"`"+(String)codeParentInfo.get(2);
				//判断当前节点是单位或者部门
				if("UM".equalsIgnoreCase(codeSet))
				{
					ArrayList codeUNInfo = bo.getCodeParenteInfo(codeItem,codeSet);
					codeUNName = (String)codeUNInfo.get(1)+"`"+(String)codeUNInfo.get(2);
				}
				this.getFormHM().put("codeSet", codeSet);
			}
			this.getFormHM().put("getCodeSet", getCodeSet);
			this.getFormHM().put("codeUMName", codeUMName);
			this.getFormHM().put("codeUNName", codeUNName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
