package com.hjsj.hrms.module.template.templatetoolbar.printout.transaction;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject.TemplateApplyPrepareBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.bjca.seal.SealVerify;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * <p>Title:PrintPrepareTrans.java</p>
 * <p>Description>:调用打印控件前的准备及校验 参考类JudgeLlexpr2Trans:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-4-27 下午03:43:36</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class PrintPrepareTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
            String sysType = frontProperty.getSysType();
            String moduleId = frontProperty.getModuleId();
            String approveFlag = frontProperty.getApproveFlag();
            String returnFlag = frontProperty.getReturnFlag();
            String tabId = frontProperty.getTabId();
            String taskId = frontProperty.getTaskId();
            String isDelete = frontProperty.getOtherParam("isDelete");
            TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn, this.userView);
            TemplateParam paramBo=new TemplateParam(this.frameconn,this.userView,Integer.parseInt(tabId));
    		int signatureType = paramBo.getTemplateModuleParam().getSignatureType();
            String tableName = utilBo.getTableName(moduleId, Integer.parseInt(tabId), taskId);
            /** 批量审批 */
            ArrayList tasklist = null;
            if (frontProperty.isBatchApprove()) {
                tasklist = getTaskList(taskId);
            } else {
                tasklist = new ArrayList();
                tasklist.add(taskId);
            }
            String ins_ids = "";
            for (int i = 0; i < tasklist.size(); i++) {
                if (i != 0)
                    ins_ids = ins_ids + ",";
                ins_ids = ins_ids + utilBo.getInsId((String) tasklist.get(i));
            }

            StringBuffer objStr = new StringBuffer("");
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            StringBuffer buf = new StringBuffer();
            buf.append("select * from ");
            buf.append(tableName);
            if (!"0".equals(taskId)) {
                buf.append(" where 1=1 ");
                buf.append(" and exists (select null from t_wf_task_objlink where " + tableName + ".seqnum=t_wf_task_objlink.seqnum and " + tableName + ".ins_id=t_wf_task_objlink.ins_id  ");
                if ("1".equals(returnFlag) || "2".equals(returnFlag)) {
                    buf.append(" and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
                }
                buf.append(" and  submitflag=1 and  task_id in (");
                for (int i = 0; i < tasklist.size(); i++) {
                    if (i != 0)
                        buf.append(",");
                    buf.append(tasklist.get(i));
                }
                buf.append(")");

                if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
                	buf.append(" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ) ");
				}else {
					buf.append(" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");
				}
            } else {
                if (frontProperty.isSelfApply()){
                    buf.append(" where ");
                    buf.append(" basepre='");
                    buf.append(this.userView.getDbname());
                    buf.append("' and a0100='");
                    buf.append(this.userView.getA0100());
                    buf.append("'");
                }
                else {
                    buf.append(" where submitflag=1");
                    
                }
            }
            /**51838 V7.6.1人事异动：业务办理/由任务监控进入后不能查看的表单却可以打印，没有按权限控制*/
            /**将人事异动进入表单里的人事权限逻辑拿过来了，放到此处*/
            if("4".equals(returnFlag)) {
	            String strB0110Where= getManageSqlWhere(tableName,paramBo);
	        	String tmp= strB0110Where.replace("T.", "");
	        	if (strB0110Where.length()>0){
	        		if(buf.indexOf("where")!=-1)
	        			buf.append(" and "+tmp);
	        		else
	        			buf.append(" where "+tmp); 				
	        	}
            }
            /****syl end*******/
            buf.append(this.getOrderBy(tabId)); // 加默认顺序,
            // 解决9054：中船_打印预演人员顺序和界面顺序显示不一致
            this.frowset = dao.search(buf.toString());
            /** 求每个对应的实例 */
            /** 安全平台改造,将basepre和a0100加密 * */
            Des des = new Des();
            while (this.frowset.next()) {
            	//BJCA电子签章将签章图片保存
            	String signature = this.frowset.getString("signature");
            	if(signature!=null&&!"".equals(signature)&&signatureType==1){
            		analysisSignatureXml(dao,signature);
            	}
                if ("1".equals(frontProperty.getInforType())) {
                    if (!"0".equals(taskId))
                        objStr.append("`" + des.EncryPwdStr(this.frowset.getString("basepre")) + "|" + des.EncryPwdStr(this.frowset.getString("a0100")) + "|" + this.frowset.getString("ins_id"));
                    else
                        objStr.append("`" + des.EncryPwdStr(this.frowset.getString("basepre")) + "|" + des.EncryPwdStr(this.frowset.getString("a0100")) + "|0");
                } else if ("2".equals(frontProperty.getInforType())) {
                    if (!"0".equals(taskId))
                        objStr.append("`" + des.EncryPwdStr(this.frowset.getString("b0110")) + "|" + des.EncryPwdStr(this.frowset.getString("b0110")) + "|" + this.frowset.getString("ins_id"));
                    else
                        objStr.append("`" + des.EncryPwdStr(this.frowset.getString("b0110")) + "|" + des.EncryPwdStr(this.frowset.getString("b0110")) + "|0");
                } else if ("3".equals(frontProperty.getInforType())) {
                    if (!"0".equals(taskId))
                        objStr.append("`" + des.EncryPwdStr(this.frowset.getString("E01A1")) + "|" + des.EncryPwdStr(this.frowset.getString("E01A1")) + "|" + this.frowset.getString("ins_id"));
                    else
                        objStr.append("`" + des.EncryPwdStr(this.frowset.getString("E01A1")) + "|" + des.EncryPwdStr(this.frowset.getString("E01A1")) + "|0");
                }
            }

            if (objStr.length() > 0)
                this.getFormHM().put("objStr", objStr.substring(1));
            else
                this.getFormHM().put("objStr", "");
            
            if (objStr.length() <1)
                throw new GeneralException("未选中打印对象!");
            String judgeisllexpr="";
            //TemplateParam paramBo = new TemplateParam(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
            if (paramBo.getOperationType() != 0) {
                if ("1".equals(frontProperty.getInforType())){
                    //调用报批的校验
                    TemplateApplyPrepareBo prepareBo=new TemplateApplyPrepareBo(this.frameconn,this.userView,paramBo,frontProperty); 
                    judgeisllexpr =prepareBo.judgeBusinessRule();
                }
                    
            }
            this.getFormHM().put("judgeisllexpr", judgeisllexpr);
            this.getFormHM().put("hosturl", this.userView.getServerurl());
            this.getFormHM().put("dbtype", Sql_switcher.searchDbServerFlag() + "");
            this.getFormHM().put("username", this.userView.getUserName());
            this.getFormHM().put("userfullname", this.userView.getUserFullName());
            this.getFormHM().put("superUser", this.userView.isSuper_admin() ? "1" : "0");
            this.getFormHM().put("tablepriv", this.userView.getTablepriv()+"");
            this.getFormHM().put("nodepriv", getFieldPriv(frontProperty.getTaskId(),paramBo));
            // todo 怎么取
            EncryptLockClient lockclient = (EncryptLockClient) this.getFormHM().get("lock");
            String _version = this.userView.getVersion() + "";
            // String license=lockclient.getLicenseCount();
            // if(license.equals("0"))
            // _version="100"+_version;
            // String usedday=lockclient.getUseddays()+"";
            this.getFormHM().put("_version", _version);
            this.getFormHM().put("usedday", "0");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    private String getOrderBy(String tabId) {
		String orderBy = " order by a0000";
		String subModuleId = "templet_"+tabId;
		TableDataConfigCache tableCacheList = (TableDataConfigCache) userView.getHm().get(subModuleId);
        if(tableCacheList!=null)
        {
        	String sortSql = tableCacheList.getSortSql();
        	HashMap customParamHM = tableCacheList.getCustomParamHM()==null?new HashMap():tableCacheList.getCustomParamHM();
			String property = customParamHM.get("property")==null?"":(String)customParamHM.get("property");
			String direction = customParamHM.get("direction")==null?"":(String)customParamHM.get("direction");
			if(StringUtils.isNotBlank(property)&&StringUtils.isNotBlank(direction)) {
        		orderBy = " order by "+property+ " "+direction;
			}
			else if(sortSql!=null&&sortSql.trim().length()>0)
        	{
        		orderBy = sortSql;
        	}
        }
		return orderBy;
	}

	private ArrayList getTaskList(String batch_task) throws GeneralException {
        String[] lists = StringUtils.split(batch_task, ",");
        ArrayList list = new ArrayList();
        for (int i = 0; i < lists.length; i++)
            list.add(lists[i]);
        return list;

    }
    private String getFieldPriv(String taskIds, TemplateParam paramBo) throws GeneralException {
        String fields = this.userView.getFieldpriv().toString();
        fields = fields.toUpperCase();
        TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn, this.userView);
        ArrayList cellList = utilBo.getAllCell(paramBo.getTabId());
        TemplateDataBo dataBo = new TemplateDataBo(this.frameconn, this.userView, paramBo);
        HashMap filedPrivMap = dataBo.getFieldPrivMap(cellList, taskIds);
        if (filedPrivMap != null) {
            Iterator iterator = filedPrivMap.entrySet().iterator();
            String key = "";
            String value = "";
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                key = (String) entry.getKey();
                key = key.toUpperCase();
                value = (String) entry.getValue();
                if (key.indexOf("_") != -1) {
                    key = key.substring(0, key.indexOf("_"));
                    if (fields.indexOf(key) != -1) {
                        if ("0".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", "");
                                fields = fields.replace(key + "1", "");
                                fields = fields.replace(key + "2", "");
                            } else {
                                fields = fields.replace("," + key + "0", "");
                                fields = fields.replace("," + key + "1", "");
                                fields = fields.replace("," + key + "2", "");
                            }
                        } else if ("1".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", key + "1");
                                fields = fields.replace(key + "1", key + "1");
                                fields = fields.replace(key + "2", key + "1");
                            } else {
                                fields = fields.replace("," + key + "0", "," + key + "1");
                                fields = fields.replace("," + key + "1", "," + key + "1");
                                fields = fields.replace("," + key + "2", "," + key + "1");
                            }
                        } else if ("2".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", key + "2");
                                fields = fields.replace(key + "1", key + "2");
                                fields = fields.replace(key + "2", key + "2");
                            } else {
                                fields = fields.replace("," + key + "0", "," + key + "2");
                                fields = fields.replace("," + key + "1", "," + key + "2");
                                fields = fields.replace("," + key + "2", "," + key + "2");
                            }
                        }
                    } else {
                        fields = fields + "," + key + value;
                    }
                } else {
                    if (fields.indexOf(key) != -1) {
                        if ("0".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", "");
                                fields = fields.replace(key + "1", "");
                                fields = fields.replace(key + "2", "");
                            } else {
                                fields = fields.replace("," + key + "0", "");
                                fields = fields.replace("," + key + "1", "");
                                fields = fields.replace("," + key + "2", "");
                            }
                        } else if ("1".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", key + "1");
                                fields = fields.replace(key + "1", key + "1");
                                fields = fields.replace(key + "2", key + "1");
                            } else {
                                fields = fields.replace("," + key + "0", "," + key + "1");
                                fields = fields.replace("," + key + "1", "," + key + "1");
                                fields = fields.replace("," + key + "2", "," + key + "1");
                            }
                        } else if ("2".equals(value)) {
                            if (fields.startsWith(key)) {
                                fields = fields.replace(key + "0", key + "2");
                                fields = fields.replace(key + "1", key + "2");
                                fields = fields.replace(key + "2", key + "2");
                            } else {
                                fields = fields.replace("," + key + "0", "," + key + "2");
                                fields = fields.replace("," + key + "1", "," + key + "2");
                                fields = fields.replace("," + key + "2", "," + key + "2");
                            }
                        }
                    } else {
                        fields = fields + "," + key + value;
                    }

                }
            }
        }
        return fields;
    }
    private void analysisSignatureXml(ContentDAO dao,String signature) {
    	RowSet rowSet=null;
    	DbSecurityImpl dbS = new DbSecurityImpl();
    	InputStream photoStream = null;
    	FileOutputStream out = null;
    	PreparedStatement prestmt = null;
    	try {
			Document doc = PubFunc.generateDom(signature);
			List<Element> elelist = doc.getRootElement().getChildren();
			for(int j = 0; j < elelist.size(); j++){
				Element ele = elelist.get(j);
				String documentid = ele.getAttributeValue("DocuemntID");
				if("BJCA".equals(documentid)){
					List<Element> list = ele.getChildren();
					for (int i = 0; i < list.size(); i++) {
						Element e = list.get(i);
						if("item".equals(e.getName())){
							String SignatureID = e.getAttributeValue("SignatureID");
							rowSet = dao.search("select * from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'");
							while(rowSet.next()){
								String caimg = rowSet.getString("caimg");
								String signaturetext = rowSet.getString("signaturetext");
								caimg = caimg==null?"":caimg;
								if("".equals(caimg)){
									//表单原文
									String plain = "hjsoft";
									SealVerify sealVerify = new SealVerify();
									sealVerify.setCoding("GBK");
									
									if (!sealVerify.doSealVerify(plain, signaturetext)) {
										//System.out.println("验证信息出错");
										continue;
									} else {
										//System.out.println("验证信息成功");
									}
									//得到签章图片（Base64编码）
									String PicData=sealVerify.getPicData(plain, signaturetext);
									//将base64编码生成图片
									if (PicData == null){ // 图像数据为空
										continue;
								    }
							        // Base64解码
							        byte[] bytes = Base64.decodeBase64(PicData);
							        for (int k = 0; k < bytes.length; ++k) {
							            if (bytes[k] < 0) {// 调整异常数据
							                bytes[k] += 256;
							            }
							        }
							        File file = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
								    if (!file.exists()) {
								    	out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
								        out.write(bytes);
								        out.flush();
								        out.close();
								    }
							        File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
								    if (!tempFile.exists()) {  
								    	continue;
								    }
								    
								    photoStream = new FileInputStream(tempFile);
									if(Sql_switcher.searchDbServer()==Constant.ORACEL)
									 {
										RecordVo vo = new RecordVo("HTMLSignature");	
										vo.setString("signatureid",SignatureID);
										vo.setString("documentid",documentid);
										Blob blob = getOracleBlob(tempFile,"HTMLSignature",SignatureID);
										vo.setObject("caimg",blob);
										dao.updateValueObject(vo);
									 }
								     if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
								     {
								    	 String sql="update HTMLSignature set caimg=? where signatureid=? and documentid=?";
								    	 prestmt = this.frameconn.prepareStatement(sql);
							             prestmt.setBinaryStream(1,photoStream,(int)tempFile.length());
							             prestmt.setString(2,SignatureID);
							             prestmt.setString(3,documentid);
							             dbS.open(this.frameconn, sql); 
							             prestmt.executeUpdate();
							             prestmt.close();
								     }
								}
							}
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(prestmt);
			PubFunc.closeResource(photoStream);
			PubFunc.closeResource(out);
			try {
				dbS.close(this.frameconn);
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		}
	}
    private Blob getOracleBlob(File file,String tablename,String id) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select caimg from ");
		strSearch.append(tablename);
		strSearch.append(" where signatureid=");
		strSearch.append(id);
		strSearch.append(" and documentid='BJCA'");
		strSearch.append("  FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set caimg=EMPTY_BLOB() where signatureid=");
		strInsert.append(id);
		strInsert.append(" and documentid='BJCA'");
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.frameconn);
	    InputStream in = null;
	    Blob blob = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			blob = blobutils.readBlob(strSearch.toString(),strInsert.toString(),in); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		}catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			PubFunc.closeIoResource(in);
		}
		return blob;
	}	
    /** 
	* @Title: getManageSqlWhere 
	* @Description: 获取权限where条件 任务监控时 按权限范围查看数据
	* @param @param dataTabName
	* @param @return
	* @return String
	*/ 
	private String getManageSqlWhere(String dataTabName,TemplateParam paramBo) {
		String strB0110Where="";
        String operOrg = this.userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
      
        if (TemplateUtilBo.isJobtitleVoteModule(this.userView)){//职称评审投票系统不是使用ehr的用户
        	operOrg="UN`";
        }
        DbWizard dbw=new DbWizard(this.frameconn);
        String un_1="";
        String um_1="";
        String um_2="";
        String un_2="";
        String um_3="";
        String un_3="";
        if (paramBo.getOperationType()==0)
        {
        	if(dbw.isExistField(dataTabName, "e0122_2", false))
        		um_2="e0122_2";
        	if(dbw.isExistField(dataTabName, "b0110_2", false))
        		un_2="b0110_2";
        }
        else 
        {
        	if(dbw.isExistField(dataTabName, "e0122_1", false))
        		um_1="e0122_1";
        	if(dbw.isExistField(dataTabName, "b0110_1", false))
        		un_1="b0110_1"; 
        	if(dbw.isExistField(dataTabName, "e0122_2", false))
        		um_3="e0122_2";
        	if(dbw.isExistField(dataTabName, "b0110_2", false))
        		un_3="b0110_2"; 
        }
        if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
        {
            if((operOrg!=null)&&(!"UN`".equalsIgnoreCase(operOrg)))
            { 
                if(operOrg.length() >3)
                {
                    String[] temp = operOrg.split("`");
                    for (int j = 0; j < temp.length; j++) { 
                         if (temp[j]!=null&&temp[j].length()>0) {
                        	 String _pre=temp[j].substring(0,2);
                        	 if (paramBo.getOperationType()==0)
                        	 {
                        		 if("UN".equalsIgnoreCase(_pre))
                        		 {
                        			 if(un_2.length()>0)
                        				 strB0110Where =strB0110Where+ " or  T."+un_2+" like '" + temp[j].substring(2)+ "%'";
                        			 else if(um_2.length()>0)
                        				 strB0110Where =strB0110Where+ " or  T."+um_2+" like '" + temp[j].substring(2)+ "%'";
                        		 }
                        		 else if("UM".equalsIgnoreCase(_pre)&&um_2.length()>0)
                        		 {
                        			 strB0110Where =strB0110Where+ " or  T."+um_2+" like '" + temp[j].substring(2)+ "%'";
                        		 }
                        	 }
                        	 else
                        	 {
                        		 if("UN".equalsIgnoreCase(_pre))
                        		 {
                        			 if(un_1.length()>0){//bug 37948 审批人没有单据中变化前部门权限，审批过后通过任务监控打开空白
                        				 if(un_3.length()>0){
                        					 strB0110Where =strB0110Where+ " or  (T."+un_1+" like '" + temp[j].substring(2)+ "%'  or (T."+un_3+" like '" + temp[j].substring(2)+ "%' and T.state=1) )";
                        				 }else{
                        					 strB0110Where =strB0110Where+ " or  T."+un_1+" like '" + temp[j].substring(2)+ "%'";
                        				 }
                        			 }
                        			 else if(um_1.length()>0)
                        				 if(um_3.length()>0){//bug 37948 审批人没有单据中变化前部门权限，审批过后通过任务监控打开空白
                        					 strB0110Where =strB0110Where+ " or  (T."+um_1+" like '" + temp[j].substring(2)+ "%'  or (T."+um_3+" like '" + temp[j].substring(2)+ "%' and T.state=1))";
                        				 }else{
                        					 strB0110Where =strB0110Where+ " or  T."+um_1+" like '" + temp[j].substring(2)+ "%'";
                        				 }
                        		 }
                        		 else if("UM".equalsIgnoreCase(_pre)&&um_1.length()>0)
                        		 {
                        			 if(um_3.length()>0){//bug 37948 审批人没有单据中变化前部门权限，审批过后通过任务监控打开空白
                        				 strB0110Where =strB0110Where+ " or  (T."+um_1+" like '" + temp[j].substring(2)+ "%'  or ( T."+um_3+" like '" + temp[j].substring(2)+ "%' and T.state=1 ) )";
                        			 }else{
                        				 strB0110Where =strB0110Where+ " or  T."+um_1+" like '" + temp[j].substring(2)+ "%'";
                        			 }
                        		 }
                        		 
                        	 } 
                         }
                    } 
                    //如果单位信息、部门信息不填写的话，可能存在自己申请的单据看不到情况，
                    //目前不改bug19063，以后也不做兼容。 
                }
                else if(operOrg==null)
                { 
                	strB0110Where=strB0110Where +" or 1=2 "; 
                }
               if(StringUtils.isNotEmpty(strB0110Where)){
            	   strB0110Where=strB0110Where.substring(3);
            	   strB0110Where = "("+strB0110Where+")";
               }
          }      
        }
		return strB0110Where;
	}	
    
}
