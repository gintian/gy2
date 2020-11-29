package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 检查计算公式
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jul 31, 2006:4:52:37 PM
 * </p>
 * 
 * @author sx
 * @version 1.0
 * 
 */
public class GetCheckExpreTrans extends IBusiness {

	public void execute() throws GeneralException {
		String exp_field = this.formHM.get("exp_field").toString();

		String c_expr = (String) this.getFormHM().get("c_expr");
		c_expr = PubFunc.keyWord_reback(c_expr);
		this.getFormHM().put("c_expr", c_expr);
		
		/* zxj zxj 20141018 该验证不完善，暂时废除（不是所有的#。。#格式都不支持）
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("#(.+?)#"); // 正则表达式，匹配 #........#
        java.util.regex.Matcher m = p.matcher(c_expr);
        String dateStr = null;
        while (m.find()) { // 在 str 中查找正则表达式匹配的部分
        	  dateStr = m.group(1); // 获取日期，即两个#之间的部分
              try {
            	  SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            	  if(!dateStr.equals(df.format(df.parse(dateStr))))//抛异常就不是正确格式
            	  {
            		  SimpleDateFormat sf = new SimpleDateFormat("yyyy.M.d");
            		  if(!dateStr.equals(sf.format(sf.parse(dateStr)))){
            			  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.d");
            			  if(!dateStr.equals(sdf.format(sdf.parse(dateStr)))){
            			  this.getFormHM().put("sige", "1");
      					  this.getFormHM().put("sigh",
      							ResourceFactory.getProperty("errors.query.expression"));
      					  this.getFormHM().put("errormsg", "#"+dateStr+"#^^^^此处必须是日期型，格式为#yyyy.mm.dd#，如#2002.5.16#");
      					  this.getFormHM().put("expr_flag", "0");
      					  this.getFormHM().put("expr_flag", "0");
      					  return;
            			  }
            		  }
            	  }
              } catch (Exception e) {
					this.getFormHM().put("sige", "1");
					this.getFormHM().put("sigh",
							ResourceFactory.getProperty("errors.query.expression"));
					this.getFormHM().put("errormsg", "#"+dateStr+"#^^^^此处必须是日期型，格式为#yyyy.mm.dd#，如#2002.5.16#");
					this.getFormHM().put("expr_flag", "0");
					this.getFormHM().put("expr_flag", "0");
					return;
              }
        }
        */
		if (c_expr != null && c_expr.length() > 0) {
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);

			YksjParser yp = new YksjParser(getUserView(), alUsedFields, 
			        YksjParser.forSearch, getFieldType(exp_field), YksjParser.forPerson, 
			        "Ht", "");
			yp.setCon(this.getFrameconn());

			if(!yp.Verify_where(c_expr.trim())) {
				String strErrorMsg = yp.getStrError();
				this.getFormHM().put("sige", "1");
				this.getFormHM().put("sigh",
						ResourceFactory.getProperty("errors.query.expression"));
				this.getFormHM().put("errormsg", strErrorMsg);
			} else {
				this.getFormHM().put("sige", "2");
				this.getFormHM().put("sigh",
						ResourceFactory.getProperty("kq.formula.tcheck"));
			}
		}
		this.getFormHM().put("expr_flag", "0");

	}

	public int getFieldType(String itemId)
	{
		int fieldType = -1;
		
		FieldItem fieldItem = DataDictionary.getFieldItem(itemId);
		String itemType = fieldItem.getItemtype();
		if ("N".equalsIgnoreCase(itemType))
		{
			if (0 == fieldItem.getDecimalwidth())
				fieldType = YksjParser.INT;
			else
				fieldType = YksjParser.FLOAT;
		}
		else if ("D".equalsIgnoreCase(itemType))
			fieldType = YksjParser.DATEVALUE;
		else
			fieldType = YksjParser.STRVALUE;
			
		return fieldType;
	}
	public String getFieldTypes(String itemId)
	{
		FieldItem fieldItem = DataDictionary.getFieldItem(itemId);
		return fieldItem.getItemtype();
	}
}
