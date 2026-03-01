import clsx from 'clsx';
import { CSSProperties } from 'react';
import * as React from 'react';

import styles from './TextButton.module.scss';

export enum TextButtonWidth {
  Content = 'content',
  Fullwidth = 'fullwidth',
}

export enum TextButtonColor {
  Icon = 'icon',
  IconTooltip = 'icon-tooltip',
  IconCrown = 'icon-crown',
  Modal = 'modal',
  Primary = 'primary',
  Destroy = 'destroy',
  Passed = 'passed',

  // $grey-400, used for clickable
  Off = 'off',
  // $grey-300, used for unclickable
  Inactive = 'inactive',
}

export type TextButtonProps = {
  id?: string;
  type?: 'submit' | 'button';
  title?: string;
  color?: TextButtonColor;
  width?: TextButtonWidth;
  className?: string;
  disabled?: boolean;
  icon?: React.ReactNode;
  onClick?: (e: React.MouseEvent<HTMLElement>) => void;
  style?: CSSProperties;
  testId?: string;
  titleFirst?: boolean;
  // If true, the button will not react to hover and click events. It differs from disabled in that it will not change the style of the button.
  anemic?: boolean;
  tabIndex?: number;
};

export const TextButton = React.forwardRef<HTMLButtonElement, TextButtonProps>(
  function Button(props, ref) {
    const {
      title,
      color,
      width,
      className,
      disabled,
      icon,
      testId,
      titleFirst,
      anemic,
      ...rest
    } = {
      ...DefaultProps,
      ...props,
    };

    const buttonStyles = [
      className,
      styles.button,
      styles[`${width}-button`],
      styles[`${color}-button`],
      anemic && styles.anemic,
    ];
    return (
      <button
        ref={ref}
        type={props.type ?? 'button'}
        data-disabled={disabled}
        {...rest}
        className={clsx(buttonStyles)}
        data-test-id={testId}
      >
        {titleFirst ? title : icon}
        {titleFirst ? icon : title}
      </button>
    );
  },
);

const DefaultProps: Partial<TextButtonProps> = {
  color: TextButtonColor.Primary,
  width: TextButtonWidth.Content,
};
