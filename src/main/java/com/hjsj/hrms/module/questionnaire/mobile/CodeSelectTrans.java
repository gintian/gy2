package com.hjsj.hrms.module.questionnaire.mobile;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title: CodeSelectTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2016-2-26 下午4:55:31</p>
 * @author jingq
 * @version 1.0
 */
public class CodeSelectTrans extends IBusiness{
	
	private enum TransType{
		/**加载codeset树**/
		codeset,
		/**获取code值**/
		codevalue
	}

	private static final long serialVersionUID = 1L;

	@Override
    public void execute() throws GeneralException {
		String message = "";
		try {
			String transType = (String) this.getFormHM().get("transType");
			String codesetid = (String) this.getFormHM().get("codesetid");
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sb = new StringBuffer();
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			if("UN".equals(codesetid.toUpperCase())
					||"UM".equals(codesetid.toUpperCase())
					||"@K".equals(codesetid.toUpperCase())){
				String codestr = "";
				if("UN".equals(codesetid.toUpperCase()))
					codestr = "codesetid = 'UN' ";
				else if("UM".equals(codesetid.toUpperCase()))
					codestr = "(codesetid = 'UN' or codesetid = 'UM') ";
				else if("@K".equals(codesetid.toUpperCase()))
					codestr = "(codesetid = 'UN' or codesetid = 'UM' or codesetid = '@K') ";
				sb.append("select codeitemid,codeitemdesc from organization where "+codestr);
			} else {
				sb.append("select codeitemid,codeitemdesc from codeitem where codesetid = '"+codesetid.toUpperCase()+"' ");
			}
			if(transType.equals(TransType.codeset.toString())){
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
				String codeitemid = (String) this.getFormHM().get("codeitemid");
				if("".equals(codeitemid)){
					sb.append("and parentid = codeitemid ");
				} else {
					sb.append("and parentid = '"+codeitemid.toUpperCase()+"' ");
				}
				if(!"".equals(codeitemid))
					sb.append("and codeitemid <> '"+codeitemid.toUpperCase()+"' ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					sb.append("and to_date('"+date+"','yyyy-mm-dd hh24:mi:ss') between start_date and end_date order by a0000,codeitemid");
				else
					sb.append("and '"+date+"' between start_date and end_date order by a0000,codeitemid");
				this.frowset = dao.search(sb.toString());
				while(this.frowset.next()){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("itemid", this.frowset.getString("codeitemid"));
					map.put("itemdesc", this.frowset.getString("codeitemdesc"));
					list.add(map);
				}
				this.getFormHM().put("data", list);
			} else if(transType.equals(TransType.codevalue.toString())){
				HashMap<String, String> map = new HashMap<String, String>();
				String param = (String) this.getFormHM().get("param");
				String str = "";
				for (String s : param.split("`")) {
					if(!"".equals(s))
						str += "or codeitemid = '"+s+"' ";
				}
				sb.append("and ("+str.substring(2)+")");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					sb.append("and to_date('"+date+"','yyyy-mm-dd hh24:mi:ss') between start_date and end_date order by a0000,codeitemid");
				else
					sb.append("and '"+date+"' between start_date and end_date order by a0000,codeitemid");
				this.frowset = dao.search(sb.toString());
				String text = "", value = "";
				while(this.frowset.next()){
					text += this.frowset.getString("codeitemdesc")+",";
					value += "`"+this.frowset.getString("codeitemid");
				}
				if(text.length()>0){
					map.put("text", text.substring(0, text.length()-1));
					map.put("value", value+"`");
				}
				this.getFormHM().put("data", map);
			}
		} catch (Exception e) {
			message = e.getMessage();
			e.printStackTrace();
		} finally {
			this.getFormHM().put("message", message);
		}
	}
}
