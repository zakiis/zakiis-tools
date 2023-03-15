package com.zakiis.mybatis.generator.util;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import com.mysql.cj.util.StringUtils;

public class MyCommentGenerator implements CommentGenerator {
	
	private final Properties properties = new Properties();

    private boolean suppressDate;

    private boolean suppressAllComments;

    private SimpleDateFormat dateFormat;

    public MyCommentGenerator() {
        super();
        suppressDate = false;
        suppressAllComments = false;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void addConfigurationProperties(Properties props) {
        this.properties.putAll(props);
        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));
        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
        String dateFormatString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
        if (StringUtility.stringHasValue(dateFormatString)) {
            dateFormat = new SimpleDateFormat(dateFormatString);
        }
    }

    /**
     * Returns a formated date string to include in the Javadoc tag and XML
     * comments. You may return null if you do not want the date in these
     * documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    protected String getDateString() {
        if (suppressDate) {
            return null;
        } else if (dateFormat != null) {
            return dateFormat.format(new Date());
        } else {
            return new Date().toString();
        }
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        if (!StringUtils.isNullOrEmpty(introspectedColumn.getRemarks())) {
        	field.addJavaDocLine("/** " + introspectedColumn.getRemarks() + " */"); //$NON-NLS-1$
        }
    }

	@Override
	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		topLevelClass.addJavaDocLine("/**"); //$NON-NLS-1$
		topLevelClass.addJavaDocLine(" * Table: " + introspectedTable.getFullyQualifiedTable());
		topLevelClass.addJavaDocLine(" * " + getDateString());
		topLevelClass.addJavaDocLine(" */"); //$NON-NLS-1$
	}

}
