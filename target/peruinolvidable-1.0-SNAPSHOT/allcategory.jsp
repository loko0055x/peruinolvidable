
<%@page import="com.alexanderdoma.peruinolvidable.model.mysql.CategoryDAO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.alexanderdoma.peruinolvidable.model.mysql.ProductDAO"%>
<%@page import="com.sun.tools.javac.resources.compiler"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script>
            const showcase = document.querySelector('.ct-productShowcase');
            let isDown = false;
            let startX;
            let scrollLeft;

            showcase.addEventListener('mousedown', (e) => {
                isDown = true;
                startX = e.pageX - showcase.offsetLeft;
                scrollLeft = showcase.scrollLeft;
            });

            showcase.addEventListener('mouseleave', () => {
                isDown = false;
            });

            showcase.addEventListener('mouseup', () => {
                isDown = false;
            });

            showcase.addEventListener('mousemove', (e) => {
                if (!isDown)
                    return; // Do nothing if not dragging
                const x = e.pageX - showcase.offsetLeft;
                const walk = (x - startX) * 3; // The multiplier controls the speed
                showcase.scrollLeft = scrollLeft - walk;
            });
        </script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Categoriax</title>
        <!--STYLES-->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
        <script src="https://kit.fontawesome.com/5d0c04c42d.js" crossorigin="anonymous"></script>


        <link rel="stylesheet" href="https://websites.getjusto.com/_next/static/css/7fc4fe33cca80a0c.css">




    </head>
    <body>


        <%@include file="components/header.jsp"%>



        <style>
            .ct-productShowcase {
                width: 100%;
                overflow-x: auto;
                white-space: nowrap;
                display: flex;
                justify-content: flex-start;
                padding-left: 20px;
                padding-right: 20px;
                -webkit-overflow-scrolling: touch; /* Enables smooth scrolling on touch devices */
                scroll-snap-type: x mandatory; /* Optional: adds snap scrolling for smoother experience */
            }

            .product-card {
                display: inline-block;
                width: 300px;
                margin-right: 20px;
                scroll-snap-align: start; /* Makes the cards snap into position */
            }

            .product-description a.btn {
                margin-top: 10px;
                text-align: center;
                display: block;
                padding: 10px;
                font-weight: bold;
            }

            .product-showcase-container {
                margin-left: auto;  /* Centers the container from the left */
                margin-right: auto; /* Centers the container from the right */
                padding-left: 20px; /* Adds space on the left of the container */
                padding-right: 20px; /* Adds space on the right of the container */
                width: 90%; /* You can adjust the width to control how much of the page is taken up */
            }

            .product-showcase-container h2 {
                text-align: center; /* Centers the h2 */
                margin-bottom: 20px; /* Adds some space below the heading */
            }
            .product-title {
                font-size: 16px;
                font-weight: bold;
                color: #333;
                display: -webkit-box;
                -webkit-line-clamp: 2; /* Máximo de 2 líneas visibles */
                -webkit-box-orient: vertical;
                overflow: hidden; /* Oculta el texto excedente */
                text-overflow: ellipsis; /* Muestra "..." si el texto es largo */
            }

            .product-price {
                font-size: 14px;
                color: #555;
            }
        </style> 

        <div class="product-showcase-container">


            <c:forEach items="${categories}" var="category">  
                <h2>${category.name} </h2>  
                <div class="ct-productShowcase">
                    <c:forEach items="${productsx}" var="pro"> 

                        <c:if test="${pro.category.id == category.id}"> 
                            <div class="product-card">
                                <img style="width: 100%; height: 200px; object-fit: cover;" src="${pro.image}" alt="Error Img">
                                <div class="product-description">
                                    <div class="product-title">${pro.name}</div>
                                    <div class="product-price">$.  ${pro.price}</div>
                                    <a class="btn btn-dark" href="product?id=${pro.category.id}">Ver Producto</a> <!-- Botón agregado -->
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
                <br><br><br> 
            </c:forEach>
        </div>

        <br><br><br> 



        <%@include file="components/footer.jsp" %>


        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js" integrity="sha512-v2CJ7UaYy4JwqLDIrZUI/4hqeoQieOmAZNXBeQyjo21dadnwR+8ZaIJVT8EE2iyI61OV8e6M8PP2/4hpQINQ/g==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
        <script src="assets/js/toastr.min.js"></script>
        <script src="assets/js/notifications.js"></script>
    </body>
</html>
