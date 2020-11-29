package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectPersonFilterFieldTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		String tabid = (String) map.get("tabid");
		String flag = (String) map.get("flag");
		String setname=(String)map.get("setname");
		if(setname!=null)
			map.remove("setname");
		map.remove("flag");
		String infor_type = (String) map.get("infor_type");
		map.remove("infor_type");
		if(infor_type!=null&&infor_type.length()>0)
		this.getFormHM().put("infor_type",infor_type);
		ArrayList list = new ArrayList();
		if ("1".equals(flag)) {
			TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			ArrayList celllist = bo.getAllCell();
			for (int i = 0; i < celllist.size(); i++) {
				LazyDynaBean abean = (LazyDynaBean) celllist.get(i);
				CommonData dataobj = null;
				// 去掉子集项
				if ("1".equals(abean.get("subflag")))
					continue;
				if ("M".equals(abean.get("field_type")))// 去掉备注型指标
					continue;
				if ("0".equals(abean.get("isvar"))) {
					// if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim())!=-1)
					// continue;
					if ("2".equals(abean.get("chgstate"))) {
						dataobj = new CommonData(
								abean.get("field_name").toString().trim()
										+ "_"
										+ abean.get("chgstate").toString()
												.trim(), "拟["
										+ abean.get("field_hz").toString()
												.trim() + "]");
					} else {
						dataobj = new CommonData(
								abean.get("field_name").toString().trim()
										+ "_"
										+ abean.get("chgstate").toString()
												.trim(), abean.get("field_hz")
										.toString().trim());
					}
				} else {
					// if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim())!=-1)
					// continue;
					dataobj = new CommonData(
							abean.get("field_name").toString(), abean.get(
									"field_hz").toString());
				}
				list.add(dataobj);
			}
			this.getFormHM().put("templateSetList", celllist);
		} else {
			try {

				ArrayList templateSetList = (ArrayList) this.getFormHM().get(
						"templateSetList");
				ArrayList pagelist = (ArrayList) this.getFormHM().get(
						"pagelist");

				String fieldSetSortStr = (String) this.getFormHM().get(
						"fieldSetSortStr");
				// String sortitem = (String)this.getFormHM().get("sortitem");
				if (fieldSetSortStr != null && fieldSetSortStr.length() > 0) {

					String temp[] = fieldSetSortStr.split(",");
					for (int i = 0; i < temp.length; i++) {
						for (int j = 0; j < templateSetList.size(); j++) {
							LazyDynaBean abean = (LazyDynaBean) templateSetList
									.get(j);
							if (("0".equals(abean.get("isvar")) && (abean.get(
									"field_name").toString().trim()
									+ "_" + abean.get("chgstate").toString()
									.trim()).equalsIgnoreCase(temp[i]))
									|| ("1".equals(abean.get("isvar")) && abean
											.get("field_name").toString()
											.trim().equalsIgnoreCase(temp[i]))) {

								// 去掉子集项
								if ("1".equals(abean.get("subflag")))
									break;
								if ("M".equals(abean.get("field_type")))// 去掉备注型指标
									break;
								CommonData dataobj = null;
								if ("0".equals(abean.get("isvar"))) {
									// if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim())!=-1)
									// break;
									if ("2".equals(abean.get("chgstate"))) {
										dataobj = new CommonData(abean.get(
												"field_name").toString().trim()
												+ "_"
												+ abean.get("chgstate")
														.toString().trim(),
												"拟["
														+ abean.get("field_hz")
																.toString()
																.trim() + "]");
									} else {
										dataobj = new CommonData(abean.get(
												"field_name").toString().trim()
												+ "_"
												+ abean.get("chgstate")
														.toString().trim(),
												abean.get("field_hz")
														.toString().trim());
									}
								} else {
									// if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim())!=-1)
									// break;
									dataobj = new CommonData(abean.get(
											"field_name").toString(), abean
											.get("field_hz").toString());
								}
								list.add(dataobj);
								break;
							}
						}
					}
				} else {
					for (int i = 0; i < templateSetList.size(); i++) {
						LazyDynaBean abean = (LazyDynaBean) templateSetList
								.get(i);
						CommonData dataobj = null;
						// 去掉子集项
						if ("1".equals(abean.get("subflag")))
							continue;
						if ("M".equals(abean.get("field_type")))// 去掉备注型指标
							continue;
						if ("0".equals(abean.get("isvar"))) {
							// if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim())!=-1)
							// continue;
							if ("2".equals(abean.get("chgstate"))) {
								dataobj = new CommonData(abean
										.get("field_name").toString().trim()
										+ "_"
										+ abean.get("chgstate").toString()
												.trim(), "拟["
										+ abean.get("field_hz").toString()
												.trim() + "]");
							} else {
								dataobj = new CommonData(abean
										.get("field_name").toString().trim()
										+ "_"
										+ abean.get("chgstate").toString()
												.trim(), abean.get("field_hz")
										.toString().trim());
							}
						} else {
							// if(sortitem!=null&&sortitem.indexOf(abean.get("field_name").toString().trim())!=-1)
							// continue;
							dataobj = new CommonData(abean.get("field_name")
									.toString(), abean.get("field_hz")
									.toString());
						}
						list.add(dataobj);
					}
				}

				ArrayList selectedFieldList = (ArrayList) this.getFormHM().get("selectedFieldList");
				// ArrayList selectedFieldList = new ArrayList();

			
				// / this.getFormHM().put("model", model);
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}

		}
		this.getFormHM().put("allList", list);
		this.getFormHM().put("selectedFieldList", new ArrayList());
		this.getFormHM().put("tabid", tabid);
		if(setname!=null){
			setname = SafeCode.decode(setname);
			this.getFormHM().put("table_name",setname);
		}
		// this.getFormHM().put("tableName",tableName);
		this.getFormHM().put("filterCondId", "");
	}

}
