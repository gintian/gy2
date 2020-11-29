/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 *<p>Title:SearchGzSpTableDataTrans</p> 
 *<p>Description:薪资审批处理交易</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-21:下午01:20:22</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchGzSpTableDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		    String gz_module=(String)this.getFormHM().get("gz_module");
			String salaryid=(String)this.getFormHM().get("salaryid");
			String a_code=(String)this.getFormHM().get("a_code");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			/**人员过滤条件编号，全部为all*/
			String swhere="";
			String condid=(String)this.getFormHM().get("cond_id_str");
			if(condid==null|| "".equalsIgnoreCase(condid))
				condid=(String)this.getFormHM().get("condid");	
			if(condid==null|| "".equalsIgnoreCase(condid))
				condid="all";
			/**项目过滤条件号，全部为all*/
			String itemid=(String)this.getFormHM().get("itemid");
			String itemid1=(String)hm.get("itemid1");
			/**解决当选择项目过滤，进行导入操作，导入或返回后项目过滤为全部问题lizhenwei add*/
			if(hm.get("import")!=null)
			{
				itemid=(String)(hm.get("import"));
				hm.remove("import");
			}
			/**业务日期和业务次数*/
			String bosdate=(String)this.getFormHM().get("bosdate");
			String count=(String)this.getFormHM().get("count");
			if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
			{
				throw new GeneralException(ResourceFactory.getProperty("salaryid not defined"));
			}
			//如果用户没有当前薪资类别的资源权限   20140926  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,gz_module);
			ContentDAO dao = new ContentDAO(this.getFrameconn());		
			//从待办进来，默认为报批状态 2013-11-27  dengc  
			if(hm.get("fromModel")!=null&& "wdxx".equals((String)hm.get("fromModel")))
			{ 
				bosdate=(String)hm.get("a00z2");
				count=(String)hm.get("a00z3");
			 	hm.remove("fromModel");
			 	hm.remove("a00z2");
			 	hm.remove("a00z3"); 
			}
			this.getFormHM().put("salaryid",salaryid);
			
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			DbWizard dbw=new DbWizard(this.frameconn);
			if(gzbo.getManager()!=null&&gzbo.getManager().trim().length()>0&&!dbw.isExistField("salaryhistory", "sp_flag2",false))//如果从不共享设置成共享，没有则加上sp_flag2 防止报错 zhaoxg add 2016-12-21
			{
				Table table=new Table("salaryhistory");
				Field field=new Field("sp_flag2","sp_flag2");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table.addField(field);
				dbw.addColumns(table);	
			}
			if((gzbo.isApprove()||(gzbo.getManager()!=null&&gzbo.getManager().trim().length()>0))&&!dbw.isExistField("salaryhistory", "appprocess",false)){//需要审批且无appprocess字段，则新增进去，防止报错  zhaoxg add 2016-9-13
				Table table=new Table("salaryhistory");
				Field field=new Field("appprocess","appprocess");
				field.setDatatype(DataType.CLOB);
				table.addField(field);
				dbw.addColumns(table);	
			}
//			if (!"soufang".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){//20150603  基于效率问题,此处同步作废
//			    gzbo.synchronismSalarySet(); 
//			}
			gzbo.SalarySet();
		//	gzbo.syncGzTableStruct();
			//因为新建的项目过滤条件不起作用，所以将下面三行行代码移动至此，wangrd 2013-12-04
            String filterid=gzbo.getFiltersIds(salaryid);
            ArrayList itemfilterlist=gzbo.getItemFilterList(filterid);
            this.getFormHM().put("itemlist", itemfilterlist);
            if("default".equals(itemid1)){
            	hm.remove("itemid1");
            	itemid="default";
            }
            if ("default".equals(itemid)){
                itemid =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"default_filterid");
                if ((","+filterid+",").indexOf(","+itemid+",")<0){//此过滤项目已删除
                    itemid=""; 
                }
                String _str=gzbo.getFiterItem();
                if ((","+_str+",").indexOf(","+itemid+",")<0){//此过滤项目已改成私有
                    itemid=""; 
                }
            }
            if(itemid==null|| "".equalsIgnoreCase(itemid))
                itemid="all";
            ArrayList fieldlist=gzbo.getFieldlist();
            ArrayList salary_fieldlist=(ArrayList)fieldlist.clone();
	
			String pro_field = (String)this.getFormHM().get("proright_str");
			this.getFormHM().put("proright_str",pro_field);
			if(!(pro_field==null || "".equalsIgnoreCase(pro_field)))
			{
				fieldlist=gzbo.filterItemsList(fieldlist,pro_field);
			}else if("new".equalsIgnoreCase(itemid) && "".equalsIgnoreCase(pro_field)){
				fieldlist=gzbo.getFieldlist();
			}
			else {
				if(!"all".equalsIgnoreCase(itemid))
					fieldlist=gzbo.filterItemList(fieldlist,itemid);	
			}
			//xieguiquan  2010-08-25
			RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
			String _str=",SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,A00Z0,A00Z1,NBASE,";
			if(vo!=null&&vo.getString("lprogram")!=null&&vo.getString("lprogram").indexOf("hidden_items")!=-1
				&&vo.getString("lprogram").indexOf("hidden_item")!=-1){
			//	System.out.println(vo.getString("lprogram"));
		    		Document doc = PubFunc.generateDom(vo.getString("lprogram"));
		    		Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items");
					if(hidden_items!=null){
						List list = hidden_items.getChildren();
						if(list.size()>0){
							boolean tempflag=false;//false：走自己设置的隐藏指标  true：走后台设置的显示指标
							boolean _flag=false;//是否有自己设置的显示/隐藏指标，无论是否是空（兼容薪资发放设置成全显示的情况），zhaoxg 2013-11-27
							String tempstr="";
							String _tempstr="";
							for(int i =0;i<list.size();i++){
								Element temp = (Element)list.get(i);
								if(temp.getAttributeValue("user_name")!=null&&temp.getAttributeValue("user_name").equalsIgnoreCase(this.userView.getUserName()))
								{
									tempstr = temp.getText();//隐藏指标
									_flag=true;
									break;
								}else if(temp.getAttributeValue("user_name")==null|| "".equals(temp.getAttributeValue("user_name"))){
									_tempstr = temp.getText();//显示指标
								}
							}

							String str = "";
							if(_flag){//tempstr.length()>0
								str=tempstr;
							}else{
								str=_tempstr;
								tempflag=true;
							}
							if(str.length()>0&&!tempflag){
							str = ","+str+",";
							ArrayList alist = new ArrayList();
							for(int j =0;j<fieldlist.size();j++){
								Field field = (Field)fieldlist.get(j);
								Field cfield = (Field)field.clone();
								if(str.indexOf(","+field.getName()+",")!=-1){
									cfield.setVisible(false);
									
								}else{
									if("a01z0".equalsIgnoreCase(field.getName())){//初试化隐藏
										if(gzbo.getCtrlparam()!=null)
										{
											String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
											if(a01z0Flag==null|| "0".equals(a01z0Flag))
											{

											}
											else
												cfield.setVisible(true);
										}
										else
											cfield.setVisible(true);
									}
									else if("add_flag".equalsIgnoreCase(field.getName())){
										//追加
									}
									else if("a0000".equalsIgnoreCase(field.getName())|| "a0100".equalsIgnoreCase(field.getName())){
										//系统默认不显示指标
									}
									else if(_str.indexOf(","+field.getName().toUpperCase()+",")==-1&& "0".equalsIgnoreCase(this.userView.analyseFieldPriv(field.getName())))
									{
										 
									}
									else
									cfield.setVisible(true);
								}
								alist.add(cfield);
							}
							if(alist.size()>0)
								fieldlist =alist;
							}
							/* 薪资审批/显示/显示隐藏指标，自定义指标全部显示，在薪资表中指标显示不全 xiaoyun 2014-10-10 start */
							else if(str.length()==0&&!tempflag)// 自己定义了全部显示
							{
								for(int j =0;j<fieldlist.size();j++){
									Field field = (Field)fieldlist.get(j);
									field.setVisible(true);
								}
							}
							/* 薪资审批/显示/显示隐藏指标，自定义指标全部显示，在薪资表中指标显示不全 xiaoyun 2014-10-10 end */
							else if(str.length()>0&&tempflag){//如果设置了审批指标且没自己设置的，那么只显示设置的指标  zhaoxg 
								str = ","+str+",";
								ArrayList alist = new ArrayList();
								for(int j =0;j<fieldlist.size();j++){
									Field field = (Field)fieldlist.get(j);
									Field cfield = (Field)field.clone();
									if(str.indexOf(","+field.getName()+",")!=-1){
										cfield.setVisible(true);
										
									}else{
										cfield.setVisible(false);
									}
									alist.add(cfield);
							}
								if(alist.size()>0)
									fieldlist =alist;
					
						}
					}
			}
			}
			//xieguiquan end
			/** 过滤人员编号和序号*/
			for(int i=0;i<fieldlist.size();i++)
			{
				Field field=(Field)fieldlist.get(i);
				if("a0100".equalsIgnoreCase(field.getName())|| "a0000".equalsIgnoreCase(field.getName()))
					field.setVisible(false);
				if("appprocess".equals(field.getName()))
					field.setLabel("审批意见");
				
				//dengcan 北京移动自动隐藏 审批状态和过程
				if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				{
				 	 if("sp_flag".equalsIgnoreCase(field.getName())|| "appprocess".equalsIgnoreCase(field.getName())|| "a00z1".equalsIgnoreCase(field.getName()))
					 		field.setVisible(false);
				}
				
				if("a00z0".equalsIgnoreCase(field.getName())|| "a00z1".equalsIgnoreCase(field.getName()))
					field.setReadonly(true);
				
			}
			/**增加薪资类别字段*/
			Field field=new Field("salaryid","salaryid");
			field.setDatatype(DataType.INT);
			field.setVisible(false);
			fieldlist.add(field);	
			salary_fieldlist.add(field);
			
			field=new Field("userflag","userflag");
			field.setDatatype(DataType.STRING);
			field.setVisible(false);
			fieldlist.add(field);	
			salary_fieldlist.add(field);
			
//			system.properties  salaryitem=false前台计算项不能编辑
			if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				gzbo.setFieldlist_read2(fieldlist);
			isSpField(fieldlist); //2013-11-25  邓灿  处理设置的项目过滤不包括 审批标识指标，会造成已报批、批准、结束的记录仍能编辑问题。
			this.getFormHM().put("fieldlist", fieldlist);
			/**人员过滤条件*/
			if(!"all".equalsIgnoreCase(condid))
				swhere=gzbo.getFilterWhere(condid,"salaryhistory");			
			/**求业务日期及业务次数列表*/
			//北京移动 修改“薪资上报”“薪资审批”帐套，在进行驳回或报批操作后，页面不显示任何数据，为空白页面
			ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");  //gzbo.getOperationDateList();
		//	ArrayList datelist=gzbo.getOperationDateList();
			this.getFormHM().put("datelist", datelist);
			if((bosdate==null|| "".equalsIgnoreCase(bosdate))&&datelist.size()>0)
			{
				bosdate=((CommonData)datelist.get(0)).getDataValue();
			}
			else if(bosdate==null|| "".equalsIgnoreCase(bosdate))
				bosdate=PubFunc.FormatDate(new Date(), "yyyy.MM.dd");			
			else
			{
				boolean isExist=false;
				for(int i=0;i<datelist.size();i++)
				{
					CommonData data=(CommonData)datelist.get(i);
					if(data.getDataValue().equalsIgnoreCase(bosdate))
						isExist=true;
				}
				if(!isExist&&datelist.size()>0)
					bosdate=((CommonData)datelist.get(0)).getDataValue();
				if(!isExist&&datelist.size()==0)
					bosdate=PubFunc.FormatDate(new Date(), "yyyy.MM.dd");	
			}
			//------------------------薪资审批汇总界面传过来的 zhaoxg add 2015-2-2-----------
			String _bosdate = (String) hm.get("bosdate");
			String _count = (String) hm.get("count");
			String collectPointid = (String) hm.get("collectPointid");
			hm.remove("collectPointid");
			hm.remove("bosdate");
			hm.remove("count");
			if(_bosdate!=null&&_bosdate.length()>0){
				bosdate = _bosdate;
				count = _count;
			}
			//------------------------end-----------------------------------------------
			
		//	ArrayList countlist=(ArrayList)this.getFormHM().get("countlist"); //gzbo.getOperationCountList(bosdate);
			ArrayList countlist=gzbo.getOperationCountList(bosdate);
			/*if(countlist.size()>0)
			{
				count=((CommonData)countlist.get(0)).getDataValue();
			}*/
			if((count==null|| "".equalsIgnoreCase(count))&&countlist.size()>0)
				count=((CommonData)countlist.get(countlist.size()-1)).getDataValue();			
			else if(count==null|| "".equalsIgnoreCase(count))
				count="1";
			else
			{
				boolean isExist=false;
				for(int i=0;i<countlist.size();i++)
				{
					CommonData data=(CommonData)countlist.get(i);
					if(count.equalsIgnoreCase(data.getDataValue()))
						isExist=true;
				}
				if(!isExist&&countlist.size()>0)
					count=((CommonData)countlist.get(countlist.size()-1)).getDataValue();	
				else if(!isExist&&countlist.size()==0)
					count="1";
				
			}
			
			this.getFormHM().put("spFlagList",getSpFlagList());
		//	this.getFormHM().put("appUserList", (ArrayList)this.getFormHM().get("appUserList"));// getAppUserList(bosdate,count,salaryid));
			this.getFormHM().put("appUserList", getAppUserList(bosdate,count,salaryid,gzbo));
			this.getFormHM().put("appUser", (String)this.getFormHM().get("appUser"));
			this.getFormHM().put("sp_flag", (String)this.getFormHM().get("sp_flag"));

			this.getFormHM().put("countlist", countlist);
			this.getFormHM().put("bosdate", bosdate);
			this.getFormHM().put("count", count);
			if("all".equalsIgnoreCase(itemid)&&pro_field!=null&&pro_field.trim().length()>1)
				itemid="new";
			this.getFormHM().put("itemid",itemid);
			StringBuffer select_str=new StringBuffer("");
			Field _field=null;
			for(int i=0;i<salary_fieldlist.size();i++)
			{
				_field=(Field)salary_fieldlist.get(i);
				select_str.append(",salaryhistory."+_field.getName());
				
			}
			/**薪资历史数据过滤*/
			StringBuffer buf=new StringBuffer();
			StringBuffer filterWhl=new StringBuffer();
			buf.append("select "+select_str.substring(1) ); //20150603 邓灿  优化性能，当salaryhistory表字段太多时SELECT * FROM 太影响性能
			//	buf.append("select  salaryhistory.* ");
				
			String withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
				withNoLock=" WITH(NOLOCK) "; 
			buf.append(" from salaryhistory "+withNoLock+" where   salaryid=");   //where 改成 and 20090917
			buf.append(salaryid);
			if(collectPointid!=null&&collectPointid.length()>0){
				buf.append(" and ");
				buf.append(this.getUserView().getHm().get("collectPoint")+" like '"+collectPointid+"%'");
			}
			buf.append(" and A00Z3=");
			filterWhl.append(" and A00Z3=");
			buf.append(count);
			filterWhl.append(count);
			buf.append(" and A00Z2=");
			filterWhl.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(bosdate));
			filterWhl.append(Sql_switcher.dateValue(bosdate));
			if(!(a_code==null|| "".equalsIgnoreCase(a_code)))
			{
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if("UN".equalsIgnoreCase(codesetid)&&value.length()>0)
				{
					buf.append(" and (b0110 like '");
					buf.append(value);
					buf.append("%'");
					filterWhl.append(" and (b0110 like '");
					filterWhl.append(value);
					filterWhl.append("%'");
					if("".equalsIgnoreCase(value))
					{
						buf.append(" or b0110 is null");
						filterWhl.append(" or b0110 is null");
					}
					buf.append(")");
					filterWhl.append(")");
				}
				if("UM".equalsIgnoreCase(codesetid)&&value.length()>0)
				{
					buf.append(" and e0122 like '");
					buf.append(value);
					buf.append("%'");
					filterWhl.append(" and e0122 like '"+value+"%'");
				}
			}	
			//上报人过滤
			if(((String)this.getFormHM().get("appUser")).trim().length()>0)
			{
				buf.append(" and upper(salaryhistory.userFlag)='"+((String)this.getFormHM().get("appUser")).toUpperCase()+"'");
				filterWhl.append(" and upper(salaryhistory.userFlag)='"+((String)this.getFormHM().get("appUser")).toUpperCase()+"'");
			}
		
			
			/**报表用sql*/
			StringBuffer reportSql = new StringBuffer("");
			if(swhere.length()>0)
			{
				buf.append(" and ");
				buf.append("("+swhere+")");
				filterWhl.append(" and ");
				filterWhl.append("("+swhere+")");
				reportSql.append(" and ("+swhere+")");
			}			
			/**需要加上权限范围,还是直接通过报批人员进行过滤? */
			/**    --dengcan 2007-10-09*/
 
			 
			buf.append(" and (( curr_user='"+this.userView.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) ) ");
			filterWhl.append(" and (( curr_user='"+this.userView.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) ) ");
			//薪资审批、确认没加此条件，影响程序性能 2014-04-21 dengcan
		//	reportSql.append(" and (( curr_user='"+this.userView.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  )  or AppUser Like '%;"+this.userView.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) ) ");
			
			/**人员筛选*/
			String empfiltersql = (String)this.getFormHM().get("empfiltersql");
			if(!(empfiltersql==null || "".equals(empfiltersql)))
			{
				empfiltersql=PubFunc.keyWord_reback(empfiltersql);
				/* 所得税管理-人员筛选-显示空白页面问题 xiaoyun 2014-9-23 start */
				empfiltersql = PubFunc.decrypt(SafeCode.decode(empfiltersql));
				/* 所得税管理-人员筛选-显示空白页面问题 xiaoyun 2014-9-23 end */
				reportSql.append(" and ("+empfiltersql+")");
				empfiltersql =gzbo.splitStr(empfiltersql);
				if(buf.toString().indexOf("where")>0)
				{
					buf.append(" and ("+empfiltersql+")");
					filterWhl.append(" and ("+empfiltersql+")");
				}else{
					buf.append(" where ("+empfiltersql+")");
					filterWhl.append(" where ("+empfiltersql+")");
				}
				
			}
			/*
			else{
				condid="all";
			}*/
			
			//审批状态
			if(((String)this.getFormHM().get("sp_flag")).trim().length()>0)
			{
				buf.append(" and salaryhistory.sp_flag='"+((String)this.getFormHM().get("sp_flag"))+"'");
				filterWhl.append(" and salaryhistory.sp_flag='"+((String)this.getFormHM().get("sp_flag"))+"'");
			}
			
			ArrayList queryFieldList=gzbo.getQueryFieldList(salaryid);
			if(((HashMap)this.getFormHM().get("requestPamaHM")).get("query")!=null)
			{
				((HashMap)this.getFormHM().get("requestPamaHM")).remove("query");
				queryFieldList=(ArrayList)this.getFormHM().get("queryFieldList");
				String sql = gzbo.getQuerySql("salaryhistory", queryFieldList);
				if(sql!=null&&!"".equals(sql))
				{
					buf.append(sql);
					filterWhl.append(sql);
					reportSql.append(sql);
				}
				
			}
			this.getFormHM().put("queryFieldList", queryFieldList);
			
			if(((String)this.getFormHM().get("appUser")).trim().length()>0)
			{
					reportSql.append(" and upper(salaryhistory.userFlag)='"+((String)this.getFormHM().get("appUser")).toUpperCase()+"'");
			}
			//审批状态
			if(((String)this.getFormHM().get("sp_flag")).trim().length()>0)
			{
					reportSql.append(" and salaryhistory.sp_flag='"+((String)this.getFormHM().get("sp_flag"))+"'");
			}
			
			this.getFormHM().put("empfiltersql","");
			this.getFormHM().put("reportSql",PubFunc.encrypt(reportSql.toString()));
			/**人员排序*/
			// zgd 2015-1-21 update 薪资审批增加人员排序 start
			String sort_fields = (String)this.getFormHM().get("sort_table_approval");
			this.getFormHM().remove("sort_table_approval");
			if(sort_fields!=null && sort_fields.length()>0)
			{
				String orderby = gzbo.getorderbystr(sort_fields);
				buf.append(" order by "+orderby);
				gzbo.sortemp(sort_fields,this.userView.getUserName(),salaryid);	
				this.getFormHM().put("order_by",PubFunc.encrypt(orderby));
			}else{
				String orderby = (String)this.getFormHM().get("order_by");
				if(orderby!=null && orderby.length()>0){
					orderby = PubFunc.decrypt(SafeCode.decode(orderby));
					if("sync".equalsIgnoreCase(orderby)){
						String order_str=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
						if(order_str!=null&&order_str.trim().length()>0){
							buf.append(" order by "+order_str);
						}else{
							buf.append(" order by  dbid, A0000, A00Z0, A00Z1");
						}
					}else{
						buf.append(" order by "+orderby);
					}
					
				}else{
					String order_str=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
					if(order_str!=null&&order_str.trim().length()>0){
						buf.append(" order by "+order_str);
					}else{
						buf.append(" order by  dbid, A0000, A00Z0, A00Z1");
					}
				}
			}
			// zgd 2015-1-21 update 薪资审批增加人员排序 end
	//		System.out.println(buf.toString());
			
			String  verify_ctrl=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL);
			if(verify_ctrl==null||verify_ctrl.trim().length()==0) ////是否按审核条件控制
				verify_ctrl="0";
			if("1".equals(verify_ctrl))
			{
				String verify_ctrl_sp=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_ctrl_sp");
				if(verify_ctrl_sp!=null&&verify_ctrl_sp.length()>0)
					verify_ctrl=verify_ctrl_sp;
			}
			String isControl=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isControl))
			{
				String amount_ctrl_ff=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");
				if(amount_ctrl_ff!=null&&amount_ctrl_ff.trim().length()>0)
					isControl=amount_ctrl_ff;
				
			}
			
			String relation_id=gzbo.getSpRelationId();
			String  sp_actor_str="";
			if(relation_id.length()>0)
				sp_actor_str=gzbo.getSpActorStr(relation_id);
			if(sp_actor_str.length()>0)
			{
				String[] temps=sp_actor_str.split("`");
				if(temps.length==1)
				{
					temps=temps[0].split("##");
					this.getFormHM().put("spActorName",temps[1]);
				}
				else
					this.getFormHM().put("spActorName", "");
			}
			else
				this.getFormHM().put("spActorName", "");
			this.getFormHM().put("sp_actor_str", SafeCode.encode(sp_actor_str));
			this.getFormHM().put("relation_id",relation_id);
			
			this.getFormHM().put("isTotalControl", isControl);
			
			
			String subNoShowUpdateFashion=gzbo.getLprogramAttri("no_show",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoShowUpdateFashion==null||subNoShowUpdateFashion.trim().length()==0)
				subNoShowUpdateFashion="0";
			
            //参数“允许修改发放结束已提交数据” 控制已提交的数据是否能编辑  wangrd  2013-11-14
			String spRowCanEditStatus=",02,07,";		  
            if (gzbo.isAllowEditSubdata_Sp(gz_module)){//允许提交后更改数据 且 具有提交权限。   
                spRowCanEditStatus=",02,07,06,";                       
            }			
            this.getFormHM().put("spRowCanEditStatus",spRowCanEditStatus);
			this.getFormHM().put("subNoShowUpdateFashion",subNoShowUpdateFashion);
			this.getFormHM().put("verify_ctrl",verify_ctrl);
			this.getFormHM().put("sql",PubFunc.encrypt(buf.toString()));
			this.getFormHM().put("filterWhl",PubFunc.encrypt(filterWhl.toString()));
			this.userView.getHm().put("gz_filterWhl",filterWhl.toString());
			ArrayList manfilterlist=gzbo.getManFilterList();
			this.getFormHM().put("condid",condid);
			this.getFormHM().put("condlist", manfilterlist);
			this.getFormHM().put("tablename","salaryhistory");
 			this.getFormHM().put("userid",this.getUserView().getUserId());
		//	if(gzbo.isHistory())
				this.getFormHM().put("bedit", "true");
		//	else
		//		this.getFormHM().put("bedit", "false");
				
			String isSendMessage="0";
			SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.getFrameconn(),Integer.parseInt(salaryid));
			if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"mail")))
				isSendMessage="1";
			if(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(ctrlparam.getValue(SalaryCtrlParamBo.NOTE,"sms")))
				isSendMessage="1";
			this.getFormHM().put("isSendMessage", isSendMessage);
			String zjjt=(String) hm.get("zjjt");
			hm.remove("zjjt");
			if(bosdate.length()>0&&count!=null&&"1".equals(zjjt)){
				LazyDynaBean bean=new LazyDynaBean();
				String[] temp=bosdate.split("\\.");
				bean.set("year", temp[0]);//年
				bean.set("month", temp[1]);//月
				bean.set("count", count);//次数
				LazyDynaBean _bean=SalaryTemplateBo.updatePendingTask(this.getFrameconn(), this.userView, this.userView.getUserName(),salaryid,bean,"4");
				PendingTask pt = new PendingTask();
				if("update".equals(_bean.get("flag"))){
					pt.updatePending("G", "G"+_bean.get("pending_id"), 2, "薪资审批", this.userView);
				}		
				if("update".equals(_bean.get("selfflag"))){
					pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
				}
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	private ArrayList getSpFlagList()
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("",ResourceFactory.getProperty("hire.jp.pos.all")));
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			
			StringBuffer buf=new StringBuffer("select codeitemid,codeitemdesc from codeitem where codesetid='23' and codeitemid in ('02','03','06','07')");
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(2)));
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	/**
	 * 判断是否有审批标识字段，没有则追加
	 * @param list
	 */
	private void isSpField(ArrayList list)
	{
		try
		{
			boolean isFlag=false;
			for(int i=0;i<list.size();i++)
			{
				Field field=(Field)list.get(i);
				String fieldname=field.getName();
				if("sp_flag".equalsIgnoreCase(fieldname))
				{
					isFlag=true;
				}
			}//for i loop end.
			if(!isFlag)
			{
				Field field=new Field("sp_flag",ResourceFactory.getProperty("label.gz.sp"));
                field.setLength(50);
                field.setCodesetid("23");
                field.setDatatype(DataType.STRING);
                field.setReadonly(true);
                field.setVisible(false);
                list.add(field);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 取得上报人列表
	 * @param bosdate
	 * @param count
	 * @param salaryid
	 * @return
	 */
	private ArrayList getAppUserList(String bosdate,String count,String salaryid,SalaryTemplateBo gzbo)
	{
		ArrayList list=new ArrayList();
		list.add(new CommonData("",ResourceFactory.getProperty("hire.jp.pos.all")));
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			
			StringBuffer buf=new StringBuffer("select distinct salaryhistory.userFlag,operuser.fullname ");
			buf.append(" from salaryhistory,operuser  where  lower(salaryhistory.userFlag)=lower(operuser.username) ");   //where 改成 and 20090917			 
			buf.append(" and (( curr_user='"+this.userView.getUserId()+"' and ( sp_flag='02' or sp_flag='07' ) ) or ( ( (AppUser is null  "+gzbo.getPrivWhlStr("")+"  ) or AppUser Like '%;"+this.userView.getUserName()+";%' ) and  ( sp_flag='06' or  sp_flag='03' ) ) )");
			buf.append(" and salaryid=");
			buf.append(salaryid);
			buf.append(" and A00Z3=");
			buf.append(count);
			buf.append(" and A00Z2=");
			buf.append(Sql_switcher.dateValue(bosdate));
			
			this.frowset=dao.search(buf.toString());
			while(this.frowset.next())
			{
				String userFlag=this.frowset.getString(1);
				String fullname=userFlag;
				if(this.frowset.getString(2)!=null&&this.frowset.getString(2).trim().length()>0)
					fullname+="("+this.frowset.getString(2)+")";
				
				list.add(new CommonData(this.frowset.getString(1),fullname));
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	
}
