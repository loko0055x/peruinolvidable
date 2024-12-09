package com.alexanderdoma.peruinolvidable.controllers;

import com.alexanderdoma.peruinolvidable.model.DAOException;
import com.alexanderdoma.peruinolvidable.model.entity.Order;
import com.alexanderdoma.peruinolvidable.model.entity.Orderline;
import com.alexanderdoma.peruinolvidable.model.entity.User;
import com.alexanderdoma.peruinolvidable.model.mysql.OrderDAO;
import com.alexanderdoma.peruinolvidable.model.mysql.UserDAO;
import com.alexanderdoma.peruinolvidable.services.PaymentService;
import com.google.gson.JsonObject;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet(name = "PaymentServlet", urlPatterns = {"/checkout", "/authorize_payment", "/execute_payment", "/review_payment", "/yapevalidation", "/redirectformYape", "/createorderYape"})
@MultipartConfig

public class PaymentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        switch (action) {
            case "/checkout":
                sendCheckoutPage(request, response);
                break;
            case "/review_payment":
                sendReviewPaymentPage(request, response);
                break;
            case "/redirectformYape":
                redirectformYape(request, response);
                break;

        }
    }

    private void sendCheckoutPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isLoggedIn(request) != true) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (doesHaveOrders(request) != true) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }

    private void sendReviewPaymentPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String paymentId = request.getParameter("paymentId");
        String payerId = request.getParameter("PayerID");
        PaymentService paymentServices = new PaymentService();
        Payment payment;
        try {
            payment = paymentServices.getPaymentDetails(paymentId);
            PayerInfo payerInfo = payment.getPayer().getPayerInfo();
            Transaction transaction = payment.getTransactions().get(0);
            ShippingAddress shippingAddress = transaction.getItemList().getShippingAddress();
            request.setAttribute("payer", payerInfo);
            request.setAttribute("transaction", transaction);
            request.setAttribute("shippingAddress", shippingAddress);
            String url = "review.jsp?paymentId=" + paymentId + "&PayerID=" + payerId;
            request.getRequestDispatcher(url).forward(request, response);
        } catch (PayPalRESTException ex) {
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("user_id") != null;
    }

    private boolean doesHaveOrders(HttpServletRequest request) {
        if (request.getSession().getAttribute("orderlines") == null) {
            return false;
        }
        List<Orderline> orderlines = (List<Orderline>) request.getSession().getAttribute("orderlines");
        return !orderlines.isEmpty();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();
        HttpSession session = request.getSession();
        switch (action) {
            case "/authorize_payment":
                authorizePayment(request, response);
                break;

            case "/execute_payment":
                executePayment(request, response);
                break;
            case "/yapevalidation":
                yapevalidation(request, response);
                break;
            case "/createorderYape": {
                createorderYape(request, response);

            }

            break;

        }
    }

    //---------------------------------------------------- REDIGIRIR PARA PAGAR CON PAYPAL//----------------------------------------------------
    private void authorizePayment(HttpServletRequest request, HttpServletResponse response) {
        List<Orderline> orderlines = getOrderlines(request);
        if (orderlines == null || getOrderlines(request).size() <= 0) {
            sendResponse(response, "error", "No hay pedidos en el carrito");
            return;
        }
        PaymentService paymentServices = new PaymentService();
        try {
            if (getUserObject(request) == null) {
                sendResponse(response, "error", "Debe iniciar sesión");
                return;
            }

            String approvalLink = paymentServices.authorizatePayment(getOrderlines(request), getUserObject(request));
            System.out.println("Link :" + approvalLink);

            sendResponse(response, "success", "Pedido generado correctamente", approvalLink);
        } catch (PayPalRESTException | DAOException ex) {
            sendResponse(response, "error", "Error al procesar pago", ex.getMessage());
        }
    }

    private void yapevalidation(HttpServletRequest request, HttpServletResponse response) {
        List<Orderline> orderlines = getOrderlines(request);
        if (orderlines == null || getOrderlines(request).size() <= 0) {
            sendResponse(response, "error", "No hay pedidos en el carrito");
            return;
        }
        try {
            if (getUserObject(request) == null) {
                sendResponse(response, "error", "Debe iniciar sesión");
                return;
            }
            sendResponse(response, "success", "Pedido generado correctamente", "redirectformYape");
        } catch (DAOException ex) {
            sendResponse(response, "error", "Error al procesar pago", ex.getMessage());
        }
    }

    private void redirectformYape(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            OrderDAO objOrderDAO = new OrderDAO();
            int user_id = (int) session.getAttribute("user_id");
            int correlative = objOrderDAO.getcount(user_id);
            LocalDate fechaActual = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy");
            LocalDate fechaAnterior = fechaActual.plusDays(1);

            request.setAttribute("currentdate", fechaActual.format(formatter));
            request.setAttribute("nextdate", fechaAnterior.format(formatter));
            // loadCartShopping(request);
            request.setAttribute("correlative", "OV01-00" + (correlative+1));
            request.getRequestDispatcher("yapeform.jsp").forward(request, response);
        } catch (ServletException | IOException | DAOException ex) {
            Logger.getLogger(PaymentServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String addstoreImage(HttpServletRequest request) {

        try {
            String rutaBD = "assets/img/storeYape/";

            String archivo = request.getServletContext().getRealPath("/"
                    + rutaBD);

            Part filePart = request.getPart("fileUpload");  // Obtener el archivo
            String fileName = filePart.getSubmittedFileName();  // Nombre del archivo

            System.out.println("" + fileName);
            System.out.println("La ruta seria :        " + archivo + fileName);
            filePart.write(archivo + fileName);  // Guardar el archivo
            return rutaBD + fileName;
        } catch (IOException | ServletException e) {
            System.out.println("Error " + e.getMessage());
            return "";
        }

    }

    private void createorderYape(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            OrderDAO objOrderDAO = new OrderDAO();
            int user_id = (int) session.getAttribute("user_id");
            int correlative = objOrderDAO.getcount(user_id);
            sendmail(request, response, correlative);
            createOrder(request, "YAPE");

            session.removeAttribute("orderlines");

            request.setAttribute("maincolor", "#8e24aa");
            request.setAttribute("secondarycolor", "#c2185b");
            request.setAttribute("photo", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQm6pdOp6fVfF9R5ArvkMOsht1f3BsFMvR8fLY78W8DquUT3Fs03UP5QNjPYQ4tBm70eN8");
            request.setAttribute("paymentType", "Yape");
            request.getRequestDispatcher("transaction-success.jsp").forward(request, response);

        } catch (DAOException | ServletException | IOException ex) {
            Logger.getLogger(PaymentServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //AQUIIIIIIII CREA LA ORDEN  Y DETALLE DE ORDEN
    //---------------------------------------------------- REDIGIRIR PARA CREAR LA ORDEN //----------------------------------------------------
    private void executePayment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            String paymentId = request.getParameter("paymentId");
            String payerId = request.getParameter("PayerID");
            PaymentService paymentServices = new PaymentService();
            Payment payment = paymentServices.executePayment(paymentId, payerId);
            PayerInfo payerInfo = payment.getPayer().getPayerInfo();
            Transaction transaction = payment.getTransactions().get(0);
            request.setAttribute("payer", payerInfo);
            request.setAttribute("transaction", transaction);
            createOrder(request, "PAYPAL");

            OrderDAO objOrderDAO = new OrderDAO();
            int user_id = (int) session.getAttribute("user_id");
            int correlative = objOrderDAO.getcount(user_id);

            sendmail(request, response, correlative);
            session.removeAttribute("orderlines");

            request.setAttribute("maincolor", "#012168");
            request.setAttribute("secondarycolor", "#009adc");
            request.setAttribute("photo", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTEJBg1uzMXYl0R2WMDCM314vJqXOquuHp8Pw&s");
            request.setAttribute("paymentType", "Paypal");
            request.getRequestDispatcher("transaction-success.jsp").forward(request, response);
        } catch (DAOException | PayPalRESTException ex) {
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private void createOrder(HttpServletRequest request, String paymentType) throws DAOException {
        OrderDAO objOrderDAO = new OrderDAO();
        HttpSession session = request.getSession();
        List<Orderline> orderlines = (List<Orderline>) session.getAttribute("orderlines");
        int user_id = (int) session.getAttribute("user_id");
        Order objOrder = new Order();
        objOrder.setUser(new UserDAO().getById(user_id));
        objOrder.setSubtotal((double) session.getAttribute("total"));
        objOrder.setTotal((double) session.getAttribute("total"));
        objOrder.setDate(Date.valueOf(LocalDate.now()));
        objOrder.setStatus("PENDING");
        objOrder.setPaymentType(paymentType);
        if (paymentType.equals("PAYPAL")) {
            objOrder.setPayment_id(request.getParameter("paymentId"));
        } else {
            objOrder.setPhoto(addstoreImage(request));
        }
        objOrderDAO.add(objOrder, orderlines);
    }

    private User getUserObject(HttpServletRequest request) throws DAOException {
        Object user_id = request.getSession().getAttribute("user_id");
        if (user_id == null) {
            return null;
        }
        return new UserDAO().getById(Integer.parseInt(user_id.toString()));
    }

    //Send a json object as response
    private void sendResponse(HttpServletResponse response, String type, String tittle) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("tittle", tittle);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
        } catch (IOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Send a json object as response
    private void sendResponse(HttpServletResponse response, String type, String tittle, String message) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("tittle", tittle);
            json.addProperty("message", message);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
            System.out.println("JSON :" + json);
        } catch (IOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<Orderline> getOrderlines(HttpServletRequest request) {
        List<Orderline> orderlines = (List<Orderline>) request.getSession().getAttribute("orderlines");
        if (orderlines == null) {
            return null;
        }
        return orderlines;
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    //------------------------------------------------------------------------------------
    private static void sendmail(HttpServletRequest request, HttpServletResponse response, int correlative) throws ServletException, IOException {
        HttpSession session = request.getSession();

        User user = null;
        try {
            user = new UserDAO().getById((int) session.getAttribute("user_id"));
            String html = createdHtml(request, response, user, correlative);
            if (convertirHtmlAPdf(html, rutaPDF)) {
                enviarCorreoConAdjunto(rutaPDF);
            }
        } catch (DAOException ex) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
            request.getRequestDispatcher("login.jsp").forward(request, response);

        }

    }

    private static String createdHtml(HttpServletRequest request, HttpServletResponse response, User user, int correlative) {
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
                + "<p class=\"company-phone\">+51 999 456 789</p>"
                + "<p class=\"company-address\">Calle Ficticia 123, Lima, Perú</p>"
                + "</div>"
                + "</header>"
                + "<section class=\"invoice-details\">"
                + "<div class=\"invoice-id\">"
                + "<p><strong>COD Venta:</strong> OV01-00" + correlative + "</p>"
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
