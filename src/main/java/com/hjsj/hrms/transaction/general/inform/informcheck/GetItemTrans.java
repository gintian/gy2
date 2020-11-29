package com.hjsj.hrms.transaction.general.inform.informcheck;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GetItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();

		String fieldsetid = (String)hm.get("fieldsetid");
		fieldsetid = fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
		
		String type = (String)hm.get("type");
		type = type!=null&&type.length()>0?type:"";
		
		String cstate = (String)hm.get("cstate");
		cstate = cstate!=null&&cstate.length()>0?cstate:"";
		
		if("tempvar".equalsIgnoreCase(fieldsetid)){
			hm.put("itemlist",itemList(type,cstate));
		}else{
			hm.put("itemlist",getitemList(fieldsetid));
		}
	}
	private ArrayList itemList(String type,String cstate){
		ArrayList itemlist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select nid,chz from midvariable");
		if("3".equals(type)){
			strsql.append(" where templetid='"+cstate+"' order by sorting");
		}else{
			strsql.append(" where cstate='"+cstate+"' order by sorting");
		}
		CommonData dataobj1 = new CommonData("","");
		itemlist.add(dataobj1);
		ContentDAO dao  = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(strsql.toString());
			while(this.frowset.next()){
				CommonData dataobj = new CommonData(this.frowset.getString("nid"),
						this.frowset.getString("nid")+":"+this.frowset.getString("chz"));
				itemlist.add(dataobj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return itemlist;
	}
	/**
	 * 根据子集获取子标
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList getitemList(String fieldsetid){
		ArrayList list = new ArrayList();
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select itemid,itemdesc from fielditem where fieldsetid='");
		sqlstr.append(fieldsetid);
		sqlstr.append("' and useflag='1'");
		ContentDAO dao  = new ContentDAO(this.frameconn);
		CommonData dataobj1 = new CommonData("","");
		list.add(dataobj1);
		try {
			this.frowset = dao.search(sqlstr.toString());
			while(this.frowset.next()){
				String itemid =this.frowset.getString("itemid");
				if("0".equals(this.userView.analyseFieldPriv(itemid)))
					continue;
				String itemdesc = this.frowset.getString("itemdesc");
				if(!itemdesc.equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					CommonData dataobj = new CommonData(itemid,itemid.toUpperCase()+":"+itemdesc);
					list.add(dataobj);
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return list;
	}

}
