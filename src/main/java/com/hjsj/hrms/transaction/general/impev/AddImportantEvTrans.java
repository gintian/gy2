/**
 * 
 */
package com.hjsj.hrms.transaction.general.impev;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:AddImportantEvTrans
 * </p>
 * <p>
 * Description:添加重要信息报告
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class AddImportantEvTrans extends IBusiness {

	/**
	 * 
	 */
	public AddImportantEvTrans() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String p0600 = (String) hm.get("p0600");
		p0600 = p0600 != null && p0600.trim().length() > 0 ? p0600 : "";
		hm.remove("p0600");
		this.getFormHM().put("p0600", p0600);
		List fieldlist = new ArrayList();
		List fieldList = DataDictionary.getFieldList("p06",
				Constant.ALL_FIELD_SET);
		FieldItem fieldItem = null;
		if ("".equals(p0600)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			this.getFormHM().put("fromdate", sdf.format(new Date()));
			this.getFormHM().put("todate", sdf.format(new Date()));
			this.getFormHM().remove("content");
			for (int i = 0; i < fieldList.size(); i++) {
				fieldItem = (FieldItem) fieldList.get(i);
				if ("p0600".equalsIgnoreCase(fieldItem.getItemid())
						|| "nbase".equalsIgnoreCase(fieldItem.getItemid())
						|| "a0100".equalsIgnoreCase(fieldItem.getItemid())
						|| "b0110".equalsIgnoreCase(fieldItem.getItemid())
						|| "e0122".equalsIgnoreCase(fieldItem.getItemid())
						|| "e01a1".equalsIgnoreCase(fieldItem.getItemid())
						|| "a0101".equalsIgnoreCase(fieldItem.getItemid())
						|| "p0603".equalsIgnoreCase(fieldItem.getItemid())
						|| "p0605".equalsIgnoreCase(fieldItem.getItemid())
						|| "p0607".equalsIgnoreCase(fieldItem.getItemid())
						|| "p0611".equalsIgnoreCase(fieldItem.getItemid())
						|| "p0609".equalsIgnoreCase(fieldItem.getItemid())) {
					continue;
				}
				fieldItem.setValue("");
				fieldItem.setViewvalue("");
				fieldlist.add(fieldItem);
			}
			this.getFormHM().put("fieldlist", fieldlist);
		} else {
			try {
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				RecordVo vo = new RecordVo("P06");
				vo.setString("p0600", p0600);
				vo = dao.findByPrimaryKey(vo);
				this.getFormHM().put("fromdate", vo.getString("p0603"));
				this.getFormHM().put("todate", vo.getString("p0605"));
				this.getFormHM().put("content", vo.getString("p0607"));
				for (int i = 0; i < fieldList.size(); i++) {
					fieldItem = (FieldItem) fieldList.get(i);
					if ("p0600".equalsIgnoreCase(fieldItem.getItemid())
							|| "nbase".equalsIgnoreCase(fieldItem.getItemid())
							|| "a0100".equalsIgnoreCase(fieldItem.getItemid())
							|| "b0110".equalsIgnoreCase(fieldItem.getItemid())
							|| "e0122".equalsIgnoreCase(fieldItem.getItemid())
							|| "e01a1".equalsIgnoreCase(fieldItem.getItemid())
							|| "a0101".equalsIgnoreCase(fieldItem.getItemid())
							|| "p0603".equalsIgnoreCase(fieldItem.getItemid())
							|| "p0605".equalsIgnoreCase(fieldItem.getItemid())
							|| "p0607".equalsIgnoreCase(fieldItem.getItemid())
							|| "p0611".equalsIgnoreCase(fieldItem.getItemid())
							|| "p0609".equalsIgnoreCase(fieldItem.getItemid())) {
						continue;
					}
					if ("0".equalsIgnoreCase(fieldItem.getCodesetid())) {
						fieldItem.setValue(vo.getString(fieldItem.getItemid()));
					} else {
						String temp = fieldItem.getCodesetid();
						String value = vo.getString(fieldItem.getItemid());
						fieldItem.setValue(value);
						RecordVo rvo = null;
						if ("@K".equalsIgnoreCase(temp)
								|| "UM".equalsIgnoreCase(temp)
								|| "UN".equalsIgnoreCase(temp)) {
							rvo = new RecordVo("organization");
							rvo.setString("codesetid", temp);
							rvo.setString("codeitemid", value);
							try {
								rvo = dao.findByPrimaryKey(rvo);
								fieldItem.setViewvalue(rvo
										.getString("codeitemdesc"));
							} catch (Exception e) {
								fieldItem.setViewvalue("");
							}
						}else{
							rvo = new RecordVo("codeitem");
							rvo.setString("codesetid", temp);
							rvo.setString("codeitemid", value);
							try {
								rvo = dao.findByPrimaryKey(rvo);
								fieldItem.setViewvalue(rvo
										.getString("codeitemdesc"));
							} catch (Exception e) {
								fieldItem.setViewvalue("");
							}
						}
					}
					fieldlist.add(fieldItem);
				}
				this.getFormHM().put("fieldlist", fieldlist);
			} catch (Exception e) {
				e.printStackTrace();
				GeneralExceptionHandler.Handle(e);
			} finally {
				this.getFormHM().put("fieldlist", fieldlist);
			}
		}
	}
}
