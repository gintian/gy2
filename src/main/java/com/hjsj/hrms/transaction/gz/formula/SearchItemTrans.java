package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hrms.frame.dao.ContentDAO;
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
public class SearchItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		TempvarBo tempvarbo = new TempvarBo();
		
		String fieldsetid = (String)hm.get("fieldsetid");
		fieldsetid = fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
		
		String type = (String)hm.get("type");
		type = type!=null&&type.length()>0?type:"";
		
		String cstate = (String)hm.get("cstate");
		cstate = cstate!=null&&cstate.length()>0?cstate:"";
		
		String flag = (String)hm.get("flag");
		flag = flag!=null&&flag.length()>0?flag:"0";
		
		String infor = (String) hm.get("infor");
		
		if("1".equals(flag)){
			hm.put("itemlist",tempvarbo.itemList1(fieldsetid,this.userView,"N"));
		}else{
			if("tempvar".equalsIgnoreCase(fieldsetid)){
				hm.put("itemlist",itemList(type,cstate));
			}else{
				if("2".equals(flag)){
					if ("5".equalsIgnoreCase(infor)) {
						ArrayList itemList = tempvarbo.itemList2(fieldsetid,this.userView);
						ArrayList list = new ArrayList();
						for (int i = 0; i < itemList.size(); i++) {
							CommonData common = (CommonData) itemList.get(i);
							String value = common.getDataValue();
							if ("E0122".equalsIgnoreCase(value) || "B0110".equalsIgnoreCase(value) 
									|| "R4502".equalsIgnoreCase(value) || "R4501".equalsIgnoreCase(value)) {
								continue;
							}
							list.add(common);
						}
						itemList = list;
						hm.put("itemlist", itemList);
						
					} else {
						if("2".equalsIgnoreCase(infor)){
							GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
							HashMap map = bo.getValuesMap();
							String spflagid = ((String)map.get("sp_flag"));
							String fc_flag=(String)map.get("fc_flag");
							String setid=(String)map.get("setid");
							if(setid.equalsIgnoreCase(fieldsetid))
								hm.put("itemlist", tempvarbo.itemList2(fieldsetid, this.userView, spflagid, fc_flag));
							else{
								hm.put("itemlist",tempvarbo.itemList2(fieldsetid,this.userView));
							}
						}else{
							hm.put("itemlist",tempvarbo.itemList3(fieldsetid,this.userView));//zgd 2014-2-26 记录录入中计算是要走写权限
						}
					}
				}else
					hm.put("itemlist",tempvarbo.itemList1(fieldsetid,this.userView));
			}
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

}
