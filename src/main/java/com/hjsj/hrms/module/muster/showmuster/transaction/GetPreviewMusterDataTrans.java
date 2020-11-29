package com.hjsj.hrms.module.muster.showmuster.transaction;

import com.hjsj.hrms.module.muster.showmuster.businessobject.impl.ShowManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
/**
 * 花名册预览分页交易类
 * @author Zhiyh
 *
 */
public class GetPreviewMusterDataTrans extends  IBusiness{
	@Override
	public void execute() throws GeneralException {
		try {
		    String musterType = (String) this.getFormHM().get("musterType");//花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；
		    ShowManageServiceImpl showManageService = new ShowManageServiceImpl(this.frameconn,this.userView);
			String datasql =  (String) this.getFormHM().get("datasql");
			datasql = StringUtils.isNotEmpty(datasql)?PubFunc.decrypt(datasql):"select * from usra01 where 1=2";
			String ordersql =  (String) this.getFormHM().get("ordersql");
			ordersql = StringUtils.isNotEmpty(ordersql)?PubFunc.decrypt(ordersql):"";
			String startStr = (String) this.getFormHM().get("start");
			String limitStr = (String) this.getFormHM().get("limit");
			String fields =(String) this.getFormHM().get("fields");
			String sortField =(String) this.getFormHM().get("sortField");
			int start = Integer.parseInt(startStr);
			int limit = Integer.parseInt(limitStr);
			String[] fieldArray= fields.split(",");
			String fieldsetString = (String) this.getFormHM().get("fieldsetString"); //A01,
			ArrayList<String> fieldsetList = getList(fieldsetString,musterType,0,showManageService);//已选指标的子集
			ArrayList<String> fieldList = arrayToList(fieldArray, musterType, showManageService);
			boolean flag01 = get01Flag(fieldsetList);//指标集中是否有X01  true 表示有 false表示没有
			HashMap<String, Object> map   = getData(showManageService,datasql,musterType,ordersql, fieldList,start,limit,sortField,flag01,fieldsetList);
			ArrayList<HashMap<String, Object>> data = (ArrayList<HashMap<String, Object>>) map.get("list");
			int total = (Integer) map.get("total");
			this.getFormHM().put("list", data);
			this.getFormHM().put("total", total);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 获取预览界面的数据
	 * @param showManageService
	 * @param sql
	 * @param fieldlist
	 * @param start
	 * @param sortField
	 * @param flag01
	 * @param fieldsetList
	 * @return
	 */
	private HashMap<String, Object> getData(ShowManageServiceImpl showManageService, String sql, String musterType, String ordersql, ArrayList<String> fieldlist, int start, int pageSize, String sortField, boolean flag01, ArrayList<String> fieldsetList) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if (null==fieldlist) {
			return null;
		}
		if (fieldsetList.size()==0||fieldlist.size()==0) {
			return null;
		}
		if (flag01&&"1".equals(musterType)&&!fieldlist.contains("A0000")) {
			fieldlist.add(0,"A0000");
		}
		Collections.sort(fieldsetList);
		boolean flag = false;
		for (String itemid:fieldlist) {
			if (itemid.equalsIgnoreCase(showManageService.getSqlX0100( musterType))) {
				flag =true;
			}
		}
		if (!flag) {
			fieldlist.add(0, showManageService.getSqlX0100(musterType));
		}

		ArrayList<HashMap<String, Object>>  list = new ArrayList<HashMap<String, Object>>();
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sqlbuf = new StringBuffer("");
			int totalcount = 0;
			sqlbuf.append("select count(*) totalcount from (");
			sqlbuf.append(sql);
	        sqlbuf.append(") hmctable ");
	        this.frowset = dao.search(sqlbuf.toString());
	        if (this.frowset.next()) {
                totalcount = this.frowset.getInt("totalcount");
            }
	        pageSize = start+pageSize;
            start++;
            pageSize++;
			sqlbuf = new StringBuffer("");
            sqlbuf.append("select * from (");
            sqlbuf.append("select ROW_NUMBER() OVER (ORDER BY ");
            sqlbuf.append(showManageService.orderBySqlFormat(fieldlist,sortField,"1",flag01,musterType,"1"));
            sqlbuf.append(") AS xuhao,");
			sqlbuf.append(showManageService.orderBySqlFormat(fieldlist,sortField,"0",flag01,musterType,"1"));
            sqlbuf.append(" from (");
            sqlbuf.append(sql);
            sqlbuf.append(") hmctable ) fianlly");
            sqlbuf.append(" where fianlly.xuhao >= ");
            sqlbuf.append(start);
            sqlbuf.append(" and fianlly.xuhao < ");
            sqlbuf.append(pageSize);
            this.frowset = dao.search(sqlbuf.toString());
			while (this.frowset.next()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < fieldlist.size(); i++) {
					String itemid = fieldlist.get(i);
					FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
					if ("XUHAO".equals(itemid)) {
					    map.put(itemid, this.frowset.getString(itemid)==null?"":this.frowset.getString(itemid));
					}else if (null==fieldItem) {
					    map.put(itemid, "");
                    }else {
						String type = fieldItem.getItemtype();
						if ("A".equals(type)) {
							String codesetid = fieldItem.getCodesetid();
							if ("0".equals(codesetid)) {
								map.put(itemid, this.frowset.getString(itemid)==null?"":this.frowset.getString(itemid));
							}else {
								String value = this.frowset.getString(itemid)==null?"":this.frowset.getString(itemid);
								CodeItem codeItem = AdminCode.getCode(codesetid, value);
								if (codeItem==null&&"UN".equals(codesetid)) {
                                    codeItem =AdminCode.getCode("UM",value);
                                }
								map.put(itemid, codeItem==null?"":codeItem.getCodename());
							}
						}else if ("D".equals(type)) {
							if (this.frowset.getDate(itemid)==null) {
								map.put(itemid, "");
							}else {
							    int  itemlength= fieldItem.getItemlength();
							    Date date = this.frowset.getDate(itemid);
                                String value = format(itemlength, date);
								map.put(itemid, value);
							}
						}else if ("N".equals(type)) {
							    if (this.frowset.getString(itemid)==null) {
							    	map.put(itemid, "");
								}else {
									if (fieldItem.getDecimalwidth()>0) {
										double value = this.frowset.getDouble(itemid);
										map.put(itemid, new DecimalFormat(",##0.00").format(value));
									}else {
										int value = this.frowset.getInt(itemid);
										map.put(itemid, new DecimalFormat(",###").format(value));
									}
								}
						}else {
							map.put(itemid, this.frowset.getString(itemid)==null?"":this.frowset.getString(itemid));
						}
					}
				}
				list.add(map);
			}
			hashMap.put("list", list);
			hashMap.put("total",totalcount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashMap;
	}
	/**
	 * 根据字段长度对时间字段进行格式化
	 * @param itemlength
	 * @param datevalue
	 * @return
	 */
	private String format(int  itemlength,Date datevalue) {
	    String datestr = "";
	    switch(itemlength){
            case 4:
                datestr=DateStyle.dateformat(datevalue,"yyyy");                 
                break;
            case 7:
                datestr=DateStyle.dateformat(datevalue,"yyyy-MM");                  
                break;
            case 10:
                datestr=DateStyle.dateformat(datevalue,"yyyy-MM-dd");                   
                break;
            case 16:
                datestr=DateStyle.dateformat(datevalue,"yyyy-MM-dd HH:mm");                 
                break;  
            case 18:
                datestr=DateStyle.dateformat(datevalue,"yyyy-MM-dd HH:mm:ss");                  
                break;
            default:
                datestr=DateStyle.dateformat(datevalue,"yyyy-MM-dd");                   
                break;
        }
	    return datestr;
    }
	private ArrayList<String> arrayToList(String[] array,String musterType,ShowManageServiceImpl showManageService) {
	    ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
	    ContentDAO contentDAO = new ContentDAO(this.frameconn);
        ArrayList<String> fieldlist =   showManageService.getPrivFieldList(contentDAO, userView, musterType);
        for (int i = list.size()-1; i >-1; i--) {
        	String fielditemid = list.get(i).toUpperCase();
        	FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid);
        	if (StringUtils.equals("E01A1", fielditemid)&&"K01".equalsIgnoreCase(fieldItem.getFieldsetid())) {
				continue;
			}
            if (!fieldlist.contains(list.get(i).toLowerCase())) {
                list.remove(i);
            }
        }
        list.add(0, "XUHAO");
        return list;
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

}
