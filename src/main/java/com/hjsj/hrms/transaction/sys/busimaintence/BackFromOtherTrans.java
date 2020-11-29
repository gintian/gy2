package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class BackFromOtherTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
//		ContentDAO dao=new ContentDAO(this.getFrameconn());
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		RecordVo busiFieldVo=new RecordVo("t_hr_busifield");
		String fieldid=(String) reqhm.get("fieldid");
//		String fieldlength=(String) reqhm.get("fieldlength");
		String fieldesc=(String) reqhm.get("fieldesc");
		String feildMemo=(String) reqhm.get("fieldMemo");
		String reserveitem=(String) reqhm.get("reserveitem");
		busiFieldVo.setString("itemdesc",fieldesc);
		busiFieldVo.setString("itemtype","A/R");
		busiFieldVo.setString("itemid",fieldid);
		busiFieldVo.setString("itemmemo",feildMemo);
		busiFieldVo.setString("reserveitem",reserveitem);
		busiFieldVo.setString("itemlength","10");
		
		hm.put("busiFieldVo",this.putRecord(busiFieldVo));
		
	}
	public RecordVo putRecord(RecordVo busiFiledVo){
		String itemtype=busiFiledVo.getString("itemtype");
		String codeflag=busiFiledVo.getString("codeflag");
		String codesetid=busiFiledVo.getString("codesetid");
		if("A".equals(itemtype)){
			if("0".equals(codesetid)){
				busiFiledVo.setString("itemtype","A/S");
			}else{
				if("0".equals(codeflag)){
					busiFiledVo.setString("itemtype","A/C");
				}else{
					busiFiledVo.setString("itemtype","A/R");
				}
			}
		}
		
		return busiFiledVo;
	}

}
