package com.hjsj.hrms.transaction.ht.inform;

import com.hjsj.hrms.businessobject.ht.inform.ContracInforBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchItemTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String a0100 = (String)hm.get("a0100");
		a0100=a0100!=null?a0100:"";
		hm.remove("a0100");
		
		String dbname = (String)hm.get("dbname");
		dbname=dbname!=null?dbname:"";
		hm.remove("dbname");
		
		String ctflag = (String)hm.get("ctflag");
		ctflag=ctflag!=null?ctflag:"";
		hm.remove("ctflag");
		
		if(dbname.trim().length()<1){
			ArrayList dblist = this.userView.getPrivDbList();
			if(dblist.size()>0)
				dbname = (String)dblist.get(0);
		}
		
		String fieldid = (String)hm.get("fieldid");
		fieldid=fieldid!=null?fieldid:"";
		hm.remove("fieldid");
		if(fieldid.trim().length()<1){
			ArrayList setlist = (ArrayList)this.getFormHM().get("setlist");
			for(int i=0;i<setlist.size();i++){
				CommonData cod = (CommonData)setlist.get(i);
				if("A01".equalsIgnoreCase(cod.getDataValue()))
					continue;
				else{
					fieldid = cod.getDataValue();
					break;
				}
			}
		}

		ArrayList list=getFieldList(fieldid);
		
		String itemtable = dbname+fieldid;
		StringBuffer strsql=new StringBuffer();
		String maintable=dbname+"A01";
		String fields=getFields(list, maintable);				
		strsql.append("select ");
		strsql.append(fields);
		strsql.append(" from ");
		strsql.append(itemtable);
		strsql.append(" a right join ");
		strsql.append(maintable);
		strsql.append(" on ");
		strsql.append(maintable);
		strsql.append(".A0100=");
		strsql.append(" a ");
		strsql.append(".A0100 ");
		strsql.append(" where ");
		strsql.append(maintable);
		strsql.append(".A0100 ");				
		strsql.append("='" );
		strsql.append(a0100);
		strsql.append("' and a.a0100 is not null ");
//		strsql.append(" and a.a0100 <>''");

		// 查看是否存在记录
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {			
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) {
				this.getFormHM().put("count", "1");
			} else {
				this.getFormHM().put("count", "0");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} 
		ConstantXml csxml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
		ContracInforBo ctbo = new ContracInforBo(this.frameconn,csxml);
		String htmainflagid = ctbo.getHtmainFlagID(fieldid); //获取状态标识指标
		if(htmainflagid!=null&&htmainflagid.trim().length()>0)
			this.getFormHM().put("checkflag","1");
		else
			this.getFormHM().put("checkflag","0");
			
		
		ArrayList ctflaglist = ctbo.ctflagList(ctflag);
		this.getFormHM().put("ctflaglist",ctflaglist);
		this.getFormHM().put("a0100", a0100);
		this.getFormHM().put("dbname", dbname);
		this.getFormHM().put("itemtable", itemtable);
		this.getFormHM().put("itemsql", strsql.toString());
		this.getFormHM().put("itemlist",list);
		this.getFormHM().put("defitem", fieldid);
		this.getFormHM().put("ctflag", ctflag);
	}
	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldList(String setname){
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
		ArrayList list = gzbo.itemList(fieldset);
		ArrayList fieldlist=new ArrayList();

		Field tempfield=new Field("A0100","A0100");
		tempfield.setDatatype(DataType.STRING);
		tempfield.setLength(8);
		tempfield.setReadonly(true);			
		tempfield.setVisible(false);
		fieldlist.add(tempfield);

		int I9999 = 1;
		for(int i=0;i<list.size();i++){
			Field field=(Field)list.get(i);
			String itemid=field.getName();

			if("B0110".equalsIgnoreCase(itemid)){
				field.setReadonly(true);
				field.setVisible(false);
			}else if("E0122".equalsIgnoreCase(itemid)){
				field.setReadonly(true);
				field.setVisible(false);
			}else if("E01A1".equalsIgnoreCase(itemid)){
				field.setReadonly(true);
				field.setVisible(false);
			}else if("A0101".equalsIgnoreCase(itemid)){
				field.setReadonly(true);
				field.setVisible(false);
			}
			if("0".equals(this.userView.analyseFieldPriv(itemid)))
				field.setVisible(false);
			if("1".equals(this.userView.analyseFieldPriv(itemid)))
				field.setReadonly(true);
			if(!"2".equals(this.userView.analyseTablePriv(setname)))
				field.setReadonly(true);
			field.setSortable(true);
			fieldlist.add(field);
			if(!fieldset.isMainset()){
				if("A0101".equalsIgnoreCase(itemid)&&I9999>0){
					Field itemfield=new Field("I9999","序号");
					itemfield.setDatatype(DataType.INT);
					itemfield.setReadonly(true);
					itemfield.setVisible(false);
					fieldlist.add(itemfield);
					I9999=0;
				}
			}
		}//i loop end.
		FieldItem item3=new FieldItem();
		item3.setFieldsetid(setname);
		item3.setItemid("oper");
		item3.setItemdesc(ResourceFactory.getProperty("column.operation"));
		item3.setItemtype("A");
		item3.setCodesetid("0");
		item3.setAlign("center");
		item3.setReadonly(true);
		fieldlist.add(item3.cloneField());
		return fieldlist;
	}
	/**
	 * 求得当前数据集中的查询字段列表
	 * @param list
	 * @return
	 */
	private String getFields(ArrayList list,String maintable)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<list.size();i++)
		{
			Field field=(Field)list.get(i);
			if("A0100".equalsIgnoreCase(field.getName()))
			{
				buf.append(maintable);
				buf.append(".A0100,");
			}
			else if(!"oper".equalsIgnoreCase(field.getName()))
				buf.append(field.getName()+",");
		
		}//for i loop end.
		buf.append("'' as oper,");
		buf.setLength(buf.length()-1);
		return buf.toString();
	}
}
