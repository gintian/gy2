package com.hjsj.hrms.transaction.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 切换方案时页面互动
* 
* 类名称：GetFaName   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Mar 27, 2014 6:16:21 PM   
* 修改人：zhaoxg   
* 修改时间：Mar 27, 2014 6:16:21 PM   
* 修改备注：   
* @version    
*
 */
public class GetFaName extends IBusiness {

	public void execute() throws GeneralException {
		String faName=(String) this.getFormHM().get("faName");
		GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn(),this.getUserView());
		bo.initXML();
		HashMap map=bo.getFullFaName(faName);
		String item=(String) map.get("item");
		String value=(String) map.get("values");
		ArrayList list=new ArrayList();
		String[] values=value.split(",");
		StringBuffer str_value = new StringBuffer();
		for(int i=0;i<values.length;i++){
			if(values[i].indexOf(":")!=-1){
				str_value.append("`"+values[i]+"~"+values[i].split(":")[1]+"");
				CommonData dataobj = new CommonData(values[i],values[i].split(":")[1]);
				list.add(dataobj);
			}else{
				str_value.append("`"+values[i]+"~"+bo.getItemCode(item, values[i])+"");
				CommonData dataobj = new CommonData(values[i],bo.getItemCode(item, values[i]));
				list.add(dataobj);
			}


		}
		this.getFormHM().put("item", item);
		this.getFormHM().put("list", list);
		if(str_value.length()>0&&str_value.length()<10000){
			this.getFormHM().put("str_value", SafeCode.encode(str_value.substring(1)));			
		}
	}

}
