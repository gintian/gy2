package com.hjsj.hrms.module.gz.salaryStandard.transaction;

import com.hjsj.hrms.module.gz.salaryStandard.businessobject.SalaryStandardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * 
 * <p>Title:ImporStandardItemTrans.java</p>
 * <p>Description>:薪资标准导入</p>
 * <p>Company:HJSJ</p>
 * <p>@version: 7x</p>
 * <p>@author:zhanghua</p>
 */
public class ImporStandardItemTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String fileName = (String)this.getFormHM().get("fileName"); //得到上传控件返回的文件名
		fileName=PubFunc.decrypt(SafeCode.decode(fileName));
		String filePath = (String)this.getFormHM().get("filePath"); //文件路径
		filePath =PubFunc.decrypt(SafeCode.decode(filePath));
		File file = null;
		String type=(String)this.getFormHM().get("type");//导入为0 导出为1
		
		if("1".equalsIgnoreCase(type)){//导出错误信息

			file=new File(filePath+fileName); //生成文件对象
			
			SimpleDateFormat sdf = new SimpleDateFormat("hhmmss"); 
			sdf.format(new Date());
			
			String filename=this.getUserView().getUserName()+"_标准导入错误详情_"+sdf.format(new Date())+".xls";
			//重命名文件
			file.renameTo(new File(file.getPath().replaceAll(file.getName(), filename)));
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(filename)));
			return;
		}
		file=new File(filePath+fileName); //生成文件对象
		String pkgid=(String)this.getFormHM().get("pkgid");//历史沿革号
		String standardID=(String)this.getFormHM().get("standardID");//薪资标准号
		
		SalaryStandardBo bo=new SalaryStandardBo(pkgid, standardID, getUserView(),this.frameconn);
		
		
		LazyDynaBean designBean=bo.getStandHistory();//获取标准表结构
		ArrayList dataList=bo.getInputDataList(file, designBean);//拼接数据
		String errorMsg=bo.getIsError();//取得错误信息
		
		if(!StringUtils.isBlank(errorMsg)&&!"1".equalsIgnoreCase(errorMsg)){//文件本身错误
			this.getFormHM().put("errorMsg","数据导入失败，"+errorMsg);
			this.getFormHM().put("fileName","");
		}
		else if("1".equalsIgnoreCase(errorMsg)){//数据错误
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
			this.getFormHM().put("errorMsg","数据导入失败");
		}
		else{//若不存在错误 进行数据更新
			bo.proceedUpdateStandard(designBean, pkgid, standardID, dataList);
			this.getFormHM().put("errorMsg","");
			this.getFormHM().put("fileName","");
		}
		
	}
	
	

}
