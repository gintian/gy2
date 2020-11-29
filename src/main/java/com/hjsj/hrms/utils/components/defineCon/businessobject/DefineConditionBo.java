package com.hjsj.hrms.utils.components.defineCon.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DefineConditionBo 
 * 类描述：条件定义Bo类
 * 创建人：sunming
 * 创建时间：2015-7-21
 * @version
 */
public class DefineConditionBo {
	private Connection conn = null;
	/** 薪资类别号 */
	private int primarykey = -1;
	private UserView userview;

	public DefineConditionBo(Connection conn,UserView userview) {
		this.conn = conn;
		this.userview = userview;
	}

	/**
	 * 获取子标List
	 * @param imodule 模块号   0时薪资发放--批量修改;   1：薪资发放计算公式;3:薪资属性提成工资数据范围  2:人事异动模块批量修改-条件组件
	 * @return ArrayList
	 * @throws GeneralException
	 */
	public ArrayList conditionsList(String primarykey,String imodule) {
		ArrayList list = new ArrayList();
		if("0".equals(imodule)|| "1".equals(imodule)){//模块号为0时是薪资发放--批量修改 1：薪资发放计算公式
			getCondList(primarykey, list);
		}
		else if("2".equals(imodule)) //人事异动模块批量修改-条件组件
		{
			list=getTemplateCondList(primarykey);
		}
		else if("".equals(imodule)){//其他模块调用时，需要在这里重新添加获取list的方法
			
		}
		
		return list;
	}
	
	/**
	 * 获得人事异动的公式条件项目列表
	 * @param primarykey 
	 */
	private ArrayList getTemplateCondList(String primarykey)
	{
		ArrayList itemlist=new ArrayList();
		TemplateListBo listBo=new TemplateListBo(primarykey,this.conn,this.userview);
		ArrayList templateSetList=listBo.getAllCell();//所有的模板字段
		String stritem=",";
		for(int i=0;i<templateSetList.size();i++){
			LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);
			String subflag = (String)abean.get("subflag");
			String isvar = (String)abean.get("isvar");
			String chgstate = (String)abean.get("chgstate");
			String field_name = (String)abean.get("field_name");
			String field_hz = (String)abean.get("field_hz");
			String field_type=(String)abean.get("field_type");
			if("2".equals(chgstate))
				field_hz="拟"+field_hz;
			String codeid=(String)abean.get("codeid");
			if("0".equals(codeid))
				codeid="";
			//CommonData dataobj = null;
			ArrayList jsonlst = new ArrayList();//存放变化前和变化后的字段（满足条件的）
			if("1".equals(subflag)|| "M".equalsIgnoreCase(field_type))//去掉子集项
				continue;
			if ("1".equals(isvar)){
			    stritem+=field_name.toLowerCase()+",";
			}
			else {
			    field_name=field_name+"_"+chgstate;
			    //因为当时出现A0XXX:测试:测试这种的，这时候前台截取value的时候错误，以前改成#!#这种形式区分
			    CommonData dataobj = new CommonData(field_name+"#!#"+field_hz+"#!#"+codeid,field_name+":"+field_hz); 
			    itemlist.add(dataobj);
			}
		}
		
		
	 
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(this.conn,primarykey);
		for(int i=0;i<templist.size();i++){
			FieldItem fielditem = (FieldItem)templist.get(i);
			if(stritem.indexOf(","+fielditem.getItemid().toLowerCase()+",")==-1)
				continue;
			String codesetid=fielditem.getCodesetid();
			if("0".equals(codesetid))
				codesetid="";
			CommonData dataobj = new CommonData(fielditem.getItemid()+"#!#"+fielditem.getItemdesc()+"#!#"+codesetid,fielditem.getItemid()+":"+fielditem.getItemdesc());
			itemlist.add(dataobj);
		}
		if(itemlist.size()==0){
			CommonData dataobj = new CommonData("","");
			itemlist.add(0,dataobj); 
		}
		return itemlist;
	}


	/**
	 * imodule为0时，获取list的方法
	 * @param primarykey
	 * @param list
	 */
	private void getCondList(String primarykey,ArrayList list) {
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlstr = new StringBuffer();
		StringBuffer sqlstr1 = new StringBuffer();
		ArrayList fieldList = new ArrayList();
		if (!"-1".equals(primarykey)) {
			sqlstr.append("select itemid,itemdesc,itemtype,codesetid from salaryset where salaryid="
					+ primarykey);
			sqlstr1.append("select nid,chz,cname from midvariable where nflag=0 and templetid=0 and (cstate is null or cstate='"
					+ primarykey + "')");
		} else {
			// 项目：取自薪资总额子集中的指标和薪资总额临时变量。
			String salaryAmountSet = "";
			GzAmountXMLBo bo = new GzAmountXMLBo(conn, 1);
			HashMap hm = bo.getValuesMap();
			if (hm != null)
				salaryAmountSet = (String) hm.get("setid") == null ? ""
						: (String) hm.get("setid");
			fieldList = DataDictionary.getFieldList(salaryAmountSet,
					Constant.USED_FIELD_SET);
			sqlstr1.append("select nid,chz,cname from midvariable where nflag=4 and templetid=0 and cstate=-1");
		}
		ArrayList dylist = null;
		try {
			CommonData dataobj = new CommonData(":", "");
//			list.add(dataobj);
			if (!"-1".equals(primarykey)) {
				dylist = dao.searchDynaList(sqlstr.toString());
				for (Iterator it = dylist.iterator(); it.hasNext();) {
					DynaBean dynabean = (DynaBean) it.next();
					String itemid = dynabean.get("itemid").toString();
					if ("A0000".equalsIgnoreCase(itemid))
						continue;
					if ("A0100".equalsIgnoreCase(itemid))
						continue;
					String itemdesc = dynabean.get("itemdesc").toString();
					String itemtype = dynabean.get("itemtype").toString();
					/**
					 * 如果是代码型，则取得codesetid
					 */
					String codesetid = "";
					if ("A".equalsIgnoreCase(itemtype)) {
						codesetid = dynabean.get("codesetid").toString();
					}
					dataobj = new CommonData(itemid + "#!#" + itemdesc+"#!#"+codesetid, itemid
							+ ":" + itemdesc);
					list.add(dataobj);
				}
				dylist.clear();
			} else {
				int n = fieldList.size();
				for (int i = 0; i < n; i++) {
					FieldItem item = (FieldItem) fieldList.get(i);
					String itemid = item.getItemid();
					if (itemid == null || "".equals(itemid))
						continue;
					String itemdesc = item.getItemdesc();
					dataobj = new CommonData(itemid + "#!#" + itemdesc, itemid
							+ ":" + itemdesc);
					list.add(dataobj);
				}
			}

			dylist = dao.searchDynaList(sqlstr1.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String itemid = dynabean.get("nid").toString();
				String itemdesc = dynabean.get("chz").toString();
				String cname = dynabean.get("cname").toString();
				dataobj = new CommonData(itemid + "#!#" + itemdesc, cname + ":"
						+ itemdesc);
				list.add(dataobj);
			}

		} catch (GeneralException e) {
			e.printStackTrace();
		}
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
	public ArrayList getMidVariableList(String primarykey,String fieldsetid){
		ArrayList fieldlist=new ArrayList();
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			if("-1".equals(primarykey)){
				buf.append(" midvariable where nflag=4 and templetid=0 ");
				buf.append(" and cstate=-1");
			}else if("-2".equals(primarykey)){//xcs modify @ 2013-8-6 处理数据采集的计算公式定义
				buf.append(" midvariable where nflag=5 and templetid=0 ");
			}else{
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate='");
				buf.append(primarykey);
				buf.append("')");
			}
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					item.setCodesetid("0");
					break;
				case 2:
					item.setItemtype("A");
					item.setCodesetid("0");
					break;
				case 4:
					item.setItemtype("A");
					item.setCodesetid(rset.getString("codesetid"));
					break;
				case 3:
					item.setItemtype("D");
					item.setCodesetid("0");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			if(!"-1".equals(primarykey)){
				if("-2".equals(primarykey)){
					StringBuffer sqlstr=new StringBuffer();
					sqlstr.append("select * from fielditem ");
					if(fieldsetid!=null&&fieldsetid.trim().length()>0){
						sqlstr.append(" where fieldsetid='"+fieldsetid+"'");
					}
					rset=dao.search(sqlstr.toString());
					while(rset.next()){
						FieldItem item=new FieldItem();
						item.setItemid(rset.getString("ITEMID"));
						item.setItemdesc(rset.getString("ITEMDESC"));
						item.setFieldsetid(rset.getString("FIELDSETID"));
						item.setItemlength(rset.getInt("ITEMLENGTH"));
						item.setFormula(Sql_switcher.readMemo(rset, "AuditingFormula"));
						item.setDecimalwidth(rset.getInt("DECIMALWIDTH"));
						item.setItemtype(rset.getString("ITEMTYPE"));
						item.setCodesetid(rset.getString("CODESETID"));
						item.setVarible(0);
						fieldlist.add(item);
					}
				}else{
					StringBuffer sqlstr=new StringBuffer();
					sqlstr.append("select * from salaryset");
					if(primarykey!=null&&primarykey.trim().length()>0){
						sqlstr.append(" where salaryid="+primarykey);
					}
					rset=dao.search(sqlstr.toString());
					while(rset.next()){
						FieldItem item=new FieldItem();
						item.setItemid(rset.getString("ITEMID"));
						item.setItemdesc(rset.getString("ITEMDESC"));
						item.setFieldsetid(rset.getString("FIELDSETID"));
						item.setItemlength(rset.getInt("ITEMLENGTH"));
						item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
						item.setDecimalwidth(rset.getInt("DECWIDTH"));
						item.setItemtype(rset.getString("ITEMTYPE"));
						item.setCodesetid(rset.getString("CODESETID"));
						item.setVarible(0);
						fieldlist.add(item);
					}
				}
				
			}else{
				String salaryAmountSet = "";
				GzAmountXMLBo bo = new GzAmountXMLBo(conn,1);
				HashMap hm = bo.getValuesMap();
				if (hm != null)
					salaryAmountSet = (String) hm.get("setid")==null?"":(String) hm.get("setid");
				ArrayList tempList = DataDictionary.getFieldList(salaryAmountSet, Constant.USED_FIELD_SET);
				int n = tempList.size();
				for(int i=0;i<n;i++){
					FieldItem item = (FieldItem)tempList.get(i);
					item.setVarible(0);
					fieldlist.add(item);
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	/**
	 * 薪资发放--批量修改对定义条件进行校验的方法
	 * @param c_expr 条件
	 * @param primarykey 传递的主键
	 * @param itemid 项目id
	 * @param fieldsetid
	 * @param itemtype 类型
	 * @return
	 */
	public String checkProjectStr(String c_expr,
			String primarykey, String itemid, String clsflag,
			String fieldsetid, String itemtype, String itemsetid) {
		FieldItem fielditem = DataDictionary.getFieldItem(itemid);
		String type="";
		
		if(fielditem!=null){
			type = fielditem.getItemtype();
		}else if(!"".equals(primarykey)){
			if("A00Z0".equalsIgnoreCase(itemid)||"A00Z2".equalsIgnoreCase(itemid))
				type="D";
			else if("A00Z1".equalsIgnoreCase(itemid)||"A00Z3".equalsIgnoreCase(itemid))
				type="N";
		}
		type=type!=null&&type.trim().length()>0?type:"L";
		if("1".equals(clsflag)){
			type="N";
		}
		
		String flag = "";
		if (c_expr != null && c_expr.length() > 0) {
		    if("S05".equalsIgnoreCase(itemsetid)){
		        primarykey="0";
		    }
			ArrayList fieldlist = this.getMidVariableList(primarykey,fieldsetid);
			
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			if(fieldlist.size()>0){
				
			}
			
			if((primarykey.length()==0) || ("S05".equalsIgnoreCase(itemsetid))){
				fieldlist.addAll(alUsedFields);
				if(itemsetid.length()>0)
					fieldlist.addAll(DataDictionary.getFieldList(itemsetid, 1));
			}
			
			if(itemtype.length()>0){
				FieldItem item = new FieldItem();
				item.setItemid(itemid);
				item.setItemtype(itemtype);
				fieldlist.add(item);
				type=itemtype;
			}
			
			if("P04".equalsIgnoreCase(itemsetid)){//业务字典目标卡任务表默认评分指标 
				FieldItem item = new FieldItem();
				item.setItemid("task_score");
				item.setItemdesc("评分");
				item.setItemtype("N");
				item.setItemlength(10);
				item.setDecimalwidth(4);
				item.setCodesetid("0");
				fieldlist.add(item);
			}
			
			if("S05".equalsIgnoreCase(itemsetid)){//s05子集前面加固定指标s0102  
				FieldItem item = new FieldItem();
				item.setItemid("S0102");
				item.setItemdesc("计件作业类别");
				item.setItemtype("A");
				item.setItemlength(10);
				item.setDecimalwidth(0);
				item.setCodesetid("0");
				fieldlist.add(item);
			}
			
			// YksjParser.LOGIC// 此处需要调用者知道该公式的数据类型
			YksjParser yp = new YksjParser(userview, fieldlist, YksjParser.forSearch, this.getColumType(type)
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
		return flag;
	}
	
	public ArrayList<CommonData> getFieldList(String setid) throws GeneralException{
		ArrayList itemlist = new ArrayList();
		try {
			ArrayList fieldlist = DataDictionary.getFieldList(setid, Constant.USED_FIELD_SET);
			CommonData data = new CommonData("","");
			//itemlist.add(data);
			for(int i=0;i<fieldlist.size();i++){
				FieldItem item = (FieldItem)fieldlist.get(i);
				if(item==null)
					continue;
				if("M".equals(item.getItemtype()))
					continue;
				//防止设置指标的时候有：设置特殊分隔符，前台也是，加上item.getCodesetid()，可以让前台看到代码sunjian2017-05-23
				data = new CommonData(item.getItemid()+"#!#"+item.getItemdesc()+"#!#"+item.getCodesetid(),item.getItemdesc());
				itemlist.add(data);
			}
			if("P04".equalsIgnoreCase(setid)){
				data = new CommonData("task_score:评分","评分");
				itemlist.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return itemlist;
	}
	
	public ArrayList<CommonData> getUsedFieldSet() throws GeneralException{
		ArrayList setlist = new ArrayList();
		try {
			ArrayList list=DataDictionary.getFieldList("s05",Constant.USED_FIELD_SET);
			//CommonData vo1 = new CommonData("","");
			//setlist.add(vo1);
			CommonData vo = new CommonData("S0102","S0102:计件作业类别");
			setlist.add(vo);
			String excludeStr=",Nbase,A0100,I9999,S0100,".toUpperCase();
			for (int i=0;i<list.size();i++) {
				FieldItem fielditem = (FieldItem) list.get(i);
				if ("0".equals(fielditem.getState())) continue;
				if (excludeStr.indexOf(","+fielditem.getItemid().toUpperCase()+",")>-1) {continue;}
				CommonData datavo = new CommonData(fielditem.getItemid().toUpperCase()+"#!#"+fielditem.getItemdesc()+"#!#"+fielditem.getCodesetid(),fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc());
				setlist.add(datavo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return setlist;
	}
	
	/**
	 * @author lis
	 * @Description: 校验公式
	 * @date 2015-12-21
	 * @param c_expr
	 * @param itemsetid
	 * @param type
	 * @return
	 * @throws GeneralException 
	 */
	public String checkCondition(String c_expr,String itemsetid,String type) throws GeneralException{
		String flag = "";
		try {
			String salaryid = "";
			if (c_expr != null && c_expr.length() > 0) {
			    if("S05".equalsIgnoreCase(itemsetid)){//计件薪资慢，不需要薪资指标，把salaryid设为0 2014-03-04
			        salaryid="0";
			    }
				ArrayList fieldlist = getMidVariableList(salaryid);
				
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				
				if((salaryid.length()==0) || ("S05".equalsIgnoreCase(itemsetid))){
					fieldlist.addAll(alUsedFields);
					if(itemsetid.length()>0)
						fieldlist.addAll(DataDictionary.getFieldList(itemsetid, 1));
				}
				
				if("S05".equalsIgnoreCase(itemsetid)){//s05子集前面加固定指标s0102  zhaoxg 2013-1-5
					FieldItem item = new FieldItem();
					item.setItemid("S0102");
					item.setItemdesc("计件作业类别");
					item.setItemtype("A");
					item.setItemlength(10);
					item.setDecimalwidth(0);
					item.setCodesetid("0");
					fieldlist.add(item);
				}
				YksjParser yp = new YksjParser(this.userview, fieldlist, YksjParser.forSearch, getColumType(type)
						, YksjParser.forPerson,"Ht", "");
				
				yp.setCon(this.conn);
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
		}
		
		return SafeCode.encode(flag);
	}
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList(String salaryid){
		ArrayList fieldlist=new ArrayList();
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			if("-1".equals(salaryid)){
				buf.append(" midvariable where nflag=4 and templetid=0 ");
				buf.append(" and cstate=-1");
			}else if("-2".equals(salaryid)){//xcs modify @ 2013-8-6 处理数据采集的计算公式定义
				buf.append(" midvariable where nflag=5 and templetid=0 ");
			}else{
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				buf.append(" and (cstate is null or cstate='");
				buf.append(salaryid);
				buf.append("')");
			}
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					item.setCodesetid("0");
					break;
				case 2:
					item.setItemtype("A");
					item.setCodesetid("0");
					break;
				case 4:
					item.setItemtype("A");
					item.setCodesetid(rset.getString("codesetid"));
					break;
				case 3:
					item.setItemtype("D");
					item.setCodesetid("0");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			if(!"-1".equals(salaryid)){
				String sqlstr = "select * from salaryset";
				if(salaryid!=null&&salaryid.trim().length()>0){
					sqlstr+=" where salaryid="+salaryid;
				}
				rset=dao.search(sqlstr);
				while(rset.next()){
					FieldItem item=new FieldItem();
					item.setItemid(rset.getString("ITEMID"));
					item.setItemdesc(rset.getString("ITEMDESC"));
					item.setFieldsetid(rset.getString("FIELDSETID"));
					item.setItemlength(rset.getInt("ITEMLENGTH"));
					item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
					item.setDecimalwidth(rset.getInt("DECWIDTH"));
					item.setItemtype(rset.getString("ITEMTYPE"));
					item.setCodesetid(rset.getString("CODESETID"));
					item.setVarible(0);
					fieldlist.add(item);
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
	
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public int getprimarykey() {
		return primarykey;
	}

	public void setprimarykey(int primarykey) {
		this.primarykey = primarykey;
	}

}
