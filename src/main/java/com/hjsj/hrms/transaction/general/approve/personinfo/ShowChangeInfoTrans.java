package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.general.approve.personinfo.PersonInfoBo;
import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class ShowChangeInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		try{
		    if(reqhm == null || reqhm.size() < 1){
		        //jax发起的请求，执行以下代码
		        String index = (String) this.getFormHM().get("index");
		        index = StringUtils.isEmpty(index) ? "0" : index;
		        String chg_id = (String) this.getFormHM().get("chgId");
		        if(StringUtils.isNotEmpty(chg_id))
		            chg_id = PubFunc.decrypt(chg_id);
		        
		        String setId = (String) this.getFormHM().get("fieldSetId");
		        
		        PersonInfoBo pibo = new PersonInfoBo(this.frameconn, chg_id,this.userView);
		        HashMap map = pibo.fieldSetList(setId);
		        ArrayList itemList = (ArrayList) map.get("itemlist");
		        HashMap columnsMap = (HashMap) map.get("columns");
		        String fieldSetId = (String) map.get("setid");
		        String fieldSetid = (String) map.get("fieldSetid");
		        String multimedia = (String) map.get("multimedia");
		        String isMainSet = (String) map.get("isMainSet");
		        ArrayList multimedialist = (ArrayList) map.get("multimedialist");
		        
		        this.getFormHM().put("index", index);
		        this.getFormHM().put("multimedia", multimedia);
		        this.getFormHM().put("fieldSetId", fieldSetId);
		        this.getFormHM().put("setId", fieldSetid);
		        this.getFormHM().put("itemList", itemList);
		        this.getFormHM().put("columns", columnsMap);
		        this.getFormHM().put("isMainSet", isMainSet);
		        this.getFormHM().put("multimedialist", multimedialist);
		        
		    } else{
		        //.do请求执行以下代码
		        String allitem = (String) reqhm.get("allitem");
		        allitem = allitem != null && allitem.trim().length() > 0 ? allitem : "y";
		        
		        String chg_id = (String) reqhm.get("chg_id");
		        chg_id = chg_id != null && chg_id.trim().length() > 0 ? chg_id :this.getFormHM().get("chg_id").toString();
		        if(chg_id != null && chg_id.length() > 0)
		            chg_id = PubFunc.decrypt(chg_id);
		        
		        reqhm.remove("chg_id");
		        
		        String setid = (String) reqhm.get("setid");
		        if(setid != null && setid.length() > 0)
		            setid = PubFunc.decrypt(setid);
		        
		        setid = setid != null && setid.trim().length() > 0 ? setid : "";
		        reqhm.remove("setid");
		        
		        String state = (String) reqhm.get("state");
		        state = state != null && state.trim().length() > 0 ? state : "";
		        reqhm.remove("state");
		        
		        String keyid = (String) reqhm.get("recordid");
		        if(keyid != null && keyid.length() > 0)
		        	keyid = PubFunc.validateNum(keyid,4)?keyid:PubFunc.decrypt(keyid);
		        
		        keyid = keyid != null && keyid.trim().length() > 0 ? keyid : "";
		        
		        String typeid = (String) reqhm.get("type");
		        typeid = typeid != null && typeid.trim().length() > 0 ? typeid : "";
		        
		        String sequenceid = (String) reqhm.get("sequence");
		        sequenceid = sequenceid != null && sequenceid.trim().length() > 0 ? sequenceid
		                : "";
		        
		        String showinfo = this.getFormHM().get("showinfo").toString();
		        
		        
		        PersonInfoBo pibo = new PersonInfoBo(this.frameconn, chg_id,this.userView);
		        
		        //审批
		        if("pz".equals(state)){
		            
		            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
		            pibo.updateApployMyselfDataApp(chg_id, fieldset, "03",
		                    keyid, typeid, sequenceid);
		        } else if("bh".equals(state)){
		            pibo.setBatchReject(false);
		            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
		            pibo.updateApployMyselfDataApp(chg_id, fieldset, "07",
		                    keyid, typeid, sequenceid);
		        } else if("allpz".equals(state)){
		            if(StringUtils.isEmpty(setid))
                        pibo.setBatchReject(true);
                    else
                        pibo.setBatchReject(false);
                    
		            pibo.allapprove(chg_id,setid,"03");
		            if (setid==null || "".equals(setid)){//单个子集批准也走这段代码		    
		                MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,
		                        this.userView);
		                mysel.batchMyselfDataApply(chg_id, "03");
		                String orgAndName = mysel.setOrgInfo(userView
		                        .getDbname(), userView.getA0100(),
		                        this.frameconn);
		                //【17379】【6976】员工管理/信息审核，批示，如果业务用户做的批准，批示中姓名显示不出来，不对。
		                if("///".equals(orgAndName)){
		                    orgAndName = userView.getUserFullName();
		                }
		                mysel.approval(this.userView, orgAndName, "批准", chg_id,"");
		            }
		        } else if("allbh".equals(state)){
		            pibo.allapprove(chg_id,setid,"07");
		            if (setid==null || "".equals(setid)){        
		                MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,
		                        this.userView);       
		                mysel.batchMyselfDataApply(chg_id, "07");
//		                String orgAndName = mysel.setOrgInfo(userView
//		                        .getDbname(), userView.getA0100(),
//		                        this.frameconn);
		                //【17379】【6976】员工管理/信息审核，批示，如果业务用户做的批准，批示中姓名显示不出来，不对。
//		                if("///".equals(orgAndName)){
//		                    orgAndName = userView.getUserFullName();
//		                }
//		                mysel.approval(this.userView, orgAndName, "退回", chg_id,
//		                "");
		            }
		        }
		        
		        TreeMap changelist = new TreeMap();
		        if("change".equals(showinfo))
		            changelist=pibo.changelist();
		        else
		            changelist = pibo.alllist();
		        
		        ArrayList showlist = (ArrayList)this.getFormHM().get("showlist");
		        if(showlist == null || showlist.size()!=changelist.size()){
		            showlist=new ArrayList();
		            for(int i=0;i<changelist.size();i++){
		                showlist.add("Y");
		            }
		            this.getFormHM().put("showlist", showlist);
		        }
		        String b0110desc = pibo.getcodedesc(null, pibo.getB0110());
		        String e0122desc = pibo.getcodedesc(null, pibo.getE0122());
		        String a0100 = pibo.getA0100();
		        String nbase = pibo.getNbase();
		        a0100 = "~" + SafeCode.encode(PubFunc.convertTo64Base(a0100.toString()));
		        String username = pibo.getA0101();
		        this.getFormHM().put("b0110desc", b0110desc);
		        this.getFormHM().put("e0122desc", e0122desc);
		        this.getFormHM().put("username", username);
		        
		        this.getFormHM().put("changelist", changelist);
		        this.getFormHM().put("chg_id", PubFunc.encrypt(chg_id));
		        this.getFormHM().put("a0100", PubFunc.encrypt(a0100));
		        this.getFormHM().put("nbase", PubFunc.encrypt(nbase));
		    }
		}catch (Exception e) {
		    e.printStackTrace();
        }
	}
	   /**
     * 获得“单位/部门/职位/姓名”形式字符窜
     * 
     * @param userbase
     * @param A0100
     * @param dao
     */
 

}
