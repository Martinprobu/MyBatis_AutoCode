package com.uniweibo.util.gencode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uniweibo.util.gencode.CodeGenDao.TableDesc;

/**
 * <h3>MyBatis项目基础编码生成器</h3>
 * 自动根据数据表生成基本的代码，如pojo对象，web, service, dao三层的curd方法代码, 及相关AngularJs前端逻辑代码 等。
 * 可在 applicationContextCodeGen.xml 中修改数据库配置及包名前缀 
 * @author BillWu
 * @since 2013-11-11
 * 
 */
public class CodeGen {
	
	// configure it in the applicationContextCodeGen.xml
	private static String BASE_PACKAGE = "com.bingbang";
	private static String BASE_PACKAGE_SUFFIX = "";
	  
	private String basePath;
	private CodeGenDao dao;
	
	public CodeGen() {
		basePath = getBasePath();
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:applicationContextCodeGen.xml");
		dao = (CodeGenDao) applicationContext.getBean("codeGenDao");
		this.BASE_PACKAGE = dao.basePackage;
		if(null != dao.basePackageSuffix && !"".equals(dao.basePackageSuffix.trim())) {
			this.BASE_PACKAGE_SUFFIX = "." + dao.basePackageSuffix;
		}
	}
	
	/**
	 * 生成POJO对象
	 * @param tableName
	 * @param list
	 * @return
	 */
	public void genPojoObj(String tableName, List<TableDesc> list) {
		String className = nameCovert(tableName, true);
		StringBuilder sb = new StringBuilder();
		String packagePath = BASE_PACKAGE + ".pojo" + BASE_PACKAGE_SUFFIX;
		sb.append("package " + packagePath + ";");
		sb.append("\n");
		sb.append("\n");
		sb.append("import java.io.Serializable;");
		sb.append("\n");
		sb.append("import java.sql.Timestamp;");
		sb.append("\n");
		sb.append("import " + packagePath + ".BasePojo;");
		sb.append("\n");
		sb.append("\n");
		sb.append("public class " + className.toString() + " extends BasePojo implements Serializable {");
		sb.append("\n");
		sb.append("\n");
		sb.append("\t");
		sb.append("private static final long serialVersionUID = 1L;");
		sb.append("\n");
		
		for(int i=0; i<list.size(); i++) {
			sb.append("\t");
			sb.append("private " + typeContvert(list.get(i).getType()) + " " + nameCovert(list.get(i).getField(), false) + ";");
			sb.append("\n");
		}

		sb.append("\n");
		sb.append("\t");
		sb.append("public " + className + "(){}");
		sb.append("\n");
		sb.append("\n");
		
		for(int i=0; i<list.size(); i++) {
			sb.append("\t");
			sb.append("public " + typeContvert(list.get(i).getType()) + " get" + nameCovert(list.get(i).getField(), true) + "() {");
			sb.append("\n");
			sb.append("\t");sb.append("\t");
			sb.append("return " + nameCovert(list.get(i).getField(), false) + ";");
			sb.append("\n");
			sb.append("\t");
			sb.append("}");
			sb.append("\n");
			
			sb.append("\t");
			sb.append("public void set" + nameCovert(list.get(i).getField(), true) + "(" + typeContvert(list.get(i).getType()) + " " + nameCovert(list.get(i).getField(), false) + ") {");
			sb.append("\n");
			sb.append("\t");sb.append("\t");
			sb.append("this. " + nameCovert(list.get(i).getField(), false) + " = " + nameCovert(list.get(i).getField(), false) + ";");
			sb.append("\n");
			sb.append("\t");
			sb.append("}");
			sb.append("\n");
		}
		
		sb.append("\n");
		sb.append("}");
		
		System.out.println(sb.toString());
		
		
		try {
			File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
			fileDirect.mkdirs();
			File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator  + className + ".java");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * 生成Dao接口
	 */
	public void genDaoObj(String tableName) {
		String className = nameCovert(tableName, true);
		String objectName = nameCovert(tableName, false);
		StringBuilder sb = new StringBuilder();
		String packagePath = BASE_PACKAGE + ".dao.mapper" + BASE_PACKAGE_SUFFIX;
		sb.append("package " + packagePath + ";");
		sb.append("\n");
		sb.append("\n");
		sb.append("import java.util.List;");
		sb.append("\n");
		sb.append("\n");
		sb.append("import " + BASE_PACKAGE + ".pojo" + BASE_PACKAGE_SUFFIX  + "." + className + ";");
		sb.append("\n");
		sb.append("import java.util.List;");
		sb.append("\n");
		sb.append("import org.apache.ibatis.annotations.Param;");
		sb.append("\n");
		sb.append("\n");
		sb.append("public interface " + className + "Mapper {");
		sb.append("\n");
		sb.append("\n");
		
		
		sb.append("\t");
		sb.append("public int insert" + className + "(" + className + " " + objectName + ");");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public int update" + className + "(" + className + " " + objectName + ");");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public int remove" + className + "(" + className + " " + objectName + ");");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public int remove" + className + "(Integer id);");
		sb.append("\n");
		sb.append("\n");

		sb.append("\t");
		sb.append("public " + className + " get" +className+ "(Integer id);");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public List<" + className + "> list" +className+ "();");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public List<" + className + "> paging" +className+ "(" + className + " " + objectName + ");");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public int list" + className + "Count();");
		sb.append("\n");
		sb.append("\n");
				
		sb.append("\n");
		sb.append("}");
		
		System.out.println(sb.toString());
		
		
		try {
			File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
			fileDirect.mkdirs();
			File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator  + className + "Mapper.java");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 生成Dao mybatis 映射文件
	 * @param tableName
	 */
	public void genDaoObjXml(String tableName, List<TableDesc> list) {
		String className = nameCovert(tableName, true);
		String objectName = nameCovert(tableName, false);
		StringBuilder sb = new StringBuilder();
		String packagePath = BASE_PACKAGE + ".dao.mapper" + BASE_PACKAGE_SUFFIX;
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		sb.append("\n");
		sb.append("<!DOCTYPE mapper");
		sb.append("\n");
		sb.append("\tPUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"");
		sb.append("\n");
		sb.append("\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
		sb.append("\n");
		sb.append("<mapper namespace=\"" + packagePath + "." + className + "Mapper\">");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("<resultMap id=\"" +objectName+ "ResultMap\" type=\"" +BASE_PACKAGE+ ".pojo" + BASE_PACKAGE_SUFFIX + "." +className+ "\">");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("<id property=\"" +nameCovert(list.get(0).getField(), false)+ "\" column=\"" +list.get(0).getField()+ "\"/>");
		for(int i=1; i<list.size(); i++) {
			sb.append("\n");
			sb.append("\t");sb.append("\t");
			sb.append("<result property=\"" +nameCovert(list.get(i).getField(), false)+ "\" column=\"" +list.get(i).getField()+ "\"/>");
		}
		sb.append("\n");
		sb.append("\t");
		sb.append("</resultMap>");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("<insert id=\"insert" + className + "\">");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("insert into " + tableName + " (");
		for(int i=1; i<list.size(); i++) {
			sb.append("\n");
			sb.append("\t");sb.append("\t");sb.append("\t");
			sb.append(list.get(i).getField() + ",");
		}
		sb.replace(0, sb.length(), sb.substring(0, sb.length()-1));
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append(")");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("values (");
		for(int i=1; i<list.size(); i++) {
			sb.append("\n");
			sb.append("\t");sb.append("\t");sb.append("\t");
			sb.append("#{" + nameCovert(list.get(i).getField(), false) + "},");
		}
		sb.replace(0, sb.length(), sb.substring(0, sb.length()-1));
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append(")");
		//sb.append("insert into " + tableName + " values (); ");
		sb.append("\n");
		sb.append("\t");
		sb.append("</insert>");
		sb.append("\n");
		sb.append("\n");
		
		
		sb.append("\t");
		sb.append("<update id=\"update" + className + "\">");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("update ");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append(tableName);
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("set");
		for(int i=1; i<list.size(); i++) {
			sb.append("\n");
			sb.append("\t");sb.append("\t");sb.append("\t");
			sb.append(list.get(i).getField() + " = #{" + nameCovert(list.get(i).getField(), false) + "},");
		}
		sb.replace(0, sb.length(), sb.substring(0, sb.length()-1));
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("where");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("id = #{id}");
		//sb.append("update " + tableName + " set xx=yy where id = #{id}");
		sb.append("\n");
		sb.append("\t");
		sb.append("</update>");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("<delete id=\"remove" + className + "\">");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("delete from " + tableName + " where id = #{id} ");
		sb.append("\n");
		sb.append("\t");
		sb.append("</delete>");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("<select id=\"get" + className + "\" resultMap=\"" +objectName+ "ResultMap\">");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("select * from " + tableName + " where id = #{id}");
		sb.append("\n");
		sb.append("\t");
		sb.append("</select>");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("<select id=\"list" + className + "\" resultMap=\"" +objectName+ "ResultMap\">");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("select * from " + tableName + "");
		sb.append("\n");
		sb.append("\t");
		sb.append("</select>");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("<select id=\"paging" + className + "\" resultMap=\"" +objectName+ "ResultMap\">");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("select * from " + tableName + " limit #{offset}, #{number}");
		sb.append("\n");
		sb.append("\t");
		sb.append("</select>");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("<select id=\"list" + className + "Count\" resultType=\"int\">");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("select count(1) from " + tableName + "");
		sb.append("\n");
		sb.append("\t");
		sb.append("</select>");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\n");
		sb.append("</mapper>");
		
		try {
			File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
			fileDirect.mkdirs();
			File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator  +className + "Mapper.xml");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 生成Service 代码
	 * @param tableName
	 */
	public void genService(String tableName) {
		String className = nameCovert(tableName, true);
		String objectName = nameCovert(tableName, false);
		StringBuilder sb = new StringBuilder();
		String packagePath = BASE_PACKAGE + ".service" + BASE_PACKAGE_SUFFIX;
		sb.append("package " + packagePath + ";");
		sb.append("\n");
		sb.append("\n");
		sb.append("import java.util.List;");
		sb.append("\n");
		sb.append("\n");
		sb.append("import " + BASE_PACKAGE + ".pojo" + BASE_PACKAGE_SUFFIX + "." + className + ";");
		sb.append("\n");
		sb.append("import " + BASE_PACKAGE + ".dao.mapper" + BASE_PACKAGE_SUFFIX + "." + className + "Mapper;");
		sb.append("\n");
		sb.append("\n");		
		sb.append("import org.springframework.beans.factory.annotation.Autowired;");
		sb.append("\n");
		sb.append("import org.springframework.stereotype.Service;");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("@Service");
		sb.append("\n");
		sb.append("public class " + className + "Service {");
		sb.append("\n");
		sb.append("\n");
		sb.append("\t");
		sb.append("@Autowired");
		sb.append("\n");
		sb.append("\t");
		sb.append("private " + className + "Mapper " + objectName + "Mapper;");
		sb.append("\n");
		sb.append("\n");		
		sb.append("\t");
		
		sb.append("public int insert" + className + "(" + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("return " + objectName + "Mapper.insert" + className + "(" + objectName + ");");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public int update" + className + "(" + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("return " + objectName + "Mapper.update" + className + "(" + objectName + ");");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public int remove" + className + "(" + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("return " + objectName + "Mapper.remove" + className + "(" + objectName + ");");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public int remove" + className + "ById(Integer id) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("return " + objectName + "Mapper.remove" + className + "(id);");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");

		sb.append("\t");
		sb.append("public " + className + " get" + className + "(Integer id)" + " {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("return " + objectName + "Mapper.get" + className + "(id);");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public List<" + className + "> list" + className + "()" + " {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("return " + objectName + "Mapper.list" + className + "();");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("public List<" + className + "> paging" + className + "(" + className + " " + objectName + ")" + " {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("List<" +className+ "> list = " + objectName + "Mapper.paging" + className + "(" + objectName + ");");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (list.size() > 0) {");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		
		sb.append("list.get(0).setTotalCount(");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append(objectName + "Mapper.list" +className+ "Count());");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("list.get(0).setNumber(" + objectName + ".getNumber());");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("list.get(0).setCurrentPage(" + objectName + ".getCurrentPage());");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return list;");
		//sb.append("return " + objectName + "Mapper.page" + className + "(offset, number);");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\n");
		sb.append("}");
		
		System.out.println(sb.toString());
		
		
		try {
			File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
			fileDirect.mkdirs();
			File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator + className + "Service.java");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 生成Controller 代码
	 * @param tableName
	 */
	public void genController(String tableName) {
		String className = nameCovert(tableName, true);
		String objectName = nameCovert(tableName, false);
		StringBuilder sb = new StringBuilder();
		String packagePath = BASE_PACKAGE + ".web" + BASE_PACKAGE_SUFFIX;
		sb.append("package " + packagePath + ";");
		sb.append("\n");
		sb.append("\n");
		sb.append("import java.util.List;");
		sb.append("\n");
		sb.append("import java.util.ArrayList;");
		sb.append("\n");
		sb.append("import java.util.Map;");
		sb.append("\n");
		sb.append("import java.util.HashMap;");
		sb.append("\n");
		sb.append("\n");
		sb.append("import javax.servlet.http.HttpServletRequest;");
		sb.append("\n");
		sb.append("import javax.servlet.http.HttpServletResponse;");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("import org.apache.commons.lang.StringUtils;");
		sb.append("\n");
		sb.append("import org.springframework.beans.factory.annotation.Autowired;");
		sb.append("\n");
		sb.append("import org.springframework.stereotype.Controller;");
		sb.append("\n");
		sb.append("import org.springframework.web.bind.annotation.RequestMapping;");
		sb.append("\n");
		sb.append("import org.springframework.web.bind.annotation.RequestMethod;");
		sb.append("\n");
		sb.append("import org.springframework.web.bind.annotation.ResponseBody;");
		sb.append("\n");
		sb.append("import org.springframework.web.servlet.ModelAndView;");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("import " + BASE_PACKAGE + ".pojo" + BASE_PACKAGE_SUFFIX + "." + className + ";");
		sb.append("\n");
		sb.append("import " + BASE_PACKAGE + ".service" + BASE_PACKAGE_SUFFIX + "." + className + "Service;");
		sb.append("\n");
		sb.append("\n");
		
		
		sb.append("@Controller");
		sb.append("\n");
		sb.append("@RequestMapping(\"/" + className.toLowerCase() + "\")");
		sb.append("\n");
		sb.append("public class " + className + "Controller {");
		sb.append("\n");
		sb.append("\n");
		sb.append("\t");
		sb.append("@Autowired");
		sb.append("\n");
		sb.append("\t");
		sb.append("private " + className + "Service " + objectName + "Service;");
		sb.append("\n");
		sb.append("\n");		
		sb.append("\t");
	
			
		sb.append("@RequestMapping(value=\"/create\", method=RequestMethod.POST)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map insert" + className + "(HttpServletRequest request, HttpServletResponse response, " + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", false);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (" + objectName + "Service.insert" + className + "(" + objectName + ") > 0) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", true);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("@RequestMapping(value=\"/update\", method=RequestMethod.POST)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map update" + className + "(HttpServletRequest request, HttpServletResponse response, " + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", false);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (" + objectName + "Service.update" + className + "(" + objectName + ") > 0) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", true);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");

		
		sb.append("\t");
		sb.append("@RequestMapping(value=\"/remove\", method=RequestMethod.POST)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map remove" + className + "(HttpServletRequest request, HttpServletResponse response, " + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", false);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (" + objectName + "Service.remove" + className + "(" + objectName + ") > 0) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", true);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("@RequestMapping(value=\"/removebyid\", method=RequestMethod.POST)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map remove" + className + "(HttpServletRequest request, HttpServletResponse response, Integer id) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", false);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (" + objectName + "Service.remove" + className + "ById(id) > 0) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", true);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("@RequestMapping(value=\"/get\", method=RequestMethod.GET)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map get" + className + "(HttpServletRequest request, HttpServletResponse response, Integer id) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append(className + " obj = " + objectName + "Service.get" + className + "(id);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"result\", obj);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("@RequestMapping(value=\"/list\", method=RequestMethod.GET)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map list" + className + "(HttpServletRequest request, HttpServletResponse response) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("List list = " + objectName + "Service.list" + className + "();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"result\", list);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("@RequestMapping(value=\"/paging\", method=RequestMethod.GET)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map page" + className + "(HttpServletRequest request, HttpServletResponse response) {");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("int page = 1;");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("int number = 25;");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("if(StringUtils.isNotEmpty(request.getParameter(\"n\"))) {");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("number = Integer.parseInt(request.getParameter(\"n\"));");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("if(StringUtils.isNotEmpty(request.getParameter(\"p\"))) {");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("page = Integer.parseInt(request.getParameter(\"p\"));");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("List list = " + objectName + "Service.paging" + className + "(page, number);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"result\", list);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\n");
		sb.append("}");
		
		System.out.println(sb.toString());
		
		
		try {
			File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
			fileDirect.mkdirs();
			File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator  + className + "Controller.java");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 生成Controller 代码
	 * @param tableName
	 */
	public void genControllerRESTStyle(String tableName) {
		String className = nameCovert(tableName, true);
		String objectName = nameCovert(tableName, false);
		StringBuilder sb = new StringBuilder();
		String packagePath = BASE_PACKAGE + ".web" + BASE_PACKAGE_SUFFIX;
		sb.append("package " + packagePath + ";");
		sb.append("\n");
		sb.append("\n");
		sb.append("import java.util.List;");
		sb.append("\n");
		sb.append("import java.util.ArrayList;");
		sb.append("\n");
		sb.append("import java.util.Map;");
		sb.append("\n");
		sb.append("import java.util.HashMap;");
		sb.append("\n");
		sb.append("\n");
		sb.append("import javax.servlet.http.HttpServletRequest;");
		sb.append("\n");
		sb.append("import javax.servlet.http.HttpServletResponse;");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("import org.apache.commons.lang.StringUtils;");
		sb.append("\n");
		sb.append("import org.springframework.beans.factory.annotation.Autowired;");
		sb.append("\n");
		sb.append("import org.springframework.stereotype.Controller;");
		sb.append("\n");
		sb.append("import org.springframework.web.bind.annotation.ModelAttribute;");
		sb.append("\n");
		sb.append("import org.springframework.web.bind.annotation.PathVariable;");
		sb.append("\n");
		sb.append("import org.springframework.web.bind.annotation.RequestMapping;");
		sb.append("\n");
		sb.append("import org.springframework.web.bind.annotation.RequestMethod;");
		sb.append("\n");
		sb.append("import org.springframework.web.bind.annotation.ResponseBody;");
		sb.append("\n");
		sb.append("import org.springframework.web.servlet.ModelAndView;");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("import " + BASE_PACKAGE + ".pojo" + BASE_PACKAGE_SUFFIX + "." + className + ";");
		sb.append("\n");
		sb.append("import " + BASE_PACKAGE + ".service" + BASE_PACKAGE_SUFFIX + "." + className + "Service;");
		sb.append("\n");
		sb.append("\n");
		
		
		sb.append("@Controller");
		sb.append("\n");
		sb.append("@RequestMapping(\"/" + className.toLowerCase() + "\")");
		sb.append("\n");
		sb.append("public class " + className + "Controller {");
		sb.append("\n");
		sb.append("\n");
		sb.append("\t");
		sb.append("@Autowired");
		sb.append("\n");
		sb.append("\t");
		sb.append("private " + className + "Service " + objectName + "Service;");
		sb.append("\n");
		sb.append("\n");		
		sb.append("\t");
	
			
		sb.append("@RequestMapping(method=RequestMethod.POST)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map insert" + className + "(HttpServletRequest request, HttpServletResponse response, @ModelAttribute " + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", false);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (" + objectName + "Service.insert" + className + "(" + objectName + ") > 0) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", true);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("@RequestMapping(method=RequestMethod.PUT)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map update" + className + "(HttpServletRequest request, HttpServletResponse response, @ModelAttribute " + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", false);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (" + objectName + "Service.update" + className + "(" + objectName + ") > 0) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", true);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");

		
		sb.append("\t");
		sb.append("@RequestMapping(method=RequestMethod.DELETE)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map remove" + className + "(HttpServletRequest request, HttpServletResponse response, @ModelAttribute " + className + " " + objectName + ") {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", false);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (" + objectName + "Service.remove" + className + "(" + objectName + ") > 0) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", true);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("@RequestMapping(value=\"/{id}\", method=RequestMethod.DELETE)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map remove" + className + "(HttpServletRequest request, HttpServletResponse response, @PathVariable Integer id) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", false);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("if (" + objectName + "Service.remove" + className + "ById(id) > 0) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"success\", true);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\t");
		sb.append("@RequestMapping(value=\"/{id}\", method=RequestMethod.GET)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map get" + className + "(HttpServletRequest request, HttpServletResponse response, @PathVariable Integer id) {");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append(className + " obj = " + objectName + "Service.get" + className + "(id);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"result\", obj);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
				
		sb.append("\t");
		sb.append("@RequestMapping(method=RequestMethod.GET)");
		sb.append("\n");
		sb.append("\t");
		sb.append("@ResponseBody");
		sb.append("\n");
		sb.append("\t");
		sb.append("public Map paging" + className + "(HttpServletRequest request, HttpServletResponse response) {");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("Map<String, Object> map = new HashMap<String, Object>();");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append(className + " queryObj = new " + className + "();");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("if(StringUtils.isNotEmpty(request.getParameter(\"n\"))) {");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("int number = Integer.parseInt(request.getParameter(\"n\"));");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("queryObj.setNumber(number);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("if(StringUtils.isNotEmpty(request.getParameter(\"p\"))) {");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("int page = Integer.parseInt(request.getParameter(\"p\"));");
		sb.append("\n");
		sb.append("\t");sb.append("\t");sb.append("\t");
		sb.append("queryObj.setCurrentPage(page);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("List list = " + objectName + "Service.paging" + className + "(queryObj);");
		sb.append("\n");
		sb.append("\t");
		sb.append("\t");
		sb.append("map.put(\"result\", list);");
		sb.append("\n");
		sb.append("\t");sb.append("\t");
		sb.append("return map;");
		sb.append("\n");
		sb.append("\t");
		sb.append("}");
		sb.append("\n");
		sb.append("\n");
		
		sb.append("\n");
		sb.append("}");
		
		System.out.println(sb.toString());
		
		
		try {
			File fileDirect = new File(this.basePath + File.separator + packagePath.replace(".", File.separator));
			fileDirect.mkdirs();
			File file = new File(this.basePath + File.separator + packagePath.replace(".", File.separator) + File.separator  + className + "Controller.java");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void genScripts(String tableName) {
		String objectName = nameCovert(tableName, false);
		StringBuffer sb = new StringBuffer();
		sb.append("var REQUEST_URI = '/" + objectName.toLowerCase() + "';");
		sb.append("\n");
		sb.append("$(function(){");
		sb.append("\n");
		sb.append("\tif($('#result_list').length>0) {");
		sb.append("\n");
		sb.append("\t\tqueryData(null);");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("});");
		sb.append("\n");
		sb.append("// Insert ");
		sb.append("\n");
		sb.append("function insertData(data) {");
		sb.append("\n");
		sb.append("\t$.ajax({");
		sb.append("\n");
		sb.append("\t\turl: REQUEST_URI,");
		sb.append("\n");
		sb.append("\t\ttype: 'POST',");
		sb.append("\n");
		sb.append("\t\tdataType: 'json',");
		sb.append("\n");
		sb.append("\t\tdata: data,");
		sb.append("\n");
		sb.append("\t\tsuccess: function(result) {");
		sb.append("\n");
		sb.append("\t\t\tconsole.log(result);");
		sb.append("\n");
		sb.append("\t\t\tif(result.success) {");
		sb.append("\n");
		sb.append("\t\t\t\tconsole.log('insert successed.');");
		sb.append("\n");
		sb.append("\t\t\t}");
		sb.append("\n");
		sb.append("\t\t},");
		sb.append("\n");
		sb.append("\t\terror: function(a, b, c) {");
		sb.append("\n");
		sb.append("\t\t\tconsole.log('error:' + a + ',' + b);");
		sb.append("\n");
		sb.append("\t\t}");
		sb.append("\n");
		sb.append("\t})");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("//Delete ");
		sb.append("\n");
		sb.append("function deleteData(id) {");
		sb.append("\n");
		sb.append("\t$.ajax({");
		sb.append("\n");
		sb.append("\t\turl: REQUEST_URI + '/' + id,");
		sb.append("\n");
		sb.append("\t\ttype: 'DELETE',");
		sb.append("\n");
		sb.append("\t\tdataType: 'json',");
		sb.append("\n");
		sb.append("\t\tsuccess: function(result) {");
		sb.append("\n");
		sb.append("\t\t\tconsole.log(result);");
		sb.append("\n");
		sb.append("\t\t\tif(result.success) {");
		sb.append("\n");
		sb.append("\t\t\t\tconsole.log('delete successed.');");
		sb.append("\n");
		sb.append("\t\t\t}");
		sb.append("\n");
		sb.append("\t\t},");
		sb.append("\n");
		sb.append("\t\terror: function(a, b, c) {");
		sb.append("\n");
		sb.append("\t\t\tconsole.log('error:' + a + ',' + b);");
		sb.append("\n");
		sb.append("\t\t}");
		sb.append("\n");
		sb.append("\t})");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("//Update ");
		sb.append("\n");
		sb.append("function updateData(data) {");
		sb.append("\n");
		sb.append("data._method = 'PUT';");
		sb.append("\n");
		sb.append("\t$.ajax({");
		sb.append("\n");
		sb.append("\t\turl: REQUEST_URI,");
		sb.append("\n");
		sb.append("\t\ttype: 'POST',");
		sb.append("\n");
		sb.append("\t\tdataType: 'json',");
		sb.append("\n");
		sb.append("\t\tdata: data,");
		sb.append("\n");
		sb.append("\t\tsuccess: function(result) {");
		sb.append("\n");
		sb.append("\t\t\tconsole.log(result);");
		sb.append("\n");
		sb.append("\t\t\tif(result.success) {");
		sb.append("\n");
		sb.append("\t\t\t\tconsole.log('update successed.');");
		sb.append("\n");
		sb.append("\t\t\t}");
		sb.append("\n");
		sb.append("\t\t},");
		sb.append("\n");
		sb.append("\t\terror: function(a, b, c) {");
		sb.append("\n");
		sb.append("\t\t\tconsole.log('error:' + a + ',' + b);");
		sb.append("\n");
		sb.append("\t\t}");
		sb.append("\n");
		sb.append("\t})");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("// Query data");
		sb.append("\n");
		sb.append("function queryData(page) {");
		sb.append("\n");
		sb.append("\tvar data = {};");
		sb.append("\n");
		sb.append("\tif(page) {");
		sb.append("\n");
		sb.append("\t\tdata.page=page;");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("\t$.ajax({");
		sb.append("\n");
		sb.append("\t\turl: REQUEST_URI,");
		sb.append("\n");
		sb.append("\t\ttype: 'GET',");
		sb.append("\n");
		sb.append("\t\tdataType: 'json',");
		sb.append("\n");
		sb.append("\t\tdata: data,");
		sb.append("\n");
		sb.append("\t\tsuccess: function(result) {");
		sb.append("\n");
		sb.append("\t\t\tconsole.log(result);");
		sb.append("\n");
		sb.append("\t\t\t$('#result_list').html('');");
		sb.append("\n");
		sb.append("\t\t\t$('#result_paging').html(rtPageInfo(result.page));");
		sb.append("\n");
		sb.append("\t\t\tif(result.list) {");
		sb.append("\n");
		sb.append("\t\t\t\tvar obj, _dataHtml;");
		sb.append("\n");
		sb.append("\t\t\t\tfor(var i=0; i<result.list.length; i++) {");
		sb.append("\n");
		sb.append("\t\t\t\t\tobj = result.list[i];");
		sb.append("\n");
		sb.append("\t\t\t\t\t_dataHtml = dataRender(dataHtml, obj);");
		sb.append("\n");
		sb.append("\t\t\t\t\t$('#result_list').append(_dataHtml);");
		sb.append("\n");
		sb.append("\t\t\t\t}");
		sb.append("\n");
		sb.append("\t\t\t}");
		sb.append("\n");
		sb.append("\t\t},");
		sb.append("\n");
		sb.append("\t\terror: function(a, b, c) {");
		sb.append("\n");
		sb.append("\t\t\tconsole.log('error:' + a + ',' + b);");
		sb.append("\n");
		sb.append("\t\t}");
		sb.append("\n");
		sb.append("\t})");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("function pagingQueryBinding() {");
		sb.append("\n");
		sb.append("\tif($('#result_paging #page_prev').length>0) {");
		sb.append("\n");
		sb.append("\t\t$('#result_paging #page_prev').click(function(){");
		sb.append("\n");
		sb.append("\t\t\tvar currentPage = parseInt($('#page_currentPage').text());");
		sb.append("\n");
		sb.append("\t\t\tqueryData(--currentPage);");
		sb.append("\n");
		sb.append("\t\t});");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("\tif($('#result_paging #page_next').length>0) {");
		sb.append("\n");
		sb.append("\t\t$('#result_paging #page_next').click(function(){");
		sb.append("\n");
		sb.append("\t\t\tvar currentPage = parseInt($('#page_currentPage').text());");
		sb.append("\n");
		sb.append("\t\t\tqueryData(++currentPage);");
		sb.append("\n");
		sb.append("\t\t});");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("\tif($('#result_paging .go_page').length>0) {");
		sb.append("\n");
		sb.append("\t\t$('#result_paging .go_page').click(function(){");
		sb.append("\n");
		sb.append("\t\t\tqueryData($(this).attr('page'));");
		sb.append("\n");
		sb.append("\t\t});");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("function rtPageInfo(paging){");
		sb.append("\n");
		sb.append("\tvar totalPage = paging.totalPage;");
		sb.append("\n");
		sb.append("\tvar currentPage = paging.currentPage;");
		sb.append("\n");
		sb.append("\tvar totalCount = paging.totalCount;");
		sb.append("\n");
		sb.append("\t");
		sb.append("\n");
		sb.append("\tvar html = '';");
		sb.append("\n");
		sb.append("\tif(currentPage>1)");
		sb.append("\n");
		sb.append("\t\thtml += '<a href=\"javascript:void(0)\" id=\"page_prev\" class=\"pre\">上一页</a>';");
		sb.append("\n");
		sb.append("\tif(currentPage-2>1) {");
		sb.append("\n");
		sb.append("\t\thtml += '<a href=\"javascript:void(0)\" page=\"1\">1</a>';");
		sb.append("\n");
		sb.append("\t\tif(currentPage>4)");
		sb.append("\n");
		sb.append("\t\t\thtml += '<a href=\"javascript:void(0)\">...</a>';");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("\tfor(var i=0, p=2; i<5; i++, p--) {");
		sb.append("\n");
		sb.append("\t\tif(currentPage-p>0) {");
		sb.append("\n");
		sb.append("\t\t\tif(p==0) {");
		sb.append("\n");
		sb.append("\t\t\t\thtml += '<a href=\"javascript:void(0)\" id=\"page_currentPage\" class=\"selected\">'+currentPage+'</a>';");
		sb.append("\n");
		sb.append("\t\t\t} else {");
		sb.append("\n");
		sb.append("\t\t\t\tif(currentPage-p<=totalPage)");
		sb.append("\n");
		sb.append("\t\t\t\t\thtml += '<a href=\"javascript:void(0)\" page=\"'+(currentPage-p)+'\">'+(currentPage-p)+'</a>';");
		sb.append("\n");
		sb.append("\t\t\t}");
		sb.append("\n");
		sb.append("\t\t}");
		sb.append("\n");
		sb.append("\t}\t\t\t\t");
		sb.append("\n");
		sb.append("\tif(totalPage-2>currentPage) {");
		sb.append("\n");
		sb.append("\t\tif(totalPage-currentPage>3)");
		sb.append("\n");
		sb.append("\t\t\thtml += '<a href=\"javascript:void(0)\">...</a>';");
		sb.append("\n");
		sb.append("\t\thtml += '<a href=\"javascript:void(0)\" page=\"'+totalPage+'\">'+totalPage+'</a>';");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("\tif(totalPage > 5) {");
		sb.append("\n");
		sb.append("//\t\t\thtml += '<input type=\"text\" id=\"gotoPage\" class=\"\" placeholder=\"页数\">';");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("\t");
		sb.append("\n");
		sb.append("\tif(currentPage<totalPage)");
		sb.append("\n");
		sb.append("\t\thtml += '<a href=\"javascript:void(0)\" id=\"page_next\" class=\"next\">下一页 </a>';");
		sb.append("\n");
		sb.append("\t\t");
		sb.append("\n");
		sb.append("//\t\thtml += '<li class=\"page-navi-meta\">('+((currentPage-1)*number+1)+'-'+((currentPage*number)>totalCount?totalCount:(currentPage*number))+'/<font id=\"page_totalCount\" totalPage=\"'+totalPage+'\">'+totalCount+'</font>)</li>';");
		sb.append("\n");
		sb.append("//\t\thtml += '<li class=\"page-navi-action\" id=\"page_rows\">每页显示：<a number=\"25\">25</a></li>';");
		sb.append("\n");
		sb.append("\t\t");
		sb.append("\n");
		sb.append("\treturn html;");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("function dataRender(dataHtml, obj) {");
		sb.append("\n");
		sb.append("\tvar _dataHtml = dataHtml;");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${ID}', obj.id);");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${NAME}', obj.nickname?obj.nickname:obj.fid);");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${GENDER}', obj.sex==='1'?'male':'female');");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${LOCATION}', obj.province?obj.province + ' ' + obj.city:'');");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${GROUPING}', obj.grouping);");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${TAG}', obj.tags);");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${SUBTIME}', dateConveter(obj.subTime, 3));");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${WEIBO}', obj.weibo);");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${EMAIL}', obj.email);");
		sb.append("\n");
		sb.append("\t_dataHtml = replaceAll(_dataHtml, '${FANSID}', obj.id);\t");
		sb.append("\n");
		sb.append("\treturn _dataHtml;");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("var dataHtml = ['<tr>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'<td><span class=\"showdetail\" cid=\"${ID}\">${NAME}</span></td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'<td><span class=\"icons ${GENDER}\"></span></td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'<td>${LOCATION}</td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'<td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t\t'<span class=\"cus_groups\">${GROUPING}</span>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t\t'<span class=\"edit_group\" fansid=\"${FANSID}\">',");
		sb.append("\n");
		sb.append("\t\t\t\t\t\t\t'<i class=\"fa fa-pencil\"></i>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t\t'</span>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'</td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'<td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t\t'<span class=\"cus_tags\">${TAG}</span>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t\t'<span class=\"edit_tag\" fansid=\"${FANSID}\">',");
		sb.append("\n");
		sb.append("\t\t\t\t\t\t\t'<i class=\"fa fa-pencil\"></i>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t\t'</span>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'</td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'<td>${SUBTIME}</td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'<td>${WEIBO}</td>',");
		sb.append("\n");
		sb.append("\t\t\t\t\t'<td>${EMAIL}</td>',");
		sb.append("\n");
		sb.append("\t\t\t\t'</tr>'].join('');");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("function replaceAll(html, key, data, escape) {");
		sb.append("\n");
		
//		sb.append("\tvar pattern = new RegExp(key, 'ig');");
//		sb.append("\n");
//		sb.append("\treturn html.replace(pattern, data);");
		sb.append("\twhile(html.indexOf(key)>-1) {");
		sb.append("\n");
		sb.append("\t\thtml = html.replace(key, data?data:'');");
		sb.append("\n");
		sb.append("\t} ");
		sb.append("\n");
		sb.append("\tif(escape) {");
		sb.append("\n");
		sb.append("\t\t    var __escapehtml = {");
		sb.append("\n");
		sb.append("\t\t\t           escapehash: {");
		sb.append("\n");
		sb.append("\t\t\t\t                '<': '&lt;',");
		sb.append("\n");
		sb.append("\t\t\t\t                '>': '&gt;',");
		sb.append("\n");
		sb.append("\t\t\t\t                '&': '&amp;',");
		sb.append("\n");
		sb.append("\t\t\t\t                '\"': '&quot;',");
		sb.append("\n");
		sb.append("\t\t\t\t                \"'\": '&#x27;',");
		sb.append("\n");
		sb.append("\t\t\t\t                '/': '&#x2f;'");
		sb.append("\n");
		sb.append("\t\t\t            },");
		sb.append("\n");
		sb.append("\t\t            escapereplace: function(k) {");
		sb.append("\n");
		sb.append("\t\t\t                return __escapehtml.escapehash[k];");
		sb.append("\n");
		sb.append("\t\t            },");
		sb.append("\n");
		sb.append("\t\t\t            escaping: function(str) {");
		sb.append("\n");
		sb.append("\t\t\t               return typeof(str) !== 'string' ? str : str.replace(/[&<>\"]/igm, this.escapereplace);");
		sb.append("\n");
		sb.append("\t\t\t           }");
		sb.append("\n");
		sb.append("\t\t       };");
		sb.append("\n");
		sb.append("\t\t	return __escapehtml.escaping(html);");
		sb.append("\n");
		sb.append("\t} else ");
		sb.append("\n");
		sb.append("\t\t	return html;");
//		sb.append("\treturn html;");
		sb.append("\n");
		sb.append("}");
		
		System.out.println(sb.toString());
		
		
		try {
			File fileDirect = new File(this.basePath + File.separator + "res/js".replace("/", File.separator));
			fileDirect.mkdirs();
			File file = new File(this.basePath + File.separator + "res/js".replace("/", File.separator) + File.separator  + objectName + ".js");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void genAngularScripts(String tableName, List<TableDesc> list) {
		String objectName = nameCovert(tableName, false);
		StringBuffer sb = new StringBuffer();
		sb.append("/**");
		sb.append("\n");
		sb.append("<!-- template -->");
		sb.append("\n");
		sb.append("<div ng-app=\"myApp\" ng-controller=\"ObjCtrl\">");
		sb.append("\n");
		sb.append("	<!-- insert  -->");
		sb.append("\n");
		sb.append("	<div>");
		sb.append("\n");
		sb.append("\t{{ save_tip }}");
		sb.append("\n");
		
		for(int i=0; i<list.size(); i++) {
			sb.append("\t<input type=\"text\" ng-model=\"obj." + nameCovert(list.get(i).getField(), false) + "\" placeholder=\"" + nameCovert(list.get(i).getField(), false) + "\" />");
			sb.append("\n");
		}

		sb.append("\t<input type=\"button\" ng-disabled=\"saveBtn\" ng-click=\"saveObj()\" value=\"Submit\" />");
		sb.append("\n");
		sb.append("	</div>");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("	<!-- update  -->");
		sb.append("\n");
		sb.append("	<div>");
		sb.append("\n");
		sb.append("\t{{ update_tip }}");
		sb.append("\n");
		
		for(int i=0; i<list.size(); i++) {
			sb.append("\t<input type=\"text\" ng-model=\"obj." + nameCovert(list.get(i).getField(), false) + "\" placeholder=\"" + nameCovert(list.get(i).getField(), false) + "\" />");
			sb.append("\n");
		}
		
		sb.append("\t<input type=\"button\" ng-disabled=\"updateBtn\" ng-click=\"updateObj()\" value=\"Submit\" />");
		sb.append("\n");
		sb.append("	</div>");
		sb.append("\n");
		sb.append("	<!-- delete  -->");
		sb.append("\n");
		sb.append("	<!-- query  -->");
		sb.append("\n");
		sb.append("	{{ delete_tip }}");
		sb.append("\n");
		sb.append("	{{ query_tip }}");
		sb.append("\n");
		sb.append("	<div ng-repeat=\"item in objList\">");
		sb.append("\n");
		
		for(int i=0; i<list.size(); i++) {
			if(i==0) {
				sb.append("\t<div ng-click=\"deleteObj(item.id)\">{{ item." + nameCovert(list.get(i).getField(), false) + " }}</div>");
			} else {
				sb.append("\t<div>{{ item." + nameCovert(list.get(i).getField(), false) + " }}</div>");
			}
			sb.append("\n");
		}
		
		sb.append("	</div>");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("	<!-- get  -->");
		sb.append("\n");
		
		for(int i=0; i<list.size(); i++) {
			if(i==0) {
				sb.append("	<div id=\"{{obj.id}}\">{{ obj." + nameCovert(list.get(i).getField(), false) + " }}</div>");
			} else {
				sb.append("\t<div>{{ obj." + nameCovert(list.get(i).getField(), false) + " }}</div>");
			}
			sb.append("\n");
		}
		
		sb.append("	</div>");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("</div>");
		sb.append("\n");
		sb.append("*/");
		sb.append("\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("var REQUEST_URI = '/" + objectName.toLowerCase() + "';");
		sb.append("\n");
		sb.append("function ObjCtrl($scope, $http) {");
		sb.append("\n");
		sb.append("    $scope.obj = {");
		sb.append("\n");
		
		for(int i=0; i<list.size(); i++) {
			sb.append("        " + nameCovert(list.get(i).getField(), false) + ": \"" + nameCovert(list.get(i).getField(), false) + "\",");
			sb.append("\n");
		}

		sb.append("    };");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("// insert      ");
		sb.append("\n");
		sb.append("    $scope.saveBtn = false;");
		sb.append("\n");
		sb.append("    $scope.saveObj = function() {");
		sb.append("\n");
		sb.append("    	$scope.saveBtn = true;");
		sb.append("\n");
		sb.append("    	var postParams = $scope.obj;");
		sb.append("\n");
		sb.append("        $http.post(REQUEST_URI, {}, {params : postParams})");
		sb.append("\n");
		sb.append("	        .success(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("	        	if(data.success) {");
		sb.append("\n");
		sb.append("	        \t$scope.save_tip = 'success!';");
		sb.append("\n");
		sb.append("	        	} else {");
		sb.append("\n");
		sb.append("	        \t$scope.save_tip = 'fail!';");
		sb.append("\n");
		sb.append("	        \t$scope.saveBtn = false;");
		sb.append("\n");
		sb.append("	        	}");
		sb.append("\n");
		sb.append("\t	})");
		sb.append("\n");
		sb.append("\t	.error(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("\t\t$scope.save_tip = 'error!';");
		sb.append("\n");
		sb.append("\t\t$scope.saveBtn = false;");
		sb.append("\n");
		sb.append("\t	});\t");
		sb.append("\n");
		sb.append("    };");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("// update ");
		sb.append("\n");
		sb.append("	$scope.updateBtn = false;");
		sb.append("\n");
		sb.append("    $scope.updateObj = function() {");
		sb.append("\n");
		sb.append("    	$scope.updateBtn = true;");
		sb.append("\n");
		sb.append("    	var putParams = $scope.obj;");
		sb.append("\n");
		sb.append("        $http.put(REQUEST_URI, {}, {params : putParams})");
		sb.append("\n");
		sb.append("	        .success(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("	        	if(data.success) {");
		sb.append("\n");
		sb.append("	        \t$scope.update_tip = 'success!';");
		sb.append("\n");
		sb.append("	        	} else {");
		sb.append("\n");
		sb.append("	        \t$scope.update_tip = 'fail!';");
		sb.append("\n");
		sb.append("	        \t$scope.updateBtn = false;");
		sb.append("\n");
		sb.append("	        	}");
		sb.append("\n");
		sb.append("\t	})");
		sb.append("\n");
		sb.append("\t	.error(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("\t\t$scope.update_tip = 'error!';");
		sb.append("\n");
		sb.append("\t\t$scope.updateBtn = false;");
		sb.append("\n");
		sb.append("\t	});	");
		sb.append("\n");
		sb.append("    };");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("// delete ");
		sb.append("\n");
		sb.append("    $scope.deleteObj = function(objId) {");
		sb.append("\n");
		sb.append("    	$http.delete(REQUEST_URI + '/' + objId)");
		sb.append("\n");
		sb.append("    \t.success(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("	        	if(data.success) {");
		sb.append("\n");
		sb.append("	        \t$scope.delete_tip = 'success!';");
		sb.append("\n");
		sb.append("	        	} else {");
		sb.append("\n");
		sb.append("	        \t$scope.delete_tip = 'fail!';");
		sb.append("\n");
		sb.append("	        	}");
		sb.append("\n");
		sb.append("\t	})");
		sb.append("\n");
		sb.append("\t	.error(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("\t\t$scope.delete_tip = 'error!';");
		sb.append("\n");
		sb.append("\t	});	");
		sb.append("\n");
		sb.append("    }");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("// query  ");
		sb.append("\n");
		sb.append("	$scope.queryObj = function(page) {");
		sb.append("\n");
		sb.append("\tvar params = {};");
		sb.append("\n");
		sb.append("\tif(page) {");
		sb.append("\n");
		sb.append("\t	params.page = page;");
		sb.append("\n");
		sb.append("\t}");
		sb.append("\n");
		sb.append("    	$http.get(REQUEST_URI, {params : params})");
		sb.append("\n");
		sb.append("    \t.success(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("	        	if(data.success) {");
		sb.append("\n");
		sb.append("	        \t$scope.objList = data.list;");
		sb.append("\n");
		sb.append("	        \t$scope.query_tip = 'done!';");
		sb.append("\n");
		sb.append("	        	} else {");
		sb.append("\n");
		sb.append("	        \t$scope.query_tip = 'error!';");
		sb.append("\n");
		sb.append("	        	}");
		sb.append("\n");
		sb.append("\t	})");
		sb.append("\n");
		sb.append("\t	.error(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("\t\t$scope.query_tip = 'error!';");
		sb.append("\n");
		sb.append("\t	});	");
		sb.append("\n");
		sb.append("    }");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("    $scope.getObj = function(objId) {");
		sb.append("\n");
		sb.append("    	$http.get(REQUEST_URI + '/' + objId, {})");
		sb.append("\n");
		sb.append("    \t.success(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("	        	if(data.success) {");
		sb.append("\n");
		sb.append("	        \t$scope.obj = data.obj;");
		sb.append("\n");
		sb.append("	        	}");
		sb.append("\n");
		sb.append("\t	})");
		sb.append("\n");
		sb.append("\t	.error(function(data, status, headers, config) { ");
		sb.append("\n");
		sb.append("\t\tconsole.log(data);");
		sb.append("\n");
		sb.append("\t	});	");
		sb.append("\n");
		sb.append("    }");
		sb.append("\n");
		sb.append("");
		sb.append("\n");
		sb.append("    // query page 1");
		sb.append("\n");
		sb.append("    $scope.queryObj(1);");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");
		
		System.out.println(sb.toString());
		
		
		try {
			File fileDirect = new File(this.basePath + File.separator + "res/angularjs".replace("/", File.separator));
			fileDirect.mkdirs();
			File file = new File(this.basePath + File.separator + "res/angularjs".replace("/", File.separator) + File.separator  + objectName + ".js");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 读取数据库中的所有表名
	 */
	public List<String> readTables() {
		String sql = "show tables";
		List<String> list = dao.showTables(sql);
		return list;
	}
	
	/**
	 * 根据某个表生成pojo对象及curd逻辑代码
	 * @param table
	 */
	public void readTable(String table) {
		List<TableDesc> list = dao.descTable(table);
		genPojoObj(table, list);
		genDaoObj(table);
		genDaoObjXml(table, list);
		genService(table);
//		genController(table);
		genControllerRESTStyle(table);
		genScripts(table);
		genAngularScripts(table, list);
	}
	
	/**
	 * 类或字段名转换
	 * @param name
	 * @param firstUpper
	 * @return
	 */
	private String nameCovert(String name, boolean firstUpper) {
		String[] classNamePath = name.split("_");
		StringBuffer className = new StringBuffer();
		int firstLetter = 0;
		for(int i=0; i<classNamePath.length; i++) {
			firstLetter = Integer.valueOf(classNamePath[i].charAt(0));
			if(firstLetter >= 97 && firstLetter <= 122) {
				char c;
				if(i==0) {
					if(firstUpper) {
						c = (char) (firstLetter-32);
					} else {
						c = (char) firstLetter;
					}
				} else {
					c = (char) (firstLetter-32);
				}
				className.append(String.valueOf(c) + classNamePath[i].substring(1));
			} else {
				className.append(classNamePath[i]);
			}
		}
		return className.toString();
	}
	
	/**
	 * 类型转换 
	 * @param type
	 * @return
	 */
	private String typeContvert(String type) {
		if(type.startsWith("int") || type.startsWith("tinyint") || type.startsWith("smallint") || type.startsWith("bit") || type.startsWith("mediumint")) {
			return "Integer";
		} else if(type.startsWith("bigint")) {
			return "Long";
		} else if(type.startsWith("float")) {
			return "Float";
		} else if(type.startsWith("double")) {
			return "Double";
		} else if(type.startsWith("char") || type.startsWith("varchar") || type.startsWith("text")) {
			return "String";
		} else if(type.startsWith("datetime") || type.startsWith("timestamp")) {
			return "Timestamp";
		}
		return type;
	}
	
	// 取得程序根目录
    private String getBasePath() {
        String basePath = CodeGen.class.getResource("") + "";//
        if (basePath.startsWith("file:")) {
            if (basePath.charAt(7) == ':') { // Windows系统路径
            	basePath = basePath.substring(6);
            } else { // Unix系统路径
            	basePath = basePath.substring(5);
            }
        }
        basePath = basePath.substring(0, basePath.indexOf("uniutil")+7);
        basePath = basePath + File.separator +  "target" + File.separator + "autoGen";
        System.out.println(basePath);
        File file = new File(basePath);
        file.mkdirs();
        return basePath;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CodeGen obj = new CodeGen();
		List<String> list = obj.readTables();
		System.out.println("Please choose which table need to gen the code, number or table_name is allowable.");
		for(int i=0; i<list.size(); i++) {
			System.out.println("Table_id: " + i + "\tTable_name: " + list.get(i));
		}
		Scanner input = new Scanner(System.in);
		
	    while(true) {
	    	System.out.println("Please Enter the talbe name or table id, type \"ALL\" will gen all the tables above.");             
		    String command = input.nextLine();
		    
		    if("all".equals(command.toLowerCase())) {
		    	for(int i=0; i<list.size(); i++) {
					obj.readTable(list.get(i));
				}
		    	break;
		    } else {
		    	try {
		    		Integer num = Integer.parseInt(command);
		    		if(command.equals(num.toString())) {
		    			obj.readTable(list.get(num));
		    			break;
		    		} else {
		    			obj.readTable(command);
		    			break;
		    		}
		    	} catch (Exception e) {
		    		obj.readTable(command);
		    		break;
		    	}
		    }
//		    System.out.println("Input error... Please try it again.");
	    }
	    
	}

}
