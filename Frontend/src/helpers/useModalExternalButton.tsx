import { useEventCallback } from '@mui/material';
import { useWindowSize } from 'helpers/useWindowSize.ts';
import { RefObject, useEffect, useImperativeHandle, useRef } from 'react';

export const useModalExternalButton = (props: {
  offsetRight: number;
  offsetTop: number;
  isOpen: boolean;
  realignButtonEventRef?: RefObject<() => void>;
}) => {
  const anchorRef = useRef<HTMLDivElement>(null);
  const buttonRef = useRef<HTMLButtonElement>(null);

  const realignButton = useEventCallback(() => {
    if (buttonRef.current) {
      buttonRef.current.style.right = `${(anchorRef.current?.offsetLeft ?? 0) + props.offsetRight}px`;
      buttonRef.current.style.top = `${(anchorRef.current?.offsetTop ?? 0) + props.offsetTop}px`;
    }
  });

  useImperativeHandle(props.realignButtonEventRef, () => realignButton);

  const windowSize = useWindowSize();
  useEffect(() => {
    setTimeout(() => {
      realignButton();
    });
  }, [
    windowSize,
    buttonRef.current,
    anchorRef.current,
    anchorRef.current?.getBoundingClientRect().toJSON(),
    props.isOpen,
  ]);

  const anchor = <div ref={anchorRef} />;

  return { anchor, buttonRef };
};
