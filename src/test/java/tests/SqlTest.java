package tests;

import java.io.InputStream;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.ee.jdbc.sqlquery.builder.xml.XmlSqlSetBuilder;
import architecture.ee.jdbc.sqlquery.factory.SqlQueryFactory;
import architecture.ee.jdbc.sqlquery.factory.SqlQueryFactoryBuilder;
import architecture.ee.jdbc.sqlquery.mapping.MapperSource;
import architecture.ee.service.Repository;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations={"classpath:application-context2.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SqlTest {

private static Logger log = LoggerFactory.getLogger(RepositoryTest.class);
	
	@Autowired
    private Repository repository;
	
	public SqlTest() {
		
	}
	
	@Test
    public void testXmlSqlBuilder() {
		
		SqlQueryFactory factory = SqlQueryFactoryBuilder.build();
		InputStream input = getClass().getClassLoader().getResourceAsStream("common-sqlset.xml");
		XmlSqlSetBuilder builder = new XmlSqlSetBuilder(input, factory.getConfiguration(), "common-sqlset.xml", null);
		builder.parse();
		 
		for (String name : factory.getConfiguration().getMapperNames()) {
			MapperSource source = factory.getConfiguration().getMapper(name);
		    log.debug("FACTORY: " +  source.toString()); 
		}
    }
    

}
