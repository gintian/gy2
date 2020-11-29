package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：GetUpdateGzItemFashionTrans 
 * 类描述： 获取提交页面高级按钮的数据
 * 创建人：zhaoxg
 * 创建时间：Oct 12, 2015 2:33:08 PM
 * 修改人：zhaoxg
 * 修改时间：Oct 12, 2015 2:33:08 PM
 * 修改备注： 
 * @version
 */
public class GetUpdateGzItemFashionTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String sets=((String)this.getFormHM().get("sets")).substring(1);
        ArrayList updateObj=(ArrayList)this.getFormHM().get("updateObj");
		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
		safeBo.isSalarySetResource(salaryid,null);
		HashMap<String,String> updateMap=new HashMap<String, String>();
        for(int i=0;i<updateObj.size();i++){
            MorphDynaBean bean = (MorphDynaBean)updateObj.get(i);
            String itemid=(String)bean.get("itemid");
            updateMap.put(itemid,(String)bean.get("flag"));
        }
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		ArrayList list=gzbo.getUpdateItemList(sets.split("/"),salaryid);

		for(int i=0;i<list.size()&&updateMap.size()!=0;i++){
		    LazyDynaBean bean=(LazyDynaBean)list.get(i);
		    String itemid=(String)bean.get("itemid");
		    if(updateMap.containsKey(itemid))
		        bean.set("flag",updateMap.get(itemid));
        }


		this.getFormHM().put("data",list);
	}

}
