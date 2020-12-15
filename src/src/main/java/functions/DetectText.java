package functions;

import com.google.api.client.util.Base64;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class DetectText implements HttpFunction {
    private static final Logger logger = Logger.getLogger(DetectText.class.getName());
    private static final Gson gson = new Gson();


    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException {
        // Check URL parameters for "name" field
        // "world" is the default value555
        String encodedImgData;
        String  imageText;
        byte[] imgData = null;

        request.getFirstQueryParameter("image_data");
        // Parse JSON request and check for "name" field
        try {
            JsonElement requestParsed = gson.fromJson(request.getReader(), JsonElement.class);
            JsonObject requestJson = null;

            if (requestParsed != null && requestParsed.isJsonObject()) {
                requestJson = requestParsed.getAsJsonObject();
            }

            if (requestJson != null && requestJson.has("image_data")) {
                encodedImgData = requestJson.get("image_data").getAsString();
                imgData = decodeBase64(encodedImgData);
            }
        } catch (JsonParseException e) {
            logger.severe("Error parsing JSON: " + e.getMessage());
        }


        if(imgData != null) {
            imageText = detectTextInImage(imgData);
        }else{
            imageText = "No Image Found";
        }

        var writer = new PrintWriter(response.getWriter());
        writer.println(request.getFirstQueryParameter("image_data"));
        writer.printf(imageText);
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

            for(AnnotateImageResponse res : responses){
                if(res.hasError()){
                    text = "Error: " + res.getError().getMessage();
                }

                for(EntityAnnotation annotation : res.getTextAnnotationsList()){
                    System.out.printf("Text: %s%n", annotation.getDescription());
                    text += annotation.getDescription();
                }
            }
        }

        return text;
    }

    private byte[] decodeBase64(String encodedStr){
        return Base64.decodeBase64(encodedStr);
    }
}