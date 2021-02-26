package utils;
 public final class FunctionLog{
     private static volatile FunctionLog instance;

    private String log;

    private FunctionLog(){
        log = "\"Log\" : {\n";
    }

     public static FunctionLog getInstance() {
        if(instance == null){
            instance = new FunctionLog();
        }
        return instance;
     }

     public static void addLog(String tag, String line){
        getInstance();
        instance.log += "\""+tag+"\" : \""+line+"\",\n";
    }

    public static String getLog(){
        String log = instance.log;
        if(log.endsWith(",\n"))
            log = log.substring(0, log.length()-2);

        log += "\n}";

        return log;
    }
}
