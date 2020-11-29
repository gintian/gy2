package com.hjsj.hrms.module.gz.mytax.transaction;

import com.hjsj.hrms.module.gz.mytax.businessobject.MyTaxService;
import com.hjsj.hrms.module.gz.mytax.businessobject.impl.MyTaxServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTaxParameterTrans extends IBusiness {
    @Override
    public void execute() {
        MyTaxService myTaxService = new MyTaxServiceImpl(this.frameconn);
        String operateType = (String) this.getFormHM().get("type");
        Map return_data = null;
        String return_code = "success";
        String return_msg = "";
        try {
            if (StringUtils.equals(operateType, "main")) {
                Map itemsMap = myTaxService.getMyTaxItemList(this.userView);
                List gzItems = myTaxService.listGzTaxMxField(this.userView);
                return_data = new HashMap();
                List itemList = (List) itemsMap.get("itemList");//正常显示在前端的itemList
                List removeItemList = (List) itemsMap.get("removeItemList");//提示用户已经被删除的指标列表
                if(removeItemList.size()>0){//如果指标有发生变化，则将最新的重新保存一下
                    myTaxService.saveMyTaxItem(itemList,this.userView);
                }
                String tips = ((MyTaxServiceImpl)myTaxService).getTaxParamColumn(this.userView,itemList,false);
                return_data.put("items",itemList);//正常显示的配置指标项
                return_data.put("removeItemList",itemsMap.get("removeItemList"));//被移除的item提示信息
                return_data.put("gz_items",gzItems);
                return_data.put("tips",tips);
            }else if(StringUtils.equals(operateType, "add")){
                List itemList = (ArrayList) this.getFormHM().get("data");
                myTaxService.saveMyTaxItem(itemList,this.userView);
            }else if(StringUtils.equals(operateType, "getFormulaCodeData")){
                String itemid = (String) this.getFormHM().get("itemid");
                List list = ((MyTaxServiceImpl) myTaxService).getCodeItems(itemid);
                this.getFormHM().put("data",list);
            }else if(StringUtils.equals(operateType, "checkFormula")){
                String c_expr = (String) this.getFormHM().get("c_expr"); //计算公式内容
                c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
                String itemType = (String) this.getFormHM().get("itemType");
                String info = myTaxService.checkFormula(userView,c_expr,itemType);
                return_data = new HashMap();
                return_data.put("info",info);
            }
        } catch (GeneralException e) {
            return_code = "fail";
            return_msg = e.getErrorDescription();
            e.printStackTrace();
        }
        this.getFormHM().put("return_data", return_data);
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_msg", return_msg);
    }
}
