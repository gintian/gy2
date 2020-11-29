/**
 * 
 */
package com.hjsj.hrms.businessobject.gz.templateset;


/**
 *<p>Title:薪资项目</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-8:上午10:07:24</p> 
 *@author cmq
 *@version 4.0
 */
public class GzItemVo {
	private String setname;
	private String fldname;
	private String hz;
	private String codeid;
	private String fldtype;
	/**构库标识
	 * =0未构库，其它状态则表示已构库
	 * */
	private int flag;
	/**长度*/
	private int len;
	/**汉字长度*/
	private int hzlen;
	/**小数点的位数,=0表示整数*/
	private int flddec;
	/**
	 *显示标识 
	 * =0不可见
	 * =1可读
	 * =2可写
	 */
	private int chgstate;
	/**
	 *指标|变量标识
	 *=0指标
	 *=1变量 
	 */
	private int isvar;
	/**
	 * 读写标
	 * =0不可见
	 * =1可读
	 * =2可写
	 */	
	private int rw;
	
	/**计算公式*/
	private String formula="";
	/**初步化标识
	 * =0清零
	 * =1累积项
	 * =2导入项
	 * =3系统项
	 * =4工资标准
	 */
	private int initflag;
	/**累积方式
	 * =0不累积
	 * =1月内累积
	 * =2季度内累积
	 * =3年内累积
	 * =4无条件累种
	 * =5季度内同次累积
	 * =6年内同次累积
	 * =7同次累累
	 * */
	private int heapflag;
	/**锁住指标
	 *=0不锁住
	 *=1锁住 
	 */
	private int lock;
	/**变动标识指标
	 * =0非变动标识指标
	 * =1变动标识指标
	 * */
	private int changeflag;
	/**顺序号*/
	private int sortid;
	public int getChangeflag() {
		return changeflag;
	}
	public void setChangeflag(int changeflag) {
		this.changeflag = changeflag;
	}
	public int getChgstate() {
		return chgstate;
	}
	public void setChgstate(int chgstate) {
		this.chgstate = chgstate;
	}
	public String getCodeid() {
		return codeid;
	}
	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getFlddec() {
		return flddec;
	}
	public void setFlddec(int flddec) {
		this.flddec = flddec;
	}
	public String getFldname() {
		return fldname;
	}
	public void setFldname(String fldname) {
		this.fldname = fldname;
	}
	public String getFldtype() {
		return fldtype;
	}
	public void setFldtype(String fldtype) {
		this.fldtype = fldtype;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public int getHeapflag() {
		return heapflag;
	}
	public void setHeapflag(int heapflag) {
		this.heapflag = heapflag;
	}
	public String getHz() {
		return hz;
	}
	public void setHz(String hz) {
		this.hz = hz;
	}
	public int getHzlen() {
		return hzlen;
	}
	public void setHzlen(int hzlen) {
		this.hzlen = hzlen;
	}
	public int getInitflag() {
		return initflag;
	}
	public void setInitflag(int initflag) {
		this.initflag = initflag;
	}
	public int getIsvar() {
		return isvar;
	}
	public void setIsvar(int isvar) {
		this.isvar = isvar;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public int getLock() {
		return lock;
	}
	public void setLock(int lock) {
		this.lock = lock;
	}
	public int getRw() {
		return rw;
	}
	public void setRw(int rw) {
		this.rw = rw;
	}
	public String getSetname() {
		return setname;
	}
	public void setSetname(String setname) {
		this.setname = setname;
	}
	public int getSortid() {
		return sortid;
	}
	public void setSortid(int sortid) {
		this.sortid = sortid;
	}
	/**
	 * 是否为代码型
	 * @return
	 */
	public boolean isCodeMenu()
	{
		if("".equalsIgnoreCase(codeid)|| "0".equalsIgnoreCase(codeid))
			return false;
		else
			return true;
	}
}
