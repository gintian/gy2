package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class AddPieceRateDetailTrans extends IBusiness {

	public void execute() throws GeneralException {
		try 
		{
			HashMap hm = this.getFormHM();
			String s0100=(String) hm.get("s0100");
			if ((s0100==null)|| ("".equals(s0100))) { s0100="0";}
			String model=(String) hm.get("model");
			String strSel =(String) hm.get("strsel");
			PieceRateBo TaskBo=new PieceRateBo(this.getFrameconn(),s0100,this.userView);
			if ("handselproduct".equalsIgnoreCase(model))
			{	if (TaskBo.HandAddProduct(strSel))
			   {
					return;					
				}
			}
			else if ("handselpeople".equalsIgnoreCase(model))
			{	if (TaskBo.HandAddPeople(strSel))
			   {
					return;					
				}
			}
			else if ("condselpeople".equalsIgnoreCase(model))
			{	String strwhere =SafeCode.decode((String) hm.get("strwhere"));
			strwhere=PubFunc.keyWord_reback(strwhere);
			
				if (TaskBo.CondAddPeople(strSel,strwhere))
			   {
					return;					
				}
			}
			else if ("delpeople".equalsIgnoreCase(model))
			{	if (TaskBo.DelPeople(strSel))
			   {
					return;					
				}
			}
			else if ("delproduct".equalsIgnoreCase(model))
			{	if (TaskBo.DelProduct(strSel))
			   {
					return;					
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
