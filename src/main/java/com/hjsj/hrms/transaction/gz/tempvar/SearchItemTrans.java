package com.hjsj.hrms.transaction.gz.tempvar;

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
		
		if("1".equals(flag)){
			hm.put("itemlist",tempvarbo.itemList(fieldsetid,this.userView,"N"));
		}else{
			if("tempvar".equalsIgnoreCase(fieldsetid)){
				hm.put("itemlist",itemList(type,cstate));
			}else{
				hm.put("itemlist",tempvarbo.getItemList(fieldsetid,this.userView,flag));
			}
		}
	}
	private ArrayList itemList(String type,String cstate){
		ArrayList itemlist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select nid,chz from midvariable");
		if("3".equals(type)){
		    //strsql.append(" where templetid="+cstate+" order by sorting"); xcs modify @2013-11-6 
			strsql.append(" where nflag=0 and (templetid="+cstate+" or (templetid<>0 and Cstate='1')) order by sorting");//该模版中能查看到的临时变量（自身的临时变量和人事异动中共享的临时变量）
		}else if("1".equals(type)){
		    if("-1".equals(cstate)){//这是从薪资总额里面进去的
		        strsql.append(" where nflag=4 and templetid=0 and cstate=-1 order by sorting");  
		    }else{//这个是 薪资类别的
		        strsql.append(" where templetid=0 and(cstate='"+cstate+"' or cstate is null) and nflag=0 order by sorting ");
		    }
		}
		else{//其余情况予以保留
			strsql.append(" where cstate='"+cstate+"' order by sorting");
		}
		CommonData dataobj1 = new CommonData("","");
		itemlist.add(dataobj1);
		ContentDAO dao  = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(strsql.toString());
			while(this.frowset.next()){
				CommonData dataobj = new CommonData(Integer.toString(this.frowset.getInt("nid")),
						Integer.toString(this.frowset.getInt("nid"))+":"+this.frowset.getString("chz"));
				itemlist.add(dataobj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return itemlist;
	}

}
