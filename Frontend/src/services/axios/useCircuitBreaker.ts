import { useEffect, useState } from "react";
import {
  getCircuitBreakerRemainingMs,
  isCircuitBreakerOpen,
  subscribeToCircuitBreaker,
} from "services/axios/circuit-breaker.ts";

type CircuitBreakerUiState = {
  isOpen: boolean;
  remainingMs: number;
};

function getStateSnapshot(): CircuitBreakerUiState {
  return {
    isOpen: isCircuitBreakerOpen(),
    remainingMs: getCircuitBreakerRemainingMs(),
  };
}

export function useCircuitBreaker(): CircuitBreakerUiState {
  const [state, setState] = useState<CircuitBreakerUiState>(getStateSnapshot);

  useEffect(() => {
    const unsubscribe = subscribeToCircuitBreaker(() => {
      setState(getStateSnapshot());
    });

    const timer = window.setInterval(() => {
      setState(getStateSnapshot());
    }, 1_000);

    return () => {
      unsubscribe();
      window.clearInterval(timer);
    };
  }, []);

  return state;
}

