package com.hjsj.hrms.transaction.competencymodal.postseq_commodal;

import com.hjsj.hrms.businessobject.competencymodal.PostModalBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
/**
 * <p>Title:SearchPostModalTrans.java</p>
 * <p>Description>:SearchPostModalTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 29, 2011  3:02:55 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchPostModalTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String object_type=(String)this.getFormHM().get("object_type");
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String a_code=(String)map.get("a_code");
//			if(!a_code.equals("@K"))
//			a_code =PubFunc.decryption(a_code);
			String codeitemid=a_code.substring(2);
			PostModalBo bo = new PostModalBo(this.getFrameconn(),this.getUserView());
			String codesetid=(String)this.getFormHM().get("codesetid");
			String historyDate=(String)this.getFormHM().get("historyDate");
			if(historyDate==null|| "".equals(historyDate)){
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();				
				historyDate = sdf.format(calendar.getTime());
			}
			ArrayList postModalList=bo.getCompetencyModalList(codeitemid, object_type, codesetid,historyDate);
			this.getFormHM().put("postModalList", postModalList);
			this.getFormHM().put("codesetid", a_code.substring(0,2));
			this.getFormHM().put("codeitemid",codeitemid);
			this.getFormHM().put("object_type",object_type);
			this.getFormHM().put("historyDate", historyDate);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
