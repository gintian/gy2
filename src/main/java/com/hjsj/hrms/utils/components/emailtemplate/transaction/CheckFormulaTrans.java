package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:CheckFormulaTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2015 11:03:09 AM</p>
 * @author sunming
 * @version 1.0
 */
public class CheckFormulaTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			String nflag = (String) this.getFormHM().get("flag");
			String field_id = (String) this.getFormHM().get("field_id");
			String c_expr = (String) this.getFormHM().get("c_expr");
			c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
			c_expr=SafeCode.decode(c_expr);
			c_expr=PubFunc.keyWord_reback(c_expr);
			String type = (String) this.getFormHM().get("type");
			type=type!=null&&type.trim().length()>0?type:"";			
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
			String itemid = (String) this.getFormHM().get("itemid");
			itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
			
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			if(fielditem!=null){
				//type = fielditem.getItemtype();
			}
			type=type!=null&&type.trim().length()>0?type:"L";
			//System.out.println(c_expr);
			
			String flag = "";
			if(c_expr.indexOf("代码转名称2")!=-1){
				c_expr="";
				flag = SafeCode.encode("\"代码转名称2\"目前不能用于临时变量的计算中，可用\"代码转名称\"代替");
			}
			
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				//当salaryid等于-1时说明不是从薪资模块过来的，不需要临时变量
				if(!"-1".equalsIgnoreCase(salaryid))
					alUsedFields.addAll(this.getMidFieldItem());
				// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
				YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forSearch, getColumType(type)
						, YksjParser.forPerson, "Ht", "");
				yp.setCon(this.getFrameconn());
				/* dengcan  
				 * 1  or null :Verify_where
				 * 2          : Verify_whereNoRetTypte
				 */
				String model = (String) this.getFormHM().get("model");  
				
				boolean b =false;
				if(model==null|| "1".equals(model))
					b=yp.Verify_where(c_expr.trim());
				else
					b=yp.Verify_whereNoRetTypte(c_expr.trim());
				
				if (b) {// 校验不通过
					flag="ok";
				}else{
					flag = yp.getStrError();
					flag=SafeCode.encode(flag);
					if(StringUtils.isEmpty(flag)){
						flag="公式不正确，请修改";
					}
				} 
			this.getFormHM().put("info", flag);
			this.getFormHM().put("flag", nflag);
			this.getFormHM().put("field_id", field_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public int getColumType(String type){
		int temp=1;
		if("A".equals(type)){
			temp=IParserConstant.STRVALUE;
		}else if("D".equals(type)){
			temp=IParserConstant.DATEVALUE;
		}else if("N".equals(type)){
			temp=IParserConstant.FLOAT;
		}else if("L".equals(type)){
			temp=IParserConstant.LOGIC;
		}else{
			temp=IParserConstant.STRVALUE;
		}
		return temp;
	}
	private ArrayList getMidFieldItem()
	{
		ArrayList fieldlist = new ArrayList();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rset = dao.search(buf.toString());
			while (rset.next()) {
				FieldItem item = new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");// 没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch (rset.getInt("ntype")) {
				case 1://
					item.setItemtype("N");
					break;
				case 2:
					item.setItemtype("A");
					break;
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fieldlist;
	}

}
