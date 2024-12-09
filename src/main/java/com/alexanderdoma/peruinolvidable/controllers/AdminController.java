package com.alexanderdoma.peruinolvidable.controllers;

import com.alexanderdoma.peruinolvidable.model.DAOException;
import com.alexanderdoma.peruinolvidable.model.entity.Category;
import com.alexanderdoma.peruinolvidable.model.entity.Product;
import com.alexanderdoma.peruinolvidable.model.entity.Admin;
import com.alexanderdoma.peruinolvidable.model.entity.Order;
import com.alexanderdoma.peruinolvidable.model.entity.User;
import com.alexanderdoma.peruinolvidable.model.mysql.AdminDAO;
import com.alexanderdoma.peruinolvidable.model.mysql.CategoryDAO;
import com.alexanderdoma.peruinolvidable.model.mysql.OrderDAO;
import com.alexanderdoma.peruinolvidable.model.mysql.OrderlineDAO;
import com.alexanderdoma.peruinolvidable.model.mysql.ProductDAO;
import com.alexanderdoma.peruinolvidable.model.mysql.UserDAO;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@WebServlet(name = "AdminController", urlPatterns = {"/admin", "/admin/dashboard", "/admin/products", "/admin/product", "/admin/product/new", "/admin/categories", "/admin/category", "/admin/category/new", "/admin/sales", "/admin/sale", "/admin/session"})
@MultipartConfig
public class AdminController extends HttpServlet {

    ProductDAO productServices = new ProductDAO();
    CategoryDAO categoryServices = new CategoryDAO();
    AdminDAO adminServices = new AdminDAO();
    OrderDAO orderServices = new OrderDAO();
    OrderlineDAO orderlineServices = new OrderlineDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/admin":
                request.getRequestDispatcher("admin/login.jsp").forward(request, response);
                break;

            case "/admin/dashboard":
                loadDashboard(request);
                request.getRequestDispatcher("dashboard.jsp").forward(request, response);
                break;

            case "/admin/products":
                loadProducts(request);
                request.getRequestDispatcher("products/products.jsp").forward(request, response);
                break;

            case "/admin/product":
                loadProduct(request);
                request.getRequestDispatcher("products/product.jsp").forward(request, response);
                break;
            case "/admin/product/new":
                loadCategories(request);
                request.getRequestDispatcher("../products/newproduct.jsp").forward(request, response);
                break;
            case "/admin/categories":
                loadCategories(request);
                request.getRequestDispatcher("categories/categories.jsp").forward(request, response);
                break;

            case "/admin/category":
                loadCategory(request);
                request.getRequestDispatcher("categories/category.jsp").forward(request, response);
                break;

            case "/admin/category/new":
                request.getRequestDispatcher("../categories/newcategory.jsp").forward(request, response);
                break;
            case "/admin/sales":
                loadOrders(request, response);
                break;

            case "/admin/sale":
                loadOrder(request, response);
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();
        switch (action) {
            case "/admin/session":
                login(request, response);
                break;

            case "/admin/category":
                createCategory(request, response);
                break;

            case "/admin/product":
                createProduct(request, response);
                break;
            case "/admin/filters":
                System.out.println("ds");
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();
        switch (action) {
            case "/admin/product":
                updateProduct(request, response);
                break;

            case "/admin/category":
                updateCategory(request, response);
                break;

            case "/admin/sale":
                updateStateOrder(request, response);
                break;

        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        switch (action) {
            case "/admin/product":
                deleteProduct(request, response);
                break;
            case "/admin/category":
                deleteCategory(request, response);
                break;
        }
    }

    private void updateStateOrder(HttpServletRequest request, HttpServletResponse response) {
        String order_state = request.getParameter("sale_state");
        int order_id = Integer.parseInt(request.getParameter("sale_id"));

        String titulo = request.getParameter("orderTitle");
        String mensaje = request.getParameter("mensajes");
        System.out.println("-------------------------");
        System.out.println("Entro a actualizar");
        System.out.println("sale statae" + order_state);
        System.out.println("titulox" + titulo);
        System.out.println("mensaje" + mensaje);

        try {
            UserDAO dao = new UserDAO();
            User usuario = dao.getUserforOrder(order_id);

            orderServices.updateOrderState(order_state, order_id);

            sendJsonResponse(response, "success", "Operación realizada exitosamente", "Orden actualizada ");
            String nombres = usuario.getName() + "  " + usuario.getLastname();
            String body = order_state.equals("COMPLETED") ? "Hola " + nombres + ", Le informamos que su pedido " + order_id + " fue aceptado " : "Hola " + nombres + ", Le informamos que su pedido " + order_id + " fue Rechazado ";
            sendsaludo(usuario.getEmail(), body);
        } catch (DAOException ex) {
            sendJsonResponse(response, "error", "Error al actualizar orden", ex.getMessage());
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            Admin objAdmin = adminServices.login(username, password);
            if (objAdmin == null) {
                sendJsonResponse(response, "error", "Error al iniciar sesión", "Credenciales inválidas");
            }
            request.getSession().setAttribute("admin_id", objAdmin.getId());
            sendJsonResponse(response, "success", "Operación exitosa", "Bienvenido al sistema");
        } catch (DAOException ex) {
            sendJsonResponse(response, "error", "Error al iniciar sesión", ex.getMessage());
        }
    }

    private void loadDashboard(HttpServletRequest request) {
        try {
            request.setAttribute("products", productServices.getAll());
            request.setAttribute("categories", categoryServices.getAll());
        } catch (DAOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadProducts(HttpServletRequest request) {
        try {
            request.setAttribute("products", productServices.getAll());
        } catch (DAOException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadCategories(HttpServletRequest request) {
        try {
            request.setAttribute("categories", categoryServices.getAll());
        } catch (DAOException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadOrders(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            String stardate = "indefinido";
            String endDate = "indefinido";
            String paymentType = "indefinido";

            if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty()) {
                stardate = request.getParameter("startDate");
                endDate = request.getParameter("endDate");
                session.setAttribute("stardate", stardate);
                session.setAttribute("endDate", endDate);
            }
            if (request.getParameter("orderStatus") != null && request.getParameter("orderStatus") != "-1") {
                paymentType = request.getParameter("orderStatus");
                session.setAttribute("paymentType", paymentType);

            }

            /*   System.out.println("Fecha inicio :" + stardate);
            System.out.println("Fecha fin :" + endDate);
            System.out.println("orden :" + paymentType);

            System.out.println("FINAL :" + session.getAttribute("stardate"));*/
            request.setAttribute("orders", orderServices.getallorderparameter(stardate, endDate, paymentType));
            request.getRequestDispatcher("sales/sales.jsp").forward(request, response);
        } catch (DAOException | IOException | ServletException e) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void loadOrder(HttpServletRequest request, HttpServletResponse response) {
        try {
            int order_id = Integer.parseInt(request.getParameter("order_id"));
            Order order = orderServices.getById(order_id);

            request.setAttribute("order", order);
            request.setAttribute("orderlines", orderlineServices.getOrderlineByOrder(order_id));
            request.getRequestDispatcher("sales/sale.jsp").forward(request, response);
        } catch (IOException | NumberFormatException | ServletException | DAOException e) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void loadProduct(HttpServletRequest request) {
        try {
            int product_id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("product", productServices.getById(product_id));
            request.setAttribute("categories", categoryServices.getAll());
        } catch (DAOException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadCategory(HttpServletRequest request) {
        try {
            int category_id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("category", categoryServices.getById(category_id));
        } catch (DAOException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response) {
        try {
            productServices.update(getProduct(request));
            sendJsonResponse(response, "success", "Operación exitosa", "Producto actualizado correctamente");
        } catch (DAOException ex) {
            sendJsonResponse(response, "error", "Error al actualizar producto", ex.getMessage());
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response) {
        try {
            categoryServices.update(getCategory(request));
            sendJsonResponse(response, "success", "Operación exitosa", "Categoria actualizada correctamente");
        } catch (DAOException ex) {
            sendJsonResponse(response, "error", "Error al actualizar categoría", ex.getMessage());
        }
    }

    private void createProduct(HttpServletRequest request, HttpServletResponse response) {
        try {
            productServices.add(getProduct(request));
            sendJsonResponse(response, "success", "Operación exitosa", "Producto creado correctamente");
        } catch (DAOException ex) {
            sendJsonResponse(response, "error", "Error al crear producto", ex.getMessage());
        }
    }

    private void createCategory(HttpServletRequest request, HttpServletResponse response) {
        try {
            categoryServices.add(getCategory(request));
            sendJsonResponse(response, "success", "Operación exitosa", "Categoria creada correctamente");
        } catch (DAOException ex) {
            sendJsonResponse(response, "error", "Error al crear categoria", ex.getMessage());
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) {
        try {
            productServices.delete(Integer.parseInt(request.getParameter("product_id")));
            sendJsonResponse(response, "success", "Operación exitosa", "Producto eliminado correctamente");
        } catch (DAOException ex) {
            sendJsonResponse(response, "error", "Error al eliminar producto", ex.getMessage());
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) {
        try {
            categoryServices.delete(Integer.parseInt(request.getParameter("category_id")));
            sendJsonResponse(response, "success", "Operación exitosa", "Categoria eliminada correctamente");
        } catch (DAOException ex) {
            sendJsonResponse(response, "error", "Error al eliminar categoria", ex.getMessage());
        }
    }

    private Product getProduct(HttpServletRequest request) {
        Product objProduct = new Product();
        if (request.getParameter("id") != null) {
            objProduct.setId(Integer.parseInt(request.getParameter("id")));
        }
        objProduct.setName(request.getParameter("name"));
        objProduct.setDescription(request.getParameter("description"));
        objProduct.setPrice(Double.parseDouble(request.getParameter("price")));
        objProduct.setBrand(request.getParameter("brand"));
        objProduct.setStock(Integer.parseInt(request.getParameter("stock")));
        objProduct.setState(request.getParameter("state"));

        //objProduct.setImage(request.getParameter("image"));
        objProduct.setImage(addstoreImage(request));
        Category objCategory = new Category();
        objCategory.setId(Integer.parseInt(request.getParameter("category_id")));
        objProduct.setCategory(objCategory);
        return objProduct;
    }

    private Category getCategory(HttpServletRequest request) {
        Category objCategory = new Category();
        if (request.getParameter("id") != null) {
            objCategory.setId(Integer.parseInt(request.getParameter("id")));
        }
        objCategory.setName(request.getParameter("name"));
        return objCategory;
    }

    private void sendJsonResponse(HttpServletResponse response, String type, String tittle, String message) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("tittle", tittle);
            json.addProperty("message", message);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
        } catch (IOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String addstoreImage(HttpServletRequest request) {
        try {
            String rutaBD = "assets/img/productsimages/";

            String archivo = request.getServletContext().getRealPath("/"
                    + rutaBD);

            Part filePart = request.getPart("image");  // Obtener el archivo
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

    private final static String mailemisor = "loko2003elcrack@gmail.com";
    private final static String contraseña = "zvkl nunq yjpw fugp";

    public static void sendsaludo(String mainreceptor, String text) {

        // Propiedades del servidor SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Crear una sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailemisor, contraseña);
            }
        });

        try {
            // Crear el mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(mainreceptor));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mainreceptor));
            mensaje.setSubject("Peru Inolvidable");
            mensaje.setText(text);

            // Enviar el mensaje
            Transport.send(mensaje);

            System.out.println("Correo enviado exitosamente.");
        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>@
}
