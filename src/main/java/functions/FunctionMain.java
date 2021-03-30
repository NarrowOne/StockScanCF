package functions;

import com.google.api.client.util.Base64;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import utils.FunctionLog;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class FunctionMain implements HttpFunction {
    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final String TAG = "FunctionMain";
    private static final Gson gson = new Gson();
    private TextDetector detector;
    private static PrintWriter writer;
    private String error;


    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException {
        writer = new PrintWriter(response.getWriter());
        writer.flush();

        String contentType = request.getContentType().orElse("No type detected");
//        String responseString = "{\"response\" : {\n";
        String responseString = "{\n";
        FunctionLog.addLog(TAG, contentType);
//        log.addLog(payload);

        switch(contentType){
            case "application/json":
                responseString = handleJson(request, responseString);
                break;
            case "application/x-www-form-urlencoded; charset=UTF-8":
            case "application/x-www-form-urlencoded":
                responseString = handleForm(request, responseString);
                break;

            default:
                response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
                responseString += "\"error\" : \"400 - Bad Request\"\n";
        }

        FunctionLog.addLog(TAG, "Function End");

        responseString += FunctionLog.getLog();

        responseString += "}";
//        JsonObject res = gson.fromJson(json, JsonObject.class);

        writer.write(responseString);
        writer.close();

    }

    private String handleJson(HttpRequest request, String responseString) throws IOException {
        JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
        String imageText;
        byte[] imgData = null;

        try {
            FunctionLog.addLog(TAG, "retrieving json data");
            if (body.has("image_data")) {
                String encodedImgData = body.get("image_data").getAsString();
                if (encodedImgData != null) {
//                    responseString += "\"encoded_data\":\"" + encodedImgData + "\",\n";

                    imgData = decodeBase64(encodedImgData);
                } else {
                    error = "Failed to find image data";
                    responseString += "\"error\" : \"" + error + "\",\n";
                }

//              If image data has been retrieved from request body and decoded, run through OCR
                if (imgData != null) {
                    responseString += getImageText(imgData);
                } else {
                    FunctionLog.addLog(TAG, "Image Data Missing");
                }
            }
        }catch (Exception e){
            error = e.getMessage();
            logger.severe(e.toString());
            responseString += "\"error\" : \"Exception: "+ error +"\",\n";
        }

        return responseString;
    }

    private String handleForm(HttpRequest request, String responseString) throws IOException {
        String  imageText;
        byte[] imgData = null;

        try {
            FunctionLog.addLog(TAG, "retrieving form data");
            Optional<String> encodedImgData = request.getFirstQueryParameter("image_data");

            FunctionLog.addLog(
                    TAG,
                    encodedImgData.isPresent() ? "Image data present" : "Image data missing"
            );

            if (encodedImgData.isPresent()) {
//                responseString +="\"encoded_data\":\""+encodedImgData.get()+"\",\n";

                imgData = decodeBase64(encodedImgData.get());
            } else {
                error = "Failed to find image data";
                responseString += "\"error\" : \""+ error +"\",\n";
            }
        } catch (Exception e) {
            error = e.getMessage();
            logger.severe(e.toString());
            responseString += "\"error\" : \"Exception: "+ error +"\",\n";
        }

//        If image data has been retrieved from request body and decoded, run through OCR
        if (imgData != null) {
            responseString += getImageText(imgData);
        } else {
            FunctionLog.addLog(TAG, "Image Data Missing");
        }

        return responseString;
    }

    private String getImageText(byte[] imgBytes){
        return TextDetector.performOCR(imgBytes);
    }

    private byte[] decodeBase64(String encodedStr){
        return Base64.decodeBase64(encodedStr);
    }


}