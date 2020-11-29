package com.hjsj.hrms.module.muster.mustermanage.transaction;

import com.hjsj.hrms.module.muster.mustermanage.businessobject.MusterManageService;
import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 花名册分类操作交易类：
 *  实现花名册分类的新建、删除、名称修改以及获取指定分类下的花名册
 * @author Manjg
 *
 */
public class MusterClassTrans extends  IBusiness{
	private static String OPERATION_SEARCH = "search_lstyle";//查找指定分类下的花名册
	private static String OPERATION_SAVE = "save_lstyle";//保存花名册分类
	private static String OPERATION_ADD = "add_lstyle";//新增花名册分类
	private static String OPERATION_DELETE = "delete_lstyle";//删除花名册分类
	private static String OPERATION_INIT = "main";//初始化获取花名册分类集合
	@Override
	public void execute() throws GeneralException {
		try {
			MusterManageService musterManageService = new MusterManageServiceImpl(this.frameconn,this.userView);
			String moduleID = (String) this.getFormHM().get("moduleID");//模块号，=0：员工管理；=1：组织机构；
			String musterType = (String) this.getFormHM().get("musterType");//花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；
			String type = (String) this.getFormHM().get("type");//请求类型，=main：主页面；=search_lstyle：查询花名册分类下花名册
			String styleid = (String) this.getFormHM().get("styleid");//花名册分类id
			String styledesc = (String) this.getFormHM().get("styledesc");//花名册分类名
			styledesc = PubFunc.hireKeyWord_filter(styledesc);
			if(!StringUtils.isEmpty(type)) {
				//获取指定分类下的花名册
				List listMuster = new ArrayList(); 
				if(MusterClassTrans.OPERATION_SEARCH.equals(type)) {
					//获取sql
		    		TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("musterManage");
		    		String sql =tableCache.getTableSql();
		    		int size=tableCache.getPageSize();
					if("-1".equals(styleid)) {
						sql = musterManageService.getMusterMainSql(musterType, moduleID);
					}else{
						int index = sql.indexOf("lname.styleid =");
						if (index==-1) {
						    sql = sql+" and lname.styleid = '"+styleid+"'";
	                    }else {
	                        sql=sql.substring(0,index-1);
	                        sql=sql+" lname.styleid = '"+styleid+"'";
	                    }
					}
	    			tableCache.setTableSql(sql);
	    			tableCache.setPageSize(size);
				}
				//删除分类前的检查
				if ("del_style_check".equals(type)) {
					listMuster = musterManageService.listMuster(styleid,musterType); 
					this.getFormHM().put("styleMuster", listMuster);
				}
				//保存花名册分类
				if(MusterClassTrans.OPERATION_SAVE.equals(type) || MusterClassTrans.OPERATION_ADD.equals(type)) {
					HashMap data = new HashMap();
	    			data.put("styleid", styleid);
	    			data.put("styledesc", styledesc);
	    			data.put("type", type);
	    			data.put("musterstyletype", musterType);
	    		    String state=musterManageService.saveMusterLstyle(data);
	    		    this.getFormHM().put("state",state);
				}
				//删除花名册分类
				if(MusterClassTrans.OPERATION_DELETE.equals(type)) {
					musterManageService.deleteMusterLstyle(styleid);
				}
				//新增分类之前的检查
				if ("check".equals(type)) {
					//获取花名册分类 集合
		    		List musterLstyle = musterManageService.listMusterLstyle(musterType);
					this.getFormHM().put("styleClass", musterLstyle);
	    			String style_text = (String) this.getFormHM().get("style_text");
	    			if (StringUtils.isNotEmpty(style_text)) {
						boolean result = isExist(style_text, musterLstyle);
						this.getFormHM().put("result", result);
					}
				}
				//获得删除分类和添加分类的权限
				if ("getPriv".equals(type)) {
					//是否具有新建花名册分类的权限
					boolean addMusterStylePriv = false;
					//是否具有删除花名册分类的权限
					boolean delMusterStylePriv = false;
					if ("1".equals(musterType)) {//人员花名册
					    if (userView.isSuper_admin()||userView.hasTheFunction("2603103")||userView.hasTheFunction("030903")){ 
		                    addMusterStylePriv = true;
		                }
					    if (userView.isSuper_admin()||userView.hasTheFunction("2603104")||userView.hasTheFunction("030904")){ 
		                    delMusterStylePriv = true;
		                }
                    }else if ("2".equals(musterType)) {//单位花名册
                        if (userView.isSuper_admin() || userView.hasTheFunction("2303103")){ 
                            addMusterStylePriv = true;
                        }
                        if (userView.isSuper_admin()||userView.hasTheFunction("2303104")){ 
                            delMusterStylePriv = true;
                        }
                    }else if ("3".equals(musterType)) {//岗位花名册
                        if (userView.isSuper_admin()||userView.hasTheFunction("2503103")){ 
                            addMusterStylePriv = true;
                        }
                        if (userView.isSuper_admin()||userView.hasTheFunction("2503104")){ 
                              delMusterStylePriv = true;
                        }
                    }else if ("4".equals(musterType)) {//基准岗位花名册
                        if (userView.isSuper_admin()||userView.hasTheFunction("2503103")){ 
                            addMusterStylePriv = true;
                        }
                        if (userView.isSuper_admin()||userView.hasTheFunction("2503104")){ 
                              delMusterStylePriv = true;
                        }
                    }
					this.getFormHM().put("addMusterStylePriv",addMusterStylePriv);
					this.getFormHM().put("delMusterStylePriv",delMusterStylePriv);
				}
				if(MusterClassTrans.OPERATION_INIT.equals(type)) {
					//获取花名册分类 集合
		    		List musterLstyle = musterManageService.listMusterLstyle(musterType);
					this.getFormHM().put("styleClass", musterLstyle);
				}
			}else {			
				throw GeneralExceptionHandler.Handle(new Exception("未定义花名册分类操作类型异常！"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
	}
	//判断新增的花名册分类名是否存在
	private boolean isExist(String musterClassName,List<HashMap<String,String>> musterLstyle) {
		boolean flag = false;
		musterClassName = StringUtils.deleteWhitespace(musterClassName);
		for (HashMap<String,String> map : musterLstyle) {
			String name = map.get("styledesc");
			name=StringUtils.deleteWhitespace(name);
			if (StringUtils.equals(name, musterClassName)) {
				flag =true;
			    break;
			}
		}
		return flag;
	}
}
