import {
  Bounce,
  CloseButtonProps,
  ToastContainer,
  ToastContainerProps,
  ToastPosition,
  TypeOptions,
} from 'react-toastify';
import styles from './Toast.module.scss';
import 'react-toastify/dist/ReactToastify.css';
import React from 'react';
import clsx from 'clsx';

export const Toast: React.FC<ToastContainerProps> = (props) => {
  return (
    <ToastContainer
      {...props}
      position={'top-center'}
      autoClose={5000}
      limit={2}
      closeButton={CloseButton}
      hideProgressBar={true}
      newestOnTop={false}
      pauseOnFocusLoss={true}
      theme="light"
      transition={Bounce}
      className={styles.toastContent}
      toastClassName={getToastClassNameFunc()}
      icon={(context) => {
        let icon;
        // switch (context.type) {
        //   case 'success':
        //     icon = <ToastSuccessIcon />;
        //     break;
        //   case 'error':
        //     icon = <ToastErrorIcon />;
        //     break;
        //   case 'warning':
        //     icon = <ToastWarningIcon />;
        //     break;
        //   default:
        //     icon = <ToastSuccessIcon />;
        //     break;
        // }

        return <div className={styles.iconContainer}>{icon}</div>;
      }}
    />
  );
};

const CloseButton = ({ closeToast }: CloseButtonProps) => (
  <div onClick={closeToast} className={styles.closeButtonContainer}>
    {/*<CloseIcon className={styles.closeButton} />*/}
  </div>
);

export enum ToastStyle {
  // Makes toast's width 360px
  Wide,
}

/**
 * This function is used by both Toast component and toast callers, since latter
 * replace classes, which is not what we want. Additionally, we usually want to
 * have predefined types of extra styles for such elements as toasts, so that's
 * another advantage.
 * @param toastStyle - enum defining the extra style to apply (e.g. "Wide" which
 * makes toast's width 360px instead of 328px).
 */
export function getToastClassNameFunc(
  toastStyle?: ToastStyle,
): (context?: {
  type?: TypeOptions;
  defaultClassName?: string;
  position?: ToastPosition;
  rtl?: boolean;
}) => string {
  return (context) =>
    clsx(
      'Toastify__toast',
      styles.toast,
      {
        [styles.success]: context?.type === 'success',
        [styles.error]: context?.type === 'error',
        [styles.warning]: context?.type === 'warning',
      },
      {
        [styles.wideToast]: toastStyle === ToastStyle.Wide,
      },
    );
}
