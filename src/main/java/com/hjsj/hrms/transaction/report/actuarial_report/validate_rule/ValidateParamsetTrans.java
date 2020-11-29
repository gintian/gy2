package com.hjsj.hrms.transaction.report.actuarial_report.validate_rule;

import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ValidateParamsetTrans extends IBusiness {

	public void execute() throws GeneralException {
		String error = "";
		TargetsortBo targetsortBo = new TargetsortBo(this.getFrameconn());
		String number2 = "0123456789.";
		String number = "0123456789";
		try {
			HashMap hm = this.getFormHM();
			// String date=(String)hm.get("date");
			// hm获得值,名字从组合里取
			// 获取组合

			boolean subUnitListflag = false;
			// 验证表三
			if (hm.get("paramcopy") != null
					&& hm.get("paramcopy").toString().length() > 0) {
				String paramcopy = hm.get("paramcopy").toString();
				// System.out.println(paramcopy);
				if (paramcopy.indexOf(",") != -1) {
					String paramlist[] = paramcopy.split(",");
					for (int i = 0; i < paramlist.length; i++) {
						if (hm.get(paramlist[i]) != null
								&& hm.get(paramlist[i]).toString().length() > 0
								&& "differ".equals(paramlist[i])) {
							String value = hm.get(paramlist[i]).toString();
							boolean flag = false;
							 if(value.indexOf(".")!=-1){
															
							 if(value.indexOf(".")!=value.lastIndexOf("."))
							 flag=true;
							 }

							for (int j = 0; j < value.length(); j++) {
								if (number2.indexOf(value.charAt(j)) == -1)
									flag = true;
								if (value.charAt(0)=='0'&&value.length()>1&&value.charAt(1)!='.') {
										flag = true;
								}
							}
							if (flag) {
								error = "表3 财务信息-差异金额中的数据请输入正数";
								break;
							}
							if(value.indexOf(".")!=-1&&value.substring(value.indexOf(".")+1,
									 value.length()).length()!=1){
									 error="表3 财务信息-差异金额中的数据请保留一位小数";
									 break;
									 }
							continue;
						}
						if (hm.get(paramlist[i]) != null
								&& hm.get(paramlist[i]).toString().length() > 0
								&& "differPercent".equals(paramlist[i])) {
							String value = hm.get(paramlist[i]).toString();

							boolean flag = false;
							 if(value.indexOf(".")!=-1){
															
							 if(value.indexOf(".")!=value.lastIndexOf("."))
							 flag=true;
							 }

							for (int j = 0; j < value.length(); j++) {
								if (number2.indexOf(value.charAt(j)) == -1)
									flag = true;
								if (value.charAt(0)=='0'&&value.length()>1&&value.charAt(1)!='.') {
									flag = true;
							}
							}
							if (flag) {
								error = "表3 财务信息-差异率中的数据请输入正数";
								break;
							}
							if (Double.parseDouble(value) >= 100.0) {
								error = "表3 财务信息-差异率中的数据请输入小于100的数";
								break;
							}
							 if(value.indexOf(".")!=-1&&value.substring(value.indexOf(".")+1,
							 value.length()).length()!=1){
							 error="表3 财务信息-差异率中的数据请保留一位小数";
							 break;
							 }

							continue;
						}
						if (hm.get(paramlist[i]) != null
								&& hm.get(paramlist[i]).toString().length() > 0
								&& paramlist[i].indexOf("differ") != -1&& paramlist[i].indexOf("differPercent") == -1) {
							String value = hm.get(paramlist[i]).toString();
							boolean flag = false;
							 if(value.indexOf(".")!=-1){
															
							 if(value.indexOf(".")!=value.lastIndexOf("."))
							 flag=true;
							 }
							for (int j = 0; j < value.length(); j++) {
								if (number2.indexOf(value.charAt(j)) == -1) {
									flag = true;
									break;
								}
								if (value.charAt(0)=='0'&&value.length()>1&&value.charAt(1)!='.') {
									flag = true;
							}
							}
							if (flag) {
								if ("medic_differ_1"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-离休人员- 医疗福利差异金额中的数据请输入正数";
								if ("medic_differ_2"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-退休人员-医疗福利差异金额中的数据请输入正数";
								if ("medic_differ_3"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-内退人员-医疗福利差异金额中的数据请输入正数";
								if ("medic_differ_4"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-遗属-除医疗福利差异金额中的数据请输入正数";
								if ("other_differ_1"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-离休人员-  其它福利差异金额中的数据请输入正数";
								if ("other_differ_2"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-退休人员- 其它福利差异金额中的数据请输入正数";
								if ("other_differ_3"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-内退人员- 其它福利差异金额中的数据请输入正数";
								if (!"".equals(error))
									break;
							}
							if(value.indexOf(".")!=-1&&value.substring(value.indexOf(".")+1,
									 value.length()).length()!=1){
									 if("medic_differ_1".equalsIgnoreCase(paramlist[i]))
									 error="表5 人员变动及人均福利对照表-离休人员- 医疗福利差异金额中的数据请保留一位小数";
									 if("medic_differ_2".equalsIgnoreCase(paramlist[i]))
									 error="表5 人员变动及人均福利对照表-退休人员-医疗福利差异金额中的数据请保留一位小数";
									 if("medic_differ_3".equalsIgnoreCase(paramlist[i]))
									 error="表5 人员变动及人均福利对照表-内退人员-医疗福利差异金额中的数据请保留一位小数";
									 if("medic_differ_4".equalsIgnoreCase(paramlist[i]))
									 error="表5 人员变动及人均福利对照表-遗属-除医疗福利差异金额中的数据请保留一位小数";
									 if("other_differ_1".equalsIgnoreCase(paramlist[i]))
									 error="表5 人员变动及人均福利对照表-离休人员- 其它福利差异金额中的数据请保留一位小数";
									 if("other_differ_2".equalsIgnoreCase(paramlist[i]))
									 error="表5 人员变动及人均福利对照表-退休人员- 其它福利差异金额中的数据请保留一位小数";
									 if("other_differ_3".equalsIgnoreCase(paramlist[i]))
									 error="表5 人员变动及人均福利对照表-内退人员- 其它福利差异金额中的数据请保留一位小数";
									 break;
									 }
						}
						if (hm.get(paramlist[i]) != null
								&& hm.get(paramlist[i]).toString().length() > 0
								&& paramlist[i].indexOf("differPercent") != -1) {
							String value = hm.get(paramlist[i]).toString();

							boolean flag = false;
							 if(value.indexOf(".")!=-1){
															
							 if(value.indexOf(".")!=value.lastIndexOf("."))
							 flag=true;
							 }
							for (int j = 0; j < value.length(); j++) {
								if (number2.indexOf(value.charAt(j)) == -1)
									flag = true;
								if (value.charAt(0)=='0'&&value.length()>1&&value.charAt(1)!='.') {
									flag = true;
							}
							}
							if (flag) {

								if ("medic_differPercent_1"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-离休人员- 医疗福利差异率中的数据请输入正数";
								if ("medic_differPercent_2"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-退休人员-医疗福利差异率中的数据请输入正数";
								if ("medic_differPercent_3"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-内退人员-医疗福利差异率中的数据请输入正数";
								if ("medic_differPercent_4"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-遗属-除医疗福利差异率中的数据请输入正数";
								if ("other_differPercent_1"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-离休人员-  其它福利差异率中的数据请输入正数";
								if ("other_differPercent_2"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-退休人员- 其它福利差异率中的数据请输入正数";
								if ("other_differPercent_3"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-内退人员- 其它福利差异率中的数据请输入正数";
								break;
							}
							if (Double.parseDouble(value) >= 100.0) {
								if ("medic_differPercent_1"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-离休人员- 医疗福利差异率中的数据请输入小于100的数";
								if ("medic_differPercent_2"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-退休人员-医疗福利差异率中的数据请输入小于100的数";
								if ("medic_differPercent_3"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-内退人员-医疗福利差异率中的数据请输入小于100的数";
								if ("medic_differPercent_4"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-遗属-除医疗福利差异率中的数据请输入小于100的数";
								if ("other_differPercent_1"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-离休人员-  其它福利差异率中的数据请输入小于100的数";
								if ("other_differPercent_2"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-退休人员- 其它福利差异率中的数据请输入小于100的数";
								if ("other_differPercent_3"
										.equalsIgnoreCase(paramlist[i]))
									error = "表5 人员变动及人均福利对照表-内退人员- 其它福利差异率中的数据请输入小于100的数";
								break;
							}
							 if(value.indexOf(".")!=-1&&value.substring(value.indexOf(".")+1,
							 value.length()).length()!=1){
							 if("medic_differPercent_1".equalsIgnoreCase(paramlist[i]))
							 error="表5 人员变动及人均福利对照表-离休人员- 医疗福利差异率中的数据请保留一位小数";
							 if("medic_differPercent_2".equalsIgnoreCase(paramlist[i]))
							 error="表5 人员变动及人均福利对照表-退休人员-医疗福利差异率中的数据请保留一位小数";
							 if("medic_differPercent_3".equalsIgnoreCase(paramlist[i]))
							 error="表5 人员变动及人均福利对照表-内退人员-医疗福利差异率中的数据请保留一位小数";
							 if("medic_differPercent_4".equalsIgnoreCase(paramlist[i]))
							 error="表5 人员变动及人均福利对照表-遗属-除医疗福利差异率中的数据请保留一位小数";
							 if("other_differPercent_1".equalsIgnoreCase(paramlist[i]))
							 error="表5 人员变动及人均福利对照表-离休人员- 其它福利差异率中的数据请保留一位小数";
							 if("other_differPercent_2".equalsIgnoreCase(paramlist[i]))
							 error="表5 人员变动及人均福利对照表-退休人员- 其它福利差异率中的数据请保留一位小数";
							 if("other_differPercent_3".equalsIgnoreCase(paramlist[i]))
							 error="表5 人员变动及人均福利对照表-内退人员- 其它福利差异率中的数据请保留一位小数";
							 break;
							 }
						}
					}
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		if ("".equals(error)) {
			try {
				HashMap hm = this.getFormHM();
				// String date=(String)hm.get("date");
				// hm获得值,名字从组合里取
				// 获取组合
				boolean subUnitListflag = false;
				// 验证表三

				if (hm.get("paramcopy") != null
						&& hm.get("paramcopy").toString().length() > 0) {
					String paramcopy = hm.get("paramcopy").toString();
					// System.out.println("paramcopy:"+paramcopy);
					// 传个map
					HashMap map = new HashMap();
					if (paramcopy.indexOf(",") != -1) {
						String paramlist[] = paramcopy.split(",");
						for (int i = 0; i < paramlist.length; i++) {
							// System.out.println(paramlist[i]+"="+hm.get(paramlist[i]));
							map.put(paramlist[i], hm.get(paramlist[i]));
						}
					}
					targetsortBo.saveView_Value(paramcopy, map, this
							.getFrameconn());
				}

			}

			catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		this.getFormHM().put("error", error);
	}

}
