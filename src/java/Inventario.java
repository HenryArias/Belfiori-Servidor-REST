/**
 *
 * @author Havoc
 */
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


@Path("Inventario")         //Este path hace parte de la URI del servicio REST
public class Inventario {
private Statement stmt; 
private ResultSet rs;
private String name,contra;
    
/**************************************  CONSTRUCTORES  *****************************************/ 
public Inventario(){                
/***************    Establecer conexión con base de datos local para pruebas     *****************/
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
@GET                   //Este método obtiene la información de un producto según el id 
@Path("Item")
@Produces(MediaType.APPLICATION_XML)
public Response item(@QueryParam("user") String usuario, @QueryParam("pass") String pass, @QueryParam("id") int id)
{
List<String> salida = new ArrayList<>();
int code=401,aux=0;
String producto=null;

aux=id/100000;
switch(aux)
    {
    case 1:     producto="flor";    break;
    case 2:     producto="caja";    break;
    case 3:     producto="base";    break;
    }
if(usuario.equals(name)&&pass.equals(contra))       //Validación de usuario
    {
    try {
        rs = stmt.executeQuery("select * from "+producto+" where id_a="+id);
        while (rs.next())
            { 
            salida.add(rs.getString("id_a")); 
            salida.add(rs.getString("nom_a")); 
            salida.add(rs.getString("cant_a")); 
            salida.add(rs.getString("costo_a"));  
            if(aux==1)
                {
                salida.add(rs.getString("color_a"));     
                }
            } 
        code=200;
        } 
        catch (SQLException ex) {
        Logger.getLogger(Inventario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
return Response.status(code).entity(salida.toString()).build();
}

@GET 
@Path("Rosas")      //Este método obtiene toda la información de las rosas
@Produces(MediaType.APPLICATION_XML)
public Response rosas(@QueryParam("user") String usuario, @QueryParam("pass") String pass)
{
List<String> salida = new ArrayList<>(); 
int aux=401;

if(usuario.equals(name)&&pass.equals(contra))       //Validación de usuario
    {
    try {
        rs = stmt.executeQuery("select * from flor");
        while (rs.next())
            {   
            salida.add(rs.getString("id_a"));  
            salida.add(rs.getString("nom_a")); 
            salida.add(rs.getString("cant_a")); 
            salida.add(rs.getString("costo_a")); 
            salida.add(rs.getString("color_a")+";"); 
            } 
        aux=200;
        } 
        catch (SQLException ex) {
        Logger.getLogger(Inventario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
return Response.status(aux).entity(salida.toString()).build();
}

@GET                                //Este método obtiene toda la información de las bases
@Path("Bases")      
@Produces(MediaType.APPLICATION_XML)
public Response bases(@QueryParam("user") String usuario, @QueryParam("pass") String pass)
{
List<String> salida = new ArrayList<>(); 
int aux=401;

if(usuario.equals(name)&&pass.equals(contra))       //Validación de usuario
    {
    try {
        rs = stmt.executeQuery("select * from base");
        while (rs.next())
            {   
            salida.add(rs.getString("id_a"));  
            salida.add(rs.getString("nom_a")); 
            salida.add(rs.getString("cant_a")); 
            salida.add(rs.getString("costo_a")+";"); 
            } 
        aux=200;
        } 
        catch (SQLException ex) {
        Logger.getLogger(Inventario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
return Response.status(aux).entity(salida.toString()).build();
}

@GET                         //Este método obtiene toda la información de las cajas
@Path("Cajas")     
@Produces(MediaType.APPLICATION_XML)
public Response cajas(@QueryParam("user") String usuario, @QueryParam("pass") String pass)
{
List<String> salida = new ArrayList<>(); 
int code=401;

if(usuario.equals(name)&&pass.equals(contra))       //Validación de usuario
    {
    try {
        rs = stmt.executeQuery("select * from caja");
        while (rs.next())
            {   
            salida.add(rs.getString("id_a"));  
            salida.add(rs.getString("nom_a")); 
            salida.add(rs.getString("cant_a")); 
            salida.add(rs.getString("costo_a")); 
            salida.add(rs.getString("dim_a")); 
            salida.add(rs.getString("rafia")+";"); 
            } 
        code=200;
        } 
        catch (SQLException ex) {
        Logger.getLogger(Inventario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
return Response.status(code).entity(salida.toString()).build();
}

@POST                   //Este método actualiza la BD de inventario despues de ser registrada una venta
@Path("Actualizar/{name}/{pass}/{id_p}")      
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
public Response Compone(@PathParam("name") String usuario, @PathParam("pass") String pass,
                        @PathParam("id_p") String id_p) 
{
int code=401,aux=0,serial=0;    
String producto=null;

serial=Integer.parseInt(id_p);
aux=serial/100000;
switch(aux)
    {
    case 1:     producto="flor";    break;
    case 2:     producto="caja";    break;
    case 3:     producto="base";    break;
    }
if(usuario.equals(name)&&pass.equals(contra))       //Validación de usuario
    {
    try {                                           //Aqui resta los productos del inventario que fueron vendidos
        stmt.executeUpdate("update "+producto+" set cant_a=cant_a-1 where id_a="+id_p);
        code=200;
        } 
        catch (SQLException ex) {
        Logger.getLogger(Inventario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
return Response.status(code).entity("".toString()).build();
}

}
