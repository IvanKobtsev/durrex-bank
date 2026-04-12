import { getApp, getApps, initializeApp } from "firebase/app";
import {
  getMessaging,
  getToken,
  isSupported,
  onMessage,
  type MessagePayload,
  type Messaging,
} from "firebase/messaging";
import {
  firebaseConfig,
  firebaseMessagingOptions,
} from "application/configs.ts";

const app = getApps().length > 0 ? getApp() : initializeApp(firebaseConfig);

type NotificationData = Record<string, string | undefined>;

const DEFAULT_NOTIFICATION_TITLE = "Durrex Bank";

let messagingPromise: Promise<Messaging | null> | null = null;
let serviceWorkerRegistrationPromise: Promise<ServiceWorkerRegistration | null> | null = null;

const isBrowser = () => typeof window !== "undefined";

const normalizePath = (value: string): string => {
  if (/^https?:\/\//i.test(value)) {
    return value;
  }

  return value.startsWith("/") ? value : `/${value}`;
};

const readMessageData = (payload: MessagePayload): NotificationData => payload.data ?? {};

export const isFirebaseMessagingSupported = async (): Promise<boolean> => {
  if (!isBrowser()) {
    return false;
  }

  if (!("Notification" in window) || !("serviceWorker" in navigator)) {
    return false;
  }

  if (!window.isSecureContext) {
    return false;
  }

  return isSupported();
};

export const getFirebaseMessaging = async (): Promise<Messaging | null> => {
  if (!messagingPromise) {
    messagingPromise = isFirebaseMessagingSupported().then((supported) =>
      supported ? getMessaging(app) : null,
    );
  }

  return messagingPromise;
};

export const registerFirebaseMessagingServiceWorker = async (): Promise<ServiceWorkerRegistration | null> => {
  if (!serviceWorkerRegistrationPromise) {
    serviceWorkerRegistrationPromise = (async () => {
      if (!(await isFirebaseMessagingSupported())) {
        return null;
      }

      return navigator.serviceWorker.register(
        firebaseMessagingOptions.serviceWorkerPath,
      );
    })();
  }

  return serviceWorkerRegistrationPromise;
};

export const requestNotificationPermission = async (): Promise<NotificationPermission> => {
  if (!isBrowser() || !(await isFirebaseMessagingSupported())) {
    return "denied";
  }

  if (Notification.permission === "granted") {
    return "granted";
  }

  if (Notification.permission === "denied") {
    return "denied";
  }

  return Notification.requestPermission();
};

export const getFirebaseMessagingToken = async (): Promise<string | null> => {
  if (!firebaseMessagingOptions.vapidKey) {
    console.warn(
      "Firebase messaging is enabled but VITE_FIREBASE_VAPID_KEY is not configured.",
    );
    return null;
  }

  const messaging = await getFirebaseMessaging();
  if (!messaging) {
    return null;
  }

  const serviceWorkerRegistration = await registerFirebaseMessagingServiceWorker();
  if (!serviceWorkerRegistration) {
    return null;
  }

  const permission = await requestNotificationPermission();
  if (permission !== "granted") {
    return null;
  }

  const token = await getToken(messaging, {
    vapidKey: firebaseMessagingOptions.vapidKey || undefined,
    serviceWorkerRegistration,
  });

  return token || null;
};

export const subscribeToForegroundMessages = async (
  callback: (payload: MessagePayload) => void,
): Promise<() => void> => {
  const messaging = await getFirebaseMessaging();
  if (!messaging) {
    return () => undefined;
  }

  return onMessage(messaging, callback);
};

export const getFirebaseMessageTitle = (payload: MessagePayload): string => {
  return (
    payload.notification?.title ||
    readMessageData(payload).title ||
    DEFAULT_NOTIFICATION_TITLE
  );
};

export const getFirebaseMessageBody = (
  payload: MessagePayload,
): string | undefined => {
  return payload.notification?.body || readMessageData(payload).body;
};

export const getFirebaseMessageToastId = (payload: MessagePayload): string => {
  const data = readMessageData(payload);

  return (
    payload.messageId ||
    data.messageId ||
    [getFirebaseMessageTitle(payload), getFirebaseMessageBody(payload)]
      .filter(Boolean)
      .join("::") ||
    DEFAULT_NOTIFICATION_TITLE
  );
};

export type { MessagePayload as FirebaseMessagePayload };
