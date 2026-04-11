import axios from "axios";
import { getBaseUrl } from "services/core-api/core-api-client.ts";

export type PushNotificationSubscriptionRequest = {
  fireBaseToken: string;
};

export const subscribeToPushNotifications = async (
  request: PushNotificationSubscriptionRequest,
): Promise<void> => {
  await axios.post(`${getBaseUrl()}/api/push-notifications/subscribe`, request);
};

