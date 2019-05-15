# architecture-ee

## 소개

자바기반의 엔터프라이즈 어플리케이션 개발 아키텍처

- 애플리케이션 관련 주요한 설정들을 XML과 데이터베이스를 사용하여 관리하는 지원.
- 데이터베이스 조작시 자바 코드와 쿼리 문을 XML 파일을 사용하여 분리하여 사용하는 것을 지원.


------
## Getting Started

5.1.x 버전 부터는 Java 8+ 지원

spring context 파일에 아래의 내용을 추가한다.

```xml
	
  <import resource="classpath:context/default-bootstrap-context.xml"/>	
	<import resource="classpath:context/default-components-context.xml"/>
	<import resource="classpath:context/default-transaction-context.xml"/>
	<import resource="classpath:context/default-freemarker-context.xml"/>
	<import resource="classpath:context/default-ehcache-context.xml"/>
  
JTA transaction 을 사용하는 경우 default-transaction-context.xml 을 주석처리하고 JTA 설정을 추가한다.
DataSource 설정을 위하여 WEB-INF/startup-config.xml 파일에 database 설정을 아래와 같이 설정한다.

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


## Opensource

* COMPILE

| Opensource | Version |
|------------|---------|
| spingframework | 4.3.2.RELEASE |
| commons-io | 2.5 |
| commons-codec | 1.10 |
| commons-dbcp2 | 1.4, 2.1 |
| commons-pool2 | 2.4.2 |
| commons-io | 2.5 |
| commons-logging | 1.2 |
| freemarker | 2.3.28 |
| dom4j | 2.1.1 |
| xml-apis | 1.0.b2 |
| guava | 27.1-jre |
| slf4j-api | 1.7.26 |
| slf4j-log4j12 | 1.7.26 |


* RUNTIME

| Opensource | Version |
|------------|---------|
|   ojdbc5 | 11.1.0 |
|   log4j | 1.2.14 |

* TEST

| Opensource | Version |
|------------|---------|
|  spring-test | 4.3.2.RELEASE |
|  junit | 4.12 |
|  hamcrest-core | 1.3 |



