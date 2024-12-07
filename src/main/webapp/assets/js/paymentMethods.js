function proccessOrder() {
    fetch('authorize_payment', {
        method: 'POST'
    })
            .then(response => response.json())

            .then(data => {
                console.log('Response JSON:', data);
                if (data.type === 'success') {
                    console.log('Response JSON:', data);

                    showToast(data.type, data.tittle);
                    setTimeout(() => {
                        window.location.href = data.message;
                    }, 1000);
                } else {
                    showToast(data.type, data.tittle);
                }
            })

            .catch(error => {
                showToast('error', 'Error al procesar orden de compra');
                console.error('Error:', error);
            });
}

 