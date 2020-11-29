/**
 * 
 */
package com.hjsj.hrms.transaction.mobileapp.template.util;

/**
 * 类名称:SubTbale
 * 类描述:
 * 创建人: xucs
 * 创建时间:2013-11-29 下午04:27:44 
 * 修改人:xucs
 * 修改时间:2013-11-29 下午04:27:44
 * 修改备注:
 * @version
 *
 */
public class SubTable {
    //<field name="A0415" need="false" width="13" title="起始时间" default="" align="1" slop="0" pre="" valign="1"/>
    private String fieldname;
    private String need;
    private String width;
    private String title;
    private String defaultvalue;
    private String align;
    private String slop;
    private String pre;
    private String valign;
    private String titleheight;
    public String getTitleheight() {
        return titleheight;
    }
    public void setTitleheight(String titleheight) {
        this.titleheight = titleheight;
    }
    public String getAlign() {
        return align;
    }
    public String getSlop() {
        return slop;
    }
    public String getValign() {
        return valign;
    }
    public String getNeed() {
        return need;
    }
    public void setNeed(String need) {
        this.need = need;
    }
    public void setAlign(String align) {
        this.align = align;
    }
    public void setSlop(String slop) {
        this.slop = slop;
    }
    public void setValign(String valign) {
        this.valign = valign;
    }
    public String getFieldname() {
        return fieldname;
    }
    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }
    public String getWidth() {
        return width;
    }
    public void setWidth(String width) {
        this.width = width;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDefaultvalue() {
        return defaultvalue;
    }
    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }
    public String getPre() {
        return pre;
    }
    public void setPre(String pre) {
        this.pre = pre;
    }
}
