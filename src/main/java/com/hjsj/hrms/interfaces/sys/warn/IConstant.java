/*
 * Created on 2006-5-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.sys.warn;

/**
 * @author zhm
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IConstant {
	public static String Key_JVM_Cache_ConnPool_Name = "JVMWarnConnPoolName";
	public static String Key_JVM_Cache_WarnConfig = "JVMWarnConfigCache";
	public static String Key_Warn_Result_Map = "DynaBeanWarnResultMap";
	
	public static String Key_XmlResul_Freq = "xmlResultFreq";
	public static String Key_XmlResul_Domain = "xmlResultDomain";
	
	public static String Key_BeanName_HrpWarn = "hrpWarnDao";
    
    public static String Key_Request_Param_HashMap = "requestPamaHM";
    public static String Key_Request_Param_IsRole = "isRole";
    public static String Key_Request_Param_OrgCode = "configWarnOrgCode";
    public static String Key_Organization_HashMap = "configOrganizationHashMap";
    public static String Key_Org_Names = "configOrgNames";
    public static String Key_Role_HashMap = "configRoleHashMap";
    public static String Key_Role_List = "configRoleCommonDataList";
    public static String Key_Domain_Names = "configDomainNames";
    public static String Key_FormMap_UserView_Result = "userViewResult";
    public static String Key_HrpWarn_Condition_FieldList = "hrpWarnConditionFieldList";
    
    public static String Key_HrpWarn_Table = "hrpwarn";
    public static String Key_HrpWarn_TableID = "hrpwarn.wid";
    public static String[] Key_HrpWarn_Fields = new String[]{"wid","wname","setid","csource","username","ntype","cmsg","warn_ctrl","valid","b0110","warntype","norder"}; 
    public static String Key_HrpWarn_FieldName_ID = Key_HrpWarn_Fields[0];
    public static String Key_HrpWarn_FieldName_Name = Key_HrpWarn_Fields[1];
    public static String Key_HrpWarn_FieldName_Setid = Key_HrpWarn_Fields[2];
    public static String Key_HrpWarn_FieldName_Msg = Key_HrpWarn_Fields[6];
    public static String Key_HrpWarn_FieldName_CSource = Key_HrpWarn_Fields[3];
    public static String Key_HrpWarn_FieldName_CtrlInf = Key_HrpWarn_Fields[7];
    public static String Key_HrpWarn_FieldName_Valid = Key_HrpWarn_Fields[8];
    public static String Key_HrpWarn_FieldName_Org = Key_HrpWarn_Fields[9];
    public static String Key_HrpWarn_FieldName_Warntyp = Key_HrpWarn_Fields[10];
    public static String Key_HrpWarn_Ctrl_VO = "HrpWarnConfigCtrlVo";
    
	public static String Key_RecorderVoName = "WarnRecorderVoName";
	public static String Key_RecorderVo = "WarnRecorderVo";
	public static String Key_List_Query_FormVo = "WarnListQueryFormVo";
	public static String Key_List_SelectedVo = "WarnListSelectedVo";
	public static String Key_HrpWarn_Template = "Template";
	public static String Key_HrpWarn_Nbase = "Nbase";
	public static String Key_URL_Manage = "/system/warn/config_manager";
	public static String Key_URL_Maintenace = "/system/warn/config_maintenance";
	
	public static String Key_Button_Save = "b_save";
	public static String Key_Button_Add = "b_add";
	public static String Key_Button_Delete = "b_delete";
	public static String Key_Button_Query = "b_query";
	
	public static String Key_Flag = "flag";
    /**
     * 操作标识位，0 update ,1 new add
     */
	public static String Key_Flag_Update = "0";
	public static String Key_Flag_NewAndAdd = "1";
}

