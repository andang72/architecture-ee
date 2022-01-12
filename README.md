# architecture-ee

자바기반의 엔터프라이즈 어플리케이션 개발 아키텍처

- 주요한 설정들을 XML과 데이터베이스를 통하여 관리하는 기능 제공.
- SQL 문을 XML 파일을 사용하여 관리하고 데이터베이스 조작 코드에서 불러와 사용하는 것을 지원.


------
## Getting Started

- 5.1.x 버전 부터는 Java 8+ 지원
- 5.2.x 버전 부터는 Spring 5.2.x 지원
- 5.3.x 버전 부터는 Spring 5.3.x 지원

spring context (WEB-INF/context-config/webApplicationContext.xml) 파일에 아래의 내용을 추가하여 기본 모듈을 로드한다.

```xml
	
  <import resource="classpath:context/default-bootstrap-context.xml"/>	
  <import resource="classpath:context/default-components-context.xml"/>
  <import resource="classpath:context/default-transaction-context.xml"/>
  <import resource="classpath:context/default-freemarker-context.xml"/>
  <import resource="classpath:context/default-ehcache-context.xml"/>
 ``` 

* Using JTA Transaction    
JTA transaction 을 사용하는 경우 default-transaction-context.xml 을 주석처리하고 JTA 설정을 추가한다.

* DataSource Setting
DataSource 설정을 위하여 WEB-INF/startup-config.xml 파일에 database 설정을 아래와 같이 설정한다.

```xml
  <!-- database connection configuration -->
   <default>
      <pooledDataSourceProvider> 
          <driverClassName></driverClassName> 
          <url></url>
          <username></username>
          <password></password>
          <connectionProperties>
              <initialSize>1</initialSize>
              <maxActive>8</maxActive>
              <maxIdle>8</maxIdle>
              <maxWait>-1</maxWait>
              <minIdle>0</minIdle>
              <testOnBorrow>true</testOnBorrow>
              <testOnReturn>false</testOnReturn>
              <testWhileIdle>false</testWhileIdle>
              <validationQuery>select 1 from dual</validationQuery>
          </connectionProperties>
      </pooledDataSourceProvider>
    </default> 
  </database> 
 ``` 
  
------

## Dependencies

* COMPILE

| Name | Version |
|------------|---------|
| spingframework | 5.3.14 |
| spring security | 5.6.1 |
| commons-io | 2.11.0 |
| commons-codec | 1.15 |
| commons-dbcp2 | 1.4, 2.9.0 |
| commons-pool2 | 2.4.2 |
| commons-io | 2.5 |
| commons-logging | 1.2 |
| freemarker | 2.3.31 |
| dom4j | 2.1.3 |
| xml-apis | 1.0.b2 |
| guava | 31.0.1-jre |
| ehcache | 3.7.0 |
| slf4j-api | 1.7.32 |
| slf4j-log4j12 | 1.7.32 |
| sqlbuilder | 3.0.2 |


* RUNTIME

| Name | Version |
|------------|---------|
|   ojdbc5 | 11.1.0 |
|   log4j | 2.17.0 |

* TEST

| Name | Version |
|------------|---------|
|  spring-test | 5.3.14 |
|  junit | 4.12 |
|  hamcrest-core | 1.3 |
|  spotbugs | 4.5.0 |



