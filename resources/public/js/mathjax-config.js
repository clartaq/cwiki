window.MathJax = {
    tex: {
        inlineMath: [['$', '$'], ['\\(', '\\)']]
    }
};

(function () {
    var script = document.createElement('script');
    script.src = '/js/tex-svg-full.3.0.5.js'
    script.id = 'MathJax-script'
    script.async = true;
    document.head.appendChild(script);
})();
