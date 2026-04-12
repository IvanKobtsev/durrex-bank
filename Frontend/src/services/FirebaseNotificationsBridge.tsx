import { useEffect } from "react";
import { useAuth } from "react-oidc-context";
import { toast } from "react-toastify";
import {
  getFirebaseMessageBody,
  getFirebaseMessageTitle,
  getFirebaseMessageToastId,
  getFirebaseMessagingToken,
  requestNotificationPermission,
  subscribeToForegroundMessages,
  type FirebaseMessagePayload,
} from "./firebase.ts";
import { subscribeToPushNotifications } from "./push-notifications.ts";

const openNotificationLink = (link?: string) => {
  if (!link) {
    return;
  }

  window.location.assign(link);
};

const renderToastContent = (payload: FirebaseMessagePayload) => {
  const title = getFirebaseMessageTitle(payload);
  const body = getFirebaseMessageBody(payload);

  return (
    <div>
      <div>
        <strong>{title}</strong>
      </div>
      {body ? <div>{body}</div> : null}
    </div>
  );
};

const showForegroundNotificationToast = (payload: FirebaseMessagePayload) => {
  const toastId = getFirebaseMessageToastId(payload);

  if (toast.isActive(toastId)) {
    return;
  }

  toast.info(renderToastContent(payload), {
    toastId,
  });
};

export function FirebaseNotificationsBridge() {
  const auth = useAuth();

  useEffect(() => {
    if (!auth.isAuthenticated) {
      return;
    }

    let isDisposed = false;

    void (async () => {
      try {
        const permission = await requestNotificationPermission();
        if (isDisposed || permission !== "granted") {
          return;
        }

        const token = await getFirebaseMessagingToken();
        if (isDisposed || !token) {
          return;
        }

        await subscribeToPushNotifications({ fireBaseToken: token });
      } catch (error) {
        console.error("Failed to register Firebase push notifications", error);
      }
    })();

    return () => {
      isDisposed = true;
    };
  }, [auth.isAuthenticated, auth.user?.profile.sub]);

  useEffect(() => {
    if (!auth.isAuthenticated) {
      return;
    }

    let unsubscribe: (() => void) | undefined;
    let isDisposed = false;

    void (async () => {
      try {
        unsubscribe = await subscribeToForegroundMessages((payload: FirebaseMessagePayload) => {
          if (isDisposed) {
            return;
          }

          showForegroundNotificationToast(payload);
        });
      } catch (error) {
        console.error("Failed to subscribe to Firebase foreground messages", error);
      }
    })();

    return () => {
      isDisposed = true;
      unsubscribe?.();
    };
  }, [auth.isAuthenticated]);

  return null;
}



