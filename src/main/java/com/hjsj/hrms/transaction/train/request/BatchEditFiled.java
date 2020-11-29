/**
 * 
 */
package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title:CourseTransAdd
 * </p>
 * <p>
 * Description:培训修改培训学员用户指标
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-11-01 12:02:43
 * </p>
 * 
 * @author LiWeichao
 * @version 1.0
 * 
 */
public class BatchEditFiled extends IBusiness {

	public void execute() throws GeneralException {
		List itemList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			//String sql = "select itemid from t_hr_busifield where FieldSetId='r40' and ownflag=0";
			//this.frowset = dao.search(sql);
			FieldItemView fieldItemView = null;
			FieldItem fieldItem = null;
			
			ArrayList list = DataDictionary.getFieldList("r40", 1);
			for (int i = 0; i < list.size(); i++) {
				fieldItem = (FieldItem)list.get(i);
				if(fieldItem==null|| "M".equalsIgnoreCase(fieldItem.getItemtype()))
					continue;
				if("R4001".equalsIgnoreCase(fieldItem.getItemid())|| "R4002".equalsIgnoreCase(fieldItem.getItemid())
						|| "R4005".equalsIgnoreCase(fieldItem.getItemid())
						|| "B0110".equalsIgnoreCase(fieldItem.getItemid())
						|| "E0122".equalsIgnoreCase(fieldItem.getItemid()))
					continue;
			//while(this.frowset.next()) {
				fieldItemView = new FieldItemView();
				//String itemid=this.frowset.getString("itemid");
				//fieldItem = DataDictionary.getFieldItem(itemid, "r40");
				
				fieldItemView.setAuditingFormula(fieldItem
						.getAuditingFormula());
				fieldItemView.setAuditingInformation(fieldItem
						.getAuditingInformation());
				fieldItemView.setCodesetid(fieldItem.getCodesetid());
				fieldItemView.setDecimalwidth(fieldItem
						.getDecimalwidth());
				fieldItemView.setDisplayid(fieldItem.getDisplayid());
				fieldItemView.setDisplaywidth(fieldItem
						.getDisplaywidth());
				fieldItemView.setExplain(fieldItem.getExplain());
				fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
				fieldItemView.setItemdesc(fieldItem.getItemdesc());
				fieldItemView.setItemid(fieldItem.getItemid());
				fieldItemView.setItemlength(fieldItem.getItemlength());
				fieldItemView.setItemtype(fieldItem.getItemtype());
				fieldItemView.setModuleflag(fieldItem.getModuleflag());
				fieldItemView.setState(fieldItem.getState());
				fieldItemView.setUseflag(fieldItem.getUseflag());
				fieldItemView
						.setPriv_status(fieldItem.getPriv_status());
				//fieldItemView.setRowflag(String.valueOf(fieldList
				//		.size() - 1)); // 在struts用来表示换行的变量
				fieldItemView.setFillable(fieldItem.isFillable());
				
				itemList.add(fieldItemView.clone());
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("itemlist", itemList);
		}
	}
}
