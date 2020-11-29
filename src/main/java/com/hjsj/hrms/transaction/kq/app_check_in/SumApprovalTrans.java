package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author szk
*
*/
public class SumApprovalTrans extends IBusiness {

 

	public void execute() throws GeneralException {
		RowSet rs = null;
		try{
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String dbpre = (String) this.getFormHM().get("dbpre");
        String byname = (String) hm.get("byname");
        String select_type = (String) hm.get("select_type");
        String start_date = "";
        String end_date = "";
        hm.remove("byname");
        /**判断考勤期间*/
        ArrayList kqlist = RegisterDate.getKqDayList(this.getFrameconn());
        if (kqlist == null || kqlist.size() <= 0) {
            throw new GeneralException(ResourceFactory.getProperty("error.kq.please"));
        }
        else if (kqlist != null && kqlist.size() > 0) {
        	//开始，结束时间为当前考勤区间
            start_date = kqlist.get(0).toString();
            end_date = kqlist.get(kqlist.size() - 1).toString();
            if (start_date != null && start_date.length() > 0)
                start_date = start_date.replaceAll("\\.", "-");
            if (end_date != null && end_date.length() > 0)
                end_date = end_date.replaceAll("\\.", "-");
        }
        
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
        HashMap hashmap = para.getKqParamterMap();
        String nbase = para.getNbase();

        ArrayList alistd = new ArrayList();
        alistd = this.getDbase(nbase);
        if (alistd.size() == 0) {
            this.getFormHM().put("cond_str", "");
            this.getFormHM().put("sql_str", "");
            this.getFormHM().put("columns", "");
            this.getFormHM().put("cond_order", "");
            return;
        }
        this.getFormHM().put("dblist", alistd);
        /**相关代码类及代码值*/
        
        try {

            if (dbpre == null || "".equals(dbpre) || "all".equals(dbpre)) {
                dbpre = getFirstDbase(alistd);
            }
            //           加 工号、考勤卡号
            String g_no = (String) hashmap.get("g_no");
            String cardno = (String) hashmap.get("cardno");
            
            //人员库sql  
            StringBuffer nbasejoin=new StringBuffer();
            StringBuffer nbasewhere=new StringBuffer();
           //加班类型
            StringBuffer addtype=new StringBuffer();
            String selectaddtype ="";
            StringBuffer strsql = new StringBuffer();
             String[] dbases = nbase.split(",");
             for (int i = 0; i < dbases.length; i++)
 			{
            	 if ( dbases[i].equals(dbpre))
				{
            		 dbases= new String[1];
            		 dbases[0]=dbpre;
            		 break;
				}
 			}
           
            for (int i = 0; i < dbases.length; i++)
			{
            	nbasejoin.append("SELECT A0100,'"+dbases[i]+"' nbase,E0127,C01TC FROM "+dbases[i]+"A01 UNION ");
            	nbasewhere.append("nbase='"+dbases[i]+"' or ");
			}
            if (nbasejoin.length() > 0){
            	nbasejoin.setLength(nbasejoin.length() - 7);
            	nbasejoin.append(" where ");
                KqUtilsClass kqUtils = new KqUtilsClass(this.frameconn, this.userView);
                //不包括暂停考勤人员
                nbasejoin.append(kqUtils.getKqTypeWhere(KqConstant.KqType.STOP, true).substring(4));
                //不包括不考勤人员
                nbasejoin.append(kqUtils.getKqTypeWhere(KqConstant.KqType.NO, true));
            }
            if (nbasewhere.length() > 0)
            	nbasewhere.setLength(nbasewhere.length() - 4);
            /***************************
             * q1 加班总时长
             * q2 已批时长
             * q3 未批时长
             * q10 公休日加班时长
             * q11 节假日加班时长
             * q12 平时加班时长
             * .
             * .
             ***************************/
          //szk加班类型名称和id列表，用于页面显示
        	ArrayList addtypenamelist = new ArrayList();
        	ArrayList addtypeidlist = new ArrayList();
            ContentDAO dao = new ContentDAO(this.frameconn);
            rs = dao.search("select item_id,item_name  from kq_item where substring(item_id,1,1) ='1' and LEN(item_id) = '2'");
            while (rs.next()) {
             	String item_id = rs.getString("item_id");
            	String item_name = rs.getString("item_name");
            	addtypenamelist.add(item_name);
            	addtypeidlist.add(item_id);
             	addtype.append(" ,sum(case substring(q1103,1,2) when '"+item_id+"' then q11z4 else 0 end) q"+item_id+" ");
             	selectaddtype+=",q"+item_id;
            }
            this.getFormHM().put("addtypenamelist", addtypenamelist);
            this.getFormHM().put("addtypeidlist", addtypeidlist);
            this.getFormHM().put("cols", addtypeidlist.size()+13+"");//table的列数
            strsql.append("select nbase,a0100,"+g_no+" gno,"+cardno+" cardno,b0110,e01a1,e0122,max(a0101) a0101,SUM(q11z4) q1");
            strsql.append(" ,sum(case Q11z5 when '03' then q11z4 else 0 end) q2 ");
            strsql.append(" ,sum(case Q11Z5 when '02' then q11z4 else 0 end) q3 ");
            strsql.append(addtype);
            strsql.append(" from   (SELECT Q.nbase,q1101,b0110,Q.a0100,e0122,e01a1,a0101,q1103,q11z1,q11z3,q11z5,q11z4,A."+g_no+",A."+cardno+" FROM Q11 Q ");
            strsql.append(" INNER JOIN ( ");
            strsql.append(nbasejoin);
            strsql.append(" ) A ON Q.A0100 = A.A0100 AND Q.nbase = A.nbase and q11z5 <> '01') B  ");
            strsql.append(" where Q11Z5 IN ('02','03') and ");
            //当前考勤期间
            SearchAllApp searchAllApp = new SearchAllApp(this.getFrameconn(), this.userView);
            String time = searchAllApp.getWhere2("q11", start_date, end_date, "all",  "all", "1", "0");
            strsql.append(time);
            strsql.append(" and ");
            strsql.append("("+nbasewhere+")");
            //权限
            String privcode = RegisterInitInfoData.getKqPrivCode(userView);
            String codevalue = RegisterInitInfoData.getKqPrivCodeValue(userView);
            if ("UM".equalsIgnoreCase(privcode))
            	strsql.append(" and e0122 like '" + codevalue + "%'");
            else if ("@K".equalsIgnoreCase(privcode))
            	strsql.append(" and e01a1 like '" + codevalue + "%'");
            else if ("UN".equalsIgnoreCase(privcode))
            	strsql.append(" and b0110 like '" + codevalue + "%'");
            //姓名、工号、卡号模糊查询条件
            if (byname != null && byname.length() > 0) {
                byname = SafeCode.decode(byname);
                if ("0".equals(select_type)) {
                	strsql.append(" AND (a0101 LIKE '" + byname + "%' )");
                } else if ("1".equals(select_type)) {
                	strsql.append( " AND " + g_no + " LIKE '" + byname + "%'");
                } else if ("2".equals(select_type)) {
                	strsql.append( " AND " + cardno + " LIKE '" + byname + "%'");
                }
             
            }
            strsql.append(" group by nbase,A0100,e0127,C01TC,b0110,e01a1,e0122 ");
            //只显示待批时长大于0的
            strsql.append(" having sum(case Q11Z5 when '02' then q11z4 else 0 end)>0 ");
               
            this.getFormHM().put("select_type", select_type);
            this.getFormHM().put("cond_str", "");
            /**条件列表*/

            
            this.getFormHM().put("sql_str", strsql.toString());
            /**字段列表*/
            strsql.setLength(0);
            strsql.append("nbase,a0100,gno,cardno,b0110,e0122,e01a1,a0101,q1,q2,q3"+selectaddtype);

            this.getFormHM().put("columns", strsql.toString());
            this.getFormHM().put("cond_order", " order by b0110,e0122");

        } catch (Exception ee) {
            ee.printStackTrace();
        }

        //显示部门层数
        Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
        if (uplevel == null || uplevel.length() == 0)
            uplevel = "0";
        this.getFormHM().put("uplevel", uplevel);
        this.getFormHM().put("viewPost", para.getKq_orgView_post());
    }
    catch (Exception e) {
    	 e.printStackTrace();
         throw GeneralExceptionHandler.Handle(e);
	}
    finally {
        KqUtilsClass.closeDBResource(rs);
    }
    }

    private String getFirstDbase(ArrayList dblist) throws GeneralException {

        CommonData vo = (CommonData) dblist.get(0);
        return vo.getDataValue();

    }

    private ArrayList getDbase(String dlist) throws GeneralException {

        StringBuffer stb = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList dbaselist = userView.getPrivDbList();
        ArrayList slist = new ArrayList();
        CommonData cd = new CommonData("all","全部人员库");
        slist.add(cd);
        try {
            stb.append("select * from dbname ORDER BY dbid");
            this.frowset = dao.search(stb.toString());
            while (this.frowset.next()) {
                String dbpre = this.frowset.getString("pre");
                for (int i = 0; i < dbaselist.size(); i++) {
                    String userbase = dbaselist.get(i).toString();
                    if ((dlist.indexOf(userbase) != -1 && dbpre == userbase)
                            || (dlist.indexOf(userbase) != -1 && dbpre.equals(userbase))) {
                        CommonData vo = new CommonData(this.frowset.getString("pre"), this.frowset
                                .getString("dbname"));
                        slist.add(vo);
                    }
                }
            }
            if(slist.size() == 2)
            	slist.remove(0);
            	
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
        return slist;

    }
}
