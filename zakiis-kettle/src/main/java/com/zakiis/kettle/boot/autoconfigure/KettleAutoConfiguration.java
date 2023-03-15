package com.zakiis.kettle.boot.autoconfigure;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KettleDatabaseProperties.class, KettleRepositoryProperties.class})
public class KettleAutoConfiguration {
	
	@Bean
	KettleDatabaseRepository kettleDatabaseRepository(KettleDatabaseProperties dbProps, KettleRepositoryProperties repoProps) throws KettleException {
		KettleEnvironment.init();
		KettleDatabaseRepository repository = new KettleDatabaseRepository();
		DatabaseMeta databaseMeta = new DatabaseMeta(dbProps.getName(), dbProps.getType(), dbProps.getAccessProtocol(),
				dbProps.getHost(), dbProps.getName(), dbProps.getPort(), dbProps.getUsername(), dbProps.getPassword());
		KettleDatabaseRepositoryMeta databaseRepositoryMeta = new KettleDatabaseRepositoryMeta(dbProps.getName(), dbProps.getName(), "Kettle Repository", databaseMeta);
		repository.init(databaseRepositoryMeta);
		//连接资源库
		repository.connect(repoProps.getUsername(), repoProps.getPassword());
		return repository;
	}

}
