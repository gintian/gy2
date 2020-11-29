package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ResetColumnsTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String optType=(String)hm.get("type");    // 0: 增减横栏目  1：增减子横栏目   2: 增减纵栏目  3：增减子纵栏目
			String[] columnsItemValue=(String[])this.getFormHM().get("columnsItemValue");
			String parentItemId=(String)this.getFormHM().get("parentItemId");
			
			
			String hfactor=(String)this.getFormHM().get("hfactor");
			String s_hfactor=(String)this.getFormHM().get("s_hfactor");
			String vfactor=(String)this.getFormHM().get("vfactor");
			String s_vfactor=(String)this.getFormHM().get("s_vfactor");
			String item=(String)this.getFormHM().get("item");
			String hcontent=(String)this.getFormHM().get("hcontent");
			String vcontent=(String)this.getFormHM().get("vcontent");
			//-----------------------转成英文符号 zhaoxg add 2013-12-31-----------
			 hfactor = PubFunc.keyWord_reback(hfactor);
			 s_hfactor = PubFunc.keyWord_reback(s_hfactor);
			 vfactor = PubFunc.keyWord_reback(vfactor);
			 s_vfactor = PubFunc.keyWord_reback(s_vfactor);
			 item = PubFunc.keyWord_reback(item);
			 hcontent = PubFunc.keyWord_reback(hcontent);
			 vcontent = PubFunc.keyWord_reback(vcontent);
			//--------------zhaoxg end------------------------
			GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn());
			
			
			//0: 增减横栏目  1：增减子横栏目   2: 增减纵栏目  3：增减子纵栏目
			
			String items []={hfactor,s_hfactor,vfactor,s_vfactor};
			
			
			String itemid=items[Integer.parseInt(optType)];
			String codeid=DataDictionary.getFieldItem(itemid).getCodesetid();
			
			if("0".equals(optType)|| "1".equals(optType))
				hcontent=bo.updateColumnContent(optType,hcontent,parentItemId,columnsItemValue,codeid);
			else
				vcontent=bo.updateColumnContent(optType,vcontent,parentItemId,columnsItemValue,codeid);
			this.getFormHM().put("hcontent",hcontent);
			this.getFormHM().put("vcontent",vcontent);
			
			String standardID=(String)this.getFormHM().get("standardID");
			String opt=(String)this.getFormHM().get("opt");
			String pkg_id=(String)this.getFormHM().get("pkg_id");
			bo=new GzStandardItemBo(this.getFrameconn(),hfactor,s_hfactor,hcontent,vfactor,s_vfactor,vcontent,item,standardID,opt,pkg_id);
			bo.init();
			GzStandardItemVo vo=bo.getGzStandardItemVo();
		//	this.getFormHM().put("gzItemList",vo.getGzItemList());
			this.getFormHM().put("gzStandardItemVo",vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
