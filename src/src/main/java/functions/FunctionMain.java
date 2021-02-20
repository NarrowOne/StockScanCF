package functions;

import com.google.api.client.util.Base64;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class FunctionMain implements HttpFunction {
    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final Gson gson = new Gson();
    private static PrintWriter writer;
    private FunctionLog log;
    private String error;


    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException {
        writer = new PrintWriter(response.getWriter());
        writer.flush();
//        BufferedReader reader = request.getReader();
//        String payload = "PAYLOAD: "+reader.readLine();
//        reader.close();
//        InputStream stream = request.getInputStream();
//        String payload = "PAYLOAD: "+ Arrays.toString(Base64.decodeBase64(stream.readAllBytes()));
//        stream.close();

//        logger.info(payload);

        String contentType = request.getContentType().orElse("No type detected");
        String responseString = "{\n";
        log  = new FunctionLog();
        log.addLog(contentType);
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

        responseString += log.getLog();

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
            log.addLog("retrieving json data");
            if (body.has("image_data")) {
                String encodedImgData = body.get("image_data").getAsString();
                if (encodedImgData != null) {
                    responseString += "\"encoded_data\":\"" + encodedImgData + "\",\n";

                    imgData = decodeBase64(encodedImgData);
                } else {
                    error = "Failed to find image data";
                    responseString += "\"error\" : \"" + error + "\",\n";
                }

                if (imgData != null) {
                    imageText = detectTextInImage(imgData);
                    responseString += "\"image_text\":\"" + imageText + "\",\n";
                } else {
                    log.addLog("Image Data Missing");
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
            log.addLog("retrieving form data");
            Optional<String> encodedImgData = request.getFirstQueryParameter("image_data");

            log.addLog(
                    encodedImgData.isPresent() ? "Image data present" : "Image data missing"
            );

            if (encodedImgData.isPresent()) {
                responseString +="\"encoded_data\":\""+encodedImgData.get()+"\",\n";

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
            imageText = detectTextInImage(imgData);
            responseString += "\"image_text\":\""+imageText+"\",\n";
        } else {
            log.addLog("Image Data Missing");
        }

        return responseString;
    }

    private String detectTextInImage(byte[] imgData) throws IOException{
        List<AnnotateImageRequest> requests = new ArrayList<>();
        String text = "";
//        ByteString imgBytes = ByteString.copyFrom(imgData);

        Image img = Image.newBuilder().setContent(ByteString.copyFrom(imgData)).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try(ImageAnnotatorClient client = ImageAnnotatorClient.create()){
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            int loopRuns = 0;
            for(AnnotateImageResponse res : responses){
                if(res.hasError()){
                    text = "Error: " + res.getError().getMessage();
                }

                for(EntityAnnotation annotation : res.getTextAnnotationsList()){
                    System.out.printf("Text: %s%n", annotation.getDescription());
                    text += annotation.getDescription();
                }
                loopRuns++;
            }
            text = text.replaceAll("\n", " ; ");
        }

        return text;
    }

    private byte[] decodeBase64(String encodedStr){
        return Base64.decodeBase64(encodedStr);
    }

    private static class FunctionLog{
        private String log;
        private int logNumber;

        private FunctionLog(){
            log = "\"Log\" : {\n";
            logNumber = 0;
        }

        private void addLog(String line){
            log += "\""+logNumber+"\" : \""+line+"\",\n";
            logNumber++;
        }

        private String getLog(){
            if(log.endsWith(",\n"))
                log = log.substring(0, log.length()-2);

            log += "\n}";

            return log;
        }
    }
}