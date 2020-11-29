package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SearchFormulaTrans extends IBusiness{

	public void execute() throws GeneralException {
		String chkid="-1";
		TrainClassBo pf=new TrainClassBo(this.getFrameconn());
		ArrayList pxFormulaList=pf.getPxFormulaList();
		if(pxFormulaList!=null&&pxFormulaList.size()>0)
		{
			LazyDynaBean bean = (LazyDynaBean)pxFormulaList.get(0);
			chkid=(String)bean.get("chkid");
		}
		String formula=pf.getPxFormula(chkid);
		ArrayList pxItemList = new ArrayList();
		pxItemList=this.getPxItemList();
		this.getFormHM().put("fieldlist", pxItemList);
		this.getFormHM().put("trainFormulaList", pxFormulaList);
		this.getFormHM().put("trainFormulaId", chkid);
		this.getFormHM().put("formula", formula);
	}

	public ArrayList getPxItemList() {
		String itemid = "";
		String itemdesc = "";
		String itemtype = "";
		String codeflag = "";
		ArrayList setlist = new ArrayList();
		setlist.add(new CommonData("", ""));

		String sql = "select itemid,itemdesc,itemtype,codeflag from t_hr_busifield where fieldsetid='R31' and useflag='1'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				itemid = this.frowset.getString("itemid");
				itemdesc = this.frowset.getString("itemdesc");
				itemtype = this.frowset.getString("itemtype");
				codeflag = this.frowset.getString("codeflag");
				if ("M".equalsIgnoreCase(itemtype) || ("A".equalsIgnoreCase(itemtype) && "1".equalsIgnoreCase(codeflag)))
					continue;
				CommonData datavo = new CommonData(itemid + ":" + itemdesc, itemid + ":" + itemdesc);
				setlist.add(datavo);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setlist;
	}
}

