# DESARROLLO DE UN MARCO WEB PARA SERVICIOS REST Y GESTIÓN DE ARCHIVOS ESTÁTICOS

Este proyecto implementa un servidor web básico en Java que maneja solicitudes RESTful y sirve archivos. El servidor es capaz de servir contenido estático desde un directorio específico procesando solicitudes get mediante funciones lambda.


 ## Arquitectura
 Este proyecto sigue la arquitectura cliente-servidor. Los clientes envían solicitudes HTTP al servidor, que maneja la lógica de los servicios REST  también retorna archivos estáticos, como HTML, CSS e imágenes a travez del directorio que se le asigne.

 ### componentes  
   - **Servidor** : SimpleWebServer maneja las conexiones y enruta las solicitudes a los servicios correspondientes. ClientHandler Gestiona la comunicación con el cliente, procesa la solicitud y decide si debe servir un archivo estático o delegar la solicitud a un servicio REST.
   - **Servicios REST**: Implementados en la clase RestServiceImpl, manejan operaciones CRUD en objetos JSON.
   - **Archivos estáticos**: Almacenados en el directorio webroot


## Primeros Pasos
Estas instrucciones le permitirán obtener una copia del proyecto en funcionamiento en su máquina local para fines de desarrollo y prueba. 


### Requisitos Previos
Para ejecutar este proyecto, necesitarás tener instalado:

- Java JDK 8 o superior.
- Un IDE de Java como IntelliJ IDEA, Eclipse, o NetBeans.
- Maven para manejar las dependencias 
- Un navegador web para interactuar con el servidor.

### Instalación 

1. Tener instalado Git en tu maquina local 
2. Elegir una carpeta en donde guardes tu proyecto
3. abrir la terminal de GIT --> mediante el clik derecho seleccionas Git bash here
4. Clona el repositorio en tu máquina local:
   ```bash
   git clone https://github.com/Medina95/Taller2-AREM.git
   ```
5. Abre el proyecto con tu IDE favorito o navega hasta el directorio del proyecto 
6. Desde la terminal  para compilar el proyecto ejecuta:

   ```bash
   mvn clean install
   ```
7. compila el proyecto  que contiene el metodo MAIN: SimpleWebServer.java o ejecuta desde la terminal

   ```bash
    java -cp target/miprimera-app-1.0-SNAPSHOT.jar SimpleWebServer
   ```
   Vera que el servidor esta listo y corriendo sobre el puerto 8080
8. Puedes Ingresar desde el navegador a  la pagina:

    **Importante**: solo servira archivos del directorio especifico que hayas proporcionado en el main staticfiles("public"), por default  los archivos se guardaran en *target/classes/public*, bien puedes agregar un html o copiar algun recurso de la carpeta webroot y probarlo como el index por ejemplo.
        http://localhost:8080/index.html
    
9. Puedes interactuar con los endpoints RESTful (/api) con:
   - http://localhost:8080/api/suma?num1=2&num2=2
   - http://localhost:8080/api/pi
   - http://localhost:8080/api/hello?name=Revisor

  

## Ejecutar las pruebas

Se implementaron pruebas unitarias para los métodos de manejo de solicitudes HTTP  en el servidor. Estas pruebas se realizaron utilizando JUnit y Mockito para simular las solicitudes y validar las respuestas.

Para ejecutar las pruebas:  
1. Desde tu IDE, ejecuta las clase AppTest.java o desde la terminal ejecutas:
   ```bash
   mvn test
   ```
### Desglosar en pruebas de extremo a extremo

- **testDirectoryCreate**: Prueba la creación del directorio de archivos estáticos.Configura el directorio de archivos estáticos y verifica si el directorio fue creado correctamente.
- **testGetSuma**: Prueba el servicio de suma en el servidor web. Configura el servicio REST para la ruta "api/suma", simula una    solicitud con dos números, y verifica si el resultado de la suma es correcto.
- **testGetSaludo**: Prueba el servicio de saludo en el servidor web. Configura el servicio REST para la ruta "api/hello", simula una solicitud con un nombre y verifica si la respuesta de saludo es correcta.
- **testGetPI**:Prueba el servicio que devuelve el valor de Pi en el servidor web.Configura el servicio REST para la ruta "api/pi", simula una solicitud,y verifica si la respuesta es el valor correcto de Pi




## Built With
* [Maven](https://maven.apache.org/) - Dependency Management



## Authors

* **Carolina Medina Acero** -  [Medina95](https://github.com/Medina95)
