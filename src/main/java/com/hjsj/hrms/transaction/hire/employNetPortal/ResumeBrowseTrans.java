package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.EmployResumeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ResumeBrowseTrans extends IBusiness {
    private String insideFlag = "1";

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String entryType = (String) hm.get("entryType");
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            HashMap resumeBrowseSetMap = new HashMap(); //应聘者各子集里的信息集合
            HashMap setShowFieldMap = new HashMap(); //子集显示 列 map
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
            HashMap map = parameterXMLBo.getAttributeValues();
            ArrayList resumeStateList = (ArrayList) this.getFormHM().get("resumeStateList");
            
            String active_field = "";
            if (map != null && map.get("active_field") != null && !"".equals((String) map.get("active_field"))) {
                active_field = (String) map.get("active_field");
            }
            
            EmployResumeBo erbo = new EmployResumeBo(this.getFrameconn());
            if (resumeStateList == null || resumeStateList.size() == 0) {
                resumeStateList = erbo.getResumeStateList(active_field);
            }
            
            String resumeStateFieldIds = "";
            /**当预览简历时，是否可以使用打印和卡片功能，默认是可以的*/
            String canPrint = "1";
            if (map.get("resume_state") != null && ((String) map.get("resume_state")).trim().length() > 0)
                resumeStateFieldIds = (String) map.get("resume_state");

            this.getFormHM().put("resumeStateList", resumeStateList);
            
            String isAttach = "0";
            if (map != null && map.get("attach") != null) {
                isAttach = (String) map.get("attach");
            }
            
            this.getFormHM().put("isAttach", isAttach);

            String a0100 = (String) hm.get("a0100");
            
            EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.getFrameconn(), isAttach);
            String dbName = employNetPortalBo.getZpkdbName();

            //当期登陆用户是否是应聘人员
            boolean isZpPerson = false;
            this.frowset = dao.search(" select count(*) from " + dbName + "a01 where userName='" + this.userView.getUserName()
                    + "'");
            if (this.frowset.next()) {
                if (this.frowset.getInt(1) > 0)
                    isZpPerson = true;
            }
            
            //a0100 = PubFunc.decrypt(a0100);
            if (entryType != null && "1".equals(entryType)) {
                a0100 = (String) this.getFormHM().get("a0100");
                dbName = (String) this.getFormHM().get("dbName");
                hm.remove("entryType");
            }
            if(a0100 != null && a0100.length()>8)
            	a0100 = PubFunc.decrypt(PubFunc.getReplaceStr(a0100));
            dbName = PubFunc.getReplaceStr(dbName);
            
            boolean isHeaderHunter = false;
            if("1".equals((String)this.userView.getHm().get("isHeadhunter"))){
            	isHeaderHunter = true;
            }
            if (isZpPerson&&!isHeaderHunter)//如果是应聘人员，为解决平行权限漏洞，不能查其它人员简历,(并且不是猎头进来查看)
            {
                a0100 = (String) this.getFormHM().get("a0100");
                dbName = (String) this.getFormHM().get("dbName");
            }
            this.getFormHM().put("a0100", a0100);
            String personType = (String) hm.get("personType");
            if (personType != null && ("1".equals(personType) || "0".equals(personType)))
                hm.remove("personType");
            
            //简历预览登记表
            String previewTableId = employNetPortalBo.getResumeTemplateId(dao, map, a0100);
            if ("-1".equals(previewTableId)) {
                previewTableId = "#";
            }
            this.getFormHM().put("previewTableId", previewTableId);

            ArrayList list = employNetPortalBo.getZpFieldList();
            String isDefineWorkExperience = EmployNetPortalBo.isDefineWorkExperience;
            String value = "";
            if ("1".equals(isDefineWorkExperience)) {
                String workExperience_item = (String) map.get("workExperience");
                this.frowset = dao.search("select " + workExperience_item + " from " + dbName + "a01 where a0100='" + a0100 + "'");
                if (this.frowset.next())
                    value = this.frowset.getString(1) != null ? this.frowset.getString(1) : "1"; //(String)this.getFormHM().get("workExperience");
            }
            //招聘渠道，如果注册时候未选择社会还是校园，默认社会
			String tem = (String)this.getFormHM().get("hireChannel");
			String hireChannel = StringUtils.isEmpty(tem) ? "02" : tem;
			
			//定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
            //headHire、猎头招聘    01、校园招聘   02、社会招聘
            if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
                hireChannel = "02";
            
			String candidate_status_itemId="#";//应聘身份指标
			if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
				candidate_status_itemId=(String)map.get("candidate_status");
			//如果应聘身份指标参数有值，则注册时必须填写应聘身份
			if(!"#".equals(candidate_status_itemId)) {
				hireChannel = employNetPortalBo.getCandidateStatus(candidate_status_itemId, a0100);
				String channelName = employNetPortalBo.getChannelName(candidate_status_itemId,a0100,"");
				this.getFormHM().put("channelName", channelName);
			}
			
			//ArrayList uploadFileList = employNetPortalBo.getUploadFileList(a0100, dbName);
            ResumeFileBo bo = new ResumeFileBo(this.getFrameconn(), this.userView);
            ArrayList uploadFileList = bo.getFiles(dbName, a0100, "1");
            
            ArrayList attach_code_list = employNetPortalBo.getAttachCodeset(map, hireChannel);
			//如果启用了上传文件分类，则对已上传文件进行排序
			String attach_codeset = (String) map.get("attachCodeset");
			if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset))
				uploadFileList = employNetPortalBo.sortFileList(attach_code_list,uploadFileList);
            this.getFormHM().put("uploadFileList", uploadFileList);
			
			list=employNetPortalBo.getSetByWorkExprience(hireChannel);
            
            String canPrintResumeStatus = SystemConfig.getPropertyValue("canPrintResumeStatus");

            String status = employNetPortalBo.getStatus(a0100, dbName, resumeStateFieldIds, dao);
            if (canPrintResumeStatus != null && !"".equals(canPrintResumeStatus)) {
                canPrintResumeStatus = "," + canPrintResumeStatus + ",";
                if (canPrintResumeStatus.indexOf(("," + status + ",")) != -1 && previewTableId.trim().trim().length() != 0)
                    canPrint = "1";
                else
                    canPrint = "0";

            }
            String admissionCard = "#";
            if (map.get("admissionCard") != null && !"".equals((String) map.get("admissionCard"))) {
                admissionCard = (String) map.get("admissionCard");
            }
           /* String canPrintAdmissionCardStatus = SystemConfig.getPropertyValue("canPrintAdmissionCardStatus");
            canPrintAdmissionCardStatus = "," + canPrintAdmissionCardStatus + ",";
            if (canPrintAdmissionCardStatus.indexOf(("," + status + ",")) == -1) {
                admissionCard = "#";
            } else {
                employNetPortalBo.setVisiablSeqField(true);
            }*/
            String zp_pos_id = (String) hm.get("zp_pos_id");
            zp_pos_id = PubFunc.decrypt(zp_pos_id);
            for (int i = 0; i < ((ArrayList) list.get(0)).size(); i++) {
                LazyDynaBean abean = (LazyDynaBean) ((ArrayList) list.get(0)).get(i);
                String setID = (String) abean.get("fieldSetId");
                if ("A01".equalsIgnoreCase(setID)) {
                    String hire_object = "";
                    boolean flag = false;
                    if (map != null && map.get("hire_object") != null && !"".equals((String) map.get("hire_object"))) {
                        hire_object = (String) map.get("hire_object");
                        /**如果招聘渠道的值为空，默认不是内部招聘*/
                        flag = employNetPortalBo.getZ0336(zp_pos_id, hire_object);//true 内部招聘  
                    }
                    ArrayList resumeFieldList = employNetPortalBo.getResumeFieldList2((ArrayList) list.get(0), (HashMap) list.get(2), 0,
                            (HashMap) list.get(1), a0100, dbName, "0", flag);
                    resumeBrowseSetMap.put(setID.toLowerCase(), resumeFieldList);
                } else {
                    ArrayList showFieldList = employNetPortalBo.getShowFieldList(setID, (HashMap) list.get(2), (HashMap) list.get(1), 0); //取得简历子集 列表需显示的 列指标 集合
                    ArrayList showFieldDataList = employNetPortalBo.getShowFieldDataList(showFieldList, a0100, setID, dbName);
                    resumeBrowseSetMap.put(setID.toLowerCase(), showFieldDataList);
                    setShowFieldMap.put(setID.toLowerCase(), showFieldList);

                }
            }
            this.getFormHM().put("dmlStatus", this.getStatus(a0100));
            this.getFormHM().put("resumeBrowseSetMap", resumeBrowseSetMap);
            this.getFormHM().put("setShowFieldMap", setShowFieldMap);
            this.getFormHM().put("fieldSetList", (ArrayList) list.get(0));//zzk  使预览简历区分渠道
            if (personType != null) {//只要是走的招聘后台,这个personType都不是为null 的
                this.getFormHM().put("a0100", PubFunc.encrypt(a0100));
                this.getFormHM().put("dbName", PubFunc.encrypt(dbName));
                this.getFormHM().put("fieldSetList", (ArrayList) list.get(0));
                this.getFormHM().put("zpPosID", PubFunc.encrypt(zp_pos_id));
                this.getFormHM().put("zpPosList", getZpPosList(a0100, zp_pos_id, status));
            }
            /**招聘外网,这里也有可能涉及到安全的问题,所以这俩及时在外网也要进行加密的处理,xucs 2014-10-31**/
            this.getFormHM().put("encryptA0100",PubFunc.encrypt(a0100));
            //评语等级参数已经不使用，去掉查询评语等级方法
            this.getFormHM().put("remarkList", null/*getRemarkList((personType == null || personType.equals("")) ? "0" : personType, a0100, dbName)*/);
            this.getFormHM().put("admissionCard", admissionCard);
            this.getFormHM().put("insideFlag", this.insideFlag);
            this.getFormHM().put("canPrint", canPrint);
            if (map != null && map.get("answerSet") != null)
                this.getFormHM().put("answerSet", (String) map.get("answerSet"));
            else
                this.getFormHM().put("answerSet", "");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    private ArrayList getRemarkList(String personType, String a0100, String dbName) throws GeneralException {
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.getFrameconn());

        try {
            ParameterXMLBo bo = new ParameterXMLBo(this.getFrameconn(), "1");
            HashMap map = bo.getAttributeValues();
            String isRemenberExamine = "";
            String remenberExamineSet = "";
            if (map != null && map.get("isRemenberExamine") != null) {
//                isRemenberExamine = (String) map.get("isRemenberExamine");//是否记录面试过程子集  1是
            	isRemenberExamine = "0"; //7x,没有记录面试过程
            }
            if (map != null && map.get("remenberExamineSet") != null) {
                remenberExamineSet = (String) map.get("remenberExamineSet");//面试过程子集
            }
            String title = "";
            String content = "";
            String commentuser = "";
            String level = "";
            String commentdate = "";
            HashMap infoMap = null;
            if (map != null) {
                infoMap = (HashMap) map.get("infoMap");
                if (infoMap != null && infoMap.get("title") != null)
                    title = (String) infoMap.get("title");
                if (infoMap != null && infoMap.get("content") != null)
                    content = (String) infoMap.get("content");
                if (infoMap != null && infoMap.get("level") != null)
                    level = (String) infoMap.get("level");
                if (infoMap != null && infoMap.get("comment_user") != null)
                    commentuser = (String) infoMap.get("comment_user");
                if (infoMap != null && infoMap.get("comment_date") != null)
                    commentdate = (String) infoMap.get("comment_date");
            }
            StringBuffer sql = new StringBuffer("");
            String codesetid = (String) map.get("resume_level");
            if (codesetid == null || "".equals(codesetid))
                throw GeneralExceptionHandler.Handle(new Exception("没有设置评语等级代码！"));
            if ("0".equals(isRemenberExamine) || isRemenberExamine == null || "".equals(isRemenberExamine)
                    || remenberExamineSet == null || "".equals(remenberExamineSet)) {
                try {
                    String strsql = "select * from zp_comment_info";
                    this.frowset = dao.search(strsql);

                } catch (SQLException e) {
                    throw GeneralExceptionHandler.Handle(new Exception("数据库缺少简历评语表，请进行库维护后重新启动系统"));
                }
                RecordVo vo = new RecordVo("zp_comment_info");
                //	    	 	if(personType.equals("0")||personType.equals("4"))
                //	    		{
                //		    		sql.append("select zpi.title,zpi.comment_date,zpi.comment_user,zpi.content from zp_comment_info zpi ");
                //                    sql.append(" where  a0100='"+a0100+"' order by info_id");
                //	    		}
                //	    		else
                //	    		{
                //	    			sql.append("select zpi.title,zpi.comment_date,zpi.comment_user,codeitem.codeitemdesc,zpi.content from zp_comment_info zpi ,(select * from codeitem where codesetid='"+codesetid+"' ) codeitem ");
                //	    			if(vo.hasAttribute("level"))
                //	    			{
                //			    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
                //			    		{
                //		        			/**level是oracle中的关键字，做处理,加双引号*/
                //		        			sql.append(" where zpi.\"level\"=codeitem.codeitemid ");
                //		    			}
                //		    			else
                //		    			{
                //		    				sql.append(" where zpi.level=codeitem.codeitemid ");
                //		    			}
                //		    		}
                //	    			else if(vo.hasAttribute("level_o"))	
                //	    				sql.append(" where zpi.level_o=codeitem.codeitemid ");
                //				
                //	    			sql.append(" and  a0100='"+a0100+"' order by info_id");
                //	    		}
                sql
                        .append("select zpi.title,zpi.comment_date,zpi.comment_user,codeitem.codeitemdesc,zpi.content from zp_comment_info zpi ,(select * from codeitem where codesetid='"
                                + codesetid + "' ) codeitem ");
                if (vo.hasAttribute("level")) {
                    if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                        /**level是oracle中的关键字，做处理,加双引号*/
                        sql.append(" where zpi.\"level\"=codeitem.codeitemid ");
                    } else {
                        sql.append(" where zpi.level=codeitem.codeitemid ");
                    }
                } else if (vo.hasAttribute("level_o"))
                    sql.append(" where zpi.level_o=codeitem.codeitemid ");

                sql.append(" and  a0100='" + a0100 + "' order by info_id");
            } else if ("1".equals(isRemenberExamine)) {
                //				if(personType.equals("0")||personType.equals("4"))
                //	    		{
                //					sql.append(" select a."+title+" as title,a."+content+" as content,a."+commentuser+" as comment_user,");
                //		    		sql.append("a."+commentdate+" as comment_date ");
                //		    		sql.append(" from "+dbName+remenberExamineSet+" a ");
                //			    	sql.append(" where a.a0100='"+a0100+"'");
                //	    		}
                //				else
                //				{
                //		    		sql.append(" select a."+title+" as title,a."+content+" as content,a."+commentuser+" as comment_user,");
                //		    		sql.append("a."+commentdate+" as comment_date,codeitem.codeitemdesc ");
                //		    		sql.append(" from "+dbName+remenberExamineSet+" a, (select * from codeitem where codesetid='"+codesetid+"') codeitem ");
                //			    	sql.append(" where a."+level+"=codeitem.codeitemid and a.a0100='"+a0100+"'");
                //				}
                sql.append("select * from (");
                sql.append(" select a." + title + " as title,a." + content + " as content,a." + commentuser + " as comment_user,");
                sql.append("a." + commentdate + " as comment_date,codeitem.codeitemdesc,a.createtime as createtime ");
                //	    		sql.append(" from "+dbName+remenberExamineSet+" a, (select * from codeitem where codesetid='"+codesetid+"') codeitem ");
                //		    	sql.append(" where a."+level+"=codeitem.codeitemid and a.a0100='"+a0100+"'");
                sql.append(" from " + dbName + remenberExamineSet + " a left join (select * from codeitem where codesetid='"
                        + codesetid + "') codeitem");//zzk 修改 2013-11-5
                sql.append(" on a." + level + "=codeitem.codeitemid where a.a0100='" + a0100 + "' ");
                sql.append(") n order by createtime ASC");
            }
            this.frowset = dao.search(sql.toString());
            SimpleDateFormat dataF = new SimpleDateFormat("yyyy-MM-dd");
            FieldItem contentitem = DataDictionary.getFieldItem(content);
            while (this.frowset.next()) {
                LazyDynaBean abean = new LazyDynaBean();
                abean.set("title", this.frowset.getString("title") == null ? "" : this.frowset.getString("title"));
                if (this.frowset.getDate("comment_date") != null)
                    abean.set("date", dataF.format(this.frowset.getDate("comment_date")));
                else
                    abean.set("date", "");
                abean.set("user", this.frowset.getString("comment_user") == null ? "" : this.frowset.getString("comment_user"));
                //if(personType.equals("1"))
                abean.set("level", this.frowset.getString("codeitemdesc") == null ? "" : this.frowset.getString("codeitemdesc"));
                if (contentitem != null && "M".equalsIgnoreCase(contentitem.getItemtype())) {
                    String tt = Sql_switcher.readMemo(this.frowset, "content") == null ? "" : Sql_switcher.readMemo(this.frowset,
                            "content");
                    abean.set("content", tt);
                } else if (contentitem != null && "A".equalsIgnoreCase(contentitem.getItemtype())) {
                    String tt = "";
                    tt = this.frowset.getString("content") == null ? "" : this.frowset.getString("content");
                    abean.set("content", tt);
                } else {
                    String tt = "";
                    tt = this.frowset.getString("content") == null ? "" : this.frowset.getString("content");
                    abean.set("content", this.frowset.getString("content"));
                }
                list.add(abean);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }

    private ArrayList getZpPosList(String a0100, String zp_pos_id, String status) {
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowset = null;
        try {

            // author:dengcan
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.frameconn, "1");
            HashMap map = parameterXMLBo.getAttributeValues();
            String hireMajor = "";
            if (map.get("hireMajor") != null)
                hireMajor = (String) map.get("hireMajor"); //招聘专业指标
            boolean hireMajorIsCode = false;
            FieldItem hireMajoritem = null;
            if (hireMajor.length() > 0) {
                hireMajoritem = DataDictionary.getFieldItem(hireMajor.toLowerCase());
                if (hireMajoritem.getCodesetid().length() > 0 && !"0".equals(hireMajoritem.getCodesetid()))
                    hireMajorIsCode = true;
            }

            String sql = "select ";

            sql += "zpt.zp_pos_id,uk.codeitemdesc ,zpt.thenumber,resume_flag,codeitem.codeitemdesc as cc,zpt.nbase, um.codeitemdesc as umc,un.codeitemdesc as unc,z03.Z0336,z03.z0321,z03.z0325 ";
            if (hireMajor.length() > 0)
                sql += "," + hireMajor;
            //			sql+=" from zp_pos_tache zpt,z03,organization,(select * from codeitem where codesetid='36') codeitem"
            //					  +" ,(select codeitemid,codeitemdesc from organization where codesetid='UM') um,(select codeitemid,codeitemdesc from organization where codesetid='UN') un where zpt.zp_pos_id=z03.z0301 and z03.z0311=organization.codeitemid  and z03.z0321=un.codeitemid and z03.z0325=um.codeitemid and  zpt.resume_flag=codeitem.codeitemid  and   a0100='"+a0100+"'";
            //dml 修改为left join2011-6-28 10:39:40 and会出现问题
            sql += " from zp_pos_tache zpt left join (select * from codeitem where codesetid='36') codeitem on zpt.resume_flag=codeitem.codeitemid left join z03 on z03.z0301=zpt.zp_pos_id left join (select codeitemid,codeitemdesc from organization where codesetid='UM') um on  z03.z0325=um.codeitemid left join (select codeitemid,codeitemdesc from organization where codesetid='UN') un on z03.z0321=un.codeitemid left join (select codeitemid,codeitemdesc from organization where codesetid='@K') uk  on z03.z0311=uk.codeitemid where  a0100='"
                    + a0100 + "' order by zpt.thenumber";

            //	if(!zp_pos_id.equals("-1"))
            //		sql+="  and zpt.zp_pos_id='"+zp_pos_id+"'";
            rowset = dao.search(sql);
            while (rowset.next()) {
                String Z0336 = "";
                if (rowset.getString("Z0336") != null && rowset.getString("Z0336").length() > 0)
                    Z0336 = rowset.getString("Z0336");

                if (rowset.getString("nbase") != null && !"".equals(rowset.getString("nbase")))//内部招聘
                    this.insideFlag = "1";//内部招聘也加了上传简历附件功能
                    //				String un="";
                    //				if(rowset.getString("unc")!=null)
                    //					un=rowset.getString("unc")+"/";
                    //				String um="";
                    //				if(rowset.getString("umc")!=null)
                    //					um=rowset.getString("umc")+"/";
                String resumeflag = rowset.getString("resume_flag") == null ? "" : rowset.getString("resume_flag");
                String rstatus = "";
                if (status != null && !"".equals(status) && "12".equals(resumeflag) && !"12".equals(status)) {
                    rstatus = AdminCode.getCodeName("36", status);
                }
                if (rstatus != null && !"".equals(rstatus))
                    rstatus = "(" + rstatus + ")";
                String value = rowset.getString("codeitemdesc");
                if (hireMajor.length() > 0 && "01".equals(Z0336)) {
                    if (hireMajorIsCode) {
                        value = rowset.getString(hireMajor);
                        value = AdminCode.getCodeName(hireMajoritem.getCodesetid(), value);
                    } else
                        value = rowset.getString(hireMajor);
                }
                if (value == null)
                    value = "";
                /*********************应聘单位部门信息受层级控制显示  zzk 2014/1/15****************************/
                String org = "";
                Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
                String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                if (display_e0122 == null || "00".equals(display_e0122) || "".equals(display_e0122))
                    display_e0122 = "0";
                int floor = Integer.parseInt(display_e0122);
                if (Integer.parseInt(display_e0122) == 0) {

                    org = rowset.getString("unc") == null ? "" : rowset.getString("unc");
                    if (rowset.getString("umc") != null)
                        org += " / " + rowset.getString("umc");

                } else {
                    if (rowset.getString("z0325") == null || "NUll".equalsIgnoreCase(rowset.getString("z0325"))
                            || rowset.getString("z0325").trim().length() == 0) {
                        org = AdminCode.getOrgUpCodeDesc(rowset.getString("z0321"), floor, 0);
                    } else {
                        org = AdminCode.getOrgUpCodeDesc(rowset.getString("z0325"), floor, 0);
                    }
                    if (org == null || org.trim().length() == 0) {
                        org = rowset.getString("unc") == null ? "" : rowset.getString("unc");
                        if (rowset.getString("umc") != null)
                            org += " / " + rowset.getString("umc");
                    }
                }
                CommonData data1 = new CommonData(PubFunc.encrypt(rowset.getString(1)), org + "/" + value + " [第" + rowset.getString("thenumber")
                        + "志愿: " + rowset.getString(5) + rstatus + "]");
                list.add(data1);
            }
            rowset.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //dml 2011-03-29
    private String getStatus(String a0100) {
        String status = "";
        StringBuffer sql = new StringBuffer();
        sql.append("select * from zp_pos_tache where a0100='");
        sql.append(a0100);
        sql.append("'");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next()) {
                status = this.frowset.getString("STATUS");
            } else {
                status = "-3";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }
}
