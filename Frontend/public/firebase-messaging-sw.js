/* eslint-disable no-undef */
importScripts("https://www.gstatic.com/firebasejs/12.12.0/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/12.12.0/firebase-messaging-compat.js");

firebase.initializeApp({
  apiKey: "AIzaSyBZmCMYpot9DxcUjoMkdsouWn2f6xeN0qs",
  authDomain: "durexbank.firebaseapp.com",
  projectId: "durexbank",
  storageBucket: "durexbank.firebasestorage.app",
  messagingSenderId: "226907763658",
  appId: "1:226907763658:web:32d18c7211150e30721533",
  measurementId: "G-8JMBH109DW",
});

const messaging = firebase.messaging();
const DEFAULT_NOTIFICATION_TITLE = "Durrex Bank";

const normalizePath = (value) => {
  if (!value) {
    return undefined;
  }

  if (/^https?:\/\//i.test(value)) {
    return value;
  }

  return value.startsWith("/") ? value : `/${value}`;
};

const resolveLink = (data = {}) => {
  const directLink =
    data.actionUrl ||
    data.click_action ||
    data.url ||
    data.link ||
    data.route ||
    data.pathname;

  if (directLink) {
    return normalizePath(directLink);
  }

  if (data.accountId) {
    return `/account/${data.accountId}`;
  }

  if (data.userId) {
    return `/users/${data.userId}`;
  }

  if (data.creditId) {
    return `/credits/${data.creditId}`;
  }

  if (data.screen === "users") {
    return "/users";
  }

  return "/";
};

messaging.onBackgroundMessage((payload) => {
  if (payload.notification) {
    return;
  }

  const data = payload.data || {};
  const title = payload.notification?.title || data.title || DEFAULT_NOTIFICATION_TITLE;
  const body = payload.notification?.body || data.body;
  const link = resolveLink(data);

  self.registration.showNotification(title, {
    body,
    data: {
      ...data,
      link,
    },
    tag: payload.messageId || data.messageId || `${title}:${body || ""}`,
  });
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();

  const targetUrl = normalizePath(event.notification?.data?.link) || "/";

  event.waitUntil(
    self.clients.matchAll({ type: "window", includeUncontrolled: true }).then((clientList) => {
      for (const client of clientList) {
        if ("focus" in client) {
          client.navigate(targetUrl);
          return client.focus();
        }
      }

      if (self.clients.openWindow) {
        return self.clients.openWindow(targetUrl);
      }

      return undefined;
    }),
  );
});

