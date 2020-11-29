package com.hjsj.hrms.utils.components.selectfield.transaction;

import com.hjsj.hrms.module.gz.tax.businessobject.TaxMxBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 项目名称 ：ehr7.x
 * 类名称：FieldItemOptTrans
 * 类描述：已选指标和备选指标页面数据初始化
 * 创建人： lis
 * 创建时间：2015-12-16
 */
public class FieldItemInitTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String imodule = "";//为空时走公共方法；0:薪资类别,1:人事异动,2:所得税管理结构设置 ,9:职称
			String feildSetid=(String)this.getFormHM().get("fieldSetid");
			String priv=(String)this.getFormHM().get("priv");//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
			priv=priv!=null&&priv.trim().length()>0?priv:"1";
			String type = "";
			String path = (String)this.getFormHM().get("path");//path=值为功能号（便于查哪个功能）
			if(this.getFormHM().get("imodule") != null)
				imodule = (String)this.getFormHM().get("imodule");//0薪资模块
			if(this.getFormHM().get("type") != null)
				type = (String)this.getFormHM().get("type");
			String opt=(String)this.getFormHM().get("opt");   // 1: fieldset   2:fielditem
			String fieldSetStr=(String)this.getFormHM().get("fieldSetStr");
			String excludeFieldSetStr=(String)this.getFormHM().get("excludeFieldSetStr");
			String[] fieldSetStrArr = null;//查询子集
			String[] excludeFieldSetStrArr = null;//排除子集
			if(StringUtils.isNotBlank(fieldSetStr)){
				fieldSetStrArr = fieldSetStr.split(",");
			}
			if(StringUtils.isNotBlank(excludeFieldSetStr)){
				excludeFieldSetStrArr = excludeFieldSetStr.split(",");
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList<CommonData> list=new ArrayList<CommonData>();
			ArrayList<CommonData> templist=new ArrayList<CommonData>();
			if("0".equals(imodule)){
				if("1".equals(opt))//获取子集列表
				{
					if(fieldSetStrArr != null)
						for(String str:fieldSetStrArr){
							this.frowset=dao.search("select fieldsetid,customdesc from fieldset where (fieldsetid like '"+str.toUpperCase()+"%' or fieldsetid like '"+str.toUpperCase()+"') and useflag='1' order by displayorder ");
							while(this.frowset.next())
							{
								if(!"0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
									list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
							}
						}
					
					StringBuffer sqlBuf = new StringBuffer("select fieldsetid,customdesc from fieldset where 1=1 ");
					if(excludeFieldSetStrArr != null){
						for(String str:excludeFieldSetStrArr){
							sqlBuf.append(" and (fieldsetid not like '"+ str.toUpperCase() +"%' or fieldsetid like '"+str.toUpperCase()+"')");
						}
						sqlBuf.append(" and useflag='1' order by displayorder ");
						this.frowset=dao.search(sqlBuf.toString());
						while(this.frowset.next())
						{
							if(!"0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
								list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
						}
					}
					
					if("1".equals(type)){
						if("2306514".equals(path)){
							
						}
						for(int i=0;i<list.size();i++)
						{
							CommonData data = list.get(i);
							if("A00".equalsIgnoreCase(data.getDataValue())|| "B00".equalsIgnoreCase(data.getDataValue())
									|| "K00".equalsIgnoreCase(data.getDataValue())|| "Y00".equalsIgnoreCase(data.getDataValue())|| "W00".equalsIgnoreCase(data.getDataValue())
									|| "V00".equalsIgnoreCase(data.getDataValue())|| "H00".equalsIgnoreCase(data.getDataValue()))
								continue;
							CommonData dataobj = new CommonData(data.getDataValue(), data.getDataName());
							templist.add(dataobj);
						}
						list = templist;
					}
				}
				else if("2".equals(opt))//根据子集id获得子集中的指标
				{
					String salaryid=(String)this.getFormHM().get("salaryid");
					salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
					ArrayList datalist = new ArrayList();
					datalist.add(feildSetid);
					StringBuffer sql=new StringBuffer("select itemid,itemdesc,itemtype from fielditem where fieldsetid=? ");
					if("1".equals(type)){
						sql.append(" and useflag='1'");
						if("a01".equalsIgnoreCase(feildSetid)|| "b01".equalsIgnoreCase(feildSetid))//2016-11-24 zhanghua 添加固定列 单位名称 24524
						{
							list.add(new CommonData("b0110","单位名称"));
						}
						
					}else{
						sql.append(" and itemid not in (select itemid from salaryset where salaryid=?) and useflag='1'");
						datalist.add(salaryid);
					}
					//加入停发标识a01z0 zhanghua 2017-7-27
					sql.append(" and ( itemid<>'"+feildSetid+"Z0' or upper(itemid)='A01Z0') and itemid<>'"+feildSetid+"Z1'  order by displayid  ");
					this.frowset=dao.search(sql.toString(),datalist);
					FieldItem item =DataDictionary.getFieldItem("e01a1");
					boolean addflag=this.hasE01a1Field(salaryid);
					boolean addflag2=true;
					while(this.frowset.next())
					{
						if(DataDictionary.getFieldItem(this.frowset.getString(1).toLowerCase())!=null)
						{
							if("1".equals(type)){
							    if("M".equals(this.frowset.getString("itemtype")))
							    	 continue;
							}
							if("e01a1".equalsIgnoreCase(this.frowset.getString(1)))
								addflag2=false;
							if(!"0".equals(this.userView.analyseFieldPriv(this.frowset.getString(1))))
								list.add(new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc").replaceAll("\r\n", "")));
						}
					}
					
					if(!"1".equals(type)){
						if("a01".equalsIgnoreCase(feildSetid)&&addflag2)
						{
							if(addflag&&item!=null)
							{
								if(!"0".equals(this.userView.analyseFieldPriv("E01A1")))
									list.add(0,new CommonData(item.getItemid(),item.getItemdesc().replaceAll("\r\n", "")));
							}
						}
					}
					if("H01".equalsIgnoreCase(feildSetid)) {//基准岗位添加基准岗位名称
						list.add(0,new CommonData("H0100","基准岗位名称"));
					}
				}else if("3".equals(opt)){
					//解析简单条件表达式
					String expr=(String)this.getFormHM().get("expr");
					expr = SafeCode.decode(expr);
					String[] exprArry = expr.split("\\|");
					ArrayList<CommonData> selectedlist = this.reParseExpression(exprArry[0], exprArry[1]);
					this.getFormHM().put("selectedlist",selectedlist);
				}
			}else if("1".equals(imodule)){
				if("1".equals(opt))//获取子集列表
				{
					  String info_type = (String)this.getFormHM().get("info_type");//1是人员模板，2是单位模板，3是岗位模板
					  ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);

				      if("2306514".equals(path)){
				    	  if("1".equals(info_type)){
				    		  ArrayList  fieldunitlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
				    		  ArrayList  fieldposlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
				    		  for(int i=0;i<fieldunitlist.size();i++)
				    		  {
				    			  fieldsetlist.add(fieldunitlist.get(i));
				    		  }
				    		  for(int i=0;i<fieldposlist.size();i++)
				    		  {
				    			  fieldsetlist.add(fieldposlist.get(i));
				    		  }
				    	  }else if("2".equals(info_type)){
				    		  fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
				    	  }else{
				    		  fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
				    	  }
				      }
				      for(int i=0;i<fieldsetlist.size();i++)
					    {
					      FieldSet fieldset = (FieldSet)fieldsetlist.get(i);
					      if("1".equals(priv)&& "0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
					    	  continue;
					      if("A00".equalsIgnoreCase(fieldset.getFieldsetid())|| "B00".equalsIgnoreCase(fieldset.getFieldsetid())
					    		  || "K00".equalsIgnoreCase(fieldset.getFieldsetid())|| "Y00".equalsIgnoreCase(fieldset.getFieldsetid())|| "W00".equalsIgnoreCase(fieldset.getFieldsetid())|| "V00".equalsIgnoreCase(fieldset.getFieldsetid())|| "H00".equalsIgnoreCase(fieldset.getFieldsetid()))
					    	  continue;
					     /* if(infor.equals("1"))
					      {
					    	  if(fieldset.getFieldsetid().equals("B01")||fieldset.getFieldsetid().equals("K01"))
					    		  continue;
					      }*/
					      
					      CommonData dataobj = new CommonData(fieldset.getFieldsetid(), /*"(" + fieldset.getFieldsetid() + ")"+*/ fieldset.getCustomdesc()/*getFieldsetdesc()*/);
				          list.add(dataobj);
					    }
					
				}else if("2".equals(opt))//获取子集列表
				{
					
					ArrayList fielditemlist=DataDictionary.getFieldList(feildSetid,Constant.USED_FIELD_SET);
					// 会报错：列名 'B0110' 无效。也不需要加这个固定指标(参考CS)。
					/*if(setname.equalsIgnoreCase("k01"))
					{
						list.add(new CommonData("b0110","单位"));
					}
					*/
				    if(fielditemlist!=null)
				    {
						for(int i=0;i<fielditemlist.size();i++)
					    {
					      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
					      if("M".equals(fielditem.getItemtype()))
					    	continue;
					      if("1".equals(priv)&& "0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
					        continue;
					      CommonData dataobj = new CommonData();
					      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
					      
					      list.add(dataobj);
					    }
				    }
				}else if("3".equals(opt)){
					//解析简单条件表达式
					String expr=(String)this.getFormHM().get("expr");
					expr = SafeCode.decode(expr);
					String[] exprArry = expr.split("\\|");
					ArrayList<CommonData> selectedlist = this.reParseExpression(exprArry[0], exprArry[1]);
					this.getFormHM().put("selectedlist",selectedlist);
				}
			}else if("2".equals(imodule)){
				TaxMxBo taxbo = new TaxMxBo(this.frameconn, this.userView);
				if("1".equals(opt))
					list = taxbo.getGzMxType(userView,"dataValue","dataName");
				else 
					if("2".equals(opt))
					list = taxbo.getGzMxprolist(feildSetid);
			}else if("9".equals(imodule)){
				if("1".equals(opt))//获取子集列表
				{
					  String info_type = (String)this.getFormHM().get("info_type");//1是人员模板，2是单位模板，3是岗位模板
					  ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);

				      if("2306514".equals(path)){
				    	  if("1".equals(info_type)){
				    		  ArrayList  fieldunitlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
				    		  ArrayList  fieldposlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
				    		  for(int i=0;i<fieldunitlist.size();i++)
				    		  {
				    			  fieldsetlist.add(fieldunitlist.get(i));
				    		  }
				    		  for(int i=0;i<fieldposlist.size();i++)
				    		  {
				    			  fieldsetlist.add(fieldposlist.get(i));
				    		  }
				    	  }else if("2".equals(info_type)){
				    		  fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
				    	  }else{
				    		  fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
				    	  }
				      }
				      for(int i=0;i<fieldsetlist.size();i++)
					    {
					      FieldSet fieldset = (FieldSet)fieldsetlist.get(i);
					      if("1".equals(priv)&& "0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
					    	  continue;
					      if("A00".equalsIgnoreCase(fieldset.getFieldsetid())|| "B00".equalsIgnoreCase(fieldset.getFieldsetid())
					    		  || "K00".equalsIgnoreCase(fieldset.getFieldsetid())|| "Y00".equalsIgnoreCase(fieldset.getFieldsetid())|| "W00".equalsIgnoreCase(fieldset.getFieldsetid())|| "V00".equalsIgnoreCase(fieldset.getFieldsetid())|| "H00".equalsIgnoreCase(fieldset.getFieldsetid()))
					    	  continue;
					     /* if(infor.equals("1"))
					      {
					    	  if(fieldset.getFieldsetid().equals("B01")||fieldset.getFieldsetid().equals("K01"))
					    		  continue;
					      }*/
					      
					      CommonData dataobj = new CommonData(fieldset.getFieldsetid(), /*"(" + fieldset.getFieldsetid() + ")"+*/ fieldset.getCustomdesc()/*getFieldsetdesc()*/);
				          list.add(dataobj);
					    }
					
				}else if("2".equals(opt))//获取子集列表
				{
					
					ArrayList fielditemlist=DataDictionary.getFieldList(feildSetid,Constant.USED_FIELD_SET);
					// 会报错：列名 'B0110' 无效。也不需要加这个固定指标(参考CS)。
					/*if(setname.equalsIgnoreCase("k01"))
					{
						list.add(new CommonData("b0110","单位"));
					}
					*/
				    if(fielditemlist!=null)
				    {
						for(int i=0;i<fielditemlist.size();i++)
					    {
					      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
					      if("M".equals(fielditem.getItemtype()))
					    	continue;
					      if("1".equals(priv)&& "0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
					        continue;
					      CommonData dataobj = new CommonData();
					      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
					      
					      list.add(dataobj);
					    }
				    }
				}else if("3".equals(opt)){
					//解析简单条件表达式
					String expr=(String)this.getFormHM().get("expr");
					expr = SafeCode.decode(expr);
					String[] exprArry = expr.split("\\|");
					ArrayList<CommonData> selectedlist = this.reParseExpression(exprArry[0], exprArry[1]);
					this.getFormHM().put("selectedlist",selectedlist);
				}
			}else{
				//imodule为空则是公共查询方案
				if("1".equals(opt))//获取子集列表
				{
					  String info_type = (String)this.getFormHM().get("info_type");//1是人员模板，2是单位模板，3是岗位模板  6 基准岗位
					  ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.USED_FIELD_SET);

				      if("2306514".equals(path)){
				    	  if("1".equals(info_type)){
				    		  ArrayList  fieldunitlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
				    		  ArrayList  fieldposlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
				    		  for(int i=0;i<fieldunitlist.size();i++)
				    		  {
				    			  fieldsetlist.add(fieldunitlist.get(i));
				    		  }
				    		  for(int i=0;i<fieldposlist.size();i++)
				    		  {
				    			  fieldsetlist.add(fieldposlist.get(i));
				    		  }
				    	  }else if("2".equals(info_type)){
				    		  fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
				    	  }else if("3".equals(info_type)){
				    		  fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);	
				    	  }else if("6".equals(info_type)) {
				    		  if(fieldSetStrArr != null)
									for(String str:fieldSetStrArr){
										this.frowset=dao.search("select fieldsetid,customdesc from fieldset where (fieldsetid like '"+str.toUpperCase()+"%' or fieldsetid like '"+str.toUpperCase()+"') and useflag='1' order by displayorder ");
										while(this.frowset.next())
										{
											if(!"0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
												list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
										}
									}
								
								StringBuffer sqlBuf = new StringBuffer("select fieldsetid,customdesc from fieldset where 1=1 ");
								if(excludeFieldSetStrArr != null){
									for(String str:excludeFieldSetStrArr){
										sqlBuf.append(" and (fieldsetid not like '"+ str.toUpperCase() +"%' or fieldsetid like '"+str.toUpperCase()+"')");
									}
									sqlBuf.append(" and useflag='1' order by displayorder ");
									this.frowset=dao.search(sqlBuf.toString());
									while(this.frowset.next())
									{
										if(!"0".equals(this.userView.analyseTablePriv(this.frowset.getString(1))))
											list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
									}
								}
				    	  }
				      }
				      if(!"6".equals(info_type)) {
				    	  for(int i=0;i<fieldsetlist.size();i++)
				    	  {
				    		  FieldSet fieldset = (FieldSet)fieldsetlist.get(i);
				    		  if("1".equals(priv)&& "0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
				    			  continue;
				    		  if("A00".equalsIgnoreCase(fieldset.getFieldsetid())|| "B00".equalsIgnoreCase(fieldset.getFieldsetid())
				    				  || "K00".equalsIgnoreCase(fieldset.getFieldsetid())|| "Y00".equalsIgnoreCase(fieldset.getFieldsetid())|| "W00".equalsIgnoreCase(fieldset.getFieldsetid())|| "V00".equalsIgnoreCase(fieldset.getFieldsetid())|| "H00".equalsIgnoreCase(fieldset.getFieldsetid()))
				    			  continue;
				    		  
				    		  CommonData dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc());
				    		  list.add(dataobj);
				    	  }
				      }
					
				}else if("2".equals(opt))//获取子集列表
				{
					String excludeFields = null;//第一次進來
					String fieldNames = null;
					if(this.formHM.containsKey("excludeFields")) {
						excludeFields = (String)this.formHM.get("excludeFields");
					}
					ArrayList fielditemlist=DataDictionary.getFieldList(feildSetid,Constant.USED_FIELD_SET);
				    if(fielditemlist!=null)
				    {
						for(int i=0;i<fielditemlist.size();i++)
					    {
					      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
					      if("M".equals(fielditem.getItemtype())) {
					    	  if (null!=excludeFields||null!=fieldNames) {
					    		  list.add(new CommonData(fielditem.getItemid(),fielditem.getItemdesc()));
					    		  continue;
							  }else {
								  continue;
							  }
					      }
					      if("1".equals(priv)&& "0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
					        continue;
					      CommonData dataobj = new CommonData();
					      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
					      
					      list.add(dataobj);
					    }
				    }
				    //分布式上报保护指标条件，排除左侧相同的指标
					if(this.formHM.containsKey("fieldNames")) {
						fieldNames = (String)this.formHM.get("fieldNames");
					}
					if (null!=fieldNames) {
						if (!"".equals(fieldNames)) {
							String [] fieldArray = fieldNames.split("/");
							for(int i=0;i<fieldArray.length;i++) {
								String itemid = fieldArray[i];
								for(int j=list.size()-1;j>=0;j--) {
						    		CommonData commonData =list.get(j);
						    		String dataValue=commonData.getDataValue();
						    		if (dataValue.equalsIgnoreCase(itemid)) {
										list.remove(j);
									}
						    	}
							}
						}
					}else {
						excludeFields = null;
						if(this.formHM.containsKey("excludeFields")) {
							excludeFields = (String)this.formHM.get("excludeFields");
						}
						if (null!=excludeFields) {
							excludeFields=excludeFields.substring(1,excludeFields.length());
							String [] fieldArray = excludeFields.split("/");
							for(int i=0;i<fieldArray.length;i++) {
								String itemid = fieldArray[i];
								for(int j=list.size()-1;j>=0;j--) {
						    		CommonData commonData =list.get(j);
						    		String dataValue=commonData.getDataValue();
						    		if (dataValue.equalsIgnoreCase(itemid)) {
										list.remove(j);
									}
						    	}
							}
						}
					}
				}else if("3".equals(opt)){
					//解析简单条件表达式
					String expr=(String)this.getFormHM().get("expr");
					expr = SafeCode.decode(expr);
					String[] exprArry = expr.split("\\|");
					ArrayList<CommonData> selectedlist = this.reParseExpression(exprArry[0], exprArry[1]);
					this.getFormHM().put("selectedlist",selectedlist);
				}
			
			}
			this.getFormHM().put("list",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	/**
	 * @author lis
	 * @Description: 判断是否有e01a1
	 * @date 2015-10-21
	 * @param salaryid
	 * @return
	 * @throws GeneralException
	 */
	private boolean hasE01a1Field(String salaryid) throws GeneralException
	{
		boolean flag=true;
		if(StringUtils.isEmpty(salaryid)){
			return flag;
		}
		RowSet rs=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			rs=dao.search("select * from salaryset where salaryid="+salaryid+" and UPPER(itemid)='E01A1'");
			while(rs.next())
			{
				flag=false;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return flag;
	}

	/**
	 * @author lis
	 * @Description: 解析简单条件表达式
	 * @date 2015-12-24
	 * @param condStr 简单条件表达式
	 * @param cexpr 逻辑关系
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<CommonData> reParseExpression(String cexpr,String condStr)throws GeneralException
	{
		ArrayList<CommonData> selectedlist=new ArrayList();
		try
		{
			ArrayList<FieldItem> list=null;
			FactorList factorlist=new FactorList(cexpr,condStr,"");
			list=factorlist.getAllFieldList();
			if(!(list==null||list.size()==0))
			{
				for(int i=0;i<list.size();i++)
				{
					 FieldItem fielditem = list.get(i);
				     CommonData dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemdesc());;
				     selectedlist.add(dataobj);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return selectedlist;
	}
}
