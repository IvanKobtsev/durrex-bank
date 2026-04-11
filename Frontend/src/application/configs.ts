type ViteImportMeta = ImportMeta & {
  env?: Record<string, string | undefined>;
};

export const appEnv = (import.meta as ViteImportMeta).env ?? {};

export const userData: { userId: number | undefined } = { userId: undefined };

export const oidcConfig = {
  authority: "https://swagor-time.ru/services/auth",
  client_id: "web-spa",
  redirect_uri: "https://swagor-time.ru/callback",
  post_logout_redirect_uri: "https://swagor-time.ru",
  scope: "openid profile bank_role bank.api offline_access",
  response_type: "code",
};

export const firebaseConfig = {
  apiKey: "AIzaSyBZmCMYpot9DxcUjoMkdsouWn2f6xeN0qs",
  authDomain: "durexbank.firebaseapp.com",
  projectId: "durexbank",
  storageBucket: "durexbank.firebasestorage.app",
  messagingSenderId: "226907763658",
  appId: "1:226907763658:web:32d18c7211150e30721533",
  measurementId: "G-8JMBH109DW",
};

export const firebaseMessagingOptions = {
  vapidKey: appEnv.VITE_FIREBASE_VAPID_KEY ?? "",
  serviceWorkerPath: "/firebase-messaging-sw.js",
};

