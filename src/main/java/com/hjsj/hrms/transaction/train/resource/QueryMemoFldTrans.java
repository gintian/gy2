package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.train.resource.TrainResourceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:QueryMemoFldTrans.java</p>
 * <p>
 * Description:查询备注字段交易类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class QueryMemoFldTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String type = (String) hm.get("type");
		if (!TrainResourceBo.hasTrainResourcePrivByType(type, this.userView))
		   throw GeneralExceptionHandler.Handle(new GeneralException("","您没有操作此功能的权限！","",""));
		
		String priFld=(String) hm.get("priFld");
		priFld = PubFunc.decrypt(SafeCode.decode(priFld));
		String memoFldName = (String)hm.get("memoFldName");
		String classid = (String)hm.get("classid");
		String dbname = "";
		if(hm.get("dbname") != null){
			dbname = hm.get("dbname").toString();
			dbname = PubFunc.decrypt(SafeCode.decode(dbname));
		}
		String flag = (String) hm.get("flag");//是否可编辑  1：不可编辑
		hm.remove("flag");
		
		TrainResourceBo bo = null;
		if("9".equals(type))
			bo = new TrainResourceBo(this.frameconn, type, classid);
		else
			bo = new TrainResourceBo(this.frameconn, type);
		String memoFild = "";
		if(!"".equals(dbname)){			
			memoFild = bo.getMemoFld1(priFld,memoFldName,dbname);
		}else{
			memoFild = bo.getMemoFld(priFld, memoFldName);
		}
		
		String[] memoFilds = memoFild.split("@@");
		
		this.getFormHM().put("memoFld", memoFilds[0]);
		this.getFormHM().put("classid", classid);
		this.getFormHM().put("itemdesc", memoFilds[1]);
		
		if((!"".equals(type))&&(type.compareTo("7")<0))
		  flag = getEditFlag(type);
		this.getFormHM().put("flag", flag);
    }
    
    private String getEditFlag(String type)
    {
    	String editFlag = null;
    	
    	int tabType = Integer.valueOf(type).intValue();
    	
    	switch(tabType)
    	{
    	case 1: //培训机构
    		if(!this.userView.hasTheFunction("3230102"))
    			editFlag = "1";    		
    		break;
    	case 2: //培训教师
    		if(!this.userView.hasTheFunction("3230202"))
    			editFlag = "1";
    		break;
    	case 3: //培训场所
    		if(!this.userView.hasTheFunction("3230302"))
    			editFlag = "1";
    		break;
    	case 4: //培训设施
    		if(!this.userView.hasTheFunction("3230402"))
    			editFlag = "1";
    		break;
    	case 5: //培训资料
    		if(!this.userView.hasTheFunction("3230506"))
    			editFlag = "1";
    		break;
    	case 6: //培训类别
    		if(!this.userView.hasTheFunction("3230002"))
    			editFlag = "1";
    		break;
    	default:
    		break;    	
    	}
    	
    	return editFlag;
    }

}
