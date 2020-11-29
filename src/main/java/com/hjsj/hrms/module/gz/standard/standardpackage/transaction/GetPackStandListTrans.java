package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Title GetPackStandListTrans
 * @Description 获取历史沿革引用的标准表
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */
public class GetPackStandListTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        ArrayList ref_standList=new ArrayList();
        HashMap returnStrMap=new HashMap();
        HashMap return_data=new HashMap();
        String return_code="success";
        String return_msg="";
        try {
            HashMap recordMap=null;
            String pkg_id = (String) this.getFormHM().get("pkg_id");
            pkg_id = PubFunc.decrypt(pkg_id);
            IStandardPackageService   iStandardPackageService=new StandardPackageServiceImpl(this.frameconn,this.userView);
            List<RecordVo> voList =iStandardPackageService.getStandListOfPackage(pkg_id);
            for (int i = 0; i < voList.size(); i++) {
                recordMap= new HashMap();
                RecordVo vo=voList.get(i);
                recordMap.put("id",vo.getInt("id") );
                recordMap.put("name",vo.getString("name"));
                ref_standList.add(recordMap);
            } 
            return_data.put("ref_standList", ref_standList);
        }catch(GeneralException e) {
            return_code = "fail";
            return_msg = e.getErrorDescription();
            e.printStackTrace();
        }finally {
            returnStrMap.put("return_code",return_code );
            returnStrMap.put("return_msg",return_msg );
            returnStrMap.put("return_data", return_data);
            this.formHM.put("returnStr", returnStrMap);
        }
        
    }
}
