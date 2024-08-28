import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AppTest {

    @Mock
    private OutputStream mockOutputStream;
    @Mock
    private Request mockRequest;
    private Response mockResponse;

    private static final String TEST_DIRECTORY = "target/classes/testwebroot";

    /**
     * Configura el entorno de prueba antes de cada prueba.
     * Inicializa los objetos simulados (mocks) y prepara el directorio de prueba.
     * Elimina el contenido del directorio de prueba si ya existe.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockOutputStream = new ByteArrayOutputStream();
        mockResponse = new Response(mockOutputStream);

        try {
            Path testDir = Paths.get(TEST_DIRECTORY);
            if (Files.exists(testDir)) {
                Files.walk(testDir)
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prueba la creación del directorio de archivos estáticos.
     * Configura el directorio de archivos estáticos y verifica si el directorio fue creado correctamente.
     */
    @Test
    public void testDirectoryCreate() {
        // Establecer el directorio de archivos estáticos
        SimpleWebServer.staticfiles("testwebroot");

        // Verificar si el directorio fue creado
        Path testDir = Paths.get(TEST_DIRECTORY);
        assertTrue(Files.exists(testDir));
    }

    /**
     * Prueba el servicio de suma en el servidor web.
     * Configura el servicio REST para la ruta "/suma", simula una solicitud con dos números,
     * y verifica si el resultado de la suma es correcto.
     */
    @Test
    public void testGetSuma() throws IOException {
        SimpleWebServer.get("/suma", (req, res) -> {
            String num1 = req.getValue("num1");
            String num2 = req.getValue("num2");
        
            if (num1 != null && num2 != null) {
                int n1 = Integer.parseInt(num1);
                int n2 = Integer.parseInt(num2);
                return String.valueOf(n1 + n2);
            }
            return "Faltan Parametros";
        });
        when(mockRequest.getValue("num1")).thenReturn("5");
        when(mockRequest.getValue("num2")).thenReturn("10");

        // Obtén el servicio para la ruta /api/suma
        Service service = SimpleWebServer.services.get("/api/suma");

        // Ejecuta el servicio
        String result = service.handleREST(mockRequest, mockResponse);

        // Verifica el resultado
        assertEquals("15", result);
    }

    /**
     * Prueba el servicio de saludo en el servidor web.
     * Configura el servicio REST para la ruta "/hello", simula una solicitud con un nombre,
     * y verifica si la respuesta de saludo es correcta.
     */
    @Test
    public void testGetSaludo() throws IOException {
        SimpleWebServer.get("/hello", (req, res) -> "Hello "+ req.getValue("name"));

        // Configurar los valores esperados en la solicitud
        when(mockRequest.getValue("name")).thenReturn("John");

        // Obtener el servicio para la ruta /api/hello
        Service service = SimpleWebServer.services.get("/api/hello");

        // Ejecutar el servicio
        String result = service.handleREST(mockRequest, mockResponse);

        // Verificar el resultado
        assertEquals("Hello John", result);
    }

    /**
     * Prueba el servicio que devuelve el valor de Pi en el servidor web.
     * Configura el servicio REST para la ruta "/pi", simula una solicitud,
     * y verifica si la respuesta es el valor correcto de Pi.
     */
    @Test
    public void testGetPI() throws IOException {
        SimpleWebServer.get("/pi", (req, resp) -> {return String.valueOf(Math.PI); });

        // Obtener el servicio para la ruta /api/pi
        Service service = SimpleWebServer.services.get("/api/pi");

        // Ejecutar el servicio
        String result = service.handleREST(mockRequest, mockResponse);

        // Verificar el resultado
        assertEquals("3.141592653589793", result);
    }
}
