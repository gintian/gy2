/*
 * Created on 2005-5-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.valueobject.common;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.io.Serializable;

public class  LabelValueView
    implements Serializable
{
//#############################################################################################
//#  在此定义的两属性，一个属性(label)是显示下拉列表的LabelProperty的值，一个属性(value)是为了显示     #
//#  下拉列表的Property的值，在显示下拉列表的时候就用此Bean来定义下拉列表的实际Value的值和显示的值。     #
//#############################################################################################
    public LabelValueView(String value, String label)
    {
        this.label = label;
        this.value = value;
    }

    public String getLabel()
    {
        return label;
    }
    //爱情是两个人去耕耘的。一个人不会有什么成果和结果的
    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

//##########################################################################################
//###                   此方法把两个属性变成一字符串                                         ###
//##########################################################################################
    public String toString()
    {
        StringBuffer sb = new StringBuffer("LabelValueBean[");
        sb.append(label);
        sb.append(", ");
        sb.append(value);
        sb.append("]");
        return sb.toString();
    }

    private String label;
    private String value;
}
