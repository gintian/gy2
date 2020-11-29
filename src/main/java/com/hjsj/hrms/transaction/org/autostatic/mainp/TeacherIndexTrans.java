package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class TeacherIndexTrans extends IBusiness{

	public void execute() throws GeneralException {
		ArrayList list = DataDictionary.getFieldList("r04", Constant.USED_FIELD_SET);
		String ajax = (String) this.formHM.get("ajax");
		ArrayList desc = new ArrayList();
		
		for(int i = 0 ; i < list.size();i++){
			FieldItem fieldItem = (FieldItem)list.get(i);
			if(!"r0401".equalsIgnoreCase(fieldItem.getItemid()) && !"r0402".equalsIgnoreCase(fieldItem.getItemid())
					&&!"r0414".equalsIgnoreCase(fieldItem.getItemid())&&!"r0415".equalsIgnoreCase(fieldItem.getItemid())
					&&!"b0110".equalsIgnoreCase(fieldItem.getItemid())){
					desc.add(fieldItem);
			}
		}
		CommonData cd = new CommonData("#","请选择...");
		ArrayList ass = new ArrayList();
		ass.add(cd);
		ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		String subset = constantbo.getNodeAttributeValue("/param/teacher_items", "subset");
			String dest = constantbo.getNodeAttributeValue("/param/teacher_items", "dest");
			String src = constantbo.getNodeAttributeValue("/param/teacher_items", "src");
		
		this.getFormHM().put("ass", ass);
		this.getFormHM().put("dest", dest);
		this.getFormHM().put("src",src );
		this.getFormHM().put("people", subset);
	//	this.getFormHM().put("dest", dest);
		if("1".equalsIgnoreCase(ajax))
			this.getFormHM().remove("desc");
		else
			this.getFormHM().put("desc", desc);
		//this.getFormHM().put("list", lists);
		this.getFormHM().put("list", getFieldDescs()); //人员主集子集
		this.getFormHM().put("codes", getFieldItem(subset));
	}
	
	//得到人员主集子集
	public ArrayList getFieldDescs(){
		ArrayList list = new ArrayList();
		String sql = "select fieldSetid,customdesc from FieldSet where fieldSetid like'A%' and useFlag = '1' order by displayorder";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		CommonData c = new CommonData("#","请选择...");
		list.add(c);
		try {
			rs = dao.search(sql);
			while(rs.next()){
				c = new CommonData(rs.getString("fieldsetid"),rs.getString("customdesc"));
				list.add(c);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//根据指标类型得到相应的指标
	public ArrayList getFieldItem(String subset){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		String sql = "";
		String itemid = "";
		String type = "";
		String codeSetId = "";
		ArrayList  arrs = new ArrayList();
		
		HashMap hs = new HashMap();
		if(null != this.getFormHM().get("itemid")){
			itemid = this.getFormHM().get("itemid").toString();
			type = getTypeById(itemid);
			if("A".equalsIgnoreCase(type)){
				codeSetId = getCodesetIdById(itemid);
			}
		}
		String people = "";
		if(null != this.getFormHM().get("peoples")){
			people = this.getFormHM().get("peoples").toString();
		}
		String flag ="";
		if(null != this.getFormHM().get("flag")){
		 flag = this.getFormHM().get("flag").toString();
		}
		CommonData c = new CommonData("#","请选择...");
		list.add(c);
		try {
			if(!"".equals(flag) && flag != null 
					&& !"".equals(type) && "1".equals(flag) && "".equals(codeSetId)){
			    sql = "select Itemid,itemdesc from FieldItem where fieldsetid ='"+people+"' and itemtype = '"+type+"'  and useFlag = '1' order by displayid";
			    if(!"".equals(sql)){
					this.frowset = dao.search(sql);
					while(this.frowset.next()){
						c = new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc"));
						list.add(c);
					}
				}
			}else if(!"".equals(flag) && null != flag
					&& !"".equals(type) && !"".equals(codeSetId) && "1".equals(flag)){
				sql = "select Itemid,itemdesc from FieldItem where fieldsetid ='"+people+"' and itemtype = '"+type+"' and codesetid = '"+codeSetId+"'  and useFlag = '1' order by displayid";
				if(!"".equals(sql)){
					this.frowset = dao.search(sql);
					while(this.frowset.next()){
						c = new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc"));
						list.add(c);
					}
				}
			}else{
				if(!"".equals(subset) && subset != null){
					ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
					String src = constantbo.getNodeAttributeValue("/param/teacher_items", "src");
					String [] sc = src.split(",");
					
					String [] sb = subset.split(",");
					for(int i = 0 ; i < sb.length ; i ++){
						list = new ArrayList();
						CommonData cc = new CommonData("#","请选择...");
						list.add(cc);
						sql = "select itemid,itemdesc from fielditem where fieldsetid = '"+sb[i]+"' and useflag = '1' order by displayid";
						//System.out.println(sql);
						if(!"".equals(sql)){
							this.frowset = dao.search(sql);
							while(this.frowset.next()){
								c = new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc"));
								list.add(c);
							}
							arrs.add(list);
							if(i < sc.length){
								hs.put(sc[i], list);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(this.getFormHM().get("itemid") != null){
			String d1 = this.getFormHM().get("itemid").toString();
			this.getFormHM().put("d1", d1);				
		}
		this.getFormHM().put("has", hs);
		this.getFormHM().put("arrs", arrs);
		for(int j = 0 ; j < list.size() ; j ++){
			CommonData c1 = (CommonData)list.get(j);
		}
		return list;
	}
	
	//通过ID找到类型
	private String getTypeById(String id){
		String s = "";
		ArrayList list = DataDictionary.getFieldList("r04", Constant.USED_FIELD_SET);
		for(int i = 0 ; i < list.size() ; i ++){
			FieldItem fieldItem = (FieldItem)list.get(i);
			if(fieldItem.getItemid().equalsIgnoreCase(id)){
				s = fieldItem.getItemtype();
			}
		}
		return s;
	}
	
	private String getCodesetIdById(String id){
		String s = "";
		ArrayList list = DataDictionary.getFieldList("r04", Constant.USED_FIELD_SET);
		for(int i = 0 ; i < list.size() ; i ++){
			FieldItem fieldItem = (FieldItem)list.get(i);
			if(fieldItem.getItemid().equalsIgnoreCase(id)){
				s = fieldItem.getCodesetid();
			}
		}
		return s;
	}
	
	private String getDescById(String id){
		String desc = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = " select fieldsetdesc from fieldset where fieldsetid = '"+id+"' ";
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				desc = this.frowset.getString("fieldsetdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return desc;
	}
}
