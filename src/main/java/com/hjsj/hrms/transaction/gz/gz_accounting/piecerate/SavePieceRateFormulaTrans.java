package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceRateFormulaBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePieceRateFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();		
		String strResult = "no";
		ContentDAO dao = new ContentDAO(this.frameconn);
		PieceRateFormulaBo formulabo = new PieceRateFormulaBo(this.getFrameconn(),"",this.userView);	
		String model= (String)hm.get("model");
		String busiid= (String)hm.get("busiid");
		busiid=busiid!=null&&busiid.trim().length()>0?busiid:"";
		try
		{
			if ("saveformula".equalsIgnoreCase(model))
			{
				String formulaitemid= (String)hm.get("formulaitemid");
				formulaitemid=formulaitemid!=null&&formulaitemid.trim().length()>0?formulaitemid:"";
				strResult = formulabo.AddFormula(dao,formulaitemid,busiid);
				if ("ok".equals(strResult)){
					hm.put("formulaid",formulabo.GetMaxFormulaId(dao, busiid));
				}			
			}
			else  if ("savesort".equalsIgnoreCase(model))
			{
				String sort_fields= (String)hm.get("sorting");
				sort_fields=sort_fields!=null&&sort_fields.trim().length()>0?sort_fields:"";
				strResult = formulabo.AdjustSeq(dao,sort_fields,busiid);			
			}
			else  if ("delformula".equalsIgnoreCase(model))
			{
				String formulaids= (String)hm.get("formulaids");
				formulaids=formulaids!=null&&formulaids.trim().length()>0?formulaids:"";
				strResult = formulabo.DelFormula(dao,formulaids,busiid);			
			}
			else  if ("savecond".equalsIgnoreCase(model))
			{
				String conditions = (String)hm.get("conditions");
				String formulaid = (String)hm.get("formulaid");
				
				conditions= PubFunc.keyWord_reback(SafeCode.decode(conditions));
				strResult = formulabo.SaveFormulaCond(dao,formulaid,conditions);
				hm.put("strResult",strResult);
			}
			else  if ("saveformulacontent".equalsIgnoreCase(model))
			{
				String formula = (String)hm.get("formula");
				String formulaid = (String)hm.get("formulaid");
				formula= PubFunc.keyWord_reback(SafeCode.decode(formula));
				strResult = formulabo.SaveFormulaContent(dao,formulaid,formula);
				hm.put("strResult",strResult);
			}
			else  if ("getformulacontent".equalsIgnoreCase(model))
			{
				String formulaid = (String)hm.get("formulaid");
				PieceRateFormulaBo formulabo1 = new PieceRateFormulaBo(this.getFrameconn(),formulaid,this.userView);	
				String formulavalue= SafeCode.encode(formulabo1.getRExpr());
				hm.put("formulavalue",formulavalue);
			}
			else  if ("calc".equalsIgnoreCase(model))//计算
			{
				ArrayList itemids=(ArrayList)this.getFormHM().get("itemids");
				String s0100 = (String)hm.get("s0100");
				if (formulabo.computing("s0100="+s0100, itemids, busiid,s0100)){
					strResult="ok";
				}

			}
			hm.put("strResult",strResult);	
		}catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}


	}



}
