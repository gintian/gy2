package com.hjsj.hrms.module.officermanage;

import com.hjsj.hrms.module.officermanage.businessobject.CardViewService;
import com.hjsj.hrms.module.officermanage.businessobject.impl.CardViewServiceImpl;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

/**
 * 左侧人员列表查询与快速查询（支持姓名，拼音简码，唯一性指标）
 * @author Administrator
 *
 */
public class SearchOfficerTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		//根据人员范围 设置查询条件取相应人员
		try {
			String searchValue=(String)this.getFormHM().get("searchValue");
			List<LazyDynaBean> list=this.getQueryData(searchValue);
			
			this.getFormHM().put("dataobjs", list);
			this.getFormHM().put("flag", true);
		} catch (Exception e) {
			this.getFormHM().put("flag", true);
			this.getFormHM().put("errMsg", e.getMessage());
			e.printStackTrace();
		}
	}

	private List<LazyDynaBean> getQueryData(String queryString) throws Exception {
		CardViewService bo=new CardViewServiceImpl(this.userView,this.frameconn);
		StringBuffer sbf=bo.getOfficerSql(queryString,true);
		String[] fields= {"nbase","guidkey","A0100","A0101","B0110","E0122"};
		String page=(String)this.getFormHM().get("limit");
		String pageIndex=(String)this.getFormHM().get("page");
		PaginationManager paginationm=new PaginationManager(" select * from ( "+sbf.toString().substring(0, sbf.length()-10)+" ) mydata ","","","order by nbase,A0000",fields,"");
		paginationm.setBAllMemo(true);
        paginationm.setPagerows(Integer.parseInt(page));
        this.getFormHM().put("totalCount", paginationm.getMaxrows());
        List<LazyDynaBean> list=paginationm.getPage(Integer.parseInt(pageIndex));
		return list;
	}
}
