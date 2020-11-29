package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:AddTrainResourceTrans.java
 * </p>
 * <p>
 * Description:添加培训体系交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddTrainResourceTrans extends IBusiness {
	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String type = (String) hm.get("type");
		if (!TrainResourceBo.hasTrainResourcePrivByType(type, this.userView))
		    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.function.nopriv"),"",""));
		
		String priFldValue = (String) hm.get("priFldValue");
		if(priFldValue != null && priFldValue.length() > 0 && !"null".equalsIgnoreCase(priFldValue))
		    priFldValue = PubFunc.decrypt(SafeCode.decode(priFldValue));
		hm.remove("priFldValue");

		String a_code = (String) this.getFormHM().get("a_code");
		a_code = a_code != null ? a_code : "";
		
		TrainResourceBo bo = new TrainResourceBo(this.frameconn, type);
		String recTable = bo.getRecTable();
		String primaryField = bo.getPrimaryField();
		String recName = bo.getRecName();

		this.getFormHM().put("recName", recName);
		this.getFormHM().put("primaryField", primaryField);
		this.getFormHM().put("a_code", a_code);
		
		TrainCourseBo tb = new TrainCourseBo(this.userView);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList fieldList = DataDictionary.getFieldList(recTable,	Constant.USED_FIELD_SET);
		ArrayList fieldInfoList = new ArrayList();
		try {
			boolean isNew = false;
			if (priFldValue == null || "".equals(priFldValue))// 新建时候要生成主键
			{
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				priFldValue = idg.getId(recTable.toUpperCase() + "." + primaryField.toUpperCase());
				this.getFormHM().put("dispSaveContinue", "true");
				isNew = true;
			} else
				this.getFormHM().put("dispSaveContinue", "false");
			
			a_code = PubFunc.decrypt(SafeCode.decode(a_code));

			for (int i = 0; i < fieldList.size(); i++)// 循环字段
			{
				FieldItem fieldItem = (FieldItem) fieldList.get(i);
				String itemid = fieldItem.getItemid();
				String itemName = fieldItem.getItemdesc();
				String itemType = fieldItem.getItemtype();
				String codesetId = fieldItem.getCodesetid();
				// if(itemid.equalsIgnoreCase("r0700"))
				// continue;

				// if (fieldItem.getPriv_status() != 0) // 只加在有读写权限的指标
				// {
				FieldItemView fieldItemView = new FieldItemView();
				fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
				fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
				fieldItemView.setCodesetid(codesetId);
				fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
				fieldItemView.setDisplayid(fieldItem.getDisplayid());
				fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
				fieldItemView.setExplain(fieldItem.getExplain());
				fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
				fieldItemView.setItemdesc(itemName);
				fieldItemView.setItemid(itemid);
				fieldItemView.setItemlength(fieldItem.getItemlength());
				fieldItemView.setItemtype(itemType);
				fieldItemView.setModuleflag(fieldItem.getModuleflag());
				fieldItemView.setState(fieldItem.getState());
				fieldItemView.setUseflag(fieldItem.getUseflag());
				fieldItemView.setPriv_status(fieldItem.getPriv_status());
				fieldItemView.setRowflag(String.valueOf(fieldList.size() - 1)); // 在struts用来表示换行的变量
				fieldItemView.setFillable(fieldItem.isFillable());

				if (!fieldItem.isVisible())
					continue;

				if (isNew)// 新建
				{
					if (itemid.equals(primaryField)) {
						fieldItemView.setViewvalue(priFldValue);
						fieldItemView.setValue(priFldValue);
					} else if ("r0700".equals(itemid)) {

						fieldItemView.setValue(a_code);
						RecordVo vo = new RecordVo("codeitem");
						vo.setString("codesetid", "54");
						vo.setString("codeitemid", a_code);
						if(a_code!=null&&a_code.length()>0){
							vo = dao.findByPrimaryKey(vo);
							fieldItemView.setViewvalue(vo
									.getString("codeitemdesc"));
						} else {
							fieldItemView.setViewvalue("");
						}
					} else {
						fieldItemView.setViewvalue("");
						fieldItemView.setValue("");
					}
					if ("b0110".equalsIgnoreCase(itemid)){
					    fieldItemView.setViewvalue("公共资源");
                        fieldItemView.setValue("HJSJ");
						String code="";
						code = tb.getUnitIdByBusi();
						this.getFormHM().put("orgparentcode",code);
						if ("2".equals(type))
						    this.getFormHM().put("teachertype", "02"); 
					}
				} else // 修改
				{
					StringBuffer strsql = new StringBuffer();
					strsql.append("select " + itemid + " from ");
					strsql.append(recTable + " where ");
					strsql.append(primaryField + "='");
					strsql.append(priFldValue + "'");
					this.frowset = dao.search(strsql.toString());
					if (this.frowset.next()) {
						Object val = null;
						if ("D".equalsIgnoreCase(itemType) && Sql_switcher.searchDbServer() == Constant.ORACEL)// 日期型
							// oracle数据库必须这样取数据
							val = this.getFrowset().getDate(itemid);
						else
							val = this.frowset.getString(itemid);
						if (val == null) {
							fieldItemView.setViewvalue("");
							fieldItemView.setValue("");
						} else {
							if ("A".equals(itemType) || "M".equals(itemType)) {
								String value = (String) val;
								if (!"0".equals(codesetId)) {
									String codevalue = value;
									if (codevalue.trim().length() > 0 && codesetId != null	&& codesetId.trim().length() > 0)
									{
									    CodeItem codeItem = AdminCode.getCode(codesetId, codevalue);
									    String codeName = "";
									    if (null != codeItem)
									        codeName = AdminCode.getCode(codesetId,codevalue).getCodename(); 
									    fieldItemView.setViewvalue(codeName);
									}										
									else
										fieldItemView.setViewvalue("");
									fieldItemView.setValue(value != null ? value.toString() : "");

									if ("b0110".equalsIgnoreCase(itemid)) {
										String code="";
//										if(!userView.isSuper_admin()){
//											if(userView.getStatus()==4)
//												code=this.getUserView().getManagePrivCodeValue();
//											else{
//												String codeall = userView.getUnit_id();
//												if(codeall!=null&&codeall.length()>2)
//													code=codeall;//.split("`")[0].substring(2);
//												if("".equals(code))
//													code=this.getUserView().getManagePrivCodeValue();
//											}
//										}else
//											code=this.getUserView().getManagePrivCodeValue();
										code = tb.getUnitIdByBusi();
										this.getFormHM().put("orgparentcode",code);
										if ("HJSJ".equalsIgnoreCase(value.toUpperCase()))
											fieldItemView.setViewvalue("公共资源");
									}
									
									if ("r0412".equalsIgnoreCase(itemid))
                                    {
									    this.getFormHM().put("teachertype", codevalue); 
                                    }

								} else {
									fieldItemView.setViewvalue(value);
									fieldItemView.setValue(value);
								}
							} else if ("D".equals(itemType)) // 日期型有待格式化处理
							{
								if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
									String value = (String) val;

									if (value != null && value.length() >= 10
											&& fieldItem.getItemlength() == 10) {
										value = new FormatValue().format(
												fieldItem, value.substring(0,
														10));
										value = PubFunc
												.replace(value, ".", "-");
										fieldItemView.setViewvalue(value);
										fieldItemView.setValue(value);
									} else if (value != null
											&& value.toString().length() >= 10
											&& fieldItem.getItemlength() == 4) {
										value = new FormatValue().format(
												fieldItem, value
														.substring(0, 4));
										value = PubFunc
												.replace(value, ".", "-");
										fieldItemView.setViewvalue(value);
										fieldItemView.setValue(value);
									} else if (value != null
											&& value.toString().length() >= 10
											&& fieldItem.getItemlength() == 7) {
										value = new FormatValue().format(
												fieldItem, value
														.substring(0, 7));
										value = PubFunc
												.replace(value, ".", "-");
										fieldItemView.setViewvalue(value);
										fieldItemView.setValue(value);
									} else {
										fieldItemView.setViewvalue("");
										fieldItemView.setValue("");
									}
								} else if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
									Date dateVal = (Date) val;
									fieldItemView.setViewvalue(dateVal
											.toString());
									fieldItemView.setValue(dateVal.toString());
								}
							} else
							// 数值类型的有待格式化处理
							{
								String value = (String) val;
								fieldItemView.setValue(PubFunc.DoFormatDecimal(
										value != null ? value.toString() : "",
										fieldItem.getDecimalwidth()));
							}
						}
					}

				}
				fieldInfoList.add(fieldItemView);
			}

			if("r04".equalsIgnoreCase(recTable)) {
				StringBuffer strsql = new StringBuffer();
				strsql.append("select nbase,a0100 from r04");
				strsql.append(" where r0401=?");
				ArrayList<String> paramList = new ArrayList<String>();
				paramList.add(priFldValue);
				this.frowset = dao.search(strsql.toString(), paramList);
				if(this.frowset.next()) {
					String nbase = this.frowset.getString("nbase");
					nbase = StringUtils.isEmpty(nbase) ? "" : PubFunc.encrypt(nbase);
					String a0100 = this.frowset.getString("a0100");
					a0100 = StringUtils.isEmpty(a0100) ? "" : PubFunc.encrypt(a0100);
					this.getFormHM().put("nbase", nbase);
					this.getFormHM().put("a0100", a0100);
				}
			}

	} catch (SQLException e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	} finally
	{
	    this.getFormHM().put("fields", fieldInfoList);
	    this.getFormHM().put("primaryKeyVal", SafeCode.encode(PubFunc.encrypt(priFldValue)));
	}
}
}
