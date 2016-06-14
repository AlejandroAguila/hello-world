/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import correos.Correos;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 *
 * @author Usuario
 */
@WebServlet(name = "ServletCorreo", urlPatterns = {"/ServletCorreo"})
public class ServletCorreo extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileUploadException, Exception {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String mensaje = "";
        String asunto = "";
        String destinatario = "";
        String cc = "";
        String cco = "";
        String ruta = getServletConfig().getServletContext().getRealPath("");
        boolean Resultado = false;



        File archivo = null;
        /*SUBIR IMAGEN AL SERVIDOR */
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            out.print("error");
            return;
        }


        FileItemFactory file_factory = new DiskFileItemFactory();
        ServletFileUpload servlet_up = new ServletFileUpload(file_factory);
        List items = servlet_up.parseRequest(request);
        ArrayList files = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            FileItem item = (FileItem) items.get(i);

            if (item.getFieldName().equals("mensaje")) {
                mensaje = item.getString();

            }
            if (item.getFieldName().equals("asunto")) {
                asunto = item.getString();
            }
            if (item.getFieldName().equals("destinatario")) {
                destinatario = item.getString();

            }
            if (item.getFieldName().equals("cc")) {
                cc = item.getString();
            }
            if (item.getFieldName().equals("cco")) {
                cco = item.getString();

            }
            try {
                if (!item.isFormField()) {
                    System.out.println(ruta + "/" + item.getName());
                    archivo = new File(ruta + "/" + item.getName());
                    item.write(archivo);
                    files.add(archivo);
                }

            } catch (Exception e) {
                System.out.println("Error " + e);
            }

        }
        


        StringWriter writer = new StringWriter();
        Properties p = new Properties();
        p.setProperty("resource.loader", "file");
        p.setProperty("file.resource. loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        p.setProperty("file.resource. loader.path", "c:/template");
        p.setProperty("file.resource. loader.cache", "true");
        p.setProperty("file.resource.loader.modificationCheckInterval", "2");
        try {
            //Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, this);
            String path1 = request.getContextPath();
            String path2 = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/template/";
            String path3 = getServletConfig().getServletContext().getRealPath("/template/");
            System.out.println("Request: " + path1);
            System.out.println("Request: " + path2);
            System.out.println("Request: " + path3);
            Velocity.setProperty("resource.loader", "file");
            Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
            Velocity.setProperty("file.resource.loader.path", path3);
            Velocity.setProperty("file.resource.loader.cache", "true");
            Velocity.setProperty("file.resource.loader.modificationCheckInterval", "2");
            Velocity.init();
            Template t = Velocity.getTemplate("formato1.vm");
            System.out.println("Template: " + t.getName());
            VelocityContext context = new VelocityContext();
            context.put("name", "Usuario");
            t.merge(context, writer);


//       
                /* Importar el jar que contiene el metodo para enviar correo*/
            Correos c = new Correos();
            Resultado = c.send(destinatario, cc, cco, asunto, true, writer.toString(), false,files);
        } catch (Exception e) {
            System.err.println("Exception caught: " + e.getMessage());
        }
//Borrar archivo del Servidor

        archivo.delete();
        files.remove(archivo);
        request.setAttribute("OK", Resultado + "");


        RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
        rd.forward(request, response);









    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (FileUploadException ex) {
            Logger.getLogger(ServletCorreo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServletCorreo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (FileUploadException ex) {
            Logger.getLogger(ServletCorreo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServletCorreo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
