package functions;

import com.google.api.client.json.Json;
import com.google.api.client.util.Base64;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.controllers.DAOController;
import dao.controllers.LogDAOController;
import dao.controllers.ProduceDAOController;
import dao.daoImpl.LogDAO;
import dao.daoImpl.ProduceDAO;
import models.LogEntry;
import models.Produce;
import utils.FunctionLog;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.logging.Logger;


public class FunctionMain implements HttpFunction {
    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final String TAG = "FunctionMain";
    private static final Gson gson = new Gson();
    private String error;


    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException {
        PrintWriter writer = new PrintWriter(response.getWriter());
        writer.flush();

        String contentType = request.getContentType().orElse("No type detected");
//        String responseString = "{\"response\" : {\n";
        String responseString = "{\n";
        FunctionLog.addLog(TAG, contentType);
//        log.addLog(payload);

        if ("application/json".equals(contentType)) {
            responseString = handleJson(request, responseString);
        } else {
            response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
            responseString += "\"error\" : \"400 - Bad Request\"\n";
        }

        FunctionLog.addLog(TAG, "Function End");

        responseString += FunctionLog.getLog();

        responseString += "}";
//        JsonObject res = gson.fromJson(json, JsonObject.class);

        writer.write(responseString);
        writer.close();
        FunctionLog.clear();

    }

    private String handleJson(HttpRequest request, String responseString) throws IOException {
        JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
        DAOController controller;
        byte[] imgData = null;

        try {
            FunctionLog.addLog(TAG, "retrieving json data");
            if(body.has("request_type")){
                switch (body.get("request_type").getAsString()){
                    case "ocr":
                        if (body.has("image_data")) {
                            String encodedImgData = body.get("image_data").getAsString();
                            if (encodedImgData != null) {

                                imgData = Base64.decodeBase64(encodedImgData);
                            } else {
                                error = "Failed to find image data";
                                responseString += "\"error\" : \"" + error + "\",\n";
                            }

//              If image data has been retrieved from request body and decoded, run through OCR
                            if (imgData != null) {
                                responseString += TextDetector.performOCR(imgData);
                            } else {
                                FunctionLog.addLog(TAG, "Image Data Missing");
                            }
                        }
                        break;
                    case "produce_crud":
                        FunctionLog.addLog(TAG, "produce_crud");
                        controller = new ProduceDAOController(new ProduceDAO());
                        Produce produce = null;
                        if(body.has("produce")) {
                            JsonObject produceJson = body.getAsJsonObject("produce");

                            String bodyString = body.toString();
                            logger.severe(bodyString);

                            produce = new Produce(
                                    produceJson.get("name").getAsString(),
                                    produceJson.get("product_code").getAsString(),
                                    produceJson.get("producer").getAsString(),
                                    produceJson.get("batch").getAsString(),
                                    produceJson.get("weight").getAsString(),
                                    produceJson.get("expiry").getAsString()
                            );
                            if(produceJson.has("id"))
                                produce.setId(produceJson.get("id").getAsString());
                        }

                        switch (body.get("crud_type").getAsString()){
                            case "save":
                                logger.info("save request");
                                    responseString += controller.create(produce);
                                break;

                            case "read_all":
                                FunctionLog.addLog(TAG, "read_all");
                                responseString += controller.read();
                                break;

                            case "read":
                                break;

                            case "update":
                                    responseString += controller.update(produce);
                                break;

                            case "delete":
                                    responseString += controller.delete(produce);
                                break;

                    }
                        break;

                    case "logs":
                        controller = new LogDAOController(new LogDAO());
                        LogEntry logEntry = null;
                        if(body.has("record")){
                            JsonObject logJson = body.getAsJsonObject("record");

                            logEntry = new LogEntry(
                                    logJson.get("log_type").getAsInt(),
                                    logJson.get("subject").getAsString(),
                                    logJson.get("description").getAsString()
                            );
                            if(logJson.has("id"))
                                logEntry.setId(logJson.get("id").getAsString());
                        }
                        switch (body.get("crud_type").getAsString()){
                            case "save":
                                responseString += controller.create(logEntry);
                                break;

                            case "read_all":
                                break;

                            case "read":
                                if(body.has("log_type")) {
                                    responseString += controller.read(body.get("log_type").getAsString());
                                }else{
                                    error = "No log type provided";
                                    responseString += "\"error\" : \"" + error + "\",\n";
                                }
                                break;

                            case "update":
                                responseString += controller.update(logEntry);
                                break;

                            case "delete":
                                responseString += controller.delete(logEntry);
                                break;
                        }
                        break;

                    default:
                        error = "Invalid request type";
                        responseString += "\"error\" : \"" + error + "\",\n";
                        break;
                }
            }else{
                error = "No request type provided";
                responseString += "\"error\" : \"" + error + "\",\n";
            }
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            logger.severe(e.toString());
            e.printStackTrace(pw);
            responseString += "\"error\" : \""+ sw +"\",\n";
        }

        return responseString;
    }

        private String getImageText(byte[] imgBytes){
        return TextDetector.performOCR(imgBytes);
    }
}