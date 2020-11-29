package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 项目名称 ：ehr7.x
 * 类名称：CheckFormulaTrans
 * 类描述：校验临时变量公式
 * 创建人： lis
 * 创建时间：2015-10-31
 */
public class CheckFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
	    
		String c_expr = (String) this.getFormHM().get("c_expr");//公式内容
		c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
		c_expr=SafeCode.decode(c_expr);//解码
		c_expr=c_expr.replaceAll("!","\r");
		c_expr=c_expr.replaceAll("`","\n");
		c_expr=PubFunc.keyWord_reback(c_expr);
		String ntype = (String) this.getFormHM().get("ntype");//数据类型
		ntype=ntype!=null&&ntype.trim().length()>0?ntype:"";
		String type=(String)this.getFormHM().get("type");  //type.equals("3")  人事异动临时变量设置  1:薪资类别 5:数据采集
		String tabid = (String) this.getFormHM().get("tabid");//薪资类别id
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		if(!"3".equals(type))//人事异动不需要加密解密
			tabid = PubFunc.decrypt(SafeCode.decode(tabid));
		
		String module = (String) this.getFormHM().get("module"); 

		ArrayList fieldItem_List=new ArrayList();
		if(this.getFormHM().containsKey("fieldItem_List")){
			ArrayList<MorphDynaBean>  list=(ArrayList<MorphDynaBean>) this.getFormHM().get("fieldItem_List");

			for (MorphDynaBean bean : list) {
				String name=(String)bean.get("dataName");
				FieldItem fieldItem=(FieldItem)DataDictionary.getFieldItem(name.split(":")[0]).clone();
				fieldItem.setItemdesc(name.split(":")[1]);
				fieldItem_List.add(fieldItem);
			}

		}
		String salaryid = (String) this.getFormHM().get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		String flag = "ok";
		boolean flag1 = false; // 判断共享临时变量是否引用别的临时变量
		String nid = (String) this.getFormHM().get("nid"); // 获取临时变量nid，为了查询该变量是否共享
		ArrayList variables= new ArrayList();
		if(StringUtils.isNotBlank(tabid)) {
			if ("1".equals(type) || "3".equals(type)) {
				variables = notShareVariables(nid, tabid, type);
			} else {
				variables = notShareVariables(nid, tabid);
			}
		}
		boolean tempvar = false;
		StringBuffer error = new StringBuffer("");
		error.append(ResourceFactory.getProperty("gz_new.gz_isShare"));
		if (variables.size() > 0) {// 如果临时变量共享，接着查找公式中是否引用别的临时变量
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
				for (int i = 0; i < variables.size(); i++) {
					// 依次比较所有不共享的临时变量
					flag1 = PubFunc.IsHasVariable(cvalue,
							(String) variables.get(i));
					if (flag1) {
						tempvar = true;
						error.append((String) variables.get(i) + "  ");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(StringUtils.isNotBlank(nid)&&!"null".equalsIgnoreCase(nid)&&c_expr.equals(isSameName(nid))) {
			this.getFormHM().put("base", ResourceFactory.getProperty("gz_new.gz_sameFomulaName"));
		}
		else if (tempvar) {// 如果共享的临时变量引用了其他不共享的临时变量
			error.append(ResourceFactory.getProperty("gz_new.gz_cannotEditFomula"));
			this.getFormHM().put("base", error.toString());
			// 给提示信息
			// 该变量引用了不共享的临时变量，不能修改公式
		} else {
	
		if (c_expr != null && c_expr.length() > 0) {
			ArrayList fieldlist =new ArrayList();
			if(StringUtils.isNotBlank(tabid)) {
				if ("1".equals(type)) {
					fieldlist = getMidVariableList("", tabid);
				} else if ("3".equals(type)) {//人事异动
					fieldlist = getMidVariableList(tabid, "");
				} else
					fieldlist = getMidVariableList(tabid, salaryid);
			}
			ArrayList alUsedFields =new ArrayList();
			if(fieldItem_List.size()==0){
				alUsedFields=DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			}else{
				alUsedFields=fieldItem_List;
			}
			if (fieldlist.size() > 0) {
				alUsedFields.addAll(fieldlist);
			}

			// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
			YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forSearch, getColumType(ntype,type)
					, YksjParser.forPerson, "Ht", "");
			yp.setCon(this.getFrameconn());
			/* dengcan  
			 * 1  or null :Verify_where
			 * 2          : Verify_whereNoRetTypte
			 */
			
			boolean b =false;
			if("1".equals(module))
				b = yp.Verify_where(c_expr.trim());
			else
				b=yp.Verify_whereNoRetTypte(c_expr.trim());
			if (b) {// 校验通过
				flag="ok";
			}else{
				flag = yp.getStrError();
				flag=SafeCode.encode(flag);
			} 
		}
		this.getFormHM().put("base", flag);
		}
	}
	
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String ntype,String type){
		int temp=2;
		/**工资类别定义人员范围时，应为逻辑型的公式*/
		if("4".equals(ntype))
		{
			if("1".equals(type)||"3".equals(type)) //人事异动|薪资类别 临时变量设置   20140815 dengcan type:2 薪资计算公式增加临时变量  
				temp=YksjParser.STRVALUE;
			else
				temp=YksjParser.LOGIC;
			
		}
		else if("2".equals(ntype)){
			temp=YksjParser.STRVALUE;
		}else if("3".equals(ntype)){
			temp=YksjParser.DATEVALUE;
		}else if("1".equals(ntype)){
			temp=YksjParser.FLOAT;
		}else{
			temp=YksjParser.STRVALUE;
		}
		return temp;
	}
	
	/**
	 * @author lis
	 * @Description: 从临时变量中取得对应指标列表
	 * @date 2015-10-31
	 * @param tabid
	 * @param salaryid
	 * @return
	 */
	private ArrayList getMidVariableList(String tabid,String salaryid){
		ArrayList fieldlist=new ArrayList();
		RowSet rset = null;
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from midvariable ");
			if(StringUtils.isNotBlank(tabid) || StringUtils.isNotBlank(salaryid)){
				buf.append("where ");
				
				if(StringUtils.isNotBlank(tabid)){
					if("-2".equals(salaryid)){
						buf.append("nflag=5 and templetid=0");
						buf.append(" and (cstate is null or cstate='"+tabid+"')");
					}else{//此处是人事异动进入的，应该查询当前模版下的临时变量，以及人事异动业务中其他模版共享的临时变量
					    buf.append("nflag=0 and templetId <> 0 and (templetId = "+tabid+" or cstate = '1')");
					}
				}if(StringUtils.isNotBlank(salaryid)){
					if("-1".equals(salaryid)){
						buf.append(" nflag=4 and templetid=0 ");
						buf.append(" and cstate=-1");
					}else if("-2".equals(salaryid)){
						buf.append("nflag=5 and templetid=0");
						buf.append(" and (cstate is null ");
					}else{
						buf.append(" nflag=0 and templetid=0 ");//是薪资类别
						buf.append(" and (cstate is null or cstate='");
						buf.append(salaryid);
						buf.append("' )");
					}
				}
			}
			ContentDAO dao=new ContentDAO(this.frameconn);
			rset = dao.search(buf.toString());
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
			GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeResource(rset);
		}
		return fieldlist;
	}
	
	/**
	 * 检查该临时变量是否共享,并查找不共享的临时变量
	 */
	private ArrayList notShareVariables(String nid, String tabid,String typeFrom) {
		boolean flag = false;//是否共享
		ArrayList variables = new ArrayList();
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select cstate from midvariable where nid=? ");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sql.toString(),Arrays.asList(nid));
			while (rs.next()) {
				if("3".equals(typeFrom)){//
				    if (!StringUtils.isBlank(rs.getString("cstate")))//人事异动共享 //liuyz 共享判断有问题。
	                    flag = true;
				}else if("1".equals(typeFrom)){
					if (StringUtils.isBlank(rs.getString("cstate")))//薪资类别,共享
	                    flag = true;
				}
			}
			if (flag) {// 如果该临时变量设置为共享，查找不共享的临时变量
				sql.setLength(0);
				if("1".equals(typeFrom))
				{
					sql.append("select chz from midvariable where templetid=0  and nflag=0 and cstate=?");
				}else if("3".equals(typeFrom))
				{
				    sql.append("select chz from midvariable where ( cstate is null or cstate<>'1' ) and Templetid=?");
                    sql.append(" and nflag=0");
				}
				
				rs = dao.search(sql.toString(),Arrays.asList(tabid));
				while (rs.next()) {
					variables.add(rs.getString("chz"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return variables;
	}
	
	/**
	 * 检查该临时变量是否共享,并查找不共享的临时变量
	 */
	public ArrayList notShareVariables(String nid,String tabid) {
		boolean flag = false;
		ArrayList Variables = new ArrayList();
		RowSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select cstate from midvariable where nid = "+nid);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs = dao.search(sql.toString());
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
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return Variables;
	}
	
	/**
	 * 检查该临时变量是否共享,并查找不共享的临时变量
	 */
	private String isSameName(String nid) {
		String formulaName = "";
		StringBuffer sql = new StringBuffer();
		ArrayList<String> list = new ArrayList<String>();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			sql.append("select chz from midvariable where nid = ?");
			list.add(nid);
			rs = dao.search(sql.toString(),list);
			if (rs.next()) {
				formulaName = rs.getString("chz");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return formulaName;
	}
}
