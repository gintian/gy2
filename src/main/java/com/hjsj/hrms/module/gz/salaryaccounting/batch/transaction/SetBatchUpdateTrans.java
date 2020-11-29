package com.hjsj.hrms.module.gz.salaryaccounting.batch.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.batch.businessobject.BatchUpdateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SetBatchUpdateTrans 
 * 类描述：设置批量更新公式
 * 创建人：sunming
 * 创建时间：2015-7-21
 * @version
 */
public class SetBatchUpdateTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");	
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String gz_module=(String) this.getFormHM().get("imodule");
		gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
		
		String codeType=(String) this.getFormHM().get("codeType");//codeType为1 是获取代码项
		String codeItemId=(String) this.getFormHM().get("codeItemId");
		
		try
		{
			if(codeType==null|| "".equals(codeType)){
				ArrayList itemlist=new ArrayList();	
				ArrayList ref_itemlist=new ArrayList();
				if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
					throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
				/**薪资类别*/
				SalaryTemplateBo bo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
				String manager=bo.getManager();
				ArrayList templist=bo.getSalaryItemList("",salaryid,1);
				
				
				HashMap map=new HashMap();
				if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				{
					ArrayList formulaList=bo.getFormulaList(-1,salaryid,null);
					for(int i=0;i<formulaList.size();i++)
					{
						  DynaBean dbean=(LazyDynaBean)formulaList.get(i);
						  String itemname=(String)dbean.get("itemname");
						  map.put(itemname.toLowerCase(),"1");
					}
				}
				
				
				for(int i=0;i<templist.size();i++)
				{
					LazyDynaBean dynabean=(LazyDynaBean)templist.get(i);
					String flag=(String)dynabean.get("initflag");
					String itemid=(String)dynabean.get("itemid");
					
					//人员编号和人员序号不要显示  lis 2015-11-11 i am a bachelor
					if("A0100".equals(itemid.toUpperCase()) || "A0000".equals(itemid.toUpperCase()))
						continue;
					
					if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
					{
						if(map.get(itemid.toLowerCase())!=null)
							continue;
					}
					
					
					LazyDynaBean vo=new LazyDynaBean();
					vo.set("dataName",dynabean.get("itemdesc"));
					vo.set("dataValue",itemid);
					vo.set("codesetid",dynabean.get("codesetid"));

					ref_itemlist.add(vo);
					/**系统项*/
					if("3".equalsIgnoreCase(flag)&&(!("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))))
						continue;
					
					if(manager.length()==0&&!this.userView.isSuper_admin())
					{
						if("3".equalsIgnoreCase(flag)&&(("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))))
							itemlist.add(vo);
						else if(!"3".equalsIgnoreCase(flag)&& "2".equals(this.userView.analyseFieldPriv(itemid)))
							itemlist.add(vo);
					}
					else if(manager.equalsIgnoreCase(this.userView.getUserName())||this.userView.isSuper_admin())
					{
						if(this.userView.isSuper_admin())
							itemlist.add(vo);
						else
						{
							if("3".equalsIgnoreCase(flag)&&(("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))))
								itemlist.add(vo);
							else if(!"3".equalsIgnoreCase(flag)&& "2".equals(this.userView.analyseFieldPriv(itemid)))
								itemlist.add(vo);
						}
					}
					else
					{
						if("3".equalsIgnoreCase(flag)&&(("A00Z0".equalsIgnoreCase(itemid)|| "A00Z1".equalsIgnoreCase(itemid))))
							itemlist.add(vo);
						else if(!"3".equalsIgnoreCase(flag)&& "2".equals(this.userView.analyseFieldPriv(itemid)))
							itemlist.add(vo);
					}
					
					
				}
				this.getFormHM().put("data", itemlist);	
				this.getFormHM().put("data2", ref_itemlist);	
			}
			else if("1".equals(codeType)){//获取代码项
				BatchUpdateBo batchupdate=new BatchUpdateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
				this.getFormHM().put("data", batchupdate.getcodeItemList(codeItemId));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
