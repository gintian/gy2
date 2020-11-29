package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:ShowRelationTrans.java</p> 
 *<p>Description: 展现 没有对应/有同号 的数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 13, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class ShowRelationTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String showRelationType=(String)hm.get("b_showRelation");   // yes:有同号数据   no:没有对应数据
			String flag="1";
			if("yes".equals(showRelationType))
				flag="2";
			
			FormFile form_file = (FormFile)this.getFormHM().get("file");
			String salaryid=(String)this.getFormHM().get("salaryid");
			ArrayList originalDataList=(ArrayList)this.getFormHM().get("originalDataList");  ////源数据 列信息
			String[] relationItem=(String[])this.getFormHM().get("relationItem");			//关联指标
			/* 薪资发放-导入-没对应数据结果错误问题 xiaoyun 2014-9-25 start */
			if(relationItem != null) {			
				String[] relationTemp = new String[relationItem.length];
				for (int i = 0; i < relationItem.length; i++) {
					relationTemp[i] = PubFunc.keyWord_reback(relationItem[i]);
				}
				System.arraycopy(relationTemp, 0, relationItem, 0, relationItem.length);
			}
			/* 薪资发放-导入-没对应数据结果错误问题 xiaoyun 2014-9-25 end */
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			ArrayList oriDataList=new ArrayList();
			if("1".equals(flag))
				oriDataList=gzbo.getOriDataList(form_file,flag,relationItem,originalDataList);
			else
			{
				oriDataList=gzbo.getOriDataList(form_file,relationItem,originalDataList);
				ArrayList sameDataList=new ArrayList();
				for(int j=0;j<relationItem.length;j++)
				{
					String temp=gzbo.removeBlank(relationItem[j]);
					String[] temps=temp.split("=");
					CommonData data=new CommonData("a"+j,temps[0]);
					sameDataList.add(data);
				}
				CommonData data=new CommonData("acount","个数");
				sameDataList.add(data);
				this.getFormHM().put("sameDataList",sameDataList);
			}
			
			this.getFormHM().put("oriDataList",oriDataList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
