package com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class BudgetSysBo {
	private Connection conn = null;
	private UserView userView = null;
	public BudgetSysBo(Connection conn, UserView userView)
	{
		this.conn = conn;
		this.userView = userView;
	}
	//得到各属性对应的值
	public HashMap getSysValueMap(){
		HashMap map = new HashMap();
		try{
			String kindstr = "";//预算表分类
			String rylb_codeset = "";//人员类别代码类
			String txCode="";//退休人员编码
			String unitmenu = ""; //归属单位指标
			String dblist = "";//参与预算的人员库
			String range = "";//参与预算的人员范围
			String rylb_value = "";//人员类别取值公式
			String txrq_value = "";//退休日期取值公式
			String units = "";//参与预算的单位 
			String createTXrecord = "";//是否生成退休记录0=否，1=是
			String datatoze = "";//预算数据到总额
			
			String ysze_set = "";//预算总额子集
			String ysze_idx_menu = "";//预算索引指标
			String ysze_ze_menu = "";//预算总额指标
			String ysze_status_menu = "";//状态
			
			String ysparam_set = "";//预算参数子集
			String ysparam_idx_menu = "";//预算索引指标
			String ysparam_newmonth_menu = "";//新员工入职月份指标
			
			RowSet rowSet = null;
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet = dao.search("select str_value from Constant where Constant='GZ_BUDGET_PARAMS'");
			if(rowSet.next()){
				String str_value = Sql_switcher.readMemo(rowSet,"str_value");
				if (str_value != null && str_value.trim().length()>0){
					    Document doc = PubFunc.generateDom(str_value);
					    String xpath = "//params";
					    XPath xpath_ = XPath.newInstance(xpath);
					    Element ele = (Element) xpath_.selectSingleNode(doc);
					    Element child;
					    if (ele != null){
							child = ele.getChild("kindstr");
							if (child != null){
								kindstr = child.getTextTrim();
							}
							child = ele.getChild("rylb");
							if (child != null){
								rylb_codeset = child.getAttributeValue("codeset");
							}

							child = ele.getChild("units");
							if (child != null){
								units = child.getTextTrim();
							}
							child = ele.getChild("namelist");
							if (child != null)
							{
								createTXrecord = child.getAttributeValue("createTXrecord");
								txCode = child.getAttributeValue("txCode");
							}
							child = ele.getChild("datatoze");
							if (child != null){
								datatoze = child.getTextTrim();
							}
							
							
							child = ele.getChild("ysze");
							if (child != null)
							{
								ysze_set = child.getAttributeValue("set");
								ysze_idx_menu = child.getAttributeValue("idx_menu");
								ysze_ze_menu = 	child.getAttributeValue("ze_menu");
								ysze_status_menu = child.getAttributeValue("status_menu");
							}
							
							
							child = ele.getChild("ysparam");
							if (child != null)
							{
								ysparam_set = child.getAttributeValue("set");
								ysparam_idx_menu = child.getAttributeValue("idx_menu");
								ysparam_newmonth_menu = child.getAttributeValue("newmonth_menu");
							}
							
							xpath = "//params/namelist";
						    xpath_ = XPath.newInstance(xpath);
						    ele = (Element) xpath_.selectSingleNode(doc);
						    Element child2;
							child2 = ele.getChild("unitmenu");
							if (child2 != null){
								unitmenu = child2.getTextTrim();
							}
							child2 = ele.getChild("dblist");
							if (child2 != null){
								dblist = child2.getTextTrim();
							}
							child2 = ele.getChild("range");
							if (child2 != null){
								range = child2.getTextTrim();
							}
							child2 = ele.getChild("rylb_value");
							if (child2 != null){
								rylb_value = child2.getTextTrim();
							}
							child2 = ele.getChild("txrq_value");
							if (child2 != null){
								txrq_value = child2.getTextTrim();
							}
					    }	

				}
         
			}
	       map.put("kindstr", kindstr);
           map.put("rylb_codeset", rylb_codeset);
           map.put("unitmenu", unitmenu);
           map.put("dblist", dblist);
           map.put("range", range);
           map.put("rylb_value", rylb_value);
           map.put("txrq_value", txrq_value);
           map.put("units", units);
           map.put("createTXrecord", createTXrecord);
           map.put("datatoze", datatoze);
           
           map.put("ysze_set", ysze_set);
           map.put("ysze_idx_menu", ysze_idx_menu);
           map.put("ysze_ze_menu", ysze_ze_menu);
           map.put("ysze_status_menu", ysze_status_menu);
           
           map.put("txCode", txCode);
           map.put("ysparam_set", ysparam_set);
           map.put("ysparam_idx_menu", ysparam_idx_menu);
           map.put("ysparam_newmonth_menu", ysparam_newmonth_menu);
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 预算参数中的必选参数是否已设置且有效 
	 * @return
	 */
	public boolean isParamValid(){
		HashMap map = getSysValueMap();
		String kindstr = (String)map.get("kindstr");//预算表分类
		String rylb_codeset = (String)map.get("rylb_codeset");//人员类别代码类
		String unitmenu = (String)map.get("unitmenu"); //归属单位指标
		String dblist = (String)map.get("dblist");//参与预算的人员库
		String range = (String)map.get("range");//参与预算的人员范围
		String rylb_value = (String)map.get("rylb_value");//人员类别取值公式
		String txrq_value = (String)map.get("txrq_value");//退休日期取值公式
		String units = (String)map.get("units");//参与预算的单位 
		String createTXrecord = (String)map.get("createTXrecord");//是否生成退休记录0=否，1=是
		String datatoze = (String)map.get("datatoze");//预算数据到总额
		
		String ysze_set = (String)map.get("ysze_set");//预算总额子集
		String ysze_idx_menu = (String)map.get("ysze_idx_menu");//预算索引指标
		String ysze_ze_menu = (String)map.get("ysze_ze_menu");//预算总额指标
		String ysze_status_menu = (String)map.get("ysze_status_menu");//状态
		
		String ysparam_set = (String)map.get("ysparam_set");//预算参数子集
		String ysparam_idx_menu = (String)map.get("ysparam_idx_menu");//预算索引指标
		String ysparam_newmonth_menu = (String)map.get("ysparam_newmonth_menu");//新员工入职月份指标
		
		if(kindstr==null) kindstr="";
		if(rylb_codeset==null) rylb_codeset="";
		if(unitmenu==null) unitmenu="";
		if(dblist==null) dblist="";
		if(range==null) range="";
		if(rylb_value==null) rylb_value="";
		if(txrq_value==null) txrq_value="";
		if(units==null) units="";
		if(createTXrecord==null) createTXrecord="";
		if(datatoze==null) datatoze="";
		if(ysze_set==null) ysze_set="";
		if(ysze_idx_menu==null) ysze_idx_menu="";
		if(ysze_ze_menu==null) ysze_ze_menu="";
		if(ysze_status_menu==null) ysze_status_menu="";
		if(ysparam_set==null) ysparam_set="";
		if(ysparam_idx_menu==null) ysparam_idx_menu="";
		if(ysparam_newmonth_menu==null) ysparam_newmonth_menu="";
		
		if (units.length()==0) return false;
		if (ysze_set.length()==0||ysze_idx_menu.length()==0||ysze_ze_menu.length()==0||ysze_status_menu.length()==0)
			return false;
		
		// 检查构库情况
		FieldSet set = null;
		FieldItem fld = null;
		set = DataDictionary.getFieldSetVo(ysze_set);
		if (set == null||!"1".equals(set.getUseflag())) return false;
		fld = DataDictionary.getFieldItem(ysze_idx_menu); 
		if (fld == null||!"1".equals(fld.getUseflag())) return false;
		fld = DataDictionary.getFieldItem(ysze_ze_menu); 
		if (fld == null||!"1".equals(fld.getUseflag())) return false;
		fld = DataDictionary.getFieldItem(ysze_status_menu); 
		if (fld == null||!"1".equals(fld.getUseflag())) return false;
		
		if (ysparam_set.length()==0||ysparam_idx_menu.length()==0)
			return false;

		set = DataDictionary.getFieldSetVo(ysparam_set);
		if (set == null||!"1".equals(set.getUseflag())) return false;
		fld = DataDictionary.getFieldItem(ysparam_idx_menu); 
		if (fld == null||!"1".equals(fld.getUseflag())) return false;

		return true;
	}
	
	//根据人员库代码得到人员库名字
	public String getDblistName(String dblist){
		String dbnames = "";
		if(dblist==null)
			return "";
		String[] temp=dblist.split(",");
		String pre = "('')";
		if(temp.length!=0){
			pre="('";
			int n = temp.length;
			for(int i=0;i<n;i++){
				if(i==n-1)
					pre +=temp[i]+"')";
				else
					pre +=temp[i]+"','";
			}
		}
		try{
			RowSet rowSet = null;
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search("select dbname from DBName where Pre in "+pre);
			while(rowSet.next()){
				String dbname = rowSet.getString("dbname");
				dbnames +=dbname+",";
			}
			if(rowSet!=null){
				rowSet.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return dbnames;
	}
	//把单位中的UN去掉
	public String getNewUnits(String units){
		String newUnits = "";
		String[] temp = units.split(",");
		for(int i=0;i<temp.length;i++){
			if(temp[i].indexOf("UN")!=-1)//如果单位带着UN
				newUnits+=temp[i].substring(2)+",";//那么就截去
			else
				newUnits+=temp[i]+",";
		}
		return newUnits;
	}
	//根据单位代码找到单位名字
	public String getUnitsName(String units){
		if(units==null || "".equals(units))
			return "";
		String unitname = "";
		String[] temp=units.split(",");
		String unitno = "('')";
		if(temp.length!=0){
			unitno="('";
			int n = temp.length;
			for(int i=0;i<n;i++){
				if(i==n-1)
					unitno +=temp[i]+"')";
				else
					unitno +=temp[i]+"','";
			}
			
		}
		try{
			RowSet rowSet = null;
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search("select codeitemdesc from organization where codesetid='UN' and codeitemid in"+unitno);
			while(rowSet.next()){
				String codeitemdesc = rowSet.getString("codeitemdesc");
				unitname +=codeitemdesc+",";
			}
			if(rowSet!=null){
				rowSet.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return unitname;
	}
	//得到人员类别的List
	public ArrayList getRylbList(){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			rowSet = dao.search("select codesetdesc,codesetid from codeset");
			while(rowSet.next()){
				String codesetid = rowSet.getString("codesetid");
				String codesetdesc = codesetid + " " + rowSet.getString("codesetdesc");
				CommonData obj=new CommonData(codesetid,codesetdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到退休人员编码的List
	public ArrayList getTxrecordList(String rylb_codeset){
		ArrayList txrecordList = new ArrayList();
		RowSet rowSet = null;
		if(rylb_codeset==null || rylb_codeset.length()==0){
			CommonData dataobj = new CommonData();
			dataobj = new CommonData("", "");
			txrecordList.add(dataobj);
		}else{
			ContentDAO dao = new ContentDAO(this.conn);

			String sqlstr = "select codeitemid,codeitemdesc from codeitem where codesetid='"+rylb_codeset+"'";
			CommonData dataobj = new CommonData();
			dataobj = new CommonData("", "");
			txrecordList.add(dataobj);
			try {
				rowSet = dao.search(sqlstr);
				while (rowSet.next()) {
					dataobj = new CommonData(rowSet.getString("codeitemid"),
							rowSet.getString("codeitemid")+" "+rowSet.getString("codeitemdesc"));
					txrecordList.add(dataobj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return txrecordList;
	}
	//得到人员归属单位指标
	public ArrayList getUnitList(){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			obj2=new CommonData("B0110","单位名称");//b0110 系统项
			list.add(obj2);
			rowSet = dao.search("select itemid,itemdesc from fielditem where codesetid='UN' and itemtype='A' and  useflag=1 order by displayid");
			while(rowSet.next()){
				String itemid = rowSet.getString("itemid");
				String itemdesc = rowSet.getString("itemdesc");
				CommonData obj=new CommonData(itemid,itemdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到预算总额子集
	public ArrayList getBudgetSetList(){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			rowSet = dao.search("select fieldsetid,customdesc from fieldset where  changeFlag=2 and UseFlag=1 and fieldSetId like 'B%' and fieldsetid<>'b01' order by displayorder");
			while(rowSet.next()){
				String fieldsetid = rowSet.getString("fieldsetid");
				String customdesc = rowSet.getString("customdesc");
				CommonData obj=new CommonData(fieldsetid,customdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到预算总额设置-->预算索引指标
	public ArrayList getBudgetIndexList(String fieldsetid){
	    if (fieldsetid==null) fieldsetid ="";
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			rowSet = dao.search("select itemid,itemdesc from fielditem where fieldsetid = '"+fieldsetid+"' and useflag=1 and itemtype='N' and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z1' and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z0' and decimalwidth=0 order by displayid");
			while(rowSet.next()){
				String itemid = rowSet.getString("itemid");
				String itemdesc = rowSet.getString("itemdesc");
				CommonData obj=new CommonData(itemid,itemdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到预算总额设置-->预算总额指标
	public ArrayList getBudgetTotalList(String fieldsetid){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			rowSet = dao.search("select itemid,itemdesc from fielditem where fieldsetid = '"+fieldsetid+"' and useflag=1 and itemtype='N' and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z1' and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z0' order by displayid");
			while(rowSet.next()){
				String itemid = rowSet.getString("itemid");
				String itemdesc = rowSet.getString("itemdesc");
				CommonData obj=new CommonData(itemid,itemdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到预算总额设置-->审批状态指标
	public ArrayList getSpStatusList(String fieldsetid){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			rowSet = dao.search("select itemid,itemdesc from fielditem where fieldsetid = '"+fieldsetid+"' and useflag=1 and itemtype='A' and codesetid='23' and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z1' and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z0' order by displayid");
			while(rowSet.next()){
				String itemid = rowSet.getString("itemid");
				String itemdesc = rowSet.getString("itemdesc");
				CommonData obj=new CommonData(itemid,itemdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到预算参数设置-->预算参数子集
	public ArrayList getBudgetParamSetList(){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			rowSet = dao.search("select fieldsetid,customdesc from fieldset where changeFlag=2 and UseFlag=1 and fieldSetId like 'B%' and fieldSetId<>'b01' order by displayorder");
			while(rowSet.next()){
				String fieldsetid = rowSet.getString("fieldsetid");
				String customdesc = rowSet.getString("customdesc");
				CommonData obj=new CommonData(fieldsetid,customdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到预算参数设置-->预算索引指标
	public ArrayList getBudgetIndexFieldList(String fieldsetid){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			rowSet = dao.search("select itemid,itemdesc from fielditem where fieldsetid = '"+fieldsetid+"' and useflag=1 and itemtype='N' and decimalwidth=0 and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z1' and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z0' order by displayid");
			while(rowSet.next()){
				String itemid = rowSet.getString("itemid");
				String itemdesc = rowSet.getString("itemdesc");
				CommonData obj=new CommonData(itemid,itemdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到预算参数设置-->新员工入职月份指标
	public ArrayList getEmployeeList(String fieldsetid){
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
//			rowSet = dao.search("select itemid,itemdesc from fielditem where fieldsetid = '"+fieldsetid+"' and useflag=1 and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z0'  and itemtype='D'");
			rowSet = dao.search("select itemid,itemdesc from fielditem where fieldsetid = '"+fieldsetid+"' and useflag=1 and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z1' and lower(itemid)<>'"+fieldsetid.toLowerCase()+"z0' and itemtype='N' and decimalwidth=0 order by displayid");
			while(rowSet.next()){
				String itemid = rowSet.getString("itemid");
				String itemdesc = rowSet.getString("itemdesc");
				CommonData obj=new CommonData(itemid,itemdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

}
