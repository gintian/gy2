package com.hjsj.hrms.utils.components.defineformula.transaction.tax;

import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.ArrayList;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：NewAndUpdateTaxTrans 
 * 类描述： 新增和修改税率表
 * 创建人：zhaoxg
 * 创建时间：Nov 27, 2015 5:28:42 PM
 * 修改人：zhaoxg
 * 修改时间：Nov 27, 2015 5:28:42 PM
 * 修改备注： 
 * @version
 */
public class NewAndUpdateTaxTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String taxid = (String) this.getFormHM().get("taxid");
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String opt = (String) this.getFormHM().get("opt");//1：新增税率表 2：修改税率表 3：修改基数 4：修改税率表 5：修改所得额，正算or反算 6：计税方式 
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
			if("1".equals(opt)){
				int taxitem =0;
				StringBuffer str= new StringBuffer("insert into gz_taxrate_item" +
						"(taxitem,taxid,ynse_down,ynse_up,sl,sskcs,flag,kc_base,description) values ");
				taxitem=bo.getTaxId("gz_taxrate_item","taxitem");
				str.append("(");
				str.append(taxitem+",");
				str.append(taxid+",'0','0','0','0','0','0','')");
				dao.insert(str.toString(),new ArrayList());
				this.getFormHM().put("taxitem", taxitem);
			}else if("2".equals(opt)){
				String taxitem = (String) this.getFormHM().get("taxitem");
				String value = (String) this.getFormHM().get("value");
				String field = (String) this.getFormHM().get("field");
				StringBuffer str = new StringBuffer();
				str.append("update gz_taxrate_item set ");
				str.append(field);
				if("description".equals(field))//对于非数字类型做特殊处理，由于传过来的是数值的null,这样有问题sunjian 2017-07-28
					str.append("=? ");
				else
					str.append("=" + Sql_switcher.isnull("NULLIF(?,'null')","0"));
				str.append(" where taxid=? ");
				str.append(" and taxitem=?");
				ArrayList list = new ArrayList();
				list.add(value);
				list.add(taxid);
				list.add(taxitem);
				dao.update(str.toString(),list);
			}else if("3".equals(opt)){
				String value = (String) this.getFormHM().get("value");
				String field = (String) this.getFormHM().get("field");
				StringBuffer str = new StringBuffer();
				str.append("update gz_tax_rate set ");
				str.append(field);
				str.append("=ISNULL(NULLIF(?,'null'),0) ");//为了防止后台传过来的是null导致错误
				str.append(" where taxid=? ");
				ArrayList list = new ArrayList();
				list.add(value);
				list.add(taxid);
				dao.update(str.toString(),list);
			}else if("4".equals(opt)){
				String itemid = (String) this.getFormHM().get("itemid");
				StringBuffer str = new StringBuffer();
				str.append("update salaryformula set standid = ?  where salaryid=? and itemid=?");
				ArrayList list = new ArrayList();
				list.add(taxid);
				list.add(salaryid);
				list.add(itemid);
				dao.update(str.toString(),list);
			}else if("5".equals(opt)){
				SalaryCtrlParamBo salarybo = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
				String income = (String) this.getFormHM().get("income");
				String mode = (String) this.getFormHM().get("mode");
				String itemid = (String) this.getFormHM().get("itemid");
	        	salarybo.setValue(SalaryCtrlParamBo.YS_FIELDITEM,"id",itemid,"mode",mode,income);
	        	salarybo.saveParameter();
			}else if("6".equals(opt)){
				String param = (String) this.getFormHM().get("param");
				String paramStr=getParam(param);
				StringBuffer str = new StringBuffer();
				str.append("update gz_tax_rate set param");
				str.append("=? ");
				str.append(" where taxid=? ");
				ArrayList list = new ArrayList();
				list.add(paramStr);
				list.add(taxid);
				dao.update(str.toString(),list);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 获取计税方式xml
	 * @param paramValue
	 * @return
	 */
	private String getParam(String paramValue){
		String return_str="";
		try{
			Element param = new Element("param");
			param.setAttribute("TaxModeCode",paramValue);
			Document myDocument = new Document(param);
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			return_str = outputter.outputString(myDocument);
		}catch(Exception e){
			e.printStackTrace();
		}
		return return_str;
	}
}
