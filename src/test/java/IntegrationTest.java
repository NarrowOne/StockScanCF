import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;

public class IntegrationTest {
    // Root URL pointing to the locally hosted function
    // The Functions Framework Maven plugin lets us run a function locally
    private static final String BASE_URL = "http://localhost:8080";

    private static Process emulatorProcess = null;
    private static HttpClient client = HttpClient.newHttpClient();

    @BeforeClass
    public static void setUp() throws IOException {
        // Get the sample's base directory (the one containing a pom.xml file)
        String baseDir = System.getProperty("basedir");

        // Emulate the function locally by running the Functions Framework Maven plugin
        emulatorProcess = new ProcessBuilder()
                .command("mvn", "function:run")
                .directory(new File(baseDir))
                .start();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        // Terminate the running Functions Framework Maven plugin process
        emulatorProcess.destroy();
    }

    @Test
    public void helloHttp_shouldRunWithFunctionsFramework() throws Throwable {
        String functionUrl = BASE_URL + "/stockscan-test";

        HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(functionUrl)).GET().build();

        // The Functions Framework Maven plugin process takes time to start up
        // Use resilience4j to retry the test HTTP request until the plugin responds
        RetryRegistry registry = RetryRegistry.of(RetryConfig.custom()
                .maxAttempts(8)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(200, 2))
                .retryExceptions(IOException.class)
                .build());
        Retry retry = registry.retry("my");

        // Perform the request-retry process
        String body = Retry.decorateCheckedSupplier(retry, () -> client.send(
                getRequest,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body()
        ).apply();

        // Verify the function returned the right results
        assertThat(body).contains("image_data");
    }
}
