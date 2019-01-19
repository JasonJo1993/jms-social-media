package com.mytwitter.app;

import java.io.IOException;

import com.mytwitter.configuration.Configuration;
import com.mytwitter.configuration.CoreSettings;
import com.mytwitter.routes.LogRouteAdapter;
import com.mytwitter.routes.Routes;

import dataservice.DataService;
import dataservice.GuavaCachingDataService;
import dataservice.MybatisDataService;
import spark.Spark;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

	public static void main(String[] args) throws IOException {

		Spark.staticFiles.location("/webapp/");
		Spark.port(Configuration.get(CoreSettings.PORT));

		DataService dataService = Configuration.get(CoreSettings.USE_CACHING) ?
				new GuavaCachingDataService(new MybatisDataService()) :
					new MybatisDataService();

		Routes routes = new Routes(dataService);
		routes.addRouteListener(new LogRouteAdapter());
		routes.start();
	}
}
