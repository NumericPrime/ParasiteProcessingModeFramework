package parasite.mode.processingmode;

import processing.app.Base;
import processing.app.Mode;
import processing.app.RunnerListener;
import processing.app.Sketch;
import processing.app.SketchCode;
import processing.app.SketchException;
import processing.mode.java.JavaMode;
import processing.mode.java.runner.Runner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parasite.core.Parasite;

/**
 * The ProcessingMode used
 */
public class ProcessingMode extends JavaMode {
    public ProcessingMode(Base base, File folder) {
        super(base, folder);
        
    }
    /**
     * Gets the display name of this mode.
     * @return the display name of this mode
     */
    @Override
    public String getTitle() {
    	String fld=getFolder().getAbsolutePath();
    	List<String> settings;
		//Reads the name of the mode from the ini file
		try {
			settings = Files.readAllLines(Path.of(fld,"parasite.ini"));
	    	for(String set:settings) if(set.matches("parasite-name *:.*")){
	    		String cln=set.replace(" ", "").replace("parasite-name:", "");
	            return cln;
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return "Processing Mode";
    }
    public String reassemble(String... strs) {
    	String ret="";
    	for(String st:strs) ret+=st;
    	return ret;
    }
    @Override
    public Runner handleLaunch(Sketch sketch,RunnerListener rl,final boolean present) throws SketchException{
    	String fld=getFolder().getAbsolutePath();
    	String parasiteName=null;
    	SketchCode[] cd=sketch.getCode();
	//Reads sketch code and file names
    	String fnames[]=new String[cd.length];
    	String fcode[]=new String[cd.length];
    	for(int i=0;i<cd.length;i++) {
    		fnames[i]=cd[i].getFileName();
    		fcode[i]=cd[i].getProgram();
    	}
    	boolean deleted[]=new boolean[cd.length];
	//applies changes to the sketch
    	try {
		//Reads name of the Parasite class used
    		Parasite ps=null;
        	List<String> settings=Files.readAllLines(Path.of(fld,"parasite.ini"));
        	for(String set:settings) if(set.matches("class-name *:.*")){
        		String cln=set.replace(" ", "").replace("class-name:", "");
        		Class<Parasite> cls=(Class<Parasite>) Class.forName(cln);
			//Creates instancee of parasite
        		ps=cls.getConstructor(new Class[] {}).newInstance();
        	}
    		String[] newRes=ps.editCode(sketch.getFolder().getAbsolutePath(),
    				sketch.getMainName(), fnames, fcode, deleted);
		//Applies changes
    		for(int i=0;i<cd.length;i++) {
    			if(deleted[i]) {
    				sketch.removeCode(cd[i]);
    				continue;
    			}
    			cd[i].setProgram(newRes[i]);
    		}
    		ps.editSketch(sketch);
    	} catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    	return super.handleLaunch(sketch, rl, present);
    }
    /**
     * Retrieve the ClassLoader for JavaMode. 
     */
    @Override
    public ClassLoader getClassLoader() {
        final String JAVA_MODE_NAME = JavaMode.class.getName();

        for (Mode mode : base.getModeList()) {
            if (mode.getClass().getName().equals(JAVA_MODE_NAME)) {
                return mode.getClassLoader();
            }
        }
        
        /* If we return null here, Processing will throw an exception when the compiler is run,
           obscuring the cause of the problem. We'll give a descriptive error message instead. */
        throw new IllegalStateException("Java mode doesn't seem to be loaded. Can't compile sketches.");

    }
}
