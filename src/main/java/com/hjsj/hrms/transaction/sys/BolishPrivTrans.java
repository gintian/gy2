package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * <p>Title: BolishPrivTrans </p>
 * <p>Description:撤销用户权限</p>
 * <p>Company: hjsj </p>
 * <p>create time 2013-11-6 上午11:53:30</p>
 * 
 * @author yangj
 * @version 1.0
 */
public class BolishPrivTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String id = (String) this.getFormHM().get("id");
		ArrayList selected = (ArrayList) this.getFormHM().get("selected");
		String msg = "ok";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			RecordVo vo = new RecordVo("t_sys_function_priv", 1);
			List list = this.searchFunctionXmlHtml(id);
			// System.out.print(list.toString());
			// System.out.println();
			// System.out.println("开始");
			for (int i = selected.size() - 1; i >= 0; i--) {
				String idstatus = (String) selected.get(i);
				String str[] = idstatus.split("`");
				if (str.length == 2) {
					String iid = str[0].substring(0, 1) + str[0].substring(1).toLowerCase();//Usr 人员库前缀  + 人员编号     oracle 区分大小写  wangb 20181016
					String status = str[1];
					vo.setString("id", iid);
					vo.setString("status", status);
					vo = dao.findByPrimaryKey(vo);
					String func = vo.getString("functionpriv");
					// String func1 = vo.getString("functionpriv");
					// System.out.println("撤销" + func);
					/**
					 * 没有必要判断是否，开头和结尾
					 */
					// if (!func.startsWith(","))
					// func = "," + func;
					// if (!func.endsWith(","))
					// func = func + ",";
					/**
					 * 取消原有方法，使用新的方法，经过测试随着数据的提升，所使用的时间差距越大
					 * */
					// long st = System.nanoTime();
					// func = func.replaceAll("," + id + "[\\w]*", "");
					// for (int d = 0, n = list.size(); d < n; d++) {
					// func = func.replaceAll("," + list.get(i), "");
					// }
					// System.out.println("getDiffrent total times "
					// + (System.nanoTime() - st));
					// func = func.replaceAll("," + id + "[\\w]*", "");
					// System.out.println("撤销" + func);

					// 修改
					// st = System.nanoTime();
					func = this.getFunc(func, list,id);
					// System.out.println("getDiffrent total times "+
					// (System.nanoTime() - st));
					// if (!func.startsWith(","))
					// func = "," + func;
					// System.out.println("撤销" + func);
					vo.setString("functionpriv", func);
					// int x = dao.updateValueObject(vo);
					dao.updateValueObject(vo);
				}
			}
		} catch (Exception e) {
			msg = "error";
			e.printStackTrace();
		} finally {
			this.getFormHM().put("msg", msg);
		}
	}

	/**
	 * 
	 * @Title: getFunc
	 * @Description: 获得更正后的权限
	 * @param deleteFnc
	 *            需要删除的权限
	 * @param sqlFnc
	 *            数据库中保存的权限
	 * @return String
	 * @throws
	 */
	private String getFunc(String sqlFnc, List listDeleteFnc,String rootPriv) {
		List listSqlFnc = new ArrayList();
		// 分割字符串，删除空的，其余放入list中
		String[] strSql = sqlFnc.split(",");
		for (int i = 0, n = strSql.length; i < n; i++) {
			if (strSql[i] == null || "".equals(strSql[i]))
				continue;
			listSqlFnc.add(strSql[i]);
		}
		// 初始化要返回的字符串
		StringBuffer strBuf = new StringBuffer(500);
		strBuf.append(",");
		try {
			// 获得需要保存的权限集合
			List list = this.getDiffrent(listSqlFnc, listDeleteFnc,rootPriv);
			for (int i = 0, n = list.size(); i < n; i++) {
				strBuf.append(list.get(i));
				strBuf.append(",");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strBuf.toString();

	}

	/**
	 * 找出不同的高效算法，yangj：2013-11-06
	 * 
	 * @Title: getDiffrent
	 * @Description:
	 * @param @param list1
	 * @param @param list2
	 * @return List<String>
	 * @throws
	 */
	private List getDiffrent(List listSqlFnc, List listDeleteFnc,String rootPriv) {
		// 最差的情况就是两个list完全不相同
		Map map = new HashMap(listSqlFnc.size() + listDeleteFnc.size());
		// 返回的字符串
		List diff = new ArrayList();
		// 标识符
		Integer flag = new Integer(1);
		// 算法思想：使用map集合，在key放入list集合，value放入1。
		// 然后使用另一张表，便利查询，查到时，使原有的数据value自增1.最后查看value的值就可知道两张表的区别
		boolean boo = true;
		try {
			// 判定两张表的大小，通过不同的语句执行。先放入大的表，然后使用小的表去查询两个list的区别
			if (listDeleteFnc.size() > listSqlFnc.size())
				boo = false;
			if (boo) {
				// 放入大的数据库查询到的集合，以下方法不适合用于jdk的高版本，在高版本使用自动封装，效率更高
				for (int i = 0, n = listSqlFnc.size(); i < n; i++) {
					/* guodd 2016-01-05
					 * 添加判断：当权限号开头是 反查节点 的权限号时，不添加到新组成的权限里
					 * 例如选中反查节点权限号为 01，当前检查权限号为010101，但map中没有010101的key时，一样剔除掉
					 */
					if(listSqlFnc.get(i).toString().startsWith(rootPriv))
						continue;
					map.put(listSqlFnc.get(i), flag);
				}
				// 循环遍历小的集合，在大的集合找到是，删除该map
				for (int i = 0, n = listDeleteFnc.size(); i < n; i++) {
					if (map.containsKey(listDeleteFnc.get(i)))
						map.remove(listDeleteFnc.get(i));
				}
				// 将value为1的找出，则为不同的，放入返回的list中
				Iterator iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Entry) iterator.next();
					diff.add(entry.getKey());
				}
			} else {
				// 放入大的list集合
				for (int i = 0, n = listDeleteFnc.size(); i < n; i++) {
					map.put(listDeleteFnc.get(i), flag);
				}
				// 循环遍历小的集合，在大的集合找到是时，不做任何操作，否则放入返回的list中
				for (int i = 0, n = listSqlFnc.size(); i < n; i++) {
					/* guodd 2016-01-05
					 * 添加判断：当权限号开头是 反查节点 的权限号时，不添加到新组成的权限里
					 * 例如选中反查节点权限号为 01，当前检查权限号为010101，但map中没有010101的key时，一样剔除掉
					 */
					if (!map.containsKey(listSqlFnc.get(i)) && !listSqlFnc.get(i).toString().startsWith(rootPriv))
						diff.add(listSqlFnc.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return diff;
	}

	/**
	 * 
	 * @Title: searchFunctionXmlHtml
	 * @Description: 从功能授权配置的文件取得所有的功能编码
	 * @param curr_id
	 *            根节点
	 * @throws GeneralException
	 * @return List
	 * @throws
	 */
	private List searchFunctionXmlHtml(String curr_id) throws GeneralException {
		// 获取xml权限集合
		InputStream in = this.getClass().getResourceAsStream(
				"/com/hjsj/hrms/constant/function.xml");
		List deleteFncList = new ArrayList();
		deleteFncList.add(curr_id);
		List leafList = new ArrayList();
		try {
			Document doc = PubFunc.generateDom(in);
			List list = null;
			String xpath = "//function[@id=\"" + curr_id + "\"]";
			XPath xpath_ = XPath.newInstance(xpath);
			Element ele = (Element) xpath_.selectSingleNode(doc);
			// 获得子节点
			list = ele.getChildren("function");
			for (int i = 0; i < list.size(); i++) {
				Element node = (Element) list.get(i);
				String func_id = node.getAttributeValue("id");
				deleteFncList.add(func_id);
				// 循环加载子节点的叶子节点
				leafList = this.searchFunctionRecursive(node);
				if (leafList != null)
					deleteFncList.addAll(leafList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return deleteFncList;

	}

	/**
	 * 
	 * @Title: searchFunctionRecursive
	 * @Description: 递归查出根节点
	 * @param nodeFather
	 *            父节点
	 * @return
	 * @return List
	 * @throws
	 */
	private List searchFunctionRecursive(Element nodeFather) {
		List deleteFncList = new ArrayList();
		List leafList = new ArrayList();
		try {
			List list = null;
			list = nodeFather.getChildren("function");
			if (list.size() == 0)
				return null;
			for (int i = 0; i < list.size(); i++) {
				Element node = (Element) list.get(i);
				String func_id = node.getAttributeValue("id");
				deleteFncList.add(func_id);
				// 嵌套循环
				leafList = this.searchFunctionRecursive(node);
				if (leafList != null)
					deleteFncList.addAll(leafList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleteFncList;
	}

}
