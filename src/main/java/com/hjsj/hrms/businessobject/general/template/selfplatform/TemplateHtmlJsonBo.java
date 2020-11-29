package com.hjsj.hrms.businessobject.general.template.selfplatform;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.ExcelLayoutDao;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.impl.ExcelLayoutDaoImpl;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.vo.UploadContant;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.TemplatePage;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Mar 3, 2010 11:39:22 AM
 * </p>
 *
 * @author dengc
 * @version 5.0
 */
public class TemplateHtmlJsonBo {
    private Connection conn = null;
    /**模板号*/
	private int tabid=0;
    private UserView userview = null;
    /**获取dao*/
	private ExcelLayoutDao excelLayoutDao =null;

	private String moudle_id="1";
	//存放内容指标级联信息
	private Map<String, LazyDynaBean> relationMap=new HashMap<String, LazyDynaBean>();

	private TemplateUtilBo utilBo;
	private String idCardFielditem;//身份证号指标
    public String getMoudle_id() {
		return moudle_id;
	}
	public void setMoudle_id(String moudle_id) {
		this.moudle_id = moudle_id;
	}
    public TemplateHtmlJsonBo(int tabid, Connection con, UserView userview) {
        this.conn = con;
        this.tabid = tabid;
        this.userview = userview;
        this.excelLayoutDao=new ExcelLayoutDaoImpl(conn);
        this.utilBo = new TemplateUtilBo(this.conn,this.userview);
        this.idCardFielditem=getIdCardField();
    }
    /**
     * 获取到
     * @param form_file
     * @throws GeneralException
     */
	public JSONObject getLayoutBySet() throws GeneralException {
        JSONObject json=new JSONObject();
        //如果有设置的不显示页签 优先走这个
        String noShowPageNo="";
		try {
			//获取审批意见指标
	        TemplateTableBo tablebo=new TemplateTableBo(this.conn,this.tabid,this.userview);
	        String optionField=tablebo.getOpinion_field();
			/**获取页数-----------------*/
			ArrayList outlist = getPageList(this.tabid, false, noShowPageNo);

			json.put("module", this.moudle_id);//"1|2|3|4 //模块标识 "
            JSONArray pages_arr=new JSONArray();
            String cellComment="";

			for(int s=0;s<outlist.size();s++){
				TemplatePage pagevo = (TemplatePage) outlist.get(s);
				String pageidstr=pagevo.getPageId()+"";
				int pageid=Integer.parseInt(pageidstr);
				String pageTitle=pagevo.getTitle();
				JSONArray layout_arr=new JSONArray();
                JSONObject pages=new JSONObject();
				 /** 输出单元格 */
			    ArrayList celllist =getPageCell(pageid);
			    int num=0;//定义每行数量
			    int cnum=0;//定义每行实际数量
			    JSONObject layout=new JSONObject();
            	JSONArray row_arr=new JSONArray();
            	//初始化relatiaonMap字段
            	initRelationMap((ArrayList)celllist.clone());
            	int i=0;
			    for(;i<celllist.size();i++)
				{
					TemplateSet setBo=(TemplateSet)celllist.get(i);
					String field_name  = setBo.getField_name();
					String hz  = setBo.getHz();
					 hz=hz.replaceAll("\\{", "");
					 hz=hz.replaceAll("\\}", "");
					 hz=hz.replaceAll("`", "");
					String flag=setBo.getFlag();
					String field_type=setBo.getField_type();
					String tabFldName = setBo.getTableFieldName();
					//过滤掉审批意见指标
					if(StringUtils.isNotEmpty(optionField)&&(optionField+"_2").equalsIgnoreCase(tabFldName)){
						continue;
					}
					if("H".equals(flag))//汉字描述
					{
						continue;
					}else if(setBo.isSubflag()){
						if(num>0){
							//换行-------------------------------------------------
							layout.put("horizontal_id", "h"+pageid+(i+1));
		            		layout.put("columns_num", cnum+"");
		            		if(cnum==2){
		            			layout.put("columns_width", "50%,50%");
		            		}else if(cnum==3){
		            			layout.put("columns_width", "33%,33%,33%");
		            		}else{
		            			layout.put("columns_width", "100%");
		            		}
		                	layout.put("content", row_arr);
		                	/**教育经历 end*/
		            		layout_arr.add(layout);

		            		layout=new JSONObject();
		                	row_arr=new JSONArray();
		                	num=0;
		                	cnum=0;
						}
    					createCollapseEditor(row_arr, hz);
    					num=3;
    					cnum++;
    					if(num>0){
							//换行-------------------------------------------------
							layout.put("horizontal_id", "h"+pageid+(i+1));
		            		layout.put("columns_num", cnum+"");
		            		if(cnum==2){
		            			layout.put("columns_width", "50%,50%");
		            		}else if(cnum==3){
		            			layout.put("columns_width", "33%,33%,33%");
		            		}else{
		            			layout.put("columns_width", "100%");
		            		}
		                	layout.put("content", row_arr);
		                	/**教育经历 end*/
		            		layout_arr.add(layout);

		            		layout=new JSONObject();
		                	row_arr=new JSONArray();
		                	num=0;
		                	cnum=0;
						}
    					createDivPanel(row_arr, setBo, cellComment);
    					num=3;
    					cnum++;
    					continue;
    				}
					else if("A".equals(flag)|| "B".equals(flag)|| "K".equals(flag)) //指标
					{
					    if(((setBo.getHismode()==2||setBo.getHismode()==3||(setBo.getHismode()==4))&&setBo.getChgstate()==1)||(setBo.isSubflag()))
					    {
					    	if(!setBo.isSubflag()){
					    		/**条件1：序号定位| 2：(条件序号&&!最近第&&最初第)&&变化前) 3：子集（subflag=true）**/
					    		if(((setBo.getHismode()==2 || setBo.getHismode()==4) && (setBo.getMode()==0
					    				|| setBo.getMode()==2))){
					    			//序号定位 、条件定位的最近第、 最初第因为是一条记录  按普通当前记录显示方式显示 wangrd 20151026
					    			FieldItem fldItem = DataDictionary.getFieldItem(field_name,setBo.getSetname());
					    			if (fldItem!=null && "M".equalsIgnoreCase(fldItem.getItemtype())){
					    				if(num>0){
					    					//换行-------------------------------------------------
					    					layout.put("horizontal_id", "h"+pageid+(i+1));
					    					layout.put("columns_num", cnum+"");
					    					if(cnum==2){
					    						layout.put("columns_width", "50%,50%");
					    					}else if(cnum==3){
					    						layout.put("columns_width", "33%,33%,33%");
					    					}else{
					    						layout.put("columns_width", "100%");
					    					}
					    					layout.put("content", row_arr);
					    					/**教育经历 end*/
					    					layout_arr.add(layout);

					    					layout=new JSONObject();
					    					row_arr=new JSONArray();
					    					num=0;
					    					cnum=0;
					    				}
					    				createTextAreaEditor(row_arr, setBo, cellComment);
					    				num=3;
					    				cnum++;
					    				continue;
					    			}
					    		}
					    	}
					      else {
					    	  if(num>0){
					    		  //换行-------------------------------------------------
					    		  layout.put("horizontal_id", "h"+pageid+(i+1));
					    		  layout.put("columns_num", cnum+"");
					    		  if(cnum==2){
					    			  layout.put("columns_width", "50%,50%");
					    		  }else if(cnum==3){
					    			  layout.put("columns_width", "33%,33%,33%");
					    		  }else{
					    			  layout.put("columns_width", "100%");
					    		  }
					    		  layout.put("content", row_arr);
					    		  /**教育经历 end*/
					    		  layout_arr.add(layout);

					    		  layout=new JSONObject();
					    		  row_arr=new JSONArray();
					    		  num=0;
					    		  cnum=0;
					    	  }
					    	  createTextAreaEditor(row_arr, setBo, cellComment);
					    	  num=3;
					    	  cnum++;
							  continue;
					      }
					  }
					  else
					  {
						  if("D".equalsIgnoreCase(field_type))
						  {
//								createInputEditor(td,setBo);
						  }
						  else if("N".equalsIgnoreCase(field_type))
						  {
//								createInputEditor(td,setBo);
						  }
						  else if("M".equalsIgnoreCase(field_type))
						  {
							  if(num>0){
								  //换行-------------------------------------------------
								  layout.put("horizontal_id", "h"+pageid+(i+1));
								  layout.put("columns_num", cnum+"");
								  if(cnum==2){
									  layout.put("columns_width", "50%,50%");
								  }else if(cnum==3){
									  layout.put("columns_width", "33%,33%,33%");
								  }else{
									  layout.put("columns_width", "100%");
								  }
								  layout.put("content", row_arr);
								  /**教育经历 end*/
								  layout_arr.add(layout);

								  layout=new JSONObject();
								  row_arr=new JSONArray();
								  num=0;
								  cnum=0;
							  }
							  createTextAreaEditor(row_arr, setBo, cellComment);
							  num=3;
							  cnum++;
							  continue;
						  }
						  else if("A".equalsIgnoreCase(field_type))
						  {
//							createInputEditor(td,setBo);
						  }
					  }
					}
					else if("P".equals(flag)) //picture
					{
						continue;
					}
					else if("F".equals(flag)) //attachment
					{
						if(num>0){
							//换行-------------------------------------------------
							layout.put("horizontal_id", "h"+pageid+(i+1));
		            		layout.put("columns_num", cnum+"");
		            		if(cnum==2){
		            			layout.put("columns_width", "50%,50%");
		            		}else if(cnum==3){
		            			layout.put("columns_width", "33%,33%,33%");
		            		}else{
		            			layout.put("columns_width", "100%");
		            		}
		                	layout.put("content", row_arr);
		                	/**教育经历 end*/
		            		layout_arr.add(layout);

		            		layout=new JSONObject();
		                	row_arr=new JSONArray();
		                	num=0;
		                	cnum=0;
						}
						createAttachmentEditor(row_arr, setBo, cellComment);
						num=3;
						cnum++;
						continue;
					}
					else if("V".equals(flag))//临时变量
					{
//					    createInputEditor(td,setBo);
					}
					else if("C".equals(flag))//计算公式 wangrd 2013-12-30
					{
//					    createInputVarEditor(td,setBo);

					}
					else if("S".equals(flag))//签章 不考虑
					{
//						createSignatureEditor(td,setBo);
						continue;
					}else{
						continue;
					}
					if(num==3){
						//换行-------------------------------------------------
						layout.put("horizontal_id", "h"+pageid+(i+1));
	            		layout.put("columns_num", cnum+"");
	            		if(cnum==2){
	            			layout.put("columns_width", "50%,50%");
	            		}else if(cnum==3){
	            			layout.put("columns_width", "33%,33%,33%");
	            		}else{
	            			layout.put("columns_width", "100%");
	            		}
	                	layout.put("content", row_arr);
	                	/**教育经历 end*/
	            		layout_arr.add(layout);

	            		layout=new JSONObject();
	                	row_arr=new JSONArray();
	                	num=0;
	                	cnum=0;
					}
					createEditor(row_arr, setBo, cellComment);
			    	num++;
			    	cnum++;
				}
			    if(cnum>0){
			    	layout.put("horizontal_id", "h"+pageid+(i+1));
			    	layout.put("columns_num", cnum+"");
			    	if(cnum==2){
			    		layout.put("columns_width", "50%,50%");
			    	}else if(cnum==3){
			    		layout.put("columns_width", "33%,33%,33%");
			    	}else{
			    		layout.put("columns_width", "100%");
			    	}
			    	layout.put("content", row_arr);
			    	/**教育经历 end*/
			    	layout_arr.add(layout);
			    }
			    if(layout_arr.size()>0){
                	pages.put("page_desc", pageTitle);
                    pages.put("page_id", pageid+"");
                    pages.put("is_out_page", false);
                    pages.put("required", "");//true|false, //页签内容是否为必填项
                    pages.put("fill_status", "");//ok|part|null, //填写情况  ok：都填完了、 part：只填了部分、null：没填
                	pages.put("layout", layout_arr);
                	pages_arr.add(pages);
                }
			}
			createTitleImageEditor(json,null);
			json.put("pages", pages_arr);
		}   catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * @Title: getPageList
	 * @Description:  获取模板显示的页签
	 * @param @param isMobile 是否显示异动标签
	 * @param noShowPageNo  不显示那些页签
	 * @param @return
	 * @param @throws Exception
	 * @return ArrayList
	 */
	public ArrayList getPageList(int tabId, boolean isMobile, String noShowPageNo) throws Exception {
		ArrayList outlist = new ArrayList();
		try {
			TemplateUtilBo utilBo = new TemplateUtilBo(this.conn,
					this.userview);
			ArrayList list = utilBo.getAllTemplatePage(tabId);
			for (int i = 0; i < list.size(); i++) {
				TemplatePage pagebo = (TemplatePage) list.get(i);
				if(!"".equals(noShowPageNo)){//如果有设置的不显示页签 优先走这个
					String pageid =  String.valueOf(pagebo.getPageId());
					String pagearr [] = noShowPageNo.split(",");
					boolean noprint = false;
					for(String pid:pagearr){
						if(pid.equalsIgnoreCase(pageid)){
							noprint = true;
							break;
						}
					}
					if(noprint) {
                        continue;
                    }
				}else if(!pagebo.isShow()) {
					continue;
				}

				if (isMobile != pagebo.isMobile()) {
					continue;
				}
				outlist.add(pagebo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return outlist;
	}

	private int getMaxPageId(String tabId) {
		return excelLayoutDao.getMaxPageId(tabId);
	}
	/**
	 * 功能：初始化 relationMap 字段
	 * @param celllist
	 * @return
	 */
	private HashMap<String, LazyDynaBean> initSubsetRelationMap(TemplateSet setBo) {
		if(setBo==null){
			return new HashMap<String, LazyDynaBean>();
		}
		HashMap relationFieldMap=new HashMap();
		ArrayList subfiledlist=setBo.getSubFieldList();
		if(subfiledlist!=null&&subfiledlist.size()>0){
			for(int j=0;j<subfiledlist.size();j++){
				SubField itemFiled=(SubField) subfiledlist.get(j);
				if(StringUtils.isNotBlank(itemFiled.getRelation_field())&&2==setBo.getChgstate()){
					relationFieldMap.put(itemFiled.getFieldname(), itemFiled);
				}
			}
		}
		HashMap<String, LazyDynaBean> subRelationMap=new HashMap<String, LazyDynaBean>();
        if(relationFieldMap.size()>0){
        	String pre=setBo.getTableFieldName()+"_";
			Iterator iterator = relationFieldMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry entry=(Entry)	iterator.next();
				SubField subsetBo = (SubField)entry.getValue();
				String relationField=subsetBo.getRelation_field();
				String fatherRelationField="";
				for(int i=0;i<subfiledlist.size();i++){
					LazyDynaBean fieldBean=null;
					SubField codeBo=(SubField)subfiledlist.get(i);
					String fieldfldName = codeBo.getFieldname();
					String childRelationField ="";
					if(relationField.equalsIgnoreCase(fieldfldName)){
						if(subRelationMap.containsKey(subsetBo.getFieldname())){
							fieldBean=subRelationMap.get(subsetBo.getFieldname());
							childRelationField = (String) fieldBean.get("childRelationField");
							fatherRelationField=(String) fieldBean.get("fatherRelationField");
						}

						if(fieldBean==null){
							fieldBean=new LazyDynaBean();
						}

						if(StringUtils.isBlank(fatherRelationField)){
							fatherRelationField=pre+codeBo.getFieldname()+"`"+codeBo.getFieldname()+"&&"+codeBo.getFieldItem().getCodesetid();
						}else{
							fatherRelationField+=";"+pre+codeBo.getFieldname()+"`"+codeBo.getFieldname()+"&&"+codeBo.getFieldItem().getCodesetid();
						}
						fieldBean.set("fatherRelationField", fatherRelationField);
						subRelationMap.put(subsetBo.getFieldname(), fieldBean);

						//创建新的对象
						fieldBean=new LazyDynaBean();
						if(subRelationMap.containsKey(codeBo.getFieldname())){
							fieldBean=subRelationMap.get(codeBo.getFieldname());
							childRelationField = (String) fieldBean.get("childRelationField");
							fatherRelationField=(String) fieldBean.get("fatherRelationField");
						}else{
							fatherRelationField="";
							childRelationField="";
						}
						if(StringUtils.isBlank(childRelationField)){
							childRelationField=pre+subsetBo.getFieldname()+"`"+subsetBo.getFieldname()+"&&"+subsetBo.getFieldItem().getCodesetid();
						}else{
							childRelationField=";"+pre+subsetBo.getFieldname()+"`"+subsetBo.getFieldname()+"&&"+subsetBo.getFieldItem().getCodesetid();
						}

						fieldBean.set("childRelationField", childRelationField);
						fieldBean.set("fatherRelationField", fatherRelationField);
						subRelationMap.put(codeBo.getFieldname(), fieldBean);
					}
				}
			}
        }
        return subRelationMap;
	}

	/**
	 * 功能：初始化 relationMap 字段
	 * @param celllist
	 */
	private void initRelationMap(ArrayList celllist) {
		if(celllist.size()<=0){
			return;
		}
		HashMap relationFieldMap=new HashMap();

		for(int m=0;m<celllist.size();m++)
		{
			TemplateSet setBo=(TemplateSet)celllist.get(m);
			if(StringUtils.isEmpty(setBo.getCodeid())||"0".equals(setBo.getCodeid())){
				continue;
			}
            if(StringUtils.isNotBlank(setBo.getRelation_field())&&2==setBo.getChgstate()){
				relationFieldMap.put(setBo.getUniqueId(), setBo);
			}
		}

        if(relationFieldMap.size()>0){
			Iterator iterator = relationFieldMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry entry=(Entry)	iterator.next();
				TemplateSet setBo = (TemplateSet)entry.getValue();
				String relationField=setBo.getRelation_field();
				String uniqueId=(String)entry.getKey();
				String fatherRelationField="";
				for(int i=0;i<celllist.size();i++){
					LazyDynaBean fieldBean=null;
					TemplateSet codeBo=(TemplateSet)celllist.get(i);
					String fieldUniqueId = codeBo.getUniqueId();
					String fieldfldName = codeBo.getPageId()+"_"+codeBo.getGridno();
					String childRelationField ="";

					if((!uniqueId.equalsIgnoreCase(fieldUniqueId))&&relationField.equalsIgnoreCase(fieldfldName)){
						if(relationMap.containsKey(setBo.getUniqueId())){
							fieldBean=relationMap.get(setBo.getUniqueId());
							childRelationField = (String) fieldBean.get("childRelationField");
							fatherRelationField=(String) fieldBean.get("fatherRelationField");
						}

						if(fieldBean==null){
							fieldBean=new LazyDynaBean();
						}

						if(StringUtils.isBlank(fatherRelationField)){
							fatherRelationField=codeBo.getTableFieldName()+"`"+codeBo.getTableFieldName()+"&&"+codeBo.getCodeid();
						}else if(fatherRelationField.indexOf("`"+codeBo.getTableFieldName()+"&&")!=-1){
							continue;
						}else{
							fatherRelationField+=";"+codeBo.getTableFieldName()+"`"+codeBo.getTableFieldName()+"&&"+codeBo.getCodeid();
						}
						fieldBean.set("fatherRelationField", fatherRelationField);
						this.relationMap.put(setBo.getUniqueId(), fieldBean);

						//创建新的对象
						fieldBean=new LazyDynaBean();
						if(this.relationMap.containsKey(codeBo.getUniqueId())){
							fieldBean=relationMap.get(codeBo.getUniqueId());
							childRelationField = (String) fieldBean.get("childRelationField");
							fatherRelationField=(String) fieldBean.get("fatherRelationField");
						}else{
							fatherRelationField="";
							childRelationField="";
						}
						if(StringUtils.isBlank(childRelationField)){
							childRelationField=setBo.getTableFieldName()+"`"+setBo.getTableFieldName()+"&&"+setBo.getCodeid();
						}else if(childRelationField.indexOf("`"+setBo.getTableFieldName()+"&&")!=-1){
							continue;
						}else{
							childRelationField+=";"+setBo.getTableFieldName()+"`"+setBo.getTableFieldName()+"&&"+setBo.getCodeid();
						}

						fieldBean.set("childRelationField", childRelationField);
						fieldBean.set("fatherRelationField", fatherRelationField);
						relationMap.put(codeBo.getUniqueId(), fieldBean);
					}
				}
			}
        }


	}
	/**
	 * Postil 批注
	 * @param td
	 * @param hz
	 */
	public void createPostilEditor(JSONArray td, TemplateSet setBo,String cellComment) {
		JSONObject text=new JSONObject();
		String hz=setBo.getHz();
		hz=hz.replaceAll("\\{", "");
		hz=hz.replaceAll("\\}", "");
		hz=hz.replaceAll("`", "");

		text.put("type", UploadContant.type_describe);
        text.put("content", hz);
        text.put("cellComment", cellComment);
        text.put("relation_id", setBo.getTableFieldName());
        td.add(text);
	}

	public void createDescribeEditor(JSONArray td, String hz,String cellComment) {
		JSONObject text=new JSONObject();
		text.put("type", UploadContant.type_describe);
        text.put("content", hz);
        //添加Excel批注
        text.put("cellComment", cellComment);
//        text.put("hidden", false);
        td.add(text);
	}
	/**
	 * 功能：describe描述  无样式
	 * @param td
	 * @param hz
	 */
	public void createDescribeBlankEditor(JSONArray td, String hz,String cellComment) {
		JSONObject text=new JSONObject();
		text.put("type", UploadContant.type_describe_blank);
        text.put("content", hz);
        //添加Excel批注
        text.put("cellComment", cellComment);
//        text.put("hidden", false);
        td.add(text);
	}
	/**
	 * 功能：增加审批意见
	 * @param td
	 * @param hz
	 */
	public void createOptionEditor(JSONArray td, String element_id,String cellComment) {
		JSONObject text=new JSONObject();
		text.put("type", UploadContant.type_opinion);
        text.put("element_id", element_id);
        //添加Excel批注
        text.put("cellComment", cellComment);
//        text.put("hidden", false);
        td.add(text);
	}
	/**
	 * 生成唯一值
	 * @return
	 */
	public String getUniqueOne(){
		UUID uuid=UUID.randomUUID();
        String uuidStr=uuid.toString();
        return uuidStr;
	}
	private void createTitleImageEditor(JSONObject json, TemplateSet setBo) {
		JSONObject avatar_obj=new JSONObject();
		avatar_obj.put("avatar_id", "p1");
		avatar_obj.put("label", "照片");
		avatar_obj.put("file_name", "");//file_name:"xxxxx.bmp", //用户上传的文件名
		avatar_obj.put("file_path", "");//file_path:"subdomain\template_201\T386\T427", //文件路径
		if(setBo==null){
			avatar_obj.put("required",false);
			avatar_obj.put("readonly",true);
			avatar_obj.put("relation_id",getUniqueOne());//数据库中的字段名称
			json.put("avatar", avatar_obj);
			return;
		}
		if(setBo.isYneed())
		{
			avatar_obj.put("required",true);
		}else{
			avatar_obj.put("required",false);
		}

		if(setBo.getChgstate()==2){
			avatar_obj.put("readonly",false);
		}else{
			avatar_obj.put("readonly",true);
		}
		setElementPublicProperty(avatar_obj,setBo);
		json.put("avatar", avatar_obj);
	}


	/**
	    * @Title: getPageCell
	    * @Description: 返回指定页的所有单元格celllist
	    * @param @param tabId
	    * @param @param pagenum
	    * @param @return
	    * @return ArrayList
	    */
	    public ArrayList getPageCell(int pageNum) {

	    	return utilBo.getPageCell(this.tabid,pageNum);
	    }


	    /**
		 * 如果为null返回“”字符串
		 * @param value
		 * @return
		 */
		private static String nullToSpace(String value)
		{
			if(value==null) {
                return "";
            } else {
                return value;
            }
		}

		/**
		 * 创建输入框
		 * @param td
		 * @param cellComment:Excel批注信息
		 * @throws GeneralException
		 */
		private void createEditor(JSONArray td,TemplateSet setBo,String cellComment) throws GeneralException
		{
			if(setBo.getFlag()==null|| "".equalsIgnoreCase(setBo.getFlag())) {
                setBo.setFlag("H");
            }
			String flag=setBo.getFlag();
			String field_type=setBo.getField_type();
			String field_name = setBo.getField_name(); //xgq 电子签章
			if("H".equals(flag)){//汉字描述 不考虑
			}else if("A".equals(flag)|| "B".equals(flag)|| "K".equals(flag)){ //指标
				if(((setBo.getHismode()==2||setBo.getHismode()==3||(setBo.getHismode()==4))&&setBo.getChgstate()==1)||(setBo.isSubflag())){
					/**条件1：序号定位| 2：(条件序号&&!最近第&&最初第)&&变化前) 3：子集（subflag=true）**/
			      if(!setBo.isSubflag()) {
			    	  if(((setBo.getHismode()==2 || setBo.getHismode()==4) && (setBo.getMode()==1
	                      || setBo.getMode()==3))||setBo.getHismode()==3){
		                //序号定位 、条件定位的最近第、 最初第因为是一条记录  按普通当前记录显示方式显示 wangrd 20151026
		                 FieldItem fldItem = DataDictionary.getFieldItem(field_name,setBo.getSetname());
		                 if (fldItem!=null && "M".equalsIgnoreCase(fldItem.getItemtype())){
		                     createTextAreaEditor(td, setBo,cellComment);
		                 }else if(fldItem.getItemlength()>=255&&"0".equals(setBo.getCodeid())&&"A".equals(setBo.getField_type())&&"A".equals(setBo.getFlag())){
		                	 createTextAreaEditor(td, setBo,cellComment);
		                 }
		                 else {
		                     createInputEditor(td,setBo,cellComment);
		                 }
		              }else {
		            	  createInputEditor(td,setBo,cellComment);
		              }
			      }else {
	            	  createDivPanel(td,setBo,cellComment);
			      }
			  }
			  else
			  {
				  if("D".equalsIgnoreCase(field_type))
				  {
						createDatePickerEditor(td,setBo,cellComment);
				  }
				  else if("N".equalsIgnoreCase(field_type))
				  {
						createInputEditor(td,setBo,cellComment);
				  }
				  else if("M".equalsIgnoreCase(field_type))
				  {
					  createTextAreaEditor(td,setBo,cellComment);
				  }
				  else if("A".equalsIgnoreCase(field_type))
				  {
					createInputEditor(td,setBo,cellComment);
				  }
			  }
			}
			else if("P".equals(flag)) //picture
			{
				createImageEditor(td,setBo,cellComment);
			}
			else if("F".equals(flag)) //attachment
			{
				createAttachmentEditor(td,setBo,cellComment);
			}
			else if("V".equals(flag))//临时变量
			{
				if("A".equals(setBo.getField_type())){
					RecordVo varVo= setBo.getVarVo();
					if(varVo.getInt("fldlen")>=255&&varVo.getInt("ntype")==2){
						createTextAreaEditor(td, setBo,cellComment);
					}else{
						createInputEditor(td,setBo,cellComment);
					}
				}else if("D".equals(setBo.getField_type())){
					createDatePickerEditor(td,setBo,cellComment);
				}else{
					createInputEditor(td,setBo,cellComment);
				}
			}
			else if("C".equals(flag))//计算公式 wangrd 2013-12-30
			{
			    createInputEditor(td,setBo,cellComment);
			}
		}

		/**
		 * 创建收缩分割线
		 * @param td
		 */
		private void createCollapseEditor(JSONArray td,String hz) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_collapse);
			//每个面板的唯一性标识
			text.put("name",getUniqueOne());//单元格唯一键值
//			text.put("relation_id",hz);//数据库中的字段名称
	        text.put("title", hz);
	        //是否为手风琴模式
	        text.put("accordion", false);
	        td.add(text);
		}

		/**
		 * 创建收缩分割线
		 * @param td
		 */
		private void createIviderEditor(JSONArray td,String content) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_divider);
			text.put("element_id",getUniqueOne());//单元格唯一键值
			//设置分割线方向  horizontal| vertical
			text.put("direction","horizontal");//horizontal| vertical （横向|纵向）
			text.put("content", content);
			text.put("contentPosition", "center");
	        td.add(text);
		}

		/**
		 * 创建历史记录输出面板
		 * @param td
		 * @throws GeneralException
		 */
		private void createDivPanel(JSONArray td,TemplateSet setBo,String cellComment) throws GeneralException {
			String setname=setBo.getSetname();
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_table);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("label", "");
	        //label 如果没有 ，就设置label_hz
	        text.put("label_hz", hz);
	        text.put("label_align", "left");//默认放置左边
	        //最大输入长度
	        text.put("maxlength", "20");
	        text.put("disabled", false);//是否禁用
	        text.put("hidden", false);////是否可见
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			//是否显示序号
			text.put("serial_flag", true);
			//是否有审核列 无’0’ 读 ‘1’写 ‘2’
			text.put("isverify", "2");
			//最多支持录入行数
			text.put("maxnum", "100");
			//表格高度
			text.put("height", setBo.getRheight());//当前单元格高度
			text.put("maxheight", setBo.getRheight());//当前单元格高度
			//审核列下拉选项，只在isverify为’1’和’2’时有，其数据格式可传 [{verify_desc:'通过',verify_code:'2'},{verify_desc:'不通过', verify_code:'1'}]
			text.put("verify_list", "[{verify_desc:'通过',verify_code:'2'},{verify_desc:'不通过', verify_code:'1'}]");
			//表格头部工具栏  数据格式 [{button_id:'b1',button_desc:'批量审核',function_id:'XXXXXXXXX',button_type:''}]
			String buttons=getSubSetButtons(setBo);
			text.put("buttons", buttons);
			//添加Excel批注
			text.put("cellComment", cellComment);

			String subFields=setBo.getSubFields();
			HashMap<String, LazyDynaBean> subRelationMap=initSubsetRelationMap(setBo);
			ArrayList subfiledlist=setBo.getSubFieldList();
			if(subfiledlist!=null&&subfiledlist.size()>0){
				JSONArray subitems=new JSONArray();
				for(int j=0;j<subfiledlist.size();j++){
					SubField itemFiled=(SubField) subfiledlist.get(j);
					String itemid=itemFiled.getFieldname();
					if("attach".equals(itemid)){
						createAttachmentEditor(subitems,itemFiled,setBo,cellComment);
						continue;
					}
					FieldItem fielditem=itemFiled.getFieldItem();
					JSONObject subitem=new JSONObject();
					subitem.put("type", UploadContant.type_input);
					fielditem=fielditem==null?DataDictionary.getFieldItem(itemid):fielditem;
					if(fielditem==null){
						throw new GeneralException(hz+"子集中"+itemFiled.getTitle()+"指标已被删除，请重新定义模板！");
					}
					//添加指标解释
					String itemmemo=fielditem.getExplain();
					if(itemmemo==null) {
                        itemmemo="";
                    }
					itemmemo = itemmemo.replaceAll("\r\n", "\n");
			        itemmemo = itemmemo.replaceAll("\n", "<br>");
			        if("<br>".equals(itemmemo.trim())) {
                        itemmemo="";
                    }
			        //设置指标解释
			        subitem.put("itemmemo", itemmemo);

					if(fielditem.getItemlength()>=255&&"0".equals(fielditem.getCodesetid())&&"A".equals(fielditem.getItemtype())){//大于255的字符型指标看做大文本处理
						subitem.put("type", UploadContant.type_textarea);
					}
					String itemdesc=itemFiled.getTitle();
			        subitem.put("label", itemdesc);
					subitem.put("column_id", itemid);
					subitem.put("element_id",setBo.getTableFieldName()+"_"+itemid);//单元格唯一键值 列是唯一的
					if (!"0".equals(fielditem.getCodesetid())){
						subitem.put("codesetid",fielditem.getCodesetid());
						//代码型指标关联的联动指标
						subitem.put("relation_field",itemid);
						subitem.put("type",UploadContant.type_select);
						int lay_num=this.excelLayoutDao.getLayerByCodesetid(fielditem.getCodesetid());
						subitem.put("lay", lay_num);
						if(subRelationMap.containsKey(itemid)){
							//需要关联模板的elementid`relationid
							LazyDynaBean subfieldBean = subRelationMap.get(itemid);
							subitem.put("linked_child_elementid_rationid",subfieldBean.get("childRelationField")==null?"":subfieldBean.get("childRelationField"));
							subitem.put("linked_parent_elementid_rationid",subfieldBean.get("fatherRelationField")==null?"":subfieldBean.get("fatherRelationField"));
						}
						//lay>1 选择任意一级选项
						subitem.put("checkStrictly", false);
				        if("UM".equalsIgnoreCase(fielditem.getCodesetid())||"UN".equalsIgnoreCase(fielditem.getCodesetid())){
				        	subitem.put("checkStrictly", true);
				        }else{
				        	//获取末端代码：
				        	subitem.put("checkStrictly", this.excelLayoutDao.getLeafCode(fielditem.getCodesetid()));
				        }

					}else{
						subitem.put("codesetid","");
						subitem.put("relation_field", "");
					}
			        //
			        subitem.put("hidden",false);
					if(setBo.isYneed())
					{
						subitem.put("required",true);
					}else{
						subitem.put("required",false);
					}
					if(setBo.getChgstate()==2){
						subitem.put("readonly",false);
					}else{
						subitem.put("readonly",true);
					}
					if("true".equalsIgnoreCase(itemFiled.getHis_readonly())){
						subitem.put("his_readonly",true);
					}else{
						subitem.put("his_readonly",false);
					}
					String va=itemFiled.getValign();
					String[] valign=getHValign(Integer.parseInt(va));
			        String hAlign= valign[1];
					subitem.put("valign", hAlign);
					//子集指标添加最大长度
					subitem.put("maxlength", fielditem.getItemlength());
					String a=itemFiled.getAlign();
					if(("N").equalsIgnoreCase(fielditem.getItemtype())){
						String[] align=getHValign(Integer.parseInt(a));
						subitem.put("align", align[0]);
						int dec=fielditem.getDecimalwidth();
		                if(Integer.valueOf(itemFiled.getSlop())<fielditem.getDecimalwidth()) {
		                	dec = Integer.valueOf(itemFiled.getSlop());
		                }
						String format_pattern="*";//表示整数
						if(dec>0){
							format_pattern+=".";
							for(int d=0;d<dec;d++){
								format_pattern+="?";
							}
						}
						// *.* 表示浮点型  *.??? 表示3位小数   * 表示整数    空表示非数值型输入框
						subitem.put("number_format",format_pattern);
						//数值型 formattype:'number'
						subitem.put("formattype","number");
					}else{
						//除数值全部居左
						subitem.put("align", "left");
					}

					subitem.put("default_value",itemFiled.getDefaultvalue());
					subitem.put("fixed", false);
//					subitem.put("placeholder", "请输入"+itemdesc);
//					//0,1,2   无，看，写   默认为无
//					subitem.put("postil_flag", "0");
//					subitem.put("postil_msg", "");
					subitem.put("width",itemFiled.getTitleWidth());
					if (("D".equals(fielditem.getItemtype()))){
						if(fielditem.getItemlength()>10){ //日期格式支持小时：分钟
							subitem.put("format",itemFiled.getSlop());
							subitem.put("type",UploadContant.type_datePicker);
						}
						else{
							subitem.put("format",itemFiled.getSlop());
							subitem.put("type",UploadContant.type_datePicker);
						}

						//后台接受日期的格式
						String value_format="yyyy-MM-dd";
						switch(fielditem.getItemlength()){
						case 16:
							value_format="yyyy-MM-dd HH:mm";
							break;
						case 18:
							value_format="yyyy-MM-dd HH:mm:ss";
							break;
						default:
							value_format="yyyy-MM-dd";
							break;
						}
						subitem.put("value_format", value_format);
					}
					/**添加该节点*/
					subitems.add(subitem);
				}
				text.put("columns", subitems);
			}
			/**添加该节点*/
			td.add(text);
		}
		/**
		 * 功能：获取当前子集buttons
		 * "[{button_id: 'b1',button_desc: '新增',function_id: 'XXXXXXXXX',button_type: 'add'}
		 * ,{button_id: 'b2',button_desc: '删除',function_id: 'XXXXXXXXX',button_type: 'del'}]"
		 *
		 * @param setBo
		 * @return
		 */
		private String getSubSetButtons(TemplateSet setBo) {
			JSONArray btnArr=new JSONArray();

			//button按钮设定
			//变化前子集 只保留刷新按钮
			//变化后子集 显示所有子集按钮
			boolean btnPre=false;
			if(2==setBo.getChgstate()){
				btnPre=true;
			}

			//新增
			JSONObject btn=new JSONObject();
			if(btnPre){
				btn.put("button_id", "b1");
				btn.put("button_desc", "新增");
				//暂定 ZC00006310
				btn.put("function_id", "ZC00006310");
				btn.put("button_type", "add");
				btnArr.add(btn);
				//插入
				btn=new JSONObject();
				btn.put("button_id", "b2");
				btn.put("button_desc", "插入");
				btn.put("function_id", "ZC00006311");
				btn.put("button_type", "insert");
				btnArr.add(btn);

				//删除
				btn=new JSONObject();
				btn.put("button_id", "b3");
				btn.put("button_desc", "删除");
				btn.put("function_id", "ZC00006312");
				btn.put("button_type", "delete");
				btnArr.add(btn);

				//导入
				btn=new JSONObject();
				btn.put("button_id", "b4");
				btn.put("button_desc", "导入");
				btn.put("function_id", "ZC00006306");
				btn.put("button_type", "import");
				btnArr.add(btn);

				//置顶
				btn=new JSONObject();
				btn.put("button_id", "b5");
				btn.put("button_desc", "置顶");
				btn.put("function_id", "ZC00006313");
				btn.put("button_type", "top");
				btnArr.add(btn);
			}


			return btnArr.toString();
		}
		/**
		 * 创建输出框INPUT
		 * @param td
		 * cellComment：Excel批注信息
		 */
		private void createInputEditor(JSONArray td,TemplateSet setBo,String cellComment) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			int dec=0;

			String itemmemo="";
			if(fielditem==null){
				//临时变量
				text.put("maxlength", "100");
				dec=setBo.getDisformat();
				//用于标识临时变量
				text.put("isVar", setBo.isSpecialItem()?false:true);
				//设置指标解释
		        text.put("itemmemo", itemmemo);
			}else{
				text.put("maxlength", fielditem.getItemlength());
				dec=fielditem.getDecimalwidth();
                if(setBo.getDisformat()<fielditem.getDecimalwidth()) {
                	dec = setBo.getDisformat();
                }
				//用于标识临时变量
				text.put("isVar", false);

				itemmemo=fielditem.getExplain();
				if(itemmemo==null) {
                    itemmemo="";
                }
				itemmemo = itemmemo.replaceAll("\r\n", "\n");
		        itemmemo = itemmemo.replaceAll("\n", "<br>");
		        if("<br>".equals(itemmemo.trim())) {
                    itemmemo="";
                }
		        //设置指标解释
		        text.put("itemmemo", itemmemo);
			}
			//添加Excel批注
			text.put("cellComment", cellComment);
			//如果是代码选项
			if(!"0".equals(setBo.getCodeid())){
				createSelectEditor(td, setBo,cellComment);
				return;
			}
			if(("N").equalsIgnoreCase(setBo.getField_type())){

				String format_pattern="*";//表示整数
				if(dec>0){
					format_pattern+=".";
					for(int d=0;d<dec;d++){
						format_pattern+="?";
					}
				}
				// *.* 表示浮点型  *.??? 表示3位小数   * 表示整数    空表示非数值型输入框
				text.put("number_format",format_pattern);
				//数值型 formattype:'number'
				text.put("formattype","number");
			}else{
				text.put("number_format","");
				if(field_name.equalsIgnoreCase(this.idCardFielditem)){
					//身份证号指标 标识。
					text.put("formattype","idCard");
				}
			}

			text.put("type", UploadContant.type_input);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边

	        text.put("minlength", 0);
	        String[] align=getHValign(setBo.getAlign());
	        String hAlign= align[0];
	        text.put("align", hAlign);
			if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}

			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			/***
			 * 是否显示输入数字 默认false
			 */
			text.put("showWordLimit",false);
			//输入框占位文本
			text.put("placeholder", "");
			//是否禁用
			text.put("disabled", false);
			//是否可以清空选项
			text.put("clearable", true);
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			text.put("postil_msg", "");
			text.put("postil_username", "");

			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 创建富文本编辑器  类似html编辑器
		 * @param td
		 */
		private void createEditorEditor(JSONArray td,TemplateSet setBo,String cellComment) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_editor);
			setElementPublicProperty(text,setBo);

			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			//添加指标解释
			String itemmemo=fielditem.getExplain();
			if(itemmemo==null) {
                itemmemo="";
            }
			itemmemo = itemmemo.replaceAll("\r\n", "\n");
	        itemmemo = itemmemo.replaceAll("\n", "<br>");
	        if("<br>".equals(itemmemo.trim())) {
                itemmemo="";
            }
	        //设置指标解释
	        text.put("itemmemo", itemmemo);
	        //添加Excel批注
	        text.put("cellComment", cellComment);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("label", hz);
	        text.put("label_align", "center");//默认放置左边
//			if(setBo.isYneed())
//			{
//				text.put("required",true);
//			}else{
//				text.put("required",false);
//			}

			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			text.put("postil_msg", "");
			text.put("postil_username", "");

			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 创建Link文字链接
		 * @param td
		 */
		private void createLinkEditor(JSONArray td,TemplateSet setBo) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_link);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //是否下划线
	        text.put("underline", false);
	        //输入框显示的值
	        text.put("value", "");
	        //原生 href 属性
	        text.put("href", "");
	        /**
	         * 图标类名(可不传，文字前面的图片，图片必须为图片库中如：el-icon-edit)
	         */
	        text.put("icon","");
			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			//是否禁用
			text.put("disabled", false);
			//是否清空
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}



		/**
		 * 创建Radio 输入框
		 * @param td
		 */
		private void createRadioEditor(JSONArray td,TemplateSet setBo) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_radio);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("show_type", UploadContant.radio_show_type_normal);
	        String codes = getDMInfo(fielditem.getCodesetid());
	        text.put("radio_values", codes);
	        text.put("value", "");

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//是否清空
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}
		/**
		 * 根据代码号，获取json字符串形式的 值，value
		 * 如：
		 * {
		 * 'bj':'北京',
		 * 'sh':'上海',
		 * 'sz':'深圳'
		 * }
		 * @param codesetid
		 * @return
		 */
		private String getDMInfo(String codesetid) {
			if(StringUtils.isEmpty(codesetid)||"0".equals(codesetid)){
				return "";
			}
	        ArrayList<CodeItem> codeitems = AdminCode.getCodeItemList(codesetid);
	        JSONObject codes=new JSONObject();
	        for(int k=0;k<codeitems.size();k++){
	        	CodeItem c=codeitems.get(k);
	        	codes.put(c.getCodeitem(), c.getCodename());
	        }
			return codes.toString();
		}

		/**
		 * 创建Checkbox多选框
		 * @param td
		 */
		private void createCheckboxEditor(JSONArray td,TemplateSet setBo) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_checkbox);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        String codes = getDMInfo(fielditem.getCodesetid());
	        text.put("checkbox_values", codes);

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //勾选的最小数量为0，最大数量为10
	        text.put("min", 0);
	        text.put("max", 100);
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//是否清空
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 创建Select选择器
		 * @param td
		 */
		private void createSelectEditor(JSONArray td,TemplateSet setBo,String cellComment) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			String itemmemo="";
			if(fielditem==null){
				//用于标识临时变量
				text.put("isVar", setBo.isSpecialItem()?false:true);
				//设置指标解释
		        text.put("itemmemo", itemmemo);
			}else{
				//用于标识临时变量
				text.put("isVar", false);
				itemmemo=fielditem.getExplain();
				if(itemmemo==null) {
                    itemmemo="";
                }
				itemmemo = itemmemo.replaceAll("\r\n", "\n");
		        itemmemo = itemmemo.replaceAll("\n", "<br>");
		        if("<br>".equals(itemmemo.trim())) {
                    itemmemo="";
                }
		        //设置指标解释
		        text.put("itemmemo", itemmemo);
			}
			//添加Excel批注
			text.put("cellComment", cellComment);
			text.put("type", UploadContant.type_select);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        int lay_num=this.excelLayoutDao.getLayerByCodesetid(setBo.getCodeid());
	        text.put("lay", lay_num);

	        text.put("codesetid",setBo.getCodeid());
	        //输入框仅显示最后一级
	        text.put("show_all_levels", false);
	        //lay>1 选择任意一级选项
	        text.put("checkStrictly", false);
	        if("UM".equalsIgnoreCase(setBo.getCodeid())||"UN".equalsIgnoreCase(setBo.getCodeid())){
	        	text.put("checkStrictly", true);
	        }else{
	        	//获取末端代码：
	        	text.put("checkStrictly", this.excelLayoutDao.getLeafCode(setBo.getCodeid()));
	        }
	        text.put("placeholder", "");
	        //是否多选
	        text.put("multiple", false);
	        //是否可搜索
	        text.put("filterable", true);
	        //搜索条件无匹配时显示的文字
	        text.put("noMatchText", "无匹配数据");
	        //选项为空时显示的文字
	        text.put("noDataText", "无数据");
	        //是否可以清空选项
	        text.put("clearable", true);
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			//需要关联模板的elementid`relationid
			if(relationMap.containsKey(setBo.getUniqueId())){
				LazyDynaBean fieldBean = relationMap.get(setBo.getUniqueId());
				text.put("linked_child_elementid_rationid",fieldBean.get("childRelationField")==null?"":fieldBean.get("childRelationField"));
				text.put("linked_parent_elementid_rationid",fieldBean.get("fatherRelationField")==null?"":fieldBean.get("fatherRelationField"));
			}
			//根据 relation
			/**添加该节点*/
			td.add(text);
		}
		/**
		 * 创建Select选择器
		 * @param td
		 */
		private void createTimePickerEditor(JSONArray td,TemplateSet setBo,String cellComment) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_dateTimePicker);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");

	        // 两种展现形式  1.单个 normal 2 区间 range  人事异动 不存在区间
	        text.put("show_type", "normal");

	        // 两种展现形式  1.间隔 space 2.任意 anyTime
	        text.put("time_show_type", "anyTime");
	        //输入框显示的值
	        text.put("value", "");

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //show_type==1 输入框占位文本
	        text.put("placeholder", "");
	        //show_type==2 开始输入框占位文本
	        text.put("start_placeholder", "");
	        //show_type==2  结束输入框占位文本
	        text.put("end_placeholder", "");
	        //time_show_type==1  开始时间
	        text.put("start", "09:00");
	      //time_show_type==1  结束时间
	        text.put("end", "18:00");
	      //time_show_type==1  间隔时间
	        text.put("step", "00:30");
	        //time_show_type==2 可选时间段，例如'18:30:00 - 20:30:00'或者传入数组['09:30:00 - 12:00:00', '14:30:00 - 18:30:00']
	        text.put("selectableRange", "");
	        //time_show_type==2 时间格式化  小时：HH，  分：mm，  秒：ss， AM/PM：A
	        text.put("format", "");
	        //选择范围时的分隔符
	        text.put("range_separator", "-");
	        //自定义头部图标的类名
	        text.put("prefix_icon", "el-icon-time");
	        //搜索条件无匹配时显示的文字
	        text.put("clear_icon", "el-icon-circle-close");
	        //是否可以清空选项
	        text.put("clearable", true);
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
	        if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
	        text.put("editable", false);
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 创建Select选择器
		 * @param td
		 */
		private void createDatePickerEditor(JSONArray td,TemplateSet setBo,String cellComment) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);

			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_datePicker);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");

			int disformat=setBo.getDisformat();
			text.put("format", disformat+"");
			//后台接受日期的格式
			String value_format="yyyy-MM-dd";
			int itemLen=0;
			if(fielditem==null){
				//判断当前指标是否时特殊指标
				if(setBo.isSpecialItem()){
					//用于标识临时变量
					text.put("isVar", false);
				}else{
					RecordVo varVo=setBo.getVarVo();
					itemLen=varVo.getInt("fldlen");
					//用于标识临时变量
					text.put("isVar", true);
				}
				//设置指标解释
		        text.put("itemmemo", "");
			}else{
				itemLen=fielditem.getItemlength();
				text.put("isVar", false);
				//添加指标解释
				String itemmemo=fielditem.getExplain();
				if(itemmemo==null) {
                    itemmemo="";
                }
				itemmemo = itemmemo.replaceAll("\r\n", "\n");
		        itemmemo = itemmemo.replaceAll("\n", "<br>");
		        if("<br>".equals(itemmemo.trim())) {
                    itemmemo="";
                }
		        //设置指标解释
		        text.put("itemmemo", itemmemo);
			}
			//添加Excel批注
			text.put("cellComment", cellComment);
			switch(itemLen){
			case 16:
				value_format="yyyy-MM-dd HH:mm";
				break;
			case 18:
				value_format="yyyy-MM-dd HH:mm:ss";
				break;
			default:
				value_format="yyyy-MM-dd";
				break;
			}
			text.put("value_format", value_format);
	        /**
	         * 两种展现形式
	         * 1.year 年
	         * 2.month 月
	         * 3.date 日期
	         * 4.week 周
	         * 5.datetime 日期时间
	         * 6.datetimeRange 日期时间范围
	         * 7.dateRange 日期范围
	         * 8. monthRange 月范围
	         */
	        if(itemLen>10){ //日期格式支持小时：分钟
	        	text.put("show_type", "datetime");
//	        	text.put("format", "yyyy-MM-dd HH:mm");
	        }
	        else{
	        	text.put("show_type", "date");
//	        	text.put("format", "yyyy-MM-dd");
	        }
	        //输入框显示的值
	        text.put("value", "");

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //show_type==1 输入框占位文本
	        text.put("placeholder", "");
	        //show_type==2 开始输入框占位文本
	        text.put("start_placeholder", "");
	        //show_type==2  结束输入框占位文本
	        text.put("end_placeholder", "");
	        //time_show_type==1  开始时间
	        text.put("align", "left");
	      //选择范围时的分隔符
	        text.put("range_separator", "-");
	      //date_show_type=week   周起始日
	        text.put("firstDayOfWeek", "7");
	        //自定义头部图标的类名
	        text.put("prefix_icon", "el-icon-time");
	        //搜索条件无匹配时显示的文字
	        text.put("clear_icon", "el-icon-circle-close");
	        //是否可以清空选项
	        text.put("clearable", true);
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
	        if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
	        text.put("editable", false);
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}
		/**
		 * 创建Text 文本框
		 * @param td
		 */
		private void createTextEditor(JSONArray td,HashMap map) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_text);
			text.put("element_id",getUniqueOne());//单元格唯一键值

	        String hz=(String) map.get("hz");
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
			text.put("content", hz);
	        text.put("label", "");
	        text.put("label_align", "");
	        text.put("align", getHValign((int)map.get("align"))[0]);
			//是否清空
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}


		/**
		 * 创建文本输入框
		 * @param td
		 */
		private void createTextAreaEditor(JSONArray td,TemplateSet setBo,String cellComment) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			if(fielditem==null) {
				fielditem=new FieldItem();
				fielditem.setItemid(field_name);
				fielditem.setInputtype(0);
			}
			//指标的文本编辑类型(大文本) //0普通编辑器 1 富文本编辑器
			if(1==fielditem.getInputtype()){
				//走富文本编辑器
				createEditorEditor(td, setBo,cellComment);
				return;
			}
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_textarea);
			setElementPublicProperty(text,setBo);

			//添加指标解释
			String itemmemo=fielditem.getExplain();
			if(itemmemo==null) {
                itemmemo="";
            }
			itemmemo = itemmemo.replaceAll("\r\n", "\n");
	        itemmemo = itemmemo.replaceAll("\n", "<br>");
	        if("<br>".equals(itemmemo.trim())) {
                itemmemo="";
            }
	        //设置指标解释
	        text.put("itemmemo", itemmemo);
	        //添加Excel批注
	        text.put("cellComment", cellComment);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
			text.put("value", "");
	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //大文本字符长度为10的话，传值为空，不限制长度
	        text.put("maxlength", (fielditem.getItemlength()==10||fielditem.getItemlength()==0)?"":fielditem.getItemlength());
	        text.put("minlength", 0);
	        /***
	         * 是否显示输入数字 默认false
	         */
	        text.put("showWordLimit",false);
	        //输入框占位文本
	        text.put("placeholder", "");
	        //输入框行数
			text.put("rows", UploadContant.textarea_rows);
	        //自适应内容高度，可传入对象，如：{ minRows: 2, maxRows: 6 }
			text.put("autosize", false);
			//是否禁用
			text.put("disabled", false);

			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			//是否可以清空选项
			text.put("clearable", true);
			if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//是否可见
			text.put("hidden", false);

			String[] align=getHValign(setBo.getAlign());
	        String hAlign= align[0];
	        text.put("align", hAlign);

	      //0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			/**syl0 是否有批注 没有这些内容*/
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 照片
		 * @param td
		 */
		private void createImageEditor(JSONArray td,TemplateSet setBo,String cellComment) {
//			Element text=new Element("input");
//			text.setAttribute("type","image");
//			text.setAttribute("src","/images/photo.jpg");
//			setElementPublicProperty(text,setBo);
//			StringBuffer style=new StringBuffer();
//			style.append("height:");
//			style.append(setBo.getRheight()-7);
//			style.append("px;");
//			style.append("width:");
//			style.append(setBo.getRwidth()-5);
//			style.append("px;");
//			text.setAttribute("style",style.toString());
//			text.setAttribute("extra","photo");
			/**业务类型为0时，才需要上传照片*/


		}
		/**
		 * 附件
		 * @param td
		 */
		private void createAttachmentEditor(JSONArray td, SubField subfile,TemplateSet setBo,String cellComment) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_upload);
			text.put("label", "附件");
//			String hz=subfile.getTitle();
//			hz=hz.replaceAll("\\{", "");
//			hz=hz.replaceAll("\\}", "");
//			hz=hz.replaceAll("`", "");
//			if(StringUtils.isNotEmpty(hz)){
//				text.put("label", hz);
//			}
			text.put("label_align", "left");
			text.put("hidden", false);
			text.put("align", "center");
			text.put("element_id",getUniqueOne());//单元格唯一键值
			text.put("relation_id",setBo.getSetname()+"attach_"+setBo.getChgstate());//数据库中的字段名称
			String itemid=subfile.getFieldname();
			if(StringUtils.isEmpty(itemid)) {
                text.put("column_id", "attach");
            } else {
                text.put("column_id", itemid);
            }
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");//是否有批注
			text.put("postil_msg", "");  //批注信息

			if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//附件61063 ZCSB：（Oracle库），批次管理，岗位聘用批次，流程设计表单授权中，附件无法设置写权限，见附件。
			text.put("readonly",false);
			/**
			 * value:[{
						   "real_file_name":"xxxxx.pdf", //存储文件名
						   "file_name":"xxxxx.pdf", //用户上传的文件名
						   "file_path":"subdomain\template_201\T386\T427", //文件路径
					     },。。。]
			 */
			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 附件
		 * @param td
		 */
		private void createAttachmentEditor(JSONArray td, TemplateSet setBo,String cellComment) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_upload);
			text.put("label", "附件");
			String hz=setBo.getHz();
			hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
			if(StringUtils.isNotEmpty(hz)){
				text.put("label", hz);
			}
			//添加Excel批注
			text.put("cellComment", cellComment);
			text.put("label_align", "left");
			text.put("hidden", false);
			text.put("align", "center");
			setElementPublicProperty(text,setBo);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");//是否有批注
			text.put("postil_msg", "");  //批注信息

			if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//附件61063 ZCSB：（Oracle库），批次管理，岗位聘用批次，流程设计表单授权中，附件无法设置写权限，见附件。
			text.put("readonly",false);
			/**
			 * value:[{
						   "real_file_name":"xxxxx.pdf", //存储文件名
						   "file_name":"xxxxx.pdf", //用户上传的文件名
						   "file_path":"subdomain\template_201\T386\T427", //文件路径
					     },。。。]
			 */
			/**添加该节点*/
			td.add(text);
		}
		/**
		* @Title: setElementPublicProperty
		* @Description: 设置元素的公用属性，id field_name 用于前台显示数据及修改数据
		* @param @param td
		* @return void
		*/
		private void setElementPublicProperty(JSONObject text,TemplateSet setBo) {
			text.put("element_id",setBo.getTableFieldName());//单元格唯一键值
			text.put("relation_id",setBo.getTableFieldName());//数据库中的字段名称
//			text.put("fieldsetid","dataset_"+setBo.getPageId());//数据结构对象的Id
//			text.put("recordsetid","dataset_"+setBo.getPageId());//数据对象的Id
		}

		/**
		 * 排列方式
		 * @param ali
		 * @return
		 */
		protected String[] getHValign(int ali) {
			String[] align = new String[2];
			switch(ali)
			{
			case 0:
				align[0] = "left";
				align[1] = "top";
				break;
			case 1:
				align[0] = "center";
				align[1] = "top";
				break;
			case 2:
				align[0] = "right";
				align[1] = "top";
				break;
			case 3:
				align[0] = "left";
				align[1] = "bottom";
				break;
			case 4:
				align[0] = "center";
				align[1] = "bottom";
				break;
			case 5:
				align[0] = "right";
				align[1] = "bottom";
				break;
			case 6:
				align[0] = "left";
				align[1] = "middle";
				break;
			case 7:
				align[0] = "center";
				align[1] = "middle";
				break;
			case 8:
				align[0] = "right";
				align[1] = "middle";
				break;
			}
			return align;
		}

		/**
		 * 得到主集中存放身份证号的字段
		 * @return
		 */
		private String getIdCardField()
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			String field=sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
			return field;
		}

}
