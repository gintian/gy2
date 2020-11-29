package com.hjsj.hrms.transaction.general.inform.informcheck;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null&itemid.trim().length()>0?itemid:"";
		
		String formula = (String)this.getFormHM().get("formula");
		formula=formula!=null&formula.trim().length()>0?formula:"";
		formula=SafeCode.decode(formula);
		
		String desc = (String)this.getFormHM().get("desc");
		desc=desc!=null&desc.trim().length()>0?desc:"";
		desc = PubFunc.keyWord_reback(desc);
		formula = PubFunc.keyWord_reback(formula);
		String flag="";
		if(formula.length()>1){
			FieldItem item = DataDictionary.getFieldItem(itemid);
			String type = "A";
			if(item!=null)
				type=item.getItemtype();
			
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forSearch, YksjParser.LOGIC
					, YksjParser.forPerson,"Ht", "");
			
			yp.setCon(this.getFrameconn());
			boolean b = yp.Verify_where(formula.trim());
			if (b) {// 校验通过
				updateFormula(itemid,formula,desc);
				flag="ok";
			}else{
				flag = yp.getStrError();
			} 
		}else{
			if(formula.trim().length()<1)
				updateFormula(itemid,formula,desc);
			flag="ok";
		}
		this.getFormHM().put("info",SafeCode.encode(flag));
		
	}
	private void updateFormula(String itemid,String formula,String desc){
		ArrayList list = new ArrayList();
		formula=formula.length()<1?null:formula;
		desc=desc.length()<1?null:desc;
		list.add(formula);
		list.add(desc);
		list.add(itemid);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("update fielditem set auditingformula=?,auditinginformation=? where itemid=?");

		ContentDAO dao =  new ContentDAO(this.frameconn);
		try {
			dao.update(sqlstr.toString(),list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String type){
		int temp=1;
		if("A".equals(type)){
			temp=YksjParser.STRVALUE;
		}else if("D".equals(type)){
			temp=YksjParser.DATEVALUE;
		}else if("N".equals(type)){
			temp=YksjParser.FLOAT;
		}else if("L".equals(type)){
			temp=YksjParser.LOGIC;
		}else{
			temp=YksjParser.STRVALUE;
		}
		return temp;
	}

}
