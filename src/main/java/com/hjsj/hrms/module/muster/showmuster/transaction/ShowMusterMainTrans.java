package com.hjsj.hrms.module.muster.showmuster.transaction;

import com.hjsj.hrms.module.muster.mustermanage.businessobject.MusterManageService;
import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hjsj.hrms.module.muster.showmuster.businessobject.impl.ShowManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/**
 * 花名册展现、预览、交易类
 * @author Zhiyh
 *
 */
public class ShowMusterMainTrans extends  IBusiness{
	@Override
	public void execute() throws GeneralException {
		try {
		    MusterManageService musterManageService = new MusterManageServiceImpl(this.frameconn,this.userView);
		    String musterType = (String) this.getFormHM().get("musterType");//花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；
		    ShowManageServiceImpl showManageService = new ShowManageServiceImpl(this.frameconn,this.userView);
			String moduleID = (String) this.getFormHM().get("moduleID");//模块号，=0：员工管理；=1：组织机构；
			String tabid = (String) this.getFormHM().get("tabid");
			String flag = (String)this.getFormHM().get("flag");
			String sortField = (String) this.getFormHM().get("sortField");//排序指标 A01.B01100,A01.E01220,
			String nbases = (String) this.getFormHM().get("nbases");//人员库 "Usr,Ret,Oth"
			String filterid = (String) this.getFormHM().get("filterid");//员工管理，模块常用查询的查询条件id :"0"
			String range_type = (String) this.getFormHM().get("range_type");//0 当前记录 1 年月 2 多个指标条件 3 单个
			boolean parttimejobvalue = false;
			if (this.getFormHM().containsKey("parttimejobvalue")) {
			    parttimejobvalue = (Boolean) this.getFormHM().get("parttimejobvalue");//是否显示兼职人员
            }
			String condition = (String) this.getFormHM().get("condition");//条件表达式
			String fieldsetString = (String) this.getFormHM().get("fieldsetString"); //A01,
			String fielditemString = (String) this.getFormHM().get("fielditemString");//A01.B0110,A01.E0122,A01.A0101,
			ArrayList<String> fieldsetList = getList(fieldsetString,musterType,0,showManageService);//已选指标的子集 
			ArrayList<String> fieldlist = getList(fielditemString,musterType,1,showManageService);//已选择的指标
			boolean flag01 = get01Flag(fieldsetList);//指标集中是否有X01  true 表示有 false表示没有
    		//获取sql
    		if("preview".equals(flag)) {//预览
    		    HashMap<String, Object> map  = new HashMap<String, Object>();
                map.put("flag", flag);
                map.put("sortField", sortField);
                map.put("parttimejobvalue", parttimejobvalue);
                map.put("Nbases", nbases);
                map.put("filterid", filterid);
                map.put("range_type", range_type);
                if ("2".equals(range_type)) {
                    map.put("condition", SafeCode.decode(condition));
                }else {
                    map.put("condition", condition);
                }
                map.put("flag01", flag01);
                map.put("fieldsetList", fieldsetList);
                map.put("fieldlist", fieldlist);
    			ArrayList<String> fields = getFields(fielditemString);
				ArrayList<HashMap<String, Object>> columns = getColumns(fielditemString,musterType,showManageService);
				String sql = showManageService.getDataSql(tabid,moduleID,musterType,map);
				this.getFormHM().put("fields", fields);
				this.getFormHM().put("columns", columns);
				this.getFormHM().put("sql", PubFunc.encrypt(sql));
				this.getFormHM().put("ordersql","");
    		}else if ("dataRang".equals(flag)) {//数据范围
    		    HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("flag", flag);
                map.put("Nbases", nbases);
                map.put("filterid", filterid);
                map.put("parttimejobvalue", parttimejobvalue);
                map.put("range_type", range_type);
                if ("2".equals(range_type)) {
                    map.put("condition", SafeCode.decode(condition));
                }else {
                    map.put("condition", condition);
                }
                String sql = showManageService.getDataSql(tabid,moduleID,musterType,map);
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("showMuster"+tabid);
	    		tableCache.setTableSql(sql);
			}else {
			    String priv = musterManageService.getMusterPriv(moduleID);//当前用户的权限
	            boolean result = checkPriv(tabid, priv);
	            if (!result) {
	                throw new GeneralException(ResourceFactory.getProperty("muster.notPriv"));//您不具备此花名册的权限
	            }
				/** 获取列头 */
			    String sql = showManageService.getDataSql(tabid,moduleID,musterType,null);
    			ArrayList<ColumnsInfo> columnList = null;
    			if (tabid!=null&&!"".equals(tabid)) {
    				columnList = showManageService.getColumnList(tabid,musterType);
    			}else {
    				columnList = getCloumList(fieldlist,musterType,showManageService);
    			}
    			/** 获取操作按钮*/
        		ArrayList buttonList = showManageService.getButtonList(musterType);
        		//获取花名册的名称
        		String musterName = "";
        		if (tabid!=null&&!"".equals(tabid)) {
        			musterName = showManageService.getMusterName(tabid);
    			}
        		TableConfigBuilder builder = new TableConfigBuilder("showMuster"+tabid, columnList, "showMuster"+tabid, userView, this.getFrameconn());
        		builder.setDataSql(sql);
        		builder.setShowRowNumber(true);
        		builder.setSortable(true);
    			builder.setAutoRender(true);
    			builder.setColumnFilter(true);
    			builder.setLockable(true);
    			builder.setPageSize(20);
    			builder.setEditable(false);
	            builder.setTitle(musterName);
	            builder.setTableTools(buttonList);
    			setShowPublicPlan(builder, musterType);
    			String config = builder.createExtTableConfig();
    			this.getFormHM().put("tableConfig", config.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 设置栏目设置
	 * @param builder
	 * @param musterType
	 */
	private void setShowPublicPlan(TableConfigBuilder builder,String musterType) {
	    if (userView.isSuper_admin()) {
            builder.setScheme(true);
            builder.setShowPublicPlan(true);
        }else {
            if (StringUtils.equals("1", musterType)&&( userView.hasTheFunction("2603106")||userView.hasTheFunction("030906"))) {//人员花名册
                builder.setScheme(true);
                builder.setShowPublicPlan(true);
            }else if (StringUtils.equals("2", musterType)&&userView.hasTheFunction("2303106")) {//单位花名册
                builder.setScheme(true);
                builder.setShowPublicPlan(true);
            }else if (StringUtils.equals("3", musterType)&&userView.hasTheFunction("2503106")) {//岗位花名册
                builder.setScheme(true);
                builder.setShowPublicPlan(true);
            }else if (StringUtils.equals("4", musterType)&&userView.hasTheFunction("2503106")) {//基准范围花名册
                builder.setScheme(true);
                builder.setShowPublicPlan(true);
            }
        }
	}
	/**
	 * 
	 * @param string  指标或子集字符串
	 * @param musterType
	 * @param flag 0：子集 ；1：指标
	 * @param showManageService
	 * @return
	 */
	private ArrayList<String>  getList(String string,String musterType,int flag,ShowManageServiceImpl showManageService) {
		if (null==string) {
			return null;
		}
		ArrayList<String>  list = new ArrayList<String>();
		try {
			String[] array = string.split(",");
			ArrayList<FieldSet> fieldSetlist=userView.getPrivFieldSetList(Integer.parseInt(musterType));
			ContentDAO contentDAO = new ContentDAO(this.frameconn);
            ArrayList<String> fieldlist = showManageService.getPrivFieldList(contentDAO, userView, musterType);
			for (int i = 0; i < array.length; i++) {
			    if (0==flag) {
			        for (FieldSet fieldSet : fieldSetlist) {
                        if (fieldSet.getFieldsetid().equals(array[i].toUpperCase())) {
                            list.add(array[i]);
                        }
                    }
                }
			    if (1==flag&&(fieldlist.contains(array[i].split("\\.")[1].toLowerCase())||("K01.E01A1".equalsIgnoreCase(array[i])))) {
                    list.add(array[i]);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 将String转成list   
	 * @param string
	 * @return
	 */
	private ArrayList<String>  getFields(String string) {
		if (null==string) {
			return null;
		}
		ArrayList<String>  list = new ArrayList<String>();
		try {
			String[] array = string.split(",");
			for (int i = 0; i < array.length; i++) {
				list.add(array[i].split("\\.")[1].toUpperCase());
			}
			list.add(0,"XUHAO");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @param string
	 * @return
	 */
	private ArrayList<HashMap<String, Object>>  getColumns(String string,String musterType,ShowManageServiceImpl showManageService) {
		if (null==string) {
			return null;
		}
		ArrayList<HashMap<String, Object>>  list = new ArrayList<HashMap<String, Object>>();
		try {
			String[] array = string.split(",");
			ContentDAO contentDAO = new ContentDAO(this.frameconn);
	        ArrayList<String> fieldlist =   showManageService.getPrivFieldList(contentDAO, userView, musterType);
			for (int i = 0; i < array.length; i++) {
				String itemid = array[i].split("\\.")[1];
				if (fieldlist.contains(itemid.toLowerCase())||"K01.E01A1".equalsIgnoreCase(array[i])) {
				    HashMap<String, Object> map = new HashMap<String, Object>();
	                FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
	                map.put("text", fieldItem.getItemdesc());
	                map.put("dataIndex", itemid.toUpperCase());
	                if ("N".equals(fieldItem.getItemtype())) {
	                	map.put("align", "right");
					}
	                map.put("width",150);
	                list.add(map);
                }
			}
			 HashMap<String, Object> map = new HashMap<String, Object>();
			 map.put("text","");
             map.put("dataIndex", "XUHAO");
             map.put("align", "right");
             map.put("width",60);
             list.add(0, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 判断list里面有01
	 * @param list
	 * @return
	 */
	private boolean  get01Flag(ArrayList<String>  list) {
		if (null==list) {
			return false;
		}
		boolean flag = false;
		try {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).indexOf("01")!=-1) {
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 
	 * @param list
	 * @return
	 */
	private ArrayList<ColumnsInfo> getCloumList(ArrayList<String> fieldList,String musterType,ShowManageServiceImpl showManageService) {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		for(int i=0;i<fieldList.size();i++) {
		    ContentDAO contentDAO = new ContentDAO(this.frameconn);
            ArrayList<String> fieldlist =   showManageService.getPrivFieldList(contentDAO, userView, musterType);
            if (fieldlist.contains(fieldList.get(i).split("\\.")[1].toLowerCase())) {
                FieldItem fieldItem = DataDictionary.getFieldItem(fieldList.get(i).split("\\.")[1]);
                if (null!=fieldItem) {
                    ColumnsInfo info = new ColumnsInfo(fieldItem);
                    info.setLocked(false);
                    info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                    list.add(info);
                }
            }
		}
		FieldItem xuhaoItem = new FieldItem();
		xuhaoItem.setItemid("xuhao");
		xuhaoItem.setItemdesc("");
		xuhaoItem.setItemtype("A");
	    ColumnsInfo info = new ColumnsInfo(xuhaoItem);
	    info.setLocked(false);
        info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
        info.setColumnWidth(50);
        info.setTextAlign("right");
        list.add(0,info);
		return list;
		
	}
	/**
	 * 检查当前用户是否有
	 * @param tabid
	 * @param priv  xxx,xxx | xxx,xxx  权限内机构 | 上级机构   
	 * @return
	 */
	private boolean checkPriv(String tabid,String priv ) {
	    boolean result = false;
	    try {
            if (userView.isSuper_admin()|| "UN".equalsIgnoreCase(priv)) {
                return true;
            }else {
                if (StringUtils.isBlank(priv)) {
                    return false;
                }
                ContentDAO dao = new ContentDAO(frameconn);
                int id = Integer.parseInt(tabid);
                ArrayList<Integer> list = new ArrayList<Integer>();
                list.add(id);
                this.frowset = dao.search("select b0110 from lname where tabid = ?",list);
                String b0110 = null;
                if (this.frowset.next()) {
                    b0110 = this.frowset.getString("b0110");
                }
                if (StringUtils.isNotEmpty(b0110)) {
                    if (priv.indexOf("|")!=-1) {
                       String privorg= priv.split("\\|")[0];
                       String[] privArray = privorg.split(",");
                       for (int i = 0; i < privArray.length; i++) {
                            String privid = privArray[i];
                            if (b0110.startsWith(privid)) {
                                result = true;
                                break;
                            }
                       }
                       if (!result&&priv.split("\\|").length==2) {
                           priv= priv.split("\\|")[1];
                           privArray = priv.split(",");
                           for (int i = 0; i < privArray.length; i++) {
                               String privid = privArray[i];
                               if (b0110.equalsIgnoreCase(privid)) {
                                   result = true;
                                   break;
                               }
                          }
                       }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
	}
	/**
	 * 根据排序指标获得排序
	 * @param sortfield
	 * @return
	 */
	private String getOrderBySql(String sortField,String musterType,ShowManageServiceImpl showManageService) {
		StringBuffer sql = new StringBuffer("");
		try {
		    if (StringUtils.isNotBlank(sortField)) {
		        String[] sortFieldArray = sortField.split(",");
	            ContentDAO contentDAO = new ContentDAO(this.frameconn);
	            ArrayList<String> fieldlist =   showManageService.getPrivFieldList(contentDAO, userView, musterType);
	            ArrayList<String> sortFieldList = new ArrayList<String>(Arrays.asList(sortFieldArray));
	            for (int i = sortFieldList.size()-1; i >-1; i--) {
	                String itemid = sortFieldList.get(i).toLowerCase().split("\\.")[1];
	                if (!fieldlist.contains(itemid.substring(0, itemid.length()-1))) {
	                    sortFieldList.remove(i);
	                }
	            }
	            if (sortFieldList.size()>0) {
	                sql.append(" order by ");
	                for (int j = 0; j < sortFieldList.size(); j++) {
	                    if (j!=0) {
	                        sql.append(",");
	                    }
	                    String itemlength = sortFieldList.get(j);
	                    String flag = itemlength.substring(itemlength.length()-1, itemlength.length());
	                    String itemid = itemlength.substring(0,itemlength.length()-1);
	                    sql.append(itemid.split("\\.")[1]);
	                    if ("1".equals(flag)) {
	                        sql.append(" desc");
	                    }
	                }
	            }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql.toString();
	}
}
