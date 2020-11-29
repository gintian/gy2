package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:QuerySnapshotTrans.java
 * </p>
 * <p>
 * Description>:QuerySnapshotTrans.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Nov 19, 2010 5:18:56 PM
 * </p>
 * <p>
 * 
 * @version: 5.0
 *           </p>
 *           <p>
 * @author: LiWeichao
 *          </p>
 */
public class QuerySnapshotTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		HashMap reqHm = (HashMap) this.getFormHM().get("requestPamaHM");
		String org_m = (String) reqHm.get("org_m");
		org_m = org_m != null ? org_m : "";
		reqHm.remove("org_m");
		ArrayList leftlist = new ArrayList();
		ArrayList rightlist = new ArrayList();
		try {
			if (org_m.length() > 0) {
				String org_c = (String)reqHm.get("org_c");
				reqHm.remove("org_c");
				ArrayList blist = DataDictionary.getFieldSetList(1, 2);
				for(int i=0;i<blist.size();i++){
					FieldSet fieldset = (FieldSet)blist.get(i);
					String setid = fieldset.getFieldsetid();
					if(!"B00".equalsIgnoreCase(setid)&&!"B01".equalsIgnoreCase(setid)&&!org_m.equalsIgnoreCase(setid)){
						if(org_c.indexOf(setid)!=-1){
							
						}else{
							CommonData data = new CommonData(setid,fieldset.getCustomdesc());
							leftlist.add(data);
						}
					}
				}
				
				String tmp[]=org_c.split(",");
		       	for(int i=0;i<tmp.length;i++){
		       		String setid=tmp[i];
		       		if(setid.length()==3){
		       			FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				       	if(fieldset!=null){
				       		CommonData data = new CommonData(setid,fieldset.getCustomdesc());
							rightlist.add(data);
				       	}
		       		}
		       	}
				
			} else {
				ContentDAO dao = new ContentDAO(this.frameconn);
				String queryfieldstr = "";
				String xmlbasestr = "";
				String snap_fields="";
				this.frowset =dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
				if(frowset.next()){
					ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
					snap_fields =xml.getTextValue("/Emp_HisPoint/Struct");
					queryfieldstr =xml.getTextValue("/Emp_HisPoint/Query");
					xmlbasestr =xml.getTextValue("/Emp_HisPoint/Base");
				}else{
			           //设置的快照指标
					frowset = dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_STRUCT'");
	                if(frowset.next())
	                    snap_fields=frowset.getString("str_value");
					//设置的查询指标
	                frowset=dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_QUERY'");
					if(frowset.next())
						queryfieldstr=frowset.getString("str_value");
				}	
				
				String leftStr = /*(String) this.getFormHM().get("snap_norm")*/snap_fields;
				this.getFormHM().remove("snap_norm");
				if (leftStr == null || leftStr.length() <= 0)
					leftStr = "";
				String[] left_f = leftStr.split(",");
				for (int m = 0; m < left_f.length; m++) {
					if (left_f[m].length() > 0) {
						FieldItem fi = DataDictionary.getFieldItem(left_f[m]);
						if(fi==null)
							continue;
						CommonData obj = new CommonData(fi.getItemid(), fi
								.getItemdesc());
						leftlist.add(obj);
					}
				}
				String rightStr = /*(String) this.getFormHM().get("sn_left")*/queryfieldstr;
				this.getFormHM().remove("sn_left");
				if (rightStr == null || rightStr.length() <= 0)
					rightStr = "";
				String[] right_f = rightStr.split(",");
				for (int m = 0; m < right_f.length; m++) {
					if (right_f[m].length() > 0) {
						FieldItem fi = DataDictionary.getFieldItem(right_f[m]);
						if(fi==null)
							continue;
						CommonData obj = new CommonData(fi.getItemid(), fi
								.getItemdesc());
						rightlist.add(obj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getFormHM().put("leftlist", leftlist);
			this.getFormHM().put("rightlist", rightlist);
		}
	}

}
