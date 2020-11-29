package com.hjsj.hrms.transaction.gz.templateset.spformula;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchSpFormulaTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)map.get("salaryid");
			String modelflag = (String)map.get("gz_module");
			if(modelflag!=null && modelflag.trim().length()>0 && "3".equals(modelflag)){
				execute2();
				return;
			}
			String gz_module=(String)this.getFormHM().get("gz_module");
			SalaryTemplateBo stb = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid));
			ArrayList spFormulaList=stb.getSpFormulaList(salaryid);
			String chkid="-1";
			String opt=(String)map.get("opt");
			if(spFormulaList!=null&&spFormulaList.size()>0&& "0".equals(opt))
			{
				LazyDynaBean bean = (LazyDynaBean)spFormulaList.get(0);
				chkid=(String)bean.get("chkid");
			}
			else if(this.getFormHM().get("spFormulaId")!=null&&!"".equals((String)this.getFormHM().get("spFormulaId")))
			{
				chkid=(String)this.getFormHM().get("spFormulaId");
			}
			String itemid="";
			ArrayList salaryItemList = new ArrayList();
			salaryItemList=stb.getSalaryItemList(salaryid);
			String formula=stb.getSpFormula(chkid);
			if(spFormulaList==null||spFormulaList.size()==0)
			{
				formula="";
				chkid="-1";
			}
			this.getFormHM().put("spFormulaList", spFormulaList);
			this.getFormHM().put("formula", formula);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("gz_module", gz_module);
			this.getFormHM().put("salaryItemList", salaryItemList);
			this.getFormHM().put("itemid", itemid);
			this.getFormHM().put("spFormulaId", chkid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * Description: 精算报表
	 * @Version1.0 
	 * Nov 26, 2012 1:48:06 PM Jianghe created
	 * @throws GeneralException
	 */
	public void execute2() throws GeneralException {
		try
		{
			SalaryTemplateBo stb = new SalaryTemplateBo(this.getFrameconn());
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String gz_module = (String)map.get("gz_module");
			ArrayList spFormulaList=this.getSpFormulaList();
			String chkid="-1";
			String opt=(String)map.get("opt");
			if(spFormulaList!=null&&spFormulaList.size()>0&& "0".equals(opt))
			{
				LazyDynaBean bean = (LazyDynaBean)spFormulaList.get(0);
				chkid=(String)bean.get("chkid");
			}
			else if(this.getFormHM().get("spFormulaId")!=null&&!"".equals((String)this.getFormHM().get("spFormulaId")))
			{
				chkid=(String)this.getFormHM().get("spFormulaId");
			}
			String itemid="";
			ArrayList salaryItemList = new ArrayList();
			salaryItemList=this.getSalaryItemList();
			String formula=stb.getSpFormula(chkid);
			if(spFormulaList==null||spFormulaList.size()==0)
			{
				formula="";
				chkid="-1";
			}
			this.getFormHM().put("spFormulaList", spFormulaList);
			this.getFormHM().put("formula", formula);
			this.getFormHM().put("gz_module", gz_module);
			this.getFormHM().put("salaryItemList", salaryItemList);
			this.getFormHM().put("itemid", itemid);
			this.getFormHM().put("spFormulaId", chkid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public ArrayList getSpFormulaList()
	{
		ArrayList list = new ArrayList();
		try
		{
			StringBuffer sql = new StringBuffer();
			sql.append("select chkid,name,validflag,formula  from hrpchkformula where 1=1  ");
				sql.append("and flag=3  ");
			sql.append(" order by seq");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = null;
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("chkid",rs.getString("chkid"));
				bean.set("name",rs.getString("name"));
				bean.set("validflag", rs.getString("validflag"));
				//bean.set("formula", )
				list.add(bean);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getSalaryItemList()
	{
		ArrayList list = new ArrayList();
		try
		{
			ArrayList fielditemlist = new ArrayList();

			fielditemlist = DataDictionary.getFieldList("U02",
					Constant.USED_FIELD_SET);
			list.add(new CommonData("",""));
			for (int i = 0; i < fielditemlist.size(); i++) {
				if (fielditemlist.get(i) == null)
					continue;
				FieldItem fielditem = (FieldItem) fielditemlist.get(i);
				list.add(new CommonData(fielditem.getItemid().toLowerCase()+":"+fielditem.getItemdesc(),fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc()));
			}
			list.add(new CommonData("escope:人员范围","ESCOPE:人员范围"));
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}

}
