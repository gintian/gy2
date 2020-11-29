package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.SalaryTotalBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class Submit_SpGzDataTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			
			String salaryid=(String)this.getFormHM().get("salaryid");
			String bosdate=(String)this.getFormHM().get("bosdate");  //业务日期(发放日期)
			String count=(String)this.getFormHM().get("count");		 //发放次数
			String gz_module=(String)this.getFormHM().get("gz_module");
			String filterWhl=(String)this.getFormHM().get("filterWhl");
			filterWhl=PubFunc.decrypt(filterWhl);
			String gzSpCollect = (String) this.getFormHM().get("gzSpCollect");//薪资汇总审批
			String collectPoint = (String) this.getFormHM().get("collectPoint");//薪资汇总审批
			String selectID=(String)this.getFormHM().get("selectID");
			if(selectID!=null)
				selectID=selectID.replaceAll("＃", "#").replaceAll("／", "/");
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String collectSpSql = "";
			if(gzSpCollect!=null&& "1".equals(gzSpCollect)&&!"sum".equalsIgnoreCase(selectID)){
				collectSpSql = gzbo.getCollectSPPriv(bosdate, count, selectID, collectPoint);
			}
			filterWhl+=collectSpSql;
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			String subNoShowUpdateFashion=(String)this.getFormHM().get("subNoShowUpdateFashion");
			//归档数据集
			ArrayList setid=new ArrayList();
			// 提交方式
			ArrayList type=new ArrayList();
			String items="";
			String uptypes="";
			if("0".equals(subNoShowUpdateFashion)) //显示数据操作方式
			{
				if(this.getFormHM().get("setid")!=null)
				{
					Object object=this.getFormHM().get("setid");
					if(object  instanceof ArrayList)
						setid=(ArrayList)this.getFormHM().get("setid");	
					else
						setid.add(String.valueOf(object));
				}
				
				if(this.getFormHM().get("type")!=null)
				{
					Object object=this.getFormHM().get("type");
					if(object  instanceof ArrayList)
						type=(ArrayList)this.getFormHM().get("type");
					else
						type.add(String.valueOf(object));
				}
				
				// 更新指标集 
				items=(String)this.getFormHM().get("items");
				// 更新方式 
				uptypes=(String)this.getFormHM().get("uptypes");
			}
			else
			{
				ArrayList list=gzbo.getSubmitTypeList();
				LazyDynaBean abean=null;
				StringBuffer sets=new StringBuffer("");
				for(int i=0;i<list.size();i++)
				{
					abean=(LazyDynaBean)list.get(i);
					setid.add((String)abean.get("setid"));
					type.add((String)abean.get("type"));
					if("0".equals((String)abean.get("type")))
						sets.append(""+(String)abean.get("setid"));
				}
				if(sets.length()>0)
				{
					ArrayList gzItemList=gzbo.getUpdateItemList(sets.toString().split("/"));
					for(int i=0;i<gzItemList.size();i++)
					{
						abean=(LazyDynaBean)gzItemList.get(i);
						String itemid=(String)abean.get("itemid");
						String flag=(String)abean.get("flag");
						items+="/"+itemid;
						uptypes+="/"+flag;
					}
				}
				
			}
			
			
				//保存数据集提交方式
			if("0".equals(subNoShowUpdateFashion))
				gzbo.saveSubmitType(setid, type,items,uptypes);
			
		
			
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),salaryid);
			if("1".equals(bo.getIsControl())&& "1".equals(isControl))
			{
			//	LazyDynaBean belongTime=gzbo.getSalaryPigeonholeDate();  //归属时间
				String[] temps=bosdate.split("\\.");
				String _year=temps[0];
				SalaryTotalBo abo=new SalaryTotalBo(this.getFrameconn(),this.userView,salaryid);
				
				GzAmountXMLBo gzAmountXMLBo=new GzAmountXMLBo(this.getFrameconn(),1);
				HashMap gzXmlMap=gzAmountXMLBo.getValuesMap();
				String ctrl_by_level=(String)gzXmlMap.get("ctrl_by_level");
				if("1".equals(ctrl_by_level))
				{
					//abo.collectData((String)belongTime.get("year"));
					abo.collectData(_year);
				}
			}
			
			
			//数据归档
			gzbo.setSp_filterWhl(PubFunc.keyWord_reback(filterWhl));
			gzbo.submitGzDataFromHistory(setid, type,items,uptypes,bosdate,count);
			this.getFormHM().put("gz_module",gz_module);
			
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
