package functions;

import com.google.api.client.json.Json;
import com.google.api.client.util.Base64;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.*;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;


public class DetectText implements HttpFunction {
    private static final Logger logger = Logger.getLogger(DetectText.class.getName());
    private static final Gson gson = new Gson();
    private static PrintWriter writer;


    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException {
        String contentType = request.getContentType().toString();
        String encodedImgData = "N/A";
        String  imageText;
        byte[] imgData = null;

        writer = new PrintWriter(response.getWriter());

        request.getFirstQueryParameter("image_data");
        // Parse JSON request and check for "image_data" field

//        writer.write(contentType);

        try {
            JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);

//            writer.write("Searching for image data");
            if (body != null && body.has("image_data")) {
                encodedImgData = body.get("image_data").getAsString();
                imgData = decodeBase64(encodedImgData);
            } else {
//                writer.write("Failed to find image data");
            }

        } catch (Exception e) {
            logger.severe("Error parsing JSON: " + e.getMessage());
        }

//        If image data has been retrieved from request body and decoded, run through OCR
        if (imgData != null) {
            imageText = detectTextInImage(imgData);
        } else {
            imageText = "failed to parse image";
        }

//        writer.write("{\"image_text\":\""+imageText+"\"}");

        String json =   "{\"image_text\":\""+imageText+"\"}";

        JsonObject res = gson.fromJson(json, JsonObject.class);

        writer.print(res);

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
        }

        return text;
    }

    private byte[] decodeBase64(String encodedStr){
        return Base64.decodeBase64(encodedStr);
    }
}