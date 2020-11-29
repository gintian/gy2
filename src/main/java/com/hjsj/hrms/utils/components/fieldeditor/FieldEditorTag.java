package com.hjsj.hrms.utils.components.fieldeditor;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import org.apache.struts.taglib.TagUtils;
import org.mortbay.util.ajax.JSON;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 指标编辑标签
 * @author guodd
 * @Description:TODO
 * @date 2015-1-12
 */
public class FieldEditorTag extends BodyTagSupport{

	//form 名称
	String formName;
	// form中 字段集合 属性名称
	String itemsProperty;
	//人员库前缀，针对人员信息集才有用
	private String dbName;
	// 操作表名
	String tableName;
	// title
	String title;
	// 提交或返回 是否走 函数
	boolean doScript;
	//保存按钮 
	String editeText="button.edit";
	//保存按钮 
	String saveText="button.save";
	//返回按钮
	String cancelText="button.return";
	// 保存后的跳转链接
	String saveAction;
	// 返回链接 如果不写则使用 forward
	String cancelAction;
	// 跳转target
	String target="_self";
	// 分几列显示
	int cols = 2;
	// 提交类型：insert（新增）\ update（修改）\view（浏览）
	String saveType;
	
	ArrayList fieldInfos = new ArrayList();
	
	public int doEndTag() throws JspException {
		fieldInfos.clear();
		ArrayList itemList = (ArrayList)TagUtils.getInstance().lookup(this.pageContext, this.formName,this.itemsProperty,null);
		if(itemList == null)
			return EVAL_BODY_BUFFERED;
		FieldSet fs  = DataDictionary.getFieldSetVo(tableName.toLowerCase());
		if(fs==null)
			return EVAL_BODY_BUFFERED;
		
			if("view".equals(saveType))
				this.title=ResourceFactory.getProperty("button."+saveType)+fs.getCustomdesc()+"信息";
			else if("insert".equals(saveType))
				this.title=ResourceFactory.getProperty("button."+saveType)+fs.getCustomdesc();
			else
				this.title=ResourceFactory.getProperty("button."+saveType)+fs.getCustomdesc()+"信息";
		
		Writer write = pageContext.getOut();
		try {
			// 主键
			StringBuffer indexKey = new StringBuffer();
			StringBuffer outstr = new StringBuffer();
			outstr.append("var compWidth = document.body.offsetWidth;");
			outstr.append("Ext.create('Ext.container.Viewport',{ \n");
			outstr.append("layout:'fit',\n");
			outstr.append("items:{ \n");
			outstr.append("xtype:'form',border:0,title:'"+title+"',autoScroll:true, \n");
			outstr.append("layout:{type:'table',columns:"+cols+", \n");
			outstr.append("tableAttrs:{width:'100%',border:0}, \n");
			outstr.append("tdAttrs:{align:'left',nowarp:'nowarp'}}, \n");
			outstr.append("items:");
				
			ArrayList fieldObjList = createEditorObj(saveType,indexKey,itemList);
				
			String dataJSON = JSON.toString(fieldObjList);
			dataJSON = dataJSON.replaceAll("\"<jsfn>","");
			dataJSON = dataJSON.replaceAll("</jsfn>\"","");
		    outstr.append(dataJSON);
		    outstr.append(",tools:"+createButtons(indexKey.toString())+",changeItems:function(){");
		    if("view".equals(saveType)){
		    	ArrayList fieldlist = createEditorObj("update", indexKey, itemList);
		    	outstr.append("this.removeAll();");
		    	String newJson = JSON.toString(fieldlist);
		    	newJson = newJson.replaceAll("\"<jsfn>","");
		    	newJson = newJson.replaceAll("</jsfn>\"","");
		    	outstr.append("this.add("+newJson+");");
		    	outstr.append("this.setTitle('"+ResourceFactory.getProperty("button.edit")+fs.getCustomdesc()+"信息"+"');");
		    }
		    outstr.append("},fieldInfos:"+JSON.toString(fieldInfos)+"}");
			outstr.append("});");
		    
			write.write("<script type='text/javascript' src='/components/tableFactory/ext_custom.js'></script>");
			write.write("<script>");
			write.write("Ext.Loader.setConfig({enabled: true,paths: {'EHR': '/components'},scriptCharset:'GBK'});");
			write.write("Ext.require('EHR.extWidget.field.CodeTreeCombox');");
			write.write("Ext.require('EHR.extWidget.field.CodeSelectField');");
			write.write("Ext.require('EHR.extWidget.field.DateTimeField');");
			write.write("Ext.onReady(function(){");
			write.write(outstr+"});</script>");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return SKIP_BODY;
	}
	
    private String createButtons(String indexKey){
		StringBuffer buttonstr = new StringBuffer();
		buttonstr.append("[");
		
		//if(!"view".equals(saveType)){
			buttonstr.append("{xtype:'button',id:'savebutton',text:'"+ResourceFactory.getProperty(saveText)+"',");
			if("view".equals(saveType))
			    buttonstr.append("hidden:true,");
			buttonstr.append("formBind:true,handler:function(){ ");
			buttonstr.append("var commitData = this.up('form').getForm().getFieldValues();");
			buttonstr.append("var fieldInfos = this.up('form').fieldInfos;");
			buttonstr.append("var hashvo = new HashMap();");
			buttonstr.append("hashvo.put('commitData',commitData); ");
			buttonstr.append("hashvo.put('dbName','"+dbName+"'); ");
			buttonstr.append("hashvo.put('tableName','"+tableName+"'); ");
			buttonstr.append("hashvo.put('indexKey','"+indexKey+"'); ");
			buttonstr.append("hashvo.put('subType','"+saveType+"'); ");
			buttonstr.append("hashvo.put('fieldInfos',fieldInfos); ");
			buttonstr.append("Rpc({functionId:'9030000031',success:function(){");
			if(doScript){
				buttonstr.append(saveAction+"(commitData);");
			}else{
				buttonstr.append("window.target='_selft';window.location.href='"+saveAction+"&save$Type="+saveType+"';");
			}
			buttonstr.append("},scope:this},hashvo);");
			buttonstr.append("}},");
		if("view".equals(saveType)){
			buttonstr.append("{xtype:'button',text:'"+ResourceFactory.getProperty(editeText)+"',formBind:false,handler:function(t){ t.setVisible(false);Ext.getCmp('savebutton').setVisible(true);t.up('form').changeItems();return;");
			buttonstr.append("window.target='_selft';window.location.href=window.location.href.replace('subType','xxx')+'&subType=update';");
			buttonstr.append("}},");
		}
		
		buttonstr.append("{xtype:'button',margin:'0 0 0 10',text:'"+ResourceFactory.getProperty(cancelText)+"',handler:function(){");
		if(doScript){
			buttonstr.append(cancelAction+"();");
		}else{
			buttonstr.append("window.target='_self';window.location.href='"+(cancelAction==null?saveAction:cancelAction)+"';");
		}
		buttonstr.append("}}]");
    	return buttonstr.toString();
    	
    }

    private ArrayList createEditorObj(String saveType,StringBuffer indexKey,ArrayList itemList){
    	
    	ArrayList fieldObjList = new ArrayList();
		ArrayList hiddenObjList = new ArrayList();
		try{
		for(int i=0;i<itemList.size();i++){
			ColumnsInfo ci = (ColumnsInfo)itemList.get(i);
			if(ci.getColumnId() == null){
				itemList.remove(i);
				i--;
				continue;
			}
			if(!"view".equals(saveType) && ci.getLoadtype() != ci.LOADTYPE_ONLYLOAD){
				HashMap fieldInfo = new HashMap();
				fieldInfo.put("fieldId", ci.getColumnId());
				fieldInfo.put("fieldType", ci.getColumnType()==null?"":ci.getColumnType());
				fieldInfo.put("codesetid", ci.getCodesetId()==null?"":ci.getCodesetId());
				if("D".equals(ci.getColumnType()))
					fieldInfo.put("fieldLength", new Integer(ci.getColumnLength()));
					
				fieldInfos.add(fieldInfo);
			}
			HashMap fieldObj = new HashMap();
			fieldObj.put("xtype", "textfield");
			fieldObj.put("margin", "<jsfn>'10 '+compWidth/20/"+(cols-1)+"+' 10 '+compWidth/15/"+(cols-1)+"</jsfn>");
			fieldObj.put("name", ci.getColumnId());
			fieldObj.put("id", ci.getColumnId());
		    fieldObj.put("fieldLabel", ci.getColumnDesc());
		    fieldObj.put("labelStyle", "word-break:break-all");
		    fieldObj.put("labelSeparator",false);
		    	fieldObj.put("value", ci.getDefaultValue());
		    fieldObj.put("labelAlign","right");
		    fieldObj.put("width",new Integer(405));
		    fieldObj.put("maxLength",new Integer(ci.getColumnLength()));
		    fieldObj.put("enforceMaxLength", Boolean.TRUE);
		    fieldObj.put("allowBlank", new Boolean(ci.isAllowBlank()));
		    fieldObj.put("readOnly", new Boolean(ci.isReadOnly()));
		    if(!"view".equals(saveType) && !ci.isAllowBlank())
		    		fieldObj.put("listeners","<jsfn>{render:function(){this.inputEl.dom.parentNode.style.borderLeft='red 2px solid';this.inputEl.dom.parentNode.style.paddingLeft='3px'}}</jsfn>");
		    if("N".equals(ci.getColumnType())){
		    	fieldObj.put("xtype", "numberfield");
		    	String formatpattern = "000000000000000000000000000000";
				String format = formatpattern.substring(0,(ci.getColumnLength()>30?30:ci.getColumnLength()));//考虑系统不可能数值型超过30位，暂定上线为30位  xuj update 2015-1-20
				format = format.length()==0?"#":format;
				int decimalwidth = ci.getDecimalWidth();
				fieldObj.put("decimalPrecision",new Integer(decimalwidth));
				if(decimalwidth>0){//浮点型   现在存在一个问题当输入的为5.00则自动变为5  缺陷  xuj
					format = format+"."+formatpattern.substring(0, decimalwidth);
					fieldObj.put("hideTrigger", "true");
					fieldObj.put("maxLength", new Integer(ci.getColumnLength()+decimalwidth+1));
				}else{//整型
					if(new Double(format.replaceAll("0", "9").replaceAll("#","0")).floatValue()>Integer.MAX_VALUE)  //整形最大值不能超过Integer.MAX_VALUE  xuj update 2015-1-20
						format = String.valueOf(Integer.MAX_VALUE);
				}
				String maxNum = format.replaceAll("0", "9");
				if(!"#".equals(maxNum)){
					fieldObj.put("xtype", "numberfield");
					if(decimalwidth>0)  //xuj update 小数型指标  2015-1-20
						fieldObj.put("maxValue", new Double(maxNum.replaceAll("#","0")));
					else
						fieldObj.put("maxValue", new Integer(maxNum.replaceAll("#","0")));
				}else{
					fieldObj.put("maxValue", new Integer(0));
				}
				fieldObj.put("decimalPrecision ", new Integer(ci.getDecimalWidth()));
				//fieldObj.put("minValue", new Integer(0));
		    }else if("D".equals(ci.getColumnType())){
			    fieldObj.put("xtype", "datetimefield");
			    String extFormat = "";
			    String dateFormat="";
				if(ci.getColumnLength() == 4){
					extFormat="Y";
					dateFormat="yyyy";
				}else if(ci.getColumnLength() == 7){
					extFormat="Y-m";
					dateFormat="yyyy-MM";
				}else if(ci.getColumnLength() == 10){
					extFormat="Y-m-d";
					dateFormat="yyyy-MM-dd";
				}else if(ci.getColumnLength() == 16){
					extFormat="Y-m-d H:i";
					dateFormat="yyyy-MM-dd HH:mm";
				}else{
					extFormat="Y-m-d H:i:s";
					dateFormat="yyyy-MM-dd HH:mm:ss";
					fieldObj.put("maxLength", new Integer(19));
				}
				if(ci.getDefaultValue()!=null && ci.getDefaultValue().length()>0){
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					String datevalue = sdf.format(sdf.parse(ci.getDefaultValue()));
					fieldObj.put("value", datevalue);
				}
			    fieldObj.put("format",extFormat);
			}else if("M".equals(ci.getColumnType())){
				fieldObj.put("xtype", "textareafield");
				fieldObj.put("enforceMaxLength", Boolean.FALSE);
				fieldObj.put("maxLength","<jsfn>Number.MAX_VALUE</jsfn>");
				fieldObj.put("colspan",new Integer(cols));
				if("view".equals(saveType)){
					fieldObj.put("xtype", "container");
					fieldObj.put("layout", "<jsfn>{type:'hbox',align:'top'}</jsfn>");
					fieldObj.put("items","<jsfn>[{xtype:'box',html:'"+ci.getColumnDesc()+"',style:'word-break:break-all;text-align:right;margin-right:5px;',width:100},{xtype:'box',flex:10,style:'word-break:break-all;',html:'"+ci.getDefaultValue().replaceAll("\n", "<br>")+"'}]</jsfn>");
				}else{
					fieldObj.put("height", new Integer(200));
				}
				if(cols>1)
					fieldObj.put("width","<jsfn>compWidth/"+cols+"*("+cols+"-1)+405</jsfn>");
				int index = i-hiddenObjList.size();
				if(index%cols >0){
					HashMap preObj = (HashMap)fieldObjList.get(fieldObjList.size()-1);
					preObj.put("colspan", new Integer(cols-(index%cols)+1));
				}
					
			}else if(ci.getCodesetId()!=null && !"0".equals(ci.getCodesetId())){
				fieldObj.put("enforceMaxLength", Boolean.FALSE);
				fieldObj.put("maxLength","<jsfn>Number.MAX_VALUE</jsfn>");
				if("view".equals(saveType)){
			    		String value = ci.getDefaultValue();
			    		if(value==null || value.length()==0 || "`".equals(value))
			    			fieldObj.put("value","");
			    		else{
			    			String tmp[] = value.split("`");
			    			if(tmp.length==1)
				    			fieldObj.put("value",value);
				    		else
				    			fieldObj.put("value",tmp[1]);
			    		}
			    }else{
			    //if(ci.getCodesetType()==ci.CODESETTYPE_SELECT){
			    		if(ci.getOperationData()==null){
						fieldObj.put("xtype", "codecomboxfield");
						fieldObj.put("codesetid", ci.getCodesetId());
						fieldObj.put("codesource", ci.getCodesource());
						fieldObj.put("ctrltype", ci.getCtrltype());
						fieldObj.put("nmodule", ci.getNmodule());
					}else{
						fieldObj.put("xtype", "codeselectfield");
						fieldObj.put("valueField","dataValue");
						fieldObj.put("displayField","dataName");
						HashMap datastore = new HashMap();
						datastore.put("fields",new String[]{"dataValue","dataName"});
						datastore.put("data", "<jsfn>"+ci.getOperationData()+"</jsfn>");
						fieldObj.put("store", datastore);
						String value = ci.getDefaultValue();
						if(value!=null&& value.split("`").length>1){
							fieldObj.put("value", value.split("`")[0]);
						}
					}
			    	fieldObj.put("value", ci.getDefaultValue());
			    }
				
			}
		    if("view".equals(saveType) && !"M".equals(ci.getColumnType()))
		    	fieldObj.put("xtype", "displayfield");
		    //是否主键
			if(ci.isKey())
				indexKey.append(ci.getColumnId()).append(",");//+=ci.getColumnId()+",";
			//隐藏型指标
			if(ci.getLoadtype() == ci.LOADTYPE_ONLYLOAD){
				fieldObj.put("xtype", "hidden");
				hiddenObjList.add(fieldObj);
			}else
				fieldObjList.add(fieldObj);
		}
		fieldObjList.addAll(hiddenObjList);
		}catch(Exception e){
			e.printStackTrace();
		}
    	return fieldObjList;
    }


	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setItemsProperty(String itemsProperty) {
		this.itemsProperty = itemsProperty;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public void setSaveText(String saveText) {
		this.saveText = saveText;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCancelText(String cancelText) {
		this.cancelText = cancelText;
	}

	public void setDoScript(boolean doScript) {
		this.doScript = doScript;
	}

	public void setSaveAction(String saveAction) {
		this.saveAction = saveAction;
	}

	public void setCancelAction(String cancelAction) {
		this.cancelAction = cancelAction;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	
}
