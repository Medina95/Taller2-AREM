
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
/**
 * implementa un servidor web básico que escucha en un puerto especificado y maneja solicitudes HTTP.
 *  El servidor es capaz de servir archivos estáticos  almacenados en el weebroot  y manejar solicitudes RESTful utilizando
 *  métodos HTTP (GET, POST, PUT, DELETE).
 * Utiliza un serversocket para aceptar conexiones de clientes y delega a un clienthadler para el manejo de solicitudes 
 */
public class SimpleWebServer {

   
    private static final int PORT = 8080;
    private static String WEB_ROOT = "src/webroot";
    static final Map<String, Service> services = new HashMap<>();

     /**
     * Método principal que inicia el servidor web.
     * Crea un {@link ServerSocket} para escuchar en el puerto especificado y acepta conexiones entrantes.
     * Cada conexión es manejada en un hilo separado utilizando {@link ClientHandler}.
     *
     * @param args Los argumentos de línea de comandos 
     */

    public static void main(String[] args) {
        staticfiles("webroot/publics");
        get("/pi", (req, resp) -> {return String.valueOf(Math.PI); });
        get("/hello", (req, res) -> "Hello "+ req.getValue("name"));
        get("/suma", (req, res) -> {
            String num1 = req.getValue("num1");
            String num2 = req.getValue("num2");
        
            if (num1 != null && num2 != null) {
                int n1 = Integer.parseInt(num1);
                int n2 = Integer.parseInt(num2);
                return String.valueOf(n1 + n2);
            }
            return "Faltan Parametros";
        });
        

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
     /**
     * Registra un servicio REST para una ruta específica.
     * @param path Ruta del servicio REST compuesta por /api/ y el nombre
     * @param action Acción a ejecutar cuando se recibe la ruta especificada.
     */
    public static void get(String path, Service action) {
        services.put("/api" + path, action);
    }


    /**
     * Establece el directorio raíz de archivos estáticos.
     * @param directory Nombre del directorio que el desarollador decida para poner sus archivos estaticos
     */
    public static void staticfiles(String folder) {
        WEB_ROOT = "target/classes/"+folder;
        Path path = Paths.get(WEB_ROOT);

        // Verifica si el directorio existe, si no, lo crea
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Directorio creado: " + WEB_ROOT);
            } catch (IOException e) {
                System.err.println("Error al crear el directorio: " + WEB_ROOT);
                e.printStackTrace();
            }
        } else {
            System.out.println("Usando el directorio existente: " + WEB_ROOT);
        }
    }

    

    /**
     * Clase interna que maneja la comunicación con un cliente en un hilo separado.
     * Procesa las solicitudes HTTP y delega el manejo de solicitudes RESTful a los servicios adecuados.
     */
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        /**
         * Inicializa el {@code ClientHandler} con el {@link Socket} del cliente.
         *
         * @param clientSocket El socket del cliente.
         */
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /**
         * Método principal que maneja la solicitud del cliente.
         * Lee la solicitud HTTP, determina el método y recurso solicitado, y llama al servicio adecuado.
         * Si el recurso no es RESTful, intenta servir un archivo estático.
         */
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 OutputStream out = clientSocket.getOutputStream()) {

                String requestLine = in.readLine();
                if (requestLine == null) return;

                String[] tokens = requestLine.split(" ");
                if (tokens.length < 3) return;

                String method = tokens[0];
                String requestedResource = tokens[1];
                String[] parts = requestedResource.split("/");
                           
                String idString = null;

                if (parts.length > 3) {
                    idString = parts[2]; 
                }       

                String basePath = requestedResource.split("\\?")[0];

                if(basePath.startsWith("/api")) {
                    if (services.containsKey(basePath)) {
                        Request req = new Request(requestedResource);
                        Response res = new Response(out);
                        res.setCodeResponse("200 OK");
                        String response = services.get(basePath).handleREST(req, res);
                        sendResponse(out, response, res);
                    } 
                } else {
                    serveStaticFile(requestedResource, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    
     /**
      * 
      * @param out flujo de salida a través del cual se enviará la respuesta HTTP al cliente
      * @param response El cuerpo de la respuesta HTTP que se enviará al cliente. Contenido que el cliente verá en su navegador 
      * @param res objeto Response que contiene información adicional para construir la respuesta HTTP, como el código de respuesta y el tipo de contenido.
      * @throws IOException
      */
        private void sendResponse(OutputStream out, String response, Response res) throws IOException {
            String httpResponse = "HTTP/1.1 "+ res.getCodeResponse()+ "\r\n" +
                    "Content-Type: " + res.getContentType() + "\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "\r\n" +
                    response;
            out.write(httpResponse.getBytes());
            out.flush();
        }
         /**
         * Sirve archivos estáticos desde el directorio raíz.
         *
         * @param resource El recurso solicitado (ruta del archivo).
         * @param out El flujo de salida para enviar la respuesta al cliente.
         * @throws IOException Si ocurre un error al leer el archivo o al escribir la respuesta.
         */

        private void serveStaticFile(String resource, OutputStream out) throws IOException {
            Path filePath = Paths.get(WEB_ROOT, resource);
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                // Detectar el tipo MIME
                String contentType = Files.probeContentType(filePath);
                byte[] fileContent = Files.readAllBytes(filePath);
                // Crear el encabezado de la respuesta HTTP
                String responseHeader = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + fileContent.length + "\r\n" +
                        "\r\n";
                out.write(responseHeader.getBytes());
                out.write(fileContent);
            } else {
                send404(out);
            }
        }
        
  /**
         * Envía una respuesta 404 Not Found al cliente.
         *
         * @param out El flujo de salida para enviar la respuesta al cliente.
         * @throws IOException Si ocurre un error al escribir la respuesta.
         */
        private void send404(OutputStream out) throws IOException {
            String response = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    "{\"error\": \"Not Found\"}";
            out.write(response.getBytes());
        }
    }
}