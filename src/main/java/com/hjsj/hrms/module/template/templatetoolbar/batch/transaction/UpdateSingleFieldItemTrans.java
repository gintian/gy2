package com.hjsj.hrms.module.template.templatetoolbar.batch.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
* <p>Title:TemplateSingleTrans </p>
* <p>Description:批量修改单个指标 </p>
* <p>Company:hjsoft </p> 
* @author gaohy
* @date 2015-11-24下午01:45:14
 */
public class UpdateSingleFieldItemTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		String logo = (String)this.getFormHM().get("transType");//标识，判断是初始化还是确定修改   init:初始化
		HashMap hmMap= this.getFormHM();
		
		//String task_id=(String)this.getFormHM().get("task_id");
		//task_id = PubFunc.decryption(task_id);
		//String tabid=(String)this.getFormHM().get("tab_id");	
		//String ins_id=(String)this.getFormHM().get("ins_id");
		//String infor_type = (String)this.getFormHM().get("infor_type");//1:人员 2：单位部门 3：岗位
		String tabid = TemplateFuncBo.getValueFromMap(hmMap,"tab_id");
		//String ins_id = TemplateFuncBo.getValueFromMap(hmMap,"ins_id");
		String task_id = TemplateFuncBo.getDecValueFromMap(hmMap,"task_id");
		String infor_type = TemplateFuncBo.getValueFromMap(hmMap,"infor_type");//1:人员 2：单位部门 3：岗位
		
		DbWizard dbWizard=new DbWizard(this.getFrameconn()); 
		try
		{
			TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			if("0".equals(task_id)||task_id.trim().length()==0)  //出于安全考虑，发起节点判断用户是否有该模版资源权限
			{
				 if(!bo.getBo().isCorrect(tabid))
					throw new GeneralException(ResourceFactory.getProperty("template.operation.noResource"));
			}
			if(tabid==null|| "-1".equalsIgnoreCase(tabid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.tabid"));
			
			ArrayList itemlist = new ArrayList();//存放变化前和变化后的字段（满足条件的）,封装成json串
			ArrayList lastlist = new ArrayList();//专门存放变化后的字段（满足条件的） 
			ArrayList lastPropertyList = new ArrayList();//存放变化后的指标的属性
			Document doc=null;
			Element element=null;
			String xpath="/sub_para/para";
			
			String table_name="";//获得表名
			if("0".equals(task_id)){
				table_name = this.userView.getUserName()+"templet_"+tabid;
			}else{
				table_name = "templet_"+tabid;
			}
			
			if("init".equalsIgnoreCase(logo)){//初始化批量修改单个指标弹框
					ArrayList templateSetList=bo.getAllCell();//所有的模板字段
					TemplateDataBo dataBo = new TemplateDataBo(this.frameconn,this.userView,Integer.parseInt(tabid));
					ArrayList cellList =  dataBo.getUtilBo().getAllCell(Integer.parseInt(tabid));
		            HashMap fieldPrivMap = dataBo.getFieldPrivMap(cellList, task_id);
		            String opinion_field = dataBo.getParamBo().getOpinion_field();

					for(int i=0;i<templateSetList.size();i++){
						LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);
						String subflag = (String)abean.get("subflag");
						String isvar = (String)abean.get("isvar");
						String chgstate = (String)abean.get("chgstate");
						String field_name = (String)abean.get("field_name");
						String field_hz = (String)abean.get("field_hz");
						String field_type=(String)abean.get("field_type");
						String codeid=(String)abean.get("codeid");
						String sub_domain=SafeCode.decode((String)abean.get("sub_domain"));
						ArrayList jsonlst = new ArrayList();//存放变化前和变化后的字段（满足条件的）
						HashMap fieldMap = new HashMap();
						if("1".equals(subflag))//去掉子集项
							continue;
						jsonlst.add(field_name+"_"+chgstate);
						if("2".equals(chgstate))
							field_hz="拟"+field_hz;
						jsonlst.add(field_hz);
						if("0".equals(isvar)){
							if("2".equals(chgstate)){ //变化后指标
								String itemid=field_name.toLowerCase();
			                    if (StringUtils.isNotBlank(opinion_field) && opinion_field.toLowerCase().equals(itemid)){//审批意见指标
			                       continue;
			                    }
								/*if((infor_type!=null&&!infor_type.equals("1")) && (field_name.equalsIgnoreCase("codesetid")||
										field_name.equalsIgnoreCase("codeitemdesc")||
										field_name.equalsIgnoreCase("corcode")||
										field_name.equalsIgnoreCase("parentid")||
										field_name.equalsIgnoreCase("start_date"))){
								}else{*/
									if(fieldPrivMap!=null&&fieldPrivMap.size()>0&&fieldPrivMap.get(itemid+"_2")!=null){
			                       		String editable=(String)fieldPrivMap.get(itemid+"_2"); //	//0|1|2(无|读|写)
			                       		if("1".equals(editable))
			                       			continue;
			                       		else if("0".equals(editable)){
			                       			continue; 
			                        	}else {
			                        		 if (codeid != null) {
			                                     if ("UN".equalsIgnoreCase(codeid.toString()) || "UM".equalsIgnoreCase(codeid.toString())|| "@K".equalsIgnoreCase(codeid.toString())){
			                                    	 String limit_manage_priv = "";
			                                    	 if(sub_domain!=null&&sub_domain.trim().length()>0){
						                					try{
					                							doc=PubFunc.generateDom(sub_domain);
					                							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
					                							List childlist=findPath.selectNodes(doc);	
					                							if(childlist!=null&&childlist.size()>0){
					                								element=(Element)childlist.get(0);
					                								if(element.getAttributeValue("limit_manage_priv")!=null){
					                									limit_manage_priv=(String)element.getAttributeValue("limit_manage_priv");
					                								}
					                							}
						                					}catch(Exception e){
						                					}
						                				}
			                                         if ("1".equals(limit_manage_priv)){
			                                        	 fieldMap.put("limit_manage_priv", "3");//业务范围
			                                         }else
			                                        	 fieldMap.put("limit_manage_priv", "0");//不控制
			                                         fieldMap.put("nmodule", "8");
			                                         fieldMap.put("codesource", "");
			                                     }else if("orgType".equalsIgnoreCase(codeid)) {
			                                    	 codeid = "codesetid";
			                                    	 fieldMap.put("codesource", "GetSpecialTemplateSetTree");
			                                     }
			                                     boolean codesetValid = false;
			                                     if("UN".equalsIgnoreCase(codeid.toString())||"@K".equalsIgnoreCase(codeid.toString()))
			                                    	 codesetValid = true;
			                                     fieldMap.put("codesetid", codeid);
			                                     fieldMap.put("codesetValid", codesetValid);
			                                     fieldMap.put("type", field_type);
			                                     fieldMap.put("fieldname",field_name+"_"+chgstate);
			                                     fieldMap.put("imppeople", abean.get("imppeople"));
			                                     fieldMap.put("fatherRelationField", abean.get("fatherRelationField"));
			                                     fieldMap.put("childRelationField", abean.get("childRelationField"));
			                                     lastPropertyList.add(fieldMap);
			                                 }else if("0".equalsIgnoreCase(codeid)){
			                                	 fieldMap.put("imppeople", abean.get("imppeople"));
			                                     fieldMap.put("fatherRelationField", abean.get("fatherRelationField"));
			                                     fieldMap.put("childRelationField", abean.get("childRelationField"));
			                                     lastPropertyList.add(fieldMap);
			                                 }
			                        	}
				                    }
							    //}
								lastlist.add(jsonlst);
							} 
						}
						if("1".equals(isvar)){
							if(!dbWizard.isExistField(table_name, field_name==null?"":field_name))
								continue;
						}
						itemlist.add(jsonlst);
				}
				this.getFormHM().put("targitemlist", lastlist); //变化后指标
				this.getFormHM().put("ref_itemlist", itemlist); //参考项目
				this.getFormHM().put("lastpropertylist", lastPropertyList); 
			}else if("ok".equalsIgnoreCase(logo)){//批量修改点击确定
				//String task_id=(String)this.getFormHM().get("task_id");
				String itemid=(String)this.getFormHM().get("itemid");//修改的是哪个指标  格式：B0110_2
				String formula=(String)this.getFormHM().get("formula");	//修改后的内容
				String cond=(String)this.getFormHM().get("cond");//条件
				String selchecked=(String)this.getFormHM().get("selected");//复选框选中
			
				cond=SafeCode.decode(cond);
				cond=PubFunc.keyWord_reback(cond);
				formula=SafeCode.decode(formula);
				if(itemid!=null&&itemid.length()>0&&formula!=null&&formula.length()>0)
				{
					boolean b=bo.batchUpdateItem(itemid,formula,cond,"",table_name,task_id,selchecked,"");
				}
			}else if("code".equals(logo)){
				String itemid = (String)this.getFormHM().get("itemid");
				if(StringUtils.isNotBlank(itemid)){
					itemid = itemid.split("_")[0];
					this.getFormHM().put("itemlist", this.getcodeItemList(itemid)); //变化后指标
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 获取代码
	 * @param itemid
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList<CommonData> getcodeItemList(String itemid) throws GeneralException
	{
		ArrayList<CommonData> list=new ArrayList<CommonData>();
		RowSet frowset = null;
		try
		{
			//list.add(new CommonData("", ""));
			ContentDAO dao=new ContentDAO(this.frameconn);
			FieldItem item=DataDictionary.getFieldItem(itemid.toLowerCase());
			if(item==null)
				return null;
			String codesetid=item.getCodesetid();
			if(!"0".equals(codesetid))
			{
				String sql="";
				if("UN".equals(codesetid)|| "UM".equals(codesetid)|| "@K".equals(codesetid))
				{
					sql="select codeitemid,codeitemdesc from organization where (codesetid='"+codesetid+"'";
				}
				else
				{
					sql="select codeitemid,codeitemdesc from codeitem where (codesetid='"+codesetid+"'";
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String backdate = sdf.format(new Date());
				if("UM".equals(codesetid)){//支持关联部门的指标也可以选择单位
					sql+= " or codesetid ='UN'";
				}
				sql+=") and " + Sql_switcher.dateValue(backdate)
     			+ " between start_date and end_date";
				if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid))
				{
					StringBuffer str = new StringBuffer();
					String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
					String[] unitarr =b_units.split("`");
					for(int i=0;i<unitarr.length;i++)
					{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				String privCode = codeid.substring(0,2);
		    				String privCodeValue = codeid.substring(2);	
		    				str.append(" or  codeitemid like '"+privCodeValue+"%'");
	    				}
					}
					if(str.length()>0){//批量修改走优先级判断，zhaoxg add 2016-9-7
						sql = sql+" and ("+str.substring(3)+")";
					}
					sql=sql+(" ORDER BY a0000,codeitemid ");
				}else if(!"@@".equalsIgnoreCase(codesetid))
				{
					sql=sql+(" ORDER BY codeitemid ");
				}
				frowset=dao.search(sql);
				while(frowset.next())
				{
					list.add(new CommonData(frowset.getString(1),frowset.getString(1)+":"+frowset.getString(2)));
				}
				if(list.size()==0){
					list.add(new CommonData("", ""));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(frowset);
		}
		return list;
	}
}
