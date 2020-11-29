package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class CheckWarnExpreTrans extends IBusiness {

	public void execute() throws GeneralException {
		String c_expr = (String) this.getFormHM().get("c_expr");
		String midvariable=(String)this.getFormHM().get("midvariable");
		c_expr = PubFunc.hireKeyWord_filter_reback(c_expr);
		c_expr = c_expr.replaceAll("'" , "\"");	
		c_expr = c_expr.replaceAll("!" , "\r");	
		c_expr = c_expr.replaceAll("`" , "\n");	
		
		String warntype=(String)this.getFormHM().get("warntype");
		String setid=(String)this.getFormHM().get("setid");
		
		//zxj changed 20130712 Q03与Q05用的是同一套字典
		if ("Q05".equalsIgnoreCase(setid))
		    setid = "Q03";
		
		String flag = "";
		if (c_expr != null && c_expr.length() > 0) {
			ArrayList alUsedFields=new ArrayList();
			if(warntype!=null&& "3".equals(warntype))
			{
				alUsedFields = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
			}else
			{
				
				alUsedFields = DataDictionary.getAllFieldItemList(
						Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				if(midvariable!=null&& "1".equals(midvariable))
					alUsedFields.addAll(getMidVariableList("3","0"));
			}
			 
			
			//System.out.println("ok...");
			
			// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
			YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forSearch, YksjParser.LOGIC
					, YksjParser.forPerson, "USR", "");
			yp.setCon(this.frameconn);
			//System.out.println("ok1...");
			
			
			boolean b = yp.Verify_where(c_expr.trim());

			
			if (b) {// 校验不通过
				flag="ok";
			}else{
				flag = yp.getStrError();
				if(flag==null||flag.trim().length()==0)
				{
					flag="此处有未知字符串!";
				}
					
				flag = flag.replaceAll("\"","'");
				flag = flag.replaceAll("\r\n","`");
			} 
		}
		
		this.getFormHM().put("info", flag);

	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList(String nflag,String templetid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		RowSet rset=null;
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where nflag="+nflag+" and templetid="+templetid+" ");
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4://代码型					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}
		}catch(Exception e)
		{
		}
		return fieldlist;
	}
}
