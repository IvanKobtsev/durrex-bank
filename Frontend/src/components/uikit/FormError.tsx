import React, { ReactNode } from 'react';
import styles from './Field.module.scss';
import clsx from 'clsx';

export const FormError = (props: {
  children?: ReactNode;
  className?: string;
}) => {
  if (!props.children) return null;

  return (
    <div className={clsx(styles.error, props.className)}>{props.children}</div>
  );
};
