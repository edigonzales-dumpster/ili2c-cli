package ch.so.agi.ili2c;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import ch.ehi.basics.logging.EhiLogger;
import ch.interlis.ili2c.CheckReposIlis;
import ch.interlis.ili2c.CloneRepos;
import ch.interlis.ili2c.ListData;
import ch.interlis.ili2c.ListModels;
import ch.interlis.ili2c.ListModels2;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.config.GenerateOutputKind;
import ch.interlis.ili2c.gui.UserSettings;
import ch.interlis.ili2c.metamodel.TransferDescription;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ili2c", mixinStandardHelpOptions = true,
    version = "ili2c 5.1.1",
    description = "Parses and compiles INTERLIS data model definitions. Other options include conversion from INTERLIS Version 1 and back (option -o1) and generation of an XML-Schema, released 2001 (option -oXSD).")
public class App implements Callable<Integer> {

    @Option(names = "--trace", description = "Display detailed trace messages.")
    boolean trace = false;
    
    @Option(names = "--quiet", description = "Suppress info messages.")
    boolean quiet = false;

    @Option(names = "--proxy", paramLabel = "host", description = "proxy server to access model repositories.")
    String httpProxyHost = null;
    
    @Option(names = "--proxyPort", paramLabel = "port", description = "proxy port to access model repositories.")    
    String httpProxyPort = null;

    @Option(names = "--no-auto", description = "don't look automatically after required models.")    
    boolean doAuto = true;
   
    @Option(names = "--check-repo-ilis", paramLabel = "uri", description = "check all ili files in the given repository.")        
    boolean doCheckRepoIlis = false;
    
    @Option(names = "--clone-repos", description = "clones the given repositories to the --out folder.")    
    boolean doCloneRepos = false;
    
    @Option(names = "--listModels", paramLabel = "uri", description = "list all models starting in the given repository. (IliRepository09).")        
    boolean doListModels = false;
    
    @Option(names = "--listAllModels", paramLabel = "uri", description = "list all models (without removing old entries) starting in the given repository. (IliRepository09).")            
    boolean doListAllModels = false;
    
    @Option(names = "--listModels2", paramLabel = "uri", description = "list all models starting in the given repository. (IliRepository20).")            
    boolean doListModels2 = false;

    @Option(names = "--listAllModels2", paramLabel = "uri", description = "list all models (without removing old entries) starting in the given repository. (IliRepository20).")                
    boolean doListAllModels2 = false;
    
    @Option(names = "--listData", paramLabel = "uri", description = "list all data starting in the given repository.")                    
    boolean doListData = false;
    
    @Option(names = "--without-warnings", description = "Report only errors, no warnings. Usually, warnings are generated as well.")    
    boolean withoutWarnings = false;
    
    @Option(names = "--modeldir", paramLabel = "directories", description = "list all data starting in the given repository.")                    
    String ilidirs = UserSettings.DEFAULT_ILIDIRS;

    @Option(names = "--out", paramLabel = "file/dir", description = "file or folder for output (folder must exist).")                    
    String outfile = null;

    @Parameters(arity = "1..*", description = "file1.ili file2.ili ...")
    List<String> files;
    //ArrayList ilifilev = new ArrayList();

    public static final String APP_JAR = "ili2c-cli.jar";
    public static final String APP_NAME = "ili2c";

    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        UserSettings settings = new UserSettings();
        setDefaultIli2cPathMap(settings);
        settings.setHttpProxyHost(httpProxyHost);
        settings.setHttpProxyPort(httpProxyPort);
        settings.setIlidirs(ilidirs);
        Configuration config = new Configuration();
        Iterator ilifilei = files.iterator();
        while (ilifilei.hasNext()) {
            String ilifile = (String) ilifilei.next();
            FileEntry file = new FileEntry(ilifile, FileEntryKind.ILIMODELFILE);
            config.addFileEntry(file);
        }
        if (doAuto) {
            config.setAutoCompleteModelList(true);
        } else {
            config.setAutoCompleteModelList(false);
        }
        withoutWarnings = withoutWarnings ? false : true;
        config.setGenerateWarnings(withoutWarnings);
        // TODO
//        config.setOutputKind(outputKind);
//        config.setLanguage(language);
//        config.setNlsxmlFilename(nlsxmlFilename);
//        config.setParams(params);
        if (doCloneRepos || doListModels || doListAllModels || doListModels2 || doListAllModels2 || doListData
                 /*|| outputKind != GenerateOutputKind.NOOUTPUT*/) { //TODO
            if (outfile != null) {
                config.setOutputFile(outfile);
            } else {
                config.setOutputFile("-");
            }
        }

        EhiLogger.logState(APP_NAME + "-" + TransferDescription.getVersion());
        if (doCheckRepoIlis) {
            boolean failed = new CheckReposIlis().checkRepoIlis(config, settings);
            if (failed) {
                EhiLogger.logError("check of ili's in repositories failed");
                System.exit(1);
            }
        } else if (doCloneRepos) {
            boolean failed = new CloneRepos().cloneRepos(config, settings);
            if (failed) {
                EhiLogger.logError("clone of repositories failed");
                System.exit(1);
            }
        } else if (doListModels || doListAllModels) {
            boolean failed = new ListModels().listModels(config, settings, doListModels == true);
            if (failed) {
                EhiLogger.logError("list of models failed");
                System.exit(1);
            }
        } else if (doListModels2 || doListAllModels2) {
            boolean failed = new ListModels2().listModels(config, settings, doListModels2 == true);
            if (failed) {
                EhiLogger.logError("list of models failed");
                System.exit(1);
            }
        } else if (doListData) {
            boolean failed = new ListData().listData(config, settings);
            if (failed) {
                EhiLogger.logError("list of data failed");
                System.exit(1);
            }
        } else {
            // compile models
//            TransferDescription td = runCompiler(config, settings, ili2cMetaAttrs);
//            if (td == null) {
//                EhiLogger.logError("compiler failed");
//                System.exit(1);
//            }
        }
        
        
        System.out.println("Hallo Stefan.");
        return 0;
    }
    
    static public void setDefaultIli2cPathMap(ch.ehi.basics.settings.Settings settings) {
        HashMap<String, String> pathmap = new HashMap<String, String>();

        String ili2cHome = getIli2cHome();
        if (ili2cHome != null) {
            pathmap.put(UserSettings.JAR_DIR, ili2cHome + File.separator + UserSettings.JAR_MODELS);
        }
        pathmap.put(UserSettings.ILI_DIR, null);
        settings.setTransientObject(UserSettings.ILIDIRS_PATHMAP, pathmap);
    }
    
    static public String getIli2cHome() {
        String classpath = System.getProperty("java.class.path");
        int index = classpath.toLowerCase().indexOf(APP_JAR);
        int start = classpath.lastIndexOf(File.pathSeparator, index) + 1;

        return (index > start) ? classpath.substring(start, index - 1) : null;
    }    
    
}
