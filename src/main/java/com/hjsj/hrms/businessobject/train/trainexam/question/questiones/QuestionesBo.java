package com.hjsj.hrms.businessobject.train.trainexam.question.questiones;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Title:QuestionesBo
 * </p>
 * <p>
 * Description:试题信息业务类
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-10-19
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class QuestionesBo {

	// 数据库连接
	private Connection conn;
	
	public QuestionesBo(){
		
	}

	public QuestionesBo(Connection conn) {
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 获得题型列表
	 * 
	 * @return
	 */
	public ArrayList getQuestionTypeList() {
		ArrayList list = new ArrayList();
		String sql = "select type_id,Type_name from tr_question_type order by norder";

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				// 题型名称
				String typeName = rs.getString("type_name");
				// 题型id
				String typeId = rs.getString("type_id");
				CommonData data = new CommonData(typeId, typeName);
				list.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	/**
	 * 获得题型难度列表
	 * 
	 * @return
	 */
	public ArrayList getDifficultyList() {
		ArrayList list = new ArrayList();

		CommonData data1 = new CommonData("-2", "容易");
		CommonData data2 = new CommonData("-1", "较易");
		CommonData data3 = new CommonData("0", "中度");
		CommonData data4 = new CommonData("1", "难(*)");
		CommonData data5 = new CommonData("2", "难(**)");
		CommonData data6 = new CommonData("3", "难(***)");
		CommonData data7 = new CommonData("4", "难(****)");
		CommonData data8 = new CommonData("5", "难(*****)");

		list.add(data1);
		list.add(data2);
		list.add(data3);
		list.add(data4);
		list.add(data5);
		list.add(data6);
		list.add(data7);
		list.add(data8);

		return list;
	}
	
	/**
	 * 获得难度名称
	 * @param diffId
	 * @return
	 */
	public static String getDifficultyValue(int diffId){
		String value = "";
		switch(diffId){
			case -2:
				value="容易";
				break;
			case -1:
				value="较易";
				break;
			case 0:
				value="中度";
				break;
			case 1:
				value="难(*)";
				break;
			case 2:
				value="难(**)";
				break;
			case 3:
				value="难(***)";
				break;
			case 4:
				value="难(****)";
				break;
			case 5:
				value="难(*****)";
				break;
		}
		return value;
	}

	/**
	 * 根据id获得试题信息
	 * 
	 * @return
	 */
	public RecordVo getQuestionVoById(String id) {

		ContentDAO dao = new ContentDAO(this.conn);
		RecordVo vo = new RecordVo("r52");
		try {
			vo.setInt("r5200", Integer.parseInt(id));
			vo = dao.findByPrimaryKey(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vo;
	}

	/**
	 * 根据试题id获得知识点id
	 * 
	 * @return
	 */
	public String getKnowledgeIdById(String id) {

		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select know_id from tr_test_knowledge where r5200="
				+ id;
		RowSet rs = null;
		StringBuffer knowId = new StringBuffer();

		try {
			rs = dao.search(sql);
			while (rs.next()) {
				knowId.append(",");
				knowId.append(rs.getString("know_id"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (knowId.length() > 0) {
			return knowId.substring(1);
		}
		return knowId.toString();
	}
	
	/**
	 * 根据试题id获得知识点名称
	 * 
	 * @return
	 */
	public static String getKnowledgeIdByNames(String id) {

		String sql = "select know_id from tr_test_knowledge where r5200=" + id;		
		StringBuffer names = new StringBuffer();

		RowSet rs = null;
		Connection conn = null;
		try {
		    conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while (rs.next()) {
				names.append(",");
				CodeItem item = AdminCode.getCode("68", rs.getString("know_id").trim());
				if (null != item)
                {
				    names.append(item.getCodename());
                }
				else {
				    //知识点不存在了
				    names.append(""); 
                }
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
			    if(conn != null) {
                    conn.close();
                }
			}
			catch(Exception e){
			    e.printStackTrace();
			}
		}

		if (names.length() > 0) {
			return names.substring(1);
		}
		return names.toString();
	}

	/**
	 * 根据题型判断是否为主观题
	 * 
	 * @param type
	 * @return
	 */
	public boolean getIsObjective(String type) {
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select Ques_type from tr_question_type where type_id = "
				+ type;
		RowSet rs = null;

		try {
			rs = dao.search(sql);
			if (rs.next()) {
				if (rs.getInt("Ques_type") == 1) {
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return flag;
	}

	/**
	 * 根据知识点的id查询知识点名称
	 * 
	 * @param ids
	 * @return
	 */
	public String getKnowledgeNamesByIds(String ids) {
		if (ids == null || ids.length() <= 0) {
			return "";
		}

		StringBuffer names = new StringBuffer();
		String str = "";
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			if (i != 0) {
				str += "," + "'" + id[i] + "'";
			} else {
				str += "'" + id[i] + "'";
			}
		}
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select codeitemdesc from codeitem where codesetid='68' and codeitemid in ("
				+ str + ")";
		RowSet rs = null;

		try {
			rs = dao.search(sql);
			while (rs.next()) {
				names.append(",");
				names.append(rs.getString("codeitemdesc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (names.length() > 0) {
			return names.substring(1);
		}

		return names.toString();

	}

	/**
	 * 获得表顺序号
	 * 
	 * @param tableName
	 * @return
	 */
	public int getNorder(String tableName) {
		int norder = 0;
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "select max(norder) norder from " + tableName;
		RowSet rs = null;

		try {
			rs = dao.search(sql);
			if (rs.next()) {
				norder = rs.getInt("norder");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return norder + 1;
	}

	/**
	 * 获得选项的字符串
	 * 
	 * @param xml
	 * @return
	 */
	public String getStrSelection(String xml) {
		
		StringBuffer selection = new StringBuffer();
		if (xml == null || xml.length() <= 0) {
			return "";
		}
		try {
			Document doc = PubFunc.generateDom(xml);
			String str_path = "/Params/item";
			XPath xpath = XPath.newInstance(str_path);
			List list = xpath.selectNodes(doc);

			for (int i = 0; i < list.size(); i++) {
				Element el = (Element) list.get(i);
				String id = el.getAttributeValue("id");
				String content = el.getText();
				if (i != 0) {
					selection.append("`~&~`");
				}
				selection.append(id);
				selection.append("`:`");
				selection.append(content);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return selection.toString();
	}

	/**
	 * 过滤html标签
	 * @param inputString 含html标签的字符串
	 * @return
	 */
	public static String html2Text(String htmlStr) {
	    if(StringUtils.isEmpty(htmlStr)) {
            return "";
        }
	    
		String textStr = "";
		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;

		try {
		    // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>}
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			// 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>}
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; 
			// 定义HTML标签的正则表达式
			String regEx_html = "<[^>]+>"; 
			
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串
	}
	
	/**
	 * 把阿拉伯数字转换成中文
	 * 注：只考虑到2位
	 */
	public static String getDigitToHanzi(String digit){
		String hanzi="";
		digit=digit==null||digit.length()<1?"0":digit;
		String tmpStr[] = new String[]{"一","二","三","四","五","六","七","八","九"};
		if(digit.length()<2){
			if("0".equals(digit)) {
                hanzi="零";
            } else {
                hanzi=tmpStr[Integer.parseInt(digit)-1];
            }
		}else if(digit.length()==2){
			if("1".equals(digit.substring(0, 1))){
				hanzi="十";
				if(!"0".equals(digit.substring(1))) {
                    hanzi+=tmpStr[Integer.parseInt(digit.substring(1))-1];
                }
			}else{
				hanzi=tmpStr[Integer.parseInt(digit.substring(0,1))-1]+"十";
				if(!"0".equals(digit.substring(1))) {
                    hanzi+=tmpStr[Integer.parseInt(digit.substring(1))-1];
                }
			}
		}
		return hanzi;
	}
	
	/**
	 * 获取该试卷类型的title信息  如：第一部分 单选题(总分：24)
	 * @param type_id 试卷类型
	 * @param r5300 试卷id
	 * @param order 位置
	 * @return
	 */
	public static String getTitle(String type_id,String r5300,String order){
		String title="";//表头信息
		Connection conns=null;
		RowSet rs = null;
		try {
		    r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			title+="第"+getDigitToHanzi(order)+"部分　";
			String sql = "select type_name from tr_question_type where type_id=" + type_id;
			rs = dao.search(sql);
			if (rs.next()) {
				title+=rs.getString("type_name");
			}
			//获取该试卷该类型下试题的总分
			sql="select score from tr_exam_question_type where r5300="+r5300+" and type_id="+type_id;
			rs = dao.search(sql);
			if(rs.next()){
				float score = rs.getFloat("score");
				int tmp = (int) score;
				if(score>tmp) {
                    title+="(总分："+score+")";
                } else {
                    title+="(总分："+tmp+")";
                }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
                if(conns != null) {
                    conns.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
		}

		return title;
	}
	
	public static String getAllStr(String id) {
		String str = "";
		
		String sql = "select r5204 from r52 where r5200=" + id;
		RowSet rs = null;
		Connection conns = null;
		try {
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			rs = dao.search(sql);
			if (rs.next()) {
				str = rs.getString("r5204");
				str = str == null ? "" : str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
                if(conns != null) {
                    conns.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
		}
		return str.replaceAll("\r\n", "<br>");
	}
	
	/**
	 * 是否被引用
	 * @param id
	 * @return
	 */
	public static Map isShow() {
		Map map = new HashMap();
		//试卷引用试题后，该试题不可以删除
		String sql = "select r5200 from tr_exam_paper";
		RowSet rs = null;
		Connection conn = null;
		try {
		    conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while (rs.next()) {
				map.put(rs.getString("r5200"), rs.getString("r5200"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
		
		return map;
	}
	
	/**
	 * 试卷是否被引用(执行中或结束)
	 * @param r5300
	 * @return
	 */
	public static String isQuote(String r5300) {
		String str = "0";
		
		String sql = "select 1 from r54 where r5300="+r5300+" and (r5411='05' or r5411='06')";
		RowSet rs = null;
		Connection conns = null;
		try {
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			rs = dao.search(sql);
			if (rs.next()) {
				str = "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
                if(conns != null) {
                    conns.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
		}
		
		return str;
	}
	
	/**
	 * 该类型的知识点个数
	 * @param r5300,type_id
	 * @return
	 */
	public static int knowLedgeSize(String r5300,String type_id) {
	    r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		int num = 1;
		String sql = "select know_ids from tr_exam_question_type where r5300="+r5300+" and type_id="+type_id+" order by norder";
		RowSet rs = null;
		Connection conns = null;
		try {
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			rs = dao.search(sql);
			if (rs.next()) {
				String tmpstr = rs.getString("know_ids");
				num = tmpstr!=null?tmpstr.split(",").length:1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
                if(conns != null) {
                    conns.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
		}
		return num;
	}
	
	/**
	 * 该试题类型名称
	 * @param type_id
	 * @return
	 */
	public static String getTypeNmae(String type_id) {
		String str = "";
		String sql = "select type_name from tr_question_type where type_id="+type_id;
		RowSet rs = null;
		Connection conns = null;
		try {
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			rs = dao.search(sql);
			if (rs.next()) {
				str = rs.getString("type_name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
                if(conns != null) {
                    conns.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
		}
		return str;
	}
	
	/**
	 * 根据知识点ids获取知识点名称
	 * @param know_ids
	 * @return
	 */
	public static String getKnowledgeviewvaluee(String know_ids){
		if(know_ids==null||know_ids.length()<1) {
            return "";
        }
		String knowledgeviewvalue="";//知识点
		try {
			String tmpKnowids[] = know_ids.split(",");
			for (int i = 0; i < tmpKnowids.length; i++) {
				knowledgeviewvalue+=AdminCode.getCodeName("68",tmpKnowids[i])+",";
			}
			
			if(knowledgeviewvalue!=null&&knowledgeviewvalue.length()>0) {
                knowledgeviewvalue=knowledgeviewvalue.substring(0, knowledgeviewvalue.length()-1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return knowledgeviewvalue;
	}

	public static String toHtml(String str) {
		String html = PubFunc.keyWord_reback(str);
		html = html.replaceAll("&amp;", "&");
		html = html.replaceAll("&nbsp;", " ");
		html = html.replaceAll("&lt;", "<");
		html = html.replaceAll("&gt;", ">");
		html = html.replaceAll("&quot;", "\"");
		html = html.replaceAll("\n", "\r\n");
		html = html.replaceAll("<br>", "\r\n");
		html = html.replaceAll("<p>", "");
		html = html.replaceAll("</p>", "");
		html = html.replaceAll("<P>", "");
		html = html.replaceAll("</P>", "");
		html = html.replaceAll("&acute;", "'");
		html = html.replaceAll("&amp;nbsp;", " ");
		html = html.replaceAll("%25", "%");
		html = html.replaceAll("%3D", "=");
		html = html.replaceAll("&llt;", "&lt;");
		html = html.replaceAll("&ggt;", "&gt;");
		html = html.replaceAll("&qquot;", "&quot;");
		return html;
	}
	
	/**
	 * 获得相应知识点的难度比例  (不想页面引入太多java类)
	 * @param key
	 * @return
	 */
	public static String getValue(String key){
		//System.out.println(key);
		String value = KnowLedgeDiffBo.getValue(key);
		value=value==null||"0".equals(value)?"":value;
		return value;
	}
	
	/**
	 * 通过知识点名称得到知识点的ID
	 */
	public  String getCodeitemId(String str) {
		Connection conns = null;
		RowSet rs = null;
		String s = "";
		try {
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			if (!"".equals(str) && str != null) {
				String sql = "select codeitemid from codeitem where codeitemdesc = '"
					+ str + "' and codesetid='68'";
				rs = dao.search(sql);
				if (rs.next()) {
					s = rs.getString("codeitemid");
				}
			}
			
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
                if(conns != null) {
                    conns.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
		}
		return s;
	}
	
	/**
	 * 通过题型名称找到题型ID
	 */
	public int getQuestionTypeIdByName(String typeName){
		Connection conns = null;
		RowSet rs = null;
		int s = 0;
		try {
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			if (!"".equals(typeName) && typeName != null) {
				String sql = "select type_id from tr_question_type where type_name = '"
					+ typeName + "'";
				rs = dao.search(sql);
				if (rs.next()) {
					s = rs.getInt("type_id");
				}
			}
			
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
                if(conns != null) {
                    conns.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
		}
		return s;
	}
	/**
	 * 保存当前行的知识点
	 */
	public  void saveKnowledge(String know_id, String r5200){
		Connection conns = null;
		try {
			List list = new ArrayList();
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			String sql1 = "insert into tr_test_knowledge values(?,?)";
			list.add(know_id);
			list.add(r5200);
			dao.insert(sql1, list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conns != null) {
					conns.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 通过试题ID得到知识点ID
	 * **/
	public static String getKonwIdByR5200(String r5200){
		String sql = " select know_id from  tr_test_knowledge where r5200 = '"+r5200+"'";
		RowSet rs = null;
		String knowId = "";
		Connection con = null;
		try {
		    con = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search(sql);
			/**
			 *  if(rs.next()){
				knowId = rs.getString("know_id");
			}
			 * */

			while(rs.next()){
				knowId += rs.getString("know_id") + ",";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
                if(con != null) {
                    con.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
		}
		return knowId;
	}
	/**
	 * 保存选择题选项
	 */
	public void updateR5207(String r5207, String r5200){
		Connection conns = null;
		try {
			List list = new ArrayList();
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			String sql = "update r52 set r5207 =? where r5200 = ?";
			list.add(r5207);
			list.add(r5200);
			dao.update(sql, list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (conns != null) {
					conns.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 处理字符串中特殊符号($、<、>、" 、\)
	 * 注：对$进行转义，对 <、>、"、\ 四个字符进行特殊处理防止字符串转为html标签时把<、>、" 、\还原
	 * @param str
	 * @return
	 */
	public static String filterSpecialStr(String str) {

		String sReturn = "";
		if (!"".equals(StringUtils.trim(str))) {

			if (str.indexOf('\\', 0) > -1) {
				while (str.length() > 0) {
					if (str.indexOf('\\', 0) > -1) {
						sReturn += str.subSequence(0, str.indexOf('\\', 0));
						sReturn += "\\\\";
						str = str.substring(str.indexOf('\\', 0) + 1, str.length());
					} else {
						sReturn += str;
						str = "";
					}
				}
			} else {
				sReturn = str;
			}
			str = sReturn;
			sReturn = "";
			if (str.indexOf('$', 0) > -1) {
				while (str.length() > 0) {
					if (str.indexOf('$', 0) > -1) {
						sReturn += str.subSequence(0, str.indexOf('$', 0));
						sReturn += "\\$";
						str = str.substring(str.indexOf('$', 0) + 1, str.length());
					} else {
						sReturn += str;
						str = "";
					}
				}
			} else {
				sReturn = str;
			}
		}
		sReturn = sReturn.replaceAll("<", "&llt;").replaceAll("&lt;", "&llt;");
		sReturn = sReturn.replaceAll(">", "&ggt;").replaceAll("&gt;", "&ggt;");
		sReturn = sReturn.replaceAll("\"","&qquot;").replaceAll("&quot;","&qquot;");
		return sReturn;
	} 
	/**
	 * 将<img>链接中的双引号换为单引号
	 * @param str
	 * @return
	 */
	public String marksSpecialStr(String str) {
		str = brToStr(str);
		String sReturn = "";
		if (!"".equals(StringUtils.trim(str))) {
		while (str.length() > 0) {
			if (str.indexOf("<img") > -1 || str.indexOf("<IMG") > -1) {
				String img = "";
				if (str.indexOf("<img") > -1) {
					sReturn += str.substring(0, str.indexOf("<img", 0));
					img = str.substring(str.indexOf("<img", 0), str.indexOf("/>", 0) + 2);
					str = str.substring(str.indexOf("/>", 0) + 2, str.length());
				} else if (str.indexOf("<IMG") > -1) {
					sReturn += str.substring(0, str.indexOf("<IMG", 0));
					img = str.substring(str.indexOf("<IMG", 0), str.indexOf(">", 0) + 1);
					str = str.substring(str.indexOf(">", 0) + 1, str.length());
				}
				if (img.indexOf("\"", 0) > 0) {
					while (img.length() > 0) {
						if (img.indexOf("\"", 0) > -1) {
							sReturn += img.substring(0, img.indexOf("\"", 0));
							sReturn += "'";
							img = img.substring(img.indexOf("\"", 0) + 1, img.length());
						} else {
							sReturn += img;
							img = "";
						}
					}
				} else {
					sReturn += str;
					str= "";
				}
				
			} else {
				sReturn += str;
				str= "";
			}
		}
		}
		sReturn = sReturn.replaceAll("\n", "<br/>");
		return sReturn;
	}
	
	/**
	 * 替换换行符为\n
	 * @param string
	 * @return
	 */
	public String brToStr(String string) {
		if (string == null || string.length() < 1) {
            return "";
        }
		string = string.replaceAll("<br>", "\n");
		string = string.replaceAll("<BR>", "\n");
		string = string.replaceAll("<br/>", "\n");
		string = string.replaceAll("<BR/>", "\n");
		string = string.replaceAll("<br />", "\n");
		string = string.replaceAll("<BR />", "\n");
		string = string.replaceAll("</br>", "\n");
		string = string.replaceAll("</BR>", "\n");
		string = string.replaceAll("</ br>", "\n");
		string = string.replaceAll("</ BR>", "\n");
		return string;
	}
	/**
	 * 查询备注指标
	 * @param itemid
	 * @param fieldsetid
	 * @param a0100
	 * @param nbase
	 * @param i9999
	 * @return
	 */
	public static String checkDate(String itemid, String fieldsetid, String a0100, String nbase, String i9999) {
		String string = "";
		RowSet rs = null;

		String sql = "select " + itemid + " from " + nbase + fieldsetid;
		if(nbase!=null&&nbase.length()>0) {
            sql+=" where a0100='" + a0100 + "' and i9999='" + i9999 + "'";
        } else if("r40".equalsIgnoreCase(fieldsetid)|| "R40".equalsIgnoreCase(fieldsetid)) {
            sql+=" where R4001='" + a0100 + "' and R4005='" + i9999 + "'";
        } else if("r31".equalsIgnoreCase(fieldsetid)|| "R31".equalsIgnoreCase(fieldsetid)) {
            sql+=" where r3101='"+a0100+"'";
        }
		Connection conns = null;
		try {
			conns = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conns);
			rs=dao.search(sql);
			if(rs.next()) {
                string = rs.getString(itemid);
            }
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
			if(rs != null) {
                rs.close();
            }
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conns != null) {
                    conns.close();
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		string = PubFunc.nullToStr(string);
		if("r4015".equalsIgnoreCase(itemid)|| "r4009".equalsIgnoreCase(itemid)) {
            string = string.replaceAll("\n", "<br>");
        }
		return string;
	}
}
