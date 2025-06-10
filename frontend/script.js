function getPrices() {
  fetch("http://localhost:8080/api/prices")
    .then(res => res.json())
    .then(data => {
      const div = document.getElementById("prices");
      div.innerHTML = "";
      for (const [key, value] of Object.entries(data)) {
        div.innerHTML += `<p><strong>${key}:</strong> $${value.usd}</p>`;
      }
    });
}

function getSingleGraph() {
  const crypto = document.getElementById("crypto1").value;
  const hours = document.getElementById("hours1").value;
  const url = `http://localhost:8081/graph?crypto=${crypto}&hours=${hours}`;
  document.getElementById("graph1").src = url;
}

function getMultiGraph() {
  const cryptos = document.getElementById("cryptos2").value;
  const hours = document.getElementById("hours2").value;
  const url = `http://localhost:8081/graph/multi?cryptos=${cryptos}&hours=${hours}`;
  document.getElementById("graph2").src = url;
}

function getRegression() {
  const crypto = document.getElementById("crypto3").value;
  const start = document.getElementById("start3").value;
  const end = document.getElementById("end3").value;
  const url = `http://localhost:8082/regression?crypto=${crypto}&start=${start}&end=${end}`;

  fetch(url)
    .then(res => {
      document.getElementById("graph3").src = url;
      const equation = res.headers.get("X-Equation");
      document.getElementById("equation").innerText = "Ecuación: " + equation;
    });
}
