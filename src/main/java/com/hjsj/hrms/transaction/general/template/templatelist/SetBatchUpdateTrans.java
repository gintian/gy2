/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.AutoFormBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:设置批量更新公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2010-9-13:下午03:07:20</p> 
 *@author 郭峰
 *@version 5.0
 */
public class SetBatchUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		try
		{
			if(tabid==null|| "-1".equalsIgnoreCase(tabid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.tabid"));
			ArrayList itemlist = new ArrayList();//存放变化前和变化后的字段（满足条件的）
			ArrayList lastlist = new ArrayList();//专门存放变化后的字段（满足条件的）
			ArrayList templateSetList = (ArrayList)this.getFormHM().get("templateSetList");//所有的模板字段
			String fieldSetSortStr = (String)this.getFormHM().get("fieldSetSortStr");//指标排列顺序 以,分割
			String table_name = (String)this.getFormHM().get("table_name");
			String infor_type = (String)this.getFormHM().get("infor_type");//1:人员 2：单位部门 3：岗位
			String taskid=(String)hm.get("taskid");
			//String sp_batch =(String)this.getFormHM().get("sp_batch");
			String sp_batch =(String)hm.get("sp_batch");//批量方式 wangrd 2015-01-09
            hm.remove("sp_batch");
			/**安全平台改造,确认taskid是否存在于后台map中**/
			HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			if(templateMap!=null&&!templateMap.containsKey(taskid)&&!"1".equals(sp_batch)){//如果是批量审批,就不用在这里面做处理了，直接在批量审批方式中判断就可以了
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			
			//String tasklist_str =(String)this.getFormHM().get("tasklist_str");//
			
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			AutoFormBo autoFormBo=new AutoFormBo();
			HashMap fieldPrivMap=new HashMap();
			if("1".equals(sp_batch)){//如果是批量方式
			    String batch_task=(String)this.getFormHM().get("batch_task");//批量方式 wangrd 2015-01-09
                if (batch_task==null || "".equals(batch_task)){
                    batch_task =(String)this.getFormHM().get("tasklist_str");//列表模式 单个指标
                }
                String[] lists=StringUtils.split(batch_task,",");
				/**安全平台改造,确认taskid是否存在于后台map中begin**/
                /*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
				for(int i=0;i<lists.length;i++){
					String tempTaskid=lists[i];
					if(templateMap!=null&&!templateMap.containsKey(tempTaskid)){
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
					}
				}
				*/
				/**安全平台改造,确认taskid是否存在于后台map中end**/
				for(int i=0;i<lists.length;i++)
				{
					if(lists[i]==null||lists[i].trim().length()==0)
						 continue;
					taskid=lists[i];
					fieldPrivMap=autoFormBo.getFieldPriv(taskid,this.getFrameconn());
					break;
				}
			}else{
				if(taskid!=null&&!"0".equals(taskid)&&tablebo.isBsp_flag()&&tablebo.getSp_mode()==0)//如果需要审批，且自动流转
					fieldPrivMap=autoFormBo.getFieldPriv(taskid,this.getFrameconn());
			}
			
			
			if(fieldSetSortStr!=null&&fieldSetSortStr.length()>0){
				String temp [] = fieldSetSortStr.split(",");
				for(int i=0;i<temp.length;i++){
					for(int j=0;j<templateSetList.size();j++){
						LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
						String isvar = (String)abean.get("isvar");
						String field_name = (String)abean.get("field_name");
						String chgstate = (String)abean.get("chgstate");
						String subflag = (String)abean.get("subflag");
						String field_hz = (String)abean.get("field_hz");
						if(("0".equals(isvar)&&(field_name+"_"+chgstate).equalsIgnoreCase(temp[i])) || ("1".equals(isvar)&&field_name.equalsIgnoreCase(temp[i]))){//如果是字段或者是临时变量
							if("1".equals(subflag))//去掉子集项
								break;
							CommonData dataobj = null;
							if("0".equals(isvar)){//如果是字段
								if("2".equals(chgstate)){//如果是变化后字段
									 String itemid=abean.get("field_name").toString().toLowerCase();
									 dataobj = new CommonData(field_name+"_"+chgstate,
											 "拟"+field_hz);
										if((infor_type!=null&&!"1".equals(infor_type))
												&&("codesetid".equalsIgnoreCase(field_name)|| "codeitemdesc".equalsIgnoreCase(field_name)|| "corcode".equalsIgnoreCase(field_name)
												|| "parentid".equalsIgnoreCase(field_name)|| "start_date".equalsIgnoreCase(field_name))){//如果是单位部门或岗位，并且field_name是制定的上述字段
										}else{//否则
											if(fieldPrivMap!=null&&fieldPrivMap.size()>0&&fieldPrivMap.get(itemid+"_2")!=null){//如果变化后的指标有权限
					                        	String editable=(String)fieldPrivMap.get(itemid+"_2"); //	//0|1|2(无|读|写)
					                        	if("1".equals(editable)){//如果只有读权限或无权限，就略过
					                        		break;
					                        	}
					                        	else if("0".equals(editable)){
													break; 
					                        	}
					                        }
											else if(!"2".equalsIgnoreCase(this.userView.analyseFieldPriv(abean.get("field_name").toString().trim()))&& "0".equals(tablebo.getUnrestrictedMenuPriv_Input())){
												break;
											}
											if(tablebo.getOpinion_field()!=null&&tablebo.getOpinion_field().length()>0&&tablebo.getOpinion_field().equalsIgnoreCase(field_name))
												break;
										}
										lastlist.add(dataobj);
								}else{//如果是变化前字段
									dataobj = new CommonData(field_name+"_"+chgstate,field_hz);
								}
							}else{//如果是临时变量
								dataobj = new CommonData(field_name,field_hz);
							}
							if("1".equals(abean.get("isvar"))){
								if(!dbWizard.isExistField(table_name, field_name==null?"":field_name)){
									break;
								}
							}
							itemlist.add(dataobj);
							break;
						} //处理字段 结束
					} //for templateSetList end
				} // for temp end
			}else{//fieldSetSortStr ==null
				for(int i=0;i<templateSetList.size();i++){
					LazyDynaBean abean = (LazyDynaBean)templateSetList.get(i);
					String subflag = (String)abean.get("subflag");
					String isvar = (String)abean.get("isvar");
					String chgstate = (String)abean.get("chgstate");
					String field_name = (String)abean.get("field_name");
					String field_hz = (String)abean.get("field_hz");
					CommonData dataobj = null;
					if("1".equals(subflag))//去掉子集项
						continue;
					if("0".equals(isvar)){
						if("2".equals(chgstate)){
							String itemid=field_name.toLowerCase();
							dataobj = new CommonData(field_name+"_"+chgstate,"拟"+field_hz);
							if((infor_type!=null&&!"1".equals(infor_type)) && ("codesetid".equalsIgnoreCase(field_name)|| "codeitemdesc".equalsIgnoreCase(field_name)|| "corcode".equalsIgnoreCase(field_name)|| "parentid".equalsIgnoreCase(field_name)|| "start_date".equalsIgnoreCase(field_name)))
							{
								
							}else{
								if(fieldPrivMap!=null&&fieldPrivMap.size()>0&&fieldPrivMap.get(itemid+"_2")!=null){
			                       		String editable=(String)fieldPrivMap.get(itemid+"_2"); //	//0|1|2(无|读|写)
			                       		if("1".equals(editable))
			                       			continue;
			                       		else if("0".equals(editable))
			                       		{
			                       			continue; 
			                        	}
			                    }
								else if(!"2".equalsIgnoreCase(this.userView.analyseFieldPriv(abean.get("field_name").toString().trim()))&& "0".equals(tablebo.getUnrestrictedMenuPriv_Input()))
									continue;
								if(tablebo.getOpinion_field()!=null&&tablebo.getOpinion_field().length()>0&&tablebo.getOpinion_field().equalsIgnoreCase(field_name))
									continue;
						    }
							lastlist.add(dataobj);
						}else{
							dataobj = new CommonData(field_name+"_"+chgstate,field_hz);
						}
					}else{
						dataobj = new CommonData(field_name,field_hz);
					}
					if("1".equals(abean.get("isvar"))){
						if(!dbWizard.isExistField(table_name, field_name==null?"":field_name))
							continue;
					}
					itemlist.add(dataobj);
				}
			}
			
/**刘红梅在安全改造时,要求这里人员编号,机构编号,职位编号不应该显示出来,所以注释掉 xcs 2014-11-3**/			
//			if(infor_type.equals("1")){
//				CommonData dataobj = new CommonData("a0100","人员编号");
//				itemlist.add(0, dataobj);
//			}else if(infor_type.equals("2")){
//				CommonData dataobj = new CommonData("b0110","机构编号");
//				itemlist.add(0, dataobj);
//			}else if(infor_type.equals("3")){
//				CommonData dataobj = new CommonData("e01a1","职位编号");
//				itemlist.add(0, dataobj);
//			}
			
			this.getFormHM().put("targitemlist", lastlist);
			this.getFormHM().put("ref_itemlist", itemlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
