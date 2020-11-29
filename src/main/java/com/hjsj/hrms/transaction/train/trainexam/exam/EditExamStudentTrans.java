package com.hjsj.hrms.transaction.train.trainexam.exam;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EditExamStudentTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5400 = (String) hm.get("r5400");
		r5400 =PubFunc.decrypt(SafeCode.decode(r5400));
		hm.remove("r5400");
		String nbase = (String) hm.get("nbase");
		nbase =PubFunc.decrypt(SafeCode.decode(nbase));
		hm.remove("nbase");
		String a0100 = (String) hm.get("a0100");
		a0100 =PubFunc.decrypt(SafeCode.decode(a0100));
		hm.remove("a0100");
		List itemList = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RecordVo vo = new RecordVo("r55");
			vo.setString("r5400", r5400);
			vo.setString("nbase", nbase);
			vo.setString("a0100", a0100);
			vo = dao.findByPrimaryKey(vo);

			List fieldList = DataDictionary.getFieldList("R55",
					Constant.USED_FIELD_SET);
			FieldItemView fieldItemView = null;
			FieldItem fieldItem = null;
			for (int i = 0; i < fieldList.size(); i++) {
				fieldItemView = new FieldItemView();
				fieldItem = (FieldItem) fieldList.get(i);
				if("D".equalsIgnoreCase(fieldItem.getItemtype())){
					Date date = vo.getDate(fieldItem.getItemid());
					if(date!=null)
						fieldItemView.setValue(DateUtils.format(date,"yyyy-MM-dd HH:mm:ss"));
				}else{
					fieldItemView.setValue(vo.getString(fieldItem.getItemid()));
				}
				if (!"0".equals(fieldItem.getCodesetid())) {
					if ("UN".equalsIgnoreCase(fieldItem.getCodesetid().toUpperCase())
							||"UM".equalsIgnoreCase(fieldItem.getCodesetid().toUpperCase())
							||"@K".equalsIgnoreCase(fieldItem.getCodesetid().toUpperCase())) {
						RecordVo voCodeItem = new RecordVo("organization");
						voCodeItem.setString("codesetid", fieldItem
								.getCodesetid());
						voCodeItem.setString("codeitemid", vo
								.getString(fieldItem.getItemid()));
						if (vo.getString(fieldItem.getItemid()) != null
								&& vo.getString(fieldItem.getItemid()).length() > 0) {
						    try{
						        voCodeItem = dao.findByPrimaryKey(voCodeItem);
						        fieldItemView.setViewvalue(voCodeItem.getString("codeitemdesc"));

						    }catch (Exception e) {
						        fieldItemView.setViewvalue("");
						    }
						} else
							fieldItemView.setViewvalue("");
					} else {

						RecordVo voCodeItem = new RecordVo("codeitem");
						voCodeItem.setString("codesetid", fieldItem
								.getCodesetid());
						voCodeItem.setString("codeitemid", vo
								.getString(fieldItem.getItemid()));
						if (vo.getString(fieldItem.getItemid()) != null
								&& vo.getString(fieldItem.getItemid())
										.length() > 0) {
							voCodeItem = dao.findByPrimaryKey(voCodeItem);
							fieldItemView.setViewvalue(voCodeItem
									.getString("codeitemdesc"));
						} else
							fieldItemView.setViewvalue("");

					}
				}
				fieldItemView.setAuditingFormula(fieldItem
						.getAuditingFormula());
				fieldItemView.setAuditingInformation(fieldItem
						.getAuditingInformation());
				fieldItemView.setCodesetid(fieldItem.getCodesetid());
				fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
				fieldItemView.setDisplayid(fieldItem.getDisplayid());
				fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
				fieldItemView.setExplain(fieldItem.getExplain());
				fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
				fieldItemView.setItemdesc(fieldItem.getItemdesc());
				fieldItemView.setItemid(fieldItem.getItemid());
				fieldItemView.setItemlength(fieldItem.getItemlength());
				fieldItemView.setItemtype(fieldItem.getItemtype());
				fieldItemView.setModuleflag(fieldItem.getModuleflag());
				fieldItemView.setState(fieldItem.getState());
				fieldItemView.setUseflag(fieldItem.getUseflag());
				fieldItemView.setPriv_status(fieldItem.getPriv_status());
				fieldItemView.setRowflag(String
						.valueOf(fieldList.size() - 1)); // 在struts用来表示换行的变量
				fieldItemView.setFillable(fieldItem.isFillable());
				fieldItemView.setVisible(true);
			
				itemList.add(fieldItemView.clone());
			}

		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("fieldlist", itemList);
		}
	}
}
