package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 导入标准表数据交易类
 * @Author wangz
 * @Date 2019/12/3 12:03
 * @Version V1.0
 **/
public class ImportStandDataTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map return_map;
        //历史沿革号
        String pkg_id=(String)this.getFormHM().get("pkg_id");
        //薪资标准号
        String stand_id=(String)this.getFormHM().get("stand_id");
        //文件信息
        HashMap fileInfo = PubFunc.DynaBean2Map((MorphDynaBean) (this.formHM.get("fileInfo")));
        String return_code = "success";
        String return_msg = "";
        HashMap return_data = new HashMap();
        try {
            pkg_id = PubFunc.decrypt(SafeCode.decode(pkg_id));
            stand_id = PubFunc.decrypt(SafeCode.decode(stand_id));
            IStandTableService standTableService = new StandTableServiceImpl(this.frameconn,this.userView);
            return_map = standTableService.importStandData((String) fileInfo.get("fileid"), pkg_id, stand_id);
            return_msg = (String) return_map.get("return_msg");
            return_code = (String) return_map.get("return_code");
            if("fail".equals(return_code)){
                return_data.put("errorlog_path",return_map.get("errorLog_path"));
            }
            this.getFormHM().put("return_code",return_code);
            this.getFormHM().put("return_msg",return_msg);
        }catch (Exception e){
            this.getFormHM().put("return_msg",return_msg);
            this.getFormHM().put("return_code","fail");
        }
        this.getFormHM().put("return_data",return_data);
    }
}
