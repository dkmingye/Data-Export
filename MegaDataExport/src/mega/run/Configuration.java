package mega.run;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Configuration {
	// variables of config
	private String cEnvironment, cRepository, cUsername, cPasswd, cFilepath,
			cFilepath_part1, cFilepath_part2;

	public Configuration(String filename) {
		System.out.println("============Start reading config file==========");

		int line_number = 0; // used for indicating exceptions
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String line_string;
			while ((line_string = br.readLine()) != null) {

				String line_header = "";
				String line_content = "";
				line_number++;
				if (line_string.length() > 0) {// ignore empty line

					if (!(line_string.charAt(0) == '#')) { // ignore comments
						String[] line_split = line_string.split("=");

						if (line_split.length > 0) {

							line_header = line_split[0];
							if (line_split.length > 1) {
								line_content = line_split[1];
							}
							if (line_header.equalsIgnoreCase("Environment")) {
								cEnvironment = line_content.substring(line_content.indexOf("[") + 1, line_content.indexOf("]"));
							} else if (line_header
									.equalsIgnoreCase("Repository")) {
								cRepository = line_content.substring(line_content.indexOf("[") + 1, line_content.indexOf("]"));;
							} else if (line_header.equalsIgnoreCase("UserName")) {
								cUsername = line_content.substring(line_content.indexOf("[") + 1, line_content.indexOf("]"));;
							} else if (line_header.equalsIgnoreCase("Passwd")) {
								cPasswd = line_content.substring(line_content.indexOf("[") + 1, line_content.indexOf("]"));;
							} else if (line_header.equalsIgnoreCase("path")) {
								cFilepath_part1 = line_content.substring(line_content.indexOf("[") + 1, line_content.indexOf("]"));;
							}
						}
					}
				}
			}
			// /finish reading file
			// naming the output file here
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			cFilepath_part2 = cEnvironment + "-" + cRepository + "-"
					+ dateFormat.format(new Date()) + ".json";
			cFilepath = cFilepath_part1 + cFilepath_part2;
			// /
			fr.close();
		} catch (Exception E) {
			System.out.println("Exception of config file, at line "
					+ line_number + " ::" + E.getMessage());
			E.printStackTrace();
		}

		// check format correctioness
		if (check_data()) {
			SystemLog.log("Environment: [" + cEnvironment+"]");
			SystemLog.log("Repository: [" + cRepository+"]");
			SystemLog.log("Username: [" + cUsername+"]");
			SystemLog.log("Password: [" + cPasswd+"]");
			SystemLog.log("JSON file path: [" + cFilepath+"]");
		}

		SystemLog.log("============Finish reading config file==========");
	}

	private boolean check_data() {
		if (cEnvironment.isEmpty() || cEnvironment == null) {
			SystemLog.log("The Environment is not defined");
			return false;
		}
		if (cRepository.isEmpty() || cRepository == null) {
			SystemLog.log("The Repository is not defined");
			return false;
		}
		if (cUsername.isEmpty() || cUsername == null) {
			SystemLog.log("The Username is not defined");
			return false;
		}

		if (cFilepath.isEmpty() || cFilepath == null) {
			SystemLog.log("The Json Filepath is not defined");
			return false;
		}

		return true;
	}

	public String getEnvironemntName() {
		return cEnvironment;
	}

	public String getRepositoryName() {
		return cRepository;
	}

	public String getUsername() {
		return cUsername;
	}

	public String getPasswd() {
		return cPasswd;
	}

	public String getFilepath() {
		return cFilepath;
	}

}
