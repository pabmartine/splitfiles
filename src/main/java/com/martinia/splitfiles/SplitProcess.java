package com.martinia.splitfiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SplitProcess {

	private static Logger LOG = LoggerFactory.getLogger(SplitfilesApplication.class);

	private void validate(String path, long size) throws InvalidParameterException {

		// validate if file exist
		if (!(new File(path)).exists()) {
			throw new InvalidParameterException(String.format("File %s not exist", path));
		}
		// validate if size is correct
		if (size == 0 || size > 1024) {
			throw new InvalidParameterException(
					String.format("Invalid size. Allowed values between 1-1024 (Mb)", path));
		}
	}

	@Async
	public void run(String path, long size, boolean addHeader) throws InvalidParameterException {

		validate(path, size);

		String fileName = path;
		String extension = "";
		if (path.contains(".")) {

			fileName = path.substring(0, path.indexOf("."));
			extension = path.substring(path.indexOf("."), path.length());
		}

		BufferedWriter writer = null;
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(path);
			sc = new Scanner(inputStream, "UTF-8");
			int i = 0;
			String header = "";
			int part = 0;
			File out = new File(fileName + "_" + (++part) + extension);
			writer = Files.newBufferedWriter(out.toPath(), StandardCharsets.UTF_8);

			// Conts of files size for progressbar
			long currentVal = out.length();			
			
			long total = (new File(path)).length();
			long partial = 0;
			long current = 0;
			
			System.out.println();

			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (i++ == 0) {
					header = line;
					writer.write(header);
					writer.newLine();
					writer.flush();

					if (!addHeader) {
						header = "";
					}
										
					
				} else {
					if ((out.length() / 1024) / 1024 < size) {
						writer.write(line);
						writer.newLine();
						writer.flush();
						
						partial = out.length();
						
					} else {
						writer.close();
						
						current += partial;
						partial = 0;
						

						out = new File(fileName + "_" + (++part) + extension);
						writer = Files.newBufferedWriter(out.toPath(), StandardCharsets.UTF_8);
						if (header.length() > 0) {
							writer.write(header);
							writer.newLine();
							writer.flush();
						}
					}

					long nextVal = (out.length() / 1024) / 1024;
					if (currentVal != nextVal) {
						currentVal = nextVal;
						System.out.print("\rSplitting ("+out.getName()+"). Total progress: " + ((current+partial)*100)/total + "%");
					}
				}
			}

			System.out.print("\rCompleted");
			writer.close();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {

			
			
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (sc != null) {
				sc.close();
			}
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}

	}

}
