package com.hjsj.hrms.module.system.qrcard.setting;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
*
* @Titile: SearchQRCardTrans
* @Description:查询表单内容用于前台生成表单
* @Company:hjsj
* @Create time:2018年8月7日10:18:51
* @author: wangwh
* @version 1.0
*
*/
public class SearchQRCardTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        UserView userview = this.userView;
        //获取dao对象
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String[]  array = null;
        if(userView.getUnitIdByBusi("4")!=null&&userView.getUnitIdByBusi("4").length()>0) {
              array = userView.getUnitIdByBusi("4").split("`");
        }
        
        String addOrganizationId = array[0];
        String  addOrganizationName ="";
        if(userView.isSuper_admin() || array!=null&&array.length>0&& "UN`".equalsIgnoreCase(addOrganizationId)) {
            addOrganizationName = "全部";
            addOrganizationId = "UN";
        }else if(array!=null && array.length>0 && ("".equalsIgnoreCase(addOrganizationId) || "UN".equalsIgnoreCase(addOrganizationId))){
        	addOrganizationId="";
            addOrganizationName ="";
        }else {
            String  codeid = addOrganizationId.substring(0,2);
            addOrganizationId=addOrganizationId.substring(2);
            addOrganizationName = AdminCode.getCodeName(codeid, addOrganizationId);
        }     
        //校验添加删除的权限
        boolean addPriv = userview.hasTheFunction("3001K01");
        boolean deletePriv = userview.hasTheFunction("3001K02");
        boolean clearPriv = userview.hasTheFunction("3001K03");
        //用于获取基本信息所组成的map
        List<HashMap<String, String>> paramList = new ArrayList<HashMap<String, String>>();
        //用于获取所有的模板名称
        List<String> tabName = new ArrayList<String>();
        //二维码表单id
        String qrid = "";
        //业务表单id
        String tabid = "";
        //二维码表单名称
        String name = "";
        //二维码表单描述信息
        String description = "";
        //二维码表单内容信息
        String detail_description = "";
        //人员调入表单资源权限
        boolean PerTransferTablePriv = false;
        //用于判断是否存在人员调入表单
        boolean PerTransferTable = false;
        String print_qrid = (String)this.getFormHM().get("qrid");
        //判断是配置二维码界面还是打印二维码界面
        if(print_qrid==null) {
        try {
        	
            //效验是否拥有人员调入表单资源权限
            String PerTransferTableSql = "SELECT Template_table.TabId FROM Template_table LEFT JOIN Operation ON Operation.OperationCode = Template_table.OperationCode WHERE Operation.OperationType = '0'";
            this.frowset = dao.search(PerTransferTableSql.toString());
            while (this.frowset.next()) {
            	PerTransferTable = true;
            	if(isHaveTemplateid(this.frowset.getString("TabId"))) {
            		PerTransferTablePriv = true;
            	}
            }
            //如果不存在人员调入表单PerTransferTablePriv变为true
            if(!PerTransferTable) {
            	PerTransferTablePriv = true;
            }
            StringBuffer selectFormSql = new StringBuffer("select * from t_sys_tipwizard_qrcord where upper(b0110)='UN' ");
            if(array!=null&&array.length>0&&!"UN".equalsIgnoreCase(addOrganizationId)) {
            	selectFormSql.append("or (");
                for(int i = 0;i<array.length;i++) {
                    selectFormSql.append(" b0110 like '"+array[i].substring(2)+"%' or ");
                }
                selectFormSql.setLength(selectFormSql.length()-3);
                selectFormSql.append(") ");
            }else if(array!=null&&array.length>0&& "UN".equalsIgnoreCase(addOrganizationId)) {
                selectFormSql.append(" or 1=1 ");
            }
            selectFormSql.append(" order by qr_id");
            this.frowset = dao.search(selectFormSql.toString());
            while (this.frowset.next()) {
                HashMap<String, String> map = new HashMap<String, String>();
                qrid = this.frowset.getString("qr_id");
                tabid = this.frowset.getString("tab_id");
                name = this.frowset.getString("qr_name");
                description = this.frowset.getString("qr_description");
                detail_description = this.frowset.getString("detail_description");
                String organizationName = "";
                String b0110 = this.frowset.getString("b0110") == null ? "" : this.frowset.getString("b0110");
                if("UN".equals(b0110)) {
                    organizationName = "全部";   
                }else {
                    organizationName = AdminCode.getCodeName("UN", b0110);
                }
                tabName.add(tabid);
                if(isHaveTemplateid(tabid)){
                    map.put("organizationName", organizationName);
                    map.put("qrid", qrid);
                    map.put("tabid", tabid);
                    map.put("name", name);
                    map.put("description", description);
                    map.put("detail_description", detail_description);
                    map.put("b0110", b0110);
                    paramList.add(map);
                }
            }
            //获取机构的模板名称
            if (tabName.size() > 0) {
                StringBuffer sql = new StringBuffer("select tabid,name from template_table where tabid in(");
                for (int i = 0; i < tabName.size(); i++) {
                    if (i < tabName.size() - 1) {
                        sql.append("?,");
                    } else {
                        sql.append("?");
                    }
                }
                sql.append(")");
                //AdminCode.getCodeName("UM", b0110);
                this.frowset = dao.search(sql.toString(), tabName);
                //循环遍历将得到的机构名称放入相对应的map中
                while (this.frowset.next()) {
                    String codeitemid = this.frowset.getString("tabid");
                    String tableName = this.frowset.getString("name");
                    for (HashMap<String, String> paramMap : paramList) {
                        String mapCodeitemId = paramMap.get("tabid");
                        if (StringUtils.equals(codeitemid, mapCodeitemId)) {
                            paramMap.put("tabName", tableName);
                        }
                    }
                }
            }
            //是否有添加权限
            this.getFormHM().put("addPriv", addPriv);
            //是否有删除权限
            this.getFormHM().put("deletePriv", deletePriv);
            //是否有清除权限
            this.getFormHM().put("clearPriv", clearPriv);
            //基本信息集
            this.getFormHM().put("qrCardData", paramList);
            //用于新建表单的机构id
            this.getFormHM().put("addOrganizationId", addOrganizationId);
            //用于新建表单的机构名称
            this.getFormHM().put("addOrganizationName", addOrganizationName);
            //用于判断是否拥有调入模板权限
            this.getFormHM().put("PerTransferTablePriv", PerTransferTablePriv);
        } catch (SQLException e) {
            e.printStackTrace();
            this.getFormHM().put("return_code", "fail");
            //数据库表不存在
            this.getFormHM().put("return_msg", "get_table_error");
        }
    }else{
        try {
        	String hrp_logon_url = SystemConfig.getPropertyValue("hrp_logon_url");
            this.frowset = dao.search("select * from t_sys_tipwizard_qrcord where qr_id ="+print_qrid);
            while (this.frowset.next()) {
                HashMap<String, String> map = new HashMap<String, String>();
                qrid = this.frowset.getString("qr_id");
                name = this.frowset.getString("qr_name");
                detail_description = this.frowset.getString("detail_description");
                map.put("qrid", qrid);
                map.put("name", name);
                map.put("detail_description", detail_description);
                this.getFormHM().put("qrCardData", map);
            }
            this.getFormHM().put("hrp_logon_url", hrp_logon_url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   }
    public boolean isHaveTemplateid(String tabid) {
        boolean b = false;
        if(userView.isSuper_admin()){
        	b = true;
        	return b;
        }
        if (userView.isHaveResource(IResourceConstant.RSBD, tabid)) {
            b = true;
        } else if (userView.isHaveResource(IResourceConstant.GZBD, tabid)) {
            b = true;
        } else if (userView.isHaveResource(IResourceConstant.INS_BD, tabid)) {
            b = true;
        } else if (userView.isHaveResource(IResourceConstant.PSORGANS, tabid)) {
            b = true;
        } else if (userView.isHaveResource(IResourceConstant.PSORGANS_FG, tabid)) {
            b = true;
        } else if (userView.isHaveResource(IResourceConstant.PSORGANS_GX, tabid)) {
            b = true;
        } else if (userView.isHaveResource(IResourceConstant.PSORGANS_JCG, tabid)) {
            b = true;
        } else if (userView.isHaveResource(IResourceConstant.ORG_BD, tabid)) {
            b = true;
        } else if (userView.isHaveResource(IResourceConstant.POS_BD, tabid)) {
            b = true;
        }
        return b;
    }
}
