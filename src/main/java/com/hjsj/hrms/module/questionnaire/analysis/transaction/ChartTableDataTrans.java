package com.hjsj.hrms.module.questionnaire.analysis.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import org.jdom.Document;
import org.jdom.Element;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title: ChartTableDataTrans </p>
 * <p>Description: 图表分析表格分页</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-12-24 上午10:53:25</p>
 * @author jingq
 * @version 1.0
 */
public class ChartTableDataTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		ResultSet rs = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		int total = 0;
		StringBuffer sql = new StringBuffer();
		StringBuffer wheresql = new StringBuffer();
		try {
			String limit = (String) this.getFormHM().get("limit");//每页显示条数
			String page = (String) this.getFormHM().get("page");//当前页
			String type = (String) this.getFormHM().get("type");//试题类型 3（填空题）、4（多项填空题）
			String itemid = (String) this.getFormHM().get("itemid");
			String qnid = (String) this.getFormHM().get("qnid");
			String subObject = (String) this.getFormHM().get("subobject");
			ContentDAO dao = new ContentDAO(this.frameconn);
			if("3".equals(type)){
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					wheresql.append(" Q"+itemid+"_1 is not null ");
				else
					wheresql.append(" datalength(Q"+itemid+"_1) > 0 ");
				sql.append("select Q"+itemid+"_1,total from qn_"+qnid+"_data qn ");
				sql.append("left join (select COUNT(*) total from qn_"+qnid+"_data ");
				sql.append("where "+wheresql);
				if(subObject!=null&&!"".equals(subObject)){
					sql.append("and subObject = '"+subObject+"') tab on 1 = 1 where qn.subObject = '"+subObject+"' and ");
				} else {
					sql.append(") tab on 1 = 1 where ");
				}
				//【61766】ZCSB：问卷调查，题型为填空题和多项填空题时，图表分析和原始数据中的数据不一致
				sql.append(wheresql+" and status in( '2','0' ) order by dataId");
				rs = dao.search(sql.toString(), Integer.parseInt(limit), Integer.parseInt(page));
				String codeset = getItemCodeSet(qnid, itemid);
				String value = "";
				while(rs.next()){
					total = Integer.parseInt(rs.getString("total"));
					HashMap<String, String> map = new HashMap<String, String>();
					if(codeset!=null&&!"".equals(codeset)){
						value = AdminCode.getCodeName(codeset, rs.getString("Q"+itemid+"_1"));
					} else {
						value = rs.getString("Q"+itemid+"_1");
					}
					if(value==null||"".equals(value))
						continue;
					map.put("Q"+itemid+"_1", value);
					list.add(map);
				}
			} else if("4".equals(type)){
				ArrayList<String> optlist = getItemOption(qnid, itemid);
				sql.append("select ");
				for (int i = 0; i < optlist.size(); i++) {
					if(i==0) {
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							wheresql.append("Q"+itemid+"_"+optlist.get(i)+" is not null ");
						else
							wheresql.append("datalength(Q"+itemid+"_"+optlist.get(i)+") > 0 ");
						sql.append("Q"+itemid+"_"+optlist.get(i));
					} else {
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							wheresql.append("or Q"+itemid+"_"+optlist.get(i)+" is not null ");
						else
							wheresql.append("or datalength(Q"+itemid+"_"+optlist.get(i)+") > 0 ");
						sql.append(",Q"+itemid+"_"+optlist.get(i));
					}
				}
				sql.append(",total from qn_"+qnid+"_data qn left join (select COUNT(*) total ");
				sql.append("from qn_"+qnid+"_data where ("+wheresql+") ");
				if(subObject!=null&&!"".equals(subObject)){
					sql.append("and subObject = '"+subObject+"') tab on 1 = 1 where qn.subObject = '"+subObject+"' and ");
				} else {
					sql.append(") tab on 1 = 1 where ");
				}
				//【61766】ZCSB：问卷调查，题型为填空题和多项填空题时，图表分析和原始数据中的数据不一致
				sql.append("("+wheresql+") and status in( '2','0' ) order by dataid");
				rs = dao.search(sql.toString(), Integer.parseInt(limit), Integer.parseInt(page));
				String codeset = getItemCodeSet(qnid, itemid);
				String value = "";
				while(rs.next()){
					total = Integer.parseInt(rs.getString("total"));
					HashMap<String, String> map = new HashMap<String, String>();
					for (String str : optlist) {
						if(codeset!=null&&!"".equals(codeset)){
							value = AdminCode.getCodeName(codeset, rs.getString("Q"+itemid+"_"+str));
						} else {
							value = rs.getString("Q"+itemid+"_"+str);
						}
						if(value==null||"".equals(value))
							continue;
						map.put("Q"+itemid+"_"+str, value);
					}
					if(map.keySet().size()<=0)
						continue;
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		this.getFormHM().put("dataobj", JSONArray.fromObject(list));
		this.getFormHM().put("totalCount", total);
	}
	
	private ArrayList<String> getItemOption(String qnid, String itemid){
		ArrayList<String> list = new ArrayList<String>();
		ResultSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs = dao.search("select optid from qn_question_item_opts where qnid = '"+qnid+"' and itemid = '"+itemid+"' order by itemid,norder");
			while(rs.next()){
				list.add(rs.getString("optid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	
	private String getItemCodeSet(String qnid, String itemid){
		String codeset = "";
		ResultSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs = dao.search("select options from qn_question_item where qnid = '"+qnid+"' and itemid = '"+itemid+"'");
			String options = "";
			while(rs.next()){
				options = rs.getString("options");
			}
			Document doc = PubFunc.generateDom(options);
			Element root = doc.getRootElement();
			Element el = root.getChild("inputtype");
			String type = el.getValue();
			if("4".equals(type)){
				Element ele = root.getChild("codeset");
				codeset = ele.getValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return codeset;
	}
}
