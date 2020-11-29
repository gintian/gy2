/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.SalaryTotalBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:SubmitGzDataTrans</p> 
 *<p>Description:提交薪资数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-27:下午03:50:44</p> 
 *@author cmq
 *@version 4.0
 */
public class SubmitGzDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String filterWhl=(String)this.getFormHM().get("filterWhl"); //分批确认条件
		filterWhl = PubFunc.decrypt(filterWhl);
		String salaryid=(String)this.getFormHM().get("salaryid");
		String isHistory=(String)this.getFormHM().get("isHistory");
		String gz_module=(String)this.getFormHM().get("gz_module");
		String subNoShowUpdateFashion=(String)this.getFormHM().get("subNoShowUpdateFashion");  //1不显示数据操作方式  0:显示数据操作方式
		String isRedo=(String)this.getFormHM().get("isRedo"); //1:重发数据  0:不是
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		gzbo.updateSalarySetDbpres(salaryid);
		/**归档数据集*/
		ArrayList setid=new ArrayList();
		/**提交方式*/
		ArrayList type=new ArrayList();
		String items="";
		String uptypes="";
		
		if(isRedo!=null&& "1".equals(isRedo)) //重发数据
		{
			//20140924  dengcan  重发薪资提交时默认不变的子集提交方式仍为不变
			ArrayList list=gzbo.getSubmitTypeList();
			LazyDynaBean abean=null; 
			HashMap setTypeMap=new HashMap();
			for(int i=0;i<list.size();i++)
			{
				abean=(LazyDynaBean)list.get(i);
				String _setid=((String)abean.get("setid")).trim();
				setTypeMap.put(_setid.toLowerCase(), ((String)abean.get("type")).trim());
			}
			
			
			ArrayList setlist=gzbo.getSetlist();
			for(int i=0;i<setlist.size();i++)
			{
				String _setid=(String)setlist.get(i);
				if(_setid.charAt(0)!='A')
					continue;
				if("A00".equalsIgnoreCase(_setid))
					continue;
				setid.add(_setid);
				if(setTypeMap.get(_setid.toLowerCase())!=null&& "2".equalsIgnoreCase((String)setTypeMap.get(_setid.toLowerCase()))) //20140924  dengcan  重发薪资时默认不变的子集重发提交仍不变
					type.add("2");
				else if(setTypeMap.get(_setid.toLowerCase())!=null&& "1".equalsIgnoreCase((String)setTypeMap.get(_setid.toLowerCase())))
					type.add("3");//新增改为更新(薪资重发时) 解决重发时插入人导致归属次数重复 zhanghua 2018-2-28
//				else if(_setid.equalsIgnoreCase("A01")) /**(0,1,2)=(更新,新增,不变)*/
//					type.add("2");// A01如果设置为更新，则任然更新 zhanghua 
				else
					type.add("0");
			}
			
		}
		else if("0".equals(subNoShowUpdateFashion)) //显示数据操作方式
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
			
			/** 更新指标集 */
			items=(String)this.getFormHM().get("items");
			/** 更新方式 */
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
					sets.append("/"+(String)abean.get("setid"));
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
		
		try
		{
		 
			
		//	filterWhl+=" and ("+gzbo.getGz_tablename()+".sp_flag<>'06' or "+gzbo.getGz_tablename()+".sp_flag is null)";
			
			/**保存数据集提交方式*/
			if("0".equals(subNoShowUpdateFashion)&&(isRedo==null||!"1".equals(isRedo)))//重发不保存提交方式 zhanghua 2018-2-28
				gzbo.saveSubmitType(setid, type,items,uptypes);
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			String isControl=ctrlparam.getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			SalaryTotalBo bo=new SalaryTotalBo(this.getFrameconn(),this.getUserView(),salaryid);
			if("1".equals(bo.getIsControl())&& "1".equals(isControl))
			{
				LazyDynaBean belongTime=gzbo.getSalaryPigeonholeDate();  //归属时间
				SalaryTotalBo abo=new SalaryTotalBo(this.getFrameconn(),this.userView,salaryid);
				
				GzAmountXMLBo gzAmountXMLBo=new GzAmountXMLBo(this.getFrameconn(),1);
				HashMap gzXmlMap=gzAmountXMLBo.getValuesMap();
				String ctrl_by_level=(String)gzXmlMap.get("ctrl_by_level");
				if(ctrl_by_level!=null&& "1".equals(ctrl_by_level))
					abo.collectData((String)belongTime.get("year"));
			}
			/**数据归档*/
			gzbo.setFilterWhl(filterWhl);
			

			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select count(a0100)  from "+gzbo.getGz_tablename()+" where 1=1 "+filterWhl);
			if(this.frowset.next())
			{
				if(this.frowset.getInt(1)==0)
					throw new GeneralException("没有人员不能进行薪资提交!");
			}
			
			
		//	DbNameBo.autoAddZ1_ff(this.getFrameconn(), this.userView, gzbo.getGz_tablename(), salaryid,gzbo.getManager(),true);
			DbNameBo.autoAddZ1_subhistory(this.getFrameconn(), this.userView, gzbo.getGz_tablename(), salaryid,gzbo.getManager(),true,gzbo.getCtrlparam(),gzbo.getGzitemlist(),filterWhl);
			gzbo.submitGzData(setid, type,items,uptypes);
			this.getFormHM().put("gz_module",gz_module);
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		
	}

}
