import React, { FC } from "react";
import { AppLink } from "components/uikit/buttons/AppLink";
import { ButtonColor } from "./buttons/Button";
import clsx from "clsx";
import styles from "./Field.module.scss";
import TruncatingContainer from "./truncatingContainer/TruncatingContainer.tsx";

export type FieldLinkProps = {
  title: string | React.ReactNode;
  disabled?: boolean;
  icon?: string;
  onClick?: (e: React.MouseEvent<HTMLAnchorElement>) => void;
  className?: string;
  testId?: string;
  color?: ButtonColor;
};

export type FieldProps = {
  title: string;
  children?: React.ReactNode;
  className?: string;
  titleClassName?: string;
  childrenWrapperClassName?: string;
  linkProps?: FieldLinkProps;
  hint?: string;
  hintClassName?: string;
  testId?: string;
};

export const Field: FC<FieldProps> = (props) => {
  const {
    children,
    className,
    linkProps,
    title,
    titleClassName,
    testId,
    hint,
    hintClassName,
  } = props;
  return (
    <div
      className={clsx(className, styles.container)}
      data-test-id={testId}
      data-app-field={title}
    >
      <div className={styles.titleContainer}>
        <div className={styles.titleWithHint}>
          <TruncatingContainer
            className={clsx(styles.title, titleClassName)}
            title={title}
          />
        </div>
        {!!linkProps &&
          (typeof linkProps.title === "string" ? (
            <AppLink
              {...linkProps}
              testId={linkProps.testId ?? "field-link"}
              color={ButtonColor.Primary}
              className={clsx(styles.fieldLink, linkProps.className)}
            >
              {linkProps.title}
            </AppLink>
          ) : (
            linkProps.title
          ))}
      </div>
      <div className={clsx(styles.field, props.childrenWrapperClassName)}>
        {children}
      </div>
    </div>
  );
};
