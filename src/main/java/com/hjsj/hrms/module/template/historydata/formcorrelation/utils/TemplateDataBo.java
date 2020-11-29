package com.hjsj.hrms.module.template.historydata.formcorrelation.utils;

import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 模板数据类，列表与卡片界面共用。
* @Title: TemplateDataBo
* @Description:
* @author: hej
* @date 2019年11月19日 下午5:14:52
* @version
 */
public class TemplateDataBo {
	    private Connection conn = null;
	    private UserView userView = null;
	    private TemplateParam paramBo = null;
	    private int tabId;
	    private String archive_id = "";
	    private ContentDAO dao; 
	    private TemplateUtilBo utilBo= null;
		private String hmuster_sql="";//当前模板处理人员的sql
    
	    /**
		 * 初始化构造函数 tabid
		 */
	    public TemplateDataBo (Connection conn,UserView userview,int tabid,String archive_id){ 
	        this.tabId=tabid;	
	        this.archive_id = archive_id;
	        init(conn,userview);
	        this.paramBo = new TemplateParam(conn, userview, tabid, archive_id);
	    }
	    /**
		 * 初始化构造函数 传递TemplateParam类，不用新创建了
		 */
	    public TemplateDataBo (Connection conn,UserView userview,TemplateParam param){                       
	    	this.paramBo = param;
	    	this.tabId=param.getTabId();
	    	this.archive_id = param.getArchive_id();
	    	init(conn,userview);
	    }
	    
	    /**
		 * 初始化本来，创建一些公共类
		 */ 
	    private void init(Connection conn,UserView userview){
	    	this.conn = conn;
	    	this.userView = userview;
	    	dao = new ContentDAO(conn);                        
	    	utilBo= new TemplateUtilBo(conn, userview);
	    }
	    
	    /**   
	     * @Title: getFieldPrivMap   
	     * @Description:获取模板指标的读写权限。   
	     * @param @param allCellList 返回有单元格celllist
	     * @param @param taskId 任务ids
	     * @param @return 
	     * @return HashMap 
	     * @throws   
	    */
	    public HashMap getFieldPrivMap(ArrayList allCellList, String taskId) {
            HashMap filedPrivMap = new HashMap();
            /** (变化后指标)数据录入不判断子集和指标权限, 0判断(默认值),1不判断 */
            String insertDataCtrl = this.paramBo.getUnrestrictedMenuPriv_Input();
            HashMap nodePrivMap = new HashMap(); 
            for (int i = 0; i < allCellList.size(); i++) {
                TemplateSet setBo = (TemplateSet) allCellList.get(i);
                String flag = setBo.getFlag();
                if (flag == null || "".equals(flag) || "H".equals(flag)) {
                    continue;
                }
                String uniqueId = setBo.getUniqueId();
                String tableFieldName = setBo.getTableFieldName();
                if ("C".equalsIgnoreCase(flag) ) {
                    filedPrivMap.put(uniqueId, "2");
                    filedPrivMap.put(tableFieldName, "2");
                    continue;
                }
                if("F".equalsIgnoreCase(flag)) {//附件需判断其归档至哪里，多媒体子集和子集需要判断权限
                	String flag_miea = "2";
                	if(paramBo.getInfor_type()==1) {
	                	String archive_attach_to = paramBo.getArchive_attach_to();	
	            		if("".equals(archive_attach_to))
	            			archive_attach_to = "A00";
	                	if("A01".equalsIgnoreCase(archive_attach_to)) {
	                		flag_miea = "2";
	                	}else if("A00".equalsIgnoreCase(archive_attach_to)) {//归档到多媒体
	                		flag_miea = userView.analyseTablePriv("A00");
	                	}else{//归档到子集
	                		flag_miea = this.userView.analyseTablePriv(archive_attach_to);
	                	}
                	}
                	if("0".equals(this.paramBo.getNeedJudgPre()))
                		flag_miea = "2";
                	String fillInfo = (String) this.userView.getHm().get("fillInfo");
                	if("1".equals(fillInfo))
                		flag_miea = "2";
                	filedPrivMap.put(uniqueId, flag_miea);
                    filedPrivMap.put(tableFieldName, flag_miea);
                	continue;
                }
                if("S".equalsIgnoreCase(flag)){//获取签章节点上设置的读写权限
                	 String  state="2";
                	 if (nodePrivMap.get("s_"+setBo.getPageId()+"_"+setBo.getGridno()) != null) {// 如果是无权限,跳出
                         state = (String) nodePrivMap.get("s_"+setBo.getPageId()+"_"+setBo.getGridno());
                     }
                	 filedPrivMap.put(uniqueId, state);
                     filedPrivMap.put("S_"+setBo.getPageId()+"_"+setBo.getGridno(), state);
                     continue;
                }
                if("V".equalsIgnoreCase(flag)){
                	if("1".equals(setBo.getReadOnly())){
                		filedPrivMap.put(uniqueId, "1");
                        filedPrivMap.put(tableFieldName, "1");
                	}else{
                		filedPrivMap.put(uniqueId, "2");
                        filedPrivMap.put(tableFieldName, "2");
                	}
                	continue;
                }
                String state = "0";
                if (!setBo.isSubflag()) {// 这里用来判断非子集的字段
                    state = this.userView.analyseFieldPriv(setBo.getField_name());
                    boolean specialItem = setBo.isSpecialItem();                
                    if (specialItem)
                    	state = "2";
                } else {// 子集数据
                    state = this.userView.analyseTablePriv(setBo.getSetname());
                } 
                if ("1".equals(insertDataCtrl)&&setBo.getChgstate()==2)
                	state="2";
                
                // 处理是没有构库的指标
                if (setBo.isABKItem()) {              	
                	String tableFieldName_lin = "";
                	if(setBo.isSubflag()){//子集
                		if(tableFieldName.startsWith("t_")){//判断子集权限
                			tableFieldName_lin = tableFieldName.substring(2,tableFieldName.length());
                			if(tableFieldName_lin.endsWith("_1")){
                				tableFieldName_lin = setBo.getSetname().toLowerCase()+"_1";
                			}else if(tableFieldName_lin.endsWith("_2")){
                				tableFieldName_lin = setBo.getSetname().toLowerCase()+"_2";
                			}
                		}else
                			tableFieldName_lin = tableFieldName;
                	}else
                		tableFieldName_lin = tableFieldName;
                    if (nodePrivMap.get(tableFieldName_lin) != null) {// 如果是无权限,跳出
                        state = (String) nodePrivMap.get(tableFieldName_lin);
                    }
                    
                    if(!setBo.isExistsThisField()){
                    	state = "0";
                    }
                }
                if ("C".equals(flag)){//计算项
                    state="1";
                }
                filedPrivMap.put(uniqueId, state);
                filedPrivMap.put(tableFieldName, state);
            }
            return filedPrivMap;
        }

  		/**
  		 * 获得历史数据对应sql
  		 * @param record_id
  		 * @param archive_year
  		 * @return
  		 * @throws GeneralException 
  		 */
  		public String getArchiveSql(String record_id, String archive_year) throws GeneralException {
  			String sql = "";
  			try {
  				sql = "select 1 submitflag2,ins_id,task_id as realtask_id,'' seqnum2,";
				//先解析json数据，得到a0100 or b0110 or e01a1
				HashMap archMap = this.analysisJson2Map(record_id,archive_year);
				String objectid = "";
				if(paramBo.getInfor_type()==1) {
            		String a0100 = (String) archMap.get("a0100");
            		String basepre = (String) archMap.get("basepre");
            		objectid = "'"+basepre+"`"+a0100+"'";
            		sql+="nbase basepre,"+a0100+" a0100,";
            	}else if(paramBo.getInfor_type()==2){
            		String b0110 = (String) archMap.get("b0110");
            		objectid = b0110;
            	}
            	else if(paramBo.getInfor_type()==3){
            		String e01a1 = (String) archMap.get("e01a1");
            		objectid = e01a1;
            	}
				sql+=objectid+" objectid_noencrypt,"+objectid+" objectid,'1' state,";
				if (paramBo.getInfor_type() == 2
						|| paramBo.getInfor_type() == 3) {//单位名称
					if (paramBo.getOperationType() == 8
							|| paramBo.getOperationType() == 9) {
						sql+="'' to_id,";
					}
					if (paramBo.getOperationType() == 5) {
						sql+= "name as codeitemdesc_2";
					} else {
						sql+= "name as codeitemdesc_1";
					}
				}
				if (paramBo.getInfor_type() == 1) {
					if (paramBo.getOperationType() == 0) {//人员调入型
						sql+= "name as a0101_2";
					} else {
						sql+= "name as a0101_1";
					}
				}
				sql+=" from t_data_"+archive_year+" where record_id="+record_id;
  			}catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return sql;
		}
	    /**
	     * 解析json文件，转成map
	     * @param archive_id
	     * @param archive_year
	     * @return
	     * @throws GeneralException 
	     */
	    public HashMap analysisJson2Map(String record_id, String archive_year) throws GeneralException {
	    	RowSet rset = null;
	    	HashMap archiveMap = new HashMap();
	    	InputStream in=null;
	    	InputStreamReader inReader=null;
			try {
				/*ConstantXml constantXml = new ConstantXml(this.conn,"FILEPATH_PARAM");
		        String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
		        if(StringUtils.isNotBlank(rootDir)){
		        	   rootDir=rootDir.replace("\\",File.separator);          
		        	   if (!rootDir.endsWith(File.separator)) 
		        		   rootDir =rootDir+File.separator;   
		        }*/
				ArrayList param = new ArrayList();
				String sql ="select * from t_data_"+archive_year +" where record_id=?";
				param.add(record_id);
				rset = dao.search(sql,param);
				if(rset.next()) {
					String jsonStr = "";
					int tabid = rset.getInt("tabid");
					String ins_id = rset.getString("ins_id");
					archiveMap.put("ins_id", ins_id);
					String file_patch = rset.getString("file_patch");
					String archiveid = rset.getString("archive_id");
					ArrayList allCellList = this.utilBo.getArchiveCell(tabid,archiveid,-1);//解析xml得到模板相关属性
					//得到文件流，分析json文件 
					//xus 20/5/15 【60565】VFS+UTF-8+达梦：人事异动，历史数据，选择已归档的单据点击浏览打印，显示undefined，见附件
					if(StringUtils.isNotBlank(file_patch)) {
						in = VfsService.getFile(file_patch);
					}
					if(in==null) {
						throw new GeneralException(ResourceFactory.getProperty("template.processArchiving.filepathmissmessage"));
					}
					int ch = 0;
		            StringBuffer sb = new StringBuffer();
		            Reader reader = new InputStreamReader(in);
		            while ((ch = reader.read()) != -1) {
		                sb.append((char) ch);
		            }
		            reader.close();
					/*File jsonFile = new File(rootDir+file_patch);
					if(!jsonFile.exists()) {//文件不存在
	    				throw new GeneralException(ResourceFactory.getProperty("template.processArchiving.filepathmissmessage"));
	        	    }
		            FileReader fileReader = new FileReader(jsonFile);
		            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
		            int ch = 0;
		            StringBuffer sb = new StringBuffer();
		            while ((ch = reader.read()) != -1) {
		                sb.append((char) ch);
		            }
		            fileReader.close();
		            reader.close();*/
		            jsonStr = sb.toString();
		            JSONObject jobj= JSONObject.fromObject(jsonStr);
	            	if(paramBo.getInfor_type()==1) {
	            		String a0100 = (String) jobj.get("a0100");
	            		String a0000 = (String) jobj.get("a0000");
	            		String basepre = (String) jobj.get("basepre");
	            		archiveMap.put("a0100", a0100);
	            		archiveMap.put("a0000", a0000);
	            		archiveMap.put("basepre", basepre);
	            	}else if(paramBo.getInfor_type()==2){
	            		String b0110 = (String) jobj.get("b0110");
	            		archiveMap.put("b0110", b0110);
	            	}
	            	else if(paramBo.getInfor_type()==3){
	            		String e01a1 = (String) jobj.get("e01a1");
	            		archiveMap.put("e01a1", e01a1);
	            	}
	            	//循环指标
	            	for(int i=0;i<allCellList.size();i++) {
		        		TemplateSet setbo = (TemplateSet) allCellList.get(i);
		        		if(setbo.getFlag()==null|| "".equalsIgnoreCase(setbo.getFlag()))
							setbo.setFlag("H");
		        		if("H".equals(setbo.getFlag())) {
		        			continue;
		        		}
		        		String fldname  = setbo.getTableFieldName();
						if(StringUtils.isNotBlank(fldname))
							fldname =fldname.toLowerCase();
						String value = jobj.containsKey(fldname)?jobj.getString(fldname):"";
						if(setbo.isABKItem()) {
							if("M".equals(setbo.getField_type())) {
								archiveMap.put(fldname, value);
							}else if("N".equals(setbo.getField_type())) {
								archiveMap.put(fldname, value);
							}else if("D".equals(setbo.getField_type())) {
								value = value.replace(".", "-");
								archiveMap.put(fldname, value);
							}else {
								archiveMap.put(fldname, value);
							}
						}else if("P".equals(setbo.getFlag())){//照片
							if(StringUtils.isNotEmpty(value)) {
								archiveMap.put(fldname, value);
							}else {
								value=jobj.containsKey("fileid")?jobj.getString("fileid"):"";
								archiveMap.put(fldname, value);
							}
							String ext = jobj.getString("ext");
							archiveMap.put("ext", ext);
						}else if("S".equals(setbo.getFlag())){//签章
							archiveMap.put(fldname, value);
						}else if("F".equals(setbo.getFlag())){//附件 特殊处理
							String attachmenttype = setbo.getAttachmentType();
							JSONArray jsonArray = jobj.containsKey("t_wf_file_"+attachmenttype)?(JSONArray) jobj.get("t_wf_file_"+attachmenttype):null;
							ArrayList wfList = new ArrayList();
							if(jsonArray!=null&&jsonArray.size()>0) {
								for(int j=0;j<jsonArray.size();j++) {
									JSONObject jsonObject = jsonArray.getJSONObject(j);
									HashMap wfMap = new HashMap();
									wfMap.put("file_id", jsonObject.getString("file_id"));
									wfMap.put("name", jsonObject.getString("name"));
									wfMap.put("sortname", jsonObject.containsKey("sortname")?jsonObject.getString("sortname"):"");
									wfMap.put("ext", jsonObject.getString("ext"));
									wfMap.put("ins_id", jsonObject.getString("ins_id"));
									wfMap.put("filetype", jsonObject.containsKey("filetype")?jsonObject.getString("filetype"):"");
									wfMap.put("create_time", jsonObject.getString("create_time"));
									wfMap.put("create_user", jsonObject.getString("create_user"));
									wfMap.put("fullname", jsonObject.containsKey("fullname")?jsonObject.getString("fullname"):"");
									wfMap.put("content",jsonObject.containsKey("content")?jsonObject.getString("content"):"");
									wfMap.put("tabid", jsonObject.getString("tabid"));
									wfMap.put("basepre", jsonObject.containsKey("basepre")?jsonObject.getString("basepre"):"");
									wfMap.put("attachmenttype", jsonObject.getString("attachmenttype"));
									wfMap.put("objectid", jsonObject.containsKey("objectid")?jsonObject.getString("objectid"):"");
									wfMap.put("filepath", jsonObject.containsKey("filepath")?jsonObject.getString("filepath"):"");
									wfMap.put("state", jsonObject.containsKey("state")?jsonObject.getString("state"):"");
									wfMap.put("i9999", jsonObject.containsKey("i9999")?jsonObject.getString("i9999"):"");
									wfList.add(wfMap);
								}
							}
							archiveMap.put("t_wf_file_"+attachmenttype, wfList);
						}
	            	}
		        }
			}catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally {
				PubFunc.closeIoResource(in);
				PubFunc.closeIoResource(inReader);
			}
			return archiveMap;
		}
		/*------类属性----------------*/
		public TemplateParam getParamBo() {
			return paramBo;
		}
		public void setParamBo(TemplateParam paramBo) {
			this.paramBo = paramBo;
		}
		public String getHmuster_sql() {
			return hmuster_sql;
		}
		public TemplateUtilBo getUtilBo() {
			return utilBo;
		}
		public void setUtilBo(TemplateUtilBo utilBo) {
			this.utilBo = utilBo;
		}
		public String getArchive_id() {
			return archive_id;
		}
		public void setArchive_id(String archive_id) {
			this.archive_id = archive_id;
		}
		
}

