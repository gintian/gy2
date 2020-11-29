package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 * @author xujian
 *Mar 23, 2010
 */
public class InitTargetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String unit=(String)this.getFormHM().get("unit");
		ArrayList unittargetlist = new ArrayList();
		//unittargetlist.add(new CommonData("",""));
		ArrayList fielditemlist = DataDictionary.getFieldList(unit, Constant.USED_FIELD_SET);
		if(fielditemlist!=null){
			for(int i=0;i<fielditemlist.size();i++){
				FieldItem fielditem = (FieldItem)fielditemlist.get(i);
				if("N".equalsIgnoreCase(fielditem.getItemtype()))
					unittargetlist.add(new CommonData(fielditem.getItemid(),fielditem.getItemdesc()));
			}
		}
		ArrayList seansonaldatalist = new ArrayList();//变化周期
		//seansonaldatalist.add(new CommonData("",""));
		if(!"".equalsIgnoreCase(unit)){
			FieldSet fs = DataDictionary.getFieldSetVo(unit);
			if(fs!=null){
				if("1".equalsIgnoreCase(fs.getChangeflag())){
					seansonaldatalist.add(new CommonData("1",ResourceFactory.getProperty("stat.info.setup.archive_type.month")));
				}else if("2".equalsIgnoreCase(fs.getChangeflag())){
					seansonaldatalist.add(new CommonData("2",ResourceFactory.getProperty("stat.info.setup.archive_type.season")));
					seansonaldatalist.add(new CommonData("3",ResourceFactory.getProperty("stat.info.setup.archive_type.half")));
					seansonaldatalist.add(new CommonData("4",ResourceFactory.getProperty("stat.info.setup.archive_type.year")));
				}
			}
		}
		this.getFormHM().put("unittargetlist", unittargetlist);
		this.getFormHM().put("seansonaldatalist", seansonaldatalist);
	}

}
