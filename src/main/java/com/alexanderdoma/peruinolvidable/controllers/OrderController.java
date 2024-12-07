package com.alexanderdoma.peruinolvidable.controllers;

import com.alexanderdoma.peruinolvidable.model.DAOException;
import com.alexanderdoma.peruinolvidable.model.mysql.OrderDAO;
import com.alexanderdoma.peruinolvidable.model.mysql.OrderlineDAO;
import com.alexanderdoma.peruinolvidable.model.entity.Orderline;
import com.alexanderdoma.peruinolvidable.model.entity.User;
import com.alexanderdoma.peruinolvidable.model.mysql.UserDAO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "OrderController", urlPatterns = {"/orders", "/order", "/sout"})
public class OrderController extends HttpServlet {

    OrderDAO orderService = new OrderDAO();
    OrderlineDAO orderlineService = new OrderlineDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        switch (action) {
            case "/orders":
                showOrdersPage(request, response);
                break;

            case "/order":
                showOrderPage(request, response);
                break;
            case "/sout":

                sendmail(request, response);
                break;
        }
    }

    private void showOrdersPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isLoggedIn(request) != true) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        loadOrders(request);
        request.getRequestDispatcher("orders.jsp").forward(request, response);
    }

    private void loadOrders(HttpServletRequest request) {
        try {
            int user_id = (int) request.getSession().getAttribute("user_id");
            request.setAttribute("orders", orderService.getOrdersByUser(user_id));
        } catch (DAOException ex) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showOrderPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isLoggedIn(request) != true) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int order_id = Integer.parseInt(request.getParameter("id").toString());

        try {
            List<Orderline> orderlines = (List<Orderline>) orderlineService.getOrderlineByOrder(order_id);
            if (orderlines == null) {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
            request.setAttribute("orderlines", orderlines);
            request.getRequestDispatcher("order.jsp").forward(request, response);
        } catch (DAOException ex) {
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("user_id") != null;
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private static void sendmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        User user = null;
        try {
            user = new UserDAO().getById((int) session.getAttribute("user_id"));
            String html = createdHtml(request, response, user);
            if (convertirHtmlAPdf(html, rutaPDF)) {
                enviarCorreoConAdjunto(rutaPDF);
            }
        } catch (DAOException ex) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
            request.getRequestDispatcher("login.jsp").forward(request, response);

        }

    }

    private static String createdHtml(HttpServletRequest request, HttpServletResponse response, User user) {
        System.out.println("El mail del usuario logeado es " + user.getEmail());
        if (user.getEmail() != null) {
            mainreceptor = user.getEmail();
        }
        HttpSession session = request.getSession();
        List<Orderline> data = (List<Orderline>) session.getAttribute("orderlines");
        Object total = session.getAttribute("total");

        String itemsbodytable = "";

        for (Orderline object : data) {

            itemsbodytable += "<tr>"
                    + "<td> " + object.getProduct().getName() + "   </td>"
                    + "<td>" + object.getQuantity() + " </td>"
                    + "<td>$. " + object.getTotal() + "</td>"
                    + "<td>  0.00</td>"
                    + "<td>$. " + object.getTotal() + "</td>"
                    + "</tr>";

        }

        LocalDate fechaActual = LocalDate.now();

        String html = html = "<html lang=\"es\"><head>"
                + "<meta charset=\"UTF-8\"/>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>"
                + "<title>Factura de Venta</title>"
                + "<style>"
                + "body {"
                + "    font-family: Arial, sans-serif;"
                + "    margin: 0;"
                + "    padding: 0;"
                + "    background-color: #f4f4f4;"
                + "}"
                + ".invoice-container {"
                + "    max-width: 800px;"
                + "    margin: 20px auto;"
                + "    background-color: #fff;"
                + "    padding: 20px;"
                + "    border-radius: 8px;"
                + "    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);"
                + "}"
                + "h1, p {"
                + "    margin: 0;"
                + "    padding: 0;"
                + "}"
                + ".invoice-header {"
                + "    display: flex;"
                + "    justify-content: space-between;"
                + "    align-items: center;"
                + "    border-bottom: 2px solid #000;"
                + "    padding-bottom: 20px;"
                + "    margin-bottom: 20px;"
                + "}"
                + ".invoice-logo {"
                + "    width: 150px;"
                + "    height: auto;"
                + "}"
                + ".company-info {"
                + "    text-align: right;"
                + "}"
                + ".company-name {"
                + "    font-size: 24px;"
                + "    font-weight: bold;"
                + "}"
                + ".company-phone, .company-address {"
                + "    font-size: 14px;"
                + "}"
                + ".invoice-details {"
                + "    display: flex;"
                + "    justify-content: space-between;"
                + "    margin-bottom: 20px;"
                + "}"
                + ".invoice-id, .customer-info {"
                + "    width: 45%;"
                + "}"
                + ".invoice-table table {"
                + "    width: 100%;"
                + "    border-collapse: collapse;"
                + "    margin-bottom: 20px;"
                + "}"
                + ".invoice-table th, .invoice-table td {"
                + "    padding: 10px;"
                + "    text-align: left;"
                + "    border-bottom: 1px solid #ddd;"
                + "}"
                + ".invoice-table th {"
                + "    background-color: #f4f4f4;"
                + "}"
                + ".invoice-totals {"
                + "    margin-top: 20px;"
                + "}"
                + ".total-row {"
                + "    display: flex;"
                + "    justify-content: space-between;"
                + "    margin-bottom: 10px;"
                + "    font-size: 16px;"
                + "    font-weight: bold;"
                + "}"
                + ".total-row p {"
                + "    margin: 0;"
                + "}"
                + ".invoice-totals p {"
                + "    font-size: 18px;"
                + "    font-weight: bold;"
                + "}"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"invoice-container\">"
                + "<header class=\"invoice-header\">"
                + "<img src=\"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcToXfFSspPphgKWbG28I8OT4r7wjhg78tEdQ-vVTOhrXWNFzpUCLcUC-XfUGeYmU0L8uiU\" alt=\"Logo\" class=\"invoice-logo\"/>"
                + "<div class=\"company-info\">"
                + "<h1 class=\"company-name\">Mi Empresa S.A.</h1>"
                + "<p class=\"company-phone\">+51 123 456 789</p>"
                + "<p class=\"company-address\">Calle Ficticia 123, Lima, Perú</p>"
                + "</div>"
                + "</header>"
                + "<section class=\"invoice-details\">"
                + "<div class=\"invoice-id\">"
                + "<p><strong>COD Venta:</strong> OV01-001</p>"
                + "<p><strong>Fecha:</strong>  " + fechaActual + "</p>"
                + "</div>"
                + "<div class=\"customer-info\">"
                + "<p><strong>Cliente:</strong> " + user.getName() + " " + user.getLastname() + " </p>"
                + "<p><strong>Celular:</strong> +51  " + user.getPhone() + "</p>"
                + "</div>"
                + "</section>"
                + "<section class=\"invoice-table\">"
                + "<table>"
                + "<thead>"
                + "<tr>"
                + "<th>Item</th>"
                + "<th>Cant</th>"
                + "<th>Precio</th>"
                + "<th>Descuento</th>"
                + "<th>Total</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + itemsbodytable
                + "</tbody>"
                + "</table>"
                + "</section>"
                + "<section class=\"invoice-totals\">"
                + "<div class=\"total-row\">"
                + "<p><strong>Sub Total:</strong> $. " + total + " </p>"
                + "</div>"
                + "<div class=\"total-row\">"
                + "<p><strong>Descuento:</strong>  0.0</p>"
                + "</div>"
                + "<div class=\"total-row\">"
                + "<p><strong>IGV (0%):</strong> 0.0</p>"
                + "</div>"
                + "<div class=\"total-row\">"
                + "<p><strong>Total A Pagar:</strong> $. " + total + "</p>"
                + "</div>"
                + "</section>"
                + "</div>"
                + "</body></html>";

        System.out.println(html);
        System.out.println("---------------------------------------------------------");

        return html;
    }

    private final static String mailemisor = "loko2003elcrack@gmail.com";
    private final static String contraseña = "zvkl nunq yjpw fugp";
    private static String mainreceptor = "glodok2003elcrack@gmail.com";
    static String archivoPDF = "D:\\USUARIO\\Documentos\\95e94181-d076-42c0-bfca-426cd58dcfaf.pdf"; // Ruta del archivo PDF
    static String rutaPDF = "D:\\USUARIO\\Documentos\\boleta_de_compra.pdf"; // Ruta donde se guardará el PDF

    public static boolean convertirHtmlAPdf(String html, String rutaPDF) {
        try ( OutputStream os = new FileOutputStream(rutaPDF)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            System.out.println("PDF generado correctamente: " + rutaPDF);
            return true;
        } catch (Exception e) {
            System.out.println("Error al generar el PDF: " + e.getMessage());
            return false;
        }
    }

    public static void enviarCorreoConAdjunto(String rutaPDF) {
        // Configuración del servidor SMTP
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port", "587");

        // Crear sesión de correo
        Session sesion = Session.getInstance(propiedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailemisor, contraseña);
            }
        });

        try {
            // Crear mensaje
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(mailemisor));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mainreceptor));
            mensaje.setSubject("Boleta de Compra");
            mensaje.setText("Hola, se adjunta tu boleta de compra en formato PDF.");

            // Adjuntar archivo
            MimeBodyPart adjunto = new MimeBodyPart();
            adjunto.attachFile(new File(rutaPDF));

            // Cuerpo del correo
            MimeBodyPart texto = new MimeBodyPart();
            texto.setText("Hola,\n\nAdjuntamos tu boleta de compra.\n\nGracias por tu preferencia.");

            // Combinar texto y adjunto
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(texto);
            multipart.addBodyPart(adjunto);

            // Configurar el contenido del mensaje
            mensaje.setContent(multipart);

            // Enviar el mensaje
            Transport.send(mensaje);
            System.out.println("Correo enviado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
