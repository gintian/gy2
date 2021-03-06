/**
 * 
 */
package com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;

/**
 * 子集指标属性定义类
* @Title: SubField
* @Description:
* @author: hej
* @date 2019年11月19日 下午5:16:47
* @version
 */
public class SubField {
    private String fieldname;//指标名称
    private String need;//是否必填
    private String title;//列头
    private String defaultvalue;//默认值
    private String slop;//格式
    private String pre;//前缀
    private String align;//横向对齐方式
    private String valign;//垂直对齐方式
    private String titleHeight;//列高
    private String titleWidth;//宽度
    private String his_readonly;//子集列是否勾选历史记录只读  hej 20180608
    private String imppeople;//是否启用引入人员组件
    private String relation_field;//关联指标
	private FieldItem fieldItem;
	
	private String formula;//子集指标计算规则 
	private String cond;//计算条件
	private String total;//显示合计  1：显示

	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getCond() {
		return cond;
	}
	public void setCond(String cond) {
		this.cond = cond;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public void setFieldItem(FieldItem fieldItem) {
		this.fieldItem = fieldItem;
	}
    public String getTitleHeight() {
        return titleHeight;
    }
    public void setTitleHeight(String titleheight) {
        this.titleHeight = titleheight;
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
    public String getTitleWidth() {
        return titleWidth;
    }
    public void setTitleWidth(String width) {
        this.titleWidth = width;
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
	public FieldItem getFieldItem() {
		return DataDictionary.getFieldItem(this.fieldname);
	}
	public String getHis_readonly() {
		return his_readonly;
	}
	public void setHis_readonly(String his_readonly) {
		this.his_readonly = his_readonly;
	}
	public String getImppeople() {
		return imppeople;
	}
	public void setImppeople(String imppeople) {
		this.imppeople = imppeople;
	}
	public String getRelation_field() {
		return relation_field;
	}
	public void setRelation_field(String relation_field) {
		this.relation_field = relation_field;
	}
	
}
