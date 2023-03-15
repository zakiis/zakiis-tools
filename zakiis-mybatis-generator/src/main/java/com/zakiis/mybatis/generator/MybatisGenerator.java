package com.zakiis.mybatis.generator;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class MybatisGenerator {

	public static void main(String[] args) throws Exception {
		createDir();
		boolean overwrite = true;
		List<String> warnings = new ArrayList<String>();
		InputStream is = ClassLoader.getSystemResourceAsStream("generator.xml");
		ConfigurationParser configurationParser = new ConfigurationParser(warnings);
		Configuration config = configurationParser.parseConfiguration(is);
		DefaultShellCallback shellCallback = new DefaultShellCallback(overwrite);
		MyBatisGenerator generator = new MyBatisGenerator(config, shellCallback, warnings);
		generator.generate(null);
		System.out.println(warnings.size());
		warnings.forEach(System.out::println);
	}

	private static void createDir() {
		File file1 = new File("target/java");
		if (!file1.exists()) {
			file1.mkdir();
		}
		
		File file2 = new File("target/mapper");
		if (!file2.exists()) {
			file2.mkdir();
		}
	}
}
