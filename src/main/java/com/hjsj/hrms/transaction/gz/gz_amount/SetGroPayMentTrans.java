package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossPayManagement;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

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
public class SetGroPayMentTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		
		GrossPayManagement gross = new GrossPayManagement(this.getFrameconn(),"GZ_PARAM");
		ArrayList setidlist = gross.elementName("/Params/Gz_amount","setid");
		ArrayList amountlist = gross.elementName("/Params/Gz_amount","amount");
		ArrayList remainlist = gross.elementName("/Params/Gz_amount","remain");
		ArrayList spflaglist = gross.elementName("/Params/Gz_amount","sp_flag");
		
		ArrayList fieldsetlist = gross.fieldsetList();
		CommonData obj= null;
		String fieldsetid = (String)hm.get("fieldsetid");
		fieldsetid=fieldsetid!=null?fieldsetid:"";
		
		if(fieldsetlist.size()>0){
			obj = (CommonData)fieldsetlist.get(0);
			fieldsetid=fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:obj.getDataValue();
		}else{
			fieldsetid=fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
		}
		
		if(fieldsetid.length()<1){
			if(setidlist.size()>0){
				fieldsetid = setidlist.get(0)!=null?setidlist.get(0).toString():"";
			}
		}

		hm.put("fieldsetid",fieldsetid);
		hm.put("fieldsetlist",fieldsetlist);
		
		ArrayList fielditemlist = gross.fielditemList(fieldsetid);
		String fielditemid = (String)hm.get("fielditemid");
		fielditemid=fielditemid!=null?fielditemid:"";
		
		if(fielditemid.length()<1){
			if(amountlist.size()>0){
				fielditemid = amountlist.get(0).toString();
			}
		}
		
		if(fielditemlist.size()>0){
			obj = (CommonData)fielditemlist.get(0);
			fielditemid=fielditemid!=null?fielditemid:obj.getDataValue();
		}else{
			fielditemid=fielditemid!=null?fielditemid:"";
		}
		
		hm.put("fielditemid",fielditemid);
		hm.put("fielditemlist",fielditemlist);
		
		ArrayList nlist = gross.nList(fieldsetid);
		String nid = (String)hm.get("nid");
		nid=nid!=null?nid:"";
		
		if(nid.length()<1){
			if(remainlist.size()>0){
				nid = remainlist.get(0).toString();
			}
		}
		
		if(nlist.size()>0){
			obj = (CommonData)nlist.get(0);
			nid=nid!=null?nid:obj.getDataValue();
		}else{
			nid=nid!=null?nid:"";
		}
		
		hm.put("nid",nid);
		hm.put("nlist",nlist);
		
		ArrayList sp_flaglist = gross.spFlagList(fieldsetid);
		String spflagid = (String)hm.get("spflagid");
		spflagid=spflagid!=null?spflagid:"";
		
		if(spflagid.length()<1){
			if(spflaglist.size()>0){
				spflagid = spflaglist.get(0)!=null?spflaglist.get(0).toString():"";
			}
		}
		
		if(sp_flaglist.size()>0){
			obj = (CommonData)sp_flaglist.get(0);
			spflagid=spflagid!=null?spflagid:obj.getDataValue();
		}else{
			spflagid=spflagid!=null?spflagid:"";
		}
		
		hm.put("spflagid",spflagid);
		hm.put("spflaglist",sp_flaglist);
	}

}
