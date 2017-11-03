/**
 *
 * @author Havoc
 */
import java.sql.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("Venta")         //Este path hace parte de la URI del servicio REST
public class Venta {
private Statement stmt; 
private ResultSet rs;
private String name,contra;

/**************************************  CONSTRUCTORES  *****************************************/ 
public Venta(){                
/***************    Establecer conexión con base de datos local  para pruebas    *****************/
//String connectString="jdbc:postgresql://localhost:5432/Belfiori";
//try{
//    Class.forName("org.postgresql.Driver");
//    Connection conexion=DriverManager.getConnection(connectString, "postgres", "perro");
//    stmt = conexion.createStatement();
//    rs = stmt.executeQuery("select * from usuarios");
//    while (rs.next())
//        { 
//        name=rs.getString("nombre"); 
//        contra=rs.getString("pass");   
//        } 
//    }
//catch(Exception e)
//    {
//    
//    } 
/***************    Establecer conexión con base de datos en azure     *****************/
String url = String.format("jdbc:postgresql://%s/%s", "pgserverbelfiori.postgres.database.azure.com", "postgres");
Properties properties = new Properties();
properties.setProperty("user", "BelfioriAdmin@pgserverbelfiori");
properties.setProperty("password", "1Gato*2Perros");
properties.setProperty("ssl", "true");
try{
    Class.forName("org.postgresql.Driver");
    Connection conexion = DriverManager.getConnection(url, properties);
    stmt = conexion.createStatement();
      rs = stmt.executeQuery("select * from usuarios");
      while (rs.next())
        { 
        name=rs.getString("nombre"); 
        contra=rs.getString("pass");   
        }
    }
catch(Exception e)
    {

    }
}   
     
 
/**************************************  MÉTODOS *****************************************/
@GET                            //Método para consultar el número consecutivo de facturas en la BD
@Path("NoFactura")      
@Produces(MediaType.APPLICATION_XML)
public Response NoFactura(@QueryParam("user") String usuario, @QueryParam("pass") String pass)
{
String salida=null;
int code=401;

if(usuario.equals(name)&&pass.equals(contra))       //Validación de usuario
    {
    try {
        rs = stmt.executeQuery("select max(id) as id from venta");
        while (rs.next())
            {   
            salida=rs.getString("id");  
            } 
        code=200;
        } 
        catch (SQLException ex) {
        Logger.getLogger(Inventario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
return Response.status(code).entity(salida.toString()).build();
}

@POST     //Ests método registra una venta en la BD
@Path("Registrar/{name}/{pass}/{id}/{cliente}/{ciudad}/{edad}/{precio}/{año}/{mes}/{dia}")      
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
public Response Registrar(@PathParam("name") String usuario, @PathParam("pass") String pass,
                      @PathParam("id") String id, @PathParam("cliente") String cliente, 
                      @PathParam("ciudad") String ciudad, @PathParam("edad") String edad,
                      @PathParam("precio") String precio, @PathParam("año") String año,
                      @PathParam("mes") String mes, @PathParam("dia") String dia)
{
int code=401;    

if(usuario.equals(name)&&pass.equals(contra))       //Validación de usuario
    {
    try {
        stmt.executeUpdate("insert into venta values("+id+",'"+cliente+"','"+ciudad+"','"+edad+"',"+precio+","+año+","+mes+","+dia+")");
        code=200;
        } 
        catch (SQLException ex) {
        Logger.getLogger(Inventario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
return Response.status(code).entity("").build();

}

@POST           //Este método registra en la BD los producto vendidos para control de inventario
@Path("Compone/{name}/{pass}/{id_c}/{id_p}")      
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
public Response Compone(@PathParam("name") String usuario, @PathParam("pass") String pass,
                        @PathParam("id_c") String id_c, @PathParam("id_p") String id_p) 
{
int code=401;    
    
if(usuario.equals(name)&&pass.equals(contra))       //Validación de usuario
    {
    try {
        stmt.executeUpdate("insert into compone values("+id_c+","+id_p+")");
        code=200;
        } 
        catch (SQLException ex) {
        Logger.getLogger(Inventario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
return Response.status(code).entity("".toString()).build();
}

}
