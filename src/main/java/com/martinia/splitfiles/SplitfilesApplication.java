package com.martinia.splitfiles;

import java.security.InvalidParameterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SplitfilesApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(SplitfilesApplication.class);

	@Autowired
	SplitProcess splitProcess;

	public static void main(String[] args) {
		SpringApplication.run(SplitfilesApplication.class, args);
	}

	@Override
	public void run(String... args) {

		try {

			String path = args[0];
			long size = Long.valueOf(args[1]);
			boolean allowHeaders = args[2] != null ? Boolean.valueOf(args[2]
					) : false;

			splitProcess.run(path, size, allowHeaders);
		} catch (InvalidParameterException e) {
			LOG.error(e.getMessage());
		} catch (Exception e) {
			LOG.error(showMenu());
		}
	}
	
	private String showMenu() {
		StringBuilder sb = new StringBuilder();	
		sb.append("Wrong parameters. The following are required:");
		sb.append(System.lineSeparator());
		sb.append("arg[0] --> File path: Path of the file to be slitted");
		sb.append(System.lineSeparator());
		sb.append("arg[1] --> Size: Size in Mb of the new subfiles generated");
		sb.append(System.lineSeparator());
		sb.append("arg[2] --> Allow headers: To keep file header (if exist) in every subfile generated. False by deafault.");
		return sb.toString();
	}

}
