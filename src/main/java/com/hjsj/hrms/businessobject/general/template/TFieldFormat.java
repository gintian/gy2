/**
 * 
 */
package com.hjsj.hrms.businessobject.general.template;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 19, 200610:17:01 AM
 * @author chenmengqing
 * @version 4.0
 */
public class TFieldFormat {
	/**列标题*/
	private String title;
	/**是否必填*/
	private boolean bneed;
	/**默认值*/
	private String value;
	/**指标代码A0101*/
	private String name;
	/**宽度*/
	private int width;
	/**水平排列方式
	 * align: 对齐方式, 0:水平居左(默认值),1:水平居中,2:水平居右
     */
	private int align=0;
    /**垂直排列方式
     * align: 对齐方式, 0:上(默认值),1:居中,2:下
     */
	private int Valign=0;
	/**对应的指标*/
	private FieldItem fielditem;
	private String slop;		//  日期类型 xieguiquan 20101027
	private String pre;//前缀符 zhaoxg add 2016-5-27
	private boolean his_readonly;//子集列是否勾选历史记录只读  hej 20180608
	public FieldItem getFielditem() {
		return fielditem;
	}

	public void setFielditem(FieldItem fielditem) {
		this.fielditem = fielditem;
	}

	public boolean isBneed() {
		return bneed;
	}

	public void setBneed(boolean bneed) {
		this.bneed = bneed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		FieldItem item=DataDictionary.getFieldItem(name);
		if(item!=null) {
            this.fielditem=(FieldItem)item.cloneItem();
        }
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public TFieldFormat() {
		super();
	}

	public int getAlign() {
		return align;
	}

	public void setAlign(int align) {
		this.align = align;
	}

	public String getSlop() {
		return slop;
	}

	public void setSlop(String slop) {
		this.slop = slop;
	}

    public int getValign() {
        return Valign;
    }

    public void setValign(int valign) {
        Valign = valign;
    }

	public String getPre() {
		return pre;
	}

	public void setPre(String pre) {
		this.pre = pre;
	}
	
	public boolean isHis_readonly() {
		return his_readonly;
	}

	public void setHis_readonly(boolean his_readonly) {
		this.his_readonly = his_readonly;
	}
}
