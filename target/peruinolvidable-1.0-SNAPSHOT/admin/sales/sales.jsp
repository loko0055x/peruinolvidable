<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    boolean state = session.getAttribute("paymentType") != null;
%>
<html>
    <head>  
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>JSP Page</title>
        <!--CDN IMPLEMENTS-->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css" integrity="sha512-vKMx8UnXk60zUwyUnUPM3HbQo8QfmNx7+ltw8Pm5zLusl1XIfwcxo8DbWCqMGKaWeNxWA8yrx5v3SaVpMvR3CA==" crossorigin="anonymous" referrerpolicy="no-referrer" />        
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg==" crossorigin="anonymous" referrerpolicy="no-referrer" /> 
        <style>
            /* Estilos personalizados */
            body {
                font-family: 'Arial', sans-serif;
                background-color: #f8f9fa;
            }
            .card {
                border-radius: 15px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            }
            .card-body {
                padding: 2rem;
            }
            h3.card-title {
                font-size: 1.8rem;
                color: #343a40;
                font-weight: bold;
            }
            h4.card-subtitle {
                font-size: 1.2rem;
                color: #6c757d;
            }
            .form-label {
                font-weight: 500;
            }
            .form-select, .form-control {
                border-radius: 8px;
                padding: 0.75rem;
            }
            .btn-custom {
                border-radius: 25px;
                padding: 0.6rem 2rem;
                font-size: 1rem;
                font-weight: bold;
                transition: background-color 0.3s ease;
            }
            .btn-custom:hover {
                background-color: #28a745;
                color: white;
            }
            .btn-clear {
                background-color: #ffc107;
                border-color: #ffc107;
            }
            .btn-clear:hover {
                background-color: #e0a800;
                border-color: #e0a800;
            }
            .filter-row {
                margin-bottom: 2rem;
            }
            .d-flex-center {
                display: flex;
                justify-content: center;
                gap: 1rem;
            }

            /* Estilo de la tabla */
            .table-bordered {
                border: 1px solid #dee2e6;
            }
            .table th, .table td {
                vertical-align: middle;
                text-align: center;
                padding: 0.75rem;  /* Ajuste de altura de fila */
            }
            .table th {
                background-color: #f8f9fa;
                color: #495057;
                border-top: 2px solid #dee2e6;
                font-weight: bold;
            }
            .table td {
                background-color: #ffffff;
                border-top: 1px solid #dee2e6;
            }
            .table-striped tbody tr:nth-child(odd) {
                background-color: #f2f2f2;
            }
            .table-striped tbody tr:nth-child(even) {
                background-color: #ffffff;
            }
            .table-hover tbody tr:hover {
                background-color: #e9ecef;
                cursor: pointer;
            }
            .table td .btn {
                border-radius: 50%;
                padding: 0.5rem;
            }
        </style>
        <style>
            .iframe-container {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 1000;
            }
            .iframe-modal {
                background: #fff;
                padding: 20px;
                border-radius: 8px;
                width: 400px;
                text-align: center;
            }
            .iframe-modal .form-group {
                margin-bottom: 15px;
            }
        </style>
    </head>
    <body>
        <%@include file="../../components/admin-header.jsp" %> 
        <main class="container d-flex justify-content-center mt-3"s style="min-height: 80vh">
            <div class="card" style="width: 1000px">
                <div class="card-body">
                    <h3 class="card-tittle">Registro de pedidos</h3>
                    <h4 class="card-subtittle text-body-secondary mb-3">Seleccione un pedido para ver los detalles</h4>
                    <div class="filter-row row">
                        <div class="col-md-4">
                            <label for="startDate" class="form-label">Fecha de Inicio</label>
                            <input type="date" class="form-control" id="startDate" name="startDate" 
                                   value="">
                        </div>
                        <div class="col-md-4">
                            <label for="endDate" class="form-label">Fecha Final</label>
                            <input type="date" class="form-control" id="endDate" name="endDate" disabled>
                        </div>
                        <div class="col-md-4">
                            <label for="orderStatus" class="form-label">Estado del Pedido</label>
                            <select class="form-select" id="orderStatus" name="combo">

                                <option <%= state && session.getAttribute("paymentType").equals("-1") ? "selected" : ""%>value="-1">Seleccione un estado</option>
                                <option  <%=state && session.getAttribute("paymentType").equals("YAPE") ? "selected" : ""%> value="YAPE">   YAPE</option>
                                <option  <%=state && session.getAttribute("paymentType").equals("PAYPAL") ? "selected" : ""%> value="PAYPAL">PAYPAL</option>
                            </select>
                        </div>
                    </div>

                    <!-- Botones Limpiar y Buscar -->
                    <div class="d-flex-center">
                        <button class="btn btn-clear" onclick="limpiar()" id="clearFilters">Limpiar</button>

                        <div class="d-flex-center">
                            <button onclick="data()" class="btn btn-custom" id="searchOrders">Buscar</button>
                        </div>                        
                        <script>
                            function limpiar() {
                                var url = '/peruinolvidable/admin/sales'

                                // Redirigir a la página con los parámetros
                                console.log(url); // Para verificar la URL generada
                                window.location.href = url;
                            }
                            function data() {
                                // Obtener los valores de los campos
                                var startDate = document.getElementById('startDate').value;
                                var endDate = document.getElementById('endDate').value;
                                var orderStatus = document.getElementById('orderStatus').value;

                                // Validar que si la fecha de inicio está seteada, la fecha final también debe estarlo
                                if (startDate && !endDate) {
                                    alert("Por favor, ingrese una fecha final.");
                                    return; // Detener la ejecución si la fecha final no está seteada
                                }

                                // Validar que al menos uno de los campos esté lleno
                                if (!startDate && !endDate && !orderStatus) {
                                    alert("Por favor, rellene al menos un campo.");
                                    return; // Detener la ejecución si todos los campos están vacíos
                                }

                                // Construir la URL con los parámetros
                                var url = '/peruinolvidable/admin/sales?startDate=' + encodeURIComponent(startDate) +
                                        '&endDate=' + encodeURIComponent(endDate) +
                                        '&orderStatus=' + encodeURIComponent(orderStatus);

                                // Redirigir a la página con los parámetros
                                console.log(url); // Para verificar la URL generada
                                window.location.href = url;
                            }
                        </script>
                    </div>

                    <table class="table table-bordered table-striped table-hover mt-4">
                        <tr>
                            <th>N° Orden</th>
                            <th>Fecha</th>
                            <th>Total</th>
                            <th>Comprador</th>
                            <th>Estado del pedido</th>
                            <th>Tipo de Pago</th>
                            <th>Accion</th>
                        </tr>
                        <c:forEach items="${orders}" var="order">
                            <tr>
                                <td>${order.getId()}</td>
                                <td>${order.getDate()}</td>
                                <td>${order.getSubtotal()} USD</td>
                                <td>${order.user.getName()}</td>
                                <td>${order.paymentType}</td>
                                <td>
                                    <p class="p-2 rounded-4
                                       <c:if test="${order.getStatus() == 'COMPLETED'}">bg-success</c:if>
                                       <c:if test="${order.getStatus() == 'PENDING'}">bg-warning</c:if>
                                       <c:if test="${order.getStatus() == 'CANCELLED'}">bg-danger</c:if>
                                           ">
                                       ${order.getStatus()}
                                    <p>
                                </td>
                                <td>
                                    <div class="d-flex gap-1">
                                        <c:if test="${order.getStatus() != 'COMPLETED' && order.getStatus() != 'CANCELLED'}">
                                            <a onclick="updateOrder('CANCELLED', ${order.getId()})" class="btn btn-danger border">
                                                <i class="fa-solid fa-xmark"></i>
                                            </a>
                                            <a onclick="updateOrder('COMPLETED', ${order.getId()})" class="btn btn-success border">
                                                <i class="fa-solid fa-check"></i>
                                            </a>
                                        </c:if>
                                        <a href="sale?order_id=${order.getId()}" class="btn btn-light border"><i class="fa-solid fa-eye"></i></a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
        </main>
        <footer class="py-5 bg-dark mt-3">
            <div class="container">
                <p class="m-0 text-center text-white">Copyright &copy; Perú Inolvidable 2024</p>
            </div>
        </footer>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js" integrity="sha512-v2CJ7UaYy4JwqLDIrZUI/4hqeoQieOmAZNXBeQyjo21dadnwR+8ZaIJVT8EE2iyI61OV8e6M8PP2/4hpQINQ/g==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js" integrity="sha512-VEd+nq25CkR676O+pLBnDW09R7VQX9Mdiij052gVCp5yVH3jGtH70Ho/UUv4mJDsEdTvqRCFZg0NKGiojGnUCw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
        <script src="../assets/js/notifications.js"></script>
        <script src="../assets/js/main.js"></script>
        <script src="../assets/js/adminMethods.js"></script>
        <script>
                                                // Deshabilitar la fecha final hasta que se seleccione la fecha de inicio
                                                document.getElementById('startDate').addEventListener('change', function () {
                                                    const startDate = document.getElementById('startDate').value;
                                                    const endDate = document.getElementById('endDate');

                                                    if (startDate) {
                                                        endDate.disabled = false;
                                                    } else {
                                                        endDate.disabled = true;
                                                        endDate.value = ''; // Limpiar la fecha final cuando se deshabilita
                                                    }
                                                });

                                                // Validación para asegurarse de que la fecha de fin no sea anterior a la de inicio
                                                document.getElementById('endDate').addEventListener('change', function () {
                                                    const startDate = new Date(document.getElementById('startDate').value);
                                                    const endDate = new Date(document.getElementById('endDate').value);

                                                    if (endDate < startDate) {
                                                        toastr.error('La fecha final no puede ser menor que la fecha de inicio.');
                                                        document.getElementById('endDate').value = ''; // Limpiar la fecha final
                                                    }
                                                });

                                                document.getElementById('clearFilters').addEventListener('click', function () {
                                                    document.getElementById('startDate').value = '';
                                                    document.getElementById('endDate').value = '';
                                                    document.getElementById('orderStatus').value = '';
                                                });

                                                document.getElementById('searchOrders').addEventListener('click', function () {
                                                    const startDate = document.getElementById('startDate').value;
                                                    const endDate = document.getElementById('endDate').value;
                                                    const orderStatus = document.getElementById('orderStatus').value;
                                                    // Aquí iría la lógica para buscar los pedidos con los filtros seleccionados
                                                });
        </script>
    </body>
</html>
