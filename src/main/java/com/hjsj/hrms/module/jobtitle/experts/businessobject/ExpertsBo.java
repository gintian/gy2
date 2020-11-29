package com.hjsj.hrms.module.jobtitle.experts.businessobject;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.personpicker.support.PersonPickerSupport;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
/**
 * 
* <p>Title:ExpertsBo </p>
* <p>Description: 专家操作bo</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Nov 25, 2015 9:24:12 AM
 */
public class ExpertsBo {
    Connection conn;
    ContentDAO dao;
    UserView userview;
    
    public ExpertsBo() {

    }
    public ExpertsBo(Connection conn) {
        this.conn = conn;
    }
    public ExpertsBo(Connection conn, UserView userview) {
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
	    	ArrayList fieldList=DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
			for(int i=0;i<fieldList.size();i++){
				FieldItem item=(FieldItem)fieldList.get(i);	
				String itemid=item.getItemid();//字段id
				String itemtype=item.getItemtype();//字段类型
				String codesetid = item.getCodesetid();//关联的代码			
				String columndesc = item.getItemdesc();//字段描述
				int itemlength=item.getItemlength();//字段长度
				String state=item.getState();//0隐藏  1显示
				String fieldsetid = item.getFieldsetid();
				ColumnsInfo examTime = getColumnsInfo(itemid, columndesc, 150, itemtype, fieldsetid);
				if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
					if("0".equals(codesetid)||codesetid==null){//非代码字符型
						//获得字段描述
						if("w0101".equals(itemid)){
							examTime.setColumnLength(itemlength);
							examTime.setCodesetId("0");
							examTime.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						}else{
							examTime.setColumnLength(itemlength);
							examTime.setCodesetId("0");
							if("0".equals(state)){
								examTime.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
							}
							if("w0103".equals(itemid)||"w0105".equals(itemid)||"w0107".equals(itemid)){
								examTime.setLocked(true);
							}
							examTime.setEditableValidFunc("experts_me.checkCell");
						}
					}else{//代码型字符
						examTime.setColumnLength(itemlength);
						examTime.setCodesetId(codesetid);
						if("0".equals(state)){
							examTime.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						}
						if("w0111".equals(itemid)){//是否外部专家列
							examTime.setEditableValidFunc("false");
							examTime.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
						}
						else{
							examTime.setEditableValidFunc("experts_me.checkCell");
						}
					}
				}
				else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
					examTime.setColumnLength(itemlength);
					examTime.setCodesetId("0");
					if("0".equals(state)){
						examTime.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					}
					examTime.setEditableValidFunc("experts_me.checkCell");
				}
				columnTmp.add(examTime);
			}
			ColumnsInfo b0110 = getColumnsInfo("b0110", "所属机构", 150, "A", "");
			b0110.setCodesetId("UM");
			b0110.setCtrltype("3");
			b0110.setNmodule("9");
			b0110.setCodeSetValid(false);
			b0110.setEditableValidFunc("experts_me.checkCell");
			columnTmp.add(b0110);

			/** 隐藏列 start */
			ColumnsInfo a0100 = getColumnsInfo("a0100", "人员编号", 150, "A", "");
			a0100.setCodesetId("0");
			a0100.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(a0100);
			
			ColumnsInfo nbase = getColumnsInfo("nbase", "人员库前缀", 150, "A", "");
			nbase.setCodesetId("0");
			nbase.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(nbase);
			
			ColumnsInfo changestate = getColumnsInfo("changestate", "", 150, "A", "");
			changestate.setCodesetId("0");
			changestate.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(changestate);

			// 人员库+a0100 加密
			ColumnsInfo nbasea0100 = new ColumnsInfo();
			nbasea0100.setColumnId("nbasea0100");
			nbasea0100.setColumnDesc("nbasea0100");
			nbasea0100.setEncrypted(true);
			nbasea0100.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnTmp.add(nbasea0100);
			/** 隐藏列 end */
			
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
    private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String type, String fieldsetid) {

        ColumnsInfo columnsInfo = new ColumnsInfo();
        columnsInfo.setColumnId(columnId);
        columnsInfo.setColumnDesc(columnDesc);
        //columnsInfo.setCodesetId("");// 指标集
        columnsInfo.setColumnType(type);// 类型N|M|A|D
        columnsInfo.setColumnWidth(columnWidth);// 显示列宽
        if ("A".equals(type)) {
            columnsInfo.setCodesetId("0");
        }
        columnsInfo.setDecimalWidth(0);// 小数位
        columnsInfo.setFieldsetid(fieldsetid);

        // 数值和日期默认居右
        if ("D".equals(type) || "N".equals(type))
            columnsInfo.setTextAlign("right");

        return columnsInfo;
    }
    /**
     * 获取功能按钮
     * @return
     */
	public ArrayList getButtonList() {
		ArrayList buttonList  = new ArrayList();
//		ButtonInfo button = new ButtonInfo();
//		button.setText(ResourceFactory.getProperty("button.export"));
//		button.setFunctype("export");
//		if (userview.isSuper_admin() || userview.hasTheFunction("380020101")){
//			buttonList.add(button);
//		}
		if (userview.isSuper_admin() || userview.hasTheFunction("380020102")){
			buttonList.add(newButton("手工引入", "importExpert", "experts_me.importExperts", null, "true"));
		}
		if (userview.isSuper_admin() || userview.hasTheFunction("380020102")){
			buttonList.add(newButton("条件引入", "importExpertsFilter", "experts_me.importExpertsFilter", null, "true"));
		}
		if (userview.isSuper_admin() || userview.hasTheFunction("380020103")){
			buttonList.add(newButton("新增", null, "experts_me.addExpert", null, "true"));
		}
		if (userview.isSuper_admin() || userview.hasTheFunction("380020104")){
			buttonList.add(newButton("保存", null, "experts_me.saveExpert", null, "true"));
		}
		if (userview.isSuper_admin() || userview.hasTheFunction("380020105")){
			buttonList.add(newButton("撤销", null, "experts_me.cancelExpert", null, "true"));
		}
		if(userview.isSuper_admin()||userview.hasTheFunction("380020109")){
			buttonList.add(newButton("信息同步", null, "experts_me.personInfoSyn", null, "true"));
		}
		if (userview.isSuper_admin() || userview.hasTheFunction("380020106")){
			ButtonInfo imagebutton = newButton("照片", "imageButton", "experts_me.imageExpert", null, "true");
			imagebutton.setParameter("enterflag", "1");
			buttonList.add(imagebutton);
		}
		ButtonInfo queryBox = new ButtonInfo();
		queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
		queryBox.setText("请输入单位名称、部门、姓名");
		queryBox.setFunctionId("ZC00002008");
		buttonList.add(queryBox);
		//buttonList.add(new ButtonInfo("<div id='fastsearch'> </div>"));
		return buttonList;
	}
	
    /**
     * 
     * @Description: 生成按钮
     * 
     * @param text
     *            按钮显示文字
     * @param id
     *            按钮id
     * @param handler
     *            按钮触发方法
     * @param icon
     *            按钮图标
     * @param getdata
     *            事件触发时是否获取选中数据
     * @return
     */
    private ButtonInfo newButton(String text, String id, String handler, String icon, String getdata) {
        ButtonInfo button = new ButtonInfo(text, handler);
        if (getdata != null)
            button.setGetData(Boolean.valueOf(getdata).booleanValue());
        if (icon != null)
            button.setIcon(icon);
        if (id != null)
            button.setId(id);
        return button;
    }
    /**
     * 获得需要查询的sql字段
     * @return
     * @throws GeneralException 
     */
	public String getSelectSql() throws GeneralException {
    	ArrayList fieldList=DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
    	StringBuffer datasql =  new StringBuffer();
		for(int i=0;i<fieldList.size();i++){
			FieldItem item=(FieldItem)fieldList.get(i);		
			String itemid=item.getItemid();//字段id
			datasql.append(" a."+itemid+",");
		}
		String sel = datasql.toString().substring(0,datasql.toString().length()-1);
		String sb = "";
		// 专家库根据人员去认证库和人员库权限的交集（ps:有问题找 chenxc同学）     haosl 2017-07-07  
		List dbnameList = getDbList();
		sb="(select c.* from (";
		for(int j=0;j<dbnameList.size();j++){
			String dbnameString = String.valueOf(dbnameList.get(j));
			sb+=" select '"+dbnameString+"' as nbase,b.A0100,a.b0110,"+sel+",'"+dbnameString+"'"+Sql_switcher.concat()+"b.A0100 as nbasea0100,b.A0000 from w01 a,"+dbnameString+"A01 b where a.guidkey = b.GUIDKEY union all";
		}
		sb+=" select '' as nbase,'' as A0100,a.b0110,"+sel+" ,'' as nbasea0100,0 as A0000 from w01 a where a.w0111='1'";
		sb+=") c) d where 1=1 ";
		//得到登录人的职称管理范围
		String unit = this.userview.getUnitIdByBusi("9");
         if(!"UN`".equals(unit)){
         	String [] unitarr = unit.split("`");
         	sb+=" and ( ";
         	for(int m=0;m<unitarr.length;m++){
         		String arr = unitarr[m];
         		arr = arr.substring(2,arr.length());
         		sb+="d.b0110 like '"+arr+"%' or ";
         		for(int k=arr.length()-1;k>0;k--){
         			arr = arr.substring(0,k);
         			String unname = AdminCode.getCodeName("UN",arr);
         			String umname = AdminCode.getCodeName("UM",arr);
         			if(!"".equals(unname)||!"".equals(umname)){
         				sb+=" d.b0110 ='"+arr+"' or ";
         			}
         		}
         		sb = sb.substring(0,sb.length()-3);
         		sb+=" or ";
         	}
         	sb=sb.substring(0,sb.length()-3);
         	sb+=" or nullif(d.b0110,'') is null)";
         }
		return sb;
	}
	/**
	 * 得到数据字典中w01的列
	 * @return
	 */
	public ArrayList getIdlist() {
		ArrayList list = new ArrayList();
		Set idset = new HashSet();
		ArrayList fieldList=DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
		for(int i=0;i<fieldList.size();i++){
			FieldItem item=(FieldItem)fieldList.get(i);		
			String itemid=item.getItemid();//字段id
			String state=item.getState();//0隐藏  1显示
			if("1".equals(state)){
				idset.add(itemid);
			}else{
				if("w0101".equals(itemid)){
					idset.add(itemid);
				}
			}
		}
		idset.add("b0110");
		list = new ArrayList(idset);
		return list;
	}
	/**
	 * 引入内部专家
	 * @param ids
	 * @param selectedIdList 所选人员的w0101
	 */
	@SuppressWarnings("unchecked")
	public String importExpert(String ids, ArrayList idlist, ArrayList<String> selectedIdList) {
		String msg = "";
		ResultSet ret = null;
		ResultSet ret1 = null;
		try {
		String tablename = "w01";
		if(StringUtils.isBlank(ids))
			return "";
		String[] idsArray = ids.split("'");
		ContentDAO dao=new ContentDAO(this.conn);


		/**获得登陆人的职称管理业务范围   **/
		String unit = this.userview.getUnitIdByBusi("9");
		ArrayList<String> unidId = new ArrayList<String>();

		if (unit != null && !"".equals(unit)) {

			String[] unitarr = unit.split("`");
			for (String arr : unitarr) {
				arr = arr.substring(2, arr.length());
				unidId.add(arr);
			}

		}


		/**得到需要更新的列  */
		HashSet upset=new HashSet();//字典添加的特殊列前缀
		for(int i=0;i<idlist.size();i++){
			String upid = (String)idlist.get(i);//数据字典的列
			FieldItem item = DataDictionary.getFieldItem(upid);
			if(!"W01".equals(item.getFieldsetid())){
				upset.add(item.getFieldsetid());
			}
		}
		Map map = new HashMap();
		for(Iterator t=upset.iterator();t.hasNext();){
			String fup = (String)t.next();
			String upids = "";
			for(int i=0;i<idlist.size();i++){
				String upid = (String)idlist.get(i);//数据字典的列
				FieldItem item = DataDictionary.getFieldItem(upid);
				if(item.getFieldsetid().equals(fup)){
					upids+=upid+",";
				}
			}
			upids = upids.substring(0,upids.length()-1);
			map.put(fup, upids);
		}
		/**导入数据*/
		HashSet set=new HashSet();
		for(String userid:idsArray)
		{
			userid = PubFunc.decrypt(SafeCode.decode(userid));
			String nbase =userid.substring(0, 3);//人员库前缀
			set.add(nbase);
		}
		ArrayList dataList=new ArrayList();
		for(Iterator t=set.iterator();t.hasNext();)
		{
			String dbName=(String)t.next();//前缀
			if(dbName==null||dbName.length()==0)
				continue;
			StringBuffer where=new StringBuffer("");
			ArrayList valueList=new ArrayList();
			String valuearry = "";
			HashMap<String,String> idmap = new HashMap<String,String>();
			for(int k=0; k<idsArray.length;k++)
			{
				String userid = PubFunc.decrypt(SafeCode.decode(idsArray[k]));
				String nbase =userid.substring(0, 3);//人员库前缀
				String a0100 =userid.substring(3);//人员编号
				if(nbase.equalsIgnoreCase(dbName)){
					valueList.add(a0100);
					valuearry+=a0100+",";
					idmap.put(a0100, String.valueOf(k));
				}
			}
			valuearry = valuearry.substring(0,valuearry.length()-1);
			HashMap<String,HashMap<String,Object>> datamaps = new HashMap<String,HashMap<String,Object>>();
			
//			for(int j=0;j<valueList.size();j++){
//				String a0100 = (String)valueList.get(j);
				Set<Object> keySet = map.keySet();
				HashMap<String,String> datamap = new HashMap<String,String>();
				for(Object obj:keySet){
					String[] select = map.get(obj).toString().split(",");
					String selwhereString = "";
					for(String sel:select){
						selwhereString+="b."+sel+",";
					}
					if(obj.toString().startsWith("A")){
						if(obj.toString().startsWith("A01")){
							StringBuffer sb = new StringBuffer();
							sb.append("select a0100,guidkey,");
							sb.append(map.get(obj));
							sb.append(" from "+dbName);
							sb.append(obj.toString());
							sb.append(" where a0100 in (");
							sb.append(valuearry+")");
							ret = dao.search(sb.toString());
							while(ret.next()){
								if(ret.getString("guidkey")==null)
									continue;
								HashMap<String,Object> datamap1 = new HashMap<String,Object>();
								String a0100=ret.getString("a0100");
								String [] colarr = map.get(obj).toString().split(",");
								for(String col:colarr){
									datamap1.put(col, ret.getObject(col)==null?"":ret.getObject(col));
								}
								//datamap1.put("guidkey", ret.getString("guidkey"));
								datamaps.put(a0100+"a", datamap1);
							}
							ret=null;
						}
						else{
							StringBuffer sb = new StringBuffer();
							sb.append("select a.a0100,a.guidkey,");
							sb.append(selwhereString.substring(0,selwhereString.length()-1));
							sb.append(" from "+dbName+"A01 a left join "+dbName);
							sb.append(obj.toString()+" b ");
							sb.append("on a.A0100=b.A0100 where a.A0100 in (");
							sb.append(valuearry+") and b.I9999=(select MAX(b7.I9999) from "+dbName);
							sb.append(obj.toString()+" b7 where a.A0100=b7.A0100)");
							ret = dao.search(sb.toString());
							while(ret.next()){
								if(ret.getString("guidkey")==null)
									continue;
								HashMap<String,Object> datamap1 = new HashMap<String,Object>();
								String a0100=ret.getString("a0100");
								String flag = idmap.get(a0100);
								String [] colarr = map.get(obj).toString().split(",");
								for(String col:colarr){
									datamap1.put(col, ret.getObject(col)==null?"":ret.getObject(col));
								}
								//datamap1.put("guidkey", ret.getString("guidkey"));
								
								datamaps.put(a0100+obj.toString(), datamap1);
							}
							ret=null;
						}
					}
					if(obj.toString().startsWith("B")){//单位
						if(obj.toString().startsWith("B01")){
							StringBuffer sb = new StringBuffer();
							sb.append("select a.a0100,a.guidkey,");
							sb.append(selwhereString.substring(0,selwhereString.length()-1));
							sb.append(" from "+dbName+"A01 a left join ");
							sb.append(obj.toString());
							sb.append(" b on a.B0110=b.B0110 where a0100 in(");
							sb.append(valuearry+")");
							ret = dao.search(sb.toString());
							while(ret.next()){
								if(ret.getString("guidkey")==null)
									continue;
								HashMap<String,Object> datamap1 = new HashMap<String,Object>();
								String a0100=ret.getString("a0100");
								String [] colarr = map.get(obj).toString().split(",");
								for(String col:colarr){
									datamap1.put(col, ret.getObject(col)==null?"":ret.getObject(col));
								}
								//datamap1.put("guidkey", ret.getString("guidkey"));
								datamaps.put(a0100+"b", datamap1);
							}
							ret=null;
						}
						else{
							StringBuffer sb = new StringBuffer();
							sb.append("select a.a0100,a.guidkey,");
							sb.append(selwhereString.substring(0,selwhereString.length()-1));
							sb.append(" from "+dbName+"A01 a left join ");
							sb.append(obj.toString()+" b ");
							sb.append("on a.B0110=b.B0110 where a.A0100 in(");
							sb.append(valuearry+") and b.I9999=(select MAX(b7.I9999) from ");
							sb.append(obj.toString()+" b7 where a.B0110=b7.B0110)");
							ret = dao.search(sb.toString());
							while(ret.next()){
								if(ret.getString("guidkey")==null)
									continue;
								HashMap<String,Object> datamap1 = new HashMap<String,Object>();
								String a0100=ret.getString("a0100");
								String [] colarr = map.get(obj).toString().split(",");
								for(String col:colarr){
									datamap1.put(col, ret.getObject(col)==null?"":ret.getObject(col));
								}
								//datamap1.put("guidkey", ret.getString("guidkey"));
								datamaps.put(a0100+obj.toString(), datamap1);
							}
							ret=null;
						}
					}
					if(obj.toString().startsWith("K")){//岗位
						if(obj.toString().startsWith("K01")){
							StringBuffer sb = new StringBuffer();
							sb.append("select a.a0100,a.guidkey,");
							sb.append(selwhereString.substring(0,selwhereString.length()-1));
							sb.append(" from "+dbName+"A01 a left join ");
							sb.append(obj.toString());
							sb.append(" b on a.E01A1=b.E01A1 where a0100 in(");
							sb.append(valuearry+")");
							ret = dao.search(sb.toString());
							while(ret.next()){
								if(ret.getString("guidkey")==null)
									continue;
								HashMap<String,Object> datamap1 = new HashMap<String,Object>();
								String a0100=ret.getString("a0100");
								String [] colarr = map.get(obj).toString().split(",");
								for(String col:colarr){
									datamap1.put(col, ret.getObject(col)==null?"":ret.getObject(col));
								}
								//datamap1.put("guidkey", ret.getString("guidkey"));
								datamaps.put(a0100+"k", datamap1);
							}
							ret=null;
						}
						else{
							StringBuffer sb = new StringBuffer();
							sb.append("select a.a0100,a.guidkey,");
							sb.append(selwhereString.substring(0,selwhereString.length()-1));
							sb.append(" from "+dbName+"A01 a left join ");
							sb.append(obj.toString()+" b ");
							sb.append("on a.E01A1=b.E01A1 where a.A0100 in(");
							sb.append(valuearry+") and b.I9999=(select MAX(b7.I9999) from ");
							sb.append(obj.toString()+" b7 where a.E01A1=b7.E01A1)");
							ret = dao.search(sb.toString());
							while(ret.next()){
								if(ret.getString("guidkey")==null)
									continue;
								HashMap<String,Object> datamap1 = new HashMap<String,Object>();
								String a0100=ret.getString("a0100");
								String [] colarr = map.get(obj).toString().split(",");
								for(String col:colarr){
									datamap1.put(col, ret.getObject(col)==null?"":ret.getObject(col));
								}
								//datamap1.put("guidkey", ret.getString("guidkey"));
								datamaps.put(a0100+obj.toString(), datamap1);
							}
							ret=null;
						}
					}
				}
				//查询三个固定字段w0103,w0105,w0107和配置的电子邮箱，电话号码
				StringBuffer sb = new StringBuffer();
				String emailId = ConstantParamter.getEmailField().toLowerCase();//电子邮箱
				String phone = ConstantParamter.getMobilePhoneField().toLowerCase();//电话号码
				sb.append("select a0100,guidkey,");
				sb.append("(select codeitemdesc from organization where codeitemid=");
				sb.append(dbName);
				sb.append("A01.E0122) as w0105 ,");
				sb.append("(select codeitemdesc from organization where codeitemid=");
				sb.append(dbName);
				sb.append("A01.B0110) as w0103,");
				sb.append("A0101 as w0107 ");
				if (StringUtils.isNotEmpty(emailId))					
					sb.append(","+ emailId + " as w0113 ");
				if (StringUtils.isNotEmpty(phone))
					sb.append(","+ phone + " as w0115 ");
				sb.append(" from ");
				sb.append(dbName);
				sb.append("A01 where A0100 in(");
				sb.append(valuearry+")");
				ret1 = dao.search(sb.toString());
				int num = 0;
				while(ret1.next()){
					if(ret1.getString("guidkey")==null){
						num++;
						if(num<=3){
							msg+=ret1.getString("w0107")+"、";
						}
						continue;
					}
					HashMap<String,Object> datamap1 = new HashMap<String,Object>();
					datamap1.put("w0103",ret1.getString("w0103"));
					datamap1.put("w0105",ret1.getString("w0105"));
					datamap1.put("w0107",ret1.getString("w0107"));
					if (StringUtils.isNotEmpty(emailId))					
						datamap1.put("w0113",ret1.getString("w0113"));
					if (StringUtils.isNotEmpty(phone))
						datamap1.put("w0115",ret1.getString("w0115"));
					datamap1.put("guidkey", ret1.getString("guidkey"));
					datamaps.put(ret1.getString("a0100"), datamap1);
				}
				if(num>0){
					msg=msg.substring(0,msg.length()-1)+ "等"+ num +"人GUIDKEY为空，请联系管理员维护人员信息！";
				}
//				datamaps.put(a0100, datamap);
				
				//将数据放入vo
				Set<String> keySets = datamaps.keySet();
				//Map datamap = new HashMap();
				ArrayList<RecordVo> list = new ArrayList<RecordVo>();
					
//					ArrayList plist = new ArrayList();
				int j = 0;
				boolean isAdd=true;
				String message = "";
					for(int i=0;i<valueList.size();i++){//循环引入的人员ID
						isAdd=true;
						RecordVo resultVo = new RecordVo("w01");
						String personid = (String)valueList.get(i);
						for(String objs:keySets){
							if(objs.contains(personid)){//某一个ID相关
								HashMap<String,Object> dmap = datamaps.get(objs);
								Set<String> keyset = dmap.keySet();
								for(String obj:keyset){
									resultVo.setObject(obj, dmap.get(obj));
								}
							}
						}
						String guidkey = resultVo.getString("guidkey");
						if(StringUtils.isEmpty(guidkey))
							continue;
						String sql = "select * from w01 where guidkey='"+guidkey+"' and w0111='2'";
						ret=dao.search(sql);
						while(ret.next()){
							isAdd=false;
							boolean isHaveFun=false;
							//仅引入具有所属部门权限的 和所属部门为空的
							String b0110=ret.getString("b0110");
							if(StringUtils.isBlank(b0110)|| "un`".equalsIgnoreCase(unit)) {
								selectedIdList.add(ret.getString("w0101"));
								isHaveFun=true;
							}
							else {
								for (String str : unidId) {
									if (str.length() <= b0110.length() && b0110.startsWith(str)) {
										selectedIdList.add(ret.getString("w0101"));
										isHaveFun=true;
										break;
									}
								}
							}
							if(!isHaveFun) {
								j++;
								if (j <= 3) {
									String name = ret.getString("W0107");
									if (j == 1)
										message += name;
									else
										message += "、" + name;
								}
							}
						}
						if(isAdd) {
							IDGenerator idg = new IDGenerator(2, this.conn);
							String w0101 = idg.getId("W01.W0101");
							resultVo.setString("w0101", String.valueOf(Integer.valueOf(w0101)));
							resultVo.setString("w0109", "1");
							resultVo.setString("w0111", "2");
							resultVo.setDate("create_time", new Date());
							resultVo.setString("create_user", this.userview.getUserName());
							resultVo.setString("create_fullname", this.userview.getUserFullName());
							String b0110 = this.userview.getUnitIdByBusi("9");
							if ("UN`".equals(b0110)) {
								resultVo.setString("b0110", "");
							} else {
								int index = b0110.indexOf("`");
								b0110 = b0110.substring(2, index);
								resultVo.setString("b0110", b0110);
							}

							selectedIdList.add(String.valueOf(Integer.valueOf(w0101)));
//							resultVo.setString("b0110", this.userview.getUnitIdByBusi("9"));
							list.add(resultVo);
						}
						//}
				}
			    dao.addValueObject(list);
				if(j>0){
					if(j>3)
						message += "等人";
					//message="没有 "+message;
					message += " 已经被其他机构纳入专家库，您无法选择！";
					return message;
				}

		}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally{
            PubFunc.closeDbObj(ret);
            PubFunc.closeDbObj(ret1);
        }
		return msg;
	}
	/**
	 * 取得复杂查询下拉中的字段
	 * @return
	 */
	public ArrayList getFieldsArray() {
		ArrayList fieldsArray = new ArrayList();
		ArrayList fieldList=DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
		for(int i=0;i<fieldList.size();i++){
			FieldItem item=(FieldItem)fieldList.get(i);	
			HashMap map = new HashMap();
			String itemid=item.getItemid();//字段id
			String itemtype=item.getItemtype();//字段类型
			String codesetid = item.getCodesetid();//关联的代码			
			String columndesc = item.getItemdesc();//字段描述
			String state=item.getState();//0隐藏  1显示
			if("1".equals(state)){
				map.put("type", itemtype);
				map.put("itemid", itemid.toUpperCase());
				map.put("itemdesc", columndesc);
				map.put("codesetid", codesetid);
				map.put("formate", "Y-m-d H:i:s");
				fieldsArray.add(map);
			}
		}
		HashMap map1 = new HashMap();
		map1.put("type", "A");
		map1.put("itemid", "b0110");
		map1.put("itemdesc", "所属机构");
		map1.put("codesetid", "UM");
		fieldsArray.add(map1);
		return fieldsArray;
	}
	/**
	 * 获得部门下拉描述
	 * @param itemid
	 * @return
	 */
	public String getItemDesc(String itemid) {
		ResultSet ret = null;
		ContentDAO dao=new ContentDAO(this.conn);
		String codeitemdesc = "";
		String sql = " select codeitemdesc from organization where codeitemid='"+itemid+"'";
		try {
			ret = dao.search(sql);
			while(ret.next()){
				codeitemdesc = ret.getString("codeitemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return codeitemdesc;
	}
	
	/**
	 * 
	 * @Title: getInformation   
	 * @Description: 获取w01 和指标在主集对应的信息
	 * @param @return
	 * @param @throws SQLException 
	 * @return String    
	 * @throws
	 * @author changxy
	 */
	public String  getInformation() throws SQLException{
		String status="";
		String org="";  //changxy 20160812 添加同步人员权限 不可同步上级
		String unit=userview.getUnitIdByBusi("9");//获取登录人权限
		ContentDAO dao=new ContentDAO(this.conn);
		if (unit != null || !"".equals("")) {
			if ("UN`".equals(unit)) {// 全部范围
				org = "";
			} else {
				String[] unitarr = unit.split("`");
				for (String arr : unitarr) {
					arr = arr.substring(2, arr.length());
					org += arr + ",";
				}
				org = org.substring(0, org.length() - 1);
			}
		}
		
		ArrayList fieldList=DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
		ArrayList dbList = userview.getPrivDbList();
		String[] Nbase=new String[dbList.size()];//应用库
		for(int i=0;i<dbList.size();i++){
			Nbase[i] = (String)dbList.get(i);
		}
		String  phone=PersonPickerSupport.getPhoneField(this.conn);//短信表名称
		String  email=PersonPickerSupport.getEmailField(this.conn);//email表名称
		ArrayList Strlist=new ArrayList();
		StringBuffer sbf=new StringBuffer();
		RowSet Arow=null;
		RowSet Wrow=null;
		RowSet rs = null;
		if(Nbase.length>0){
			//haosl 20160926 start 兼容首体专家信息同步失败问题（不同人员库的指标顺序不同，所以才用拼接方式来同意指标的顺序，这样union all的时候才不会报错）
			String queryColum = "";
			String sql = "select * from " + Nbase[0] + "A01 where 1=2";
			rs = dao.search(sql);
			ResultSetMetaData metaData =rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnItem = metaData.getColumnName(i).toLowerCase();
				if(i==1)
					queryColum+=columnItem;
				else
					queryColum+=","+columnItem;
				
			 }
			//haosl 20160926 end
			try {
				/**
				 * w01固定列查询与修改 单位 部门 姓名 email phone
				 */
				RecordVo vo = new RecordVo(Nbase[0] + "A01");
				ArrayList A01List = vo.getModelAttrs();
				sbf.append("select ");
				sbf.append("a.w0105 ,");
				sbf.append("a.w0103 ,");
				sbf.append("a.A0101 W0107 ,");
				sbf.append("a.guidkey ");
				if (email != null && !StringUtils.isEmpty(email))// haosl
																	// 20160926
																	// 优化
					sbf.append(",a." + email + " W0113 ");
				else
					sbf.append(",'' W0113 ");

				if (phone != null && !StringUtils.isEmpty(phone))
					sbf.append(",a." + phone + " W0115 ");
				else
					sbf.append(",'' W0115 ");
				sbf.append(" from (");
				for (int i = 0; i < Nbase.length; i++) {
					sbf.append("select orgM.codeitemdesc w0105,A011.* from ( ");
					sbf.append(" select org.codeitemdesc w0103, A01.* from ");
					sbf.append(" (select " + queryColum + " from ");
					sbf.append(Nbase[i] + "A01 ");
					sbf.append(" where GUIDKEY in  (select guidkey from w01))A01 left join organization org on ");
					sbf.append(" A01.B0110=org.codeitemid and org.codesetid='UN') A011 left join organization orgM on A011.E0122=orgM.codeitemid and orgM.codesetid='UM' ");
					if (i < (Nbase.length - 1))
						sbf.append(" union all ");
				}
				sbf.append(" )a  ");
				Arow = dao.search(sbf.toString());
				String b0110Final = Sql_switcher.isnull("b0110", "0");// haosl  20160920  兼容 Oracle 查询下级人员
				Wrow = dao.search("select w0105 ,w0103 ,W0107 ,guidkey ,W0113, W0115 from w01 where " + b0110Final + " like '" + org + "%'");
				ArrayList Wlist = new ArrayList();
				ArrayList Alist = new ArrayList();
				while (Wrow.next()) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("w0105", Wrow.getString("w0105"));
					map.put("w0103", Wrow.getString("w0103"));
					map.put("W0107", Wrow.getString("W0107"));
					map.put("guidkey", Wrow.getString("guidkey"));
					map.put("W0113", Wrow.getString("W0113"));
					map.put("W0115", Wrow.getString("W0115"));
					Wlist.add(map);
				}
				while (Arow.next()) {
					String Aw0105 = Arow.getString("w0105") == null ? "" : Arow.getString("w0105");
					String Aw0103 = Arow.getString("w0103") == null ? "" : Arow.getString("w0103");
					String AW0107 = Arow.getString("W0107") == null ? "" : Arow.getString("W0107");
					String Aguidkey = Arow.getString("guidkey") == null ? "" : Arow.getString("guidkey");
					String AW0113 = Arow.getString("W0113") == null ? "" : Arow.getString("W0113");
					String AW0115 = Arow.getString("W0115") == null ? "" : Arow.getString("W0115");
					for (int i = 0; i < Wlist.size(); i++) {
						HashMap<String, String> map = (HashMap<String, String>) Wlist.get(i);
						HashMap<String, String> Amap = new HashMap<String, String>();
						if (Aguidkey.equals(map.get("guidkey"))) {
							if (!(Aw0105.equals(map.get("w0105")) && Aw0103.equals(map.get("w0103")) && AW0107.equals(map.get("W0107")) && AW0113.equals(map.get("W0113")) && AW0115.equals(map.get("W0115")))) {
								Amap.put("guidkey", Aguidkey);
								Amap.put("w0105", Aw0105);
								Amap.put("w0103", Aw0103);
								Amap.put("W0107", AW0107);
								Amap.put("W0113", AW0113);
								Amap.put("W0115", AW0115);
								Alist.add(Amap);
							}
						}
					}

				}
				if (Alist.size() > 0) {
					for (int i = 0; i < Alist.size(); i++) {
						HashMap<String, String> map = (HashMap<String, String>) Alist.get(i);
						String guidkey = map.get("guidkey");
						String w0105 = map.get("w0105");
						String w0103 = map.get("w0103");
						String W0107 = map.get("W0107");
						String W0113 = map.get("W0113");
						String W0115 = map.get("W0115");
						dao.update("update W01 set w0103='" + w0103 + "',w0105='" + w0105 + "',W0107='" + W0107 + "',W0113='" + W0113 + "',W0115='" + W0115 + "' where GUIDKEY='" + guidkey + "' ");
						// System.out.println(w0105+"<><"+w0103+"<><>"+W0107+"<><>"+W0113+"<><>"+W0115+"<><>"+guidkey);
					}
				}

				for (int j = 0; j < fieldList.size(); j++) {// 专家库结构
					FieldItem item = (FieldItem) fieldList.get(j);
					String itemid = item.getItemid();// 字段id
					if (!(itemid == null || itemid == "" || "w".equalsIgnoreCase(itemid.substring(0, 1)) || "b0110".equalsIgnoreCase(itemid) || "GUIDKEY".equalsIgnoreCase(itemid) || "create_time".equalsIgnoreCase(itemid) || "create_fullname".equalsIgnoreCase(itemid) || "create_user".equalsIgnoreCase(itemid) || "modify_time".equalsIgnoreCase(itemid))) {
						String Asql = getSql(Nbase, itemid);// 获取w01在主集或子集对应的指标信息sql
						if(StringUtils.isNotBlank(Asql)) {
							String WSql = " select GUIDKEY," + itemid + " from w01 where " + b0110Final + " like '" + org + "%'";// haosl 20160920 兼容 Oracle 查询下级人员
							UpdateInfo(Asql, WSql, itemid);
						}
					}
				}

				status = "1";

			} catch (Exception e) {
				status = "0";
				e.printStackTrace();
			} finally {
				PubFunc.closeDbObj(Arow);
				PubFunc.closeDbObj(Wrow);
				PubFunc.closeDbObj(rs);
			}
		}
		return status;
	}
	

	/**   
	 * 
	 * @Title: UpdateInfo   
	 * @Description: Asql 主集或子集查询对应内容sql 
	 * WSql w01 查询导入指标的内容   
	 * @param @param Asql 
	 * @param @param WSql
	 * @param @param itemid
	 * @param @throws SQLException 
	 * @return void    
	 * @throws   
	*/
	public void UpdateInfo(String Asql,String WSql,String itemid)throws SQLException{
		RowSet Arow=null;
		RowSet Wrow=null;
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list=new ArrayList();
		FieldItem item = DataDictionary.getFieldItem(itemid, "W01");
		try {
			Arow=dao.search(Asql);
			Wrow=dao.search(WSql);
			while(Wrow.next()){
				HashMap map=new HashMap();
				map.put("GUIDKEY", Wrow.getString("GUIDKEY")==null?"":Wrow.getString("GUIDKEY"));
				if("D".equals(item.getItemtype()))
					map.put(itemid, Wrow.getDate(itemid)==null?"":String.valueOf(Wrow.getDate(itemid)));
				else
					map.put(itemid, Wrow.getString(itemid)==null?"":Wrow.getString(itemid));
				list.add(map);
			}
			while(Arow.next()){
				String GUIDKEY=Arow.getString("GUIDKEY");
				String itemidfield = "";
				if("D".equals(item.getItemtype()))
					itemidfield=Arow.getDate(itemid)==null?"":String.valueOf(Arow.getDate(itemid));//主集参数名
				else 
					itemidfield = Arow.getString(itemid)==null?"":Arow.getString(itemid);//主集参数名
				for (int l = 0; l < list.size(); l++) {
				HashMap wmap=(HashMap)list.get(l);
				if((GUIDKEY.equalsIgnoreCase(wmap.get("GUIDKEY").toString()))){
					if(!itemidfield.equalsIgnoreCase(wmap.get(itemid).toString())){
//						Object value = itemidfield;
//						if("N".equals(item.getItemtype())){
//							if("".equals(itemidfield)){
//								value = null;
//							}
//						}
//						if("D".equals(item.getItemtype())){
//							value = java.sql.Date.valueOf(itemidfield);
//						}
						dao.update("update w01 set "+itemid+"='"+itemidfield+"' where GUIDKEY='"+GUIDKEY+"' ");
					}
				 }
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			Arow.close();
			Wrow.close();
		}
		
		
	}
	/***
	 * 
	 * @Title: getSql   
	 * @Description:根据w01导入的指标 获取主集或者子集查询对应指标信息sql   
	 * @param @param Nbase
	 * @param @param fielditem
	 * @param @return
	 * @param @throws GeneralException 
	 * @return String    
	 * @throws
	 * @author changxy 
	 * 
	 */
	public String getSql(String[] Nbase, String fielditem) throws GeneralException {

		StringBuffer sbf = new StringBuffer();
		RowSet row = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String fieldsetid = null;
			if("b0110".equalsIgnoreCase(fielditem) || "e01a1".equalsIgnoreCase(fielditem)){
				fieldsetid = "A01";
			}else {
				String sql = "select fieldsetid from fielditem where lower(itemid)='" + StringUtils.lowerCase(fielditem) + "'";
				
				row = dao.search(sql);
				while (row.next()) {
					fieldsetid = row.getString("fieldsetid");
				}
			}
			//fieldsetid 如果不在指标集里面会报错的  haosl 2018年4月2日
			if(StringUtils.isBlank(fieldsetid)) {
				return null;
			}
			for (int i = 0; StringUtils.isNotBlank(fieldsetid) && i < Nbase.length; i++) {
				if ("A".equalsIgnoreCase(fieldsetid.substring(0, 1))) {// 人员信息集

					if ("A01".equalsIgnoreCase(fieldsetid.substring(0, 3))) {// 人员信息主集
						sbf.append("select GUIDKEY," + fielditem + " from " + Nbase[i] + fieldsetid + " where  GUIDKEY in  (select guidkey from w01)");

					} else {// 人员信息子集
						sbf.append(" select GUIDKEY," + fielditem + " from ");
						sbf.append(" (select d.GUIDKEY,c." + fielditem + " from  ");
						sbf.append(" (select b.A0100,b." + fielditem + " from ");
						sbf.append(" (select A0100,MAX(I9999)I9999 from " + Nbase[i] + fieldsetid + " group by A0100)a ");
						sbf.append(" left join " + Nbase[i] + fieldsetid + " b on a.A0100=b.A0100 and a.I9999=b.I9999)c left join " + Nbase[i] + "A01 d ");
						sbf.append(" on c.A0100=d.A0100) e where e.GUIDKEY in (select GUIDKEY from w01) ");
					}
				} else if ("B".equalsIgnoreCase(fieldsetid.substring(0, 1))) {//

					if ("B01".equalsIgnoreCase(fieldsetid.substring(0, 3))) {// 主集
						sbf.append(" select GUIDKEY," + fielditem + " from ");
						sbf.append(" (select GUIDKEY,B0110 from " + Nbase[i] + "A01 where GUIDKEY in (select GUIDKEY from w01)) b left join ");
						sbf.append(fieldsetid + " a  on a.E01A1=b.E01A1 ");
					} else {// 子集
						sbf.append(" select e.guidkey,f." + fielditem + " from ");
						sbf.append(" (select d.guidkey,Max(I9999) I9999 from ");
						sbf.append(" (select a.guidkey,a.b0110,b." + fielditem + ",b.I9999 from ");
						sbf.append(" (select GUIDKEY,b0110 from " + Nbase[i] + "A01 where GUIDKEY in (select GUIDKEY from w01))a left join  " + fieldsetid + " b ");
						sbf.append(" on a.b0110=b.b0110) d group by d.guidkey) e left join ");
						sbf.append(" (select a.guidkey,a.b0110,b." + fielditem + ",b.I9999 from ");
						sbf.append(" (select GUIDKEY,b0110 from " + Nbase[i] + "A01 where GUIDKEY in (select GUIDKEY from w01))a left join  " + fieldsetid + " b ");
						sbf.append(" on a.b0110=b.b0110) f on e.guidkey=f.guidkey and e.I9999=f.I9999 ");
					}

				} else if ("K".equalsIgnoreCase(fieldsetid.substring(0, 1))) {//
					if ("K01".equalsIgnoreCase(fieldsetid.substring(0, 3))) {// 主集
						sbf.append(" select GUIDKEY," + fielditem + " from ");
						sbf.append(" (select GUIDKEY,E01A1 from " + Nbase[i] + "A01 where GUIDKEY in (select GUIDKEY from w01)) b left join ");
						sbf.append(fieldsetid + " a  on a.E01A1=b.E01A1 ");
					} else {// 子集
						sbf.append("select e.guidkey,f." + fielditem + " from ");
						sbf.append(" (select d.guidkey,Max(I9999) I9999 from ");
						sbf.append(" (select a.guidkey,a.E01A1,b." + fielditem + ",b.I9999 from  ");
						sbf.append(" (select GUIDKEY,E01A1 from " + Nbase[i] + "A01 where GUIDKEY in (select GUIDKEY from w01))a left join " + fieldsetid + " b ");
						sbf.append(" on a.E01A1=b.E01A1) d group by d.guidkey) e left join ");
						sbf.append(" (select a.guidkey,a.E01A1,b." + fielditem + ",b.I9999 from ");
						sbf.append(" (select GUIDKEY,E01A1 from " + Nbase[i] + "A01 where GUIDKEY in (select GUIDKEY from w01))a left join " + fieldsetid + " b ");
						sbf.append(" on a.E01A1=b.E01A1) f on e.guidkey=f.guidkey and e.I9999=f.I9999 ");
					}

				}

				if (i < Nbase.length - 1) {
					sbf.append(" union all ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(row);
		}
		return sbf.toString();
	}
	
	/**
	 * 修改专家可聘任标识时，同步修改学科组和专家库的可聘任标识
	 * @param w0101
	 * 			专家编号
	 * @param flag
	 * 			专家可聘任标识
	 * @throws GeneralException
	 */
	public void syscExpertFlag(String w0101,String flag) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String judgingpanelSql = "update zc_judgingpanel_experts set flag = ? where w0101= ? ";
			String subjectgroupSql = "update zc_subjectgroup_experts set flag = ? where expertid= ? ";
			List<String> values = new ArrayList<String>();
			values.add(flag);
			values.add(w0101);
			dao.update(judgingpanelSql, values);
			dao.update(subjectgroupSql, values);
			
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			
		}
	}
	/**
	 * 获得人员库权限列表与认证库的交集
	 * @return
	 * @throws GeneralException
	 */
	 private ArrayList getDbList() throws GeneralException {
	        ArrayList nbaseList = new ArrayList();
	        //获得人员库 haosl 2017-07-07
	       List privDb = this.userview.getPrivDbList();
	        DbNameBo dbNameBo = new DbNameBo(this.conn);
	        ArrayList dblist = dbNameBo.getAllLoginDbNameList();//所有认证人员库
	        if (dblist == null || dblist.size() == 0 
	        		|| privDb == null || privDb.size() == 0)
	           return nbaseList;
	        CommonData da = null;
	        for (int i = 0; i < dblist.size(); i++) {
	            RecordVo vo = (RecordVo) dblist.get(i);
	            da = new CommonData();
	            String per = vo.getString("pre");

	            if(!privDb.contains(per))
	            	continue;
	            
	            nbaseList.add(per);
	        }
	        return nbaseList;
	    }

}
