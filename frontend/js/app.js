(function () {
  "use strict";

  /** Backend base URL: empty = same host (when served from Spring on :8080). */
  function apiRoot() {
    if (window.location.protocol === "file:") {
      return "http://localhost:8080";
    }
    var p = window.location.port;
    if (p === "8080" || p === "") {
      return "";
    }
    return "http://localhost:8080";
  }

  var API = apiRoot();
  var hint = document.getElementById("apiHint");
  if (hint) {
    hint.textContent =
      API === ""
        ? "Using API on this server (port 8080)."
        : "API: " + API + " (start the Java app there).";
  }

  async function api(path, options) {
    var url = API + path;
    var r = await fetch(url, {
      headers: { "Content-Type": "application/json" },
      credentials: "omit",
      ...(options || {}),
    });
    var t = await r.text();
    var data;
    try {
      data = JSON.parse(t);
    } catch (e) {
      data = { raw: t };
    }
    if (!r.ok) {
      throw new Error(data.error || t || String(r.status));
    }
    return data;
  }

  var guestId = null;

  function selectedIds(selector) {
    return Array.prototype.map.call(
      document.querySelectorAll(selector + ":checked"),
      function (el) {
        return Number(el.value);
      }
    );
  }

  async function loadHotelData() {
    var d = await api("/api/data");
    var h = d.hotel;
    var hotelEl = document.getElementById("hotel");
    hotelEl.innerHTML =
      "<h2>" +
      escapeHtml(h.hotelName) +
      "</h2>" +
      "<p>" +
      escapeHtml(h.location) +
      "</p>" +
      "<p>" +
      escapeHtml(h.phoneNumber) +
      " · " +
      escapeHtml(h.email) +
      "</p>" +
      "<p>" +
      escapeHtml(h.description || "") +
      "</p>";

    var rooms = d.rooms || [];
    document.getElementById("rooms").innerHTML =
      "<strong>Rooms</strong>" +
      rooms
        .map(function (room) {
          var id = "r" + room.id;
          return (
            '<div class="chk"><input type="checkbox" class="rid" value="' +
            room.id +
            '" id="' +
            id +
            '"/><label for="' +
            id +
            '">' +
            escapeHtml(room.roomNumber) +
            " — " +
            escapeHtml(room.typeName) +
            " ($" +
            escapeHtml(String(room.pricePerNight)) +
            ")</label></div>"
          );
        })
        .join("");

    var services = d.services || [];
    document.getElementById("services").innerHTML =
      "<strong>Services</strong>" +
      services
        .map(function (s) {
          var id = "s" + s.id;
          return (
            '<div class="chk"><input type="checkbox" class="sid" value="' +
            s.id +
            '" id="' +
            id +
            '"/><label for="' +
            id +
            '">' +
            escapeHtml(s.serviceName) +
            " ($" +
            escapeHtml(String(s.servicePrice)) +
            ")</label></div>"
          );
        })
        .join("");

    document.getElementById("checkIn").value = new Date().toISOString().slice(0, 10);
  }

  function escapeHtml(s) {
    var d = document.createElement("div");
    d.textContent = s;
    return d.innerHTML;
  }

  function setGuestMsg(text, isErr) {
    var el = document.getElementById("guestOut");
    el.textContent = text;
    el.className = "msg " + (isErr ? "err" : "ok");
  }

  document.getElementById("btnReg").onclick = async function () {
    try {
      var o = await api("/api/register", {
        method: "POST",
        body: JSON.stringify({
          fullName: document.getElementById("name").value,
          email: document.getElementById("email").value,
          phoneNumber: document.getElementById("phone").value,
          password: document.getElementById("pw").value,
        }),
      });
      guestId = o.id;
      setGuestMsg("Logged in as id " + guestId + " — " + o.fullName, false);
    } catch (e) {
      setGuestMsg(e.message, true);
    }
  };

  document.getElementById("btnLog").onclick = async function () {
    try {
      var o = await api("/api/login", {
        method: "POST",
        body: JSON.stringify({
          email: document.getElementById("email").value,
          password: document.getElementById("pw").value,
        }),
      });
      guestId = o.id;
      setGuestMsg("Logged in as id " + guestId + " — " + o.fullName, false);
    } catch (e) {
      setGuestMsg(e.message, true);
    }
  };

  document.getElementById("btnBook").onclick = async function () {
    var out = document.getElementById("bookOut");
    out.textContent = "";
    if (!guestId) {
      out.textContent = "Register or login first.";
      return;
    }
    try {
      var o = await api("/api/book", {
        method: "POST",
        body: JSON.stringify({
          guestId: guestId,
          roomIds: selectedIds(".rid"),
          serviceIds: selectedIds(".sid"),
          checkIn: document.getElementById("checkIn").value,
          nights: Number(document.getElementById("nights").value),
        }),
      });
      out.textContent = JSON.stringify(o, null, 2);
      document.getElementById("resId").value = o.reservationId;
    } catch (e) {
      out.textContent = e.message;
    }
  };

  document.getElementById("btnInv").onclick = async function () {
    document.getElementById("payOut").textContent = "";
    try {
      var id = document.getElementById("resId").value.trim();
      var o = await api("/api/reservation/" + encodeURIComponent(id) + "/invoice", {
        method: "POST",
      });
      document.getElementById("payOut").textContent = JSON.stringify(o, null, 2);
      document.getElementById("invId").value = o.invoiceId;
      document.getElementById("payAmt").value = o.totalAmount;
    } catch (e) {
      document.getElementById("payOut").textContent = e.message;
    }
  };

  document.getElementById("btnPay").onclick = async function () {
    try {
      var inv = document.getElementById("invId").value.trim();
      var o = await api("/api/invoice/" + inv + "/pay", {
        method: "POST",
        body: JSON.stringify({
          paymentMethod: document.getElementById("payMethod").value,
          amountPaid: Number(document.getElementById("payAmt").value),
        }),
      });
      document.getElementById("payOut").textContent = JSON.stringify(o, null, 2);
    } catch (e) {
      document.getElementById("payOut").textContent = e.message;
    }
  };

  document.getElementById("btnCancel").onclick = async function () {
    document.getElementById("extraOut").textContent = "";
    try {
      var id = document.getElementById("resId").value.trim();
      var o = await api("/api/reservation/" + encodeURIComponent(id) + "/cancel", {
        method: "POST",
        body: JSON.stringify({ reason: "changed plans" }),
      });
      document.getElementById("extraOut").textContent = JSON.stringify(o, null, 2);
    } catch (e) {
      document.getElementById("extraOut").textContent = e.message;
    }
  };

  document.getElementById("btnReview").onclick = async function () {
    if (!guestId) {
      document.getElementById("extraOut").textContent = "Login first.";
      return;
    }
    try {
      var o = await api("/api/review", {
        method: "POST",
        body: JSON.stringify({
          guestId: guestId,
          rating: Number(document.getElementById("rating").value),
          comment: document.getElementById("comment").value,
        }),
      });
      document.getElementById("extraOut").textContent = JSON.stringify(o, null, 2);
    } catch (e) {
      document.getElementById("extraOut").textContent = e.message;
    }
  };

  loadHotelData().catch(function (e) {
    alert(e.message);
  });
})();
