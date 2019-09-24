package fr.inria.sacha.coming.analyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import fr.inria.sacha.coming.analyzer.commitAnalyzer.LangAnalyzer;
import fr.inria.sacha.coming.analyzer.commitAnalyzer.LangAnalyzer.CommitInfo;

/**
 * 
 * @author Matias Martinez
 *
 */
public class LangInspector {

	public static int PARAM_GIT_PATH = 0;
	public static int PARAM_MASTER_BRANCH = 1;

	public static void main(String[] args) throws IOException {

		System.out.println(
				"usage arg 1: path to git repo, arg 2: branch, arg 3: commit slides (Default 1-all commits) , arg 4: temp dir , arg 5: cloc path");
		String repositoryPath, masterBranch;

		repositoryPath = args[PARAM_GIT_PATH];
		masterBranch = args[PARAM_MASTER_BRANCH];
		int commitwindows = 1;
		if (args.length > 2) {
			String cwind = args[2];
			commitwindows = Integer.valueOf(cwind);
		}
		File repof = new File(repositoryPath);
		if (!repof.exists()) {
			System.out.println("Repo does not exist: " + repositoryPath);
		}

		String tmpDifS = (args.length > 3) ? args[3] : "/tmp/";

		File tmpDir = new File(tmpDifS);
		if (!tmpDir.exists())
			tmpDir.mkdirs();

		String clocpath = (args.length > 4) ? args[4] : "/usr/bin/perl /home/mmartinez/others/cloc/cloc";

		LangAnalyzer analyzer = new LangAnalyzer(commitwindows, tmpDifS, clocpath);

		List<CommitInfo> ci = (List<CommitInfo>) analyzer.navigateRepo(repositoryPath, masterBranch);
		// System.out.println("Results: ");
		for (CommitInfo commitInfo : ci) {
			// System.out.println("--> " + commitInfo);
		}

		JSONObject json = analyzer.resultToJSON();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser p = new JsonParser();
		JsonElement je = p.parse(json.toJSONString());

		String jsonstr = gson.toJson(je);
		File f = new File("./out/");
		f.mkdirs();
		File fo = new File(f.getAbsolutePath() + File.separator + repof.getName() + ".json");
		FileWriter file = new FileWriter(fo);
		file.write(jsonstr);
		file.flush();
		file.close();

		System.out.println("Result stored at: " + fo);
	}

}
