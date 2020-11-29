package com.hjsj.hrms.module.template.templatemain.transaction;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:ImportMenByConditionTrans.java</p>
 * <p>Description>:按检索条件导入人员</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-4-26 上午10:11:00</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class ImportMenByConditionTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		try
		{
            TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
            String tabId = frontProperty.getTabId();
            // 1:清空当前人员,重新引入 2:不清空,引入符合条件的数据
            String flag = (String) this.getFormHM().get("flag"); 

            TemplateBo templateBo = new TemplateBo(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
            TemplateParam tableParamBo = templateBo.getParamBo();

            String no_priv_ctrl = tableParamBo.getNo_priv_ctrl(); // 手工选人、条件选人不按管理范围过滤,
                                                                    // 0按管理范围过滤(默认值),1不按
            String intbase = tableParamBo.getInit_base();
            String factor = tableParamBo.getFactor();
            String init_base = tableParamBo.getInit_base();
            ContentDAO dao = new ContentDAO(this.frameconn);
            int infor_type = tableParamBo.getInfor_type();
            ArrayList dblist = new ArrayList();
            if(infor_type==1) {
            	if (this.userView.isSuper_admin()) {
                    this.frowset = dao.search("select * from dbname");
                    while (this.frowset.next())
                        dblist.add(this.frowset.getString("pre"));
                } else {
                    if (init_base != null && init_base.trim().length() > 0) {
                        if (this.userView.getDbpriv().toString().toLowerCase().indexOf("," + init_base.toLowerCase() + ",") == -1)
                            return;
                        dblist.add(init_base);
                    } else {
                        String[] temps = this.userView.getDbpriv().toString().split(",");
                        for (int i = 0; i < temps.length; i++) {
                            if (temps[i].trim().length() > 0)
                                dblist.add(temps[i]);
                        }
                    }
                }
            }

            if ("1".equals(flag))
                dao.update("delete from " + this.userView.getUserName() + "templet_" + tabId);

            ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
            if(infor_type==1) {
	            for (int e = 0; e < dblist.size(); e++) {
	            	templateBo.setComputeVar(false);//bug 40865 因为templateBo用的是同一个，第一个人员库计算完后，设置为true，后面的人员库中临时变量就不再计算了。
	                String BasePre = (String) dblist.get(e);
	
	                if (intbase != null && intbase.trim().length() > 0) {
	                    if (!intbase.equalsIgnoreCase(BasePre))
	                        continue;
	                }
	
	                StringBuffer sql = new StringBuffer();
	
	                sql.append("select a0100 from ");
	                int infoGroup = 0; // forPerson 人员
	                int varType = 8; // logic
	                String whereIN = InfoUtils.getWhereINSql(this.userView, BasePre);
	                whereIN = "select a0100 " + whereIN;
	                //
	                if ("1".equals(no_priv_ctrl))
	                    whereIN = "";
	                YksjParser yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
	                YearMonthCount ymc = null;
	                yp.setSupportVar(true, "select  *  from   midvariable where nflag=0 and templetid= " + tabId); // 支持临时变量
	                yp.run_Where(factor, ymc, "", "", dao, whereIN, this.frameconn, "A", null);
	                String tempTableName = yp.getTempTableName();
	                sql.append(tempTableName);
	                sql.append(" where " + yp.getSQL());
	
	                if ("2".equals(flag)) {
	                    sql.append(" and a0100 not in (select a0100 from ");
	                    sql.append(this.userView.getUserName() + "templet_" + tabId);
	                    sql.append(" where upper(basepre)='");
	                    sql.append(BasePre.toUpperCase());
	                    sql.append("')");
	                }
	
	                ArrayList a0100list = new ArrayList();
	                this.frowset = dao.search(sql.toString());
	                while (this.frowset.next())
	                    a0100list.add(this.frowset.getString("a0100"));
	
	                if (a0100list.size() == 0)
	                    continue;
	
	                if (a0100list.size() <= 500)
	                    templateBo.impDataFromArchive(a0100list, BasePre);
	                else {
	                    ArrayList tempList = null;
	                    int size = a0100list.size();
	                    int n = size / 500 + 1;
	                    for (int i = 0; i < n; i++) {
	                        tempList = new ArrayList();
	                        for (int j = i * 500; j < (i + 1) * 500; j++) {
	                            if (j < a0100list.size())
	                                tempList.add((String) a0100list.get(j));
	                            else
	                                break;
	                        }
	                        if (tempList.size() > 0)
	                            templateBo.impDataFromArchive(tempList, BasePre);
	
	                    }
	
	                }
	            }
            }
            else{//单位岗位
            	templateBo.setComputeVar(false);
                StringBuffer sql = new StringBuffer();
                String fieldcode = "b0110";
                String key = "B01";
                String BasePre = "B";
                int infoGroup=YksjParser.forUnit;
				if(infor_type==3)
				{
					fieldcode = "e01a1";
					infoGroup=YksjParser.forPosition;
					BasePre = "K";
					key = "K01";
				}
				sql.append("select "+fieldcode+" from ");
                int varType = 8;
                String whereIN = "";
                //获得管理范围sql
                whereIN = "select "+fieldcode+" from "+key+" "+templateBo.getPrivSQL(fieldcode);
                if ("1".equals(no_priv_ctrl))
                    whereIN = "";
                YksjParser yp = new YksjParser(this.userView, alUsedFields, YksjParser.forSearch, varType, infoGroup, "Ht", "");
                YearMonthCount ymc = null;
                yp.setSupportVar(true, "select  *  from   midvariable where nflag=0 and templetid= " + tabId); // 支持临时变量
                yp.run_Where(factor, ymc, "", "", dao, whereIN, this.frameconn, "A", null);
                String tempTableName = yp.getTempTableName();
                sql.append(tempTableName);
                sql.append(" where " + yp.getSQL());
                
                if ("2".equals(flag)) {
                    sql.append(" and "+fieldcode+" not in (select "+fieldcode+" from ");
                    sql.append(this.userView.getUserName() + "templet_" + tabId);
                    sql.append(")");
                }

                ArrayList a0100list = new ArrayList();
                this.frowset = dao.search(sql.toString());
                while (this.frowset.next())
                    a0100list.add(this.frowset.getString(fieldcode));

                if (a0100list.size() <= 500)
                    templateBo.impDataFromArchive(a0100list, BasePre);
                else {
                    ArrayList tempList = null;
                    int size = a0100list.size();
                    int n = size / 500 + 1;
                    for (int i = 0; i < n; i++) {
                        tempList = new ArrayList();
                        for (int j = i * 500; j < (i + 1) * 500; j++) {
                            if (j < a0100list.size())
                                tempList.add((String) a0100list.get(j));
                            else
                                break;
                        }
                        if (tempList.size() > 0)
                            templateBo.impDataFromArchive(tempList, BasePre);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }
}
