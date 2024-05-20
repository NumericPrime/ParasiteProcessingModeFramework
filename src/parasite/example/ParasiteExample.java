package parasite.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParasiteExample extends parasite.core.Parasite{

	@Override
	public String[] editCode(String folderName,String sketchName, String[] fileNames, String[] code, boolean[] deleteFile) {
		String rootName=fileNames[0]+"\\\\..";
		for(int i=0;i<code.length;i++) {
			String rt="";
			for(String line:code[i].split("\n")) {
				if(line.startsWith("$")) {
					if(line.startsWith("$INCLUDE")) {
						line=line.substring(line.indexOf('"')+1,line.lastIndexOf('"'));
						try {
							rt+=Files.readString(Path.of(folderName,line));
						} catch (IOException e) {
							e.printStackTrace();
						}
						if(line.startsWith("$RECORD")) {
							line=line.replaceFirst(".RECORD +", "");
							String cln=line.substring(0,line.replace(" ","").indexOf('('));
							rt+="public final class "+cln+"{";
							line=line.substring(line.indexOf('(')+1,line.indexOf(')')).replaceAll(" +", " ");
							String params[]=line.split(",");
							for(String p:params) rt+="private "+p+";";
							rt+="public "+cln+"("+line+"){";
							for(String p:params) rt+="this."+p+"="+p+";";
							rt+="}";
						}
					}
				}else rt+=line+"\n";
			}
			code[i]=rt;
		}
		return code;
	}

}
