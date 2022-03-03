package com.example.quick;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

@SpringBootApplication
public class QuickApplication {

	private static Logger log = LoggerFactory.getLogger(QuickApplication.class);

	// https://stackoverflow.com/a/14612564/1098564
	public static void main(String[] args) throws IOException, URISyntaxException {
		String resource = System.getenv("EDMS_DFC_PROPERTIES_PATH");
		// if EDMS_DFC_PROPERTIES_PATH provided use it, otherwise fall back to foo-local.properties on classpath
		String fileUri = getFileUri(resource != null ? resource : "/foo-local.properties");
		System.setProperty("dfc.properties.file", fileUri);

		String dfcPropertiesFilePath = System.getProperty("dfc.properties.file");

		// do something
		File dfcFile = new File(dfcPropertiesFilePath);
		FileInputStream fileInputStream = new FileInputStream(dfcFile);
		Properties prop = new Properties();

		// load a properties file
		prop.load(fileInputStream);
		log.info("foo: " + prop.getProperty("foo"));

		SpringApplication.run(QuickApplication.class, args);
	}

	private static String getFileUri(String resource) throws URISyntaxException, MalformedURLException {
		File file = null;
		URL res = QuickApplication.class.getResource(resource);

		if (res == null) {
			log.info("we couldn't find it on the classpath, so take \"as is\"");
			return resource;
		} else {
			if (res.getProtocol().equals("jar")) {
				log.info("running in a jar so creating a temp file from classpath resource.");
				try {
					InputStream input = QuickApplication.class.getResourceAsStream(resource);
					file = File.createTempFile("tempfile", ".tmp");
					OutputStream out = new FileOutputStream(file);
					int read;
					byte[] bytes = new byte[1024];

					while ((read = input.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					out.close();
					file.deleteOnExit();

				} catch (IOException ex) {
					log.error("File IO Failed.", ex);
				}
			} else {
				//this will probably work in your IDE, but not from a JAR
				log.info("not running in a jar so should be able to read file.");
				file = new File(res.getFile());
			}

			if (file != null && !file.exists()) {
				throw new RuntimeException("Error: File " + file + " not found!");
			}
		}

		String fileUri = file.getAbsoluteFile().toString();

		log.info("new file path: " + fileUri);
		return fileUri;
	}
}
