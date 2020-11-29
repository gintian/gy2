package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.io.InputStream;
import java.util.ArrayList;

/**
 *Title:SetExpTypeTrans
 *Description:显示上传excel与薪资项目的数据对比
 *Company:HJHJ
 *Create time:2015-7-3 
 *@author lis
 */
public class ShowNoRelationData extends IBusiness {

	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
		InputStream input = null;
		try
		{
			// 上传组件 vfs改造
            String fileid = (String)this.getFormHM().get("fileid");
            input = VfsService.getFile(fileid);
            
			String salaryid=(String)this.getFormHM().get("salaryid");
			
			salaryid=SafeCode.decode(salaryid); //解码
			salaryid =PubFunc.decrypt(salaryid); //解密
			
			SalaryInOutBo inOutBo=new SalaryInOutBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			
//			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.valueOf(salaryid),this.userView);
			
			/** 取得导入文件中列指标列表 */
			ArrayList<CommonData> originalHeadList=inOutBo.getOriginalDataFiledList2(input);
			
			/** 关联指标 */
			ArrayList<MorphDynaBean> relationItem=(ArrayList<MorphDynaBean>)this.getFormHM().get("relationItem");
			/* 薪资发放-导入-没对应数据结果错误问题 xiaoyun 2014-9-25 start */
			ArrayList<String> relationTemp = new ArrayList<String>();
			if(relationItem != null) {			
				for (MorphDynaBean bean : relationItem) {
					if(bean != null)
						relationTemp.add(PubFunc.keyWord_reback((String)bean.get("itemid")+":"+(String)bean.get("itemid2")));
				}
			}
			/* 薪资发放-导入-没对应数据结果错误问题 xiaoyun 2014-9-25 end */
			/**薪资类别*/
			ArrayList oriDataList=new ArrayList();
			// 由于现在vfs返回的流只能读一次 故这里再获取一遍
			input = VfsService.getFile(fileid);
			oriDataList=inOutBo.getOriDataList(input, relationTemp, originalHeadList);
			this.getFormHM().put("noRelationData", oriDataList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeIoResource(input);
		}
	}

}
