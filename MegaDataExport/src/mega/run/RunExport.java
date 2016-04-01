package mega.run;

public class RunExport {
	final static String config_file = "mega_config.ini";
	final static String log_file = "mega_export_log.txt";

	public static void main(String[] args) {
		// initialize system log
		SystemLog.initialize(log_file);

		// get settings from config file
		Configuration megaConfig = new Configuration(config_file);
		// mega session
		MegaSession mSession = new MegaSession(megaConfig);
		mSession.establish();
		// close session
		mSession.close();
		SystemLog.close();
	}

}
