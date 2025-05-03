function loadPage(pageName) {
    fetch(`pages/${pageName}.html`)
      .then(res => res.text())
      .then(html => {
        document.getElementById('app').innerHTML = html;

        if (pageName === "pay") {
          const cardInput = document.getElementById("cardNumber");
          if (cardInput) {
            cardInput.addEventListener("input", function (e) {
              let value = e.target.value.replace(/\D/g, "").slice(0, 16); 
              value = value.match(/.{1,4}/g)?.join(" ") || "";
              e.target.value = value;
            });
          }
  
          const expiryInput = document.getElementById("expiry");
          if (expiryInput) {
            expiryInput.addEventListener("input", function (e) {
              let value = e.target.value.replace(/\D/g, "").slice(0, 4);
              if (value.length > 2) {
                value = value.slice(0, 2) + "/" + value.slice(2);
              }
              e.target.value = value;
            });
          }
        }
      })
      .catch(err => {
        document.getElementById('app').innerHTML = "<p class='text-danger'>Failed to load page.</p>";
      });
  }
  
  window.addEventListener('DOMContentLoaded', () => {
    loadPage('home');
  });

  document.addEventListener("click", function (e) {
    const target = e.target.closest("[data-page]");
    if (target) {
      const page = target.getAttribute("data-page");
      loadPage(page);
    }
  });
  