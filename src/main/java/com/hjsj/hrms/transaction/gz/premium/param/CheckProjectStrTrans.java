package com.hjsj.hrms.transaction.gz.premium.param;


import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CheckProjectStrTrans extends IBusiness {

	public void execute() throws GeneralException {
		String c_expr = (String) this.getFormHM().get("c_expr");
		c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
		
		String itemid = (String) this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
		String setid = (String) this.getFormHM().get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		FieldItem fielditem = DataDictionary.getFieldItem(itemid);
		String type="";
		if(fielditem!=null){
			type = fielditem.getItemtype();
		}
		type=type!=null&&type.trim().length()>0?type:"L";
		c_expr=SafeCode.decode(c_expr);
		//System.out.println(c_expr);
		
		String flag = "";
		if (c_expr != null && c_expr.length() > 0) {
			ArrayList fieldlist =this.userView.getPrivFieldList(setid, Constant.UNIT_FIELD_SET);
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			if(fieldlist.size()>0){
				alUsedFields.addAll(fieldlist);
				
			}
			fieldlist.addAll(alUsedFields);
			
			//System.out.println("ok...");
			
			// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
			YksjParser yp = new YksjParser(getUserView(), fieldlist, YksjParser.forSearch, getColumType(type)
					, YksjParser.forPerson,"Ht", "");
			
			//System.out.println("ok1...");
			
			yp.setCon(this.getFrameconn());
			boolean b = false;
			try{
				b = yp.Verify_where(c_expr.trim());
			}catch (Exception e) {
				e.printStackTrace();
				
				b = false;
			}

			if (b) {// 校验通过
				flag="ok";
			}else{
				flag = yp.getStrError();
			} 
		}else{
			flag="ok";
		}
		this.getFormHM().put("info",SafeCode.encode(flag));
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
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
//	private ArrayList getMidVariableList(String setid){
//		ArrayList fieldlist=new ArrayList();
//		try{
//			StringBuffer buf=new StringBuffer();
//			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
//			buf.append(" midvariable where nflag=0 and templetid=0 ");
//			buf.append(" and (cstate is null or cstate='");
//			buf.append(setid);
//			buf.append("')");
//			ContentDAO dao=new ContentDAO(this.frameconn);
//			RowSet rset=dao.search(buf.toString());
//			while(rset.next())
//			{
//				FieldItem item=new FieldItem();
//				item.setItemid(rset.getString("cname"));
//				item.setFieldsetid("A01");//没有实际含义
//				item.setItemdesc(rset.getString("chz"));
//				item.setItemlength(rset.getInt("fldlen"));
//				item.setDecimalwidth(rset.getInt("flddec"));
//				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
//				switch(rset.getInt("ntype"))
//				{
//				case 1://
//					item.setItemtype("N");
//					item.setCodesetid("0");
//					break;
//				case 2:
//					item.setItemtype("A");
//					item.setCodesetid("0");
//					break;
//				case 4:
//					item.setItemtype("A");
//					item.setCodesetid(rset.getString("codesetid"));
//					break;
//				case 3:
//					item.setItemtype("D");
//					item.setCodesetid("0");
//					break;
//				}
//				item.setVarible(1);
//				fieldlist.add(item);
//			}// while loop end.
//			String sqlstr = "select * from salaryset";
//			if(setid!=null&&setid.trim().length()>0){
//				sqlstr+=" where setid="+setid;
//			}
//			rset=dao.search(sqlstr);
//			while(rset.next()){
//				FieldItem item=new FieldItem();
//				item.setItemid(rset.getString("ITEMID"));
//				item.setItemdesc(rset.getString("ITEMDESC"));
//				item.setFieldsetid(rset.getString("FIELDSETID"));
//				item.setItemlength(rset.getInt("ITEMLENGTH"));
//				item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
//				item.setDecimalwidth(rset.getInt("DECWIDTH"));
//				item.setItemtype(rset.getString("ITEMTYPE"));
//				item.setCodesetid(rset.getString("CODESETID"));
//				item.setVarible(1);
//				fieldlist.add(item);
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return fieldlist;
//	}
}
