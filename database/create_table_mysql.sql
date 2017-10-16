		-- 1. 프로퍼티 테이블 생성 스크립트
		drop table V2_LOCALIZED_PROPERTY ; -- cascade constraints PURGE ;		
		drop table V2_PROPERTY ; --cascade constraints PURGE ;		
		drop table V2_SEQUENCER ; --cascade constraints PURGE ;		
		drop table V2_I18N_TEXT ; --cascade constraints PURGE ;
				
				
		CREATE TABLE V2_LOCALIZED_PROPERTY (
				  LOCALE_CODE            VARCHAR(100)   NOT NULL,
				  PROPERTY_NAME          VARCHAR(100)   NOT NULL,
				  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL,
				  CONSTRAINT V2_LOCALIZED_PROPERTY_PK PRIMARY KEY (LOCALE_CODE, PROPERTY_NAME)
		);
		
		
		ALTER TABLE `V2_LOCALIZED_PROPERTY`  COMMENT '애플리케이션 전역에서 사용되는 로케일 기반 프로퍼티 정보';
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `V2_LOCALIZED_PROPERTY`.`LOCALE_CODE` IS '로케일 코드 값'; */ 
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `V2_LOCALIZED_PROPERTY`.`PROPERTY_NAME` IS '프로퍼디 이름'; */ 
        /* Moved to CREATE TABLE
		COMMENT ON COLUMN `V2_LOCALIZED_PROPERTY`.`PROPERTY_VALUE` IS '프로퍼티 값'; */
        
		
		CREATE TABLE V2_PROPERTY (
				  PROPERTY_NAME          VARCHAR(100)   NOT NULL,
				  PROPERTY_VALUE         VARCHAR(1024)  NOT NULL,
				  CONSTRAINT V2_PROPERTY_PK PRIMARY KEY (PROPERTY_NAME)
		); 		
        
		ALTER TABLE `V2_PROPERTY`  COMMENT '애플리케이션 전역에서 사용되는 프로퍼티 정보';
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `V2_PROPERTY`.`PROPERTY_NAME` IS '프로퍼디 이름'; */ 
        /* Moved to CREATE TABLE
		COMMENT ON COLUMN `V2_PROPERTY`.`PROPERTY_VALUE` IS '프로퍼티 값'; */
        
        -- 2. 유니크 아이디 생성 테이블 생성 스크립트
		CREATE TABLE V2_SEQUENCER (
		    SEQUENCER_ID           INTEGER NOT NULL,
		    NAME                   VARCHAR(200) NOT NULL,
		    VALUE                  INTEGER NOT NULL,
		    CONSTRAINT V2_SEQUENCER_PK PRIMARY KEY (SEQUENCER_ID)
		); 			
		
		CREATE UNIQUE INDEX V2_SEQUENCER_NAME_IDX ON V2_SEQUENCER (NAME);		
        
		ALTER TABLE `V2_SEQUENCER`  COMMENT '애플리케이션 전역에서 사용되는 시퀀서 정보';
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `V2_SEQUENCER`.`SEQUENCER_ID` IS '시퀀서 ID'; */ 
        /* Moved to CREATE TABLE
		COMMENT ON COLUMN `V2_SEQUENCER`.`NAME` IS '시퀀서 이름'; */		
		/* Moved to CREATE TABLE
		COMMENT ON COLUMN `V2_SEQUENCER`.`VALUE` IS '시퀀서 값'; */		
		
		
		