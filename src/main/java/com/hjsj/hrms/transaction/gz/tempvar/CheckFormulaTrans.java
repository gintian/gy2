package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class CheckFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	    
	    
		String c_expr = (String) this.getFormHM().get("c_expr");
		c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
		c_expr=SafeCode.decode(c_expr);
		c_expr=c_expr.replaceAll("!","\r");
		c_expr=c_expr.replaceAll("`","\n");
		c_expr=PubFunc.keyWord_reback(c_expr);
		String type = (String) this.getFormHM().get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		String type_from=(String)this.getFormHM().get("type_from");  //type_from.equals("3")  人事异动临时变量设置  1:薪资类别 5:数据采集
		String tabid = (String) this.getFormHM().get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		//System.out.println(c_expr);
		
		String flag = "";
		boolean flag1 = false; // 判断共享临时变量是否引用别的临时变量
		String nid = (String) this.getFormHM().get("nid"); // 获取临时变量nid，为了查询该变量是否共享
		ArrayList Variables= new ArrayList();
		if("5".equals(type_from)||"3".equals(type_from)){
			 Variables = notShareVariables(nid,tabid,type_from);
		} else{
			 Variables = notShareVariables(nid,tabid);
		}
		boolean tempvar = false;
		StringBuffer error = new StringBuffer("");
		error.append("该临时变量是共享的，并且引用了如下不共享的临时变量：");
		if (Variables.size() > 0) {// 如果临时变量共享，接着查找公式中是否引用别的临时变量
			// 取出该共享的临时变量的公式
			try {
				String cvalue = "";
				StringBuffer sql = new StringBuffer();
				sql.append("select cvalue from midvariable where nid = "+nid);
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				RowSet rs = dao.search(sql.toString());
				while (rs.next()) {
					cvalue = rs.getString("cvalue");
				}
				for (int i = 0; i < Variables.size(); i++) {
					// 依次比较所有不共享的临时变量
					flag1 = PubFunc.IsHasVariable(cvalue,
							(String) Variables.get(i));
					if (flag1) {
						tempvar = true;
						error.append((String) Variables.get(i) + "  ");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (tempvar) {// 如果共享的临时变量引用了其他不共享的临时变量
			error.append("不能修改公式！");
			this.getFormHM().put("info", error.toString());
			// 给提示信息
			// 该变量引用了不共享的临时变量，不能修改公式
		} else {
	/*
		if(c_expr.indexOf("代码转名称2")!=-1){
			c_expr="";
			flag = SafeCode.encode("\"代码转名称2\"目前不能用于临时变量的计算中，可用\"代码转名称\"代替");
		}
	*/	
		if (c_expr != null && c_expr.length() > 0) {
			ArrayList fieldlist =new ArrayList();
			if(type_from!=null&&("1".equals(type_from)|| "2".equals(type_from))){
				fieldlist=getMidVariableList("",tabid);
			}
			else if(type_from!=null&&("3".equals(type_from))){
	                fieldlist=getMidVariableList(tabid,"");
			}else if(type!=null &&("5".equals(type_from))){//从数据采集进入时要进行的判断
				fieldlist = getMidVariableList(tabid, "-2");
			} else
				fieldlist=getMidVariableList(tabid,salaryid);
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			if(fieldlist.size()>0){
				alUsedFields.addAll(fieldlist);
			}
			//System.out.println("ok...");
			
			// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
			YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forSearch, getColumType(type,type_from)
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
			} 
		}
		this.getFormHM().put("info", flag);
		}
	}
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String type,String type_from){
		int temp=2;
		/**工资类别定义人员范围时，应为逻辑型的公式*/
		if("4".equals(type))
		{
			if(type_from!=null&&("3".equals(type_from)|| "1".equals(type_from)|| "5".equals(type_from)|| "2".equals(type_from))) //人事异动|薪资类别 临时变量设置   20140815 dengcan type_from:2 薪资计算公式增加临时变量
				temp=YksjParser.STRVALUE;
			else
				temp=YksjParser.LOGIC;
			
		}
		else if("2".equals(type)){
			temp=YksjParser.STRVALUE;
		}else if("3".equals(type)){
			temp=YksjParser.DATEVALUE;
		}else if("1".equals(type)){
			temp=YksjParser.FLOAT;
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
	private ArrayList getMidVariableList(String tabid,String salaryid){
		ArrayList fieldlist=new ArrayList();
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from midvariable ");
			if((tabid!=null&&tabid.trim().length()>0)||(salaryid!=null&&salaryid.trim().length()>0)){
				buf.append("where ");
				if(tabid!=null&&tabid.trim().length()>0){
					if("-2".equals(salaryid)){
						buf.append("nflag=5 and templetid=0");
						buf.append(" and (cstate is null or cstate='"+tabid+"')");
					}else{//此处是人事异动进入的，应该查询当前模版下的临时变量，以及人事异动业务中其他模版共享的临时变量
					    buf.append("nflag=0 and templetId <> 0 and (templetId = "+tabid+" or cstate = '1')");
//						buf.append(" templetid='");
//						buf.append(tabid);
//						buf.append("' or");
					}
					
				}else if(salaryid!=null&&salaryid.trim().length()>0){
					if("-1".equals(salaryid)){
						buf.append(" nflag=4 and templetid=0 ");
						buf.append(" and cstate=-1");
					}else if("-2".equals(salaryid)){
						buf.append("nflag=5 and templetid=0");
						buf.append(" and (cstate is null ");
					}else{
						buf.append(" nflag=0 and templetid=0 ");
						buf.append(" and (cstate is null or cstate='");
						buf.append(salaryid);
						buf.append("' )");
					}
				}
			}
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(/*"A01"*/"");//没有实际含义
				item.setItemdesc(rset.getString("chz").trim());
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setCodesetid(rset.getString("codesetid")==null?"0":rset.getString("codesetid"));
				switch(rset.getInt("ntype")){
					case 1:
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
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	
	/**
	 * 检查该临时变量是否共享,并查找不共享的临时变量
	 */
	public ArrayList notShareVariables(String nid,String tabid) {
		boolean flag = false;
		ArrayList Variables = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select cstate from midvariable where nid = "+nid);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql.toString());
			while (rs.next()) {
				if (rs.getString(1) == null || "".equals(rs.getString(1)))
					flag = true;
			}
			if (flag) {// 如果该临时变量设置为共享，查找不共享的临时变量
				sql.setLength(0);
				sql.append("select chz from midvariable where templetid=0 and cstate= " +tabid);
				sql.append(" and nflag=0");
				rs = dao.search(sql.toString());
				while (rs.next()) {
					Variables.add(rs.getString(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Variables;
	}
	private ArrayList notShareVariables(String nid, String tabid,
			String typeFrom) {
		boolean flag = false;
		ArrayList Variables = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select cstate from midvariable where nid = "+nid);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql.toString());
			while (rs.next()) {
				if("3".equals(typeFrom)){
				    if (rs.getString(1)!= null&&"1".equals(rs.getString(1)))
	                    flag = true;
				}
				else if (rs.getString(1) == null || "".equals(rs.getString(1)))
					flag = true;
			}
			if (flag) {// 如果该临时变量设置为共享，查找不共享的临时变量
				sql.setLength(0);
				if("3".equals(typeFrom))
				{
				    sql.append("select chz from midvariable where ( cstate is null or cstate<>'1' ) and Templetid= '" +tabid+"'");
                    sql.append(" and nflag=0");
				}
				else
				{
				    sql.append("select chz from midvariable where templetid=0 and cstate= '" +tabid+"'");
				    sql.append(" and nflag=5");
				}
				rs = dao.search(sql.toString());
				while (rs.next()) {
					Variables.add(rs.getString(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Variables;
	}
}
