/*
 * Created on 2005-6-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AddSelfInfoTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String emp_e = (String)this.getFormHM().get("emp_e");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String dbpre = (String) hm.get("dbpre");
		String a0100 = (String) hm.get("a0100");
		String i9999 = (String) hm.get("pi9999");
		hm.remove("dbpre");
		hm.remove("a0100");
		hm.remove("i9999");


			String tablename = dbpre + emp_e; // 表的名称
			searchMessage(hm, dbpre, a0100, i9999, tablename, emp_e);

	}

	private void searchMessage(HashMap hm, String userbase, String A0100,
			String I9999, String tablename, String setname) throws GeneralException {
		List rs = null;
		List infoFieldList = null;
		List infoSetList = null;
		boolean deptvalue = false;
		boolean posvalue = false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		String actiontype = (String) this.getFormHM().get("actiontype");
		String link_field  = (String)this.getFormHM().get("link_field");
		String b0110field = (String)this.getFormHM().get("b0110field");
		String orderbyfield = (String)this.getFormHM().get("orderbyfield");


			infoFieldList = userView.getPrivFieldList(setname); // 获得当前子集的所有属性
			infoSetList = userView.getPrivFieldSetList(
					Constant.EMPLOY_FIELD_SET, 0); // 获得所有权限的子集

		ArrayList infoFieldViewList = new ArrayList(); // 保存处理后的属性

		try {
			if (!infoFieldList.isEmpty()) {

				boolean isExistData = false;
				if ("update".equals(actiontype)) // 若是修改取其值
				{
					StringBuffer strsql = new StringBuffer();
					strsql.append("select * from ");
					strsql.append(tablename);
					strsql.append(" where A0100='");
					strsql.append(A0100);
					strsql.append("'");
					if (!"A01".substring(1, 3).equals(setname.substring(1, 3))) // 如果子集的修改则条件有I9999
					{
						strsql.append(" and I9999=");
						strsql.append(I9999);
					}
					rs = ExecuteSQL.executeMyQuery(strsql.toString(), this
							.getFrameconn());
					isExistData = !rs.isEmpty();
				}
				String b0110value="";
				String a0101value="";
				{
					
					for(int i=0;i<infoFieldList.size();i++){
						FieldItem fieldItem = (FieldItem)infoFieldList.get(i);
						if(link_field.equalsIgnoreCase(fieldItem.getItemid())||b0110field.equalsIgnoreCase(fieldItem.getItemid())||orderbyfield.equalsIgnoreCase(fieldItem.getItemid()))
						{	infoFieldList.remove(i);
							i--;
						}
					}
					
					/*b0110value=com.hrms.frame.codec.SafeCode.decode((String)hm.get("b0110value"));
					b0110value=AdminCode.getCodeName("UN", b0110value);
					a0101value=com.hrms.frame.codec.SafeCode.decode((String)hm.get("a0101value"));
					
					FieldItem fieldItem = DataDictionary.getFieldItem("B0110");
					fieldItem.setItemid("b0110value");
					fieldItem.setReadonly(true);
					fieldItem.setCodesetid("0");
					infoFieldList.add(0, fieldItem);
					
					fieldItem = DataDictionary.getFieldItem("A0101");
					fieldItem.setItemid("a0101value");
					fieldItem.setReadonly(true);
					infoFieldList.add(1, fieldItem);*/
				}
				for (int i = 0; i < infoFieldList.size(); i++) // 字段的集合
				{
					FieldItem fieldItem = (FieldItem) infoFieldList.get(i);
					if (fieldItem.getPriv_status() != 0) // 只加在有读写权限的指标
					{
						
						// 为了在选择代码时方便而压入权限码开始

						// 为了在选择代码时方便而压入权限码结束
						FieldItemView fieldItemView = new FieldItemView();
						fieldItemView.setSequencename(fieldItem
								.getSequencename());
						fieldItemView.setSequenceable(fieldItem
								.isSequenceable());
						// fieldItemView=
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
						// 在struts用来表示换行的变量
						fieldItemView.setRowflag(String.valueOf(infoFieldList
								.size() - 1));
						if ("update".equals(actiontype) && isExistData) {
							LazyDynaBean rec = (LazyDynaBean) rs.get(0);
							if (isExistData) {
								if ("A".equals(fieldItem.getItemtype())
										|| "M".equals(fieldItem.getItemtype())) {
									if (!"0".equals(fieldItem.getCodesetid())) {
										String codevalue = rec.get(fieldItem
												.getItemid()) != null ? rec
												.get(fieldItem.getItemid())
												.toString() : "";
										if (codevalue != null
												&& codevalue.trim().length() > 0
												&& fieldItem.getCodesetid() != null
												&& fieldItem.getCodesetid()
														.trim().length() > 0)
											fieldItemView
													.setViewvalue(AdminCode.getCodeName(fieldItem.getCodesetid(), codevalue));
										else
											fieldItemView.setViewvalue("");
										
									} else {
										if("b0110value".equalsIgnoreCase(fieldItem
												.getItemid())){
											fieldItemView
											.setViewvalue(b0110value);
										}else if("a0101value".equalsIgnoreCase(fieldItem
												.getItemid())){
											fieldItemView
											.setViewvalue(a0101value);
										}else
										fieldItemView
												.setViewvalue(rec.get(fieldItem
														.getItemid()) != null ? rec
														.get(
																fieldItem
																		.getItemid())
														.toString()
														: "");
										
									}
									if("b0110value".equalsIgnoreCase(fieldItem
											.getItemid())){
									}else if("a0101value".equalsIgnoreCase(fieldItem
											.getItemid())){
									}else{
										fieldItemView.setValue(rec.get(fieldItem
												.getItemid()) != null ? rec.get(
												fieldItem.getItemid()).toString()
												: "");
									}
								} else if ("D".equals(fieldItem.getItemtype())) // 日期型有待格式化处理
								{
									if (rec.get(fieldItem.getItemid()) != null
											&& rec.get(fieldItem.getItemid())
													.toString().length() >= 10
											&& fieldItem.getItemlength() == 10) {
										fieldItemView
												.setViewvalue(new FormatValue()
														.format(
																fieldItem,
																rec
																		.get(
																				fieldItem
																						.getItemid()
																						.toLowerCase())
																		.toString()
																		.substring(
																				0,
																				10)));
										fieldItemView
												.setValue(new FormatValue()
														.format(
																fieldItem,
																rec
																		.get(
																				fieldItem
																						.getItemid()
																						.toLowerCase())
																		.toString()
																		.substring(
																				0,
																				10)));
									} else if (rec.get(fieldItem.getItemid()) != null
											&& rec.get(fieldItem.getItemid())
													.toString().length() >= 10
											&& fieldItem.getItemlength() == 4) {
										fieldItemView
												.setViewvalue(new FormatValue()
														.format(
																fieldItem,
																rec
																		.get(
																				fieldItem
																						.getItemid()
																						.toLowerCase())
																		.toString()
																		.substring(
																				0,
																				4)));
										fieldItemView
												.setValue(new FormatValue()
														.format(
																fieldItem,
																rec
																		.get(
																				fieldItem
																						.getItemid()
																						.toLowerCase())
																		.toString()
																		.substring(
																				0,
																				4)));
									} else if (rec.get(fieldItem.getItemid()) != null
											&& rec.get(fieldItem.getItemid())
													.toString().length() >= 10
											&& fieldItem.getItemlength() == 7) {
										fieldItemView
												.setViewvalue(new FormatValue()
														.format(
																fieldItem,
																rec
																		.get(
																				fieldItem
																						.getItemid()
																						.toLowerCase())
																		.toString()
																		.substring(
																				0,
																				7)));
										fieldItemView
												.setValue(new FormatValue()
														.format(
																fieldItem,
																rec
																		.get(
																				fieldItem
																						.getItemid()
																						.toLowerCase())
																		.toString()
																		.substring(
																				0,
																				7)));
									} else {
										fieldItemView.setViewvalue("");
										fieldItemView.setValue("");
									}
								} else // 数值类型的有待格式化处理
								{
									fieldItemView.setValue(PubFunc
											.DoFormatDecimal(rec.get(fieldItem
													.getItemid()) != null ? rec
													.get(fieldItem.getItemid())
													.toString() : "", fieldItem
													.getDecimalwidth()));
								}
							}

						} 
						fieldItemView.setFillable(fieldItem.isFillable());
						infoFieldViewList.add(fieldItemView);
					}
				}
				
			}else
				throw GeneralExceptionHandler.Handle(new GeneralException("您没有指标权限！"));

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			
			this.getFormHM().put("infofieldlist", infoFieldViewList); // 压回页面

		}
	}

	

	public ArrayList cloneList(ArrayList list) {
		ArrayList itemlist = new ArrayList();
		FieldItem item = null;
		for (int i = 0; i < list.size(); i++) {
			FieldItem fielditem = (FieldItem) list.get(i);
			item = (FieldItem) fielditem.clone();
			itemlist.add(item);
		}
		return itemlist;
	}

	
}
