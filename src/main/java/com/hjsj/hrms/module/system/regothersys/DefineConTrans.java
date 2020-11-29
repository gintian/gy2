package com.hjsj.hrms.module.system.regothersys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 条件选择初始化Trans类
 * @author cuibl
 *
 */
public class DefineConTrans extends IBusiness {

	// 日志
	private Category log = Category.getInstance(getClass().getName());
	
	@Override
	public void execute() throws GeneralException {
		String method = (String) this.getFormHM().get("method");//访问的方法
		String opt = (String) this.getFormHM().get("opt");//选择的类目  机构 岗位 人员
		String node = "";//要解析的节点
		if("org".equalsIgnoreCase(opt)) {
			node = "org_fields";
		}else if("post".equalsIgnoreCase(opt)) {
			node = "post_fields";
		}else if("emp".equalsIgnoreCase(opt)) {
			node = "fields";
		}
		
		if("init".equalsIgnoreCase(method)) {
			ArrayList<CommonData> itemlist = this.init(opt,node);
			this.getFormHM().put("itemlist", itemlist);
		}else if("check".equalsIgnoreCase(method)) {
			String c_expr = (String) this.getFormHM().get("c_expr");//需要检验的语句
			c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
			c_expr=SafeCode.decode(c_expr);
			
			String flag = this.checkCondition(c_expr, node);
			this.getFormHM().put("info",flag);
		}
		
	}
	
	/**
	 * 初始化条件选择界面
	 * @param opt
	 * @param node
	 * @return
	 */
	private ArrayList<CommonData> init(String opt, String node) {
		ArrayList<CommonData> itemlist = new ArrayList<CommonData>();
		if("org".equalsIgnoreCase(opt)) {
			CommonData b0110_0 = new CommonData("b0110_0#!#机构编码#!#UN","机构编码");
			itemlist.add(b0110_0);
			CommonData sDate = new CommonData("sDate#!#更新时间#!#0","更新时间");
			itemlist.add(sDate);
		}else if("post".equalsIgnoreCase(opt)) {
			CommonData e0122_0 = new CommonData("e0122_0#!#部门编码#!#UM","部门编码");
			itemlist.add(e0122_0);
			CommonData sDate = new CommonData("sDate#!#更新时间#!#0","更新时间");
			itemlist.add(sDate);
		}else if("emp".equalsIgnoreCase(opt)) {
			CommonData nbase_0 = new CommonData("nbase_0#!#人员库前缀#!#0","人员库前缀");
			itemlist.add(nbase_0);
			CommonData b0110_0 = new CommonData("b0110_0#!#机构编码#!#UN","机构编码");
			itemlist.add(b0110_0);
			CommonData e0122_0 = new CommonData("e0122_0#!#部门编码#!#UM","部门编码");
			itemlist.add(e0122_0);
			CommonData e01a1_0 = new CommonData("e01a1_0#!#岗位编码#!#@K","岗位编码");
			itemlist.add(e01a1_0);
			CommonData sDate = new CommonData("sDate#!#更新时间#!#0","更新时间");
			itemlist.add(sDate);
		}
		String[] parseResult = this.parseXML(node);
		if(parseResult == null) {
			return itemlist;
		}
		for (int i = 0; i < parseResult.length; i++) {
			String temp = parseResult[i];
			if(StringUtils.isBlank(temp)) {
				return itemlist;
			}
			FieldItem item = DataDictionary.getFieldItem(temp);
			CommonData data = new CommonData(item.getItemid()+"#!#"+item.getItemdesc()+"#!#"+item.getCodesetid(),item.getItemdesc());
			itemlist.add(data);
		}
		
		return itemlist;
	}
	
	/**
	 * 检查定义的范围条件
	 * @param c_expr
	 * @param node
	 * @return
	 * @throws GeneralException
	 */
	public String checkCondition(String c_expr,String node) throws GeneralException{
		Connection conn = null;
		String flag = "";
		String[] parseResult = this.parseXML(node);
		try {
			conn = AdminDb.getConnection();
			if (c_expr != null && c_expr.length() > 0) {
			    ArrayList fieldlist = new ArrayList();
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			    
				/*if((salaryid.length()==0) || ("S05".equalsIgnoreCase(itemsetid))){
					fieldlist.addAll(alUsedFields);
					if(itemsetid.length()>0)
						fieldlist.addAll(DataDictionary.getFieldList(itemsetid, 1));
				}*/
				fieldlist.addAll(alUsedFields);
				
				FieldItem nbase_0 = new FieldItem();
				nbase_0.setItemid("nbase_0");
				nbase_0.setItemdesc("人员库前缀");
				nbase_0.setItemtype("A");
				nbase_0.setItemlength(3);
				nbase_0.setDecimalwidth(0);
				nbase_0.setCodesetid("0");
				fieldlist.add(nbase_0);
				
				FieldItem b0110_0 = new FieldItem();
				b0110_0.setItemid("b0110_0");
				b0110_0.setItemdesc("机构编码");
				b0110_0.setItemtype("A");
				b0110_0.setItemlength(50);
				b0110_0.setDecimalwidth(0);
				b0110_0.setCodesetid("0");
				fieldlist.add(b0110_0);
				
				FieldItem e0122_0 = new FieldItem();
				e0122_0.setItemid("e0122_0");
				e0122_0.setItemdesc("部门编码");
				e0122_0.setItemtype("A");
				e0122_0.setItemlength(50);
				e0122_0.setDecimalwidth(0);
				e0122_0.setCodesetid("0");
				fieldlist.add(e0122_0);
				
				FieldItem e01a1_0 = new FieldItem();
				e01a1_0.setItemid("e01a1_0");
				e01a1_0.setItemdesc("岗位编码");
				e01a1_0.setItemtype("D");
				e01a1_0.setItemlength(50);
				e01a1_0.setDecimalwidth(0);
				e01a1_0.setCodesetid("0");
				fieldlist.add(e01a1_0);
				
				FieldItem sDate = new FieldItem();
				sDate.setItemid("sDate");
				sDate.setItemdesc("更新时间");
				sDate.setItemtype("D");
				sDate.setItemlength(0);
				sDate.setDecimalwidth(0);
				sDate.setCodesetid("0");
				fieldlist.add(sDate);
				if(parseResult != null) {
					for (int i = 0; i < parseResult.length; i++) {
						FieldItem fieldItem = DataDictionary.getFieldItem(parseResult[i]);
						fieldlist.add(fieldItem);
					}
				}
				YksjParser yp = new YksjParser(this.userView, fieldlist, YksjParser.forSearch, YksjParser.LOGIC
						, YksjParser.forPerson,"Ht", "");
				
				yp.setCon(conn);
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
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(conn);
		}
		return SafeCode.encode(flag);//加密并返回
	}
	
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	/*public int getColumType(String type){
		int temp=1;
		if(type.equals("A")){
			temp=YksjParser.STRVALUE;
		}else if(type.equals("D")){
			temp=YksjParser.DATEVALUE;
		}else if(type.equals("N")){
			temp=YksjParser.FLOAT;
		}else if(type.equals("L")){
			temp=YksjParser.LOGIC;
		}else{
			temp=YksjParser.STRVALUE;
		}
		return temp;
	}*/
	
	/**
	 * 解析XML
	 * @param node
	 * @return
	 */
	private String[] parseXML(String node) {
		Connection conn = null;
		String[] result = null;
		String str = "";
		try {
			String sql = "select Str_Value from Constant where Constant = 'SYS_EXPORT_VIEW'";
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(sql);
			while(rs.next()) {
				str = rs.getString("Str_Value");
			}
			
		} catch (Exception e) {
			//e1.printStackTrace();
			log.error("获取Constant表中数据出错:");
			log.error(e);
		}finally {
			PubFunc.closeDbObj(conn);
		}
		if(str.length()>0) {
			try {
				Document doc = PubFunc.generateDom(str);
				XPath xpath =null;
				xpath = XPath.newInstance("/root/"+node);
				Element ele = (Element) xpath.selectSingleNode(doc);
				String tempStr = ele.getText();
				if(StringUtils.isBlank(tempStr)) {
					log.error("应用注册->Constant中"+ node +"的数据为空值");
					return null;
				}
				result = tempStr.split(",");
			} catch (Exception e) {
				log.error("应用注册->解析 "+node+" 时出错:");
				log.error(e);
			}
		}
		return result;
	}
	
}
