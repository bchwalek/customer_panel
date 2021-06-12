/*!
    * Start Bootstrap - Grayscale v6.0.3 (https://startbootstrap.com/theme/grayscale)
    * Copyright 2013-2020 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-grayscale/blob/master/LICENSE)
    */
    (function ($) {
    "use strict"; // Start of use strict

    // Smooth scrolling using jQuery easing
    $('a.js-scroll-trigger[href*="#"]:not([href="#"])').click(function () {
        if (
            location.pathname.replace(/^\//, "") ===
                this.pathname.replace(/^\//, "") &&
            location.hostname === this.hostname
        ) {
            var target = $(this.hash);
            target = target.length
                ? target
                : $("[name=" + this.hash.slice(1) + "]");
            if (target.length) {
                $("html, body").animate(
                    {
                        scrollTop: target.offset().top - 70,
                    },
                    1000,
                    "easeInOutExpo"
                );
                return false;
            }
        }
    });

    // Closes responsive menu when a scroll trigger link is clicked
    $(".js-scroll-trigger").click(function () {
        $(".navbar-collapse").collapse("hide");
    });

    // Activate scrollspy to add active class to navbar items on scroll
    $("body").scrollspy({
        target: "#mainNav",
        offset: 100,
    });

    // Collapse Navbar
    var navbarCollapse = function () {
        if ($("#mainNav").offset().top > 100) {
            $("#mainNav").addClass("navbar-shrink");
        } else {
            $("#mainNav").removeClass("navbar-shrink");
        }
    };
    // Collapse now if page is not at top
    navbarCollapse();
    // Collapse the navbar when page is scrolled
    $(window).scroll(navbarCollapse);
})(jQuery); // End of use strict


//nowe
// function myFunction(imgs) {
//     // Get the expanded image
//     var expandImg = document.getElementById("expandedImg");
//     // Get the image text
//     var imgText = document.getElementById("imgtext");
//     // Use the same src in the expanded image as the image being clicked on from the grid
//     expandImg.src = imgs.src;
//     // Use the value of the alt attribute of the clickable image as text inside the expanded image
//     imgText.innerHTML = imgs.alt;
//     // Show the container element (hidden with CSS)
//     expandImg.parentElement.style.display = "block";
// }

//confirm password
const check = function() {
    if (document.getElementById('newPass').value ===
        document.getElementById('newPassConfirm').value) {
        document.getElementById('newPassConfirm').style.borderColor = 'green';
        document.getElementById('message').innerHTML = 'Hasła zgodne';
        document.getElementById('submit').disabled = false;
    } else {
        document.getElementById('submit').disabled = true;
        document.getElementById('newPassConfirm').style.borderColor = 'red';
        document.getElementById('message').innerHTML = 'Hasła są różne';
    }
}