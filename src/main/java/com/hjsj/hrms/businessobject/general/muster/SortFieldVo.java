/**
 * 
 */
package com.hjsj.hrms.businessobject.general.muster;

import java.io.Serializable;

/**
 * <p>Title:SortFieldVo</p>
 * <p>Description:排序指标对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-5:10:22:48</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SortFieldVo implements Serializable {
	/**排序指标名*/
	private String field_name;
	/**库前缀*/
	private String pre;
	/**数据表名称*/
	private String set_name;
	/**升序或为降序0(升)=1降*/
	private int order_flag;
	
	/**
	 * 
	 */
	public SortFieldVo(String set_name,String field_name,int order_flag) {
		this.set_name=set_name;
		this.field_name=field_name;
		this.order_flag=order_flag;
	}

	public String getField_name() {
		return field_name;
	}

	public void setField_name(String field_name) {
		this.field_name = field_name;
	}

	public int getOrder_flag() {
		return order_flag;
	}

	public void setOrder_flag(int order_flag) {
		this.order_flag = order_flag;
	}

	public String getPre() {
		return pre;
	}

	public void setPre(String pre) {
		this.pre = pre;
	}

	public String getSet_name() {
		return set_name;
	}

	public void setSet_name(String set_name) {
		this.set_name = set_name;
	}

	@Override
    public String toString() {
          StringBuffer str=new StringBuffer();
          str.append("{");
          str.append("set_name=");
          str.append(this.set_name);
          str.append(",field_name=");
          str.append(this.field_name);
          str.append(",order_flag=");
          str.append(this.order_flag);          
          str.append("}");
          return str.toString();
	}	
}
