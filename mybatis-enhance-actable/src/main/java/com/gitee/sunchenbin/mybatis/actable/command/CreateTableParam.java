package com.gitee.sunchenbin.mybatis.actable.command;

import java.util.List;

/**
 * 用于存放创建表的字段信息
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午6:11:17
 */
public class CreateTableParam{

	/**
	 * 字段名
	 */
	private String	fieldName;

	/**
	 * 字段类型
	 */
	private String	fieldType;

	/**
	 * 类型长度
	 */
	private int		fieldLength;

	/**
	 * 类型小数长度
	 */
	private int		fieldDecimalLength;

	/**
	 * 字段是否非空
	 */
	private boolean	fieldIsNull;

	/**
	 * 字段是否是主键
	 */
	private boolean	fieldIsKey;

	/**
	 * 主键是否自增
	 */
	private boolean	fieldIsAutoIncrement;

	/**
	 * 字段默认值
	 */
	private String	fieldDefaultValue;

	/**
	 * 该类型需要几个长度（例如，需要小数位数的，那么总长度和小数长度就是2个长度）一版只有0、1、2三个可选值，自动从配置的类型中获取的
	 */
	private int		fileTypeLength;

	/**
	 * 值是否唯一
	 */
	private boolean	fieldIsUnique;
	
	/**
	 * 索引名称
	 */
	private String filedIndexName;
	
	/**
	 * 所以字段列表
	 */
	private List<String> filedIndexValue;
	
	/**
	 * 唯一约束名称
	 */
	private String filedUniqueName;
	
	/**
	 * 唯一约束列表
	 */
	private List<String> filedUniqueValue;
	

	public String getFieldName(){
		return fieldName;
	}

	public void setFieldName(String fieldName){
		this.fieldName = fieldName;
	}

	public String getFieldType(){
		return fieldType;
	}

	public void setFieldType(String fieldType){
		this.fieldType = fieldType;
	}

	public int getFieldLength(){
		return fieldLength;
	}

	public void setFieldLength(int fieldLength){
		this.fieldLength = fieldLength;
	}

	public int getFieldDecimalLength(){
		return fieldDecimalLength;
	}

	public void setFieldDecimalLength(int fieldDecimalLength){
		this.fieldDecimalLength = fieldDecimalLength;
	}

	public boolean isFieldIsNull(){
		return fieldIsNull;
	}

	public void setFieldIsNull(boolean fieldIsNull){
		this.fieldIsNull = fieldIsNull;
	}

	public boolean isFieldIsKey(){
		return fieldIsKey;
	}

	public void setFieldIsKey(boolean fieldIsKey){
		this.fieldIsKey = fieldIsKey;
	}

	public boolean isFieldIsAutoIncrement(){
		return fieldIsAutoIncrement;
	}

	public void setFieldIsAutoIncrement(boolean fieldIsAutoIncrement){
		this.fieldIsAutoIncrement = fieldIsAutoIncrement;
	}

	public String getFieldDefaultValue(){
		return fieldDefaultValue;
	}

	public void setFieldDefaultValue(String fieldDefaultValue){
		this.fieldDefaultValue = fieldDefaultValue;
	}

	public int getFileTypeLength(){
		return fileTypeLength;
	}

	public void setFileTypeLength(int fileTypeLength){
		this.fileTypeLength = fileTypeLength;
	}

	@Deprecated
	public boolean isFieldIsUnique(){
		return fieldIsUnique;
	}

	@Deprecated
	public void setFieldIsUnique(boolean fieldIsUnique){
		this.fieldIsUnique = fieldIsUnique;
	}

	public String getFiledIndexName() {
		return filedIndexName;
	}

	public void setFiledIndexName(String filedIndexName) {
		this.filedIndexName = filedIndexName;
	}

	public List<String> getFiledIndexValue() {
		return filedIndexValue;
	}

	public void setFiledIndexValue(List<String> filedIndexValue) {
		this.filedIndexValue = filedIndexValue;
	}

	public String getFiledUniqueName() {
		return filedUniqueName;
	}

	public void setFiledUniqueName(String filedUniqueName) {
		this.filedUniqueName = filedUniqueName;
	}

	public List<String> getFiledUniqueValue() {
		return filedUniqueValue;
	}

	public void setFiledUniqueValue(List<String> filedUniqueValue) {
		this.filedUniqueValue = filedUniqueValue;
	}

}
