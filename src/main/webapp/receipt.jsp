<%@page import="java.time.LocalDate"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Payment Receipt</title>
        <!--STYLES-->
        <link rel="stylesheet" href="assets/css/styles.css"/>
        <!--CDN IMPLEMENTS-->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css" integrity="sha512-vKMx8UnXk60zUwyUnUPM3HbQo8QfmNx7+ltw8Pm5zLusl1XIfwcxo8DbWCqMGKaWeNxWA8yrx5v3SaVpMvR3CA==" crossorigin="anonymous" referrerpolicy="no-referrer" />        
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <style type="text/css">
            table {
                border: 0;
            }
            table td {
                padding: 5px;
            }
        </style>
    </head>
    <body>
        <%@include file="components/header.jsp" %>
        <!--        <div align="center">
                    <h1>Payment Done. Thank you for purchasing our products</h1>
                    <br/>
                    <h2>Receipt Details:</h2>
                    <table>
                        <tr>
                            <td><b>Merchant:</b></td>
                            <td>Company ABC Ltd.</td>
                        </tr>
                        <tr>
                            <td><b>Payer:</b></td>
                            <td>${payer.firstName} ${payer.lastName}</td>      
                        </tr>
                        <tr>
                            <td><b>Description:</b></td>
                            <td>${transaction.description}</td>
                        </tr>
                        <tr>
                            <td><b>Subtotal:</b></td>
                            <td>${transaction.amount.details.subtotal} USD</td>
                        </tr>
                        <tr>
                            <td><b>Shipping:</b></td>
                            <td>${transaction.amount.details.shipping} USD</td>
                        </tr>
                        <tr>
                            <td><b>Tax:</b></td>
                            <td>${transaction.amount.details.tax} USD</td>
                        </tr>
                        <tr>
                            <td><b>Total:</b></td>
                            <td>${transaction.amount.total} USD</td>
                        </tr>                    
                    </table>
                </div>-->

        <div class="container">
            <div class="row">
                <div class="col-md-6">   

                    <strong>Perú Inolvidable</strong><br>
                    Dirección: Calle Lima Peru<br>
                    Teléfono: 924 555 452
                </div>
                <div class="col-md-6 text-right">
                    <strong>Recibo #12345</strong><br>
                     Fecha:  <%=LocalDate.now()%><br>
                    Cliente: ${payer.firstName} ${payer.lastName}
                </div>
            </div>

            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Concepto</th>
                        <th>Cantidad</th>
                        <th>Precio Unitario</th>
                        <th>Subtotal</th>
                    </tr>
                </thead>
                <tbody>


                    <c:forEach items="${orderlines}" var = "orderline"> 
                        <tr>
                            <td>${orderline.getProduct().getName()}</td>
                            <td>$ ${orderline.getQuantity()}</td>
                            <td>$ ${orderline.getProduct().getPrice()}</td>

                            ${orderline.setTotal(orderline.getQuantity() * orderline.getProduct().getPrice())}

                            <td>$ ${orderline.getTotal()}</td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3">Total</td>
                        <td>${transaction.amount.total} USD</td>
                    </tr>
                </tfoot>
            </table>

            <div>
                <p>Gracias por su compra.</p>
                <p>Este recibo es un comprobante de pago.</p>
            </div>
        </div>

        <%@include file="components/footer.jsp" %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js" integrity="sha512-v2CJ7UaYy4JwqLDIrZUI/4hqeoQieOmAZNXBeQyjo21dadnwR+8ZaIJVT8EE2iyI61OV8e6M8PP2/4hpQINQ/g==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
        <script src="assets/js/toastr.min.js"></script>
        <script src="assets/js/notifications.js"></script>
    </body>
</html>