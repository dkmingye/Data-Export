package mega.run;
import mega.export.DataExportManager;

import com.mega.modeling.api.MegaApplication;
import com.mega.modeling.api.MegaDatabase;
import com.mega.modeling.api.MegaDatabases;
import com.mega.modeling.api.MegaEnvironment;
import com.mega.modeling.api.MegaEnvironments;
import com.mega.modeling.api.MegaRoot;

public class MegaSession {

	private MegaApplication mMegaApp;
	private MegaEnvironment mEnvironement;
	private MegaEnvironments EnvironmentsCollection;
	private MegaDatabase mDatabase;
	private Configuration megaConfig;
	private MegaRoot mRoot;

	public MegaSession(Configuration config) {
		this.megaConfig = config;
	}

	public void establish() {
		SystemLog.log("============Start connnecting with environment==========");
		if (connection_phase1()) {
			if (connection_phase2()) {
				connection_phase3();
			}
		}
	}

	// phase1 check connection and get available environments
	private boolean connection_phase1() {
		try {
			mMegaApp = new MegaApplication();
			mMegaApp.toolkit().setInteractiveMode("Batch");
			EnvironmentsCollection = mMegaApp.getEnvironments();
			return true;

		} catch (Exception E) {			
			SystemLog
					.log("Error in connection phase 1: failed to connect by Mega Application(API), "
							+ E.getMessage());

			return false;
		}
	}

	// phase2 get the specified environment
	private boolean connection_phase2() {
		for (int i = 1; i <= EnvironmentsCollection.size(); i++) {
			String[] envPath_split_string = EnvironmentsCollection.get(i)
					.getName().split("\\\\");

			int lastPosition = envPath_split_string.length - 1;
			// match the environment
			if (envPath_split_string[lastPosition].equalsIgnoreCase(megaConfig
					.getEnvironemntName())) {
				mEnvironement = EnvironmentsCollection.get(i);
			}
			// System.out.println("Available environment : "+EnvironmentsCollection.get(i).getName());
		}

		if (mEnvironement == null) {
			SystemLog
					.log("Error in connection phase 2: specified environment not found. ");
			return false;
		} else {			
			SystemLog.log("Accessed environment : " + mEnvironement.getName());
			return true;
		}
	}

	// phase3 get specified data repository
	private boolean connection_phase3() {
		try {
			// set username and passwd
			mEnvironement.setCurrentAdministrator(megaConfig.getUsername());
			mEnvironement.setCurrentPassword(megaConfig.getPasswd());

			// get root object
			mDatabase = getDatabaseObject(mEnvironement,
					megaConfig.getRepositoryName());
			if (mDatabase == null) {			
				SystemLog.log("Database could not be found or it is in use.");
			}else {
				mRoot = mDatabase.open();
				// start the export manager
				DataExportManager exportManager = new DataExportManager(mRoot,
						megaConfig.getFilepath());
				exportManager.start();
				return true;
			}
			return false;
			
		} catch (Exception E) {
			SystemLog.log("Error in connection phase 3: "+E.getMessage());
			return false;
		}

	}

	public void close() {
		mEnvironement.release();
		mDatabase.release();
		mRoot.release();
		mMegaApp.release();
		SystemLog.log("============Session end===========");
	}

	private MegaDatabase getDatabaseObject(MegaEnvironment targetEnvironment,
			String databaseName) {
		MegaDatabases cDatabases = targetEnvironment.databases();
		final MegaDatabase megadb = cDatabases.get(databaseName);
		cDatabases.release();
		return megadb;
	}

}
