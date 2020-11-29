
package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.AutoFormBo;
import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:人事异动支持多指标批量修改功能</p> 
 *@author 郭峰
 *@version 5.0
 */
public class SetBatchUpdateFieldsTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		try
		{
			if(tabid==null|| "-1".equalsIgnoreCase(tabid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.tabid"));
			ArrayList lastlist = new ArrayList();//专门存放变化后的字段（满足条件的）
			//bean的属性示例：{field_name=E0112, itemlength=10, field_type=D, isvar=0, disformat=6, yneed=0, 
			//codeid=0, hismode=1, mode=0, setname=A01, subflag=0, gridno=23, field_hz=减员时间, 
			//sub_domain_id=, chgstate=2, hz=减员时间`, sub_domain=, formula=, pageid=0}
			TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			ArrayList templateSetList=bo.getAllCell();
			String fieldSetSortStr = (String)this.getFormHM().get("fieldSetSortStr");//指标排列顺序 以,分割
			String infor_type = (String)this.getFormHM().get("infor_type");//1:人员 2：单位部门 3：岗位
			String taskid=(String)hm.get("taskid");//任务号
			String sp_batch =(String)hm.get("sp_batch");//批量方式 wangrd 2015-01-09
			hm.remove("sp_batch");
			//String sp_batch =(String)this.getFormHM().get("sp_batch");//批量方式
			HashMap templateMap=(HashMap) this.userView.getHm().get("templateMap");
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			if(templateMap!=null&&!templateMap.containsKey(taskid)&&!"1".equals(sp_batch)){//流程号被串改后，操作结束
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			hm.remove("taskid");
			
			//String tasklist_str =(String)this.getFormHM().get("tasklist_str");//？？？
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			AutoFormBo autoFormBo=new AutoFormBo();
			HashMap fieldPrivMap=new HashMap();
			if("1".equals(sp_batch)){//如果是批量方式
			    String batch_task=(String)this.getFormHM().get("batch_task");//批量方式 wangrd 2015-01-09
			    if (batch_task==null || "".equals(batch_task)){
                    batch_task =(String)this.getFormHM().get("tasklist_str");//列表模式 多个指标
                }
				String[] lists=StringUtils.split(batch_task,",");
				for(int i=0;i<lists.length;i++)
				{
					if(lists[i]==null||lists[i].trim().length()==0)
						 continue;
					taskid=lists[i];
					/**安全平台改造所有的taskid都需要后台确认是否正确**/
					/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
					if(!templateMap.containsKey(taskid)){//流程号被串改后，操作结束
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
					}
					*/
					//fieldPrivMap=autoFormBo.getFieldPriv(taskid,this.getFrameconn());
				//	break;
				}
				fieldPrivMap=autoFormBo.getFieldPriv(batch_task,this.getFrameconn());
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
						String field_name = (String)abean.get("field_name");//没有chgstate
						String chgstate = (String)abean.get("chgstate");
						String subflag = (String)abean.get("subflag");
						if(("0".equals(isvar)&&(field_name+"_"+chgstate).equalsIgnoreCase(temp[i])) || ("1".equals(isvar)&&field_name.equalsIgnoreCase(temp[i]))){//如果是字段或者是临时变量
							if("1".equals(subflag))//去掉子集项
								break;
							if("0".equals(isvar)){//如果是字段
								if("2".equals(chgstate)){//如果是变化后字段
									String itemid=abean.get("field_name").toString().toLowerCase();
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
									lastlist.add(abean);	
								}
							}
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
					String field_name = (String)abean.get("field_name");//没有chgstate
					if("1".equals(subflag))//去掉子集项
						continue;
					if("0".equals(isvar)){
						if("2".equals(chgstate)){
							String itemid=field_name.toLowerCase();
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
							lastlist.add(abean);
						}
					}
				}
			}
			
			this.getFormHM().put("targitemlist", lastlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
