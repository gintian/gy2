package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewExamine;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hjsj.hrms.businessobject.hire.InterviewExamine;
import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitInterviewExamineTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            DbWizard dbWizard = new DbWizard(this.frameconn);
            // 如果z05.z0515(拟录用时间)字段不存在，则新建 by 刘蒙
            if (!dbWizard.isExistField("z05", "Z0515")) {
                Table table = new Table("z05");
                Field obj = new Field("Z0515");
                obj.setDatatype(DataType.DATETIME);
                obj.setNullable(true);
                obj.setKeyable(false);
                table.addField(obj);
                dbWizard.addColumns(table);
            }
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String b_query = (String) hm.get("b_query");
            //	hm.remove("b_query");
            String codeid = (String) hm.get("code");
            String codesetid = (String) hm.get("codeset");
            String operate = (String) hm.get("operate");
            String extendSql = PubFunc.decrypt((String) this.getFormHM().get("extendSql"));
            String orderSql = PubFunc.decrypt((String) this.getFormHM().get("orderSql"));
            String z0101 = (String) hm.get("z0101");
            String returnflag = "";
            if (hm.get("returnflag") != null)//控制返回按钮
                returnflag = (String) hm.get("returnflag");
            else
                returnflag = (String) this.getFormHM().get("returnflag");
            this.getFormHM().put("returnflag", returnflag == null ? "" : returnflag);
            InterviewExamine interviewExamine = new InterviewExamine(this.getFrameconn());
            if (codeid != null && "summarise".equals(codeid)) {
                interviewExamine.setStr(codeid);
                hm.remove("code");
            } else {
                interviewExamine.setStr("");
            }
            if ((operate != null && "init".equals(operate))) {
                extendSql = "";
                if (codeid != null/*&&!codeid.equals("summarise")*/) {
                    if (codeid == null || "0".equals(b_query) || "summarise".equals(codeid)) {
                        if (!(userView.isAdmin() && "1".equals(userView.getGroupId()))) {
                            /**
                             * modify dml 2012-3-31 15:50:33
                             * reason 因增加业务管理范围导致权限规则改边
                             * */
                            //							if(this.userView.getStatus()==4/*hm.get("operateType")!=null&&((String)hm.get("operateType")).equals("employ")*/)
                            //							{
                            //								String codeset=this.getUserView().getManagePrivCode();
                            //								codeid=this.getUserView().getManagePrivCodeValue();
                            //								if(codeset==null||codeset.equals(""))
                            //								{
                            //									codeid="#";
                            //									throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                            //								}							
                            //							}
                            //							else
                            //							{
                            //								if(this.getUserView().getUnit_id()==null||this.getUserView().getUnit_id().equals("")||this.getUserView().getUnit_id().equalsIgnoreCase("UN"))
                            //								{
                            //									throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                            //								}
                            //								else
                            //								{
                            //									String temp0=this.getUserView().getUnit_id();
                            //									if(temp0.equals(""))
                            //									{
                            //										codeid="#";
                            //									}else if(temp0.trim().length()==3)
                            //									{
                            //										codeid="";
                            //									}
                            //									else
                            //									{
                            //										if(temp0.indexOf("`")==-1)
                            //										{
                            //											codeid=temp0.substring(2);
                            //										}
                            //										else
                            //										{
                            //						        			String[] temps=temp0.split("`");
                            //						        			codeid="";
                            //						           			for(int i=0;i<temps.length;i++)
                            //						        				codeid+=temps[i].substring(2)+"`";
                            //										}
                            //									}
                            //								}
                            //							}
                            codeid = this.userView.getUnitIdByBusi("7");
                            if (codeid == null || "".equals(codeid) || "UN".equalsIgnoreCase(codeid)) {
                                throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                            }
                            if (codeid.trim().length() == 3) {
                                codeid = "";
                            } else {
                                if (codeid.indexOf("`") == -1) {
                                    codeid = codeid.substring(2);
                                } else {
                                    String[] temps = codeid.split("`");
                                    codeid = "";
                                    for (int i = 0; i < temps.length; i++)
                                        codeid += temps[i].substring(2) + "`";
                                }
                            }
                        } else {

                            codeid = "0";
                        }
                    } else {
                        if (codeid.indexOf("UN") != -1 || codeid.indexOf("un") != -1 || codeid.indexOf("UM") != -1
                                || codeid.indexOf("um") != -1) {
                            if (codeid.indexOf("`") == -1)
                                codeid = codeid.substring(2);
                            else {
                                String[] temps = codeid.split("`");
                                String _str = "";
                                for (int i = 0; i < temps.length; i++)
                                    _str += temps[i].substring(2) + "`";
                                codeid = _str;
                            }

                        }
                    }
                } else if (codeid == null || "0".equals(b_query) || "summarise".equals(codeid)) {
                    if (!(userView.isAdmin() && "1".equals(userView.getGroupId()))) {
                        //						if(this.userView.getStatus()==4/*hm.get("operateType")!=null&&((String)hm.get("operateType")).equals("employ")*/)
                        //						{
                        //							String codeset=this.getUserView().getManagePrivCode();
                        //							codeid=this.getUserView().getManagePrivCodeValue();
                        //							if(codeset==null||codeset.equals(""))
                        //							{
                        //								codeid="#";
                        //								throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                        //							}							
                        //						}
                        //						else
                        //						{
                        //							if(this.getUserView().getUnit_id()==null||this.getUserView().getUnit_id().equals("")||this.getUserView().getUnit_id().equalsIgnoreCase("UN"))
                        //							{
                        //								throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                        //							}
                        //							else
                        //							{
                        //								String temp0=this.getUserView().getUnit_id();
                        //								if(temp0.equals(""))
                        //								{
                        //									codeid="#";
                        //								}else if(temp0.trim().length()==3)
                        //								{
                        //									codeid="";
                        //								}
                        //								else
                        //								{
                        //									if(temp0.indexOf("`")==-1)
                        //									{
                        //										codeid=temp0.substring(2);
                        //									}
                        //									else
                        //									{
                        //					        			String[] temps=temp0.split("`");
                        //					        			codeid="";
                        //					           			for(int i=0;i<temps.length;i++)
                        //					        				codeid+=temps[i].substring(2)+"`";
                        //									}
                        //								}
                        //							}
                        //						}
                        codeid = this.userView.getUnitIdByBusi("7");
                        if (codeid == null || "".equals(codeid) || "UN".equalsIgnoreCase(codeid)) {
                            throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                        }
                        if (codeid.trim().length() == 3) {
                            codeid = "";
                        } else {
                            if (codeid.indexOf("`") == -1) {
                                codeid = codeid.substring(2);
                            } else {
                                String[] temps = codeid.split("`");
                                codeid = "";
                                for (int i = 0; i < temps.length; i++)
                                    codeid += temps[i].substring(2) + "`";
                            }
                        }
                    } else {

                        codeid = "0";
                    }
                }/*else if(codeid.equals("summarise"))
                 {
                 
                 }*/
                else {
                    if (codeid.indexOf("UN") != -1 || codeid.indexOf("un") != -1 || codeid.indexOf("UM") != -1
                            || codeid.indexOf("um") != -1) {
                        //	codeid=codeid.substring(2);

                        if (codeid.indexOf("`") == -1)
                            codeid = codeid.substring(2);
                        else {
                            String[] temps = codeid.split("`");
                            String _str = "";
                            for (int i = 0; i < temps.length; i++)
                                _str += temps[i].substring(2) + "`";
                            codeid = _str;
                        }
                    }
                }

            } else {
                if (!(userView.isAdmin() && "1".equals(userView.getGroupId()))) {
                    //					if(this.userView.getStatus()==4/*hm.get("operateType")!=null&&((String)hm.get("operateType")).equals("employ")*/)
                    //					{
                    //						String codeset=this.getUserView().getManagePrivCode();
                    //						codeid=this.getUserView().getManagePrivCodeValue();
                    //						if(codeset==null||codeset.equals(""))
                    //						{
                    //							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                    //						}							
                    //					}
                    //					else
                    //					{
                    //						if(this.getUserView().getUnit_id()==null||this.getUserView().getUnit_id().equals("")||this.getUserView().getUnit_id().equalsIgnoreCase("UN"))
                    //						{
                    //							throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                    //						}
                    //						else
                    //						{
                    //							String temp0=this.getUserView().getUnit_id();
                    //							if(temp0.equals(""))
                    //							{
                    //								codeid="#";
                    //							}else if(temp0.trim().length()==3)
                    //							{
                    //								codeid="";
                    //							}
                    //							else
                    //							{
                    //								if(temp0.indexOf("`")==-1)
                    //								{
                    //									codeid=temp0.substring(2);
                    //								}
                    //								else
                    //								{
                    //				        			String[] temps=temp0.split("`");
                    //				        			codeid="";
                    //				           			for(int i=0;i<temps.length;i++)
                    //				        				codeid+=temps[i].substring(2)+"`";
                    //								}
                    //							}
                    //						}
                    //					}
                    codeid = this.userView.getUnitIdByBusi("7");
                    if (codeid == null || "".equals(codeid) || "UN".equalsIgnoreCase(codeid)) {
                        throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
                    }
                    if (codeid.trim().length() == 3) {
                        codeid = "";
                    } else {
                        if (codeid.indexOf("`") == -1) {
                            codeid = codeid.substring(2);
                        } else {
                            String[] temps = codeid.split("`");
                            codeid = "";
                            for (int i = 0; i < temps.length; i++)
                                codeid += temps[i].substring(2) + "`";
                        }
                    }
                } else {

                    codeid = "0";
                }
            }

            EmployActualize employActualize = new EmployActualize(this.getFrameconn());
            String dbname = employActualize.getZP_DB_NAME();
            if (operate != null && "init".equals(operate)) {
                //orderSql=" order by Z05.state asc,Z05.a0100 asc";
                if (Sql_switcher.searchDbServer() != Constant.ORACEL) {
                    orderSql = " order by Z05.state_date desc,Z0509A desc";
                } else {
                    orderSql = " order by Z05.state_date desc,Z05.Z0509 desc";// zzk 2013/12/19  中核华兴  面试默认按面试时间降序排列
                }
//                this.getFormHM().put("orderSql", orderSql);
//                this.getFormHM().put("extendSql", " ");
                this.userView.getHm().put("hire_sql_extend", " ");
                this.userView.getHm().put("hire_sql_order", orderSql);

            }
            ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
            HashMap map = bo2.getAttributeValues();
            ArrayList testTemplatAdvance = (ArrayList) map.get("testTemplatAdvance");//高级测评的相关参数
            ArrayList tableColumnsList = new ArrayList();
            String columns = "";
            int advanceFlag = testTemplatAdvance.size();

            if (testTemplatAdvance.size() > 0) {
                ArrayList tempList = interviewExamine
                        .getTableColumnsForAdvance(dbname, tableColumnsList, "1", testTemplatAdvance);//得到要显示的列
                columns = (String) tempList.get(0);
                ArrayList itemIdList = (ArrayList) tempList.get(1);
                interviewExamine.setAdvanceList(itemIdList);
            } else {
                columns = interviewExamine.getTableColumns(dbname, tableColumnsList, "1");//得到要显示的列
            }
            String resume_state_field = "";

            String testTemplateIds = bo2.getTestTemplateIds();

            String testAdvanceTemplateIds = bo2.getTestAdvanceTemplateIds();

            ParameterSetBo parameterSetBo = new ParameterSetBo(this.getFrameconn());
            boolean flag = parameterSetBo.createEvaluatingTable(testTemplateIds);//创建zp_test_template、：zp_test_result_招聘测评表号 这几张表动态创建

            parameterSetBo.createEvaluatingTable(testAdvanceTemplateIds);//创建高级测评方式的 zp_test_template、：zp_test_result_招聘测评表号 这几张表动态创建

            if (map != null && map.get("resume_state") != null && ((String) map.get("resume_state")).trim().length() > 0)
                resume_state_field = (String) map.get("resume_state");
            
            if (resume_state_field == null || "".equals(resume_state_field) || "#".equals(resume_state_field))
                throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));

            FieldItem it = DataDictionary.getFieldItem(resume_state_field);
            if (it == null)
                throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));

            String xx = it.getUseflag();
            if ("0".equals(xx))
                throw GeneralExceptionHandler.Handle(new Exception("请在配置参数模块中设置简历状态指标！"));

            String viewType = "";
            if ("summarise".equals(interviewExamine.getStr())) {
                viewType = (String) hm.get("viewType"); //1:用工需求  2：招聘计划
            } 
            
            ArrayList interviewExamineList = interviewExamine.getInterviewExamineList(this.getUserView(), codeid, dbname, extendSql,
                    orderSql, tableColumnsList, z0101, viewType, advanceFlag);
                
            this.getFormHM().put("sumrise", interviewExamine.getStr());
            this.getFormHM().put("z0101", z0101);
            this.getFormHM().put("dbName", PubFunc.encrypt(dbname));
            this.getFormHM().put("codeid", codeid);
            this.getFormHM().put("interviewExamineList", interviewExamineList);
            this.getFormHM().put("columns", columns);
            this.getFormHM().put("tableColumnsList", tableColumnsList);
            this.getFormHM().put("username", this.getUserView().getUserName());
            this.getFormHM().put("linkDesc", b_query);
            hm.remove("operate");

            //是否面试评价 =0 不。=1是
            /*ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
            HashMap map=parameterXMLBo.getAttributeValues();*/
            String examineNeedRecord = "0";
            String examineNeedRecordSet = "";
            String commentUserFild = "";
            String commentdateFild = "";
            if (map != null && map.get("isRemenberExamine") != null)
                examineNeedRecord = (String) map.get("isRemenberExamine");
            if ("1".equals(examineNeedRecord) && map != null && map.get("remenberExamineSet") != null)
                examineNeedRecordSet = (String) map.get("remenberExamineSet");
            HashMap infoMap = null;
            if (map != null) {
                infoMap = (HashMap) map.get("infoMap");
                // 取得负责人指标
                if (infoMap != null && infoMap.get("comment_user") != null)
                    commentUserFild = (String) infoMap.get("comment_user");
                //取得评审日期指标
                if (infoMap != null && infoMap.get("comment_date") != null)
                    commentdateFild = (String) infoMap.get("comment_date");
            }
            this.getFormHM().put("commentdateFild", commentdateFild);
            this.getFormHM().put("commentUserFild", commentUserFild);
            this.getFormHM().put("examineNeedRecord", examineNeedRecord);
            this.getFormHM().put("examineNeedRecordSet", PubFunc.encrypt(examineNeedRecordSet));
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public static void main(String[] arg) {
        String ss = "sdfasd#";
        System.out.println(ss.split("#").length);
    }

}
