package com.hjsj.hrms.module.template.utils.javabean;

import com.hrms.hjsj.sys.FieldItem;

/**
 * <p>Title:TemplateItem.java</p>
 * <p>Description>:人事异动字段列表，包括普通指标、子集、临时变量；  因为人事异动与普通指标体系指标不一样，需单分出来
 * 如：在指标体系中的指标描述为岗位 ，但人事异动中叫变动前岗位或变动后岗位
 * 如：在指标体系中指标类型为日期型，但人事异动中为备注型，因为需要存储多条记录
 * 如：在指标提醒中指标名称为A0101 但在人事异动中指标名称为A0101_1
 *  </p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-2-26 下午02:17:28</p>
 * <p>@version: 7.0</p>
 */
public class TemplateItem {
	  
	/**
	 * 模板指标名称 区分变化前变化后 如子集T_a04_1_1  a0101_1
	*/
	private String fieldName;
	  
	/**
	 * 模板中自定义的指标名称 未定义则默认为指标体系中的指标名称 不再自定义拟，目前只用于列头显示 ，暂不能用于计算公式等。
	 */
	private String fieldDesc="";
	
	/**
	 * 指标类型 与指标体系中可能不一样。
	 * 存储的是人为改变类型
	 * D:日期型A:字符型N:数值型M:备注型
	 */
	private String fieldType="";
	
	/**
	 * 指标体系中对应的指标类
	 * */
	private FieldItem fieldItem;
	
	   /**
     * 指标体系中对应单元格类
     * */
    private TemplateSet cellBo;

	/**
	 * 是否是临时变量 用isVarialbe()判断 
	 * */
	private boolean bVarialbeItem=false;
	
	/**
	 * 是否是是子集 ,用isSubSet()判断
	 * */
	private boolean bSubSetItem=false;
	
	/**
	 * 是否是指标体系中的指标，定义为普通指标；其他为自定义指标 包括临时变量、子集、照片、电子签章等指标
	 * */
	private boolean bCommonFieldItem=false;
	

	/**
	 * 多个同名指标的标识
	 * */
	private String subDomainId="";	
	



	public FieldItem getFieldItem() {
		return fieldItem;
	}

	public void setFieldItem(FieldItem fieldItem) {
		this.fieldItem = fieldItem;
	}


	public String getFieldName() {
		return fieldName;
	}



	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}



	public String getFieldDesc() {
		if (fieldDesc==null || fieldDesc.length()<1){
			return fieldItem.getItemdesc();
		}
		else {
			return fieldDesc;
		}
	}



	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}



	public String getFieldType() {
		if (fieldType==null || fieldType.length()<1){
			return fieldItem.getItemtype();
		}
		else {
			return fieldType;
		}
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * 是否是是临时变量指标
	 * */
	public boolean isbVarialbeItem() {
		return bVarialbeItem;
	}

	public void setbVarialbeItem(boolean bVarialbeItem) {
		this.bVarialbeItem = bVarialbeItem;
	}
	/**
	 * 是否是子集
	 * */
	public boolean isbSubSetItem() {
		return bSubSetItem;
	}

	public void setbSubSetItem(boolean bSubSetItem) {
		this.bSubSetItem = bSubSetItem;
	}
	
	/**
	 * 是否是是普通指标体系中的指标 ,用isCommonFieldItem()判断
	 * */
	public boolean isCommonFieldItem() {
		return bCommonFieldItem;
	}

	public void setbCommonFieldItem(boolean bCommonFieldItem) {
		this.bCommonFieldItem = bCommonFieldItem;
	}

	public String getSubDomainId() {
		return subDomainId;
	}



	public void setSubDomainId(String subDomainId) {
		this.subDomainId = subDomainId;
	}


    public TemplateSet getCellBo() {
        if (cellBo==null)
            cellBo= new TemplateSet();
        return cellBo;
    }

    public void setCellBo(TemplateSet cellBo) {
        this.cellBo = cellBo;
    }
	

	
	



	
}
