package dao;

import java.util.Vector;
import models.*;
import java.sql.*;

public class ComentarioDAO implements DAOInterface<Comentario>
{

  Vector<Comentario> listaComentarios;
  DAOInterface<Comentario> dao;

  public ComentarioDAO() throws SQLException
  {
    Connection conex = null;
    Statement statement = null;
    ResultSet rs = null;

    listaComentarios = new Vector<Comentario>();
    conex = getConnection();
    statement = conex.createStatement();

    rs = statement.executeQuery("SELECT * FROM Comentario;");

    while(rs.next())
    {
      Comentario m = new Comentario(
      rs.getString("contenido"),
      rs.getDate("fecha"),
      null,
      new Vector<Comentario>()
      );
      m.setId(rs.getInt("idComentario"));
      asignarAutor(m);
      asignarComentarios(m);
      listaComentarios.add(m);
    }
    conex.close();
  }

  public void asignarAutor(Comentario com) throws SQLException
  {
    Connection conex = getConnection();
    Statement statement = conex.createStatement();
    ResultSet rs = statement.executeQuery("SELECT * FROM "); // INNNER para obtener al autor

    if(rs.next())
    {
      Alumno autor = new Alumno(
      rs.getString("nombre"),
      rs.getString("password"),
      rs.getString("email"),
      rs.getString("boleta"),
      rs.getString("apellidoMaterno"),
      rs.getString("apellidoPaterno"),
      null
      );
      autor.setId(rs.getInt("idAlumno"));
      com.setAutor(autor);
    }
    conex.close();
  }

  public void asignarComentarios(Comentario com) throws SQLException
  {
    Connection conex = getConnection();
    Statement statement = conex.createStatement();
    ResultSet rs = statement.executeQuery("SELECT * FROM "); // INNNER para obtener los comentarios asociados

    while(rs.next())
    {
      Comentario m = new Comentario(
      rs.getString("contenido"),
      rs.getDate("fecha"),
      null,
      null
      );
      m.setId(rs.getInt("idComentario"));
      com.getComentarios().add(m);
    }
    conex.close();
  }

  //Establecer conexion
  public Connection getConnection()
  {
    try{

      Class.forName("com.mysql.jdbc.Driver");

      return DriverManager.getConnection(dao.url + dao.dbName, "root", "" );


    }catch(SQLException sql){
      sql.printStackTrace();
    }catch(ClassNotFoundException cl){
      cl.printStackTrace();
    }

    return null;
  }

  //Leer un unico comentario de la lista
  public Comentario read(int id)
  {
    return listaComentarios.get(id);
  }

  //Leer todos los comentarios
  public Vector<Comentario> read()
  {
    return listaComentarios;
  }

  //Crear un nuevo comentario
  public void create(Comentario c)
  {
    Connection conex = null;
    Statement statement = null;
    PreparedStatement ps = null;

    listaComentarios.add(c);

    try{
      String sql = "INSERT into comentario(contenido, fecha) VALUES (?,?)";
      conex = getConnection();
      ps = conex.prepareStatement(sql);
      ps.setString(1, c.getContenido());
      ps.setDate(2, c.getFecha());
      ps.executeUpdate();
      ps.close();
      conex.close();//

    }catch(SQLException sql){
      sql.printStackTrace();
    }
  }

  //Editar un comentario
  public void update(int id, Comentario t)
  {
    Connection conex = null;
    Statement statement = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Comentario c = read(id);

    try{
      String sql = "UPDATE Comentario "+
      "SET contenido = '"+t.getContenido()+"', fecha = '"+t.getFecha()+
      "' WHERE idComentario = "+c.getId()+";";
      conex = getConnection();
      ps = conex.prepareStatement(sql);
      ps.executeUpdate();
      conex.close();//

    }catch(SQLException sql){
      sql.printStackTrace();
    }

    t.setComentarios(c.getComentarios());
    t.setAutor(c.getAutor());
    t.setId(c.getId());
    listaComentarios.set(id, t);
  }

  //Eliminar un comentario
  public void delete(int id)
  {
    Connection conex = null;
    Statement statement = null;

    Comentario c = read(id);

    try{
      String sql = "DELETE FROM Comentario WHERE idComentario = "+c.getId()+";";
      conex = getConnection();
      statement = conex.createStatement();
      statement.executeUpdate(sql);
      conex.close();//

    }catch(SQLException sql){
      sql.printStackTrace();
    }

    listaComentarios.removeElementAt(id);
  }
}
