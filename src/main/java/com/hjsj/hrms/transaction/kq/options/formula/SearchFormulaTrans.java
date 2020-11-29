package com.hjsj.hrms.transaction.kq.options.formula;

import com.hjsj.hrms.businessobject.kq.options.formula.KqFormulaBo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SearchFormulaTrans extends IBusiness{

	public void execute() throws GeneralException {
		String chkid="-1";
		KqFormulaBo kf=new KqFormulaBo(this.getFrameconn());
		ArrayList kqFormulaList=kf.getKqFormulaList();
		if(kqFormulaList!=null&&kqFormulaList.size()>0)
		{
			LazyDynaBean bean = (LazyDynaBean)kqFormulaList.get(0);
			chkid=(String)bean.get("chkid");
		}
		String formula=kf.getKqFormula(chkid);
		ArrayList kqItemList = new ArrayList();
		kqItemList=this.getKqItemList();
		this.getFormHM().put("fieldlist", kqItemList);
		this.getFormHM().put("kqFormulaList", kqFormulaList);
		this.getFormHM().put("kqFormulaId", chkid);
		this.getFormHM().put("formula", formula);
	}

	public ArrayList getKqItemList(){
		ArrayList list=DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		Field field=null;
 	    ArrayList setlist=new ArrayList();
 	    setlist.add(new CommonData("", ""));
		  for(int i=0;i<list.size();i++){
			  if(list.get(i)==null){
				  continue;
			  }
			  FieldItem fi=(FieldItem)list.get(i);
			  if("a0100".equalsIgnoreCase(fi.getItemid()) || "i9999".equalsIgnoreCase(fi.getItemid()))
				  continue;
		       CommonData datavo=new CommonData(fi.getItemid()+":"+fi.getItemdesc(),fi.getItemid()+":"+fi.getItemdesc());
		       setlist.add(datavo);
		  }
		  return setlist;
	}
}
