package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ValidateDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String[] oppositeItem=(String[])this.getFormHM().get("oppositeItem");   //对应指标 
			String[] relationItem=(String[])this.getFormHM().get("relationItem");  //关联指标
			String salaryid=(String)this.getFormHM().get("salaryid");
			
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			FormFile form_file = (FormFile) getFormHM().get("file");
			ArrayList list=new ArrayList();
			ArrayList originalDataList=(ArrayList)this.getFormHM().get("originalDataList");  ////源数据 列信息
			String msg="";
			ArrayList updateDateList=new ArrayList();    
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			for(int i=0;i<oppositeItem.length;i++)
			{
				/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 xiaoyun 2014-9-19 start */
				String[] temps = PubFunc.keyWord_reback(oppositeItem[i]).split("=");
				//String[] temps=oppositeItem[i].split("=");	
				/* 薪资发放：新建/导入：设置对应指标页面，点击【保存对应】、【没对应数据】、设置方案后点击【确定】都保存报错 xiaoyun 2014-9-19 end */
				if(list.contains(temps[1])){
					FieldItem item1=DataDictionary.getFieldItem(temps[1]);
					throw GeneralExceptionHandler.Handle(new Exception(item1.getItemdesc()+"不能重复作为目标数据"));
				}else{
					list.add(temps[1]);
				}
				if("1".equalsIgnoreCase(this.userView.analyseFieldPriv(temps[1])))
				{
					FieldItem item=DataDictionary.getFieldItem(temps[1]);
					msg+="数据对应指标 ("+temps[0]+"="+item.getItemdesc()+") 中 "+item.getItemdesc()+"为只读权限，不能导入数据!";
				}
			}
			
			/**薪资类别*/
			if(msg.length()==0)
			{
				
				Map<String, ArrayList> map=gzbo.validateData(oppositeItem,relationItem,form_file,originalDataList);
				//lis 从map里取得excel数据 updateDateList
				Iterator it = map.entrySet().iterator();
				while (it.hasNext()) {
				   Map.Entry entry = (Map.Entry) it.next();
				   Object key = entry.getKey();
				   Object value = entry.getValue();
				   msg = (String) key;
				   updateDateList = (ArrayList) value;
				 }
			}
			this.getFormHM().put("msg",msg);
			this.getFormHM().put("updateDateList",updateDateList);
			ArrayList relationItemList=gzbo.getArrayList(relationItem);
			ArrayList oppositeItemList=gzbo.getArrayList(oppositeItem);
			this.getFormHM().put("relationItemList",relationItemList);
			this.getFormHM().put("oppositeItemList",oppositeItemList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
