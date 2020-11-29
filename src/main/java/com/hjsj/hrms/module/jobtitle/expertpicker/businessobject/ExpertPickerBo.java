package com.hjsj.hrms.module.jobtitle.expertpicker.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * 资格评审_专家选择控件
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 */
public class ExpertPickerBo {
    Connection conn;
    UserView userview;
    
    public ExpertPickerBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;
    }
    
    /**
     * 获取列头，表格渲染
     * @return
     */
    public ArrayList<ColumnsInfo> getColumnList(){
    	ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
    	try{
	    	//取得数据字典中设置的w01的构库的所有字段
	    	ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
			for(int i=0; i<fieldList.size(); i++){
				FieldItem item = (FieldItem)fieldList.get(i);		
				String itemid = item.getItemid();//字段id
				String itemtype = item.getItemtype();//字段类型
				String codesetid = item.getCodesetid();//关联的代码			
				String columndesc = item.getItemdesc();//字段描述
				int itemlength = item.getItemlength();//字段长度
				String state = item.getState();//0隐藏  1显示
				//列类型特殊处理
				if("w0103".equals(itemid) || "w0105".equals(itemid) || "w0107".equals(itemid)){
					itemtype = "M";
				}
				
				ColumnsInfo columnsInfo = getColumnsInfo(itemid, columndesc, 100, itemtype);
				if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
					if("0".equals(codesetid) || codesetid == null){//非代码字符型
						//获得字段描述
						if("w0101".equals(itemid)){
							columnsInfo.setColumnLength(itemlength);
							columnsInfo.setCodesetId("0");
							columnsInfo.setEncrypted(true);
							columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						}else{
							columnsInfo.setColumnLength(itemlength);
							columnsInfo.setCodesetId("0");
							if("0".equals(state)){
								columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
							}
						}
					}else{//代码型字符
						columnsInfo.setColumnLength(itemlength);
						columnsInfo.setCodesetId(codesetid);
						if("0".equals(state)){
							columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						}
					}
				} else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
					columnsInfo.setColumnLength(itemlength);
					columnsInfo.setCodesetId("0");
					if("0".equals(state)){
						columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					}
				}
				if("w0103".equals(itemid) || "w0105".equals(itemid) || "w0107".equals(itemid)){
					columnsInfo.setLocked(true);
				}
				columnTmp.add(columnsInfo);
				
			}
			ColumnsInfo b0110 = getColumnsInfo("b0110", "所属机构", 150, "A");
			b0110.setCodesetId("UM");
			b0110.setCtrltype("3");
			b0110.setNmodule("9");
			columnTmp.add(b0110);
    	} catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
    	return columnTmp;
    }
  
	/**
     * 列头ColumnsInfo对象初始化
     * 
     * @param columnId
     *            id
     * @param columnDesc
     *            名称
     * @param columnDesc
     *            显示列宽
     * @param type
     *            列的数据类型
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        //columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        columnsInfo.setSortable(true);// 是否排序
        if ("A".equals(type)) {
            columnsInfo.setCodesetId("0");
        }
        columnsInfo.setDecimalWidth(0);// 小数位

        // 数值和日期默认居右
        if ("D".equals(type) || "N".equals(type))
            columnsInfo.setTextAlign("right");

        return columnsInfo;
    }
	
    /**
     * 获得需要查询的sql字段
     * @return
     */
	public String getSelectSql() {
    	ArrayList fieldList = DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
    	StringBuffer datasql =  new StringBuffer();
		for(int i=0;i<fieldList.size();i++){
			FieldItem item = (FieldItem)fieldList.get(i);		
			String itemid = item.getItemid();//字段id
			datasql.append(" "+itemid+",");
		}
		return datasql.toString().substring(0, datasql.toString().length()-1);
	}
	
	
	/**
	 * @param sql 原sql
	 * @param personidList 需要排出的人员列表
	 * @return 所有人列表
	 * @throws GeneralException
	 */
	public ArrayList<String> getAllPerson(String sql, ArrayList<String> personidList) throws GeneralException {
		
		ArrayList<String> personList = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.conn);
    	RowSet rs = null;
    	
    	try {
    		StringBuilder newsql = new StringBuilder(sql);
    		if(personidList.size() != 0){
    			newsql.append(" and W0101 not in ( ");
    		}
    		for(int i=0; i<personidList.size(); i++){
				String expertid = personidList.get(i);
				expertid = PubFunc.decrypt(expertid);
				if(i != personidList.size()-1){
					newsql.append("'" + expertid + "', ");
				}else{
					newsql.append("'" + expertid + "' ");
				}
    		}
    		if(personidList.size() != 0){
    			newsql.append(" ) ");
    		}
    		rs = dao.search(newsql.toString());
    		while(rs.next()){
    			String w0101 = rs.getString("W0101");
    			w0101 = PubFunc.encrypt(w0101);
    			personList.add(w0101);
    		}
    		
    	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeDbObj(rs);
		}
		
		
		
		return personList;
	}
}
