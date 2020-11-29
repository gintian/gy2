package com.hjsj.hrms.utils.analyse;

import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RetValue extends HashMap implements IParserConstant {
	
	private int valueType = 0;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private final static String Value_Key = "RetValueKey";

	/**
	 * 
	 * @return
	 */
	public Object getValue() {
		return this.get(Value_Key);
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(Object value) {		
		this.put(Value_Key, value);
		if (value instanceof Integer) {
			setValueType(INT);
			
			Integer i = (Integer)value;
			//System.out.println("int" + i.intValue());
			
		} else if (value instanceof Float) {
			setValueType(FLOAT);
			
			Float f = (Float)value;
			//System.out.println("float=" + f.floatValue());
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getValueType() {
		return valueType;
	}

	/**
	 * 
	 * @param valueType
	 */
	public void setValueType(int valueType) {		
		this.valueType = valueType;
	}

	/**
	 * 是否为整数类型
	 * @return
	 */
	public boolean isIntType() {

		return valueType == INT;
	}

	/**
	 * 是否为浮点类型
	 * @return
	 */
	public boolean isFloatType() {

		return valueType == FLOAT;
	}

	/**
	 * 是否位boolean类型
	 * @return
	 */
	public boolean isBooleanType() {

		return valueType == LOGIC;
	}

	/**
	 * 类型是否相同
	 * @param hold
	 * @return
	 */
	public boolean IsSameType(RetValue hold) {
		if (hold.valueType == NULLVALUE) {
			return true;
		}
		if (valueType == hold.getValueType()) {
			return true;
		}
		// 整数|小数 类型相同
		if ((valueType == INT || valueType == FLOAT)
				&& (hold.getValueType() == INT || hold.getValueType() == FLOAT)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是空
	 * @return
	 */
	public boolean IsNullType() {
		return valueType == NULLVALUE;
	}

	/**
	 * 是否是字符串
	 * @return
	 */
	public boolean IsStringType() {
		return valueType == STRVALUE;
	}

	/**
	 * 是否是日期类型
	 * @return
	 */
	public boolean IsDateType() {
		return valueType == DATEVALUE;
	}

	/**
	 * 求和(加)
	 * @param hold
	 * @return
	 */
	public boolean add(RetValue hold) {
		switch (valueType) {//当前数据类型
			case INT: {
				if (hold.getValueType() == INT) {
					int sum = ((Integer) getValue()).intValue()
							+ ((Integer) hold.getValue()).intValue();
					setValue(new Integer(sum));
				} else {
					float sum = ((Integer) getValue()).floatValue()
							+ ((Float) hold.getValue()).floatValue();
					setValueType(FLOAT);
					setValue(new Float(sum));
				}
				return true;
	
			}
			case FLOAT: {//
				if (hold.getValueType() == INT||hold.getValue() instanceof Integer) {
					float sum =0;
					if(this.getValue() instanceof Float||this.getValue() instanceof Integer)
					{
						sum=((Float) getValue()).floatValue()
								+ ((Integer) hold.getValue()).intValue();
					}
					else if(this.getValue() instanceof String)
					{
						sum=(Float.parseFloat((String) getValue()))+ ((Integer) hold.getValue()).intValue();
					}
					setValue(new Float(sum));
					return true;
				} else if (hold.getValueType() == FLOAT) {
					float sum =0;
					if(this.getValue() instanceof Float||this.getValue() instanceof Integer)
					{
						sum=((Float) getValue()).floatValue()
								+ ((Float) hold.getValue()).floatValue();
					}
					else if(this.getValue() instanceof String)
					{
						sum=(Float.parseFloat((String) getValue()))+ ((Float) hold.getValue()).floatValue();
					}
					setValue(new Float(sum));
					return true;
				}
	
			}
			case STRVALUE: {
				if (hold.getValueType() == STRVALUE) {
					String strAdd = (String) getValue() + (String) hold.getValue();
					setValue(strAdd);
					return true;
				}
			}

		}

		return false;
	}

	/**
	 * 减 处理
	 * @param hold
	 * @return
	 */
	public boolean minus(RetValue hold) {
		int tempType=valueType;
		if(tempType==FLOAT&&getValue() instanceof Integer)
			tempType=INT;
			
		switch (tempType) {
		case INT: {
			if (hold.getValueType() == INT) {
				int sum = ((Integer) getValue()).intValue()
						- ((Integer) hold.getValue()).intValue();
				setValue(new Integer(sum));
			} else {
				float sum = ((Integer) getValue()).floatValue()
						- ((Float) hold.getValue()).floatValue();
				setValueType(FLOAT);
				setValue(new Float(sum));
			}
			return true;
		}
		case FLOAT: {//
			if (hold.getValueType() == INT||hold.getValue() instanceof Integer) {
				float sum = ((Float) getValue()).floatValue()
						- ((Integer) hold.getValue()).floatValue();
				setValue(new Float(sum));
				return true;
			} else if (hold.getValueType() == FLOAT) {
				float sum = ((Float) getValue()).floatValue()
						- ((Float) hold.getValue()).floatValue();
				setValue(new Float(sum));
				return true;
			}
		}
		}

		return false;
	}

	/**
	 * 乘 处理
	 * @param hold
	 * @return
	 */
	public boolean multiply(RetValue hold) {
		switch (valueType) {
		case INT: {
			if (hold.getValueType() == INT) {
				int a=1;
				if(getValue()!=null&&String.valueOf(getValue()).trim().length()>0)		
					a=((Integer) getValue()).intValue();
				int sum = a* ((Integer) hold.getValue()).intValue();
				setValue(new Integer(sum));
			} else {
				float a=1;
				
				if(getValue()!=null&&String.valueOf(getValue()).trim().length()>0)					
					a=Float.parseFloat(String.valueOf(getValue()));
				 
			//	System.out.println(hold.getValueType());
				float sum = a ;
					//	* ((Float) hold.getValue()).floatValue();
				setValueType(FLOAT);
				setValue(new Float(sum));
			}
			return true;
		}
		case FLOAT: {//
			if (hold.getValueType() == INT||hold.getValue() instanceof Integer) {
				float sum = ((Float) getValue()).floatValue()
						* ((Integer) hold.getValue()).floatValue();
				setValue(new Float(sum));
				return true;
			} else if (hold.getValueType() == FLOAT) {
				float sum = ((Float) getValue()).floatValue()
						* ((Float) hold.getValue()).floatValue();
				setValue(new Float(sum));
				return true;
			}
		}

		}

		return false;
	}

	/**
	 * 除 处理
	 * @param hold
	 * @return
	 */
	public boolean division(RetValue hold) {
		switch (valueType) {
		case INT: {
			if ((hold.getValueType() == INT)
					&& (((Integer) hold.getValue()).intValue() != 0)) {
				int sum = ((Integer) getValue()).intValue()
						/ ((Integer) hold.getValue()).intValue();
				setValue(new Integer(sum));
			} else {
				float sum=0;
				if(hold.getValueType()==FLOAT)
				{
					sum = ((Integer) getValue()).floatValue()/((Float) hold.getValue()).floatValue();
				}
				else
					sum = ((Integer) getValue()).floatValue()/((Integer) hold.getValue()).floatValue();
					
				setValueType(FLOAT);
				setValue(new Float(sum));
			}
			return true;
		}
		case FLOAT: {//
			if ((hold.getValueType() == INT||hold.getValue() instanceof Integer)
					&& (((Integer) hold.getValue()).intValue() != 0)) {
				float sum = ((Float) getValue()).floatValue()
						/ ((Integer) hold.getValue()).floatValue();
				setValue(new Float(sum));
				return true;
			} else if (hold.getValueType() == FLOAT) {
				float sum = ((Float) getValue()).floatValue()
						/ ((Float) hold.getValue()).floatValue();
				setValue(new Float(sum));
				return true;
			}
		}

		}

		return false;
	}

	/**
	 * 整除 处理
	 * @param hold
	 * @return
	 */
	public boolean Div(RetValue hold) {
		if (isIntType() && hold.isIntType()) {
			int nResult = ((Integer) getValue()).intValue()
					/ ((Integer) hold.getValue()).intValue();
			setValue(new Integer(nResult));
			return true;
		}
		return false;
	}

	/**
	 * 求模 处理
	 * @param hold
	 * @return
	 */
	public boolean Mod(RetValue hold) {
		if (isIntType() && hold.isIntType()) {
			int nResult = ((Integer) getValue()).intValue()
					% ((Integer) hold.getValue()).intValue();
			setValue(new Integer(nResult));
			this.setValueType(FLOAT);
			return true;
		}
		return false;
	}

	/**
	 * boolean 类型 取反处理
	 * @return
	 */
	public boolean NotValue() {
		if (isBooleanType()) {
			setValue(new Boolean(!((Boolean) getValue()).booleanValue()));
			return true;
		}
		return false;
	}

	/**
	 *  boolean 类型 并且处理
	 * @param hold
	 * @return
	 */
	public boolean And(RetValue hold) {
		if (isBooleanType() && hold.isBooleanType()) {
			boolean b = ((Boolean) getValue()).booleanValue()
					&& ((Boolean) (hold.getValue())).booleanValue();
			setValue(new Boolean(b));
			return true;
		}
		
		if(this.IsNullType() && hold.isBooleanType())  //空<>空
		{
			setValue(new Boolean(true));
			return true;
		}
		

		if(this.isBooleanType() && hold.IsNullType())  //空<>空
		{
			setValue(new Boolean(true));
			return true;
		}
		
		if(this.IsNullType() && hold.IsNullType())  //空<>空
		{
			setValue(new Boolean(true));
			return true;
		}
		
		return false;
	}

	/**
	 * boolean 类型的 或处理
	 * @param hold
	 * @return
	 */
	public boolean Or(RetValue hold) {
		if (isBooleanType() && hold.isBooleanType()) {
			boolean b = ((Boolean) getValue()).booleanValue()
					|| ((Boolean) (hold.getValue())).booleanValue();
			setValue(new Boolean(b));
			return true;
		}
		
		if(this.IsNullType() && hold.isBooleanType())  //空<>空
		{
			setValue((Boolean) (hold.getValue()));
			return true;
		}
		
		if(this.isBooleanType() && hold.IsNullType())  //空<>空
		{
			setValue(new Boolean(true));
			return true;
		}
		
		if(this.IsNullType() && hold.IsNullType())  //空<>空
		{
			setValue(new Boolean(true));
			return true;
		}
		
		return false;
	}

	/**
	 * 自身对象 与 特定对象比较
	 * 关系运算 等于 处理
	 * @param hold  特定对象
	 * @return
	 */
	public boolean Equal(RetValue hold) {
		//System.out.println(hold.valueType);
		if (hold.valueType == NULLVALUE) {
			setValueType(LOGIC);
			this.setValue(new Boolean("true"));
			return true;
		}
		if (IsSameType(hold)) {
			switch (getValueType()) {
			case INT: {
				int holdValue = 0;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).intValue();
				} else {
					if (hold.getValue() instanceof String) {
						holdValue = Integer.parseInt((String) hold.getValue());
					} else if (hold.getValue() instanceof Integer) {
						holdValue = ((Integer) hold.getValue()).intValue();
					}
				}
				
				//zfj修改
				boolean bTemp = false;
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Integer) getValue()).intValue() == holdValue;
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case FLOAT: {
				float holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).floatValue();
				} else {
					holdValue = ((Integer) hold.getValue()).floatValue();
				}
				
				//zfj修改
				boolean bTemp = false;
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
		
				//原来
				//boolean bTemp = ((Float) getValue()).intValue() == holdValue;
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case STRVALUE: {
				boolean bTemp = ((String) getValue()).equals(hold.getValue());
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case LOGIC: {
				boolean b1 = ((Boolean) getValue()).booleanValue();
				boolean b2 = ((Boolean) hold.getValue()).booleanValue();
				boolean bTemp = (b1 && b2) || (!b1 && !b2);
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case DATEVALUE: {
				boolean bTemp = (getValue().equals(hold.getValue()));
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			}
			return true;
		}
		return false;
	}

	public boolean Greater(RetValue hold) {
		if (IsSameType(hold) && !isBooleanType() && !hold.isBooleanType()) {
			switch (getValueType()) {
			case INT: {
				int holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).intValue();
				} else {
					holdValue = ((Integer) hold.getValue()).intValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Integer) getValue()).intValue() > holdValue;
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case FLOAT: {
				float holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).floatValue();
				} else {
					holdValue = ((Integer) hold.getValue()).floatValue();
				}

				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Float) getValue()).floatValue() > holdValue;
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case STRVALUE: {
				boolean bTemp = ((String) getValue())
						.compareToIgnoreCase((String) hold.getValue()) > 0;
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case DATEVALUE: {
				boolean bTemp = ((String) getValue()).compareTo((String) hold
						.getValue()) > 0;
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
			}
			default:
				break;
			}
			return true;
		}
		return false;
	}

	public boolean NotSmaller(RetValue hold) {
		if (IsSameType(hold) && !isBooleanType() && !hold.isBooleanType()) {
			switch (getValueType()) {
			case INT: {
				int holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).intValue();
				} else {
					holdValue = ((Integer) hold.getValue()).intValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Integer) getValue()).intValue() >= holdValue;
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case FLOAT: {
				float holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).floatValue();
				} else {
					holdValue = ((Integer) hold.getValue()).floatValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Float) getValue()).floatValue() >= holdValue;
				
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case STRVALUE: {
				boolean bTemp = ((String) getValue())
						.compareToIgnoreCase((String) hold.getValue()) >= 0;
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case DATEVALUE: {
				boolean bTemp = ((String) getValue()).compareTo((String) hold
						.getValue()) >= 0;
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
			}
			default:
				break;
			}
			return true;
		}
		return false;
	}

	public boolean Smaller(RetValue hold) {
		if (IsSameType(hold) && !isBooleanType() && !hold.isBooleanType()) {
			switch (getValueType()) {
			case INT: {
				int holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).intValue();
				} else {
					holdValue = ((Integer) hold.getValue()).intValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Integer) getValue()).intValue() < holdValue;
				
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case FLOAT: {
				float holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).floatValue();
				} else {
					holdValue = ((Integer) hold.getValue()).floatValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Float) getValue()).floatValue() < holdValue;
				
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case STRVALUE: {
				boolean bTemp = ((String) getValue())
						.compareToIgnoreCase((String) hold.getValue()) < 0;
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case DATEVALUE: {
				boolean bTemp = ((String) getValue()).compareTo((String) hold
						.getValue()) < 0;
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
			}
			default:
				break;
			}
			return true;
		}
		return false;
	}

	public boolean NotGreater(RetValue hold) {
		if (IsSameType(hold) && !isBooleanType() && !hold.isBooleanType()) {
			switch (getValueType()) {
			case INT: {
				int holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).intValue();
				} else {
					holdValue = ((Integer) hold.getValue()).intValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Integer) getValue()).intValue() <= holdValue;
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case FLOAT: {
				float holdValue;
				if (hold.getValueType() == FLOAT) {
					holdValue = ((Float) hold.getValue()).floatValue();
				} else {
					holdValue = ((Integer) hold.getValue()).floatValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Float) getValue()).floatValue() <= holdValue;
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case STRVALUE: {
				boolean bTemp = ((String) getValue())
						.compareToIgnoreCase((String) hold.getValue()) <= 0;
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}

			case DATEVALUE: {
				boolean bTemp = ((String) getValue()).compareTo((String) hold
						.getValue()) <= 0;
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
			}
			default:
				break;
			}
			return true;
		}
		return false;
	}

	public boolean NotEqual(RetValue hold) {
		if (IsSameType(hold) && !isBooleanType() && !hold.isBooleanType()) {
			switch (getValueType()) {
			case INT: {
				int holdValue;
				if (hold.getValueType() == FLOAT) {
					if(hold.getValue()==null|| "null".equalsIgnoreCase(hold.getValue().toString()))
					  holdValue=0;
					else						
					  holdValue = ((Float) hold.getValue()).intValue();
				} else {
					if(hold.getValue()==null|| "null".equalsIgnoreCase(hold.getValue().toString()))
						  holdValue=0;
					else
					  holdValue = ((Integer) hold.getValue()).intValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Integer) getValue()).intValue() != holdValue;
				
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case FLOAT: {
				float holdValue;
				if (hold.getValueType() == FLOAT) {
					if(hold.getValue()==null|| "null".equalsIgnoreCase(hold.getValue().toString()))
					   holdValue=0;
					else
					   holdValue = ((Float) hold.getValue()).floatValue();
				} else {
					if(hold.getValue()==null|| "null".equalsIgnoreCase(hold.getValue().toString()))
						  holdValue=0;
					else
					   holdValue = ((Integer) hold.getValue()).floatValue();
				}
				
				boolean bTemp = false;//zfj修改
				Object obj = getValue();
				if(obj == null){
				}else{
					if (obj instanceof Integer) {
						bTemp = ((Integer) getValue()).intValue() == holdValue;
					} else if (obj instanceof Float) {
						bTemp = ((Float) getValue()).intValue() == holdValue;
					}
				}
				//boolean bTemp = ((Float) getValue()).floatValue() != holdValue;
				
				
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			case STRVALUE: {
				boolean bTemp = !((String) getValue()).equals((String) hold
						.getValue());
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}

			case DATEVALUE: {
				boolean bTemp = !getValue().equals(hold.getValue());
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			
			case MEMO: {  //20141103 dengcan   如果 拟审批意见备注<>空
				boolean bTemp = !((String) getValue()).equals((String) hold
						.getValue());
				setValueType(LOGIC);
				setValue(new Boolean(bTemp));
				break;
			}
			
			default:
				break;
			}
			return true;
		}
		return false;
	}

	public boolean MinusValue() {
		if (isIntType()) {
			setValue(new Integer(((Integer) getValue()).intValue() * (-1)));
			return true;
		} else if (isFloatType()) {
			setValue(new Float(((Float) getValue()).floatValue() * (-1)));
			return true;
		}
		return false;
	}

	public int diffYear(RetValue hold) {
		int ny1 = getYearOfDate();
		int ny2 = hold.getYearOfDate();
		return ny1 - ny2;
	}

	public int diffMonth(RetValue hold) {
		return (getYearOfDate() * 12 + getMonthOfDate())
				- (hold.getYearOfDate() * 12 + hold.getMonthOfDate());
	}

	public int diffDay(RetValue hold) {
		Calendar c1 = Calendar.getInstance();
		c1.set(getYearOfDate(), getMonthOfDate(), getDayOfDate());
		Calendar c2 = Calendar.getInstance();
		c2
				.set(hold.getYearOfDate(), hold.getMonthOfDate(), hold
						.getDayOfDate());

		long lTemp = c1.getTimeInMillis() - c2.getTimeInMillis();
		return (int) (lTemp / (1000 * 60 * 60 * 24));
	}

	public void addYear(RetValue hold) {
		if (hold.isIntType()) {
			Calendar c = Calendar.getInstance();
			c.set(getYearOfDate(), getMonthOfDate(), getDayOfDate());
			c.add(Calendar.YEAR, ((Integer) hold.getValue()).intValue());
			String strNewDate = "'" + sdf.format(c.getTime()) + "'";
			setValue(strNewDate);
			// this.setValueType(DATEVALUE);
		}
	}

	public void addMonth(RetValue hold) {
		if (hold.isIntType()) {
			Calendar c = Calendar.getInstance();
			c.set(getYearOfDate(), getMonthOfDate(), getDayOfDate());
			c.add(Calendar.MONTH, ((Integer) hold.getValue()).intValue());
			String strNewDate = "'" + sdf.format(c.getTime()) + "'";
			setValue(strNewDate);
		}
	}

	public void addDay(RetValue hold) {
		if (hold.isIntType()) {
			Calendar c = Calendar.getInstance();
			c.set(getYearOfDate(), getMonthOfDate(), getDayOfDate());
			c
					.add(Calendar.DAY_OF_MONTH, ((Integer) hold.getValue())
							.intValue());
			String strNewDate = "'" + sdf.format(c.getTime()) + "'";
			setValue(strNewDate);
		}
	}

	public void addWeek(RetValue hold) {
		if (hold.isIntType()) {
			Calendar c = Calendar.getInstance();
			c.set(getYearOfDate(), getMonthOfDate(), getDayOfDate());
			c
					.add(Calendar.WEEK_OF_YEAR, ((Integer) hold.getValue())
							.intValue());
			String strNewDate = "'" + sdf.format(c.getTime()) + "'";
			setValue(strNewDate);
		}
	}

	public void addQuarter(RetValue hold) {
		if (hold.isIntType()) {
			Calendar c = Calendar.getInstance();
			c.set(getYearOfDate(), getMonthOfDate(), getDayOfDate());
			c.add(Calendar.MONTH, ((Integer) hold.getValue()).intValue() * 3);
			String strNewDate = "'" + sdf.format(c.getTime()) + "'";
			setValue(strNewDate);
		}
	}

	public String ValueToString() {
		return getValue() == null ? "" : getValue().toString();
	}

	public int getYearOfDate() {// 注意日期类型的字符串包含左右单引号 '1966.06.24'
		if (valueType == DATEVALUE) {
			int year = 0;
			try {
				String tempValue=(String) getValue();
				if(tempValue.trim().length()==12)
					year = Integer.parseInt(((String) getValue()).substring(1, 5));
				else if(tempValue.trim().indexOf("TO_DATE")!=-1)
					year = Integer.parseInt(((String) getValue()).substring(9, 13));
				else
					year = Integer.parseInt(((String) getValue()).substring(0, 4));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return year;
		}
		return 0;
	}

	public int getMonthOfDate() {// 注意日期类型的字符串包含左右单引号 '1966.06.24'
		if (valueType == DATEVALUE) {
			String dateValue = (String) getValue();
			String s="";
			if(dateValue.trim().indexOf("TO_DATE")!=-1)
			{
				int from=dateValue.indexOf("('");
				int to=dateValue.indexOf("',");
				dateValue=dateValue.substring(from+2,to);
				dateValue=dateValue.replaceAll("\\.", "-");
				String[] dates=StringUtils.split(dateValue,"-");
				s = dates[1];
			}
			else
			{
				dateValue=dateValue.substring(1, dateValue.length()-1);
				dateValue=dateValue.replaceAll("\\.", "-");
				String[] dates=StringUtils.split(dateValue,"-");
				s = dates[1];
			}
			//String s = dateValue.substring(6, 8);	//1990.1.1 则出错		
			return Integer.parseInt(s);
		}
		return 0;
	}

	public int getDayOfDate() {// 注意日期类型的字符串包含左右单引号 '1966.06.24'
		if (valueType == DATEVALUE) {
			String dateValue = (String) getValue();
			
			String s="";
			if(dateValue.trim().indexOf("TO_DATE")!=-1)
			{
				int from=dateValue.indexOf("('");
				int to=dateValue.indexOf("',");
				dateValue=dateValue.substring(from+2,to);
				dateValue=dateValue.replaceAll("\\.", "-");
				String[] dates=StringUtils.split(dateValue,"-");
				s = dates[2];
			}
			else
			{
			
				dateValue=dateValue.substring(1, dateValue.length()-1);		
				dateValue=dateValue.replaceAll("\\.", "-");			
				String[] dates=StringUtils.split(dateValue,"-");
				s = dates[2];
			}
			return Integer.parseInt(s/*((String) getValue()).substring(9, 11)*/);////1990.1.1 则出错
		}
		return 0;
	}

	public String getTypeString() {
		switch (getValueType()) {
		case INT:
		case FLOAT: {
			return "N";
		}
		case STRVALUE: {
			return "A";
		}
		case DATEVALUE: {
			return "D";
		}
		case NULLVALUE: {
			return "NULL";
		}
		}
		return "";
	}
}
