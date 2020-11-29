package com.hjsj.hrms.transaction.org.gzdatamaint;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SearchNewRecrodValueTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		/**子集名*/
		String setname=(String)this.getFormHM().get("setname");	
		String type=(String)this.getFormHM().get("type");
		try{
		  FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
		  String i9999=(String)this.getFormHM().get("I9999");
		  String checkadd=(String)this.getFormHM().get("checkadd");
		  checkadd=checkadd!=null&&checkadd.trim().length()>0?checkadd:"";
		  if("insert".equalsIgnoreCase(type)){
			if(!fieldset.isMainset()){
				initSubSetValue(setname,i9999);
			}
		  }else{
			  if(!fieldset.isMainset()){
					initSubSetValue1(setname,checkadd);
			  }	else{
				  initMaintSubSetValue(setname); 
			  }		  
		  }
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
		this.getFormHM().put("setname",setname);
		String item ="";
		IDGenerator idg=new IDGenerator(2,this.getFrameconn());
		ArrayList fieldlist = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		if(fieldlist!=null){
			for(int i=0;i<fieldlist.size();i++){
				FieldItem fielditem = (FieldItem)fieldlist.get(i);
				if(fielditem!=null&&fielditem.isSequenceable()){
					String seq_no=idg.getId(setname+"."+fielditem.getItemid());
					item+=fielditem.getItemid()+",";
					this.getFormHM().put(fielditem.getItemid(),seq_no);
				}
			}
		}
		this.getFormHM().put("fielditem",item);

	}
	/**
	 * 初始化子集参数
	 * @param setname
	 * @param curri9999 //当前插入记录的值
	 * @throws GeneralException
	 */
	private void initSubSetValue(String setname,String curri9999)throws GeneralException {
		GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
		String setvalue=(String)this.getFormHM().get("itemid");
		String itemid = "";
		String infor="2";
		if(!"K".equalsIgnoreCase(setname.substring(0,1))){
			itemid="B0110";
		}else{
			itemid="E01A1";
		}
		String i9999=gzbo.insertSubSet(setname,itemid,setvalue,curri9999);
		this.getFormHM().put("I9999", i9999);
		this.getFormHM().put(itemid, setvalue);
	}
	private void initMaintSubSetValue(String setname)throws GeneralException {
		GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
		String setvalue=(String)this.getFormHM().get("itemid");
		String itemid = "";
		String infor = "2";
		String e0122 ="";
		if(!"K".equalsIgnoreCase(setname.substring(0,1))){
			itemid="B0110";
		}else{
			itemid="E01A1";
			infor = "3";
			e0122 = codeItemid(setvalue,"UM");
			this.getFormHM().put("E0122",e0122);
			String b0110 = codeItemid(e0122,"UN");
			this.getFormHM().put("B0110",e0122);
		}
		this.getFormHM().put(itemid, setvalue);
		gzbo.insertMaintSubSet(setname,itemid,setvalue,"E0122",e0122,infor);
	}
	private void initSubSetValue1(String setname,String checkadd)throws GeneralException {
		GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
		String setvalue=(String)this.getFormHM().get("itemid");
		String itemid = "";
		String infor = "2";
		if(!"K".equalsIgnoreCase(setname.substring(0,1))){
			itemid="B0110";
			String i9999=gzbo.insertSubSet2(setname,itemid,setvalue,checkadd,infor);
			this.getFormHM().put("I9999", i9999);
		}else{
			itemid="E01A1";
			infor = "3";
			String e0122 = codeItemid(setvalue,"UM");
			this.getFormHM().put("E0122",e0122);
			String b0110 = codeItemid(e0122,"UN");
			this.getFormHM().put("B0110",e0122);
			String i9999=gzbo.insertSubSet2(setname,itemid,setvalue,"E0122",e0122,checkadd,infor);
			this.getFormHM().put("I9999", i9999);
		}
		
		this.getFormHM().put(itemid, setvalue);
		this.getFormHM().put("infor", infor);
	}
	private String codeItemid(String code,String codesetid){
		String codeitemid="";
		StringBuffer strsql=new StringBuffer();
		strsql.append("select codeitemid,codesetid,parentid from organization where codeitemid='");
		strsql.append(code);
		strsql.append("' and codeitemid<>parentid");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(strsql.toString());
			if(this.frowset.next()){		
				String codeid =this.frowset.getString("codeitemid");
				String setid =this.frowset.getString("codesetid");
				String paprentid =this.frowset.getString("parentid");
				if(setid.equalsIgnoreCase(codesetid)){
					codeitemid=codeid;
				}else{
					codeitemid = codeItemid(paprentid,codesetid);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return codeitemid;
	}
}
