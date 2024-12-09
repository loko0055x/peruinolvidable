<%@page import="com.alexanderdoma.peruinolvidable.model.entity.Order"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<!DOCTYPE html>
<!DOCTYPE html>
<html lang="es">
    <head>

        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Detalles de Pedido</title>
        <!-- Estilos de Bootstrap -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
        <!-- FontAwesome para iconos -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" rel="stylesheet" />
        <!-- Toastr -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css" rel="stylesheet" />
        <!-- Estilos personalizados -->
        <style>
            body {
                font-family: 'Roboto', sans-serif;
                background-color: #f4f5f7;
                color: #333;
            }
            .container {
                margin: auto;
                padding: 20px;
            }
            .card {
                border-radius: 15px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                background: linear-gradient(145deg, #ffffff, #e6e6e6);
            }
            .card-title {
                font-size: 1.8rem;
                font-weight: bold;
                color: #007bff;
                text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
            }
            .card-subtitle {
                font-size: 1.2rem;
                color: #6c757d;
            }
            .table th {
                background-color: #007bff;
                color: #fff;
                text-align: center;
            }
            .table td {
                text-align: center;
            }
            .btn-primary {
                background-color: #007bff;
                border: none;
                font-size: 1rem;
                padding: 0.6rem 1.2rem;
                transition: background-color 0.3s;
            }
            .btn-primary:hover {
                background-color: #0056b3;
            }
            .image-container {
                display: flex;
                justify-content: center;  /* Centra la imagen horizontalmente */
                align-items: center;      /* Centra la imagen verticalmente */
                margin-top: 20px;
                width: 70%;                /* Reduce el ancho del contenedor (ajustable) */
                max-width: 400px;          /* Limita el ancho máximo del contenedor */
                height: 400px;             /* Ajusta la altura del contenedor */
                margin-left: auto;
                margin-right: auto;
                border: 3px solid #007bff;
                border-radius: 10px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                overflow: hidden;          /* Asegura que la imagen no se salga del contenedor */
            }

            .image-container img {
                max-width: 100%;
                max-height: 100%;
                object-fit: contain;       /* Asegura que la imagen se ajuste dentro del contenedor */
            }
            .icon-eye {
                font-size: 1.5rem;
                cursor: pointer;
                color: #007bff;
                transition: color 0.3s;
            }
            .icon-eye:hover {
                color: #0056b3;
            }
        </style>
    </head>
    <body>
        <%
            Order orden = (Order) request.getAttribute("order");

            String photoUrl = orden == null ? "NULL" : (String) orden.getPhoto();
            photoUrl = "http://localhost:8080/peruinolvidable/" + photoUrl;
            orden = orden == null ? new Order() : (Order) request.getAttribute("order");
        %>
        <%@ include file="../../components/admin-header.jsp" %>

        <div class="container mt-5">
            <div class="row justify-content-center">
                <div class="col-md-10">
                    <div class="card p-4">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h3 class="card-title">Detalles de pedido ${order.getId()}</h3>
                                <div>
                                    <%
                                        if (orden.getPaymentType().equals("YAPE")) {

                                         
                                    %> 
                                    <button id="previewButton" class="btn btn-primary">
                                        <i class="fa fa-eye icon-eye"></i> Ver Imagen
                                    </button>
                                    <button id="downloadButton" class="btn btn-primary">
                                        <i class="fa fa-download icon-eye"></i> Descargar Imagen
                                    </button>
                                    <%
                                        }
                                    %>

                                </div>
                            </div>
                            <h4 class="card-subtitle mb-3">Productos en este pedido:</h4>
                            <table class="table table-bordered">
                                <thead>
                                    <tr>
                                        <th>Nombre</th>
                                        <th>Marca</th>
                                        <th>Precio</th>
                                        <th>Cantidad</th>
                                        <th>Total</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${orderlines}" var="orderline">
                                        <tr>
                                            <td>${orderline.product.getName()}</td>
                                            <td>${orderline.product.getBrand()}</td>
                                            <td>${orderline.product.getPrice()} USD</td>
                                            <td>${orderline.getQuantity()} unidades</td>
                                            <td>${orderline.getTotal()} USD</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="image-container" id="imageContainer" style="display: none;">
                                <img id="image" src="" alt="Imagen del pedido">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>

        <script>
            // URL de la imagen (puedes obtenerla dinámicamente desde el servidor)
            const imageUrl = "<%=photoUrl%>";

            const previewButton = document.getElementById("previewButton");
            const downloadButton = document.getElementById("downloadButton");
            const imageContainer = document.getElementById("imageContainer");
            const image = document.getElementById("image");

            previewButton.addEventListener("click", function () {
                image.src = imageUrl;
                imageContainer.style.display = "flex";
            });

            downloadButton.addEventListener("click", function () {
                fetch(imageUrl)
                        .then(response => response.blob())
                        .then(blob => {
                            const url = window.URL.createObjectURL(blob);
                            const a = document.createElement('a');
                            a.style.display = 'none';
                            a.href = url;
                            a.download = 'imagen_pedido.jpg'; // Nombre del archivo de descarga
                            document.body.appendChild(a);
                            a.click();
                            window.URL.revokeObjectURL(url);
                        })
                        .catch(() => alert('Error al descargar la imagen.'));
            });
        </script>
    </body>
</html>




