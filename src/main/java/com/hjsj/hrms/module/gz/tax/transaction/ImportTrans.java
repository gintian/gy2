package com.hjsj.hrms.module.gz.tax.transaction;

import com.hjsj.hrms.module.gz.tax.businessobject.TaxForExcelBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * @author wangjl
 * 所得税明细导入
 * excelheads 数据对应指标
 * relation 可更新指标
 *	else 关联指标
 */
public class ImportTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			// 上传组件 vfs改造 
            String fileid = (String)this.getFormHM().get("fileid");
            
			String flag = (String) this.formHM.get("flag");
			String salaryid = (String) this.formHM.get("salaryid");
			//关联指标
			ArrayList<MorphDynaBean> relationItem1 = (ArrayList<MorphDynaBean>) this.formHM.get("relationItem");
			//更新指标
			ArrayList<MorphDynaBean> oppositeItem1 = (ArrayList<MorphDynaBean>) this.formHM.get("oppositeItem");
			//关联指标
			ArrayList<String> relationItem = new ArrayList<String>();
			//更新指标
			ArrayList<String> oppositeItem = new ArrayList<String>();
			TaxForExcelBo forExc = new TaxForExcelBo(this.frameconn,this.userView);
			ArrayList<LazyDynaBean> dataFiledList = null;
			//上传文件后，解析表头，得到能修改的指标，得到关联指标
			if(relationItem1==null&&oppositeItem1==null){
				if("excelheads".equals(flag)){
					dataFiledList = forExc.getOriginalDataFiledList(fileid);
					ArrayList aimDataList = forExc.getTaxMxItemList();
					this.getFormHM().put("aimDataList", aimDataList);
					if(dataFiledList!=null){
						this.getFormHM().put("dataFiledList", dataFiledList);
					}
				}else if("relation".equals(flag)){
					dataFiledList = forExc.getOriginalDataFiledList(fileid);
					ArrayList<LazyDynaBean> relation = forExc.getRelation(dataFiledList);
					this.getFormHM().put("relationList", relation);
				}else{
					ArrayList aimDataList = forExc.getTaxMxItemList();
					this.getFormHM().put("aimDataList", aimDataList);
				}
			}else{//导入数据
				ArrayList<String> list=new ArrayList<String>();
				String msg="";
				for(MorphDynaBean oppbean:oppositeItem1)
				{
					//判断数据对应指标中目标数据不能重复
					if(list.contains((String)oppbean.get("itemid1"))){
						FieldItem item1=DataDictionary.getFieldItem((String)oppbean.get("itemid1"));
						//不能重复作为目标数据
						throw GeneralExceptionHandler.Handle(new Exception((String)oppbean.get("itemdesc")+ResourceFactory.getProperty("gz_new.gz_accounting.repeatdata")));
					}else{
						list.add((String)oppbean.get("itemid1"));
					}
					
					//只读权限的指标不能修改
					if("1".equalsIgnoreCase(this.userView.analyseFieldPriv((String)oppbean.get("itemid1"))))
					{
						FieldItem item=DataDictionary.getFieldItem((String)oppbean.get("itemid1"));
						msg+= ResourceFactory.getProperty("gz_new.gz_relationItme")+(String)oppbean.get("itemid")+"="+item.getItemdesc()+") 中 "+item.getItemdesc()+ResourceFactory.getProperty("gz_new.gz_onlyRead");
					}else{
						oppositeItem.add(oppbean.get("itemid") + "=" + oppbean.get("itemid1"));
					}
				}
				for(MorphDynaBean bean:relationItem1){
					relationItem.add(bean.get("itemid") + "=" + bean.get("itemid2"));
				}
				/**薪资类别*/
				if(msg.trim().length()==0)
				{
					int rowNums=forExc.importFileDataToTaxMx(relationItem, oppositeItem, fileid);
					this.getFormHM().put("rowNums", rowNums);
					this.getFormHM().put("succeed", true);
				}else{
					throw GeneralExceptionHandler.Handle(new Throwable(msg));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
