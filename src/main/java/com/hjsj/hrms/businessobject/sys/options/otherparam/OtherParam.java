package com.hjsj.hrms.businessobject.sys.options.otherparam;

import com.hjsj.hrms.businessobject.sys.options.ParseSYS_OTH_PARAM;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class OtherParam extends ParseSYS_OTH_PARAM {
	private Document doc;

	private Connection conn;

	public OtherParam(Document docs) throws JDOMException, IOException {
		super(docs);
		this.doc = doc;
		// TODO Auto-generated constructor stub
	}

	public OtherParam(String xml) throws Exception {
		super(xml);
		this.doc = super.getParamXml(xml);
	}

	public OtherParam(Connection conn) throws Exception {		
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs=null;
		try 
		{
			   /**昆仑数据库constant为保留词*/ 
			    if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
                    rs = dao.search("select * from \"constant\" where \"constant\"='SYS_OTH_PARAM'");
                } else {
                    rs = dao.search("select * from constant where constant='SYS_OTH_PARAM'");
                }
				if(rs.next())
				{
					String str_value =Sql_switcher.readMemo(rs, "str_value").trim();
					if(str_value==null|| "".equals(str_value))
					{
						str_value="<?xml version=\"1.0\" encoding=\"GB2312\"?><param></param>";
					}
					this.doc = super.getParamXml(str_value);
				}
				else
				{
					//
				    String insert=null;
					if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
                        insert = "insert into \"constant\"(\"constant\") values(?)";
                    } else {
                        insert = "insert into constant(constant) values(?)";
                    }
					
					List vl = new ArrayList();
					vl.add("SYS_OTH_PARAM");
					try {
						dao.insert(insert, vl);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.doc = super.getParamXml("<?xml version=\"1.0\" encoding=\"GB2312\"?><param></param>");
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally
			{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}	
	}

	public OtherParam() throws JDOMException, IOException {
		//List l = null;
		ContentDAO dao = new ContentDAO(this.getConn());
		RowSet rs=null;
		try 
		{
			if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
                rs = dao.search("select * from \"constant\" where \"constant\"='SYS_OTH_PARAM'");
            } else {
                rs = dao.search("select * from constant where constant='SYS_OTH_PARAM'");
            }
			if(rs.next())
			{
				String str_value =Sql_switcher.readMemo(rs, "str_value").trim();
				if(str_value==null|| "".equals(str_value))
				{
					str_value="<?xml version=\"1.0\" encoding=\"GB2312\"?><param></param>";
				}
				this.doc = super.getParamXml(str_value);
			}
			else
			{
			    String insert=null;
				if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
                    insert = "insert into \"constant\"(\"constant\") values(?)";
                } else {
                    insert = "insert into constant(constant) values(?)";
                }
				//String insert = "insert into constant(constant) values(?)";
				List vl = new ArrayList();
				vl.add("SYS_OTH_PARAM");
				try {
					dao.insert(insert, vl);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.doc = super.getParamXml("<?xml version=\"1.0\" encoding=\"GB2312\"?><param></param>");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		    if (conn != null) {
			    try {
						conn.close();
				} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
		}		
	}

	/**
	 * 根据人员库名得到相关的主集和指标
	 * 
	 * @param dbname
	 *            人员库名
	 * @return
	 */
	public Map getBaseFieldMap(String dbname) {
		Map myMap = new HashMap();
		Element root = doc.getRootElement();
		if (root != null) {
			List childrenlist = root.getChildren();
			if (childrenlist != null && childrenlist.size() > 0) {
				for (Iterator it = childrenlist.iterator(); it.hasNext();) {
					Element tempchild = (Element) it.next();
					if ("base_fields".equals(tempchild.getName())) {
						List cclist = tempchild.getChildren();
						if (cclist != null && cclist.size() > 0) {
							for (Iterator its = cclist.iterator(); its
									.hasNext();) {
								Element cce = (Element) its.next();
								if (cce.getAttributeValue("name")
										.equalsIgnoreCase(dbname)) {
									Element table = cce.getChild("table");
									Element field = cce.getChild("field");
									myMap.put("table", table.getValue());
									myMap.put("field", field.getValue());
								}
							}
						}
					}
				}
			}
		}
		return myMap;
	}

	/***************************************************************************
	 * 得到所有的人员库主集和指标
	 * 
	 * @return
	 */
	public Map getBaseFieldMap() {
		Map myMap = new HashMap();		
		Element root = doc.getRootElement();
		if (root != null) {
			List childrenlist = root.getChildren();
			if (childrenlist != null && childrenlist.size() > 0) {
				for (Iterator it = childrenlist.iterator(); it.hasNext();) {
					Element tempchild = (Element) it.next();
					if ("base_fields".equals(tempchild.getName())) {
						List cclist = tempchild.getChildren();
						if (cclist != null && cclist.size() > 0) {
							for (Iterator its = cclist.iterator(); its
									.hasNext();) {
								Element cce = (Element) its.next();
								Map tempMap = new HashMap();
								Element table = cce.getChild("table");
								Element field = cce.getChild("field");
								tempMap.put("table", table.getValue());
								tempMap.put("field", field.getValue());								
								myMap.put(cce.getAttributeValue("name"),
										tempMap);

							}
						}
					}
				}
			}
		}
		return myMap;
	}

	/**
	 * 
	 * @param field
	 * @param codesetid
	 * @param name
	 * @return
	 */
	public Map getEmployeeType(String field, String codesetid, String name) {
		Map myMap = new HashMap();
		Element root = super.getRootUri();
		if (root != null) {
			List childrenlist = root.getChildren("employ_type");
			if (childrenlist != null && childrenlist.size() > 0) {
				for (Iterator it = childrenlist.iterator(); it.hasNext();) {
					Element employeetype = (Element) it.next();
					if (employeetype.getAttributeValue("field")
							.equalsIgnoreCase(field)
							&& employeetype.getAttributeValue("codesetid")
									.equalsIgnoreCase(codesetid)) {
						List typefieldlist = employeetype
								.getChildren("type_field");
						if (typefieldlist != null && typefieldlist.size() > 0) {
							for (Iterator its = typefieldlist.iterator(); its
									.hasNext();) {
								Element e = (Element) its.next();
								if (e.getAttributeValue("name")
										.equalsIgnoreCase(name)) {
									Map tempMap = new HashMap();
									Element table = e.getChild("table");
									Element fields = e.getChild("field");
									myMap.put("table", table.getValue());
									myMap.put("field", fields.getValue());
								}
							}
						}
					}
				}
			}
		}
		return myMap;
	}

	/**
	 * 
	 * @return
	 */
	public Map getEmployeeType() {
		Map myMap = new HashMap();
		Element root = super.getRootUri();
		if (root != null) {
			Element employ_type = root.getChild("employ_type");
			if (employ_type != null) {
				List l = employ_type.getChildren();
				for (Iterator it = l.iterator(); it.hasNext();) {
					Element type_field = (Element) it.next();
					// Map typemap=new HashMap();
					myMap.put(type_field.getAttributeValue("name"), this
							.getEmtableField(type_field));					
				}
			}

		}
		return myMap;
	}

	public Map getEmtableField(Element e) {
		Map myMap = new HashMap();
		myMap.put("table", e.getChild("table").getValue());
		myMap.put("field", e.getChild("field").getValue());		
		return myMap;
	}

	public Map getEmployeeType(String field, String codesetid) {
		Map myMap = new HashMap();
		Element root = super.getRootUri();
		if (root != null) {
			List childrenlist = root.getChildren("employ_type");
			if (childrenlist != null && childrenlist.size() > 0) {
				for (Iterator it = childrenlist.iterator(); it.hasNext();) {
					Element employeetype = (Element) it.next();
					if (employeetype.getAttributeValue("field")
							.equalsIgnoreCase(field)
							&& employeetype.getAttributeValue("codesetid")
									.equalsIgnoreCase(codesetid)) {
						List typefieldlist = employeetype
								.getChildren("type_field");
						if (typefieldlist != null && typefieldlist.size() > 0) {
							for (Iterator its = typefieldlist.iterator(); its
									.hasNext();) {
								Element e = (Element) its.next();
								String atrr_name = e.getAttributeValue("name");
								myMap.put("name" + atrr_name, this
										.getEmployeeType(field, codesetid,
												atrr_name));
							}
						}
					}
				}
			}
		}
		return myMap;
	}

	/**
	 * 是否存在base_Field元素
	 * 
	 * @param xpath
	 * @return
	 * @throws JDOMException
	 */
	public boolean isExistbaseField(String name) throws JDOMException {
		boolean flag = false;
		// XPath xPath =
		// XPath.newInstance("/param/base_fields/base_field[@name="+name+"]");
		// Element e=(Element) xPath.selectSingleNode(doc);
		Element root = super.getRootUri();
		Element basefields = root.getChild("base_fields");
		List basefieldlist = basefields.getChildren();
		for (Iterator it = basefieldlist.iterator(); it.hasNext();) {
			Element base_field = (Element) it.next();
			if (base_field.getAttributeValue("name").equals(name)) {
				flag = true;
			}
		}

		return flag;
	}

	public boolean isExistbaseFields() throws JDOMException {
		boolean flag = true;
		// XPath xPath = XPath.newInstance("/param/base_fields/");
		// Element e=(Element) xPath.selectSingleNode(doc);
		Element e = super.getRootUri();
		e = e.getChild("base_fields");
		if (e == null) {
			flag = false;
		}
		return flag;
	}

	public String uporadd(String table, String field, String name)
			throws JDOMException {
		if (this.isExistbaseFields()) {
			if (this.isExistbaseField(name)) {
				XPath xPath = XPath
						.newInstance("/param/base_fields/base_field[@name='"
								+ name + "']");
				Element e = (Element) xPath.selectSingleNode(doc);
				e.removeChild("table");
				e.removeChild("field");
				Element tableatome = super
						.CreateAtomElement("table", table, "");
				Element fieldatome = super
						.CreateAtomElement("field", field, "");
				e.addContent(tableatome);
				e.addContent(fieldatome);
			} else {
				// XPath xPath = XPath.newInstance("/param/base_fields/");
				// List xlist=xPath.selectNodes(doc);
				Element root = super.getRootUri();
				Element e = root.getChild("base_fields");
				Element tableatome = super
						.CreateAtomElement("table", table, "");
				Element fieldatome = super
						.CreateAtomElement("field", field, "");
				List l = new ArrayList();
				l.add(tableatome);
				l.add(fieldatome);
				Element base_fieldmoc = super.CreateMoleculeElement(
						"base_field", "name=" + name, l);
				e.addContent(base_fieldmoc);
			}

		} else {
			Element tableatome = super.CreateAtomElement("table", table, "=");
			Element fieldatome = super.CreateAtomElement("field", field, "=");
			List l = new ArrayList();
			l.add(tableatome);
			l.add(fieldatome);
			Element base_fieldmoc = super.CreateMoleculeElement("base_field",
					"name=" + name, l);
			Element base_fieldsmoc = super.CreateMoleculeElement("base_fields",
					"valid=false", base_fieldmoc);
			this.doc = super.AddDoc(base_fieldsmoc);
		}
		String xml = super.docToString(this.doc);
		return xml;
	}

	public boolean isemploy_typeExisit(String codesetid, String typefield) {
		boolean flag = true;
		Element ez = super.getRootUri();
		Element e = ez.getChild("employ_type");

		if (e == null) {
			flag = false;
		} else {
			String cid = e.getAttributeValue("codesetid");
			String fid = e.getAttributeValue("field");
			if (!cid.equalsIgnoreCase(codesetid)
					|| !fid.equalsIgnoreCase(typefield)) {
				flag = false;
			}
		}
		return flag;
	}

	public boolean istype_fieldExist(Element e, String name) {
		boolean flag = false;
		List l = e.getChildren();
		if (l != null && l.size() > 0) {
            for (Iterator it = l.iterator(); it.hasNext();) {
                Element field = (Element) it.next();
                if (field.getAttributeValue("name").equalsIgnoreCase(name)) {
                    flag = true;
                }
            }
        }
		return flag;
	}

	/**
	 * 
	 * @param codesetid
	 * @param tfield
	 *            属性type_field
	 * @param tpname
	 *            属性 name
	 * @param table
	 *            table值
	 * @param field
	 *            field值
	 * @return
	 * @throws JDOMException
	 */
	public String uporaddfield(String codesetid, String tfield, String tpname,
			String table, String field) throws JDOMException {
		if (this.isemploy_typeExisit(codesetid, tfield)) {
			// 根节点存在
			Element firte = super.getRootUri().getChild("employ_type");
			if (this.istype_fieldExist(firte, tpname)) {
				// 相关节点存在 修改内容
				XPath xPath = XPath
						.newInstance("/param/employ_type/type_field[@name='"
								+ tpname + "']");
				Element tete = (Element) xPath.selectSingleNode(doc);
				tete.removeChild("table");
				tete.removeChild("field");
				Element tabe = super.CreateAtomElement("table", table, "=");
				Element fielde = super.CreateAtomElement("field", field, "=");
				tete.addContent(tabe);
				tete.addContent(fielde);

			} else {
				// 相关节点不存在，创建节点。
				// XPath xPath = XPath.newInstance("/param/employ_type/");
				Element tete = firte;
				Element tabe = super.CreateAtomElement("table", table, "=");
				Element fielde = super.CreateAtomElement("field", field, "=");
				List l = new ArrayList();
				l.add(tabe);
				l.add(fielde);
				Element moc = super.CreateMoleculeElement("type_field", "name="
						+ tpname, l);
				tete.addContent(moc);
			}
		} else {
			// 根节点不存在
			Element root = super.getRootUri();
			Element f = root.getChild("employ_type");
			if (f != null) {
				root.removeChild("employ_type");
			}
			Element tabe = super.CreateAtomElement("table", table, "=");
			Element fielde = super.CreateAtomElement("field", field, "=");
			List l = new ArrayList();
			l.add(tabe);
			l.add(fielde);
			Element moc = super.CreateMoleculeElement("type_field", "name="
					+ tpname, l);
			List al = new ArrayList();
			al.add("field=" + tfield);
			al.add("codesetid=" + codesetid);
			al.add("valid=false");
			Element moc2 = super.CreateMoleculeElement("employ_type", al, moc);
			super.AddDoc(moc2);
		}
		String xml = super.docToString(this.doc);
		return xml;
	}

	public String getCodesetid() {

		String codesetid = null;
		Element root = super.getRootUri();
		if (root != null) {
			Element f = root.getChild("employ_type");
			if (f != null) {
				codesetid = f.getAttributeValue("codesetid");
			}
		}
		return codesetid;

	}

	/**
	 * 判断是否存在元素
	 * 
	 * @param nodename
	 *            节点名称
	 * @return boolean
	 */
	public boolean isExistElement(String nodename) {
		boolean flag = false;
		Element root = super.getRootUri();
		if (root != null) {
			List l = root.getChildren(nodename);
			if (l.size() > 0) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 判断是否存在元素
	 * 
	 * @param nodename
	 *            元素名称
	 * @param nodeAtr
	 *            元素属性
	 * @return
	 */
	public boolean isExistElement(String nodename, String nodeAtr) {
		boolean flag = false;
		Element root = super.getRootUri();
		if (root != null) {
			List l = root.getChildren(nodename);
			if (l.size() > 0) {
				for (Iterator it = l.iterator(); it.hasNext();) {
					Element formual = (Element) it.next();
					if (formual.getAttributeValue("name").equalsIgnoreCase(
							nodeAtr)) {
						flag = true;
					}
				}

			}
		}
		return flag;
	}

	public void editFormual(String name, String src, String dest, String valid) {
		if (isExistElement("formual", name)) {
			// 修改formual
			Element root = super.getRootUri();
			List l = root.getChildren("formual");
			for (Iterator it = l.iterator(); it.hasNext();) {
				Element formual = (Element) it.next();
				if (formual.getAttributeValue("name").equalsIgnoreCase(name)) {
					formual.getAttribute("src").setValue(src);
					formual.getAttribute("dest").setValue(dest);
					formual.getAttribute("valid").setValue(valid);
					// super.AddDoc(formual);
				}
			}
		} else {
			// 增加formal
			List la = new ArrayList();
			la.add("name=" + name);
			la.add("src=" + src);
			la.add("dest=" + dest);
			la.add("valid=" + valid);
			Element formual = super.CreateAtomElement("formual", "", la);
			super.AddDoc(formual);
		}
	}

	public void editFormual(String name, String src, String birthday,
			String age, String valid, String bycardnoax) {
		if (isExistElement("formual", name)) {
			// 修改formual
			Element root = super.getRootUri();
			List l = root.getChildren("formual");
			for (Iterator it = l.iterator(); it.hasNext();) {
				Element formual = (Element) it.next();
				if (formual.getAttributeValue("name").equalsIgnoreCase(name)) {
					formual.getAttribute("src").setValue(src);
					formual.getAttribute("birthday").setValue(birthday);
					formual.getAttribute("age").setValue(age);
					formual.getAttribute("valid").setValue(valid);
					formual.getAttribute("ax").setValue(bycardnoax);
					// super.AddDoc(formual);
				}
			}

		} else {
			// 增加formal
			List la = new ArrayList();
			la.add("name=" + name);
			la.add("src=" + src);
			la.add("birthday=" + birthday);
			la.add("age=" + age);
			la.add("ax=" + bycardnoax);
			la.add("valid=" + valid);
			Element formual = super.CreateAtomElement("formual", "", la);
			super.AddDoc(formual);
		}
	}

	public String saveXml(ContentDAO dao) throws Exception {
		//String xml = this.syncField(dao);
		String xml=this.docToString(doc);
		ArrayList parslist=new ArrayList();
		parslist.add(xml);
		//System.out.println(xml);
	    String insert=null;
		if(Sql_switcher.searchDbServer()==Constant.KUNLUN) {
            dao.update("update \"constant\" set STR_VALUE=? where \"constant\"='SYS_OTH_PARAM'",parslist);
        } else {
            dao.update("update constant set STR_VALUE=? where constant='SYS_OTH_PARAM'",parslist);
        }
		
//		dao.update("update constant set STR_VALUE='" + xml
//				+ "' where constant='SYS_OTH_PARAM'");
		return xml;
	}

	public Connection getConn() {
		try {
			this.conn = AdminDb.getConnection();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	/*
	 * 获得所有计算公式设计的指标 返回一个set对象
	 */
	public Set getAllgsfield() {
		Set mySet = new HashSet();
		mySet.add("b0110");
		mySet.add("E0122");
		// mySet.add("e01a1");
		Element root = this.getRootUri();
		if (root != null) {
			List flist = root.getChildren("formual");
			for (Iterator it = flist.iterator(); it.hasNext();) {
				Element e = (Element) it.next();
				if ("true".equalsIgnoreCase(e.getAttributeValue("valid"))) {
					List al = e.getAttributes();
					for (Iterator its = al.iterator(); its.hasNext();) {
						Attribute ea = (Attribute) its.next();
						if (!"name".equalsIgnoreCase(ea.getName())
								&& !"valid".equalsIgnoreCase(ea.getName())) {
                            mySet.add(ea.getValue().toString());
                        }
					}
				}
			}
		}
		return mySet;
	}

	/**
	 * 同步计算公式中的对象 返回多有人员类别使用的人员主集 和人员类别对象
	 * 
	 * @return
	 * @throws JDOMException
	 */
	public Map getAllEmployeeSet(ContentDAO dao) throws JDOMException {
		// 获得计算公式指标

		Set gsfield = this.getAllgsfield();
		String gsset = "A01,";
		Map retMap = new HashMap();
		StringBuffer stb = new StringBuffer();
		StringBuffer sfield = new StringBuffer();
		// 获得人员类别指标
		Map myMap = this.getEmployeeType();
		for (Iterator kit = myMap.keySet().iterator(); kit.hasNext();) {
			String key = kit.next().toString();
			Map tempMap = (HashMap) myMap.get(key);
			stb.append(tempMap.get("table").toString());
			sfield.append(tempMap.get("field").toString());
			this.updateEmployeeOtherParam(key, tempMap.get("table").toString(),
					tempMap.get("field").toString(), gsfield, dao);

		}		
		if(myMap.size()<1){
			if(gsfield.size()>0){
				for(Iterator ist=gsfield.iterator();ist.hasNext();){
					sfield.append(ist.next());
					sfield.append(",");
				}
			}
		}
		stb.append("A01");
		Set tableset = new HashSet();
		Set fielditem = new HashSet();		
		String[] tables = stb.toString().split(",");
		String[] fields = sfield.toString().split(",");		
		for (int i = 0; i < tables.length; i++) {
			tableset.add(tables[i]);			
		}
		for (int j = 0; j < fields.length; j++) {
			fielditem.add(fields[j]);			
		}

		retMap.put("set", tableset);
		retMap.put("field", fielditem);
		return retMap;
	}

	/**
	 * 同步人员库指标
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	public String syncField(ContentDAO dao) throws Exception {
		// 获得人员库指标
		Map myMap = this.getAllEmployeeSet(dao);
		Map dbMap = this.getBaseFieldMap();
		
		for (Iterator di = dbMap.keySet().iterator(); di.hasNext();) {
			
			Set etSet = (Set) myMap.get("set");
			Set efSet = (Set) myMap.get("field");
			Set dbset = new HashSet();
			Set dbfield = new HashSet();
			
			String key = (String) di.next().toString();
			Map tempMap = (Map) dbMap.get(key);
			String tabless = (String) tempMap.get("table");
			String fieldss = (String) tempMap.get("field");			
			String[] tables = tabless.split(",");
			String[] fields = fieldss.split(",");
			for (int i = 0; i < tables.length; i++) {
				dbset.add(tables[i]);
			}
			for (int j = 0; j < fields.length; j++) {
				dbfield.add(fields[j]);
			}
			dbset.addAll(etSet);
			dbfield.addAll(efSet);
			// 进行同步操作。
			this.updateBaseDataOtherParam(key, dbset, dbfield, dao);
		}
		// this.saveXml(dao);
		return this.docToString(doc);
	}

	public boolean updateEmployeeOtherParam(String key, String table,
			String field, Set gsset, ContentDAO dao) {
		// 获得主集
		boolean flag = true;
		try {
			String[] teptable = table.split(",");
			Set tempset = new HashSet();
			for (int i = 0; i < teptable.length; i++) {
				tempset.add(teptable[i]);
			}
			tempset.add("A01");

			// 获得指标
			String[] tempfield = field.split(",");
			Set tempfset = new HashSet();
			for (int j = 0; j < tempfield.length; j++) {
				tempfset.add(tempfield[j]);
			}
			// tempfset.add(gsset);
			tempfset.addAll(gsset);
			// String tsorttable=tempset.toString();
			StringBuffer sbsrotable = new StringBuffer();
			for (Iterator tit = tempset.iterator(); tit.hasNext();) {
				String tabname = (String) tit.next().toString() + ",";
				sbsrotable.append(tabname);
			}
			String sorttable = sbsrotable.toString();

			// String tsortfield=tempfset.toString();
			StringBuffer sbsrotfield = new StringBuffer();
			for (Iterator fit = tempfset.iterator(); fit.hasNext();) {
				String fieldname = (String) fit.next().toString() + ",";
				sbsrotfield.append(fieldname);
			}
			String sortfield = sbsrotfield.toString();

			XPath xPath = XPath
					.newInstance("/param/employ_type/type_field[@name='" + key
							+ "']");
			Element tete = (Element) xPath.selectSingleNode(doc);
			tete.removeChild("table");
			tete.removeChild("field");
			Element tabe = super.CreateAtomElement("table", sorttable, "=");
			Element fielde = super.CreateAtomElement("field", sortfield, "=");
			tete.addContent(tabe);
			tete.addContent(fielde);
			// this.saveXml(dao);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public boolean updateBaseDataOtherParam(String name, Set table, Set field,
			ContentDAO dao) {
		boolean flag = true;
		StringBuffer sbtable = new StringBuffer();
		StringBuffer sbfield = new StringBuffer();
		for (Iterator it = table.iterator(); it.hasNext();) {
			String tset = it.next().toString();
			sbtable.append(tset + ",");
		}
		for (Iterator its = field.iterator(); its.hasNext();) {
			String tf = its.next().toString();
			sbfield.append(tf + ",");
		}
		try {
			XPath xPath = XPath
					.newInstance("/param/base_fields/base_field[@name='" + name
							+ "']");
			Element e = (Element) xPath.selectSingleNode(doc);
			e.removeChild("table");
			e.removeChild("field");
			Element tableatome = super.CreateAtomElement("table", sbtable
					.toString(), "");
			Element fieldatome = super.CreateAtomElement("field", sbfield
					.toString(), "");
			e.addContent(tableatome);
			e.addContent(fieldatome);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return false;
	}
}
