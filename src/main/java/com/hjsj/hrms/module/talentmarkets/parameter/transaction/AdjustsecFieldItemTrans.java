package com.hjsj.hrms.module.talentmarkets.parameter.transaction;


import com.hjsj.hrms.module.talentmarkets.parameter.businessobject.TalentMarketsParameterService;
import com.hjsj.hrms.module.talentmarkets.parameter.businessobject.impl.TalentMarketsParameterServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *
 * @Titile: AdjustsecFieldItemTrans
 * @Description:拖拽排序交易类
 * @Company:hjsj
 * @Create time: 2019年8月8日下午6:33:48
 * @author: wangdi
 * @version 1.0
 *
 */
public class AdjustsecFieldItemTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
	    try{
            TalentMarketsParameterService talentMarketsParameterService=new TalentMarketsParameterServiceImpl(this.frameconn);
            //String ori_itemid=(String)this.formHM.get("ori_itemid");
            //拖动前itemid集合
            ArrayList<String> oriItemList = (ArrayList<String>) this.formHM.get("oriItemArr");
            //拖动后itemid
            String toItemid=(String)this.formHM.get("to_itemid");
            String dropPosition = (String)this.formHM.get("dropPosition");
            String message=talentMarketsParameterService.dragAndDropSort(oriItemList,toItemid,dropPosition);
            this.formHM.put("return_msg", message);
        }catch (Exception e){
	        e.printStackTrace();
        }
	}
	
}
