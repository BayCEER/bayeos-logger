package bayeos.logger.dump;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DumpFileRepository {

	public static List<DumpFile> getFiles(String path) throws IOException {
		List<DumpFile> dumpFiles;
		File d = new File(path);
		if (!d.exists()) {
			if (!d.mkdir())
				throw new IOException("Failed to create directory.");
		}
		dumpFiles = new ArrayList<DumpFile>();
		for (File f : d.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".db");
			}
		})) {
			dumpFiles.add(new DumpFile(f.getAbsolutePath()));
		}
		return dumpFiles;
	}

}
