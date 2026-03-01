import EyeHide from "assets/auth/eye-hide.svg?react";
import EyeShow from "assets/auth/eye-show.svg?react";
import clsx from "clsx";
import React, {
  KeyboardEventHandler,
  ReactElement,
  Ref,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";

import styles from "./Input.module.scss";
import { mergeRefs } from "react-merge-refs";
import { Field, FieldProps } from "../Field.tsx";

export type Props = React.InputHTMLAttributes<HTMLInputElement> & {
  containerRef?: Ref<HTMLDivElement>;
  onEnterPressed?: (text: string) => void;
  className?: string;
  errorText?: string;
  helperText?: string;
  type?: string;
  badge?: React.ReactNode;
  /*
   * endAdornment is an element that is placed on the right of the Input (visually inside the input)
   */
  endAdornment?: ReactElement<any, any> | null;
  endAdornmentClassname?: string;
  testId?: string;
  /*
   * 'normal' - input will have minimal width
   * 'formInput' - input will have the standard width (as all form elements)
   */
  variant?: "normal" | "formInput";

  /*
   * This prop might be set if input is part of Autocomplete (DropDown/Combobox/etc).
   * If we don't do this, `selectedValue` ends up as DOM attribute which isn't what we want.
   * It might be better implemented by wrapping inputs that are used in Autocomplete, but it's less of a hassle like it is now.
   */
  selectedValue?: unknown;

  /*
   * This prop is used to render custom component over the input.
   */
  customNode?: React.ReactNode;

  /*
   * Postfix for input field (e.g. `(+5)` for multiple selected values)
   */
  postfix?: string;

  /*
   * If true, input width will be based on its value length.
   */
  valueBasedAutoWidth?: boolean;

  /*
   * Input value will be forcibly replaced with this value.
   */
  forcedInputValue?: string;

  /*
   * If passed, wraps the input into `<Field />` component with specified props.
   */
  fieldProps?: FieldProps;
};

export const Input = React.forwardRef<HTMLInputElement, Props>(
  function Input(props, ref) {
    const {
      containerRef: forwardRef,
      onClick,
      style,
      className,
      helperText,
      errorText,
      onEnterPressed,
      type,
      badge,
      endAdornment: endAdornmentProps,
      endAdornmentClassname,
      variant,
      testId,
      selectedValue,
      disabled,
      customNode,
      postfix,
      value,
      valueBasedAutoWidth,
      forcedInputValue,
      fieldProps,
      ...rest
    } = props;
    const isError = !!errorText;

    const onKeyDown: KeyboardEventHandler<HTMLInputElement> = (e) => {
      if (e.key === "Enter") {
        if (onEnterPressed) {
          onEnterPressed(e.currentTarget.value);
          e.preventDefault();
          e.stopPropagation();
        }
      }
    };
    const [showPassword, setShowPassword] = useState<boolean | undefined>(
      type === "password" ? false : undefined,
    );

    const endAdornment = useMemo(() => {
      if (showPassword === undefined) {
        if (!props.endAdornment) return undefined;
        return { element: props.endAdornment, onClick: props.onClick };
      }
      return {
        style: { marginRight: "2px" },
        element: showPassword ? <EyeHide /> : <EyeShow />,
        onClick: () => setShowPassword((v) => !v),
      };
    }, [props.endAdornment, showPassword]);

    const spanRef = useRef<HTMLSpanElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);
    const containerRef = useRef<HTMLDivElement>(null);
    const innerInputRef = useRef<HTMLInputElement>(null);
    useEffect(() => {
      if (spanRef.current && inputRef.current) {
        if (spanRef.current.offsetWidth <= containerRef.current!.offsetWidth)
          inputRef.current.style.width = `${spanRef.current.offsetWidth}px`;
        else inputRef.current.style.width = "100%";
      }
    }, [endAdornment, postfix, value]);

    const input = (
      <div
        ref={forwardRef ? mergeRefs([forwardRef, containerRef]) : containerRef}
        onClick={onClick}
        style={{ ...style, display: "flex", flexDirection: "column" }}
      >
        {valueBasedAutoWidth && (
          <span
            ref={spanRef}
            className={clsx(
              styles.invisibleSpan,
              endAdornment && !postfix && styles.inputExtraPadding,
              styles.input,
              variant === "normal" ? styles.nonFormInput : null,
              className,
            )}
          >
            {value || props.placeholder || " "}
          </span>
        )}

        <div className={styles.inputContainer} ref={inputRef}>
          {customNode}
          <input
            ref={mergeRefs([ref, innerInputRef])}
            className={clsx(
              endAdornment && !postfix && styles.inputExtraPadding,
              styles.input,
              variant === "normal" ? styles.nonFormInput : null,
              className,
            )}
            data-error={isError.toString()}
            onKeyDown={onEnterPressed ? onKeyDown : undefined}
            type={showPassword ? "input" : type}
            data-test-id={testId}
            disabled={disabled}
            value={forcedInputValue ?? value}
            {...rest}
          />
          {endAdornment ? (
            <div
              style={endAdornment && endAdornment.style}
              className={clsx(
                styles.endAdornment,
                disabled && styles.endAdornmentDisabled,
                type === "password" && styles.passwordEye,
                endAdornmentClassname,
              )}
              onClick={(e) => {
                innerInputRef.current?.focus();
                endAdornment.onClick?.(e as any);
              }}
              onFocus={props.onFocus}
              onMouseDown={props.onMouseDown}
              data-test-id={testId ? `${testId}-end-adornment` : undefined}
            >
              {endAdornment.element}
            </div>
          ) : null}

          {postfix ? (
            <span
              onMouseDown={props.onMouseDown}
              className={clsx(
                styles.postfix,
                endAdornment && styles.postfixExtraPadding,
              )}
            >
              {postfix}
            </span>
          ) : null}
        </div>

        {badge}
        {!!(helperText || errorText) && (
          <div data-error={isError.toString()} className={styles.helperText}>
            {helperText || errorText}
          </div>
        )}
      </div>
    );

    return fieldProps ? <Field {...fieldProps}>{input}</Field> : input;
  },
);
