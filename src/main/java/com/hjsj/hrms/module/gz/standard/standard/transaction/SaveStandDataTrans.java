package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 标准表数据保存交易类
 * @Author wangz
 * @Date 2019/12/3 12:01
 * @Version V1.0
 **/
public class SaveStandDataTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map returnData = new HashMap();
        String returnCode = "success";
        String returnMsgCode = "";
        IStandTableService standTableServiceImpl = new StandTableServiceImpl(this.frameconn,this.userView);
        String pkg_id = (String) this.getFormHM().get("pkg_id");
        pkg_id = PubFunc.decrypt(pkg_id);
        String stanard_id = (String) this.getFormHM().get("stanard_id");
        stanard_id = PubFunc.decrypt(stanard_id);
        List dataList = (List) this.getFormHM().get("data");
        String saveType = (String) this.getFormHM().get("saveType");
        try{
            if (StringUtils.equalsIgnoreCase("edit", saveType)) {
                standTableServiceImpl.saveStandData(pkg_id, stanard_id, dataList);
            }else if(StringUtils.equalsIgnoreCase("struct",saveType) || StringUtils.equalsIgnoreCase("create",saveType)){
                MorphDynaBean standInfor = (MorphDynaBean) this.getFormHM().get("standInfor");
                Map standInforMap = PubFunc.DynaBean2Map(standInfor);
                stanard_id = standTableServiceImpl.saveStandData(pkg_id,stanard_id,standInforMap,dataList,saveType);
            }
        }catch (GeneralException e){
            returnCode = "fail";
        }
        stanard_id = PubFunc.encrypt(stanard_id);
        this.getFormHM().put("stanard_id",stanard_id);
        this.getFormHM().put("returnData",returnData);
        this.getFormHM().put("returnCode",returnCode);
        this.getFormHM().put("returnMsgCode",returnMsgCode);

    }
}
