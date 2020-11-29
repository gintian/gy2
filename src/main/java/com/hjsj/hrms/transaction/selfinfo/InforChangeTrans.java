package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InforChangeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		
		MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,this.userView);
		
		String allflag = (String)this.getFormHM().get("allflag");
		allflag=allflag!=null&&allflag.trim().length()>0?allflag:"01";

		String viewitem = (String)reqhm.get("viewitem");
		viewitem=viewitem!=null&&viewitem.trim().length()>0?viewitem:"0";

		String chg_id = (String) reqhm.get("chg_id");
		if(chg_id != null && chg_id.length() > 0)
            chg_id = PubFunc.decrypt(chg_id);
		
		chg_id=chg_id!=null&&chg_id.trim().length()>0?chg_id:mysel.getChg_id();
		reqhm.remove("chg_id");
			
		ArrayList fieldlist = mysel.queryMyselfFieldSetListFormChgid(chg_id,"01,02,03,07");
	
		String sflag=(String)reqhm.get("sflag");
		sflag=sflag!=null&&sflag.trim().length()>0?sflag:"closes";
		reqhm.remove("sflag");

		String savEdit=(String)reqhm.get("savEdit");
		savEdit=savEdit!=null&&savEdit.trim().length()>0?savEdit:"search";
		reqhm.remove("savEdit");
		
		String setid=(String)reqhm.get("setid");
		if(setid != null && setid.length() > 0)
		    setid = PubFunc.decrypt(setid);
		
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		//reqhm.remove("setid");//update  by xiegh on date  20180417 bug36743 setid清空后  点击radio时，如果setid为空 默认去第一个子集
		if(!this.userView.isSuper_admin()&&"0".equals(this.userView.analyseTablePriv(setid,0))){
			return;
		}
		if("opens".equalsIgnoreCase(sflag)){
			
			if(setid.trim().length()<1){
				if(fieldlist.size()>0){
					FieldSet set = (FieldSet)fieldlist.get(0);
					setid = set.getFieldsetid();
				}
			}
			mysel.getOtherParamList(chg_id,setid,"01,02,03,07");
			ArrayList keylist = mysel.getKeyvalueList();
			String keyid=(String)reqhm.get("keyid");
			if(keyid != null && keyid.length() > 0 && !keyid.matches("^\\d+$") 
			        && !"-1".equalsIgnoreCase(keyid))
			    keyid = PubFunc.decrypt(keyid);
			
			keyid=keyid!=null&&keyid.trim().length()>0?keyid:(String)keylist.get(0);
			reqhm.remove("keyid");
				
			ArrayList typelist = mysel.getTypeList();
			String typeid=(String)reqhm.get("typeid");
			typeid=typeid!=null&&typeid.trim().length()>0?typeid:(String)typelist.get(0);
			reqhm.remove("typeid");
			
			ArrayList sequenceList = mysel.getSequenceList();
			String sequenceid=(String)reqhm.get("sequenceid");
			sequenceid=sequenceid!=null&&sequenceid.trim().length()>0?sequenceid:(String)sequenceList.get(0);
			reqhm.remove("sequenceid");
				
			ArrayList newFieldList=new ArrayList();
			ArrayList multimediaInfoList=new ArrayList();
			ArrayList oldFieldList = new ArrayList();
			if("save".equalsIgnoreCase(savEdit)){
				newFieldList = (ArrayList)this.getFormHM().get("newFieldList");
				oldFieldList = (ArrayList)this.getFormHM().get("oldFieldList");
//				itemlist = (ArrayList)this.getFormHM().get("itemlist");
//					oldFieldList=cloneList(itemlist);
//					itemlist = cloneList(newFieldList);
				String sp_flag=(String)reqhm.get("sp_flag");
				sp_flag=sp_flag!=null&&sp_flag.trim().length()>0?sp_flag:mysel.getRecord_spflag();
				reqhm.remove("sp_flag");
				FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				mysel.updateMyselfData(chg_id,fieldset,newFieldList,oldFieldList,typeid,sp_flag,keyid,sequenceid); 
				this.getFormHM().put("sp_flag",sp_flag);
			}else if("search".equalsIgnoreCase(savEdit)){
				mysel.getOneMyselfData(chg_id,setid,keyid,typeid,sequenceid,viewitem);
				newFieldList = mysel.getNewValueList();
				oldFieldList = mysel.getOldValueList();
				multimediaInfoList=mysel.getMultimediaInfoList();
				this.getFormHM().put("sp_flag",mysel.getRecord_spflag());
			}else if("app".equalsIgnoreCase(savEdit)){
				FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				mysel.updateApployMyselfDataApp(chg_id,fieldset,"02",keyid,typeid,sequenceid); 
				fieldlist = mysel.queryMyselfFieldSetListFormChgid(chg_id,"01,02,03,07");
				newFieldList = (ArrayList)this.getFormHM().get("newFieldList");
//				itemlist = (ArrayList)this.getFormHM().get("itemlist");
				oldFieldList = (ArrayList)this.getFormHM().get("oldFieldList");	
				this.getFormHM().put("sp_flag","02");
			}else if("del".equalsIgnoreCase(savEdit)){
				FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				mysel.delMyselfData(chg_id,fieldset,typeid,keyid,sequenceid); 
				
				mysel.getOtherParamList(chg_id,setid,"01,02,03,07");
				keylist = mysel.getKeyvalueList();
				if(keylist.size()>0)
					keyid=(String)keylist.get(0);

				typelist = mysel.getTypeList();
				if(typelist.size()>0)
					typeid=(String)typelist.get(0);
				
				sequenceList = mysel.getSequenceList();
				if(sequenceList.size()>0)
					sequenceid=(String)sequenceList.get(0);
				
				mysel.getOneMyselfData(chg_id,setid,keyid,typeid,sequenceid,viewitem);
				newFieldList = mysel.getNewValueList();
				oldFieldList = mysel.getOldValueList();
//				itemlist=cloneList(newFieldList);
				this.getFormHM().put("sp_flag",mysel.getRecord_spflag());
				fieldlist = mysel.queryMyselfFieldSetListFormChgid(chg_id,"01,02,03,07");
				if(fieldlist.size()<1){
					delRecode(chg_id);
				}
				String description = "";
			    for(int i=0;i<fieldlist.size();i++){
			    	FieldSet fieldSetvlaue = (FieldSet)fieldlist.get(i);   
			    	description+=fieldSetvlaue.getCustomdesc()+",";
			    }
			    if(fieldlist.size()>0)
			    	updateDescription(chg_id,description);
				this.getFormHM().put("sp_flag",mysel.getRecord_spflag());
			}
			this.getFormHM().put("oldFieldList",changeList(oldFieldList));
			this.getFormHM().put("keylist",keylist);
			this.getFormHM().put("keyid",PubFunc.encrypt(keyid));
			this.getFormHM().put("typeid",typeid);
			this.getFormHM().put("typelist",typelist);
			this.getFormHM().put("sequenceid",sequenceid);
			this.getFormHM().put("sequenceList",sequenceList);
			
			this.getFormHM().put("newFieldList",changeList(newFieldList));
			this.getFormHM().put("multimediaInfoList",multimediaInfoList);
			
		}
		
		this.getFormHM().put("fieldlist",fieldlist);
		this.getFormHM().put("chg_id",PubFunc.encrypt(chg_id));
		this.getFormHM().put("viewitem",viewitem);
		this.getFormHM().put("sflag",sflag);
		this.getFormHM().put("allflag",mysel.getSp_flag());
		this.getFormHM().put("setid",PubFunc.encrypt(setid));
	}
	public ArrayList changeList(ArrayList list){
		ArrayList itemlist = new ArrayList();
		
		for(int i=0;i<list.size();i++){
			FieldItem item = (FieldItem)list.get(i);
			ArrayList priitemlist = this.userView.getPrivFieldList(item.getFieldsetid(),0);
			FieldItem fielditem = null;
			for(int j=0;j<priitemlist.size();j++){
				fielditem = (FieldItem)priitemlist.get(j);
				if(fielditem!=null&&fielditem.getItemid().equalsIgnoreCase(item.getItemid()))
					break;
			}
			if(fielditem==null)
				continue;
			item.setPriv_status(fielditem.getPriv_status());
			if(fielditem.isCode()){
				item.setViewvalue(AdminCode.getCodeName(item.getCodesetid(),item.getValue()));
			}
			itemlist.add(item);
		}
		return itemlist;
	}
	public ArrayList cloneList(ArrayList list){
		ArrayList itemlist = new ArrayList();
		FieldItem item =null;
		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
			item=(FieldItem)fielditem.clone();
			itemlist.add(item);
		}
		return itemlist;
	}
	private void delRecode(String chg_id){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update("delete from t_hr_mydata_chg where chg_id="+chg_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void updateDescription(String chg_id,String description){
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		ArrayList deslist = new ArrayList();
		deslist.add(description);
		deslist.add(chg_id);
		list.add(deslist);
		try {
			dao.update("update t_hr_mydata_chg set description=? where chg_id=?",deslist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
