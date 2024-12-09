<%-- 
    Document   : yapeform
    Created on : 7 dic. 2024, 15:06:14
    Author     : Windows 10
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.alexanderdoma.peruinolvidable.model.entity.Orderline"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Resumen de Pedido</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
        <style>
            body {
                font-family: 'Poppins', sans-serif;
                background-color: #f9f9f9;
            }

            .container {
                max-width: 800px;
                margin: 50px auto;
                background-color: #ffffff;
                border-radius: 10px;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
                padding: 30px;
            }

            .header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 30px;
            }

            .header h1 {
                font-size: 24px;
                color: #6a1b9a;
                font-weight: 600;
            }

            .order-info {
                margin-bottom: 20px;
            }

            .order-info span {
                font-weight: bold;
                color: #555;
            }

            .order-info p {
                font-size: 16px;
                color: #888;
            }

            .product-list {
                margin-bottom: 30px;
            }

            .product-item {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 10px 0;
                border-bottom: 1px solid #f1f1f1;
            }

            .product-item img {
                width: 80px;
                height: 80px;
                border-radius: 8px;
            }

            .product-info {
                flex: 1;
                margin-left: 15px;
            }

            .product-info h5 {
                font-size: 18px;
                color: #333;
                margin-bottom: 5px;
            }

            .product-info p {
                font-size: 14px;
                color: #777;
            }

            .total {
                font-size: 20px;
                font-weight: 600;
                color: #6a1b9a;
                margin-top: 20px;
            }

            .payment-section {
                background-color: #f8f8f8;
                padding: 20px;
                border-radius: 8px;
                margin-top: 30px;
            }

            .payment-section h3 {
                font-size: 18px;
                color: #333;
                margin-bottom: 15px;
                font-weight: 600;
            }

            .payment-method {
                display: flex;
                align-items: center;
                margin-bottom: 10px;
            }

            .payment-method img {
                width: 40px;
                margin-right: 10px;
            }

            .payment-method p {
                font-size: 16px;
                color: #555;
            }

            .pay-button {
                background-color: #6a1b9a;
                color: white;
                padding: 12px 25px;
                border: none;
                border-radius: 5px;
                font-size: 18px;
                width: 100%;
                cursor: pointer;
                text-align: center;
                display: block;
                margin-top: 20px;
            }

            .pay-button:hover {
                background-color: #9c4dcc;
            }

            .icon {
                color: #6a1b9a;
                font-size: 18px;
                margin-right: 10px;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <!-- Header Section -->
            <div class="header">
                <h1><i class="fas fa-box-open icon"></i> Detalle de Pedido</h1>
                <span class="badge bg-primary text-white">Pedido N°: ${correlative}</span>
            </div>

            <!-- Order Info -->
            <div class="order-info">
                <p><span>Realizado:</span> ${currentdate}</p>
                <p><span>Entrega máxima:</span> ${nextdate}</p>
            </div>

            <!-- Product List -->
            <div class="product-list">
                <%

                    List<Orderline> obj = (List<Orderline>) session.getAttribute("orderlines");
                    if (session.getAttribute("orderlines") == null || obj.size() == 0) {
                        request.getRequestDispatcher("index.jsp").forward(request, response);

                    }
                    for (int i = 0; i < obj.size(); i++) {
                %>
                <div class="product-item">
                    <img src="<%=obj.get(i).getProduct().getImage()%>" alt="Producto">
                    <div class="product-info">
                        <h5><strong> <%=obj.get(i).getProduct().getName()%></strong></h5>
                        <p><i class="fas fa-cogs icon"></i> Unidad:  <%=obj.get(i).getQuantity()%></p>
                        <p><i class="fas fa-tag icon"></i> $.  <%=obj.get(i).getTotal()%></p>
                    </div>
                </div>
                <%
                    }
                %>


            </div>

            <!-- Total Section -->
            <div class="total">
                <p><strong>Total a Pagar: $. ${total}</strong></p>
            </div>

            <!-- Payment Section -->
            <form action="createorderYape" method="POST" accept-charset="UTF-8" enctype="multipart/form-data" onsubmit="return validateFile()">
                <div class="payment-section">
                    <h3>Detalle de Pago</h3>
                    <div class="payment-method">
                        <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQm6pdOp6fVfF9R5ArvkMOsht1f3BsFMvR8fLY78W8DquUT3Fs03UP5QNjPYQ4tBm70eN8" alt="Yape">
                        <p><strong>Yape</strong> - Paga mediante código QR.</p>
                    </div>

                    <p><i class="fas fa-info-circle icon"></i> Adjunta el comprobante con el justificante de pedido para confirmación.</p>
                    <!-- Campo para seleccionar la imagen -->
                    <div class="mb-3">
                        <label for="fileUpload" class="form-label">Selecciona una imagen para adjuntar:</label>
                        <input type="file" class="form-control" name="fileUpload" id="fileUpload" accept="image/*" onchange="previewImage()">

                        <button type="button" id="previewButton" class="btn btn-outline-secondary" onclick="togglePreview()"> 
                            <i class="fas fa-eye"></i> Ver imagen
                        </button>
                        <div id="imagePreviewContainer" style="display:none; margin-top: 20px;">
                            <iframe id="imagePreviewIframe" style="width:100%; height:300px; display:none;" frameborder="0"></iframe>
                            <img id="imagePreview" style="width:100%; height:auto; display:none;" />
                        </div>

                        <script>
                            // Mostrar el botón solo cuando se haya seleccionado un archivo válido
                            function preparePreview() {
                                const fileInput = document.getElementById('fileUpload');
                                const file = fileInput.files[0];
                                const allowedExtensions = /(\.jpg|\.jpeg|\.png)$/i;

                                const previewButton = document.getElementById('previewButton');
                                const imagePreviewContainer = document.getElementById('imagePreviewContainer');
                                const iframePreview = document.getElementById('imagePreviewIframe');
                                const imagePreview = document.getElementById('imagePreview');

                                // Verifica que el archivo sea una imagen válida
                                if (file && allowedExtensions.test(file.name)) {
                                    previewButton.style.display = "inline-block"; // Mostrar el botón de vista previa
                                    const fileURL = URL.createObjectURL(file);
                                    imagePreview.src = fileURL; // Configura la fuente de la imagen

                                    // Ocultar imagen y iframe inicialmente
                                    imagePreview.style.display = "none";
                                    iframePreview.style.display = "none";
                                    imagePreviewContainer.style.display = "none";
                                } else {
                                    previewButton.style.display = "none"; // Ocultar el botón si no es una imagen válida
                                }
                            }

                            // Alternar la vista previa entre iframe y imagen
                            function togglePreview() {
                                const imagePreviewContainer = document.getElementById('imagePreviewContainer');
                                const iframePreview = document.getElementById('imagePreviewIframe');
                                const imagePreview = document.getElementById('imagePreview');
                                const fileInput = document.getElementById('fileUpload');
                                const file = fileInput.files[0];

                                // Verifica que se haya seleccionado un archivo válido
                                if (file) {
                                    const fileURL = URL.createObjectURL(file);

                                    if (iframePreview.style.display === "none") {
                                        iframePreview.style.display = "block";
                                        imagePreview.style.display = "none";
                                        iframePreview.src = fileURL; // Mostrar el iframe con el archivo
                                    } else {
                                        iframePreview.style.display = "none";
                                        imagePreview.style.display = "block";
                                        imagePreview.src = fileURL; // Mostrar la imagen en lugar del iframe
                                    }

                                    imagePreviewContainer.style.display = "block"; // Mostrar el contenedor
                                }
                            }

                            // Llama a la función para preparar la vista previa cuando se selecciona el archivo
                            document.getElementById('fileUpload').addEventListener('change', preparePreview);
                        </script>
                        <small id="fileError" class="text-danger" style="display:none;">Por favor selecciona un archivo válido (.jpeg, .png, .jpg).</small>
                    </div>
                </div>

                <!-- Botón para enviar -->
                <button type="submit" class="pay-button">Realizar Pedido</button>
            </form>

            <script>
                function validateFile() {
                    const fileInput = document.getElementById('fileUpload');
                    const fileError = document.getElementById('fileError');
                    const file = fileInput.files[0]; // Obtiene el archivo seleccionado
                    const allowedExtensions = /(\.jpg|\.jpeg|\.png)$/i; // Extensiones permitidas

                    if (!file) {
                        // Si no se selecciona ningún archivo
                        fileError.textContent = "Por favor selecciona un archivo.";
                        fileError.style.display = "block";
                        return false;
                    }

                    if (!allowedExtensions.test(file.name)) {
                        // Si el archivo no tiene una extensión válida
                        fileError.textContent = "Por favor selecciona un archivo válido (.jpeg, .png, .jpg).";
                        fileError.style.display = "block";
                        return false;
                    }

                    fileError.style.display = "none"; // Oculta el mensaje de error si todo está bien
                    return true; // Permite enviar el formulario
                }
            </script>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.min.js"></script>
    </body>
</html>
