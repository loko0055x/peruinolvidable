<%-- 
    Document   : transaction-success
    Created on : 8 dic. 2024, 07:49:30
    Author     : Windows 10
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Pedido Exitoso</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
        <style>
            /* Estilos personalizados */
            body {
                font-family: 'Poppins', sans-serif;
                background: linear-gradient(135deg,  ${maincolor},  ${secondarycolor});/**/

                height: 100vh;
                margin: 0;
                display: flex;
                justify-content: center;
                align-items: center;
                color: #fff;
            }
            .success-container {
                background-color: #f9f9f9;
                padding: 40px;
                border-radius: 15px;
                text-align: center;
                max-width: 450px;
                width: 100%;
                box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
                transform: scale(1);
                transition: transform 0.3s;
            }
            .success-container:hover {
                transform: scale(1.03);
            }
            .success-container img {
                width: 120px;
                margin-bottom: 20px;
            }
            .success-container h1 {
                font-size: 28px;
                font-weight: 600;
                color:  ${maincolor};/**/
                margin-bottom: 15px;
            }
            .success-container p {
                font-size: 16px;
                font-weight: 300;
                color: #333;
                margin-bottom: 25px;
            }
            .success-container .btn-primary {
                background-color:${maincolor}; /**/
                border: none;
                font-size: 18px;
                padding: 10px 25px;
                border-radius: 5px;
                transition: background-color 0.3s;
            }
            .success-container .btn-primary:hover {
                background-color: #0056b3;
            }
            .success-container small {
                display: block;
                margin-top: 20px;
                color: #777;
                font-size: 14px;
            }
        </style>
    </head>
    <body>
        <div class="success-container">
            <img src="${photo}" alt="Yape Logo">
            <h1>¡Pedido Exitoso!</h1>
            <p>Gracias por tu compra. Tu pago con ${paymentType}
                ha sido procesado exitosamente.</p>
            <a href="http://localhost:8080/peruinolvidable/" class="btn btn-primary">Volver al Inicio</a>
            <small>Si tienes alguna consulta, contáctanos al soporte técnico.</small>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.min.js"></script>
    </body>
</html>
