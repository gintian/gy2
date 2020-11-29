package com.hjsj.hrms.module.projectmanage.workhours.manhoursdetail.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ManHoursDetailBo {
    Connection conn;
    ContentDAO dao;
    UserView userview;
    public ManHoursDetailBo(Connection frameconn, UserView userView) {
        this.conn = frameconn;
        this.userview = userView;
    }
    /**
     * 
     * @Title:getColumnList
     * @Description： 获取列头、表格渲染
     * @author liuyang
     * @param islock 
     * @param isAddWidth 
     * @param notEditFields 
     * @param exceptFields 
     * @param canEdit 
     * @return ArrayList<ColumnsInfo>
     */
    public ArrayList<ColumnsInfo> getColumnList(String exceptFields, String EditFields, String isAddWidth, String islock) {
        ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();

        //成员id（主键、隐藏）
        ArrayList fieldList = DataDictionary.getFieldList("P13",1);
        
        ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
        
        ColumnsInfo columnsInfo = new ColumnsInfo();
        for(int i=0; i<fieldList.size(); i++){
            columnsInfo = new ColumnsInfo();
            
            FieldItem fi = (FieldItem)fieldList.get(i);
         
            // 去除不需要的指标
            if(exceptFields.indexOf(","+fi.getItemid().toLowerCase()+",") != -1){
                continue;
            }
            // 去除未构库的指标
            if(!"1".equals(fi.getUseflag())){
                continue;
            }
            // 去除隐藏的指标
            if(!"1".equals(fi.getState())){
                columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            }
            String itemid = fi.getItemid();
            String itemdesc = fi.getItemdesc();
            String codesetId = fi.getCodesetid();
            String columnType = fi.getItemtype();
            int columnLength = fi.getItemlength();// 显示长度 
            int decimalWidth = fi.getDecimalwidth();// 小数位
            columnsInfo = getColumnsInfo(itemid, itemdesc, 100, codesetId, columnType, columnLength, decimalWidth);
            
            if("P1319".equalsIgnoreCase(fi.getItemid())){// 实际工时
                columnsInfo.setTextAlign("right");
            }
            
            if("P1321".equalsIgnoreCase(fi.getItemid())){// 标准工时
                columnsInfo.setTextAlign("right");
            }
            
            if("A0101".equalsIgnoreCase(fi.getItemid())){// 姓名
                if(userview.hasTheFunction("39003"))
                columnsInfo.setRendererFunc("manhoursdetail_me.toMenDetailPage");
            }
            
            if("P1311".equalsIgnoreCase(fi.getItemid())){// 承担角色
                columnsInfo.setCodesource("GetProjectMemberRoleSelectTreeResource");
            }
            
            if("P1323".equalsIgnoreCase(fi.getItemid())){// 超额工时
                columnsInfo.setTextAlign("right");
            }
            
            if("P1301".equalsIgnoreCase(fi.getItemid())){// 主键id
                columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
                columnsInfo.setEncrypted(true);
            }
            // 允许编辑的列
            if(!StringUtils.isEmpty(EditFields)){
                if(EditFields.indexOf(","+fi.getItemid()+",") != -1&&"p1311".equalsIgnoreCase(itemid)){
                    columnsInfo.setEditableValidFunc("manhoursdetail_me.isManager");
                }else if(EditFields.indexOf(","+fi.getItemid()+",") == -1){
                    columnsInfo.setEditableValidFunc("false");
                }
            }
            // 需要增加列宽的列
            if(!StringUtils.isEmpty(isAddWidth)){
                if(isAddWidth.indexOf(","+fi.getItemid()+",") != -1){
                    columnsInfo.setColumnWidth(145);//显示列宽
                }
            }
            // 需要锁列
            if(!StringUtils.isEmpty(islock)){
                if(islock.indexOf(","+fi.getItemid()+",") != -1){
                    columnsInfo.setLocked(true);
                }
            }      
            columnsList.add(columnsInfo);
        }
        return columnsList;
    }
    public ArrayList getButtonList() {
        ArrayList buttonList = new ArrayList();
        if(userview.hasTheFunction("3900201"))
            buttonList.add("-");
        if(userview.hasTheFunction("3900202")){
            buttonList.add(newButton("保存",null,"manhoursdetail_me.save",null,"true"));
            buttonList.add("-"); 
            }
        buttonList.add(newButton("返回",null,"manhoursdetail_me.returnToMainPage",null,"true"));
        ButtonInfo querybox = new ButtonInfo();
        querybox.setFunctionId("PM00000204");
        querybox.setType(ButtonInfo.TYPE_QUERYBOX);
        querybox.setText("请输入姓名，电话，邮箱...");
        buttonList.add(querybox);
        return buttonList;
        
    }
    /**
     * 列头ColumnsInfo对象初始化
     * @param columnId id
     * @param columnDesc 名称
     * @param columnDesc 显示列宽
     * @return
     */
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String codesetId, String columnType, int columnLength, int decimalWidth){
        
        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        columnsInfo.setColumnWidth(columnWidth);//显示列宽
        columnsInfo.setCodesetId(codesetId);// 指标集
        columnsInfo.setColumnType(columnType);// 类型N|M|A|D
        columnsInfo.setColumnLength(columnLength);// 显示长度 
        columnsInfo.setDecimalWidth(decimalWidth);// 小数位
        columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
        columnsInfo.setReadOnly(false);// 是否只读
        columnsInfo.setFromDict(true);// 是否从数据字典里来
        columnsInfo.setLocked(false);//是否锁列
        return columnsInfo;
    }
    /**
     * 
     * @Title:newButton
     * @Description：获取按钮
     * @author liuyang
     * @param text
     * @param id
     * @param handler
     * @param icon
     * @param getdata
     * @return
     */
    private ButtonInfo newButton(String text,String id,String handler,String icon,String getdata)
    {  
        ButtonInfo button = new ButtonInfo(text,handler); 
        if(getdata!=null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());
        if(icon!=null)
            button.setIcon(icon);
        if(id!=null)    
            button.setId(id);
        return button;
    }
   /**
    * 
    * @Title:updateManHoursDetail
    * @Description：更新人员信息
    * @author liuyang
    * @param projectId
    * @param dataList
    * @return
    * @throws GeneralException
    */
    public String updateManHoursDetail(String projectId, ArrayList dataList) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        MorphDynaBean aBean = new MorphDynaBean();
        String resultTip = "1";
        ArrayList values = new ArrayList();
        StringBuffer updateAllStr = new StringBuffer(" UPDATE P13 ");
        updateAllStr.append(" set ");
        updateAllStr.append(" P1307=?,");
        updateAllStr.append(" P1309=?, ");
        updateAllStr.append(" P1311=?, ");
        updateAllStr.append(" P1313=?, ");
        updateAllStr.append(" P1315=?, ");
        updateAllStr.append(" P1317=? ");
        updateAllStr.append(" where  P1301=? ");
        updateAllStr.append(" and  P1101=? ");
        try {
           
            for (int i = 0; i < dataList.size(); i++) {
                ArrayList valueList = new ArrayList();
                aBean = (MorphDynaBean) dataList.get(i);
                valueList.add((String) aBean.get("P1307"));
                valueList.add((String) aBean.get("P1309"));
                String P1311 = (String) aBean.get("P1311");
                if(StringUtils.isNotEmpty(P1311))
                    P1311 = P1311.substring(0,P1311.indexOf("`"));
                valueList.add(P1311);
                valueList.add(aBean.get("P1313"));
                Date P1315 = DateUtils.getTimestamp((String) aBean.get("P1315")+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
                Date P1317 = DateUtils.getTimestamp((String) aBean.get("P1317")+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
                valueList.add(P1315);
                valueList.add(P1317);
                
                String manId = "";
                if(StringUtils.isNotEmpty((String)aBean.get("P1301")))
                    manId = PubFunc.decrypt(aBean.get("P1301").toString());
                    
                valueList.add(manId);
                valueList.add(projectId);
                values.add(valueList);
            }
            dao.batchUpdate(updateAllStr.toString(), values);
        } catch (SQLException e) {
            resultTip = "0";
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return resultTip;
    }
}
