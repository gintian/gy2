package com.hjsj.hrms.businessobject.attestation.ldap.execution;

import com.hjsj.hrms.businessobject.attestation.ldap.conn.ConnFactory;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LdapExecution {

	private DirContext sslctx = null;
	
	private int SCRIPT = 0x0001;//1

	private int ACCOUNTDISABLE=0x0002;//2

	private int HOMEDIR_REQUIRED=0x0008;//	8

	private int LOCKOUT = 0x0010;//16

	private int PASSWD_NOTREQD = 0x0020; //32

	private int PASSWD_CANT_CHANGE=0x0040;//64

	private int ENCRYPTED_TEXT_PWD_ALLOWED=0x0080;//128

	private int TEMP_DUPLICATE_ACCOUNT=0x0100;//256

	private int NORMAL_ACCOUNT = 0x0200;//512

	private int INTERDOMAIN_TRUST_ACCOUNT=0x0800;//2048

	private int WORKSTATION_TRUST_ACCOUNT=0x1000;//4096

	private int SERVER_TRUST_ACCOUNT=0x2000;//8192

	private int DONT_EXPIRE_PASSWORD=0x10000;//65536

	private int MNS_LOGON_ACCOUNT=0x20000;//131072

	private int SMARTCARD_REQUIRED=0x40000;//262144

	private int TRUSTED_FOR_DELEGATION=0x80000;//524288

	private int NOT_DELEGATED=0x100000;//1048576

	private int USE_DES_KEY_ONLY=0x200000;//2097152

	private int DONT_REQ_PREAUTH=0x400000;//4194304

	private int PASSWORD_EXPIRED=0x800000;//8388608

	private int TRUSTED_TO_AUTH_FOR_DELEGATION=0x1000000;//16777216
	
	public LdapExecution(){
//		ConnFactory cf = new ConnFactory("192.192.100.212","Administrator@smallbusiness.local","1234","DC=smallbusiness,DC=local","C:\\Users\\Administrator\\security51.keystore","123456");
		ConnFactory cf = new ConnFactory();
		this.sslctx = cf.getSSLConn();
	}
	
	public void add(String name,Attributes attrs) throws NamingException {
		if(attrs.get("unicodePwd") != null){
			procPwd(name,attrs);
		}
		this.sslctx.createSubcontext(name, attrs);
	}
	
	public void del(String name) throws NamingException{
		this.sslctx.destroySubcontext(name);
	}
	
	/**
	 * 修改属性   在attrID存在属性名 attrs存在 而LDAP服务器没有 视为新增
	 *           在attrID存在属性名 attrs存在 而LDAP服务器存在 视为修改
	 *           在attrID存在属性名 attrs不存在 而LDAP服务器存在 视为删除
	 * @param name    
	 * @param attrID  所以需要处理的属性名组
	 * @param attrs   修改后的属性
	 * @throws NamingException
	 */
	public void mod(String name,String attrID[],Attributes attrs) throws NamingException{
		if(attrs.get("unicodePwd") != null){
			procPwd(name,attrs);
		}
		BasicAttributes updateAttrs = new BasicAttributes();
		BasicAttributes addAttrs = new BasicAttributes();
		BasicAttributes delAttrs = new BasicAttributes();
		Attributes searchAttrs;
		searchAttrs = getAttributeByName(name);
		for (int i = 0; i < attrID.length; i++) {
			Attribute getAttr = searchAttrs.get(attrID[i]);
			if (attrs.get(attrID[i]) == null && getAttr != null) {
				delAttrs.put(getAttr);
			} else if (getAttr == null && attrs.get(attrID[i]) != null) {
				addAttrs.put(attrs.get(attrID[i]));
			} else if (getAttr != null && attrs.get(attrID[i]) != null) {
				updateAttrs.put(attrs.get(attrID[i]));
			}
		}
		delAttr(name,delAttrs);//删除属性
		modAttr(name,updateAttrs);//修改属性
		addAttr(name,addAttrs);//新增属性
	}
	
	public void mod(String name,ModificationItem[] mods) throws NamingException{
		this.sslctx.modifyAttributes(name, mods);
	}
	
	private void delAttr(String name,Attributes attrs) throws NamingException{
		this.sslctx.modifyAttributes(name, DirContext.REMOVE_ATTRIBUTE, attrs);
	}
	
	private void modAttr(String name,Attributes attrs) throws NamingException{
		this.sslctx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
	}
	
	private void addAttr(String name,Attributes attrs) throws NamingException{
		this.sslctx.modifyAttributes(name, DirContext.ADD_ATTRIBUTE, attrs);
	}
	
	public void modPassword(String name,String password) throws NamingException, UnsupportedEncodingException{
		BasicAttributes attrs = new BasicAttributes();
		String newQuotedPassword = "\"" + password + "\"";
		byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
		attrs.put("unicodePwd", newUnicodePassword);
		this.sslctx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
	}
	
	/**
	 * 绑定对象
	 * @param name
	 * @param obj
	 * @param attrs
	 * @throws NamingException
	 */
	public void bind(String name,String obj,BasicAttributes attrs) throws NamingException{
		this.sslctx.bind(name, obj, attrs);
	}
	
	/**
	 * 绑定对象
	 * @param name
	 * @param obj
	 * @throws NamingException
	 */
	public void bind(String name,String obj) throws NamingException{
		this.sslctx.bind(name, obj);
	}
	
	public Object lookup(String name) throws NamingException{
		return this.sslctx.lookup(name);
	}
	
	public List getName(String name,String filter) throws NamingException {
		ArrayList list = new ArrayList();
		// String account = (String) mapSet.get("account");
		SearchControls constraints = new SearchControls();
		if(name == null){
			name = "";
		}
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);// 可以在SearchControls对象中设置范围
		NamingEnumeration ne = this.sslctx.search(name, filter, constraints);
		while (ne != null && ne.hasMoreElements()) {
			Object obj = ne.nextElement();
			if (obj instanceof SearchResult) {
				SearchResult is = (SearchResult) obj;
				list.add(is.getName());
			}
		}
		return list;
	}
	
	
	public List getAttribute(String name,String filter) throws NamingException {
		ArrayList list = new ArrayList();
		// String account = (String) mapSet.get("account");
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);// 可以在SearchControls对象中设置范围
		NamingEnumeration ne = this.sslctx.search("", filter, constraints);
		while (ne != null && ne.hasMoreElements()) {
			Object obj = ne.nextElement();
			if (obj instanceof SearchResult) {
				SearchResult is = (SearchResult) obj;
				list.add(is.getAttributes());
			}
		}
		return list;
	}
	
	/**
	 * 根据name获取属性
	 * @param name
	 * @return
	 * @throws NamingException
	 */
	public Attributes getAttributeByName(String name) throws NamingException{
		return this.sslctx.getAttributes(name);
	}
	
	
	public void rename(String oldName,String newName) throws NamingException{
		this.sslctx.rename(oldName, newName);
	}
	/**
	 * 对密码数据进行处理
	 * @param attrs
	 * @throws NamingException
	 */
	public void procPwd(String name,Attributes attrs) throws NamingException{
		String newQuotedPassword = "\"" + attrs.get("unicodePwd").getID() + "\"";
		byte[] newUnicodePassword;
		try {
			newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
			attrs.put("unicodePwd", newUnicodePassword);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 用户禁用
	 * @param name
	 * @return
	 */
	public boolean disable(String name){
		try {
			BasicAttributes attrs = new BasicAttributes();
			attrs.put("userAccountControl", Integer.toString(ACCOUNTDISABLE + NORMAL_ACCOUNT));// 启用：512，禁用：514，// 密码永不过期：66048
			this.sslctx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 用户启用
	 * @param name
	 * @return
	 */
	public boolean enable(String name){
		try {
			
			BasicAttributes attrs = new BasicAttributes();
			attrs.put("userAccountControl", Integer.toString(NORMAL_ACCOUNT + PASSWORD_EXPIRED));
			this.sslctx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE,attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 用户密码永不过期
	 * @param name
	 * @return
	 */
	public boolean neverExpire(String name){
		try {
			BasicAttributes attrs = new BasicAttributes();
			attrs.put("userAccountControl", Integer.toString(NORMAL_ACCOUNT + DONT_EXPIRE_PASSWORD + PASSWORD_EXPIRED));// 启用：512，禁用：514，// 密码永不过期：66048
			this.sslctx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void close() throws NamingException{
		if(this.sslctx != null){
			this.sslctx.close();
		}
	}
	
	public static void main(String[] args) {
		LdapExecution le = new LdapExecution();
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);// 可以在SearchControls对象中设置范围
		try {
			NamingEnumeration ne = le.sslctx.search("OU=董事会工作部,OU=集团总部,OU=某集团公司", "cn=*", constraints);
			while (ne != null && ne.hasMoreElements()) {
				Object obj = ne.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult is = (SearchResult) obj;
					Attributes attrs = is.getAttributes();
					System.out.println(attrs);
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
//		try {
//			le.modPassword("CN=张普发,OU=董事会工作部,OU=集团总部,OU=某集团公司", "123456789");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
		le.disable("CN=张普发,OU=董事会工作部,OU=集团总部,OU=某集团公司");
	}
}
