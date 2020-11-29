package com.hjsj.hrms.module.recruitment.baseoptions.transaction;

import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;


public class SearchResourceTrans extends IBusiness {
	private String flag;
    private HashMap<String, HashMap<String, String>> fieldNames = new HashMap<String, HashMap<String,String>>();
    /** 根据传过的的指标串，分解成对应的指标对象 */
    private ArrayList splitField(String strfields) throws GeneralException {
        ArrayList list = new ArrayList();
        if (!",".equals(strfields.substring(strfields.length())))
            strfields = strfields + ",";
        
        StringTokenizer st = new StringTokenizer(strfields, ",");
        HashMap map = new HashMap();
        String str = "";
        while (st.hasMoreTokens()) {
            String fieldname = st.nextToken();
            String fieldSetName = "";
            if (3 <= fieldname.length()) 
                fieldSetName = fieldname.substring(0, 3);
            
            map.put(fieldSetName, fieldname);
            str += "'" + fieldSetName + "',";
        }
        
        if (str.length() > 0) 
            str = str.substring(0, str.length() - 1);
        else
            str = "''";
        
        String sql = " select * from fieldSet where fieldSetId in(" + str + ") order by Displayorder";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {// 为了子集顺序与库结构中的顺序一致
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                if (map.containsKey(this.frowset.getString("fieldSetid"))) 
                    list.add(map.get(this.frowset.getString("fieldSetid")));
                
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return list;
    }

    /**
     * 生成选库前台界面
     * 
     * @return
     * @throws GeneralException
     */
    private String searchDbNameHtml() throws GeneralException {
        StringBuffer db_str = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        strsql.append("select dbid,dbname,pre from dbname order by dbid");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(strsql.toString());

            db_str.append("<div align='center'>");
            while (this.frowset.next()) {
                db_str.append("<div id='");
                db_str.append("div");
                db_str.append(this.frowset.getString("dbid"));
                db_str.append("'>");
                db_str.append("<span style='text-align:left;'>");
                db_str.append("<input type='radio' name='func' value='");
                db_str.append(this.frowset.getString("pre"));
                db_str.append("' id='input");
                db_str.append(this.frowset.getString("dbid"));
                db_str.append("','input");
                db_str.append(this.frowset.getString("dbid"));
                db_str.append("'");
                RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
                if (vo != null) {
                    String dbpre = vo.getString("str_value");
                    if (dbpre.equals(this.frowset.getString("pre")))
                        db_str.append(" checked");
                    
                }
                
                db_str.append(">");
                db_str.append(this.frowset.getString("dbname"));
                db_str.append("</span>");
                db_str.append("</div>");
            }
            
            db_str.append("</div>");
            return db_str.toString();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
    }

    /**
     * 增加表头
     * 
     * @return
     */
    private String addTableHeader(String label, String fieldsetid) {
        StringBuffer str_header = new StringBuffer();
        str_header.append("<table width='90%' id = 'tab' border='0' cellpadding='0' cellspacing='0' style='border-collapse:collapse;margin:auto'>");
        str_header.append("<tr>");
        str_header.append("<th align='center' style='height:30px;border:1px solid #c5c5c5;' width='20%' bgcolor='#f0f0f0' nowrap>");
        str_header.append(ResourceFactory.getProperty(label));
        str_header.append("</th>");
        str_header.append("<th align='center' style='height:30px;border:1px solid #c5c5c5;' width='20%' bgcolor='#f0f0f0' nowrap>");
        str_header.append("显示名称");
        str_header.append("</th>");
        if (label.equals(GeneralConstant.Field_LABLE)) {
            str_header.append("<th align='center' style='height:30px;border:1px solid #c5c5c5;' width='15%' nowrap bgcolor='#f0f0f0'>");
            str_header.append("有效指标");
        } else {
            str_header.append("<th align='center' style='height:30px;border:1px solid #c5c5c5;' width='30%' nowrap bgcolor='#f0f0f0'>");
            str_header.append(ResourceFactory.getProperty("lable.zp_plan.zp_object0"));
        }
        
        str_header.append("</th>");
        if (label.equals(GeneralConstant.Field_LABLE)) {
            // 前台显示指标
            if (!"a01".equalsIgnoreCase(fieldsetid)) {
                str_header.append("<th align='center' style='height:30px;border:1px solid #c5c5c5;' width='15%' nowrap bgcolor='#f0f0f0'>");
                str_header.append("前台子集列表显示指标");
                str_header.append("</th>");
            }

            // 必填项
            str_header.append("<th align='center' style='height:30px;border:1px solid #c5c5c5;' width='15%' nowrap bgcolor='#f0f0f0'>");
            str_header.append("必填指标");
            str_header.append("</th>");
            if ("a01".equalsIgnoreCase(fieldsetid)) {
                str_header.append("<th align='center' style='height:30px;border:1px solid #c5c5c5;' width='15%' nowrap bgcolor='#f0f0f0'>");
                str_header.append("唯一性校验指标");
                str_header.append("</th>");
            } else {
                str_header.append("<td align='center' width='15%' style='border:none;' nowrap>");
                str_header.append("</td>");
            }

            if ("a01".equalsIgnoreCase(fieldsetid)) {
                str_header.append("<td align='center' width='15%' style='border:none;' nowrap>");
                str_header.append("</td>");
            }
        } else {
            str_header.append("<th align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap bgcolor='#f0f0f0'>");
            str_header.append(ResourceFactory.getProperty("lable.zp_plan.zp_object1"));
            str_header.append("</th>");
        }
        
        str_header.append("</tr>");
        return str_header.toString();
    }

    /**
     * 增加表尾
     * 
     * @return
     */
    private String addTableFoot() {
        StringBuffer str_footer = new StringBuffer();
        str_footer.append("</table>");
        return str_footer.toString();
    }

    /**
     * 指标权限
     * 
     * @param setid
     * @param setdesc
     * @param flag
     * @return
     */
    private String addTableRow(String setid, String setdesc, HashMap fieldMap, FieldItem item, HashMap<String, String> fieldName) {
        StringBuffer str_row = new StringBuffer();
        str_row.append("<tr>");
        str_row.append("<td align='right' style='height:30px;border:1px solid #c5c5c5; padding-right:5px;' nowrap>");
        str_row.append("<input name='itemName' type='hidden' value='");
        str_row.append(setdesc+"'/>");
        str_row.append(setdesc);
        str_row.append("</td>");
        
        String[] temp = setid.split("\\.");
        str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
        str_row.append("<input type='text' name='itemDisplayName' maxlength='20'");
        str_row.append(" value='");
        if(fieldName != null && fieldName.containsKey(item.getItemid()))
            str_row.append(fieldName.get(item.getItemid()));
        else
            str_row.append(setdesc);
        
        str_row.append("' id='" + setid + "'");
        if (fieldMap == null || fieldMap.get(temp[1].toLowerCase()) == null)
            str_row.append("' disabled='disabled'");
        
        str_row.append("/></td>");
        
        str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
        str_row.append("<input type='checkbox' id='func" + setid + "' name='func");
        str_row.append("' value='");
        str_row.append(setid);
        str_row.append("' onclick='isDisable(\"" + setid + "\")'");

        if (fieldMap != null && fieldMap.get(temp[1].toLowerCase()) != null)
            str_row.append(" checked");

        str_row.append("/>");
        if ("a01".equalsIgnoreCase(item.getFieldsetid())) {
            str_row.append("<input type='hidden' name='func_show");
            str_row.append("' value='");
            str_row.append(setid);
            str_row.append("'>");
        } else {
            str_row.append("<input type='hidden' name='func_onlys");
            str_row.append("' value='");
            str_row.append(setid);
            str_row.append("'>");
        }
        
        str_row.append("</td>");

        // 前台显示指标
        String value = "";
        if (fieldMap != null && fieldMap.get(temp[1].toLowerCase()) != null)
            value = (String) fieldMap.get(temp[1].toLowerCase());

        if (!"a01".equalsIgnoreCase(item.getFieldsetid())) {
            str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
            str_row.append("<input type='checkbox' id='func_show" + setid + "' name='func_show");
            str_row.append("' value='");
            str_row.append(setid);
            str_row.append("' onclick='isFuncShow(\"" + setid + "\")'");

            if (!"".equals(value)) {
                String[] value_arr = value.split("#");
                if ("1".equals(value_arr[0]))
                    str_row.append(" checked");
            }
            
            str_row.append(">");
            str_row.append("</td>");
        }
        // 必填项
        str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
        str_row.append("<input type='checkbox' id='func_must" + setid + "' name='func_must");
        str_row.append("' value='");
        str_row.append(setid);
        str_row.append("'");
        
        if (!"".equals(value)) {
            String[] value_arr = value.split("#");
            if ("1".equals(value_arr[1]))
                str_row.append(" checked");
        }
        
        str_row.append(" onclick='isFuncMust(\"" + setid + "\")'");
        str_row.append(">");
        str_row.append("</td>");
        String emailField = ConstantParamter.getEmailField();
        /** 唯一性校验指标 */
        if ("a01".equalsIgnoreCase(item.getFieldsetid())) {
            str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
            str_row.append("<table ");
            //如果指标不是字符型或者是姓名，邮箱时不显示不允许选择唯一性指标
            if (!"a".equalsIgnoreCase(item.getItemtype()) || !"0".equals(item.getCodesetid())
            		|| "a0101".equalsIgnoreCase(item.getItemid()) || item.getItemid().equalsIgnoreCase(emailField))
                str_row.append(" style='display:none'");
            
            str_row.append("><tr><td>");
            str_row.append("<input type='checkbox' id='func_onlys" + setid + "' name='func_onlys'  value='");
            str_row.append(setid.toUpperCase());
            str_row.append("' onclick='isFuncOnly(\"" + setid + "\")'");
            str_row.append("'> </td></tr></table>");
            str_row.append("</td>");
        }
        
        str_row.append("</tr>");
        return str_row.toString();
    }

    /**
     * 子集权限
     * 
     * @param setid
     * @param setdesc
     * @param flag
     * @return
     * @throws GeneralException
     */
    @Deprecated
    private String addSetRow(String setid, String setdesc, HashMap map, int index, String trClass)
            throws GeneralException {
        StringBuffer str_row = new StringBuffer();
        str_row.append("<tr bgcolor='" + trClass + "'>");
        str_row.append("<td align='right' ");
        str_row.append(" style='height:30px;border:1px solid #c5c5c5; padding-right:5px;' nowrap>");
        str_row.append(" <div   id='tableName" + index +"'>");
        str_row.append(setdesc);
        str_row.append("</div></td>");

        String id = "";
        LinkedHashMap<String,String> mm = null;
        String displayName = setdesc;
        RecordVo vo = ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
        if (vo != null) {
            String setlist = vo.getString("str_value");
            ArrayList<String> infoSetList = splitField(setlist);
            for (int i = 0; i < infoSetList.size(); i++) {
                String set = infoSetList.get(i);
                if (set.indexOf("[") != -1)
                    id = set.substring(0, set.indexOf("["));
                else
                    id = set;

                if (id.equals(setid) || id.indexOf(setid + "#") > -1) {
                    if (set.indexOf("[") != -1) {
                        mm = this.analyse(set.substring(set.indexOf("[") + 1, set.length() - 1));
                        String tableName = set.substring(0, set.indexOf("["));
                        if (tableName.indexOf("#") > -1)
                            displayName = tableName.substring(tableName.indexOf("#") + 1);
                    }

                    break;
                }
            }
        }
        
        boolean disabled = true;
        HashMap temp = mm;
        if (mm != null) {
            for (Iterator iterator = mm.keySet().iterator(); iterator.hasNext();) {
                String str = (String) iterator.next();
                if ("A01".equals(setid)) 
                    disabled = false;
                else {
                    if (("1".equals(mm.get(str)) || "2".equals(mm.get(str))) && !"-1".equals(str))
                        disabled = false;
                }
            }
        } else {
            for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
                String str = (String) iterator.next();
                if ("A01".equals(setid)) {
                    disabled = false;
                } else {
                    if (temp != null && ("1".equals(temp.get(str)) || "2".equals(temp.get(str))) && !"-1".equals(str))
                        disabled = false;
                }
            }
        }

        str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5; padding-right:5px;' nowrap>");
        str_row.append("<input type='text' maxlength='20' id='displayName" + index + "' value='");
        str_row.append(displayName);
        if(disabled)
            str_row.append("' disabled='disabled'");
        str_row.append("'/></td>");

        if (mm != null && mm.keySet().size() < 2)
            mm = null;
        if (mm != null) {
        	int i = 1;
        	for (Iterator iterator = mm.keySet().iterator(); iterator.hasNext();) {
                String str = (String) iterator.next();
                if("-1".equalsIgnoreCase(str)) {
                	if(i == 1)
                		str = "01";
                	else
                		str = "02";
                }
                	
                if ("A01".equals(setid)) {
                    str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
                    str_row.append("<input type='checkbox' name='func" + index
                            + "' disabled='true' value='" + setid + str + "1' checked/>可选");
                    str_row.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    str_row.append("<input type='checkbox' name='func" + index
                            + "' disabled='true' value='" + setid + str + "2' checked/>");
                    str_row.append(ResourceFactory.getProperty("lable.investigate_item.must_fillflag"));
                    str_row.append("</td>");
                } else {
                    str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
                    str_row.append("<input type='checkbox' name='func" + index + "' value='"
                            + setid + str + "1' onclick='setCheck(this);isTableDisable(" + index + ");'");
                    if (("1".equals(mm.get(str)) || "2".equals(mm.get(str))) && !"-1".equals(str))
                        str_row.append(" checked ");
                    str_row.append("/>可选");
                    str_row.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    str_row.append("<input type='checkbox' name='func" + index + "' value='"
                            + setid + str + "2' onclick='setCheck(this);isTableDisable(" + index + ");'");
                    if ("2".equals(mm.get(str)) && !"-1".equals(str))
                        str_row.append(" checked ");
                    
                    str_row.append("/>" + ResourceFactory.getProperty("lable.investigate_item.must_fillflag"));
                    str_row.append("</td>");
                }
                
                i++;
            }
        } else {
            for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
                String str = (String) iterator.next();
                if ("A01".equals(setid)) {
                    str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
                    str_row.append("<input type='checkbox' name='func" + index
                            + "' disabled='true' value='" + setid + str
                            + "1' onclick='setCheck(this)' checked/>可选");
                    str_row.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    str_row.append("<input type='checkbox' name='func" + index
                            + "' disabled='true' value='" + setid + str
                            + "2' onclick='setCheck(this)' checked/>");
                    str_row.append(ResourceFactory
                            .getProperty("lable.investigate_item.must_fillflag"));
                    str_row.append("</td>");
                } else {
                    str_row.append("<td align='center' style='height:30px;border:1px solid #c5c5c5;' nowrap>");
                    str_row.append("<input type='checkbox' name='func" + index + "' value='"
                            + setid + str + "1' onclick='setCheck(this);isTableDisable(" + index + ");'");
                    if (temp != null && ("1".equals(temp.get(str)) || "2".equals(temp.get(str))) && !"-1".equals(str))
                        str_row.append(" checked ");
                    
                    str_row.append("/>可选");
                    str_row.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    str_row.append("<input type='checkbox' name='func" + index + "' value='"
                            + setid + str + "2' onclick='setCheck(this);isTableDisable(" + index + ");'");
                    if (temp != null && "2".equals(temp.get(str)) && !"-1".equals(str))
                        str_row.append(" checked ");
                    
                    str_row.append("/>" + ResourceFactory.getProperty("lable.investigate_item.must_fillflag"));
                    str_row.append("</td>");
                }
            }
        }
        str_row.append("</tr>");
        return str_row.toString();
    }

    public LinkedHashMap<String,String> analyse(String tt) {
        LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();

        String[] ss = tt.split("`");
        for (int i = 0; i < ss.length; i++) {
            String str = ss[i];
            String[] arr_str = str.split("#");
            if (arr_str.length > 1) {
            	String key = arr_str[0].toLowerCase();
            	if("-1".equals(key)&&i==0)
            		key = "01";
            	else if("-1".equals(key)&&i==1)
            		key = "02";
                map.put(key, arr_str[1]);
            }

        }
        return map;
    }

    /**
     * 查询子集权限信息
     * 
     * @return
     */
   /* private String searchTablePriv1() throws GeneralException {
        StringBuffer table_str = new StringBuffer();
        ArrayList infoSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
        try {
            ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
            Map<String,String> map = bo.getHireChannelList();
            table_str.append(addTableHeader(GeneralConstant.FIELD_SET, "a"));
            for (int i = 0; i < infoSetList.size(); i++) {
                FieldSet fieldset = (FieldSet) infoSetList.get(i);
                if ("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
                    continue;
                
                if (i % 2 == 1)
                    table_str.append(addSetRow(fieldset.getFieldsetid(), fieldset.getCustomdesc(),
                            map, i, "#FAFAFA"));
                else
                    table_str.append(addSetRow(fieldset.getFieldsetid(), fieldset.getCustomdesc(),
                            map, i, "#FFFFFF"));
            }

            table_str.append(addTableFoot());
            return table_str.toString();
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
    }*/
    /*{ text: '子集名称', dataIndex: 'name' },
    { text: '显示名称', dataIndex: 'displayname'},
    { text: '校园招聘', dataIndex: 'a01',
     renderer:function(value,b,record){
         if(value==1)
            return "<input type='radio'   name='"+record.getId()+"_a01' checked=true />可选 <input name='"+record.getId()+"_a01' type='radio' />必填";
     } },
    { text: '社会招聘', dataIndex: 'a02' },
    { text: '人才招聘', dataIndex: 'a03' }*/
	private String[] searchTablePriv() throws GeneralException {
		Map<String, String> storeMap = null;
		Map<String, String> columnMap = null;
		Map<String, Map<String, String>> sortmap = null;
		ArrayList storelist= new ArrayList();
		ArrayList columnlist= new ArrayList();
		String fields = "'name','displayname'";//fields参数
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			ArrayList infoSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
			ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
			ArrayList<String> fieldlist = bo.getFieldList();
			Map<String,String> map = bo.getHireChannelList();
			this.frowset = dao.search("select Str_Value from constant where constant='ZP_SUBSET_LIST' ");
			String str_value = "{}";
			if(this.frowset.next()) {
				str_value = this.frowset.getString("str_value");
				if(StringUtils.isBlank(str_value)) {
					str_value="{}";
				}
				if (!"{".equals(str_value.charAt(0)+"")) {//如果表中存放的还是旧数据，则对数据进行格式转换，并替换原数据
					str_value = bo.convertStringFormat(str_value);
				}
			}
			Map<String, Map<String, String>>   maps = (Map<String, Map<String, String>> )JSON.parse(str_value);
			if(null==maps||maps.size()==0||"resetvalue".equals(flag)){//如果数据库中已经保存了数据
				maps = new LinkedHashMap<String,Map<String, String>>();
				LinkedHashMap<String,String> omap =null;
				for(int k=0;k<infoSetList.size();k++){
					omap = new LinkedHashMap<String,String>();
					FieldSet set = (FieldSet)infoSetList.get(k);
					String itemid = set.getFieldsetid();
					String desc = set.getCustomdesc();
					for(Entry<String, String> allEntry : map.entrySet()){
						String key =allEntry.getKey();
						omap.put(key, "A01".equals(itemid)?"2":"-1");
					}
					omap.put("displayname", desc);
					maps.put(itemid, omap);
				}
			}
			sortmap = bo.sortConstantMap(fieldlist, maps);
			for(Entry<String, Map<String, String>> elemap : sortmap.entrySet()){
				storeMap = new LinkedHashMap<String,String>();
				String key = elemap.getKey();
				FieldSet  set = DataDictionary.getFieldSetVo(key);
				String desc ="";
				if(null!=set){
				    desc= set.getCustomdesc();
					if(null==desc||"".equals(desc))
						throw GeneralExceptionHandler.Handle(new Exception("指标的描述信息不能为空！"));
					storeMap.put("name", desc+"'"+key);
				}
					
				Map<String, String> sonmap = elemap.getValue();
				String displayname = sonmap.get("displayname");
				
				if(StringUtils.isEmpty(displayname)){
					displayname = set.getCustomdesc();
				}
				
				if(null!=displayname)
					storeMap.put("displayname",displayname);
				else
					storeMap.put("displayname",desc);
				int i =0;
				for(Entry<String, String> allEntry : map.entrySet()){
					String allkey =allEntry.getKey();
					if(sonmap.containsKey(allkey)&&0==i){
						for(Entry<String, String> obj : sonmap.entrySet()){
							String sonkey = obj.getKey();
							String value = obj.getValue();
							if("displayname".equalsIgnoreCase(sonkey))
								continue;
							
							if("A01".equalsIgnoreCase(key))
								storeMap.put("a"+sonkey,"2");
							else
								storeMap.put("a"+sonkey,value);
						}
						i++;
					}else if(!sonmap.containsKey(allkey)){
						if("A01".equalsIgnoreCase(key))
							storeMap.put("a"+allkey,"2");
						else
							storeMap.put("a"+allkey,"-1");
					}
				}
				storelist.add(storeMap);
			}
		
			
			//处理前台表格的列
			columnMap = new LinkedHashMap<String,String>();
			columnMap.put("text", "子集名称");
			columnMap.put("dataIndex", "name");
			columnMap.put("menuDisabled", "true");
			columnMap.put("align", "right");
			columnMap.put("renderer", "<GGGG>");
			columnMap.put("<YYYY>", "<MMMM>");
			columnlist.add(columnMap);
			columnMap = new LinkedHashMap<String,String>();
			columnMap.put("text", "显示名称");
			columnMap.put("dataIndex", "displayname");
			columnMap.put("menuDisabled", "true");
			columnMap.put("align", "center");
			columnMap.put("<TTTT>", "<$$$$>");
			columnMap.put("<YYYY>", "<MMMM>");
			columnlist.add(columnMap);
			for(Entry<String, String> entry : map.entrySet()){
				columnMap = new LinkedHashMap<String,String>();
				String textkey = entry.getKey();
				CodeItem item = AdminCode.getCode("35", textkey);
				if(null!=item)
					columnMap.put("text", item.getCodename());
				fields = fields+",'a"+textkey+"'";
				String textvalue = entry.getValue();
				columnMap.put("dataIndex","a"+textkey);
				columnMap.put("menuDisabled", "true");
				columnMap.put("align", "center");
				columnMap.put("renderer", "<####>");
				columnMap.put("<YYYY>", "<ZZZZ>");
				columnlist.add(columnMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String columnJson = JSON.toString(columnlist);
		columnJson = columnJson.replaceAll("\"<jsfn>", "");
		columnJson = columnJson.replaceAll("</jsfn>\"", "");
	    StringBuffer str = new StringBuffer();
		str.append("function(value, metaData, record, rowIndex, colIndex){ return setNbase_me.backStr(value, metaData, record, rowIndex, colIndex);}");//处理各个指标的可选，必填的方法
		columnJson = columnJson.replaceAll("\"<####>\"", str.toString());
		str = new StringBuffer();
		str.append("editor:{xtype: 'textfield',allowBlank:false}");
	    //str.append("function(value,b,record){ displaycontrol(record);}");//处理各个指标的可选，必填的方法
		columnJson = columnJson.replace("\"<$$$$>\"", str.toString());
		columnJson = columnJson.replace("\"<YYYY>\":", "");
		columnJson = columnJson.replace("\"<TTTT>\":", "");
		//width:500
		str = new StringBuffer();
		str.append("width:210,locked:true");
		columnJson = columnJson.replaceAll("\"<MMMM>\"", str.toString());
		str = new StringBuffer();
		str.append("minWidth:220");
		columnJson = columnJson.replaceAll("\"<ZZZZ>\"", str.toString());
		str = new StringBuffer();
		str.append("function(value){ return setNbase_me.processname(value);}");
		columnJson = columnJson.replace("\"<GGGG>\"", str.toString());
	    
		return new String[]{columnJson,JSON.toString(storelist),fields};
	}
	

	
    // A01{A0101[0#0],A0107[1#0],},A04{C0401[0#1],},
    public HashMap getZpFieldList(String strValue) throws GeneralException {
        HashMap fieldSetMap = new HashMap();
        int idx = -1;
        try {
            String temp = strValue;
            if (temp != null && temp.trim().length() > 0) {
                String[] temps = temp.split(",},");
                for (int i = 0; i < temps.length; i++) {
                    String setid = temps[i].indexOf("{")>0?temps[i].substring(0, temps[i].indexOf("{")):temps[i].substring(0, temps[i].indexOf("["));
                    String fieldstr = temps[i].substring((temps[i].indexOf("{") + 1));
                    HashMap fieldItemMap = new HashMap();
                    HashMap<String, String> fieldName = new HashMap<String, String>();
                    String[] fields = fieldstr.split(",");
                    for (int n = 0; n < fields.length; n++) {
                        /** 考虑兼容性，以后定义过参数 */
                        idx = fields[n].indexOf("[");
                        if (idx != -1) {
                            String a = fields[n].substring(0, fields[n].indexOf("[")).toLowerCase();
                            if(a.indexOf("#") > -1){
                                String[] displayName = a.split("#");
                                a = displayName[0];
                                fieldName.put(a, displayName[1]);
                            }
                            
                            String b = fields[n].substring((fields[n].indexOf("[") + 1), fields[n].indexOf("]"));
                            fieldItemMap.put(a, b);
                        } else 
                            fieldItemMap.put(fields[n], "0");
                        
                    }
                    fieldNames.put(setid, fieldName);
                    fieldSetMap.put(setid.toLowerCase(), fieldItemMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return fieldSetMap;
    }

    /**
     * 生成指标授权界面
     * 
     * @param fieldsetid
     * @return
     * @throws GeneralException
     */
    private String addFieldDomain(String fieldsetid, HashMap fieldMap) throws GeneralException {
        StringBuffer domain_str = new StringBuffer();
        ArrayList infofieldlist = DataDictionary.getFieldList(fieldsetid, Constant.EMPLOY_FIELD_SET);
        try {
            String sql = "select * from fielditem where fieldsetid='" + fieldsetid
                    + "' order by displayid";
            ContentDAO dao = new ContentDAO(this.frameconn);
            ResultSet rs = null;
            rs = dao.search(sql);
            HashMap map = new HashMap();
            ArrayList infofieldlist_order = new ArrayList();
            for (int i = 0; i < infofieldlist.size(); i++) {
                FieldItem fielditem = (FieldItem) infofieldlist.get(i);
                map.put(fielditem.getItemid().toLowerCase(), fielditem);
            }
            
            while (rs.next()) {
                String itemid = rs.getString("itemid").toLowerCase();
                if (map.get(itemid) != null)
                    infofieldlist_order.add(map.get(itemid));// 按指标设置顺序存放
                
            }

            domain_str.append("<div style='display:none' id='");
            domain_str.append("div");
            domain_str.append(fieldsetid + "_1");
            domain_str.append("'>");
            domain_str.append(addTableHeader(GeneralConstant.Field_LABLE, fieldsetid));

            for (int i = 0; i < infofieldlist_order.size(); i++) {
                FieldItem fielditem = (FieldItem) infofieldlist_order.get(i);
                /**
                 * 未解决bug 0025115 因为在前台即使设置上也不显示
                 */
                if ("b0110".equalsIgnoreCase(fielditem.getItemid()) || "e0122".equalsIgnoreCase(fielditem.getItemid())
                        || "e01A1".equalsIgnoreCase(fielditem.getItemid())) {
                    continue;
                }
                /***/
                domain_str.append(addTableRow(fieldsetid + "." + fielditem.getItemid(), fielditem.getItemdesc(), fieldMap, fielditem, fieldNames.get(fieldsetid)));
            }

            domain_str.append(addTableFoot());
            domain_str.append("</div>");

            return domain_str.toString();
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
    }

    /**
     * 查询指标权限
     * 
     * @return
     */
   /* private String searchFieldPriv() throws GeneralException {
        StringBuffer field_str = new StringBuffer();
        RecordVo vo = ConstantParamter.getConstantVo("ZP_SUBSET_LIST");
        String setlist = "";
        if (vo != null) {
            setlist = vo.getString("str_value");
            setlist = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(setlist);
        }
        
        ArrayList infoSetList = splitField(setlist);

        RecordVo vo2 = ConstantParamter.getConstantVo("ZP_FIELD_LIST");
        String fieldStr = "";
        if (vo2 != null)
            fieldStr = vo2.getString("str_value");
        
        HashMap fieldsetMap = getZpFieldList(fieldStr);

        for (int i = 0; i < infoSetList.size(); i++) {
            StringBuffer strsql = new StringBuffer();
            String setid = (String) infoSetList.get(i);
            if (setid.indexOf("[") != -1) {// 应聘简历指标页面，只显示应聘子集中选中的子集（社会招聘或校园招聘只选中一个就显示）。jingq upd 2015.08.04
                String str = setid.substring(setid.indexOf("[") + 1, setid.length() - 1);
                if (str.indexOf("01#") == -1 && str.indexOf("02#") == -1)
                    continue;
                
                setid = setid.substring(0, 3);
            }
            strsql.append("select fieldsetid,customdesc from fieldset where fieldsetid = '" + setid + "'");
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            try {
                this.frowset = dao.search(strsql.toString());
                while (this.frowset.next()) {
                    field_str.append("<div id='");
                    field_str.append("div");
                    field_str.append(this.frowset.getString("fieldsetid"));
                    field_str.append("' style='padding:3 0 3 3'>");

                    field_str.append("<span style='cursor:hand' title='单击展开子集' onclick=show('");
                    field_str.append("div");
                    field_str.append(this.frowset.getString("fieldsetid"));
                    field_str.append("')>");
                    field_str.append("<img src='/images/table.gif' border=0 align='absmiddle' style='margin-right:5px;'>");
                    field_str.append(this.frowset.getString("customdesc"));
                    field_str.append("</span>");

                    HashMap fieldMap = (HashMap) fieldsetMap.get(this.frowset.getString("fieldsetid").toLowerCase());
                    field_str.append(addFieldDomain(this.frowset.getString("fieldsetid"), fieldMap));

                    field_str.append("</div>");
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                throw GeneralExceptionHandler.Handle(sqle);
            }
        }
        return field_str.toString();
    }*/
    private String[] searchFieldPriv() throws GeneralException {
        Map<String, String> storeMap = null;
        String map2Json = null;
        Map<String, Object> columnMap = null;
        ArrayList storelist= new ArrayList();
        ArrayList maplist= new ArrayList();
        ArrayList columnlist= new ArrayList();
        String fields = "'name','displayname'";//fields参数
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String setlist = "";
        String fieldStr = "";
        FieldItem FieItem = null;
        try {
            ArrayList infoSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
            ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
            ArrayList<String> fieldlist = bo.getFieldList();
            Map<String,String> map = bo.getHireChannelList();
            Map<String,Object> map2 = new HashMap<String,Object>();
            String str_value = null;
            boolean isExist = false;
            
             this.frowset = dao.search("select Str_Value,Constant from constant where constant='ZP_SUBSET_LIST' or constant='ZP_FIELD_LIST' or constant='ZP_FIELD_LIST_JSON' or constant='ZP_ONLY_FIELD'");
             while(this.frowset.next()) {
                 String constant = this.frowset.getString("Constant");
                 if("ZP_SUBSET_LIST".equalsIgnoreCase(constant))
                     setlist = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(this.frowset.getString("str_value"));
                 else if("ZP_FIELD_LIST_JSON".equalsIgnoreCase(constant)){
                     str_value = this.frowset.getString("str_value");
                     isExist = true;
                 }else if("ZP_FIELD_LIST".equalsIgnoreCase(constant) && isExist == false)
                     str_value = this.frowset.getString("str_value");
             }   
             
             if(!isExist){
                 HashMap fieldsetMap = getZpFieldList(str_value);
                 str_value = bo.convertStringField(fieldsetMap);
             }
             
             if(StringUtils.isBlank(setlist)) {
            	 setlist="{}";
             }
             
			if(StringUtils.isBlank(str_value)) {
				str_value="{}";
			}
             
             JSONObject json1 = JSONObject.fromObject(str_value);
             Map<String,  Map<String,Object>> jsonMaps =  (Map<String, Map<String, Object>>) JSON.parse(str_value);
             Map<String, Map<String, String>> maps = (Map<String, Map<String, String>> )JSON.parse(setlist);
             Map<String, Map<String, String>> sortmap = bo.sortConstantMap(fieldlist, maps);    
            
             for(Entry<String, Map<String, String>> elemap : sortmap.entrySet()){
                 StringBuffer strsql = new StringBuffer();
                 String fieldsetid = elemap.getKey();
                 Map<String, String> sonmap = elemap.getValue();
                 ArrayList maplist2= new ArrayList();
                 Map<String,Object> map3 = new HashMap<String,Object>();
                 String name = sonmap.get("displayname");
                 boolean displayFlag = false;
                 for(Entry<String, String> obj : sonmap.entrySet()){
                     String key =obj.getKey();
                     String value = obj.getValue();
                     
                     if(map.containsKey(key)&&!"-1".equals(value)){
                         displayFlag = true;
                         map3.put("name", name);
                         map3.put("id", fieldsetid);
                         map3.put("leaf", false);
                         break;
                     }
                 }
            if(displayFlag){
                ArrayList<FieldItem> headList = DataDictionary.getFieldList(fieldsetid, Constant.USED_FIELD_SET);
                
                if(headList.size() == 0)
                    continue;
                
                String emailField = ConstantParamter.getEmailField();
                for(int j = 0 ; j < headList.size(); j++) {
                    Map<String,Object> map4 = new HashMap<String,Object>();
                    FieItem = (FieldItem) headList.get(j);
                    String itemid= FieItem.getItemid().toLowerCase();
                    String itemidType =  FieItem.getItemtype();
                    String codesetid =  FieItem.getCodesetid();
                    String value= FieItem.getItemdesc();
                    
                    if("b0110".equalsIgnoreCase(itemid) || "e0122".equalsIgnoreCase(itemid) 
                    		|| "e01a1".equalsIgnoreCase(itemid) )
                    	continue;
                    
                    map4.put("name", value);
                    map4.put("displayname", value);
                    map4.put("id", itemid);
                    map4.put("leaf", true);
                     for(Entry<String, String> entry : map.entrySet()){
                         columnMap = new LinkedHashMap<String,Object>();
                         String textkey = entry.getKey();
                         String tkeys =sonmap.get(textkey);
                         
                         if(itemid.equalsIgnoreCase(emailField)){
                        	 map4.put("a"+textkey, "10");
                        	 continue;
                         }
                         
                         if(!json1.containsKey(textkey) && !"A01".equalsIgnoreCase(fieldsetid))
                             continue;
                         
                         JSONArray array = null;
                         if(json1.containsKey(textkey)){
	                         Map<String, Object> channelMap = (Map<String, Object>) jsonMaps.get(textkey);
	                         JSONObject json2   =  (JSONObject) json1.get(textkey);
	                         array  =  (JSONArray) json2.get(fieldsetid);
                         }
                         
                         if("-1".equalsIgnoreCase(sonmap.get(textkey))){
                             map4.put("a"+textkey, "-4");
                             continue;
                         }
                         
                         displayFlag = false;
                         Map<String,String> itemidMap = null;
                         if("A01".equalsIgnoreCase(fieldsetid)){
                                 if(array == null || "resetvalue".equals(flag)){
                                     map4.put("a"+textkey, "-3");
                                     continue;
                                 }
                                 
                                 for(int i = 0;i <array.size();i++)
                                 {
                                     itemidMap = (Map<String, String>) array.get(i);
                                     if(itemidMap.containsValue(itemid)){
                                         displayFlag = true;
                                         break;
                                     }
                                 }
                                 
                                 if(displayFlag){
                                     String in_list = itemidMap.get("in_list");
                                     String must = itemidMap.get("must");
                                     String displayname = itemidMap.get("name");
                                     if(!"".equalsIgnoreCase(displayname))
                                         map4.put("displayname", displayname);
                                     
                                     if("0".equalsIgnoreCase(must)){
                                         map4.put("a"+textkey, "9");
                                     }else{
                                         map4.put("a"+textkey, "10");
                                     }
                                     
                                 }else{
                                     map4.put("a"+textkey, "-3");
                                 }
                                 continue;
                         }else{
                             if(array == null || "resetvalue".equals(flag)){
                                 map4.put("a"+textkey, "-2");
                                 continue;
                             }
                             
                             for(int i = 0;i <array.size();i++)
                             {
                                 itemidMap = (Map<String, String>) array.get(i);
                                 if(itemidMap.containsValue(itemid)){
                                     displayFlag = true;
                                     break;
                                 }
                             }
                             
                             if(displayFlag){
                                 String in_list = itemidMap.get("in_list");
                                 String must = itemidMap.get("must");
                                 String displayname = itemidMap.get("name");
                                 if(!"".equalsIgnoreCase(displayname))
                                     map4.put("displayname", displayname);
                                 
                                 if("0".equalsIgnoreCase(in_list)){
                                     if("0".equalsIgnoreCase(must)){
                                         map4.put("a"+textkey, "7");
                                     }else{
                                         map4.put("a"+textkey, "4");
                                     }
                                 }else{
                                     if("0".equalsIgnoreCase(must)){
                                         map4.put("a"+textkey, "6");
                                     }else{
                                         map4.put("a"+textkey, "5");
                                     }
                                 }
                                 
                             }else  
                                 map4.put("a"+textkey, "-2");
                         }
                     }
                     maplist2.add(map4);
                     
                 }
                 map3.put("children", maplist2);
                 maplist.add(map3);
                 
             }
             }
                map2.put("children", maplist);
                map2Json = JSON.toString(map2);
             
                //处理前台表格的列
                //str.append("<jsfn>function(value,b,record){if(value==1)return \"<input type='radio'   name='+record.getId()+_a01' checked=true />可选 <input name='+record.getId()+_a01' type='radio' />必填\";}</jsfn>");
                columnMap = new LinkedHashMap<String,Object>();
                columnMap.put("text", "指标名称");
                columnMap.put("dataIndex", "name");
                columnMap.put("menuDisabled", true);
                columnMap.put("sortable", true);
                columnMap.put("xtype", "treecolumn");
                columnMap.put("align", "left");
                columnMap.put("renderer", "<GGGG>");
                columnMap.put("<YYYY>", "<MMMM>");
                columnlist.add(columnMap);
                columnMap = new LinkedHashMap<String,Object>();
                columnMap.put("text", "显示名称");
                columnMap.put("dataIndex", "displayname");
                columnMap.put("menuDisabled", true);
                columnMap.put("sortable", true);
                columnMap.put("editable", "true");
                
                columnMap.put("align", "left");
                columnMap.put("<TTTT>", "<$$$$>");
                columnMap.put("<YYYY>", "<MMMM>");
                columnlist.add(columnMap);
                for(Entry<String, String> entry : map.entrySet()){
                    columnMap = new LinkedHashMap<String,Object>();
                    String textkey = entry.getKey();
                        
                    CodeItem item = AdminCode.getCode("35", textkey);
                    if(null!=item)
                        columnMap.put("text", item.getCodename());
                    fields = fields+",'a"+textkey+"'";
                    String textvalue = entry.getValue();
                    columnMap.put("dataIndex","a"+textkey);
                    columnMap.put("menuDisabled", true);
                    columnMap.put("sortable", true);
                    columnMap.put("align", "left");
                    columnMap.put("renderer", "<####>");
                    columnMap.put("<YYYY>", "<ZZZZ>");
                    columnlist.add(columnMap);
            }
             
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String columnJson = JSON.toString(columnlist);
        columnJson = columnJson.replaceAll("\"<jsfn>", "");
        columnJson = columnJson.replaceAll("</jsfn>\"", "");
        StringBuffer str = new StringBuffer();
        str.append("function(value, metaData, record, rowIndex, colIndex){ return setIndex_me.backStr(value, metaData, record, rowIndex, colIndex);}");//处理各个指标的可选，必填的方法
        columnJson = columnJson.replaceAll("\"<####>\"", str.toString());
        str = new StringBuffer();
        str.append("editor:{xtype: 'textfield',allowBlank:false}");
        columnJson = columnJson.replace("\"<$$$$>\"", str.toString());
        columnJson = columnJson.replace("\"<YYYY>\":", "");
        columnJson = columnJson.replace("\"<TTTT>\":", "");
        str = new StringBuffer();
        str.append("width:210,locked:true");
        columnJson = columnJson.replaceAll("\"<MMMM>\"", str.toString());
        str = new StringBuffer();
        str.append("minWidth:220");
        columnJson = columnJson.replaceAll("\"<ZZZZ>\"", str.toString());
        str = new StringBuffer();
        str.append("function(value){ return setIndex_me.processname(value);}");
        columnJson = columnJson.replace("\"<GGGG>\"", str.toString());
        fields = fields.replaceAll("'", "");
        return new String[]{columnJson,map2Json,fields};
    }
    

    /*
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    @Override
    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
       
        flag = (String)this.getFormHM().get("flag");
        flag = flag==null?"":flag;
        String tab_name = "";
        if(null==hm){
        	tab_name = (String)this.getFormHM().get("a_tab");
        }else
        	tab_name = (String) hm.get("a_tab");
        
        if (tab_name == null || "".equals(tab_name))
            return;
        
        String str = "";
        String str_only = "";
        String[] SubsetArray =null;
        try {

            /**
             * 人员库授权
             */

            if ("dbpriv".equals(tab_name)) {
                str = searchDbNameHtml();
            }

            /**
             * 子集授权
             */
            if ("tablepriv".equals(tab_name)) {
                SubsetArray = searchTablePriv();
            }
            /**
             * 指标授权
             */
            if ("fieldpriv".equals(tab_name)) {
                SubsetArray = searchFieldPriv();
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        }
        /**
         * save the role_id.
         */
        this.getFormHM().put("storeJson", SubsetArray[1]);
        this.getFormHM().put("columnJson", SubsetArray[0]);
        this.getFormHM().put("fields", SubsetArray[2]);
    }

}
