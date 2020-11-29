/**
 * 
 */
package com.hjsj.hrms.transaction.browse;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @author Owner
 *
 */
public class SearchBrowseInfoNuclearTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {	
		
		String type = (String) this.getFormHM().get("type");
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String a_code = (String) map.get("a_code");
		map.remove("a_code");
		if (a_code == null) {
			a_code = (String) this.getFormHM().get("a_code");
		}		
		
		if (a_code == null) {
			a_code = "";
		}
		
		this.formHM.put("a_code", a_code);
		// 组织机构代码
		if (a_code == null) {
			a_code = "";
		} else if ((a_code.startsWith("UN") || a_code.startsWith("UM") || a_code.startsWith("@K")) && a_code.length() > 2){
			a_code = a_code.substring(2);
		}
		
		// 需要显示的字段
		String cols = getFields();
		String[] tempcols = cols.split(",");
		ArrayList fields = new ArrayList();
		try {
			List  infoFieldList = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
			
			for (int i = 0; i < tempcols.length; i++) {
				String tem = tempcols[i];
				for(int j=0;j<infoFieldList.size();j++){
					FieldItem item = (FieldItem) infoFieldList.get(j);
						if(tem.equalsIgnoreCase(item.getItemid())){
							fields.add(item);
						}
				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StringBuffer strsql=new StringBuffer();	
		StringBuffer strwhere = new StringBuffer();

		if ("1".equals(type)) {
			strsql.append("select ");
			
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strsql.append(tempcols[i]);
					strsql.append(",");
				}
			}
			strsql.append("nbase,a0000,a0100 ");
			
			strwhere.append(" FROM ("); 
			strwhere.append(" select ");
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strwhere.append("t1.");
					strwhere.append(tempcols[i]);
					strwhere.append(",");
				}
			}
			strwhere.append("'Usr' nbase,t1.a0000,t1.a0100 from "); 
			strwhere.append("UsrA01 AS t1 INNER JOIN ( ");
			strwhere.append("SELECT t.A0100, t.C170M FROM UsrA17 AS t INNER JOIN ( ");
			strwhere.append("SELECT A0100, MAX(I9999) AS i9999 FROM UsrA17 GROUP BY A0100 ");
			strwhere.append(") AS t0 ON t.A0100 = t0.A0100 AND t.I9999 = t0.i9999 ");
			strwhere.append(") AS t2 ON t1.A0100 = t2.A0100  where (e0122 like '");
			strwhere.append(a_code);
			strwhere.append("%' or b0110 like '");
			strwhere.append(a_code);
			strwhere.append("%' or e01a1 like '");
			strwhere.append(a_code);
			strwhere.append("%') ");  
			strwhere.append("and c170m = '01' ");
			strwhere.append("union all ");
			strwhere.append(" select ");
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strwhere.append("t1.");
					strwhere.append(tempcols[i]);
					strwhere.append(",");
				}
			}
			strwhere.append("'Fzy' nbase,t1.a0000,t1.a0100 from "); 
			strwhere.append("FZYA01 AS t1 INNER JOIN ( ");
			strwhere.append("SELECT   t.A0100, t.C170M from FZYA17 AS t INNER JOIN ( ");
			strwhere.append("SELECT A0100, MAX(I9999) AS i9999 FROM FZYA17 GROUP BY A0100 ");
			strwhere.append(") AS t0 ON t.A0100 = t0.A0100 AND t.I9999 = t0.i9999 ");
			strwhere.append(") AS t2 ON t1.A0100 = t2.A0100 where (e0122 like '");
			strwhere.append("a_code");
			strwhere.append("%' or b0110 like '");
			strwhere.append(a_code);
			strwhere.append("%' or e01a1 like '");
			strwhere.append(a_code);
			strwhere.append("%')  and c170m = '01') m");
		} else if ("2".equals(type)) {
			strsql.append("select ");
			
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strsql.append(tempcols[i]);
					strsql.append(",");
				}
			}
			strsql.append("nbase,a0000,a0100 ");
			
			strwhere.append(" FROM ("); 
			strwhere.append(" select ");
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strwhere.append("t1.");
					strwhere.append(tempcols[i]);
					strwhere.append(",");
				}
			}
			strwhere.append("'Usr' nbase,t1.a0000,t1.a0100 from "); 
			strwhere.append("UsrA01 AS t1 INNER JOIN ( ");
			strwhere.append("SELECT t.A0100, t.C170M FROM UsrA17 AS t INNER JOIN ( ");
			strwhere.append("SELECT A0100, MAX(I9999) AS i9999 FROM UsrA17 GROUP BY A0100 ");
			strwhere.append(") AS t0 ON t.A0100 = t0.A0100 AND t.I9999 = t0.i9999 ");
			strwhere.append(") AS t2 ON t1.A0100 = t2.A0100  where (e0122 like '");
			strwhere.append(a_code);
			strwhere.append("%' or b0110 like '");
			strwhere.append(a_code);
			strwhere.append("%' or e01a1 like '");
			strwhere.append(a_code);
			strwhere.append("%') ");  
			strwhere.append("and (t2.c170m <> '01' or t2.c170m is null) ");
			strwhere.append("union all ");
			strwhere.append(" select ");
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strwhere.append("t1.");
					strwhere.append(tempcols[i]);
					strwhere.append(",");
				}
			}
			strwhere.append("'Fzy' nbase,t1.a0000,t1.a0100 from "); 
			strwhere.append("FZYA01 AS t1 INNER JOIN ( ");
			strwhere.append("SELECT   t.A0100, t.C170M from FZYA17 AS t INNER JOIN ( ");
			strwhere.append("SELECT A0100, MAX(I9999) AS i9999 FROM FZYA17 GROUP BY A0100 ");
			strwhere.append(") AS t0 ON t.A0100 = t0.A0100 AND t.I9999 = t0.i9999 ");
			strwhere.append(") AS t2 ON t1.A0100 = t2.A0100 where (e0122 like '");
			strwhere.append("a_code");
			strwhere.append("%' or b0110 like '");
			strwhere.append(a_code);
			strwhere.append("%' or e01a1 like '");
			strwhere.append(a_code);
			strwhere.append("%')  and (t2.c170m <> '01' or t2.c170m is null)) m");
		} else if ("3".equals(type)) {
			strsql.append("select ");
			
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strsql.append(tempcols[i]);
					strsql.append(",");
				}
			}
			strsql.append("nbase,a0000,a0100 ");
			
			strwhere.append(" FROM ("); 
			strwhere.append(" select ");
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strwhere.append("t1.");
					strwhere.append(tempcols[i]);
					strwhere.append(",");
				}
			}
			
			strwhere.append("'Trs' nbase,t1.a0000,t1.a0100 from "); 
			strwhere.append("TrsA01 AS t1 INNER JOIN( ");
			strwhere.append("SELECT t.A0100, t.C170n FROM TrsA17 AS t INNER JOIN(");
			strwhere.append("SELECT A0100, MAX(I9999) AS i9999 FROM TrsA17 GROUP BY A0100 ");
			strwhere.append(") AS t0 ON t.A0100 = t0.A0100 AND t.I9999 = t0.i9999 ");
			strwhere.append("	) AS t2 ON t1.A0100 = t2.A0100 where left(convert(varchar(100),");
			strwhere.append("t1.e0122, 112),6) = left(convert(varchar(100), getdate(), 112),6) ");
			strwhere.append("and (e0122 like '");
			strwhere.append(a_code);
			strwhere.append("%' or b0110 like '");
			strwhere.append(a_code);
			strwhere.append("%' or e01a1 like '");
			strwhere.append(a_code);
			strwhere.append("%') and t2.c170n=1 "); 
			strwhere.append("union all ");

			strwhere.append(" select ");
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strwhere.append("t1.");
					strwhere.append(tempcols[i]);
					strwhere.append(",");
				}
			}
			
			strwhere.append("'Ret' nbase,t1.a0000,t1.a0100 from "); 
			strwhere.append("RetA01 AS t1 INNER JOIN( ");
			strwhere.append("SELECT t.A0100, t.C170n FROM RetA17 AS t INNER JOIN( ");
			strwhere.append("SELECT A0100, MAX(I9999) AS i9999 FROM   dbo.RetA17 GROUP BY A0100");
			strwhere.append(") AS t0 ON t.A0100 = t0.A0100 AND t.I9999 = t0.i9999");
			strwhere.append(") AS t2 ON t1.A0100 = t2.A0100 where left(convert(varchar(100),");
			strwhere.append(" t1.e0122, 112),6) = left(convert(varchar(100), getdate(), 112),6) ");
			strwhere.append("and (e0122 like '");
			strwhere.append(a_code);
			strwhere.append("%' or b0110 like '");
			strwhere.append(a_code);
			strwhere.append("%' or e01a1 like '");
			strwhere.append(a_code);
			strwhere.append("%') and t2.c170n=1 ) m");
		} else if ("4".equals(type)) {
			strsql.append("select ");
			
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strsql.append(tempcols[i]);
					strsql.append(",");
				}
			}
			strsql.append("nbase,a0000,a0100 ");
			
			strwhere.append(" FROM ("); 
			strwhere.append(" select ");
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strwhere.append("t1.");
					strwhere.append(tempcols[i]);
					strwhere.append(",");
				}
			}
			
			strwhere.append("'Usr' nbase,t1.a0000,t1.a0100 from ");
			strwhere.append("UsrA01 AS t1 INNER JOIN ( ");
			strwhere.append("SELECT   t.A0100, t.C170N FROM   dbo.UsrA17 AS t INNER JOIN ( ");
			strwhere.append("SELECT  A0100, MAX(I9999) AS i9999 FROM   dbo.UsrA17 GROUP BY A0100 ");
			strwhere.append(") AS t0 ON t.A0100 = t0.A0100 AND t.I9999 = t0.i9999 ");
			strwhere.append(") AS t2 ON t1.A0100 = t2.A0100 where (e0122 like '");
			strwhere.append(a_code);
			strwhere.append("%' or b0110 like '");
			strwhere.append(a_code);
			strwhere.append("%' or e01a1 like '");
			strwhere.append(a_code);
			strwhere.append("%') and C170N ='1' ");

			strwhere.append("union all ");
			strwhere.append(" select ");
			for (int i = 0; i < tempcols.length; i++) {
				if (tempcols[i] != null && tempcols[i].length() > 0) {
					strwhere.append("t1.");
					strwhere.append(tempcols[i]);
					strwhere.append(",");
				}
			}
			
			strwhere.append("'FZY' nbase,t1.a0000,t1.a0100 from ");
			strwhere.append("FZYA01 AS t1 INNER JOIN ( ");
			strwhere.append("SELECT   t.A0100, t.C170N FROM   dbo.fzyA17 AS t INNER JOIN ( ");
			strwhere.append("SELECT  A0100, MAX(I9999) AS i9999 FROM   dbo.fzyA17 GROUP BY A0100 ");
			strwhere.append(") AS t0 ON t.A0100 = t0.A0100 AND t.I9999 = t0.i9999  ");
			strwhere.append(") AS t2 ON t1.A0100 = t2.A0100 where (e0122 like '");
			strwhere.append(a_code);
			strwhere.append("%' or b0110 like '");
			strwhere.append(a_code);
			strwhere.append("%' or e01a1 like '");
			strwhere.append(a_code);
			strwhere.append("%') ");
			strwhere.append("and C170N ='1' ) m ");
		}
		
		String cardid = searchCard("1");
		this.getFormHM().put("cardid",cardid);	
	    this.getFormHM().put("strsql",strsql.toString());
	    this.getFormHM().put("browsefields",fields);
		this.getFormHM().put("cond_str",strwhere.toString()); 		

		this.getFormHM().put("order_by","order by a0000");
		this.getFormHM().put("columns",cols+",nbase,a0100,a0000");	
	}
	
	/**
	 * 获得字段
	 * @return
	 */
	private String getFields() {
		SaveInfo_paramXml param = new SaveInfo_paramXml(this.frameconn);
		String fieldstr = param.getInfo_paramNode("browser");
		String[] str = fieldstr.split(",");
		// 获得定义的列
		StringBuffer cols = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			if (str[i] != null && str[i].length() > 0) {
				cols.append(",");
				cols.append(str[i]);
			}
		}
		
		// 默认显示
		if(cols.length() == 0){
			cols.append(",a0000,a0100,B0110,E0122,E01A1,A0101");
		}
				
		return cols.substring(1);
	}
	
	 /**
     * 根据信息群类别，查询定义的登记表格号
     * @param infortype =1人员 =2单位 3=职位 
     * @return
     */
    private String searchCard(String infortype) {
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String cardid="-1";
		 try
		 {
			 if("1".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
			 }
			 if("2".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
			 }
			 if("3".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
			 }
			 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
				 cardid="-1";
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
		 return cardid;
    }
}
